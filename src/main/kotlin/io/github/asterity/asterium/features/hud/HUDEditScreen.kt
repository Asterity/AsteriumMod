package io.github.asterity.asterium.features.hud

import com.mojang.blaze3d.systems.RenderSystem
import io.github.asterity.asterium.Asterium
import io.github.asterity.asterium.features.hud.position.AbsoluteScreenPosition
import io.github.asterity.asterium.features.hud.position.RelativeScreenPosition
import io.github.asterity.asterium.features.hud.position.ScreenArea
import io.github.asterity.asterium.features.hud.position.ScreenPosition
import io.github.asterity.asterium.utils.DrawUtil
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW


class HUDEditScreen(val features: List<DraggableFeature>) : Screen(Text.literal("HUD Edit Screen")) {

    var selectingStart: AbsoluteScreenPosition? = null
    val selected = arrayListOf<DraggableFeature>()
    // TODO: better variable name
    var isFeatureDragging = false
    var resizing: DraggableFeature? = null

    override fun shouldCloseOnEsc(): Boolean {
        return selected.isEmpty()
    }

    override fun init() {
        for (feature in features) {
            Asterium.logger.info("${feature.name}: ${feature.position.x}, ${feature.position.y} scale: ${feature.scale}, visible: ${feature.visible}")
        }
    }

    private var preMousePos: AbsoluteScreenPosition? = null
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val res = Asterium.mc.window
        context.matrices.scale(1f, 1f, 1f)
        RenderSystem.enableBlend()

        // draw background
        DrawUtil.drawRect2D(context, 0.0, 0.0, res.scaledWidth.toDouble(), res.scaledHeight.toDouble(), 0x80000000)

        val selectingArea = selectingStart?.let { ScreenArea(it, AbsoluteScreenPosition(mouseX.toDouble(), mouseY.toDouble())) }
        if (selectingArea != null) {
            DrawUtil.drawRect2D(context, selectingArea.start.x, selectingArea.start.y, selectingArea.end.x, selectingArea.end.y, 0x80DAA520)
            DrawUtil.drawHollowRect2D(context, selectingArea.start.x, selectingArea.start.y, selectingArea.end.x, selectingArea.end.y, 1.5f, 0xFFDAA520)
        }

        for (feature in features) {
            // detect selecting
            if (selectingArea != null) {
                if (isFeatureInArea(feature, selectingArea)) {
                    if (!selected.contains(feature)) selected.add(feature)
                } else {
                    selected.remove(feature)
                }
            }

            val isSelected = selected.contains(feature)

            val borderColor = if (isSelected) 0xFFDAA520 else 0xFFFFFFFF

            if (isSelected && isFeatureDragging) {
                // this if statement may be needless because dragging is always null before calling onMouseClicked.
                if (preMousePos != null) {
                    moveFeature(feature, mouseX.toDouble() - preMousePos!!.x, mouseY.toDouble() - preMousePos!!.y)
                }
            }
            context.matrices.push()
            context.matrices.translate(feature.position.x, feature.position.y, 0.0)
            context.matrices.scale(feature.scale, feature.scale, 1.0f)
            feature.renderDummy(context)
            // TODO: fix hollow rect scale and position
            DrawUtil.drawHollowRect2D(context, 0.0, 0.0, feature.width, feature.height, 1f, borderColor)
            context.matrices.pop()
        }
        context.matrices.pop()
        if (isFeatureDragging) preMousePos = AbsoluteScreenPosition(mouseX.toDouble(), mouseY.toDouble())
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> {
                if (selected.isNotEmpty()) {
                    selected.clear()
                    return true
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private var lastClickedPos: AbsoluteScreenPosition? = null
    private var lastClickedTime: Long = 0

    override fun mouseClicked(x: Double, y: Double, button: Int): Boolean {
        val feature = getFeatureWithPos(x, y)
        if (feature != null) {
            isFeatureDragging = true
            val isCtrlDown = hasControlDown()
            if (!selected.contains(feature)) {
                if (!isCtrlDown) selected.clear()
                selected.add(feature)
            } else {
                if (isCtrlDown) {
                    selected.remove(feature)
                } else {
                    lastClickedPos = AbsoluteScreenPosition(x, y)
                    lastClickedTime = System.currentTimeMillis()
                }
            }
        } else {
            selected.clear()
            selectingStart = AbsoluteScreenPosition(x, y)
        }

        preMousePos = AbsoluteScreenPosition(x, y)

        return true
    }

    override fun mouseReleased(x: Double, y: Double, button: Int): Boolean {
        isFeatureDragging = false
        selectingStart = null

        // 最後にクリックした地点と変わっていなく、時間があまり経っていない場合は、シングルクリックをしたとみなし、選択中のHUDをクリックしたHUDに変更する
        if (lastClickedPos != null) {
            if (lastClickedPos!!.x == x && lastClickedPos!!.y == y && System.currentTimeMillis() - lastClickedTime < 500) {
                lastClickedTime = 0
                val feature = getFeatureWithPos(x, y)
                if (feature != null) {
                    selected.clear()
                    selected.add(feature)
                }
            }
        }
        return true
    }

    override fun close() {
        for (feature in features) {
            feature.save()
        }
        super.close()
    }

    override fun shouldPause(): Boolean = true

    private fun getFeatureWithPos(x: Double, y: Double): DraggableFeature? {
        val matched = features.filter { isFeatureAtPos(it, x, y) }
        return matched.minByOrNull { feature -> feature.width * feature.scale * feature.height * feature.scale }
    }

    private fun isFeatureAtPos(feature: DraggableFeature, x: Double, y: Double): Boolean {
        val ex = feature.position.x + feature.scale * feature.width
        val ey = feature.position.y + feature.scale * feature.height
        return ScreenArea(feature.position, AbsoluteScreenPosition(ex, ey)).containsPos(AbsoluteScreenPosition(x, y))
    }

    private fun isFeatureInArea(feature: DraggableFeature, area: ScreenArea): Boolean {
        val ex = feature.position.x + feature.scale * feature.width
        val ey = feature.position.y + feature.scale * feature.height
        return ScreenArea(feature.position, AbsoluteScreenPosition(ex, ey)).overlapsArea(area)
    }

    private fun moveFeature(feature: DraggableFeature, offsetX: Double, offsetY: Double) {

        val movedX = feature.position.x + offsetX
        val movedY = feature.position.y + offsetY

        val newPosition: ScreenPosition = if (feature.position is RelativeScreenPosition) {
            val window = Asterium.mc.window
            RelativeScreenPosition((movedX / window.scaledWidth), movedY / window.scaledHeight)
        } else {
            AbsoluteScreenPosition(movedX, movedY)
        }

        feature.position = newPosition
        feature.position = feature.adjustBounds()
    }

}