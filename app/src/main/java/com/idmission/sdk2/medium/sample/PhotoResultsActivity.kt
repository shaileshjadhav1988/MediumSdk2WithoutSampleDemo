package com.idmission.sdk2.medium.sample


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.idmission.sdk2.R
import com.idmission.sdk2.capture.IdMissionCaptureLauncher
import com.idmission.sdk2.capture.presentation.camera.helpers.ProcessedCapture
import com.idmission.sdk2.client.model.CommonApiResponse
import com.idmission.sdk2.client.model.HostDataResponse
import com.idmission.sdk2.client.model.Response
import com.idmission.sdk2.identityproofing.IdentityProofingSDK
import kotlinx.android.synthetic.main.activity_photo_results.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

internal class PhotoResultsActivity : Activity() {

    companion object {

        fun launch(activity: Activity, processedCaptures: List<ProcessedCapture>) {
            val intent = Intent(activity, PhotoResultsActivity::class.java).apply {
                putExtra(
                    IdMissionCaptureLauncher.EXTRA_PROCESSED_CAPTURES,
                    processedCaptures.toTypedArray()
                )
            }

            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_results)

        bt_done.setOnClickListener {
            finish()
        }
        photoResultToolbar.apply {
            setTitleTextColor(ContextCompat.getColor(this@PhotoResultsActivity, R.color.white))
            setNavigationIcon(com.idmission.sdk2.capture.R.drawable.arrow_back)
            setNavigationOnClickListener { finish() }
        }

        val processedCaptures =
            intent.extras?.getParcelableArray(IdMissionCaptureLauncher.EXTRA_PROCESSED_CAPTURES)
                ?.mapNotNull {
                    it as? ProcessedCapture
                }
        showProcessedCaptures(processedCaptures)
        callFinalSubmit()
    }

    private fun showProcessedCaptures(processedCaptures: List<ProcessedCapture>?) {
        processedCaptures?.forEach {
            when(it){
                is ProcessedCapture.DocumentDetectionResult.RealDocument -> showDocumentImages(processedCaptures)
                is ProcessedCapture.DocumentDetectionResult.SpoofDocument -> showDocumentImages(processedCaptures)
                is ProcessedCapture.LiveFaceDetectionResult.RealFace -> showFaceImages(processedCaptures)
                is ProcessedCapture.LiveFaceDetectionResult.SpoofFace -> showFaceImages(processedCaptures)
            }
        }
    }

    private fun showFaceImages(processedCaptures: List<ProcessedCapture>) {
        val detection = processedCaptures[0] as ProcessedCapture.LiveFaceDetectionResult.RealFace
        Glide.with(photoResultImageView).load(detection.file).into(photoResultImageView)
        realSpoofTextView.text = "${realSpoofTextView.context.getString(R.string.real)}\t${detection.livenessScore}"

    }


    private fun showDocumentImages(processedCaptures: List<ProcessedCapture>) {
        val frontDetection = processedCaptures[1] as ProcessedCapture.DocumentDetectionResult
        .RealDocument
        Glide.with(docFrontImageView).load(frontDetection.file).into(docFrontImageView)
        val backDetection = processedCaptures[2] as ProcessedCapture.DocumentDetectionResult
        .RealDocument
        Glide.with(docBackImageView).load(backDetection.file).into(docBackImageView)

        var extractedDataMap: Map<String, String>? = null

        processedCaptures.forEach{
            if (it is ProcessedCapture.DocumentDetectionResult.RealDocument) {
                extractedDataMap = if (!it.barcodeMap.isNullOrEmpty()) {
                    it.barcodeMap!!
                } else if (!it.mrzMap.isNullOrEmpty()) {
                    it.mrzMap!!
                } else {
                    mapOf()
                }
                if(it.faceMatch != null) {
                    faceMatchTextView.visibility= View.VISIBLE
                    val matchText = if (it.faceMatch!!.withinTolerance) "FACE MATCHES" else "FACE DOES NOT MATCH"
                    faceMatchTextView.text = matchText

                }
            } else {
                extractedDataMap = mapOf()
            }
        }
        val detectedData = getExtractDataFromMap(extractedDataMap!!)

        if (!detectedData?.FirstName.isNullOrEmpty()){
            tv_processed_full_name.setText(detectedData?.FirstName +" "+ (detectedData?.MiddleName +" "+detectedData?.LastName))
        }else{
            ll_processed_full_name.visibility=View.GONE
        }
        if (!detectedData?.DateofBirth.isNullOrEmpty()){
            tv_processed_dob.setText(detectedData?.DateofBirth)
        }else{
            ll_processed_dob.visibility=View.GONE
        }
        if (!detectedData?.AddressLine1.isNullOrEmpty()){
            tv_processed_address.setText(detectedData?.AddressLine1)
        }else{
            ll_processed_address.visibility=View.GONE
        }
        if (!detectedData?.IDNumber.isNullOrEmpty()){
            tv_processed_document.setText(detectedData?.IDNumber)
        }else{
            ll_processed_document.visibility=View.GONE
        }
        if (!detectedData?.ExpiryDate.isNullOrEmpty()){
            tv_processed_expiry.setText(detectedData?.ExpiryDate)
        }else{
            ll_processed_expiry.visibility=View.GONE
        }
        if (!detectedData?.IssueDate.isNullOrEmpty()){
            tv_processed_issue_date.setText(detectedData?.IssueDate)
        }else{
            ll_processed_issue_date.visibility=View.GONE
        }
        if (!detectedData?.Gender.isNullOrEmpty()){
            tv_processed_gender.setText(detectedData?.Gender)
        }else{
            ll_processed_gender.visibility=View.GONE
        }
    }

