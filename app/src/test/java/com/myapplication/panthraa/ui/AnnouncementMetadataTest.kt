package com.myapplication.panthraa.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class AnnouncementMetadataTest {
    @Test
    fun editedLabelIsHiddenUntilAnnouncementIsEdited() {
        assertNull(formatAnnouncementEditedTime(null))
        assertNull(formatAnnouncementEditedTime("   "))
    }

    @Test
    fun editedLabelUsesPhilippineTime() {
        val previousLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals(
                "(Edited Jul 10, 1:30 PM)",
                formatAnnouncementEditedTime("2026-07-10T05:30:00Z"),
            )
        } finally {
            Locale.setDefault(previousLocale)
        }
    }
}
