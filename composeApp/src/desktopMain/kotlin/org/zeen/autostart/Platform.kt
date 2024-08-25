package org.zeen.autostart

import java.awt.Desktop
import java.io.File

class JVMPlatform {
    val name: String = "Java ${System.getProperty("java.version")}"

    /**
     * 启动应用
     * @return true，正常启动，false启动发生异常
     */
    fun runApplication(path: String): Boolean {
        if (path.isNotEmpty()) {
            try {
                Desktop.getDesktop().open(File(path))
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }
}

fun getPlatform() = JVMPlatform()