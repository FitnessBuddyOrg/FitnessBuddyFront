package com.project.fitnessbuddy.api.user

import com.project.fitnessbuddy.database.entity.enums.Category
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ShareExerciseDTOTest {

    private lateinit var shareExerciseDTO: ShareExerciseDTO

    @BeforeEach
    fun setUp() {
        shareExerciseDTO = ShareExerciseDTO(
            name = "Push Ups",
            instructions = "Do 10 reps.",
            videoLink = "https://example.com/video",
            category = Category.CHEST
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getName() {
        assertEquals("Push Ups", shareExerciseDTO.name)
    }

    @Test
    fun getInstructions() {
        assertEquals("Do 10 reps.", shareExerciseDTO.instructions)
    }

    @Test
    fun getVideoLink() {
        assertEquals("https://example.com/video", shareExerciseDTO.videoLink)
    }

    @Test
    fun getCategory() {
        assertEquals(Category.CHEST, shareExerciseDTO.category)
    }

    @Test
    operator fun component1() {
        assertEquals("Push Ups", shareExerciseDTO.component1())
    }

    @Test
    operator fun component2() {
        assertEquals("Do 10 reps.", shareExerciseDTO.component2())
    }

    @Test
    operator fun component3() {
        assertEquals("https://example.com/video", shareExerciseDTO.component3())
    }

    @Test
    operator fun component4() {
        assertEquals(Category.CHEST, shareExerciseDTO.component4())
    }

    @Test
    fun copy() {
        val copy = shareExerciseDTO.copy()
        assertEquals(shareExerciseDTO, copy)
    }


    @Test
    fun testHashCode() {
        val expectedHashCode = shareExerciseDTO.hashCode()
        assertEquals(expectedHashCode, shareExerciseDTO.hashCode())
    }

    @Test
    fun testEquals() {
        val anotherShareExerciseDTO = ShareExerciseDTO(
            name = "Push Ups",
            instructions = "Do 10 reps.",
            videoLink = "https://example.com/video",
            category = Category.CHEST
        )
        assertTrue(shareExerciseDTO == anotherShareExerciseDTO)
    }
}
