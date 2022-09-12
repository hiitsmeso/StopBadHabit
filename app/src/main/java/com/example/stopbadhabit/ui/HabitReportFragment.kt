package com.example.stopbadhabit.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.stopbadhabit.R
import com.example.stopbadhabit.databinding.FragmentHabitDetailBinding
import com.example.stopbadhabit.databinding.FragmentHabitReportBinding
import com.example.stopbadhabit.ui.viewmodel.HabitReportViewModel
import com.example.stopbadhabit.ui.viewmodel.MainViewModel
import com.example.stopbadhabit.util.Utils
import com.example.stopbadhabit.util.toCalender
import com.example.stopbadhabit.util.toDate
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class HabitReportFragment : DialogFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val habitReportViewModel: HabitReportViewModel by viewModels()
    private val binding by lazy { FragmentHabitReportBinding.inflate(layoutInflater) }


    private var result: Int = 0
    private var endDate: String = ""

    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time.time


    override fun onStart() {
        super.onStart()
        if (dialog != null && activity != null && isAdded) {
            val fullWidth = Utils.getScreenWidth(requireActivity()) * .85
            dialog?.window?.setLayout(fullWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnReportClose.setOnClickListener {
            mainViewModel.getHabitList()
            habitReportViewModel.updateState(state = result,endDate)
            Log.e(javaClass.simpleName, "habit Report  ${mainViewModel.habitList.value}", )
            dismiss()
        }

        mainViewModel.detailHabitId.observe(viewLifecycleOwner) {
            if (it != -1) {
                habitReportViewModel.getHabitDetail(it)
                habitReportViewModel.getHabitAndDiary(it)
            } else
                Log.e("fsda", "onCreateView: sdfsdf")
        }

        habitReportViewModel.habitAndDiary.observe(viewLifecycleOwner) {
            with(binding) {
                val mDate = LocalDate.now()
                tvReportName.text = it.habit.name

                tvReportLife.text = String.format(
                    requireContext().getString(R.string.life),
                    it.habit.current_life,
                    it.habit.setting_life
                )

                it.habit.habit_id?.let { it1 -> mainViewModel.getDiaryList(it1) }

                var dateFromStart:Int = -1

                if (it.diaries?.isNotEmpty() == true) {
                    dateFromStart =((((it.diaries.last().diary_date.toDate()) - it.habit.start_date.toDate())) / (24 * 60 * 60 * 1000) + 1).toInt()
                }

                if (it.habit.current_life == 0) {
                    result = 2 //실패
                    it.diaries?.last()
                        ?.let { endDate = it.diary_date }

                    //mainViewModel.getHabitList()

                    tvReportDate.text = it.diaries?.last()?.let { it1 ->
                        String.format(
                            requireContext().getString(R.string.hr_date), it.habit.start_date,
                            it1.diary_date
                        )
                    }
                    Glide.with(binding.root).load(R.drawable.bg_easy_fail)
                        .into(binding.ivReportChar)
                    tvState.text = String.format(requireContext().getString(R.string.hr_state_fail))
                    tvReportResult.text =
                        String.format(requireContext().getString(R.string.hr_result_fail))
                    tvReportFromStart.text =
                        String.format(requireContext().getString(R.string.hr_fromStart), dateFromStart)

                    Glide.with(binding.root).load(R.drawable.ic_emptyheart)
                        .into(binding.ivReportHeart)
                } else {
                    result = 1 //성공
                    endDate = it.habit.start_date.toCalender(it.habit.goal_date)

                    //mainViewModel.getHabitList()

                    tvReportDate.text = String.format(
                        requireContext().getString(R.string.hr_date),
                        it.habit.start_date,
                        it.habit.start_date.toCalender(it.habit.goal_date)
                    )
                    tvState.text =
                        String.format(requireContext().getString(R.string.hr_state_success))
                    tvReportResult.text =
                        String.format(requireContext().getString(R.string.hr_result_success))
                    tvReportFromStart.text = String.format(
                        requireContext().getString(R.string.hr_fromStart),
                        it.habit.goal_date
                    )
                    when (it.habit.mode) {
                        0 -> Glide.with(binding.root).load(R.drawable.bg_easy_fail)
                            .into(binding.ivReportChar)
                        1 -> Glide.with(binding.root).load(R.drawable.bg_normal_fail)
                            .into(binding.ivReportChar)
                        2 -> Glide.with(binding.root).load(R.drawable.bg_mob_hard)
                            .into(binding.ivReportChar)
                    }
                    Glide.with(binding.root).load(R.drawable.ic_heart).into(binding.ivReportHeart)
                }

            }
        }
    }
}