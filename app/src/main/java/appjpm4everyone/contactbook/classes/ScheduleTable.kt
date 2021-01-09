package appjpm4everyone.contactbook.classes

import android.provider.BaseColumns
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ScheduleTable(
    @SerializedName(value = "id", alternate = ["Id"])
    @Expose
    var id: Int,
    @SerializedName(value = "event", alternate = ["Event"])
    @Expose
    var event: String,
    @SerializedName(value = "date", alternate = ["Date"])
    @Expose
    var date: String,
    @SerializedName(value = "clock", alternate = ["Clock"])
    @Expose
    var clock: String
) {
    constructor() : this(0, "", "", "")

    // Table contents are grouped together in an anonymous object.
    object ScheduleTable : BaseColumns {
        const val COL_ID = "id"
        const val COL_EVENT = "event"
        const val COL_DATE = "date"
        const val COL_CLOCK = "clock"
    }
}