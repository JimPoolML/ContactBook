package appjpm4everyone.contactbook.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.adapters.ContactAdapter
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.classes.WeakContact
import appjpm4everyone.contactbook.createuser.CreateUserActivity
import appjpm4everyone.contactbook.databinding.ActivityMainBinding
import appjpm4everyone.libraryFAB.CommunicateFab
import appjpm4everyone.libraryFAB.MovableFloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity


class MainActivity : BaseActivity(), CommunicateFab {

    private lateinit var binding : ActivityMainBinding

    //To RecyclerView
    private lateinit var contactAdapter: ContactAdapter

    private var list : ArrayList<WeakContact> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {

        binding.btnSearch.setOnClickListener {
            if(binding.searchBreed.visibility == View.VISIBLE ){
                binding.searchBreed.visibility = View.GONE
                binding.searchBreed.clearFocus()
            }else{
                binding.searchBreed.visibility = View.VISIBLE
                binding.searchBreed.isFocusable = true
                binding.searchBreed.isIconified = false
                binding.searchBreed.requestFocusFromTouch()
            }
        }
        setContactAdapter()
        showFAB()
    }

    private fun setContactAdapter() {
        list = getMockList()
        contactAdapter =
            ContactAdapter(list)
        binding.rvContact.setHasFixedSize(true)
        binding.rvContact.layoutManager = LinearLayoutManager(this)
        binding.rvContact.adapter = contactAdapter
        val dividerItemDecoration = DividerItemDecoration( this, LinearLayoutManager.HORIZONTAL)
        binding.rvContact.addItemDecoration(dividerItemDecoration)
        hideKeyboardFrom(this)

        deleteItem()
    }

    private fun getMockList(): ArrayList<WeakContact> {
        val mockList : ArrayList<WeakContact> = ArrayList()
        mockList.add(WeakContact("JP", "Jim Moreno", "641436962"))
        mockList.add(WeakContact("FM", "Luis Fernando", "643759819"))
        mockList.add(WeakContact("RL", "Rosalba Latorre", "3142595590"))
        return mockList
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
                showLongSnackError(this@MainActivity, resources.getString(R.string.delete_contact),
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete)!!)
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                list.removeAt(position)
                contactAdapter.notifyDataSetChanged()
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvContact)
    }

    private fun showFAB() {
        //Cast length
        val spaceFab = resources.getDimension(R.dimen.dp_56).toInt()
        val movableFloatingActionButton =
            MovableFloatingActionButton(this, true, spaceFab)
        movableFloatingActionButton.openFAB(this, R.drawable.radioactive_free, null, null, null)
    }

    override fun onClickFab(xPos: Float, yPos: Float) {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
    }

}