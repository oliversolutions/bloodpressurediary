package com.oliversolutions.dev.bloodpressurediary.data

import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureDataSource
import com.oliversolutions.dev.bloodpressurediary.utils.Result
import java.util.LinkedHashMap

class FakeDataSource : BloodPressureDataSource {

    var bloodPressureData: LinkedHashMap<String, BloodPressureDTO> = LinkedHashMap()

    override suspend fun createNewBloodPressure(bloodPressure: BloodPressureDTO) {
        bloodPressureData[bloodPressure.id] = bloodPressure
    }

    override suspend fun editBloodPressure(bloodPressureDTO: BloodPressureDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllRecords(): Result<List<BloodPressureDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecordsByDate(
        fromDate: String,
        toDate: String
    ): Result<List<BloodPressureDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecordsByDateInList(fromDate: String, toDate: String): List<BloodPressureDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecord(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

    override suspend fun getBloodPressure(id: String): Result<BloodPressureDTO> {
        TODO("Not yet implemented")
    }
}