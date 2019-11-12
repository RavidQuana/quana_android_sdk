package il.co.quana.protocol

import timber.log.Timber


@ExperimentalUnsignedTypes
class ProtocolManager(val channelApi: ChannelApi) : ChannelApiCallback {

    init {
        channelApi.registerCallback(this)
    }

    private var pendingRequest: ProtocolMessage? = null

    fun startScan() {
        pendingRequest?.let {
            resetConnection("Can't start scan, there is pending request")
        }
    }

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
            } else {
                handleValidResponse(response)
            }
        }
    }

    private fun handleValidResponse(response: ProtocolMessage) {
        this.pendingRequest = null
    }

    private fun resetConnection(reason: String?) {
        this.pendingRequest = null
        Timber.w("Resetting connection [%s]", reason)
        channelApi.resetConnection()
    }
}