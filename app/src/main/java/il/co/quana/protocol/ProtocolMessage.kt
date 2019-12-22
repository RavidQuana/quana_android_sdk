package il.co.quana.protocol

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.xor

val START_IDENTIFIER = byteArrayOf(0x51, 0x75)

val ProtocolByteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN

@ExperimentalUnsignedTypes
enum class ProtocolOpcode(val value: UByte) {

    SetConfigurationParameter(0x01u),
    GetConfigurationParameter(0x02u),
    GetStatus(0x03u),
    StartScan(0x04u),
    QuitScan(0x05u),
    GetSample(0x06u),
    ChangeToFirmwareUpgrade(0x07u),
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
                    ProtocolOpcode.QuitScan -> QuitScanReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.GetConfigurationParameter -> GetConfigurationParameterReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.GetStatus -> GetDeviceStatusReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.GetSample -> GetSampleReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.ChangeToFirmwareUpgrade -> GoToFirmwareUpdateReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.TakeFirmwareChunk -> TakeFirmwareChunkReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.CheckFirmwareChunkSaved -> CheckFirmwareChunkReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.Reset -> ResetDeviceReply(
                        messageId,
                        data
                    )
                    ProtocolOpcode.GetScanResults -> GetScanResultsReply(
                        messageId,
                        data
                    )
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

    override fun toString() = "${this.javaClass.simpleName} [id=$id; dataSize=${data.size}]"

    abstract class BaseReply(id: UShort, opcode: ProtocolOpcode) :
        ProtocolMessage(id, opcode, byteArrayOf()) {

        abstract val ack: UByte

        override fun toString() = "${this.javaClass.simpleName} [id=$id; ack=${ack}]"
    }

    abstract class SimpleReply(id: UShort, opcode: ProtocolOpcode, data: ByteArray) :
        BaseReply(id, opcode) {
        override val ack = data[0].toUByte()
    }

    //----------------------------------------------------------------------------------------------

    class StartScan(id: UShort) :
        ProtocolMessage(id, ProtocolOpcode.StartScan, byteArrayOf())

    class StartScanReply(id: UShort, data: ByteArray) :
        SimpleReply(id, ProtocolOpcode.StartScan, data)

    //----------------------------------------------------------------------------------------------

    class QuitScan(id: UShort) :
        ProtocolMessage(id, ProtocolOpcode.QuitScan, byteArrayOf())

    class QuitScanReply(id: UShort, data: ByteArray) :
        SimpleReply(id, ProtocolOpcode.QuitScan, data)

    //----------------------------------------------------------------------------------------------

    class SetConfigurationParameter(
        id: UShort,
        val parameterCode: UByte,
        val parameterValues: ByteArray
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

        override fun toString() =
            "${this.javaClass.simpleName} [id=$id; code=${parameterCode}; values=${parameterValues.firstOrNull()}...]"

    }

    class SetConfigurationParameterReply(id: UShort, data: ByteArray) :
        BaseReply(id, ProtocolOpcode.SetConfigurationParameter) {
        val parameterCode: UByte = data[0].toUByte()
        override val ack = data[1].toUByte()
    }

    //----------------------------------------------------------------------------------------------

    class GetConfigurationParameter(id: UShort, parameterCode: UByte) : ProtocolMessage(
        id, ProtocolOpcode.GetConfigurationParameter, byteArrayOf(parameterCode.toByte())
    )

    class GetConfigurationParameterReply(id: UShort, data: ByteArray) :
        BaseReply(id, ProtocolOpcode.GetConfigurationParameter) {

        val parameterCode: UByte
        val parameterValues: ByteArray
        override val ack: UByte

        init {
            val buffer = ByteBuffer.wrap(data)
                .order(ProtocolByteOrder)

            parameterCode = buffer.get().toUByte()
            val length = buffer.get()
            parameterValues = ByteArray(length.toInt())
            buffer.get(parameterValues)
            ack = buffer.get().toUByte()
        }

        override fun toString() =
            "${this.javaClass.simpleName} [id=$id; code=${parameterCode}; values=${parameterValues.firstOrNull()}...]"
    }

    //----------------------------------------------------------------------------------------------

    class GetDeviceStatus(id: UShort) :
        ProtocolMessage(id, ProtocolOpcode.GetStatus, byteArrayOf())

    class GetDeviceStatusReply(id: UShort, data: ByteArray) :
        BaseReply(id, ProtocolOpcode.GetStatus) {
        val deviceStatus: DeviceStatus = DeviceStatus.fromValue(data[0]) ?: throw ProtocolException(
            "Unknown DeviceStatus[${data[0]}]"
        )
        override val ack = data[1].toUByte()

        override fun toString() =
            "${this.javaClass.simpleName} [id=$id; deviceStatus=${deviceStatus}]"

    }

    //----------------------------------------------------------------------------------------------

    class GetSample(id: UShort, sampleId: UShort) : ProtocolMessage(
        id,
        ProtocolOpcode.GetSample,
        ByteBuffer.allocate(UShort.SIZE_BYTES).order(ProtocolByteOrder).putShort(sampleId.toShort()).safeArray()
    )

