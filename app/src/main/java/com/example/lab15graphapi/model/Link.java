package com.example.lab15graphapi.model;

public class Link
{
    public int ID;
    public float value;

    public int a;
    public int b;

    public Link(int ID, int a, int b, float value)
    {
        this.ID = ID;
        this.a = a;
        this.b = b;
        this.value = value;
    }
}
