package appjpm4everyone.contactbook.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.adapters.ContactAdapter
import appjpm4everyone.contactbook.adapters.OnGetButton
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.classes.StrongContact
import appjpm4everyone.contactbook.classes.WeakContact
import appjpm4everyone.contactbook.createuser.CreateUserActivity
import appjpm4everyone.contactbook.database.MyDataBase
import appjpm4everyone.contactbook.databinding.ActivityMainBinding
import appjpm4everyone.libraryFAB.CommunicateFab
import appjpm4everyone.libraryFAB.MovableFloatingActionButton
import com.google.gson.GsonBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber
import java.io.File


class MainActivity : BaseActivity(), CommunicateFab, OnGetButton, OnFragmentContactListener {

    private val ADD_CODE = 10
    private val MODIFY_CODE = 11

    private lateinit var binding: ActivityMainBinding

    //To RecyclerView
    private lateinit var contactAdapter: ContactAdapter

    private var list: ArrayList<WeakContact> = ArrayList()

    //To SearchView
    private lateinit var contactList: Array<String?>
    private lateinit var mAdapter: SimpleCursorAdapter

    //DataBase
    private lateinit var dataBase: MyDataBase
    private lateinit var ids: IntArray

    //To Fragments
    private lateinit var contactFragment: ContactFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPermissions()
        initUI()

    }

    private fun initUI() {

        //Instance dataBase
        dataBase = MyDataBase(this)

        loadFragments()

        binding.btnSearch.setOnClickListener {
            contactFragment.showSearchContact()
        }
        /*setContactAdapter()
        deleteItem()*/
        showFAB()
    }

    private fun loadFragments() {
        //Instance fragment
        val fm: FragmentManager = supportFragmentManager
        contactFragment = fm.findFragmentById(R.id.fragmentContact) as ContactFragment

        var frg: Fragment = supportFragmentManager.findFragmentById(R.id.fragmentContact)!!
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.detach(frg)
        fragmentTransaction.attach(frg)
        fragmentTransaction.commit()
    }

    private fun setSearchView() {
        //binding.searchBreed.clearFocus()

        contactList = arrayOfNulls(list.size)
        for (i in list.indices) {
            contactList[i] = list[i].name
        }

        val from = arrayOf("contactsFound")
        val to = intArrayOf(android.R.id.text1)
        mAdapter = SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

       /* binding.searchBreed.suggestionsAdapter = mAdapter

        binding.searchBreed.setOnSuggestionListener(object :
                androidx.appcompat.widget.SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = mAdapter.getItem(position) as Cursor
                val txt = cursor.getString(cursor.getColumnIndex("contactsFound"))
                binding.searchBreed.setQuery(txt, false)
                callContact(list[position].number.trim())
                hideKeyboardFrom(this@MainActivity)
                return true
            }
        })

        binding.searchBreed.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                populateAdapter(newText);
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (contactList.contains(query)) {
                    showLongSnackError(this@MainActivity, resources.getString(R.string.call_contact),
                            ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_error)!!)
                } else {
                    showLongSnackError(this@MainActivity, resources.getString(R.string.not_found_contact),
                            ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_error)!!)
                }
                hideKeyboardFrom(this@MainActivity)
                return false
            }

        })*/
    }

    private fun callContact(callNumber: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$callNumber"))
        startActivity(intent)
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

    private fun setContactAdapter() {
        /*list = ArrayList()
        val rowNumber = dataBase.rowNumber()
        if (rowNumber > 0) {
            ids = dataBase.recoverIds()!!
            var minStringContact = StrongContact()
            for (i in ids.indices) {
                minStringContact = dataBase.recoverContact(ids[i])!!
                list.add(WeakContact(minStringContact.name, minStringContact.name, minStringContact.cellPhone, ids[i]))
            }
            //Set in adapter the list and interface
            contactAdapter = ContactAdapter(applicationContext, list, this)
            binding.rvContact.setHasFixedSize(true)
            binding.rvContact.layoutManager = LinearLayoutManager(this)
            binding.rvContact.adapter = contactAdapter
            val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL)
            binding.rvContact.addItemDecoration(dividerItemDecoration)

            setJSONFile(ids)
        }*/
        contactFragment.setContactAdapter()
        hideKeyboardFrom(this)
    }

    private fun deleteItem() {
        /*val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
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
                    showLongSnackError(this@MainActivity, resources.getString(R.string.delete_contact),
                            ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete)!!)
                    //Remove swiped item from dataBase and notify the RecyclerView
                    //take database position
                    val position = list[viewHolder.adapterPosition].id
                    dataBase.eraseContact(position)
                    //Take list position
                    list.removeAt(viewHolder.adapterPosition)
                    contactAdapter.notifyDataSetChanged()
                } else if (swipeDir == 8) {
                    //Right direction
                    recoverPosition(list[viewHolder.adapterPosition].id)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvContact)*/
    }

    /*private fun recoverPosition(id: Int) {
        val i = Intent(this, CreateUserActivity::class.java)
        i.putExtra("modify", true)
        i.putExtra("id", id)
        val minStrongContact: StrongContact = dataBase.recoverContact(id)!!
        i.putExtra("name", minStrongContact.name)
        i.putExtra("address", minStrongContact.address)
        i.putExtra("cellPhone", minStrongContact.cellPhone)
        i.putExtra("localPhone", minStrongContact.localPhone)
        i.putExtra("email", minStrongContact.email)
        startActivityForResult(i, MODIFY_CODE)
    }*/

    private fun showFAB() {
        //Cast length
        val spaceFab = resources.getDimension(R.dimen.dp_64).toInt()
        val movableFloatingActionButton =
                MovableFloatingActionButton(this, true, spaceFab)
        movableFloatingActionButton.openFAB(this, R.drawable.ic_coronavirus, null, null, null)
    }

    override fun onClickFab(xPos: Float, yPos: Float) {
        addContact()
    }

    override fun onClickButton(phoneNumber: String) {
        callContact(phoneNumber)
    }

    private fun addContact() {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivityForResult(intent, ADD_CODE)
    }

    override fun onActivityResult(resul: Int, codigo: Int, data: Intent?) {
        super.onActivityResult(resul, codigo, data)
        if (codigo == Activity.RESULT_OK) {
            if (resul == ADD_CODE) {
                val name = data?.extras!!.getString("name")
                val address = data.extras!!.getString("address")
                val cellPhone = data.extras!!.getString("cellPhone")
                val localPhone = data.extras!!.getString("localPhone")
                val email = data.extras!!.getString("email")
                dataBase.addContact(name, address, cellPhone, localPhone, email)
                setContactAdapter()
            } else if (resul == MODIFY_CODE) {
                val name = data?.extras!!.getString("name")
                val address = data.extras!!.getString("address")
                val cellPhone = data.extras!!.getString("cellPhone")
                val localPhone = data.extras!!.getString("localPhone")
                val email = data.extras!!.getString("email")
                val id = data.extras!!.getInt("id")
                dataBase.modifyContact(id, name, address, cellPhone, localPhone, email)
                setContactAdapter()
            }
        } else {
            setContactAdapter()
        }
    }

    /*private fun setJSONFile(iDS: IntArray) {
        val minStringContact: Array<StrongContact?> = arrayOfNulls(iDS.size)
        for (i in iDS.indices) {
            minStringContact[i] = dataBase.recoverContact(iDS[i])!!
        }


        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutsListPretty: String = gsonPretty.toJson(minStringContact)
        try {
            val filePath: String = applicationContext.filesDir.path.toString() + "/ContactBook.json"
            val f = File(filePath)
            f.writeText(jsonTutsListPretty)
            Timber.e(filePath)
        }catch (io : java.lang.Exception){
            Toast.makeText(this, io.message, Toast.LENGTH_LONG).show()
        }
    }*/

        private fun getPermissions() {
            if (allPermissionsGranted()) {
                Timber.i("Permission granted")
            } else {
                requestPermissionsDexter()
            }
        }

        private fun allPermissionsGranted(): Boolean {
            for (permission in this.getRequiredPermissions()!!) {
                permission?.let {
                    if (isPermissionGranted(this, it).not()) {
                        return false
                    }
                }
            }
            return true
        }

        private fun isPermissionGranted(context: Context, permission: String): Boolean {
            if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED
            ) {
                Timber.i("Permission granted: %s", permission)
                return true
            }
            Timber.i("Permission NOT granted: %s", permission)
            return false
        }

        private fun getRequiredPermissions(): Array<String?>? {
            return try {
                val info = this.packageManager
                        .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
                val ps = info.requestedPermissions
                if (ps != null && ps.isNotEmpty()) {
                    ps
                } else {
                    arrayOfNulls(0)
                }
            } catch (e: Exception) {
                arrayOfNulls(0)
            }
        }

        private fun requestPermissionsDexter() {
            Dexter.withContext(this)
                    //Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                permissionsOK()
                            }
                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied) {
                                // show alert dialog navigating to Settings
                                showSettingsDialog()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permissions: List<PermissionRequest>,
                                token: PermissionToken
                        ) {
                            token.continuePermissionRequest()
                        }
                    })
                    .withErrorListener { error -> Toast.makeText(applicationContext, "Error occurred! $error", Toast.LENGTH_SHORT).show() }
                    .onSameThread()
                    .check()
        }

        private fun permissionsOK() {
            showLongSnackError(this, resources.getString(R.string.granted_access),
                    ContextCompat.getDrawable(this, R.drawable.ic_check_ok)!!)
        }


        /**
         * Showing Alert Dialog with Settings option
         * Navigates user to app settings
         * NOTE: Keep proper title and message depending on your app
         */
        private fun showSettingsDialog() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Need Permissions")
            builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
            builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
                dialog.cancel()
                openSettings()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            builder.show()
        }

        // navigating user to app settings
        private fun openSettings() {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }

    //Fragment listeners
    override fun onCallContact(callNumber: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$callNumber"))
        startActivity(intent)
    }

    override fun showLongSnackErrorFragment(message: String, icon: Int) {
        showLongSnackError(this@MainActivity, message,
            ContextCompat.getDrawable(this@MainActivity, icon)!!)
    }

    override fun setJSONFile(iDS: IntArray) {
        val minStringContact: Array<StrongContact?> = arrayOfNulls(iDS.size)
        for (i in iDS.indices) {
            minStringContact[i] = dataBase.recoverContact(iDS[i])!!
        }


        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutsListPretty: String = gsonPretty.toJson(minStringContact)
        try {
            val filePath: String = applicationContext.filesDir.path.toString() + "/ContactBook.json"
            val f = File(filePath)
            f.writeText(jsonTutsListPretty)
            Timber.e(filePath)
        }catch (io : java.lang.Exception){
            Toast.makeText(this, io.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun recoverPosition(id: Int) {
        val i = Intent(this, CreateUserActivity::class.java)
        i.putExtra("modify", true)
        i.putExtra("id", id)
        val minStrongContact: StrongContact = dataBase.recoverContact(id)!!
        i.putExtra("name", minStrongContact.name)
        i.putExtra("address", minStrongContact.address)
        i.putExtra("cellPhone", minStrongContact.cellPhone)
        i.putExtra("localPhone", minStrongContact.localPhone)
        i.putExtra("email", minStrongContact.email)
        startActivityForResult(i, MODIFY_CODE)
    }

}