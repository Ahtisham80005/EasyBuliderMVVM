package com.tradesk.activity.proposalModule

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.github.gcacace.signaturepad.views.SignaturePad
import com.tradesk.R
import com.tradesk.databinding.ActivitySignatureBinding
import com.tradesk.databinding.ActivitySignupBinding
import com.tradesk.util.FilePath
import okhttp3.MediaType
import java.io.File
import java.io.IOException
import java.io.OutputStream

class SignatureActivity : AppCompatActivity() {
//    lateinit var  signature_pad: SignaturePad
//    lateinit var  mClearButton: Button
//    lateinit var  mSaveButton: Button
    lateinit var binding:ActivitySignatureBinding

    var selectedimage_client=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_signature)
        verifyStoragePermissions(this)
//        signature_pad  =findViewById(R.id.signature_pad) as SignaturePad

//        mClearButton = findViewById<View>(R.id.clear_button) as Button
//        mSaveButton = findViewById<View>(R.id.save_button) as Button

        binding.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
                //Event triggered when the pad is signed
                binding.saveButton.setEnabled(true)
                binding.clearButton.setEnabled(true)
            }

            override fun onClear() {
                //Event triggered when the pad is cleared
                binding.saveButton.setEnabled(false)
                binding.clearButton.setEnabled(false)
            }
        })

        binding.mIvBack.setOnClickListener { finish() }

        binding.clearButton.setOnClickListener { binding.signaturePad.clear() }

        binding.saveButton.setOnClickListener {
            val signatureBitmap: Bitmap = binding.signaturePad.getSignatureBitmap()

            if (binding.signaturePad.getSignatureBitmap() != null) {
                selectedimage_client = FilePath.getPath(
                    this@SignatureActivity,
                    saveBitmap(
                        this,
                        binding.signaturePad.getSignatureBitmap(),
                        System.currentTimeMillis().toString(),
                        "Signatures"
                    )!!
                ).toString()

                val MEDIA_TYPE =
                    if (selectedimage_client.endsWith("png")) MediaType.parse("image/png") else MediaType.parse(
                        "image/jpeg"
                    )
                AddProposalActivity.mFileSignature = File(selectedimage_client)

                val returnIntent = Intent()
                returnIntent.putExtra("result", selectedimage_client)
                returnIntent.putExtra("mySignatureUri", selectedimage_client)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        /**
         * Checks if the app has permission to write to device storage
         *
         *
         * If the app does not has permission then the user will be prompted to grant permissions
         *
         * @param activity the activity from which permissions are checked
         */
        fun verifyStoragePermissions(activity: Activity?) {
            // Check if we have write permission
            val permission = ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }

    @NonNull
    @Throws(IOException::class)
    private fun saveBitmap(
        @NonNull context: Context, @NonNull bitmap: Bitmap,
        @NonNull displayName: String,subFolder: String
    ): Uri? {
        var relativeLocation = Environment.DIRECTORY_PICTURES
        if (!TextUtils.isEmpty(subFolder)) {
            relativeLocation += File.separator.toString() + subFolder
        }
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        val resolver = context.contentResolver
        var stream: OutputStream? = null
        var uri: Uri? = null
        return try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            stream = resolver.openOutputStream(uri)
            if (stream == null) {
                throw IOException("Failed to get output stream.")
            }
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream) == false) {
                throw IOException("Failed to save bitmap.")
            }
            uri
        } catch (e: IOException) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null)
            }
            throw e
        } finally {
            stream?.close()
        }
    }
}