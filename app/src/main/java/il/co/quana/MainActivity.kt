package il.co.quana

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import il.co.quana.protocol.ProtocolMessage
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*

const val SERVICE_UUID = "65636976-7265-7320-5444-20616e617551"
const val CHARACTERISTIC_UUID = "74636172-6168-6320-5444-20616e617551"
private const val ADDRESS = "80:E1:26:00:6A:8B"
private const val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"


class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var rxBleClient: RxBleClient

    private lateinit var quanaBluetoothServer: QuanaBluetoothServer
    private lateinit var bluetoothDevice: BluetoothDevice

    private var scanning = false

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

//        rxBleClient.bondedDevices.let {
//            Timber.i("${it.size} bonded devices")
//            it.forEach { device ->
//                Timber.i("Bonded device ${device.name}")
//            }
//        }

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

//                    scanResult.bleDevice.observeConnectionStateChanges()
//                        .subscribe { state ->
//                            Timber.d("State = %s", state)
//                        }.let {
//                            compositeDisposable.add(it)
//                        }

                },
                { throwable ->
                    Timber.e(throwable)
                }
            )

        compositeDisposable.add(scanSubscription)
    }

    private fun connectToDevice(device: RxBleDevice) {

        device.observeConnectionStateChanges()
            .subscribe { state ->
                Timber.d("State = %s", state)
            }.let {
                compositeDisposable.add(it)
            }


        bluetoothDevice = device.bluetoothDevice

        device.establishConnection(false)
            .subscribe(
                { connection ->
                    handleConnection(connection)
                },
                { throwable ->
                    Timber.e(throwable)
                }
            ).let {
                compositeDisposable.add(it)
            }
    }

    private fun handleConnection(connection: RxBleConnection) {
        connection.discoverServices()
            .flatMap { services -> services.getService(SERVICE_UUID.toUUID()) }
            .map { service -> service.getCharacteristic(CHARACTERISTIC_UUID.toUUID()) }
            .subscribe({ characteristic ->
                handleCharacteristic(connection, characteristic)
            },
                { throwable ->
                    Timber.e(throwable)
                }
            )
            .let {
                compositeDisposable.add(it)
            }
    }

    @SuppressLint("CheckResult")
    private fun handleCharacteristic(
        connection: RxBleConnection,
        characteristic: BluetoothGattCharacteristic
    ) {

//        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG.toUUID())
//        connection.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
//            .andThen {
                connection.setupNotification(characteristic)
                    .flatMap { notificationObservable -> notificationObservable }
                    .subscribe(
                        { bytes ->
                            Timber.i("${bytes.size} bytes received")
                            val message = ProtocolMessage.parseReply(bytes)
                            Timber.i("$message")

                        },
                        { throwable ->
                            Timber.e(throwable)
                        }
                    ).let {
                        compositeDisposable.add(it)
                    }
//            }
    }
//
//    private fun _connectToDevice(device: BluetoothDevice) {
//        bluetoothDevice = device
//        bluetoothGatt = device.connectGatt(applicationContext, false, object :
//            BluetoothGattCallback() {
//
//
//            override fun onCharacteristicChanged(
//                gatt: BluetoothGatt,
//                characteristic: BluetoothGattCharacteristic
//            ) {
//                Timber.d("onCharacteristicChanged bytes=${characteristic.value.size}")
//            }
//
//            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
//                if (newState == BluetoothProfile.STATE_CONNECTED) {
//                    gatt.discoverServices()
//                }
//            }
//
//            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
//                Timber.d("onServicesDiscovered")
//
//
//                val deviceCharacteristic =
//                    gatt.getService(UUID.fromString(SERVICE_UUID)).getCharacteristic(
//                        UUID.fromString(
//                            CHARACTERISTIC_UUID
//                        )
//                    )
//
//                Timber.i("Characteristic = ${deviceCharacteristic.uuid}")
//
//                val descriptor = deviceCharacteristic
//                    .getDescriptor(
//                        UUID
//                            .fromString(CLIENT_CHARACTERISTIC_CONFIG)
//                    )
//                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
//                gatt.writeDescriptor(descriptor)
//
//                gatt.setCharacteristicNotification(deviceCharacteristic, true)
//            }
//        })
//
//    }

    private var messageId = 0u

    fun buttonPuuushed() {
        quanaBluetoothServer.write(
            ProtocolMessage.StartScan(messageId++.toUShort()),
            bluetoothDevice
        )
    }
}

fun String.toUUID() = UUID.fromString(this)
