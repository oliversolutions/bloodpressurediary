package com.oliversolutions.dev.bloodpressurediary.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class BloodPressureDatabaseDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: BloodPressureDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BloodPressureDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
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
        database.bloodPressureDatabaseDao.insert(bloodPressureData)
        val loaded = database.bloodPressureDatabaseDao.getBloodPressure(bloodPressureData.id)
        MatcherAssert.assertThat(loaded, Matchers.notNullValue())
        loaded.let {
            MatcherAssert.assertThat(it.notes, `is`(bloodPressureData.notes))
            MatcherAssert.assertThat(it.id, `is`(bloodPressureData.id))
            MatcherAssert.assertThat(it.systolic, `is`(bloodPressureData.systolic))
            MatcherAssert.assertThat(it.diastolic, `is`(bloodPressureData.diastolic))
            MatcherAssert.assertThat(it.pulse, `is`(bloodPressureData.pulse))
            MatcherAssert.assertThat(it.creationTime, `is`(bloodPressureData.creationTime))
            MatcherAssert.assertThat(it.creationDate, `is`(bloodPressureData.creationDate))

        }
    }
}