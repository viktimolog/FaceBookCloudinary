package edu.test1.bfrohtua.bfrohtua;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import edu.test1.bfrohtua.bfrohtua.controllers.Controller;
import edu.test1.bfrohtua.bfrohtua.myGallery.ActivityGallery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.Activity.RESULT_OK;
import static edu.test1.bfrohtua.bfrohtua.ActivityLogin.SERVICE_IdUser;


public class FragmentCloudinary extends Fragment
{
    private BroadcastReceiver br;;
    private ArrayAdapter<String> adapterPhotos;
    private Spinner spinner;
    private Button btnChoice;
    private Button btnChoice1;
    private ImageView iv;
    private Controller con;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 38456;
    private static final int PICK_IMAGE_MULTIPLE = 78546;
    public static final String PARAM_STATUS = "PARAM_STATUS";
    public static final int STATUS_FINISH = 52178;
    public final static String BROADCAST_ACTION = "edu.test1.bfrohtua.bfrohtua";

    public FragmentCloudinary()
    {

    }

    class GetImage extends AsyncTask<Void, Integer, Void> {
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

        adapterPhotos = new ArrayAdapter<>(getActivity().getApplicationContext()
                , R.layout.spinner_row , con.getPhotosFromCloudinary());

        spinner.setAdapter(adapterPhotos);

        spinner.setSelection(spinner.getCount()-1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        br = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
                int status = intent.getIntExtra(PARAM_STATUS, 0);

                if (status == STATUS_FINISH)
                {
                    refreshSpinner();
                }
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        getActivity().registerReceiver(br, intFilt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        con = ((ActivityLogin)getActivity()).getCon();

        con.getPhotosFromDB();

        View v = inflater.inflate(R.layout.fragment_fragment_cloudinary, container, false);

        spinner = (Spinner) v.findViewById(R.id.spinner);

        refreshSpinner();

        btnChoice = (Button)v.findViewById(R.id.btnChoice);
        btnChoice1 = (Button)v.findViewById(R.id.btnChoice1);

        iv = (ImageView)v.findViewById(R.id.iv);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                con.setChoicePhoto(con.getPhotosFromCloudinary().get(position));

                new GetImage().execute();//AsyncTask get Image from Cloudinary and load ImageView
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
                    Toast.makeText(getActivity(), "Select Picture: 1 - 5", Toast.LENGTH_LONG).show();
                    startActivityForResult(Intent.createChooser(intent,"Select Picture: 1 - 5"), PICK_IMAGE_MULTIPLE);
            }
        });

        btnChoice1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    Intent intent = new Intent(getActivity(), ActivityGallery.class);
                    intent.putExtra(SERVICE_IdUser,con.getIdUser()+"");
                    startActivity(intent);
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
                    con.newImagesPathsList();//create new List for paths

                    int numberOfImages;

                    ClipData clipData = imageReturnedIntent.getClipData();

                    if (clipData == null) //selected 1 file
                    {
                        Uri uri = imageReturnedIntent.getData();
                        con.getImagesPaths().add(con.getPath(getActivity(), uri));
                    }
                    else
                    {
                        numberOfImages = imageReturnedIntent.getClipData().getItemCount();

                        if(numberOfImages>5)
                        {
                            Toast.makeText(getActivity(), "You have selected more than 5 images", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,"Select Picture: 1 - 5"), PICK_IMAGE_MULTIPLE);
                        }

                        for (int i = 0; i < numberOfImages; i++)
                        {
                            try
                            {
                                Uri tempUri = imageReturnedIntent.getClipData().getItemAt(i).getUri();

                                con.getImagesPaths().add(con.getPath(getActivity(), tempUri));
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    getActivity().startService(new Intent(getActivity(),ImagesToCloudinaryService.class)
                            .putExtra(ActivityLogin.SERVICE_ImagesPaths, new Gson().toJson(con.getImagesPaths()))
                    .putExtra(SERVICE_IdUser,con.getIdUser()));

                }
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        getActivity().unregisterReceiver(br);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        super.onSaveInstanceState(savedState);
    }
}
