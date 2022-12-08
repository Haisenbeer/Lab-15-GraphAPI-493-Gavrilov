package com.example.lab15graphapi;

import static com.example.lab15graphapi.Authorization.database;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivitySetting extends AppCompatActivity {

    Intent i;
    String token;
    String API;
    String password;

    EditText edAPI;
    EditText edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        edAPI = findViewById(R.id.edAPI);
        edPassword = findViewById(R.id.edPassword);

        i = getIntent();
        token = i.getStringExtra("token");

        API = database.GetAPI();
        edAPI.setText(API);
        password = database.GetPassword();
        edPassword.setText(password);
    }

    public void OnSave_Click(View v)
    {
        API = edAPI.getText().toString();

        Request checkAPI = new Request()
        {
            @Override
            public void onSuccess(String res) throws Exception {
                Request updateUser = new Request()
                {
                    @Override
                    public void onSuccess(String res) throws Exception {
                        database.UpdatePassword(edPassword.getText().toString());
                        i.putExtra("API", API);
                        setResult(RESULT_OK, i);
                        finish();
                    }

                    @Override
                    public void onFail() {
                        ActivitySetting.this.runOnUiThread(() ->
                        {
                            Toast.makeText(ActivitySetting.this, "Не удалось обновить данные", Toast.LENGTH_SHORT).show();
                        });

                        finish();
                    }
                };

                updateUser.send(ActivitySetting.this, API, "POST", "/account/update?token=" + token + "&secret=" + edPassword.getText().toString());
            }

            @Override
            public void onFail() {
                ActivitySetting.this.runOnUiThread(() ->
                {
                    Toast.makeText(ActivitySetting.this, "Введенный адрес API не отвечает", Toast.LENGTH_SHORT).show();
                });
            }
        };

        checkAPI.send(this, API, "GET", "/session/list?token=" + token);
    }

    public void OnCancel_Click(View v)
    {
        finish();
    }
}
