package com.oliversolutions.dev.bloodpressurediary.repository

import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.utils.Result

interface BloodPressureDataSource {
    suspend fun createNewBloodPressure(bloodPressureDTO: BloodPressureDTO)
    suspend fun editBloodPressure(bloodPressureDTO: BloodPressureDTO)
    suspend fun getAllRecords(): Result<List<BloodPressureDTO>>
    suspend fun getRecordsByDate(fromDate: String, toDate: String): Result<List<BloodPressureDTO>>
    suspend fun getRecordsByDateInList(fromDate: String, toDate: String): List<BloodPressureDTO>
    suspend fun deleteRecord(id: String)
    suspend fun clear()
    suspend fun getBloodPressure(id: String) : Result<BloodPressureDTO>
}