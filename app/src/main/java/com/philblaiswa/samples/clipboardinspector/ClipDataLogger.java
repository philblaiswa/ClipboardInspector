package com.philblaiswa.samples.clipboardinspector;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

public class ClipDataLogger {
    public static String getClipDescription(@Nullable ClipDescription clipDescription) {
        StringBuffer buf = new StringBuffer();
        buf.append(("[CLIP Description]\n"));
        if (clipDescription == null) {
            buf.append("\nnull");
        } else {
            long timestamp = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                timestamp = clipDescription.getTimestamp();
            }
            buf.append("  timestamp: ").append(timestamp).append('\n');
            CharSequence text = clipDescription.getLabel();
            buf.append("      label: ").append(TextUtils.isEmpty(text) ? "null" : text).append('\n');
            buf.append(" MIME types: ");
            for (int i = 0; i < clipDescription.getMimeTypeCount(); i++) {
                buf.append('"').append(clipDescription.getMimeType(i)).append("\" ");
            }
            buf.append('\n');

            PersistableBundle bundle = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bundle = clipDescription.getExtras();
            }
            if (bundle == null) {
                buf.append("     Extras: null");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buf.append("     Extras: ");
                    for (String key : bundle.keySet()) {
                        String value = bundle.get(key).toString();
                        buf.append("\n       ").append(key).append('=').append(value);
                    }
                }
            }
        }
        return buf.toString();
    }

    public static String getClipData(@Nullable ClipData clipData) {
        StringBuffer buf = new StringBuffer();
        buf.append(("[CLIP Items]"));
        if (clipData == null) {
            buf.append("\nnull");
        } else {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);

                buf.append("\n  CLIP ITEM ").append(i + 1).append('\n');
                CharSequence text = item.getText();
                buf.append("    text: ").append(TextUtils.isEmpty(text) ? "null" : text).append('\n');
                text = item.getHtmlText();
                buf.append("    html: ").append(TextUtils.isEmpty(text) ? "null" : text).append('\n');
                Uri uri = item.getUri();
                buf.append("     uri: ").append(uri == null ? "null" : uri.toString()).append('\n');
            }
        }
        return buf.toString();
    }
}
