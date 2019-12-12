package il.co.quana

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import il.co.quana.protocol.*
import timber.log.Timber
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger


private interface ProtocolResponseHandler<T : ProtocolMessage.BaseReply> {
    fun handleResponse(response: T)
}

internal const val BINARY_LOG_TAG = "BIN"
internal const val binaryLogEnabled = true
internal const val NON_ACK_RETRY_COUNT = 3

internal fun ByteArray.binaryLog() = this.joinToString(separator = ",")


class QuanaDeviceCommunicatorFactory {
    companion object {
        fun createQuanaDeviceCommunicator(
            context: Context,
            deviceAddress: String
        ): QuanaDeviceCommunicator {
            val rxBleClient = DI.rxBleClient(context.applicationContext)

            val quanaBluetoothServer = QuanaBluetoothServer(context.applicationContext)
            quanaBluetoothServer.open()
//                .let { compositeDisposable.add(it) }

            val device = rxBleClient.getBleDevice(deviceAddress)

            val quanaBluetoothClient = QuanaBluetoothClient(device)
            quanaBluetoothClient.connect()
//                .let { compositeDisposable.add(it) }

            return QuanaDeviceCommunicator(quanaBluetoothServer, quanaBluetoothClient)
        }
    }
}


@ExperimentalUnsignedTypes
class QuanaDeviceCommunicator(
    private val server: QuanaBluetoothServer,
    private val client: QuanaBluetoothClient
) :
    QuanaBluetoothClientCallback {

    init {
        client.callback = this
    }

    private val handlerThread = HandlerThread("QuanaDeviceCommunicator").apply {
        start()
    }
    private val handler = Handler(handlerThread.looper)

    private var pendingRequest: ProtocolMessage? = null
    private var pendingResponseHandler: ((ProtocolMessage.BaseReply) -> Unit)? = null
    private val requestAttemptsCounter = AtomicInteger()

    private val messageId = AtomicInteger(0)

    private fun assertThread() {
        if (handlerThread != Thread.currentThread()) {
            throw RuntimeException("Run this method from 'handlerThread' only")
        }
    }

    override fun messageReceived(response: ProtocolMessage) {
        handleResponse(response)
    }

    override fun messageError(exception: ProtocolException) {
        Timber.e(exception)
        resetConnection("ProtocolException")
    }

    private fun handleResponse(response: ProtocolMessage) {

        pendingRequest.let { pendingRequest ->

            if (pendingRequest == null) {
                resetConnection("No pending request")
            } else if (pendingRequest.id != response.id || pendingRequest.opcode != response.opcode) {
                resetConnection("Response is not for pending request")
            } else if (response is ProtocolMessage.BaseReply) {
                handleValidResponse(response)
            } else {
                resetConnection("Unsupported response $response")
            }
        }
    }

    private fun resetConnection(reason: String?) {
        this.pendingRequest = null
        Timber.e("Device re-connection is not yet implemented")
        Timber.d("Resetting connection [%s]", reason)
    }

    private fun assertIdle(resetIfNot: Boolean = false): Boolean {
        return pendingRequest?.let {
            if (resetIfNot) {
                resetConnection("Forced by assertIdle")
                true
            } else {
                false
            }
        } ?: true
    }

    private fun <T : ProtocolMessage.BaseReply> sendMessage(
        messageFactory: ((messageId: UShort) -> ProtocolMessage),
        responseHandler: ProtocolResponseHandler<T>
    ) {
        handler.post {
            sendMessageInner(messageFactory, responseHandler)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : ProtocolMessage.BaseReply> sendMessageInner(
        messageFactory: ((messageId: UShort) -> ProtocolMessage),
        responseHandler: ProtocolResponseHandler<T>
    ) {

        assertThread()
        if (!assertIdle()) {
            return
        }

        val message = messageFactory(messageId.getAndIncrement().toUShort())
        pendingRequest = message
        pendingResponseHandler = {
            responseHandler.handleResponse(it as T)
        }
        requestAttemptsCounter.set(NON_ACK_RETRY_COUNT - 1)
        server.write(
            message,
            client.device
        )
    }

    private fun handleValidResponse(response: ProtocolMessage.BaseReply) {
        handler.post {
            if (response.ack == ACK) {
                handleACKResponse(response)
            } else {
                handleNonACKResponse(response)
            }
        }
    }

    private fun handleACKResponse(response: ProtocolMessage.BaseReply) {

        assertThread()

        this.pendingRequest = null
        this.pendingResponseHandler?.invoke(response)
        this.pendingResponseHandler = null
    }

    private fun handleNonACKResponse(response: ProtocolMessage.BaseReply) {

        assertThread()

        this.pendingRequest?.let { message ->
            if (response.ack == ErrorCodes.crcFailure.value) {
                if (requestAttemptsCounter.decrementAndGet() > 0) {
                    Timber.d("CRC Failure. Retrying...")
                    server.write(
                        message,
                        client.device
                    )
                } else {
                    resetConnection("Non ACK response")
                }
            } else {
                this.pendingRequest = null
                this.pendingResponseHandler?.invoke(response)
                this.pendingResponseHandler = null
            }
        }
    }

    fun startScan(callback: ((Boolean) -> Unit)? = null) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.StartScan(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.StartScanReply> {
            override fun handleResponse(response: ProtocolMessage.StartScanReply) {
                callback?.invoke(true)
            }
        })
    }

    fun resetDevice(callback: ((Boolean) -> Unit)? = null) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.ResetDevice(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.ResetDeviceReply> {
            override fun handleResponse(response: ProtocolMessage.ResetDeviceReply) {
                callback?.invoke(true)
            }
        })
    }


    fun quitScan(callback: ((Boolean) -> Unit)? = null) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.QuitScan(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.QuitScanReply> {
            override fun handleResponse(response: ProtocolMessage.QuitScanReply) {
                callback?.invoke(true)
            }
        })
    }

    fun setConfigurationParameter(
        parameterCode: Byte,
        values: ByteArray,
        callback: ((Boolean) -> Unit)? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.SetConfigurationParameter(
                id,
                parameterCode.toUByte(),
                values
            )
        }, object : ProtocolResponseHandler<ProtocolMessage.SetConfigurationParameterReply> {
            override fun handleResponse(response: ProtocolMessage.SetConfigurationParameterReply) {
                callback?.invoke(true)
            }
        })
    }

    fun getConfigurationParameter(
        parameterCode: Byte,
        callback: ((ByteArray) -> Unit)? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GetConfigurationParameter(
                id,
                parameterCode.toUByte()
            )
        }, object : ProtocolResponseHandler<ProtocolMessage.GetConfigurationParameterReply> {
            override fun handleResponse(response: ProtocolMessage.GetConfigurationParameterReply) {
                callback?.invoke(response.parameterValues)
            }
        })
    }

    fun getDeviceStatus(callback: ((DeviceStatus) -> Unit)? = null) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GetDeviceStatus(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.GetDeviceStatusReply> {
            override fun handleResponse(response: ProtocolMessage.GetDeviceStatusReply) {
                callback?.invoke(response.deviceStatus)
            }
        })
    }

    fun getSample(
        sampleId: UShort,
        callback: ((sensorCode: UByte, sampleData: ByteArray) -> Unit)? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GetSample(
                id.toUShort(),
                sampleId
            )
        }, object : ProtocolResponseHandler<ProtocolMessage.GetSampleReply> {
            override fun handleResponse(response: ProtocolMessage.GetSampleReply) {
                callback?.invoke(response.sensorCode, response.sampleData)
            }
        })
    }

    fun getScanResults(
        callback: ((amountOfSamples: UShort, scanStatus: ScanStatus) -> Unit)? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GetScanResults(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.GetScanResultsReply> {
            override fun handleResponse(response: ProtocolMessage.GetScanResultsReply) {
                callback?.invoke(response.amountOfSamples, response.scanStatus)
            }
        })
    }

    fun takeFirmwareChunk(
        chunkId: UInt, address: UInt, chunk: ByteArray,
        callback: ((chunkId: UInt) -> Unit)? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.TakeFirmwareChunk(id, chunkId, address, chunk)
        }, object : ProtocolResponseHandler<ProtocolMessage.TakeFirmwareChunkReply> {
            override fun handleResponse(response: ProtocolMessage.TakeFirmwareChunkReply) {
                callback?.invoke(response.chunkId)
            }
        })
    }

    fun checkFirmwareChunk(
        chunkId: UInt,
        callback: ((chunkId: UInt, ack: UByte) -> Unit)? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.CheckFirmwareChunk(id, chunkId)
        }, object : ProtocolResponseHandler<ProtocolMessage.CheckFirmwareChunkReply> {
            override fun handleResponse(response: ProtocolMessage.CheckFirmwareChunkReply) {
                callback?.invoke(response.chunkId, response.ack)
            }
        })
    }

    fun goToFirmwareUpdate(callback: ((Boolean) -> Unit)? = null) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GoToFirmwareUpdate(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.GoToFirmwareUpdateReply> {
            override fun handleResponse(response: ProtocolMessage.GoToFirmwareUpdateReply) {
                callback?.invoke(true)
            }
        })
    }
}