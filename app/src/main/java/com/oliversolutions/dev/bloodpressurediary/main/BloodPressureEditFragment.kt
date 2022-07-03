package com.oliversolutions.dev.bloodpressurediary.main

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentBloodPressureEditBinding
import com.google.android.material.button.MaterialButton
import com.oliversolutions.dev.bloodpressurediary.*
import com.oliversolutions.dev.bloodpressurediary.base.BaseFragment
import com.oliversolutions.dev.bloodpressurediary.base.NavigationCommand
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

class BloodPressureEditFragment : BaseFragment() {
    private lateinit var binding: FragmentBloodPressureEditBinding
    override lateinit var _viewModel: BloodPressureEditViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_blood_pressure_edit, container, false
        )
        binding.lifecycleOwner = this
        val viewModelFactory = BloodPressureEditViewModelFactory(
           BloodPressureEditFragmentArgs.fromBundle(requireArguments()).bloodPressure, requireActivity().application, BloodPressureRepository(get())
        )
        _viewModel = ViewModelProvider(this, viewModelFactory).get(BloodPressureEditViewModel::class.java)
        binding.viewModel = _viewModel
        configureLayout()
        return binding.root
    }

    private fun configureLayout() {
        configureNumberPickers()
        configureDateInput()
        binding.diagnosisLinearlayout.setOnClickListener {
            BloodPressureTypesDialogFragment().show(childFragmentManager, BloodPressureTypesDialogFragment.TAG)
        }
        _viewModel.systolic.observe(viewLifecycleOwner) {
            setHighPressureDiagnose()
        }
        _viewModel.diastolic.observe(viewLifecycleOwner) {
            setHighPressureDiagnose()
        }
        setHasOptionsMenu(true)
        configureDiagnosisButtons()
    }

    private fun configureDateInput() {
        binding.creationDate.showSoftInputOnFocus = false
        binding.creationTime.showSoftInputOnFocus = false
        binding.creationTime.setOnClickListener{ showTimePickerDialog() }
        binding.timeImageView.setOnClickListener{ showTimePickerDialog() }
        binding.creationDate.setOnClickListener{ showDatePickerDialog() }
        binding.dateImageView.setOnClickListener{ showDatePickerDialog() }
    }

    private fun configureDiagnosisButtons() {
        val hypotensionButton = binding.hypotensionButton
        val normalButton = binding.normalButton
        val preHypertensionButton = binding.preHypertensionButton
        val stage1Button = binding.stage1Button
        val stage2Button = binding.stage2Button
        hypotensionButton.setOnClickListener {
            _viewModel.systolic.value = getBloodPressureSystolic(BloodPressureDiagnosis.Hypotension)
            _viewModel.diastolic.value = getBloodPressureDiastolic(BloodPressureDiagnosis.Hypotension)
            binding.diagnosisTitle.text = getString(R.string.hypotension_title)
            binding.diagnosisValue.text = getString(R.string.hypotension_value)
            setBorder(hypotensionButton)
            removeBorder(normalButton)
            removeBorder(preHypertensionButton)
            removeBorder(stage1Button)
            removeBorder(stage2Button)
        }
        normalButton.setOnClickListener {
            _viewModel.systolic.value = getBloodPressureSystolic(BloodPressureDiagnosis.Normal)
            _viewModel.diastolic.value = getBloodPressureDiastolic(BloodPressureDiagnosis.Normal)
            binding.diagnosisTitle.text = getString(R.string.normal_title)
            binding.diagnosisValue.text = getString(R.string.normal_value)
            setBorder(normalButton)
            removeBorder(hypotensionButton)
            removeBorder(preHypertensionButton)
            removeBorder(stage1Button)
            removeBorder(stage2Button)
        }
        preHypertensionButton.setOnClickListener {
            _viewModel.systolic.value = getBloodPressureSystolic(BloodPressureDiagnosis.Pre)
            _viewModel.diastolic.value = getBloodPressureDiastolic(BloodPressureDiagnosis.Pre)
            binding.diagnosisTitle.text = getString(R.string.prehypertension_title)
            binding.diagnosisValue.text = getString(R.string.prehypertension_value)
            setBorder(preHypertensionButton)
            removeBorder(hypotensionButton)
            removeBorder(normalButton)
            removeBorder(stage1Button)
            removeBorder(stage2Button)
        }

        stage1Button.setOnClickListener {
            _viewModel.systolic.value = getBloodPressureSystolic(BloodPressureDiagnosis.Stage1)
            _viewModel.diastolic.value = getBloodPressureDiastolic(BloodPressureDiagnosis.Stage1)
            binding.diagnosisTitle.text = getString(R.string.stage_1_title)
            binding.diagnosisValue.text = getString(R.string.stage_1_value)
            setBorder(stage1Button)
            removeBorder(hypotensionButton)
            removeBorder(normalButton)
            removeBorder(preHypertensionButton)
            removeBorder(stage2Button)
        }
        stage2Button.setOnClickListener {
            _viewModel.systolic.value = getBloodPressureSystolic(BloodPressureDiagnosis.Stage2)
            _viewModel.diastolic.value = getBloodPressureDiastolic(BloodPressureDiagnosis.Stage2)
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
                val highPressure = BloodPressure(
                    _viewModel.systolic.value?.toDouble(),
                    _viewModel.diastolic.value?.toDouble(),
                    _viewModel.pulse.value?.toDouble(),
                    _viewModel.notes.value,
                    startTime,
                    startDate,
                    null,
                    null,
                    null)
                _viewModel.createNewBloodPressure(highPressure)
            }
        }
        _viewModel.navigationCommand.value = NavigationCommand.To(BloodPressureEditFragmentDirections.actionHighPressureEditFragmentToNavigationHome())
        return true
    }
}