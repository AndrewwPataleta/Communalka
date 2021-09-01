package com.patstudio.communalka.common.utils

class MaskFormatter(private val pattern: String, private val splitter: Char? = null) {

    fun format(text: String): String {
        val patternArr = pattern.toCharArray()
        val textArr = text.toCharArray()
        var textI = 0
        for (patternI in patternArr.indices) {
            if (patternArr[patternI] == splitter) {
                continue
            }
            if (patternArr[patternI] == 'A' && textI < textArr.size) {
                patternArr[patternI] = textArr[textI]
            }
            textI++
        }
        return String(patternArr)
    }

}