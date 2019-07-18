package com.example.policyfolio.ui.login


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.policyfolio.R
import com.example.policyfolio.util.Constants
import com.example.policyfolio.viewmodels.LoginSignUpViewModel
import com.facebook.login.widget.LoginButton
import com.google.android.material.textfield.TextInputLayout
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment @SuppressLint("ValidFragment") constructor(private val callback: LoginCallback) : Fragment() {

    private var rootView: View? = null
    private var viewModel: LoginSignUpViewModel? = null
    private var type: Int = -1

    private var emailPhone: EditText? = null
    private var emailWrap: TextInputLayout? = null
    private var password: EditText? = null
    private var passwordWrap: TextInputLayout? = null
    private var forgetPassword: TextView? = null
    private var login: Button? = null

    private var google: CircleImageView? = null
    private var facebook: CircleImageView? = null
    private var facebookLogin: LoginButton? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_login, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(LoginSignUpViewModel::class.java)
        viewModel!!.initiateRepo(context)

        emailPhone = rootView!!.findViewById(R.id.email)
        emailWrap = rootView!!.findViewById(R.id.email_wrap)
        password = rootView!!.findViewById(R.id.password)
        passwordWrap = rootView!!.findViewById(R.id.password_wrap)
        login = rootView!!.findViewById(R.id.login)
        google = rootView!!.findViewById(R.id.google_signUp)
        facebook = rootView!!.findViewById(R.id.facebook_signUp)
        forgetPassword = rootView!!.findViewById(R.id.forgot_password)

        facebookLogin = rootView!!.findViewById(R.id.facebook_login)
        facebookLogin!!.setPermissions(listOf(Constants.Facebook.BIRTHDAY, Constants.Facebook.EMAIL, Constants.Facebook.GENDER, Constants.Facebook.PROFILE, Constants.Facebook.LOCATION))
        facebookLogin!!.fragment = this

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
        emailPhone!!.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var valid = false
                s!!.toString()
                        .validator()
                        .validEmail()
                        .addSuccessCallback {
                            type = Constants.LoginInInfo.Type.EMAIL
                            valid = true
                        }.check()
                s!!.toString()
                        .validator()
                        .validNumber()
                        .minLength(10)
                        .addSuccessCallback {
                            if(s.toString()[0]!='+'){
                                emailPhone!!.setText("+91$s")
                            }
                            type = Constants.LoginInInfo.Type.PHONE
                            valid = true
                        }.check()

                if(!valid){
                    emailWrap!!.isErrorEnabled = true
                    emailWrap!!.error = "Invalid Email or Phone Number"
                    type = -1
                } else {
                    emailWrap!!.isErrorEnabled = false
                    if (type == Constants.LoginInInfo.Type.PHONE) {
                        var phone = emailPhone!!.text.toString()
                        if(Patterns.PHONE.matcher(phone).matches())
                            callback.sendOTP(phone)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var valid = false
                s!!.toString()
                        .validator()
                        .validEmail()
                        .addSuccessCallback {
                            type = Constants.LoginInInfo.Type.EMAIL
                            valid = true
                        }.check()
                s!!.toString()
                        .validator()
                        .validNumber()
                        .minLength(10)
                        .addSuccessCallback {
                            if(s.toString()[0]!='+'){
                                emailPhone!!.setText("+91$s")
                            }
                            type = Constants.LoginInInfo.Type.PHONE
                            valid = true
                        }.check()

                if(!valid){
                    emailWrap!!.isErrorEnabled = true
                    emailWrap!!.error = "Invalid Email or Phone Number"
                    type = -1
                } else {
                    emailWrap!!.isErrorEnabled = false
                    if (type == Constants.LoginInInfo.Type.PHONE) {
                        var phone = emailPhone!!.text.toString()
                        if(Patterns.PHONE.matcher(phone).matches())
                            callback.sendOTP(phone)
                    }
                }
            }
        })

        forgetPassword!!.setOnClickListener { callback.forgotPassword() }

        login!!.setOnClickListener {
            var isComplete = true

            if(type == Constants.LoginInInfo.Type.EMAIL){
                password!!.validator()
                        .minLength(8)
                        .addSuccessCallback {
                            passwordWrap!!.isErrorEnabled = false
                        }
                        .addErrorCallback {
                            isComplete = false
                            passwordWrap!!.isErrorEnabled = true
                            passwordWrap!!.error = it
                        }.check()
            } else if(type == Constants.LoginInInfo.Type.PHONE){
                password!!.validator()
                        .minLength(6)
                        .addSuccessCallback {
                            passwordWrap!!.isErrorEnabled = false
                        }
                        .addErrorCallback {
                            isComplete = false
                            passwordWrap!!.isErrorEnabled = true
                            passwordWrap!!.error = "Invalid OTP"
                        }.check()
            }

            if(isComplete && type == Constants.LoginInInfo.Type.EMAIL){
                viewModel!!.email = emailPhone!!.text.toString()
                viewModel!!.setPassword(password!!.text.toString())
                callback.Login()
            } else if(isComplete && type == Constants.LoginInInfo.Type.PHONE){
                viewModel!!.phone = emailPhone!!.text.toString()
                viewModel!!.setOTP(password!!.text.toString())
                callback!!.phoneSignUp()
            } else {
                callback.showSnackbar("Invalid Information!")
            }
        }

        google!!.setOnClickListener {
            val id = getString(R.string.default_web_client_id)
            viewModel!!.initiateGoogleLogin(id, context)
            callback.GoogleSignUp()
        }

        facebook!!.setOnClickListener { facebookLogin!!.performClick() }


    }

//    fun setOTP(otp: String) {
//        password!!.setText(otp)
//        login!!.performClick()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel!!.onActivityResult(requestCode, resultCode, data)
    }
}
