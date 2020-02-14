package com.philblaiswa.samples.clipboardinspector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> clipboardLogItems = new ArrayList();
    private ArrayAdapter clipboardItemsAdapter;
    private ListView clipboardItemsListView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        imageView.setImageResource(R.mipmap.ic_launcher);

        findViewById(R.id.button_get_primary_clip).setOnClickListener(v -> {
            clipboardLogItems.clear();

            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            if (!clipboard.hasPrimaryClip()) {
                clipboardLogItems.add("===================\nNO PRIMARY CLIP\n===================");
            } else {
                ClipData clipData = clipboard.getPrimaryClip();
                clipboardLogItems.add("===================\nPRIMARY CLIP\n===================");
                clipboardLogItems.add(ClipDataLogger.getClipDescription(clipData.getDescription()));
                clipboardLogItems.add(ClipDataLogger.getClipData(clipData));

                imageView.setImageResource(R.mipmap.ic_launcher);
                if (clipData.getDescription().hasMimeType("image/jpeg")) {
                    List<String> events = processImages(clipData);
                    clipboardLogItems.addAll(events);
                }
            }

            notifyDataSetChanged();
        });

        clipboardItemsAdapter = new ArrayAdapter<String>(this, R.layout.listview_single_item, clipboardLogItems);
        clipboardItemsListView = findViewById(R.id.log_items__list);
        clipboardItemsListView.setAdapter(clipboardItemsAdapter);
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        clipboardItemsAdapter.notifyDataSetChanged();
        clipboardItemsListView.post(() -> clipboardItemsListView.setSelection(clipboardItemsAdapter.getCount() - 1));
    }

    private List<String> processImages(ClipData clipData) {
        List<String> events = new ArrayList<>();

        try {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();

                String displayName = getDisplayName(uri);
                events.add("Filename: " + displayName);

                AssetFileDescriptor fd = getContentResolver().openAssetFileDescriptor(uri, "r");
                events.add("File size: " + fd.getLength());

                Bitmap bitmap = BitmapFactory.decodeStream(fd.createInputStream());
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            events.add("Exception: " + e.getMessage());
        }
        return events;
    }

    private String getDisplayName(Uri uri) {
        Cursor cursor = null;

        try {
            String[] projection = { OpenableColumns.DISPLAY_NAME };
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) { // Should only have one hit
                return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return "No display name";
    }

}
