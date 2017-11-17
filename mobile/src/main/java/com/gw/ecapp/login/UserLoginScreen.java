package com.gw.ecapp.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.gw.ecapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by iningosu on 11/10/2017.
 */

public class UserLoginScreen extends AppCompatActivity {


    @BindView(R.id.user_id)
    public EditText mUser;

    @BindView(R.id.user_password)
    public EditText mPassword;

    @BindView(R.id.login_btn)
    public Button mLoginBtn;


    private UserLoginPresenter mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);
        ButterKnife.bind(this);

        mPresenter = new UserLoginPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick(R.id.login_btn)
    public void onLoginPressed(){

        String userEmail = mUser.getEditableText().toString();
        String password = mPassword.getEditableText().toString();

        if(mPresenter.validateUserCred(userEmail,password)){

        }else{
            // Didn't meet the constraint
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
