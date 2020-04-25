package com.example.xlriproject

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    var login_btn: Button?=null
    var register_btn:Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        login_btn=findViewById(R.id.user_login_btn)
        login_btn!!.setOnClickListener {
            startActivity(Intent(applicationContext,login::class.java))
        }
        register_btn=findViewById(R.id.user_register_btn)
        register_btn!!.setOnClickListener {
            var dialog=AlertDialog.Builder(this)
            dialog.setTitle("Who are you?")
            var options= arrayOf("Teacher","Student","Parent")
            dialog.setItems(options,object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(which){
                        0->{
                            var intent= Intent(applicationContext,register::class.java)
                            intent.putExtra("category","Teacher")
                            startActivity(intent)

                        }
                        1->{
                            var intent= Intent(applicationContext,register::class.java)
                            intent.putExtra("category","Student")
                            startActivity(intent)
                        }
                        2->{
                            var intent= Intent(applicationContext,register::class.java)
                            intent.putExtra("category","Parent")
                            startActivity(intent)
                        }

                    }
                }

            })
            dialog.show()
        }
    }
}
