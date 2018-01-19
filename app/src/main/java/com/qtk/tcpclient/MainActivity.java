package com.qtk.tcpclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> arrayList;
    private ClientListAdapter adapter;
    private TcpClient tcpClient;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<>();

        final EditText editText = findViewById(R.id.editText);
        final Button sendButton = findViewById(R.id.sendButton);

        listView = findViewById(R.id.list);
        adapter = new ClientListAdapter(this, arrayList);
        listView.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                String msg = editText.getText().toString();
                arrayList.add("Client: " + msg);

                if(tcpClient !=null){
                    tcpClient.sendMsg(msg);
                }

                adapter.notifyDataSetChanged();
                editText.setText("");
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(tcpClient != null) {
            tcpClient.stopClient();
            tcpClient = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(tcpClient == null){
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
        }else{
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.connect:
                new ConnectTask().execute("");
                return true;
            case R.id.disconnect:
                if(tcpClient != null) {
                    tcpClient.stopClient();
                    tcpClient=null;
                }
                arrayList.clear();
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ConnectTask extends AsyncTask<String, String, TcpClient>{
        @Override
        protected TcpClient doInBackground(String... msg){
            tcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                public void messageReceived (String msg) {
                    publishProgress(msg);
                }
            });

            tcpClient.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);

            arrayList.add(values[0]);
            adapter.notifyDataSetChanged();
        }
    }
}
