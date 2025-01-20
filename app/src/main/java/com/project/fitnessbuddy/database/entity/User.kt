package com.project.fitnessbuddy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user"
)
data class User (
    val name: String? = null,

    @ColumnInfo(name = "access_token")
    val accessToken: String? = null,

    val email: String? = null,

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Long? = null
)
