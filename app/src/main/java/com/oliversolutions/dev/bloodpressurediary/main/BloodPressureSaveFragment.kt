package com.oliversolutions.dev.bloodpressurediary.main

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.oliversolutions.dev.bloodpressurediary.BloodPressure
import com.oliversolutions.dev.bloodpressurediary.BloodPressureDiagnosis
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentBloodPressureSaveBinding
import com.oliversolutions.dev.bloodpressurediary.getBloodPressureDiagnosis
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class BloodPressureSaveFragment : Fragment() {

    val viewModel: BloodPressureSaveViewModel by lazy {
        ViewModelProvider(this).get(BloodPressureSaveViewModel::class.java)
    }
    private lateinit var binding: FragmentBloodPressureSaveBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blood_pressure_save, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        configureNumberPickers()
        viewModel.systolic.observe(viewLifecycleOwner, {
            setHighPressureDiagnose()
        })
        viewModel.diastolic.observe(viewLifecycleOwner, {
            setHighPressureDiagnose()
        })
        binding.creationDate.showSoftInputOnFocus = false
        binding.creationDate.setOnClickListener{ showDatePickerDialog() }
        binding.dateImageView.setOnClickListener{ showDatePickerDialog() }
        binding.creationTime.showSoftInputOnFocus = false
        binding.creationTime.setOnClickListener{ showTimePickerDialog() }
        binding.timeImageView.setOnClickListener{ showTimePickerDialog() }

        binding.diagnosisLinearlayout.setOnClickListener {
            BloodPressureTypesDialogFragment().show(childFragmentManager, BloodPressureTypesDialogFragment.TAG)
        }
        setHasOptionsMenu(true)
        configureDiagnosisButtons()
        return binding.root
    }

    private fun configureDiagnosisButtons() {
        val hypotensionButton = binding.hypotensionButton
        val normalButton = binding.normalButton
        val preHypertensionButton = binding.preHypertensionButton
        val stage1Button = binding.stage1Button
        val stage2Button = binding.stage2Button
        hypotensionButton.setOnClickListener {
            viewModel.systolic.value = 89
            viewModel.diastolic.value = 59
            binding.diagnosisTitle.text = getString(R.string.hypotension_title)
            binding.diagnosisValue.text = getString(R.string.hypotension_value)
            setBorder(hypotensionButton)
            removeBorder(normalButton)
            removeBorder(preHypertensionButton)
            removeBorder(stage1Button)
            removeBorder(stage2Button)
        }
        normalButton.setOnClickListener {
            viewModel.systolic.value = 120
            viewModel.diastolic.value = 61
            binding.diagnosisTitle.text = getString(R.string.normal_title)
            binding.diagnosisValue.text = getString(R.string.normal_value)
            setBorder(normalButton)
            removeBorder(hypotensionButton)
            removeBorder(preHypertensionButton)
            removeBorder(stage1Button)
            removeBorder(stage2Button)
        }
        preHypertensionButton.setOnClickListener {
            viewModel.systolic.value = 130
            viewModel.diastolic.value = 81
            binding.diagnosisTitle.text = getString(R.string.prehypertension_title)
            binding.diagnosisValue.text = getString(R.string.prehypertension_value)
            setBorder(preHypertensionButton)
            removeBorder(hypotensionButton)
            removeBorder(normalButton)
            removeBorder(stage1Button)
            removeBorder(stage2Button)
        }

        stage1Button.setOnClickListener {
            viewModel.systolic.value = 150
            viewModel.diastolic.value = 91
            binding.diagnosisTitle.text = getString(R.string.stage_1_title)
            binding.diagnosisValue.text = getString(R.string.stage_1_value)
            setBorder(stage1Button)
            removeBorder(hypotensionButton)
            removeBorder(normalButton)
            removeBorder(preHypertensionButton)
            removeBorder(stage2Button)
        }
        stage2Button.setOnClickListener {
            viewModel.systolic.value = 161
            binding.diastolic.value = 101
            binding.diagnosisTitle.text = getString(R.string.stage_2_title)
            binding.diagnosisValue.text = getString(R.string.stage_2_value)
            setBorder(stage2Button)
            removeBorder(hypotensionButton)
            removeBorder(normalButton)
            removeBorder(preHypertensionButton)
            removeBorder(stage1Button)
        }
    }


    private fun configureNumberPickers() {
        val numberPickerSystolic = binding.systolic
        numberPickerSystolic.minValue = 20
        numberPickerSystolic.maxValue = 220
        numberPickerSystolic.wrapSelectorWheel = true
        val numberPickerDiastolic = binding.diastolic
        numberPickerDiastolic.minValue = 20
        numberPickerDiastolic.maxValue = 220
        numberPickerDiastolic.wrapSelectorWheel = true
        val numberPickerPulse = binding.pulse
        numberPickerPulse.minValue = 30
        numberPickerPulse.maxValue = 200
        numberPickerPulse.wrapSelectorWheel = true
    }

    private fun setBorder(button: MaterialButton) {
        button.strokeWidth = 6
    }

    private fun removeBorder(button: MaterialButton) {
        button.strokeWidth = 0
    }

    private fun setHighPressureDiagnose() {

        val systolic = binding.systolic.value
        val diastolic = binding.diastolic.value
        val hypotensionButton = binding.hypotensionButton
        val normalButton = binding.normalButton
        val preHypertensionButton = binding.preHypertensionButton
        val stage1Button = binding.stage1Button
        val stage2Button = binding.stage2Button

        binding.hypotensionButton.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        binding.hypotensionButton.strokeWidth = 0

        when (getBloodPressureDiagnosis(systolic, diastolic)) {
            BloodPressureDiagnosis.Stage2 -> {
                binding.diagnosisTitle.text = getString(R.string.stage_2_title)
                binding.diagnosisValue.text = getString(R.string.stage_2_value)
                setBorder(stage2Button)
                removeBorder(stage1Button)
                removeBorder(preHypertensionButton)
                removeBorder(normalButton)
                removeBorder(hypotensionButton)
            }
            BloodPressureDiagnosis.Stage1 -> {
                binding.diagnosisTitle.text = getString(R.string.stage_1_title)
                binding.diagnosisValue.text = getString(R.string.stage_1_value)
                removeBorder(stage2Button)
                setBorder(stage1Button)
                removeBorder(preHypertensionButton)
                removeBorder(normalButton)
                removeBorder(hypotensionButton)
            }
            BloodPressureDiagnosis.Pre -> {
                binding.diagnosisTitle.text = getString(R.string.prehypertension_title)
                binding.diagnosisValue.text = getString(R.string.prehypertension_value)
                removeBorder(stage2Button)
                removeBorder(stage1Button)
                setBorder(preHypertensionButton)
                removeBorder(normalButton)
                removeBorder(hypotensionButton)
            }
            BloodPressureDiagnosis.Normal -> {
                binding.diagnosisTitle.text = getString(R.string.normal_title)
                binding.diagnosisValue.text = getString(R.string.normal_value)
                removeBorder(stage2Button)
                removeBorder(stage1Button)
                removeBorder(preHypertensionButton)
                setBorder(normalButton)
                removeBorder(hypotensionButton)
            }
            BloodPressureDiagnosis.Hypotension -> {
                binding.diagnosisTitle.text = getString(R.string.hypotension_title)
                binding.diagnosisValue.text = getString(R.string.hypotension_value)
                removeBorder(stage2Button)
                removeBorder(stage1Button)
                removeBorder(preHypertensionButton)
                removeBorder(normalButton)
                setBorder(hypotensionButton)
            }
        }
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment { onTimeSelected(it) }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String) {
        viewModel.currentTime.value = time
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            viewModel.currentDate.value = LocalDateTime.of(year,month + 1,day,0,0).format(DateTimeFormatter.ISO_DATE)
        } else {
            val monthPlus1 = month + 1
            val selectedDate = "-$year-$monthPlus1-$day"
            viewModel.currentDate.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate).time))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_new_record_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val startDate = viewModel.currentDate.value
        val startTime = viewModel.currentTime.value

        val highPressure = BloodPressure(
            viewModel.systolic.value?.toDouble(),
            viewModel.diastolic.value?.toDouble(),
            viewModel.pulse.value?.toDouble(),
            viewModel.notes.value,
            startTime,
            startDate,
        null,
        null,
        null)
        viewModel.saveHighPressure(highPressure)
        this.findNavController().navigate(R.id.action_highPressureSaveFragment_to_navigation_home)
        return true
    }
}