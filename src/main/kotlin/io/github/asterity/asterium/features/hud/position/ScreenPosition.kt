package io.github.asterity.asterium.features.hud.position

import com.google.gson.JsonObject

abstract class ScreenPosition {
    abstract val x: Double
    abstract val y: Double

    abstract fun toJSON(): JsonObject

    companion object {
        fun getFromJSON(json: JsonObject): ScreenPosition {
            return when (json["type"].asString) {
                "absolute" -> {
                    val x = json["x"].asDouble
                    val y = json["y"].asDouble
                    AbsoluteScreenPosition(x, y)
                }
                "relative" -> {
                    val relativeX = json["relativeX"].asDouble
                    val relativeY = json["relativeY"].asDouble
                    RelativeScreenPosition(relativeX, relativeY)
                }
                else -> throw Exception("Position data corrupted.")
            }
        }
    }
}