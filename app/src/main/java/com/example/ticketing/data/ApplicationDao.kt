package com.example.ticketing.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.ticketing.vo.DbToken

@Dao
interface ApplicationDao {
    @Query("select * from token")
    fun getLocalToken() : DbToken

    @Insert
    fun insertLocalToken(token : DbToken)

    @Update
    fun updateAccessToken(token : DbToken)

    @Delete
    fun removeLocalToken(token: DbToken)
}

@Database(entities = [DbToken::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun appDao() : ApplicationDao
}