package il.co.quana

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.cocosw.bottomsheet.BottomSheet
import com.polidea.rxandroidble2.RxBleClient
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_device.*
import timber.log.Timber
import java.util.*


//private const val ADDRESS = "80:E1:26:00:6A:8B"


class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DEVICE_MAC_ADDRESS = "EXTRA_DEVICE_MAC_ADDRESS"
    }

    private val compositeDisposable = CompositeDisposable()
    private lateinit var rxBleClient: RxBleClient

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
                        Timber.i("Quit scan result-> $it")
                    }
                    R.id.getConfigurationParameter -> quanaDeviceCommunicator.getConfigurationParameter(
                        0
                    ) {
                        Timber.i("Configuration parameter-> ${it[0]}")
                    }
                    R.id.getDeviceStatus -> quanaDeviceCommunicator.getDeviceStatus {
                        Timber.i("Device status-> $it")
                    }
                }
            }.show()

    }
}

fun String.toUUID() = UUID.fromString(this)
