package com.lukasgamedev.instafire

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lukasgamedev.instafire.databinding.ActivityCreateBinding
import com.lukasgamedev.instafire.databinding.ActivityPostsBinding
import com.lukasgamedev.instafire.models.Post
import com.lukasgamedev.instafire.models.User

private const val TAG = "CreateActivity"
private const val PICK_PHOTO_CODE = 1234

class CreateActivity : AppCompatActivity() {

    private var signedInUser: User? = null
    private lateinit var binding: ActivityCreateBinding
    private var photoUri: Uri? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var storageReference: StorageReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        storageReference = FirebaseStorage.getInstance().reference
        firestoreDb = FirebaseFirestore.getInstance()

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener {userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG,"signed in user: $signedInUser")
            }
            .addOnFailureListener{ exception ->
                Log.i(TAG,"Failure fetching signed in user",exception)
            }

        binding.btnPickImage.setOnClickListener{
            Log.i(TAG,"Open up image picker on device")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if(imagePickerIntent.resolveActivity(packageManager) != null){
                Log.i(TAG,"package manager is ready")
                startActivityForResult(imagePickerIntent,PICK_PHOTO_CODE)
            }
        }
        binding.btnSubmit.setOnClickListener {
            handleSubmitButtonClick()
        }
    }

    private fun handleSubmitButtonClick() {
        if(photoUri == null){
            Toast.makeText(this,"No photo selected",Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.etDescription.text.isBlank()){
            Toast.makeText(this,"Description cannot be empty",Toast.LENGTH_SHORT).show()
            return
        }
        if(signedInUser == null){
            Toast.makeText(this,"No signed user, please wait",Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmit.isEnabled = false
        val photoUploadUri = photoUri as Uri
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        //upload the photo to firebase storeage
        photoReference.putFile(photoUploadUri)
            .continueWithTask{photoUploadTask ->
                Log.i(TAG,"Uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                //retrieve the url of the uploaded photo
                photoReference.downloadUrl
            }
            .continueWithTask { downloadUrlTask->
                //create a post object with the image Url and add that to the posts collection
                val post = Post(
                    binding.etDescription.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser)
                firestoreDb.collection("posts").add(post)
            }
            .addOnCompleteListener { postCreationTask ->
                binding.btnSubmit.isEnabled = true
                if(!postCreationTask.isSuccessful){
                    Log.e(TAG,"Exception during Post create operation", postCreationTask.exception)
                    Toast.makeText(this,"Failed to save post",Toast.LENGTH_SHORT).show()

                }
                binding.etDescription.text.clear()
                binding.imageView.setImageResource(0)
                Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show()
                val profileIntent = Intent(this,PostsActivity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME,signedInUser?.username)
                startActivity(profileIntent)
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_PHOTO_CODE){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                Log.i(TAG,"photoUri $photoUri")
                binding.imageView.setImageURI(photoUri)
            }else{
                Toast.makeText(this,"Image picker action cancelled",Toast.LENGTH_SHORT).show()
            }
        }
    }
}