package il.co.quana

import android.bluetooth.*
import android.content.Context
import android.util.Log
import com.polidea.rxandroidble2.RxBleDevice
import il.co.quana.protocol.ProtocolMessage
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.*

class QuanaBluetoothServer(private val context: Context): Disposable {


    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private lateinit var bluetoothServer: BluetoothGattServer
    private lateinit var bluetoothServerCharacteristic: BluetoothGattCharacteristic

    private var disposed = false

    fun open() {
        innerOpen()
    }

    private fun innerOpen() {
        bluetoothServer =
            bluetoothManager.openGattServer(context, object : BluetoothGattServerCallback() {
                override fun onConnectionStateChange(
                    device: BluetoothDevice,
                    status: Int,
                    newState: Int
                ) {
                    Timber.d("Server.onConnectionStateChange Status=$status; newState=$newState")
                }

                override fun onNotificationSent(device: BluetoothDevice, status: Int) {
                    Timber.d("Server.onNotificationSent")
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

    fun write(message: ProtocolMessage, device: RxBleDevice?) =
        write(message, device?.bluetoothDevice)

    fun write(message: ProtocolMessage, device: BluetoothDevice?) {

        val bytes = message.toByteArray()

        bluetoothServerCharacteristic.value = bytes

        if (binaryLogEnabled) {
            Log.d(BINARY_LOG_TAG, "out >> [${bytes.binaryLog()}]")
        }

        val targetDevices =
            if (device != null) listOf(device) else bluetoothManager.getConnectedDevices(
                BluetoothProfile.GATT_SERVER
            )

        targetDevices.forEach { device ->

            bluetoothServer.notifyCharacteristicChanged(
                device,
                bluetoothServerCharacteristic,
                false
            )
        }
    }

    override fun isDisposed() = disposed

    override fun dispose() {
        disposed = true
        bluetoothServer.close()
    }
}