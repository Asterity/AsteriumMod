package io.github.asterity.asterium.features.hud

import io.github.asterity.asterium.Asterium
import io.github.asterity.asterium.features.hud.position.AbsoluteScreenPosition
import io.github.asterity.asterium.features.hud.position.ScreenPosition
import kotlin.math.max

abstract class DraggableFeature : HUDFeature() {
    open val defaultPosition: ScreenPosition = AbsoluteScreenPosition(0.0, 0.0)
    open val defaultScale: Float = 1f

    var scale: Float
        get() {
            if (!config.has("scale")) {
                return defaultScale
            }
            return config.getValue("scale").asFloat
        }
        set(value) {
            config.set("scale", value)
        }

    var position: ScreenPosition
        get() {
            if (!config.has("position")) {
                return defaultPosition
            }
            val jsonData = config.getValue("position").asJsonObject
            return ScreenPosition.getFromJSON(jsonData)
        }
        set(value) {
            val jsonData = value.toJSON()
            config.set("position", jsonData)
        }

    /**
     * This method does not affect original position.
     */
    fun adjustBounds(): AbsoluteScreenPosition {
        val window = Asterium.mc.window
        val screenWidth = window.scaledWidth
        val screenHeight = window.scaledHeight
        val absoluteX =
            0.0.coerceAtLeast(position.x.coerceAtMost(max(screenWidth - width * scale, 0.0)))
        val absoluteY =
            0.0.coerceAtLeast(position.y.coerceAtMost(max(screenHeight - height * scale, 0.0)))
        return AbsoluteScreenPosition(absoluteX, absoluteY)
    }
}