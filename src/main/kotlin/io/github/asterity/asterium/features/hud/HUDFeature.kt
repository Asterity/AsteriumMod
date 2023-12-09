package io.github.asterity.asterium.features.hud

import com.google.gson.JsonObject
import io.github.asterity.asterium.Asterium
import io.github.asterity.asterium.utils.ConfigFile
import net.minecraft.client.gui.DrawContext
import java.io.File

abstract class HUDFeature {
    abstract val name: String
    abstract val id: String

    abstract val height: Double
    abstract val width: Double

    open val defaultVisibility: Boolean = true

    open fun init() {}
    abstract fun render(context: DrawContext)
    open fun renderDummy(context: DrawContext) = render(context)

    val config by lazy {
        ConfigFile(File("$id.json"), JsonObject())
    }

    var visible: Boolean
        get() {
            if (!config.has("visible")) {
                return defaultVisibility
            }
            return config.getValue("visible").asBoolean
        }
        set(value) {
            config.set("visible", value)
        }

    /**
     * Save scale, position, and state of the HUD.
     */
    fun save() {
        config.save(compact = false)
    }

    companion object {
        val font by lazy {
            Asterium.mc.textRenderer
        }

        fun getLineOffset(lineNum: Int): Int {
            return (font.fontHeight + 1) * lineNum
        }
    }

}