package appjpm4everyone.contactbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import appjpm4everyone.contactbook.classes.WeakContact
import appjpm4everyone.contactbook.databinding.ItemContactsBinding
import kotlinx.android.synthetic.main.item_contacts.view.*

class ContactAdapter (private val weakContact: List<WeakContact>, private var onGetButton: OnGetButton
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = weakContact[position]
        holder.setContactItem(item.initials, item.name, item.number)
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
        fun setContactItem(image: String, name: String, number: String) {
            itemView.rv_circle_txt.text = image
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