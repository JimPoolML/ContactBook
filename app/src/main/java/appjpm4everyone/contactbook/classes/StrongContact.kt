package appjpm4everyone.contactbook.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StrongContact (
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
        var email: String
){
    constructor() : this("", "", "", "", "" )
}