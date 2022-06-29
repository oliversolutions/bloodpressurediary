package com.oliversolutions.dev.bloodpressurediary.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.oliversolutions.dev.bloodpressurediary.App
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentRemindersBinding
import com.oliversolutions.dev.bloodpressurediary.main.TimePickerFragment
import com.oliversolutions.dev.bloodpressurediary.work.MeasurementReminderWorker
import com.oliversolutions.dev.bloodpressurediary.work.MedicationReminderWorker
import com.oliversolutions.dev.bloodpressurediary.work.RecurringWork
import org.koin.androidx.viewmodel.ext.android.viewModel

class RemindersFragment : Fragment() {

    private lateinit var binding: FragmentRemindersBinding
    val mainViewModel: RemindersViewModel by viewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminders, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        setHasOptionsMenu(true)

        if (App.prefs.sendMeasurementReminder) {
            binding.measuramentReminderSwitch.isChecked = true
            binding.howOftenMeasurementReminderLayout.visibility = View.VISIBLE
            binding.measurementReminderWhichTimeLinearLayout.visibility = View.VISIBLE
        }
        if (App.prefs.sendMedicationReminder) {
            binding.medicationReminderSwitch.isChecked = true
            binding.medicationReminderWhichTimeLinearLayout.visibility = View.VISIBLE

            if (App.prefs.medicationReminderDays == 2) {
                binding.medicationReminderSecondTimeLinearLayout.visibility = View.VISIBLE
            } else if (App.prefs.medicationReminderDays == 3) {
                binding.medicationReminderSecondTimeLinearLayout.visibility = View.VISIBLE
                binding.medicationReminderThirdTimeLinearLayout.visibility = View.VISIBLE
            }
            binding.howOftenPerDayLinearLayout.visibility = View.VISIBLE
        }

        binding.numberPickerVertical.value = App.prefs.measurementReminderDays
        binding.numberPickerVertical.setListener{
            App.prefs.measurementReminderDays = it
        }

        binding.howOftenPerDayNumberPicker.value = App.prefs.medicationReminderDays
        binding.howOftenPerDayNumberPicker.setListener{
            App.prefs.medicationReminderDays = it
            when (it) {
                1 -> {
                    binding.medicationReminderSecondTimeLinearLayout.visibility = View.GONE
                    WorkManager.getInstance(requireContext()).cancelUniqueWork(MedicationReminderWorker.WORK_NAME_2)
                }
                2 -> {
                    binding.medicationReminderSecondTimeLinearLayout.visibility = View.VISIBLE
                    binding.medicationReminderThirdTimeLinearLayout.visibility = View.GONE
                    WorkManager.getInstance(requireContext()).cancelUniqueWork(MedicationReminderWorker.WORK_NAME_3)
                    RecurringWork.setup(MedicationReminderWorker.WORK_NAME, App.prefs.medicationSecondReminderTime!!, requireContext()
                    )
                }
                3 -> {
                    binding.medicationReminderThirdTimeLinearLayout.visibility = View.VISIBLE
                    RecurringWork.setup(MedicationReminderWorker.WORK_NAME, App.prefs.medicationThirdReminderTime!!, requireContext()
                    )
                }
            }
        }

