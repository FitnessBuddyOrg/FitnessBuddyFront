package com.project.fitnessbuddy.api.user

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TokenResponseDTOTest {

    private lateinit var tokenResponseDTO: TokenResponseDTO

    @BeforeEach
    fun setUp() {
        tokenResponseDTO = TokenResponseDTO("sample_token")
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getToken() {
        assertEquals("sample_token", tokenResponseDTO.token)
    }

    @Test
    operator fun component1() {
        assertEquals("sample_token", tokenResponseDTO.component1())
    }

    @Test
    fun copy() {
        val copy = tokenResponseDTO.copy()
        assertEquals(tokenResponseDTO, copy)
    }

    @Test
    fun testToString() {
        val expectedString = "TokenResponseDTO(token=sample_token)"
        assertEquals(expectedString, tokenResponseDTO.toString())
    }

    @Test
    fun testHashCode() {
        val expectedHashCode = tokenResponseDTO.hashCode()
        assertEquals(expectedHashCode, tokenResponseDTO.hashCode())
    }

    @Test
    fun testEquals() {
        val anotherTokenResponseDTO = TokenResponseDTO("sample_token")
        assertTrue(tokenResponseDTO == anotherTokenResponseDTO)
    }
}
