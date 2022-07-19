package com.oliversolutions.dev.bloodpressurediary.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.oliversolutions.dev.bloodpressurediary.*
import com.oliversolutions.dev.bloodpressurediary.base.BaseFragment
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentBloodPressureBinding
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import com.oliversolutions.dev.bloodpressurediary.utils.BloodPressureDiagnosis
import com.oliversolutions.dev.bloodpressurediary.utils.getBloodPressureDiagnosis
import com.oliversolutions.dev.bloodpressurediary.utils.getBloodPressureDiastolic
import com.oliversolutions.dev.bloodpressurediary.utils.getBloodPressureSystolic
import org.koin.android.ext.android.get
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BloodPressureFragment : BaseFragment() {

    private lateinit var binding: FragmentBloodPressureBinding
    override lateinit var _viewModel: BloodPressureViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_blood_pressure,
            container,
            false
        )
        binding.lifecycleOwner = this

        val viewModelFactory = BloodPressureViewModelFactory(
           BloodPressureFragmentArgs.fromBundle(requireArguments()).bloodPressure,
            requireActivity().application,
            BloodPressureRepository(get())
        )
        _viewModel = ViewModelProvider(this, viewModelFactory).get(BloodPressureViewModel::class.java)
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        configureNumberPickers()
        configureDateInput()
        configureObservers()

        binding.diagnosisLinearlayout.setOnClickListener {
            BloodPressureTypesDialogFragment().show(childFragmentManager, BloodPressureTypesDialogFragment.TAG)
        }
        return binding.root
    }

    private fun configureObservers() {
        _viewModel.systolic.observe(viewLifecycleOwner) {
            setBloodPressureDiagnose()
        }
        _viewModel.diastolic.observe(viewLifecycleOwner) {
            setBloodPressureDiagnose()
        }
    }

    private fun configureDateInput() {
        binding.creationDate.showSoftInputOnFocus = false
        binding.creationTime.showSoftInputOnFocus = false
        binding.creationTime.setOnClickListener{ showTimePickerDialog() }
        binding.timeImageView.setOnClickListener{ showTimePickerDialog() }
        binding.creationDate.setOnClickListener{ showDatePickerDialog() }
        binding.dateImageView.setOnClickListener{ showDatePickerDialog() }
    }


    private fun configureNumberPickers() {
        val numberPickerSystolic = binding.systolic
        numberPickerSystolic.minValue = SYSTOLIC_MIN_VALUE
        numberPickerSystolic.maxValue = SYSTOLIC_MAX_VALUE
        numberPickerSystolic.wrapSelectorWheel = true

        val numberPickerDiastolic = binding.diastolic
        numberPickerDiastolic.minValue = DIASTOLIC_MIN_VALUE
        numberPickerDiastolic.maxValue = DIASTOLIC_MAX_VALUE
        numberPickerDiastolic.wrapSelectorWheel = true

        val numberPickerPulse = binding.pulse
        numberPickerPulse.minValue = PULSE_MIN_VALUE
        numberPickerPulse.maxValue = PULSE_MAX_VALUE
        numberPickerPulse.wrapSelectorWheel = true
    }

    private fun setBorder(button: MaterialButton) {
        button.strokeWidth = 6
    }

    private fun removeBorder(button: MaterialButton) {
        button.strokeWidth = 0
    }

    private fun setBloodPressureDiagnose() {
        val systolic = binding.systolic.value
        val diastolic = binding.diastolic.value
        val hypotensionButton = binding.hypotensionButton
        val normalButton = binding.normalButton
        val preHypertensionButton = binding.preHypertensionButton
        val stage1Button = binding.stage1Button
        val stage2Button = binding.stage2Button

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (_viewModel.bloodPressure == null) {
            inflater.inflate(R.menu.add_new_record_menu, menu)
        } else {
            inflater.inflate(R.menu.edit_record_menu, menu)
        }
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment { onTimeSelected(it) }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String) {
        _viewModel.creationTime.value = time
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _viewModel.creationDate.value = LocalDateTime.of(year,month + 1,day,0,0).format(DateTimeFormatter.ISO_DATE)
        } else {
            val monthPlus1 = month + 1
            val selectedDate = "$year-$monthPlus1-$day"
            _viewModel.creationDate.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Date(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate).time)
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_record -> {
                _viewModel.deleteRecord()
            }
            R.id.edit_record -> {
                _viewModel.editBloodPressure()
            }
            R.id.add_new_record -> {
                val startDate = _viewModel.creationDate.value
                val startTime = _viewModel.creationTime.value
                val bloodPressure = BloodPressure(
                    _viewModel.systolic.value?.toDouble(),
                    _viewModel.diastolic.value?.toDouble(),
                    _viewModel.pulse.value?.toDouble(),
                    _viewModel.notes.value,
                    startTime,
                    startDate)
                _viewModel.createNewBloodPressure(bloodPressure)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val SYSTOLIC_MIN_VALUE = 20
        const val SYSTOLIC_MAX_VALUE = 220
        const val DIASTOLIC_MIN_VALUE = 20
        const val DIASTOLIC_MAX_VALUE = 220
        const val PULSE_MIN_VALUE = 30
        const val PULSE_MAX_VALUE = 200
    }
}