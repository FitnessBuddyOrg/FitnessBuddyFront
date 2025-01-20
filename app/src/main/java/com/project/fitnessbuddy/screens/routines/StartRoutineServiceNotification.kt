package com.project.fitnessbuddy.screens.routines

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.project.fitnessbuddy.MainActivity
import com.project.fitnessbuddy.R
import com.project.fitnessbuddy.database.dto.RoutineDTO
import com.project.fitnessbuddy.screens.common.formatElapsedSeconds
import java.util.Date


const val CHANNEL_ID = "ch-1"
const val CHANNEL_NAME = "Start Routine Notification"
const val NOTIFICATION_ID = 1
const val STOP_TIMER = "STOP_TIMER"

const val ROUTINE_UPDATE = "ROUTINE_UPDATE"
const val SELECTED_ROUTINE_DTO = "SELECTED_ROUTINE_DTO"

const val DESTINATION = "DESTINATION"
const val EXTRA_ACTION = "EXTRA_ACTION"
const val FINISHED_ROUTINE = "FINISHED_ROUTINE"
const val UNFINISHED_ROUTINE = "UNFINISHED_ROUTINE"
const val NONE = "NONE"


class StartRoutineServiceNotification : Service() {
    private var isRunning = true

    private var routineDTO: RoutineDTO = RoutineDTO()
    private var elapsedSeconds = 0L
    private var formattedTime = "00:00"


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_TIMER) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            elapsedSeconds = 0L
            return START_NOT_STICKY
        } else if (intent?.action == ROUTINE_UPDATE) {
            val routineDTOJson = intent.getStringExtra(SELECTED_ROUTINE_DTO)
            routineDTO = routineDTOJson?.let { Gson().fromJson(it, RoutineDTO::class.java) }!!
            elapsedSeconds = routineDTO.routine.startDate?.let {
                ((Date().time - it.time) / 1000)
            } ?: 0

        }

        startForeground(NOTIFICATION_ID, createNotification())

        Thread {
            while (isRunning) {
                elapsedSeconds++
                formattedTime = elapsedSeconds.formatElapsedSeconds()

                val notification = createNotification()
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification)

                Thread.sleep(1000)
            }
        }.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
//        val cancelRoutine = Intent(this, StartRoutineServiceNotification::class.java).apply {
//            action = STOP_TIMER
//        }
//        val cancelRoutinePendingIntent = PendingIntent.getService(
//            this,
//            0,
//            cancelRoutine,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        val returnToRoutineIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(DESTINATION, getString(R.string.start_routine_route))
            putExtra(SELECTED_ROUTINE_DTO, Gson().toJson(routineDTO))
            putExtra(EXTRA_ACTION, NONE)
        }

        val returnToRoutinePendingIntent = PendingIntent.getActivity(
            this,
            0,
            returnToRoutineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

//        val completeRoutineIntent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//            val allChecked =
//                routineDTO.routineExerciseDTOs.all { it.routineExerciseSetDTOs.all { it2 -> it2.checked } }
//            if (allChecked) {
//                putExtra(DESTINATION, getString(R.string.start_routine_route))
//                putExtra(EXTRA_ACTION, FINISHED_ROUTINE)
//            } else {
//                putExtra(DESTINATION, getString(R.string.start_routine_route))
//                putExtra(EXTRA_ACTION, UNFINISHED_ROUTINE)
//            }
//
//            putExtra(SELECTED_ROUTINE_DTO, Gson().toJson(routineDTO))
//        }
//
//        val completeRoutinePendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            completeRoutineIntent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//        )

        val firstUncheckedExerciseDTO =
            routineDTO.routineExerciseDTOs.find { it.routineExerciseSetDTOs.find { it2 -> !it2.checked } != null }

        val firstUncheckedExerciseSetDTO =
            firstUncheckedExerciseDTO?.routineExerciseSetDTOs?.find { !it.checked }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(routineDTO.routine.name)
            .setContentText("${getString(R.string.started_since)}: $formattedTime")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat
                    .InboxStyle()
                    .addLine("${getString(R.string.started_since)}: $formattedTime")
                    .addLine("${getString(R.string.current_exercise)}: ${firstUncheckedExerciseDTO?.routineExerciseSetDTOs?.size} × ${firstUncheckedExerciseDTO?.exercise?.name}")
                    .addLine("${getString(R.string.current_set)}: ${firstUncheckedExerciseSetDTO?.reps} × ${firstUncheckedExerciseSetDTO?.weight} kg")
            )
            .setContentIntent(returnToRoutinePendingIntent)
//            .addAction(
//                android.R.drawable.ic_delete,
//                getString(R.string.complete_routine),
//                completeRoutinePendingIntent
//            )
//            .addAction(
//                android.R.drawable.ic_delete,
//                getString(R.string.cancel_routine),
//                cancelRoutinePendingIntent
//            )
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
