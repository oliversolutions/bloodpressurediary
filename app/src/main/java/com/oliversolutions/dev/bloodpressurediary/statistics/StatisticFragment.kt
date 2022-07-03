package com.oliversolutions.dev.bloodpressurediary.statistics

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import com.oliversolutions.dev.bloodpressurediary.App
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentStatisticBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.oliversolutions.dev.bloodpressurediary.base.BaseFragment
import ir.androidexception.datatable.model.DataTableHeader
import ir.androidexception.datatable.model.DataTableRow
import ir.mahozad.android.PieChart
import ir.mahozad.android.unit.Dimension
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class StatisticFragment : BaseFragment() {

    private lateinit var binding: FragmentStatisticBinding
    override val _viewModel: StatisticViewModel by viewModel()

    fun loadPieChart() {
        val pieChart = binding.pieChart
        pieChart.apply {
            slices = listOf(
                PieChart.Slice(
                    _viewModel.bloodPressureStatisticData.stage_2!!,
                    Color.rgb(255, 68, 68),
                    legend = context.getString(R.string.stage_2_legend_title),
                    legendIcon = R.drawable.ic_about,
                    legendColor = Color.BLACK,
                    legendIconTint = Color.rgb(255,68,68)
                ),
                PieChart.Slice(
                    _viewModel.bloodPressureStatisticData.stage_1!!,
                    Color.rgb(255, 136, 0),
                    legend = context.getString(R.string.stage_1_legend_title),
                    legendIcon = R.drawable.ic_about,
                    legendIconTint = Color.rgb(255,136,0),
                    legendColor = Color.BLACK
                ),
                PieChart.Slice(
                    _viewModel.bloodPressureStatisticData.pre!!,
                    Color.rgb(255, 187, 51),
                    legend = context.getString(R.string.prehypertension_legend_title),
                    legendIcon = R.drawable.ic_about,
                    legendIconTint = Color.rgb(255, 187, 51),
                    legendColor = Color.BLACK
                ),
                PieChart.Slice(
                    _viewModel.bloodPressureStatisticData.normal!!,
                    Color.rgb(153, 204, 0),
                    legend = context.getString(R.string.normal_legend_title),
                    legendIcon = R.drawable.ic_about,
                    legendIconTint = Color.rgb(153, 204, 0),
                    legendColor = Color.BLACK
                ),
                PieChart.Slice(
                    _viewModel.bloodPressureStatisticData.hypotension!!,
                    Color.rgb(51, 181, 229),
                    legend = context.getString(R.string.hypotension_legend_title),
                    legendIcon = R.drawable.ic_about,
                    legendIconTint = Color.rgb(51, 181, 229),
                    legendColor = Color.BLACK
                )
            ).filter{
                it.fraction > 0f
            }
            startAngle = 0
            labelType = PieChart.LabelType.INSIDE
            outsideLabelsMargin = Dimension.DP(8f)
            gradientType = PieChart.GradientType.RADIAL
            legendPosition = PieChart.LegendPosition.BOTTOM
            legendBoxBorder = Dimension.DP(2f)
            legendBoxBorderCornerRadius = Dimension.DP(8f)
            legendTitleMargin = Dimension.DP(14f)
            legendLinesMargin = Dimension.DP(10f)
            legendsMargin = Dimension.DP(20f)
            legendsPercentageMargin = Dimension.DP(8f)
            legendsSize = Dimension.SP(15f)
            legendsPercentageSize = Dimension.SP(15f)
            isLegendEnabled = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStatisticBinding.inflate(inflater, container, false)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        observeHighPressureValues()
        setHasOptionsMenu(true)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeHighPressureValues() {
        _viewModel.bloodPressureValues.observe(viewLifecycleOwner) {
            _viewModel.numRecords = it.size
            App.prefs.fromDate = _viewModel.fromDate
            App.prefs.toDate = _viewModel.toDate
            bindHeader()
            if (it.isNotEmpty()) {
                binding.noDataFoundLayout.visibility = View.GONE
                binding.pieChart.visibility = View.VISIBLE
                binding.dataTable.visibility = View.VISIBLE
                _viewModel.setBloodPressureStatisticData(it)
                loadPieChart()
                bindDataTable()
            } else {
                binding.pieChart.visibility = View.GONE
                binding.dataTable.visibility = View.GONE
                binding.noDataFoundLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun bindDataTable () {
        val dataTable = binding.dataTable
        val arrayListFieldName = arrayListOf("", getString(R.string.max), getString(R.string.min), getString(R.string.average))
        val arrayListWeight = arrayListOf(2, 1, 1, 2)
        dataTable.headerTextSize = 16f
        dataTable.headerBackgroundColor = Color.rgb(41, 47, 61)
        dataTable.headerTextColor = Color.WHITE
        dataTable.rowTextSize = 12f
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING

        val rows = arrayListOf(
            DataTableRow(arrayListOf(
                getString(R.string.systolic_column_name_text_value),
                df.format(_viewModel.bloodPressureStatisticData.maxSys).toString(),
                df.format(_viewModel.bloodPressureStatisticData.minSys).toString(),
                df.format(_viewModel.bloodPressureStatisticData.averageSys).toString()
            )),

            DataTableRow(arrayListOf(
                getString(R.string.diastolic_column_name_text_value),
                df.format(_viewModel.bloodPressureStatisticData.maxDia).toString(),
                df.format(_viewModel.bloodPressureStatisticData.minDia).toString(),
                df.format(_viewModel.bloodPressureStatisticData.averageDia).toString()
            )),

            DataTableRow(arrayListOf(
                getString(R.string.pulse_text_view_value),
                df.format(_viewModel.bloodPressureStatisticData.maxPulse).toString(),
                df.format(_viewModel.bloodPressureStatisticData.minPulse).toString(),
                df.format(_viewModel.bloodPressureStatisticData.averagePulse).toString()
            )),
        )
        dataTable.rows = rows
        dataTable.header = DataTableHeader(arrayListFieldName, arrayListWeight)
        dataTable.inflate(requireContext())
    }

    private fun bindHeader() {
        if (_viewModel.fromDate.isEmpty() || _viewModel.toDate.isEmpty()) {
            binding.fromDateToDateTextView.text = binding.fromDateToDateTextView.context.getString(R.string.all_records)
        } else if (_viewModel.fromDate.isNotEmpty() && _viewModel.toDate.isNotEmpty()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("dd-MMMM")
                val formattedFromDate = LocalDate.parse(_viewModel.fromDate).format(formatter)
                val formattedToDate = LocalDate.parse(_viewModel.toDate).format(formatter)
                binding.fromDateToDateTextView.text = "$formattedFromDate - $formattedToDate"
            } else {
                val formattedFromDate = SimpleDateFormat("dd-MMMM", Locale.getDefault()).format(Date(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(_viewModel.fromDate).time))
                val formattedToDate = SimpleDateFormat("dd-MMMM", Locale.getDefault()).format(Date(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(_viewModel.toDate).time))
                binding.fromDateToDateTextView.text = "$formattedFromDate - $formattedToDate"
            }
        }
        binding.numRecordsTextView.text =
            _viewModel.numRecords.toString() + " " + binding.fromDateToDateTextView.context.getString(R.string.records)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_date_record_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.custom) {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            val now = Calendar.getInstance()
            builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
            val picker = builder.build()
            picker.show(activity?.supportFragmentManager!!, picker.toString())
            picker.addOnPositiveButtonClickListener {
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.first
                val customFromDate = formatter.format(calendar.time)
                calendar.timeInMillis = it.second
                val customToDate = formatter.format(calendar.time)
                _viewModel.updateFilter(item, customFromDate, customToDate)
            }
        }
        if (intArrayOf(R.id.all_records,
                R.id.today,
                R.id.yesterday,
                R.id.last_7_days,
                R.id.last_14_days,
                R.id.last_30_days,
                R.id.last_60_days,
                R.id.last_90_days,
            ).contains(item.itemId)) {
            _viewModel.updateFilter(item)
        }
        return true
    }

}