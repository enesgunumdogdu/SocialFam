package com.enesgunumdogdu.kotlininstagram.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.enesgunumdogdu.kotlininstagram.R
import com.enesgunumdogdu.kotlininstagram.adapter.FeedRecyclerAdapter
import com.enesgunumdogdu.kotlininstagram.databinding.ActivityFeedBinding
import com.enesgunumdogdu.kotlininstagram.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var feedAdapter : FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore
        postArrayList = ArrayList<Post>()

        getData()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter


        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    true
                }
                R.id.add_post -> {
                    val intent = Intent(this, UploadActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.signout -> {
                    auth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.profile ->{
                    val intent = Intent(this,ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


    }

    private fun getData(){
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if(error != null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(value !=null){
                  if(!value.isEmpty){
                      val documents = value.documents
                      postArrayList.clear()

                      for(document in documents){
                          val comment = document.get("comment") as String
                          val userEmail = document.get("userEmail") as String
                          val downloadUrl = document.get("downloadUrl") as String
                          val post = Post(userEmail,comment,downloadUrl)
                          postArrayList.add(post)
                      }
                      feedAdapter.notifyDataSetChanged()
                  }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_post -> {
                val intent = Intent(this, UploadActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.signout -> {
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.profile ->{
                //profil intent
                val intent = Intent(this,ProfileActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}