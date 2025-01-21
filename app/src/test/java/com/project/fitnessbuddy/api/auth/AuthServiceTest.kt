package com.project.fitnessbuddy.api.auth

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthServiceTest {

    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = mockk()
    }

    @Test
    fun `test login API`() = runBlocking {
        val request = LoginRequest(email = "user@example.com", password = "password123")
        val expectedResponse = UserResponse(
            accessToken = "sample_access_token",
            email = "user@example.com",
            id = 123L
        )

        coEvery { authService.login(request) } returns expectedResponse

        val response = authService.login(request)
        assertEquals(expectedResponse, response)
        assertEquals("sample_access_token", response.accessToken)
        assertEquals("user@example.com", response.email)
        assertEquals(123L, response.id)
    }

    @Test
    fun `test register API`() = runBlocking {
        val request = RegisterRequest(
            email = "user@example.com",
            password = "password123",
            confirmPassword = "password123"
        )
        val expectedResponse = UserResponse(
            accessToken = "sample_access_token",
            email = "user@example.com",
            id = 123L
        )

        coEvery { authService.register(request) } returns expectedResponse

        val response = authService.register(request)
        assertEquals(expectedResponse, response)
    }
}
