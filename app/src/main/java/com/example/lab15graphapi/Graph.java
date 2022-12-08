package com.example.lab15graphapi;

import com.example.lab15graphapi.model.Link;
import com.example.lab15graphapi.model.Node;

import java.util.ArrayList;

public class Graph
{
    public int ID;

    String name;

    int countNode = 0;
    int countLink = 0;

    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Link> links = new ArrayList<Link>();

    public void addNode(int id, float x, float y)
    {
        nodes.add(new Node(id, x, y, "Node"));
    }

    public void addNode(Node n)
    {
        nodes.add(n);
    }

    public void deleteNode(int index)
    {
        if (index < 0) return;

        for (int i = 0; i < nodes.size(); i++)
        {
            if (nodes.get(i).ID == index)
            {
                nodes.remove(i);
                return;
            }
        }
    }

    public Node getNode(int ID)
    {
        for (int i = 0; i < nodes.size(); i++)
        {
            if (nodes.get(i).ID == ID)
            {
                Node n = nodes.get(i);
                return n;
            }
        }

        return null;
    }

    public void deleteAllNodes()
    {
        nodes.clear();
    }

    public void addLink(int id, int selectedNode1, int selectedNode2)
    {
        links.add(new Link(id, selectedNode1, selectedNode2, 0));
    }

    public void addLink(Link l)
    {
        links.add(l);
    }

    public void deleteLink(int linkID)
    {
        if (linkID < 0) return;

        for (int i = 0; i < links.size(); i++)
        {
            if (links.get(i).ID == linkID)
            {
                links.remove(i);
                return;
            }
        }
    }

    public Link getLink(int ID)
    {
        for (int i = 0; i < links.size(); i++)
        {
            if (links.get(i).ID == ID)
            {
                Link l = links.get(i);
                return l;
            }
        }

        return null;
    }

    public void deleteAllLinks()
    {
        links.clear();
    }

    @Override
    public String toString() {
        return name;
    }
}
