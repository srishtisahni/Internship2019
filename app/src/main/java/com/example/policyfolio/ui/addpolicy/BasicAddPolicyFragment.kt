package com.example.policyfolio.ui.addpolicy


import android.os.Bundle

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible

import com.example.policyfolio.util.Constants
import com.example.policyfolio.data.local.classes.InsuranceProvider
import com.example.policyfolio.R
import com.example.policyfolio.ui.adapters.ListAdapters.BasicDropdownProviderAdapter
import com.example.policyfolio.ui.adapters.ListAdapters.BasicDropdownTextAdapter
import com.example.policyfolio.viewmodels.AddPolicyViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class BasicAddPolicyFragment(private val callback: AddPolicyCallback) : Fragment(), BasicDropdownTextAdapter.ParentCallback, BasicDropdownProviderAdapter.ParentCallback {

    private var rootView: View? = null
    private var viewModel: AddPolicyViewModel? = null

    private var typeValue: TextView? = null
    private var typeChoice: RecyclerView? = null
    private var typeAdapter: BasicDropdownTextAdapter? = null
    private var insurances: Array<String>? = null

    private var providerChoice: RecyclerView? = null
    private var providerText: TextView? = null
    private var providerFrame: FrameLayout? = null
    private var providerAdapter: BasicDropdownProviderAdapter? = null
    private var providers: ArrayList<InsuranceProvider>? = null

    private var policyNumber: EditText? = null
    private var numberEmpty: TextView? = null

    private var next: Button? = null

    private var divider: LinearLayout? = null
    private var buy: ConstraintLayout? = null
    private var sellerList: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_basic_add_policy, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(AddPolicyViewModel::class.java)
        viewModel!!.initiateRepo(context)

        typeValue = rootView!!.findViewById(R.id.policy_type)
        typeChoice = rootView!!.findViewById(R.id.policy_choice)

        providerFrame = rootView!!.findViewById(R.id.frame_insurance)
        providerText = rootView!!.findViewById(R.id.text_insurance)
        providerChoice = rootView!!.findViewById(R.id.insurance_provider)

        policyNumber = rootView!!.findViewById(R.id.policy_number)
        numberEmpty = rootView!!.findViewById(R.id.number_empty)

        next = rootView!!.findViewById(R.id.next)

        divider = rootView!!.findViewById(R.id.divider)

        buy = rootView!!.findViewById(R.id.buy_new)
        sellerList = rootView!!.findViewById(R.id.policy_sellers)

        setUpViews()
        setDefaultType()

        return rootView
    }

    private fun setDefaultType() {
        if (arguments != null) {
            val type = arguments!!.getInt(Constants.InsuranceProviders.TYPE, -1)
            if (type != -1)
                setValue(type, Constants.ListTypes.INSURANCE_TYPE)
        }
    }

    private fun setUpViews() {
        insurances = resources.getStringArray(R.array.insurance_type)
        typeAdapter = BasicDropdownTextAdapter(context, insurances, this, Constants.ListTypes.INSURANCE_TYPE, resources.getColor(R.color.Grey))
        typeChoice!!.adapter = typeAdapter
        typeChoice!!.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        typeValue!!.setOnClickListener {
            typeValue!!.visibility = View.GONE
            typeChoice!!.visibility = View.VISIBLE

            providerFrame!!.visibility = View.GONE
            divider!!.visibility = View.GONE
            buy!!.visibility = View.GONE

            policyNumber!!.visibility = View.GONE
            next!!.visibility = View.GONE
        }

        providers = ArrayList()
        providerAdapter = BasicDropdownProviderAdapter(context, providers, this)
        providerChoice!!.adapter = providerAdapter
        providerChoice!!.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        providerFrame!!.setOnClickListener {
            providerText!!.visibility = View.GONE
            providerChoice!!.visibility = View.VISIBLE
            providerFrame!!.setBackgroundColor(resources.getColor(android.R.color.transparent))

            divider!!.visibility = View.GONE
            buy!!.visibility = View.GONE

            policyNumber!!.visibility = View.GONE
            next!!.visibility = View.GONE
        }

        next!!.setOnClickListener {
            numberEmpty!!.visibility = View.GONE
            policyNumber!!.validator()
                    .minLength(10)
                    .addErrorCallback{message ->
                        numberEmpty!!.text = message
                        numberEmpty!!.visibility = View.VISIBLE
                    }
                    .addSuccessCallback {
                        numberEmpty!!.visibility = View.GONE
                    }.check()

            if(numberEmpty!!.isGone) {
                viewModel!!.policyNumber = policyNumber!!.text.toString().toUpperCase()
                callback.next()
            } else {
                callback.showSnackbar("Invalid Policy Number")
            }
        }
    }

    override fun setValue(position: Int, type: Int) {
        when (type) {
            Constants.ListTypes.INSURANCE_TYPE -> {
                typeValue!!.text = insurances!![position]
                typeValue!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                typeValue!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
                typeChoice!!.visibility = View.GONE
                typeValue!!.visibility = View.VISIBLE

                providerFrame!!.visibility = View.VISIBLE
                divider!!.visibility = View.VISIBLE
                buy!!.visibility = View.VISIBLE

                providerText!!.text = "Insurance Provider*"
                providerText!!.visibility = View.VISIBLE
                providerChoice!!.visibility = View.GONE
                providerText!!.setTextColor(resources.getColor(R.color.Grey))
                providerFrame!!.background = resources.getDrawable(R.drawable.dropdown_item_8dp)

                policyNumber!!.visibility = View.GONE
                next!!.visibility = View.GONE
                policyNumber!!.setText("")

                viewModel!!.setType(position).observe(this, Observer { result ->
                    providers!!.clear()
                    providers!!.addAll(result)
                    providerAdapter!!.notifyDataSetChanged()
                })
            }

            Constants.ListTypes.INSURANCE_PROVIDER -> {
                providerText!!.text = providers!![position].name
                providerText!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                providerChoice!!.visibility = View.GONE
                providerText!!.visibility = View.VISIBLE

                policyNumber!!.visibility = View.VISIBLE
                next!!.visibility = View.VISIBLE

                viewModel!!.provider = providers!![position]
            }
        }
    }
}
