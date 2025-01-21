package com.project.fitnessbuddy.api.auth

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AuthDataClassesTest {

    @Test
    fun testLoginRequest() {
        val loginRequest = LoginRequest(email = "user@example.com", password = "password123")
        assertEquals("user@example.com", loginRequest.email)
        assertEquals("password123", loginRequest.password)
    }

    @Test
    fun testRegisterRequest() {
        val registerRequest = RegisterRequest(
            email = "user@example.com",
            password = "password123",
            confirmPassword = "password123"
        )
        assertEquals("user@example.com", registerRequest.email)
        assertEquals("password123", registerRequest.password)
        assertEquals("password123", registerRequest.confirmPassword)
    }

    @Test
    fun testGoogleLoginRequest() {
        val googleLoginRequest = GoogleLoginRequest(
            idToken = "sample_id_token",
            profilePictureUrl = "https://example.com/profile.jpg"
        )
        assertEquals("sample_id_token", googleLoginRequest.idToken)
        assertEquals("https://example.com/profile.jpg", googleLoginRequest.profilePictureUrl)
    }

    @Test
    fun testUserResponse() {
        val userResponse = UserResponse(
            accessToken = "sample_access_token",
            email = "user@example.com",
            id = 123L
        )
        assertEquals("sample_access_token", userResponse.accessToken)
        assertEquals("user@example.com", userResponse.email)
        assertEquals(123L, userResponse.id)
    }

    @Test
    fun testGitHubTokenRequestDTO() {
        val gitHubTokenRequestDTO = GitHubTokenRequestDTO(code = "sample_code")
        assertEquals("sample_code", gitHubTokenRequestDTO.code)
    }
}
