package com.tradesk.activity.jobModule.Expense

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Model.Expenses
import com.tradesk.R
import com.tradesk.activity.ImageActivity
import com.tradesk.databinding.ActivityAddExpenseBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseActivity : AppCompatActivity() {
    var myImageUri = ""
    var mFile: File? = null
    var isViewMode=false
    lateinit var expenses: Expenses
    lateinit var binding:ActivityAddExpenseBinding
    lateinit var viewModel: JobsViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_expense)
        viewModel= ViewModelProvider(this).get(JobsViewModel::class.java)

        if (intent.hasExtra("expenses")) {
            findViewById<TextView>(R.id.textView6).setText("View Expense")
            isViewMode = true
            val builder = GsonBuilder()
            val gson = builder.create()
            var projectJsonString = intent.getStringExtra("expenses");
            expenses = gson.fromJson(projectJsonString, Expenses::class.java)
            binding.mEtExpenseTitle.setText(expenses.title)
            binding.mEtExpenseTitle.isEnabled = false
            binding.mEtAmount.setText(expenses.amount)
            binding.mEtAmount.isEnabled = false
            binding.mIvProof.loadWallImage(expenses.image)
            binding.mIvProofEdit.visibility = View.GONE
            binding.mBtnAdd.visibility = View.GONE
            binding.mIvProof.setOnClickListener {
                startActivity(
                    Intent(this, ImageActivity::class.java)
                        .putExtra("expense", "Expenses")
                        .putExtra("image", expenses.image)
                )
            }
        }
        else if (intent.hasExtra("expense_id")) {
            findViewById<TextView>(R.id.textView6).setText("Update Expense")
            binding.mBtnAdd.text = "Update"
            binding.mEtExpenseTitle.setText(intent.getStringExtra("expense_title"))
            binding.mEtAmount.setText(intent.getStringExtra("expense_amount"))
            binding.mIvProof.loadWallImage(intent.getStringExtra("expense_image").toString())
        }
        binding.mEtAmount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                binding.mEtAmount.removeTextChangedListener(this)

                try {
                    var originalString = s.toString()
                    val longval: Long
                    if (originalString.contains(",")) {
                        originalString = originalString.replace(",".toRegex(), "")
                    }
                    longval = originalString.toLong()
                    val formatter: DecimalFormat =
                        NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString: String = formatter.format(longval)

                    //setting text after format to EditText
                    binding.mEtAmount.setText(formattedString)
                    binding.mEtAmount.setSelection(binding.mEtAmount.getText()!!.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }
                binding.mEtAmount.addTextChangedListener(this)
            }
        })

        binding.mIvBack.setOnClickListener {
            finish()
        }
        binding.mIvProofEdit.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
//                showImagePop()
                dispatchTakeGalleryIntent()
            }
        }
        binding.mBtnAdd.setOnClickListener {
            if (intent.hasExtra("expense_id")) {
                if (binding.mEtExpenseTitle.text.isEmpty()) {
                    toast("Please enter expense title")
                } else if (binding.mEtAmount.text.isEmpty()) {
                    toast("Please enter expense amount")
                }  else {
                    if (isInternetConnected(this))
                        hashMapOf<String, RequestBody>().also {
                            it.put(
                                "title",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtExpenseTitle.text.toString()
                                )
                            )

                            it.put(
                                "amount",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtAmount.text.toString().replace(",", "")
                                )
                            )
                            if(mFile!=null)
                            {
                                it.put(
                                    "image\"; filename=\"image.jpg",
                                    RequestBody.create(MediaType.parse("image/*"), mFile!!)
                                )
                            }

                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.updateExpense(
                                    intent.getStringExtra("expense_id").toString(), it
                                )
                            }

                        }
                }
            } else {

                if (binding.mEtExpenseTitle.text.isEmpty()) {
                    toast("Please enter expense title")
                } else if (binding.mEtAmount.text.isEmpty()) {
                    toast("Please enter expense amount")
                } else if (mFile == null) {
                    toast("Please add expense receipt as proof by taking picture")
                } else {
                    if (isInternetConnected(this))
                        hashMapOf<String, RequestBody>().also {
                            it.put(
                                "job_id",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    intent.getStringExtra("job_id").toString()
                                )
                            )

                            it.put(
                                "title",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtExpenseTitle.text.toString()
                                )
                            )

                            it.put(
                                "amount",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtAmount.text.toString().replace(",", "")
                                )
                            )

                            it.put(
                                "image\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mFile!!)
                            )
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.addexpense(it)
                            }

                        }
                }
            }
        }
        initObserve()
    }
    fun initObserve() {
        viewModel.responseSuccessModel.observe(this, androidx.lifecycle.Observer { it ->
                Constants.hideLoading()
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Updated successfully")
                        finish()
                    }
                    is NetworkResult.Error -> {
                        toast(it.message)
                    }
                    is NetworkResult.Loading -> {
                        Constants.showLoading(this)
                    }
                }
            })
        viewModel.responseAddExpenseModel.observe(this, androidx.lifecycle.Observer { it ->
                Constants.hideLoading()
                when (it) {
                    is NetworkResult.Success -> {
                        toast(it.data!!.message)
                        finish()
                    }
                    is NetworkResult.Error -> {
                        toast(it.message)
                    }
                    is NetworkResult.Loading -> {
                        Constants.showLoading(this)
                    }
                }
            })
    }
    private fun dispatchTakeGalleryIntent() {

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(intent, Constants.REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1010)
        {
            data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let {
                if (it.isNotEmpty()) saveCaptureImageResults(it[0])
            }
            Log.e("RESULT","RESULT")
//                saveCaptureImageResults()
        }
        else if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == Constants.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri).setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                Toast.makeText(this,"Profile crop", Toast.LENGTH_SHORT).show()
                Log.e("Crop","Crop Image")
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    private fun saveCaptureImageResults(data: Uri) = try {
        val file = File(data.path!!)
        mFile = Compressor(this@AddExpenseActivity)
            .setMaxHeight(1000).setMaxWidth(1000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        if (intent.getStringExtra("type").equals("soldier")) {
            binding.mIvProof.loadWallImage(mFile!!.absolutePath)
        } else {
            binding.mIvProof.loadWallImage(mFile!!.absolutePath)
        }
    } catch (e: Exception) {
    }

    private fun saveCaptureImageResults(path: String) = try {
        val file = File(path!!)
        mFile = Compressor(this@AddExpenseActivity)
            .setMaxHeight(4000).setMaxWidth(4000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        if (intent.getStringExtra("type").equals("soldier")) {
            binding.mIvProof.loadWallImage(mFile!!.absolutePath)
        } else {
            binding.mIvProof.loadWallImage(mFile!!.absolutePath)
        }

    } catch (e: Exception) {
    }
}