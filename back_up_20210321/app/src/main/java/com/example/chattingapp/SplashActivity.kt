package com.example.chattingapp

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity


class SplashActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0).build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(R.xml.default_config)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                } else {

                }
                displayMessage()
            }
    }

    private fun displayMessage(){
        val splash_background = remoteConfig.getString("splash_background")
        val caps = remoteConfig.getBoolean("splash_message_caps")
        val splash_message = remoteConfig.getString("splash_message")

        splashactivity_linearlayout.setBackgroundColor(Color.parseColor(splash_background))

        if(caps) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(splash_message).setPositiveButton(
                "확인",
                DialogInterface.OnClickListener { dialog, which -> finish() })
            builder.create().show()
        }else{
            startActivity<LoginActivity>()
            finish()
        }
    }
}