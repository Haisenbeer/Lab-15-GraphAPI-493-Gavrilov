package com.example.lab15graphapi;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request
{
    HttpURLConnection con;

    public void onSuccess(String res) throws Exception
    {

    }

    public void onFail()
    {
        return;
    }

    public void send(Activity ctx, String base, String method, String request)
    {
        Runnable r = () ->
        {
            try
            {
                URL url = new URL(base + request);
                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod(method);

                if (method == "POST" && con.getResponseCode() == 200)
                {
                    con.disconnect();

                    ctx.runOnUiThread(() ->
                    {
                        try
                        {
                            onSuccess("");
                        }
                        catch (Exception e)
                        {
                            Log.e("Error", e.toString());
                        }
                    });

                    return;
                }

                InputStream is = con.getInputStream();
                BufferedInputStream inp = new BufferedInputStream(is);

                byte[] buf = new byte[512];
                String str = "";

                while (true)
                {
                    int len = inp.read(buf);
                    if (len < 0) break;

                    str += new String(buf, 0, len);
                }

                con.disconnect();

                final String res = str;
                ctx.runOnUiThread(() ->
                {
                    try
                    {
                        onSuccess(res);
                    }
                    catch (Exception e)
                    {
                        Log.e("Error", e.toString());
                    }
                });
            }
            catch (Exception e)
            {
                try {
                    if (con.getResponseCode() == 200)
                    {
                        con.disconnect();

                        ctx.runOnUiThread(() ->
                        {
                            try
                            {
                                onSuccess("");
                            }
                            catch (Exception ex)
                            {
                                Log.e("Error", ex.toString());
                            }
                        });

                        return;
                    }

                    /*ctx.runOnUiThread(() ->
                    {
                        Toast.makeText(ctx, "Не удалось выполнить запрос", Toast.LENGTH_SHORT).show();
                    });*/
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                onFail();
            }
        };

        Thread t = new Thread(r);
        t.start();
    }
}
