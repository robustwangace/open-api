package io.openfuture.api.controller.api

import io.openfuture.api.config.ControllerTests
import io.openfuture.api.domain.auth.OpenKeyDto
import io.openfuture.api.domain.scaffold.GenerateOpenKeyRequest
import io.openfuture.api.entity.auth.OpenKey
import io.openfuture.api.entity.auth.Role
import org.junit.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(OpenKeyApiController::class)
class OpenKeyApiControllerTests : ControllerTests() {

    @Test
    fun generateTokenTest() {
        val openKey = createOpenKey(setOf(Role("ROLE_MASTER")))
        val request = GenerateOpenKeyRequest(Date())
        val newOpenKey = OpenKey(openKey.user, request.expireDate)
        val requestJson = objectMapper.writeValueAsString(request)

        given(keyService.find(openKey.value)).willReturn(openKey)
        given(keyService.generate(request, openKey.user)).willReturn(newOpenKey)

        mvc.perform(post("/api/keys")
                .header(AUTHORIZATION, openKey.value)
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))

                .andExpect(status().isOk)
                .andExpect(content().json("""
                    {
                      "value": ${newOpenKey.value},
                      "enabled": ${newOpenKey.enabled},
                      "expiredDate": ${objectMapper.writeValueAsString(newOpenKey.expiredDate)}
                    }
                    """.trimIndent(), true))
    }

    @Test
    fun generateTokenWhenOpenTokenIsNotFoundShouldRedirectToIndexPageTest() {
        val invalidToken = "not_valid_token"

        given(keyService.find(invalidToken)).willReturn(null)

        mvc.perform(get("/api/keys")
                .header(AUTHORIZATION, invalidToken))

                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrl("http://localhost/"))
    }

    @Test
    fun getAllTest() {
        val openKey = createOpenKey(setOf(Role("ROLE_DEPLOY")))

        given(keyService.find(openKey.value)).willReturn(openKey)
        given(keyService.getAll(openKey.user)).willReturn(listOf(openKey))

        mvc.perform(get("/api/keys")
                .header(AUTHORIZATION, openKey.value))

                .andExpect(status().isOk)
                .andExpect(content().json(objectMapper.writeValueAsString(listOf(OpenKeyDto(openKey))), true))
    }

    @Test
    fun disableTest() {
        val openKey = createOpenKey(setOf(Role("ROLE_DEPLOY")))
        val expectedOpenKey = createOpenKey(setOf(Role("ROLE_DEPLOY"))).apply { enabled = false }

        given(keyService.find(openKey.value)).willReturn(openKey)
        given(keyService.disable(openKey.value)).willReturn(expectedOpenKey)

        mvc.perform(delete("/api/keys/" + openKey.value)
                .header(AUTHORIZATION, openKey.value))

                .andExpect(status().isOk)
                .andExpect(content().json(objectMapper.writeValueAsString(OpenKeyDto(expectedOpenKey)), true))
    }

}