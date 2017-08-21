package com.example.chuboy.mystore;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ChuBoy on 2017/8/20.
 */

public class AccountActivity extends AppCompatActivity {
    ListView listView;
    List<Map<String, Object>> mList;
    private Integer[] mThumbIds={
            R.drawable.mystore_round,
            R.drawable.icon1,R.drawable.icon2,
            R.drawable.icon3,R.drawable.icon4,
            R.drawable.icon5,R.drawable.icon6,
            R.drawable.icon7,R.drawable.icon8,
            R.drawable.icon9,R.drawable.icon10,
            R.drawable.icon11,R.drawable.icon12
    };
    String[] category={
            "MyStore","Line","Twitter","Youtube","Facebook","Opera","IE edge",
            "Firefox","Safari","IE","Linux","Apple","Chrome"
    };
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.account_list);
        mList = new ArrayList<Map<String,Object>>();
        //String[] listFromResource = getResources().getStringArray(R.array.weekday);
        for(int i = 0;i < mThumbIds.length; i++){
            Map<String,Object> item = new HashMap<String,Object>();
            item.put("imgView",mThumbIds[i]);
            item.put("txtView",category[i]);
            mList.add(item);
        }
        //宣告一個adapter並產生作用
        SimpleAdapter adapter = new SimpleAdapter(this, mList, R.layout.list_layout,new String[]{"imgView","txtView"},new int[]{R.id.imgView,R.id.txtView});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent login_intent = new Intent().setClass(AccountActivity.this,LoginActivity.class);
                        startActivity(login_intent);
                        break;
                    default:
                        break;
                }
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
}