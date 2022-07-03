package com.oliversolutions.dev.bloodpressurediary.main

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.oliversolutions.dev.bloodpressurediary.*
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.base.BaseFragment
import com.oliversolutions.dev.bloodpressurediary.base.NavigationCommand
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : BaseFragment() {
    private lateinit var bloodPressureGridAdapter: BloodPressureGridAdapter
    private lateinit var binding: FragmentMainBinding
    private var mInterstitialAd: InterstitialAd? = null
    override val _viewModel: MainViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel
        binding.bloodPressureRecycler.adapter =
            BloodPressureGridAdapter(BloodPressureGridAdapter.OnClickListener {
                _viewModel.navigationCommand.value = NavigationCommand.To(
                    MainFragmentDirections.actionNavigationHomeToHighPressureEditFragment(
                        it,
                        getString(R.string.edit_record)
                    )
                )
            }).apply {
                bloodPressureGridAdapter = this
            }
        loadAds()
        binding.bloodPressureSave.setOnClickListener {
            showAd()
            _viewModel.navigationCommand.value = NavigationCommand.To(
                MainFragmentDirections.actionNavigationHomeToHighPressureEditFragment(
                    null,
                    getString(R.string.add_new_record_nav)
                )
            )
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeBloodPressureValues() {
        _viewModel.bloodPressureValues.observe(viewLifecycleOwner) {
            bloodPressureGridAdapter.addHeaderAndSubmitList(
                it,
                _viewModel.fromDate,
                _viewModel.toDate
            )
            App.prefs.fromDate = _viewModel.fromDate
            App.prefs.toDate = _viewModel.toDate
        }
    }
}