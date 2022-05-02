package com.idmission.sdk2.medium.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.idmission.sdk2.R
import com.idmission.sdk2.capture.IdMissionCaptureLauncher
import com.idmission.sdk2.capture.presentation.camera.helpers.ProcessedCapture
import com.idmission.sdk2.identityproofing.IdentityProofingSDK

class ServiceCallActivity : AppCompatActivity() {
    private var processedCaptures: List<ProcessedCapture>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_call)
        setListener()
    }

    private fun setListener() {

        findViewById<Button>(R.id.service_id_50).setOnClickListener {
            IdentityProofingSDK.idValidationAndcustomerEnroll(
                this,
//                analysisSize = Size(1080,1920),
                uniqueNumber = "123333"
            )
        }
        /*findViewById<Button>(R.id.processed_captures).setOnClickListener {
            if (processedCaptures!=null){
                PhotoResultsActivity.launch(this, processedCaptures!!)
            }else{
                Toast.makeText(this,"No captures Avaialable", Toast.LENGTH_SHORT).show()
            }
        }*/
        /*findViewById<Button>(R.id.final_submit).setOnClickListener {
            callFinalSubmit()
        }*/
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


}