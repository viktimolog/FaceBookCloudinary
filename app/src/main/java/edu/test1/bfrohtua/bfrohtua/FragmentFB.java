package edu.test1.bfrohtua.bfrohtua;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import edu.test1.bfrohtua.bfrohtua.controllers.Controller;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class FragmentFB extends Fragment
{
    private Controller con;

    private LoginButton enterByFB ;

    private CallbackManager callbackManager;

    public FragmentFB()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        LoginManager.getInstance().logOut();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_fragment_fb, null, false);

        con = ((ActivityLogin) getActivity()).getCon();

        enterByFB = (LoginButton) v.findViewById(R.id.login_button);

        enterByFB.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        enterByFB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>()
                        {
                            @Override
                            public void onSuccess(LoginResult loginResult)
                            {
                                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback()
                                        {
                                            @Override
                                            public void onCompleted(JSONObject object, GraphResponse response)
                                            {
                                                try
                                                {
                                                    con.setEmail(object.getString("email"));//wrote email in controller OK
                                                    Log.d("EMAIL = ", con.getEmail());

                                                    ((ActivityLogin) getActivity()).installFragment(new FragmentCloudinary(), true);// run 2 fragment
                                                }
                                                catch (JSONException e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                Bundle parameters = new Bundle();

                                parameters.putString("fields", "id,name,email,gender,first_name,last_name,link,picture.type(large)");
                                request.setParameters(parameters);
                                request.executeAsync();
                            }

                            @Override
                            public void onCancel() {
                                //Cancel Stuff
                            }

                            @Override
                            public void onError(FacebookException exception)
                            {
                                exception.printStackTrace();
                                //Error Stuff
                            }
                        });
                LoginManager.getInstance().logInWithReadPermissions(FragmentFB.this, Arrays.asList("email"));

            }
        });
        return v;
    }

    @Override
    public void onResume()
    {
        LoginManager.getInstance().logOut();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        LoginManager.getInstance().logOut();
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        LoginManager.getInstance().logOut();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        super.onSaveInstanceState(savedState);
    }
}
