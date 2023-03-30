package com.tradesk.activity.leadModule

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.AdditionalImageLeadDetail
import com.tradesk.Model.LeadDetailModel
import com.tradesk.Model.Sale
import com.tradesk.R
import com.tradesk.activity.galleryModule.GallaryActivity
import com.tradesk.activity.jobModule.JobDetailActivity
import com.tradesk.activity.jobModule.notes.LeadNotesActivity
import com.tradesk.activity.proposalModule.ProposalsActivity
import com.tradesk.activity.salesPerson.AllSalesPersonActivity
import com.tradesk.activity.salesPerson.SalesPersonDetailActivity
import com.tradesk.adapter.JobContractorUsersAdapter
import com.tradesk.databinding.ActivityLeadDetailBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.LeadDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class LeadDetailActivity : AppCompatActivity(), SingleItemCLickListener {
    var status_api = ""
    var converted_job = ""
    var client_id = ""
    var client_name = ""
    var client_email = ""
    var address = ""
    var email_idclient = ""
    var phonenumberclient = ""
    var alertDialog: AlertDialog? = null
    var latitudeJob = ""
    var longitudeJob = ""
    var sales = ""
    var myImageUri = ""
    var mFile: File? = null
    val mList = mutableListOf<AdditionalImageLeadDetail>()
    val mListSubUsers = mutableListOf<Sale>()
    val mJobContractorUsersAdapter by lazy { JobContractorUsersAdapter(this, this, mListSubUsers) }
    lateinit var binding:ActivityLeadDetailBinding
    lateinit var viewModel:LeadDetailViewModel
    lateinit var leadDetailModel: LeadDetailModel
    val mInfoBuilderTwo: Dialog by lazy { Dialog(this) }
    @Inject
    lateinit var mPrefs: PreferenceHelper
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_lead_detail)
        viewModel=ViewModelProvider(this).get(LeadDetailViewModel::class.java)

        binding.mIvBack.setOnClickListener { finish() }
        binding.rvContractUsers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvContractUsers.adapter = mJobContractorUsersAdapter
        binding.mIvAddImage.setOnClickListener {
//            if (permissionFile.checkLocStorgePermission(this)) {
//                showImagePop()
//            }
            startActivity(
                Intent(this, GallaryActivity::class.java)
                    .putExtra("id", intent.getStringExtra("id").toString())
                    .putExtra("additionalimages", "yes")
            )
        }
        binding.mIvAddImageIcon.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
            binding.mIvAddUsers.visibility = View.VISIBLE
        } else {
            binding.mIvAddUsers.visibility = View.GONE
        }
        binding.mIvAddUsers.setOnClickListener {
            val i = Intent(this, AllSalesPersonActivity::class.java)
            i.putExtra("fromjob", "Job")
            i.putExtra("job_id", intent.getStringExtra("id"))
            startActivityForResult(i, 2222)
        }
        binding.imageView13.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",binding.mTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + binding.mTvClientName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        binding.textView13.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", binding.mTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + binding.mTvClientName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        binding.mTvEmail.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", binding.mTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + binding.mTvClientName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        binding.mIvEmailSendButton.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", binding.mTvEmail.text.toString(), null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + binding.mTvClientName.text.toString())
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
        binding.mIvView.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_leads, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogView)

            dialog.findViewById<ImageView>(R.id.mIvNotes)!!.setOnClickListener {
                Log.e("Job id",intent.getStringExtra("id").toString())
                startActivity(
                    Intent(
                        this,
                        LeadNotesActivity::class.java
                    ).putExtra("id", intent.getStringExtra("id").toString())
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvInvoices)!!.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.LeadDelete(leadDetailModel.data.leadsData._id);
                }

            }
            dialog.findViewById<ImageView>(R.id.mIvExpense)!!.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")
                var shareMessage = """
                    ${
                    "\n" + binding.mTvTitle.text.toString() + "\n" +
                            binding.mTvClientName.text.toString() + "\n" +
                            binding.mTvEmail.text.toString() + "\n" +
                            binding.mTvPhone.text.toString() + "\n" +
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
            dialog.findViewById<ImageView>(R.id.mIvProposals)!!
                .setOnClickListener {
                    startActivity(
                        Intent(this, ProposalsActivity::class.java)
                            .putExtra("title", "Proposals")
                            .putExtra("job_id", intent.getStringExtra("id"))
                            .putExtra("client_name", client_name)
                            .putExtra("client_email", email_idclient)
                            .putExtra("client_id", client_id)
                    )
                    dialog.dismiss()
                }
            dialog.findViewById<ImageView>(R.id.mIvProposal)!!.setOnClickListener {
                startActivity(
                    Intent(this, ProposalsActivity::class.java)
                        .putExtra("title", "Proposals")
                        .putExtra("job_id", intent.getStringExtra("id"))
                        .putExtra("client_name", client_name)
                        .putExtra("client_email", email_idclient)
                        .putExtra("client_id", client_id)
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvDocuments)!!
                .setOnClickListener { toast("notes") }
            dialog.findViewById<ImageView>(R.id.mIvGallary)!!.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        GallaryActivity::class.java
                    ).putExtra("id", intent.getStringExtra("id").toString())
                        .putExtra("additionalimages", "yes")
                )
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvCalendars)!!
                .setOnClickListener { toast("notes") }
            dialog.findViewById<ImageView>(R.id.mIvCalendar)!!.setOnClickListener { toast("notes") }
            dialog.findViewById<ImageView>(R.id.mIvEndJob)!!.setOnClickListener {
                if (Constants.isInternetConnected(this)) {
                    status_api = "Completed"
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.convertLeads(intent.getStringExtra("id").toString(), "job", "completed", "false"
                        )
                    }
                }
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.mIvCancel)!!.setOnClickListener {
                if (Constants.isInternetConnected(this)) {
                    status_api = "Cancel Job"
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.convertLeads(
                            intent.getStringExtra("id").toString(),
                            "job",
                            "cancel",
                            "false"
                        )
                    }
                }
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.mIvCallIcon.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.mTvPhone.text.toString())
            startActivity(dialIntent)
        }

        binding.mIvEmailIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:" + binding.mTvPhone.text.toString())
            intent.putExtra("sms_body", "")
            startActivity(intent)
        }
        binding.mTvPhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.mTvPhone.text.toString())
            startActivity(dialIntent)
        }
        binding.imageView12.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.mTvPhone.text.toString())
            startActivity(dialIntent)
        }
        binding.textView12.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.mTvPhone.text.toString())
            startActivity(dialIntent)
        }
        binding.mIvMenus.setOnClickListener {
//            showDotsMenu(mIvMenus, 1)
            val builder = GsonBuilder()
            val gson = builder.create()
            var string=gson.toJson(leadDetailModel);
            startActivity(Intent(this, AddLeadActivity::class.java).putExtra("edit",true).putExtra("lead",string));
        }
        binding.tvStatus.setOnClickListener {
            if (converted_job == "true")
            {
                startActivity(
                    Intent(this, JobDetailActivity::class.java).putExtra(
                        "id",
                        intent.getStringExtra("id").toString()
                    )
                )
            } else {
                showLogoutMenu(binding.tvStatus, 1)
            }
        }
//        binding.mIvEmail.setOnClickListener { showInformationPop(4) }
        binding.mIvNotes.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LeadNotesActivity::class.java
                ).putExtra("id", intent.getStringExtra("id").toString())
            )
        }

        binding.mIvNavigates.setOnClickListener {
            val lat = latitudeJob.toDouble()
            val long = longitudeJob.toDouble()
            val uri = "geo:${lat},${long}?q=${lat},${long}"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

//        binding.tv_view_gallery.setOnClickListener {
//            startActivity(
//                Intent(this, GallaryActivity::class.java)
//                    .putExtra("id", intent.getStringExtra("id").toString())
//                    .putExtra("additionalimages", "yes")
//            )
//        }

        if (Constants.isInternetConnected(this)) {
            Log.e("Leads Id",intent.getStringExtra("id").toString())
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getLeadDetail(intent.getStringExtra("id").toString())
            }
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
        viewModel.responseSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Lead added to junk successfully")
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
        viewModel.responseLeadConvertSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    if (status_api.equals("Convert to Job")) {
                        finish()
                    } else if (status_api.equals("follow_up")) {
                        toast("Lead converted to Follow Up")
                    } else if (status_api.equals("sale")) {
                        toast("Lead converted to Sale")
                    } else {
                        toast(it.data!!.message)
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
        viewModel.responseAddReminderSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message)
                    status_api = "follow_up"
                    if (status_api.equals("follow_up")) {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.convertLeads(intent.getStringExtra("id").toString(), "lead", "follow_up", "false")
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
        binding.mTvAddress.text = it.data.leadsData.address.street

        if (it.data.leadsData.createdAt.isNotEmpty())
        {
            binding.mTvDate.text = Constants.convertDateFormatWithTime(it.data.leadsData.startDate.toString())
        }
        else {
            binding.mTvDate.text = "N/A"
        }

        if (it.data.leadsData.description.trim().isNotEmpty()) {
            binding.mTvDesc.text = it.data.leadsData.description
        } else {
            binding.mTvDesc.text = "N/A"
        }

        if(leadDetailModel.data.leadsData.source.trim().isNotEmpty()){
            binding.mTvSource.text=(leadDetailModel.data.leadsData.source.trim())
        }
        else {
            binding.mTvSource.text = "N/A"
        }

        binding.mIvMapImage.loadWallImage("https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x500&maptype=roadmap&markers=color:red%7Clabel:S%7C" + it.data.leadsData.address.location.coordinates[0] + "," + it.data.leadsData.address.location.coordinates[1] + "&key=AIzaSyAweeG9yxU6nQulKdyN6nIIBtSPak1slfo")
//        mTvTitleTop.setText(it.data.leadsData.project_name.capitalize())

        binding.mTvTitleTop.text = "Lead"
        binding.mTvTitle.text = it.data.leadsData.project_name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        try{
            binding.mTvClientName.text = it.data.leadsData.client[0].name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            binding.mTvPhone.text = Constants.insertString(it.data.leadsData.client[0].phone_no, "", 0)
            binding.mTvPhone.text = Constants.insertString(binding.mTvPhone.text.toString(), ")", 2)
            binding.mTvPhone.text = Constants.insertString(binding.mTvPhone.text.toString(), " ", 3)
            binding.mTvPhone.text = Constants.insertString(binding.mTvPhone.text.toString(), "-", 7)
            binding.mTvPhone.text = "+1 (" + binding.mTvPhone.text.toString()

            binding.mTvEmail.text = it.data.leadsData.client[0].email
            client_id = it.data.leadsData.client[0]._id
            client_name = it.data.leadsData.client[0].name
            email_idclient = it.data.leadsData.client[0].email
        }
        catch(e:Exception)
        {

        }
        phonenumberclient = binding.mTvPhone.text.toString()
        //  mTvAddress.text = it.data.leadsData.address.street + ", " + it.data.leadsData.address.city
        address = it.data.leadsData.address.street //+ ", " + it.data.leadsData.address.city + ", " + it.data.leadsData.address.state + ", " + it.data.leadsData.address.zipcode
//       if (it.data.leadsData.start_date!=null){
//           mTvDate.setText(convertDateFormat(it.data.leadsData.start_date.toString()))
//       }else{
//           mTvDate.setText("N/A")
//       }
        latitudeJob = it.data.leadsData.address.location.coordinates[0].toString()
        longitudeJob = it.data.leadsData.address.location.coordinates[1].toString()

        if (it.data.leadsData.converted_to_job == true)
        {
            converted_job = "true"
            binding.tvStatus.text = "Job"
            binding.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrowtoforward, 0)
        }
        else {
            if (it.data.leadsData.status.equals("follow_up")) {
                binding.tvStatus.text = "Follow Up"
            } else {
                binding.tvStatus.text = it.data.leadsData.status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            }
            binding.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
        }

        status_api = it.data.leadsData.status
        mList.clear()

        if (!it.data.leadsData.additional_images.isNullOrEmpty()) {
            mList.addAll(it.data.leadsData.additional_images.filter { it.status!!.equals("image") })
        }
        Collections.reverse(mList)
//        binding.viewPager.adapter = ImageVPagerAdapter(this,mList, this)
//        dots.attachViewPager(viewPager)

        mListSubUsers.clear()
        mListSubUsers.addAll(it.data.leadsData.sales)
        mJobContractorUsersAdapter.notifyDataSetChanged()

        binding.inner.visibility = View.VISIBLE
    }

    fun showImagePop() {
//        Pix.start(this@LeadsActivity, options)
    }

    fun showLogoutMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        if (status_api.equals("pending")) {
            popup.menu.add("Pending") // menus items
            popup.menu.add("Follow Up") // menus items
            popup.menu.add("Sale")
        } else if (status_api.equals("follow_up")) {
            popup.menu.add("Pending") // menus items
            popup.menu.add("Follow Up") // menus items
            popup.menu.add("Sale")
        } else if (status_api.equals("sale")) {
            popup.menu.add("Pending") // menus items
            popup.menu.add("Follow Up")
            popup.menu.add("Convert to Job")
        }
        popup.setOnMenuItemClickListener {
            binding.tvStatus.text = it!!.title
            if (it.title!!.equals("Follow Up")) {
                Toast.makeText(this, "From", Toast.LENGTH_SHORT).show()
                showFollowUpPop(0)
            }
            else if (it.title!!.equals("Pending"))
            {
                status_api = binding.tvStatus.text.toString().trim().lowercase()
                Constants.showLoading(this)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.convertLeads(
                        intent.getStringExtra("id").toString(),
                        "lead",
                        "pending",
                        "false"
                    )
                }
            } else if (it.title!!.equals("Sale")) {
                status_api = binding.tvStatus.text.toString().trim().lowercase()
                Constants.showLoading(this)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.convertLeads(intent.getStringExtra("id").toString(), "lead", "sale", "false")

                }
            } else if (it.title!!.equals("Convert to Job")) {
                status_api = "Convert to Job"
                AllinOneDialog(getString(R.string.app_name),
                    "Would you like to convert this lead to a new job?",
                    btnLeft = "No",
                    btnRight = "Yes",
                    onLeftClick = {},
                    onRightClick = {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.convertLeads(intent.getStringExtra("id").toString(), "job", "pending", "true")
                        }
                    })
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }
    fun showFollowUpPop(id: Int) {
        mInfoBuilderTwo.setContentView(R.layout.popup_followup)
        val displayMetrics = DisplayMetrics()
        mInfoBuilderTwo.window!!.attributes.windowAnimations = R.style.DialogAnimationNew
        mInfoBuilderTwo.window!!.setGravity(Gravity.BOTTOM)
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        mInfoBuilderTwo.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
            (displayMetrics.widthPixels * 0.99).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mInfoBuilderTwo.findViewById<TextView>(R.id.mTvCallReminder).setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                this@LeadDetailActivity,
                { view, year, month, dayOfMonth ->
                    val _year = year.toString()
                    val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                    val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    val _pickedDate = "$_year-$_month-$_date"
                    Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12

                    val c2 = Calendar.getInstance()
                    var mHour = c2[Calendar.HOUR_OF_DAY]
                    var mMinute = c2[Calendar.MINUTE]

                    // Launch Time Picker Dialog

                    // Launch Time Picker Dialog
                    val timePickerDialog = TimePickerDialog(
                        this,
                        { view, hourOfDay, minute ->
                            mHour = hourOfDay
                            mMinute = minute
                            var _pickedTime = ""
                            if (hourOfDay.toString().length == 1 && minute.toString().length == 1) {
                                _pickedTime = "0" + hourOfDay.toString() + ":" + "0" + minute.toString()
                            } else if (hourOfDay.toString().length == 1) {
                                _pickedTime = "0" + hourOfDay.toString() + ":" + minute.toString()
                            } else if (minute.toString().length == 1) {
                                _pickedTime = hourOfDay.toString() + ":" + "0" + minute.toString()
                            } else {
                                _pickedTime = hourOfDay.toString() + ":" + minute.toString()
                            }
                            Log.e(
                                "PickedTime: ",
                                "Date: $_pickedDate" + _pickedTime
                            ) //2019-02-12
//                            et_show_date_time.setText(date_time.toString() + " " + hourOfDay + ":" + minute)

                            val tz = TimeZone.getDefault()
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch{
                                viewModel.addreminder(intent.getStringExtra("id").toString(), client_id, "phone",
                                    "lead", _pickedDate + " " + _pickedTime + ":00", tz.id)
                                 }
                            mInfoBuilderTwo.dismiss()

                        },
                        mHour,
                        if (mMinute == 60) 0 + 2 else if (mMinute == 59) 0 + 1 else mMinute + 2,
                        false
                    )
                    timePickerDialog.show()

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dialog.show()

        }

        mInfoBuilderTwo.findViewById<TextView>(R.id.mTvAppointReminder).setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                this@LeadDetailActivity,
                { view, year, month, dayOfMonth ->
                    val _year = year.toString()
                    val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                    val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    val _pickedDate = "$_year-$_month-$_date"
                    Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12

                    val c2 = Calendar.getInstance()
                    var mHour = c2[Calendar.HOUR_OF_DAY]
                    var mMinute = c2[Calendar.MINUTE]

                    // Launch Time Picker Dialog

                    // Launch Time Picker Dialog
                    val timePickerDialog = TimePickerDialog(
                        this,
                        { view, hourOfDay, minute ->
                            mHour = hourOfDay
                            mMinute = minute
                            var _pickedTime = ""

                            if (hourOfDay.toString().length == 1 && minute.toString().length == 1) {
                                _pickedTime =
                                    "0" + hourOfDay.toString() + ":" + "0" + minute.toString()
                            } else if (hourOfDay.toString().length == 1) {
                                _pickedTime = "0" + hourOfDay.toString() + ":" + minute.toString()
                            } else if (minute.toString().length == 1) {
                                _pickedTime = hourOfDay.toString() + ":" + "0" + minute.toString()
                            } else {
                                _pickedTime = hourOfDay.toString() + ":" + minute.toString()
                            }
                            Log.e(
                                "PickedTime: ",
                                "Date: $_pickedDate" + _pickedTime
                            ) //2019-02-12
//                            et_show_date_time.setText(date_time.toString() + " " + hourOfDay + ":" + minute)
                            val tz = TimeZone.getDefault()
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch{
                                viewModel.addreminder(
                                    intent.getStringExtra("id").toString(),
                                    client_id,
                                    "appointment",
                                    "lead",
                                    _pickedDate + " " + _pickedTime + ":00", tz.id
                                )
                            }
                            mInfoBuilderTwo.dismiss()
                        },
                        mHour,
                        if (mMinute == 60) 0 + 2 else if (mMinute == 59) 0 + 1 else mMinute + 2,
                        false
                    )
                    timePickerDialog.show()

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dialog.show()

        }
        mInfoBuilderTwo.findViewById<TextView>(R.id.tvDone)
            .setOnClickListener {
                mInfoBuilderTwo.dismiss()
            }
        mInfoBuilderTwo.show()
    }
    override fun onSingleItemClick(item: Any, position: Int) {
        startActivity(
            Intent(this, SalesPersonDetailActivity::class.java)
                .putExtra("title","User Profile")
                .putExtra("from", "LeadsActivity")
                .putExtra("id", mListSubUsers[position]._id)
                .putExtra("type", mListSubUsers[position].type)
                .putExtra("name", mListSubUsers[position].name)
                .putExtra("email", mListSubUsers[position].email)
                .putExtra("phone", mListSubUsers[position].phone_no)
                .putExtra("trade", mListSubUsers[position].trade)
                .putExtra("address", if (mListSubUsers[position].address.street.isNotEmpty()) {
                    mListSubUsers[position].address.street + ", " + mListSubUsers[position].address.city + ", " + mListSubUsers[position].address.state + ", " + mListSubUsers[position].address.zipcode
                } else {
                    mListSubUsers[position].address.street + ", " + mListSubUsers[position].address.city + ", " + mListSubUsers[position].address.state + ", " + mListSubUsers[position].address.zipcode
                }
                )
                .putExtra("image", mListSubUsers[position].image)
                .putExtra("trade", mListSubUsers[position].trade)
        )
    }
    override fun onResume() {
        super.onResume()
        if (Constants.isInternetConnected(this)) {
            Log.e("Leads Id",intent.getStringExtra("id").toString())
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getLeadDetail(intent.getStringExtra("id").toString())
            }
        }
    }
}