    class GetSampleReply(id: UShort, data: ByteArray) :
        BaseReply(id, ProtocolOpcode.GetSample) {

        val sensorCode: UByte
        val sampleId: UShort
        val sampleData: ByteArray

        override val ack: UByte

        init {
            val buffer = ByteBuffer.wrap(data)
                .order(ProtocolByteOrder)

            sensorCode = buffer.get().toUByte()
            sampleId = buffer.getShort().toUShort()
            val length = buffer.get()
            sampleData = ByteArray(length.toInt())
            buffer.get(sampleData)
            ack = buffer.get().toUByte()
        }
    }

    //----------------------------------------------------------------------------------------------

    class GoToFirmwareUpdate(id: UShort) :
        ProtocolMessage(id, ProtocolOpcode.ChangeToFirmwareUpgrade, byteArrayOf())

    class GoToFirmwareUpdateReply(id: UShort, data: ByteArray) :
        SimpleReply(id, ProtocolOpcode.ChangeToFirmwareUpgrade, data)

    //----------------------------------------------------------------------------------------------

    class ResetDevice(id: UShort) :
        ProtocolMessage(id, ProtocolOpcode.Reset, byteArrayOf())

    class ResetDeviceReply(id: UShort, data: ByteArray) :
        SimpleReply(id, ProtocolOpcode.Reset, data)

    //----------------------------------------------------------------------------------------------

    class GetScanResults(id: UShort) : ProtocolMessage(
        id, ProtocolOpcode.GetScanResults, byteArrayOf()
    )

    class GetScanResultsReply(id: UShort, data: ByteArray) :
        BaseReply(id, ProtocolOpcode.GetScanResults) {

        val scanStatus: DeviceStatus
        val amountOfSamples: UShort
        override val ack: UByte

        init {
            val buffer = ByteBuffer.wrap(data)
                .order(ProtocolByteOrder)

            val scanStatusByte = buffer.get()
            scanStatus = DeviceStatus.fromValue(scanStatusByte)
                ?: throw ProtocolException("Unknown ScanStatus[$scanStatusByte]")
            amountOfSamples = buffer.getShort().toUShort()

            ack = buffer.get().toUByte()
        }
    }

    //----------------------------------------------------------------------------------------------

    class TakeFirmwareChunk(
        id: UShort,
        val chunkId: UInt,
        val address: UInt,
        val chunk: ByteArray
    ) :
        ProtocolMessage(
            id,
            ProtocolOpcode.TakeFirmwareChunk,
            assembleData(chunkId, address, chunk)
        ) {
        companion object {
            private fun assembleData(chunkId: UInt, address: UInt, chunk: ByteArray): ByteArray {
                return ByteBuffer.allocate(
                    chunk.size +
                            UInt.SIZE_BYTES +
                            UInt.SIZE_BYTES +
                            Byte.SIZE_BYTES
                )
                    .order(ProtocolByteOrder)
                    .putInt(chunkId.toInt())
                    .putInt(address.toInt())
                    .put(chunk.size.toByte())
                    .put(chunk)
                    .safeArray()
            }
        }

        override fun toString() =
            "${this.javaClass.simpleName} [id=$id; chunkId=${chunkId}; address=${address}; chunk=${chunk.firstOrNull()}...]"

    }

    class TakeFirmwareChunkReply(id: UShort, data: ByteArray) :
        BaseReply(id, ProtocolOpcode.TakeFirmwareChunk) {

        val chunkId: UInt

        override val ack: UByte

        init {
            val buffer = ByteBuffer.wrap(data)
                .order(ProtocolByteOrder)

            chunkId = buffer.getInt().toUInt()
            ack = buffer.get().toUByte()
        }
    }

    //----------------------------------------------------------------------------------------------

    class CheckFirmwareChunk(id: UShort, chunkId: UInt) : ProtocolMessage(
        id,
        ProtocolOpcode.CheckFirmwareChunkSaved,
        ByteBuffer.allocate(UShort.SIZE_BYTES).order(ProtocolByteOrder).putInt(chunkId.toInt()).safeArray()
    )

    class CheckFirmwareChunkReply(id: UShort, data: ByteArray) :
        BaseReply(id, ProtocolOpcode.CheckFirmwareChunkSaved) {

        val chunkId: UInt

        override val ack: UByte

        init {
            val buffer = ByteBuffer.wrap(data)
                .order(ProtocolByteOrder)

            chunkId = buffer.getInt().toUInt()
            ack = buffer.get().toUByte()
        }
    }
}


private fun ByteBuffer.safeArray() = if (hasArray()) {
    array()
} else {
    val array = ByteArray(capacity())
    get(array)
    array
}

private fun ByteBuffer.writtenArray() = safeArray().sliceArray(0 until position())

class ProtocolException @JvmOverloads constructor(
    override val message: String = "Unknown exception",
    override val cause: Throwable? = null
) : Exception()
