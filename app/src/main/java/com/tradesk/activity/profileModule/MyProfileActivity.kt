package com.tradesk.activity.profileModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.socialgalaxyApp.util.extension.loadWallImage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.PhoneTextFormatter
import com.tradesk.Model.ProfileModel
import com.tradesk.R
import com.tradesk.databinding.ActivityMyProfileBinding
import com.tradesk.filemanager.checkStoragePermission
import com.tradesk.filemanager.requestStoragePermission
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.REQUEST_CODE_DOCS
import com.tradesk.util.Constants.insertString
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.Constants.openApp
import com.tradesk.util.FileFinder
import com.tradesk.util.file.FilePath
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MyProfileActivity : AppCompatActivity(), SingleItemCLickListener {
    var myImageUri = ""
    lateinit var camera_image_uri: Uri
    var image_type = ""
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    var licenseimage = ""
    var mFile: File? = null
    var exploreFile: File? = null
    val mListImages = mutableListOf<String>()
    var isBack = false;
//    val mUserDocumentsAdapter by lazy { UserDocumentsAdapter(this, this, mListImages,mListImages) }
    //to
    val mUserDocumentsAdapter by lazy { UserDocumentsAdapter2(this, this, mListImages) }
    val mBuilder: Dialog by lazy { Dialog(this@MyProfileActivity) }
    var uriList=ArrayList<Uri>()
    lateinit var binding:ActivityMyProfileBinding
    lateinit var viewModel:ProfileViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    private var hasPermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_my_profile)
        viewModel=ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.mRvDocs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.mRvDocs.adapter = mUserDocumentsAdapter
        binding.mIvAddressEdit.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }
        if (isInternetConnected(this)) {
            Constants.showLoading(this@MyProfileActivity)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getProfile()
            }
        }
        binding.mIvEdit.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
        binding.mIvEditCompanyLogo.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                image_type = "logo"
                showImagePop()
            }
        }
        binding.mIvLicenseImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                licenseimage = "1"
                showImagePop()
            }
        }
        binding.mIvAddDocImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                hasPermission = checkStoragePermission(this)
                if (hasPermission)
                {
                    licenseimage = "2"
                    showDocsPop()
                } else {
                    requestStoragePermission(this)
                }


            }
        }
        binding.mIvBack.setOnClickListener {
            updateData()
        }
        binding.mIvHomePhoneEdit.addTextChangedListener(PhoneTextFormatter(binding.mIvHomePhoneEdit, "(###)###-####"))

        initObserve()
    }

    fun initObserve() {
        viewModel.responseProfileModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    Log.e("Profile Data",it.data.toString())
                    setData(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseUpdateProfileModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    finish()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseEditProfileDoc.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message)
                    if (isInternetConnected(this)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProfile()
                        }
                    }
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
    }

    fun setData(it: ProfileModel) {
        if (it.data.image.isNotEmpty()) {
            binding.mIvPic.loadWallImage(it.data.image)
        }

        if (it.data.company_logo.isNotEmpty()) {
            binding.mIvLogo.loadWallImage(it.data.company_logo)
        }
        image_type = ""

        binding.mTvName.setText(it.data.name)
        binding.mTvEmail.setText(it.data.email)

        if (it.data.addtional_info.trade.isEmpty()) {
            binding.mTvType.setText("Contractor")
        } else {
            binding.mTvType.setText(it.data.addtional_info.trade)
        }

        if (it.data.phone_no.isEmpty()) {
            binding.mTvPhone.setText("N/A")
        }
        else {
            //new
            binding.mTvPhone.text = insertString(it.data.addtional_info.home_phone_no, "", 0)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), ")", 2)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), " ", 3)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), "-", 7)
            binding.mTvPhone.text = "+1 (" + binding.mTvPhone.text.toString()
        }

        if (it.data.license_and_ins.docs_url.isNotEmpty()) {
            mListImages.clear()
            mListImages.addAll(it.data.license_and_ins.docs_url)
            mUserDocumentsAdapter.notifyDataSetChanged()
        }

        if (it.data.license_and_ins.licence_image.isNotEmpty()) {
            binding.mIvLicenseImage.loadWallImage(it.data.image)
        }
        if (it.data.address.postal_code.isNotEmpty()) {
            lat = it.data.address.location.coordinates[0].toString()
            lng = it.data.address.location.coordinates[1].toString()
        } else {
            lat = "0.0"
            lng = "0.0"
        }

        if (!binding.mIvPhoneEdit.text.toString().trim().replace("(", "")
                .replace(")", "").replace("-", "").equals(it.data.phone_no)
        ) {
            if (it.data.phone_no.isEmpty()) {
                binding.mIvPhoneEdit.setHint("N/A")
                binding.mIvPhoneEdit.addTextChangedListener(
                    PhoneTextFormatter(
                        binding.mIvPhoneEdit,
                        "(###)###-####"
                    )
                )
            }
            else {
                var phone = Constants.insertString(it.data.phone_no, "", 0)
                phone = Constants.insertString(phone!!, ")", 2)
                phone = Constants.insertString(phone!!, " ", 3)
                phone = Constants.insertString(phone!!, "-", 7)
                phone = "(" + phone!!

                binding.mIvPhoneEdit.setText(phone)

                binding.mIvPhoneEdit.addTextChangedListener(
                    PhoneTextFormatter(
                        binding.mIvPhoneEdit,
                        "(###)###-####"
                    )
                )
            }
        }


        if (!binding.mIvHomePhoneEdit.text.toString().trim().replace("(", "")
                .replace(")", "").replace("-", "").equals(it.data.addtional_info.home_phone_no)
        )
        {
            if (it.data.addtional_info.home_phone_no.isEmpty()) {
                binding.mIvHomePhoneEdit.setHint("N/A")
                binding.mIvHomePhoneEdit.addTextChangedListener(PhoneTextFormatter(
                        binding.mIvHomePhoneEdit, "(###)###-####"
                    )
                )
            } else {
                binding.mIvHomePhoneEdit.setText(insertString(binding.mIvHomePhoneEdit.text.toString(), ")", 2))
                binding.mIvHomePhoneEdit.setText(insertString(binding.mIvHomePhoneEdit.text.toString(), " ", 3))
                binding.mIvHomePhoneEdit.setText(insertString(binding.mIvHomePhoneEdit.text.toString(), "-", 7))
                binding.mIvHomePhoneEdit.setText("(" + it.data.addtional_info.home_phone_no.trim())
                binding.mIvHomePhoneEdit.addTextChangedListener(
                    PhoneTextFormatter(
                        binding.mIvHomePhoneEdit,
                        "(###)###-####"
                    )
                )
            }
        }

        binding.mIvNameEdit.setText(it.data.company_name)

        binding.mIvSeccEmailEdit.setText(it.data.company_email)

        binding.mIvUNameEdit.setText(it.data.name)
        binding.mIvSecEmailEdit.setText(it.data.email)

        binding.mTvType.setText(it.data.addtional_info.trade)

        binding.mIvTradeEdit.setText(it.data.addtional_info.trade)
        binding.mIvTradeEditCom.setText(it.data.addtional_info.trade)

        binding.mIvAddressEdit.setText(it.data.address.street)
