package appjpm4everyone.contactbook.main

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.adapters.ContactAdapter
import appjpm4everyone.contactbook.adapters.OnGetButton
import appjpm4everyone.contactbook.classes.StrongContact
import appjpm4everyone.contactbook.classes.WeakContact
import appjpm4everyone.contactbook.createuser.CreateUserActivity
import appjpm4everyone.contactbook.database.MyDataBase
import appjpm4everyone.contactbook.databinding.FragmentContactBinding
import appjpm4everyone.contactbook.scheduledate.ScheduleActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [ContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFragment : Fragment(), OnGetButton {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listener: OnFragmentContactListener? = null

    //Binding
    private lateinit var binding: FragmentContactBinding

    //To RecyclerView
    private lateinit var contactAdapter: ContactAdapter

    private var list: ArrayList<WeakContact> = ArrayList()

    //To SearchView
    private lateinit var contactList: Array<String?>
    private lateinit var mAdapter: SimpleCursorAdapter

    //DataBase
    private lateinit var dataBase: MyDataBase
    private lateinit var ids: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContactBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        //Instance dataBase
        dataBase = MyDataBase(context)

        setSearchView()
        showSearchContact()
        setContactAdapter()
        deleteItem()
        setSchedule()

    }

    private fun setSchedule() {
        binding.btnAgenda.setOnClickListener {
            val intent = Intent(context, ScheduleActivity::class.java)
            startActivity(intent)
        }
    }

    fun showSearchContact() {
        if (binding.searchContactFragment.visibility == View.VISIBLE) {
            binding.searchContactFragment.visibility = View.GONE
            binding.searchContactFragment.clearFocus()
        } else {
            binding.searchContactFragment.visibility = View.VISIBLE
            binding.searchContactFragment.isFocusable = true
            binding.searchContactFragment.isIconified = false
            binding.searchContactFragment.requestFocusFromTouch()
            setSearchView()
        }
    }

    private fun setSearchView() {
        binding.searchContactFragment.clearFocus()

        contactList = arrayOfNulls(list.size)
        for (i in list.indices) {
            contactList[i] = list[i].name
        }

        val from = arrayOf("contactsFound")
        val to = intArrayOf(android.R.id.text1)
        mAdapter = SimpleCursorAdapter(
            context,
            android.R.layout.simple_list_item_1,
            null,
            from,
            to,
            android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        binding.searchContactFragment.suggestionsAdapter = mAdapter

        binding.searchContactFragment.setOnSuggestionListener(object :
            androidx.appcompat.widget.SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = mAdapter.getItem(position) as Cursor
                val txt = cursor.getString(cursor.getColumnIndex("contactsFound"))
                binding.searchContactFragment.setQuery(txt, false)
                callContact(list[position].number.trim())
                (activity as MainActivity).hideKeyboardFrom(activity as MainActivity)
                return true
            }
        })

        binding.searchContactFragment.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                populateAdapter(newText);
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (contactList.contains(query)) {
                    listener?.showLongSnackErrorFragment(
                        resources.getString(R.string.call_contact),
                        R.drawable.ic_error
                    )
                } else {
                    listener?.showLongSnackErrorFragment(
                        resources.getString(R.string.not_found_contact),
                        R.drawable.ic_error
                    )
                }
                (activity as MainActivity).hideKeyboardFrom(activity as MainActivity)
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

    fun setContactAdapter() {

        Timer().schedule(500){
            //dummy time
        }

        list = ArrayList()
        val rowNumber = dataBase.rowNumber()
        if (rowNumber > 0) {
            ids = dataBase.recoverIds()!!
            var minStringContact = StrongContact()
            for (i in ids.indices) {
                minStringContact = dataBase.recoverContact(ids[i])!!
                //val phoneNumber = "(+${minStringContact.country}) ${minStringContact.cellPhone}"
                list.add(WeakContact(minStringContact.name, minStringContact.name, minStringContact.cellPhone, ids[i], minStringContact.image,minStringContact.country ))
            }
            //Set in adapter the list and interface
            contactAdapter = context?.let { ContactAdapter(it, list, this) }!!
            binding.rvContactFragment.setHasFixedSize(true)
            binding.rvContactFragment.layoutManager = LinearLayoutManager(context)
            binding.rvContactFragment.adapter = contactAdapter
            val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL)
            binding.rvContactFragment.addItemDecoration(dividerItemDecoration)

            listener?.setJSONFile(ids)
        }
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
                if (swipeDir == 4) {
                    listener?.showLongSnackErrorFragment(resources.getString(R.string.delete_contact), R.drawable.ic_delete)
                    val position = list[viewHolder.adapterPosition].id
                    dataBase.eraseContact(position)
                    //Take list position
                    list.removeAt(viewHolder.adapterPosition)
                    contactAdapter.notifyDataSetChanged()
                    listener?.onDeleteUser()
                } else if (swipeDir == 8) {
                    //Right direction
                    recoverPosition(list[viewHolder.adapterPosition].id)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvContactFragment)
    }

    private fun recoverPosition(id: Int) {
        listener?.recoverPosition(id)
    }

    private fun callContact(callNumber: String) {
        listener?.onCallContact(callNumber, id)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentContactListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClickButton(phoneNumber: String, id: Int) {
        listener?.onCallContact(phoneNumber,id)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}