package io.github.asterity.asterium.features.hud.items

import io.github.asterity.asterium.features.hud.DraggableFeature
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

object TestHud : DraggableFeature() {
    override val name: String
        get() = "TestHud"
    override val id: String
        get() = "test_hud"
    override val height: Double
        get() = font.fontHeight.toDouble()
    override val width: Double
        get() = font.getWidth("TestHud").toDouble() + 1

    override fun render(context: DrawContext) {
        context.drawTextWithShadow(font, Text.literal("TestHud"), 0, 0, 0xFFFFFFFF.toInt())
    }

}