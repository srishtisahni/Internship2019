package com.example.policyfolio.ui.login


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.policyfolio.R
import com.example.policyfolio.util.Constants
import com.example.policyfolio.viewmodels.LoginSignUpViewModel
import com.facebook.login.widget.LoginButton
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment @SuppressLint("ValidFragment") constructor(private val callback: LoginCallback) : Fragment() {

    private var rootView: View? = null
    private var viewModel: LoginSignUpViewModel? = null

    private var emailText: EditText? = null
    private var emailError: TextView? = null
    private var password: EditText? = null
    private var passwordError: TextView? = null
    private var forgetPassword: TextView? = null
    private var login: Button? = null

    private var google: CircleImageView? = null
    private var facebook: CircleImageView? = null
    private var phone: CircleImageView? = null
    private var email: CircleImageView? = null
    private var facebookLogin: LoginButton? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_login, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(LoginSignUpViewModel::class.java)
        viewModel!!.initiateRepo(context)

        emailText = rootView!!.findViewById(R.id.email)
        password = rootView!!.findViewById(R.id.password)
        login = rootView!!.findViewById(R.id.login)
        google = rootView!!.findViewById(R.id.google_signUp)
        facebook = rootView!!.findViewById(R.id.facebook_signUp)
        phone = rootView!!.findViewById(R.id.phone_signUp)
        email = rootView!!.findViewById(R.id.email_signUp)

        emailError = rootView!!.findViewById(R.id.email_error)
        passwordError = rootView!!.findViewById(R.id.password_error)
        forgetPassword = rootView!!.findViewById(R.id.forgot_password)

        forgetPassword!!.setOnClickListener { callback.forgotPassword() }

        if (viewModel!!.email != null) {
            emailText!!.setText(viewModel!!.email)
        }

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
        login!!.setOnClickListener {
            emailText!!.validator()
                    .validEmail()
                    .addErrorCallback { message ->
                        emailError!!.text = message
                        emailError!!.visibility = View.VISIBLE
                    }
                    .addSuccessCallback {
                        emailError!!.visibility = View.GONE
                    }.check()
            Log.e("EMAIL ERROR",emailError!!.isVisible.toString())

            password!!.validator()
                    .minLength(8)
                    .addErrorCallback { message ->
                        passwordError!!.text = message
                        passwordError!!.visibility = View.VISIBLE
                    }
                    .addSuccessCallback {
                        passwordError!!.visibility = View.GONE
                    }.check()
            Log.e("PASSWORD ERROR",passwordError!!.isVisible.toString())

            if( passwordError!!.isGone && emailError!!.isGone){
                viewModel!!.email = emailText!!.text.toString()
                viewModel!!.setPassword(password!!.text.toString())
                callback.Login()
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

        phone!!.setOnClickListener { callback.enterPhone() }

        email!!.setOnClickListener { callback.enterEmail() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel!!.onActivityResult(requestCode, resultCode, data)
    }
}
