package il.co.quana

import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.internal.RxBleLog
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import il.co.quana.protocol.ProtocolMessage
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

private const val SERVICE_UUID = "65636976-7265-7320-5444-20616e617551"
private const val CHARACTERISTIC_UUID = "74636172-6168-6320-5444-20616e617551"

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var rxBleClient: RxBleClient
    private lateinit var bluetoothServer: BluetoothGattServer
    private lateinit var bluetoothCharacterstic: BluetoothGattCharacteristic


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Timber.i("User country: ${resources.configuration.locales[0].country}")
        }

        if (isLocationPermissionGranted()) {
            openGattServer()
            scanBleDevices()
        } else {
            Timber.i("ACCESS_COARSE_LOCATION is not granted. Requesting...")
            requestLocationPermission()
        }

        RxBleLog.setLogger { level, tag, msg -> Timber.tag(tag).log(level, msg) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isLocationPermissionGranted(requestCode, grantResults)) {
            openGattServer()
            scanBleDevices()
        } else {
            Timber.e("ACCESS_COARSE_LOCATION was not granted")
        }
    }

    private fun openGattServer() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothServer =
            bluetoothManager.openGattServer(this, object : BluetoothGattServerCallback() {
                override fun onConnectionStateChange(
                    device: BluetoothDevice,
                    status: Int,
                    newState: Int
                ) {
                    Timber.d("onConnectionStateChange Status=$status; newState=$newState")

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        bluetoothCharacterstic.value = TmpTmp.notifyConnectedDevices("1")
                        bluetoothServer.notifyCharacteristicChanged(
                            device,
                            bluetoothCharacterstic,
                            false
                        )


                        val bluetoothGatt = device.connectGatt(applicationContext, false, object:
                            BluetoothGattCallback() {


                            override fun onCharacteristicChanged(
                                gatt: BluetoothGatt?,
                                characteristic: BluetoothGattCharacteristic?
                            ) {
                                super.onCharacteristicChanged(gatt, characteristic)
                                Timber.d("onCharacteristicChanged")
                            }


                        })
                        bluetoothGatt.setCharacteristicNotification(bluetoothGatt.getService(
                            UUID.fromString(SERVICE_UUID)
                        ).getCharacteristic(
                            UUID.fromString(CHARACTERISTIC_UUID)
                        ), true)
                    }
                }

                override fun onCharacteristicReadRequest(
                    device: BluetoothDevice?,
                    requestId: Int,
                    offset: Int,
                    characteristic: BluetoothGattCharacteristic?
                ) {
                    Timber.d("onCharacteristicReadRequest")
                    bluetoothServer.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        offset,
                        byteArrayOf(0)
                    )
                }

                override fun onCharacteristicWriteRequest(
                    device: BluetoothDevice?,
                    requestId: Int,
                    characteristic: BluetoothGattCharacteristic?,
                    preparedWrite: Boolean,
                    responseNeeded: Boolean,
                    offset: Int,
                    value: ByteArray?
                ) {
                    Timber.d("onCharacteristicWriteRequest")
                }

                override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
                    Timber.d("onNotificationSent")
                }
            })

        val service = BluetoothGattService(
            UUID.fromString(SERVICE_UUID),
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        bluetoothCharacterstic = BluetoothGattCharacteristic(
            UUID.fromString(CHARACTERISTIC_UUID),
            //supports notifications
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        service.addCharacteristic(bluetoothCharacterstic)
        bluetoothServer.addService(service)
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
            .delay(1, TimeUnit.SECONDS)
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

        bluetoothServer.connect(device.bluetoothDevice, false)




//        device.establishConnection(false)
////            .delay(10, TimeUnit.SECONDS)
//            .subscribe(
//                { connection ->
//                    handleDeviceConnection(device, connection)
//                },
//                { throwable ->
//                    Timber.e(throwable)
//                }
//            ).let {
//                compositeDisposable.add(it)
//            }
    }

    private fun handleDeviceConnection(device: RxBleDevice, connection: RxBleConnection) {
        connection.discoverServices()
            .subscribe(
                { services ->
                    services.bluetoothGattServices.first {
                        UUID.fromString(SERVICE_UUID) == it.uuid
                    }?.let { service ->
                        Timber.i("Gatt Service: [${service.uuid}]")
                        handleService(connection, service)
                    }
                },
                { throwable ->
                    Timber.e(throwable)
                }
            ).let {
                compositeDisposable.add(it)
            }
    }

    private fun handleService(connection: RxBleConnection, service: BluetoothGattService) {
        service.characteristics.forEach {
            Timber.d("Characteristic: ${it.uuid}")

            if (UUID.fromString(CHARACTERISTIC_UUID) == it.uuid) {
                Timber.i("Found Required Characteristic")
                handleCharacteristic(connection, service, it)
            }
        }
    }

    private fun handleCharacteristic(
        connection: RxBleConnection,
        service: BluetoothGattService,
        characteristic: BluetoothGattCharacteristic
    ) {


//        connection.setupNotification(characteristic)
//            .doOnNext { notificationObservable ->
//                Timber.d("Notification setup successfully")
//            }
//            .flatMap { notificationObservable -> notificationObservable } // <-- Notification has been set up, now observe value changes.
//            .subscribe({ bytes ->
//                Timber.d("${bytes.size} bytes received")
//            }, { throwable ->
//                Timber.e(throwable)
//            }
//            ).let {
//                compositeDisposable.add(it)
//            }

//        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
//        characteristic.value = ProtocolMessage.StartScan(0u).toByteArray()

//        connection.writeCharacteristic(
//            characteristic,
//            ProtocolMessage.StartScan(0u).toByteArray()
//        )
//            .subscribe(
//                { characteristicValue ->
//                    Timber.d("${characteristicValue.size} bytes sent")
//
//                },
//                { throwable ->
//                    Timber.e(throwable)
//                }
//            ).let {
//                compositeDisposable.add(it)
//            }

//        connection.readCharacteristic(characteristic)
//            .subscribe(
//                { characteristicValue ->
//                    Timber.d("Value: ${characteristicValue.size} bytes")
//                },
//                { throwable ->
//                    Timber.e(throwable)
//                }
//            ).let {
//                compositeDisposable.add(it)
//            }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
