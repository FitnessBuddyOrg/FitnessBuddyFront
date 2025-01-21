package com.project.fitnessbuddy.api.user

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserDTOTest {

    private lateinit var userDTO: UserDTO

    @BeforeEach
    fun setUp() {
        userDTO = UserDTO(1, "John Doe", "john.doe@example.com", "user", "google")
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getId() {
        assertEquals(1, userDTO.id)
    }

    @Test
    fun getName() {
        assertEquals("John Doe", userDTO.name)
    }

    @Test
    fun getEmail() {
        assertEquals("john.doe@example.com", userDTO.email)
    }

    @Test
    fun getRole() {
        assertEquals("user", userDTO.role)
    }

    @Test
    fun getProvider() {
        assertEquals("google", userDTO.provider)
    }

    @Test
    operator fun component1() {
        assertEquals(1, userDTO.component1())
    }

    @Test
    operator fun component2() {
        assertEquals("John Doe", userDTO.component2())
    }

    @Test
    operator fun component3() {
        assertEquals("john.doe@example.com", userDTO.component3())
    }

    @Test
    operator fun component4() {
        assertEquals("user", userDTO.component4())
    }

    @Test
    operator fun component5() {
        assertEquals("google", userDTO.component5())
    }

    @Test
    fun copy() {
        val copy = userDTO.copy()
        assertEquals(userDTO, copy)
    }

    @Test
    fun testToString() {
        val expectedString = "UserDTO(id=1, name=John Doe, email=john.doe@example.com, role=user, provider=google)"
        assertEquals(expectedString, userDTO.toString())
    }

    @Test
    fun testHashCode() {
        val expectedHashCode = userDTO.hashCode()
        assertEquals(expectedHashCode, userDTO.hashCode())
    }

    @Test
    fun testEquals() {
        val anotherDTO = UserDTO(1, "John Doe", "john.doe@example.com", "user", "google")
        assertTrue(userDTO == anotherDTO)
    }
}