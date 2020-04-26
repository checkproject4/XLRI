package com.example.xlriproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StudentDetail : AppCompatActivity() {
    lateinit var RootRef : DatabaseReference
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var name:EditText
    lateinit var age :EditText
    lateinit var sex:Spinner
    lateinit var email:EditText
    lateinit var phone:EditText
    lateinit var address:EditText
    lateinit var interest:EditText
    lateinit var profile_btn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_detail)
         name = findViewById<EditText>(R.id.sudent_name)
        age = findViewById<EditText>(R.id.student_age)
        sex = findViewById<Spinner>(R.id.student_sex)
        email = findViewById<EditText>(R.id.student_email)
        phone = findViewById<EditText>(R.id.student_phone)
        address = findViewById<EditText>(R.id.student_address)
        interest = findViewById<EditText>(R.id.student_interests)
        profile_btn = findViewById<Button>(R.id.student_profile_btn)
        RootRef= FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()
        RootRef.child(firebaseAuth!!.currentUser!!.uid)?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("StudentAddress"))
                {
                    var intent: Intent = Intent(this@StudentDetail , StudentActivity::class.java)
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
        var Sex = sex.selectedItem.toString().trim()
        var Email = email.text.toString().trim()
        var Phone = phone.text.toString().trim()
        var Address = address.text.toString().trim()
        var Interest = interest.text.toString().trim()
        RootRef= FirebaseDatabase.getInstance().getReference("Student")
        firebaseAuth = FirebaseAuth.getInstance()
        var ProfileMap  = HashMap<String , String>()
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
        else if(TextUtils.isEmpty(Interest)){
            Toast.makeText(applicationContext,"Please Provide Your Interests" , Toast.LENGTH_SHORT).show()
        }
        else {
            ProfileMap.put("StudentName", Name)
            ProfileMap.put("StudentAge", Age)
            ProfileMap.put("StudentSex", Sex)
            ProfileMap.put("StudentEmail", Email)
            ProfileMap.put("StudentPhone", Phone)
            ProfileMap.put("StudentAddress", Address)
            ProfileMap.put("StudentInterests", Interest)

            RootRef.child(firebaseAuth.currentUser!!.uid)?.setValue(ProfileMap).addOnCompleteListener(object :
                OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful()) {
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