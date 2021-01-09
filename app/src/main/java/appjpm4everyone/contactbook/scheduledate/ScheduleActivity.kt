package appjpm4everyone.contactbook.scheduledate

import android.database.Cursor
import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
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
import appjpm4everyone.contactbook.adapters.ContactAdapter
import appjpm4everyone.contactbook.adapters.OnGetButton
import appjpm4everyone.contactbook.adapters.ScheduleAdapter
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.classes.ScheduleTable
import appjpm4everyone.contactbook.classes.StrongContact
import appjpm4everyone.contactbook.classes.WeakContact
import appjpm4everyone.contactbook.database.MyDataBase
import appjpm4everyone.contactbook.database.ScheduleDataBase
import appjpm4everyone.contactbook.databinding.ActivityMainBinding
import appjpm4everyone.contactbook.databinding.ActivityScheduleBinding
import appjpm4everyone.contactbook.main.MainActivity

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

        setSearchView()
        showSearchSchedule()
        setScheduleAdapter()
        deleteItem()
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
        /*val rowNumber = dataBase.rowNumber()
        if (rowNumber > 0) {
            ids = dataBase.recoverIds()!!
            var scheduleTable = ScheduleTable()
            *//*for (i in ids.indices) {
                scheduleTable = dataBase.recoverContact(ids[i])!!
                list.add(ScheduleTable(ids[i], scheduleTable.event, scheduleTable.date, scheduleTable.clock))
            }*//*
            list.add(ScheduleTable(0, "bornDate","1989/08/20", "12:00" ))
            list.add(ScheduleTable(1, "bachellor","2006/11/30", "11:00" ))
            list.add(ScheduleTable(2, "Dad","2020/12/20", "11:00" ))
            //Set in adapter the list and interface
            scheduleAdapter = ScheduleAdapter(this, list, this)
            binding.rvSchedule.setHasFixedSize(true)
            binding.rvSchedule.layoutManager = LinearLayoutManager(this)
            binding.rvSchedule.adapter = scheduleAdapter
            val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL)
            binding.rvSchedule.addItemDecoration(dividerItemDecoration)
        }*/

        list.add(ScheduleTable(0, "bornDate","1989/08/20", "12:00" ))
        list.add(ScheduleTable(1, "bachellor","2006/11/30", "11:00" ))
        list.add(ScheduleTable(2, "Dad","2020/12/20", "11:00" ))
        //Set in adapter the list and interface
        scheduleAdapter = ScheduleAdapter(this, list, this)
        binding.rvSchedule.setHasFixedSize(true)
        binding.rvSchedule.layoutManager = LinearLayoutManager(this)
        binding.rvSchedule.adapter = scheduleAdapter
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL)
        binding.rvSchedule.addItemDecoration(dividerItemDecoration)
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
                    /*showLongSnackError(this@MainActivity, resources.getString(R.string.delete_contact),
                            ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete)!!)*/
                    //Remove swiped item from dataBase and notify the RecyclerView
                    //take database position
                    val position = list[viewHolder.adapterPosition].id
                    dataBase.eraseContact(position)
                    //Take list position
                    list.removeAt(viewHolder.adapterPosition)
                    scheduleAdapter.notifyDataSetChanged()
                } /*else if (swipeDir == 8) {
                    //Right direction
                    recoverPosition(list[viewHolder.adapterPosition].id)
                }*/
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvSchedule)
    }

    private fun recoverPosition(id: Int) {
        //listener?.recoverPosition(id)
    }



    override fun onClickButton(phoneNumber: String, id: Int) {
        TODO("Not yet implemented")
    }
}