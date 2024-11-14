# Module IDentity-SDK

## Introduction

This guide introduces the IDentity Android SDK within the IDmission product suite. Developers,
project managers and QA testers should reference this guide for information on configuration and
use of the IDentity SDK on the Android platform. We recommend reviewing the entire implementation
guide to fully understand the IDentity SDK functionality and its respective capabilities.

This guide details processes and procedures for embedding the IDentity SDK into your host
application and utilizing its current features. For additional IDentity SDK support, please contact
our Customer Support team at support@idmission.com.

## Overview and Key Features

The IDmission IDentity SDK is a comprehensive toolkit that enables the use of any combination of
factors of identity to complete digital transformation goals. The goal of the IDentity SDK is to
offer seamless integration into an existing digital paradigm where the end-to-end customer
experience is still owned and managed in-house.

## Quick Links to get started with IDentity SDK for Android

SDK Flavours Download Links - As per your requirement you can downloads the below IDentitySDK /
IDentityMediumSDK / IDentityLiteSDK / IDentityVideoIDSDK.

[Download IDentitySDK](https://github.com/Idmission-LLC/Sdk2SampleDemo) - Directly links to the
IDentitySDK Sample app on IDmission GitHub Repository <br/>
[Download IDentityMediumSDK](https://github.com/Idmission-LLC/MediumSdk2SampleDemo) - Directly links
to the IDentityMediumSDK Sample app on IDmission GitHub Repository
<br/>
[Download IDentityLiteSDK](https://github.com/Idmission-LLC/LiteSdk2SampleDemo) - Directly links to
the IDentityLiteSDK Sample app on IDmission GitHub Repository
<br/>
*Download IDentityVideoIDSDK (Coming soon) - Directly links to
the IDentityVideoIDSDK Sample app on IDmission GitHub Repository*
<br/>

<a href="https://documentation.idmission.com/identity/Android-SDK-2/index.html">
SDK Documentation</a> - Directly links to the Identity Proofing SDK

<br/>
<br/>

The main features supported in this SDK are:
<br/>

* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/live-face-check.html">
  Live face Check</a><br/>
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/id-validation.html">
  ID Validation</a><br/>
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/id-validation-and-match-face.html">
  ID Validation and face match</a><br/>
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/id-validation-andcustomer-enroll.html">
  Enrollment</a><br/>
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/customer-enroll-biometrics.html">
  Enrollment with Biometrics</a><br/>
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/customer-verification.html">
  Customer Verification</a><br/>
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/auto-fill.html">
  Auto Fill</a><br/>
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/video-id.html">
  Video ID</a><br/><br/>
## SDK Server API call
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/initialize.html">
  Initialize SDK</a><br/>
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/final-submit.html">
  Final Submit</a><br/><br/>

## Getting Started

### 1. Please contact to sales@idmission.com for Login Credentials, which you will later pass to the SDK.

### 2.1. (Groovy) Go to your **project-level** build.gradle file, and add the following in the

    ```
    allprojects {  
        repositories {  
            google()  
            jcenter()  
            // important stuff below  
            maven {
                url "https://gitlab.idmission.com/api/v4/projects/220/packages/maven"
                name "GitLab"
                credentials(HttpHeaderCredentials) {
                    name = "Private-Token"
                    value = "WESesyuSD9fQeqNEyig6"
                }
                authentication {
                    header(HttpHeaderAuthentication)
                }
            }
            //Required for fingerprint capture
            maven { url 'https://jitpack.io' }
        }  
    }
    ```
### 2.2. (Kotlin DSL) Go to your **project-level** settings.gradle.kts file, and add the following in the

```
   pluginManagement {
        repositories {
            google()
            mavenCentral()
            gradlePluginPortal()
        }
    }
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            // important stuff below 
            maven {
                url = uri("https://gitlab.idmission.com/api/v4/projects/220/packages/maven")
                name = "GitLab"
                credentials(HttpHeaderCredentials::class) {
                    name = "Private-Token"
                    value = "WESesyuSD9fQeqNEyig6"
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
            //Required for fingerprint capture
            maven {
                 url = uri("https://jitpack.io")
            }
        }
    } 
    rootProject.name = "AndroidSDKWithKTS"
    include(":app")

```


### 3. In your **app-level** build.gradle file, add the following:

    ```
    android {  
        // Java 8 is required for CameraX  
        compileOptions {  
            sourceCompatibility JavaVersion.VERSION_1_8  
            targetCompatibility JavaVersion.VERSION_1_8  
        }  
        kotlinOptions {  
            jvmTarget = '1.8'  
        }  
    }
    
    //IdentityFull SDK
    dependencies {  
         implementation 'com.idmission.sdk2:idmission-sdk:9.6.22.2.13'     
    }
    
    //IdentityMedium SDK
    dependencies {  
         implementation 'com.idmission.sdk2:idmission-mediumsdk:9.6.22.2.13'     
    }
    
    //IdentityLite SDK
    dependencies {  
         implementation 'com.idmission.sdk2:idmission-litesdk:9.6.22.2.13'     
    }
    
    //IdentityVideoID SDK
    dependencies {  
         implementation 'com.idmission.sdk2:idmission-videoidsdk:9.6.22.2.13'     
    }
    ```

### 4. Sync your project with Gradle
### 5. Generate Access Token :
<b>Tokens are to be obtained on your server and sent to the client in the initialize call.</b>
### Login Credentials required for token generation.
The following details are required to generate a token:

    * Auth_url : Authentication url for the given environment

    * client_id : Client ID generated for the specific company

    * client_secret : Client credentials/secret generated for the specific company

    * Username : Integ user name

    * Password: Password of the Integ user

All company login details needed are provided upon sign up on the Identity Portal.

### Token Generation using RESTful API
Token Generation Sample Request:
```
         curl --location --request POST 'https://auth.idmission.com/auth/realms/identity/protocol/openid-connect/token' \
         --header 'Content-Type: application/x-www-form-urlencoded' \
         --data-urlencode 'grant_type=password' \
         --data-urlencode 'client_id=XXXXXXX' \
         --data-urlencode 'client_secret=XXXXXXXX' \
         --data-urlencode 'username=XXXXXXXX' \
         --data-urlencode 'password=XXXXXXXXX' \
         --data-urlencode 'scope=api_access'
```

Sample Response - Token Generation:
```
     {
        "access_token": "eyJhbGciO....5gNZx03Myb8ZuyY2gu3u-8KgGmULBs9mkPcg",
        "expires_in": 18000,
        "refresh_expires_in": 0,
        "token_type": "Bearer",
        "session_state": "e0b689e8-e7c6-47b7-bf9d-1349ea813c96",
        "scope": "email profile api_access"
     }
```
The following details are the response parameters

    * access_token : Access Token value

    * expires_in : Access token expires time in seconds

    * refresh_expires_in : Refresh access token expires time in seconds

    * token_type : Token type

    * session_state : session state value

    * scope : Token scope

### 6.  SDK Initialization API.
* <a href="https://documentation.idmission.com/identity/Android-SDK-2/-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/initialize.html">
  Initialize SDK</a><br/>
<b>Token obtained from server needs to be passed in initialize call as below</b>
```
     IdentityProofingSDK.initialize(
     activity,
     apiBaseUrl = apiBaseUrl,
     sdkCustomizationOptions = SDKCustomizationOptions(language = "EN"),
     enableDebug = false,
     accessToken = accessTokenValue)
```

### 7. You may now use the library. Example usage below:
        ```
        class LaunchActivity : Activity() {    
        
            private val launcher = IdMissionCaptureLauncher()    
             var apiBaseUrl = "https://api.idmission.com/"
            
            override fun onCreate(savedInstanceState: Bundle?) {  
                super.onCreate(savedInstanceState)  
                setContentView(R.layout.activity_launch)
                
                //SDK initialize call
                init_button.setOnClickListener{
                    CoroutineScope(Dispatchers.Main).launch {
                        var response: Response<InitializeResponse>
                        withContext(Dispatchers.IO) {
                            // Generate a token as described in the 'Generate Access Token' section.
                            response = IdentityProofingSDK.initialize(
                                applicationContext, 
                                apiBaseUrl = apiBaseUrl, 
                                enableDebug = enableDebug,
                                enableGPS = enableGPS,
                                sdkCustomizationOptions = SDKCustomizationOptions(
                                    language = LANGUAGE
                                        .valueOf("EN")
                                ),
                                accessToken = accessTokenValue,
                            )
                        }
                }
                
            //Call Enroll Service 
                someButton.setOnClickListener {
                    IdentityProofingSDK.idValidationAndcustomerEnroll(
                        this,
                        uniqueNumber = uniqueNumber.text.toString()) 
                }  
                
                // finalSubmit call for submit data to the server
                submitDataButton.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        IdentityProofingSDK.finalSubmit(
                            applicationContext
                        )
                    }
                }  
             
        
            // capture result is received in onActivityResult    
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {  
                data ?: return  
                if (requestCode != IdMissionCaptureLauncher.CAPTURE_REQUEST_CODE) return  
                val processedCaptures = data.extras?.getParcelableArray(IdMissionCaptureLauncher.EXTRA_PROCESSED_CAPTURES)
                // do whatever you want with the data!  
            } 
        ```


Additional supported features

* <a href="./-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/document-capture.html">
  Document Capture</a><br/>
* Signature Capture<br/>
* Four Fingerprint Capture<br/><br/>

## Getting Started for Signature Capture

1. In your **app-level** build.gradle file, add the following:

```
android {  
    // Java 8 is required for CameraX  
    compileOptions {  
        sourceCompatibility JavaVersion.VERSION_1_8  
        targetCompatibility JavaVersion.VERSION_1_8  
    }  
    kotlinOptions {  
        jvmTarget = '1.8'  
    }  
}

dependencies {  
     implementation 'com.idmission.sdk2:signatureLib:9.6.22.1'    
}

```

2. Sync your project with Gradle
3. To capture a signature, use the following code.

```
SignatureSDK.captureSignature(Activity activityContext)

SignatureSDK.captureSignature(
    Activity activityContext, 
    JSONObject captureSignatureConfig)
```

Signature Capture Parameters

|Parameter |      Type      |                                                                                                           Description                                                                                                            |
| :---: |:--------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|<B>Parameter | <B>Type        |                                                                                                          <B>Description                                                                                                          |
| activityContext|    Context     |                                                                                                    Instance of your Activity                                                                                                     |
| captureSignatureConfig|   JSONObject   | { "signature_capture_background": "N", "signature_title_label_message": "Sign 1", "signature_clear_btn_label_message": "clear 1", "signature_done_btn_label_message": "done 1", "signature_capture_detect_coordinates": "true" } |

4. You may now use the library. Example usage below:

```
class LaunchActivity : Activity() {    

    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        setContentView(R.layout.activity_launch)
        
        //SDK signature call
        signatureCapture.setOnClickListener{
        var doneBtnText = getString(R.string.done)
            var cancelBtnText = getString(R.string.clear)
            var signTitle = getString(R.string.sign_title)

            SignatureSDK.captureSignature(this@LaunchActivity, JSONObject("{ " +
                "\"signature_capture_background\": “N”, \"signature_title_label_message\": ${signTitle}, " +
                "\"signature_clear_btn_label_message\": ${cancelBtnText}, " +
                "\"signature_done_btn_label_message\": ${doneBtnText}, " +
                "\"signature_capture_detect_coordinates\": \"true\" }"))
        }
        

    // Signature result is received in onActivityResult and set this result to SDK 2.0 Api setSignatureData  
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {  
        data ?: return  
        if (requestCode == SignatureConstants.SIGNATURE_CAPTURE_REQUEST_CODE) {
            var signatureData = data?.getStringExtra("SignatureImage")
            var signatureDataCoordinates = data?.getStringExtra("SignatureDataCoordinates")
            IdentityProofingSDK.setSignatureData(signatureData,signatureDataCoordinates)
        } else if (requestCode == SignatureConstants.SIGNATURE_CANCEL_RESPONSE_CODE) {
            // cancelled by user
            IdentityProofingSDK.setSignatureData(null,null)
        }
    } 
```

## Getting Started for Four Fingerprint Capture

1. In your **app-level** build.gradle file, add the following:

```
android {  
    // Java 8 is required for CameraX  
    compileOptions {  
        sourceCompatibility JavaVersion.VERSION_1_8  
        targetCompatibility JavaVersion.VERSION_1_8  
    }  
    kotlinOptions {  
        jvmTarget = '1.8'  
    }  
}

dependencies {  
    implementation 'com.idmission.sdk2:4FingerprintCaptureLib:9.5.9.2' 
}

```

2. Sync your project with Gradle
3. You may now use the library. Example usage below:

```
class LaunchActivity : Activity() {    

    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        setContentView(R.layout.activity_launch)
        
        //capture fingerprint
        fingerPrintCapture.setOnClickListener{
            val indexCapture: Boolean = true
            val middleCapture: Boolean = true
            val ringCapture: Boolean = true
            val babyCapture: Boolean = true

            val dIndexKeep = true
            val dMiddleKeep = true
            val dRingKeep = true
            val dBabyKeep = true

            val captureLeftHand: Boolean = true
            val instructionScreen: Boolean = false
            val config = JSONObject()
            config.put(
                UIConfigurationParameters.CFC_PROCESS_INDEX_FINGER,
                if (indexCapture) "Y" else "N"
            )
            config.put(
                UIConfigurationParameters.CFC_PROCESS_MIDDLE_FINGER,
                if (middleCapture) "Y" else "N"
            )
            config.put(
                UIConfigurationParameters.CFC_PROCESS_RING_FINGER,
                if (ringCapture) "Y" else "N"
            )
            config.put(
                UIConfigurationParameters.CFC_PROCESS_BABY_FINGER,
                if (babyCapture) "Y" else "N"
            )
            config.put(
                UIConfigurationParameters.CFC_CAPTURE_LEFT_HAND,
                if (captureLeftHand) "Y" else "N"
            )

            config.put(
                UIConfigurationParameters.CFC_KEEP_INDEX_FINGER,
                if (dIndexKeep) "Y" else "N"
            )
            config.put(
                UIConfigurationParameters.CFC_KEEP_MIDDLE_FINGER,
                if (dMiddleKeep) "Y" else "N"
            )
            config.put(
                UIConfigurationParameters.CFC_KEEP_RING_FINGER,
                if (dRingKeep) "Y" else "N"
            )
            config.put(
                UIConfigurationParameters.CFC_KEEP_BABY_FINGER,
                if (dBabyKeep) "Y" else "N"
            )

            config.put(
                UIConfigurationParameters.CFC_SHOW_INSTRUCTION_SCREEN,
                if (instructionScreen == true) "Y" else "N"
            )
            //set language for sdk capture
            FingerPrintCaptureSDK.setLanguage(LanguageUtils.LANGUAGE.ES.toString())
            
            FingerPrintCaptureSDK.captureFourFingerprint(this@IDCaptureActivity,
                config
            ) { resultMap, response -> //set 4fingerPrintData in to the sdk 2.0
                IdentityProofingSDK.set4FingerPrintData(resultMap)
            }
        }

    
```

Custom Camera Fingerprint Capture Configurations
<table><thead>
<tr>
<th style="text-align: left"><strong>Camera fingerprint capture config</strong></th>
<th style="text-align: left"><strong>Description</strong></th>
</tr>
</thead><tbody>
<tr>
<td style="text-align: left"><code>cfc_label_text_typeface_type</code></td>
<td style="text-align: left"><p>Following values are supported for label typeface.</p><p></p><p>DEFAULT, DEFAULT_BOLD, SANS_SARIF, SERIF, MONOSPACE</p></td>
</tr>
<tr>
<td style="text-align: left"><code>cfc_label_text_typeface_style</code></td>
<td style="text-align: left"><p>Following values are supported for label typeface style.</p><p></p><p>NORMAL, BOLD, ITALIC, BOLD_ITALIC</p></td>
</tr>
<tr>
<td style="text-align: left"><p><code>cfc_label_text_color</code></p><p><code>cfc_label_text_color_alpha</code></p></td>
<td style="text-align: left">Color and Transparency of label text on instruction screen.</td>
</tr>
<tr>
<td style="text-align: left"><p><code>cfc_instruction_button_color</code></p><p><code>cfc_instruction_button_alpha</code></p></td>
<td style="text-align: left">Color and Transparency of instruction screen continue button.</td>
</tr>
<tr>
<td style="text-align: left"><p><code>cfc_instruction_button_txt_color</code></p><p><code>cfc_instruction_button_txt_alpha</code></p></td>
<td style="text-align: left">Color and Transparency of instruction screen continue button text.</td>
</tr>
<tr>
<td style="text-align: left"><code>id_enable_label_shadow</code></td>
<td style="text-align: left">Enable/Disable label shadow</td>
</tr>
<tr>
<td style="text-align: left"><p><code>id_capture_button_color</code></p><p><code>id_capture_button_alpha</code></p></td>
<td style="text-align: left">Color and Transparency of ID capture button.</td>
</tr>
<tr>
    <td style="text-align: left"><code>labels</code></td>
    <td style="text-align: left"><p>Currently following labels are shown on FingerPrint Capture screen, that can be customized with your own custom message.</p><p>camera_finger_capture_title<br/>move_closer<br/>move_away<br/>incorrect_hand<br/>hold_steady<br/>capturing_detail<br/>finger_too_close<br/>finger_too_far</td>
</tr>
</tbody></table>Additional functions are also detailed in
the <a href="./-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/index.html">
SDK Documentation</a>

<br/>
Note: When using the IDentity SDK, you do not need to create a request for XML; it is automatically generated by the SDK based on the function that you are calling

#### Parameters Used-

##### SDK initialization-

- [initializeApiBaseUrl] - Base url provided by Idmission to initialize the SDK.
- [apiBaseUrl] - Base url provided by Ismission for API calls.
- [loginId] - LoginId provided by Idmission.
- [password] - Password you have created with loginId.
- [merchantId] - MerchantId provided by idmission.
- [enableDebug] - (Boolean) If you want to enable debug options or not.
- [enableGPS] - (Boolean) If you want to enable GPS options or not.
- [sdkCustomizationOptions] - SDKCustomizationOptions options if you want to add your customized
  UI details.

##### Service Enroll Call

- [UniqueNumber] - Unique Number required.

##### SDK UI Customization Options-

- You can add your own customised ui details for ID and Face in Instruction, Capture and Retry
  screen by adding SDKCustomizationOptions in initialization or service call. You can refer
  below example:

````

    private fun getSdkCustomOptimization(): SDKCustomizationOptions {
        return SDKCustomizationOptions(
            language = LANGUAGE
                .valueOf(language!!),
            
            
            /* For Document related customizations */
            idCaptureCustomizationOptions = IDCaptureCustomizationOptions(
                enableIdInstructionScreen = true,
                stringOptions = IDStringOptions(
                    captureScreenFrontIDLabel = "Scan the Front of your ID",
                    captureScreenBackIDLabel = "SCAN the Back of your ID",
                    captureScreenBarcodeLabel ="Scan the Barcode on the Back of your ID ",
                    captureScreenDocumentCaptureLabel = "Frame Your Document.",
                    captureScreenError = "Sorry, we can\'t seem to find a face and/or required text on this ID.",
                    captureScreenBarcodeError = "Sorry, we can\'t seem to detect the barcode in this image.",
                    moveCloser = "Move ID Closer",
                    moveAway = "Move ID Away",
                    alignRectangle = "Align Document Inside Rectangle",
                    useFront = "Use Front of ID",
                    useBack = "Use Back of ID",
                    makeSurePhotoTextVisible = "Make sure all text on the ID is completely visible.",
                    scanBarcode = "Scan Barcode",
                    makeSureBarcodeVisible = "Make sure the barcode on the ID is completely visible.",
                    frontBackMismatch = "ID Front and Back Do Not Match",
                    flipToBack = "Flip to Capture ID Back",
                    tooMuchGlare = "Too much light,\n move document away from direct light",
                    tooMuchDark = "It\'s too dark to take a good image. Find a place with better lighting.",
                    retryScreenLabelText = "Real ID not detected. Please try again.",
                    retryButtonText = "Retry",
                    cancelButtonText = "Cancel",
                    idInstructionText = "Scan ID",
                    documentInstructionText = "Scan ID",
                    cancelText = "Cancel"
                    
                ),
                layoutOptions = IDLayoutOptions(
                captureLabelGravity = LabelGravity.CENTER,
                topBarCancelButtonGravity = CancelButtonGravity.START),
                colorOptions = IDColorOptions(
                    captureBackgroundColor = "#1C2B48",
                    captureLabelColor = "#FFFFFF",
                    captureSuccessLabelTextColor = "#FFFFFF",
                    
                    captureErrorLabelTextColor = "#1C2B48",
                    captureErrorLabelBackgroundColor = "#FFFFFF",
                    captureErrorLabelBackgroundColor = "#FFFFFF"
                    instructionScreenBackgroundColor = "#1C2B48",
                    instructionScreenLabelTextColor = "#FFFFFF",
                    instructionScreenButtonBackgroundColor = "#FFFFFF",
                    instructionScreenButtonTextColor = "#000000",
                    retryScreenBackgroundColor = "#1C2B48",
                    retryScreenLabelTextColor = "#FFFFFF",
                    retryScreenButtonTextColor = "#000000",
                    retryScreenButtonBackgroundColor = "#FFFFFF",
                    retryScreenImageTintColor = "#FFFFFF",
                    topBarBackgroundColor = "#FFFFFF",
                    topBarTitleTextColor = "#000000"
                ),
                fontOptions = IDFontOptions(
                    labelFont = R.font.roboto_medium,
                    labelFontSize = 14,
                    labelPromptFontSize = 14,
                    instructionScreenButtonFont = R.font.roboto_medium,
                    instructionScreenLabelFont = R.font.roboto_medium,
                    retryScreenLabelFont = R.font.roboto_medium,
                    retryScreenButtonFont = R.font.roboto_medium
                )
            ),

            
            /* For Selfie related customizations */
            selfieCaptureCustomizationOptions = SelfieCaptureCustomizationOptions(
                enableSelfieInstructionScreen = true,
                stringOptions = SelfieStringOptions(
                
                 captureScreenLabel = "Frame your face inside the oval.",
                captureScreenError = "Sorry, we can\'t seem to detect a face.",
                moveCloser = "Move Closer",
                moveAway = "Move Further",
                alignInsideOval = "Align Face Inside Oval",
                capturingFace = "Capturing Face",
                realFace = "Real Face",
                leftEyeClosed = "Left Eye Closed",
                rightEyeClosed = "Right Eye Closed",
                faceMaskDetected = "Face Mask Detected",
                tooMuchLight = "Excessive backlighting detected. Please reduce backlighting to proceed.",
                tooDark = "Excessive darkness detected. Please reduce darkness to proceed.",
                straightenHead = "Tilt head upright",
                eyesClosed = "Keep eyes open",
                moveFaceDown = "Move Face Down",
                moveFaceUp = "Move Face Up",
                glassesDetected = "Remove Glasses",
                hatDetected = "Hat detected",
                scarfDetected = "Scarf Detected",
                scarfHatDetected = "Scarf and Hat detected",
                glassesHatDetected = "Glasses and Hat detected",
                scarfGlassesDetected = "Scarf and Glasses detected",
                maskDetected = "Mask detected",
                maskHatDetected = "Mask and Hat detected",
                scarfMaskDetected = "Scarf and Mask detected",
                maskGlassesDetected = "Mask and Glasses detected",
                fakeFace = "Fake Face",
                retryScreenLabelText = "Live face not detected. Please try again.",
                retryButtonText = "Retry",
                cancelButtonText = "Cancel",
                selfieInstructionText = "Scan selfie",
                cancelText = "Cancel",
                ),
                layoutOptions = SelfieLayoutOptions(
                captureLabelGravity = LabelGravity.CENTER,
                topBarCancelButtonGravity = CancelButtonGravity.START),
                colorOptions = SelfieColorOptions(
                    captureBackgroundColor = "#1C2B48",
                    captureLabelColor = "#FFFFFF",
                    captureSuccessLabelTextColor = "#FFFFFF"
                    captureErrorLabelTextColor = "#1C2B48",
                    captureErrorLabelBackgroundColor = "#FFFFFF",
                    captureErrorLabelBackgroundColor = "#FFFFFF"
                    instructionScreenBackgroundColor = "#1C2B48",
                    instructionScreenLabelTextColor = "#FFFFFF",
                    instructionScreenButtonBackgroundColor = "#FFFFFF",
                    instructionScreenButtonTextColor = "#000000",
                    retryScreenBackgroundColor = "#1C2B48",
                    retryScreenLabelTextColor = "#FFFFFF",
                    retryScreenButtonTextColor = "#000000",
                    retryScreenButtonBackgroundColor = "#FFFFFF",
                    retryScreenImageTintColor = "#FFFFFF",
                    topBarBackgroundColor = "#FFFFFF",
                    topBarTitleTextColor = "#000000",
                ),
                fontOptions = SelfieFontOptions(
                    labelFont = R.font.roboto_medium,
                    labelFontSize = 14,
                    labelPromptFontSize = 14,
                    instructionScreenButtonFont = R.font.roboto_medium,
                    instructionScreenLabelFont = R.font.roboto_medium,
                    retryScreenLabelFont = R.font.roboto_medium,
                    retryScreenButtonFont = R.font.roboto_medium

                )
            )
        )
    }


````

##### Setting an on cancelled callback

See the code snippet below for how to specify a callback function to be triggered when
cancelling/backing out of an SDK operation:

````
IdentityProofingSDK.setOnCancelled(fun() { print("Cancelled") })
````

## SDK documentation

You can find SDK
documentation <a href="./-i-dentity--s-d-k/com.idmission.sdk2.identityproofing/-identity-proofing-s-d-k/index.html">
here</a>

## SDK Flavours

- Identity SDK
- IdentityMedium SDK
- IdentityLite SDK
- IdentityVideoID SDK

## SDK Flavours Supported Features

|                         |  Identity SDK   |  IdentityMedium SDK   |  IdentityLite SDK   |  IdentityVideoID SDK   |
|:-----------------------:|:---------------:|:---------------------:|:-------------------:|:----------------------:|
|                         | <B>Identity SDK | <B>IdentityMedium SDK | <B>IdentityLite SDK | <B>IdentityVideoID SDK |
|     Document Detect     |    On Device    |       On Device       |      On Device      |       On Device        |
|    Rotate, crop etc.    |    On Device    |       On Server       |      On Server      |       On Device        |
|    Document Realness    |    On Device    |       On Server       |      On Server      |       On Device        |
| Document Classification |    On Device    |       On Server       |      On Server      |       On Device        |
|   MRZ/Barcode reading   |    On Device    |       On Device       |      On Server      |       On Device        |
|     OCR from front      |    On Server    |       On Server       |      On Server      |       On Server        |
|       Face detect       |    On Device    |       On Device       |      On Device      |       On Device        |
|     Liveness detect     |    On Device    |       On Device       |      On Device      |       On Device        |
| Detect hats and glasses |    On Device    |       On Server       |      On Server      |       On Device        |
|        Video ID         |       N/A       |          N/A          |         N/A         |       On Device        |

## SDK Version History
#### v9.6.22.2.13 (November 2024)
* Integrated the latest swagger V4 API version. This includes updates to model download based on
  V4, updates to the new initialization process, as well as ensuring backward compatibility.
* Added the selfie image in the response.
* Added support for an initialization parameter that will enable / disable Location / GPS capture.
* Improved ID capture guidance messages to inform the user to hold still once the document is
  detected to ensure a higher quality capture.
* Added configuration support to allow selfie capture with the back camera.
* Added support for additional date and timestamp capture as Exif for ID capture and Selfie
  capture image.
* QR code for the test applications supports the new initialization process.
* Update to support multiple security enhancements including the removal of outdated/unused
  libraries.
* Enhanced API 34 Support in Android
* Bug fixes and performance improvements.
* Updated all default AI models to the latest versions.

##### v9.6.4.2.07 (February 2024)
* Reduce response callback time for SDK Initialization Method.
* Use the doc-detect model to determine document is ID or Passport.
* Updated all default AI models to the latest versions.

##### v9.6.3.2.04 (January 2024)
* Updated SDK support for additional flags (sendProcessedImagesInResponse
  and sendInputImagesInResponse)
* Added ID extracted profession and profession non English data in response
* Updated all default AI models to the latest versions.

##### v9.5.18.2.03 (December 2023)
* Changed default app_name key to sdk_app_name.
* Updated all default AI models to the latest versions.
*
##### v9.5.15.2.09 (November 2023)
* Enhanced Android SDK to support a new feature for voice capture.
* Enhanced Android SDK to support a new face focus model for improved quality at the time of capture.
* Updated the Android SDK to ensure the user is not prompted for file storage permission for ID capture as no images are stored on the device.
* Updated the Android SDK with new Spanish translations for server response status messages.
* Updated the base URLs used in Android SDK for initialization to ensure they are consistent.
* Updated fingerprint feature libraries to be consistent with SDK 1.0
* Updated all default AI models to the latest versions.

##### v9.5.9.2.05
* UI Changes and bug Fixes for Passport NFC detection
* Removed Firebase Analytics
* Bug fixes and performance improvements.

##### v9.5.7.2.08
* Integrate ID Chip reading ability for passport
* Improvement for real ID detection and bug fixes.

##### v9.5.4.2.02
* Fixed GTE Data upload issue for ID capture

##### v9.5.3.2.04
* Updated all local models

##### v9.5.2.2.06
* Updated focus model to be downloaded from server and updated
* default focus threshold.
* Updated UI elements to allow for customization by integrator - all UI elements are now customizable.
  - Instruction Screen button text and colors
  - Instruction text and colors
* Enhancements added to improve handling of Video ID

##### v9.4.8.2.15

* New IdentityVideoID SDK for capturing a selfie and ID to match against a previous ID validation
  call, and then recording a video instructing the user to speak a customizable phrase.
* Added SignatureCapture support
* Added FingerPrintCapture support

##### v9.3.10.2.9

* Prevent two faces from being captured during selfie capture.
* Model Decryption using latest algorithm(AES/GCM/NoPadding).
* Handle the CAN BC DL barcode XSLT
* Added Spanish language support
* Now fitting instead of filling the camera preview to better support the 600px height requirement.
* Added additional customer response data parameters;  `state` and `postalCode`.
* Updated instruction screen for IDCapture & SelfieCapture
* Bug fixes and performance improvements.

##### v9.3.4.2.8

* Additional document upload and capture feature.
* Added customization of overlay images while capturing ID and selfie.
* Added focus model for better image capture.
* Bug fixes and performance improvements.

##### v9.3.1.7

* Autofill.
* Added SDK UI Customization.
* Instruction screens for selfie and ID capture.
* Bug fixes and performance improvements.

##### v9.2.3.4

* Bug fixes and performance improvements

##### v9.1.7.20

* Enrollment
* Enrollment with Biometrics
* Customer Verification
* ID Validation and face match
