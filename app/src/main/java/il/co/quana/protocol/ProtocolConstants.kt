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

//enum class ScanStatus(val value: Byte) {
//
//    noScanResultsFound(0x00),
//    stillScanning(0x01),
//    scanFailure(0x02),
//    scanCompleted(0x03);
//
//    companion object {
//
//        fun fromValue(value: Byte) =
//            ScanStatus.values().firstOrNull { it.value == value }
//    }
//}


@ExperimentalUnsignedTypes
val ACK: UByte = 0u

@ExperimentalUnsignedTypes
enum class ErrorCodes(val value: UByte) {

    ACK(il.co.quana.protocol.ACK),
    crcFailure(0xE0u), //Require retry
    unknownCommand(0xE1u), // Incompatibility
    operationCurrentlyNotAllowed(0xE2u), //Operation not allowed in current device status. Require follow status
    parameterCodeInvalid(0xE3u); //Incompatibility
//    Sensor not available
//    0xE4
//    HW failure
//    Battery low
//    0xE5
//    Recharge
//    Fan failure
//    0xE6
//    HW failure
//    Heater failure
//    0xE7
//    HW failure
//    Weight failure
//    0xE8
//    HW failure
//    Sample ID missing
//    0xE9
//
//
//    Data lost
//    0xEA
//
//
//    FW chunk wrong CRC
//    0xEB
//
//
//    Saving chunk in process
//    0xEC
//    Check again
//    Failed to save chunk to external flash
//    0xED
//    Retry same chunk 3 times if failed abort FW upgrade
//    Unknown Error
//    0xFF


    companion object {

        fun fromValue(value: UByte) =
            ErrorCodes.values().firstOrNull { it.value == value }
    }
}

