package com.example.lyngua.views.Categories.goals

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lyngua.R
import com.example.lyngua.controllers.CategoryController
import com.example.lyngua.controllers.notifications.GoalUpdatePublisher
import com.example.lyngua.controllers.notifications.GoalNotificationPublisher
import com.example.lyngua.controllers.notifications.AlarmService
import com.example.lyngua.models.goals.Goal
import com.example.lyngua.views.Categories.UpdateCategory.SwitchType.SWITCH_OFF
import com.example.lyngua.views.Categories.UpdateCategory.SwitchType.SWITCH_ON
import com.example.lyngua.views.Categories.UpdateCategoryArgs
import java.util.*
import kotlinx.android.synthetic.main.fragment_word_interval.*
import kotlinx.android.synthetic.main.fragment_word_interval.view.*
import kotlin.collections.ArrayList

    class WordIntervalGoal(arg: UpdateCategoryArgs) : Fragment() {

    private lateinit var categoryController: CategoryController
    private val args = arg

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_word_interval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myCalendar = Calendar.getInstance()
        var timeFrameFlag: Int = SWITCH_OFF
        var notificationFlag = SWITCH_OFF
        var goalType: Int = SWITCH_OFF
        var wordGoalCount = SWITCH_OFF

        categoryController = CategoryController(requireContext())

        if (args.categoryChosen.goal.goalType == SWITCH_ON) {
            setSelectedOption(tv_goal_option_one)
        }

        //Sets the word count for goal based on last update of category
        if (args.categoryChosen.goal.totalNumWords != 0) {
            view.words_goal_count_text.setText(args.categoryChosen.goal.totalNumWords.toString())
        }

        //If notifications were enabled before, ensures the box is checked
        if (args.categoryChosen.goal.notificationFlag == 1) {
            view.notification_checkbox.isChecked = true
            notificationFlag = SWITCH_ON
        }

        val spinner: Spinner = view.findViewById(R.id.set_goals_spn)

        //Create the options for the spinner
        var options: MutableList<String> = ArrayList()
        options.add(0, "No Goal")
        options.add(1, "Day")
        options.add(2, "Week")
        options.add(3, "Month")

        //Create array adapter to display the options list with the spinner
        val arrayAdapter = ArrayAdapter<String>(
            requireActivity().applicationContext,
            android.R.layout.simple_list_item_1,
            options
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        //If a previous time frame was already set, update spinner to that option
        if (args.categoryChosen.goal.goalType != 0) {
            spinner.setSelection(args.categoryChosen.goal.timeFrame)
        }

        // Create spinner listeners for goal time frame
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent!!.getItemAtPosition(position) == "No Goal") {
                    timeFrameFlag = SWITCH_OFF
                    goalType = SWITCH_OFF
                    notificationFlag = SWITCH_OFF
                    wordGoalCount = SWITCH_OFF

                } else {

                    //Timeframe based on the spinner selected
                    when {
                        parent.getItemAtPosition(position) == "Day" -> {
                            timeFrameFlag = 1
                        }
                        parent.getItemAtPosition(position) == "Week" -> {
                            timeFrameFlag = 2
                        }
                        parent.getItemAtPosition(position) == "Month" -> {
                            timeFrameFlag = 3
                        }
                    }
                    goalType = SWITCH_ON
                }
            }
        }

        //View for checkbox for notification reminder
        val checkBox = view.findViewById(R.id.notification_checkbox) as CheckBox
        checkBox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                notificationFlag = SWITCH_ON
            } else {
                notificationFlag = SWITCH_OFF
            }

        }

        //On Click listener for the update category button
        view.update_category_btn.setOnClickListener {

            if(goalType == SWITCH_ON) {
                if (view.words_goal_count_text.text.toString().isEmpty()) {
                    wordGoalCount = 50
                } else {
                    wordGoalCount = Integer.parseInt(view.words_goal_count_text.text.toString())
                }
            }

            //Based on which spinner was chosen, detail the time for when the goal should be complete
            when (timeFrameFlag) {
                0 -> cancelAlarms()
                1 -> myCalendar.add(Calendar.DAY_OF_MONTH, 1)
                2 -> myCalendar.add(Calendar.DAY_OF_MONTH, 7)
                3 -> myCalendar.add(Calendar.MINUTE, 2)
            }

            //Creates a goal object based on the options chosen from updating to be put into the database
            val goal: Goal = Goal(
                myCalendar,
                timeFrameFlag,
                notificationFlag,
                goalType,
                args.categoryChosen.goal.numWordsCompleted,
                wordGoalCount,
                0,
                0
            )

            val result = categoryController.updateCategory(
                args.categoryChosen.id,
                args.categoryChosen.name,
                args.categoryChosen.numWords,
                args.categoryChosen.wordsList,
                args.categoryChosen.sessionNumber,
                goal
            )

            if (timeFrameFlag != SWITCH_OFF) {
                //Creates an alarmservice to run in the background
                val alarm =
                    AlarmService(requireActivity().applicationContext, args.categoryChosen, goal)

                alarm.startAlarm()
            }

            if (result) {
                findNavController().navigate(R.id.action_updateCategoryFragment_to_practice)
            }

        }

        //On Click listener for the delete category button
        view.delete_category_btn.setOnClickListener {
            val confirmation = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
            confirmation.setTitle("Delete?")
            confirmation.setMessage("Are you sure you would like to delete this category?")
            confirmation.setPositiveButton("Delete") { _, _ ->

                cancelAlarms()
                categoryController.deleteCategory(args.categoryChosen)
                Toast.makeText(requireContext(), "Delete Success", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_updateCategoryFragment_to_practice)
            }
            confirmation.setNegativeButton("Cancel") { _, _ -> }
            confirmation.create().show()

        }

    }

    private fun setSelectedOption(view: TextView) {
        view.setTextColor(Color.parseColor("#FFFFFF"))
        view.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.selected_goal_background_color)
    }

    private fun cancelAlarms(){
        val mAlarmSender: PendingIntent
        val alarmGoalSender: PendingIntent
        var broadcastIntent: Intent = Intent(context, GoalNotificationPublisher::class.java)
        var broadcastIntent1: Intent = Intent(context, GoalUpdatePublisher::class.java)

        //Create bundle to send category and goal information to the broadcast receiver
        var bundle = Bundle()
        bundle.putParcelable("category", args.categoryChosen)
        bundle.putParcelable("goal", args.categoryChosen.goal)
        broadcastIntent.putExtra("bundle", bundle)
        broadcastIntent1.putExtra("bundle", bundle)

        //Create pending intent to be able to cancel the alarms set for the specific category
        mAlarmSender =
            PendingIntent.getBroadcast(
                context,
                args.categoryChosen.id,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmGoalSender =
            PendingIntent.getBroadcast(
                context,
                args.categoryChosen.id + 1000,
                broadcastIntent1,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val am = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.cancel(mAlarmSender)
        am.cancel(alarmGoalSender)
    }
}
