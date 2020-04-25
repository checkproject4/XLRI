package com.example.xlriproject

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class register : AppCompatActivity() {
var email:EditText?=null
    var password:EditText?=null
    var repassword:EditText?=null
    var register_btn: Button?= null
    var firebaseauth:FirebaseAuth?=null
    var userreference:DatabaseReference?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        email=findViewById(R.id.email_register)
        password=findViewById(R.id.password_register)
        repassword=findViewById(R.id.repassword_register)
        register_btn=findViewById(R.id.register_user_btn)
        firebaseauth=FirebaseAuth.getInstance()
        var category=intent.getStringExtra("category")
        register_btn!!.setOnClickListener {
            registerTeacher(email!!,password!!,repassword!!,category)
        }
    }
    private fun registerTeacher(email: EditText, password: EditText, repassword: EditText, category: String) {
        var email=email.text.toString().trim()
        var password=password.text.toString().trim()
        var repassword=repassword.text.toString().trim()

        if(TextUtils.isEmpty(email))
            Toast.makeText(applicationContext,"Email field can't be empty",Toast.LENGTH_SHORT).show()
        else
            if(TextUtils.isEmpty(password))
                Toast.makeText(applicationContext,"Password field can't be empty",Toast.LENGTH_SHORT).show()
        if(TextUtils.isEmpty(repassword))
            Toast.makeText(applicationContext,"Re password field can't be empty",Toast.LENGTH_SHORT).show()
        if(!password.equals(repassword))
            Toast.makeText(applicationContext,"Password and Confirm Password is not matching",Toast.LENGTH_SHORT).show()
else{
            var   loadingbar= ProgressDialog(this)
            loadingbar?.setTitle("Registering")
            loadingbar?.setMessage("Please wait a while we are registering you...")
            loadingbar?.show()
            loadingbar?.setCanceledOnTouchOutside(false)
            firebaseauth?.createUserWithEmailAndPassword(email,password)?.addOnCompleteListener(object :
                OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if(p0.isSuccessful){
                        var firebaseuser=firebaseauth?.currentUser
                        userreference=FirebaseDatabase.getInstance().getReference(category)
                        userreference!!.child(firebaseuser!!.uid)!!.setValue(firebaseuser!!.uid).addOnCompleteListener(object :OnCompleteListener<Void>{
                            override fun onComplete(p0: Task<Void>) {
                                if(p0.isSuccessful){
                                    Toast.makeText(applicationContext,"You are registered.",Toast.LENGTH_SHORT).show()
                                    firebaseuser?.sendEmailVerification()?.addOnCompleteListener(object :
                                        OnCompleteListener<Void> {
                                        override fun onComplete(p0: Task<Void>) {
                                            if(p0?.isSuccessful){
                                                loadingbar?.dismiss()
                                                Toast.makeText(applicationContext,"Email verification link has been sent.",Toast.LENGTH_SHORT).show()
                                            }
                                            else{
                                                loadingbar?.dismiss()
                                                var err=p0?.exception?.message
                                                Toast.makeText(applicationContext,err,Toast.LENGTH_SHORT).show()
                                            }

                                        }

                                    })
                                }
                                else{
                                    var err=p0.exception?.message
                                    Toast.makeText(applicationContext,err,Toast.LENGTH_SHORT).show()
                                    loadingbar?.dismiss()
                                }
                            }

                        })


                    }
                    else{
                        var err=p0.exception?.message
                        Toast.makeText(applicationContext,err,Toast.LENGTH_SHORT).show()
                        loadingbar?.dismiss()


                    }
                }

            })
        }

   }
}
