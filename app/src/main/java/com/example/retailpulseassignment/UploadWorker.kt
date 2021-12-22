package com.example.retailpulseassignment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.retailpulseassignment.model.Store
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class UploadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    val databaseURL = "gs://retail-pulse-assignment-532d0.appspot.com"
    lateinit var filePath: Uri
    lateinit var storeId : String

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun doWork(): Result {

        firebaseStore = FirebaseStorage.getInstance(databaseURL)
        storageReference = FirebaseStorage.getInstance(databaseURL).reference

        filePath = Uri.parse(inputData.getString("filePath"))
        storeId = inputData.getString("storeId").toString()

        createNotification("Uploading", "Uploading in progress")
        uploadImages()

        return Result.success()
    }

    private fun uploadImages() {
        val ref = storageReference?.child( "uploads/" + UUID.randomUUID().toString())
        val uploadTask = ref?.putFile(filePath!!)

        val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                addUploadRecordToDb(downloadUri.toString())
            } else {
                // Handle failures
            }
        }?.addOnFailureListener{

        }
    }

    private fun addUploadRecordToDb(uri: String){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["storeId"] = storeId
        data["imageUrl"] = uri
        data["timespatmp"] = FieldValue.serverTimestamp()

        db.collection(storeId).add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(applicationContext, "Saved to DB", Toast.LENGTH_LONG).show()
                updateNotification("Successful", "Upload is finished")
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Error saving to DB", Toast.LENGTH_LONG).show()
                updateNotification("Failure", "Upload failed")
            }
    }

    fun createNotification(title: String, description: String) {

        var notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("101", "channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, "101")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_launcher_background)

        notificationManager.notify(1, notificationBuilder.build())

    }

    fun updateNotification(title: String, description: String){

        var notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("101", "channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, "101")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_launcher_background)

        notificationManager.notify(1, notificationBuilder.build())
    }

}