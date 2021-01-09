package appjpm4everyone.contactbook.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import appjpm4everyone.contactbook.classes.ScheduleTable
import appjpm4everyone.contactbook.databinding.ItemAgendBinding
import appjpm4everyone.contactbook.databinding.ItemContactsBinding
import appjpm4everyone.contactbook.utils.Utils
import kotlinx.android.synthetic.main.item_agend.view.*
import kotlinx.android.synthetic.main.item_contacts.view.rv_constraint

class ScheduleAdapter(private val context: Context, private val scheduleTable: List<ScheduleTable>, private var onGetButton: OnGetButton
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = scheduleTable[position]
        holder.setContactItem(item.event, item.date, item.clock, context, position)
        holder.setContactClick(item.event.trim(), item.id, onGetButton )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemAgendBinding = ItemAgendBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(
            itemAgendBinding.root
        )
    }

    override fun getItemCount(): Int {
        return scheduleTable.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun setContactItem(event: String, date: String, clock: String, context: Context, position: Int) {
            //It do it because because is compatible with Android 19
            ImageViewCompat.setImageTintList(itemView.img_calendar, Utils.setBackgroundCircle(context, position))
            ImageViewCompat.setImageTintList(itemView.img_clock, Utils.setBackgroundCircle(context, position))
            itemView.txt_date.text = event
            itemView.txt_calendar.text = date
            itemView.txt_clock.text = clock
        }

        fun setContactClick(
            number: String,
            id: Int,
            onGetButton: OnGetButton
        ) {
            itemView.rv_constraint.setOnClickListener {
                Log.d("my id is: ", id.toString())
                onGetButton.onClickButton(number, id)
            }
        }
    }
}