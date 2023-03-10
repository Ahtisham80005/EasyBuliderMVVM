package com.tradesk.activity.salesPerson

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.BuildzerApp
import com.tradesk.Model.ClientSalesModelNew
import com.tradesk.Model.PhoneTextFormatter
import com.tradesk.R
import com.tradesk.databinding.ActivityAddSalesPersonBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.insertString
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.FilePath
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.SalesUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddSalesPersonActivity : AppCompatActivity() {
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    var selectedTrade = ""
    var designation = ""
    var myImageUri = ""
    var mFile: File? = null
    lateinit var camera_image_uri: Uri
    lateinit var dialog: Dialog
    val mBuilder: Dialog by lazy { Dialog(this@AddSalesPersonActivity) }
    lateinit var binding:ActivityAddSalesPersonBinding
    lateinit var viewModel: SalesUserViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_sales_person)
        viewModel= ViewModelProvider(this).get(SalesUserViewModel::class.java)

        if(intent.hasExtra("title")) {
            if(intent.getStringExtra("title").equals("Add Sale person"))
            {
                binding.mBtnSubmit.text = "Send Request"
            }
            else if(intent.getStringExtra("title").equals("Edit Users"))
            {
                binding.textView6.text="Edit Profile"
            }
        }
        if(intent.hasExtra("addUser")){
            binding.textView6.text="Add User"
        }
        else if(intent.hasExtra("screen_title")) {
            val title = intent.getStringExtra("screen_title")
            binding.textView6.text = title
        }
        binding.mIvBack.setOnClickListener {
            finish()
        }
//        mEtTrade.setOnClickListener { showTradeMenu(mEtTrade, 1) }
//        imageView9.setOnClickListener { showTradeMenu(mEtTrade, 1) }

        binding.mIvPic.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
        binding.mEtAddress.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }
        if (intent.hasExtra("id")) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getUserDetails(intent.getStringExtra("id").toString(), "1", "10", intent.getStringExtra("type").toString(), ""
                )
            }
        } else {
            binding.mEtNumber.addTextChangedListener(PhoneTextFormatter(binding.mEtNumber, "(###) ###-####"))
        }
        binding.mBtnSubmit.setOnClickListener {
            if (intent.hasExtra("id"))
            {
                if (binding.mEtName.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_name), false)
                } else if (binding.mEtEmail.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_email_address), false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.mEtEmail.text.toString().trim()).matches())
                {
                    toast(getString(R.string.enter_valid_address), false)
                }
                else if (binding.mEtNumber.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_phone), false)
                } else if (binding.mEtNumber.text.toString().trim().length < 14) {
                    toast(getString(R.string.enter_valid_phone), false)
                }
                else if (binding.mEtTrade.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.select_trade), false)
                } else {

                    Log.e("ahtisham","ahtisham ali")
                    designation=binding.mEtDesignation.text.toString()
//                    Toast.makeText(this,designation,Toast.LENGTH_SHORT).show()

                    if (isInternetConnected(this))
                        hashMapOf<String, RequestBody>().also {
                            it.put("id", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getStringExtra("id")))

                            it.put(
                                "name",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtName.text.toString().trim()
                                )
                            )

                            it.put(
                                "email",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtEmail.text.toString().trim()
                                )
                            )

                            it.put(
                                "phone_no",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtNumber.text.toString().trim().replace(" ", "")
                                        .replace("(", "").replace(")", "").replace("-", "")
                                )
                            )

                            it.put(
                                "type",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"), intent.getStringExtra("type")
                                )
                            )

//                            it.put(
//                                "address",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    mEtAddress.text.toString()
//                                )
//                            )
//
//                            it.put(
//                                "city",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    city
//                                )
//                            )
//
//                            it.put(
//                                "state",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    state
//                                )
//                            )
//                            it.put(
//                                "zipcode",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    post_code
//                                )
//                            )
//
//                            it.put(
//                                "latLong",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    lat + "," + lng
//                                )
//                            )

                            it.put(
                                "trade",
                                RequestBody.create(MediaType.parse("multipart/form-data"),binding.mEtTrade.text.toString().trim())
                            )
