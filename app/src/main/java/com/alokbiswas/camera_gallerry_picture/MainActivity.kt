package com.alokbiswas.camera_gallerry_picture

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var currentPath: String? = null
    val TAKE_PICTURE =1
    val SELECT_PICTURE =2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_gallery.setOnClickListener {
            dispatchGalleryIntent()
        }
        btn_camera.setOnClickListener {
            dispatchCameraIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK){
            try {
                val file = File(currentPath!!)
                val uri = Uri.fromFile(file)
                imageView.setImageURI(uri)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK){
            try {
                val uri = data!!.data
                imageView.setImageURI(uri)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
    fun dispatchGalleryIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select image"), SELECT_PICTURE)
    }

    fun dispatchCameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImage()

            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null){
                //You must create a content provider matching the authority
                var photoUrl = FileProvider.getUriForFile(this,
                    "com.alokbiswas.camera_gallerry_picture.fileprovider",photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUrl)
                startActivityForResult(intent,TAKE_PICTURE)
            }
        }
    }
    fun createImage(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "JPEG_" + timeStamp + "_"
        var storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image = File.createTempFile(imageName, ".jpg", storageDir)
        currentPath = image.absolutePath
        return image
    }
}

