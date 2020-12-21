package appjpm4everyone.contactbook.utils

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Patterns
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import appjpm4everyone.contactbook.R
import kotlinx.android.synthetic.main.item_contacts.view.*
import java.util.regex.Pattern


object Utils {

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target != null) {
            if (target.isEmpty()) {
                false
            } else {
                Patterns.EMAIL_ADDRESS.matcher(target).matches()
            }
        }else{
            false
        }
    }

    fun isValidName(target: CharSequence?): Boolean {
        val patron = Pattern.compile("^[a-zA-Z ]+$")
        return if (target != null) {
            if (target.isEmpty()) {
                false
            } else {
                patron.matcher(target).matches() &&  (target.length<30)
            }
        }else{
            false
        }
    }

    fun isValidPhoneNumber(target: CharSequence?): Boolean {
        return if (target != null) {
            if (target.isEmpty()) {
                false
            } else {
                Patterns.PHONE.matcher(target).matches()
            }
        }else{
            false
        }
    }

    fun editableToString(string: String) : Editable{
        return if(string.isNullOrEmpty()){
            SpannableStringBuilder("")
        }else{
            SpannableStringBuilder(string)
        }
    }

    fun getInitialChar(string: String) : String {
        return if (string != null) {
            if (string.isEmpty()) {
                ""
            } else {
                val words = string.trim()
                val numberOfInputWords : Int  = words.split("\\s+".toRegex()).size
                if(numberOfInputWords >= 2){
                    val totalWords = words.split("\\s+".toRegex())
                    ""+totalWords[0].first() + totalWords[1].first()
                } else{
                    string.substring(0,2)
                }
            }
        }else{
            ""
        }
    }

    fun throwRandom() : Int{
        // generated random from 1 to 9 included
        return (0..10).random()
    }

    fun setBackgroundCircle(context: Context, position: Int): ColorStateList? {
        return when {
            position % 9 == 0 -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.magenta_generic_id))
            }
            position % 8 == 0 -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_text_nav))
            }
            position % 7 == 0 -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))
            }
            position % 6 == 0 -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.alert_1))
            }
            position % 5 == 0 -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_ligth))
            }
            position % 4 == 0 -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_abis))
            }
            position % 3 == 0 -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.success))
            }
            position % 2 == 0 -> {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_abis))
            }
            else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple))
        }
    }

}