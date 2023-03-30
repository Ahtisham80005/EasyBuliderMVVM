package com.tradesk.activity.galleryModule

import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.*
import com.tradesk.R
import com.tradesk.activity.ImageActivity
import com.tradesk.activity.galleryModule.adapter.AdditionalGallaryAdapter
import com.tradesk.activity.galleryModule.adapter.GallaryAdapterUpdated
import com.tradesk.activity.galleryModule.adapter.SubAdditionalGallaryAdapter
import com.tradesk.data.entity.MoveImagesInFolderModel
import com.tradesk.data.entity.MoveImagesJobToProfileModel
import com.tradesk.databinding.ActivitySubGallaryBinding
import com.tradesk.filemanager.checkStoragePermission
import com.tradesk.filemanager.requestStoragePermission
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.FileFinder
import com.tradesk.util.file.FilePath
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.toast
import com.tradesk.util.file.PathUtil
import com.tradesk.viewModel.GallaryViewModel
import dagger.hilt.android.AndroidEntryPoint
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
class SubGallaryActivity : AppCompatActivity(), SingleListCLickListener, LongClickListener, CustomCheckBoxListener,
    UnselectCheckBoxListener {

    var imageUriArray = ArrayList<Uri>()
    var selectedImageArray = ArrayList<String>()
    var selectedIdArray = ArrayList<String>()

    lateinit var camera_image_uri:Uri
    var jobsProjectNameArray = ArrayList<String>()
    var projectIdArray = ArrayList<String>()
    var userFolderNameArray = ArrayList<String>()
    val mListUserGallery = ArrayList<UsersAdditionalImageX>()

    var checkBoxVisibility:Boolean=false
    var allCheckBoxSelect:Boolean=false
    var activeSlectMenu:Boolean=false

    var mcheckBoxModelList=mutableListOf<CheckModel>()

    /*For gallary adapter*/
    var mListUpdatedAdditionalImage = ArrayList<LeadsDataImageClient>()
    val mAdditionalGallaryAdapter by lazy { AdditionalGallaryAdapter(this,this,mListUpdatedAdditionalImage,mListUpdatedAdditionalImage) }


    val mListAdditional = mutableListOf<AdditionalImageLeadDetail>()
    val mGallaryAdapterUpdated by lazy { GallaryAdapterUpdated(this,this,mListAdditional,this,this,this,checkBoxVisibility,allCheckBoxSelect,mcheckBoxModelList) }
    var mSubListUpdatedAdditionalImage = ArrayList<AdditionalImageImageClient>()
    val subAdditionalGallaryAdapter by lazy { SubAdditionalGallaryAdapter( mSubListUpdatedAdditionalImage,this,this,this,this,checkBoxVisibility,allCheckBoxSelect,mcheckBoxModelList) }

    var job_id = ""
    var image_id = ""
    var image_url = ""
    var to_job_id = ""
    var toIndex: Int? = null
    var isJobAlbumMenuClick = false

    lateinit var binding:ActivitySubGallaryBinding
    lateinit var viewModel:GallaryViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    lateinit var dialog: Dialog
    private var hasPermission = false
    val mBuilder: Dialog by lazy { Dialog(this@SubGallaryActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_sub_gallary)
        viewModel=ViewModelProvider(this).get(GallaryViewModel::class.java)
        callApi()
        Log.e("SubJobID",intent.getStringExtra("job_id").toString())
        try {
            callApi()
        }catch (e:Exception){
            e.printStackTrace()
        }
        binding.mIvRightMenue.visibility = View.VISIBLE
        binding.mIvBack.setOnClickListener { finish() }
        binding.mIvAddImage.setOnClickListener {
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
        binding.mIvRightMenue.setOnClickListener {
            if(!activeSlectMenu)
            {
                if (mSubListUpdatedAdditionalImage.isNotEmpty()){
                    showRightMenu(binding.mIvRightMenue)
                }
                else if(mListAdditional.isNotEmpty())
                {
                    showRightMenu(binding.mIvRightMenue)
                }
            }
            else
            {
                Log.e("imageArray 22",selectedImageArray.size.toString())
                Log.e("IdArray 22",selectedIdArray.size.toString())
                showSecondRightMenu(binding.mIvRightMenue)
            }
        }
        initObserve()
    }

    private fun callApi() {
        if (intent.hasExtra("additionalimages"))
        {
            job_id = intent.getStringExtra("job_id").toString()
            binding.mIvAddImage.visibility= View.VISIBLE
            binding.textView6.setText("Images")
            GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
                binding.rvGallary.layoutManager = this
//                reverseLayout=true
            }
            binding.rvGallary.adapter = mGallaryAdapterUpdated
            if (isInternetConnected(this@SubGallaryActivity)) {
                Constants.showLoading(this)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getLeadDetail(intent.getStringExtra("job_id").toString())
                }
            }
        }
        else{
            // For Gallery Profile
            job_id = intent.getStringExtra("job_id").toString()
            binding.textView6.text = intent.getStringExtra("title")

            binding.mIvAddImage.visibility = View.VISIBLE
            if (intent.getParcelableArrayListExtra<AdditionalImageImageClient>("imagelist") != null )
            {
                var allAdditionalImage = intent.getParcelableArrayListExtra<AdditionalImageImageClient>("imagelist") as ArrayList<AdditionalImageImageClient>
                mSubListUpdatedAdditionalImage.addAll(allAdditionalImage)
                allAdditionalImage.clear()
                title=intent.getStringExtra("title")!!
                var layoutmanager= GridLayoutManager(this, 2, RecyclerView.VERTICAL, false).apply {
                    binding.rvGallary.layoutManager = this
                }
                binding.rvGallary.adapter = subAdditionalGallaryAdapter
                mcheckBoxModelList.clear()
                for(i in mSubListUpdatedAdditionalImage)
                {
                    mcheckBoxModelList.add(CheckModel(false))
                }
                subAdditionalGallaryAdapter.notifyDataSetChanged()
            }
        }
        if (isInternetConnected(this)){
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getadditionalimagesjobs("1","100","image")
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
        viewModel.responseLeadDetailModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    setLeadData(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseMoveImagesJobToProfileSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Moved Successfully")
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
        viewModel.responseMoveAdditionalImagesSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Moved Successfully")
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
        viewModel.responseDeleteImagesSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Image deleted successfully")
//                    mListAdditionalImage.clear()
                    mGallaryAdapterUpdated.notifyDataSetChanged()
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
        viewModel.responseMoveImagesInFolderSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Moved Successfully")
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

    private fun setLeadData(it: LeadDetailModel) {
        if (intent.hasExtra("additionalimages")){
            if (it.data.leadsData.additional_images!=null) {
                Collections.reverse(it.data.leadsData.additional_images)
                mListAdditional.clear()
//                mListAdditionalImage.clear()
//                mListAdditionalImage.addAll(it.data.leadsData.additional_images.filter { it.status!!.equals("image") })
                //mListAdditional.addAll(it.data.leadsData.additional_images.filter { it.status!!.equals("image") })

                var allAdditionalImage= mutableListOf<AdditionalImageLeadDetail>()
                allAdditionalImage.addAll(it.data.leadsData.additional_images.filter { it.status!!.equals("image") })

                if(allAdditionalImage.size>10)
                {
                    for(i in 0..9)
                    {
                        mListAdditional.add(allAdditionalImage.get(i))
                        allAdditionalImage.removeAt(i)
                    }
                }
                else{
                    mListAdditional.addAll(allAdditionalImage)
                    allAdditionalImage.clear()
                }
                mcheckBoxModelList.clear()
                for(i in mListAdditional)
                {
                    mcheckBoxModelList.add(CheckModel(false))
                }
                mGallaryAdapterUpdated.notifyDataSetChanged()
            }
        }
    }

    private fun setImagesData(it: AdditionalImagesWithClientModel) {
        mListUpdatedAdditionalImage.clear()
        jobsProjectNameArray.clear()
        projectIdArray.clear()

        Collections.reverse(it.data.leadsData);
        mListUpdatedAdditionalImage.addAll(it.data.leadsData)
        mListUpdatedAdditionalImage.forEach {
            jobsProjectNameArray.add(it.project_name)
            projectIdArray.add(it._id)
        }
        Collections.reverse(jobsProjectNameArray)
        Collections.reverse(projectIdArray);

        Log.e("jobsProjectNameArray",jobsProjectNameArray.toString())

        mAdditionalGallaryAdapter.notifyDataSetChanged()

        mListUserGallery.clear()
        userFolderNameArray.clear()
        Collections.reverse(it.data.users.additional_images)
        mListUserGallery.addAll(it.data.users.additional_images)
        mListUserGallery.forEach {
            userFolderNameArray.add(it.folder_name)
        }
        Collections.reverse(userFolderNameArray)
    }

    override fun onCheckBoxClick(position: Int) {
        if (intent.hasExtra("additionalimages"))
        {
            selectedIdArray.add(mListAdditional[position]._id)
            selectedImageArray.add(mListAdditional[position].image)
            imageUriArray.add(mListAdditional[position].image.toUri())

            Log.e("J_id",mListAdditional[position]._id) //63ecbd8f0303fe4963eee984
            Log.e("J_image",mListAdditional[position].image) //https://tradesk.s3.us-east-2.amazonaws.com/docs/1676459407135.jpeg
            Log.e("J_imag_uri",mListAdditional[position].image)
        }
        else
        {
            selectedIdArray.add(mSubListUpdatedAdditionalImage[position]._id)
            selectedImageArray.add(mSubListUpdatedAdditionalImage[position].image)
            imageUriArray.add(mSubListUpdatedAdditionalImage[position].image.toUri())

            Log.e("G_id",mSubListUpdatedAdditionalImage[position]._id) //63ecbd8f0303fe4963eee984
            Log.e("G_image",mSubListUpdatedAdditionalImage[position].image) //https://tradesk.s3.us-east-2.amazonaws.com/docs/1676459407135.jpeg
            Log.e("G_imag_uri",mSubListUpdatedAdditionalImage[position].image)
        }
    }
    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
        if (intent.hasExtra("additionalimages"))
        {
            selectedIdArray.removeAll(setOf(mListAdditional[position]._id))
            selectedImageArray.removeAll(setOf(mListAdditional[position].image))
        }
        else
        {
            selectedIdArray.removeAll(setOf(mSubListUpdatedAdditionalImage[position]._id))
            selectedImageArray.removeAll(setOf(mSubListUpdatedAdditionalImage[position].image))
        }
    }

    override fun onLongClickListener(item: Any, position: Int) {
        showRightMenuOnLong(item as View, position)
    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Multi"))
        {
            startActivity(
                Intent(this, ImageActivity::class.java)
                    .putExtra("position",position)
                    .putExtra("title", "Job Images")
                    .putExtra("job_id", intent.getStringExtra("job_id"))
                    .putParcelableArrayListExtra(
                        "imagelist", mSubListUpdatedAdditionalImage as ArrayList<AdditionalImageImageClient>
                    )
            )
        }
    }

    fun showImagePop() {
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
                if (Build.VERSION.SDK_INT >= 30) {
                    if (!Environment.isExternalStorageManager())
                    {
                        val getpermission = Intent()
                        getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        startActivity(getpermission)
                    } else {
                        if (permissionFile.checkLocStorgePermission(this))
                        {
                            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                }
                else
                {
                    if (permissionFile.checkLocStorgePermission(this)) {
                        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
//                val intent = Intent()
//                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                intent.action = Intent.ACTION_GET_CONTENT
//                resultLauncher.launch(intent)
            }
        }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }
    val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(15)) { uris ->
        // Callback is invoked after the user selects media items or closes the
        // photo picker.
        if (uris.isNotEmpty()) {
            Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
            saveCaptureImageResults(uris!!)
        } else {
            toast("No media selected Permission not allow")
            Log.d("PhotoPicker", "No media selected")
        }
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

    private fun saveCaptureImageResults(uriList: List<Uri>) = try {
//        Toast.makeText(this,uriList.toString(),Toast.LENGTH_SHORT).show()
        Log.e("MYImages",uriList.toString())

        var parts1: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        for(data in uriList)
        {
            var path = FilePath.getPath(this@SubGallaryActivity,data)
            if(path==null)
            {
                path= PathUtil.getPath(this@SubGallaryActivity,data)
//                Log.e("My File 2",path)
            }
            if(path==null)
            {
                path= FileFinder.getFilePath(this@SubGallaryActivity,data)
//                Log.e("My File 3",path)
            }
            var image=Constants.getRequestParam("image",path)
            parts1.add(image!!)
        }

        hashMapOf<String, RequestBody>().also {
            it.put("_id", RequestBody.create(MediaType.parse("multipart/form-data"), intent.getStringExtra("job_id").toString()))
            it.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "image"))
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.addMultipleImgaes(it,parts1)
            }
        }
    } catch (e: Exception) {
        toast(e.message.toString())
        e.printStackTrace()
    }

    private fun showRightMenuOnLong(anchor: View,selectedPosition:Int): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_gallery_menu, popup.getMenu())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }
        popup.setOnDismissListener {
//            if (selectedPosition != null) {
//                selectedIdArray.removeAll(setOf(mSubListUpdatedAdditionalImage[selectedPosition]._id))
//                selectedImageArray.removeAll(setOf(mSubListUpdatedAdditionalImage[selectedPosition].image))
//            }
        }
        selectedIdArray.clear()
        selectedImageArray.clear()
        if (intent.hasExtra("additionalimages"))
        {
            selectedIdArray.add(mListAdditional[selectedPosition]._id)
            selectedImageArray.add(mListAdditional[selectedPosition].image)
        }
        else{
            selectedIdArray.add(mSubListUpdatedAdditionalImage[selectedPosition]._id)
            selectedImageArray.add(mSubListUpdatedAdditionalImage[selectedPosition].image)
        }
        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                if (selectedPosition != null) {
                    shareImage()
                } else {
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_move) {
                if (selectedPosition != null) {
                    if (isInternetConnected(this)){
                        showMoveItemToAlbumDialog()
                    }
                } else {
                    toast("Select an item")
                }
            }else if (it.itemId == R.id.item_delete){
                if (selectedPosition != null) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this) && selectedPosition != null) {
//                                itemPosition = selectedPosition
                                val selectedImageIds = SelectedImageIds(job_id,selectedIdArray)
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedGallery(selectedImageIds)
                                }
