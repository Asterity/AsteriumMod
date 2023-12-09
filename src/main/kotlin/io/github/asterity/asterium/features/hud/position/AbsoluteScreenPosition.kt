package io.github.asterity.asterium.features.hud.position

import com.google.gson.JsonObject

class AbsoluteScreenPosition(override var x: Double, override var y: Double) : ScreenPosition() {

    override fun toJSON(): JsonObject {
        val json = JsonObject()
        json.addProperty("type", "absolute")
        json.addProperty("x", x)
        json.addProperty("y", y)
        return json
    }

}