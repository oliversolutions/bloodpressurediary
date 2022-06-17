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
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var bloodPressureGridAdapter: BloodPressureGridAdapter
    private lateinit var binding: FragmentMainBinding
    private var mInterstitialAd: InterstitialAd? = null

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        binding.highPressureRecycler.adapter = BloodPressureGridAdapter(BloodPressureGridAdapter.OnClickListener {
            mainViewModel.displayEditHighPressure(it)
        }).apply {
            bloodPressureGridAdapter = this
        }
        loadAds()
        binding.highPressureSave.setOnClickListener{
            showAd()
            mainViewModel.displayEditHighPressure(null)
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
        observeHighPressureValues()
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
                mainViewModel.updateFilter(HighPressureFilter.SHOW_CUSTOM, selectedFromDate, selectedToDate)
                observeHighPressureValues()
            }
            return true
        } else {
            mainViewModel.updateFilter(
                when (item.itemId) {
                    R.id.all_records -> {
                        binding.progressBar.visibility = View.VISIBLE
                        HighPressureFilter.SHOW_ALL
                    }
                    R.id.today -> {
                        binding.progressBar.visibility = View.VISIBLE
                        HighPressureFilter.SHOW_TODAY
                    }
                    R.id.yesterday -> {
                        binding.progressBar.visibility = View.VISIBLE
                        HighPressureFilter.SHOW_YESTERDAY
                    }
                    R.id.last_7_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        HighPressureFilter.SHOW_LAST_7_DAYS
                    }
                    R.id.last_14_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        HighPressureFilter.SHOW_LAST_14_DAYS
                    }
                    R.id.last_30_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        HighPressureFilter.SHOW_LAST_30_DAYS
                    }
                    R.id.last_60_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        HighPressureFilter.SHOW_LAST_60_DAYS
                    }
                    R.id.last_90_days -> {
                        binding.progressBar.visibility = View.VISIBLE
                        HighPressureFilter.SHOW_LAST_90_DAYS
                    }

                    else -> return false
                }
            )
            observeHighPressureValues()
            return true
        }
    }

    private fun observeHighPressureValues() {
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