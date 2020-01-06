package il.co.quana

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import il.co.quana.protocol.*
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger


enum class ErrorType {
    Timeout
}

interface ResponseCallback<T> {
    fun onMessageResult(messageResult: MessageResult<T>)
}

data class MessageResult<T>(
    val success: T?,
    val error: ErrorType?
)

private class ResponseHandlerResult<T : ProtocolMessage.BaseReply> {

    constructor(success: T) {
        this.success = success
        this.error = null
    }

    constructor(error: ErrorType) {
        this.success = null
        this.error = error
    }

    val success: T?
    val error: ErrorType?
}

private interface ProtocolResponseHandler<T : ProtocolMessage.BaseReply> {
    fun handleResponse(result: ResponseHandlerResult<T>)
}

internal const val BINARY_LOG_TAG = "BIN"
internal const val binaryLogEnabled = true
internal const val NON_ACK_RETRY_COUNT = 3
internal const val RESPONSE_TIMEOUT = 3_000L

@ExperimentalUnsignedTypes
internal fun ByteArray.binaryLog() = this.map { it.toUByte() }.joinToString(separator = ",")

interface QuanaDeviceCommunicatorCallback {
    fun deviceConnected()
    fun deviceDisconnected()
    fun deviceInfoReceived(info: QuanaDeviceInfo)
}

class QuanaDeviceCommunicatorFactory {
    companion object {
        fun createQuanaDeviceCommunicator(
            context: Context,
            deviceAddress: String,
            communicatorCallback: QuanaDeviceCommunicatorCallback? = null
        ): QuanaDeviceCommunicator {
            val rxBleClient = DI.rxBleClient(context.applicationContext)

//            val quanaBluetoothServer = QuanaBluetoothServer(context.applicationContext)
//            quanaBluetoothServer.open()

            val device = rxBleClient.getBleDevice(deviceAddress)

            val quanaBluetoothClient = QuanaBluetoothClient(device)
            quanaBluetoothClient.connect()

            return QuanaDeviceCommunicator(
//                quanaBluetoothServer,
                quanaBluetoothClient,
                communicatorCallback
            )
        }
    }
}


