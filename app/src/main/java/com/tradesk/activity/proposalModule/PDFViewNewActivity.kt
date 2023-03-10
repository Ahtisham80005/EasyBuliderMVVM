package com.tradesk.activity.proposalModule

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.tradesk.R
import com.tradesk.databinding.ActivityPdfviewNewBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.sendEmail
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.ProposalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

@AndroidEntryPoint
class PDFViewNewActivity : AppCompatActivity(), OnLoadCompleteListener,
    OnPageErrorListener {
//    var pdfView: PDFView? = null
//    var pb_loadingpdf: ProgressBar? = null

    var alertDialog: AlertDialog? = null
    var type: String = "";
    lateinit var binding:ActivityPdfviewNewBinding
    lateinit var viewModel: ProposalsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_pdfview_new)
        viewModel= ViewModelProvider(this).get(ProposalsViewModel::class.java)

        //initializing our pdf view.
        if (intent.getStringExtra("title").equals("proposals",true)) {
            type = "Proposal"
            binding.textView6.text = "Estimate PDF"
        } else {
            type = "invoice"
            binding.textView6.text="Invoice PDF"
//            findViewById<LinearLayout>(R.id.buttons).visibility=View.GONE
        }

        binding.mIvBack!!.setOnClickListener(View.OnClickListener { finish() })

//        RetrivePDFfromUrl().execute(intent.getStringExtra("pdfurl"))
        var url=intent.getStringExtra("pdfurl");
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute(Runnable {
            //Background work here
            binding.pbLoadingpdf!!.visibility = View.VISIBLE
            var inputStream: InputStream? = null
            try {
                val url = URL(url)
                // below is the step where we are creating our connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                if (urlConnection.responseCode == 200) {
                    //response is success.
                    //we are getting input stream from url and storing it in our variable.
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: IOException) {
                //this is the method to handle errors.
                e.printStackTrace()
            }
            handler.post {
                //UI Thread work here
                binding.idPDFView!!.fromStream(inputStream).onLoad(this).load()
//                pb_loadingpdf!!.visibility = View.GONE
            }
        })


        if (intent.hasExtra("status") && intent.getStringExtra("status").equals("pending", true)) {
            //     findViewById<LinearLayout>(R.id.buttons).visibility = View.VISIBLE
        }
        if (type.equals("invoice")){
            findViewById<LinearLayout>(R.id.buttons).visibility = View.GONE
            if (intent.hasExtra("status") && !intent.getStringExtra("status").equals("completed", true)) {
                findViewById<Button>(R.id.paidButton).visibility = View.VISIBLE
            }
        }
        binding.mIvEmailSend!!.setOnClickListener(View.OnClickListener {
            if (intent.hasExtra("id"))
            {
                alertDialog = sendEmail("Email Estimate", intent.getStringExtra("email").toString()) {
                    if (isInternetConnected(this)) {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.sendProposal(intent.getStringExtra("id").toString(), it.toString())
                        }
                    }
                }
            } else {
                val stringArray1 = arrayOf(intent.getStringExtra("email"))
                composeEmail(stringArray1, "Proposal", intent.getStringExtra("pdfurl")!!)
            }
        })
        initObserve()
    }

    fun initObserve() {
        viewModel.responseSendPorposals.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    if (alertDialog != null) {
                        if (alertDialog!!.isShowing) alertDialog!!.dismiss()
                    }
                    if (ProposalsActivity.context != null) {
                        Handler().postDelayed({
                            Constants.showSuccessDialog(ProposalsActivity.context, "Email Sent")
                        }, 1500)
                    }
                    toast("Sent successfully")
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

    fun composeEmail(addresses: Array<String?>?, subject: String?, content: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain"
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Hi,\n\nHere I am sharing proposal for your construction work. \n\nLink :- $content"
        )
        //        intent.setType("message/rfc822");
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun loadComplete(nbPages: Int) {
        binding.pbLoadingpdf!!.visibility = View.GONE
    }
    override fun onPageError(page: Int, t: Throwable?) {

    }

    //create an async task class for loading pdf file from URL.
//    class RetrivePDFfromUrl : AsyncTask<String?, Void?, InputStream?>() {
//
//        override fun onPostExecute(inputStream: InputStream?) {
//            // after the execution of our async task we are loading our pdf in our pdf view.
//            pb_loadingpdf.setVisibility(View.GONE)
//            idPDFView.fromStream(inputStream).load()
//        }
//
//        override fun doInBackground(vararg params: String?): InputStream? {
//            //we are using inputstream for getting out PDF.
//            pb_loadingpdf.setVisibility(View.VISIBLE)
//            var inputStream: InputStream? = null
//            try {
//                val url = URL(strings[0])
//                // below is the step where we are creating our connection.
//                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
//                if (urlConnection.responseCode == 200) {
//                    //response is success.
//                    //we are getting input stream from url and storing it in our variable.
//                    inputStream = BufferedInputStream(urlConnection.inputStream)
//                }
//            } catch (e: IOException) {
//                //this is the method to handle errors.
//                e.printStackTrace()
//                return null
//            }
//            return inputStream
//        }
//
//    }
}