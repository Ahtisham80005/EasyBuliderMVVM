package com.tradesk.activity.proposalModule

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.tradesk.Interface.AttachedDocListener
import com.tradesk.Interface.DocListener
import com.tradesk.Interface.OnItemRemove
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.*
import com.tradesk.R
import com.tradesk.activity.clientModule.AllCustomersActivity
import com.tradesk.activity.proposalModule.adapter.AttachedDocsAdapter
import com.tradesk.activity.proposalModule.adapter.ProSelectedItemsAdapter
import com.tradesk.adapter.DocsAdapter
import com.tradesk.adapter.ImagesPagerAdapter
import com.tradesk.databinding.ActivityAddProposalBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.DatePickerHelper
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.customCenterDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.ProposalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddProposalActivity : AppCompatActivity() , SingleItemCLickListener, OnItemRemove,
    AttachedDocListener, DocListener, () -> Unit{
    companion object {
        var mFileSignature: File? = null
        var mAddItemDataUpdate = arrayListOf<AddItemDataUpdateProposal>()
        var sub_total_amount = ""
        var total_amount = ""
        var tax_amount = ""

        @RequiresApi(Build.VERSION_CODES.O)
        fun getDecodedString(text: String): String {
            val decodedBytes =
                Base64.getDecoder().decode(text)
            return String(decodedBytes)
        }
    }
    var users = ""
    var date_pass = ""
    var clients_id = ""
    var clients_name = ""
    var clients_email = ""
    var clients_address = ""
    var clients_phone = ""
    var sales = ""
    var taxRate = 10
    var lat = ""
    var lng = ""
    var city = ""
    var state = ""
    var post_code = ""
    var type = ""
    var todaydate = ""
    var status = "pending"

    var isUploadingDoc = false
    var myImageUri = ""
    var mySignatureUri = ""
    var mFile: File? = null
    var mDocFile: File? = null
    lateinit var viewPager: ViewPager

    var touch_pad_client = false
    var selectedimage_key = ""
    var contract_started_text = ""
    var selectedimage_client = ""
    var TAG = "AddProposalActivity"
    var mail_sent = "true"
    var estimate = ""
    var selectedDoc = ""
    var estimate_start = "00000"
    var isClientSign = false;
    var isMySign = false;
    var isEditMode = false;
    var isUrl = true;
    var imagesList = mutableListOf<String>()
    var filesList = mutableListOf<File>()
    var selectedDocList = mutableListOf<String>()
    lateinit var proposalDetailModel: ProposalDetailModel
    val mAddItemData = arrayListOf<AddItemDataProposal>()
    val imagesAdapter by lazy { ImagesPagerAdapter(this, imagesList, this) }
    val docsAdapter by lazy { DocsAdapter(this, this, selectedDocList) }
    val mProSelectedItemsAdapter by lazy { ProSelectedItemsAdapter(this, this, this, mAddItemDataUpdate) }
    val mBuilder: Dialog by lazy { Dialog(this@AddProposalActivity) }

    lateinit var mySignSwitch: Switch
    lateinit var clientSignSwitch: Switch
    lateinit var datePicker: DatePickerHelper
    @Inject
    lateinit var permissionFile: PermissionFile
    @Inject
    lateinit var mPrefs: PreferenceHelper
    lateinit var binding:ActivityAddProposalBinding
    lateinit var viewModel:ProposalsViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_proposal)
        viewModel=ViewModelProvider(this).get(ProposalsViewModel::class.java)

        mySignSwitch = findViewById(R.id.switchMySign)
        viewPager = findViewById(R.id.mIvMainImage);

        clientSignSwitch = findViewById(R.id.clientSignSwitch)
        if (intent.hasExtra("is_proposal")) {
            binding.mBtnSave.visibility = View.GONE
            type = "proposal"
        }
        mySignSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isMySign = isChecked
        }
        clientSignSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isClientSign = isChecked
        }
        val formatter_amount = DecimalFormat("#,###,###")
        if (intent.hasExtra("count")) {
            findViewById<LinearLayout>(R.id.proposalNumber).visibility = View.VISIBLE
            binding.estimateNoEdit.setText(intent.getStringExtra("count").toString())
            estimate = intent.getStringExtra("count").toString();
        }
        mPrefs.setKeyValue("myTitle", intent.getStringExtra("title").toString());

        Log.d(TAG, "onCreate: "+intent.getStringExtra("job_id").toString())

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter("itemupdated"))
        if (intent.hasExtra("client_name")) {
            binding.mTvClientName.setText("Client : " + intent.getStringExtra("client_name"))
            clients_id = intent.getStringExtra("client_id").toString()
            clients_email = intent.getStringExtra("client_email").toString()
            binding.mLAddClient.visibility = View.GONE
            binding.mTvClientName.visibility = View.VISIBLE
        }

        datePicker = DatePickerHelper(this, true)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        todaydate = sdf.format(Date())
        mAddItemDataUpdate.clear()

        binding.rvInflateItem.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvInflateItem.adapter = mProSelectedItemsAdapter

        binding.mIvBack.setOnClickListener { finish() }
        binding.addPaymentScedule.setOnClickListener { startActivity(Intent(this,PaymentSchduleActivity::class.java)) }
        binding.attachDoc.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getProfile()
                }
            }
        binding.crossDoc.setOnClickListener {
            selectedDoc = ""
            binding.selectedDocLayout.visibility = View.GONE
            binding.atttachDocLayout.visibility = View.VISIBLE
            binding.uploadDoc.visibility = View.VISIBLE
            binding.docName.text = ""
            if (mDocFile != null) {
                mDocFile = null
            }
        }
        findViewById<EditText>(R.id.mEtTaxes).setOnClickListener {
            showTaxDialog()
        }
