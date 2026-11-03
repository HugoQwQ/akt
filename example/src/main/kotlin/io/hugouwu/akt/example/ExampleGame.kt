package io.hugouwu.akt.example

import io.hugouwu.akt.core.Color
import io.hugouwu.akt.core.StoryAction
import io.hugouwu.akt.core.Transition
import io.hugouwu.akt.core.actor
import io.hugouwu.akt.core.position
import io.hugouwu.akt.runtime.aktGame
import kotlin.time.Duration.Companion.milliseconds

public fun main() {
    aktGame("one more stop") {
            window {
                size(1280, 720)
                resizable(true)
                vsync(true)
            }

            story {
                character("mira", "mira", Color.hex("#e7a98f"))
                character("conductor", "the conductor", Color.hex("#b8c7d9"))

                scene("platform") {
                    background("backgrounds/platform-night")
                    show("mira", actor(position(0.7f, 0.18f)))
                    action(StoryAction.Emit("chapter-started"))
                    say("mira", "the last train left an hour ago.")
                    narrate("rain taps the empty timetable. a single lamp stays awake.")
                    say("mira", "so why is there still a light in the tunnel?")
                    choice(
                        id = "wait",
                        text = "wait for the dawn",
                        targetScene = "dawn",
                    )
                    choice(
                        id = "board",
                        text = "walk toward the light",
                        targetScene = "train",
                    )
                    transition(Transition.Fade(600.milliseconds))
                }

                scene("dawn") {
                    background("backgrounds/platform-dawn")
                    show("mira", actor(position(0.64f, 0.18f), scale = 1.02f))
                    narrate("the light in the tunnel fades before the sun arrives.")
                    say("mira", "some trains are only there when you stop waiting.")
                    say(
                        speakerId = "conductor",
                        text = "last call. this one is going home.",
                    )
                    transition(Transition.Dissolve(800.milliseconds))
                }

                scene("train") {
                    background("backgrounds/empty-train")
                    show("mira", actor(position(0.38f, 0.16f), mirrored = true))
                    narrate("the doors close around a carriage with no passengers.")
                    say("conductor", "you made it. we were beginning to think you would stay.")
                    say("mira", "where does this train go?")
                    say("conductor", "wherever you were trying to remember.")
                    transition(
                        Transition.Slide(io.hugouwu.akt.core.SlideDirection.LEFT, 700.milliseconds)
                    )
                }

                start("platform")
            }
        }
        .run()
}
