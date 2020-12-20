package appjpm4everyone.contactbook.createuser

import android.os.Bundle
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.base.BaseActivity
import appjpm4everyone.contactbook.databinding.ActivityCreateUserBinding

class CreateUserActivity : BaseActivity() {

    private lateinit var binding : ActivityCreateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.botonCancelar.setOnClickListener {
            hideKeyboardFrom(this)
            finish()
        }
    }


}