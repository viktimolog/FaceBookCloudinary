package edu.test1.bfrohtua.bfrohtua;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import com.cloudinary.Cloudinary;
import com.cloudinary.Url;
import com.cloudinary.utils.ObjectUtils;
import edu.test1.bfrohtua.bfrohtua.controllers.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadToCloudinary implements Runnable
{
private Controller con;

public UploadToCloudinary(Controller con)
{
   // this.images = images;
    this.con = con;
}

    @Override
    public void run()
    {
        try
        {
           /* Log.d("MyFilePath", con.getImages().get(0).getFilePath());

            URL url=new URL(con.getMobileCloudinary().url().generate("xcsxtgkgdhbespd3x2qm"));

            Log.d("Url url = ", url.toString());*/


            File file = new File(con.getImages().get(0).getFilePath());

            Map uploadResult = con.getMobileCloudinary().uploader().upload(file, ObjectUtils.emptyMap());

            Log.d("MyTag", uploadResult.toString());

            con.setPublicId(uploadResult.get("public_id").toString());//получение id OK

            Log.d("ID = ", con.getPublicId());

            con.addPhotoToDB(); // в главный тред передаем id(имя) новой фотки, всё паблик-колхоз

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