//        findViewById<EditText>(R.id.estimateNoEdit).addTextChangedListener(textWatcher)
        findViewById<EditText>(R.id.mEtTaxes).doAfterTextChanged {
        }
        binding.mIvChangeDefault.setOnClickListener {
            binding.mEtTaxes.setText("10")
            taxRate = 10
            try {
                if (mAddItemDataUpdate != null && mAddItemDataUpdate.isNotEmpty()) {
                    setTotals()
                }
            } catch (nfe: java.lang.NumberFormatException) {
                nfe.printStackTrace()
                toast("Exception")
            }
        }

        binding.mIvCalendar.setOnClickListener {
            showDatePickerDialog()
        }
        binding.mIvAddImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
        binding.uploadDoc.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                filePicker()
            }
        }


        binding.mLAddClient.setOnClickListener {
            val i = Intent(this, AllCustomersActivity::class.java)
            i.putExtra("from", "Add")
            startActivityForResult(i, 3333)
        }
        binding.mIvAddSig.setOnClickListener {
            val i = Intent(this, SignatureActivity::class.java)
            i.putExtra("from", "Add")
            startActivityForResult(i, 5555)
        }

        binding.mLItemAdd.setOnClickListener {
            startActivity(
                Intent(this, ProposaltemsActivity::class.java).putExtra(
                    "taxRate",
                    binding.mEtTaxes.text.toString()
                )
            )
        }

        if (intent.hasExtra("is_EditMode")) {
            isEditMode = true
            binding.mBtnPreview.setText("Update and Review")
            binding.mBtnSave.setText("Update")
            val builder = GsonBuilder()
            val gson = builder.create()
            var projectJsonString = intent.getStringExtra("proposalData");
            proposalDetailModel = gson.fromJson(projectJsonString, ProposalDetailModel::class.java)
            setData()
        }
        binding.mBtnPreview.setOnClickListener {

            var subTotal = 0
            var total_sub = 0
            var totalTax = 0
            var item_tax = 0
            var item_total = 0
            for (i in mAddItemDataUpdate.indices) {
                var item_sub = 0;
                if (subTotal == 0) {
                    var quantity = 1
//                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
                    item_sub =
                        mAddItemDataUpdate.get(i).amount.replace("$", "").replace(",", "").toInt()
                    quantity =
                        mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
                    item_sub = item_sub * quantity;
                } else {
                    var quantity = 1
                    item_sub = mAddItemDataUpdate.get(i).amount.replace("$", "")
                        .replace(",", "").toInt()
                    quantity =
                        mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
                    item_sub = item_sub * quantity;
                }

                if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                    item_tax = item_sub * taxRate / 100
                    totalTax += item_tax;
                } else {
                    item_tax = 0
                }
                item_total += item_sub + item_tax;
                subTotal += item_sub
            }

            val myJSONObjects: ArrayList<JSONObject> =
                ArrayList<JSONObject>(mAddItemDataUpdate.size)

            for (i in 0 until mAddItemDataUpdate.size) {
                val encodedString: String = Base64.getEncoder().encodeToString(mAddItemDataUpdate.get(i).description.toByteArray())

                val decodedBytes = Base64.getDecoder().decode(encodedString)
                val decodedString = String(decodedBytes)

                Log.d("encoded", encodedString)
                Log.d("decoded", decodedString)

                val encodedString2: String = Base64.getUrlEncoder()
                    .encodeToString(mAddItemDataUpdate.get(i).description.toByteArray())
                val decodedBytes2 = Base64.getUrlDecoder().decode(encodedString2)
                val decodedString2 = String(decodedBytes2)
                Log.d("encoded2", encodedString2)
                Log.d("decoded2", decodedString2)


                val obj = JSONObject()
                obj.put("title", mAddItemDataUpdate.get(i).title.toString())
                obj.put("description", mAddItemDataUpdate.get(i).description.toString())
                if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                    obj.put("textAble", true)
                }else{
                    obj.put("textAble", false)
                }
                obj.put("quantity", mAddItemDataUpdate.get(i).quantity.toString())
                obj.put("amount", mAddItemDataUpdate.get(i).amount.toString())
                myJSONObjects.add(obj)
            }
            Log.e("Array data", myJSONObjects.toString())

            if (clients_id.isEmpty()) {
                toast("Please select client")
            } else if (binding.mTvDates.text.isEmpty()) {
                toast("Please select date")
//            } else if (mEtExtraInfo.text.isEmpty()) {
//                toast("Please add extra info")
            } /*else if (mEtEstimates.text.isEmpty()) {
                    toast("Please enter estimate")
                }*/ else if (mAddItemDataUpdate.isEmpty()) {
                toast("Please add proposal items")
            } else {

                var docsList = arrayListOf<RequestBody>()
                var jsonArray: JSONArray? = JSONArray()
                var filesParts = arrayListOf<MultipartBody.Part>()
                if (isInternetConnected(this))
                    hashMapOf<String, RequestBody>().also {

                        if (intent.hasExtra("job_id")) {
                            it.put(
                                "contract_id",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    intent.getStringExtra("job_id").toString()
                                )
                            )
                        }

                        it.put(
                            "estimate",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                estimate

                            )
                        )

                        it.put(
                            "client_id",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                clients_id
                            )
                        )
                        it.put(
                            "tax_rate",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                taxRate.toString()
                            )
                        )

                        it.put(
                            "subtotal",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
//                                (formatter_amount.format(sub_total_amount.toInt())).toString()
                                subTotal.toString().replace(",", "")
                            )
                        )
                        it.put(
                            "tax",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
//                                (formatter_amount.format(item_tax.toInt()).toString())
                                totalTax.toString().replace(",", "")
                            )
                        )
                        it.put(
                            "total",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
//                                (formatter_amount.format(total_amount.toInt())).toString()
                                item_total.toString().replace(",", "")
                            )
                        )
                        it.put(
                            "extra_info",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                binding.mEtExtraInfo.text.toString().trim()
                            )
                        )

                        it.put(
                            "items",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                myJSONObjects.toString()
                            )
                        )


                        it.put(
                            "status",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                status
                            )
                        )


                        it.put(
                            "type",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "proposal"
                            )
                        )


                        it.put(
                            "mail_sent",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                "false"
                            )
                        )
                        it.put(
                            "date",
                            RequestBody.create(
                                MediaType.parse("multipart/form-data"),
                                date_pass
                            )
                        )
