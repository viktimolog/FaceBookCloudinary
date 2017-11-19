package edu.test1.bfrohtua.bfrohtua;

import android.graphics.Bitmap;
import android.net.Uri;


public class ImageData
{
    private Uri uri;
    private String filePath;

    public Uri getUri() {
        return uri;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
