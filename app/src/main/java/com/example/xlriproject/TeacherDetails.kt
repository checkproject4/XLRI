package com.example.xlriproject
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_teacher_details.*
import java.lang.Exception

class TeacherDetails : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var RootRef : DatabaseReference
    lateinit var loadingBar : ProgressDialog
    lateinit var currentUserId : String
    lateinit var profilePicRef : StorageReference
    lateinit var DocumentRef : StorageReference
    lateinit var name:EditText
    lateinit var age :EditText
    lateinit var sex:Spinner
    lateinit var email:EditText
    lateinit var phone:EditText
    lateinit var company:EditText
    lateinit var address:EditText
    lateinit var teach:EditText
    lateinit var experience:EditText
    lateinit var fees:EditText
    lateinit var pic:CircleImageView
    lateinit var selected_document:Button
    lateinit var document_type_spinner:Spinner
    var document_link: Uri?=null
    var document_name:String?=null
    lateinit var document_type:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_details)
        name = findViewById<EditText>(R.id.teacher_name)
        profilePicRef = FirebaseStorage.getInstance().reference.child("Profile Images")
        DocumentRef = FirebaseStorage.getInstance().reference.child("Document Images")
        age = findViewById<EditText>(R.id.teacher_age)
        sex = findViewById<Spinner>(R.id.teacher_sex)
        email = findViewById<EditText>(R.id.teacher_email)
        phone = findViewById<EditText>(R.id.teacher_phone)
        address = findViewById<EditText>(R.id.teacher_address)
        company = findViewById<EditText>(R.id.teacher_company_name)
        teach = findViewById<EditText>(R.id.teach)
        selected_document=findViewById(R.id.upload_document_btn)
        document_type_spinner=findViewById(R.id.document_type_spinner)
        document_type=document_type_spinner!!.selectedItem.toString()
        selected_document!!.setOnClickListener {
            selectDocument()
        }
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
              if(p0.hasChild("Document"))
          {
              loadingBar!!.dismiss()
              var intent: Intent = Intent(this@TeacherDetails , TeacherActivity::class.java)
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

    private fun selectDocument() {
        var gallery= Intent()
        gallery.type="image/*"
        gallery.action= Intent.ACTION_GET_CONTENT
        startActivityForResult(gallery,111)
    }

    private fun CreateProfile(){
        var Name = name.text.toString().trim()
        var Age = age.text.toString().trim()
        var Sex = teacher_sex.selectedItem.toString().trim()
        var Email = email.text.toString().trim()
        var Phone = phone.text.toString().trim()
        var Address = address.text.toString().trim()
        var Company = company.text.toString().trim()
        var Teach = teach.text.toString().trim()
        var Exp = experience.text.toString().trim()
        var Fees = fees.text.toString().trim()
        if(TextUtils.isEmpty(Name)||TextUtils.isEmpty(Age)||TextUtils.isEmpty(Sex)
            ||TextUtils.isEmpty(Email)||
            TextUtils.isEmpty(Phone)||TextUtils.isEmpty(Address)||TextUtils.isEmpty(Address)||
                TextUtils.isEmpty(Company)||TextUtils.isEmpty(Teach)||
                TextUtils.isEmpty(Exp)||
                TextUtils.isEmpty(Fees))
            Toast.makeText(applicationContext,"Please fill all the fields",Toast.LENGTH_SHORT).show()
        else {
            var ProfileMap = HashMap<String, Any>()
            ProfileMap.put("TeacherName", Name)
            ProfileMap.put("TeacherAge", Age)
            ProfileMap.put("TeacherSex", Sex)
            ProfileMap.put("TeacherEmail", Email)
            ProfileMap.put("TeacherPhone", Phone)
            ProfileMap.put("TeacherAddress", Address)
            ProfileMap.put("TeacherCompany", Company)
            ProfileMap.put("TeacherTeach", Teach)
            ProfileMap.put("TeacherExp", Exp)
            ProfileMap.put("TeacherFees", Fees)
            if (document_name == null)
                Toast.makeText(applicationContext, "Select Document first", Toast.LENGTH_SHORT).show()
            else {
                loadingBar.setTitle("Creating Profile")
                loadingBar.setMessage("Please wait while We are creating your profile")
                loadingBar.setCanceledOnTouchOutside(false)
                loadingBar.show()
                RootRef.child(firebaseAuth.currentUser!!.uid)?.updateChildren(ProfileMap)?.addOnCompleteListener(object :
                    OnCompleteListener<Void> {
                    override fun onComplete(task: Task<Void>) {
                        if (task.isSuccessful()) {
                            var document_title = selected_document!!.text.toString()
                            var filepath: StorageReference = DocumentRef.child(currentUserId + document_title)
                            filepath?.putFile(document_link!!)
                                ?.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                                    override fun onSuccess(p0: UploadTask.TaskSnapshot) {
                                        var result = p0.metadata!!.reference!!.downloadUrl
                                        result.addOnSuccessListener(object : OnSuccessListener<Uri> {
                                            override fun onSuccess(p0: Uri) {
                                                var downloadUri = p0.toString()
                                                var document = HashMap<String, Any>()
                                                document.put("document type", document_type)
                                                document.put("document link", downloadUri)
                                                RootRef.child(firebaseAuth!!.currentUser!!.uid)
                                                    ?.child("Document")?.child("Document Description")
                                                    ?.updateChildren(document)
                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                        override fun onComplete(task: Task<Void>) {
                                                            if (task.isSuccessful()) {

                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "Profile Uploaded Successfully",
                                                                    Toast.LENGTH_SHORT
                                                                )

                                                            } else {
                                                                loadingBar!!.dismiss()
                                                                var error = task.exception?.message
                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "Error -> " + error,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                            }
                                                        }
                                                    })
                                            }

                                        })
                                    }


                                })


                        }
                    }
                })
            }
        }
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
        else
            if(requestCode==111&& resultCode== Activity.RESULT_OK&&data!=null){
                document_link=data.data
                var  cursor = getContentResolver().query(document_link!!, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    document_name = cursor!!.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    if(document_name!=null)
                        selected_document!!.text=document_name
                }


            }

}}