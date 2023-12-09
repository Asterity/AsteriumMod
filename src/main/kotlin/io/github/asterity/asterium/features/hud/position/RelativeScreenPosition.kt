package io.github.asterity.asterium.features.hud.position

import com.google.gson.JsonObject
import io.github.asterity.asterium.Asterium

class RelativeScreenPosition(val relativeX: Double, val relativeY: Double) : ScreenPosition() {


    override val x: Double
        get() = Asterium.mc.window.scaledWidth * relativeX
    override val y: Double
        get() = Asterium.mc.window.scaledHeight * relativeY

    override fun toJSON(): JsonObject {
        val json = JsonObject()
        json.addProperty("type", "relative")
        json.addProperty("relativeX", relativeX)
        json.addProperty("relativeY", relativeY)
        return json
    }
}