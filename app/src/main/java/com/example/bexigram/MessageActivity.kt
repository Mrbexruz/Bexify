package com.example.bexigram

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bexigram.adapters.MessageAdapter
import com.example.bexigram.databinding.ActivityMessageBinding
import com.example.bexigram.models.Message
import com.example.bexigram.models.User
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*



class MessageActivity : AppCompatActivity() {

    lateinit var currentUser: String
    lateinit var user: User
    lateinit var messageAdapter: MessageAdapter
    lateinit var list: ArrayList<Message>
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var statusIndicator: View // Status indicator for user

    private val binding by lazy { ActivityMessageBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Get the current user and target user details
        currentUser = intent.getStringExtra("uid").toString()
        user = intent.getSerializableExtra("user") as User
        list = ArrayList()

        // Initialize the messageAdapter
        messageAdapter = MessageAdapter(object : MessageAdapter.RvAction {
            override fun imageClick(message: Message) {
                // Handle image click if necessary
            }

            override fun onEdit(message: Message) {
                // AlertDialog yaratish uchun
                val alertDialog = AlertDialog.Builder(this@MessageActivity)
                val dialogView = layoutInflater.inflate(R.layout.edit_dialog, null)
                alertDialog.setView(dialogView)

                // Dialogni yaratish
                val dialog = alertDialog.create()
                dialog.show()

                // EditText ni sozlash va eski matnni ko'rsatish
                val edt = dialogView.findViewById<EditText>(R.id.menu_edit)
                edt.setText(message.text)

                // Save tugmasini ishlashga sozlash
                dialogView.findViewById<View>(R.id.btn_save).setOnClickListener {
                    val newText = edt.text.toString()
                    if (newText.isNotBlank()) {
                        // Firebase'da xabarni yangilash
                        message.text = newText
                        reference.child(currentUser).child("messages").child(user.uid!!).child(message.id!!)
                            .setValue(message)
                        reference.child(user.uid!!).child("messages").child(currentUser).child(message.id!!)
                            .setValue(message)
                        dialog.dismiss() // Dialogni yopish
                    } else {
                        Toast.makeText(this@MessageActivity, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                }

                // Cancel tugmasini ishlashga sozlash
                dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
                    dialog.dismiss() // Dialogni yopish
                }
            }



            override fun onDelete(message: Message) {
                reference.child(currentUser).child("messages").child(user.uid!!).child(message.id!!).removeValue()
                reference.child(user.uid!!).child("messages").child(currentUser).child(message.id!!).removeValue()
            }


        }, list, currentUser)

        // Set up RecyclerView
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = messageAdapter
        messageAdapter.notifyDataSetChanged() // Refresh the RecyclerView after the change


        // Set the user details
        binding.name.text = user.name
        Picasso.get().load(user.photoUrl).into(binding.image)

        // Status indicator
        statusIndicator = findViewById(R.id.statusIndicator)

        // Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        // Check user status
        checkUserStatus()

        binding.apply {
            btnSend.setOnClickListener {
                val text = edtSend.text.toString()
                if (text.isNotBlank()) {
                    val message = Message(text, currentUser, user.uid, "sent")
                    message.date = getDate() // Set the date
                    val key = reference.child(user.uid!!).child("messages").push().key // Ensure key is generated

                    if (key != null) {
                        message.id = key // Assign the key to messageId
                        // Send the message to both users' messages
                        reference.child(user.uid!!).child("messages").child(currentUser).child(key).setValue(message)
                        reference.child(currentUser).child("messages").child(user.uid!!).child(key).setValue(message)

                        edtSend.text.clear()
                    } else {
                        Toast.makeText(this@MessageActivity, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        // Read and update messages
        reference.child(currentUser).child("messages").child(user.uid ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach { child ->
                        val message = child.getValue(Message::class.java)
                        if (message != null) {
                            list.add(message)
                        }
                    }
                    messageAdapter.notifyDataSetChanged() // Notify adapter after adding new messages
                    binding.rv.scrollToPosition(list.size - 1) // Scroll to the bottom
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MessageActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

    }

    // Check the user status (online/offline)
    private fun checkUserStatus() {
        user.uid?.let {
            reference.child(it).child("status").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java)
                    when (status) {
                        "online" -> statusIndicator.setBackgroundResource(R.drawable.green_circle)
                        "offline" -> statusIndicator.setBackgroundResource(R.drawable.gray_circle)
                        else -> statusIndicator.setBackgroundResource(R.drawable.gray_circle)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MessageActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Function to get the current date
    fun getDate(): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return simpleDateFormat.format(date)
    }

    // Mark user as online when resuming activity
    override fun onResume() {
        super.onResume()
        reference.child(currentUser).child("status").setValue("online")
    }

    // Mark user as offline when pausing activity
    override fun onPause() {
        super.onPause()
        reference.child(currentUser).child("status").setValue("offline")
    }
}
