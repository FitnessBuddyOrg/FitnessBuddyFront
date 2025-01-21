package com.project.fitnessbuddy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.fitnessbuddy.database.dao.ExerciseDao
import com.project.fitnessbuddy.database.dao.ParameterDao
import com.project.fitnessbuddy.database.dao.RoutineDao
import com.project.fitnessbuddy.database.dao.RoutineExerciseDao
import com.project.fitnessbuddy.database.dao.RoutineExerciseSetDao
import com.project.fitnessbuddy.database.dao.UserDao
import com.project.fitnessbuddy.database.entity.Exercise
import com.project.fitnessbuddy.database.entity.Parameter
import com.project.fitnessbuddy.database.entity.Routine
import com.project.fitnessbuddy.database.entity.RoutineExercise
import com.project.fitnessbuddy.database.entity.RoutineExerciseSet
import com.project.fitnessbuddy.database.entity.User

@Database(
    entities =
    [
        User::class,
        Exercise::class,
        RoutineExercise::class,
        RoutineExerciseSet::class,
        Routine::class,
        Parameter::class
    ],
    version = 30
)
@TypeConverters(Converters::class)
abstract class FitnessBuddyDatabase : RoomDatabase() {
    abstract val userDao: UserDao

    abstract val exerciseDao: ExerciseDao

    abstract val routineDao: RoutineDao
    abstract val routineExerciseDao: RoutineExerciseDao
    abstract val routineExerciseSetDao: RoutineExerciseSetDao

    abstract val parameterDao: ParameterDao

    companion object {
        const val DATABASE_NAME = "fitnessBuddy.db"
    }
}
