package il.co.quana

import android.bluetooth.BluetoothGattCharacteristic
import com.polidea.rxandroidble2.RxBleConnection
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

    var callback: QuanaBluetoothClientCallback? = null

    fun connect(): Disposable {

        device.observeConnectionStateChanges()
            .subscribe { state ->
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

    private fun handleCharacteristic(
        connection: RxBleConnection,
        characteristic: BluetoothGattCharacteristic
    ) {
        connection.setupNotification(characteristic)
            .flatMap { notificationObservable -> notificationObservable }
            .subscribe(
                { bytes ->
                    Timber.i("${bytes.size} bytes received")
                    val message = ProtocolMessage.parseReply(bytes)
                    callback?.messageReceived(message)
                    Timber.i("$message")


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
}