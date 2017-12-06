package edu.test1.bfrohtua.bfrohtua.myGallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.gson.Gson;
import edu.test1.bfrohtua.bfrohtua.ActivityLogin;
import edu.test1.bfrohtua.bfrohtua.ImagesToCloudinaryService;
import edu.test1.bfrohtua.bfrohtua.R;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class ActivityGallery extends AppCompatActivity
{
    private ControllerMyGallery con;

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("upload").setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();

        if(con.getImagesPaths().size()>0)
        {
            startService(new Intent(this,ImagesToCloudinaryService.class)
                    .putExtra(ActivityLogin.SERVICE_ImagesPaths, new Gson().toJson(con.getImagesPaths()))
                    .putExtra(ActivityLogin.SERVICE_IdUser,con.getIdUser()));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        con = new ControllerMyGallery(this);

        Intent in = getIntent();

        String tmp = in.getStringExtra(ActivityLogin.SERVICE_IdUser);

        con.setIdUser(Integer.parseInt(tmp));

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);

        MyAdapter adapter = new MyAdapter(getApplicationContext(), con);
        recyclerView.setAdapter(adapter);
    }
}
