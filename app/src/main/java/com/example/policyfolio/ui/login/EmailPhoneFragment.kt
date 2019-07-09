package com.example.policyfolio.ui.login


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

import com.example.policyfolio.util.Constants
import com.example.policyfolio.R
import com.example.policyfolio.viewmodels.LoginSignUpViewModel
import com.hbb20.CountryCodePicker
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail

/**
 * A simple [Fragment] subclass.
 */
class EmailPhoneFragment @SuppressLint("ValidFragment") constructor(private val callback: LoginCallback) : Fragment() {

    private var rootView: View? = null
    private var viewModel: LoginSignUpViewModel? = null

    private var email: EditText? = null
    private var phone: LinearLayout? = null
    private var phoneText: EditText? = null
    private var ccp: CountryCodePicker? = null
    private var done: Button? = null

    private var textError: TextView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_email_phone, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(LoginSignUpViewModel::class.java)
        viewModel!!.initiateRepo(context)

        val bundle = arguments

        email = rootView!!.findViewById(R.id.email)
        phoneText = rootView!!.findViewById(R.id.phone_text)
        phone = rootView!!.findViewById(R.id.phone)
        ccp = rootView!!.findViewById(R.id.ccp)
        done = rootView!!.findViewById(R.id.done)

        if (viewModel!!.email != null) {
            email!!.setText(viewModel!!.email)
        }

        if (bundle!!.getInt(Constants.LoginInInfo.TYPE, -1) == Constants.LoginInInfo.Type.PHONE) {
            email!!.visibility = View.GONE
            ccp!!.registerCarrierNumberEditText(phoneText)
            ccp!!.setNumberAutoFormattingEnabled(true)
            done!!.text = "Sign Up"
        } else if (bundle.getInt(Constants.LoginInInfo.TYPE, -1) == Constants.LoginInInfo.Type.EMAIL) {
            phone!!.visibility = View.GONE
            done!!.text = "Next"
        }

        textError = rootView!!.findViewById(R.id.text_empty)

        setOnClick()

        return rootView
    }


    private fun setOnClick() {
        done!!.setOnClickListener {
            if(email!!.isVisible){
                textError!!.visibility = View.GONE
                email!!.validEmail { message ->
                    textError!!.text = message
                    textError!!.visibility = View.VISIBLE
                }
                if(textError!!.isGone){
                    viewModel!!.email = email!!.text.toString()
                    callback.EmailNext()
                } else {
                    callback.showSnackbar("Invalid Email!")
                }
            }
            else if (phone!!.isVisible) {
                val phone = ccp!!.formattedFullNumber
                if (phone == "") {
                    textError!!.visibility = View.VISIBLE
                    textError!!.text = "Can't be empty!"
                    callback.showSnackbar("Incomplete Information!")
                } else if (!ccp!!.isValidFullNumber) {
                    textError!!.visibility = View.VISIBLE
                    textError!!.text = "Invalid Phone Number!"
                    callback.showSnackbar("Invalid Phone Number!")
                } else {
                    textError!!.visibility = View.GONE
                    callback.PhoneSignUp()
                }
            }
        }
    }
}
