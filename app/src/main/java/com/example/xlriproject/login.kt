package com.example.xlriproject

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class login : AppCompatActivity() {
    var email: EditText? = null
    var password: EditText? = null
    var login_btn: Button? = null
    var forgotpassword: TextView? = null
    var firebaseauth: FirebaseAuth? = null
    var userreference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.email_login)
        password = findViewById(R.id.password_login)
        login_btn = findViewById(R.id.login_user_btn)
        forgotpassword = findViewById(R.id.forgot_password)
        firebaseauth = FirebaseAuth.getInstance()
        userreference = FirebaseDatabase.getInstance().reference
        login_btn!!.setOnClickListener {
            loginUser(email!!, password!!)
        }
        forgotpassword!!.setOnClickListener {
            startActivity(Intent(applicationContext,forgot_password::class.java))
        }
    }

    private fun loginUser(email: EditText, password: EditText) {
        var email = email.text.toString().trim()
        var password = password.text.toString().trim()
        if (TextUtils.isEmpty(email))
            Toast.makeText(applicationContext, "Email field can't be empty", Toast.LENGTH_SHORT).show()
        else
            if (TextUtils.isEmpty(password))
                Toast.makeText(applicationContext, "Password field can't be empty", Toast.LENGTH_SHORT).show()
            else {
                var progressDialog = ProgressDialog(this)
                progressDialog?.setTitle("Login")
                progressDialog?.setMessage("Please wait a while you are allowed to login into your account...")
                progressDialog?.setCanceledOnTouchOutside(false)
                progressDialog?.show()
                firebaseauth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(object :
                    OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful) {
                            var firebaseuser = firebaseauth!!.currentUser
                            if (firebaseuser!!.isEmailVerified) {
                                progressDialog?.dismiss()
                                var uid = firebaseuser!!.uid
                                checkCategoryFromUid(uid)
                                Toast.makeText(
                                    applicationContext,
                                    "You are logged in successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                firebaseuser.sendEmailVerification()
                                Toast.makeText(
                                    applicationContext,
                                    "Your email is not verified. We have sent a verification link to your email id.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog?.dismiss()
                            }
                        } else {
                            var err = p0.exception?.message
                            progressDialog?.dismiss()
                            Toast.makeText(applicationContext, err, Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
    }

    private fun checkCategoryFromUid(uid: String) {
        userreference!!.child("Teacher").child(uid)!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                 startActivity(Intent(applicationContext, TeacherDetails::class.java))
                    finish()
                }
                else {
                    userreference!!.child("Student").child(uid)!!.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                Toast.makeText(applicationContext, "Welcome Student", Toast.LENGTH_SHORT).show()
                            } else {
                                userreference!!.child("Parent").child(uid)!!.addValueEventListener(object :
                                    ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if (p0.exists()) {
                                            startActivity(Intent(applicationContext, ParentDetails::class.java))
                                            finish()
                                        }
                                    }
                                })
                            }


                        }
                    })
                }
            }
        })
    }
}