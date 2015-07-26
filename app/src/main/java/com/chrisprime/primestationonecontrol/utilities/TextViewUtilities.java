package com.chrisprime.primestationonecontrol.utilities;

import android.text.Editable;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by cpaian on 7/26/15.
 */
public class TextViewUtilities {
    public static final int MAX_OUTPUT_LINES = 50;
    public static final boolean AUTO_SCROLL_BOTTOM = true;

    //call to add line(s) to TextView
//This should work if either lineText contains multiple
//linefeeds or none at all
    public static void addLinesToTextView(String lineText, TextView textView, ScrollView scrollView) {
        textView.append(lineText);
        removeOldLinesFromTextView(textView);
        if (AUTO_SCROLL_BOTTOM)
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    // remove leading lines from beginning of the output view
    public static void removeOldLinesFromTextView(TextView textView) {
        int linesToRemove = textView.getLineCount() - MAX_OUTPUT_LINES;
        if (linesToRemove > 0) {
            for (int i = 0; i < linesToRemove; i++) {
                Editable text = textView.getEditableText();
                int lineStart = textView.getLayout().getLineStart(0);
                int lineEnd = textView.getLayout().getLineEnd(0);
                text.delete(lineStart, lineEnd);
            }
        }
    }
}
