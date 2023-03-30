package com.tradesk.activity.jobModule

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.socialgalaxyApp.util.extension.loadWallImage
import com.theartofdev.edmodo.cropper.CropImage
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.AdditionalImageLeadDetail
import com.tradesk.Model.LeadDetailModel
import com.tradesk.Model.Sale
import com.tradesk.R
import com.tradesk.activity.proposalModule.ProposalsActivity
import com.tradesk.activity.documentModule.DocumentsSubActivity
import com.tradesk.activity.galleryModule.SubGallaryActivity
import com.tradesk.activity.jobModule.Expense.MainExpenseActivity
import com.tradesk.activity.jobModule.notes.LeadNotesActivity
import com.tradesk.activity.jobModule.task.LeadTasksActivity
import com.tradesk.activity.salesPerson.AllSalesPersonActivity
import com.tradesk.activity.salesPerson.SalesPersonDetailActivity
import com.tradesk.adapter.ImageVPagerAdapter
import com.tradesk.adapter.JobContractorUsersAdapter
import com.tradesk.databinding.ActivityJobDetailBinding
import com.tradesk.filemanager.checkStoragePermission
import com.tradesk.filemanager.requestStoragePermission
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.convertDateFormatWithTime
import com.tradesk.util.Constants.insertString
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.file.FilePath
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class JobDetailActivity : AppCompatActivity() , SingleItemCLickListener , () -> Unit{
    var client_name = ""
    var client_id = ""
    var receiver_id = ""
    var sales_id = ""
    var client_email = ""
    var myImageUri = ""
    var status_api = ""
    var sales = ""
    var address = ""
    var latitudeJob = ""
    var longitudeJob = ""
    var converted_job = "false"
    var mFile: File? = null
    val mList = mutableListOf<AdditionalImageLeadDetail>()
    val mListSubUsers = mutableListOf<Sale>()
    val mJobContractorUsersAdapter by lazy { JobContractorUsersAdapter(this, this, mListSubUsers) }
    lateinit var leadDetailModel: LeadDetailModel;
    var alertDialog: AlertDialog? = null
    var jobDcment_id:String=""
    lateinit var binding:ActivityJobDetailBinding
    lateinit var viewModel:JobsViewModel
    @Inject
    lateinit var mPrefs: PreferenceHelper
    @Inject
    lateinit var permissionFile: PermissionFile
    private var hasPermission = false
    lateinit var camera_image_uri:Uri
    val mBuilder: Dialog by lazy { Dialog(this@JobDetailActivity) }
    val options by lazy {
        Options.init()
            .setRequestCode(1010) //Request code for activity results
            .setCount(3) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setPreSelectedUrls(arrayListOf()) //Pre selected Image Urls
            .setSpanCount(4) //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.All) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(30) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images") //Custom Path For media Storage

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_job_detail)
        viewModel= ViewModelProvider(this).get(JobsViewModel::class.java)

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            Log.e("Leads Id",intent.getStringExtra("id").toString())
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getLeadDetail(intent.getStringExtra("id").toString())
            }
        }
        binding.rvContractUsers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvContractUsers.adapter = mJobContractorUsersAdapter
        binding.mIvBack.setOnClickListener { finish() }
        binding.mIvNavigate.setOnClickListener {
            val lat = latitudeJob.toDouble()
            val long = longitudeJob.toDouble()
            val uri = "geo:${lat},${long}?q=${lat},${long}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }
        binding.mIvAddImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this))
            {
                startActivity(Intent(this, SubGallaryActivity::class.java)
                    .putExtra("job_id", intent.getStringExtra("id").toString())
                    .putExtra("title", "Images")
                    .putExtra("additionalimages", "yes")
                )
            }
        }
        binding.mIvAddImageIcon.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                hasPermission = checkStoragePermission(this)
                if (hasPermission)
                {
                    showImagePop()
                } else {
                    requestStoragePermission(this)
                }
            }
        }
        binding.tvStatuses.setOnClickListener { showLogoutMenu(binding.tvStatuses, 1) }