//                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                }else{
                    toast("Select an item")
                }

            } else if (it.itemId == R.id.item_download) {
                if (selectedPosition != null) {
                    selectedIdArray.zip(selectedImageArray).forEach { pair ->
                        downloadImage(pair.first, pair.second)
                    }
                } else {
                    toast("Select an item")
                }

            } else if (it.itemId == R.id.item_make_an_album) {
                showCreateAlbumDialog()
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }
    private fun showMoveItemToAlbumDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.move_gallery_item_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window: Window? = dialog.window
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
        dialog.window
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

        val cancel: Button = dialog.findViewById<Button>(R.id.cancel_button)
        val create: Button = dialog.findViewById<Button>(R.id.done_button)
        val ivDownArrow: ImageView = dialog.findViewById<ImageView>(R.id.ivDownArrow)
        val tvSelectAlbum: TextView = dialog.findViewById<TextView>(R.id.tvSelectAlbum)

        var hashMapString = HashMap<String,String>()

//        selectedIdArray.add(mSubListUpdatedAdditionalImage[slectedPosition]._id)
//        selectedImageArray.add(mSubListUpdatedAdditionalImage[slectedPosition].image)

//        if (slectedPosition != null){
        jobsProjectNameArray.zip(projectIdArray).forEach { pair ->
            hashMapString.put(pair.first,pair.second)
        }
