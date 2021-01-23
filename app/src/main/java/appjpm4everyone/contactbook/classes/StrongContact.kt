package appjpm4everyone.contactbook.classes

import android.os.Parcelable
import android.provider.BaseColumns
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StrongContact (
        @SerializedName(value = "id", alternate = ["Id"])
        @Expose
        var id: Int,
        @SerializedName(value = "name", alternate = ["Name"])
        @Expose
        var name: String,
        @SerializedName(value = "address", alternate = ["Address"])
        @Expose
        var address: String,
        @SerializedName(value = "cellPhone", alternate = ["CellPhone"])
        @Expose
        var cellPhone: String,
        @SerializedName(value = "localPhone", alternate = ["LocalPhone"])
        @Expose
        var localPhone: String,
        @SerializedName(value = "email", alternate = ["Email"])
        @Expose
        var email: String,
        @SerializedName(value = "image", alternate = ["Image"])
        @Expose
        var image: String,
        @SerializedName(value = "country", alternate = ["Country"])
        @Expose
        var country: Int,
        @SerializedName(value = "countryTel", alternate = ["CountryTel"])
        @Expose
        var countryTel: Int
) : Parcelable {
    constructor() : this(0,"", "", "", "", "", "" , 0, 0)

        // Table contents are grouped together in an anonymous object.
        object StrongContact : BaseColumns {
                const val COL_ID = "id"
                const val COL_NAME = "name"
                const val COL_ADDRESS = "address"
                const val COL_CELLPHONE = "cellPhone"
                const val COL_LOCAL_PHONE = "localPhone"
                const val COL_EMAIL = "email"
                const val COL_IMAGE = "image"
                const val COL_COUNTRY = "country"
                const val COL_COUNTRY_TEL = "countryTel"
        }
}