package appjpm4everyone.contactbook.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.adapters.ContactAdapter
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.classes.StrongContact
import appjpm4everyone.contactbook.classes.WeakContact
import appjpm4everyone.contactbook.createuser.CreateUserActivity
import appjpm4everyone.contactbook.database.MyDataBase
import appjpm4everyone.contactbook.databinding.ActivityMainBinding
import appjpm4everyone.libraryFAB.CommunicateFab
import appjpm4everyone.libraryFAB.MovableFloatingActionButton


class MainActivity : BaseActivity(), CommunicateFab {

    val ADD_CODE = 10
    val MODIFY_CODE = 11
    val DELETE_CODE = 12

    private lateinit var binding : ActivityMainBinding
    //To RecyclerView
    private lateinit var contactAdapter: ContactAdapter

    private var list : ArrayList<WeakContact> = ArrayList()

    //DataBase
    private lateinit var dataBase : MyDataBase
    private lateinit var ids: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {

        //Instance dataBase
        dataBase = MyDataBase(this)

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
        list = ArrayList()
        val rowNumber = dataBase.rowNumber()
        if(rowNumber>0){
            ids = dataBase.recoverIds()!!
            var minStringContact = StrongContact()
            for (i in ids.indices) {
                minStringContact = dataBase.recoverContact(ids[i])!!
                list.add(WeakContact("JP", minStringContact.name, minStringContact.cellPhone, ids[i]))
            }
            contactAdapter =
                    ContactAdapter(list)
            binding.rvContact.setHasFixedSize(true)
            binding.rvContact.layoutManager = LinearLayoutManager(this)
            binding.rvContact.adapter = contactAdapter
            val dividerItemDecoration = DividerItemDecoration( this, LinearLayoutManager.HORIZONTAL)
            binding.rvContact.addItemDecoration(dividerItemDecoration)
        }
        hideKeyboardFrom(this)
        deleteItem()
    }

    private fun getMockList(): ArrayList<WeakContact> {
        val mockList : ArrayList<WeakContact> = ArrayList()
        mockList.add(WeakContact("JP", "Jim Moreno", "641436962", 1))
        mockList.add(WeakContact("FM", "Luis Fernando", "643759819", 2))
        mockList.add(WeakContact("RL", "Rosalba Latorre", "3142595590", 3))
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
                //Left direction
                if(swipeDir == 4){
                    showLongSnackError(this@MainActivity, resources.getString(R.string.delete_contact),
                            ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete)!!)
                    //Remove swiped item from dataBase and notify the RecyclerView
                    //val position = viewHolder.adapterPosition
                    val position = list[viewHolder.adapterPosition].id
                    dataBase.eraseContact(position)
                    list.removeAt(viewHolder.adapterPosition)
                    contactAdapter.notifyDataSetChanged()
                }


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
        addContact()
    }

    private fun addContact() {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivityForResult(intent, ADD_CODE)
        //startActivity(intent)
    }

    /*  public void eliminaNota(View vista)
    {
        Intent i = new Intent(this,MuestraNota.class);
        i.putExtra("ID",iDAct );


    }
*/
    override fun onActivityResult(resul: Int, codigo: Int, data: Intent?) {
        super.onActivityResult(resul, codigo, data)
        if (codigo == Activity.RESULT_OK) {
            if (resul == ADD_CODE) {
                val name = data?.extras!!.getString("name")
                val address = data.extras!!.getString("address")
                val cellPhone = data.extras!!.getString("cellPhone")
                val localPhone = data.extras!!.getString("localPhone")
                val email = data.extras!!.getString("email")
                dataBase.addContact(name, address, cellPhone, localPhone, email )
                setContactAdapter()
                // adaptador.notifyDataSetChanged(); // metodo para notificar que los datos han cambiado
            } else {
                /*val fecha = data.extras!!.getString("Fecha")
                val contenido = data.extras!!.getString("Contenido")
                val mid = data.extras!!.getInt("ID")
                MDB.modificarNota(mid, contenido, fecha)
                rellenaLista()*/
            }
        }
    }


}