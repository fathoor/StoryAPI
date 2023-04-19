package com.fathoor.storyapi.view.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.fathoor.storyapi.R
import com.google.android.material.textfield.TextInputEditText

class Email: TextInputEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val email = s.toString()
                error = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotEmpty()) {
                    resources.getString(R.string.error_email)
                } else {
                    null
                }
            }
        })
    }
}