package appjpm4everyone.contactbook.scheduledate

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.adapters.OnGetButton
import appjpm4everyone.contactbook.adapters.ScheduleAdapter
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.classes.ScheduleTable
import appjpm4everyone.contactbook.database.ScheduleDataBase
import appjpm4everyone.contactbook.databinding.ActivityScheduleBinding
import appjpm4everyone.contactbook.utils.CustomAlertDialog
import kotlinx.android.synthetic.main.view_custom_alert_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ScheduleActivity : BaseActivity(), OnGetButton {

    private val ADD_CODE = 10
    private val MODIFY_CODE = 11

    private lateinit var binding: ActivityScheduleBinding

    //To RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter

    private var list: ArrayList<ScheduleTable> = ArrayList()

    //To SearchView
    private lateinit var contactList: Array<String?>
    private lateinit var mAdapter: SimpleCursorAdapter

    //DataBase
    private lateinit var dataBase: ScheduleDataBase
    private lateinit var ids: IntArray

    //datePicker
    lateinit var customAlertDialog: CustomAlertDialog
    var isShowing: Boolean = false
    private lateinit var myCalendar: Calendar
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_schedule)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        //Instance dataBase
        dataBase = ScheduleDataBase(this)

        customAlertDialog = CustomAlertDialog()

        setSearchView()
        showSearchSchedule()
        setScheduleAdapter()
        deleteItem()

        binding.btnSchedule.setOnClickListener {
            onShowDataPicker(false)
        }
    }


    private fun onShowDataPicker(isModify: Boolean) {
        if(!isShowing){
            isShowing = true
            customAlertDialog.show(this)
        }

        if (::customAlertDialog.isInitialized && customAlertDialog.dialog.isShowing) {

            customAlertDialog.view.btn_Cancel.setOnClickListener {
                hideKeyboardFrom(this)
                customAlertDialog.hideProgress()
                isShowing = false
                delayMs(1000)
            }

            myCalendar = Calendar.getInstance()

            //val editDate = customAlertDialog.view.edt_date
            val date =
                OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateLabel()
                }

            val dateTime =
                OnTimeSetListener { _, hourOfDay, minute ->
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    myCalendar.set(Calendar.MINUTE, minute)
                    updateLabelTime()
                }

            customAlertDialog.view.edt_date.setOnClickListener {
                DatePickerDialog(
                    this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            customAlertDialog.view.edt_clock.setOnClickListener{
                val timePickerDialog= TimePickerDialog(
                    this, dateTime, myCalendar.get(Calendar.HOUR_OF_DAY),
                    myCalendar.get(Calendar.MINUTE), true)
                timePickerDialog.setTitle("Seleccione hora")
                timePickerDialog.show()

            }

            customAlertDialog.view.btn_Send.setOnClickListener {
                hideKeyboardFrom(this)
                if(customAlertDialog.view.edt_event.text.isNullOrEmpty() || customAlertDialog.view.edt_date.text.isNullOrEmpty() || customAlertDialog.view.edt_clock.text.isNullOrEmpty()  ){
                    showLongSnackError(this,  resources.getString(R.string.text_incomplete),
                        ContextCompat.getDrawable(this, R.drawable.ic_error)!!)
                }else{
                    customAlertDialog.hideProgress()
                    isShowing = false
                    if(isModify){
                        modifySchedule(customAlertDialog.view.edt_event.text.toString(), customAlertDialog.view.edt_date.text.toString(), customAlertDialog.view.edt_clock.text.toString() )
                    }else{
                        addSchedule()
                    }
                }
                delayMs(1000)
            }
        }
    }

    private fun addSchedule() {
        val event = customAlertDialog.view.edt_event.text.toString()
        val date = customAlertDialog.view.edt_date.text.toString()
        val clock = customAlertDialog.view.edt_clock.text.toString()
        dataBase.addContact(event, date, clock)
        setScheduleAdapter()
    }

    private fun updateLabel() {
        val myFormat = "MM/dd/yy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        customAlertDialog.view.edt_date.setText(sdf.format(myCalendar.time))
    }

    private fun updateLabelTime() {
        val myFormat = "HH:mm" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        customAlertDialog.view.edt_clock.setText(sdf.format(myCalendar.time))
    }

    private fun showSearchSchedule() {
        if (binding.searchSchedule.visibility == View.VISIBLE) {
            binding.searchSchedule.visibility = View.GONE
            binding.searchSchedule.clearFocus()
        } else {
            binding.searchSchedule.visibility = View.VISIBLE
            binding.searchSchedule.isFocusable = true
            binding.searchSchedule.isIconified = false
            binding.searchSchedule.requestFocusFromTouch()
            setSearchView()
        }
    }

    private fun setSearchView() {
        binding.searchSchedule.clearFocus()

        contactList = arrayOfNulls(list.size)
        for (i in list.indices) {
            contactList[i] = list[i].event
        }

        val from = arrayOf("contactsFound")
        val to = intArrayOf(android.R.id.text1)
        mAdapter = SimpleCursorAdapter(
            applicationContext,
            android.R.layout.simple_list_item_1,
            null,
            from,
            to,
            android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        binding.searchSchedule.suggestionsAdapter = mAdapter

        binding.searchSchedule.setOnSuggestionListener(object :
            androidx.appcompat.widget.SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = mAdapter.getItem(position) as Cursor
                val txt = cursor.getString(cursor.getColumnIndex("contactsFound"))
                binding.searchSchedule.setQuery(txt, false)
                //callContact(list[position].event.trim())
                hideKeyboardFrom(this@ScheduleActivity)
                return true
            }
        })

        binding.searchSchedule.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                populateAdapter(newText);
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (contactList.contains(query)) {
                    showLongSnackError(
                        this@ScheduleActivity, resources.getString(R.string.call_contact),
                        ContextCompat.getDrawable(this@ScheduleActivity, R.drawable.ic_error)!!
                    )

                } else {
                    showLongSnackError(
                        this@ScheduleActivity, resources.getString(R.string.not_found_event),
                        ContextCompat.getDrawable(this@ScheduleActivity, R.drawable.ic_error)!!
                    )
                }
                hideKeyboardFrom(this@ScheduleActivity)
                return false
            }

        })
    }

    // You must implements your logic to get data using OrmLite
    private fun populateAdapter(query: String) {
        val c = MatrixCursor(arrayOf(BaseColumns._ID, "contactsFound"))
        for (i in contactList.indices) {
            if (contactList[i]!!.toLowerCase()
                    .startsWith(query.toLowerCase())
            ) c.addRow(arrayOf(i, contactList[i]!!))
        }
        mAdapter.changeCursor(c)
    }

    private fun setScheduleAdapter() {
        list = ArrayList()
        val rowNumber = dataBase.rowNumber()
        if (rowNumber > 0) {
            ids = dataBase.recoverIds()!!
            var scheduleTable = ScheduleTable()
            for (i in ids.indices) {
                scheduleTable = dataBase.recoverContact(ids[i])!!
                list.add(ScheduleTable(ids[i], scheduleTable.event, scheduleTable.date, scheduleTable.clock))
            }
            //Set in adapter the list and interface
            scheduleAdapter = ScheduleAdapter(this, list, this)
            binding.rvSchedule.setHasFixedSize(true)
            binding.rvSchedule.layoutManager = LinearLayoutManager(this)
            binding.rvSchedule.adapter = scheduleAdapter
            val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL)
            binding.rvSchedule.addItemDecoration(dividerItemDecoration)
        }

        /*list.add(ScheduleTable(0, "bornDate","1989/08/20", "12:00" ))
        list.add(ScheduleTable(1, "bachellor","2006/11/30", "11:00" ))
        list.add(ScheduleTable(2, "Dad","2020/12/20", "11:00" ))
        //Set in adapter the list and interface
        scheduleAdapter = ScheduleAdapter(this, list, this)
        binding.rvSchedule.setHasFixedSize(true)
        binding.rvSchedule.layoutManager = LinearLayoutManager(this)
        binding.rvSchedule.adapter = scheduleAdapter
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL)
        binding.rvSchedule.addItemDecoration(dividerItemDecoration)*/
    }

    private fun deleteItem() {
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Left direction
                if (swipeDir == 4 && swipeDir == 8) {
                    showLongSnackError(
                        this@ScheduleActivity, resources.getString(R.string.delete_event),
                        ContextCompat.getDrawable(this@ScheduleActivity, R.drawable.ic_delete)!!
                    )
                    //take database position
                    val position = list[viewHolder.adapterPosition].id
                    dataBase.eraseContact(position)
                    //Take list position
                    list.removeAt(viewHolder.adapterPosition)
                    scheduleAdapter.notifyDataSetChanged()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvSchedule)
    }

    private fun recoverPosition(id: Int) {
        //listener?.recoverPosition(id)
    }

    private fun modifySchedule(
        event: String,
        date: String,
        clock: String
    ) {
        dataBase.modifyContact(id+1, event, date, clock)
        setScheduleAdapter()

        /*list = ArrayList()
        val rowNumber = dataBase.rowNumber()
        if (rowNumber > 0) {
            ids = dataBase.recoverIds()!!
            var scheduleTable: ArrayList<ScheduleTable> = ArrayList()
            for (i in ids.indices) {
                scheduleTable.add(dataBase.recoverContact(ids[i])!!)
            }
            //Set data into 2nd fragment
            if(scheduleTable[id]!=null){
                val schedule = scheduleTable[id]
                dataBase.modifyContact(id, schedule.event, schedule.date, schedule.clock)
                setScheduleAdapter()
            }
        }*/
    }


    override fun onClickButton(phoneNumber: String, id: Int) {
        this.id= id-1
        onShowDataPicker(true)
    }
}