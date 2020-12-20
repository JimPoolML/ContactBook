package appjpm4everyone.contactbook.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeakContact (
    @SerializedName(value = "initials", alternate = ["Initials"])
    @Expose
    var initials:String,
    @SerializedName(value = "name", alternate = ["Name"])
    @Expose
    var name: String,
    @SerializedName(value = "number", alternate = ["Number"])
    @Expose
    var number: String
){
    constructor() : this("", "", "" )
}