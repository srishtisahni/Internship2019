package com.example.policyfolio.ui.addpolicy


import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible

import com.example.policyfolio.util.Constants
import com.example.policyfolio.data.local.classes.Nominee
import com.example.policyfolio.R
import com.example.policyfolio.ui.adapters.ListAdapters.BasicDropdownNomineeAdapter
import com.example.policyfolio.ui.adapters.ListAdapters.BasicDropdownTextAdapter
import com.example.policyfolio.viewmodels.AddPolicyViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

import java.io.ByteArrayOutputStream
import java.util.ArrayList
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 */
class AddPolicyDetailsFragment(private val callback: AddPolicyCallback) : Fragment(), BasicDropdownTextAdapter.ParentCallback, BasicDropdownNomineeAdapter.ParentCallback {

    private var rootView: View? = null
    private var viewModel: AddPolicyViewModel? = null

    private var insuranceProvider: TextView? = null
    private var policyNumber: TextView? = null
    private var coverAmount: EditText? = null
    private var coverError: TextView? = null

    private var premiumFrame: FrameLayout? = null
    private var premiumText: TextView? = null
    private var premiumAdapter: BasicDropdownTextAdapter? = null
    private var premiums: Array<String>? = null
    private var frequencyError: TextView? = null

    private var premiumAmount: EditText? = null
    private var premiumError: TextView? = null

    private var dueDatePickerLayout: LinearLayout? = null
    private var dueDate: TextView? = null
    private var dueDateImage: ImageView? = null
    private var premiumDateEpoch: Long? = null
    private var dueError: TextView? = null

    private var matureDatePickerLayout: LinearLayout? = null
    private var matureDate: TextView? = null
    private var matureDateImage: ImageView? = null
    private var matureDateEpoch: Long? = null
    private var matureError: TextView? = null

    private var nomineeFrame: FrameLayout? = null
    private var nomineeText: TextView? = null
    private var nominees: ArrayList<Nominee>? = null
    private var nomineeAdapter: BasicDropdownNomineeAdapter? = null
    private var nomineeError: TextView? = null

    private var addPolicy: LinearLayout? = null
    private var documentAddText: TextView? = null
    private var documentAddImage: ImageView? = null
    private var optional1: TextView? = null

    private var clickPolicy: LinearLayout? = null
    private var optional2: TextView? = null

    private var done: Button? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_add_policy_details, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(AddPolicyViewModel::class.java)
        viewModel!!.initiateRepo(context)

        insuranceProvider = rootView!!.findViewById(R.id.insurance_provider)
        policyNumber = rootView!!.findViewById(R.id.policy_number)
        coverAmount = rootView!!.findViewById(R.id.cover_amount)
        coverError = rootView!!.findViewById(R.id.cover_empty)

        premiumFrame = rootView!!.findViewById(R.id.frequency_frame)
        premiumText = rootView!!.findViewById(R.id.frequency_text)
        premiums = resources.getStringArray(R.array.premium_frequency)
        frequencyError = rootView!!.findViewById(R.id.frequency_empty)

        premiumAmount = rootView!!.findViewById(R.id.premium_amount)
        premiumError = rootView!!.findViewById(R.id.premium_empty)

        dueDatePickerLayout = rootView!!.findViewById(R.id.date_picker)
        dueDate = rootView!!.findViewById(R.id.date)
        dueDateImage = rootView!!.findViewById(R.id.date_image)
        dueError = rootView!!.findViewById(R.id.due_empty)

        matureDatePickerLayout = rootView!!.findViewById(R.id.date_picker2)
        matureDate = rootView!!.findViewById(R.id.date2)
        matureDateImage = rootView!!.findViewById(R.id.date_image2)
        matureError = rootView!!.findViewById(R.id.mature_empty)

        nomineeFrame = rootView!!.findViewById(R.id.nominee_frame)
        nomineeText = rootView!!.findViewById(R.id.nominee_text)
        nominees = ArrayList()
        nomineeError = rootView!!.findViewById(R.id.nominee_empty)

