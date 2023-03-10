package com.tradesk.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.shrikanthravi.collapsiblecalendarview.data.CalendarAdapter
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.CalendarDetailModel
import com.tradesk.Model.RemainderData
import com.tradesk.R
import com.tradesk.activity.SettingsActivity
import com.tradesk.activity.calendarModule.CalendarActivity
import com.tradesk.adapter.CalendarItemsAdapter
import com.tradesk.databinding.FragmentCalendarBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.calendar.CollapsibleCalendarCustom
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.CalendarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CalendarFragment : Fragment() , SingleListCLickListener, SingleItemCLickListener,
    LongClickListener {

    @Inject
    lateinit var mPrefs: PreferenceHelper
    val mInfoBuilderTwo: Dialog by lazy { Dialog(requireActivity()) }
    private val lastDayInCalendar = Calendar.getInstance(Locale.ENGLISH)
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)

    // current date
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]

    // selected date
    private var selectedDay: Int? = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear

    private var isEditClick = false
    var selected_position=0

    // all days in month
    private val dates = ArrayList<Date>()

    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }

    val mList = mutableListOf<RemainderData>()
    val mHomeLeadsAdapter by lazy { CalendarItemsAdapter(requireActivity(),this,this,mList) }

    lateinit var binding: FragmentCalendarBinding
    lateinit var viewModel: CalendarViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProvider(requireActivity()).get(CalendarViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCalendarBinding.inflate(inflater, container, false)
        Constants.showLoading(requireActivity())
        setUp()
        initObserve()
        return binding.getRoot()
    }

    fun setUp() {
        binding.mIvSettingsLead.setOnClickListener {
            startActivity(
                Intent(requireActivity(), SettingsActivity::class.java)
            )
        }
        val tz = TimeZone.getDefault()

        /**
         * Adding SnapHelper here, but it is not needed. I add it just to looks better.
         */
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarRecyclerView)

        /**
         * This is the maximum month that the calendar will display.
         * I set it for 6 months, but you can increase or decrease as much you want.
         */
        lastDayInCalendar.add(Calendar.MONTH, 6)

        setUpCalendar()

        /**
         * Go to the previous month. First, make sure the current month (cal)
         * is after the current date so that you can't go before the current month.
         * Then subtract  one month from the sludge. Finally, ask if cal is equal to the current date.
         * If so, then you don't want to give @param changeMonth, otherwise changeMonth as cal.
         */
        binding.mIvPrevious!!.setOnClickListener {
//            if (cal.after(currentDate)) {
//                cal.add(Calendar.MONTH, -1)
//                if (cal == currentDate)
//                    setUpCalendar()
//                else
//                    setUpCalendar(changeMonth = cal)
//            }

            binding.viewCal.prevMonth()
        }

        /**
         * Go to the next month. First check if the current month (cal) is before lastDayInCalendar,
         * so that you can't go after the last possible month. Then add one month to cal.
         * Then put @param changeMonth.
         */
        binding.mIvNext!!.setOnClickListener {
//            if (cal.before(lastDayInCalendar)) {
//                cal.add(Calendar.MONTH, 1)
//                setUpCalendar(changeMonth = cal)
//            }
            binding.viewCal.nextMonth()
        }

        binding.mIvAddLead.setOnClickListener { startActivity(Intent(activity, CalendarActivity::class.java)) }
        binding.mIvAddCal.setOnClickListener { showFollowUpPop() }

        binding.rvCalendarItems.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding.rvCalendarItems.adapter = mHomeLeadsAdapter

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val todaydate = sdf.format(Date())
        System.out.println(" C DATE is  "+currentDate)

