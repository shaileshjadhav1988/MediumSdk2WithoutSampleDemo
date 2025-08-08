package com.idmission.sdk2.medium.sample

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.idmission.sdk2.capture.analysis.helpers.FaceMatch
import com.idmission.sdk2.capture.presentation.camera.helpers.ProcessedCapture


internal class PhotoResultsAdapter : RecyclerView.Adapter<PhotoResultsViewHolder>() {

    var processedCaptures: List<ProcessedCapture> = emptyList()
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoResultsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PhotoResultsViewHolder(inflater.inflate(com.idmission.sdk2.capture.R.layout.item_photo_result, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoResultsViewHolder, position: Int) {
        holder.bind(processedCaptures[position], position)
    }

    override fun getItemCount() = processedCaptures.size
}

internal class PhotoResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val photoResultImageView: ImageView = itemView.findViewById(R.id.photoResultImageView)
    private val barcodeResultsText: TextView = itemView.findViewById(com.idmission.sdk2.capture.R.id.barcodeResultTextView)
    private val barcodeResultsLayout: View = itemView.findViewById(com.idmission.sdk2.capture.R.id.barcodeResultLayout)
    private val ocrResultsText: TextView = itemView.findViewById(com.idmission.sdk2.capture.R.id.ocrResultTextView)
    private val ocrResultsLayout: View = itemView.findViewById(com.idmission.sdk2.capture.R.id.ocrResultLayout)
    private val photoNumberText: TextView = itemView.findViewById(com.idmission.sdk2.capture.R.id.photoNumberTextView)
    private val realSpoofText: TextView = itemView.findViewById(R.id.realSpoofTextView)
    private val faceMatchText: TextView = itemView.findViewById(R.id.faceMatchTextView)

    fun bind(processedCapture: ProcessedCapture, index: Int) {
        val number = index + 1
        photoNumberText.text = itemView.context.getString(com.idmission.sdk2.capture.R.string.photo_number, number)
        when (processedCapture) {
            is ProcessedCapture.DocumentDetectionResult.RealDocument -> bindDocumentResult(processedCapture)
            is ProcessedCapture.DocumentDetectionResult.SpoofDocument -> bindSpoofDocumentResult(processedCapture)
            is ProcessedCapture.LiveFaceDetectionResult.RealFace -> bindRealLiveFaceResult(
                processedCapture
            )
            is ProcessedCapture.LiveFaceDetectionResult.SpoofFace -> bindSpoofLiveFaceResult(
                processedCapture
            )
            is ProcessedCapture.LiveFaceDetectionResult.TimeOut -> bindTimeOutLiveFaceResult(
                processedCapture
            )

            is ProcessedCapture.DocumentDetectionResult.TimeOutAutofillDocument -> TODO()
            is ProcessedCapture.DocumentDetectionResult.TimeOutDocument -> TODO()
            is ProcessedCapture.DocumentDetectionResult.TimeOutIdDocument -> TODO()
            is ProcessedCapture.VideoIdDetectionResult.FailedIdMatch -> TODO()
            is ProcessedCapture.VideoIdDetectionResult.FailedIdToIdFaceMatch -> TODO()
            is ProcessedCapture.VideoIdDetectionResult.FailedIdToLiveFaceMatch -> TODO()
            is ProcessedCapture.VideoIdDetectionResult.FailedReadText -> TODO()
            is ProcessedCapture.VideoIdDetectionResult.ImageFrames -> TODO()
            is ProcessedCapture.VideoIdDetectionResult.LiveFaceLost -> TODO()
            is ProcessedCapture.VideoIdDetectionResult.TimeOut -> TODO()
            is ProcessedCapture.VideoIdDetectionResult.Video -> TODO()
        }
//        Log.i("PhotoResultsViewHolder", "Photo $number")
//        Log.i("PhotoResultsViewHolder", "timeStartedAt:      ${
//            processedCapture.timeStartedAt!! /
//            1000}")
//        Log.i("PhotoResultsViewHolder", "timeDetectedAt:     ${processedCapture.timeDetectedAt!! /
//            1000}")
//        Log.i("PhotoResultsViewHolder", "timeWithinBoundsAt: ${processedCapture
//            .timeWithinBoundsAt!! /
//            1000}")
//        Log.i("PhotoResultsViewHolder", "timeFinishedAt:     ${processedCapture.timeFinishedAt!! /
//            1000}")
    }


