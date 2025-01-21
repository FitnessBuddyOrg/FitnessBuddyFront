package com.project.fitnessbuddy.database.entity.enums

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LanguageTest {



    @Test
    fun testEnumValues() {
        val expectedLanguages = listOf(
            Language.ENGLISH,
            Language.FRENCH,
            Language.RUSSIAN,
            Language.CUSTOM
        )
        assertEquals(expectedLanguages, Language.entries)
    }

    @Test
    fun testToString() {
        assertEquals("English", Language.ENGLISH.toString())
        assertEquals("French", Language.FRENCH.toString())
        assertEquals("Russian", Language.RUSSIAN.toString())
        assertEquals("Custom", Language.CUSTOM.toString())
    }


    @Test
    fun testLocaleString() {
        assertEquals("gb", Language.ENGLISH.localeString)
        assertEquals("fr", Language.FRENCH.localeString)
        assertEquals("ru", Language.RUSSIAN.localeString)
        assertEquals("custom", Language.CUSTOM.localeString)
    }

    @Test
    fun testIsCustom() {
        assertFalse(Language.ENGLISH.isCustom)
        assertFalse(Language.FRENCH.isCustom)
        assertFalse(Language.RUSSIAN.isCustom)
        assertTrue(Language.CUSTOM.isCustom)
    }

    @Test
    fun testGetLanguage() {
        assertEquals(Language.ENGLISH, Language.getLanguage("ENGLISH"))
        assertEquals(Language.FRENCH, Language.getLanguage("FRENCH"))
        assertEquals(Language.RUSSIAN, Language.getLanguage("RUSSIAN"))
        assertEquals(Language.CUSTOM, Language.getLanguage("CUSTOM"))
        assertEquals(Language.ENGLISH, Language.getLanguage("UNKNOWN"))
    }
}
