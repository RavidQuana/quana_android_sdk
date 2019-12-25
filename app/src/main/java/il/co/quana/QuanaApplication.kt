package il.co.quana

import android.app.Application
import android.util.Log.INFO
import com.jraska.console.timber.ConsoleTree
import timber.log.Timber

class QuanaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupKoin()
    }

    fun setupKoin() {
        Timber.w("Koin is not configured")
    }

    fun setupTimber() {
        val consoleTree = ConsoleTree.Builder()
            .minPriority(INFO)
            .build()
        Timber.plant(consoleTree)
        Timber.plant(Timber.DebugTree())

        Timber.i("App Version: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
    }
}