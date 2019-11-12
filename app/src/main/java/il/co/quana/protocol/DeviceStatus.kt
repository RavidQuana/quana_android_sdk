package il.co.quana.protocol

enum class DeviceStatus(val value: Byte) {
    idle(0x00),
    scanning(0x01),
    scanningComplete(0x02),
    calibrating(0x03),
    fwUpgrade(0x04),
    failure(0x05)
}