//        binding.mIvMenus.setOnClickListener { showSideMenu(mIvMenus, 1) }
//        binding.mIvEmail.setOnClickListener { showInformationPop(4) }
        binding.mIvNotes.setOnClickListener {
            startActivity(
                Intent(this, LeadNotesActivity::class.java).putExtra("id", intent.getStringExtra("id").toString())
            )
        }
        binding.mIvChat.setOnClickListener {
//            startActivity(Intent(this,MainExpenseActivity::class.java)
//                    .putExtra("job_id", intent.getStringExtra("id")))
        }
        binding.tvViewGallery.setOnClickListener {
//            startActivity(
//                Intent(this, GallaryActivity::class.java)
//                    .putExtra("id", intent.getStringExtra("id").toString())
//                    .putExtra("additionalimages", "yes")
//            )
        }
        binding.mIvView.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogView)
            dialog.findViewById<ImageView>(R.id.mIvShare)!!.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nJob Detail")
                var shareMessage = """
                    ${
                    "\n" + "Job Detail" + "\n" + binding.etTvTitle.text.toString() + "\n" +
                            binding.etTvName.text.toString() + "\n" +
                            binding.etTvEmail.text.toString() + "\n" +
                            binding.etTvPhone.text.toString() + "\n" +
                            address + "\n"
                }
               
                    """.trimIndent()
                shareMessage = """
                    $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}
                    
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

                startActivity(Intent.createChooser(shareIntent, "choose one"))
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvNote)!!.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        LeadNotesActivity::class.java
                    ).putExtra("id", intent.getStringExtra("id").toString())
                )
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvTask)!!.setOnClickListener {
                startActivity(Intent(this, LeadTasksActivity::class.java).putExtra("id", intent.getStringExtra("id").toString()))
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvChat)!!.setOnClickListener {
//                startActivity(
//                    Intent(this, ChatActivity::class.java)
//                        .putExtra("job_id", intent.getStringExtra("id").toString())
//                        .putExtra("receiver_id", receiver_id)
//                        .putExtra("sales_id", sales_id)
//                        .putExtra("job_title", etTvTitle.text.toString())
//
//                )
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvExpensesJob)!!.setOnClickListener {
                startActivity(Intent(this, MainExpenseActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("id"))
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvChatsJobs)!!.setOnClickListener {
                startActivity(
                    Intent(this, ProposalsActivity::class.java)
                        .putExtra("title", "Proposals")
                        .putExtra("job_id", intent.getStringExtra("id").toString())
                        .putExtra("client_name", client_name)
                        .putExtra("client_id", client_id)
                        .putExtra("client_email", client_email)

                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvInovicesJobs)!!.setOnClickListener {
                startActivity(
                    Intent(this, ProposalsActivity::class.java)
                        .putExtra("title", "Invoices")
                        .putExtra("job_id", intent.getStringExtra("id").toString())
                        .putExtra("client_name", client_name)
                        .putExtra("client_id", client_id)
                        .putExtra("client_email", client_email)

                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvTimesheet)!!.setOnClickListener {
                startActivity(
                    Intent(this, JobTimeSheetActivity::class.java).putExtra("id", intent.getStringExtra("id"))
                )
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.mIvPermitsJobs)!!.setOnClickListener {
                Log.e("pass id",intent.getStringExtra("id").toString())
                Log.e("job id", jobDcment_id)
                startActivity(
                    Intent(this, DocumentsSubActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("id").toString())
                        .putExtra("id", intent.getStringExtra("id").toString())
                        .putExtra("jobDocument_id", jobDcment_id) //Orginal job id
                        .putExtra("project_name", "Documents")
                        .putExtra("permits", "yes")
                        .putExtra("additionaljob", "yes")
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvGallery)!!.setOnClickListener {
                if (permissionFile.checkLocStorgePermission(this))
                {
                    startActivity(Intent(this, SubGallaryActivity::class.java)
//                    startActivity(Intent(this, GallaryActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("id").toString())
                        .putExtra("title", "Images")
                        .putExtra("additionalimages", "yes")
                    )
                }
            }
            dialog.findViewById<ImageView>(R.id.mIvCancelJobs)!!.setOnClickListener {
                if (isInternetConnected(this)) {
                    status_api = "Cancel Job"
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.convertJobs(intent.getStringExtra("id").toString(), "job", "cancel", converted_job)
                    }
                }
                dialog.dismiss()
            }
            dialog.show()
        }
        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
            binding.mIvAddUsers.visibility = View.VISIBLE
        }
        else {
            binding.mIvAddUsers.visibility = View.GONE
        }
        binding.mIvAddUsers.setOnClickListener {
            val i = Intent(this, AllSalesPersonActivity::class.java)
            i.putExtra("fromjob", "Job")
            i.putExtra("job_id", intent.getStringExtra("id"))
            startActivityForResult(i, 2222)
        }
        binding.imageView2.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", binding.etTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to job")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + binding.etTvName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        binding.mIvEmailSendButton.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", binding.etTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to job")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + binding.etTvName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        binding.textView2.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", binding.etTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to job")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + binding.etTvName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        binding.etTvEmail.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", binding.etTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to job")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + binding.etTvName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        binding.mIvCallIcon.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.etTvPhone.text.toString())
            startActivity(dialIntent)
        }
        binding.mIvEmailIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:" + binding.etTvPhone.text.toString())
            intent.putExtra("sms_body", "")
            startActivity(intent)
        }
        binding.etTvPhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.etTvPhone.text.toString())
            startActivity(dialIntent)
        }
        binding.imageView4.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.etTvPhone.text.toString())
            startActivity(dialIntent)
        }
        binding.textView3.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.etTvPhone.text.toString())
            startActivity(dialIntent)
        }
        binding.editIcon.setOnClickListener {
//            showDotsMenu(mIvMenus, 1)
            val builder = GsonBuilder()
            val gson = builder.create()
            var string=gson.toJson(leadDetailModel);
            startActivity(Intent(this, AddJobActivity::class.java)
                .putExtra("edit",true)
                .putExtra("lead",string));
        }

        initObserve()
    }

    fun initObserve() {
        viewModel.responseLeadDetailModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    leadDetailModel=it.data!!
                    setLeadDetailData(leadDetailModel)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseJobConvertSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    when (status_api) {
                        "Completed" -> {
                            toast("Job completed successfully")
                            finish()
                        }
                        "Cancel Job" -> {
                            toast("Job cancelled successfully")
                            finish()
                        }
                        else -> {
                            toast("Job status updated successfully")
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
        viewModel.responseSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message)
                    if (isInternetConnected(this)) {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getLeadDetail(intent.getStringExtra("id").toString())
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
        viewModel.responseMoveAdditionalImagesSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message)
                    if (isInternetConnected(this)) {
                        Constants.showLoading(this)
                        binding.mIvMainImageBanner.visibility = View.INVISIBLE
                        binding.mIvAddImageIcon.visibility = View.INVISIBLE
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getLeadDetail(intent.getStringExtra("id").toString())
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
    private fun setLeadDetailData(it: LeadDetailModel) {
        leadDetailModel=it

        binding.etTvAddress.text = it.data.leadsData.address.street
        if (it.data.leadsData.startDate != null) {
            binding.etTvDate.text = convertDateFormatWithTime(it.data.leadsData.startDate.toString())
        } else {
            binding.etTvDate.text = "N/A"
        }

        if (it.data.leadsData.description.isNotEmpty()) {
            binding.mTvDesc.text = it.data.leadsData.description
        } else {
            binding. mTvDesc.text = "N/A"
        }

        if(it.data.leadsData.source.isNotEmpty()){
            binding.mTvSourceJob.text=(leadDetailModel.data.leadsData.source)
        }else{
            binding.mTvSourceJob.text = "N/A"
        }

        mList.clear()
        Log.e("Lead ID",it.data.leadsData._id)
        jobDcment_id=it.data.leadsData._id
        if (!it.data.leadsData.additional_images.isNullOrEmpty()) {
            mList.addAll(it.data.leadsData.additional_images.filter { it.status!! == "image" })
        }

        if (mList.isNotEmpty()) {
            binding.viewPager.visibility = View.VISIBLE
//            dots.visibility = View.GONE
            binding.imageView42.visibility = View.INVISIBLE
            binding.imageView43.visibility = View.INVISIBLE

            binding.mIvMainImageBanner.visibility = View.INVISIBLE
            binding.mIvAddImageIcon.visibility = View.INVISIBLE

        } else {
            binding.viewPager.visibility = View.GONE
//            dots.visibility = View.GONE
            binding.imageView42.visibility = View.GONE
            binding.imageView43.visibility = View.GONE

            binding.mIvMainImageBanner.visibility = View.VISIBLE
            binding.mIvAddImageIcon.visibility = View.VISIBLE
        }
        mList.reverse()
        binding.viewPager.adapter = ImageVPagerAdapter(this,mList, this)
//        dots.attachViewPager(viewPager)


        binding.etIvMapView.loadWallImage("https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x500&maptype=roadmap&markers=color:red%7Clabel:S%7C" + it.data.leadsData.address.location.coordinates[0] + "," + it.data.leadsData.address.location.coordinates[1] + "&key=AIzaSyAweeG9yxU6nQulKdyN6nIIBtSPak1slfo")
//        etTvTopTitle.setText(it.data.leadsData.project_name.capitalize())
        binding.etTvTopTitle.text = "Job"
        binding.etTvTitle.text = it.data.leadsData.project_name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        if(it.data.leadsData!=null && !it.data.leadsData.client.isEmpty())
        {
            binding.etTvName.text = it.data.leadsData.client[0].name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }

            client_name = it.data.leadsData.client[0].name
            client_id = it.data.leadsData.client[0]._id
            client_email = it.data.leadsData.client[0].email
            binding.etTvPhone.text = insertString(it.data.leadsData.client[0].phone_no, "", 0)
            binding.etTvPhone.text = insertString(binding.etTvPhone.text.toString(), ")", 2)
            binding.etTvPhone.text = insertString(binding.etTvPhone.text.toString(), " ", 3)
            binding.etTvPhone.text = insertString(binding.etTvPhone.text.toString(), "-", 7)
            binding.etTvPhone.text = "+1 (" + binding.etTvPhone.text.toString()

            binding.etTvEmail.text = it.data.leadsData.client[0].email
        }

        // etTvAddress.text = it.data.leadsData.address.street + ", " + it.data.leadsData.address.city
        address = it.data.leadsData.address.street //+ ", " + it.data.leadsData.address.city + ", " + it.data.leadsData.address.state + ", " + it.data.leadsData.address.zipcode
        latitudeJob = it.data.leadsData.address.location.coordinates[0].toString()
        longitudeJob = it.data.leadsData.address.location.coordinates[1].toString()

       converted_job = if (it.data.leadsData.converted_to_job) {
            "true"
        } else {
            "false"
        }

        if (it.data.leadsData.status == "completed") {
            binding.tvStatuses.text = "Complete"
        }
        else {
            binding.tvStatuses.text = it.data.leadsData.status.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        }

        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE) == "1") {
            if(!it.data.leadsData.sales.isEmpty())
            {
                receiver_id = it.data.leadsData.sales[0]._id
            }
        }
        else {
            receiver_id = it.data.leadsData.created_by
        }


        if(!it.data.leadsData.sales.isEmpty())
        {
            sales_id = it.data.leadsData.sales[0]._id
        }


        status_api = it.data.leadsData.status
        if (status_api == "cancel") {
            binding.textView7.visibility = View.INVISIBLE
            binding.mIvAddImage.visibility = View.INVISIBLE
            binding.mIvMenus.visibility = View.INVISIBLE
            binding.mIvAddUsers.visibility = View.INVISIBLE
        }
        else {
            binding.textView7.visibility = View.VISIBLE
            binding.mIvMenus.visibility = View.INVISIBLE
            binding.mIvAddImage.visibility = View.VISIBLE
            if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE) == "1") {
                binding.mIvAddUsers.visibility = View.VISIBLE
            } else {
                binding.mIvAddUsers.visibility = View.GONE
            }
        }
        mListSubUsers.clear()
        mListSubUsers.addAll(it.data.leadsData.sales)
        mJobContractorUsersAdapter.notifyDataSetChanged()

        binding.inner.visibility = View.VISIBLE
    }

    override fun onSingleItemClick(item: Any, position: Int) {
        startActivity(
            Intent(this, SalesPersonDetailActivity::class.java)
                .putExtra("title","User Profile")
                .putExtra("from", "jobDetailActivity")
                .putExtra("id", mListSubUsers[position]._id)
                .putExtra("type",mListSubUsers[position].type)
                .putExtra("name", mListSubUsers[position].name)
                .putExtra("email", mListSubUsers[position].email)
                .putExtra("phone", mListSubUsers[position].phone_no)
                .putExtra(
                    "address", if (mListSubUsers[position].address.street.isNotEmpty()) {
                        mListSubUsers[position].address.street + ", " + mListSubUsers[position].address.city + ", " + mListSubUsers[position].address.state + ", " + mListSubUsers[position].address.zipcode
                    } else {
                        mListSubUsers[position].address.street + ", " + mListSubUsers[position].address.city + ", " + mListSubUsers[position].address.state + ", " + mListSubUsers[position].address.zipcode
                    }
                )
                .putExtra("image", mListSubUsers[position].image)
                .putExtra("trade", mListSubUsers[position].trade)
        )
    }
    fun showLogoutMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        if (status_api.equals("pending")) {
            popup.menu.add("Pending") // menus items
            popup.menu.add("Ongoing") // menus items
            popup.menu.add("Complete")
        } else if (status_api.equals("ongoing")) {
            popup.menu.add("Pending")
            popup.menu.add("Ongoing")
            popup.menu.add("Complete")
        } else if (status_api.equals("completed")) {
            popup.menu.add("Pending")
            popup.menu.add("Ongoing")
            popup.menu.add("Complete")
        }
//        val popup = PopupMenu(anchor.context, anchor)
//        popup.getMenuInflater().inflate(R.menu.jobsstatus_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            binding.tvStatuses.text = it.title
            if (isInternetConnected(this)) {
                status_api = binding.tvStatuses.text.toString().trim().lowercase()

                if (binding.tvStatuses.text.toString().trim().equals("Completed")) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.convertJobs(
                            intent.getStringExtra("id").toString(),
                            "job",
                            "completed", converted_job)
                    }
                } else {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.convertJobs(
                            intent.getStringExtra("id").toString(),
                            "job",
                            binding.tvStatuses.text.toString().trim().lowercase(), converted_job
                        )
                    }
                }
            }
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    override fun invoke() {}
    fun showImagePop() {
//        Pix.start(this@JobDetailActivity, options)
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleCamera).also {
            it.isVisible = !intent.hasExtra("permits")
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
        mBuilder.findViewById<View>(R.id.view11).isVisible = intent.hasExtra("permits")
        mBuilder.findViewById<TextView>(R.id.titleGallery).also {
            it.isVisible = !intent.hasExtra("permits")
            //       it.isVisible = false
            it.setOnClickListener {
                mBuilder.dismiss()
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                resultLauncher.launch(intent)
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
//            Toast.makeText(this,"Gallery", Toast.LENGTH_SHORT).show()
            var uriList: ArrayList<Uri> = ArrayList<Uri>()
            val data: Intent? = result.data!!
            if(data!=null)
            {
                if (data.clipData!= null)
                {
                    val count: Int = data.clipData!!.getItemCount()
                    for (i in 0 until count) {
                        val imageUri: Uri = data.clipData!!.getItemAt(i).getUri()
                        uriList.add(imageUri)
                    }
                    saveCaptureImageResults(uriList!!)
                }
                else
                {
                    if (result.resultCode == Activity.RESULT_OK) {
                        uriList.add(result.data?.data!!)
                        saveCaptureImageResults(uriList!!)
                    }
                }
            }
            else
            {
                Toast.makeText(this,"Null", Toast.LENGTH_SHORT).show()
            }
        }
    }
    var resultLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
//            Toast.makeText(this,"Good",Toast.LENGTH_SHORT).show()
            var uriList: ArrayList<Uri> = ArrayList<Uri>()
            uriList.add(camera_image_uri)
            saveCaptureImageResults(uriList!!)
//            Toast.makeText(this@SubGallaryActivity,"FromCamera", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(applicationContext,"No Select", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCaptureImageResults(uriList: ArrayList<Uri>) = try {
//        Toast.makeText(this,uriList.toString(),Toast.LENGTH_SHORT).show()
        Log.e("MYImages",uriList.toString())

        var parts1: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        for(data in uriList)
        {
            var path= FilePath.getPath(applicationContext,data);
            var image=Constants.getRequestParamImage("image",path)
            parts1.add(image!!)
        }
        hashMapOf<String, RequestBody>().also {
            it.put("_id", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getStringExtra("id").toString()))
            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "image"))
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.addMultipleImgaes(it,parts1)
            }
        }

//        hashMapOf<String, RequestBody>().also {
//            it.put("image\"; filename=\"image.jpg", RequestBody.create(MediaType.parse("image/*"), mFile!!))


//            it.put("_id", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getStringExtra("job_id").toString()))
//            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "image"))
//            Constants.showLoading(this)
//            CoroutineScope(Dispatchers.IO).launch {
//                viewModel.addImgaes(it)
//            }
//        }

    } catch (e: Exception) {
        e.printStackTrace()
    }



    private fun saveCaptureImageResults(data: Uri) = try {
        val file = File(data.path!!)
        mFile = Compressor(this@JobDetailActivity)
            .setMaxHeight(1000).setMaxWidth(1000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        hashMapOf<String, RequestBody>().also {
            it.put(
                "image\"; filename=\"image.jpg",
                RequestBody.create(MediaType.parse("image/*"), mFile!!)
            )
            it.put(
                "_id",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    intent.getStringExtra("id").toString()
                )
            )
            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "image"))
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.addImgaes(it)
            }
        }
    } catch (e: Exception) { }


    private fun saveCaptureImageResults(path: String) = try {
        val file = File(path)
        mFile = Compressor(this@JobDetailActivity)
            .setMaxHeight(4000).setMaxWidth(4000)
            .setQuality(99)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFile(file)

        hashMapOf<String, RequestBody>().also {
            it.put(
                "image\"; filename=\"image.jpg",
                RequestBody.create(MediaType.parse("image/*"), mFile!!)
            )
            it.put(
                "_id",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    intent.getStringExtra("id").toString()
                )
            )
            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "image"))
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.addImgaes(it)
            }
        }

    } catch (e: Exception) {
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1010)
        {
            data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.let {
                if (it.isNotEmpty()) saveCaptureImageResults(it[0])
            }
            Log.e("RESULT", "RESULT")
//                saveCaptureImageResults()
        }
        else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_TAKE_PHOTO) {
            } else if (requestCode == Constants.REQUEST_IMAGE_GET) {

            } else if (requestCode == 2222) {
                if (resultCode == Activity.RESULT_OK) {
                    sales = ""
                    sales = data!!.getStringExtra("result").toString()
                    if (isInternetConnected(this)) {
//                        presenter.getLeadDetail(intent.getStringExtra("id").toString())
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    override fun onResume() {
        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            Log.e("Leads Id",intent.getStringExtra("id").toString())
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getLeadDetail(intent.getStringExtra("id").toString())
            }
        }
        super.onResume()

    }
}