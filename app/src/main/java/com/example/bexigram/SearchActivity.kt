package com.example.bexigram

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bexigram.adapters.UserAdapter
import com.example.bexigram.databinding.ActivitySearchBinding
import com.example.bexigram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity(), UserAdapter.RvAction {
    private val binding by lazy {ActivitySearchBinding.inflate(layoutInflater)}
    private lateinit var reference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var list: ArrayList<User>
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.search.requestFocus()
        binding.search.setQuery("", false)
        binding.search.isIconified = false
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        val myLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rv.apply {
            layoutManager = myLayoutManager
            addItemDecoration(
                DividerItemDecoration(
                    this@SearchActivity,
                    myLayoutManager.orientation
                )
            )
        }
        binding.chiqish.setOnClickListener {
            finish()
        }

        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()
                val children = snapshot.children
                for (child in children) {
                    val user = child.getValue(User::class.java)
                    if (user?.uid != auth.uid) {
                        list.add(user!!)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity, "${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
      //          binding.searchRv.adapter = RvAdapter(this@SearchActivity, list)
                val l = ArrayList<User>()
                for (user in list) {
                    if (user.name?.lowercase()!!.contains(newText!!.lowercase()) && newText.isNotBlank()) {
                        l.add(user)
                    }
                }
                binding.rv.adapter = UserAdapter(this@SearchActivity, l)
                return true
            }
        })


    }

    override fun itemClick(user: User) {
        // MessageActivity ga o'tish uchun Intent yaratish
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("uid", auth.uid)
        startActivity(intent)
    }

    }
