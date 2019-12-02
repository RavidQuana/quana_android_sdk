package il.co.quana

import il.co.quana.protocol.ACK
import il.co.quana.protocol.ProtocolException
import il.co.quana.protocol.ProtocolMessage
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger


private interface ProtocolResponseHandler<T : ProtocolMessage.BaseReply> {
    fun handleResponse(response: T)
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

    private var pendingRequest: ProtocolMessage? = null
    private var pendingResponseHandler: ((ProtocolMessage.BaseReply) -> Unit)? = null

    private val messageId = AtomicInteger(0)

    override fun messageReceived(response: ProtocolMessage) {
        handleResponse(response)
    }

    override fun messageError(exception: ProtocolException) {
        Timber.i(exception)
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
        Timber.w("Resetting connection [%s]", reason)
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

    @Suppress("UNCHECKED_CAST")
    private fun <T : ProtocolMessage.BaseReply> sendMessage(
        messageFactory: ((messageId: UShort) -> ProtocolMessage),
        responseHandler: ProtocolResponseHandler<T>
    ) {
        val message = messageFactory(messageId.getAndIncrement().toUShort())
        pendingRequest = message
        pendingResponseHandler = {
            responseHandler.handleResponse(it as T)
        }
        server.write(
            message,
            client.device
        )
    }

    private fun handleValidResponse(response: ProtocolMessage.BaseReply) {
        if (response.ack == ACK) {
            this.pendingRequest = null
            this.pendingResponseHandler?.invoke(response)
            this.pendingResponseHandler = null
        } else {
            handleNonACKResponse(response)
        }
    }

    private fun handleNonACKResponse(response: ProtocolMessage.BaseReply) {

    }

    fun startScan() {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.StartScan(id.toUShort())
        }, object : ProtocolResponseHandler<ProtocolMessage.StartScanReply> {
            override fun handleResponse(response: ProtocolMessage.StartScanReply) {
            }
        })
    }

    fun quitScan() {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.QuitScan(id.toUShort())
        }, object : ProtocolResponseHandler<ProtocolMessage.QuitScanReply> {
            override fun handleResponse(response: ProtocolMessage.QuitScanReply) {
            }
        })
    }

    fun setConfigurationParameter(parameterCode: Byte, values: ByteArray) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.SetConfigurationParameter(
                id.toUShort(),
                parameterCode.toUByte(),
                values
            )
        }, object : ProtocolResponseHandler<ProtocolMessage.SetConfigurationParameterReply> {
            override fun handleResponse(response: ProtocolMessage.SetConfigurationParameterReply) {
            }
        })
    }

    fun getConfigurationParameter(parameterCode: Byte) {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GetConfigurationParameter(
                id.toUShort(),
                parameterCode.toUByte()
            )
        }, object : ProtocolResponseHandler<ProtocolMessage.GetConfigurationParameterReply> {
            override fun handleResponse(response: ProtocolMessage.GetConfigurationParameterReply) {
            }
        })
    }

    fun getDeviceStatus() {
        if (!assertIdle()) {
            return
        }

        sendMessage({ id ->
            ProtocolMessage.GetDeviceStatus(
                id.toUShort()
            )
        }, object : ProtocolResponseHandler<ProtocolMessage.GetDeviceStatusReply> {
            override fun handleResponse(response: ProtocolMessage.GetDeviceStatusReply) {
            }
        })
    }
}