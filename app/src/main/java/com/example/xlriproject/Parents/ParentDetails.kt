package com.example.xlriproject.Parents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.xlriproject.R
import com.example.xlriproject.Teachers.TeacherActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class ParentDetails : AppCompatActivity() {
    lateinit var RootRef : DatabaseReference
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var name:EditText
    lateinit var age :EditText
    lateinit var sex:EditText
    lateinit var email:EditText
    lateinit var phone:EditText
    lateinit var address:EditText
    lateinit var interest:EditText
    lateinit var profile_btn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_details)
        name = findViewById<EditText>(R.id.parent_name)
        age = findViewById<EditText>(R.id.parent_age)
        sex = findViewById<EditText>(R.id.parent_sex)
        email = findViewById<EditText>(R.id.parent_email)
        phone = findViewById<EditText>(R.id.parent_phone)
        address = findViewById<EditText>(R.id.parent_address)
        interest = findViewById<EditText>(R.id.parent_interests)
        profile_btn = findViewById<Button>(R.id.parent_profile_btn)
        RootRef= FirebaseDatabase.getInstance().getReference("Parent")
        firebaseAuth = FirebaseAuth.getInstance()
        RootRef.child(firebaseAuth!!.currentUser!!.uid)?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("ParentAddress"))
                {
                    var intent: Intent = Intent(this@ParentDetails , ParentActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }

        })
        profile_btn.setOnClickListener {
            CreateProfile()
        }

    }
    private fun CreateProfile(){
        var Name = name.text.toString().trim()
        var Age = age.text.toString().trim()
        var Sex = sex.text.toString().trim()
        var Email = email.text.toString().trim()
        var Phone = phone.text.toString().trim()
        var Address = address.text.toString().trim()
        var Interests = interest.text.toString().trim()

        if(TextUtils.isEmpty(Name)){
            Toast.makeText(applicationContext,"Please Provide Your Name" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Age)){
            Toast.makeText(applicationContext,"Please Provide Your Age" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Sex)){
            Toast.makeText(applicationContext,"Please Provide Your Sex" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Email)){
            Toast.makeText(applicationContext,"Please Provide Your Email" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Phone)){
            Toast.makeText(applicationContext,"Please Provide Your Phone" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Address)){
            Toast.makeText(applicationContext,"Please Provide Your Address" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Interests)){
            Toast.makeText(applicationContext,"Please Provide Your Interests" , Toast.LENGTH_SHORT).show()
        }
        else {
            var ProfileMap = HashMap<String, String>()

            ProfileMap.put("ParentName", Name)
            ProfileMap.put("ParentAge", Age)
            ProfileMap.put("ParentSex", Sex)
            ProfileMap.put("ParentEmail", Email)
            ProfileMap.put("ParentPhone", Phone)
            ProfileMap.put("ParentAddress", Address)
            ProfileMap.put("ParentInterests", Interests)

            RootRef.child(firebaseAuth.currentUser!!.uid)?.setValue(ProfileMap).addOnCompleteListener(object :
                OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful()) {
                         var intent: Intent = Intent(this@ParentDetails, ParentActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                 Toast.makeText(applicationContext, "Profile Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        var error = task.exception?.message
                        Toast.makeText(applicationContext, "Error ->" + error, Toast.LENGTH_SHORT).show()
                    }
                }

            })

        }

    }
}