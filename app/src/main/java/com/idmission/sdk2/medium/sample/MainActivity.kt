package com.idmission.sdk2.medium.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.idmission.sdk2.R
import com.idmission.sdk2.client.model.InitializeResponse
import com.idmission.sdk2.client.model.Response
import com.idmission.sdk2.client.model.SDKCustomizationOptions
import com.idmission.sdk2.identityproofing.IdentityProofingSDK
import com.idmission.sdk2.sample.tokenapi.LoginTokenApiHandler
import com.idmission.sdk2.utils.LANGUAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    var apiBaseUrl = "https://apidemo.idmission.com/"
    //TODO update your loginID, password, MerchantID and productID
    var loginID = ""
    var password = ""
    var merchantID: Long = 0
    var productID = ""
    var clientSecret = ""
    var clientID = ""
    var productName = "Identity_Validation_and_Face_Matching"
    var lang = "EN"
    var isSDKinit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val edApiUrl = findViewById<EditText>(R.id.edit_api_text_url)
        edApiUrl.setText(apiBaseUrl)
        val edtLogin = findViewById<EditText>(R.id. edit_text_login_id)
        edtLogin.setText(loginID)
        val edtPassword = findViewById<EditText>(R.id.edit_text_password)
        edtPassword.setText(password)
        val edtMerchantId = findViewById<EditText>(R.id.edit_text_merchant_id)
        edtMerchantId.setText(merchantID.toString(), TextView.BufferType.EDITABLE)

        val edtClientSecret = findViewById<EditText>(R.id.edit_text_client_secret)
        edtClientSecret.setText(clientSecret)

        val edtClientId = findViewById<EditText>(R.id.edit_text_client_id)
        edtClientId.setText(""+clientID)

        findViewById<Button>(R.id.button_continue).setOnClickListener(View.OnClickListener {
            var response: Response<InitializeResponse>
            showProgress();
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {

                    var accessTokenInfo = LoginTokenApiHandler.getLoginAccessTokenRequest(
                        clientId = findViewById<EditText>(R.id.edit_text_client_id).text.toString(),
                        userId = findViewById<EditText>(R.id.edit_text_login_id).text.toString(),
                        password = findViewById<EditText>(R.id.edit_text_password).text.toString(),
                        clientSecret = findViewById<EditText>(R.id.edit_text_client_secret).text.toString(),
                        tokenCreateEnvironment = if(findViewById<EditText>(R.id.edit_api_text_url).text.toString().contains("demo")) "DEMO"
                        else if(findViewById<EditText>(R.id.edit_api_text_url).text.toString().contains("uat"))
                            "UAT" else "KYC"
                    )

                    response = IdentityProofingSDK.initialize(
                        this@MainActivity,
                        edApiUrl.text.toString(),
                        sdkCustomizationOptions = SDKCustomizationOptions(LANGUAGE.valueOf(lang)),
                        enableDebug = false,
                        isUpdateModelsData = true,
                        accessToken = accessTokenInfo?.access_token)

                }
                isSDKinit = response.result?.status?.statusCode?.equals("000") == true
            }.invokeOnCompletion {
                if(isSDKinit){
                    startActivity(Intent(this, ServiceCallActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }else{
                    Toast.makeText(this@MainActivity,"Error: SDK initialization credentials are not correct",Toast.LENGTH_SHORT).show()
                }
                hideProgress()
            }

        })
    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.indeterminateBar).visibility=View.VISIBLE
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.indeterminateBar).visibility = View.GONE
    }
}