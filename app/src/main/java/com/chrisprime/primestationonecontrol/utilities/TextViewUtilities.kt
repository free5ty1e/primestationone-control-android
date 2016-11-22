package com.chrisprime.primestationonecontrol.utilities

import android.widget.ScrollView
import android.widget.TextView

/**
 * Created by cpaian on 7/26/15.
 */
object TextViewUtilities {
    val MAX_OUTPUT_LINES = 50
    val AUTO_SCROLL_BOTTOM = true

    //call to add line(s) to TextView
    //This should work if either lineText contains multiple
    //linefeeds or none at all
    fun addLinesToTextView(lineText: String, textView: TextView, scrollView: ScrollView) {
        textView.append(lineText)
        removeOldLinesFromTextView(textView)
        if (AUTO_SCROLL_BOTTOM)
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    // remove leading lines from beginning of the output view
    fun removeOldLinesFromTextView(textView: TextView) {
        val linesToRemove = textView.lineCount - MAX_OUTPUT_LINES
        if (linesToRemove > 0) {
            for (i in 0..linesToRemove - 1) {
                val text = textView.editableText
                val lineStart = textView.layout.getLineStart(0)
                val lineEnd = textView.layout.getLineEnd(0)
                text.delete(lineStart, lineEnd)
            }
        }
    }
}
