package il.co.quana

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.cocosw.bottomsheet.BottomSheet
import kotlinx.android.synthetic.main.activity_device.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.CountDownLatch


//private const val ADDRESS = "80:E1:26:00:6A:8B"


class DeviceActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DEVICE_MAC_ADDRESS = "EXTRA_DEVICE_MAC_ADDRESS"
    }

    private var amountOfScans = 0

    private lateinit var quanaDeviceCommunicator: QuanaDeviceCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        if (isLocationPermissionGranted()) {
            startFlow()
        } else {
            Timber.e("ACCESS_COARSE_LOCATION is not granted. Stopping...")
        }

        button.setOnClickListener {
            buttonClicked()
        }
    }

    fun startFlow() {

        val address = intent.getStringExtra(EXTRA_DEVICE_MAC_ADDRESS)
        if (TextUtils.isEmpty(address)) {
            Timber.e("EXTRA_DEVICE_MAC_ADDRESS is required. Stopping...")
        } else {
            connectToDevice(address)
        }
    }

    private fun connectToDevice(deviceAddress: String) {

        quanaDeviceCommunicator = QuanaDeviceCommunicatorFactory.createQuanaDeviceCommunicator(
            applicationContext,
            deviceAddress
        )

    }


    fun buttonClicked() {

        BottomSheet.Builder(this).title("Message To Send").sheet(R.menu.messages)
            .listener { dialog, which ->
                when (which) {
                    R.id.startScan -> quanaDeviceCommunicator.startScan {
                        Timber.i("Start scan result-> $it")
                    }
                    R.id.quitScan -> quanaDeviceCommunicator.quitScan {
                        Timber.i("Quit scan result-> $it")
                    }
                    R.id.setConfigurationParameter -> quanaDeviceCommunicator.setConfigurationParameter(
                        0,
                        byteArrayOf(20)
                    ) {
                        Timber.i("Set configuration parameter-> $it")
                    }
                    R.id.getConfigurationParameter -> quanaDeviceCommunicator.getConfigurationParameter(
                        0
                    ) {
                        Timber.i("Configuration parameter-> ${it[0]}")
                    }
                    R.id.getDeviceStatus -> quanaDeviceCommunicator.getDeviceStatus {
                        Timber.i("Device status-> $it")
                    }
                    R.id.getScanResults -> quanaDeviceCommunicator.getScanResults { amount, status ->
                        Timber.i("Scan results -> amount=$amount, status=$status")
                        amountOfScans = amount.toInt()
                    }
                    R.id.getSample -> getScanResults()
                    R.id.resetDevice -> quanaDeviceCommunicator.resetDevice {
                        Timber.i("Reset result-> $it")
                    }
                    R.id.takeFirmwareChunk -> quanaDeviceCommunicator.takeFirmwareChunk(
                        0u,
                        777u,
                        byteArrayOf(1, 2, 3, 4, 5)
                    ) {
                        Timber.i("Take FW Chunk-> $it")
                    }
                    R.id.goToFirmwareUpdate -> quanaDeviceCommunicator.goToFirmwareUpdate {
                        Timber.i("Go to FW Update-> $it")
                    }
                    R.id.checkFirmwareChunk -> quanaDeviceCommunicator.checkFirmwareChunk(0u) { id, ack ->
                        Timber.i("Check FW Chunk-> ack=$ack")
                    }

                }
            }.show()
    }

    fun getScanResults() {
        Thread {
            (1..amountOfScans).forEach { index ->
                val countDownLatch = CountDownLatch(1)
                Timber.i("Getting sample $index/$amountOfScans")
                quanaDeviceCommunicator.getSample(index.toUShort()) { sensorCode, sampleData ->
                    Timber.i("Ready $sensorCode/$sensorCode, sampleData=${sampleData.size} bytes")
                    countDownLatch.countDown()
                }
                countDownLatch.await()
                Thread.sleep(500)
            }
            Timber.i("--- Done getting samples ---")
        }.start()
    }
}

fun String.toUUID() = UUID.fromString(this)