        binding.measuramentReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                App.prefs.sendMeasurementReminder = true
                binding.measurementReminderWhichTimeLinearLayout.visibility = View.VISIBLE
                binding.howOftenMeasurementReminderLayout.visibility = View.VISIBLE
                RecurringWork.setup(MeasurementReminderWorker.WORK_NAME, App.prefs.measurementReminderTime!!, requireContext()
                )

            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork(MeasurementReminderWorker.WORK_NAME)
                App.prefs.sendMeasurementReminder = false
                binding.howOftenMeasurementReminderLayout.visibility = View.GONE
                binding.measurementReminderWhichTimeLinearLayout.visibility = View.GONE
            }
        }
        binding.medicationReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                App.prefs.sendMedicationReminder = true
                binding.medicationReminderWhichTimeLinearLayout.visibility = View.VISIBLE
                binding.howOftenPerDayLinearLayout.visibility = View.VISIBLE
                RecurringWork.setup(MedicationReminderWorker.WORK_NAME, App.prefs.medicationReminderTime!!, requireContext())

                if (App.prefs.medicationReminderDays == 2) {
                    binding.medicationReminderSecondTimeLinearLayout.visibility = View.VISIBLE
                    RecurringWork.setup(MedicationReminderWorker.WORK_NAME_2, App.prefs.medicationSecondReminderTime!!, requireContext())
                } else if (App.prefs.medicationReminderDays == 3) {
                    binding.medicationReminderThirdTimeLinearLayout.visibility = View.VISIBLE
                    binding.medicationReminderSecondTimeLinearLayout.visibility = View.VISIBLE
                    RecurringWork.setup(MedicationReminderWorker.WORK_NAME_2, App.prefs.medicationSecondReminderTime!!, requireContext())
                    RecurringWork.setup(MedicationReminderWorker.WORK_NAME_3, App.prefs.medicationThirdReminderTime!!, requireContext())
                }
            } else {
                App.prefs.sendMedicationReminder = false
                binding.medicationReminderWhichTimeLinearLayout.visibility = View.GONE
                binding.medicationReminderSecondTimeLinearLayout.visibility = View.GONE
                binding.medicationReminderThirdTimeLinearLayout.visibility = View.GONE
                binding.howOftenPerDayLinearLayout.visibility = View.GONE
                WorkManager.getInstance(requireContext()).cancelUniqueWork(MedicationReminderWorker.WORK_NAME)
                WorkManager.getInstance(requireContext()).cancelUniqueWork(MedicationReminderWorker.WORK_NAME_2)
                WorkManager.getInstance(requireContext()).cancelUniqueWork(MedicationReminderWorker.WORK_NAME_3)
            }
        }
        mainViewModel.measurementReminderTime.observe(viewLifecycleOwner, {
            App.prefs.measurementReminderTime = it
        })

        mainViewModel.medicationReminderTime.observe(viewLifecycleOwner, {
            App.prefs.medicationReminderTime = it
        })

        mainViewModel.medicationSecondReminderTime.observe(viewLifecycleOwner, {
            App.prefs.medicationSecondReminderTime = it
        })

        mainViewModel.medicationThirdReminderTime.observe(viewLifecycleOwner, {
            App.prefs.medicationThirdReminderTime = it
        })

        binding.measurementReminderWhichTimeEditText.showSoftInputOnFocus = false
        binding.measurementReminderWhichTimeEditText.setOnClickListener{ showTimePickerDialog("measurement") }
        binding.measuramentReminderWhichTimeImageView.setOnClickListener{ showTimePickerDialog("measurement") }
        binding.medicationReminderWhichTimeEditText.showSoftInputOnFocus = false
        binding.medicationReminderWhichTimeEditText.setOnClickListener{ showTimePickerDialog("medication") }
        binding.medicationReminderWhichTimeImageView.setOnClickListener{ showTimePickerDialog("medication") }
        binding.medicationReminderSecondTimeEditText.setOnClickListener{ showTimePickerDialog("medication_2") }
        binding.medicationReminderSecondTimeImageView.setOnClickListener{ showTimePickerDialog("medication_2") }
        binding.medicationReminderThirdTimeEditText.setOnClickListener{ showTimePickerDialog("medication_3") }
        binding.medicationReminderThirdTimeImageView.setOnClickListener{ showTimePickerDialog("medication_3") }

        return binding.root
    }

    private fun showTimePickerDialog(type: String) {
        val timePicker = TimePickerFragment { onTimeSelected(it, type) }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String, type: String) {
        when (type) {
            "measurement" -> {
                if (mainViewModel.measurementReminderTime.value!! != time && App.prefs.sendMeasurementReminder) {
                    RecurringWork.setup(MeasurementReminderWorker.WORK_NAME, time, requireContext())
                }
                mainViewModel.measurementReminderTime.value = time
            }
            "medication" -> {
                if (mainViewModel.medicationReminderTime.value!! != time && App.prefs.sendMedicationReminder) {
                    RecurringWork.setup(MedicationReminderWorker.WORK_NAME, time, requireContext())
                }
                mainViewModel.medicationReminderTime.value = time
            }
            "medication_2" -> {
                if (mainViewModel.medicationSecondReminderTime.value!! != time && App.prefs.sendMedicationReminder) {
                    RecurringWork.setup(MedicationReminderWorker.WORK_NAME_2, time, requireContext())
                }
                mainViewModel.medicationSecondReminderTime.value = time
            }
            "medication_3" -> {
                if (mainViewModel.medicationThirdReminderTime.value!! != time && App.prefs.sendMedicationReminder) {
                    RecurringWork.setup(MedicationReminderWorker.WORK_NAME_3, time, requireContext())
                }
                mainViewModel.medicationThirdReminderTime.value = time
            }
        }
    }
}