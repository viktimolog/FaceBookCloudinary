package edu.test1.bfrohtua.bfrohtua;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.test1.bfrohtua.bfrohtua.controllers.Controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class FragmentCloudinary extends Fragment
{
    private ArrayAdapter<String> adapterPhotos;
    private Spinner spinner;
    private Button btnChoice;
    private ImageView iv;
    private Controller con;

    private static final int PICK_IMAGE_MULTIPLE = 78546;

    public FragmentCloudinary()
    {

    }

    class GetImageFromCloudinary extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused)
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

            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... items) {

        }
        @Override
        protected void onPostExecute(Void unused) {
            iv.setImageBitmap(con.getBmp());
        }
    }

    public void refreshSpinner()
    {
        con.getPhotosFromDB();

        adapterPhotos=null;

        adapterPhotos = new ArrayAdapter<String>(getActivity().getApplicationContext()
                , android.R.layout.simple_list_item_1 , con.getPhotosFromCloudinary());

        spinner.setAdapter(adapterPhotos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//фрагмент не изменяется при перевороте устройства
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        con = ((ActivityLogin)getActivity()).getCon();

        con.getPhotosFromDB();//либо загрузил фотки в photosFromCloudinary либо добавил юзера

//        Log.d("email Constr Cloud = ", con.getEmail());//OK

        View v = inflater.inflate(R.layout.fragment_fragment_cloudinary, container, false);

        spinner = (Spinner) v.findViewById(R.id.spinner);

        refreshSpinner();

        btnChoice = (Button)v.findViewById(R.id.btnChoice);

        iv = (ImageView)v.findViewById(R.id.iv);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                con.setChoicePhoto(con.getPhotosFromCloudinary().get(position));//0 start OK
//                Log.d("choicePhoto 0 = ", con.getChoicePhoto());

                new GetImageFromCloudinary().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }

        });

        btnChoice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture: 1 - 5"), PICK_IMAGE_MULTIPLE);
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode)
        {
            case PICK_IMAGE_MULTIPLE:
                if(resultCode == RESULT_OK)
                {
                    con.emptyImages();

                    int numberOfImages = imageReturnedIntent.getClipData().getItemCount();

                    Log.d("numberOfImages = ", numberOfImages+"");

                    for (int i = 0; i < numberOfImages; i++)
                    {
                        try
                        {
                            ImageData imageData = new ImageData();
                            imageData.setUri(imageReturnedIntent.getClipData().getItemAt(i).getUri()); //нормальный это Uri

                            imageData.setFilePath(con.getPath(getActivity(), imageData.getUri()));//TODO getActivity() не факт OK

//                            Log.d("imageData.filePath = ",imageData.getFilePath());//OK

                            con.getImages().add(imageData);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    new Thread(new UploadToCloudinary(con)).start();//пытаемся загрузить на cloudinary TODO

                    try//этот колхоз порешать хендлером
                    {
                        Thread.sleep(10000);//меньше 10с не катит
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    refreshSpinner();
                }
        }
    }

    public ImageView getIv() {
        return iv;
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }
}
