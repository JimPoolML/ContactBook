package appjpm4everyone.contactbook.createuser

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.databinding.ActivityCreateUserBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber

class CreateUserActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        getPermissions()
    }

    private fun initUI() {
        binding.botonCancelar.setOnClickListener {
            hideKeyboardFrom(this)
            finish()
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
        showLongSnackError(this@CreateUserActivity, resources.getString(R.string.granted_access),
                ContextCompat.getDrawable(this@CreateUserActivity, R.drawable.ic_check_ok)!!)
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


}