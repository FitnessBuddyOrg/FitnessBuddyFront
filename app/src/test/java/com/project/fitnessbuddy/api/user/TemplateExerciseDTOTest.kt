package com.project.fitnessbuddy.api.user

import com.project.fitnessbuddy.database.entity.enums.Category
import com.project.fitnessbuddy.database.entity.enums.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TemplateExerciseDTOTest {

    private lateinit var templateExerciseDTO: TemplateExerciseDTO

    @BeforeEach
    fun setUp() {
        templateExerciseDTO = TemplateExerciseDTO(
            name = "Push Ups",
            instructions = "Do 10 reps.",
            videoLink = "https://example.com/video",
            category = Category.CHEST,
            language = Language.ENGLISH
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun getName() {
        assertEquals("Push Ups", templateExerciseDTO.name)
    }

    @Test
    fun getInstructions() {
        assertEquals("Do 10 reps.", templateExerciseDTO.instructions)
    }

    @Test
    fun getVideoLink() {
        assertEquals("https://example.com/video", templateExerciseDTO.videoLink)
    }

    @Test
    fun getCategory() {
        assertEquals(Category.CHEST, templateExerciseDTO.category)
    }

    @Test
    fun getLanguage() {
        assertEquals(Language.ENGLISH, templateExerciseDTO.language)
    }

    @Test
    operator fun component1() {
        assertEquals("Push Ups", templateExerciseDTO.component1())
    }

    @Test
    operator fun component2() {
        assertEquals("Do 10 reps.", templateExerciseDTO.component2())
    }

    @Test
    operator fun component3() {
        assertEquals("https://example.com/video", templateExerciseDTO.component3())
    }

    @Test
    operator fun component4() {
        assertEquals(Category.CHEST, templateExerciseDTO.component4())
    }

    @Test
    operator fun component5() {
        assertEquals(Language.ENGLISH, templateExerciseDTO.component5())
    }

    @Test
    fun copy() {
        val copy = templateExerciseDTO.copy()
        assertEquals(templateExerciseDTO, copy)
    }


    @Test
    fun testHashCode() {
        val expectedHashCode = templateExerciseDTO.hashCode()
        assertEquals(expectedHashCode, templateExerciseDTO.hashCode())
    }

    @Test
    fun testEquals() {
        val anotherTemplateExerciseDTO = TemplateExerciseDTO(
            name = "Push Ups",
            instructions = "Do 10 reps.",
            videoLink = "https://example.com/video",
            category = Category.CHEST,
            language = Language.ENGLISH
        )
        assertTrue(templateExerciseDTO == anotherTemplateExerciseDTO)
    }
}
