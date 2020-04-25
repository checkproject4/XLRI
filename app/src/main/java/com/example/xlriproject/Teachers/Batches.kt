package com.example.xlriproject.Teachers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.xlriproject.R

class Batches : AppCompatActivity() {
lateinit var add_batch_btn:CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batches)
        add_batch_btn=findViewById(R.id.add_batch_btn)
    }
}
