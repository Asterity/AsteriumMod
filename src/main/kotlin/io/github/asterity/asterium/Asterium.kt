package io.github.asterity.asterium

import io.github.asterity.asterium.features.hud.HUDEditScreen
import io.github.asterity.asterium.features.hud.items.TestHud
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW
import java.util.logging.Logger

@Suppress("unused")
object Asterium : ClientModInitializer {
    val MOD_ID = "asterium"
    val MOD_NAME = "Asterium"

    val mc by lazy {
        MinecraftClient.getInstance()
    }

    private val settingsKeyBinding: KeyBinding = KeyBinding("key.asterium.settings", GLFW.GLFW_KEY_R, "key.categories.asterium")

    // TODO: better logger
    val logger = Logger.getLogger(MOD_NAME)
    override fun onInitializeClient() {
        logger.info("Initializing $MOD_NAME")

        KeyBindingHelper.registerKeyBinding(settingsKeyBinding)
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            if (settingsKeyBinding.wasPressed()) {
                // TODO: add HUDs
                mc.setScreen(HUDEditScreen(listOf(TestHud)))
            }
        })
    }
}