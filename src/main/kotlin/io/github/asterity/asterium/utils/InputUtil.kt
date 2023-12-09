package io.github.asterity.asterium.utils

import io.github.asterity.asterium.Asterium
import org.lwjgl.glfw.GLFW

object InputUtil {
    fun getKeyState(keyCode: Int): Int {
        return GLFW.glfwGetKey(Asterium.mc.window.handle, keyCode)
    }

    fun isKeyDown(keyCode: Int): Boolean {
        return getKeyState(keyCode) != GLFW.GLFW_RELEASE
    }
}