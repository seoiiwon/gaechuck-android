package com.example.gaechuck.ui.menu

import android.content.Context
import android.util.AttributeSet
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import com.example.gaechuck.R


class ArrowToggleSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.spinnerStyle
) : AppCompatSpinner(context, attrs, defStyleAttr) {

    private val downDrawableRes = R.drawable.spinner_bg_arrow_down
    private val upDrawableRes   = R.drawable.spinner_bg_arrow_up

    init {
        background = ContextCompat.getDrawable(context, downDrawableRes)
    }

    override fun performClick(): Boolean {
        background = ContextCompat.getDrawable(context, upDrawableRes)
        return super.performClick()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)


        if (hasWindowFocus) {
            background = ContextCompat.getDrawable(context, downDrawableRes)
        }
    }
}