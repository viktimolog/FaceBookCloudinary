package edu.test1.bfrohtua.bfrohtua.controllers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import com.cloudinary.Cloudinary;
import com.cloudinary.Url;
import com.cloudinary.utils.ObjectUtils;
import edu.test1.bfrohtua.bfrohtua.ImageData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller
{
    private String email;

    private SQLiteDatabase db;

    private Cloudinary mobileCloudinary;

    private ArrayList<String> photosFromCloudinary;
//    public ArrayAdapter<String> adapterPhotos;

    private List<ImageData> images;
    
    private int idUser;
    private String publicId;

    public Controller(SQLiteDatabase db)
    {
        this.db = db;
        photosFromCloudinary = new ArrayList<>();
        idUser = -1;
        newCloudinary();//create logins for Cloudinary, my logins now
    }

    public void emptyImages()
    {
        images=null;
        images = new ArrayList<>();
    }

    public void newCloudinary()
    {
        Map config = new HashMap();
        config.put("cloud_name", "viktimolog");
        config.put("api_key", "873641349846155");
        config.put("api_secret", "uXghuRY1CL8Hfsd7ibGclxhRmk4");
        mobileCloudinary = new Cloudinary(config);
    }

    public void addPhotoToDB()
    {
        ContentValues cv = new ContentValues();
        cv.put("idUser", idUser);
        cv.put("public_id", publicId);

        db.insert("photos", null, cv);
    }

    public void addUserToDB()
    {
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        db.insert("users", null, cv);
    }

    public int isUserInDB()
    {
        String tmp="";

        Cursor result = db.rawQuery("SELECT _id FROM users WHERE email = '"+email+"'", null);

        if(result.moveToNext())
        {
            tmp = result.getString(0);
            idUser = Integer.parseInt(tmp); //нашли айди юзера текущего
        }
        return idUser;
    }

    public void getPhotosFromDB()
    {
        photosFromCloudinary.clear();

        String tmp="";

        Cursor result = db.rawQuery("SELECT _id FROM users WHERE email = '"+email+"'", null);

        if(result.moveToNext())
        {
            tmp = result.getString(0);

            idUser = Integer.parseInt(tmp); //нашли айди юзера текущего

            Cursor result1 = db.rawQuery("SELECT * FROM photos WHERE idUser = '" + idUser + "'", null);

            while (result1.moveToNext())
            {
                photosFromCloudinary.add(result1.getString(2));
            }

            //TODO загрузка фоток в адаптер спиннера
        }
        else
        {
            addUserToDB();

        }

//		Log.d("get History From DB",Integer.toString(arrListHistory.size()));
    }

    public String getPath(final Context context, final Uri uri)
    {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public Cloudinary getMobileCloudinary() {
        return mobileCloudinary;
    }

    public List<ImageData> getImages() {
        return images;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public ArrayList<String> getPhotosFromCloudinary() {
        return photosFromCloudinary;
    }
}
