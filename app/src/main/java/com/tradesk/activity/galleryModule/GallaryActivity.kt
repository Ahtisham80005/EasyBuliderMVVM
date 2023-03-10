package com.tradesk.activity.galleryModule

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.AdditionalImageImageClient
import com.tradesk.Model.AdditionalImagesWithClientModel
import com.tradesk.Model.LeadsDataImageClient
import com.tradesk.Model.UsersAdditionalImageX
import com.tradesk.R
import com.tradesk.activity.galleryModule.adapter.AdditionalGallaryAdapter
import com.tradesk.activity.galleryModule.adapter.UserGallaryAdapter
import com.tradesk.databinding.ActivityGallaryBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.FilePath
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.addWatcher
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.GallaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class GallaryActivity : AppCompatActivity() , SingleListCLickListener {

    var jobsProjectNameArray = ArrayList<String>()
    var projectIdArray = ArrayList<String>()

    var mListUpdatedAdditionalImage = ArrayList<LeadsDataImageClient>()
    val mAdditionalGallaryAdapter by lazy { AdditionalGallaryAdapter(this,this,mListUpdatedAdditionalImage,mListUpdatedAdditionalImage) }

    var mListUserGallery = ArrayList<UsersAdditionalImageX>()
    val mUserGalleryAdapter by lazy { UserGallaryAdapter(this,this,mListUserGallery,mListUserGallery) }

    var mUserGalleryImage = ArrayList<String>()
    var userFolderNameArray = ArrayList<String>()

    lateinit var dialog: Dialog
    val mBuilder: Dialog by lazy { Dialog(this@GallaryActivity) }

    lateinit var camera_image_uri: Uri
    var pathFromUriList=ArrayList<Uri>()

    lateinit var binding:ActivityGallaryBinding
    lateinit var viewModel:GallaryViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_gallary)
        viewModel= ViewModelProvider(this).get(GallaryViewModel::class.java)
        try {
            callApi()
        }catch (e:Exception){
            e.printStackTrace()
        }
        binding.mIvBack.setOnClickListener { finish() }
        binding.mIvAddImage.setOnClickListener {
            showMakeAlbumDialog()
        }
        binding.mIvRightMenue.setOnClickListener {

        }
        initObserve()
    }
    private fun callApi() {
        binding.textView6.setText("Gallary")
        binding.mIvAddImage.visibility=View.VISIBLE
//        isUserFromProfileGallery = true
        GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
            binding.rvGallary.layoutManager = this
        }
        binding.rvGallary.adapter= mAdditionalGallaryAdapter

        binding.rvUserGallery.visibility = View.VISIBLE
        GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
            binding.rvUserGallery.layoutManager = this
        }
        binding.rvUserGallery.adapter = mUserGalleryAdapter

        binding.mEtSearchName.visibility=View.VISIBLE

        binding.mEtSearchName.addWatcher {
            mAdditionalGallaryAdapter.filter.filter(it)
            mUserGalleryAdapter.filter.filter(it)
        }
        if (isInternetConnected(this)){
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getadditionalimagesjobs("1","30","image")
            }
        }
    }
    fun initObserve() {
        viewModel.responsAdditionalImages.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    setImagesData(it.data!!)
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
                    toast("Added Successfully")
                    CoroutineScope(Dispatchers.IO).launch {
                       viewModel.getadditionalimagesjobs("1","30","image")
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
    private fun setImagesData(it: AdditionalImagesWithClientModel) {
        mListUpdatedAdditionalImage.clear()
        jobsProjectNameArray.clear()
        projectIdArray.clear()

        for(leadsData in it.data.leadsData)
        {
            Collections.reverse(leadsData.additional_images)
        }
        Collections.reverse(it.data.leadsData)
        mListUpdatedAdditionalImage.addAll(it.data.leadsData)

        mListUpdatedAdditionalImage.forEach {
            jobsProjectNameArray.add(it.project_name)
            projectIdArray.add(it._id)
        }
        mAdditionalGallaryAdapter.notifyDataSetChanged()

        mListUserGallery.clear()
        userFolderNameArray.clear()

        for(additionalImages in it.data.users.additional_images)
        {
            Collections.reverse(additionalImages.images)
        }

        Collections.reverse(it.data.users.additional_images)
        mListUserGallery.addAll(it.data.users.additional_images)
        mListUserGallery.forEach {
            userFolderNameArray.add(it.folder_name)
        }
        mUserGalleryAdapter.notifyDataSetChanged()
    }
    override fun onSingleListClick(item: Any, position: Int) {
        binding.mEtSearchName.setText("")
        if (item.equals("Multi")) {
            var title = "Gallery"
            for (i in 0 until mListUpdatedAdditionalImage[position].users_assigned.size) {
                if (mListUpdatedAdditionalImage[position].users_assigned[i].user_id != null) {
                    title = mListUpdatedAdditionalImage[position].project_name.capitalize() + " - " +
                            mListUpdatedAdditionalImage[position].users_assigned[i].user_id.name
                    break
                }else{
                    title = mListUpdatedAdditionalImage[position].project_name.capitalize() + " - "
                }
            }

            val imageList = mListUpdatedAdditionalImage[position].additional_images.filter { it.status.equals("image") } as ArrayList<AdditionalImageImageClient>
            startActivity(
                Intent(this,SubGallaryActivity::class.java)
                    .putExtra("job_id", mListUpdatedAdditionalImage[position]._id)
                    .putExtra("title", title)
                    .putParcelableArrayListExtra("imagelist",imageList)
            )

        }
        else if (item == "UserGalleryAdapter"){
            mUserGalleryImage.clear()
            mUserGalleryImage.addAll(mListUserGallery[position].images)
//            Log.d(TAG, "onSingleListClick: "+ mListUserGallery[position].images)
//            Log.d(TAG, "onSingleListClick: " +position)
            Log.e("ClickAlbum",mListUserGallery[position].images.toString())
            Log.e("Total index size",mListUserGallery.size.toString())
            var index=0
            if(position==0)
            {
                index=mListUserGallery.size-1
            }
            else{
                index=(mListUserGallery.size-1)-position
            }

            startActivity(
                Intent(this,SubGallaryUserActivity::class.java)
                    .putExtra("folderName", mListUserGallery[position].folder_name)
                    .putExtra("id", mListUserGallery[position]._id)
                    .putExtra("index",index)
                    .putStringArrayListExtra("imagelist",mUserGalleryImage)
            )

        }
    }
    private fun showMakeAlbumDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_image_album_dialog)
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
        val etFolderName: EditText = dialog.findViewById<EditText>(R.id.etFolderName)
        val tvSubTitle: TextView = dialog.findViewById<TextView>(R.id.tvSubTitle)

        ivUpload.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
        create.setOnClickListener { v: View? ->
            if (etFolderName.text.isNotEmpty() && pathFromUriList != null) {
                makeUserAlbumFromUriPath(pathFromUriList,etFolderName.text.toString())
                dialog.dismiss()
            }else{
                toast("Please enter name")
            }
        }
        cancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
    }
    fun showImagePop() {
        mBuilder.setContentView(R.layout.camera_dialog)
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleCamera).also {
            it.isVisible = !intent.hasExtra("permits")
            //     it.isVisible = false
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
        mBuilder.findViewById<TextView>(R.id.titleGallery).also {
            it.isVisible = !intent.hasExtra("permits")
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
    var resultLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            var uriList: ArrayList<Uri> = ArrayList<Uri>()
            uriList.clear()
            uriList.add(camera_image_uri)
            pathFromUriList=uriList
            if(dialog!=null)
            {
                try{
                    val ivBackground: ImageView = dialog.findViewById<ImageView>(R.id.ivBackground)
                    ivBackground.loadWallImage(camera_image_uri)
                }
                catch(e:Exception)
                {
                }
            }
        }
        else
        {
            Toast.makeText(applicationContext,"No Select", Toast.LENGTH_SHORT).show()
        }
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
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
                    pathFromUriList=uriList
                }
                else  {
                    uriList.add(result.data?.data!!)
                    pathFromUriList=uriList
                    setDialogBgImageOnResult()
                }

                try{
                    val ivBackground: ImageView = dialog.findViewById<ImageView>(R.id.ivBackground)
                    ivBackground.loadWallImage(pathFromUriList.get(0))
                }
                catch(e:Exception)
                {
                }
            }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun setDialogBgImageOnResult() {
        val ivBackground: ImageView = dialog.findViewById<ImageView>(R.id.ivBackground)
        if (pathFromUriList != null){
            ivBackground.loadWallImage(pathFromUriList.get(0)!!)
        }
    }

    private fun makeUserAlbumFromUriPath(uriList: ArrayList<Uri>,folderName:String) = try {
        var parts1: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        for(data in uriList)
        {
            var path=FilePath.getPath(applicationContext,data);
            var image=Constants.getRequestParam("image",path)
            parts1.add(image!!)
        }
        hashMapOf<String, RequestBody>().also {
            it.put("folder_name", RequestBody.create(MediaType.parse("multipart/form-data"),folderName))
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
               viewModel.usersAddAlbum(it,parts1)
            }
        }
    } catch (e: Exception) {
        Log.e("GalleryActivity", e.message.toString())
    }

    override fun onResume() {
        super.onResume()
        callApi()
    }

//    private fun saveCaptureImageResults(uriList: ArrayList<Uri>) = try {
//        var parts1: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
//        for(data in uriList)
//        {
//            var path= FilePath.getPath(applicationContext,data);
//            var image=Constants.getRequestParam("image",path)
//            parts1.add(image!!)
//        }
//        hashMapOf<String, RequestBody>().also {
//            it.put("_id", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getStringExtra("id").toString()))
//            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "image"))
//            presenter.addMultipleImgaes(it,parts1)
//        }
//
//    } catch (e: Exception) {
//        Log.e("GalleryActivity", e.message.toString())
//    }
}