package com.tradesk.activity.documentModule

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AbsListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.*
import com.tradesk.Model.SelectedUserDocsDel
import com.tradesk.R
import com.tradesk.databinding.ActivityDocumentSubUserBinding
import com.tradesk.databinding.ActivityDocumentsBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.FileFinder
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.AllinOneDialog
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
class DocumentSubUserActivity : AppCompatActivity(), SingleListCLickListener,
    SingleItemCLickListener, LongClickListener, CustomCheckBoxListener,
    UnselectCheckBoxListener {

    var mListUserDocuments = ArrayList<String>()
    var userSubDocumentsAdapter: UserSubDocumentsAdapter? = null
    var TAG = "UserDocumentsSubActivity"
    var folderName = ""
    var id = ""
    var docsFrom = ""
    var myImageUri = ""
    var mFile: File? = null
    var selectedUrlsArray = ArrayList<String>()
    var selectedIdArray = ArrayList<String>()
    var index:Int? = null
    var checkBoxVisibility:Boolean=false
    var allCheckBoxSelect:Boolean=false
    var activeSlectMenu:Boolean=false
    lateinit var binding: ActivityDocumentSubUserBinding
    lateinit var viewModel: DocumentViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_document_sub_user)
        viewModel= ViewModelProvider(this).get(DocumentViewModel::class.java)
        setRv()
        clickEvents()
        initObserve()
    }

    private fun setRv(){
        folderName = intent.getStringExtra("folderName").toString()
        binding.tvTitleUserDocs.text = folderName
        index = intent.getIntExtra("index",-1)
        id = intent.getStringExtra("id").toString()
        docsFrom = intent.getStringExtra("docsFrom").toString()
        mListUserDocuments = intent.getStringArrayListExtra("docslist") as ArrayList<String>
        Log.d(TAG, "setRv: "+mListUserDocuments)

        userSubDocumentsAdapter = UserSubDocumentsAdapter(this,this,mListUserDocuments,this,this,this,checkBoxVisibility,allCheckBoxSelect)
        GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
            binding.rvSelectItemUserDocuments.layoutManager = this
        }
        binding.rvSelectItemUserDocuments.adapter= userSubDocumentsAdapter

        if (docsFrom == "LicenseAndIns"){
            binding.mIvRightMenuUserDocs.visibility = View.GONE
            binding.mIvAddUserDocs.visibility = View.GONE
        }
    }

    private fun clickEvents(){
        binding.ivBack.setOnClickListener { finish() }
        binding.mIvRightMenuUserDocs.setOnClickListener {
            if(!activeSlectMenu)
            {
                if (mListUserDocuments.isNotEmpty()){
                    showRightMenu(it)
                }
            }
            else
            {
                showSecondRightMenu(it)
            }

        }
        binding.mIvAddUserDocs.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                browseDocuments()
            }
        }
    }

    fun initObserve()
    {
        viewModel.responseUsersDelSelectedAdditionalDocs.observe(this, androidx.lifecycle.Observer {it->
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
        viewModel.responseUsersUpdateAdditionalDocs.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Update Successfully")
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
                userSubDocumentsAdapter!!.checkboxVisibility=true
                userSubDocumentsAdapter!!.notifyDataSetChanged()
            }else if (it.itemId == R.id.item_select_all){
                checkBoxVisibility=true
                userSubDocumentsAdapter!!.checkboxVisibility=true
                userSubDocumentsAdapter!!.allCheckBoxSelect=true
//                mSubListUpdatedAdditionalImage.forEach { additional ->
//                    selectedIdArray.add(additional._id)
//                    selectedImageArray.add(additional.image)
//                }
                userSubDocumentsAdapter!!.notifyDataSetChanged()
                activeSlectMenu=true
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

            if (it.itemId == R.id.item_share) {
                shareDocs()
            } else if (it.itemId == R.id.item_delete) {
//                if (selectedPosition != null && selectionResult >= 1 && selectType.equals("single")) {
                AllinOneDialog(ttle = "Delete",
                    msg = "Are you sure you want to Delete it ?",
                    onLeftClick = {/*btn No click*/ },
                    onRightClick = {/*btn Yes click*/
//                            if (isInternetConnected() && selectedPosition != null && selectionResult >= 1) {
                        val selectedUserDocsDel = SelectedUserDocsDel(index!!, selectedUrlsArray)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteUserSelectedAdditionalDocs(selectedUserDocsDel)
                        }
                        Log.d(TAG, "showRightMenu: " +selectedUrlsArray)
//                            }
                    })
            }
            else if (it.itemId == R.id.item_download) {
                selectedIdArray.zip(selectedUrlsArray).forEach { pair ->
                    downloadFile(pair.first, pair.second)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }
    private fun browseDocuments() {
//        startActivityForResult(
//            Intent(this, FileExplorerActivity::class.java),
//            ConstUtils.REQUEST_CODE_DOCS
//        )

        val mimeTypes = arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
            "text/plain",
            "application/pdf",
            "application/zip", "application/vnd.android.package-archive","image/*")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        intent.type = "application/pdf"
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
                if (data.clipData!= null) {
                    val count: Int = data.clipData!!.getItemCount()
                    Toast.makeText(this,count.toString(), Toast.LENGTH_SHORT).show()
                    //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (i in 0 until count) {
                        val documentUri: Uri = data.clipData!!.getItemAt(i).getUri()
                        uriList.add(documentUri)
//                        Toast.makeText(this,imageUri.toString(),Toast.LENGTH_SHORT).show()
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }
                    uploadFileToAPI(uriList)
                }
                else
                {
                    if (result.resultCode == Activity.RESULT_OK) {
                        uriList.add(result.data?.data!!)
                        uploadFileToAPI(uriList)
                    }
                }
            }

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
            var doc= MultipartBody.Part.createFormData("doc", File(path).name, requestFile)
            parts1.add(doc!!)
        }

        if (isInternetConnected(this)){
            hashMapOf<String, RequestBody>().also {
//                it.put("doc\"; filename=\"doc.${mFile!!.extension}", RequestBody.create(MediaType.parse("application/${mFile!!.extension}"),mFile!!))
                it.put("index", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getIntExtra("index",-1).toString()))
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.usersUpdateAdditionalDocs(it,parts1)
                }
            }
        }
    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (docsFrom == "LicenseAndIns") {
            if (item == "UserGalleryAdapter"){
//                openApp(this, mListUserDocuments[position])
            }
        }
    }
    override fun onSingleItemClick(item: Any, position: Int) {
    }

    override fun onLongClickListener(item: Any, position: Int) {
        if (docsFrom == "UserDocumentsAdapter"){
            showRightMenuOnLong(item as View,position)
        }
    }
    override fun onCheckBoxClick(selectedPosition: Int) {
        selectedIdArray.add(mListUserDocuments[selectedPosition].substringAfterLast("/"))
        selectedUrlsArray.add(mListUserDocuments[selectedPosition])
    }

    override fun onCheckBoxUnCheckClick(item: Any, selectedPosition: Int) {
        selectedIdArray.removeAll(setOf(mListUserDocuments[selectedPosition].substringAfterLast("/")))
        selectedUrlsArray.removeAll(setOf(mListUserDocuments[selectedPosition]))
    }

    private fun showRightMenuOnLong(anchor: View,selectedPosition:Int): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_edit).isVisible = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

        popup.setOnDismissListener {
            if (selectedPosition != null) {
                selectedIdArray.removeAll(setOf(mListUserDocuments[selectedPosition].substringAfterLast("/")))
                selectedUrlsArray.removeAll(setOf(mListUserDocuments[selectedPosition]))
            }
        }

        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                shareDocs()
            }
            else if (it.itemId == R.id.item_delete) {
//                if (selectedPosition != null) {
                AllinOneDialog(ttle = "Delete",
                    msg = "Are you sure you want to Delete it ?",
                    onLeftClick = {/*btn No click*/ },
                    onRightClick = {/*btn Yes click*/
                        val selectedUserDocsDel = SelectedUserDocsDel(index!!, selectedUrlsArray)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteUserSelectedAdditionalDocs(selectedUserDocsDel)
                        }
                        Log.d(TAG, "showRightMenu: " +selectedUrlsArray)
                    })
            }
            else if (it.itemId == R.id.item_download) {
                if (selectedPosition != null) {
                    selectedIdArray.zip(selectedUrlsArray).forEach { pair ->
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
        shareIntent.putExtra(Intent.EXTRA_TEXT,selectedUrlsArray.get(0))
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
}