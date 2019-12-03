package com.example.policyfolio.ui.bottomsheets


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment

import com.example.policyfolio.util.Constants
import com.example.policyfolio.R
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

/**
 * A simple [Fragment] subclass.
 */
class EmailBottomSheet @SuppressLint("ValidFragment") constructor(private val callBack: EmailSheetCallback) : Fragment() {

    private var rootView: View? = null

    private var email: EditText? = null
    private var next: Button? = null
    private var error: TextView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(R.layout.bottom_sheet_email, container, false)

        email = rootView!!.findViewById(R.id.email)
        next = rootView!!.findViewById(R.id.next)
        error = rootView!!.findViewById(R.id.error)

        if (arguments != null) {
            email!!.setText(arguments!!.getString(Constants.User.EMAIL, null))
        }

        next!!.isEnabled = false
        next!!.background = resources.getDrawable(R.drawable.disabled_button_background_white)
        next!!.setTextColor(resources.getColor(R.color.Grey))

        email!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val text = charSequence.toString()
                text.validator()
                        .validEmail()
                        .addErrorCallback {
                            next!!.isEnabled = false
                            next!!.background = resources.getDrawable(R.drawable.disabled_button_background_white)
                            next!!.setTextColor(resources.getColor(R.color.Grey))
                            error!!.setTextColor(resources!!.getColor(R.color.red))
                        }
                        .addSuccessCallback {
                            next!!.isEnabled = true
                            next!!.background = resources.getDrawable(R.drawable.button_background_white)
                            next!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                            error!!.setTextColor(resources!!.getColor(android.R.color.transparent))
                        }
                        .check()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        next!!.setOnClickListener { callBack.ForgotPassword(email!!.text.toString()) }

        return rootView
    }

}