//                            it.put(
//                                "designation",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    designation
//                                )
//                            )

                            if (mFile != null) {
                                it.put(
                                    "image\"; filename=\"image.jpg",
                                    RequestBody.create(MediaType.parse("image/*"), mFile!!)
                                )
                            }
                            Constants.showLoading(this@AddSalesPersonActivity)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.updatesaleclient(it)
                            }
                        }
                }
            }
            else {
//                if (mFile == null)
//                {
//                    toast(getString(R.string.pleaseuploadpic), false)
//                }
                if (binding.mEtName.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_name), false)
                } else if (binding.mEtEmail.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_email_address), false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.mEtEmail.text.toString().trim())
                        .matches()
                ) {
                    toast(getString(R.string.enter_valid_address), false)
                } else if (binding.mEtNumber.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_phone), false)
                } else if (binding.mEtNumber.text.toString().trim().length < 14) {
                    toast(getString(R.string.enter_valid_phone), false)
                }
                else if (binding.mEtTrade.getText().toString().trim().isEmpty()) {
                    toast(getString(R.string.select_trade), false)
                } else {
                    Log.e("ahtisham","ahtisham")
                    designation=binding.mEtDesignation.text.toString()
                    selectedTrade=binding.mEtTrade.text.toString().trim()
                    if (isInternetConnected(this))
                        hashMapOf<String, RequestBody>().also {
                            it.put(
                                "name",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtName.text.toString()
                                )
                            )

                            it.put(
                                "email",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtEmail.text.toString()
                                )
                            )
                            it.put(
                                "phone_no",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtNumber.text.toString().trim().replace(" ", "")
                                        .replace("(", "").replace(")", "").replace("-", "")
                                )
                            )
                            it.put(
                                "type",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    "sales"
                                )
                            )
//                            it.put(
//                                "address",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    mEtAddress.text.toString()
//                                )
//                            )
//                            it.put(
//                                "city",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    city
//                                )
//                            )
//                            it.put(
//                                "state",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    state
//                                )
//                            )
//                            it.put(
//                                "zipcode",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    post_code
//                                )
//                            )

//                            it.put(
//                                "latLong",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    lat + "," + lng
//                                )
//                            )

                            it.put(
                                "trade",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    selectedTrade
                                )
                            )
//                            it.put(
//                                "designation",
//                                RequestBody.create(
//                                    MediaType.parse("multipart/form-data"),
//                                    designation
//                                )
//                            )
                            if (mFile != null) {
                                it.put("image\"; filename=\"image.jpg", RequestBody.create(MediaType.parse("image/*"), mFile!!)
                                )
                            }
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                               viewModel.add_sales(it)
                            }
                        }
                }
            }
        }
        initObserver()
    }
    fun initObserver() {
        viewModel.responseUserDetail.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    setUserDetails(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Sales person added successfully")
                    finish()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this@AddSalesPersonActivity)
                }
            }
        })
        viewModel.responseClientUpdateSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Updated successfully")
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
    }

    fun setUserDetails(it: ClientSalesModelNew) {
        if (it.data.client.image.isNotEmpty()) {
            binding.mIvPic.loadWallImage(it.data.client.image)
        }

        binding.mEtName.setText(it.data.client.name)
        binding.mEtEmail.setText(it.data.client.email)

        binding.mTvNumber.text = insertString(it.data.client.phone_no, "", 0)
        binding.mTvNumber.text = insertString(binding.mTvNumber.text.toString(), ")", 2)
        binding.mTvNumber.text = insertString(binding.mTvNumber.text.toString(), " ", 3)
        binding.mTvNumber.text = insertString(binding.mTvNumber.text.toString(), "-", 7)
        binding.mTvNumber.text = "(" + binding.mTvNumber.text.toString()
        binding.mEtNumber.setText(binding.mTvNumber.text.toString())
        binding.mEtAddress.setText(it.data.client.address.street)
        binding.mEtTrade.setText(it.data.client.trade)
        lat = it.data.client.address.location.coordinates[0].toString()
        lng = it.data.client.address.location.coordinates[1].toString()
        city = it.data.client.address.city
        state = it.data.client.address.state
        post_code = it.data.client.address.zipcode
        selectedTrade = it.data.client.trade


        binding.mEtNumber.addTextChangedListener(PhoneTextFormatter(binding.mEtNumber, "(###) ###-####"))
    }

    fun showImagePop() {

        mBuilder.setContentView(R.layout.camera_dialog)
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleCamera).also {
            it.isVisible = !intent.hasExtra("permits")
            //     it.isVisible = false
            it.setOnClickListener {
                mBuilder.dismiss()
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
                camera_image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camera_image_uri)
                resultLauncherCamera.launch(cameraIntent)
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleGallery).also {
            it.isVisible = !intent.hasExtra("permits")
            it.setOnClickListener {
                mBuilder.dismiss()
                val intent = Intent()
                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                resultLauncher.launch(intent)

            }
        }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    var resultLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            var uriList: ArrayList<Uri> = ArrayList<Uri>()
            uriList.clear()
            uriList.add(camera_image_uri)
