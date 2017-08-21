package com.example.chuboy.mystore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ChuBoy on 2017/8/19.
 */

public class SearchActivity extends AppCompatActivity {
    private SharedPreferences pref;
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    String server_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.listView);
        arrayList.add("one");
        arrayList.add("two");
        arrayList.add("three");
        arrayList.add("four");
        arrayList.add("five");
        arrayList.add("six");
        arrayList.add("seven");
        arrayList.add("eight");
        arrayList.add("night");
        arrayList.add("ten");

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        server_url = pref.getString("server_url","");
        refresh();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = listView.getItemAtPosition(i).toString();
                Snackbar.make(view, text , Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<String> tempList = new ArrayList<>();
                for(String temp: arrayList){
                    if(temp.toLowerCase().contains(s.toLowerCase())){
                        tempList.add(temp);
                    }
                }
                ArrayAdapter<String> adapter =  new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, tempList);
                listView.setAdapter(adapter);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private HttpURLConnection urlConnection;
    public static final int REFRESH = 100;
    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://"+ server_url);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    //定義一個讀取的物件並把資料讀到BufferedReader中
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                    //再把資料轉進String當中，要用StringBuilder不然會讀錯
                    StringBuilder jsonStringBuilder = new StringBuilder();
                    String jsonString;
                    while((jsonString = reader.readLine()) != null){jsonStringBuilder.append(jsonString);}
                    reader.close();
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(jsonStringBuilder));
                        JSONArray resultArray = jsonObject.getJSONArray("purchased");
                        //倒著加入清單，先入後進FILO
                        for(int i=resultArray.length();i>0;i--){
                            JSONObject subObject = resultArray.getJSONObject(i-1);
                            String item = subObject.getString("name");
                            int price = subObject.getInt("price");
                            arrayList.add(item);
                            
                            //把要更改UI的資訊丟到Message當中
                            Message message = new Message();
                            message.what = REFRESH;
                            message.obj = item;
                            handler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }catch(Exception e){

                }
            }
        }).start();
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case REFRESH:
                    break;
                default:
                    break;
            }
        }
    };
}