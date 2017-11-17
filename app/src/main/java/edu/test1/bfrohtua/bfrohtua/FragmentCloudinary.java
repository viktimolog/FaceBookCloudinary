package edu.test1.bfrohtua.bfrohtua;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentCloudinary extends Fragment
{

    public FragmentCloudinary()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_fragment_cloudinary, container, false);

        return v;
    }

}