//                        if (mFile != null) {
//                            it.put(
//                                "image\"; filename=\"image.jpg",
//                                RequestBody.create(MediaType.parse("image/*"), mFile!!)
//                            )
//                        }
                        if (!filesList.isNullOrEmpty()) {
                            filesParts = Constants.getParts(filesList, "image")
                        }

                        if (mDocFile != null) {
                            it.put(
                                "document\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mDocFile!!)
                            )
                            it.put(
                                "isUrl",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    "false"
                                )
                            )

                        } else {
                            if (selectedDocList != null && selectedDocList.isNotEmpty()) {
                                docsList = getSelectedDocList()
                                it.put(
                                    "isUrl",
                                    RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        "true"
                                    )
                                )
                            }
                        }
                        if (isMySign && mFileSignature != null) {
                            it.put(
                                "my_signature\"; filename=\"image.jpg",
                                RequestBody.create(MediaType.parse("image/*"), mFileSignature!!)
                            )
                        }
                        if (isClientSign) {
                            it.put(
                                "client_signature",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    isClientSign.toString()
                                )
                            )
                        }
                        if (!isEditMode) {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.addProposals(it, docsList, filesParts)
                            }
                        } else {
                            it.put(
                                "id",
                                RequestBody.create(
                                    MediaType.parse("multipart/form-data"),
                                    proposalDetailModel.data.proposal_details._id
                                )
                            )
                            if (!proposalDetailModel.data.proposal_details.images.isNullOrEmpty()) {
                                for (item in proposalDetailModel.data.proposal_details.images) {
                                    it.put(
                                        "image",
                                        RequestBody.create(
                                            MediaType.parse("multipart/form-data"),
                                            item
                                        )
                                    )
                                }
                            }
                            var existingDocs = arrayListOf<RequestBody>()
                            if (!proposalDetailModel.data.proposal_details.images.isNullOrEmpty()) {
                                existingDocs = getExistingDocList()
                            }
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.updateProposal(it, docsList, filesParts, existingDocs)
                            }
                        }
                    }

            }
        }
        initObserve()
    }

    fun initObserve()
    {
        viewModel.responseProfileModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    showDocsPop(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseAddProposalsModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    if (isEditMode) {
                        toast("Updated Successfully")
                        finish()
                    }
                    else if (mail_sent.equals("true")) {
                        toast(it.data!!.message)
                        startActivity(
                            Intent(
                                this@AddProposalActivity,
                                PDFViewNewActivity::class.java
                            ).putExtra("pdfurl", it.data.data.pdfLink)
                                .putExtra("id", it.data.data._id)
                                .putExtra("title", "proposals")
                                .putExtra("email", clients_email)
                        )
                        finish()
                    } else {
                        toast("Proposal Saved Successfully")
                        finish()
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
        viewModel.responseUpdateProposalsModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    if (isEditMode) {
                        toast("Updated Successfully")
                        finish()
                    }
                    else if (mail_sent.equals("true")) {
                        toast(it.data!!.message)
                        startActivity(
                            Intent(this@AddProposalActivity, PDFViewNewActivity::class.java).putExtra("pdfurl", it.data.data.pdfLink)
                                .putExtra("id", it.data.data._id)
                                .putExtra("title", "proposals")
                                .putExtra("email", clients_email))
                        finish()
                        } else {
                            toast("Proposal Saved Successfully")
                            finish()
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

    fun setData() {
        var proposalDetail: ProposalDetails = proposalDetailModel.data.proposal_details
        binding.mTvClientName.setText("Client : " + proposalDetail.client_id.name)
        clients_id = proposalDetail.client_id._id
        clients_email = proposalDetail.client_id.email
        binding.mLAddClient.visibility = View.GONE
        binding.mTvClientName.visibility = View.VISIBLE
        binding.estimateNoEdit.setText(proposalDetail.estimate)
        estimate = (proposalDetail.estimate)
        binding.mEtTaxes.setText(proposalDetail.tax_rate)
        binding.mTvDates.setText(Constants.getFormatedDateString(proposalDetail.date))
        date_pass = (proposalDetail.date)
        type = (proposalDetail.type)
        status = (proposalDetail.status)

        try {
            taxRate = proposalDetail.tax_rate.toString().toInt()
        }catch (e:NumberFormatException){
        }

        for (item in proposalDetail.items) {
            var taxAble="0";
            if (item.textAble){
                taxAble="1"
            }
            var actualItem = AddItemDataUpdateProposal(
                item.title,
                item.description,
                item.quantity,
                item.amount,
                taxAble

            )
            mAddItemDataUpdate.add(actualItem)

            val intent = Intent("itemupdated")
            intent.putExtra("itemupdated", "itemupdated")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
        if (proposalDetail.images != null && proposalDetail.images.isNotEmpty()) {
//            mIvMainImage.loadWallImage(
//                proposalDetail.images[0]
//            )
            imagesList.clear()
            imagesList.addAll(proposalDetail.images)

            viewPager.adapter = imagesAdapter
            imagesAdapter.notifyDataSetChanged()
//            dots.attachViewPager(viewPager)
        }
        binding.mEtExtraInfo.setText(proposalDetail.extra_info)
        if (proposalDetail.my_signature != null && proposalDetail.my_signature.isNotEmpty()) {
            binding.switchMySign.isChecked = true;
        }
        if (proposalDetail.client_signature != null && proposalDetail.client_signature.isNotEmpty()) {
            clientSignSwitch.isChecked = true;
        }
        if (proposalDetail.isUrl && !proposalDetail.doc_url.isNullOrEmpty()) {

            selectedDocList.addAll(proposalDetail.doc_url)
            binding.documentsRV.visibility = View.VISIBLE
            binding.documentsRV.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.documentsRV.adapter = docsAdapter
            docsAdapter.notifyDataSetChanged()

            binding.uploadDoc.visibility = View.GONE
            binding.docName.setOnClickListener { }

            mDocFile = null
        } else if (!proposalDetail.document.isNullOrEmpty()) {
            binding.selectedDocLayout.visibility = View.VISIBLE
            binding.crossDoc.visibility = View.VISIBLE
            binding.atttachDocLayout.visibility = View.GONE
            if (!proposalDetail.document.isNullOrEmpty()) binding.docName.text = proposalDetail.document[0]
            binding.docName.setOnClickListener {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(proposalDetail.document[0]))
                browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(browserIntent)
            }
            selectedDoc = ""
            selectedDocList.clear()
            isUploadingDoc = false
        }
    }

    fun showDocsPop(profileModel: ProfileModel) {
        if (profileModel.data.license_and_ins.docs_url != null && profileModel.data.license_and_ins.docs_url.isNotEmpty()) {
            mBuilder.setContentView(R.layout.docs_dialog);
            mBuilder.setCancelable(true)
            mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
            mBuilder.window!!.setGravity(Gravity.BOTTOM)
            mBuilder.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            var docsRV: RecyclerView = mBuilder.findViewById<RecyclerView>(R.id.docsRV)
            var list = arrayListOf<String>()
            val attachedDocsAdapter by lazy {
                AttachedDocsAdapter(
                    this, this,
                    profileModel.data.license_and_ins.docs_url as MutableList<String>
                )
            }
            docsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            docsRV.adapter = attachedDocsAdapter;
//            list.addAll(profileModel.data.license_and_ins.docs_url)
//            attachedDocsAdapter.notifyDataSetChanged()
            mBuilder.show();
        } else toast("No documents attached yet, Please upload new one")
    }

    fun showTaxDialog() {
        customCenterDialog(R.layout.addtax_dialog, true) { v, d ->
            val taxRateEdit = v.findViewById<EditText>(R.id.mEtTaxes)
            val button: Button =v.findViewById(R.id.done_button);
            button.setOnClickListener {
                if (!taxRateEdit.text.isNullOrEmpty()) {
                    taxRate = taxRateEdit.text.toString().toInt()
                    findViewById<EditText>(R.id.mEtTaxes).setText(taxRate.toString())
                    try {
                        if (mAddItemDataUpdate != null && mAddItemDataUpdate.isNotEmpty()) {
                            setTotals()
                        }
                        d.dismiss()
                    } catch (nfe: java.lang.NumberFormatException) {
                        nfe.printStackTrace()
                        toast("Exception")
                    }

                } else toast("Please enter value")

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1010) {
//            data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let {
//                if (it.isNotEmpty()) {
//                    isUploadingDoc = false
//                    saveCaptureImageResults(it[0])
//                }
//            }
//            Log.e("RESULT", "RESULT")
////                saveCaptureImageResults()
//        }
//        else if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == Constants.REQUEST_TAKE_PHOTO) {
//            } else if (requestCode == Constants.REQUEST_UPLOAD_DOC) {
//                CropImage.activity(Uri.parse(selectedDoc))
//                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setGuidelinesColor(android.R.color.transparent).start(this)
//            } else if (requestCode == Constants.REQUEST_IMAGE_GET) {
//            } else if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//                try {
//                    val place = Autocomplete.getPlaceFromIntent(data!!)
//                } catch (e: java.lang.Exception) {
//                }
//            } else if (requestCode == Constants.PICKFILE_RESULT_CODE) {
//                try {
//                    if (data != null) {
//                        val filePath =
//                            data.data?.let { getRealPathFromUri(it, this) }
//                        val parsedpath = filePath?.split("/")?.toTypedArray()
//                        //now we will upload the file
//                        //lets import okhttp first
//                        var fileName = parsedpath?.get(parsedpath.size - 1)
//                        var file = File(filePath)
//                        mDocFile = file
//                        binding.selectedDocLayout.visibility = View.VISIBLE
//                        binding.crossDoc.visibility = View.VISIBLE
//                        binding.atttachDocLayout.visibility = View.GONE
//                        binding.docName.text = fileName
//                        selectedDoc = ""
//                        selectedDocList.clear()
//                        isUploadingDoc = false
//
//
//                    }
//
//                } catch (e: java.lang.Exception) {
//                }
//            } else if (requestCode == 3333) {
//                if (resultCode == Activity.RESULT_OK) {
//                    clients_id = ""
//                    clients_name = ""
//                    clients_email = ""
//                    clients_address = ""
//                    clients_phone = ""
//                    clients_id = data!!.getStringExtra("result").toString()
//                    clients_name = data!!.getStringExtra("name").toString()
//                    binding.mTvClientName.setText("Client : " + clients_name)
//                    binding.mTvClientName.visibility = View.VISIBLE
//                    clients_email = data!!.getStringExtra("email").toString()
//                    clients_address = data!!.getStringExtra("address").toString()
//                    clients_phone = data!!.getStringExtra("phonenumber").toString()
//                }
//                if (resultCode == Activity.RESULT_CANCELED) {
//                    // Write your code if there's no result
//                }
//            } else if (requestCode == 4444) {
//                if (resultCode == Activity.RESULT_OK) {
//                    sales = ""
//                    sales = data!!.getStringExtra("result").toString()
//                    if (data!!.getStringExtra("image").toString().isNotEmpty()) {
////                        imageView223.loadWallImage(data!!.getStringExtra("image").toString())
//                    }
//                }
//                if (resultCode == Activity.RESULT_CANCELED) {
//                    // Write your code if there's no result
//                }
//            } else if (requestCode == 5555) {
//                if (resultCode == Activity.RESULT_OK) {
//                    mySignatureUri = ""
//                    mySignatureUri = data!!.getStringExtra("result").toString()
//                    if (data!!.getStringExtra("mySignatureUri").toString().isNotEmpty()) {
//                        Handler().postDelayed({ Constants.showSuccessDialog(this, "Signature Added")
//                        }, 100)
//                        binding.switchMySign.isChecked = true
//                        isMySign = true
////                        imageView223.loadWallImage(data!!.getStringExtra("image").toString())
//                    }
//                }
//                if (resultCode == Activity.RESULT_CANCELED) {0
//
//                    // Write your code if there's no result
//                }
//            }
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                val result = CropImage.getActivityResult(data)
//                if (resultCode == AppCompatActivity.RESULT_OK) {
//                    result.uri.path?.let { saveCaptureImageResults(it) }
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    val error = result.error
//                }
//            }
//        }
    }

    fun getRealPathFromUri(uri: Uri, activity: Activity): String? {
        return getDriveFilePath(uri, activity)
    }
    private fun getDriveFilePath(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, name)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }


    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)

        val minDate = Calendar.getInstance()
        minDate.set(Calendar.HOUR_OF_DAY, 0)
        minDate.set(Calendar.MINUTE, 0)
        minDate.set(Calendar.SECOND, 0)

        datePicker.setMinDate(minDate.timeInMillis)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "${mon}"

                binding.mTvDates.text = "${monthStr}-${dayStr}-${year}"
                date_pass = "${year}-${monthStr}-${dayStr}"
                Log.d(TAG, "onDateSelected: "+ date_pass)

            }
        })
    }
    fun getSelectedDocList(): ArrayList<RequestBody> {
        var list = arrayListOf<RequestBody>()
        for (it in selectedDocList) {
            list.add(Constants.createBodyString(it.toString()))
        }
        return list
    }
    fun getExistingDocList(): ArrayList<RequestBody> {
        var list = arrayListOf<RequestBody>()
        for (it in proposalDetailModel.data.proposal_details.images) {
            list.add(Constants.createBodyString(it.toString()))
        }
        return list
    }


    fun showImagePop() {
//        if(imagesList.size < 5) {
//            Pix.start(this@AddProposalActivity, options)
//        } else {
//            Toast.makeText(this@AddProposalActivity, "You can not add more than 5 pictures", Toast.LENGTH_LONG).show()
//        }
    }

    private fun saveCaptureImageResults(path: String) = try {
        if (isUploadingDoc) {
            val file = File(path)
//            mDocFile = Compressor(this@AddProposalActivity)
//                .setMaxHeight(1000).setMaxWidth(1000)
//                .setQuality(99)
//                .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                .compressToFile(file)
            binding.selectedDocLayout.visibility = View.VISIBLE
            binding.crossDoc.visibility = View.GONE
            binding.atttachDocLayout.visibility = View.GONE
            binding.docName.text = "document uploaded"
            selectedDoc = ""
            isUploadingDoc = false

        } else {
            val file = File(path)
//            mFile = Compressor(this@AddProposalActivity)
//                .setQuality(99)
//                .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                .compressToFile(file)
//dont
//            if (!imagesList.contains(mFile!!.absolutePath)) imagesList.add(mFile!!.absolutePath)
//            if (!filesList.contains(mFile)) {
//                filesList.add(mFile!!)
//            }
            imagesList.add(mFile!!.absolutePath)
            filesList.add(mFile!!)
            viewPager.adapter = imagesAdapter
            imagesAdapter.notifyDataSetChanged()
//            dots.attachViewPager(mIvMainImage)
        }
    } catch (e: Exception) {
        Log.e("AddProposalActivity", e.message.toString())
    }

    private fun filePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        startActivityForResult(intent, Constants.PICKFILE_RESULT_CODE)
        isUploadingDoc = true;
    }

    private fun setItemsTotals(isTaxable: Switch, etCost: TextInputEditText, etQty: TextInputEditText, itemTax: TextView, subTotal: TextView, total: TextView) {

        var sub_total =
            etCost.text.toString().replace(",", "").toInt() * etQty.text.toString()
                .toInt()

        var tax = 0
        if (isTaxable.isChecked) {
            tax = sub_total * taxRate / 100
        } else {
            tax = 0
        }

        var totalamount = sub_total + tax


        val formatter = DecimalFormat("#,###,###")


        subTotal.setText("$ " + formatter.format(sub_total))
        itemTax.setText("$ " + formatter.format(tax))
        total.setText("$ " + formatter.format(totalamount))


        AddProposalActivity.sub_total_amount = sub_total.toString()
        AddProposalActivity.total_amount = totalamount.toString()
        AddProposalActivity.tax_amount = tax.toString()
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                "itemupdated" -> {
                    if (mAddItemDataUpdate.isNotEmpty()) {

                        var subtotal_loop = 0
                        var tax_loop = 0
                        var total_loop = 0
                        var total = 0
                        var subTotal = 0
                        var Totaltax = 0

                        for (i in mAddItemDataUpdate.indices) {
                            if (subtotal_loop == 0) {
//                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
                                subtotal_loop =
                                    mAddItemDataUpdate.get(i).amount.replace("$", "")
                                        .replace(",", "").toInt()
                            } else {
                                subtotal_loop =
                                    subtotal_loop + mAddItemDataUpdate.get(i).amount.replace(
                                        "$",
                                        ""
                                    ).replace(",", "").toInt()
                            }

                            val formatter = DecimalFormat("#,###,###")

                            binding.mTvSubTotal.setText("$ " + formatter.format(subtotal_loop))



                            tax_loop = subtotal_loop * taxRate / 100
                            total_loop = subtotal_loop + tax_loop

                            if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                                binding.mTvTax.setText("$ " + formatter.format(tax_loop))
                            } else {
                                binding.mTvTax.setText("$ " + "0")
                            }
//
                            if (mAddItemDataUpdate.get(i).tax.equals("1")) {

                                binding.mTvTotals.setText("$ " + formatter.format(total_loop.toInt()))
//                                mTvTotals.setText("$ " +  total_loop.toString())
                            } else {
                                binding.mTvTotals.setText("$ " + formatter.format(subtotal_loop.toInt()))
//                                mTvTotals.setText("$ " +  subtotal_loop.toString())
                            }
                        }
                        mProSelectedItemsAdapter.notifyDataSetChanged()
                        setTotals();
                    }
                }
            }
        }
    }

    fun setTotals() {
        var subTotal = 0
        var total_sub = 0

        var totalTax = 0
        var item_tax = 0
        var item_total = 0
        for (i in mAddItemDataUpdate.indices) {
            var item_sub = 0;
            if (subTotal == 0) {
                var quantity = 1
//                                subtotal = java.lang.String.valueOf(mAddItemDataUpdate.get(i).getId())
                item_sub =
                    mAddItemDataUpdate.get(i).amount.replace("$", "").replace(",", "").toInt()
                quantity =
                    mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
                item_sub = item_sub * quantity;
            } else {
                var quantity = 1
                item_sub = mAddItemDataUpdate.get(i).amount.replace("$", "")
                    .replace(",", "").toInt()
                quantity =
                    mAddItemDataUpdate.get(i).quantity.replace("$", "").replace(",", "").toInt()
                item_sub = item_sub * quantity;
            }

            if (mAddItemDataUpdate.get(i).tax.equals("1")) {
                item_tax = item_sub * taxRate / 100
                totalTax += item_tax;
            } else {
                item_tax = 0
            }
            item_total += item_sub + item_tax;
            subTotal += item_sub
        }
        binding.mTvSubTotal.text = subTotal.toString()
        binding.mTvTax.text = totalTax.toString()
        binding.mTvTotals.text = item_total.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
    }

    override fun onRemove(item: Any, position: Int) {
        if (mAddItemDataUpdate.isNotEmpty()) {
            mAddItemDataUpdate.removeAt(position)
            mProSelectedItemsAdapter.notifyItemRemoved(position)
            mProSelectedItemsAdapter.notifyItemRangeChanged(position, mAddItemDataUpdate.size)
            setTotals()
        }
    }
    override fun onDocClick(item: Any, position: Int) {
        if (!selectedDocList.contains(item.toString())) {
            selectedDocList.add(item.toString())
        } else return
        mBuilder.hide()
        binding.documentsRV.visibility = View.VISIBLE
        binding.documentsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.documentsRV.adapter = docsAdapter
        docsAdapter.notifyDataSetChanged()
        binding.uploadDoc.visibility = View.GONE
        mDocFile = null
    }

    override fun onCrossClick(item: Any, position: Int) {
        selectedDocList.removeAt(position);
        if (selectedDocList.isEmpty()) {
            binding.uploadDoc.visibility = View.VISIBLE
        }
        docsAdapter.notifyDataSetChanged()
    }
    override fun onSingleItemClick(item: Any, position: Int) {
    }
    override fun invoke() {

    }

}