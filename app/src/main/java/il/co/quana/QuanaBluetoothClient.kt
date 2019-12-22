package il.co.quana

import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleConnection.GATT_MTU_MAXIMUM
import com.polidea.rxandroidble2.RxBleDevice
import il.co.quana.protocol.ProtocolException
import il.co.quana.protocol.ProtocolMessage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

interface QuanaBluetoothClientCallback {
    fun messageReceived(response: ProtocolMessage)
    fun messageError(exception: ProtocolException)
}

class QuanaBluetoothClient(val device: RxBleDevice) {

    private val compositeDisposable = CompositeDisposable()

    var connection: RxBleConnection? = null

    var callback: QuanaBluetoothClientCallback? = null

    fun connect(): Disposable {

        device.observeConnectionStateChanges()
            .subscribe { state ->
                if (state == RxBleConnection.RxBleConnectionState.DISCONNECTED) {
                    connection = null
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

        return compositeDisposable
    }

    private fun handleConnection(connection: RxBleConnection) {
        this.connection = connection

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

    fun getDeviceInfo() {
        arrayOf(
            GattAttributes.FIRMWARE_REVISION_STRING,
            GattAttributes.MANUFACTURER_NAME_STRING,
            GattAttributes.HARDWARE_REVISION_STRING,
            GattAttributes.SOFTWARE_REVISION_STRING
        ).forEach {
            readAndPrint(it)
        }
    }

    private fun readAndPrint(uuid: String) {
        connection?.let { connection ->
            connection
                .readCharacteristic(uuid.toUUID())
                .map { bytes -> String(bytes) }
                .subscribe({ value ->
                    Timber.d("${GattAttributes.lookup(uuid)} = $value")
                },
                    { throwable ->
                        Timber.e(throwable)
                    }
                )
                .let {
                    compositeDisposable.add(it)
                }
        }
    }
}