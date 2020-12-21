package appjpm4everyone.contactbook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import appjpm4everyone.contactbook.classes.WeakContact
import appjpm4everyone.contactbook.databinding.ItemContactsBinding
import appjpm4everyone.contactbook.utils.Utils
import kotlinx.android.synthetic.main.item_contacts.view.*

class ContactAdapter (private  val context: Context, private val weakContact: List<WeakContact>, private var onGetButton: OnGetButton
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = weakContact[position]
        holder.setContactItem(item.initials, item.name, item.number, context, position)
        holder.setContactClick(item.number.trim(), onGetButton )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val contactsItemBinding = ItemContactsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(
            contactsItemBinding.root
        )
    }

    override fun getItemCount(): Int {
        return weakContact.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun setContactItem(image: String, name: String, number: String, context: Context, position: Int) {
            //It do it because because is compatible with Android 19
            ImageViewCompat.setImageTintList(itemView.rv_circle, Utils.setBackgroundCircle(context, position));
            itemView.rv_circle_txt.text = Utils.getInitialChar(image).toUpperCase()
            itemView.rv_person.text = name
            itemView.rv_number.text = number
        }

        fun setContactClick(number: String, onGetButton: OnGetButton) {
            itemView.rv_constraint.setOnClickListener {
                onGetButton.onClickButton(number)
            }
        }
    }
}