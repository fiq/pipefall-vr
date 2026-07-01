package com.pipefall.pressure

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.pipefall.pressure.renderer.QuestRenderView

class QuestActivity : Activity() {
    private lateinit var renderView: QuestRenderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        renderView = QuestRenderView(this)
        // setContentView must run before configureFullscreen so the window's
        // decor view is installed; otherwise window.insetsController throws an
        // NPE because the DecorView has not been created yet.
        setContentView(renderView)
        configureFullscreen()
    }

    override fun onResume() {
        super.onResume()
        configureFullscreen()
        renderView.onResume()
        renderView.requestFocus()
    }

    override fun onPause() {
        renderView.onPause()
        super.onPause()
    }

    @Suppress("DEPRECATION")
    private fun configureFullscreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
}
