package com.project.fitnessbuddy.api.user

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CompletedRoutineDTOTest {

    private lateinit var completedRoutineDTO: CompletedRoutineDTO

    @BeforeEach
    fun setUp() {
        completedRoutineDTO = CompletedRoutineDTO(1, 1, "2023-10-10T10:15:30")
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getParsedCompletedTime() {
        val expectedTime = LocalDateTime.of(2023, 10, 10, 10, 15, 30)
        assertEquals(expectedTime, completedRoutineDTO.getParsedCompletedTime())
    }

    @Test
    fun getId() {
        assertEquals(1, completedRoutineDTO.id)
    }

    @Test
    fun getUserId() {
        assertEquals(1, completedRoutineDTO.userId)
    }

    @Test
    fun getCompletedTime() {
        assertEquals("2023-10-10T10:15:30", completedRoutineDTO.completedTime)
    }

    @Test
    operator fun component1() {
        assertEquals(1, completedRoutineDTO.component1())
    }

    @Test
    operator fun component2() {
        assertEquals(1, completedRoutineDTO.component2())
    }

    @Test
    operator fun component3() {
        assertEquals("2023-10-10T10:15:30", completedRoutineDTO.component3())
    }

    @Test
    fun copy() {
        val copy = completedRoutineDTO.copy()
        assertEquals(completedRoutineDTO, copy)
    }

    @Test
    fun testToString() {
        val expectedString = "CompletedRoutineDTO(id=1, userId=1, completedTime=2023-10-10T10:15:30)"
        assertEquals(expectedString, completedRoutineDTO.toString())
    }

    @Test
    fun testHashCode() {
        val expectedHashCode = completedRoutineDTO.hashCode()
        assertEquals(expectedHashCode, completedRoutineDTO.hashCode())
    }

    @Test
    fun testEquals() {
        val anotherDTO = CompletedRoutineDTO(1, 1, "2023-10-10T10:15:30")
        assertTrue(completedRoutineDTO == anotherDTO)
    }
}