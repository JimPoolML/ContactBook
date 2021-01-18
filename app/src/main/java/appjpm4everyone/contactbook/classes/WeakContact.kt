package appjpm4everyone.contactbook.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeakContact(
        @SerializedName(value = "initials", alternate = ["Initials"])
        @Expose
        var initials: String,
        @SerializedName(value = "name", alternate = ["Name"])
        @Expose
        var name: String,
        @SerializedName(value = "number", alternate = ["Number"])
        @Expose
        var number: String,
        @SerializedName(value = "id", alternate = ["Id"])
        @Expose
        var id: Int
        ,
        @SerializedName(value = "image", alternate = ["Image"])
        @Expose
        var image: String
) {
    constructor() : this("", "", "", 0,"")
}