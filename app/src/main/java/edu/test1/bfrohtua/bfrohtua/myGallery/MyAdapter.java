package edu.test1.bfrohtua.bfrohtua.myGallery;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import edu.test1.bfrohtua.bfrohtua.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> //TODO adapter
{
    private ControllerMyGallery con;
    private Context context;
    private int countPhoto;

    public MyAdapter(Context context, ControllerMyGallery con)
    {
        this.con = con;
        countPhoto=0;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.ViewHolder viewHolder, int i) {

        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(con.getGalleryList().get(i)));//TODO

        viewHolder.img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int pos = viewHolder.getAdapterPosition();

                int color = ((ColorDrawable) v.findViewById(R.id.img).getBackground()).getColor();

                if(color==Color.RED)//delete image from imagesPaths
                {
                    int find=-1;
                    for(int i=0; i<con.getPosImages().size();i++)
                    {
                        if(con.getPosImages().get(i)==pos)
                        {
                            find = i;
                            break;
                        }
                    }
                    if(find!=-1)
                    {
                        con.getImagesPaths().remove(find);
                        con.getPosImages().remove(find);
                        countPhoto--;
                    }

                    v.findViewById(R.id.img).setBackgroundColor(Color.WHITE);
                }
                else//add image to imagesPaths
                {
                    if(countPhoto==5)
                    {
                        Toast.makeText(context,"You've selected "+con.getMaxCountPhoto()+" photos. "+"It's maximum for upload!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        con.getImagesPaths().add(con.getGalleryList().get(pos));

                        con.getPosImages().add(pos);

                        v.findViewById(R.id.img).setBackgroundColor(Color.RED);
                        countPhoto++;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return con.getGalleryList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }
}