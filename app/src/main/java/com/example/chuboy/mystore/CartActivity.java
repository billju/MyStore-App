package com.example.chuboy.mystore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.List;

/**
 * Created by ChuBoy on 2017/8/20.
 */

public class CartActivity extends AppCompatActivity{
    private SharedPreferences pref;
    TextView balance_text, total_text;
    int total_amount = 0;
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    String balance_url,server_url;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        balance_text = (TextView)findViewById(R.id.balance);
        total_text = (TextView)findViewById(R.id.total_amount);
        listView = (ListView)findViewById(R.id.cart_list);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
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
                        JSONArray resultArray = jsonObject.getJSONArray("cart");
                        for(int i=0;i<resultArray.length();i++ ){
                            JSONObject subObject = resultArray.getJSONObject(i);
                            String item = subObject.getString("name");
                            int price = subObject.getInt("price");
                            arrayList.add(item+" $"+price);
                            total_amount += price;
                        }
                        int balance = jsonObject.getInt("balance");
                        //把要更改UI的資訊丟到Message當中
                        Message message = new Message();
                        message.what = REFRESH;
                        message.obj = balance;
                        handler.sendMessage(message);
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
                    balance_text.setText("$"+msg.obj.toString());
                    total_text.setText("商品總額: $" + total_amount);
                    break;
                default:
                    break;
            }
        }
    };
}
