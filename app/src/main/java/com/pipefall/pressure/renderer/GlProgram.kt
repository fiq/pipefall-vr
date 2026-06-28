package com.pipefall.pressure.renderer

import android.opengl.GLES32

class GlProgram(
    vertexShaderSource: String,
    fragmentShaderSource: String,
) {
    val id: Int

    init {
        val vertexShader = compileShader(GLES32.GL_VERTEX_SHADER, vertexShaderSource)
        val fragmentShader = compileShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderSource)

        id = GLES32.glCreateProgram()
        GLES32.glAttachShader(id, vertexShader)
        GLES32.glAttachShader(id, fragmentShader)
        GLES32.glLinkProgram(id)

        val linkStatus = IntArray(1)
        GLES32.glGetProgramiv(id, GLES32.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val log = GLES32.glGetProgramInfoLog(id)
            GLES32.glDeleteProgram(id)
            GLES32.glDeleteShader(vertexShader)
            GLES32.glDeleteShader(fragmentShader)
            error("Program link failed: $log")
        }

        GLES32.glDeleteShader(vertexShader)
        GLES32.glDeleteShader(fragmentShader)
    }

    fun use() {
        GLES32.glUseProgram(id)
    }

    fun attributeLocation(name: String): Int =
        GLES32.glGetAttribLocation(id, name)

    fun uniformLocation(name: String): Int =
        GLES32.glGetUniformLocation(id, name)

    private fun compileShader(shaderType: Int, shaderSource: String): Int {
        val shader = GLES32.glCreateShader(shaderType)
        GLES32.glShaderSource(shader, shaderSource)
        GLES32.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val log = GLES32.glGetShaderInfoLog(shader)
            GLES32.glDeleteShader(shader)
            error("Shader compile failed: $log")
        }

        return shader
    }
}
