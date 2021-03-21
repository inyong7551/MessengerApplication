package com.example.chattingapp

import android.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.chattingapp.fragment.ChatFragment
import com.example.chattingapp.fragment.PeopleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat.*

class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = mainactivity_bottomnaviationview
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_people -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, PeopleFragment()).commit()
                }
                R.id.action_chat -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, ChatFragment()).commit()
                }
                else -> null
            }
            true
        }
        supportFragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, PeopleFragment()).commit()
    }
}