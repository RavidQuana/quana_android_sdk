package il.co.quana

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cocosw.bottomsheet.BottomSheet
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*


const val SERVICE_UUID = "65636976-7265-7320-5444-20616e617551"
const val CHARACTERISTIC_UUID = "74636172-6168-6320-5444-20616e617551"
private const val ADDRESS = "80:E1:26:00:6A:8B"


class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var rxBleClient: RxBleClient

    private lateinit var quanaBluetoothServer: QuanaBluetoothServer
    private lateinit var quanaBluetoothClient: QuanaBluetoothClient

    private lateinit var quanaDeviceCommunicator: QuanaDeviceCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Timber.i("User country: ${resources.configuration.locales[0].country}")
        }

        if (isLocationPermissionGranted()) {
            startFlow()
        } else {
            Timber.i("ACCESS_COARSE_LOCATION is not granted. Requesting...")
            requestLocationPermission()
        }

//        RxBleLog.setLogger { level, tag, msg -> Timber.tag(tag).log(level, msg) }

        button.setOnClickListener {
            buttonPuuushed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isLocationPermissionGranted(requestCode, grantResults)) {
            startFlow()
        } else {
            Timber.e("ACCESS_COARSE_LOCATION was not granted")
        }
    }

    fun startFlow() {
        openQuanaServer()
        scanBleDevices()
    }

    private fun openQuanaServer() {
        quanaBluetoothServer = QuanaBluetoothServer(applicationContext)
        quanaBluetoothServer.open().let { compositeDisposable.add(it) }
    }

    private fun scanBleDevices() {

        rxBleClient = RxBleClient.create(applicationContext)

        val scanSubscription = rxBleClient.scanBleDevices(
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build(),
            ScanFilter.Builder()
                .setDeviceAddress("80:E1:26:00:6A:8B")
                .build()
        )
            .doOnSubscribe {
                Timber.d("Scanning for BLE devices...")
            }
            .take(1)
            .subscribe(
                { scanResult ->
                    Timber.d("BLE Device found: [${scanResult.bleDevice.macAddress}] ${scanResult.bleDevice.name}")
                    scanResult.scanRecord.serviceUuids?.forEach {
                        Timber.d("UUID: [$it]")
                    }
                    connectToDevice(scanResult.bleDevice)
                },
                { throwable ->
                    Timber.e(throwable)
                }
            )

        compositeDisposable.add(scanSubscription)
    }

    private fun connectToDevice(device: RxBleDevice) {

        quanaBluetoothClient = QuanaBluetoothClient(device)
        quanaBluetoothClient.connect()
            .let { compositeDisposable.add(it) }

        quanaDeviceCommunicator =
            QuanaDeviceCommunicator(quanaBluetoothServer, quanaBluetoothClient)


    }


    fun buttonPuuushed() {

        BottomSheet.Builder(this).title("Message To Send").sheet(R.menu.messages)
            .listener { dialog, which ->
                when (which) {
                    R.id.startScan -> quanaDeviceCommunicator.startScan()
                    R.id.quitScan -> quanaDeviceCommunicator.quitScan()
                    R.id.setConfigurationParameter -> quanaDeviceCommunicator.setConfigurationParameter(
                        7,
                        byteArrayOf(1, 2, 3, 4, 5)
                    )
                    R.id.getConfigurationParameter -> quanaDeviceCommunicator.getConfigurationParameter(
                        7
                    )
                    R.id.getDeviceStatus -> quanaDeviceCommunicator.getDeviceStatus()
                }
            }.show()

    }
}

fun String.toUUID() = UUID.fromString(this)
