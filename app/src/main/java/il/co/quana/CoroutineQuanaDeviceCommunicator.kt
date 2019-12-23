package il.co.quana

import android.content.Context
import il.co.quana.protocol.DeviceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CoroutineQuanaDeviceCommunicator(deviceAddress: String, applicationContext: Context){

    private lateinit var quanaDeviceCommunicator: QuanaDeviceCommunicator

    init {
        initDevice(deviceAddress, applicationContext)
    }

    private fun initDevice(deviceAddress: String, applicationContext: Context){
        quanaDeviceCommunicator = QuanaDeviceCommunicatorFactory.createQuanaDeviceCommunicator(
            applicationContext,
            deviceAddress)
    }


    suspend fun startScan(): Boolean = withContext(Dispatchers.IO){
        suspendCoroutine<Boolean> { coroutine ->
            quanaDeviceCommunicator.startScan { success ->
                coroutine.resume(success)
            }}
    }

    suspend fun resetDevice(): Boolean = withContext(Dispatchers.IO){
        suspendCoroutine<Boolean> { coroutine ->
            quanaDeviceCommunicator.resetDevice {success ->
                coroutine.resume(success)
            }
        }
    }

    suspend fun quitScan(): Boolean = withContext(Dispatchers.IO){
        suspendCoroutine<Boolean> { coroutine ->
            quanaDeviceCommunicator.quitScan {success ->
                coroutine.resume(success)
            }
        }
    }

    suspend fun getDeviceStatus(): DeviceStatus = withContext(Dispatchers.IO){
        suspendCoroutine<DeviceStatus> { coroutine ->
            quanaDeviceCommunicator.getDeviceStatus {deviceStatus ->
                coroutine.resume(deviceStatus)
            }
        }
    }


    suspend fun getSample(index: Int): Sample = withContext(Dispatchers.IO){
        suspendCoroutine<Sample> { coroutine ->
            quanaDeviceCommunicator.getSample(index.toUShort()){sensorCode,sampleData  ->
                coroutine.resume(Sample(sensorCode, sampleData))
            }
        }
    }

    suspend fun getScanResults(): ScanResult = withContext(Dispatchers.IO){
        suspendCoroutine<ScanResult> { coroutine ->
            quanaDeviceCommunicator.getScanResults{ amountOfSamples, scanStatus  ->
                coroutine.resume(ScanResult(amountOfSamples, scanStatus))
            }
        }
    }

    suspend fun takeFirmwareChunk(chunkId: UInt, address: UInt, chunk: ByteArray): UInt = withContext(Dispatchers.IO){
        suspendCoroutine<UInt> { coroutine ->
            quanaDeviceCommunicator.takeFirmwareChunk(chunkId, address, chunk){ chunkId  ->
                coroutine.resume(chunkId)
            }
        }
    }

    suspend fun goToFirmwareUpdate(): Boolean = withContext(Dispatchers.IO){
        suspendCoroutine<Boolean> { coroutine ->
            quanaDeviceCommunicator.goToFirmwareUpdate{ success  ->
                coroutine.resume(success)
            }
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