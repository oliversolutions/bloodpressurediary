package com.oliversolutions.dev.bloodpressurediary

import android.app.Dialog
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oliversolutions.dev.bloodpressurediary.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var running = false

    override fun onStart() {
        running = true
        super.onStart()
    }
    override fun onStop() {
        running = false
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_statistics, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(0)

        Handler(Looper.getMainLooper()).postDelayed({
            showRateUsDialog()
        }, 60000)

    }

    private fun showRateUsDialog() {
        if (!App.prefs.rateUsIsLaunched && running) {
            try {
                val view = layoutInflater.inflate(R.layout.dialog_fragment_rate_us, null)
                var dialog = Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
                dialog.setContentView(view)
                dialog.show()
                dialog.findViewById<View>(R.id.closeRateUs).setOnClickListener{
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.rateUsButton).setOnClickListener{
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_url)))
                    startActivity(i)
                }
                App.prefs.rateUsIsLaunched = true
            } catch (e: IllegalStateException) {
                Log.d("RateUsDialogFragment", "Exception", e);
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return this.findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
    }


}