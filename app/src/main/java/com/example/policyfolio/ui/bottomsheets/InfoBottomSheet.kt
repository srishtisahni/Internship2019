package com.example.policyfolio.ui.bottomsheets


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.policyfolio.ui.adapters.ListAdapters.BasicDropdownTextAdapter
import com.example.policyfolio.util.Constants
import com.example.policyfolio.R
import com.example.policyfolio.viewmodels.HomeViewModel
import com.hbb20.CountryCodePicker
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 */
class InfoBottomSheet : Fragment, BasicDropdownTextAdapter.ParentCallback {

    private val callback: InfoSheetCallback
    private val viewModel: HomeViewModel
    private var rootView: View? = null

    private var name: EditText? = null
    private var email: EditText? = null
    private var phone: LinearLayout? = null
    private var phoneText: EditText? = null
    private var ccp: CountryCodePicker? = null
    private var birthday: TextView? = null
    private var genderText: TextView? = null
    private var genderChoice: RecyclerView? = null
    private var city: EditText? = null
    private var save: Button? = null

    private var nameError: TextView? = null
    private var emailError: TextView? = null
    private var phoneError: TextView? = null
    private var birthdayError: TextView? = null
    private var cityError: TextView? = null

    private var birthdayEpoch: Long? = null
    private var genderAdapter: BasicDropdownTextAdapter? = null
    private var genderArray: Array<String>? = null
    private var genderSelection: Int = 0

    @SuppressLint("ValidFragment")
    constructor(callback: InfoSheetCallback, viewModel: HomeViewModel) {
        this.callback = callback
        this.viewModel = viewModel
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.bottom_sheet_info, container, false)

        name = rootView!!.findViewById(R.id.name)
        email = rootView!!.findViewById(R.id.email)
        phoneText = rootView!!.findViewById(R.id.phone_text)
        phone = rootView!!.findViewById(R.id.phone)
        ccp = rootView!!.findViewById(R.id.ccp)
        birthday = rootView!!.findViewById(R.id.birthday)
        genderText = rootView!!.findViewById(R.id.gender_text)
        genderChoice = rootView!!.findViewById(R.id.gender_choice)
        city = rootView!!.findViewById(R.id.city)
        save = rootView!!.findViewById(R.id.save)

        nameError = rootView!!.findViewById(R.id.name_empty)
        emailError = rootView!!.findViewById(R.id.email_empty)
        phoneError = rootView!!.findViewById(R.id.phone_empty)
        birthdayError = rootView!!.findViewById(R.id.birthday_empty)
        cityError = rootView!!.findViewById(R.id.city_empty)

        ccp!!.registerCarrierNumberEditText(phoneText)
        ccp!!.setNumberAutoFormattingEnabled(true)

        setGenderAdapter()

        birthday!!.setOnClickListener { FetchDate() }

        save!!.setOnClickListener { save() }

        setAvailableInfo()

        return rootView
    }

    fun setAvailableInfo() {
        if (viewModel.email != null)
            email!!.setText(viewModel.email)
        if (viewModel.name != null)
            name!!.setText(viewModel.name)
        if (viewModel.phone != null)
            phoneText!!.setText(viewModel.phone)
        if (viewModel.birthday != null) {
            birthday!!.text = Constants.Time.DATE_FORMAT.format(viewModel.birthday!! * 1000)
            birthdayEpoch = viewModel.birthday
            birthday!!.setTextColor(resources.getColor(R.color.white))
        }
        setValue(viewModel.gender, Constants.ListTypes.GENDER)
        if (viewModel.city != null)
            city!!.setText(viewModel.city)
    }

    private fun save() {
        val phone = ccp!!.formattedFullNumber
        if (phone == "") {
            phoneError!!.isVisible = true
            phoneError!!.isGone = false
            phoneError!!.text = "Can't be empty!"
        } else if (!ccp!!.isValidFullNumber) {
            phoneError!!.isVisible = true
            phoneError!!.isGone = false
            phoneError!!.text = "Invalid Phone Number!"
        } else {
            phoneError!!.isVisible = false
            phoneError!!.isGone = true
        }

        nameError!!.isVisible = false
        nameError!!.isGone = true
        name!!.nonEmpty { message ->
            nameError!!.text = message
            nameError!!.isVisible = true
            nameError!!.isGone = false
        }

        if (birthdayEpoch == null) {
            birthdayError!!.isVisible = true
            birthdayError!!.isGone = false
        } else {
            birthdayError!!.isVisible = false
            birthdayError!!.isGone = true
        }

        cityError!!.isVisible = false
        cityError!!.isGone = true
        city!!.nonEmpty { message ->
            cityError!!.text = message
            cityError!!.isVisible = true
            cityError!!.isGone = false
        }

        if (nameError!!.isGone && birthdayError!!.isGone && cityError!!.isGone && phoneError!!.isGone) {
            viewModel.complete = true
            viewModel.name = name!!.text.toString()
            viewModel.birthday = birthdayEpoch
            viewModel.gender = genderSelection
            viewModel.city = city!!.text.toString()
            viewModel.phone = phone
            callback.updateInfo()
        }
    }

    private fun FetchDate() {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.date_picker)
        dialog.setTitle("")
        val datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { view, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            birthdayEpoch = calendar.timeInMillis / 1000
            birthday!!.text = Constants.Time.DATE_FORMAT.format(birthdayEpoch!! * 1000)
            birthday!!.setTextColor(resources.getColor(R.color.white))
        }
        dialog.show()
    }

    private fun setGenderAdapter() {
        genderSelection = 0
        genderArray = resources.getStringArray(R.array.gender_array)
        genderAdapter = BasicDropdownTextAdapter(context, genderArray, this, Constants.ListTypes.GENDER, resources.getColor(R.color.colorPrimaryDarkest))
        genderChoice!!.adapter = genderAdapter
        genderChoice!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        genderText!!.setOnClickListener {
            genderText!!.visibility = View.GONE
            genderChoice!!.visibility = View.VISIBLE
        }
    }

    override fun setValue(position: Int, type: Int) {
        if (type == Constants.ListTypes.GENDER) {
            genderSelection = position
            if (genderSelection == 0)
                genderText!!.setTextColor(resources.getColor(R.color.dustyWhite))
            else
                genderText!!.setTextColor(resources.getColor(R.color.white))
            genderText!!.text = genderArray!![genderSelection]
            genderText!!.visibility = View.VISIBLE
            genderChoice!!.visibility = View.GONE
        }
    }

}