//        if (isInternetConnected()){
//            presenter.getremindersdate(todaydate,"1","20",tz.id)
//        }

        binding.viewCal.state = 0

        binding.viewCal.setCalendarListener(object: CollapsibleCalendarCustom.CalendarListener{
            override fun onClickListener() {
            }
            override fun onDataUpdate() {
            }
            override fun onDayChanged() {
            }
            override fun onDaySelect() {
                selectedYear = binding.viewCal.selectedDay?.year ?: 0
                selectedMonth = (binding.viewCal.month).plus(1)
                selectedDay = binding.viewCal.selectedDay?.day ?: 0
//                presenter.getremindersdate("$selectedYear-$selectedMonth-$selectedDay 00:00:00","1","100",tz.id)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getremindersdate("$selectedYear-$selectedMonth-$selectedDay 00:00:00","1","100","America/Los_Angeles")
                }
            }

            override fun onItemClick(v: View) {

            }

            override fun onMonthChange() {
                val arr = resources.getStringArray(R.array.months_name)
                binding.mTvCurrentMonth.text = "${arr[binding.viewCal.month]} ${binding.viewCal.year}"

                getRemindersData()
            }
            override fun onWeekChange(position: Int) {
            }

        })

        getRemindersData()
    }

    private fun getRemindersData(date:Int=1,type:Int=2){
        if (isInternetConnected(requireActivity())) {
            val tz = TimeZone.getDefault()

            val temp = binding.viewCal.month+1
            val month = if(temp < 10) {
                "0${temp}"
            } else {
                "$temp"
            }

            val date = "${binding.viewCal.year}-${month}-0${date}"
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.calendardetail(tz.id, date, "month")
            }
        }
    }

    fun initObserve()
    {
        viewModel.responsCalendarDetailModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                   setList(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseDeleteReminderSuccessModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message.toString())
                    mInfoBuilderTwo.dismiss()
                    val tz = TimeZone.getDefault()
                    if (isInternetConnected(requireActivity())){
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val todaydate = sdf.format(Date())
                        val date = "$selectedYear-$selectedMonth-$selectedDay"
                        System.out.println(" C DATE is  "+currentDate)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getremindersdate(date+ " 00:00:00","1","20",tz.id)
                        }
                        getRemindersData()
                    }
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responsGetRemindersModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.remainderData)
                    mHomeLeadsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responsAddRemindersSuccessModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.message.toString())
                    mInfoBuilderTwo.dismiss()
                    val tz = TimeZone.getDefault()
                    if (isInternetConnected(requireActivity())){
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val todaydate = sdf.format(Date())
                        val date = "$selectedYear-$selectedMonth-$selectedDay"
                        System.out.println(" C DATE is  "+currentDate)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getremindersdate(date+ " 00:00:00","1","20",tz.id)
                        }
                        getRemindersData()
                    }
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responsEditRemindersSuccessModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message.toString())
                    mInfoBuilderTwo.dismiss()
                    val tz = TimeZone.getDefault()
                    if (isInternetConnected(requireActivity())){
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val todaydate = sdf.format(Date())
                        val date = "$selectedYear-$selectedMonth-$selectedDay"
                        System.out.println(" C DATE is  "+currentDate)
                        CoroutineScope(Dispatchers.IO).launch {
                           viewModel.getremindersdate(date+ " 00:00:00","1","20",tz.id)
                        }
                        getRemindersData()
                    }
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

    private fun setList(it: CalendarDetailModel) {
        it.data.remainderData.forEach { data ->
            val date = data.dateTime
            val arr = date.split("-").toTypedArray()
            val arr2 = arr[2].split(" ")
            val year = arr[0].toInt()
            val month = arr[1].toInt().minus(1)
            val day = arr2[0].toInt()
            binding.viewCal.addEventTag(year, month, day, resources.getColor(R.color.colorPrimary))

        }
    }


    /**
     * @param changeMonth I am using it only if next or previous month is not the current month
     */
    private fun setUpCalendar(changeMonth: Calendar? = null) {
        binding.mTvCurrentMonth!!.text = sdf.format(cal.time)
        binding.textView47!!.text = sdf.format(cal.time).substringAfter(" ")
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        /**
         *
         * If changeMonth is not null, then I will take the day, month, and year from it,
         * otherwise set the selected date as the current date.
         */
        selectedDay =
            when {
                changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
                else -> currentDay
            }
        selectedMonth =
            when {
                changeMonth != null -> changeMonth[Calendar.MONTH]
                else -> currentMonth
            }
        selectedYear =
            when {
                changeMonth != null -> changeMonth[Calendar.YEAR]
                else -> currentYear
            }
        var currentPosition = 0
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        /**
         * Fill dates with days and set currentPosition.
         * currentPosition is the position of first selected day.
         */
        while (dates.size < maxDaysInMonth) {
            // get position of selected day
            if (monthCalendar[Calendar.DAY_OF_MONTH] == selectedDay)
                currentPosition = dates.size
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Assigning calendar view.
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.calendarRecyclerView!!.layoutManager = layoutManager
        val calendarAdapter = com.tradesk.activity.calendarModule.CalendarAdapter(requireActivity(),dates, currentDate, changeMonth)
        binding.calendarRecyclerView!!.adapter = calendarAdapter

        /**
         * If you start the application, it centers the current day, but only if the current day
         * is not one of the first (1, 2, 3) or one of the last (29, 30, 31).
         */
        when {
            currentPosition > 2 -> binding.calendarRecyclerView!!.scrollToPosition(currentPosition - 3)
            maxDaysInMonth - currentPosition < 2 -> binding.calendarRecyclerView!!.scrollToPosition(currentPosition)
            else -> binding.calendarRecyclerView!!.scrollToPosition(currentPosition)
        }

        /**
         * After calling up the OnClickListener, the text of the current month and year is changed.
         * Then change the selected day.
         */
        calendarAdapter.setOnItemClickListener(object : com.tradesk.activity.calendarModule.CalendarAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clickCalendar = Calendar.getInstance()
                clickCalendar.time = dates[position]
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]
                var newselectedMonth = 0
                if (clickCalendar[Calendar.MONTH]==12){
                    newselectedMonth = 1
                }else{
                    newselectedMonth = clickCalendar[Calendar.MONTH] + 1
                }

                var newselectedYear = clickCalendar[Calendar.YEAR]

//                toast(newselectedYear.toString() + "-"+newselectedMonth.toString()+"-" + selectedDay.toString())

                val tz = TimeZone.getDefault()
                if (isInternetConnected(requireActivity())){
                    if (newselectedMonth.toString().length==1) {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getremindersdate("$newselectedYear-0$newselectedMonth-$selectedDay 00:00:00","1","20",tz.id)
                        }
                    }else if (selectedDay.toString().length==1){
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getremindersdate("$newselectedYear-$newselectedMonth-0$selectedDay 00:00:00","1","20",tz.id)
                        }
                    }else if(newselectedMonth.toString().length==1 && selectedDay.toString().length==1){
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getremindersdate("$newselectedYear-0$newselectedMonth-0$selectedDay 00:00:00","1","20",tz.id)
                        }
                    }else{
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getremindersdate("$newselectedYear-$newselectedMonth-$selectedDay 00:00:00","1","20",tz.id)
                        }
                    }



                }
            }
        })
    }

    fun showFollowUpPop() {
        mInfoBuilderTwo.setContentView(R.layout.popup_addingcalender);
        val displayMetrics = DisplayMetrics()
        mInfoBuilderTwo.window!!.attributes.windowAnimations = R.style.DialogAnimationNew
        mInfoBuilderTwo.window!!.setGravity(Gravity.BOTTOM)
        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        mInfoBuilderTwo.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
            (displayMetrics.widthPixels * 0.99).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        var _pickedTime=""
        var _pickedDate=""
        var selected=""

        if (isEditClick){
            mInfoBuilderTwo.findViewById<EditText>(R.id.mEtDescription).setText(mList[selected_position].description)
        }

        mInfoBuilderTwo.findViewById<TextView>(R.id.mTvCallReminder).setOnClickListener {

            val c2 = Calendar.getInstance()
            var mHour = c2[Calendar.HOUR_OF_DAY]
            var mMinute = c2[Calendar.MINUTE]
            _pickedDate = "$selectedYear-$selectedMonth-$selectedDay"

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                requireActivity(),R.style.TimePicker,
                { view, hourOfDay, minute ->
                    mHour = hourOfDay
                    mMinute = minute
                    if (hourOfDay.toString().length==1 && minute.toString().length==1){
                        _pickedTime = "0"+hourOfDay.toString() + ":" + "0"+minute.toString()
                    }else if (hourOfDay.toString().length==1 ){
                        _pickedTime = "0"+hourOfDay.toString() + ":" +minute.toString()
                    }else if (minute.toString().length==1 ){
                        _pickedTime = hourOfDay.toString() + ":" +"0"+minute.toString()
                    }else{
                        _pickedTime = hourOfDay.toString() + ":" + minute.toString()
                    }
                    Log.e(
                        "PickedTime: ",
                        "Date: $_pickedDate$_pickedTime"
                    ) //2019-02-12
//                            et_show_date_time.setText(date_time.toString() + " " + hourOfDay + ":" + minute)

                    selected="1"

                    mInfoBuilderTwo.findViewById<TextView>(R.id.mTvCallReminder).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tickorange, 0);
                    mInfoBuilderTwo.findViewById<TextView>(R.id.mTvAppointReminder).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }, mHour, if (mMinute==60) 0+2 else if (mMinute==59) 0+1 else mMinute+2, false
            )
            timePickerDialog.show()
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)

