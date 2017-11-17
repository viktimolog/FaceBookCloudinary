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
    private String email;

    private LoginButton enterByFB ;

    private TextView tv;

    private CallbackManager callbackManager;

    public FragmentFB()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_fragment_fb, null, false);

        enterByFB = (LoginButton) v.findViewById(R.id.login_button);

        tv = (TextView) v.findViewById(R.id.tv);

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
                                                    tv.setText(object.getString("email")+" - "+object.getString("first_name"));
                                                    ((ActivityLogin) getActivity()).getCon().setEmail(object.getString("email"));//wrote email in controller
                                                    Log.d("EMAIL = ", ((ActivityLogin) getActivity()).getCon().getEmail());//working
                                                    ((ActivityLogin) getActivity()).installFragment(new FragmentCloudinary());
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

    //как только возвращаемся обратно запускаем обратно поток
    @Override
    public void onResume()
    {
        super.onResume();
    }

    // на паузе стопим все к чертям
    @Override
    public void onPause()
    {
        super.onPause();
    }

    //выходя гасите свет, убиваем процес что бы не палить електричество
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    //по возвращению обратно на активность передаем все полученные данные с диалога в колбек
    // и живем дальше счастливо
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    //при вращении экрана и т д сохраняем все что происходит на экране,
    // а то активити обычно обновляется. а диалог останется жив
    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }

}
