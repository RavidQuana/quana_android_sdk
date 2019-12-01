package il.co.quana.protocol

import org.junit.Test
import kotlin.experimental.and
import kotlin.experimental.xor
import il.co.quana.protocol.ProtocolMessage.*

@ExperimentalUnsignedTypes
class ProtocolMessageTest {


    @ExperimentalUnsignedTypes
    @Test
    fun `test messages format messages`() {

        ProtocolOpcode.values().forEach { protocolOpcode ->
            when (protocolOpcode) {
                ProtocolOpcode.SetConfigurationParameter -> {
                    test_SetConfigurationParameter()
                    test_SetConfigurationParameterReply()
                }
                ProtocolOpcode.GetConfigurationParameter -> {
                }
                ProtocolOpcode.GetStatus -> {
                }
                ProtocolOpcode.StartScan -> {
                    test_StartScan()
                    test_SimpleReply(ProtocolOpcode.StartScan)
                }
                ProtocolOpcode.QuitScan -> {
                }
                ProtocolOpcode.GetSample -> {
                }
                ProtocolOpcode.ChangeToFirmwareUpgrade -> {
                }
                ProtocolOpcode.TakeFirmwareChunk -> {
                }
                ProtocolOpcode.CheckFirmwareChunkSaved -> {
                }
                ProtocolOpcode.Reset -> {
                }
                ProtocolOpcode.GetScanResults -> {
                }
            }
        }
    }
}

@ExperimentalUnsignedTypes
fun test_SetConfigurationParameter() {
    val id: UShort = 32768u
    val parameterCode: UByte = 255u
    val values = ByteArray(200) { it.toByte() }

    val message = SetConfigurationParameter(id, parameterCode, values)

    val messageBytes = message.toByteArray()
    validateCRC(messageBytes)

    val expectedBytes = withCRC(
        byteArrayOf(
            START_IDENTIFIER[0],
            START_IDENTIFIER[1],
            id.firstByteLittleEndian(),
            id.secondByteLittleEndian(),
            ProtocolOpcode.SetConfigurationParameter.value.toByte(),
            (values.size + 2).toByte(), //Without CRC
            parameterCode.toByte(),
            values.size.toByte()
        ) + values + byteArrayOf(0)
    )

    assert(expectedBytes.contentEquals(messageBytes))
}

@ExperimentalUnsignedTypes
fun test_SetConfigurationParameterReply() {
    val id: UShort = 32768u
    val parameterCode: UByte = 255u
    val reply = ProtocolMessage.parseReply(
        withCRC(
            byteArrayOf(
                START_IDENTIFIER[0],
                START_IDENTIFIER[1],
                id.firstByteLittleEndian(),
                id.secondByteLittleEndian(),
                ProtocolOpcode.SetConfigurationParameter.value.toByte(),
                2,
                parameterCode.toByte(),
                ACK.toByte(),
                0
            )
        )
    ) as SetConfigurationParameterReply

    assert(reply.id == id)
    assert(reply.opcode == ProtocolOpcode.SetConfigurationParameter)
    assert(reply.parameterCode == parameterCode)
    assert(reply.ack == ACK)
}

@ExperimentalUnsignedTypes
fun test_StartScan() {
    val id: UShort = 32768u

    val message = StartScan(id)

    val messageBytes = message.toByteArray()
    validateCRC(messageBytes)

    val expectedBytes = withCRC(
        byteArrayOf(
            START_IDENTIFIER[0],
            START_IDENTIFIER[1],
            id.firstByteLittleEndian(),
            id.secondByteLittleEndian(),
            ProtocolOpcode.StartScan.value.toByte(),
            0.toByte(), //Without CRC
            0
        )
    )

    assert(expectedBytes.contentEquals(messageBytes))
}

@ExperimentalUnsignedTypes
fun test_SimpleReply(opcode: ProtocolOpcode) {
    val id: UShort = 32768u
    val reply = ProtocolMessage.parseReply(
        withCRC(
            byteArrayOf(
                START_IDENTIFIER[0],
                START_IDENTIFIER[1],
                id.firstByteLittleEndian(),
                id.secondByteLittleEndian(),
                opcode.value.toByte(),
                1.toByte(),
                ACK.toByte(),
                0
            )
        )
    ) as SimpleReply

    assert(reply.id == id)
    assert(reply.opcode == opcode)
    assert(reply.ack == ACK)
}


@ExperimentalUnsignedTypes
fun UShort.secondByteLittleEndian() = (this.toInt() ushr 8).and(0xFF).toByte()

@ExperimentalUnsignedTypes
fun UShort.firstByteLittleEndian() = this.toShort().and(0xFF).toByte()

fun withCRC(byteArray: ByteArray): ByteArray {
    byteArray[byteArray.size - 1] = computeCRC(byteArray, 0, byteArray.size - 2)
    return byteArray
}

fun computeCRC(byteArray: ByteArray, start: Int? = null, end: Int? = null): Byte {
    var crc: Byte = 0
    for (i in (start ?: 0)..(end ?: byteArray.size)) {
        crc = crc.xor(byteArray[i])
    }
    return crc
}

private fun validateCRC(byteArray: ByteArray): Boolean {
    return computeCRC(byteArray, 0, byteArray.size - 2) == byteArray[byteArray.size - 1]
}