//        mIvAddressEdit.setText(it.data.address.street+" "+it.data.address.city+" "+it.data.address.postal_code)
        binding.mIvCityEdit.setText(it.data.address.city)
        binding.mIvStateEdit.setText(it.data.address.state)
        binding.mIvPostalEdit.setText(it.data.address.postal_code)

        binding.mIvFaxEdit.setText(it.data.addtional_info.fax)
        binding.mIvWebsiteEdit.setText(it.data.addtional_info.website_link)

        binding.mIvLicenseEdit.setText(it.data.license_and_ins.license)

        binding.mIvSocialWebEdit.setText(it.data.social_media.website)
        binding.mIvFbEdit.setText(it.data.social_media.facebook)
        binding.mIvInstaEdit.setText(it.data.social_media.instagram)
        binding.editGoogleBusiness.setText(it.data.social_media.googleBusiness)
        binding.editYelp.setText(it.data.social_media.yelp)
        binding.faxEdit.setText(it.data.addtional_info.fax);
    }

    private fun browseDocuments() {
//        startActivityForResult(Intent(this, FileExplorerActivity::class.java), REQUEST_CODE_DOCS)
        val mimeTypes = arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
            "text/plain",
            "application/pdf",
            "application/zip", "application/vnd.android.package-archive","application/png")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        if (intent.resolveActivity(this.packageManager) != null) {
//            startActivityForResult(intent, Constants.REQUEST_CODE_DOCS)
//        }
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            uriList.clear()
            // There are no request codes
            addDocumentList(result.data!!)
        }
    }

    fun addDocumentList(data: Intent) {
        if(data!=null)
        {
            uriList.clear()
            if (data.clipData!= null)
            {
                val count: Int = data.clipData!!.getItemCount()
                Toast.makeText(this,count.toString(), Toast.LENGTH_SHORT).show()
                //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (i in 0 until count) {
                    val documentUri: Uri = data.clipData!!.getItemAt(i).getUri()
                    uriList.add(documentUri)
                }
//                licenseimage="2"
                uploadFileToAPI(true)
            }
            else
            {
//                licenseimage="2"


//                if (result.resultCode == Activity.RESULT_OK) {
                    uriList.add(data?.data!!)
                    Log.e("Upload Document",uriList.toString())
                    uploadFileToAPI(true)
//                }
            }

        }
    }
    fun showImagePop() {
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleDoc).also {
            it.isVisible = false
            it.setOnClickListener {
                mBuilder.dismiss()
                browseDocuments()
            }
        }
        mBuilder.findViewById<View>(R.id.view11).isVisible = true
        mBuilder.findViewById<TextView>(R.id.titleCamera).also {
            it.isVisible = true
            it.setOnClickListener {
                mBuilder.dismiss()
                dispatchTakePictureIntent()
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleGallery).also {
            it.isVisible = true
            it.setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent()
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(this@MyProfileActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    Constants.createImageFile(this@MyProfileActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this@MyProfileActivity,
                        "${this@MyProfileActivity.packageName}.provider",
                        it
                    )
                    myImageUri = photoURI.toString()
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, Constants.REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun dispatchTakeGalleryIntent() {
        Toast.makeText(this,"1",Toast.LENGTH_SHORT).show()
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(intent, Constants.REQUEST_IMAGE_GET)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == REQUEST_CODE_DOCS)
            {
                addDocumentList(data!!)
//                val filePath = data?.getStringExtra("_data") ?: ""
//                if (filePath.isNotEmpty()) {
//                    mFile = File(filePath)
//                    uploadFileToAPI(true)
//                }
//                Log.e("data file", data?.getStringExtra("_data") ?: "")
            }/*
            if (requestCode === REQUEST_CODE_DOCS) {

                writePdf(data!!)

            }*/
            else if (requestCode == Constants.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            }
            else if (requestCode ==Constants.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri).setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(2, 1)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(this)
            }
            else if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    setPlaceData(place)
                } catch (e: java.lang.Exception) {
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                Toast.makeText(this,"Profile crop",Toast.LENGTH_SHORT).show()
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
    private fun saveCaptureImageResults(path: Uri) = try {
//        binding.mIvPic.loadWallImage(path)
        var path2= FilePath.getPath(this,path)
        mFile= File(path2)
        uploadFileToAPI(false)
    }
    catch (e: Exception) {
    }

    fun uploadFileToAPI(isDoc: Boolean) {

        if (licenseimage.equals("2"))
        {
            when {
                isInternetConnected(this) -> {
                    var parts1: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
                    for(data in uriList)
                    {
                        contentResolver.takePersistableUriPermission(data,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val path = FileFinder.getFilePath(applicationContext,data)
                        if (path != null && path.length > 3)
                        {
                            Log.e("Pdf path","PDF's path "+path)
                        }
                        else
                        {
                            Log.e("Pdf path","PDF's path is null")
                        }
                        val requestFile: RequestBody =RequestBody.create(MediaType.parse("multipart/form-data"),File(path))
                        var doc=
                            MultipartBody.Part.createFormData("doc", File(path).name, requestFile)
                        parts1.add(doc!!)
                    }
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateProfileDoc(parts1)
                    }
                }
                else -> {}
            }
        }
        else if (image_type.isNotEmpty()) {
            binding.mIvLogo.loadWallImage(mFile!!.absolutePath)
            when {
                isInternetConnected(this) -> {
                    hashMapOf<String, RequestBody>().also {
                        it.put("company_logo\"; filename=\"image.jpg", RequestBody.create(MediaType.parse("image/*"), mFile!!))
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.updateProfile(it)
                        }
                    }
                }
                else -> {}
            }
        }
        else {
            binding.mIvPic.loadWallImage(mFile!!.absolutePath)
            when {
                isInternetConnected(this) -> {
                    hashMapOf<String, RequestBody>().also {
                        it.put("image\"; filename=\"image.jpg",
                            RequestBody.create(MediaType.parse("image/*"), mFile!!)
                        )
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.updateProfile(it)
                        }
                    }
                }
                else -> {}
            }
        }

    }
    fun showDocsPop() {
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleDoc).also {
            it.isVisible = true
            it.setOnClickListener {
                mBuilder.dismiss()
                browseDocuments()
            }
        }
        mBuilder.findViewById<View>(R.id.view11).isVisible = true
        mBuilder.findViewById<TextView>(R.id.titleCamera).also {
            it.isVisible = false
            it.setOnClickListener {
                mBuilder.dismiss()
//                dispatchTakePictureIntent()
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleGallery).also {
            it.isVisible = false
            it.setOnClickListener {
                mBuilder.dismiss()
//                dispatchTakeGalleryIntent()
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }
    override fun onSingleItemClick(item: Any, position: Int) {
        openApp(this@MyProfileActivity, mListImages[position])
    }
    fun updateData() {
        isBack = true
        if (binding.mIvNameEdit.text.toString().isEmpty()) {
            toast("Please enter your company name")
        } else if (binding.mIvUNameEdit.text.toString().isEmpty()) {
            toast("Please enter your user name")
        } else if (binding.mIvAddressEdit.text.toString().trim().isEmpty()) {
            toast("Please enter address")
        } else {
            if (isInternetConnected(this))
                hashMapOf<String, RequestBody>().also {
                    it.put("name", RequestBody.create(MediaType.parse("multipart/form-data"),
                        binding.mIvUNameEdit.text.toString()))

                    if (binding.mIvNameEdit.text.toString().isNotEmpty()) {
                        it.put("company_name", RequestBody.create(MediaType.parse("multipart/form-data"),
                            binding.mIvNameEdit.text.toString()))
                    }

                    if (binding.mIvSeccEmailEdit.text.toString().isNotEmpty()) {
                        it.put(
                            "company_email",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                binding.mIvSeccEmailEdit.text.toString()
                            )
                        )
                    }
                    it.put(
                        "phone_no",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            if (binding.mIvPhoneEdit.text.toString()
                                    .isEmpty() || binding.mIvPhoneEdit.text.toString().trim()
                                    .equals("N/A")
                            ) {
                                ""
                            } else {
                                binding.mIvPhoneEdit.text.toString().replace(" ", "").replace("(", "")
                                    .replace(")", "").replace("-", "").trim()
                            }
                        )
                    )
                    it.put(
                        "type",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            "sales"
                        )
                    )

                    it.put(
                        "address",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.mIvAddressEdit.text.toString()
                        )
                    )

                    it.put(
                        "city",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.mIvCityEdit.text.toString()
                        )
                    )

                    it.put(
                        "state",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            state
                        )
                    )

                    it.put(
                        "postal_code",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            post_code
                        )
                    )

                    it.put(
                        "latLong",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            lat + "," + lng
                        )
                    )

                    it.put(
                        "trade",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.mIvTradeEditCom.text.toString().trim()
                        )
                    )
                    it.put(
                        "home_phone_no",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),

                            if (binding.mIvHomePhoneEdit.text.toString().trim()
                                    .isEmpty() || binding.mIvHomePhoneEdit.text.toString().trim()
                                    .equals("N/A")
                            ) {
                                ""
                            } else {
                                binding.mIvHomePhoneEdit.text.toString().trim().replace("(", "")
                                    .replace(")", "").replace("-", "").trim()
                            }

                        )
                    )

                    it.put(
                        "fax",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.faxEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "website_link",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.mIvWebsiteEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "license",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.mIvLicenseEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "website",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.mIvSocialWebEdit.text.toString().trim()
                        )
                    )
                    it.put(
                        "googleBusiness",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.editGoogleBusiness.text.toString().trim()
                        )
                    )
                    it.put(
                        "yelp",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.editYelp.text.toString().trim()
                        )
                    )

                    it.put(
                        "facebook",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.mIvFbEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "instagram",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            binding.mIvInstaEdit.text.toString().trim()
                        )
                    )

                    it.put(
                        "latLong",
                        RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            lat + "," + lng
                        )
                    )
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateProfile(it)
                    }
                }
        }
    }
    fun openPlaceDialog() {
        val fields = Arrays.asList(
            Place.Field.PHONE_NUMBER,
            Place.Field.BUSINESS_STATUS,
            Place.Field.ADDRESS,
            Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ID,
            Place.Field.LAT_LNG
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this@MyProfileActivity)
        startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }
    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                binding.mIvAddressEdit.setText("")
                binding.mIvCityEdit.setText("")
                binding.mIvStateEdit.setText("")
                binding.mIvPostalEdit.setText("")
                city = ""
                state = ""
                post_code = ""

                if (!placeData.getName()!!.toLowerCase().isEmpty()) {
                    binding.mIvAddressEdit.setText(placeData.getName())
                    binding.mIvAddressEdit.setSelection(binding.mIvAddressEdit.getText()!!.length)
                } else binding.mIvAddressEdit.setError("Not available")

                for (i in 0 until placeData.getAddressComponents()!!.asList().size) {
                    val place: AddressComponent =
                        placeData.getAddressComponents()!!.asList().get(i)

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("neighborhood") && place.types.contains("political")) {
                            city = place.name
                            binding.mIvCityEdit.setText(city)
                        }
                    }
                    if (city.trim().isEmpty()) {
                        if (place.types.contains("locality") && place.types.contains("political")) {
                            city = place.name
                            binding.mIvCityEdit.setText(city)
                        }
                    }
                    if (place.types.contains("administrative_area_level_1")) {
                        state = place.name
                        binding.mIvStateEdit.setText(state)
                    }
                    if (place.types.contains("postal_code")) {
                        post_code = place.name
                        binding.mIvPostalEdit.setText(post_code)
                    }
                    var address: Address = getAddressFromLocation(
                        placeData.latLng!!.latitude,
                        placeData.latLng!!.longitude
                    )
                    if (address.postalCode == null) {
                        address.postalCode = "unknown"
                    }
                    if (address.locality == null) {
                        address.locality = "unknown"
                    }
                    if (address.adminArea == null) {
                        address.adminArea = "unknown"
                    }
                    post_code = getZipCodeFromLocation(address);
                    binding.mIvPostalEdit.setText(post_code)
                    state = address.adminArea
                    city = address.locality
                }
                binding.mIvCityEdit.setText(city)

                lng = placeData.latLng!!.longitude.toString();
                lat = placeData.latLng!!.latitude.toString();
            }
        } catch (e: java.lang.Exception) {
            Log.e("Exce......", e.toString())
        }

    }
    private fun getZipCodeFromLocation(addr: Address): String {

        return if (addr.getPostalCode() == null) "" else addr.getPostalCode()
    }
    private fun getAddressFromLocation(lat: Double, long: Double): Address {
        val geocoder = Geocoder(this)
        var address = Address(Locale.getDefault())
        try {
            val addr: List<Address> = geocoder.getFromLocation(lat, long, 1)!!
            if (addr.size > 0) {
                address = addr[0]
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }
    override fun onBackPressed() {
        super.onBackPressed()
        updateData()
    }


}