package appjpm4everyone.contactbook.utils

import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Patterns
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

}