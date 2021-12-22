package com.example.retailpulseassignment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.work.*
import com.example.retailpulseassignment.model.Store
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class StoreImageUploadActivity : AppCompatActivity() {

    lateinit var store: Store
    val databaseURL = "gs://retail-pulse-assignment-532d0.appspot.com"

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    lateinit var btn_choose_image: Button
    lateinit var btn_upload_image: Button
    lateinit var img:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_image_upload)

        store =intent.getSerializableExtra("store") as Store

        firebaseStore = FirebaseStorage.getInstance(databaseURL)
        storageReference = FirebaseStorage.getInstance(databaseURL).reference


        btn_choose_image = findViewById(R.id.btnChoose)
        btn_upload_image= findViewById(R.id.btnUpload)
        img = findViewById(R.id.imgView)



        btn_choose_image.setOnClickListener { launchGallery() }
        /*btn_upload_image.setOnClickListener { uploadImage() }*/


        btn_upload_image.setOnClickListener {
            startUploading()
        }


    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                img.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun startUploading(){
        if(filePath != null){
            val uploadWorkerRequest: OneTimeWorkRequest.Builder = OneTimeWorkRequest.Builder(UploadWorker::class.java)

            val data = Data.Builder()
            data.putString("filePath", filePath.toString())
            data.putString("storeId", store.storeId)

            uploadWorkerRequest.setInputData(data.build())

            WorkManager.getInstance(this@StoreImageUploadActivity).enqueue(uploadWorkerRequest.build())

        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

}