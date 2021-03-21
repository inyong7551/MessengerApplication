package com.example.chattingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    private lateinit var id: EditText
    private lateinit var pw: EditText
    private lateinit var login: Button
    private lateinit var signup: Button
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        id = loginActivity_edittext_id
        pw = loginActivity_edittext_pw
        login = loginActivity_button_login
        signup = loginActivity_button_signup
        remoteConfig = FirebaseRemoteConfig.getInstance()
        auth = FirebaseAuth.getInstance()

        auth.signOut()

        login.setOnClickListener{
            loginEvent()
        }

        signup.setOnClickListener{
            startActivity<SignupActivity>()
        }

        authStateListener = FirebaseAuth.AuthStateListener(){
            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            if(user != null){
                // 로그인
                startActivity<MainActivity>()
                finish()
            }else{
                // 로그 아웃
            }
        }
    }

    private fun loginEvent(){
        auth.signInWithEmailAndPassword(id.text.toString(), pw.text.toString()).addOnCompleteListener{ task ->
            if(!task.isSuccessful){
                // Login failure
                toast(task.exception!!.message.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }
}