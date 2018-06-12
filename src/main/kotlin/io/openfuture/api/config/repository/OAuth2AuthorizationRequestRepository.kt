package io.openfuture.api.config.repository

import io.openfuture.api.config.propety.AuthorizationProperties
import io.openfuture.api.util.CookieUtils
import org.apache.commons.lang3.SerializationUtils
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Alexey Skadorva
 */
class OAuth2AuthorizationRequestRepository (private val properties: AuthorizationProperties) : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest {
        val authCookie = request.cookies.find({ properties.cookieName == it.name })

        return SerializationUtils.deserialize(Base64.getUrlDecoder().decode(authCookie?.value))
    }

    override fun saveAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest, request: HttpServletRequest,
                                          response: HttpServletResponse) {
        val serializedAuthorizationRequest = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(authorizationRequest))

        CookieUtils.add(response, properties.cookieName!!, serializedAuthorizationRequest)
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest {
        return loadAuthorizationRequest(request)
    }

}
