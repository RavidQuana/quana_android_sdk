package il.co.quana

import android.app.Application
import com.jraska.console.timber.ConsoleTree
import il.co.quana.di.appModule
import il.co.quana.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class QuanaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupKoin()
    }

    fun setupKoin() {
        //Timber.w("Koin is not configured")
        // start Koin (di) context
        startKoin {
            androidContext(this@QuanaApplication)
            androidLogger()
            modules(arrayListOf(appModule, networkModule))
        }
    }

    fun setupTimber() {
//        val consoleTree = ConsoleTree.Builder()
//            .build()
//        Timber.plant(consoleTree)
        Timber.plant(Timber.DebugTree())
    }
}