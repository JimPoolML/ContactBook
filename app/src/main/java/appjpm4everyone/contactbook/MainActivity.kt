package appjpm4everyone.contactbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import appjpm4everyone.contactbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

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
    }
}