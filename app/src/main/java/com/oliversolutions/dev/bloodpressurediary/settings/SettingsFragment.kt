package com.oliversolutions.dev.bloodpressurediary.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment()  {
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        binding.remindersLayout.setOnClickListener{
            this.findNavController().navigate(SettingsFragmentDirections.actionNavigationNotificationsToNotificationsFragment())
        }
        binding.dataLinearLayout.setOnClickListener {
            this.findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToDataFragment())
        }
        binding.rateUsLinearLayout.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_url)))
            startActivity(i)
        }
        binding.privacyPolicyLinearLayout.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(i)
        }
        return binding.root
    }
}