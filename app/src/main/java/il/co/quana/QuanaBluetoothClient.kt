package il.co.quana

import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleConnection.GATT_MTU_MAXIMUM
import com.polidea.rxandroidble2.RxBleDevice
import il.co.quana.protocol.ProtocolException
import il.co.quana.protocol.ProtocolMessage
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import timber.log.Timber

data class QuanaDeviceInfo(
    val firmwareRevision: String?,
    val manufacturerName: String?,
    val hardwareRevision: String?,
    val softwareRevision: String?
) {
    override fun toString(): String {
        return "QuanaDeviceInfo(firmwareRevision=$firmwareRevision, manufacturerName=$manufacturerName, hardwareRevision=$hardwareRevision, softwareRevision=$softwareRevision)"
    }
}

interface QuanaBluetoothClientCallback {
    fun messageReceived(response: ProtocolMessage)
    fun messageError(exception: ProtocolException)
    fun deviceConnected()
    fun deviceDisconnected()
    fun deviceInfoReceived(info: QuanaDeviceInfo)
}

class QuanaBluetoothClient(val device: RxBleDevice) :
    Disposable {

    private val compositeDisposable = CompositeDisposable()

    var connection: RxBleConnection? = null

    var callback: QuanaBluetoothClientCallback? = null

    private var writabeCharacteristic: BluetoothGattCharacteristic? = null

    fun connect() {

        device.observeConnectionStateChanges()
            .subscribe { state ->
                when (state) {
                    RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                        connection = null
                        callback?.deviceDisconnected()
                    }
                    RxBleConnection.RxBleConnectionState.CONNECTED -> {
                        callback?.deviceConnected()
                    }
                }
                Timber.d("Client: State = %s", state)
            }.let {
                compositeDisposable.add(it)
            }


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
        this.connection = connection

        connection.discoverServices()
            .flatMap { services -> services.getService(SERVICE_UUID.toUUID()) }
            .map { service ->
                arrayOf(
                    service.getCharacteristic(CHARACTERISTIC_UUID.toUUID()),
                    service.getCharacteristic(WRITEABLE_CHARACTERISTIC_UUID.toUUID())
                )
            }
            .subscribe({ characteristics ->
                characteristics.firstOrNull { it.uuid == CHARACTERISTIC_UUID.toUUID() }?.let {
                    handleCharacteristic(connection, it)
                }
                characteristics.firstOrNull { it.uuid == WRITEABLE_CHARACTERISTIC_UUID.toUUID() }?.let {
                    handleWriteableCharacteristic(connection, it)
                }
            },
                { throwable ->
                    Timber.e(throwable)
                }
            )
            .let {
                compositeDisposable.add(it)
            }

        getDeviceInfo()
    }

    private fun handleCharacteristic(
        connection: RxBleConnection,
        characteristic: BluetoothGattCharacteristic
    ) {
        connection.requestMtu(GATT_MTU_MAXIMUM)
            .toObservable()
            .flatMap { connection.setupNotification(characteristic) }
            .flatMap { notificationObservable -> notificationObservable }
            .subscribe(
                { bytes ->
                    Timber.d("${bytes.size} bytes received")

                    if (binaryLogEnabled) {
                        Log.d(BINARY_LOG_TAG, "in  << [${bytes.binaryLog()}]")
                    }

                    val message = ProtocolMessage.parseReply(bytes)
                    callback?.messageReceived(message)
                    Timber.d("$message")


                },
                { throwable ->
                    callback?.messageError(
                        ProtocolException(
                            "Can't read protocol message",
                            throwable
                        )
                    )
                    Timber.e(throwable)
                }
            ).let {
                compositeDisposable.add(it)
            }
    }

    private fun handleWriteableCharacteristic(
        connection: RxBleConnection,
        characteristic: BluetoothGattCharacteristic
    ) {
        this.writabeCharacteristic = characteristic
    }

    fun write(message: ProtocolMessage) {

        val bytes = message.toByteArray()

        writabeCharacteristic?.let {
            connection?.writeCharacteristic(it,bytes)?.subscribe(
                { bytes ->
                    Timber.i("${bytes.size} written")
                },
                { throwable ->
                    Timber.e(throwable)
                }
            )
        }

        if (binaryLogEnabled) {
            Log.d(BINARY_LOG_TAG, "out >> [${bytes.binaryLog()}]")
        }
    }

    fun getDeviceInfo() {
        val observables = arrayOf(
            GattAttributes.FIRMWARE_REVISION_STRING,
            GattAttributes.MANUFACTURER_NAME_STRING,
            GattAttributes.HARDWARE_REVISION_STRING,
            GattAttributes.SOFTWARE_REVISION_STRING
        ).map { uuid ->
            readStringCharacteristic(uuid)
        }

        Observables.combineLatest(
            readStringCharacteristic(GattAttributes.FIRMWARE_REVISION_STRING),
            readStringCharacteristic(GattAttributes.MANUFACTURER_NAME_STRING),
            readStringCharacteristic(GattAttributes.HARDWARE_REVISION_STRING),
            readStringCharacteristic(GattAttributes.SOFTWARE_REVISION_STRING)
        ) { firmware, manufacturer, hardware, software ->
            return@combineLatest QuanaDeviceInfo(firmware, manufacturer, hardware, software)
        }.subscribe {
            callback?.deviceInfoReceived(it)
        }
    }

    private fun readStringCharacteristic(uuid: String) = connection?.let { connection ->
        connection
            .readCharacteristic(uuid.toUUID())
            .map { bytes -> String(bytes) }
            .toObservable()
    } ?: Observable.just("")

    override fun isDisposed() = compositeDisposable.isDisposed

    override fun dispose() = compositeDisposable.dispose()
}