//        }

//        if (slectedPosition != null){
        selectedIdArray.zip(selectedImageArray).forEach { pair ->
            image_id = pair.first
            image_url = pair.second
        }
//        }
        ivDownArrow.setOnClickListener {
            showMenuPopup(tvSelectAlbum,jobsProjectNameArray,userFolderNameArray, hashMapString)
        }

        create.setOnClickListener { v: View? ->
            if (tvSelectAlbum.text.isNotEmpty() && job_id.isNotEmpty()
                && to_job_id.isNotEmpty() && image_id.isNotEmpty()
                && image_url.isNotEmpty()){

//                selectedIdArray.add(mSubListUpdatedAdditionalImage[slectedPosition]._id)
//                selectedImageArray.add(mSubListUpdatedAdditionalImage[slectedPosition].image)

                val moveAdditionalImagesModel = MoveAdditionalImagesModel(job_id,to_job_id,selectedIdArray,selectedImageArray)
                val moveImagesJobToProfileModel = MoveImagesJobToProfileModel(job_id,selectedImageArray, selectedIdArray, toIndex!!)

                if (isInternetConnected(this)) {
                    if (isJobAlbumMenuClick) {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.moveAdditionalImages(moveAdditionalImagesModel)
                        }
                        dialog.dismiss()
                    } else {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.usersMoveImagesJobToProfile(moveImagesJobToProfileModel)
                        }
                        dialog.dismiss()
                    }
                }
            }else{
                toast("select album")
            }
        }
        cancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
    }
    private fun showCreateAlbumDialog() {
        Log.e("imageArray 5",selectedImageArray.size.toString())
        Log.e("IdArray 5",selectedIdArray.size.toString())
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.make_gallery_item_album_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window: Window? = dialog.window
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
        val create: Button = dialog.findViewById<Button>(R.id.done_button)
        val etFolderName: EditText = dialog.findViewById<EditText>(R.id.etFolderName)

        create.setOnClickListener { v: View? ->
            if (etFolderName.text.isNotEmpty()) {
                Log.e("imageArray 6",selectedImageArray.size.toString())
                Log.e("IdArray 6",selectedIdArray.size.toString())
                val moveImagesInFolderModel = MoveImagesInFolderModel(job_id,selectedImageArray, selectedIdArray,etFolderName.text.toString())
                if (isInternetConnected(this)) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                       viewModel.moveImagesInFolder(moveImagesInFolderModel)
                    }
                    dialog.dismiss()
                }
            }else{
                toast("write name")
            }
        }
        cancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
    }
    fun showRightMenu(anchor: View ): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.sub_gallery_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{

            if (it.itemId == R.id.item_select_items){
                activeSlectMenu=true
                selectedIdArray.clear()
                selectedImageArray.clear()
                checkBoxVisibility=true
                if (intent.hasExtra("additionalimages"))
                {
                    //From Job
                    mcheckBoxModelList.clear()
                    for(i in mListAdditional)
                    {
                        mcheckBoxModelList.add(CheckModel(false))
                    }
                    mGallaryAdapterUpdated.checkboxVisibility=true
                    mGallaryAdapterUpdated.notifyDataSetChanged()
                }
                else{
                    //From Gallery
                    mcheckBoxModelList.clear()
                    for(i in mSubListUpdatedAdditionalImage)
                    {
                        mcheckBoxModelList.add(CheckModel(false))
                    }
                    subAdditionalGallaryAdapter.checkboxVisibility=true
                    subAdditionalGallaryAdapter.notifyDataSetChanged()
                }


            }
            else if (it.itemId == R.id.item_select_all){
                checkBoxVisibility=true
                activeSlectMenu=true
                selectedIdArray.clear()
                selectedImageArray.clear()

                if (intent.hasExtra("additionalimages"))
                {
                    //From Job
                    mcheckBoxModelList.clear()
                    for(i in mListAdditional)
                    {
                        mcheckBoxModelList.add(CheckModel(true))
                    }
                    mGallaryAdapterUpdated.checkboxVisibility=true
                    mGallaryAdapterUpdated.allCheckBoxSelect=true
                    //No Add List
                    mGallaryAdapterUpdated.notifyDataSetChanged()
                }
                else{
                    //From Gallery
                    mcheckBoxModelList.clear()
                    for(i in mSubListUpdatedAdditionalImage)
                    {
                        mcheckBoxModelList.add(CheckModel(true))
                    }
                    subAdditionalGallaryAdapter.checkboxVisibility=true
                    subAdditionalGallaryAdapter.allCheckBoxSelect=true
                    //No Add List
                    Log.e("imageArray 1",selectedImageArray.size.toString())
                    Log.e("IdArray 1",selectedIdArray.size.toString())
                    subAdditionalGallaryAdapter.notifyDataSetChanged()
                }
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }
    private fun showSecondRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_gallery_menu, popup.getMenu())
        if (selectedIdArray.size > 1) {
            popup.menu.findItem(R.id.item_share).isVisible = false
        }
        Log.e("imageArray 3",selectedImageArray.size.toString())
        Log.e("IdArray 3",selectedIdArray.size.toString())
        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                if (!selectedIdArray.isEmpty()) {
                        shareImage()
                } else {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_move) {
                if (!selectedIdArray.isEmpty())
                {
                    if (isInternetConnected(this)) {
                        //  presenter.getadditionalimagesjobs("1","30","image")
                        showMoveItemToAlbumDialog()
                    }
                }
                else
                {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_delete) {
                if (!selectedIdArray.isEmpty()) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
//                            if (isInternetConnected() && selectedPosition != null && selectionResult >= 1) {
                            val selectedImageIds = SelectedImageIds(job_id,selectedIdArray)
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteSelectedGallery(selectedImageIds)
                            }
//                        Log.d(TAG, "showRightMenu: "+selectedIdArray)
//                            }
                        })
                }
                else{
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_download) {
                 if (!selectedIdArray.isEmpty()) {
                     selectedIdArray.zip(selectedImageArray).forEach { pair ->
                         downloadImage(pair.first, pair.second)
                     }
                 }
                    else{
                        toast("Select an item")
                    }
            }
            else if (it.itemId == R.id.item_make_an_album) {
                    if (!selectedIdArray.isEmpty()) {
                        Log.e("imageArray 4",selectedImageArray.size.toString())
                        Log.e("IdArray 4",selectedIdArray.size.toString())
                        showCreateAlbumDialog()
                     }
                    else{
                        toast("Select an item")
                    }
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }
    private fun shareImage() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
