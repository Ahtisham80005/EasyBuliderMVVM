package com.tradesk.activity.auth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.*
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.messaging.FirebaseMessaging
import com.tradesk.MainActivity
import com.tradesk.R
import com.tradesk.databinding.ActivitySignupBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() ,LocationListener {
    var token = ""
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var selectedTrade = ""
    var post_code = "unknown"
    var RC_SIGN_IN = 444
    private val EMAIL = "email"
    lateinit var callbackManager: CallbackManager;
    var mGoogleSignInClient: GoogleSignInClient? = null
    var addresses: List<Address>? = null
    var locationManager: LocationManager? = null
    private var userLocation: Location? = null
    var address = "unknown"
    var accessToken = "";
    var loginType=""
    lateinit var binding:ActivitySignupBinding
    lateinit var viewModel: SignupViewModel

    @Inject
    lateinit var mPrefs: PreferenceHelper
    @Inject
    lateinit var permissionFile: PermissionFile
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_signup)
        viewModel= ViewModelProvider(this).get(SignupViewModel::class.java)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if(result != null){
                token = result
                // DO your thing with your firebase token
            }
        }
        if (permissionFile.checklocationPermissions(this)) {
            startLocationUpdates()
        }
        binding.textView2.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.etAddress.setOnFocusChangeListener { view, b ->
            if (b)
                openPlaceDialog()
        }
        binding.ivSignUp.setOnClickListener {
            if (binding.etName.text.toString().trim().isEmpty()) {
                toast("Enter your name", false)
            } else if (binding.etEmail.text.toString().trim().isEmpty()) {
                toast("Enter your email", false)
            }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches()) {
                toast("Enter valid email", false)
            } else if (binding.etAddress.text.toString().trim().isEmpty()) {
                toast("Enter your address", false)
            } else if (binding.mEtTrade.text.toString().trim().isEmpty()) {
                toast("Please select trade", false)
            } else if (binding.etCPassword.text.toString().trim().isEmpty()) {
                toast("Enter password", false)
            } else if (!Constants.PATTERN.matcher(binding.etCPassword.text.toString().trim()).matches()) {
                toast("Password must contain upper and lower letters, numerics, and a special character with a min of 8 characters", false)
            } else if (binding.etPassword.text.toString().trim().isEmpty()) {
                toast("Confirm password", false)
            } else if (!binding.etPassword.text.toString().trim().equals(binding.etCPassword.text.toString().trim(),false)) {
                toast("Password does not match", false)
            }else if (binding.checkBox.isChecked.not()) {
                toast("Please accept phone number terms and conditions", false)
            }else{
                if (isInternetConnected(this@SignupActivity))
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.Signup(binding.etName.text.toString().trim(),binding.etEmail.text.toString().trim(),
                            binding.etAddress.text.toString().trim(),binding.etPassword.text.toString().trim(),"1",city,state,post_code,lat+","+lng,binding.mEtTrade.text.toString().trim())
                    }
            }
        }
        //google SSO
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .requestId()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        callbackManager = CallbackManager.Factory.create();
        binding.googleLoginS.setOnClickListener {
            googleSignIn()
        }
        binding.fbLoginS.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(
                    this,
                    Arrays.asList("email", "public_profile", "user_friends")
                )

            // If you are using in a fragment, call loginButton.setFragment(this);

            // Callback registration
            // If you are using in a fragment, call loginButton.setFragment(this);

            // Callback registration
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        accessToken = loginResult.accessToken.token
                        loginType="fb"
                        getLocation()

                    }
                    override fun onCancel() {
                        toast("cancel")
                    }

                    override fun onError(error: FacebookException) {
                        toast("error")
                    }
                })
        }
        initObserve()
    }

    fun initObserve()
    {
        viewModel.responseSignupModel.observe(this, androidx.lifecycle.Observer {
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Your account is created successfully. Please check your email to verify the email.")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this@SignupActivity)
                }
            }
        })
        viewModel.responseLoginModel.observe(this, androidx.lifecycle.Observer {
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mPrefs.setUserLoggedIn(PreferenceConstants.USER_LOGGED_IN, true)
                    mPrefs.setKeyValue(PreferenceConstants.USER_TYPE, it.data!!.data.userType.toString())
                    mPrefs.setKeyValue(PreferenceConstants.USER_TOKEN, it.data!!.data.token ?: "")
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this@SignupActivity)
                }
            }
        })
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
            .build(this@SignupActivity)
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
            callbackManager.onActivityResult(requestCode, resultCode, data)
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }
    private fun setPlaceData(placeData: Place) {
        try {
            if (placeData != null) {
                binding.etAddress.setText("")
                city=""
                state=""
                post_code=""

                if (!placeData.getName()!!.toLowerCase().isEmpty()) {
                    binding.etAddress.setText(placeData.getName())
                    binding.etAddress.setSelection(binding.etAddress.getText()!!.length)
                } else binding.etAddress.setError("Not available")

                for (i in 0 until placeData.getAddressComponents()!!.asList().size) {
                    val place: AddressComponent =
                        placeData.getAddressComponents()!!.asList().get(i)

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("neighborhood") && place.types.contains("political")) {
                            city=place.name
                        }
                    }

                    if (city.trim().isEmpty()) {
                        if (place.types.contains("locality") && place.types.contains("political")) {
                            city=place.name
                        }
                    }

                    if (place.types.contains("administrative_area_level_1")) {
                        state=place.name
                    }
                    if (place.types.contains("postal_code")) {
                        post_code=place.name
                    }
                    var address: Address = getAddressFromLocation(
                        placeData.latLng!!.latitude,
                        placeData.latLng!!.longitude
                    )
                    if (address.postalCode == null) {
                        address.postalCode="unknown"
                    }
                    if (address.locality == null) {
                        address.locality="unknown"
                    }
                    if (address.adminArea == null) {
                        address.adminArea="unknown"
                    }
                    post_code=getZipCodeFromLocation(address);
                    state=address.adminArea
                    city=address.locality
                }


                lng = placeData.latLng!!.longitude.toString();
                lat = placeData.latLng!!.latitude.toString();
            }
        } catch (e: java.lang.Exception) {
            Log.e("Exce......", e.toString())
        }

    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            accessToken = account.idToken.toString();
            loginType="google"
            getLocation()
            signOut()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(
                ContentValues.TAG,
                "signInResult:failed code=" + e.statusCode
            )
            // updateUI(null);
        }
    }
    fun signOut() {
        mGoogleSignInClient!!.signOut()
        LoginManager.getInstance().logOut()
    }
    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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

    fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    10
                )
            }
            .setNegativeButton("No") { dialog, id ->
                buildAlertMessageNoGps()
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    fun getLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        locationManager = this
            .getSystemService(
                LOCATION_SERVICE
            ) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        userLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        geoCodeLocation()
    }
    private fun geoCodeLocation() {
        val geocoder = Geocoder(this)
        try {
            addresses =
                geocoder.getFromLocation(
                    userLocation!!.getLatitude(),
                    userLocation!!.getLongitude(),
                    1
                )
            try {
                if ((addresses as MutableList<Address>?)!![0].postalCode != null) {
                    post_code = (addresses as MutableList<Address>?)!![0].postalCode
                }
                if ((addresses as MutableList<Address>?)!![0].countryName != null) {
                    state = (addresses as MutableList<Address>?)!![0].countryName
                }
                if ((addresses as MutableList<Address>?)!![0].locality != null) {
                    city = (addresses as MutableList<Address>?)!![0].locality
                }
                if ((addresses as MutableList<Address>?)!![0].getAddressLine(0)!=null) {
                    address = (addresses as MutableList<Address>?)!![0].getAddressLine(0)
                }
                lat = userLocation!!.latitude.toString()
                lng = userLocation!!.longitude.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.SocialLogin(address,accessToken,token, "android",post_code,city,state,lat+","+lng,loginType)
                }
            } catch (e: java.lang.Exception) {
                toast("Something went wrong")
            }
        } catch (e: IOException) {
            e.printStackTrace()
//            Helper.hideProgressBar(progressBar)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> buildAlertMessageNoGps() //Tell to user the need of grant permission
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

    override fun onLocationChanged(p0: Location) {
//        geoCodeLocation()
    }

    //Combine Location
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@SignupActivity)
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult ?: return
//                    for (location in locationResult.locations) {
                    if (locationResult.locations.size > 0) {
                        val lat =
                            locationResult.locations[locationResult.locations.size - 1].latitude
                        val lng =
                            locationResult.locations[locationResult.locations.size - 1].longitude
                        mPrefs.setKeyValue(PreferenceConstants.LAT, lat.toString())
                        mPrefs.setKeyValue(PreferenceConstants.LNG, lng.toString())
//                        toast(lat.toString()+lng.toString())
                        //cal dis , longitude:
//                        Log.e("Distance ", distance(lat, lng, 21.0359851, 105.7804743).toString())
                    }
                    stopLocationUpdates()
                }

            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
        task.addOnFailureListener { exception ->
            exception.printStackTrace()
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, 123)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("exception", sendEx.toString())
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}