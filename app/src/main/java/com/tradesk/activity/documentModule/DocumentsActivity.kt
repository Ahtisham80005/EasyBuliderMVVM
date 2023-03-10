package com.tradesk.activity.documentModule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.AdditionalDocument
import com.tradesk.Model.AdditionalImageDocuments
import com.tradesk.Model.JobDocuments
import com.tradesk.Model.Users
import com.tradesk.R
import com.tradesk.databinding.ActivityDocumentsBinding
import com.tradesk.databinding.RowAllDocumentsBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.FileFinder
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.addWatcher
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.DocumentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DocumentsActivity : AppCompatActivity(), SingleListCLickListener, SingleItemCLickListener {

    var uriList=ArrayList<Uri>()
    var mListAllDocuments = mutableListOf<JobDocuments>()
    var mUserAdditionalDocumentsList = mutableListOf<AdditionalDocument>()
    val mLicenseAndInsList = mutableListOf<Users>()
    var mListUserDocuments = ArrayList<String>()

    val mAllDocumentsAdapter by lazy { AllDocumentsAdapter(this, this,mListAllDocuments,mListAllDocuments) }
    val mUserDocumentsAdapter by lazy { UserDocumentsAdapter(this, this,mUserAdditionalDocumentsList,mUserAdditionalDocumentsList) }
    val mLicenseAndInsDocumentsAdapter by lazy { LicenseAndInsDocumentsAdapter(this, this,mLicenseAndInsList) }

    lateinit var dialog: Dialog

    lateinit var binding: ActivityDocumentsBinding
    lateinit var viewModel:DocumentViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_documents)
        viewModel=ViewModelProvider(this).get(DocumentViewModel::class.java)

        binding.mRvDocument.layoutManager = GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false)
        binding.mRvDocument.adapter = mAllDocumentsAdapter

        binding.mRvUserDocument.layoutManager = GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false)
        binding.mRvUserDocument.adapter = mUserDocumentsAdapter

        binding.mRvLicenseAndInsDocument.layoutManager = GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false)
        binding.mRvLicenseAndInsDocument.adapter = mLicenseAndInsDocumentsAdapter

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllDocuments()
            }
        }
        clickEvent()
        binding.mEtSearchName.visibility= View.VISIBLE
        binding.mEtSearchName.addWatcher {
            mAllDocumentsAdapter.filter.filter(it)
            mUserDocumentsAdapter.filter.filter(it)
        }
        initObserve()
    }
    private fun clickEvent() {
        binding.mIvBack.setOnClickListener { finish() }
        binding.mIvAddImage.setOnClickListener {
            showNoteDialog()
        }
    }
    fun initObserve()
    {
        viewModel.responseAllDocumentsModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data!!.jobs.isNotEmpty())
                    {
                        mListAllDocuments.clear()
                        mListAllDocuments.addAll(it.data.jobs)
                        mAllDocumentsAdapter.notifyDataSetChanged()
                        mUserAdditionalDocumentsList.clear()
                        mUserAdditionalDocumentsList.addAll(it.data.users.additional_documents)
                        mUserDocumentsAdapter.notifyDataSetChanged()

                        mLicenseAndInsList.clear()
                        mLicenseAndInsList.addAll(listOf(it.data.users))
                        mLicenseAndInsDocumentsAdapter.notifyDataSetChanged()

                    }else{
                        mListAllDocuments.clear()
                        mAllDocumentsAdapter.notifyDataSetChanged()
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
        viewModel.responseUsersAddAdditionalDocsSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    if (isInternetConnected(this)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getAllDocuments()
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

    private fun browseDocuments() {
        val mimeTypes = arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
            "text/plain",
            "application/pdf",
            "application/zip", "application/vnd.android.package-archive","image/*")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra("android.intent.extra.MIME_TYPES",mimeTypes)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data!!
            if(data!=null)
            {
                val ivBackground: ImageView = dialog.findViewById<ImageView>(R.id.ivBackground)
                val ivCancel: ImageView = dialog.findViewById<ImageView>(R.id.ivCancel)
                val ivUpload: ImageView = dialog.findViewById<ImageView>(R.id.ivUpload)
                ivCancel.visibility = View.VISIBLE
                ivUpload.visibility = View.GONE
                ivBackground.background = AppCompatResources.getDrawable(this,R.drawable.ic_documents_thumbnail)

                if (data.clipData!= null)
                {
                    uriList.clear()
                    val count: Int = data.clipData!!.getItemCount()
                    Toast.makeText(this,count.toString(), Toast.LENGTH_SHORT).show()
                    //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (i in 0 until count) {
                        val documentUri: Uri = data.clipData!!.getItemAt(i).getUri()
                        uriList.add(documentUri)
//                        Toast.makeText(this,imageUri.toString(),Toast.LENGTH_SHORT).show()
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }
                }
                else
                {
                    if (result.resultCode == Activity.RESULT_OK) {
                        uriList.clear()
                        uriList.add(result.data?.data!!)
                    }
                }
            }
        }
    }

    private fun showNoteDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_documents_dialog)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        val window: Window? = dialog.getWindow()
        val wlp = window?.attributes
        if (wlp != null) {
            wlp.gravity = Gravity.CENTER
        }
        //   dialog.setCancelable(false)
        if (wlp != null) {
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        }
        if (window != null) {
            window.attributes = wlp
        }
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

        val cancel: Button = dialog.findViewById<Button>(R.id.cancel_button)
        val create: Button = dialog.findViewById<Button>(R.id.create_button)
        val ivUpload: ImageView = dialog.findViewById<ImageView>(R.id.ivUpload)
        val ivBackground: ImageView = dialog.findViewById<ImageView>(R.id.ivBackground)
        val ivCancel: ImageView = dialog.findViewById<ImageView>(R.id.ivCancel)
        val etFolderName: EditText = dialog.findViewById<EditText>(R.id.etFolderName)

        ivUpload.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