//            pathFromUriList=uriList
            saveCaptureImageResults(camera_image_uri)
            if(dialog!=null)
            {
                try{
                    val ivBackground: ImageView = dialog.findViewById<ImageView>(R.id.ivBackground)
                    ivBackground.loadWallImage(camera_image_uri)
                }
                catch(e:Exception)
                {
                }
            }
        }
        else
        {
            Toast.makeText(applicationContext,"No Select", Toast.LENGTH_SHORT).show()
        }
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            var uriList: ArrayList<Uri> = ArrayList<Uri>()
            val data: Intent? = result.data!!
            if(data!=null)
            {
//                    uriList.add(result.data?.data!!)
                saveCaptureImageResults(result.data?.data!!)
//                    pathFromUriList=uriList
//                    setDialogBgImageOnResult()
//                }
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
            .build(this@AddSalesPersonActivity)
        startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1010) {
//            data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let {
//                if (it.isNotEmpty()) saveCaptureImageResults(it[0])
//            }
            Log.e("RESULT","RESULT")
//                saveCaptureImageResults()
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_TAKE_PHOTO) {

            } else if (requestCode == Constants.REQUEST_IMAGE_GET) {

            } else if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    setPlaceData(place)
                } catch (e: java.lang.Exception) {
                }
            }
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                val result = CropImage.getActivityResult(data)
//                if (resultCode == AppCompatActivity.RESULT_OK) {
//
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    val error = result.error
//                }
//            }
        }
    }

    private fun saveCaptureImageResults(path: Uri) = try {
//        val file = File(path)
//        mFile = Compressor(this@AddSalesPersonActivity)
//            .setMaxHeight(1000).setMaxWidth(1000)
//            .setQuality(99)
//            .setCompressFormat(Bitmap.CompressFormat.JPEG)
//            .compressToFile(file)
//
//        if (intent.getStringExtra("type").equals("soldier")) {
//            binding.mIvPic.loadWallImage(mFile!!.absolutePath)
//        } else {
//            binding.mIvPic.loadWallImage(mFile!!.absolutePath)
        binding.mIvPic.loadWallImage(path)
        var path2=FilePath.getPath(this,path)
        mFile= File(path2)

//        }
    }
    catch (e: Exception) {
    }

    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                binding.mEtAddress.setText("")
                city = ""
                state = ""
                post_code = ""

                if (!placeData.name!!.lowercase(Locale.getDefault()).isEmpty()) {
//                    mEtAddress.setText(placeData.name)
//                    mEtAddress.setSelection(mEtAddress.text!!.length)

                    var addressPlace = placeData.address
                    var namePlace = placeData.name
                    var addressFromPlace = addressPlace?.replace(namePlace.toString(),"")
                    //     mEtAddress.setText(placeData.name + ", " + placeData.address)
                    binding.mEtAddress.setText(placeData.name + " " + addressFromPlace)
                    binding.mEtAddress.setSelection(binding.mEtAddress.text!!.length)

                } else binding.mEtAddress.error = "Not available"

                for (i in 0 until placeData.addressComponents!!.asList().size) {
                    val place: AddressComponent =
                        placeData.addressComponents!!.asList().get(i)

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("neighborhood") && place.types.contains("political")) {
                            city = place.name
                        }
                    }

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("locality") && place.types.contains("political")) {
                            city = place.name
                        }
                    }

                    if (place.types.contains("administrative_area_level_1")) {
                        state = place.name
                    }
                    if (place.types.contains("postal_code")) {
                        post_code = place.name
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
                    post_code = getZipCodeFromLocation(address)
                    state = address.adminArea
                    city = address.locality
                }


                lng = placeData.latLng!!.longitude.toString()
                lat = placeData.latLng!!.latitude.toString()
            }
        } catch (e: java.lang.Exception) {
            Log.e("Exce......", e.toString())
        }
    }

    private fun getZipCodeFromLocation(addr: Address): String {
        return if (addr.postalCode == null) "" else addr.postalCode
    }

    private fun getAddressFromLocation(lat: Double, long: Double): Address {
        val geocoder = Geocoder(BuildzerApp.appContext)
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
}