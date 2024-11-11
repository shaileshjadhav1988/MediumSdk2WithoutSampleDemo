package com.idmission.idmissioncapture.presentation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** @suppress */
@Serializable
data class LoginAccessTokenInfo(
    val access_token: String? = null,
    val expires_in: Int? = null,
    @SerialName("not-before-policy")
    val notBeforePolicy: Int? = null,
    val refresh_expires_in: Int? = null,
    val scope: String? = null,
    val session_state: String? = null,
    val token_type: String? = null,
    var tokenCreatedEnvironment: String? = null,
    var expireTimeInMilli: Long? = null
)
