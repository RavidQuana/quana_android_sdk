package il.co.quana

import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*

private const val SERVICE_UUID = "65636976-7265-7320-5444-20616e617551"
private const val CHARACTERISTIC_UUID = "74636172-6168-6320-5444-20616e617551"
private const val ADDRESS = "80:E1:26:00:6A:8B"
private const val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"


class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothServer: BluetoothGattServer
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothServerCharacteristic: BluetoothGattCharacteristic
    private lateinit var bluetoothGatt: BluetoothGatt

    private var scanning = false

    private val handler = Handler()


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
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        openGattServer()
        scanBleDevices()
    }

    private fun openGattServer() {
        bluetoothServer =
            bluetoothManager.openGattServer(this, object : BluetoothGattServerCallback() {
                override fun onConnectionStateChange(
                    device: BluetoothDevice,
                    status: Int,
                    newState: Int
                ) {
                    Timber.d("Server.onConnectionStateChange Status=$status; newState=$newState")
                }

                override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
                    Timber.d("onNotificationSent")
                }
            })

        val service = BluetoothGattService(
            UUID.fromString(SERVICE_UUID),
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        bluetoothServerCharacteristic = BluetoothGattCharacteristic(
            UUID.fromString(CHARACTERISTIC_UUID),
            //supports notifications
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        service.addCharacteristic(bluetoothServerCharacteristic)
        bluetoothServer.addService(service)
    }

    private fun scanBleDevices() {

        if (scanning) {
            return
        }

        scanning = true

        bluetoothManager.adapter.startLeScan(object : BluetoothAdapter.LeScanCallback {
            override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray?) {
                if (device.address.equals(ADDRESS, true)) {
                    Timber.i("Device found: ${device.name}")

                    if (scanning) {
                        connectToDevice(device)
                    }

                    bluetoothManager.adapter.stopLeScan(this)
                    scanning = false
                }
            }
        })
    }

    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothDevice = device
        bluetoothGatt = device.connectGatt(applicationContext, false, object :
            BluetoothGattCallback() {


            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                Timber.d("onCharacteristicChanged bytes=${characteristic.value.size}")
            }

            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                Timber.d("onServicesDiscovered")


                val deviceCharacteristic =
                    gatt.getService(UUID.fromString(SERVICE_UUID)).getCharacteristic(
                        UUID.fromString(
                            CHARACTERISTIC_UUID
                        )
                    )

                Timber.i("Characteristic = ${deviceCharacteristic.uuid}")

                val descriptor = deviceCharacteristic
                    .getDescriptor(
                        UUID
                            .fromString(CLIENT_CHARACTERISTIC_CONFIG)
                    )
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)

                gatt.setCharacteristicNotification(deviceCharacteristic, true)
            }
        })

    }

    fun buttonPuuushed() {
        bluetoothServerCharacteristic.value = TmpTmp.notifyConnectedDevices("1")
        bluetoothServer.notifyCharacteristicChanged(
            bluetoothDevice,
            bluetoothServerCharacteristic,
            false
        )
    }
}
