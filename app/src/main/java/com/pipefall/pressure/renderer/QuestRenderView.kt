package com.pipefall.pressure.renderer

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.pipefall.pressure.QuestShellView

class QuestRenderView(context: Context) : FrameLayout(context) {
    private val surfaceView = GLSurfaceView(context)
    private val shellView = QuestShellView(context)

    val rendererController = InputController()
    val renderer = OpenXRRenderer()

    init {
        surfaceView.setEGLContextClientVersion(3)
        surfaceView.preserveEGLContextOnPause = true
        surfaceView.setRenderer(renderer)
        surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        addView(
            surfaceView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )

        addView(
            shellView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    fun onResume() {
        surfaceView.onResume()
    }

    fun onPause() {
        surfaceView.onPause()
    }
}
