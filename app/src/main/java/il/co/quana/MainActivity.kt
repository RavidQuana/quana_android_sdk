package il.co.quana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var rxBleClient: RxBleClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isLocationPermissionGranted()) {
            scanBleDevices()
        } else {
            Timber.i("ACCESS_COARSE_LOCATION is not granted. Requesting...")
            requestLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isLocationPermissionGranted(requestCode, grantResults)) {
            scanBleDevices()
        } else {
            Timber.e("ACCESS_COARSE_LOCATION was not granted")
        }
    }


    private fun scanBleDevices() {
        rxBleClient = RxBleClient.create(applicationContext)
        val scanSubscription = rxBleClient.scanBleDevices(
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build(),
            ScanFilter.Builder()
//                .setDeviceAddress("80:E1:26:00:6A:8B")
                .build()
        )
            .doOnSubscribe {
                Timber.d("Scanning for BLE devices...")
            }
            .subscribe(
                { scanResult ->
                    Timber.d("BLE Device found: [${scanResult.bleDevice.macAddress}] ${scanResult.bleDevice.name}")
                },
                { throwable ->
                    Timber.e(throwable)
                }
            )

        compositeDisposable.add(scanSubscription)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
