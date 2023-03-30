package com.tradesk.activity.documentModule

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.Interface.*
import com.tradesk.Model.AdditionalImageDocuments
import com.tradesk.Model.AdditionalImageLeadDetail
import com.tradesk.Model.CheckModel
import com.tradesk.Model.SelectedDocsIds
import com.tradesk.R
import com.tradesk.activity.documentModule.adapter.DocumentsSubAdapter
import com.tradesk.activity.documentModule.adapter.PermitsAdapter
import com.tradesk.databinding.ActivityDocumentsSubBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.FileFinder
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.AllinOneDialog
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
class DocumentsSubActivity : AppCompatActivity() , SingleListCLickListener, SingleItemCLickListener,
    LongClickListener, CustomCheckBoxListener, UnselectCheckBoxListener {
    var job_id = ""
    var jobDcment_id = ""
    lateinit var camera_image_uri:Uri
    var folderName = ""
    var id = ""
    var docsFrom = ""
    var myImageUri = ""
    var mFile: File? = null
    var selectedUrlsArray = ArrayList<String>()
    var selectedIdArray = ArrayList<String>()
    var selectedDocsIdArray = ArrayList<String>()
    var selectedImageArray = ArrayList<String>()
    var index:Int? = null
    var checkBoxVisibility:Boolean=false
    var allCheckBoxSelect:Boolean=false
    var activeSlectMenu:Boolean=false
    var mcheckBoxModelList=mutableListOf<CheckModel>()

    var mListAllDocuments = mutableListOf<AdditionalImageDocuments>()
    var documentsSubAdapter: DocumentsSubAdapter? = null

    val mPermits = ArrayList<AdditionalImageLeadDetail>()
    var mPermitsAdapter: PermitsAdapter? = null

    lateinit var binding: ActivityDocumentsSubBinding
    lateinit var viewModel: DocumentViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_documents_sub)
        viewModel= ViewModelProvider(this).get(DocumentViewModel::class.java)
        job_id = intent.getStringExtra("job_id").toString()
        jobDcment_id = intent.getStringExtra("jobDocument_id").toString()
        binding.tvTitle.text = intent.getStringExtra("project_name").toString()

        if (intent.getParcelableArrayListExtra<AdditionalImageDocuments>("docslist") != null)
        {
            mListAllDocuments = intent.getParcelableArrayListExtra<AdditionalImageDocuments>("docslist") as ArrayList<AdditionalImageDocuments>
            mcheckBoxModelList.clear()
            for(i in mListAllDocuments)
            {
                mcheckBoxModelList.add(CheckModel(false))
            }
            documentsSubAdapter = DocumentsSubAdapter(this, this, mListAllDocuments,this,this,this,checkBoxVisibility,allCheckBoxSelect,mcheckBoxModelList)
            binding.mRvDocument.layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
            binding.mRvDocument.adapter = documentsSubAdapter


//            if (isInternetConnected(this)) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    viewModel.getAllDocuments()
//                }
//            }
        }
        else if (intent.hasExtra("permits"))
        {
//            job_id=jobDcment_id
            mPermitsAdapter = PermitsAdapter(this, this, mPermits,this,this,checkBoxVisibility,allCheckBoxSelect,mcheckBoxModelList)
            binding.mRvDocument.layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
            binding.mRvDocument.adapter = mPermitsAdapter
            if (isInternetConnected(this)){
                Constants.showLoading(this)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getLeadDetail(intent.getStringExtra("id").toString())
                }
            }
        }
        clickEvent()
        initObserve()
    }
    private fun clickEvent() {
        binding.mIvBack.setOnClickListener { finish() }
        binding.editTextTextPersonName6.addWatcher {
        }
        binding.mIvRightMenuDoc.setOnClickListener {
            if(!activeSlectMenu)
            {
                if (mListAllDocuments.isNotEmpty()){
                    showRightMenu(it)
                }
                else if(mPermits.isNotEmpty())
                {
                    showRightMenu(it)
                }
            }
            else
            {
                showSecondRightMenu(it)
            }
        }
        binding.mIvAddDocs.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 30)
            {
                if (!Environment.isExternalStorageManager()) {
                    val getpermission = Intent()
                    getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivity(getpermission)
                } else {
                    if (permissionFile.checkLocStorgePermission(this)) {
                        browseDocuments()
                    }
                }
            }
            else
            {
                if (permissionFile.checkLocStorgePermission(this)) {
                    browseDocuments()
                }
            }
        }
    }

    fun initObserve() {
        viewModel.responseAllDocumentsModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
//                    finish()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseLeadDetailModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    if (intent.hasExtra("permits"))
                    {
                        Log.e("Primets Size",it.data!!.data.leadsData.additional_images!!.filter { it.status!!.equals("permit") }.toString())
                        mPermits.clear()
                        mPermits.addAll(it.data.data.leadsData.additional_images!!.filter { it.status!!.equals("permit") })
                        Log.e("Permit List",mPermits.toString())
                        mcheckBoxModelList.clear()
                        for(i in mPermits)
                        {
                            mcheckBoxModelList.add(CheckModel(false))
                        }
                        mPermitsAdapter!!.notifyDataSetChanged()
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
        viewModel.responseDeleteAllDocumentsJobsSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted Successfully")
                    mListAllDocuments.clear()
                    documentsSubAdapter?.notifyDataSetChanged()
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
        viewModel.responseDeleteSelectedDocumentsJobsSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted Successfully")
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
        viewModel.responseAddImagesSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Added Successfully")
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
    }

    fun showRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.sub_gallery_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{

            if (it.itemId == R.id.item_select_items){
                activeSlectMenu=true
                checkBoxVisibility=true
                selectedDocsIdArray.clear()
                selectedIdArray.clear()
                selectedImageArray.clear()
                if (intent.hasExtra("permits"))
                {
                    //From Job
                    mPermitsAdapter!!.checkboxVisibility=true
                    mcheckBoxModelList.clear()
                    for(i in mPermits)
                    {
                        mcheckBoxModelList.add(CheckModel(false))
                    }
                    mPermitsAdapter!!.notifyDataSetChanged()
                }
                else
                {
                    //From Document
                    documentsSubAdapter!!.checkboxVisibility=true
                    mcheckBoxModelList.clear()
                    for(i in mListAllDocuments)
                    {
                        mcheckBoxModelList.add(CheckModel(false))
                    }
                    documentsSubAdapter!!.notifyDataSetChanged()
                }
            }else if (it.itemId == R.id.item_select_all){
                checkBoxVisibility=true
                activeSlectMenu=true
                selectedDocsIdArray.clear()
                selectedIdArray.clear()
                selectedImageArray.clear()
                if (intent.hasExtra("permits"))
                {
                    //From Job
                    mPermitsAdapter!!.checkboxVisibility=true
                    mPermitsAdapter!!.allCheckBoxSelect=true
                    mcheckBoxModelList.clear()
                    for(i in mPermits)
                    {
                        mcheckBoxModelList.add(CheckModel(true))
                    }
                    mPermitsAdapter!!.notifyDataSetChanged()
                }
                else
                {
                    documentsSubAdapter!!.checkboxVisibility=true
                    documentsSubAdapter!!.allCheckBoxSelect=true
                    mcheckBoxModelList.clear()
                    for(i in mListAllDocuments)
                    {
                        mcheckBoxModelList.add(CheckModel(true))
                    }
                    documentsSubAdapter!!.notifyDataSetChanged()
                }
            }else if(it.itemId == R.id.item_delete_all){
                if (isInternetConnected(this)) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete all ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            //  presenter.deleteAllDocs()
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllDocumentsJobs(job_id)
                            }
                        })
                }
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun showSecondRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_edit).isVisible = false
        if (selectedIdArray.size > 1) {
            popup.menu.findItem(R.id.item_share).isVisible = false
            //  popup.menu.findItem(R.id.item_move).isVisible = false
        }
        popup.setOnMenuItemClickListener {

            if (it.itemId == R.id.item_share)
            {
                shareDocs()
            }
            else if (it.itemId == R.id.item_delete)
            {
                AllinOneDialog(ttle = "Delete",
                    msg = "Are you sure you want to Delete it ?",
                    onLeftClick = {/*btn No click*/ },
                    onRightClick = {/*btn Yes click*/

                        Log.e("job_id",intent.getStringExtra("job_id").toString()) //63f49c50634dd198d9afcb53
                        Log.e("DocsIdArray",selectedDocsIdArray.toString()) //[63f722197aad36fa4d5d317b]

                        val selectedDocsIds = SelectedDocsIds(intent.getStringExtra("job_id").toString(), selectedDocsIdArray)
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteSelectedDocumentsJobs(selectedDocsIds)
                        }
//                        Log.d(TAG, "showRightMenu: " +selectedDocsIdArray)

                    })

            }
            else if (it.itemId == R.id.item_download) {
                selectedIdArray.zip(selectedImageArray).forEach { pair ->
                    downloadFile(pair.first, pair.second)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun browseDocuments() {
        val mimeTypes = arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
            "text/plain",
            "application/pdf",
            "application/zip", "application/vnd.android.package-archive","image/*")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "*/*"
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            var uriList: ArrayList<Uri> = ArrayList<Uri>()
            val data: Intent? = result.data!!
            if(data!=null)
            {
                if (data.clipData!= null)
                {
                    val count: Int = data.clipData!!.getItemCount()
                    for (i in 0 until count)
                    {
                        val documentUri: Uri = data.clipData!!.getItemAt(i).getUri()
                        uriList.add(documentUri)
                    }
                    Log.e("Upload Document",uriList.toString())
                    uploadFileToAPI(uriList)
                }
                else
                {
                    if (result.resultCode == Activity.RESULT_OK) {
                        uriList.add(result.data?.data!!)
                        Log.e("Upload Document",uriList.toString())
                        uploadFileToAPI(uriList)
                    }
                }
            }

        }
    }
    var resultLauncherSingleFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            Toast.makeText(this,"Good", Toast.LENGTH_SHORT).show()
            var uriList: ArrayList<Uri> = ArrayList<Uri>()
            uriList.add(camera_image_uri)
            uploadFileToAPI(uriList)
        }
    }

    private fun uploadFileToAPI(uriList: ArrayList<Uri>) {

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
                RequestBody.create(MediaType.parse("multipart/form-data"),File(path))
            var doc= MultipartBody.Part.createFormData("image", File(path).name, requestFile)
            parts1.add(doc!!)
        }

        if (isInternetConnected(this)){
            hashMapOf<String, RequestBody>().also {
//                it.put("image\"; filename=\"image.${mFile!!.extension}", RequestBody.create(MediaType.parse("application/${mFile!!.extension}"), mFile!!))
                it.put("_id", RequestBody.create(MediaType.parse("multipart/form-data"),job_id))
                it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "permit"))
                Constants.showLoading(this)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.addImages(it,parts1)
                }
            }
        }
    }

    override fun onSingleListClick(item: Any, position: Int) {}
    override fun onLongClickListener(item: Any, position: Int) {
        if (!intent.hasExtra("permits"))
        {
            showRightMenuOnLong(item as View,position)
        }
    }

    override fun onCheckBoxClick(selectedPosition: Int) {
        if (intent.hasExtra("permits"))
        {
            selectedDocsIdArray.add(mPermits[selectedPosition]._id)
            selectedIdArray.add(mPermits[selectedPosition].image.substringAfterLast("/"))
            selectedImageArray.add(mPermits[selectedPosition].image)
        }
        else{
            selectedDocsIdArray.add(mListAllDocuments[selectedPosition]._id)
            selectedIdArray.add(mListAllDocuments[selectedPosition].image.substringAfterLast("/"))
            selectedImageArray.add(mListAllDocuments[selectedPosition].image)
        }
    }
    override fun onCheckBoxUnCheckClick(item: Any,selectedPosition: Int) {
        if (intent.hasExtra("permits"))
        {
            selectedDocsIdArray.removeAll(setOf(mPermits[selectedPosition]._id))
            selectedIdArray.removeAll(setOf(mPermits[selectedPosition].image.substringAfterLast("/")))
            selectedImageArray.removeAll(setOf(mPermits[selectedPosition].image))

        }
        else{
            selectedDocsIdArray.removeAll(setOf(mListAllDocuments[selectedPosition]._id))
            selectedIdArray.removeAll(setOf(mListAllDocuments[selectedPosition].image.substringAfterLast("/")))
            selectedImageArray.removeAll(setOf(mListAllDocuments[selectedPosition].image))
        }

    }
    private fun showRightMenuOnLong(anchor: View,selectedPosition:Int): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_edit).isVisible = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

        popup.setOnDismissListener {
        }
        selectedDocsIdArray.clear()
        selectedIdArray.clear()
        selectedImageArray.clear()
        if (intent.hasExtra("permits"))
        {
            selectedDocsIdArray.add(mPermits[selectedPosition]._id)
            selectedIdArray.add(mPermits[selectedPosition].image.substringAfterLast("/"))
            selectedImageArray.add(mPermits[selectedPosition].image)

            Log.e("Data Add",selectedDocsIdArray.size.toString())
        }
        else{
            selectedDocsIdArray.add(mListAllDocuments[selectedPosition]._id)
            selectedIdArray.add(mListAllDocuments[selectedPosition].image.substringAfterLast("/"))
            selectedImageArray.add(mListAllDocuments[selectedPosition].image)
        }

        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                if (!selectedDocsIdArray.isEmpty()) {
                    shareDocs()
//                    shareDocs(mListAllDocuments[selectedPosition].image)
                } else {
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_delete) {
                if (!selectedDocsIdArray.isEmpty()) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this) && selectedPosition != null)
                            {
                                Log.e("job_id",job_id)
                                Log.e("DocsIdArray",selectedDocsIdArray.toString())
                                val selectedDocsIds = SelectedDocsIds(job_id,selectedDocsIdArray)
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedDocumentsJobs(selectedDocsIds)
                                }
//                                Log.d(TAG, "showRightMenu: " +selectedDocsIdArray)
                            }
                        })
                } else {
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_download) {
                if (!selectedDocsIdArray.isEmpty()) {
                    selectedIdArray.zip(selectedImageArray).forEach { pair ->
                        downloadFile(pair.first, pair.second)
                    }
                } else {
                    toast("Select an item")
                }
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }
    private fun shareDocs() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, selectedIdArray.get(0))
        startActivity(Intent.createChooser(shareIntent, "Share link using"))
    }
    private fun downloadFile(filename: String, downloadUrlOfImage: String) {
        try {
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("pdf") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOCUMENTS,
                    File.separator.toString() + filename + ".pdf"
                )
            dm.enqueue(request)
            Toast.makeText(this, "File download started.", Toast.LENGTH_SHORT).show()
            // finish()
        } catch (e: Exception) {
            toast("File download failed.")
        }
    }
    override fun onSingleItemClick(item: Any, position: Int) {}

}