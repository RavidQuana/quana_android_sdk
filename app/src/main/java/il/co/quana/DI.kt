package il.co.quana

import android.content.Context
import com.polidea.rxandroidble2.RxBleClient

object DI {
    private var _rxBleClient: RxBleClient? = null
    fun rxBleClient(applicationContext: Context): RxBleClient {
        _rxBleClient = _rxBleClient ?: RxBleClient.create(applicationContext)
        return _rxBleClient!!
    }
}