@ExperimentalUnsignedTypes
class QuanaDeviceCommunicator(
//    private val server: QuanaBluetoothServer,
    private val client: QuanaBluetoothClient,
    private val callback: QuanaDeviceCommunicatorCallback?
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
    private var pendingResponseHandler: ((ResponseHandlerResult<ProtocolMessage.BaseReply>) -> Unit)? =
        null
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

    override fun deviceConnected() {
        callback?.deviceConnected()
    }

    override fun deviceDisconnected() {
        resetConnection("Device disconnected")
        callback?.deviceDisconnected()
    }

    override fun deviceInfoReceived(info: QuanaDeviceInfo) {
        callback?.deviceInfoReceived(info)
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

        client.dispose()
//        server.dispose()
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

        val nextMessageId = messageId.getAndIncrement().toUShort()
        val message = messageFactory(nextMessageId)
        pendingRequest = message
        pendingResponseHandler = {
            responseHandler.handleResponse(it as ResponseHandlerResult<T>)
        }
        requestAttemptsCounter.set(NON_ACK_RETRY_COUNT - 1)
        client.write(
            message
        )

        handler.postDelayed({
            if (pendingRequest?.id == nextMessageId) {
                pendingRequest = null
                pendingResponseHandler?.invoke(ResponseHandlerResult(ErrorType.Timeout))
            }
        }, RESPONSE_TIMEOUT)
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
        this.pendingResponseHandler?.invoke(ResponseHandlerResult(response))
        this.pendingResponseHandler = null
    }

    private fun handleNonACKResponse(response: ProtocolMessage.BaseReply) {

        assertThread()

        this.pendingRequest?.let { message ->
            if (response.ack == ErrorCodes.crcFailure.value) {
                if (requestAttemptsCounter.decrementAndGet() > 0) {
                    Timber.d("CRC Failure. Retrying...")
                    client.write(
                        message
                    )
                } else {
                    resetConnection("Non ACK response")
                }
            } else {
                this.pendingRequest = null
                this.pendingResponseHandler?.invoke(ResponseHandlerResult(response))
                this.pendingResponseHandler = null
            }
        }
    }

    fun startScan(callback: ResponseCallback<Boolean>? = null) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.StartScan(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.StartScanReply> {
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.StartScanReply>) {
                callback?.onMessageResult(MessageResult(response.success != null, response.error))
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
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.ResetDeviceReply>) {
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
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.QuitScanReply>) {
                callback?.invoke(true)
            }
        })
    }

    fun setConfigurationParameter(
        parameterCode: Byte,
        values: ByteArray,
        callback: ResponseCallback<Boolean>? = null
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
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.SetConfigurationParameterReply>) {
                callback?.onMessageResult(MessageResult(response.success != null, response.error))
            }
        })
    }

    fun getConfigurationParameter(
        parameterCode: Byte,
        callback: ResponseCallback<ByteArray>? = null
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
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.GetConfigurationParameterReply>) {
                callback?.onMessageResult(MessageResult(response.success?.parameterValues, response.error))
            }
        })
    }

    fun getDeviceStatus(callback: ResponseCallback<DeviceStatus>? = null) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GetDeviceStatus(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.GetDeviceStatusReply> {
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.GetDeviceStatusReply>) {
                callback?.onMessageResult(MessageResult(response.success?.deviceStatus, response.error))
            }
        })
    }

    data class SampleInfo(val sensorCode: UByte, val sampleData: ByteArray, val rawData: ByteArray)

    fun getSample(
        sampleId: UShort,
        callback: ResponseCallback<SampleInfo>? = null
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
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.GetSampleReply>) {
                response.success?.let {
                    callback?.onMessageResult(MessageResult(SampleInfo(it.sensorCode, it.sampleData, it.data), null))
                } ?: callback?.onMessageResult(MessageResult(null, response.error))
            }
        })
    }

    data class ScanResult(val amountOfSamples: UShort, val scanStatus: DeviceStatus)

    fun getScanResults(
        callback: ResponseCallback<ScanResult>? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GetScanResults(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.GetScanResultsReply> {
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.GetScanResultsReply>) {
                response.success?.let {
                    callback?.onMessageResult(MessageResult(ScanResult(it.amountOfSamples, it.scanStatus), null))
                } ?: callback?.onMessageResult(MessageResult(null, response.error))
            }
        })
    }

    fun takeFirmwareChunk(
        chunkId: UInt, address: UInt, chunk: ByteArray,
        callback: ResponseCallback<UInt>? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.TakeFirmwareChunk(id, chunkId, address, chunk)
        }, object : ProtocolResponseHandler<ProtocolMessage.TakeFirmwareChunkReply> {
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.TakeFirmwareChunkReply>) {
                callback?.onMessageResult(MessageResult(response.success?.chunkId, response.error))
            }
        })
    }

    data class ChunkCheckResult(val chunkId: UInt, val ack: UByte)

    fun checkFirmwareChunk(
        chunkId: UInt,
        callback: ResponseCallback<ChunkCheckResult>? = null
    ) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.CheckFirmwareChunk(id, chunkId)
        }, object : ProtocolResponseHandler<ProtocolMessage.CheckFirmwareChunkReply> {
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.CheckFirmwareChunkReply>) {
                response.success?.let {
                    callback?.onMessageResult(MessageResult(ChunkCheckResult(it.chunkId, it.ack), null))
                } ?: callback?.onMessageResult(MessageResult(null, response.error))
            }
        })
    }

    fun goToFirmwareUpdate(callback: ResponseCallback<Boolean>? = null) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GoToFirmwareUpdate(id)
        }, object : ProtocolResponseHandler<ProtocolMessage.GoToFirmwareUpdateReply> {
            override fun handleResponse(response: ResponseHandlerResult<ProtocolMessage.GoToFirmwareUpdateReply>) {
                callback?.onMessageResult(MessageResult(response.success != null, response.error))
            }
        })
    }
}