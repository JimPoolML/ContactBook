package appjpm4everyone.contactbook.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import appjpm4everyone.contactbook.R
import appjpm4everyone.contactbook.classes.StrongContact
import appjpm4everyone.contactbook.databinding.FragmentDataContactBinding
import appjpm4everyone.contactbook.utils.Utils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DataContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DataContactFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listener: OnFragmentDataContactListener? = null

    //Binding
    private lateinit var binding: FragmentDataContactBinding

    //Bundle
    private lateinit var strongContact: StrongContact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        strongContact = if(arguments!=null) {
            // Inflate the layout for this fragment
            arguments?.getParcelable("example")!!
        }else{
            StrongContact(0,"", "", "", "", "","", 0, 0)
        }
        binding = FragmentDataContactBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        //setStrongContact(StrongContact(0,"a", "b", "c", "d", "e"))
        if(strongContact!=null) {
            setStrongContact(strongContact)
        }else{
            setStrongContact(StrongContact(0,"", "", "", "", "", "",34, 34))
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initUI() {
        binding.btnCall.setOnClickListener{
            val phoneNumber = "(+${binding.ccpCodeCountryCell.selectedCountryCode}) ${binding.edtCellPhone.text}"
            //val phoneNumber = "(+${strongContact.country}) ${strongContact.cellPhone}"
            Log.d("number phone fragment: ", phoneNumber)
            callContact(phoneNumber)
        }

        //binding.addImage.background = resources.getDrawable(R.drawable.ic_default_user)
        binding.addImage.setImageResource(R.drawable.ic_default_user)
        binding.ccpCodeCountryCell.enableDialogInitialScrollToSelection(false)

    }

    private fun callContact(cellPhone: String) {
        listener?.onCallLandscapeContact(cellPhone)
    }

    fun setStrongContact(strongContact: StrongContact) {

        if(strongContact.image.isNullOrEmpty()){
            binding.addImage.setImageResource(R.drawable.ic_default_user)
        }else{
            binding.addImage.setImageURI(Uri.parse(strongContact.image))
        }

        if(strongContact.name != null){
            binding.edtName.text = Utils.editableToString(strongContact.name)
        }
        if(strongContact.address != null){
            binding.edtAddress.text = Utils.editableToString(strongContact.address)
        }
        if(strongContact.cellPhone != null){
            binding.edtCellPhone.text = Utils.editableToString(strongContact.cellPhone)
        }
        if(strongContact.localPhone != null){
            binding.edtLocalPhone.text = Utils.editableToString(strongContact.localPhone)
        }
        if(strongContact.email != null){
            binding.edtEmail.text = Utils.editableToString(strongContact.email)
        }
        //Country picker
        if(strongContact.country != 0){
            binding.ccpCodeCountryCell.setCountryForPhoneCode(strongContact.country)
        }
        if(strongContact.countryTel != 0){
            binding.ccpCodeCountryTel.setCountryForPhoneCode(strongContact.countryTel)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentDataContactListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        this.strongContact = StrongContact(0, "", "","","","","", 0 ,0)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DataContactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DataContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}