package com.example.ticketing.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class DbToken (
    @PrimaryKey
    val userId : String,
    val refreshToken : String,
    val accessToken : String
)