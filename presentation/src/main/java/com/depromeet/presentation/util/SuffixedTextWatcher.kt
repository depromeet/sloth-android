package com.depromeet.presentation.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.math.BigInteger
import java.text.NumberFormat

abstract class SuffixedTextWatcher(
    private val editText: EditText,
    private val suffix: String
) : TextWatcher {
    var beforeText = ""
    var afterText = ""
    var startPos = 0
    var endPos = 0

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        beforeText = p0.toString()
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        afterText = p0.toString()
        startPos = p2
        endPos = p3
    }

    override fun afterTextChanged(e: Editable?) {
        if (e == null) return

        // 무한루프를 방지
        if (beforeText == afterText) return

        // suffix 가 수정된 경우 마지막 문자열을 suffix 로 만들어 주고 커서를 suffix 바로 앞으로 이동시킵니다
        if (!e.toString().endsWith(suffix, false)) {
            editText.setText(beforeText)

            if (beforeText.length < suffix.length) {
                // 문자를 지운 경우
                editText.setSelection(0)
            } else {
                // 문자가 삽입된 경우
                editText.setSelection(afterText.length - suffix.length)
            }
            return
        }

        // 빈값이 들어오면 0으로 리턴해줍니다
        val num = e.toString()
            .replace(",", "")
            .removeSuffix(suffix)
            .toBigIntegerOrNull()
            ?: kotlin.run {
                onNumberChanged(BigInteger.ZERO)
                return
            }

        // 정상적으로 텍스트를 세팅해주는 경우입니다
        if (beforeText != afterText) {
            // 숫자에 suffix 를 붙여주고
            val str = NumberFormat.getInstance().format(num).plus(suffix)
            editText.setText(str)

            // suffix 바로 앞으로 커서를 이동시켜줍니다
            val selectedPos = if (editText.text.length - suffix.length < 0) {
                0
            } else {
                editText.text.length - suffix.length
            }
            editText.setSelection(selectedPos)
        }

        // 마지막으로 콜백도 호출해줍니다, 현재는 정수형 입출력만 가능하도록 되어 있습니다
        // 만약 실수형의 데이터를 사용하고 싶다면 데이터 타입을 바꿔줘야 합니다, 유의하세요!
        onNumberChanged(num)
    }

    abstract fun onNumberChanged(number: BigInteger)

}