        addPolicy = rootView!!.findViewById(R.id.add_document)
        documentAddText = rootView!!.findViewById(R.id.add_document_text)
        documentAddImage = rootView!!.findViewById(R.id.document_add_image)
        optional1 = rootView!!.findViewById(R.id.optional1)

        clickPolicy = rootView!!.findViewById(R.id.click_document)
        optional2 = rootView!!.findViewById(R.id.optional2)

        done = rootView!!.findViewById(R.id.done)

        setAdapters()
        setDefaults()

        return rootView
    }

    private fun setAdapters() {
        coverAmount!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    coverAmount!!.setPadding(0, 4, 0, 4)
                    coverAmount!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
                } else {
                    coverAmount!!.setPadding(24, 4, 32, 4)
                    coverAmount!!.background = resources.getDrawable(R.drawable.text_background_grey_8dp)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        premiumAdapter = BasicDropdownTextAdapter(context, premiums, this, Constants.ListTypes.PREMIUM_FREQUENCY, resources.getColor(R.color.colorAccent))
        viewModel!!.premiumFrequency = -1
        premiumFrame!!.setOnClickListener {
            premiumFrame!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
            premiumFrame!!.setPadding(0, 4, 0, 4)
            callback.openListSheet(Constants.ListTypes.PREMIUM_FREQUENCY, premiumAdapter)
        }

        premiumAmount!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    premiumAmount!!.setPadding(0, 4, 0, 4)
                    premiumAmount!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
                } else {
                    premiumAmount!!.setPadding(24, 4, 32, 4)
                    premiumAmount!!.background = resources.getDrawable(R.drawable.text_background_grey_8dp)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        dueDatePickerLayout!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = premiumDateEpoch!! * 1000

            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                premiumDateEpoch = calendar.timeInMillis / 1000
                viewModel!!.premiumDateEpoch = premiumDateEpoch
                dueDate!!.text = Constants.Time.DATE_FORMAT.format(premiumDateEpoch!! * 1000)
                dueDate!!.setPadding(0, 4, 0, 4)
                dueDate!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                dueDateImage!!.visibility = View.GONE
                dueDatePickerLayout!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
            }

            DatePickerDialog(context,R.style.MyDatePickerDialogTheme, dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        matureDatePickerLayout!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = premiumDateEpoch!! * 1000

            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                matureDateEpoch = calendar.timeInMillis / 1000
                viewModel!!.matureDateEpoch = matureDateEpoch
                matureDate!!.text = Constants.Time.DATE_FORMAT.format(matureDateEpoch!! * 1000)
                matureDate!!.setPadding(0, 4, 0, 4)
                matureDate!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                matureDateImage!!.visibility = View.GONE
                matureDatePickerLayout!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
            }

            DatePickerDialog(context,R.style.MyDatePickerDialogTheme, dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        nomineeAdapter = BasicDropdownNomineeAdapter(context, nominees, this)

        nomineeFrame!!.setOnClickListener {
            if (nominees!!.size != 0) {
                nomineeFrame!!.setBackgroundColor(resources.getColor(android.R.color.transparent))
                callback.openListSheet(Constants.ListTypes.NOMINEE, nomineeAdapter)
            } else
                callback.showSnackbar("No Nominees exist for the User")
        }

        addPolicy!!.setOnClickListener {
            callback.addPolicyImage()
        }

        clickPolicy!!.setOnClickListener {
            callback.clickPolicyImage()
        }

        done!!.setOnClickListener {
            var isComplete:Boolean = true
            coverAmount!!.validator()
                    .nonEmpty()
                    .addErrorCallback { message ->
                        coverError!!.text = message
                        coverError!!.setTextColor(resources!!.getColor(R.color.red))
                        isComplete = false
                    }
                    .addSuccessCallback {
                        coverError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
                    }.check()

            if(viewModel!!.premiumFrequency == -1){
                frequencyError!!.setTextColor(resources!!.getColor(R.color.red))
                isComplete = false
            } else{
                frequencyError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
            }

            premiumAmount!!.validator()
                    .nonEmpty()
                    .addErrorCallback { message ->
                        premiumError!!.text = message
                        premiumError!!.setTextColor(resources!!.getColor(R.color.red))
                        isComplete = false
                    }
                    .addSuccessCallback {
                        premiumError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
                    }.check()

            if(dueDateImage!!.isGone){
                dueError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
            } else{
                dueError!!.setTextColor(resources!!.getColor(R.color.red))
                isComplete = false
            }

            if(matureDateImage!!.isGone){
                matureError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
            } else{
                matureError!!.setTextColor(resources!!.getColor(R.color.red))
                isComplete = false
            }

            if(viewModel!!.nominee == null){
                nomineeError!!.setTextColor(resources!!.getColor(R.color.red))
                isComplete = false
            } else{
                nomineeError!!.setTextColor(resources!!.getColor(android.R.color.transparent))
            }

            if(isComplete){
                viewModel!!.coverAmount = coverAmount!!.text.toString()
                viewModel!!.premiumAmount = premiumAmount!!.text.toString()
                callback.done()
            } else {
                callback.showSnackbar("Incomplete Information!")
            }
        }
    }

    private fun setDefaults() {
        insuranceProvider!!.text = viewModel!!.provider.name
        policyNumber!!.text = viewModel!!.policyNumber
        viewModel!!.fetchNominees().observe(this, Observer { results ->
            nominees!!.clear()
            nominees!!.addAll(results)
            nomineeAdapter!!.notifyDataSetChanged()
        })
        premiumDateEpoch = System.currentTimeMillis() / 1000
        matureDateEpoch = System.currentTimeMillis() / 1000
    }

    override fun setValue(position: Int, type: Int) {
        coverAmount!!.clearFocus()
        premiumAmount!!.clearFocus()
        callback.closeListSheet()
        when (type) {
            Constants.ListTypes.PREMIUM_FREQUENCY -> {
                premiumText!!.text = premiums!![position]
                premiumText!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                premiumText!!.setPadding(0, 4, 0, 4)
                viewModel!!.premiumFrequency = position
            }
            Constants.ListTypes.NOMINEE -> {
                val relations = resources.getStringArray(R.array.relationship_array)
                nomineeText!!.text = nominees!![position].name + ", " + relations[nominees!![position].relation]
                Log.e("RELATION",relations[nominees!![position].relation])
                nomineeText!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                nomineeText!!.setPadding(0, 4, 0, 4)
                viewModel!!.nominee = nominees!![position]
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PermissionAndRequests.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = activity!!.contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            val bmp = BitmapFactory.decodeFile(picturePath)
            documentAddImage!!.setImageBitmap(bmp)
            documentAddImage!!.setPadding(0,0,0,0)
            documentAddText!!.visibility = View.GONE
            clickPolicy!!.visibility = View.GONE
            optional2!!.visibility = View.GONE
            optional1!!.text = "Policy Document"
            viewModel!!.bitmap = reducedImage(bmp)
        }
        if (requestCode == Constants.PermissionAndRequests.CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val bmp = data.extras!!.get("data") as Bitmap
            documentAddImage!!.setImageBitmap(bmp)
            documentAddImage!!.setPadding(0,0,0,0)
            documentAddText!!.visibility = View.GONE
            clickPolicy!!.visibility = View.GONE
            optional2!!.visibility = View.GONE
            optional1!!.text = "Policy Document"
            viewModel!!.bitmap = reducedImage(bmp)
        }
    }

    private fun reducedImage(bmp: Bitmap): Bitmap {
        var bmp = bmp
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val currSize = stream.toByteArray().size
        if (currSize > Constants.Documents.ONE_MEGABYTE) {
            val width = (bmp.width * Constants.Documents.ONE_MEGABYTE / currSize).toInt()
            val height = (bmp.height * Constants.Documents.ONE_MEGABYTE / currSize).toInt()
            bmp = Bitmap.createScaledBitmap(bmp, width, height, true)
        }
        return bmp
    }
}
