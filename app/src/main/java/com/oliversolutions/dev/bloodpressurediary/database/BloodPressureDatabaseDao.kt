/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oliversolutions.dev.bloodpressurediary.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Defines methods for using the SleepNight class with Room.
 */
@Dao
interface BloodPressureDatabaseDao {

    @Insert
    suspend fun insert(bloodPressureDTO: BloodPressureDTO)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(bloodPressureDTO: BloodPressureDTO)

    @Query("SELECT * from high_pressure_table WHERE id = :key")
    suspend fun get(key: Int): BloodPressureDTO?

    @Query("SELECT a.id,a.systolic, a.diastolic, a.pulse, a.notes, a.creation_time, a.creation_date ,\n" +
            "(SELECT SUM(diastolic)/COUNT(creation_date) FROM high_pressure_table WHERE creation_date = a.creation_date GROUP BY creation_date) AS average_diastolic, \n" +
            "(SELECT SUM(systolic)/COUNT(creation_date) FROM high_pressure_table WHERE creation_date = a.creation_date GROUP BY creation_date) AS average_systolic,\n" +
            "(SELECT SUM(pulse)/COUNT(creation_date) FROM high_pressure_table WHERE creation_date = a.creation_date GROUP BY creation_date) AS average_pulse\n  " +
            "FROM high_pressure_table a WHERE a.creation_date >= :fromDate AND a.creation_date <= :toDate ORDER BY a.creation_date DESC")
    fun getRecordsByDate(fromDate: String, toDate: String): LiveData<List<BloodPressureDTO>>


    @Query("SELECT * FROM high_pressure_table WHERE creation_date >= :fromDate AND creation_date <= :toDate ORDER BY creation_date DESC")
    suspend fun getRecordsByDateInList(fromDate: String, toDate: String): List<BloodPressureDTO>

    @Query("DELETE FROM high_pressure_table")
    suspend fun clear()

    @Query("DELETE FROM high_pressure_table WHERE id = :key")
    suspend fun deleteRecord(key: Int)

    @Query("SELECT a.id,a.systolic, a.diastolic, a.pulse, a.notes, a.creation_time, a.creation_date ,\n" +
            "(SELECT SUM(diastolic)/COUNT(creation_date) FROM high_pressure_table WHERE creation_date = a.creation_date GROUP BY creation_date) AS average_diastolic, \n" +
            "(SELECT SUM(systolic)/COUNT(creation_date) FROM high_pressure_table WHERE creation_date = a.creation_date GROUP BY creation_date) AS average_systolic,\n" +
            "(SELECT SUM(pulse)/COUNT(creation_date) FROM high_pressure_table WHERE creation_date = a.creation_date GROUP BY creation_date) AS average_pulse\n " +
            "FROM high_pressure_table a ORDER BY a.creation_date DESC")
     fun getAllRecords(): LiveData<List<BloodPressureDTO>>

    @Query("SELECT * FROM high_pressure_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastRecord(): BloodPressureDTO?

    @Query("SELECT * from high_pressure_table WHERE id = :key")
    fun getHighPressureWithId(key: Int): LiveData<BloodPressureDTO>
}

