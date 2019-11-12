package il.co.quana.protocol

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.xor

val START_IDENTIFIER = byteArrayOf(0x51, 0x75)

val ProtocolByteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN

private val MAX_BUFFER_LENGTH = 150 //Base on ble packetSize

@ExperimentalUnsignedTypes
val ACK: UByte = 0u //Base on ble packetSize

@ExperimentalUnsignedTypes
@Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
enum class ProtocolOpcode(val value: UByte) {

    SetConfigurationParameter(0x01u),
    GetConfigurationParameter(0x02u),
    GetStatus(0x03u),
    StartScan(0x04u),
    QuitScan(0x05u),
    GetSample(0x06u),
    ChangeToFirwareUpgrade(0x07u),
    TakeFirmwareChunk(0x08u),
    CheckFirmwareChunkSaved(0x09u),
    Reset(0x0Au),
    GetScanResults(0x0Bu);

    companion object {
        fun fromValue(value: UByte) = values().firstOrNull { it.value == value }
    }

}

@ExperimentalUnsignedTypes
sealed class ProtocolMessage(
    val id: UShort,
    val opcode: ProtocolOpcode,
    private val data: ByteArray
) {
    companion object {

        private fun computeCRC(byteArray: ByteArray, start: Int? = null, end: Int? = null): Byte {
            var crc: Byte = 0
            for (i in (start ?: 0)..(end ?: byteArray.size)) {
                crc = crc.xor(byteArray[i])
            }
            return crc
        }

        private fun validateCRC(byteArray: ByteArray): Boolean {
            return computeCRC(byteArray, 0, byteArray.size - 2) == byteArray[byteArray.size - 1]
        }

        fun parseReply(byteArray: ByteArray): ProtocolMessage {
            if (!validateCRC(byteArray)) {
                throw ProtocolException("Invalid CRC")
            }


            val byteBuffer = ByteBuffer.wrap(byteArray)
            byteBuffer.order(ProtocolByteOrder)

            byteBuffer.position(START_IDENTIFIER.size) // Skipping START_IDENTIFIER for now

            val messageId = byteBuffer.getShort().toUShort()
            val opcodeValue = byteBuffer.get().toUByte()
            val dataLength = byteBuffer.get()
            val data = ByteArray(dataLength.toInt())
            byteBuffer.get(data)

            return ProtocolOpcode.fromValue(opcodeValue)?.let { opcode ->
                when (opcode) {
                    ProtocolOpcode.SetConfigurationParameter -> SetConfigurationParameterReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.StartScan -> StartScanReply(
                        messageId,
                        data
                    )
                    else -> throw ProtocolException("Unsupported opcode $opcode")
                }

            } ?: throw ProtocolException("Unknown Opcode")
        }
    }

    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(size())
            .order(ProtocolByteOrder)
            .put(START_IDENTIFIER)
            .putShort(id.toShort())
            .put(opcode.value.toByte())
            .put(data.size.toByte())
            .put(data)

        val array = buffer.safeArray()
        val crc = computeCRC(array, 0, array.size - 2)
        array[array.size - 1] = crc
        return array
    }

    private fun size() =
        START_IDENTIFIER.size + //Qu
                Short.SIZE_BYTES + //id
                Byte.SIZE_BYTES + //opcode
                Byte.SIZE_BYTES + //data length
                data.size +
                Byte.SIZE_BYTES //CRC

    abstract class BaseReply(id: UShort, opcode: ProtocolOpcode) :
        ProtocolMessage(id, opcode, byteArrayOf()) {

        abstract val ack: UByte
    }

    abstract class SimpleReply(id: UShort, opcode: ProtocolOpcode, data: ByteArray) :
        BaseReply(id, opcode) {
        override val ack = data[0].toUByte()
    }

    class SetConfigurationParameter(
        id: UShort,
        parameterCode: UByte,
        parameterValues: ByteArray
    ) :
        ProtocolMessage(
            id,
            ProtocolOpcode.SetConfigurationParameter,
            assembleData(parameterCode, parameterValues)
        ) {
        companion object {
            private fun assembleData(parameterCode: UByte, parameterValues: ByteArray): ByteArray {
                return ByteBuffer.allocate(
                    parameterValues.size +
                            Byte.SIZE_BYTES +
                            Byte.SIZE_BYTES
                )
                    .order(ProtocolByteOrder)
                    .put(parameterCode.toByte())
                    .put(parameterValues.size.toByte())
                    .put(parameterValues)
                    .safeArray()
            }
        }
    }

    class SetConfigurationParameterReply(id: UShort, data: ByteArray) :
        BaseReply(id, ProtocolOpcode.SetConfigurationParameter) {
        val parameterCode: UByte = data[0].toUByte()
        override val ack = data[1].toUByte()
    }

    class StartScan(id: UShort) :
        ProtocolMessage(id, ProtocolOpcode.StartScan, byteArrayOf())

    class StartScanReply(id: UShort, data: ByteArray) :
        SimpleReply(id, ProtocolOpcode.StartScan, data)
}


private fun ByteBuffer.safeArray() = if (hasArray()) {
    array()
} else {
    val array = ByteArray(capacity())
    get(array)
    array
}

private fun ByteBuffer.writtenArray() = safeArray().sliceArray(0 until position())

class ProtocolException(message: String) : Exception(message)
