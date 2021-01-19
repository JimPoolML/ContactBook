package appjpm4everyone.contactbook.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.MatrixCursor
import android.net.Uri
import android.os.Build
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


class MainActivity : BaseActivity(), CommunicateFab, OnFragmentContactListener, OnFragmentDataContactListener {

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
    private lateinit var dataContactFragment: DataContactFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        loadDataContact()
        showFAB()
    }

    private fun loadFragments() {
        val fm1: FragmentManager = supportFragmentManager
        dataContactFragment = fm1.findFragmentById(R.id.fragmentDataContact) as DataContactFragment

        /*val frg1: Fragment = supportFragmentManager.findFragmentById(R.id.fragmentDataContact)!!
        val fragmentTransaction1 = supportFragmentManager.beginTransaction()
        fragmentTransaction1.detach(frg1)
        fragmentTransaction1.attach(frg1)
        fragmentTransaction1.commit()*/

        val fragmentTransaction1 = supportFragmentManager.beginTransaction()
        fragmentTransaction1.replace(R.id.fragmentDataContact, dataContactFragment)
        fragmentTransaction1.addToBackStack(null)
        fragmentTransaction1.commit()

        //Instance fragment
        val fm: FragmentManager = supportFragmentManager
        contactFragment = fm.findFragmentById(R.id.fragmentContact) as ContactFragment

        val frg: Fragment = supportFragmentManager.findFragmentById(R.id.fragmentContact)!!
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.detach(frg)
        fragmentTransaction.attach(frg)
        fragmentTransaction.commit()
    }

    private fun loadDataContact() {
        list = ArrayList()
        val rowNumber = dataBase.rowNumber()
        if (rowNumber > 0) {
            ids = dataBase.recoverIds()!!
            var strongList: ArrayList<StrongContact> = ArrayList()
            for (i in ids.indices) {
                strongList.add(dataBase.recoverContact(ids[i])!!)
            }
            //Set data into 2nd fragment
            if(strongList[0]!=null){
                val strongContact = strongList[0]
                val bundle = Bundle()
                bundle.putParcelable("example", strongContact)
                // set Fragmentclass Arguments
                dataContactFragment.arguments = bundle
            }

        }
    }

    private fun loadDataContact(id: Int) {
        list = ArrayList()
        val rowNumber = dataBase.rowNumber()
        if (rowNumber > 0) {
            ids = dataBase.recoverIds()!!
            var strongList: ArrayList<StrongContact> = ArrayList()
            for (i in ids.indices) {
                strongList.add(dataBase.recoverContact(ids[i])!!)
            }
            //Set data into 2nd fragment
            if(strongList[id]!=null){
                val strongContact = strongList[id]
                dataContactFragment.setStrongContact(strongContact)
            }
        }
    }

    private fun setContactAdapter() {
        contactFragment.setContactAdapter()
        hideKeyboardFrom(this)
    }

    private fun showFAB() {
        //Cast length
        val spaceFab = resources.getDimension(R.dimen.dp_64).toInt()
        val movableFloatingActionButton =
            MovableFloatingActionButton(this, true, spaceFab)
        movableFloatingActionButton.openFAB(this, R.drawable.ic_fab, null, null, null)
    }

    override fun onClickFab(xPos: Float, yPos: Float) {
        addContact()
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
                val image = data.extras!!.getString("image")
                val country = data.extras!!.getInt("country")
                val countryTel = data.extras!!.getInt("countryTel")
                dataBase.addContact(name, address, cellPhone, localPhone, email, image, country, countryTel)
                setContactAdapter()
            } else if (resul == MODIFY_CODE) {
                val name = data?.extras!!.getString("name")
                val address = data.extras!!.getString("address")
                val cellPhone = data.extras!!.getString("cellPhone")
                val localPhone = data.extras!!.getString("localPhone")
                val email = data.extras!!.getString("email")
                val image = data.extras!!.getString("image")
                val id = data.extras!!.getInt("id")
                val country = data.extras!!.getString("country")
                val countryTel = data.extras!!.getString("countryTel")
                dataBase.modifyContact(id, name, address, cellPhone, localPhone, email, image, country!!.toInt(),
                    countryTel!!.toInt()
                )
                setContactAdapter()
            }
        } else {
            setContactAdapter()
        }
    }


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
                Manifest.permission.CAMERA
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
            .withErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    "Error occurred! $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onSameThread()
            .check()
    }

    private fun permissionsOK() {
        showLongSnackError(
            this, resources.getString(R.string.granted_access),
            ContextCompat.getDrawable(this, R.drawable.ic_check_ok)!!
        )
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
    override fun onCallContact(callNumber: String, id: Int) {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            loadDataContact(id-1)
        } else {
            // In portrait
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$callNumber"))
            startActivity(intent)
        }

    }

    override fun showLongSnackErrorFragment(message: String, icon: Int) {
        showLongSnackError(
            this@MainActivity, message,
            ContextCompat.getDrawable(this@MainActivity, icon)!!
        )
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
        } catch (io: java.lang.Exception) {
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
        i.putExtra("image", minStrongContact.image)
        i.putExtra("country", minStrongContact.country)
        i.putExtra("countryTel", minStrongContact.countryTel)
        startActivityForResult(i, MODIFY_CODE)
    }

    override fun onCallLandscapeContact(callNumber: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$callNumber"))
        startActivity(intent)
    }

}