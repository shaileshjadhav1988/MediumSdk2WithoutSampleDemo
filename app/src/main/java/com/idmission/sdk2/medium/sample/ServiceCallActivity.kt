package com.idmission.sdk2.medium.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.idmission.sdk2.capture.IdMissionCaptureLauncher
import com.idmission.sdk2.capture.presentation.camera.helpers.CaptureBack
import com.idmission.sdk2.capture.presentation.camera.helpers.ProcessedCapture
import com.idmission.sdk2.client.model.CountryMaster
import com.idmission.sdk2.client.model.IdTypeMaster
import com.idmission.sdk2.client.model.StateMasterVO
import com.idmission.sdk2.identityproofing.IdentityProofingSDK
import com.idmission.sdk2.medium.sample.databinding.ActivityServiceCallBinding

class ServiceCallActivity : AppCompatActivity() {
    private lateinit var binding : ActivityServiceCallBinding
    private var processedCaptures: List<ProcessedCapture>? = null
    private var idTypeAdapter: ArrayAdapter<IdTypeMaster>? = null
    private var countryAdapter: ArrayAdapter<CountryMaster>? = null
    private var alertDialog: AlertDialog? = null
    private var idMaster: IdTypeMaster? = null
    private var countryMaster: CountryMaster? = null
    private var stateMasterVO: StateMasterVO? = null
    var idTypes: List<IdTypeMaster> = emptyList()
    var countries: List<CountryMaster> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupArrayAdapters()
        setListener()
    }

    private fun setListener() {
        // Service ID 50 with
        binding.serviceId50.setOnClickListener {
            showOptionsAlertDialog()
        }
//
    }

    /**
     * fetch list and create ArrayAdapters.
     *
     */
    private fun setupArrayAdapters() {
        /* IdType Adapter */
        idTypes = IdentityProofingSDK.getSupportedIdTypeList(this)
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            idTypes
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            idTypeAdapter = adapter
        }

        /* Countries Adapter */
        countries = IdentityProofingSDK.getSupportedIdCountriesList()
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            countries
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            countryAdapter = adapter
        }
    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.indeterminateBar).visibility= View.VISIBLE
        findViewById<Button>(R.id.service_id_50).visibility = View.GONE
       /* findViewById<Button>(R.id.final_submit).visibility = View.GONE
        findViewById<Button>(R.id.processed_captures).visibility = View.GONE
    */}

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.indeterminateBar).visibility= View.GONE
        findViewById<Button>(R.id.service_id_50).visibility = View.VISIBLE
     /*   findViewById<Button>(R.id.final_submit).visibility = View.VISIBLE
        if (processedCaptures!=null){ findViewById<Button>(R.id.processed_captures).visibility = View.VISIBLE }
     */  }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        if (requestCode != IdMissionCaptureLauncher.CAPTURE_REQUEST_CODE) return
        processedCaptures = if (!data.extras?.getParcelableArray(
                IdMissionCaptureLauncher
                    .EXTRA_PROCESSED_CAPTURES
            )
                .isNullOrEmpty()
        ) {
            data.extras?.getParcelableArray(IdMissionCaptureLauncher.EXTRA_PROCESSED_CAPTURES)
                ?.toList() as List<ProcessedCapture>?
        } else {
            emptyList()
        }
        if (processedCaptures!=null){
            PhotoResultsActivity.launch(this, processedCaptures!!)
          //  findViewById<Button>(R.id.processed_captures).visibility = View.VISIBLE
           // findViewById<Button>(R.id.final_submit).visibility = View.VISIBLE
        }
    }

    /**
     * Create and Show Alert Dialog for the options.
     */
    private fun showOptionsAlertDialog() {
        if(alertDialog != null ) {
            alertDialog!!.show()
        } else {
            val optionsView = layoutInflater.inflate(
                R.layout.layout_options,
                null,
                false
            )

            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setMessage("Select option to continue.")
            alertBuilder.setView(optionsView)
            alertDialog = alertBuilder.create()
            val rbWithoutBackCapture = optionsView.findViewById<RadioButton>(R.id.rb_without_back)
            val rgSelection = optionsView.findViewById<RadioGroup>(R.id.rg_selection)
            val rbBackCapture = optionsView.findViewById<RadioButton>(R.id.rb_with_back)
            val rbDocument = optionsView.findViewById<RadioButton>(R.id.rb_document)
            val idTypeSpinner = optionsView.findViewById<Spinner>(R.id.spinner_id_type)
            val llDocumentSelection = optionsView.findViewById<LinearLayout>(R.id.ll_document_info)
            idTypeSpinner.adapter = idTypeAdapter
            idTypeSpinner.setSelection(3)
            val countrySpinner = optionsView.findViewById<Spinner>(R.id.spinner_country)
            countrySpinner.adapter = countryAdapter
            val stateSpinner = optionsView.findViewById<Spinner>(R.id.spinner_state)

            rgSelection.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_without_back -> {
                        change(llDocumentSelection, idTypeSpinner, countrySpinner, stateSpinner)
                    }
                    R.id.rb_with_back -> {
                        change(llDocumentSelection, idTypeSpinner, countrySpinner, stateSpinner)
                    }
                    R.id.rb_document -> {
                        change(
                            llDocumentSelection,
                            idTypeSpinner,
                            countrySpinner,
                            stateSpinner,
                            true
                        )
                    }
                }
            }
            countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCountry = countries[position]
                    val selectedCountryCode = selectedCountry.countryCode
                    ArrayAdapter(
                        parent!!.context,
                        android.R.layout.simple_spinner_item,
                        IdentityProofingSDK.getSupportedIdStatesList(selectedCountry)
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        stateSpinner.adapter = adapter
                    }
                    if (selectedCountryCode.isNotEmpty()) {
                        stateSpinner.setSelection(4)
                    }
                }
            }
            optionsView.findViewById<Button>(R.id.bt_continue)
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        if (!rbWithoutBackCapture.isChecked && !rbDocument.isChecked && !rbBackCapture.isChecked) {
                            showToastMessage("Please select one of the options, to continue.")
                            return
                        }
                        getSelectedItem(idTypeSpinner, countrySpinner, stateSpinner)

                        if (rbWithoutBackCapture.isChecked) {
                            startService(isBackCaptureRequired = CaptureBack.NO)
                            alertDialog!!.dismiss()
                        } else if (rbBackCapture.isChecked) {
                            startService(isBackCaptureRequired = CaptureBack.YES)
                            alertDialog!!.dismiss()
                        } else if (rbDocument.isChecked) {
                            startService(isDocumentCapture = true)
                            alertDialog!!.dismiss()
                        }
                    }

                })
            change(llDocumentSelection, idTypeSpinner, countrySpinner, stateSpinner)
            countrySpinner.setSelection(237)
            alertDialog!!.show()
        }
    }

    /**
     * method to show Toast message.
     */
    private fun showToastMessage(message: String) {
        Toast.makeText(
            this@ServiceCallActivity,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * method to enable/disable spinners on the basis of boolean when Document option is selected.
     */
    private fun change(
        llDocumentInfo: View, spinnerIdType: View,
        spinnerCountry: View, spinnerState: View,
        isEnable: Boolean = false
    ) {
        llDocumentInfo.alpha = if (isEnable) 1.0f else 0.5f
        spinnerCountry.isEnabled = isEnable
        spinnerState.isEnabled = isEnable
        spinnerIdType.isEnabled = isEnable
    }

    /**
     * function to invoke SDK corresponds to selected Service.
     */
    private fun startService(
        isBackCaptureRequired: CaptureBack? = null,
        isDocumentCapture: Boolean? = null) {
        if (isBackCaptureRequired != null) {
            IdentityProofingSDK.idValidationAndcustomerEnroll(
                this,
                uniqueNumber = "12345678",
                isBackCaptureRequired,
                null
            )
        } else {
            IdentityProofingSDK.idValidationAndcustomerEnroll(
                this,
                uniqueNumber = "12345678",
                idType = idMaster!!,
                idCountry = countryMaster!!,
                idState = stateMasterVO,
                null
            )
        }
    }

    /**
     * get selected item from the IdType, Country and State spinner.
     */
    private fun getSelectedItem(
        idTypeSpinner: Spinner,
        countrySpinner: Spinner,
        stateSpinner: Spinner
    ) {
        idMaster = idTypeSpinner.selectedItem as IdTypeMaster
        countryMaster = countrySpinner.selectedItem as CountryMaster
        stateMasterVO =
            if (stateSpinner.selectedItem != null) stateSpinner.selectedItem as StateMasterVO else null
    }

}