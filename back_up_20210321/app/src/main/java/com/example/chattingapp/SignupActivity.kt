package com.example.chattingapp

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.net.toUri
import com.example.chattingapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private val PICK_FROM_ALBUM: Int = 10
    private lateinit var email: EditText
    private lateinit var name: EditText
    private lateinit var pw: EditText
    private lateinit var signup: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var profile: ImageView
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        profile = signupActivity_imageview_profile
        profile.setOnClickListener{
            val intent: Intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(intent, PICK_FROM_ALBUM)
        }
        email = signupActivity_edittext_email
        name = signupActivity_edittext_name
        pw = signupActivity_edittext_pw
        signup = signupActivity_button_signup
        auth = FirebaseAuth.getInstance()

        signup.setOnClickListener{

            if(email.text.toString() == null || name.text.toString() == null || pw.text.toString() == null){
                return@setOnClickListener;
            }

            auth.createUserWithEmailAndPassword(email.text.toString(), pw.text.toString())
                    .addOnCompleteListener(this){ task ->
                        val uid:String = task.getResult()?.user!!.uid
                        val ref = FirebaseStorage.getInstance().reference.child("userImages").child(auth.currentUser!!.uid)
                        var uploadTask = ref.putFile(imageUri)

                        uploadTask.addOnFailureListener{
                            // Handle Exception
                        }

                        val uriTask = uploadTask.continueWithTask{ task ->
                            if(!task.isSuccessful){
                                task.exception?.let{
                                    throw it
                                }
                            }
                            ref.downloadUrl
                        }.addOnCompleteListener{ task ->
                            if(task.isSuccessful){
                                val downloadUri = task.result.toString()
                                var userModel = UserModel(name.text.toString(), downloadUri, FirebaseAuth.getInstance().currentUser!!.uid)

                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener {
                                    it -> finish()
                                }

                            }else{
                                // Handle Failure
                            }
                        }
                    }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == Activity.RESULT_OK){
            profile.setImageURI(data?.data)
            imageUri = data?.data!!

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}