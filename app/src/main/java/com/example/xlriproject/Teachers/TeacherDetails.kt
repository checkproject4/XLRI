package com.example.xlriproject.Teachers
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.xlriproject.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class TeacherDetails : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var RootRef : DatabaseReference
    lateinit var loadingBar : ProgressDialog
    lateinit var currentUserId : String
    lateinit var profilePicRef : StorageReference
    lateinit var name:EditText
    lateinit var age :EditText
    lateinit var sex:EditText
    lateinit var email:EditText
    lateinit var phone:EditText
    lateinit var company:EditText
    lateinit var address:EditText
    lateinit var teach:EditText
    lateinit var experience:EditText
    lateinit var fees:EditText
    lateinit var pic:CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_details)
        name = findViewById<EditText>(R.id.teacher_name)
        profilePicRef = FirebaseStorage.getInstance().reference.child("Profile Images")
        age = findViewById<EditText>(R.id.teacher_age)
        sex = findViewById<EditText>(R.id.teacher_sex)
        email = findViewById<EditText>(R.id.teacher_email)
        phone = findViewById<EditText>(R.id.teacher_phone)
        address = findViewById<EditText>(R.id.teacher_address)
        company = findViewById<EditText>(R.id.teacher_company_name)
        teach = findViewById<EditText>(R.id.teach)
        pic=findViewById(R.id.teacher_picture)
         experience = findViewById<EditText>(R.id.exp)
         fees = findViewById<EditText>(R.id.fees)
        loadingBar = ProgressDialog(this)
        profilePicRef = FirebaseStorage.getInstance().reference.child("Profile Images")
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserId = firebaseAuth.currentUser!!.uid
        var set_image: CircleImageView = findViewById(R.id.teacher_picture)
        RootRef = FirebaseDatabase.getInstance().getReference("Teacher")
        RootRef.child(firebaseAuth!!.currentUser!!.uid)?.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
          if(p0.hasChild("TeacherAddress"))
          {
              var intent: Intent = Intent(this@TeacherDetails , TeacherActivity::class.java)
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
              startActivity(intent)
              finish()
          }
                if(p0.hasChild("image"))
                {
                    Picasso.get().load(p0.child("image").value.toString()).placeholder(
                        R.drawable.profile_image).into(pic)
                }
            }

        })
        firebaseAuth = FirebaseAuth.getInstance()
        var profile_btn = findViewById<Button>(R.id.teacher_profile_btn)
        set_image.setOnClickListener {
UploadImageToStorage()
        }
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
        var Company = company.text.toString().trim()
        var Teach = teach.text.toString().trim()
        var Exp = experience.text.toString().trim()
        var Fees = fees.text.toString().trim()
        var ProfileMap  = HashMap<String , String>()
        ProfileMap.put("TeacherName" , Name)
        ProfileMap.put("TeacherAge" , Age)
        ProfileMap.put("TeacherSex" , Sex)
        ProfileMap.put("TeacherEmail" , Email)
        ProfileMap.put("TeacherPhone" , Phone)
        ProfileMap.put("TeacherAddress" , Address)
        ProfileMap.put("TeacherCompany" , Company)
        ProfileMap.put("TeacherTeach" , Teach)
        ProfileMap.put("TeacherExp" , Exp)
        ProfileMap.put("TeacherFees" , Fees)

        RootRef.child(firebaseAuth.currentUser!!.uid)?.setValue(ProfileMap)?.addOnCompleteListener(object :
            OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                if(task.isSuccessful()){
                 var intent: Intent = Intent(this@TeacherDetails , TeacherActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(applicationContext,"Profile Uploaded Successfully", Toast.LENGTH_SHORT).show()
                }
                else{
                    var error = task.exception?.message
                    Toast.makeText(applicationContext,"Error ->" + error , Toast.LENGTH_SHORT).show()
                }
            }

        })

    }
    private fun UploadImageToStorage(){
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            var result = CropImage . getActivityResult (data)
            if (resultCode == RESULT_OK){
                var imageUri = result.getUri()
                try{
                    loadingBar.setTitle("Uploading Profile Pic")
                    loadingBar.setMessage("Please wait while We are Uploading Your Pic")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    var filePath : StorageReference = profilePicRef.child(currentUserId+".jpg")
                    filePath?.putFile(imageUri!!)
                        ?.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                            override fun onSuccess(p0: UploadTask.TaskSnapshot) {
                                var result=p0.metadata!!.reference!!.downloadUrl
                                result.addOnSuccessListener(object :OnSuccessListener<Uri>{
                                    override fun onSuccess(p0: Uri) {
                                  var downloadUri=p0.toString()
                                        RootRef.child(firebaseAuth!!.currentUser!!.uid)
                                            ?.child("image").setValue(downloadUri)
                                            .addOnCompleteListener(object : OnCompleteListener<Void>{
                                                override fun onComplete(task: Task<Void>) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(applicationContext,"We are Uploading Your Image in Our Database" , Toast.LENGTH_SHORT).show()
                                                        loadingBar.dismiss()
                                                    }
                                                    else{
                                                        var error = task.exception?.message
                                                        Toast.makeText(applicationContext,"Error -> "+error , Toast.LENGTH_SHORT).show()
                                                        loadingBar.dismiss()
                                                    }
                                                }
                                            })
                                    }

                                })
                             }


                        })
                }
                catch(err: Exception){

                }
            }
        }
    }

}