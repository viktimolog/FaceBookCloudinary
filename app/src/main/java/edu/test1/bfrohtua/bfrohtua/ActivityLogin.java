package edu.test1.bfrohtua.bfrohtua;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import edu.test1.bfrohtua.bfrohtua.controllers.Controller;
import edu.test1.bfrohtua.bfrohtua.myGallery.ActivityGallery;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import static edu.test1.bfrohtua.bfrohtua.FragmentCloudinary.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

public class ActivityLogin extends AppCompatActivity
{
    public static final String SERVICE_ImagesPaths = "SERVICE_ImagesPaths";
    public static final String SERVICE_IdUser = "SERVICE_IdUser";

    private Controller con;

    private SQLiteConnector connector;
    private SQLiteDatabase db;

    private class SQLiteConnector extends SQLiteOpenHelper
    {

        public SQLiteConnector(Context context, String name, int version)
        {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {

            db.execSQL("create table users (_id integer primary key autoincrement, email varchar(100))");

            db.execSQL("create table photos (_id integer primary key autoincrement, idUser integer, public_id varchar(50))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            if(oldVersion==1 && newVersion==2)
            {

            }
            else if(oldVersion==2 && newVersion==3)
            {

            }
            else if(oldVersion==1 && newVersion==3)
            {

            }
        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                }
                else
                {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context, final String permission)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    connector = new SQLiteConnector(this, "edu.hometask.androidmessenger.Messenger", 1);
                    db = connector.getWritableDatabase();

                    con = new Controller();
                    con.setDb(db);

                    installFragment(new FragmentFB(), false);
                }
                else
                {
                    Toast.makeText(this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


    public void installFragment(Fragment frag, Boolean BackStack)
    {
        FragmentTransaction ft = null;

        try
        {
            ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.F1MA, frag);

            if(BackStack)
            {
              ft.addToBackStack(null);
            }

            ft.commit();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (checkPermissionREAD_EXTERNAL_STORAGE(this))
        {
            connector = new SQLiteConnector(this, "edu.hometask.androidmessenger.Messenger", 1);
            db = connector.getWritableDatabase();

            con = new Controller();
            con.setDb(db);

            installFragment(new FragmentFB(), false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.F1MA);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public Controller getCon() {
        return con;
    }
}
