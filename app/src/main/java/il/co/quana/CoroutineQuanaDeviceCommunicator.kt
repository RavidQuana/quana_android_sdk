package il.co.quana

import android.content.Context
import il.co.quana.protocol.DeviceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CoroutineQuanaDeviceCommunicator(deviceAddress: String, applicationContext: Context){

    init {
        initDevice(deviceAddress, applicationContext)
    }

    private lateinit var quanaDeviceCommunicator: QuanaDeviceCommunicator

    private fun initDevice(deviceAddress: String, applicationContext: Context) {
        quanaDeviceCommunicator = QuanaDeviceCommunicatorFactory.createQuanaDeviceCommunicator(
            applicationContext,
            deviceAddress)
    }

    suspend fun startScan(): Boolean = suspendCoroutine<Boolean> { continuation ->
            quanaDeviceCommunicator.startScan { success ->
                continuation.resume(success)
            }
    }

    suspend fun resetDevice(): Boolean = suspendCoroutine<Boolean> { continuation ->
            quanaDeviceCommunicator.resetDevice {success ->
                continuation.resume(success)
            }
    }

    suspend fun quitScan(): Boolean = suspendCoroutine<Boolean> { continuation ->
            quanaDeviceCommunicator.quitScan {success ->
                continuation.resume(success)
            }
    }

    suspend fun getDeviceStatus(): DeviceStatus = suspendCoroutine<DeviceStatus> { continuation ->
            quanaDeviceCommunicator.getDeviceStatus {deviceStatus ->
                continuation.resume(deviceStatus)
            }
    }


    suspend fun getSample(index: Int): Sample = suspendCoroutine<Sample> { continuation ->
            quanaDeviceCommunicator.getSample(index.toUShort()){sensorCode,sampleData  ->
                continuation.resume(Sample(sensorCode, sampleData))
            }
    }

    suspend fun getScanResults(): ScanResult = suspendCoroutine<ScanResult> { continuation ->
            quanaDeviceCommunicator.getScanResults{ amountOfSamples, scanStatus  ->
                continuation.resume(ScanResult(amountOfSamples, scanStatus))
            }
    }

    suspend fun takeFirmwareChunk(chunkId: UInt, address: UInt, chunk: ByteArray): UInt = suspendCoroutine<UInt> { continuation ->
            quanaDeviceCommunicator.takeFirmwareChunk(chunkId, address, chunk){ chunkId  ->
                continuation.resume(chunkId)
            }
    }

    suspend fun goToFirmwareUpdate(): Boolean = suspendCoroutine<Boolean> { continuation ->
            quanaDeviceCommunicator.goToFirmwareUpdate{ success  ->
                continuation.resume(success)
            }
    }

    data class Sample(
        val sensorCode: UByte,
        val sampleData: ByteArray
    )

    data class ScanResult(
        val amountOfSamples: UShort,
        val scanStatus: DeviceStatus
    )

}