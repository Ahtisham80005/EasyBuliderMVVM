package com.tradesk.activity.jobModule

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.JobLogTimeNewTimeSheet
import com.tradesk.Model.TimeModelNewUPdate
import com.tradesk.R
import com.tradesk.activity.timesheetModule.TimesheetActivity
import com.tradesk.adapter.TimeSheetDaysListAdapter
import com.tradesk.databinding.ActivityJobTimeSheetBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class JobTimeSheetActivity : AppCompatActivity(), SingleListCLickListener {
    var job_id = ""
    var mHomeImage = true
    var CheckVersion = true

    var addresss = ""
    var city = ""
    var state = ""
    var country = ""
    var postalCode = ""
    var knownName = ""

    var status = ""
    var api_status = ""
    var enddate = ""
    var enddate2 = ""
    var server_save_date = ""

    var total_ring_minutes = ""
    var clockoutday = ""


    val mList = mutableListOf<JobLogTimeNewTimeSheet>()
    val mTimeSheetAdapter by lazy { TimeSheetDaysListAdapter(this,this,mList) }
    lateinit var binding:ActivityJobTimeSheetBinding
    lateinit var viewModel: JobsViewModel
    @Inject
    lateinit var mPrefs: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_job_time_sheet)
        viewModel= ViewModelProvider(this).get(JobsViewModel::class.java)
        val tz = TimeZone.getDefault()

        binding.rvTimeSheet.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvTimeSheet.adapter = mTimeSheetAdapter

        binding.mIvBack.setOnClickListener { finish() }

        if (!mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
            binding.tvStatuses.visibility= View.GONE
            if (isInternetConnected(this)) {
                Constants.showLoading(this)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.timesheetlist("1", "10", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
                }
            }
        }else{
            binding.tvStatuses.visibility= View.GONE
            if (isInternetConnected(this)) {
                Constants.showLoading(this)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.timesheetlist("1", "10", "")
                }
            }
        }
        if (isInternetConnected(this)){
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.jobsdetailtimesheet(intent.getStringExtra("id").toString())
            }
        }
        binding.tvClickOn.setOnClickListener {
            if (status.equals("clocked_out")) {

                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(this, Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                    mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
//                    ("34.0628345").toDouble(), ("-118.3018231").toDouble(),
                    1
                )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5


//                      addresss = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                if (!addresses[0].featureName.isNotEmpty()) {
                    addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
                } else {
                    if (addresses[0].thoroughfare!==null) {
                        addresss = addresses[0].thoroughfare
                    }else{
                        addresss = addresses[0].getLocality()
                    }
                }
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                city = addresses[0].getLocality()
                state = addresses[0].getAdminArea()
                country = addresses[0].getCountryName()
//                postalCode = addresses[0].getPostalCode()

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                enddate = sdf.format(Date())

                if (checkedDate(server_save_date).equals(checkedDate(enddate))) {
                    if (isInternetConnected(this)) {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
//                            presenter.outTime(
//                                job_id,
//                                "clocked_out",
//                                server_save_date,
//                                enddate,
//                                tz.id,
//                                addresss,
//                                city,
//                                state,
//                                postalCode,
//                                mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
//                                    PreferenceConstants.LNG
//                                )
//                            )
                        }

                    }
                } else {
                    clockoutday = "1"
                    if (isInternetConnected(this)) {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
//                            presenter.outTime(
//                                job_id,
//                                "clocked_out",
//                                server_save_date,
//                                checkedDate(server_save_date) + " 23:59:58",
//                                tz.id,
//                                addresss,
//                                city,
//                                state,
//                                postalCode,
//                                mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
//                                    PreferenceConstants.LNG
//                                )
//                            )
                        }
                    }
                }
            } else {
                job_id = intent!!.getStringExtra("id").toString()

                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(this, Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                    mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
                    1
                )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                if (!addresses[0].featureName.isNotEmpty()) {
                    addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
                } else {
                    if (addresses[0].thoroughfare!==null) {
                        addresss = addresses[0].thoroughfare
                    }else{
                        addresss = addresses[0].getLocality()
                    }
                }
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                city = addresses[0].getLocality()
                state = addresses[0].getAdminArea()
                country = addresses[0].getCountryName()
//                postalCode = addresses[0].getPostalCode()
//                      knownName = addresses[0].getFeatureName()

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                enddate = sdf.format(Date())
                val tz = TimeZone.getDefault()
                if (isInternetConnected(this)) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.addtime(job_id,"clocked_in",
                            enddate, tz.id, addresss, city, state, postalCode,
                            mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                                PreferenceConstants.LNG))
                    }

                }

            }

        }
    }

    fun initObserve() {
        viewModel.responseTimesheetList.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    onDetail(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading -> {
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseJobDetailTimesheet.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    var mins = it.data!!.data.job_details[0].JobSheettotalTime.toString().substringAfter(":")
                    var sec = mins.toString().substringAfter(":")
                    binding.mTvHrTime.setText(it.data!!.data.job_details[0].JobSheettotalTime.toString().substringBefore(":"))
                    binding.mTvMiTime.setText(mins.toString().substringBefore(":"))
                    binding.mTvSec.setText(sec)

                    mList.clear()
                    mList.addAll(it.data!!.data.job_details[0].job_log_time)
                    mTimeSheetAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading -> {
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseClockInOutModel.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Time Clocked In")
                    status = "clocked_out"
                    server_save_date = it.data!!.data.create_jobs_logs.start_date
                    binding.tvClickOn.setText("Clock Out")
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

    fun onDetail(it: TimeModelNewUPdate) {

        if (it.data.total_time != null) {
            var mins = it.data.total_time.totalLogTime.toString().substringAfter(":")
            var sec = mins.toString().substringAfter(":")

            binding.mTvHrMiTime.setText("00:00:00")
        } else {
            binding.mTvHrMiTime.setText("00:00:00")
            binding.mTvHrTime.setText("00")
            binding.mTvMiTime.setText("00")
        }
        if (it.data.clockedInJob!=null ) {
            if ( it.data.clockedInJob.isNotEmpty()){
                job_id = it.data.clockedInJob[0].job_id

                server_save_date = it.data.clockedInJob.get(0).start_date
                if (it.data.clockedInJob.get(0).status.equals("clocked_in")) {
                    status = "clocked_out"
                    binding.tvClickOn.setText("Clock Out")
                    if (isInternetConnected(this)) {

                        val geocoder: Geocoder
                        val addresses: List<Address>
                        geocoder = Geocoder(this, Locale.getDefault())
                        addresses = geocoder.getFromLocation(mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                            mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
//                        ("34.0628345").toDouble(), ("-118.3018231").toDouble(),
                            1)!!
                        if (!addresses[0].featureName.isNotEmpty()) {
                            addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
                        } else {
                            if (addresses[0].thoroughfare!==null) {
                                addresss = addresses[0].thoroughfare
                            }else{
                                addresss = addresses[0].getLocality()
                            }
                        }
                        city = addresses[0].getLocality()
                        state = addresses[0].getAdminArea()
                        country = addresses[0].getCountryName()
                        postalCode = addresses[0].getPostalCode()

                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        enddate = sdf.format(Date())
                        val tz = TimeZone.getDefault()

                        if (tz.id.equals(it.data.clockedInJob.get(0).timezone)) {
                            if (checkedDate(server_save_date).equals(checkedDate(enddate))) {
                                if (isInternetConnected(this)) {
                                    Constants.showLoading(this)
                                    CoroutineScope(Dispatchers.IO).launch {
//                                        presenter.gettime(
//                                            job_id,
//                                            "clocked_in",
//                                            server_save_date,
//                                            enddate,
//                                            tz.id,
//                                            addresss,
//                                            city,
//                                            state,
//                                            postalCode,
//                                            mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
//                                                PreferenceConstants.LNG
//                                            )
//                                        )
                                    }

                                }
                            } else {
                                clockoutday = "80"
                                if (isInternetConnected(this)) {
                                    Constants.showLoading(this)
                                    CoroutineScope(Dispatchers.IO).launch {
//                                        presenter.outTime(
//                                            job_id,
//                                            "clocked_out",
//                                            server_save_date,
//                                            checkedDate(server_save_date) + " 23:59:58",
//                                            it.data.clockedInJob.get(0).timezone,
//                                            addresss,
//                                            city,
//                                            state,
//                                            postalCode,
//                                            mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
//                                                PreferenceConstants.LNG
//                                            )
//                                        )
                                    }
                                }

                            }
                        } else {

                            clockoutday = "1"
                            if (isInternetConnected(this)) {
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
//                                    presenter.outTime(
//                                        job_id,
//                                        "clocked_out",
//                                        server_save_date,
//                                        checkedDate(server_save_date) + " 23:59:58",
//                                        it.data.clockedInJob.get(0).timezone,
//                                        addresss,
//                                        city,
//                                        state,
//                                        postalCode,
//                                        mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
//                                            PreferenceConstants.LNG
//                                        )
//                                    )
                                }
                            }
                        }
                    }
                } else {
                    binding.tvClickOn.setText("Clock In")
                    status = "clocked_in"
                }
            }
        } else {
            binding.tvClickOn.setText("Clock In")
            status = "clocked_in"
        }
    }

    override fun onSingleListClick(item: Any, position: Int) {
        startActivity(Intent(this, TimesheetActivity::class.java)
            .putExtra("id",intent.getStringExtra("id").toString())
            .putExtra("date",mList[position].start_date)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2222) {
                if (resultCode == Activity.RESULT_OK) {

                    job_id = data!!.getStringExtra("result").toString()

                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(this, Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                        mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
                        1
                    )!!
                    // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    if (!addresses[0].featureName.isNotEmpty()) {
                        addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
                    } else {
                        if (addresses[0].thoroughfare!==null) {
                            addresss = addresses[0].thoroughfare
                        }else{
                            addresss = addresses[0].getLocality()
                        }
                    }
                    // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    city = addresses[0].getLocality()
                    state = addresses[0].getAdminArea()
                    country = addresses[0].getCountryName()
                    postalCode = addresses[0].getPostalCode()
//                      knownName = addresses[0].getFeatureName()

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    enddate = sdf.format(Date())
                    val tz = TimeZone.getDefault()
                    if (isInternetConnected(this)) {
//                        presenter.addtime(
//                            job_id,
//                            "clocked_in",
//                            enddate,
//                            tz.id,
//                            addresss,
//                            city,
//                            state,
//                            postalCode,
//                            mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
//                                PreferenceConstants.LNG
//                            )
//                        )
                    }

                }


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

    }

    private fun checkedDate(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
            spf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}