package il.co.quana

import il.co.quana.protocol.ACK
import il.co.quana.protocol.ProtocolException
import il.co.quana.protocol.ProtocolMessage
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger


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

    private fun sendMessage(factory: ((messageId: UShort) -> ProtocolMessage)) {
        val message = factory(messageId.getAndIncrement().toUShort())
        pendingRequest = message
        server.write(
            message,
            client.device
        )
    }

    private fun handleValidResponse(response: ProtocolMessage.BaseReply) {
        this.pendingRequest = null
        if (response.ack == ACK) {
            when (response) {
            }
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

        sendMessage { id ->
            ProtocolMessage.StartScan(id.toUShort())
        }
    }

    fun setConfigurationParameter(parameterCode: Byte, values: ByteArray) {
        if (!assertIdle()) {
            return
        }

        sendMessage { id ->
            ProtocolMessage.SetConfigurationParameter(
                id.toUShort(),
                parameterCode.toUByte(),
                values
            )
        }
    }

    fun getConfigurationParameter(parameterCode: Byte) {
        if (!assertIdle()) {
            return
        }

        sendMessage { id ->
            ProtocolMessage.GetConfigurationParameter(
                id.toUShort(),
                parameterCode.toUByte()
            )
        }
    }

    fun getDeviceStatus() {
        if (!assertIdle()) {
            return
        }

        sendMessage { id ->
            ProtocolMessage.GetDeviceStatus(
                id.toUShort()
            )
        }
    }
}