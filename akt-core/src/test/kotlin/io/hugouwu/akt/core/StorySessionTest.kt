package io.hugouwu.akt.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StorySessionTest {
    private val story: StoryDefinition =
        visualNovel("test story") {
            character("mira", "mira")
            scene("platform") {
                say("mira", "first line")
                say("mira", "second line")
                choice("leave", "leave", "leave")
                choice("stay", "stay", "stay")
            }
            scene("leave") { narrate("the platform emptied.") }
            scene("stay") { narrate("the station clock stopped.") }
            start("platform")
        }

    @Test
    fun advancesThroughDialogueAndChoices() {
        val session = StorySession(story)
        assertEquals("first line", session.frame().text)

        session.advance()
        assertEquals("second line", session.frame().text)

        session.advance()
        assertTrue(session.frame().isAwaitingChoice)
        assertEquals(2, session.frame().choices.size)

        session.choose("leave")
        assertEquals("leave", session.frame().sceneId)
        assertEquals("the platform emptied.", session.frame().text)
    }

    @Test
    fun rejectsUnavailableChoice() {
        val session = StorySession(story)
        session.advance()
        session.advance()

        assertThrows<IllegalArgumentException> { session.choose("missing") }
    }
}
