package edu.test1.bfrohtua.bfrohtua;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import edu.test1.bfrohtua.bfrohtua.controllers.Controller;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class ActivityLogin extends AppCompatActivity
{
    public static final int HANDLER_FROMFB = 173547;

    private Controller con;
    static Handler hMain;

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
            //все возможные варианты
            if(oldVersion==1 && newVersion==2)
            {
//				alterTable etc
            }
            else if(oldVersion==2 && newVersion==3)
            {

            }
            else if(oldVersion==1 && newVersion==3)
            {

            }
        }
    }

    static class MyHandler extends Handler
    {
        WeakReference<ActivityLogin> wrActivity;

        public MyHandler(ActivityLogin activity)
        {
            wrActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == ActivityLogin.HANDLER_FROMFB)
            {
                ActivityLogin ma = wrActivity.get();
                if (ma != null)
                {
                    //проверка есть ли юзер адд ту база и тп

                    ma.con.setEmail(msg.obj.toString());

                    Log.d("email handler = ", ma.con.getEmail());

                    ma.installFragment(new FragmentCloudinary());
                }
            }
           /* if (msg.what == ActivityLogin.HANDLER_KEYUSERNAME)//static const
            {
                ActivityLogin ma = wrActivity.get();
                if (ma != null)
                {
                    ma.con.setUserName(msg.obj.toString());
                    ma.con.getMessage().setFrom(ma.con.getUserName());
                    ma.con.getMessage().setTo("server");
                    ma.con.getMessage().setText("regusername");

                    //Первое подключение к серверу
                    new Thread(new CreateSocketThread(ma.con)).start();
                }
            }*/
        }
    }

    public void installFragment(Fragment frag)
    {
        FragmentTransaction ft = null;

        try
        {
            ft = getFragmentManager().beginTransaction();

//            ft.replace(R.id.F1MA, new FragmentFB());
            ft.replace(R.id.F1MA, frag);

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

        hMain = new MyHandler(this);

        connector = new SQLiteConnector(this, "edu.hometask.androidmessenger.Messenger", 1);
        db = connector.getWritableDatabase();

        con = new Controller(db);

        installFragment(new FragmentFB());

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

    public void setCon(Controller con)
    {
        this.con = con;
    }
}
