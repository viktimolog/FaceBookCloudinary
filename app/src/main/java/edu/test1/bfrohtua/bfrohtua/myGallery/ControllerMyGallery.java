package edu.test1.bfrohtua.bfrohtua.myGallery;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import java.util.ArrayList;

public class ControllerMyGallery
{
    private ArrayList<String> imagesPaths;
    private ArrayList<String> galleryList;
    private ArrayList<Integer> posImages;
    private int maxCountPhoto;
    private int idUser;
    private ActivityGallery ma;
    private Uri uri;

    public ControllerMyGallery(ActivityGallery ma)
    {
        this.ma = ma;
        imagesPaths = new ArrayList<>();
        posImages = new ArrayList<>();
        maxCountPhoto = 5;
        idUser=-1;
        uri=null;
        getAllShowImagesPath();
    }

    private void getAllShowImagesPath()
    {
        galleryList = new ArrayList<>();

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        for(int i=0; i<2;i++)
        {
            if(uri!=null)
            {
                getAllImagesFromMedia();
                uri=null;
            }
            uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }
    }

    private void getAllImagesFromMedia()
    {
        Cursor cursor;
        int column_index_data;
        String absolutePathOfImage = null;

        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = ma.getContentResolver().query(uri, projection, null,null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext())
        {
            absolutePathOfImage = cursor.getString(column_index_data);

            galleryList.add(absolutePathOfImage);
        }
    }

    public ArrayList<String> getGalleryList() {
        return galleryList;
    }

    public void setGalleryList(ArrayList<String> galleryList) {
        this.galleryList = galleryList;
    }

    public ArrayList<String> getImagesPaths() {
        return imagesPaths;
    }

    public ArrayList<Integer> getPosImages() {
        return posImages;
    }

    public int getMaxCountPhoto() {
        return maxCountPhoto;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
