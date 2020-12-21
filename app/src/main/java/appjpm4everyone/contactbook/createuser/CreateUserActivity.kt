package appjpm4everyone.contactbook.createuser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.databinding.ActivityCreateUserBinding
import appjpm4everyone.contactbook.utils.Utils


class CreateUserActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateUserBinding
    private var idPosition : Int = 0
    private var isModify = false

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
        }
    }




    private fun initUI() {
        binding.botonCancelar.setOnClickListener {
            hideKeyboardFrom(this)
            finish()
        }

        binding.botonAceptar.setOnClickListener {
            hideKeyboardFrom(this)
            setDataBaseValues()
        }
    }

    private fun setDataBaseValues() {
        if(validateText()){
            val intent = Intent()
            if(isModify){
                intent.putExtra("id", idPosition)
            }
            intent.putExtra("name", ""+binding.edtName.text)
            intent.putExtra("address", ""+binding.edtAddress.text)
            intent.putExtra("cellPhone", ""+binding.edtCellPhone.text)
            intent.putExtra("localPhone", ""+binding.edtLocalPhone.text)
            intent.putExtra("email", ""+binding.edtEmail.text)
            setResult(Activity.RESULT_OK, intent)
            showLongSnackError(this@CreateUserActivity, resources.getString(R.string.text_complete),
                    ContextCompat.getDrawable(this@CreateUserActivity, R.drawable.ic_check_ok)!!)
            finish()
        }else{
            showLongSnackError(this@CreateUserActivity, resources.getString(R.string.text_incomplete),
                    ContextCompat.getDrawable(this@CreateUserActivity, R.drawable.ic_check_ok)!!)
        }
    }

    private fun validateText(): Boolean {
        if(binding.edtName.text.isNullOrEmpty() || binding.edtAddress.text.isNullOrEmpty() || binding.edtCellPhone.text.isNullOrEmpty()
                || binding.edtLocalPhone.text.isNullOrEmpty() || binding.edtEmail.text.isNullOrEmpty()  ){
            return false
        }else if(!Utils.isValidName(binding.edtName.text) || !Utils.isValidPhoneNumber(binding.edtCellPhone.text)
                || !Utils.isValidPhoneNumber(binding.edtLocalPhone.text) || !Utils.isValidEmail(binding.edtEmail.text)){
            return false
        }
        return true
    }

}