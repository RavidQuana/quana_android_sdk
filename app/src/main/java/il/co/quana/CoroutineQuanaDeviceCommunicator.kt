package il.co.quana

import android.content.Context
import il.co.quana.protocol.DeviceStatus
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CoroutineQuanaDeviceCommunicator(deviceAddress: String, applicationContext: Context, listener: QuanaDeviceCommunicatorCallback){

    init {
        initDevice(deviceAddress, applicationContext, listener = listener)
    }

    private lateinit var quanaDeviceCommunicator: QuanaDeviceCommunicator

    private fun initDevice(deviceAddress: String, applicationContext: Context, listener: QuanaDeviceCommunicatorCallback) {
        quanaDeviceCommunicator = QuanaDeviceCommunicatorFactory.createQuanaDeviceCommunicator(
            applicationContext,
            deviceAddress, listener)
    }

    suspend fun startScan(): MessageResult<Boolean> =
        suspendCoroutine<MessageResult<Boolean>> { continuation ->
            quanaDeviceCommunicator.startScan(object : ResponseCallback<Boolean> {
                override fun onMessageResult(messageResult: MessageResult<Boolean>) {
                    continuation.resume(messageResult)
                }
            })
        }

    suspend fun resetDevice(): MessageResult<Boolean> =
        suspendCoroutine<MessageResult<Boolean>> { continuation ->
            quanaDeviceCommunicator.resetDevice { object : ResponseCallback<Boolean> {
                    override fun onMessageResult(messageResult: MessageResult<Boolean>) {
                        continuation.resume(messageResult)
                    }
                }
            }
        }

    suspend fun quitScan(): MessageResult<Boolean> =
        suspendCoroutine<MessageResult<Boolean>> { continuation ->
            quanaDeviceCommunicator.quitScan { object : ResponseCallback<Boolean> {
                    override fun onMessageResult(messageResult: MessageResult<Boolean>) {
                        continuation.resume(messageResult)
                    }
                }
            }
        }

    suspend fun getDeviceStatus(): MessageResult<DeviceStatus> =
        suspendCoroutine<MessageResult<DeviceStatus>> { continuation ->
            quanaDeviceCommunicator.getDeviceStatus(object : ResponseCallback<DeviceStatus> {
                override fun onMessageResult(messageResult: MessageResult<DeviceStatus>) {
                    continuation.resume(messageResult)
                }
            })
        }


    suspend fun getSample(index: Int): MessageResult<QuanaDeviceCommunicator.SampleInfo>  = suspendCoroutine<MessageResult<QuanaDeviceCommunicator.SampleInfo>> { continuation ->
        quanaDeviceCommunicator.getSample(index.toUShort(), object : ResponseCallback<QuanaDeviceCommunicator.SampleInfo>{
            override fun onMessageResult(messageResult: MessageResult<QuanaDeviceCommunicator.SampleInfo>) {
                continuation.resume(messageResult)
            }
        })
    }

    suspend fun getScanResults(): MessageResult<QuanaDeviceCommunicator.ScanResult> =
        suspendCoroutine<MessageResult<QuanaDeviceCommunicator.ScanResult>> { continuation ->
            quanaDeviceCommunicator.getScanResults(object :
                ResponseCallback<QuanaDeviceCommunicator.ScanResult> {
                override fun onMessageResult(messageResult: MessageResult<QuanaDeviceCommunicator.ScanResult>) {
                    continuation.resume(messageResult)
                }
            })
        }

    suspend fun takeFirmwareChunk(chunkId: UInt, address: UInt, chunk: ByteArray): MessageResult<UInt> = suspendCoroutine<MessageResult<UInt>> { continuation ->
            quanaDeviceCommunicator.takeFirmwareChunk(chunkId, address, chunk, object : ResponseCallback<UInt>{
                override fun onMessageResult(messageResult: MessageResult<UInt>) {
                    continuation.resume(messageResult)
                }
            })
    }

    suspend fun goToFirmwareUpdate(): MessageResult<Boolean> = suspendCoroutine<MessageResult<Boolean>> { continuation ->
            quanaDeviceCommunicator.goToFirmwareUpdate( object : ResponseCallback<Boolean>{
                override fun onMessageResult(messageResult: MessageResult<Boolean>) {
                    continuation.resume(messageResult)
                }
            })
    }
}