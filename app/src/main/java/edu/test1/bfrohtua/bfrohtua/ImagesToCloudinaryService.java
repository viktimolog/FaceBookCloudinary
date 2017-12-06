package edu.test1.bfrohtua.bfrohtua;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;
import edu.test1.bfrohtua.bfrohtua.controllers.Controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class ImagesToCloudinaryService extends Service
{
    private Controller con;
    private SQLiteConnector connector;
    private SQLiteDatabase db;

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate()
    {
        super.onCreate();

        connector = new SQLiteConnector(this, "edu.hometask.androidmessenger.Messenger", 1);
        db = connector.getWritableDatabase();//1

        con = new Controller();
        con.setDb(db);

        con.newCloudinary();//2

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String tmp = intent.getStringExtra(ActivityLogin.SERVICE_ImagesPaths);

        con.setImagesPaths(new Gson().fromJson(tmp,ArrayList.class));//3

        con.setIdUser(intent.getIntExtra(ActivityLogin.SERVICE_IdUser,-1));//4

        new Upload().execute();

        return START_STICKY;
    }
    @Override
    public void onDestroy()
    {

    }

    class Upload extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused)
        {
            try
            {
                for(int i=0; i<con.getImagesPaths().size();i++)
                {
                    File file = new File(con.getImagesPaths().get(i));
                    Map uploadResult = con.getMobileCloudinary().uploader().upload(file, ObjectUtils.emptyMap());

                    con.setPublicId(uploadResult.get("public_id").toString());//get id OK

                    con.addPhotoToDB();
                }

                Intent intent1 = new Intent(FragmentCloudinary.BROADCAST_ACTION);
                intent1.putExtra(FragmentCloudinary.PARAM_STATUS, FragmentCloudinary.STATUS_FINISH);
                sendBroadcast(intent1);
                stopSelf();
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
        protected void onPostExecute(Void unused)
        {

        }
    }
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
}
