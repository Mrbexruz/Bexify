package com.example.bexigram

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bexigram.adapters.UserAdapter
import com.example.bexigram.databinding.ActivityUsersBinding
import com.example.bexigram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UsersActivity : AppCompatActivity(), UserAdapter.RvAction {

    private val binding by lazy { ActivityUsersBinding.inflate(layoutInflater) }
    lateinit var list: ArrayList<User>
    lateinit var auth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // Firebase initialization
        auth = FirebaseAuth.getInstance()
        Picasso.get().load(auth.currentUser?.photoUrl).into(binding.imageProfile)
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        // RecyclerView setup
        val myLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rv.layoutManager = myLayoutManager

        // Click on Floating Action Button to go to SearchActivity
        binding.fab.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Get users from Firebase
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()

                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    // Filter out the current user
                    if (user?.uid != auth.uid) {
                        list.add(user!!)
                    }
                }

                // Set adapter
                binding.rv.adapter = UserAdapter(this@UsersActivity, list)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    override fun itemClick(user: User) {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("uid", auth.uid)
        startActivity(intent)
    }
}
