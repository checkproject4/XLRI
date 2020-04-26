package com.example.xlriproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddBatchActivity : AppCompatActivity() {
    lateinit var name: EditText
    lateinit var age : EditText
    lateinit var sex: EditText
    lateinit var email: EditText
    lateinit var phone: EditText
    lateinit var address: EditText
    lateinit var profile_btn:Button
    lateinit var RootRef:DatabaseReference
    lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_batch)
        name = findViewById<EditText>(R.id.batch_name)
        age = findViewById<EditText>(R.id.batch_location)
        sex = findViewById<EditText>(R.id.batch_teacher)
        email = findViewById<EditText>(R.id.batch_time)
        phone = findViewById<EditText>(R.id.batch_days)
        address = findViewById<EditText>(R.id.batch_max_slots)
        profile_btn = findViewById<Button>(R.id.save_batch_btn)
        firebaseAuth = FirebaseAuth.getInstance()
        RootRef= FirebaseDatabase.getInstance().getReference("Batches")?.child(firebaseAuth!!.currentUser!!.uid)
        profile_btn.setOnClickListener {
            CreateProfile()
        }

    }
    private fun CreateProfile(){
        var Name = name.text.toString().trim()
        var Location = age.text.toString().trim()
        var Teacher =sex.text.toString().trim()
        var Time = email.text.toString().trim()
        var Days = phone.text.toString().trim()
        var Slots = address.text.toString().trim()
        if(TextUtils.isEmpty(Name)){
            Toast.makeText(applicationContext,"Please Provide Batch Name" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Location)){
            Toast.makeText(applicationContext,"Please Provide Batch Location" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Teacher)){
            Toast.makeText(applicationContext,"Please Provide Batch Teacher" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Time)){
            Toast.makeText(applicationContext,"Please Provide Batch Time" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Days)){
            Toast.makeText(applicationContext,"Please Provide Batch Days" , Toast.LENGTH_SHORT).show()
        }
        else if(TextUtils.isEmpty(Slots)){
            Toast.makeText(applicationContext,"Please Provide Batch Slots" , Toast.LENGTH_SHORT).show()
        }
        else{
var ref=RootRef.push()
            ref.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
              var batch_id=ref.key.toString()
                    var ProfileMap  = HashMap<String , String>()
                    ProfileMap.put("name", Name)
                    ProfileMap.put("location", Location)
                    ProfileMap.put("StudentSex", Teacher)
                    ProfileMap.put("time", Time)
                    ProfileMap.put("days", Days)
                    ProfileMap.put("slots", Slots)
                    ProfileMap.put("Batch Id",batch_id)

                    RootRef.child(batch_id)?.setValue(ProfileMap).addOnCompleteListener(object :
                        OnCompleteListener<Void> {
                        override fun onComplete(task: Task<Void>) {
                            if (task.isSuccessful()) {
                                var intent: Intent = Intent(this@AddBatchActivity, Batches::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                                Toast.makeText(applicationContext, "Batch created Successfully", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                var error = task.exception?.message
                                Toast.makeText(applicationContext, "Error ->" + error, Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
                }

            })


        }
    }}