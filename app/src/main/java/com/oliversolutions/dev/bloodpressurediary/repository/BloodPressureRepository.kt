package com.oliversolutions.dev.bloodpressurediary.repository

import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.oliversolutions.dev.bloodpressurediary.utils.Result
import com.oliversolutions.dev.bloodpressurediary.utils.wrapEspressoIdlingResource

class BloodPressureRepository(
    private val database: BloodPressureDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BloodPressureDataSource {

    override suspend fun createNewBloodPressure(bloodPressureDTO: BloodPressureDTO) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                database.bloodPressureDatabaseDao.insert(bloodPressureDTO)
            }
        }

    override suspend fun editBloodPressure(bloodPressureDTO: BloodPressureDTO) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                database.bloodPressureDatabaseDao.update(bloodPressureDTO)
            }
        }

    override suspend fun getAllRecords(): Result<List<BloodPressureDTO>> = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            return@withContext try {
                Result.Success(database.bloodPressureDatabaseDao.getAllRecords())
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
    }

    override suspend fun getRecordsByDate(fromDate: String, toDate: String): Result<List<BloodPressureDTO>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(database.bloodPressureDatabaseDao.getRecordsByDate(fromDate, toDate))
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }

    override suspend fun getRecordsByDateInList(fromDate: String, toDate: String): List<BloodPressureDTO> {
        return database.bloodPressureDatabaseDao.getRecordsByDateInList(fromDate, toDate)
    }

    override suspend fun deleteRecord(id: String) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                database.bloodPressureDatabaseDao.deleteRecord(id)
            }
        }
    }

    override suspend fun clear() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                database.bloodPressureDatabaseDao.clear()
            }
        }
    }

    override suspend fun getBloodPressure(id: String): Result<BloodPressureDTO> = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            try {
                val bloodPressure = database.bloodPressureDatabaseDao.getBloodPressure(id)
                if (bloodPressure != null) {
                    return@withContext Result.Success(bloodPressure)
                } else {
                    return@withContext Result.Error("Blood pressure not found!")
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e.localizedMessage)
            }
        }
    }


}