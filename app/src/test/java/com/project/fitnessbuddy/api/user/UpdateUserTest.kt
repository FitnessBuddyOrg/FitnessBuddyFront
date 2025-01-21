package com.project.fitnessbuddy.api.user

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateUserTest {

    private lateinit var updateUser: UpdateUser

    @BeforeEach
    fun setUp() {
        updateUser = UpdateUser(1, "John Doe")
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getId() {
        assertEquals(1, updateUser.id)
    }

    @Test
    fun getName() {
        assertEquals("John Doe", updateUser.name)
    }

    @Test
    operator fun component1() {
        assertEquals(1, updateUser.component1())
    }

    @Test
    operator fun component2() {
        assertEquals("John Doe", updateUser.component2())
    }

    @Test
    fun copy() {
        val copy = updateUser.copy()
        assertEquals(updateUser, copy)
    }

    @Test
    fun testToString() {
        val expectedString = "UpdateUser(id=1, name=John Doe)"
        assertEquals(expectedString, updateUser.toString())
    }

    @Test
    fun testHashCode() {
        val expectedHashCode = updateUser.hashCode()
        assertEquals(expectedHashCode, updateUser.hashCode())
    }

    @Test
    fun testEquals() {
        val anotherUpdateUser = UpdateUser(1, "John Doe")
        assertTrue(updateUser == anotherUpdateUser)
    }
}
