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
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.*
import com.tradesk.R
import com.tradesk.activity.ImageActivity
import com.tradesk.activity.galleryModule.adapter.SubGallaryUserAdapter
import com.tradesk.data.entity.MoveImagesInFolderModel
import com.tradesk.databinding.ActivitySubGallaryUserBinding
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
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class SubGallaryUserActivity : AppCompatActivity() , SingleListCLickListener, LongClickListener,
    CustomCheckBoxListener,
    UnselectCheckBoxListener {

    lateinit var camera_image_uri:Uri
    var jobsProjectNameArray = ArrayList<String>()
    var projectIdArray = ArrayList<String>()
    var userFolderNameArray = ArrayList<String>()
    var mListUserGallery = ArrayList<String>()
    var subGallaryUserAdapter: SubGallaryUserAdapter? = null
    var additionalImageData = ArrayList<LeadsDataImageClient>()
    val mListUserGalleryFolderName = ArrayList<UsersAdditionalImageX>()

    var imageUriArray = ArrayList<Uri>()
    var selectedImageArray = ArrayList<String>()
    var selectedIdArray = ArrayList<String>()

    var checkBoxVisibility:Boolean=false
    var allCheckBoxSelect:Boolean=false
    var activeSlectMenu:Boolean=false

    var image_id = ""
    var image_url = ""
    var to_job_id = ""
    var toIndex: Int? = null
    var index: Int? = null
    var isJobAlbumMenuClick = false
    var folderName = ""
    var id = ""
    var job_folder : String? = null


    lateinit var dialog: Dialog
    val mBuilder: Dialog by lazy { Dialog(this@SubGallaryUserActivity) }

    lateinit var binding: ActivitySubGallaryUserBinding
    lateinit var viewModel: GallaryViewModel
    @Inject
    lateinit var permissionFile: PermissionFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_sub_gallary_user)
        viewModel= ViewModelProvider(this).get(GallaryViewModel::class.java)
        setRv()
        clickEvents()
        initObserve()
    }
    private fun setRv(){
        folderName = intent.getStringExtra("folderName").toString()
        binding.tvTitle.text = folderName.capitalize()
        id = intent.getStringExtra("id").toString()
        index = intent.getIntExtra("index",-1)
        var allAdditionalImage = intent.getStringArrayListExtra("imagelist") as ArrayList<String>
//        Log.d(TAG, "setRv: "+mListUserGallery)
        subGallaryUserAdapter = SubGallaryUserAdapter(this,this,mListUserGallery,this,this,this,checkBoxVisibility,allCheckBoxSelect)
        GridLayoutManager(this,   2,  RecyclerView.VERTICAL,   false).apply {
            binding.rvSelectItemUserGallery.layoutManager = this
        }
        binding.rvSelectItemUserGallery.adapter= subGallaryUserAdapter
        mListUserGallery.addAll(allAdditionalImage!!)
        allAdditionalImage.clear()
//        }
        subGallaryUserAdapter!!.notifyDataSetChanged()

        if (isInternetConnected(this)) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getadditionalimagesjobs("1", "30", "image")
            }
        }
    }

    private fun clickEvents(){
        binding.ivBack.setOnClickListener { finish() }
        binding.mIvRightMenuUserGallery.setOnClickListener {
            if(!activeSlectMenu)
            {
                if (mListUserGallery.isNotEmpty()){
                    showRightMenu(it)
                }
            }
            else
            {
                showSecondRightMenu(it)
            }
        }
        binding.mIvAddUserImage.setOnClickListener {
            if (permissionFile.checkLocStorgePermission(this)) {
                showImagePop()
            }
        }
    }

    fun initObserve() {
        viewModel.responseUsersAddAdditionalImagesSuccessModel.observe(this, androidx.lifecycle.Observer {it->
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
        viewModel.responsAdditionalImages.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    setData(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseUserDelSelectedImagesSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Image deleted successfully")
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
        viewModel.responseUserMoveImagesUserToJobSuccessModel.observe(this, androidx.lifecycle.Observer {it->
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
        viewModel.responseUserMoveImagesUserToUserSuccessModel.observe(this, androidx.lifecycle.Observer {it->
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

    private fun setData(it: AdditionalImagesWithClientModel) {
        additionalImageData.clear()
        jobsProjectNameArray.clear()
        projectIdArray.clear()
        additionalImageData.addAll(it.data.leadsData)
        additionalImageData.forEach {
            jobsProjectNameArray.add(it.project_name)
            projectIdArray.add(it._id)
        }
        mListUserGalleryFolderName.clear()
        userFolderNameArray.clear()
        mListUserGalleryFolderName.addAll(it.data.users.additional_images)
        mListUserGalleryFolderName.forEach {
            userFolderNameArray.add(it.folder_name)
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
//            it.isVisible = !intent.hasExtra("permits")
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

//                // dispatchTakeGalleryIntent()
////                ImagePicker.with(this)
////                    .galleryOnly()
////                    .compress(1024)
////                    .maxResultSize(1080, 1080)
////                    .start()
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
        }
    }
    var resultLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
//            Toast.makeText(this,"Good", Toast.LENGTH_SHORT).show()
            var uriList: ArrayList<Uri> = ArrayList<Uri>()
            uriList.add(camera_image_uri)
            saveCaptureImageResults(uriList!!)
        }
        else
        {
            Toast.makeText(applicationContext,"No Select", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCaptureImageResults(uriList: List<Uri>) = try {
        Log.e("List Size final",uriList.size.toString())
        var parts1: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
//        for(data in uriList)
//        {
//            var path= FilePath.getPath(applicationContext,data)
//            Log.e("file Path",path.toString())
//            var image=Constants.getRequestParam("image",path)
//            parts1.add(image!!)
//        }

        for(data in uriList)
        {
            var path = FilePath.getPath(this@SubGallaryUserActivity,data)
            if(path==null)
            {
                path= PathUtil.getPath(this@SubGallaryUserActivity,data)
//                Log.e("My File 2",path)
            }
            if(path==null)
            {
                path= FileFinder.getFilePath(this@SubGallaryUserActivity,data)
//                Log.e("My File 3",path)
            }
            var image=Constants.getRequestParam("image",path)
            parts1.add(image!!)
        }



        hashMapOf<String, RequestBody>().also {
//            it.put("image\"; filename=\"image.jpg", RequestBody.create(MediaType.parse("image/*"), file))
            it.put("index", RequestBody.create(
                MediaType.parse("multipart/form-data"),
                intent.getIntExtra("index",-1).toString()))
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.usersAddAdditionalImages(it,parts1)
            }
        }
    } catch (e: Exception) {
        toast(e.message.toString())
        Log.e("GalleryActivity", e.message.toString())
    }

    fun showRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.sub_gallery_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{

            if (it.itemId == R.id.item_select_items){
                selectedIdArray.clear()
                selectedImageArray.clear()
                activeSlectMenu=true
                checkBoxVisibility=true
                subGallaryUserAdapter!!.checkboxVisibility=true
                subGallaryUserAdapter!!.notifyDataSetChanged()

            }else if (it.itemId == R.id.item_select_all){
                selectedIdArray.clear()
                selectedImageArray.clear()
                checkBoxVisibility=true
                subGallaryUserAdapter!!.checkboxVisibility=true
                subGallaryUserAdapter!!.allCheckBoxSelect=true
                subGallaryUserAdapter!!.notifyDataSetChanged()
                activeSlectMenu=true
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun showSecondRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_gallery_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share)
            {
                if (!selectedIdArray.isEmpty()) {
                    shareImage()
                }
                else{
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_move)
            {
                if (!selectedIdArray.isEmpty()) {
                    showMoveItemToAlbumDialog()
                }
                else{
                    toast("Select an item")
                }

            }
            else if (it.itemId == R.id.item_delete)
            {
                if (!selectedIdArray.isEmpty()) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
//                            if (isInternetConnected() && selectedPosition != null && selectionResult >= 1) {
                            val selectedUrlsUser = SelectedUrlsUser(index!!, selectedImageArray)
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.userDelSelectedImages(selectedUrlsUser)
                            }

//                        Log.d(TAG, "showRightMenu: " + selectedImageArray)
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
                        Log.e("File Name",pair.first)
                        Log.e("File URL",pair.second)
                        downloadImage(pair.first, pair.second)
                    }
                }
                else{
                    toast("Select an item")
                }



//                selectedIdArray.zip(selectedImageArray).forEach { pair ->
//                    downloadImage(pair.first, pair.second)
//                }
            } else if (it.itemId == R.id.item_make_an_album) {
                if (!selectedIdArray.isEmpty()) {
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

        var hashMapString = HashMap<String, String>()
        jobsProjectNameArray.zip(projectIdArray).forEach { pair ->
            hashMapString.put(pair.first, pair.second)
        }
        selectedIdArray.zip(selectedImageArray).forEach { pair ->
            image_id = pair.first
            image_url = pair.second
        }
        ivDownArrow.setOnClickListener {
            showMenuPopup(tvSelectAlbum, jobsProjectNameArray, userFolderNameArray, hashMapString)
        }
        create.setOnClickListener { v: View? ->
            if (tvSelectAlbum.text.isNotEmpty()) {
                val moveImagesUserToJob =
                    MoveImagesUserToJob("job_folder", index!!, to_job_id!!,
                        selectedImageArray)
                val moveImagesUserToUser =
                    MoveImagesUserToUser("user_folder",index!!,toIndex!!,
                        selectedImageArray)
                if (isInternetConnected(this)) {
                    if (isJobAlbumMenuClick){
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.userMoveImagesUserToJob(moveImagesUserToJob)
                        }
                        dialog.dismiss()
                    }else{
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.userMoveImagesUserToUser(moveImagesUserToUser)
                        }
                        dialog.dismiss()
                    }
                }
            } else {
                toast("select album")
            }
        }
        cancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
    }

    override fun onSingleListClick(item: Any, position: Int) {
        startActivity(Intent(this, ImageActivity::class.java)
            .putExtra("title","subUser")
            .putExtra("position", position)
            .putStringArrayListExtra("imagelist", mListUserGallery as ArrayList<String>)
        )
    }
    override fun onLongClickListener(item: Any, position: Int) {
        id = intent.getStringExtra("id").toString()
        showRightMenuOnLong(item as View,position)
    }
    override fun onCheckBoxClick(position: Int) {
//        selectedIdArray.add(mListAdditional[position]._id)
        selectedIdArray.add(id)
        selectedImageArray.add(mListUserGallery[position])

    }
    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
        selectedIdArray.removeAll(setOf(id))
        selectedImageArray.removeAll(setOf(mListUserGallery[position]))
    }

    private fun showMenuPopup(
        textView: TextView, arrayListJobsFolder: ArrayList<String>,
        arrayListUserFolder: ArrayList<String>,
        hashMap: HashMap<String, String>
    )
    {
        val popupMenu = PopupMenu(this, textView)
        popupMenu.menuInflater.inflate(R.menu.move_folder_gallery_menu, popupMenu.menu)

        for (i in 0 until arrayListJobsFolder.size) {
            //  popupMenu.menu.add(arrayList[i])
            popupMenu.menu.findItem(R.id.item_jobs_folder).subMenu!!.add(arrayListJobsFolder[i])
        }
        for (i in 0 until arrayListUserFolder.size) {
            popupMenu.menu.findItem(R.id.item_user_folder).subMenu!!.add(arrayListUserFolder[i])
        }

        popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                textView.text = item.title
                job_folder = item.title.toString()
                to_job_id = hashMap[item.title].toString()

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

    private fun showRightMenuOnLong(anchor: View,selectedPosition:Int): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_gallery_menu, popup.getMenu())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

        popup.setOnDismissListener {
//            if (selectedPosition != null) {
//                selectedIdArray.removeAll(setOf(mListUserGallery[selectedPosition].substringAfterLast("/")))
//                selectedImageArray.removeAll(setOf(mListUserGallery[selectedPosition]))
//            }
        }

        selectedIdArray.clear()
        selectedImageArray.clear()
        selectedIdArray.add(mListUserGallery[selectedPosition].substringAfterLast("/"))
        selectedImageArray.add(mListUserGallery[selectedPosition])
        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                if (selectedPosition != null) {
//                    shareImage(selectedPosition)
                    shareImage()
                } else {
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_move) {
                if (selectedPosition != null) {
                    if (isInternetConnected(this)) {
                        showMoveItemToAlbumDialog()
                    }
                } else {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_delete) {
                if (selectedPosition != null) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this) && selectedPosition != null) {
//                                selectedImageArray.add(mListUserGallery[selectedPosition])
                                Log.e("index",index!!.toString()) //1
                                Log.e("image Array",selectedImageArray.toString())
                                val selectedUrlsUser = SelectedUrlsUser(index!!, selectedImageArray)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.userDelSelectedImages(selectedUrlsUser)
                                }
//                                Log.d(TAG, "showRightMenu: " + selectedImageArray)
                            }
                        })

                } else {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_download) {
                if (selectedPosition != null) {
                    selectedIdArray.zip(selectedImageArray).forEach { pair ->
                        Log.e("File Name",pair.first)
                        Log.e("File URL",pair.second)
                        downloadImage(pair.first, pair.second)
                    }
                } else {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_make_an_album) {
                showCreateAlbumDialog()
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun showCreateAlbumDialog() {
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
                Log.e("selectIDArray",selectedIdArray.toString())
                Log.e("selectImageArray",selectedImageArray.toString())
                Log.e("folder", etFolderName.text.toString())
                Log.e("id",id)
                val moveImagesInFolderModel = MoveImagesInFolderModel(id, selectedImageArray, selectedIdArray, etFolderName.text.toString())
                if (isInternetConnected(this)) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.moveImagesInFolder(moveImagesInFolderModel)
                    }
                    dialog.dismiss()
                }
            } else {
                toast("write name")
            }
        }

        cancel.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
    }
    private fun shareImage() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
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
            Toast.makeText(this, "Image download started 3.", Toast.LENGTH_SHORT).show()
            // finish()
        } catch (e: Exception) {
            toast("Image download failed.")
        }
    }

}