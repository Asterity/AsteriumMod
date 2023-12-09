package io.github.asterity.asterium.utils

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.util.math.ColorHelper
import org.joml.Matrix4f
import kotlin.math.sqrt


object DrawUtil {
    fun drawLine2D(context: DrawContext, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, color: Long) {
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()
        matrixStack.multiplyPositionMatrix(context.matrices.peek().positionMatrix)
        // TODO: better way to fix line position
        matrixStack.scale(1.00345f, 1.00345f, 1.0f)
        RenderSystem.applyModelViewMatrix()

        GlStateManager._depthMask(false)
        GlStateManager._disableCull()
        RenderSystem.setShader { GameRenderer.getRenderTypeLinesProgram() }
        val tessellator = RenderSystem.renderThreadTesselator()
        val bufferBuilder = tessellator.buffer

        RenderSystem.lineWidth(width)
        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)

        val normalVector = getNormalVector2D(x1, y1, x2, y2)
        bufferBuilder.vertex(x1, y1, 0.0).color(color.toInt()).normal(normalVector.first.toFloat(), normalVector.second.toFloat(), 0.0f).next()
        bufferBuilder.vertex(x2, y2, 0.0).color(color.toInt()).normal(normalVector.first.toFloat(), normalVector.second.toFloat(), 0.0f).next()

        tessellator.draw()
        RenderSystem.lineWidth(1.0f)
        GlStateManager._enableCull()
        GlStateManager._depthMask(true)

        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()
    }

    fun drawHollowRect2D(context: DrawContext, x1: Double, y1: Double, x2: Double, y2: Double, width: Float, color: Long) {
        drawLine2D(context, x1, y1, x2, y1, width, color)
        drawLine2D(context, x1, y2, x2, y2, width, color)
        drawLine2D(context, x1, y1, x1, y2, width, color)
        drawLine2D(context, x2, y1, x2, y2, width, color)
    }

    fun drawRect2D(context: DrawContext, x1: Double, y1: Double, x2: Double, y2: Double, color: Long) {
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()
        matrixStack.multiplyPositionMatrix(context.matrices.peek().positionMatrix)
        RenderSystem.applyModelViewMatrix()

        GlStateManager._depthMask(false)
        GlStateManager._disableCull()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        val tessellator = RenderSystem.renderThreadTesselator()
        val bufferBuilder = tessellator.buffer

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(x1, y2, 0.0).color(color.toInt()).next()
        bufferBuilder.vertex(x2, y2, 0.0).color(color.toInt()).next()
        bufferBuilder.vertex(x2, y1, 0.0).color(color.toInt()).next()
        bufferBuilder.vertex(x1, y1, 0.0).color(color.toInt()).next()

        tessellator.draw()
        GlStateManager._enableCull()
        GlStateManager._depthMask(true)

        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()
    }

    private fun getNormalVector2D(x1: Double, y1: Double, x2: Double, y2: Double): Pair<Double, Double> {
        val dx = x2 - x1
        val dy = y2 - y1
        val length = sqrt(dx * dx + dy * dy)
        if (length == 0.0) return Pair(0.0, 0.0)
        return Pair(dx / length, dy / length)
    }
}