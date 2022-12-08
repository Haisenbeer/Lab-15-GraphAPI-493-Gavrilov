package com.example.lab15graphapi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {

    String empty = "";

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Settings (Http TEXT, Login TEXT, Password TEXT, Token TEXT);";
        db.execSQL(sql);

        sql = "INSERT INTO Settings VALUES('" + "http://nodegraph.spbcoit.ru:8000" + "', '" + empty + "', '" + empty + "', '" + empty + "');";
        db.execSQL(sql);
    }

    public void SaveLoginPassword(String login, String password, String token)
    {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "UPDATE Settings SET Login = '" + login + "', Password = '" + password + "', Token = '" + token + "';";

        try
        {
            db.execSQL(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void NotSaveLoginPassword()
    {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "UPDATE Settings SET Login = '" + empty + "', Password = '" + empty + "';";

        try
        {
            db.execSQL(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void NullToken()
    {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "UPDATE Settings SET Token = '" + empty + "';";

        try
        {
            db.execSQL(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void UpdatePassword(String password)
    {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "UPDATE Settings SET Password = '" + password + "';";

        try
        {
            db.execSQL(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String GetAPI()
    {
        String API = "";

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT Http FROM Settings;";

        try
        {
            Cursor cur = db.rawQuery(sql , null);

            if (cur.moveToFirst())
            {
                API = cur.getString(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return API;
    }

    public String GetLogin()
    {
        String login = "";

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT Login FROM Settings;";

        try
        {
            Cursor cur = db.rawQuery(sql , null);

            if (cur.moveToFirst())
            {
                login = cur.getString(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return login;
    }

    public String GetPassword()
    {
        String password = "";

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT Password FROM Settings;";

        try
        {
            Cursor cur = db.rawQuery(sql , null);

            if (cur.moveToFirst())
            {
                password = cur.getString(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return password;
    }

    public String GetToken()
    {
        String token = "";

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT Token FROM Settings;";

        try
        {
            Cursor cur = db.rawQuery(sql , null);

            if (cur.moveToFirst())
            {
                token = cur.getString(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return token;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
