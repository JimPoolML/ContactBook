package appjpm4everyone.contactbook.main

interface OnFragmentContactListener {
    fun onCallContact(callNumber: String, id: Int)
    fun showLongSnackErrorFragment(message: String, icDelete: Int)
    fun setJSONFile(iDS: IntArray)
    fun recoverPosition(id: Int)
    fun onDeleteUser()
}