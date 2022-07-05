package com.oliversolutions.dev.bloodpressurediary.main

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oliversolutions.dev.bloodpressurediary.MainCoroutineRule
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.data.FakeDataSource
import com.oliversolutions.dev.bloodpressurediary.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class BloodPressureEditViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var bloodPressureEditViewModel: BloodPressureEditViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupBloodPressureEditViewModel() {
        fakeDataSource = FakeDataSource()
        bloodPressureEditViewModel = BloodPressureEditViewModel(null, ApplicationProvider.getApplicationContext(), fakeDataSource)
    }


    @Test
    fun saveBloodPressure_loading() {
        mainCoroutineRule.pauseDispatcher()
        val bloodPressureData = BloodPressure(
            120.0,
            60.0,
            52.0,
            "hola",
            "11:38",
            "2022-07-22",
            null,
            null,
            null
        )
        bloodPressureEditViewModel.createNewBloodPressure(bloodPressureData)
        MatcherAssert.assertThat(
            bloodPressureEditViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(
            bloodPressureEditViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }

}
