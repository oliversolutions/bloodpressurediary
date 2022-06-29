package com.oliversolutions.dev.bloodpressurediary

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.main.MainViewModel
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import com.oliversolutions.dev.bloodpressurediary.settings.DataViewModel
import com.oliversolutions.dev.bloodpressurediary.settings.RemindersViewModel
import com.oliversolutions.dev.bloodpressurediary.statistics.StatisticViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App: Application() {
    companion object {
        lateinit var prefs: Prefs
        lateinit var instance: App
            private set
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = Prefs(applicationContext)

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                DataViewModel(
                    get(),
                    get() as BloodPressureRepository
                )
            }
            viewModel {
                RemindersViewModel(
                    get()
                )
            }
            viewModel {
                StatisticViewModel(
                    get(),
                    get() as BloodPressureRepository
                )
            }
            viewModel {
                MainViewModel(
                    get(),
                    get() as BloodPressureRepository
                )
            }

            single { BloodPressureRepository(get())}
            single { BloodPressureDatabase.getInstance(this@App) }
        }

        startKoin {
            androidContext(this@App)
            modules(listOf(myModule))
        }
    }
}
