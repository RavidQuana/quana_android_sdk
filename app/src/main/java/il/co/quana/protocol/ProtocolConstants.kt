package il.co.quana.protocol

enum class DeviceStatus(val value: Byte) {
    idle(0x00),
    scanning(0x01),
    scanningComplete(0x02),
    calibrating(0x03),
    fwUpgrade(0x04),
    failure(0x05);

    companion object {

        fun fromValue(value: Byte) =
            DeviceStatus.values().firstOrNull { it.value == value }
    }
}

enum class ScanStatus(val value: Byte) {

    noScanResultsFound(0x00),
    stillScanning(0x01),
    scanFailure(0x02),
    scanCompleted(0x03);

    companion object {

        fun fromValue(value: Byte) =
            ScanStatus.values().firstOrNull { it.value == value }
    }
}