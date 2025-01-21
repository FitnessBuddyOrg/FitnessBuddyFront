package com.project.fitnessbuddy.api.user

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import java.time.LocalDateTime

class AppOpenDTOTest {

    private lateinit var appOpenDTO: AppOpenDTO

    @BeforeEach
    fun setUp() {
        appOpenDTO = AppOpenDTO(1, 1, "2023-10-10T10:15:30")
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getParsedOpenTime() {
        val expectedTime = LocalDateTime.of(2023, 10, 10, 10, 15, 30)
        assertEquals(expectedTime, appOpenDTO.getParsedOpenTime())
    }

    @Test
    fun getId() {
        assertEquals(1, appOpenDTO.id)
    }

    @Test
    fun getUserId() {
        assertEquals(1, appOpenDTO.userId)
    }

    @Test
    fun getOpenTime() {
        assertEquals("2023-10-10T10:15:30", appOpenDTO.openTime)
    }

    @Test
    operator fun component1() {
        assertEquals(1, appOpenDTO.component1())
    }

    @Test
    operator fun component2() {
        assertEquals(1, appOpenDTO.component2())
    }

    @Test
    operator fun component3() {
        assertEquals("2023-10-10T10:15:30", appOpenDTO.component3())
    }

    @Test
    fun copy() {
        val copy = appOpenDTO.copy()
        assertEquals(appOpenDTO, copy)
    }

    @Test
    fun testToString() {
        val expectedString = "AppOpenDTO(id=1, userId=1, openTime=2023-10-10T10:15:30)"
        assertEquals(expectedString, appOpenDTO.toString())
    }

    @Test
    fun testHashCode() {
        val expectedHashCode = appOpenDTO.hashCode()
        assertEquals(expectedHashCode, appOpenDTO.hashCode())
    }

    @Test
    fun testEquals() {
        val anotherDTO = AppOpenDTO(1, 1, "2023-10-10T10:15:30")
        assertTrue(appOpenDTO == anotherDTO)
    }
}