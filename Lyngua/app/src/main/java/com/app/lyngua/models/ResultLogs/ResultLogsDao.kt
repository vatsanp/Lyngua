package com.app.lyngua.models.ResultLogs


import androidx.lifecycle.LiveData
import androidx.room.*


/*
* This is the interface for the category database operations
* Here all the different queries are listed.
* */
@Dao
@TypeConverters(ResultsTypeConverter::class)
interface ResultLogsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addResult(results: ResultLogs): Long

    @Query("SELECT * FROM result_logs_database ORDER BY id DESC")
    fun readAllData(): LiveData<List<ResultLogs>>

    //read data from a certain time
    @Query("SELECT * FROM result_logs_database WHERE epochTimestamp>:timeFrame ORDER BY id DESC")
    fun readLogs(timeFrame: Long): List<ResultLogs>

}
