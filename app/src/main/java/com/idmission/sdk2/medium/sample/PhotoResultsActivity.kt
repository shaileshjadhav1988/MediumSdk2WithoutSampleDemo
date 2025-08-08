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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.idmission.sdk2.capture.IdMissionCaptureLauncher
import com.idmission.sdk2.capture.presentation.camera.helpers.ProcessedCapture
import com.idmission.sdk2.client.model.CommonApiResponse
import com.idmission.sdk2.client.model.HostDataResponse
import com.idmission.sdk2.client.model.Response
import com.idmission.sdk2.identityproofing.IdentityProofingSDK
import com.idmission.sdk2.medium.sample.databinding.ActivityPhotoResultsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

internal class PhotoResultsActivity : Activity() {
    private lateinit var binding : ActivityPhotoResultsBinding
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
        binding = ActivityPhotoResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btDone.setOnClickListener {
            finish()
        }

        binding.photoResultToolbar.apply {
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
                is ProcessedCapture.DocumentDetectionResult.TimeOutAutofillDocument -> TODO()
                is ProcessedCapture.DocumentDetectionResult.TimeOutDocument -> TODO()
                is ProcessedCapture.DocumentDetectionResult.TimeOutIdDocument -> TODO()
                is ProcessedCapture.LiveFaceDetectionResult.TimeOut -> TODO()
                is ProcessedCapture.VideoIdDetectionResult.FailedIdMatch -> TODO()
                is ProcessedCapture.VideoIdDetectionResult.FailedIdToIdFaceMatch -> TODO()
                is ProcessedCapture.VideoIdDetectionResult.FailedIdToLiveFaceMatch -> TODO()
                is ProcessedCapture.VideoIdDetectionResult.FailedReadText -> TODO()
                is ProcessedCapture.VideoIdDetectionResult.ImageFrames -> TODO()
                is ProcessedCapture.VideoIdDetectionResult.LiveFaceLost -> TODO()
                is ProcessedCapture.VideoIdDetectionResult.TimeOut -> TODO()
                is ProcessedCapture.VideoIdDetectionResult.Video -> TODO()
            }
        }
    }

    private fun showFaceImages(processedCaptures: List<ProcessedCapture>) {
        val detection = processedCaptures[0] as ProcessedCapture.LiveFaceDetectionResult.RealFace
        Glide.with(binding.photoResultImageView).load(detection.file)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true).into(binding.photoResultImageView)
        binding.realSpoofTextView.text = "${binding.realSpoofTextView.context.getString(R.string.real)}\t${detection.livenessScore}"

    }


    private fun showDocumentImages(processedCaptures: List<ProcessedCapture>) {
        val frontDetection = processedCaptures[1] as ProcessedCapture.DocumentDetectionResult
        .RealDocument
        Glide.with(binding.docFrontImageView).load(frontDetection.file)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true).into(binding.docFrontImageView)
        if(processedCaptures.size > 2){
            val backDetection = processedCaptures[2] as ProcessedCapture.DocumentDetectionResult
            .RealDocument
            Glide.with(binding.docBackImageView).load(backDetection.file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.docBackImageView)
        }

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
                    binding.faceMatchTextView.visibility= View.VISIBLE
                    val matchText = if (it.faceMatch!!.withinTolerance) "FACE MATCHES" else "FACE DOES NOT MATCH"
                    binding.faceMatchTextView.text = matchText

                }
            } else {
                extractedDataMap = mapOf()
            }
        }
        val detectedData = getExtractDataFromMap(extractedDataMap!!)

        if (!detectedData?.FirstName.isNullOrEmpty()){
            binding.tvProcessedFullName.setText(detectedData?.FirstName +" "+ (detectedData?.MiddleName +" "+detectedData?.LastName))
        }else{
            binding.llProcessedFullName.visibility=View.GONE
        }
        if (!detectedData?.DateofBirth.isNullOrEmpty()){
            binding.tvProcessedDob.setText(detectedData?.DateofBirth)
        }else{
            binding.llProcessedDob.visibility=View.GONE
        }
        if (!detectedData?.AddressLine1.isNullOrEmpty()){
            binding.tvProcessedAddress.setText(detectedData?.AddressLine1)
        }else{
            binding.llProcessedAddress.visibility=View.GONE
        }
        if (!detectedData?.IDNumber.isNullOrEmpty()){
            binding.tvProcessedDocument.setText(detectedData?.IDNumber)
        }else{
            binding.llProcessedDocument.visibility=View.GONE
        }
        if (!detectedData?.ExpiryDate.isNullOrEmpty()){
            binding.tvProcessedExpiry.setText(detectedData?.ExpiryDate)
        }else{
            binding.llProcessedExpiry.visibility=View.GONE
        }
        if (!detectedData?.IssueDate.isNullOrEmpty()){
            binding.tvProcessedIssueDate.setText(detectedData?.IssueDate)
        }else{
            binding.llProcessedIssueDate.visibility=View.GONE
        }
        if (!detectedData?.Gender.isNullOrEmpty()){
            binding.tvProcessedGender.setText(detectedData?.Gender)
        }else{
            binding.llProcessedGender.visibility=View.GONE
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
            binding.llIdData.visibility=View.VISIBLE
            binding.llEnrolledData.visibility = View.VISIBLE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.firstName.isNullOrEmpty()){
            binding.tvFullName.setText(response.result?.responseCustomerData?.extractedPersonalData?.firstName+" "+ (response.result?.responseCustomerData?.extractedPersonalData?.middleName +" "+response.result?.responseCustomerData?.extractedPersonalData?.lastName))
        }else{
            binding.llFullName.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.dob.isNullOrEmpty()){
            binding.tvDob.setText(response.result?.responseCustomerData?.extractedPersonalData?.dob)
        }else{
            binding.llDob.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.addressLine1.isNullOrEmpty()){
            binding.tvAddress.setText(response.result?.responseCustomerData?.extractedPersonalData?.addressLine1+" "+response.result?.responseCustomerData?.extractedPersonalData?.addressLine2)
        }else{
            binding.llAddress.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedIdData?.idNumber.isNullOrEmpty()){
            binding.tvDocument.setText(response.result?.responseCustomerData?.extractedIdData?.idNumber)
        }else{
            binding.llDocument.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedIdData?.idExpirationDate.isNullOrEmpty()){
            binding.tvExpiry.setText(response.result?.responseCustomerData?.extractedIdData?.idExpirationDate)
        }else{
            binding.llExpiry.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedIdData?.idIssueDate.isNullOrEmpty()){
            binding.tvIssueDate.setText(response.result?.responseCustomerData?.extractedIdData?.idIssueDate)
        }else{
            binding.llIssueDate.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.gender.isNullOrEmpty()){
            binding.tvGender.setText(response.result?.responseCustomerData?.extractedPersonalData?.gender)
        }else{
            binding.llGender.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.enrolledDate.isNullOrEmpty()){
            binding.tvEnrolledDate.setText(response.result?.responseCustomerData?.extractedPersonalData?.enrolledDate)

        }else{
            binding.llEnrolledDate.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedPersonalData?.firstName.isNullOrEmpty()){
            binding.tvEnrolledFullName.setText(response.result?.responseCustomerData?.extractedPersonalData?.firstName+" "+ (response.result?.responseCustomerData?.extractedPersonalData?.middleName +" "+response.result?.responseCustomerData?.extractedPersonalData?.lastName))

        }else{
            binding.llEnrolledFullName.visibility=View.GONE
        }
        binding.llClientCustomerNumber.visibility = View.GONE

        if (!response.result?.responseCustomerData?.extractedPersonalData?.enrolledFaceImage.isNullOrEmpty()){
            binding.rlLiveResult.visibility = View.VISIBLE
            Glide.with(binding.liveImage).load(response.result?.responseCustomerData?.extractedPersonalData?.enrolledFaceImage)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.liveImage)
        }else{
            binding.liveImage.visibility=View.GONE
        }
        if (!response.result?.responseCustomerData?.extractedIdData?.idProcessImageFront.isNullOrEmpty()){
            binding.llDocImage.visibility = View.VISIBLE
            Glide.with(binding.ivDocImage).load(response.result?.responseCustomerData?.extractedIdData?.idProcessImageFront)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.ivDocImage)
        }else{
            binding.ivDocImage.visibility = View.GONE
        }
        if(response.result?.responseCustomerData?.hostData !=null){
            binding.kycDmvResult.visibility = View.VISIBLE
            val dmvResult = getDmvResult(response.result?.responseCustomerData?.hostData!!)
            val kycResult = getAmlKycResult(response.result?.responseCustomerData?.hostData!!)
            binding.ivDmvResult.setColorFilter(resources.getColor(
                dmvResult.second))
            binding.tvDmvResult.text = kycResult.first
            binding.ivKycResult.setColorFilter(resources.getColor(
                kycResult.second))
            binding.tvKycResult.text = kycResult.first

            if(response.result?.responseCustomerData?.hostData!!.pepresult!=null){
                val pepResult = getResult(response.result?.responseCustomerData?.hostData!!.pepresult?.resultCountPEP)
                setHostResult(pepResult,binding.ivPepStatus)
            }
            if(response.result?.responseCustomerData?.hostData!!.wlsresult!=null){
                val wlsResult = getResult(response.result?.responseCustomerData?.hostData!!.wlsresult?.resultCountWLS)
                setHostResult(wlsResult,binding.ivWlStatus)
            }
            if(response.result?.responseCustomerData?.hostData!!.nmresult!=null){
                val nmResult = getResult(response.result?.responseCustomerData?.hostData!!.nmresult?.resultCountNM)
                setHostResult(nmResult,binding.tvNmStatus)
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