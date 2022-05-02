package com.idmission.sdk2.medium.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.idmission.sdk2.R
import com.idmission.sdk2.client.model.InitializeResponse
import com.idmission.sdk2.client.model.Response
import com.idmission.sdk2.client.model.UiCustomizationOptions
import com.idmission.sdk2.identityproofing.IdentityProofingSDK
import com.idmission.sdk2.utils.LANGUAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    var initializeApiBaseUrl = "https://demo.idmission.com/"
    var apiBaseUrl = "https://apidemo.idmission.com/"
    //TODO update your loginID, password, MerchantID and productID
    var loginID = ""
    var password = ""
    var merchantID: Long = 0
    var productID = 0
    var productName = "Identity_Validation_and_Face_Matching"
    var lang = "EN"
    var isSDKinit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val edtUrl=findViewById<EditText>(R.id.edit_text_url)
            edtUrl.setText(initializeApiBaseUrl)
        val edtLogin=findViewById<EditText>(R.id. edit_text_login_id)
            edtLogin.setText(loginID)
        val edtPassword=findViewById<EditText>(R.id.edit_text_password)
            edtPassword.setText(password)
        val edtMerchantId=findViewById<EditText>(R.id.edit_text_merchant_id)
            edtMerchantId.setText(merchantID.toString(), TextView.BufferType.EDITABLE)
        /*val debugSwitch = findViewById<SwitchCompat>(R.id.switch_btn_debug)
        val gpsSwitch = findViewById<SwitchCompat>(R.id.switch_btn_gps)*/

        findViewById<Button>(R.id.button_continue).setOnClickListener(View.OnClickListener {
            var response: Response<InitializeResponse>
            showProgress();
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    response = IdentityProofingSDK.initialize(
                        this@MainActivity,
                        edtUrl.text.toString(),
                        apiBaseUrl,
                        edtLogin.text.toString(),
                        edtPassword.text.toString(),
                        edtMerchantId.text.toString().toLong(),
                        uiCustomizationOptions = UiCustomizationOptions(LANGUAGE.valueOf(lang)),
                        enableDebug = false)

                }
                if (response.errorStatus==null){
                    isSDKinit = true
                }
            }.invokeOnCompletion {
                startActivity(Intent(this,ServiceCallActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                hideProgress()
            }

        })
    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.indeterminateBar).visibility=View.VISIBLE
        //findViewById<LinearLayout>(R.id.ll_main).visibility = View.GONE
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.indeterminateBar).visibility=View.GONE
       // findViewById<LinearLayout>(R.id.ll_main).visibility = View.VISIBLE
    }
}