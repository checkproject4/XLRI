package com.example.xlriproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

class TeacherActivity : AppCompatActivity() {
lateinit var batch_activity_click:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
        batch_activity_click=findViewById(R.id.batch_activity_click)
        batch_activity_click.setOnClickListener {
            startActivity(Intent(this, Batches::class.java))
        }

    }
}
