package com.app.lyngua.controllers.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.app.lyngua.models.categories.Category
import com.app.lyngua.models.goals.Goal
import com.app.lyngua.views.Categories.UpdateCategory.SwitchType.SWITCH_ON
import java.util.*


class AlarmService(context: Context, category: Category, goal: Goal) {
    private val context: Context
    private val mAlarmSender: PendingIntent
    private val alarmGoalSender: PendingIntent

    val category = category
    val goal = goal

    val goalCalendar: Calendar = goal.time
    fun startAlarm() {

        val firstTime: Long = goalCalendar.timeInMillis
        val intervalTime: Long = firstTime - Calendar.getInstance().timeInMillis

        //Create the alarm for notification if the checkbox was enabled
        if (goal.notificationFlag == SWITCH_ON) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, intervalTime, mAlarmSender)
        }

        //Schedule alarm for finishing goal
        val am2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am2.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, intervalTime, alarmGoalSender)
        //am2.set(AlarmManager.RTC_WAKEUP, firstTime, alarmGoalSender)

    }

    init {
        this.context = context
        var broadcastIntent: Intent = Intent(context, GoalNotificationPublisher::class.java)
        var broadcastIntent1: Intent = Intent(context, GoalUpdatePublisher::class.java)

        //Create bundle to send category and goal information to the broadcast receiver
        var bundle = Bundle()
        bundle.putParcelable("category", category)
        bundle.putParcelable("goal", goal)
        broadcastIntent.putExtra("bundle", bundle)
        broadcastIntent1.putExtra("bundle", bundle)

        mAlarmSender =
            PendingIntent.getBroadcast(
                context,
                category.id,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmGoalSender =
            PendingIntent.getBroadcast(
                context,
                category.id + 1000,
                broadcastIntent1,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
    }
}