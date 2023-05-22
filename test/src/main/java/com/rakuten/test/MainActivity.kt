package com.rakuten.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rmc_iam.AppStartEvent
import com.example.rmc_iam.RmcIam

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        RmcIam.instance().logEvent(AppStartEvent())
    }

    override fun onResume() {
        super.onResume()
        RmcIam.instance().registerMessageDisplayActivity(this)
    }

    override fun onPause() {
        super.onPause()
        RmcIam.instance().unregisterMessageDisplayActivity()
    }
}