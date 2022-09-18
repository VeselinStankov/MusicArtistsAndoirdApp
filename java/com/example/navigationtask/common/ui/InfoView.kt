package com.example.navigationtask.common.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.example.navigationtask.R
import com.example.navigationtask.databinding.InfoViewBinding


class InfoView : LinearLayout {

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

    private val binding = InfoViewBinding.inflate(LayoutInflater.from(context), this)

    private fun init(attributes: AttributeSet?) {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attributes, R.styleable.InfoView)

        val topLabelText = typedArray.getString(R.styleable.InfoView_topLabel)
        val middleLabelText = typedArray.getString(R.styleable.InfoView_middleLabel)
        val bottomLabelText = typedArray.getString(R.styleable.InfoView_bottomLabel)
        val middleIcon = typedArray.getDrawable(R.styleable.InfoView_middleIcon)
        val hasSeparator = typedArray.getBoolean(R.styleable.InfoView_isSeparatorVisible, false)

        setTopLabelText(topLabelText)
        setBottomLabelText(bottomLabelText)

        if (middleLabelText == null) {
            binding.imgMiddleIcon.isVisible = true
            binding.txtMiddleLabel.isVisible = false
            setMiddleLabelIcon(middleIcon)

        } else {
            binding.imgMiddleIcon.isVisible = false
            binding.txtMiddleLabel.isVisible = true
            setMiddleLabelText(middleLabelText)
        }
        binding.viewSeparator.isVisible = hasSeparator
        typedArray.recycle()
    }

    fun setTopLabelText(text: String?) {
        binding.txtTopLabel.text = text
    }

    fun setMiddleLabelText(text: String?) {
        binding.txtMiddleLabel.text = text
    }

    fun setMiddleLabelIcon(image: Drawable?) {
        binding.imgMiddleIcon.setImageDrawable(image)
    }

    fun setBottomLabelText(text: String?) {
        binding.txtBottomLabel.text = text
    }
}