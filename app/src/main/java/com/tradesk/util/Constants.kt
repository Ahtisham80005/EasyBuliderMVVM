package com.tradesk.util

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Browser
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.tradesk.Model.Client
import com.tradesk.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Predicate
import java.util.regex.Pattern

object Constants {

    val PATTERN =
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!*@#\$%^&+=])(?=\\S+\$).{6,}\$")
    val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1121
    var REQUEST_CODE_LOCATION = 1002
    var REQUEST_CODE_MICROPHONE = 1452
    var MULTI_LOC_STOR = 1254
    var MULTI_STOR = 1252
    const val TimeFormat = "hh:mm a"
    const val ServerDateTimeFormat = "yyyy-MM-dd"
    const val SimpleDateFormatDashs = "MM-dd-yyyy"

    val PICKFILE_RESULT_CODE= 51337
    val REQUEST_TAKE_PHOTO = 3210
    val REQUEST_UPLOAD_DOC = 3233
    val REQUEST_CODE_DOCS = 3215
    val REQUEST_IMAGE_GET = 1032


    fun isInternetConnected(activity: Activity): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting){
            return true
        }
        else {
//            Snackbar.make(findViewById<View>(android.R.id.content), "You are Offline!", 2000).show()
            return false
        }
    }

    var mProgressDialog: ProgressDialog? = null
    fun showLoading(activity: Activity) {
        hideLoading()
        mProgressDialog = initProgressDialog(activity)
    }
    fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }
    }

    fun initProgressDialog(context: Context): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }

    fun showSuccessDialog(activity: Activity, msg: String) {
        var dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.success_dialog)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        val window: Window? = dialog.getWindow()
        val wlp = window?.attributes
        if (wlp != null) {
            wlp.gravity = Gravity.CENTER
        }
        dialog.setCancelable(true)
        if (wlp != null) {
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        }
        if (window != null) {
            window.attributes = wlp
        }
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
        val message: TextView = dialog.findViewById<TextView>(R.id.message)
        message.text = msg
    }

    fun convertToString(obj: Any?): String? {
        return Gson().toJson(obj)
    }
    fun convertObject(objString: String?, serviceClass: Class<*>?): Any? {
        val gson = Gson()
        return gson.fromJson(objString, serviceClass)
    }

    fun convertDateFormatWithTime(date: String?): String? {

//    var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm aa", Locale.getDefault())
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        var newDate: Date? = Date()
        try {
            if(date!=null)
            {
                newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                spf = SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.getDefault())
                return spf.format(newDate)
            }
            else
            {
                return ""
            }
        } catch (e: ParseException) {
            Log.e("Date Execption",e.message.toString())
            e.printStackTrace()
        }
        return ""
    }

    fun getFormatedDateString(STAMP: String?): String? {
        val from = SimpleDateFormat(ServerDateTimeFormat)
        val to =
            SimpleDateFormat(SimpleDateFormatDashs)
        var formattedDate: Date? = null
        try {
            val date = from.parse(STAMP)
            formattedDate = date
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return to.format(formattedDate)
    }

    fun getCurrentTimeOnly(): String? {
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat(TimeFormat)
        return mdformat.format(calendar.time)
    }

    fun createPartFromArray(param: ArrayList<String>): ArrayList<RequestBody?>? {
        val d: ArrayList<RequestBody?> = arrayListOf()
        for (p in param) {
            d.add(createPartFromString(p))
        }
        return d
    }
    fun createPartFromString(param: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), param)
    }
    fun getRequestParam(key: String?, actualPath: String?): MultipartBody.Part? {
        if (actualPath == null || actualPath == "") {
            return null
        }
        val requestFile: RequestBody =RequestBody.create(MediaType.parse("multipart/form-data"), File(actualPath)
        )
        return MultipartBody.Part.createFormData(key, File(actualPath).name, requestFile)
    }
    fun getParts(files: List<File>, key: String): ArrayList<MultipartBody.Part> {
        val surveyImagesParts = arrayListOf<MultipartBody.Part>()

        for (index in files.indices) {
            Log.d(
                "TAG",
                "requestUploadSurvey: survey image " + index + "  " + files.get(index).absolutePath
            )
            val file = files.get(index)
            val surveyBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            surveyImagesParts.add(MultipartBody.Part.createFormData(key, file.name, surveyBody))
        }
        return surveyImagesParts;
    }
    fun getRequestParamImage(key: String?, actualPath: String?): MultipartBody.Part? {
        if (actualPath == null || actualPath == "") {
            return null
        }
        val requestFile: RequestBody =RequestBody.create(MediaType.parse("multipart/form-data"),File(actualPath))
        return MultipartBody.Part.createFormData(key, File(actualPath).name, requestFile)
    }
    fun createBodyString(string: String): RequestBody {
        return RequestBody.create(MediaType.parse("text"), string)
    }
    fun openApp(activity: AppCompatActivity, url: String) {
        // Get URI and MIME type of file
        // Open file with user selected app
        val ext = MimeTypeMap.getFileExtensionFromUrl(url)

        val isDoc=ext.equals("pdf", true) || ext.equals("docx", true) || ext.equals("doc", true)
        if (isDoc) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome");
            return activity.startActivity(intent)
        }
        else{
//            activity.startActivity(Intent(activity,FullViewActivity::class.java).putExtra("url_file",url))
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun containsId(list: List<Client>, id: String?): Boolean {
        return list.stream().filter(Predicate<Client> { o: Client ->
            o._id.equals(id)
        }).findFirst().isPresent()
    }

    fun insertString(
        originalString: String,
        stringToBeInserted: String?,
        index: Int
    ): String? {
        // Create a new string
        var newString: String? = String()
        for (i in 0 until originalString.length) {
            // Insert the original string character
            // into the new string
            newString += originalString[i]
            if (i == index) {
                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted
            }
        }
        // return the modified String
        return newString
    }

}