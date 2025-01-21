package com.project.fitnessbuddy.database.entity.enums

import com.project.fitnessbuddy.R
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CategoryTest {

    @Test
    fun testEnumValues() {
        val expectedCategories = listOf(
            Category.ARMS,
            Category.BACK,
            Category.CHEST,
            Category.LEGS,
            Category.SHOULDERS,
            Category.ABS
        )
        assertEquals(expectedCategories, Category.entries)
    }

    @Test
    fun testToString() {
        assertEquals("Arms", Category.ARMS.toString())
        assertEquals("Back", Category.BACK.toString())
        assertEquals("Chest", Category.CHEST.toString())
        assertEquals("Legs", Category.LEGS.toString())
        assertEquals("Shoulders", Category.SHOULDERS.toString())
        assertEquals("Abs", Category.ABS.toString())
    }

    @Test
    fun testResourceId() {
        assertEquals(R.string.arms, Category.ARMS.resourceId)
        assertEquals(R.string.back, Category.BACK.resourceId)
        assertEquals(R.string.chest, Category.CHEST.resourceId)
        assertEquals(R.string.legs, Category.LEGS.resourceId)
        assertEquals(R.string.shoulders, Category.SHOULDERS.resourceId)
        assertEquals(R.string.abs, Category.ABS.resourceId)
    }
}