    private fun callFinalSubmit() {
        showProgress()
        var response: Response<CommonApiResponse>? = null
        var resultStatus = false
        var errormessage = ""
        var responseString = ""
        val format = Json { prettyPrint = true }
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                response = IdentityProofingSDK.finalSubmit(
                    applicationContext
                )
            }
            if(response?.errorStatus ==null){
                resultStatus = true
                responseString = format.encodeToString(response?.result)
            }
            else{
                responseString = format.encodeToString(response?.errorStatus)
                errormessage = response?.errorStatus?.statusMessage.toString()
            }
        }.invokeOnCompletion {
            hideProgress()
            setValues(response!!)
            val response = responseString
            val responseLines = response!!.split("\n").map {
                if (it.length <= 255) it.subSequence(
                    0,
                    it.length
                ).toString() else it.subSequence(0, 255).toString() + "..."
            }


            if (resultStatus){
                Toast.makeText(this, "Submitted successfuly", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, errormessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setValues(response: Response<CommonApiResponse>) {
        if (response.result?.responseCustomerData !=null){
            ll_id_data.visibility=View.VISIBLE
            ll_enrolled_data.visibility = View.VISIBLE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.firstName.isNullOrEmpty()){
            tv_full_name.setText(response.result?.responseCustomerData?.extractedPersonalData?.firstName+" "+ (response.result?.responseCustomerData?.extractedPersonalData?.middleName +" "+response.result?.responseCustomerData?.extractedPersonalData?.lastName))
        }else{
            ll_full_name.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.dob.isNullOrEmpty()){
            tv_dob.setText(response.result?.responseCustomerData?.extractedPersonalData?.dob)
        }else{
            ll_dob.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.addressLine1.isNullOrEmpty()){
            tv_address.setText(response.result?.responseCustomerData?.extractedPersonalData?.addressLine1+" "+response.result?.responseCustomerData?.extractedPersonalData?.addressLine2)
        }else{
            ll_address.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedIdData?.idNumber.isNullOrEmpty()){
            tv_document.setText(response.result?.responseCustomerData?.extractedIdData?.idNumber)
        }else{
            ll_document.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedIdData?.idExpirationDate.isNullOrEmpty()){
            tv_expiry.setText(response.result?.responseCustomerData?.extractedIdData?.idExpirationDate)
        }else{
            ll_expiry.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedIdData?.idIssueDate.isNullOrEmpty()){
            tv_issue_date.setText(response.result?.responseCustomerData?.extractedIdData?.idIssueDate)
        }else{
            ll_issue_date.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.gender.isNullOrEmpty()){
            tv_gender.setText(response.result?.responseCustomerData?.extractedPersonalData?.gender)
        }else{
            ll_gender.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.enrolledDate.isNullOrEmpty()){
            tv_enrolled_date.setText(response.result?.responseCustomerData?.extractedPersonalData?.enrolledDate)

        }else{
            ll_enrolled_date.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.firstName.isNullOrEmpty()){
            tv_enrolled_full_name.setText(response.result?.responseCustomerData?.extractedPersonalData?.firstName+" "+ (response.result?.responseCustomerData?.extractedPersonalData?.middleName +" "+response.result?.responseCustomerData?.extractedPersonalData?.lastName))

        }else{
            ll_enrolled_full_name.visibility=View.GONE
        }
        ll_client_customer_number.visibility = View.GONE

        if (!response.result?.responseCustomerData?.extractedPersonalData?.enrolledFaceImage.isNullOrEmpty()){
            rl_live_result.visibility = View.VISIBLE
            Glide.with(live_image).load(response.result?.responseCustomerData?.extractedPersonalData?.enrolledFaceImage).into(live_image)
        }else{
            live_image.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedIdData?.idProcessImageFront.isNullOrEmpty()){
            ll_doc_image.visibility = View.VISIBLE
            Glide.with(iv_doc_image).load(response.result?.responseCustomerData?.extractedIdData?.idProcessImageFront).into(iv_doc_image)
        }else{
            iv_doc_image.visibility = View.GONE
        }
        if(response.result?.responseCustomerData?.hostData !=null){
            kyc_dmv_result.visibility = View.VISIBLE
            val dmvResult = getDmvResult(response.result?.responseCustomerData?.hostData!!)
            val kycResult = getAmlKycResult(response.result?.responseCustomerData?.hostData!!)
            iv_dmv_result.setColorFilter(resources.getColor(
                dmvResult.second))
            tv_dmv_result.text = kycResult.first
            iv_kyc_result.setColorFilter(resources.getColor(
                kycResult.second))
            tv_kyc_result.text = kycResult.first

            if(response.result?.responseCustomerData?.hostData!!.pepresult!=null){
                val pepResult = getResult(response.result?.responseCustomerData?.hostData!!.pepresult?.resultCountPEP)
                setHostResult(pepResult,iv_pep_status)
            }
            if(response.result?.responseCustomerData?.hostData!!.wlsresult!=null){
                val wlsResult = getResult(response.result?.responseCustomerData?.hostData!!.wlsresult?.resultCountWLS)
                setHostResult(wlsResult,iv_wl_status)
            }
            if(response.result?.responseCustomerData?.hostData!!.nmresult!=null){
                val nmResult = getResult(response.result?.responseCustomerData?.hostData!!.nmresult?.resultCountNM)
                setHostResult(nmResult,tv_nm_status)
            }

        }
        }

    private fun setHostResult(result: Triple<Boolean, String, Int>, resultTextView: TextView) {
        if(result.first) {
            resultTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done, 0, 0, 0)
            resultTextView.compoundDrawables[0].setTint(resources.getColor(result.third))
            resultTextView.text = ""
        } else {
            resultTextView.text = result.second
            resultTextView.setTextColor(getColor(result.third))
            resultTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    private fun getResult(resultCount: String?):Triple<Boolean, String, Int> {
        return when {
            resultCount.isNullOrEmpty() -> {
                Triple(false,"NA", R.color.color_ad0000)
            }
            resultCount == "0" -> {
                Triple(true, resultCount, R.color.color_15AD00)
            }
            resultCount == "1" -> {
                Triple(false, resultCount, R.color.color_ad0000)
            }
            else -> {
                Triple(false, resultCount, R.color.color_FF9F30)
            }
        }
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.indeterminateBar).visibility = View.GONE
    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.indeterminateBar).visibility = View.VISIBLE
    }


    /**
     * method to parse Map<Key, Value> to ExtractedIdData instance using Gson.
     */
    private fun getExtractDataFromMap(mrzExtractedMap: Map<String, String>): ParsedMrzBarcodeData? {
        val jsonData = JSONObject(mrzExtractedMap)
        var extractedMrzData: ParsedMrzBarcodeData? = null
        try {
            extractedMrzData = Gson().fromJson(jsonData.toString(), ParsedMrzBarcodeData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return extractedMrzData
    }
    private fun getAmlKycResult(hostDataResponse: HostDataResponse):Pair<String, Int> {
        val result = Pair("NA", R.color.color_ad0000)
        if(hostDataResponse!=null) {
            if(hostDataResponse.pepresult!=null && hostDataResponse.wlsresult!=null && hostDataResponse.nmresult!=null) {
                if(hostDataResponse.pepresult?.resultCountPEP.isNullOrEmpty() || hostDataResponse.wlsresult?.resultCountWLS.isNullOrEmpty()) {
                    return result
                } else {
                    if(hostDataResponse.pepresult?.resultCountPEP.equals("0") && hostDataResponse.wlsresult?.resultCountWLS.equals("0")) {
                        return Pair("Pass", R.color.color_15AD00)
                    } else if(hostDataResponse.pepresult?.resultCountPEP.equals("1") && hostDataResponse.wlsresult?.resultCountWLS.equals("0")) {
                        return Pair("Review Required", R.color.color_FF9F30)
                    } else if(hostDataResponse.pepresult?.resultCountPEP.equals("0") && hostDataResponse.wlsresult?.resultCountWLS.equals("1")) {
                        return Pair("Review Required", R.color.color_FF9F30)
                    }
                }
            }
        }
        return result
    }


    private fun getDmvResult(hostDataResponse: HostDataResponse):Pair<String, Int> {
        val result = Pair("NA", R.color.color_ad0000)
        if(hostDataResponse!=null) {
            if(hostDataResponse.textMatchResult!=null) {

                if (!hostDataResponse.textMatchResult!!.thirdPartyVerificationResultDescription.isNullOrEmpty()) {
                    if (hostDataResponse.textMatchResult!!.thirdPartyVerificationResultDescription == "DMV Data Match") {
                        return Pair("Pass", R.color.color_15AD00)
                    } else if (hostDataResponse.textMatchResult!!.thirdPartyVerificationResultDescription == "DMV Data Mismatch") {
                        return Pair("Failed", R.color.color_ad0000)
                    }
                }
            }
        }
        return result
    }
}