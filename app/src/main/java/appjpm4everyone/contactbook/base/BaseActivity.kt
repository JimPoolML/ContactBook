package appjpm4everyone.contactbook.base

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import appjpm4everyone.contactbook.R
import de.mateware.snacky.Snacky


abstract class BaseActivity : AppCompatActivity() {

    //internal val mProgressDialog = CustomProgressBar()

    /*override fun showProgress(msgRes: String) {
        mProgressDialog.show(this, msgRes)
    }

    override fun showProgress() {
        mProgressDialog.show(this)
    }
    override fun hideProgress() {
        mProgressDialog.hideProgress()
    }*/

    fun showShortSnackError(activity: Activity, message: String, icon: Drawable) {
        showSnack(activity, message, icon, Snacky.LENGTH_SHORT)
    }

    fun showShortSnackError(activity: Activity, msgResource: Int) {
        showSnack(activity, msgResource,  Snacky.LENGTH_SHORT)
    }

    fun showLongSnackError(activity: Activity, message: String, icon: Drawable) {
        showSnack(activity, message, icon, Snacky.LENGTH_LONG)
    }

    fun showLongSnackError(activity: Activity, msgResource: Int) {
        showSnack(activity, msgResource, Snacky.LENGTH_LONG)
    }

    fun showSnack(activity: Activity, message: String, icon: Drawable, lenght: Int) {
        val typeface = ResourcesCompat.getFont(this, R.font.brown_regular)
        Snacky.builder()
            .setActivity(activity)
            .setTextSize(16f)
            .setTextTypeface(typeface)
            .setIcon(icon)
            .setText(message)
            .setBackgroundColor(ContextCompat.getColor(activity, R.color.purple))
            .setDuration(lenght)
            .error()
            .show()
    }

    fun showSnack(activity: Activity, msgResource: Int, lenght: Int) {
        val typeface = ResourcesCompat.getFont(this, R.font.brown_regular)
        Snacky.builder()
            .setActivity(activity)
            .setTextSize(16f)
            .setTextTypeface(typeface)
            .setText(msgResource)
            .setIcon(R.drawable.ic_warning)
            .setDuration(lenght)
            .setBackgroundColor(ContextCompat.getColor(activity, R.color.pink))
            .error()
            .show()
    }

    fun hideKeyboardFrom(activity: Activity) {
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            imm.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0
            )
        }
    }

}