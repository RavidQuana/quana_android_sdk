package il.co.quana

import android.app.Application
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
            .build()
        Timber.plant(consoleTree)
    }
}