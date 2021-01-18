package appjpm4everyone.contactbook.createuser

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.databinding.ActivityCreateUserBinding
import appjpm4everyone.contactbook.utils.Utils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel


class CreateUserActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateUserBinding
    private var idPosition: Int = 0
    private var isModify = false
    private lateinit var imageFileUri: Uri

    //Permisions
    private lateinit var cameraPermission: MutableList<Int?>
    private lateinit var storagePermission: MutableList<Int?>

    //For Intents Code
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103
    private val TAG = "CreateUserActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        onDataBundle(intent.extras)
    }

    private fun onDataBundle(extras: Bundle?) {
        extras?.let {
            isModify = it.getBoolean("modify")
            idPosition = it.getInt("id", 0)
            binding.edtName.text = Utils.editableToString(it.getString("name", ""))
            binding.edtAddress.text = Utils.editableToString(it.getString("address", ""))
            binding.edtCellPhone.text = Utils.editableToString(it.getString("cellPhone", ""))
            binding.edtLocalPhone.text = Utils.editableToString(it.getString("localPhone", ""))
            binding.edtEmail.text = Utils.editableToString(it.getString("email", ""))
            val image = it.getString("image", "")
            if(image.isNullOrEmpty()){
                binding.addPicture.background = resources.getDrawable(R.drawable.ic_default_user)
            }else{
                binding.addPicture.setImageURI(Uri.parse(image))
            }

        }
    }


    private fun initUI() {
        //Initialising array permissions

        //Initialising array permissions
        /*cameraPermission.add(
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )
        )

        cameraPermission.add(
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

        storagePermission = arrayOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ).toMutableList()*/

        imageFileUri = Uri.EMPTY

        binding.botonCancelar.setOnClickListener {
            hideKeyboardFrom(this)
            finish()
        }

        binding.botonAceptar.setOnClickListener {
            hideKeyboardFrom(this)
            setDataBaseValues()
        }

        binding.addPicture.setOnClickListener {
            hideKeyboardFrom(this)
            imageDialog()
        }
    }

    //Is always a good idea to put all the logic in methods, for easier debugging
    private fun imageDialog() {

        //Options of the dialog
        val options = arrayOf("Camara", "Galeria")
        //Display dialog
        val builder =
            AlertDialog.Builder(this)
        //Set title
        builder.setTitle("Obtener imagen de: ")
        builder.setItems(
            options
        ) { _: DialogInterface?, i: Int ->
            when (i) {
                0 -> {
                    launchCamera()
                }
                1 -> {
                    launchGalley()
                }
            }
        }.show()
    }

    //Because we are in a API that is higher than Marshmallow, we need to ask for permissions;
    /*private fun RequestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            storagePermission,
            com.example.practica3eduardogomez.AddUpdateContactActivity.STORAGE_REQUEST_CODE
        )
    }

    private fun RequestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            cameraPermission,
            com.example.practica3eduardogomez.AddUpdateContactActivity.CAMERA_REQUEST_CODE
        )
    }*/

    //Intent for launching the gallery
    private fun launchGalley() {
        //Intent for launching the gallery
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*" // We want only images
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE)
    }

    //Intent for launching the camera, the image will be returned in the onActivityResult method
    private fun launchCamera() {
        //This line will put the picture as extra on our cameraIntent, so that we can grab the URI, and store it io our DB
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Image title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")

        //Put image in ImageFileUri
        imageFileUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!

        //If we o not do this, we will receive null in or uri
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri)
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE)
    }

    private fun setDataBaseValues() {
        if (validateText()) {
            val intent = Intent()
            if (isModify) {
                intent.putExtra("id", idPosition)
            }
            intent.putExtra("name", "" + binding.edtName.text)
            intent.putExtra("address", "" + binding.edtAddress.text)
            intent.putExtra("cellPhone", "" + binding.edtCellPhone.text)
            intent.putExtra("localPhone", "" + binding.edtLocalPhone.text)
            intent.putExtra("email", "" + binding.edtEmail.text)
            intent.putExtra("image", "" + imageFileUri)
            setResult(Activity.RESULT_OK, intent)
            showLongSnackError(
                this@CreateUserActivity, resources.getString(R.string.text_complete),
                ContextCompat.getDrawable(this@CreateUserActivity, R.drawable.ic_check_ok)!!
            )
            finish()
        } else {
            showLongSnackError(
                this@CreateUserActivity, resources.getString(R.string.text_incomplete),
                ContextCompat.getDrawable(this@CreateUserActivity, R.drawable.ic_check_ok)!!
            )
        }
    }

    private fun validateText(): Boolean {
        if (binding.edtName.text.isNullOrEmpty() || binding.edtAddress.text.isNullOrEmpty() || binding.edtCellPhone.text.isNullOrEmpty()
            || binding.edtLocalPhone.text.isNullOrEmpty() || binding.edtEmail.text.isNullOrEmpty()
        ) {
            return false
        } else if (!Utils.isValidName(binding.edtName.text) || !Utils.isValidPhoneNumber(binding.edtCellPhone.text)
            || !Utils.isValidPhoneNumber(binding.edtLocalPhone.text) || !Utils.isValidEmail(binding.edtEmail.text)
        ) {
            return false
        }
        return true
    }

    override fun onActivityResult(resul: Int, codigo: Int, data: Intent?) {
        super.onActivityResult(resul, codigo, data)
        if (codigo == Activity.RESULT_OK) {
            //Image is picked
            if (resul == IMAGE_PICK_GALLERY_CODE) {
                //This means that we picked a image from gallery
                //I want to crop the image#
                assert(data != null)
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            } else if (resul == IMAGE_PICK_CAMERA_CODE) {
                //This means that we picked a image from camera
                //I want to crop the image
                CropImage.activity(imageFileUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            } else if (resul == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                //Cropped imaged received
                val result = CropImage.getActivityResult(data)
                if (result != null) {
                    imageFileUri = result.uri
                }
                //Finally we can set the image to our ProfileImageView
                binding.addPicture.setImageURI(imageFileUri)
                copyFileOrDirectory(
                    "" + imageFileUri.path,
                    "" + getDir("SQLiteRecordImage", Context.MODE_PRIVATE)
                )
            }
        }
    }

    /*
    This is for fixing a problem that I have, that every picture that I take inside the app, and I cleared the cache the photos gets deleted
 */
    private fun copyFileOrDirectory(srcDir: String?, desDir: String?) {
        try {
            val src = File(srcDir)
            val des = File(desDir, src.name)
            if (src.isDirectory) {
                val files = src.list()
                val filesLength = files.size // This was going to be used in a for loop
                for (file in files) {
                    val src1 = File(src, file).path
                    val des1 = des.path
                    copyFileOrDirectory(src1, des1)
                }
            } else {
                copyFile(src, des)
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyFile(srcDir: File, desDir: File) {
        if (!desDir.parentFile.exists()) {
            desDir.mkdirs()
        } else if (!desDir.exists()) {
            desDir.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(srcDir).channel
            destination = FileOutputStream(desDir).channel
            destination.transferFrom(source, 0, source.size())
            imageFileUri = Uri.parse(desDir.path) // Image URI
            Log.e(
                TAG,
                "CopyFile: ImagePath: $imageFileUri"
            )
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        } finally {
            //Close Resources
            source?.close()
            destination?.close()
        }
    }

}