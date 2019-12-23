package il.co.quana

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import il.co.quana.ui.TestDeviceActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_device_lookup.*
import kotlinx.android.synthetic.main.list_item.view.*
import timber.log.Timber

class DeviceLookupActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var rxBleClient: RxBleClient

    private val scannedDevices = mutableSetOf<ScanResult>()
    private lateinit var adapter: DevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_lookup)

        adapter = DevicesAdapter(object : DiffUtil.ItemCallback<ScanResult>() {
            override fun areItemsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean {
                return oldItem.bleDevice.macAddress == newItem.bleDevice.macAddress
            }

            override fun areContentsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean {
                return oldItem.bleDevice.macAddress == newItem.bleDevice.macAddress
            }

        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.listener = { scanResult ->

            Intent(this, TestDeviceActivity::class.java).apply {
                putExtra(TestDeviceActivity.EXTRA_DEVICE_MAC_ADDRESS, scanResult.bleDevice.macAddress)
            }.let {
                startActivity(it)
                finish()
            }
//            Intent(this, DeviceActivity::class.java).apply {
//                putExtra(DeviceActivity.EXTRA_DEVICE_MAC_ADDRESS, scanResult.bleDevice.macAddress)
//            }.let {
//                startActivity(it)
//            }
        }

        if (isLocationPermissionGranted()) {
            startFlow()
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
            startFlow()
        } else {
            Timber.e("ACCESS_COARSE_LOCATION was not granted")
        }
    }

    fun startFlow() {
        scanBleDevices()
    }

    private fun scanBleDevices() {

        rxBleClient = DI.rxBleClient(applicationContext)

        val scanSubscription = rxBleClient.scanBleDevices(
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build(),
            ScanFilter.empty()
        )
            .filter {
                it.scanRecord.deviceName?.startsWith("Quana") ?: false
                        || it.bleDevice.name?.startsWith("Quana") ?: false

            }
            .doOnSubscribe {
                Timber.d("Scanning for BLE devices...")
            }
//            .take(100)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { scanResult ->
                    Timber.d("BLE Device found: [${scanResult.bleDevice.macAddress}] ${scanResult.bleDevice.name}")
                    scanResult.scanRecord.serviceUuids?.forEach {
                        Timber.d("UUID: [$it]")
                    }
                    addScanResult(scanResult)
                },
                { throwable ->
                    Timber.e(throwable)
                }
            )

        compositeDisposable.add(scanSubscription)
    }

    private fun addScanResult(result: ScanResult) {
        scannedDevices.removeAll {
            it.bleDevice.macAddress == result.bleDevice.macAddress
        }
        scannedDevices.add(result)

        adapter.submitList(scannedDevices.sortedWith(Comparator { o1, o2 ->
            o1.bleDevice.macAddress.compareTo(
                o2.bleDevice.macAddress
            )
        }).toList())
    }
}


class DevicesAdapter(diffCallback: DiffUtil.ItemCallback<ScanResult>) :
    ListAdapter<ScanResult, DevicesAdapter.ViewHolder>(diffCallback) {

    var listener: ((ScanResult) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setScanResult(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1 = itemView.text1
        private val text2 = itemView.text2

        init {
            itemView.setOnClickListener {
                listener?.invoke(getItem(adapterPosition))
            }
        }

        fun setScanResult(scanResult: ScanResult) {
            text1.text = scanResult.scanRecord.deviceName ?: "Unknown Device"
            text2.text = scanResult.bleDevice.macAddress
        }
    }

}
