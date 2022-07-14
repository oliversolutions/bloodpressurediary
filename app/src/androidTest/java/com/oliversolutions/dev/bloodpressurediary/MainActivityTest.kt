package com.oliversolutions.dev.bloodpressurediary

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.main.MainViewModel
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureDataSource
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import com.oliversolutions.dev.bloodpressurediary.settings.DataViewModel
import com.oliversolutions.dev.bloodpressurediary.settings.RemindersViewModel
import com.oliversolutions.dev.bloodpressurediary.statistics.StatisticViewModel
import com.oliversolutions.dev.bloodpressurediary.util.DataBindingIdlingResource
import com.oliversolutions.dev.bloodpressurediary.util.monitorActivity
import com.oliversolutions.dev.bloodpressurediary.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest : AutoCloseKoinTest() {
    private lateinit var repository: BloodPressureDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Before
    fun init() {
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel {
                DataViewModel(
                    get(),
                    get() as BloodPressureDataSource
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
                    get() as BloodPressureDataSource
                )
            }
            viewModel {
                MainViewModel(
                    get(),
                    get() as BloodPressureDataSource
                )
            }
            single { BloodPressureRepository(get()) as BloodPressureDataSource }
            single { BloodPressureDatabase.getInstance(appContext) }
        }

        startKoin {
            androidContext(appContext)
            modules(listOf(myModule))
        }
        repository = get()
        runBlocking {
            repository.clear()
        }
    }

    @Test
    fun saveBloodPressure_getToastMessage() = runBlocking {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario) // LOOK HERE
        Espresso.onView(withId(R.id.bloodPressureSave)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.systolic)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.diastolic)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.notes_edit_text)).perform(
            ViewActions.replaceText("Blood Pressure Notes"))
        Espresso.onView(withId(R.id.add_new_record)).perform(ViewActions.click())
        val activity = getActivity(activityScenario)
        Espresso.onView(withText(R.string.blood_pressure_saved))
            .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.`is`(activity!!.window.decorView))))
            .check(
                matches(
                    isDisplayed()
                )
            )
        activityScenario.close()
    }

    private fun getActivity(activityScenario: ActivityScenario<MainActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }
}
