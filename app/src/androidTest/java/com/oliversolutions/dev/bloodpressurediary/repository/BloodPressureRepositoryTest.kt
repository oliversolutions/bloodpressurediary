package com.oliversolutions.dev.bloodpressurediary.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.oliversolutions.dev.bloodpressurediary.utils.Result
import com.oliversolutions.dev.bloodpressurediary.utils.succeeded
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class BloodPressureRepositoryTest {
    private lateinit var bloodPressureRepository: BloodPressureRepository
    private lateinit var database: BloodPressureDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initRepoAndDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BloodPressureDatabase::class.java).allowMainThreadQueries().build()
        bloodPressureRepository = BloodPressureRepository(database, Dispatchers.Main)
    }

    @Test
    fun getNonExistentBloodPressure_dataNotFound() = runBlocking {
        val nonExistentId = "555"
        val result = bloodPressureRepository.getBloodPressure(nonExistentId)
        result as Result.Error
        ViewMatchers.assertThat(result.message, CoreMatchers.`is`("Blood pressure not found!"))
    }

    @Test
    fun saveBloodPressure_retrievesBloodPressure() = runBlocking {
        val bloodPressureData = BloodPressureDTO(
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
        bloodPressureRepository.createNewBloodPressure(bloodPressureData)
        val result = bloodPressureRepository.getBloodPressure(bloodPressureData.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.notes, `is`("hola"))
        assertThat(result.data.systolic, `is`(120.0))
        assertThat(result.data.creationTime, `is`("11:38"))
    }
}