//            mSubListUpdatedAdditionalImage[selectedPosition].image
            selectedImageArray.get(0)
        )
        startActivity(Intent.createChooser(shareIntent, "Share link using"))
    }
    private fun downloadImage(filename: String, downloadUrlOfImage: String) {
        try {
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator.toString() + filename + ".jpg"
                )
            dm.enqueue(request)
            Toast.makeText(this, "Image download started.", Toast.LENGTH_SHORT).show()
            //   finish()
        } catch (e: Exception) {
            toast("Image download failed.")
        }

    }

    private fun showMenuPopup(textView: TextView,  arrayListJobsFolder: ArrayList<String>,
                              arrayListUserFolder: ArrayList<String>,
                              hashMap: HashMap<String,String>) {
        val popupMenu = PopupMenu(this, textView)
        popupMenu.menuInflater.inflate(R.menu.move_folder_gallery_menu, popupMenu.menu)

        for (i in 0 until arrayListJobsFolder.size) {
            popupMenu.menu.findItem(R.id.item_jobs_folder).subMenu!!.add(arrayListJobsFolder[i])
        }
        for (i in 0 until arrayListUserFolder.size) {
            popupMenu.menu.findItem(R.id.item_user_folder).subMenu!!.add(arrayListUserFolder[i])
        }

        popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                textView.text = item.title
                to_job_id = hashMap[item.title].toString()

//                Log.d(TAG, "onMenuItemClick: " + item.title)
//                Log.d(TAG, "onMenuItemClick: " + to_job_id)
//
//                Log.d(TAG, "arrayListJobsFolder: " +arrayListJobsFolder.indexOf(item.title))
//                Log.d(TAG, "arrayListUserFolder: " +arrayListUserFolder.indexOf(item.title))

                toIndex = arrayListUserFolder.indexOf(item.title)
                if (item.itemId == R.id.item_jobs_folder) {
                    isJobAlbumMenuClick = true
                }else if (item.itemId == R.id.item_user_folder){
                    isJobAlbumMenuClick = false
                }

                return false
            }
        })
        popupMenu.show()
    }




}