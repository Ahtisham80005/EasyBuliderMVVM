package com.tradesk.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Model.ProfileModel
import com.tradesk.activity.proposalModule.ProposalsActivity
import com.tradesk.activity.SettingsActivity
import com.tradesk.activity.analyticsModule.MainAnalyticsActivity
import com.tradesk.activity.chatModule.ChatUsersActivity
import com.tradesk.activity.clientModule.AllCustomersActivity
import com.tradesk.activity.documentModule.DocumentsActivity
import com.tradesk.activity.galleryModule.GallaryActivity
import com.tradesk.activity.profileModule.MyProfileActivity
import com.tradesk.activity.salesPerson.UsersContrActivity
import com.tradesk.activity.timesheetModule.TimesheetActivity
import com.tradesk.databinding.FragmentProfileBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.insertString
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    var mHomeImage = true
    var CheckVersion = true
    lateinit var binding: FragmentProfileBinding
    lateinit var viewModel: ProfileViewModel
    @Inject
    lateinit var mPrefs: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentProfileBinding.inflate(inflater, container, false)
        Constants.showLoading(requireActivity())
        CoroutineScope(Dispatchers.IO).launch {
            if (isInternetConnected(requireActivity())){
                viewModel.getProfile()
            }
        }
        setUp()
        initObserve()
        return binding.getRoot()
    }

    fun setUp() {

        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE) == "1"){
            binding.cvPay.visibility=View.VISIBLE
            binding.mCvContacts.visibility=View.VISIBLE
            binding.mCvDicuments.visibility=View.VISIBLE

        }else{
            binding.cvPay.visibility=View.GONE
            binding.mCvContacts.visibility=View.GONE
            binding.mCvDicuments.visibility=View.GONE
        }

        binding.mCvMyProfile.setOnClickListener { startActivity(Intent(requireActivity(), MyProfileActivity::class.java)) }
        binding.mCvUsers.setOnClickListener { startActivity(Intent(requireActivity(), DocumentsActivity::class.java)) }
        binding.mCvAnalytics.setOnClickListener {  startActivity(Intent(requireActivity(), MainAnalyticsActivity::class.java))  }
        binding.cvPay.setOnClickListener {   }
        binding.mCvProposals.setOnClickListener { startActivity(Intent(requireActivity(), ProposalsActivity::class.java).putExtra("title","Proposals")
            .putExtra("userfrom","profile")) }
        binding.mCvInvoices.setOnClickListener { startActivity(Intent(requireActivity(), ProposalsActivity::class.java).putExtra("title","Invoices")
            .putExtra("userfrom","profile")) }
        binding.clTime.setOnClickListener { startActivity(Intent(requireActivity(), TimesheetActivity::class.java)) }
        binding.mCvDicuments.setOnClickListener { startActivity(Intent(requireActivity(), UsersContrActivity::class.java)) }
        binding.mCvContacts.setOnClickListener { startActivity(Intent(requireActivity(), AllCustomersActivity::class.java)) }
        binding.mCvGallery.setOnClickListener { startActivity(Intent(requireActivity(), GallaryActivity::class.java)
            .putExtra("userfrom","profile")) }

        binding.ivSettings.setOnClickListener { startActivity(Intent(requireActivity(), SettingsActivity::class.java)) }
        binding.clPayments.setOnClickListener { startActivity(Intent(requireActivity(), ChatUsersActivity::class.java))}
        binding.techspport.setOnClickListener{toast("Coming soon")}
        binding.financing.setOnClickListener{toast("Coming soon")}
        binding.timeSheet.setOnClickListener{toast("Coming soon")}
    }

    fun initObserve() {
        viewModel.responseProfileModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    setData(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
    }

    fun setData(it:ProfileModel) {

        mPrefs.setKeyValue(PreferenceConstants.USER_NAME, it.data.name)
        if (it.data.image.isNotEmpty()){
            binding.imageView334.loadWallImage(it.data.image)
        }
        if (it.data.addtional_info.trade.isEmpty()) {
            binding.mTvType.setText("Contractor")
        }else{
            binding.mTvType.setText(it.data.addtional_info.trade)
        }

        binding.mTvName.setText(it.data.name)
        binding.mTvEmail.setText(it.data.email)

        if (it.data.phone_no.isEmpty()){
            binding.mTvPhone.setText("N/A")
        }else{
            //new
            binding.mTvPhone.text = insertString(it.data.addtional_info.home_phone_no, "", 0)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), ")", 2)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), " ", 3)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), "-", 7)
            binding.mTvPhone.text = "+1 (" + binding.mTvPhone.text.toString()

        }

    }

    override fun onResume() {
        super.onResume()
        Constants.showLoading(requireActivity())
        CoroutineScope(Dispatchers.IO).launch {
            if (isInternetConnected(requireActivity())){
                viewModel.getProfile()
            }
        }
    }



}