//                licenseimage = "2"
                browseDocuments()
            }
        }

        ivCancel.setOnClickListener {
            ivBackground.background = null
//            isFilePathNotEmpty = false
            ivUpload.visibility = View.VISIBLE
            ivCancel.visibility = View.GONE
        }

        create.setOnClickListener { v: View? ->
            if (etFolderName.text.isNotEmpty()) {
                uploadFileToAPI(etFolderName.text.toString())
                dialog.dismiss()
            } else {
                toast("Please enter name")
            }
        }
        cancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
    }

    private fun uploadFileToAPI(folderName:String) {
        var parts1: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        for(data in uriList)
        {
            contentResolver.takePersistableUriPermission(data,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val path = FileFinder.getFilePath(applicationContext,data)
            if (path != null && path.length > 3)
            {
                Log.e("Pdf path","PDF's path "+path)
            }
            else
            {
                Log.e("Pdf path","PDF's path is null")
            }
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), File(path))
            var doc= MultipartBody.Part.createFormData("image", File(path).name, requestFile)
            parts1.add(doc!!)
        }
        if (isInternetConnected(this)){
            hashMapOf<String, RequestBody>().also {
//                    it.put("doc\"; filename=\"doc.${mFile!!.extension}", RequestBody.create(MediaType.parse("application/${mFile!!.extension}"),mFile!!))
                it.put("folder_name", RequestBody.create(MediaType.parse("multipart/form-data"), folderName))
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.usersAddAdditionalDocs(it,parts1)
                }
            }
        }
    }

    override fun onSingleItemClick(item: Any, position: Int) {
    }
    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Multi"))
        {
            var title = "Documents"
            if(mListAllDocuments[position].users_assigned.isEmpty())
            {
                title = mListAllDocuments[position].project_name.capitalize() + " - ".toString()
            }

            for (i in 0 until mListAllDocuments[position].users_assigned.size)
            {
                if (mListAllDocuments[position].users_assigned[i].user_id != null)
                {
                    title = mListAllDocuments[position].project_name.capitalize() + " - " +
                            mListAllDocuments[position].users_assigned[i].user_id.name
                    break
                }
                else{
                    title = mListAllDocuments[position].project_name.capitalize() + " - ".toString()
                }
            }
            val docList = mListAllDocuments[position].additional_images.filter { it.status.equals("permit") } as ArrayList<AdditionalImageDocuments>

            startActivity(
                Intent(this, DocumentsSubActivity::class.java)
//                    .putExtra("job_id", "Ahtisham")
                    .putExtra("job_id", mListAllDocuments[position]._id)
                    .putExtra("project_name", title)
//                    .putExtra("project_name", "Ahtisham")
                    .putParcelableArrayListExtra("docslist",docList)
            )

        }
        else if(item == "UserDocumentsAdapter"){
            mListUserDocuments.clear()
            mListUserDocuments.addAll(mUserAdditionalDocumentsList[position].documents)
//            Log.d(TAG, "onSingleListClick: "+ mUserAdditionalDocumentsList[position].documents)
//            Log.d(TAG, "onSingleListClick: " +position)

            startActivity(
                Intent(this,DocumentSubUserActivity::class.java)
                    .putExtra("folderName", mUserAdditionalDocumentsList[position].folder_name)
                    .putExtra("id", mUserAdditionalDocumentsList[position]._id)
                    .putExtra("index", position)
                    .putExtra("docsFrom", "UserDocumentsAdapter")
                    .putStringArrayListExtra(
                        "docslist", mListUserDocuments)
            )
        }
        else if (item == "LicenseAndInsDocumentsAdapter"){
            mListUserDocuments.clear()
            mListUserDocuments.addAll(mLicenseAndInsList[position].license_and_ins.docs_url)
//            Log.d(TAG, "onSingleListClick: "+ mLicenseAndInsList[position].license_and_ins.docs_url)
//            Log.d(TAG, "onSingleListClick: " +position)

            startActivity(
                Intent(this,DocumentSubUserActivity::class.java)
                    .putExtra("folderName", mLicenseAndInsList[position].name)
                    .putExtra("id", mLicenseAndInsList[position]._id)
                    .putExtra("index", position)
                    .putExtra("docsFrom", "LicenseAndIns")
                    .putStringArrayListExtra(
                        "docslist", mListUserDocuments)
            )
        }
    }
}