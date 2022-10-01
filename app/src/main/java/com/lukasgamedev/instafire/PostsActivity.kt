package com.lukasgamedev.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.lukasgamedev.instafire.databinding.ActivityLoginBinding
import com.lukasgamedev.instafire.databinding.ActivityPostsBinding
import com.lukasgamedev.instafire.models.Post

private const val TAG = "PostActivity"

class PostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostsBinding

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Create the layout file that represent one post - DONE
        //Create data source - DONE
        posts = mutableListOf()
        //Create the adapter
        adapter = PostsAdapter(this, posts)
        //Bind the layout manager and the adapter to the RV
        binding.rvPosts.adapter = adapter
        binding.rvPosts.layoutManager = LinearLayoutManager(this)

        firestoreDb = FirebaseFirestore.getInstance()
        val postsReference = firestoreDb
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        postsReference.addSnapshotListener {
            snapshot, exception ->
            if (exception != null || snapshot == null){
                Log.e(TAG,"Exception when querying post", exception)
                return@addSnapshotListener
            }

            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()

            for (post in postList){
                Log.i(TAG,"Post ${post}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_profile){
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}