    private fun bindDocumentResult(detectionResult: ProcessedCapture.DocumentDetectionResult
    .RealDocument) {
        photoResultImageView.isVisible = detectionResult.file != null
        Glide.with(photoResultImageView).load(detectionResult.file).into(photoResultImageView)
        barcodeResultsText.text = detectionResult.barcodeString + "\n" + detectionResult
            .modelName + detectionResult.barcodeMap.toString()
        barcodeResultsLayout.isVisible = detectionResult.barcodeString != null
        ocrResultsText.text =  " \nOCR Result : \n\n${detectionResult.ocrString}"
        if (!detectionResult.mrzMap.isNullOrEmpty()) {
            ocrResultsText.text =
                "${ocrResultsText.text as String?}\n\nDetected MRZ :  \n\n${
                    detectionResult
                        .mrzMap
                        .toString()
                }"
        }
        ocrResultsText.text = "${ocrResultsText.text as String?}\n\n Model Detail :  \n\n${
            detectionResult
                .modelName
        }"


        ocrResultsLayout.isVisible = detectionResult.ocrString != null
        displayFaceMatch(detectionResult.faceMatch)
    }

    private fun bindSpoofDocumentResult(detectionResult: ProcessedCapture.DocumentDetectionResult.SpoofDocument) {
        photoResultImageView.isVisible = false
        barcodeResultsLayout.isVisible = false
        ocrResultsLayout.isVisible = false
        realSpoofText.text = "${realSpoofText.context.getString(R.string.spoof_document)}\n${detectionResult
            .realnessScore}\n${detectionResult.modelName}"
        realSpoofText.isVisible = true
    }

    private fun bindRealLiveFaceResult(detectionResult: ProcessedCapture.LiveFaceDetectionResult.RealFace) {
        photoResultImageView.isVisible = detectionResult.file != null
        Glide.with(photoResultImageView).load(detectionResult.file).into(photoResultImageView)
        barcodeResultsLayout.isVisible = false
        ocrResultsLayout.isVisible = false
        realSpoofText.text = "${realSpoofText.context.getString(R.string.real)}\n${detectionResult.livenessScore}\n${detectionResult.modelName}"
        realSpoofText.isVisible = true
        displayFaceMatch(detectionResult.faceMatch)
    }

    private fun bindSpoofLiveFaceResult(detectionResult: ProcessedCapture.LiveFaceDetectionResult.SpoofFace) {
        photoResultImageView.isVisible = false
        barcodeResultsLayout.isVisible = false
        ocrResultsLayout.isVisible = false
        realSpoofText.text = "${realSpoofText.context.getString(R.string.spoof)}\n${detectionResult.livenessScore}\n${detectionResult.modelName}"
        realSpoofText.isVisible = true
    }

    private fun bindTimeOutLiveFaceResult(detectionResult: ProcessedCapture.LiveFaceDetectionResult.TimeOut){
        photoResultImageView.isVisible = false
        barcodeResultsLayout.isVisible = false
        ocrResultsLayout.isVisible = false
        realSpoofText.setText("TimeOut")
        realSpoofText.isVisible = true
    }

    @SuppressLint("SetTextI18n")
    private fun displayFaceMatch(faceMatch: FaceMatch?) {
        faceMatchText.isVisible = faceMatch != null
        faceMatch ?: return
        val matchText = if (faceMatch.withinTolerance) "FACE MATCHES" else "FACE DOES NOT MATCH"
        faceMatchText.text = "$matchText (distance = ${faceMatch.distance})\n${faceMatch.modelName}"
    }

}
