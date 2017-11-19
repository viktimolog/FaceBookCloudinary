package edu.test1.bfrohtua.bfrohtua;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import edu.test1.bfrohtua.bfrohtua.controllers.Controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GetImageFromCloudinary implements Runnable
{
private Controller con;

    public GetImageFromCloudinary(Controller con)
    {
        this.con = con;
    }
    @Override
    public void run()
    {
        try
        {
            con.setUrl(new URL(con.getMobileCloudinary().url().generate(con.getChoicePhoto())));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        try
        {
            con.setBmp(BitmapFactory.decodeStream(con.getUrl().openConnection().getInputStream()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}
