package com.project.fitnessbuddy.api.user

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProfilePictureDTOTest {

    private lateinit var profilePictureDTO: ProfilePictureDTO

    @BeforeEach
    fun setUp() {
        profilePictureDTO = ProfilePictureDTO("https://example.com/profile.jpg")
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getUrl() {
        assertEquals("https://example.com/profile.jpg", profilePictureDTO.url)
    }

    @Test
    operator fun component1() {
        assertEquals("https://example.com/profile.jpg", profilePictureDTO.component1())
    }

    @Test
    fun copy() {
        val copy = profilePictureDTO.copy()
        assertEquals(profilePictureDTO, copy)
    }

    @Test
    fun testToString() {
        val expectedString = "ProfilePictureDTO(url=https://example.com/profile.jpg)"
        assertEquals(expectedString, profilePictureDTO.toString())
    }

    @Test
    fun testHashCode() {
        val expectedHashCode = profilePictureDTO.hashCode()
        assertEquals(expectedHashCode, profilePictureDTO.hashCode())
    }

    @Test
    fun testEquals() {
        val anotherProfilePictureDTO = ProfilePictureDTO("https://example.com/profile.jpg")
        assertTrue(profilePictureDTO == anotherProfilePictureDTO)
    }
}
