package com.pipefall.pressure

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class QuestShellView(context: Context) : LinearLayout(context) {
    private val titleView = TextView(context)
    private val statusView = TextView(context)

    init {
        setBackgroundColor(Color.TRANSPARENT)
        orientation = VERTICAL
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        isFocusable = true
        isFocusableInTouchMode = true
        setPadding(dp(24), dp(24), dp(24), dp(24))

        titleView.apply {
            setTextColor(Color.rgb(224, 228, 230))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            gravity = Gravity.CENTER
            text = context.getString(R.string.shell_title)
        }

        statusView.apply {
            setTextColor(Color.rgb(180, 188, 192))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            gravity = Gravity.CENTER
            text = context.getString(R.string.shell_status)
        }

        addView(
            titleView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                bottomMargin = dp(8)
            },
        )

        addView(
            statusView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )
    }

    fun setStatus(text: CharSequence) {
        statusView.text = text
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
