package appjpm4everyone.contactbook.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import appjpm4everyone.contactbook.R
import kotlinx.android.synthetic.main.view_custom_alert_dialog.view.*

class CustomAlertDialog {
    lateinit var dialog: Dialog
    lateinit var view: View

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.view_custom_alert_dialog, null)
        view.customAlertDialog.setBackgroundColor(Color.parseColor("#70000000")) // Background Color
        view.cad_cardview.setCardBackgroundColor(Color.parseColor("#FFFFFF")) // Box Color
        dialog = Dialog(context, R.style.CustomProgressBarTheme)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        if(!dialog.isShowing) {
            dialog.show()
        }
        return dialog
    }

    fun hideProgress() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }

}