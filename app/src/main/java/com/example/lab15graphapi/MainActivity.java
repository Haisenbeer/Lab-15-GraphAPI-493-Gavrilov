package com.example.lab15graphapi;

import static com.example.lab15graphapi.Authorization.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab15graphapi.model.Link;
import com.example.lab15graphapi.model.Node;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    ArrayList<Graph> gr = new ArrayList<>();

    GraphView gv;

    int curGraphID = -1;

    Context ctx = this;
    Activity act = this;

    String nameGraph;
    String token;
    String API;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv = findViewById(R.id.graphView);

        Intent i = getIntent();
        token = i.getStringExtra("token");
        API = i.getStringExtra("API");

        gv.token = token;
        gv.ctx = this;
        gv.API = API;

        OpenGraphList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        final AlertDialog.Builder[] builder = new AlertDialog.Builder[1];
        LayoutInflater inflater;
        View view;

        final Request[] r = new Request[3];

        switch (id)
        {
            case R.id.new_graph:
                gv.clearGraph();
                nameGraph = "New graph";
                CreateNewGraph(nameGraph);
                break;
            case R.id.load_graph:
                gr.clear();

                r[0] = new Request(){
                    public void onSuccess(String res) throws Exception {
                        JSONArray obj = new JSONArray(res);

                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject graph = obj.getJSONObject(i);

                            Graph g = new Graph();
                            g.ID = graph.getInt("id");
                            g.name = graph.getString("name");
                            g.countNode = graph.getInt("nodes");

                            gr.add(g);
                        }

                        int sizeGr = gr.size();

                        String[] curName = {""};
                        String[] arrGraph = new String[sizeGr];

                        for (int i = 0; i < gr.size(); i ++)
                        {
                            arrGraph[i] = gr.get(i).name;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                        builder.setItems(arrGraph, (dialog, which) -> {
                            curName[0] = arrGraph[which];

                            for (int i = 0; i < sizeGr; i ++)
                            {
                                if (curName[0].equals(gr.get(i).name))
                                {
                                    Graph curGr = gr.get(i);

                                    gv.clearGraph();
                                    gv.currentGraph.ID = curGraphID = curGr.ID;
                                    gv.currentGraph.name = curGr.name;
                                    gv.currentGraph.countNode = curGr.countNode;

                                    r[1] = new Request()
                                    {
                                        public void onSuccess(String res1) throws Exception
                                        {
                                            JSONArray arr = new JSONArray(res1);

                                            for (int j = 0; j < arr.length(); j ++)
                                            {
                                                JSONObject obj1 = arr.getJSONObject(j);
                                                Node n = new Node(obj1.getInt("id"), (float) obj1.getDouble("x"), (float) obj1.getDouble("y"), obj1.getString("name"));
                                                gv.currentGraph.addNode(n);
                                            }

                                            r[2] = new Request()
                                            {
                                                public void onSuccess(String res1) throws Exception
                                                {
                                                    JSONArray arr = new JSONArray(res1);

                                                    for (int j = 0; j < arr.length(); j ++)
                                                    {
                                                        JSONObject obj1 = arr.getJSONObject(j);
                                                        Link l = new Link(obj1.getInt("id"), obj1.getInt("source"), obj1.getInt("target"), (float) obj1.getDouble("value"));
                                                        gv.currentGraph.addLink(l);
                                                    }

                                                    gv.invalidate();
                                                }

                                                public void onFail()
                                                {
                                                    MainActivity.this.runOnUiThread(() ->
                                                    {
                                                        Toast.makeText(ctx, "Не удалось загрузить список связей", Toast.LENGTH_SHORT).show();
                                                    });
                                                }
                                            };

                                            r[2].send(MainActivity.this, API, "GET", "/link/list?token=" + token + "&id=" + curGraphID);
                                        }

                                        public void onFail()
                                        {
                                            MainActivity.this.runOnUiThread(() ->
                                            {
                                                Toast.makeText(ctx, "Не удалось получить список узлов", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    };

                                    r[1].send(MainActivity.this, API, "GET", "/node/list?token=" + token + "&id=" + curGraphID);

                                    break;
                                }
                            }
                        })
                                .setNegativeButton("Закрыть", (dialogInterface, i) -> dialogInterface.cancel());

                        builder.show();
                    }

                    public void onFail()
                    {
                        MainActivity.this.runOnUiThread(() ->
                        {
                            Toast.makeText(ctx, "Не удалось получить список графов", Toast.LENGTH_SHORT).show();
                        });
                    }
                };

                r[0].send(this, API, "GET", "/graph/list?token=" + token);
                break;
            case R.id.rename_graph:
                builder[0] = new AlertDialog.Builder(this);

                inflater = this.getLayoutInflater();
                view = inflater.inflate(R.layout.dialog_new_graph_name, null);
                builder[0].setView(view);

                EditText et = view.findViewById(R.id.ed_NewGraphName);

                et.setText(gv.currentGraph.name);

                builder[0].setPositiveButton("Сохранить", (dialogInterface, i) ->
                {
                    String newName = et.getText().toString();
                    gv.currentGraph.name = newName;

                    r[0] = new Request()
                    {
                        public void onSuccess(String res) throws Exception
                        {
                            JSONObject obj = new JSONObject(res);

                            curGraphID = obj.getInt("id");
                        }

                        public void onFail()
                        {
                            MainActivity.this.runOnUiThread(() ->
                            {
                                Toast.makeText(ctx, "Не удалось переименовать граф", Toast.LENGTH_SHORT).show();
                            });
                        }
                    };

                    r[0].send(this, API, "POST", "/graph/update?token=" + token + "&id=" + curGraphID + "&name=" + newName);

                    dialogInterface.cancel();
                })
                        .setNegativeButton("Закрыть", (dialogInterface, i) -> dialogInterface.cancel());

                builder[0].show();
                break;
            case R.id.delete_graph:
                builder[0] = new AlertDialog.Builder(this);

                builder[0].setPositiveButton("ОК", (dialogInterface, i) ->
                {
                    r[0] = new Request()
                    {
                        public void onSuccess(String res) throws Exception
                        {
                            Toast.makeText(ctx, "Граф удален", Toast.LENGTH_SHORT).show();

                            gv.clearGraph();

                            if (gr.size() > 0)
                            {
                                for (int i = 0; i < gr.size(); i++)
                                {
                                    if (gr.get(i).ID == curGraphID)
                                    {
                                        gr.remove(i);
                                        break;
                                    }
                                }

                                OpenGraphList();
                            }
                            else
                            {
                                nameGraph = "New graph";
                                CreateNewGraph(nameGraph);
                            }
                        }

                        public void onFail()
                        {
                            MainActivity.this.runOnUiThread(() ->
                            {
                                Toast.makeText(ctx, "Не удалось удалить граф", Toast.LENGTH_SHORT).show();
                            });
                        }
                    };

                    r[0].send(this, API, "DELETE", "/graph/delete?token=" + token + "&id=" + curGraphID);

                    dialogInterface.cancel();
                })
                        .setNegativeButton("Закрыть", (dialogInterface, i) -> dialogInterface.cancel())
                        .setTitle("Удалить текущий граф?");

                builder[0].show();
                break;
            case R.id.sessions:
                Request sessionsRequest = new Request()
                {
                    public void onSuccess(String res) throws Exception
                    {
                        JSONArray obj = new JSONArray(res);

                        String date;
                        String[][] sessions = {new String[obj.length()]};
                        String[] tokens = new String[obj.length()];

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");

                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject session = obj.getJSONObject(i);

                            int timestamp = session.getInt("timestamp");

                            Date dt = new Date();
                            dt.setTime(timestamp * 1000L);
                            date = newFormat.format(dt);

                            sessions[0][i] = "\n";
                            sessions[0][i] += "ID: " + session.getInt("id") + "\n";
                            sessions[0][i] += "Token: " + session.getString("token") + "\n";
                            sessions[0][i] += "Date: " + date + "\n";
                            sessions[0][i] += "";

                            tokens[i] = session.getString("token");
                        }

                        AlertDialog.Builder builderSession;

                        builderSession = new AlertDialog.Builder(ctx);

                        builderSession.setNegativeButton("Закрыть", (dialogInterface, i) -> dialogInterface.cancel())
                                .setTitle("Текущие сессии")
                                .setItems(sessions[0], (dialog, which) ->
                                {
                                    String curToken = tokens[which];
                                    String dbToken = database.GetToken();

                                    AlertDialog.Builder builderDeleteToken = new AlertDialog.Builder(ctx);

                                    String strTitle = "Закрыть сессию?";
                                    if (dbToken.equals(curToken))
                                        strTitle = "Закрыть текущую сессию?";


                                    builderDeleteToken.setTitle(strTitle)
                                            .setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.cancel())
                                            .setPositiveButton("Да", ((dialogInterface, i) ->
                                            {
                                                Request deleteToken = new Request()
                                                {
                                                    public void onSuccess(String res) throws Exception
                                                    {
                                                        Toast.makeText(ctx, "Сессия успешно закрыта", Toast.LENGTH_SHORT).show();

                                                        if (dbToken.equals(curToken))
                                                        {
                                                            database.NullToken();
                                                            finish();
                                                        }
                                                    }

                                                    public void onFail()
                                                    {
                                                        MainActivity.this.runOnUiThread(() ->
                                                        {
                                                            Toast.makeText(ctx, "Не удалось закрыть сессию", Toast.LENGTH_SHORT).show();
                                                        });
                                                    }
                                                };

                                                deleteToken.send(act, API, "DELETE", "/session/close?token=" + curToken);
                                            }));

                                    builderDeleteToken.show();
                                });

                        builderSession.show();
                    }
                };

                sessionsRequest.send(this, API, "GET", "/session/list?token=" + token);
                break;

            case R.id.setting:
                Intent i = new Intent(this, ActivitySetting.class);
                i.putExtra("token", token);
                startActivityForResult(i, 0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK)
        {
            API = data.getStringExtra("API");
        }
    }

    public void addNode_Click(View v)
    {
        int[] nodeID = new int[1];

        Request r = new Request()
        {
            public void onSuccess(String res) throws Exception
            {
                JSONObject obj = new JSONObject(res);

                nodeID[0] = obj.getInt("id");

                gv.addNode(nodeID[0]);
            }

            public void onFail()
            {
                MainActivity.this.runOnUiThread(() ->
                {
                    Toast.makeText(ctx, "Не удалось создать узел", Toast.LENGTH_SHORT).show();
                });
            }
        };

        r.send(this, API, "PUT", "/node/create?token=" + token + "&id=" + curGraphID + "&x=100&y=100&name=Node");
    }

    public void captionNode_Click(View v)
    {
        if (gv.lastHitNode < 0) return;

        Node curNode = gv.currentGraph.getNode(gv.lastHitNode);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_node_caption, null);
        builder.setView(view);

        builder.setTitle("Описание узла")
                .setPositiveButton("Изменить", (dialogInterface, i) ->
                {
                    EditText et = view.findViewById(R.id.et_CaptionNode);

                    Request r = new Request()
                    {
                        public void onSuccess(String res) throws Exception
                        {
                            gv.setCaptionNode(et.getText().toString());
                            dialogInterface.cancel();
                        }

                        public void onFail()
                        {
                            MainActivity.this.runOnUiThread(() ->
                            {
                                Toast.makeText(ctx, "Не удалось обновить узел", Toast.LENGTH_SHORT).show();
                            });
                        }
                    };

                    r.send(this, API, "POST", "/node/update?token=" + token + "&id=" + curNode.ID + "&x=" + curNode.x + "&y=" + curNode.y + "&name=" + curNode.caption);
                })
                .setNegativeButton("Закрыть", (dialogInterface, i) -> dialogInterface.cancel());

        builder.show();
    }

    public void deleteNode_Click(View v)
    {
        if (gv.lastHitNode < 0) return;

        Request r = new Request()
        {
            public void onSuccess(String res) throws Exception
            {
                gv.deleteNode();
            }

            public void onFail()
            {
                MainActivity.this.runOnUiThread(() ->
                {
                    Toast.makeText(ctx, "Не удалось удалить узел", Toast.LENGTH_SHORT).show();
                });
            }
        };

        r.send(this, API, "DELETE", "/node/delete?token=" + token + "&id=" + gv.lastHitNode);
    }

    public void addLink_Click(View v)
    {
        if (gv.selected1 < 0) return;
        if (gv.selected2 < 0) return;

        Request r = new Request()
        {
            public void onSuccess(String res) throws Exception
            {
                JSONObject obj = new JSONObject(res);

                int linkID = obj.getInt("id");

                gv.addLink(linkID);
            }

            public void onFail()
            {
                MainActivity.this.runOnUiThread(() ->
                {
                    Toast.makeText(ctx, "Не удалось создать узел", Toast.LENGTH_SHORT).show();
                });
            }
        };

        r.send(this, API, "PUT", "/link/create?token=" + token + "&source=" + gv.selected1 + "&target=" + gv.selected2 + "&value=0.0");
    }

    public void captionLink_Click(View v)
    {
        if (gv.lastHitLink < 0) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_link_caption, null);
        builder.setView(view);

        EditText ed1 = view.findViewById(R.id.ed_CaptionOne);
        ed1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setTitle("Вес связи")
                .setPositiveButton("Сохранить", (dialogInterface, i) ->
                {
                    String strNewValue = ed1.getText().toString();

                    float newValue = Float.parseFloat(strNewValue);

                    try
                    {
                        Request r = new Request()
                        {
                            public void onSuccess(String res) throws Exception
                            {
                                gv.setValueLink(newValue);
                            }

                            public void onFail()
                            {
                                MainActivity.this.runOnUiThread(() ->
                                {
                                    Toast.makeText(ctx, "Не удалось создать узел", Toast.LENGTH_SHORT).show();
                                });
                            }
                        };

                        r.send(this, API, "POST", "/link/update?token=" + token + "&id=" + gv.lastHitLink + "&value=" + newValue);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this, "Ошибка при вводе данных", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    dialogInterface.cancel();
                })
                .setNegativeButton("Закрыть", (dialogInterface, i) -> dialogInterface.cancel());

        builder.show();
    }

    public void deleteLink_Click(View v)
    {
        if (gv.lastHitLink < 0) return;

        Request r = new Request()
        {
            public void onSuccess(String res) throws Exception
            {
                gv.deleteLink();
            }

            public void onFail()
            {
                MainActivity.this.runOnUiThread(() ->
                {
                    Toast.makeText(ctx, "Не удалось удалить связь", Toast.LENGTH_SHORT).show();
                });
            }
        };

        r.send(this, API, "DELETE", "/link/delete?token=" + token + "&id=" + gv.lastHitLink);
    }

    public void CreateNewGraph(String nameGraph)
    {
        Request r = new Request()
        {
            public void onSuccess(String res) throws Exception
            {
                JSONObject obj = new JSONObject(res);

                gv.currentGraph.ID = obj.getInt("id");

                gv.currentGraph.name = nameGraph;
                gr.add(gv.currentGraph);
            }

            public void onFail()
            {
                MainActivity.this.runOnUiThread(() ->
                {
                    Toast.makeText(ctx, "Не удалось создать граф", Toast.LENGTH_SHORT).show();
                });
            }
        };

        r.send(this, API, "PUT", "/graph/create?token=" + token + "&name=" + nameGraph);
    }

    public void OpenGraphList()
    {
        gr.clear();

        Request[] r = new Request[3];

        r[0] = new Request(){
            public void onSuccess(String res) throws Exception {
                JSONArray obj = new JSONArray(res);

                for (int i = 0; i < obj.length(); i++) {
                    JSONObject graph = obj.getJSONObject(i);

                    Graph g = new Graph();
                    g.ID = graph.getInt("id");
                    g.name = graph.getString("name");
                    g.countNode = graph.getInt("nodes");

                    gr.add(g);
                }

                int sizeGr = gr.size();

                String[] curName = {""};
                String[] arrGraph = new String[sizeGr];

                for (int i = 0; i < gr.size(); i ++)
                {
                    arrGraph[i] = gr.get(i).name;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                builder.setItems(arrGraph, (dialog, which) -> {
                    curName[0] = arrGraph[which];

                    for (int i = 0; i < sizeGr; i ++)
                    {
                        if (curName[0] == gr.get(i).name)
                        {
                            Graph curGr = gr.get(i);

                            gv.clearGraph();
                            gv.currentGraph.ID = curGraphID = curGr.ID;
                            gv.currentGraph.name = curGr.name;
                            gv.currentGraph.countNode = curGr.countNode;

                            r[1] = new Request()
                            {
                                public void onSuccess(String res1) throws Exception
                                {
                                    JSONArray arr = new JSONArray(res1);

                                    for (int j = 0; j < arr.length(); j ++)
                                    {
                                        JSONObject obj1 = arr.getJSONObject(j);
                                        Node n = new Node(obj1.getInt("id"), (float) obj1.getDouble("x"), (float) obj1.getDouble("y"), obj1.getString("name"));
                                        gv.currentGraph.addNode(n);
                                    }

                                    r[2] = new Request()
                                    {
                                        public void onSuccess(String res1) throws Exception
                                        {
                                            JSONArray arr = new JSONArray(res1);

                                            for (int j = 0; j < arr.length(); j ++)
                                            {
                                                JSONObject obj1 = arr.getJSONObject(j);
                                                Link l = new Link(obj1.getInt("id"), obj1.getInt("source"), obj1.getInt("target"), (float) obj1.getDouble("value"));
                                                gv.currentGraph.addLink(l);
                                            }

                                            gv.invalidate();
                                        }

                                        public void onFail()
                                        {
                                            MainActivity.this.runOnUiThread(() ->
                                            {
                                                Toast.makeText(ctx, "Не удалось загрузить список связей", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    };

                                    r[2].send(MainActivity.this, API, "GET", "/link/list?token=" + token + "&id=" + curGraphID);
                                }

                                public void onFail()
                                {
                                    MainActivity.this.runOnUiThread(() ->
                                    {
                                        Toast.makeText(ctx, "Не удалось получить список узлов", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            };

                            r[1].send(MainActivity.this, API, "GET", "/node/list?token=" + token + "&id=" + curGraphID);

                            break;
                        }
                    }
                })
                        .setNegativeButton("Новый граф", (dialogInterface, i) -> CreateNewGraph("New graph"));

                builder.show();
            }

            public void onFail() {
                Toast.makeText(ctx, "Не удалось получить список графов", Toast.LENGTH_SHORT).show();
                return;
            }
        };

        r[0].send(this, API, "GET", "/graph/list?token=" + token);
    }
}