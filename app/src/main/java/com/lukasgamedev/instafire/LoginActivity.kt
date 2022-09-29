package com.lukasgamedev.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

private const val TAG = "LoginActivity"


class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var etMail: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //set views
        btnLogin = findViewById(R.id.btnLogin)
        etMail = findViewById(R.id.etMail)
        etPassword = findViewById(R.id.etPassword)

        //firebase init
        val auth = FirebaseAuth.getInstance();
        if(auth.currentUser!=null){
            goPostsActivity()
        }

        //login
        btnLogin.setOnClickListener(){
            btnLogin.isEnabled = false
            val email = etMail.text.toString()
            val password = etPassword.text.toString()
            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Email/password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //Firebase authentication check
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
                btnLogin.isEnabled = true
                if(task.isSuccessful()){
                    Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show()
                    goPostsActivity()
                }else{
                    Log.i(TAG,"singInWithEmail failed", task.exception)
                    Toast.makeText(this,"Authetication failed",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
    private fun goPostsActivity(){
        Log.i(TAG,"goPostsActivity")
        val intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }
}