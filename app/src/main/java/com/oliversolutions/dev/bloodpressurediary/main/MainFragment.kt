package com.oliversolutions.dev.bloodpressurediary.main

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.oliversolutions.dev.bloodpressurediary.*
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.settings.DataViewModel
import com.oliversolutions.dev.bloodpressurediary.statistics.StatisticViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var bloodPressureGridAdapter: BloodPressureGridAdapter
    private lateinit var binding: FragmentMainBinding
    private var mInterstitialAd: InterstitialAd? = null

    val mainViewModel: MainViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        binding.bloodPressureRecycler.adapter = BloodPressureGridAdapter(BloodPressureGridAdapter.OnClickListener {
            mainViewModel.displayEditBloodPressure(it)
        }).apply {
            bloodPressureGridAdapter = this
        }
        loadAds()
        binding.bloodPressureSave.setOnClickListener{
            showAd()
            mainViewModel.displayEditBloodPressure(null)
        }
        mainViewModel.navigateToSelectedBloodPressure.observeOnce(viewLifecycleOwner, {
            showAd()
            val labelString = if (it == null)  {
                getString(R.string.add_new_record_nav)
            } else {
                getString(R.string.edit_record)
            }
            this.findNavController().navigate(MainFragmentDirections.actionNavigationHomeToHighPressureEditFragment(it, labelString))
        })
        setHasOptionsMenu(true)
        observeBloodPressureValues()
        return binding.root
    }

    private fun showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity as Activity)
        }
    }

    private fun loadAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            getString(R.string.bp_intersticial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_date_record_menu, menu)
    }

    @SuppressLint("SimpleDateFormat")
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
                val selectedFromDate = formatter.format(calendar.time)
                calendar.timeInMillis = it.second
                val selectedToDate = formatter.format(calendar.time)
                mainViewModel.updateFilter(BloodPressureFilter.SHOW_CUSTOM, selectedFromDate, selectedToDate)
                observeBloodPressureValues()
            }
            return true
        } else {
            mainViewModel.updateFilter(
                when (item.itemId) {
                    R.id.all_records -> {
                        binding.progressBar.visibility = View.VISIBLE
                        BloodPressureFilter.SHOW_ALL
                    }
                    R.id.today -> {
                        binding.progressBar.visibility = View.VISIBLE
                        BloodPressureFilter.SHOW_TODAY
                    }
                    R.id.yesterday -> {
                        binding.progressBar.visibility = View.VISIBLE
                        BloodPressureFilter.SHOW_YESTERDAY
                    }
                    R.id.last_7_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        BloodPressureFilter.SHOW_LAST_7_DAYS
                    }
                    R.id.last_14_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        BloodPressureFilter.SHOW_LAST_14_DAYS
                    }
                    R.id.last_30_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        BloodPressureFilter.SHOW_LAST_30_DAYS
                    }
                    R.id.last_60_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        BloodPressureFilter.SHOW_LAST_60_DAYS
                    }
                    R.id.last_90_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        BloodPressureFilter.SHOW_LAST_90_DAYS
                    }

                    else -> return false
                }
            )
            observeBloodPressureValues()
            return true
        }
    }

    private fun observeBloodPressureValues() {
        mainViewModel.bloodPressureValues.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.noDataFoundLayout.visibility = View.VISIBLE
                if (App.prefs.isLaunched) {
                    binding.noDataFoundTitle.text = getString(R.string.oops)
                    binding.noDataFoundDescription.text = getString(R.string.no_data_found)
                } else {
                    binding.noDataFoundTitle.text = getString(R.string.get_started)
                    binding.noDataFoundDescription.text = getString(R.string.add_new_record)

                    App.prefs.isLaunched = true
                }
            } else {
                binding.noDataFoundLayout.visibility = View.GONE
            }
            bloodPressureGridAdapter.addHeaderAndSubmitList(it, mainViewModel.fromDate, mainViewModel.toDate)
            App.prefs.fromDate = mainViewModel.fromDate
            App.prefs.toDate = mainViewModel.toDate
            binding.progressBar.visibility = View.INVISIBLE
        })
    }
}