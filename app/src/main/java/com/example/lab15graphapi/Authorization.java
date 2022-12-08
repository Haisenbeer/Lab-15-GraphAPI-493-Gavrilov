package com.example.lab15graphapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class Authorization extends AppCompatActivity
{
    public static DB database;

    String strRegistration;
    String strLogin;
    String strBack;
    String strSignUp;

    String token;
    String API;

    EditText ed_Login;
    EditText ed_Password;

    Button btn_Login;
    Button btn_Registration;

    Switch sw_SaveData;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        ed_Login = findViewById(R.id.ed_Login);
        ed_Password = findViewById(R.id.ed_Password);

        btn_Login = findViewById(R.id.btn_Login);
        btn_Registration = findViewById(R.id.btn_Registration);

        sw_SaveData = findViewById(R.id.sw_SaveData);

        strRegistration = getResources().getString(R.string.btn_Registration);
        strLogin = getResources().getString(R.string.btn_Login);
        strBack = getResources().getString(R.string.btn_Back);
        strSignUp = getResources().getString(R.string.btn_SignUp);

        database = new DB(this, "Graph.db", null, 1);

        token = database.GetToken();
        API = database.GetAPI();

        if (!token.equals(""))
        {
            i = new Intent(this, MainActivity.class);
            i.putExtra("token", token);
            i.putExtra("API", API);
            startActivityForResult(i, 0);
        }

        ed_Login.setText(database.GetLogin());
        ed_Password.setText(database.GetPassword());
    }

    //Нажатие на кнопку регистрации
    public void onRegistration_Click(View v)
    {
        if (btn_Registration.getText().toString().equals(strRegistration))
        {
            sw_SaveData.setVisibility(View.INVISIBLE);
            btn_Registration.setText(strBack);
            btn_Login.setText(strSignUp);
        }
        else
        {
            sw_SaveData.setVisibility(View.VISIBLE);
            btn_Registration.setText(strRegistration);
            btn_Login.setText(strLogin);
        }
    }

    //Нажатие на кнопку входа
    public void onLogin_Click(View v)
    {
        Context ctx = this;

        String login = ed_Login.getText().toString();
        String password = ed_Password.getText().toString();

        if (btn_Login.getText().toString().equals(strLogin))
        {
            Request r = new Request()
            {
                public void onSuccess(String res) throws Exception
                {
                    JSONObject obj = new JSONObject(res);

                    token = obj.getString("token");

                    Toast.makeText(ctx, "Успешный вход!", Toast.LENGTH_SHORT).show();

                    if (sw_SaveData.isChecked())
                    {
                        database.SaveLoginPassword(login, password, token);
                    }
                    else
                    {
                        database.NotSaveLoginPassword();
                    }

                    i = new Intent(ctx, MainActivity.class);
                    i.putExtra("token", token);
                    i.putExtra("API", API);
                    startActivity(i);
                }

                public void onFail()
                {
                    Authorization.this.runOnUiThread(() ->
                    {
                        Toast.makeText(ctx, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                    });
                }
            };

            r.send(this, API, "PUT", "/session/open?name=" + login + "&secret=" + password);
        }
        else
        {
            Request r = new Request()
            {
                public void onFail()
                {
                    Authorization.this.runOnUiThread(() ->
                    {
                        Toast.makeText(ctx, "Не удалось создать пользователя", Toast.LENGTH_SHORT).show();
                    });
                }
            };

            r.send(this, API, "PUT", "/account/create?name=" + login + "&secret=" + password);
        }
    }
}
