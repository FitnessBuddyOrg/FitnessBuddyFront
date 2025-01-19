package com.project.fitnessbuddy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.fitnessbuddy.database.dao.ExerciseDao
import com.project.fitnessbuddy.database.dao.ParameterDao
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.Parameter

@Database(
    entities = [Exercise::class, Parameter::class],
    version = 2
)
abstract class FitnessBuddyDatabase: RoomDatabase() {
    abstract val exerciseDao: ExerciseDao
    abstract val parameterDao: ParameterDao
}
