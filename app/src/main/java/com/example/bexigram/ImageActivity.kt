package com.example.bexigram

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bexigram.models.Message
import com.squareup.picasso.Picasso

class ImageActivity : AppCompatActivity() {
    lateinit var back: ImageView
    lateinit var more: ImageView
    lateinit var rasm: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        back = findViewById(R.id.back)
        more = findViewById(R.id.more)
        rasm = findViewById(R.id.rasm)

        back.setOnClickListener {
            finish()
        }

        val message = intent.getSerializableExtra("key") as? Message
        if (message == null) {
            Toast.makeText(this, "Message data is missing.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val imageUri = message.imageUri
        if (!imageUri.isNullOrEmpty()) {
            Log.d("ImageActivity", "Loading image URI: $imageUri")
            Picasso.get().load(imageUri).into(rasm)
        } else {
            Toast.makeText(this, "Image URL is missing or invalid.", Toast.LENGTH_SHORT).show()
        }
    }
}
