package com.example.xlriproject

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class forgot_password : AppCompatActivity() {
var email:EditText?=null
    var send_reset_btn:Button?=null
    var firebaseauth:FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        email=findViewById(R.id.email_forgotpassword)
        send_reset_btn=findViewById(R.id.forgot_password)
        firebaseauth=FirebaseAuth.getInstance()
        send_reset_btn!!.setOnClickListener { 
            sendResetLink(email!!)
        }
    }

    private fun sendResetLink(email: EditText) {
        var emailid=email.text.toString().trim()
        if(TextUtils.isEmpty(emailid))
            Toast.makeText(applicationContext,"Please type email id...", Toast.LENGTH_SHORT).show()
        else{
            var progressDialog= ProgressDialog(this)
            progressDialog?.setTitle("Sending password reset link")
            progressDialog?.setMessage("Please wait...")
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.show()
            firebaseauth?.sendPasswordResetEmail(emailid)?.addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {
                    if(p0.isSuccessful){
                        progressDialog?.dismiss()
                        Toast.makeText(applicationContext,"The password reset link has been sent to your email id.",
                            Toast.LENGTH_SHORT).show()

                    }
                    else{
                        progressDialog?.dismiss()
                        var err=p0.exception?.message
                        Toast.makeText(applicationContext,err, Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }

    }
}