//                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH)
//            )
//            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
//            dialog.show()

        }

        mInfoBuilderTwo.findViewById<TextView>(R.id.mTvAppointReminder).setOnClickListener {

//            val c: Calendar = Calendar.getInstance()
//            val dialog = DatePickerDialog(
//                requireActivity(),
//                { view, year, month, dayOfMonth ->
//                    val _year = year.toString()
//                    val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
//                    val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
//                    _pickedDate = "$_year-$_month-$_date"
//                    Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12

            val c2 = Calendar.getInstance()
            var mHour = c2[Calendar.HOUR_OF_DAY]
            var mMinute = c2[Calendar.MINUTE]
            _pickedDate = "$selectedYear-$selectedMonth-$selectedDay"

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                requireActivity(),R.style.TimePicker,
                { view, hourOfDay, minute ->
                    mHour = hourOfDay
                    mMinute = minute
                    if (hourOfDay.toString().length==1 && minute.toString().length==1){
                        _pickedTime = "0"+hourOfDay.toString() + ":" + "0"+minute.toString()
                    }else if (hourOfDay.toString().length==1 ){
                        _pickedTime = "0"+hourOfDay.toString() + ":" +minute.toString()
                    }else if (minute.toString().length==1 ){
                        _pickedTime = hourOfDay.toString() + ":" +"0"+minute.toString()
                    }else{
                        _pickedTime = hourOfDay.toString() + ":" + minute.toString()
                    }
                    Log.e(
                        "PickedTime: ", "Date: $_pickedDate"+" " + _pickedTime
                    ) //2019-02-12
//                            et_show_date_time.setText(date_time.toString() + " " + hourOfDay + ":" + minute)

                    selected="2"

                    mInfoBuilderTwo.findViewById<TextView>(R.id.mTvAppointReminder).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tickorange, 0)
                    mInfoBuilderTwo.findViewById<TextView>(R.id.mTvCallReminder).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }, mHour, if (mMinute==60) 0+2 else if (mMinute==59) 0+1 else mMinute+2, false
            )
            timePickerDialog.show()
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
//
//                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH)
//            )
//            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
//            dialog.show()

        }
        mInfoBuilderTwo.findViewById<TextView>(R.id.tvCancel)
            .setOnClickListener {
                mInfoBuilderTwo.dismiss()
            }
        mInfoBuilderTwo.findViewById<TextView>(R.id.tvDone)
            .setOnClickListener {
                val tz = TimeZone.getDefault()

                if (selected.isNotEmpty() && !isEditClick){
                    if (selected.equals("1")){
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.addreminderCal(
                                "phone",
                                "note",
                                _pickedDate + " " + _pickedTime+":00", mInfoBuilderTwo.findViewById<EditText>(R.id.mEtDescription).text.toString().trim(),tz.id
                            )
                        }
                    }else{
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.addreminderCal(
                                "appointment",
                                "note",
                                _pickedDate + " " + _pickedTime+":00", mInfoBuilderTwo.findViewById<EditText>(R.id.mEtDescription).text.toString().trim(),tz.id
                            )
                        }
                    }
                }

                if (selected.isNotEmpty() && isEditClick){
                    isEditClick = false

                    if (selected.equals("1")){
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.editreminder(
                                mList[selected_position]._id,
                                "phone",
                                "note",
                                _pickedDate + " " + _pickedTime+":00", mInfoBuilderTwo.findViewById<EditText>(R.id.mEtDescription).text.toString().trim(),tz.id
                            )
                        }


                    }else{
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.editreminder(
                                mList[selected_position]._id,
                                "appointment",
                                "note",
                                _pickedDate + " " + _pickedTime+":00", mInfoBuilderTwo.findViewById<EditText>(R.id.mEtDescription).text.toString().trim(),tz.id
                            )
                        }
                    }
                }

            }
        mInfoBuilderTwo.show();
    }

    override fun onSingleItemClick(item: Any, position: Int) {
        showRightMenu(item as View,position)
    }
    override fun onLongClickListener(item: Any, position: Int) {
        showRightMenu(item as View,position)
    }

    override fun onSingleListClick(item: Any, position: Int) {
    }

    fun showRightMenu(anchor: View, position: Int): Boolean {

        val popup = PopupMenu(context, anchor)
        popup.menuInflater.inflate(R.menu.calender_reminder_menu, popup.menu)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

        popup.setOnMenuItemClickListener{
            if (it.itemId == R.id.item_delete){
                AllinOneDialog(ttle = "Delete",
                    msg = "Are you sure you want to Delete it ?",
                    onLeftClick = {/*btn No click*/ },
                    onRightClick = {/*btn Yes click*/
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deletereminder(mList[position]._id)
                        }
                    })
            }else if(it.itemId == R.id.item_edit){
                isEditClick = true
                selected_position = position
                showFollowUpPop()
            }else if(it.itemId == R.id.item_share){
                shareReminderData(mList[position].description,mList[position].dateTime)
            }else if(it.itemId == R.id.item_copy){
                copyTextToClipboard(mList[position].description)
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun shareReminderData(description:String,dateTime:String){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")
        var shareMessage = """
                    ${"\n" + description + "\n" + dateTime + "\n" }
                    """.trimIndent()
        shareMessage = """
                    $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}
                    
                    """.trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "choose one"))
    }

    private fun copyTextToClipboard(textToCopy:String) {
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        toast("Text copy")
    }




}