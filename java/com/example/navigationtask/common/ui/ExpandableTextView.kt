package com.example.navigationtask.common.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.navigationtask.R

class ExpandableTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes) {
        init(attributes)
    }

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    ) {
        init(attributes)
    }

    private var isTextExpanded = false

    private fun init(attributes: AttributeSet?) {

        val typedArray: TypedArray =
            context.obtainStyledAttributes(attributes, R.styleable.ExpandableTextView)

        val linesInCollapsedMode =
            typedArray.getInt(R.styleable.ExpandableTextView_linesInCollapsedMode, 2)

        this.setOnClickListener {
            if (isTextExpanded) {
                this.maxLines = linesInCollapsedMode
                isTextExpanded = false
            } else {
                this.maxLines = Integer.MAX_VALUE
                isTextExpanded = true
            }
        }
        typedArray.recycle()
    }
}