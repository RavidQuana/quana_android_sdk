package il.co.quana.protocol

interface ChannelApi {
    fun sendMessage(message: ProtocolMessage)
    fun resetConnection()
    fun registerCallback(callback: ChannelApiCallback)
}

interface ChannelApiCallback {
    fun messageReceived(response: ProtocolMessage)
    fun messageError(exception: ProtocolException)
}