package com.example.chuboy.mystore;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private WebView webView;
    private LinearLayout linearLayout;
    public static int lastPosition = 0;
    private long lastBackTime = 0;
    private long currentBackTime = 0;
    private SharedPreferences pref;
    String home_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isIntro = pref.getBoolean("intro",false);
        Log.d("intro",""+isIntro);
        if(!isIntro){
            Intent intent = new Intent().setClass(MainActivity.this,IntroActivity.class);
            startActivity(intent);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setOrientationLocked(false);
                integrator.setPrompt("");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_group);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_public);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_apps);
        */
        webView = (WebView)findViewById(R.id.webView);
        linearLayout = (LinearLayout)findViewById(R.id.network_icon);
        linearLayout.setVisibility(View.GONE);
        //設定WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;    //使用當前的WebView打開新網頁，不用跳轉到系統瀏覽器
            }
        });
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        //如果未連線的話，mNetworkInfo會等於null
        if(mNetworkInfo != null)
        {
            //網路是否已連線(true or false)
            if(mNetworkInfo.isConnected()){
                home_url = pref.getString("home_url","");
                webView.loadUrl("http://" + home_url);
            }
            //網路連線方式名稱(WIFI or mobile)
            mNetworkInfo.getTypeName();
            //網路連線狀態
            mNetworkInfo.getState();
            //網路是否可使用
            mNetworkInfo.isAvailable();
            //網路是否已連接or連線中
            mNetworkInfo.isConnectedOrConnecting();
            //網路是否故障有問題
            mNetworkInfo.isFailover();
            //網路是否在漫遊模式
            mNetworkInfo.isRoaming();
        }else {
            linearLayout.setVisibility(View.VISIBLE);
        }
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_login){
            Intent login_intent = new Intent().setClass(MainActivity.this, LoginActivity.class);
            startActivity(login_intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //viewPager.setCurrentItem(0);
            Intent intro_intent = new Intent().setClass(MainActivity.this, IntroActivity.class);
            startActivity(intro_intent);
        } else if (id == R.id.nav_search) {
            Intent search_intent = new Intent().setClass(MainActivity.this, SearchActivity.class);
            startActivity(search_intent);
        } else if (id == R.id.nav_cart) {
            Intent cart_intent = new Intent().setClass(MainActivity.this, CartActivity.class);
            startActivity(cart_intent);
        } else if (id == R.id.nav_scan) {
            // Handle the camera action
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        } else if (id == R.id.nav_settings) {
            Intent settings_intent = new Intent().setClass(MainActivity.this, SettingsActivity.class);
            startActivity(settings_intent);
        } else if (id == R.id.nav_individual) {
            Intent account_intent = new Intent().setClass(MainActivity.this, AccountActivity.class);
            startActivity(account_intent);
        }else if (id == R.id.nav_info) {
            Intent about_intent = new Intent().setClass(MainActivity.this,AboutActivity.class);
            startActivity(about_intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "掃描中斷", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "載入網址" + result.getContents(), Toast.LENGTH_SHORT).show();
                webView.loadUrl(result.getContents());
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            currentBackTime = System.currentTimeMillis();
            if(currentBackTime - lastBackTime > 2000){
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                lastBackTime = currentBackTime;
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
