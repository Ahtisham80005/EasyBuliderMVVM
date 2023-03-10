package com.tradesk.activity.clientModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tradesk.BuildzerApp
import com.tradesk.Model.ClientSalesModelNew
import com.tradesk.Model.PhoneTextFormatter
import com.tradesk.R
import com.tradesk.databinding.ActivityAddClientBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.insertString
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class AddClientActivity : AppCompatActivity() {
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    lateinit var dialog: Dialog;
    var mFile: File? = null
    lateinit var binding:ActivityAddClientBinding
    lateinit var viewModel:CustomerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_add_client)
        viewModel=ViewModelProvider(this).get(CustomerViewModel::class.java)
        if (intent.hasExtra("title")){
            binding.textView6.text = "Edit Client"
        } else if(intent.hasExtra("screen_title")) {
            val title = intent.getStringExtra("screen_title")
            binding.textView6.text = title
        }
        binding.mEtAddress.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }

        if (intent.hasExtra("id")) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getClientDetail(
                    intent.getStringExtra("id").toString(),
                    "1",
                    "10",
                    intent.getStringExtra("type").toString(),
                    ""
                )
            }

        }else{

            binding.mEtPhoneNumber.addTextChangedListener(PhoneTextFormatter(binding.mEtPhoneNumber, "(###) ###-####"))
            binding.mEtHomeNumber.addTextChangedListener(PhoneTextFormatter(binding.mEtHomeNumber, "(###) ###-####"))
        }
        binding.mIvBack.setOnClickListener { finish() }
        binding.mBtnSubmit.setOnClickListener {
            if(intent.hasExtra("id")){

                if (binding.mEtName.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_name), false)
                } else if (binding.mEtEmail.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_email_address), false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.mEtEmail.text.toString().trim())
                        .matches()
                ) {
                    toast(getString(R.string.enter_valid_address), false)
                } else if (binding.mEtPhoneNumber.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_phone), false)
                }else if (binding.mEtPhoneNumber.text.toString().length<14) {
                    toast("Enter valid phone number", false)
                }/* else if (mEtHomeNumber.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_home_phone), false)
            }*/ else if (binding.mEtAddress.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_address), false)
                } else {


                    if (isInternetConnected(this)) hashMapOf<String, RequestBody>().also {
                        it.put(
                            "id",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                intent.getStringExtra("id")
                            )
                        )

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
                                binding.mEtPhoneNumber.text.toString().trim().replace(" ","").replace("(","").replace(")","").replace("-","")
                            )
                        )

                        if (binding.mEtHomeNumber.text.toString().trim().isNotEmpty()) {
                            it.put(
                                "home_phone_number",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtHomeNumber.text.toString().trim().replace(" ", "")
                                        .replace("(", "").replace(")", "").replace("-", "")
                                )
                            )
                        }

                        it.put(
                            "type",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "client"
                            )
                        )

                        it.put(
                            "privatenotes",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                binding.mEtNotes.text.toString().trim()
                            )
                        )

                        it.put(
                            "address",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                binding.mEtAddress.text.toString()
                            )
                        )

                        it.put(
                            "city",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                city
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
                            "zipcode",
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
                                ""
                            )
                        )
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.updateClient(it)
                        }

                    }
                }
            }else {
                if (binding.mEtName.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_name), false)
                } else if (binding.mEtEmail.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_email_address), false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.mEtEmail.text.toString().trim())
                        .matches()
                ) {
                    toast(getString(R.string.enter_valid_address), false)
                } else if (binding.mEtPhoneNumber.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_phone), false)
                }else if (binding.mEtPhoneNumber.text.toString().length<14) {
                    toast("Enter valid phone number", false)
                }/* else if (mEtHomeNumber.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_home_phone), false)
            }*/ else if (binding.mEtAddress.text.toString().trim().isEmpty()) {
                    toast(getString(R.string.enter_address), false)
                } else {
                    if (isInternetConnected(this)) hashMapOf<String, RequestBody>().also {
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
                                binding.mEtPhoneNumber.text.toString().trim().replace(" ","").replace("(","").replace(")","").replace("-","")
                            )
                        )

                        if (binding.mEtHomeNumber.text.toString().trim().isNotEmpty()) {
                            it.put(
                                "home_phone_number",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    binding.mEtHomeNumber.text.toString().trim().replace(" ", "")
                                        .replace("(", "").replace(")", "").replace("-", "")
                                )
                            )
                        }
                        it.put(
                            "type",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "client"
                            )
                        )

                        it.put(
                            "privatenotes",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                binding.mEtNotes.text.toString().trim()
                            )
                        )

                        it.put(
                            "address",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                binding.mEtAddress.text.toString()
                            )
                        )

                        it.put(
                            "city",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                city
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
                            "zipcode",
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
                                ""
                            )
                        )
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.addClient(it)
                        }

                    }
                }
            }
        }
        initObserver()
    }

    fun initObserver()
    {
        viewModel.responseSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
//                    Handler().postDelayed({
//                        Constants.showSuccessDialog(AllCustomersActivity.context,"Client Added")
//                    }, 1500)
                    toast("Client Added")
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
        viewModel.responseClientUpdateSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
//                    Handler().postDelayed({
//                        Constants.showSuccessDialog(AllCustomersActivity.context,"Client Added")
//                    }, 1500)
                    toast("Client Update")
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
    }

    fun setUserDetails(it: ClientSalesModelNew) {
        binding.mEtName.setText(it.data.client.name)
        binding.mEtEmail.setText(it.data.client.email)

        binding.mTvNumber.setText(insertString(it.data.client.phone_no,"",0))
        binding.mTvNumber.setText(insertString(binding.mTvNumber.text.toString(), ")", 2))
        binding.mTvNumber.setText(insertString(binding.mTvNumber.text.toString(), " ", 3))
        binding.mTvNumber.setText(insertString(binding.mTvNumber.text.toString(), "-", 7))
        binding.mTvNumber.setText("("+binding.mTvNumber.text.toString())

        binding.mEtPhoneNumber.setText(binding.mTvNumber.text.toString())

        if (it.data.client.home_phone_number.isNotEmpty()) {
            binding.mTvphoneNumber.setText(insertString(it.data.client.home_phone_number, "", 0))
            binding.mTvphoneNumber.setText(insertString(binding.mTvphoneNumber.text.toString(), ")", 2))
            binding.mTvphoneNumber.setText(insertString(binding.mTvphoneNumber.text.toString(), " ", 3))
            binding.mTvphoneNumber.setText(insertString(binding.mTvphoneNumber.text.toString(), "-", 7))
            binding.mTvphoneNumber.setText("(" + binding.mTvphoneNumber.text.toString())

            binding.mEtHomeNumber.setText(binding.mTvphoneNumber.text.toString())
        }

        binding.mEtAddress.setText(it.data.client.address.street)
        binding.mEtNotes.setText(it.data.client.privatenotes)

        lat = it.data.client.address.location.coordinates[0].toString()
        lng = it.data.client.address.location.coordinates[1].toString()
        city = it.data.client.address.city
        state = it.data.client.address.state
        post_code = it.data.client.address.zipcode

        binding.mEtPhoneNumber.addTextChangedListener(PhoneTextFormatter(binding.mEtPhoneNumber, "(###) ###-####"))
        binding.mEtHomeNumber.addTextChangedListener(PhoneTextFormatter(binding.mEtHomeNumber, "(###) ###-####"))
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
            .build(this@AddClientActivity)
        startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    setPlaceData(place)
                } catch (e: java.lang.Exception) {
                }
            }
        }
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