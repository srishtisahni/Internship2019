package com.example.policyfolio.ui.login


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

import com.example.policyfolio.ui.adapters.ListAdapters.BasicDropdownTextAdapter
import com.example.policyfolio.util.Constants
import com.example.policyfolio.R
import com.example.policyfolio.viewmodels.LoginSignUpViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment @SuppressLint("ValidFragment") constructor(private val callback: LoginCallback) : Fragment(), BasicDropdownTextAdapter.ParentCallback {

    private var rootView: View? = null
    private var viewModel: LoginSignUpViewModel? = null

    private var name: EditText? = null
    private var birthday: TextView? = null
    private var gender: TextView? = null
    private var city: EditText? = null
    private var password: EditText? = null
    private var signUp: Button? = null

    private var nameError: TextView? = null
    private var birthdayError: TextView? = null
    private var cityError: TextView? = null
    private var passwordError: TextView? = null

    private var genderAdapter: BasicDropdownTextAdapter? = null
    private var birthdayEpoch: Long? = null
    private var genderSelection: Int = 0
    private var genderArray: Array<String>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(LoginSignUpViewModel::class.java)
        viewModel!!.initiateRepo(context)

        name = rootView!!.findViewById(R.id.name)
        birthday = rootView!!.findViewById(R.id.birthday)
        gender = rootView!!.findViewById(R.id.gender)
        city = rootView!!.findViewById(R.id.city)
        password = rootView!!.findViewById(R.id.password)
        signUp = rootView!!.findViewById(R.id.sign_up)

        nameError = rootView!!.findViewById(R.id.name_empty)
        birthdayError = rootView!!.findViewById(R.id.birthday_empty)
        cityError = rootView!!.findViewById(R.id.city_empty)
        passwordError = rootView!!.findViewById(R.id.password_empty)

        birthday!!.setOnClickListener { fetchDate() }

        setGenderAdapter()

        signUp!!.setOnClickListener { signUp() }

        return rootView
    }

    private fun signUp() {
        name!!.validator()
                .nonEmpty()
                .addErrorCallback { message ->
                    nameError!!.text = message
                    nameError!!.visibility = View.VISIBLE
                }
                .addSuccessCallback {
                    nameError!!.visibility = View.GONE
                }.check()
        Log.e("NAME ERROR",nameError!!.isVisible.toString())

        if (birthdayEpoch == null) {
            birthdayError!!.visibility = View.VISIBLE
        } else {
            birthdayError!!.visibility = View.GONE
        }
        Log.e("BIRTHDAY ERROR",birthdayError!!.isVisible.toString())

        city!!.validator()
                .nonEmpty()
                .addErrorCallback { message ->
                    cityError!!.text = message
                    cityError!!.visibility = View.VISIBLE
                }
                .addSuccessCallback {
                    cityError!!.visibility = View.GONE
                }.check()
        Log.e("CITY ERROR",cityError!!.isVisible.toString())

        password!!.validator()
                .nonEmpty()
                .minLength(8)
                .addErrorCallback {message ->
                    passwordError!!.text = message
                    passwordError!!.visibility = View.VISIBLE
                }
                .addSuccessCallback {
                    passwordError!!.visibility = View.GONE
                }.check()
        Log.e("PASSWORD ERROR",passwordError!!.isVisible.toString())

        if (nameError!!.isGone && birthdayError!!.isGone && cityError!!.isGone && passwordError!!.isGone) {
            viewModel!!.setName(name!!.text.toString())
            viewModel!!.setBirthDay(birthdayEpoch)
            viewModel!!.setGender(genderSelection)
            viewModel!!.setCity(city!!.text.toString())
            viewModel!!.setPassword(password!!.text.toString())
            callback.SignUpEmailAndPassword()
        } else {
            callback.showSnackbar("Incomplete Information!")
        }
    }

    private fun fetchDate() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            birthdayEpoch = calendar.timeInMillis / 1000
            birthday!!.text = Constants.Time.DATE_FORMAT.format(birthdayEpoch!! * 1000)
            birthday!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
        }

        DatePickerDialog(context,R.style.MyDatePickerDialogTheme, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun setGenderAdapter() {
        genderSelection = 0
        genderArray = resources.getStringArray(R.array.gender_array)
        genderAdapter = BasicDropdownTextAdapter(context, genderArray, this, Constants.ListTypes.GENDER, resources.getColor(R.color.colorPrimaryDarkest))

        gender!!.setOnClickListener { callback.openListSheet(Constants.ListTypes.GENDER, genderAdapter) }
    }

    override fun setValue(position: Int, type: Int) {
        callback.closeListSheet()
        if (type == Constants.ListTypes.GENDER) {
            genderSelection = position
            if (genderSelection == 0)
                gender!!.setTextColor(resources.getColor(R.color.borderGrey))
            else
                gender!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            gender!!.text = genderArray!![genderSelection]
        }
    }
}
