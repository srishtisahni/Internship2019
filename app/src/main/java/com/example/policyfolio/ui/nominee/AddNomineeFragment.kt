package com.example.policyfolio.ui.nominee


import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.example.policyfolio.R
import com.example.policyfolio.ui.adapters.ListAdapters.BasicDropdownTextAdapter
import com.example.policyfolio.util.Constants
import com.example.policyfolio.viewmodels.NomineeViewModel
import com.hbb20.CountryCodePicker
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

/**
 * A simple [Fragment] subclass.
 */
class AddNomineeFragment(private val callback: NomineeCallback) : Fragment(), BasicDropdownTextAdapter.ParentCallback {

    private var rootView: View? = null
    private var viewModel: NomineeViewModel? = null

    private var name: EditText? = null
    private var nameError: TextView? = null

    private var email: EditText? = null
    private var emailError: TextView? = null

    private var phone: LinearLayout? = null
    private var phoneText: EditText? = null
    private var ccp: CountryCodePicker? = null
    private var phoneError: TextView? = null

    private var alternativePhone: LinearLayout? = null
    private var altPhoneText: EditText? = null
    private var altCcp: CountryCodePicker? = null

    private var relationText: TextView? = null
    private var relation: LinearLayout? = null
    private var textAdapter: BasicDropdownTextAdapter? = null
    private var relations: Array<String>? = null
    private var relationError: TextView? = null

    private var done: Button? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_add_nominee, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(NomineeViewModel::class.java)

        name = rootView!!.findViewById(R.id.name)
        nameError = rootView!!.findViewById(R.id.name_empty)

        email = rootView!!.findViewById(R.id.email)
        emailError = rootView!!.findViewById(R.id.email_empty)

        phoneText = rootView!!.findViewById(R.id.phone_text)
        phone = rootView!!.findViewById(R.id.phone)
        ccp = rootView!!.findViewById(R.id.ccp)
        phoneError = rootView!!.findViewById(R.id.phone_empty)


        alternativePhone = rootView!!.findViewById(R.id.alt_phone)
        altPhoneText = rootView!!.findViewById(R.id.alt_phone_text)
        altCcp = rootView!!.findViewById(R.id.ccp_alt)

        relationText = rootView!!.findViewById(R.id.relation_text)
        relation = rootView!!.findViewById(R.id.relation)
        relationError = rootView!!.findViewById(R.id.relation_empty)

        relations = resources.getStringArray(R.array.relationship_array)
        textAdapter = BasicDropdownTextAdapter(context, relations, this, Constants.ListTypes.RELATIONSHIPS, resources.getColor(R.color.colorAccent))

        done = rootView!!.findViewById(R.id.done)

        setListeners()

        return rootView
    }

    private fun setListeners() {
        ccp!!.registerCarrierNumberEditText(phoneText)
        ccp!!.setNumberAutoFormattingEnabled(true)

        altCcp!!.registerCarrierNumberEditText(altPhoneText)
        altCcp!!.setNumberAutoFormattingEnabled(true)

        name!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = s.toString()
                if (string.isNotEmpty())
                    name!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
                else
                    name!!.background = resources.getDrawable(R.drawable.text_background_grey_8dp)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        email!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = s.toString()
                if (string.isNotEmpty())
                    email!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
                else
                    email!!.background = resources.getDrawable(R.drawable.text_background_grey_8dp)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        phoneText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = s.toString()
                if (string.isNotEmpty())
                    phone!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
                else
                    phone!!.background = resources.getDrawable(R.drawable.text_background_grey_8dp)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        altPhoneText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = s.toString()
                if (string.isNotEmpty())
                    alternativePhone!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
                else
                    alternativePhone!!.background = resources.getDrawable(R.drawable.text_background_grey_8dp)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        viewModel!!.relation = -1
        relation!!.setOnClickListener { callback.openListSheet(Constants.ListTypes.RELATIONSHIPS, textAdapter) }

        done!!.setOnClickListener {
            var isComplete = true
            name!!.validator()
                    .nonEmpty()
                    .addErrorCallback { message ->
                        nameError!!.text = message
                        nameError!!.setTextColor(resources!!.getColor(R.color.red))
                        isComplete = false
                    }
                    .addSuccessCallback {
                        nameError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
                    }.check()
            emailError!!.validator()
                    .validEmail()
                    .addErrorCallback { message ->
                        emailError!!.text = message
                        emailError!!.setTextColor(resources!!.getColor(R.color.red))
                        isComplete = false
                    }
                    .addSuccessCallback {
                        emailError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
                    }.check()

            val phone = ccp!!.formattedFullNumber
            if(ccp!!.isValidFullNumber){
                phoneError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
                viewModel!!.phone = phone
            } else{
                phoneError!!.text = "Invalid Phone Number!"
                phoneError!!.setTextColor(resources!!.getColor(R.color.red))
                isComplete = false
            }

            if(viewModel!!.relation == -1){
                relationError!!.setTextColor(resources!!.getColor(R.color.red))
                isComplete = false
                relationError!!.text = "Can't be empty!"
            } else {
                relationError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
            }

            val altPhone = altCcp!!.formattedFullNumber
            if(altCcp!!.isValidFullNumber){
                viewModel!!.alternateNumber = altPhone
            } else if(altPhoneText!!.length() > 0){
                callback.showSnackbar("Invalid Alternate Phone Number")
                isComplete = false
            }

            if(isComplete){
                viewModel!!.name = name!!.text.toString()
                viewModel!!.email = email!!.text.toString()
                callback.done()
            } else{
                callback.showSnackbar("Invalid Information!")
            }
        }
    }

    override fun setValue(position: Int, type: Int) {
        callback.closeListSheet()
        if (type == Constants.ListTypes.RELATIONSHIPS) {
            relationText!!.text = relations!![position]
            relationText!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            relation!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
            viewModel!!.relation = position
        }
    }
}
