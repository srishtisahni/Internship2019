package com.example.policyfolio.ui.login


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.policyfolio.ui.adapters.ListAdapters.BasicDropdownTextAdapter
import com.example.policyfolio.util.Constants
import com.example.policyfolio.R
import com.example.policyfolio.viewmodels.LoginSignUpViewModel
import com.facebook.login.widget.LoginButton
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import de.hdodenhof.circleimageview.CircleImageView

import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment @SuppressLint("ValidFragment") constructor(private val callback: LoginCallback) : Fragment() {

    private var rootView: View? = null
    private var viewModel: LoginSignUpViewModel? = null

    private var name: EditText? = null
    private var email: EditText? = null
    private var password: EditText? = null

    private var login: TextView? = null
    private var signUp: Button? = null
    private var google: CircleImageView? = null
    private var facebook: CircleImageView? = null
    private var facebookLogin: LoginButton? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(LoginSignUpViewModel::class.java)
        viewModel!!.initiateRepo(context)

        name = rootView!!.findViewById(R.id.name)
        password = rootView!!.findViewById(R.id.password)
        email = rootView!!.findViewById(R.id.email)

        login = rootView!!.findViewById(R.id.login)
        google = rootView!!.findViewById(R.id.google_signUp)
        facebook = rootView!!.findViewById(R.id.facebook_signUp)

        facebookLogin = rootView!!.findViewById(R.id.facebook_login)
        facebookLogin!!.setPermissions(listOf(Constants.Facebook.BIRTHDAY, Constants.Facebook.EMAIL, Constants.Facebook.GENDER, Constants.Facebook.PROFILE, Constants.Facebook.LOCATION))
        facebookLogin!!.fragment = this

        signUp = rootView!!.findViewById(R.id.sign_up)

        facebookCallback()
        setListeners()

        return rootView
    }

    private fun facebookCallback() {
        viewModel!!.facebookLogin().observe(this, Observer { integer ->
            if (integer != null) {
                when (integer) {
                    Constants.Facebook.Login.LOGGED_IN -> viewModel!!.fetchFacebookData().observe(this, Observer { facebookData ->
                        if (facebookData != null) {
                            callback.FacebookSignUp(facebookData)
                        } else
                            Toast.makeText(context, "Login Error Occurred", Toast.LENGTH_LONG).show()
                    })

                    Constants.Facebook.Login.LOGIN_CANCELLED -> Toast.makeText(context, "Login Cancelled", Toast.LENGTH_LONG).show()

                    Constants.Facebook.Login.LOGIN_FAILED -> Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG).show()

                    Constants.Facebook.Login.LOGIN_ERROR -> Toast.makeText(context, "Login Error Occurred", Toast.LENGTH_LONG).show()
                }
            } else
                Toast.makeText(context, "Login Error Occurred", Toast.LENGTH_LONG).show()
        })
    }

    private fun setListeners() {
        signUp!!.setOnClickListener {
            //TODO Validations
        }

        login!!.setOnClickListener {
            callback.openLoginFragment()
        }

        google!!.setOnClickListener {
            val id = getString(R.string.default_web_client_id)
            viewModel!!.initiateGoogleLogin(id, context)
            callback.GoogleSignUp()
        }

        facebook!!.setOnClickListener { facebookLogin!!.performClick() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel!!.onActivityResult(requestCode, resultCode, data)
    }
}
