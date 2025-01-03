package com.project.fitnessbuddy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.fitnessbuddy.database.dao.ExerciseDao
import com.project.fitnessbuddy.database.entity.Exercise

@Database(
    entities = [Exercise::class],
    version = 1
)
abstract class FitnessBuddyDatabase: RoomDatabase() {
    abstract val exerciseDao: ExerciseDao
}
