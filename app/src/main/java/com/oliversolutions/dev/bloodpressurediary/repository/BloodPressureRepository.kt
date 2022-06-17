package com.oliversolutions.dev.bloodpressurediary.repository

import androidx.lifecycle.LiveData
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase

class BloodPressureRepository(private val database: BloodPressureDatabase) {
    suspend fun saveHighPressure(bloodPressureDTO: BloodPressureDTO) {
        return database.bloodPressureDatabaseDao.insert(bloodPressureDTO)
    }

    suspend fun editHighPressure(bloodPressureDTO: BloodPressureDTO) {
        return database.bloodPressureDatabaseDao.update(bloodPressureDTO)
    }

     fun getAllRecords() : LiveData<List<BloodPressureDTO>> {
        return database.bloodPressureDatabaseDao.getAllRecords()
    }

     fun getRecordsByDate(fromDate: String, toDate: String) : LiveData<List<BloodPressureDTO>> {
        return database.bloodPressureDatabaseDao.getRecordsByDate(fromDate, toDate)
    }

    suspend fun getRecordsByDateInList(fromDate: String, toDate: String) : List<BloodPressureDTO> {
        return database.bloodPressureDatabaseDao.getRecordsByDateInList(fromDate, toDate)
    }

    suspend fun deleteRecord(id: Int)  {
        database.bloodPressureDatabaseDao.deleteRecord(id)
    }

    suspend fun clear()  {
        database.bloodPressureDatabaseDao.clear()
    }
}