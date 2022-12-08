package com.example.lab15graphapi.model;

public class Node
{
    //Номер узла в графе
    public int ID;

    //Позиция центра узла
    public float x;
    public float y;

    public String caption;

    public Node(int ID, float x, float y, String caption)
    {
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.caption = caption;
    }
}
