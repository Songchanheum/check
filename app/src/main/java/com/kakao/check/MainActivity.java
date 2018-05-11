package com.kakao.check;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.KakaoParameterException;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.check.LoginActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
import static com.kakao.check.R.id.container;
import static com.kakao.check.R.id.profileNickname;
import static com.kakao.check.R.id.profileId;
import static com.kakao.check.R.id.profileImageView;
import static com.kakao.usermgmt.StringSet.nickname;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

   private Fragment checkFragment;
    private Fragment listFragment;
    private Fragment infoFragment;
    private Intent intent;
    private String id;
    private String nick;
    private String profile;

    ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = getIntent();
        id = intent.getStringExtra("id");
        nick = intent.getStringExtra("nickname");
        profile = intent.getStringExtra("profile");



        checkFragment = new CheckFragment();
        listFragment = new ListCheckFragment();
        infoFragment = new InfoFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, checkFragment);
        transaction.addToBackStack(null);
        transaction.commit();


//        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.nav_header_main, null);
//
//        TextView nickname = (TextView) view.findViewById(R.id.profileNickname);
//        nickname.setText(nick);
//
//        TextView idText = (TextView) view.findViewById(R.id.profileId);
//        idText.setText(id.toString());
//
//        try {
//            ImageView proImage = (ImageView) view.findViewById(R.id.profileImageView);
//            URL url = new URL(profile);
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            proImage.setImageBitmap(bmp);
//        }catch(Exception e){
//            e.printStackTrace();
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View nav_header_view = navigationView.getHeaderView(0);

        TextView profileNickname = (TextView) nav_header_view.findViewById(R.id.profileNickname);
        System.out.println(nick);
        System.out.println(id);
        System.out.println(profile);
        profileNickname.setText(nick);
        TextView profileId = (TextView) nav_header_view.findViewById(R.id.profileId);
        profileId.setText(id);
        profileImageView = (ImageView) nav_header_view.findViewById(R.id.profileImageView);


        new Thread() {
            public void run() {
                try {
                    URL url = new URL(profile);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    profileImageView.setImageBitmap(bmp);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        if (id == R.id.action_settings) {
            onClickLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }
    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        item.setChecked(true);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            transaction.replace(container, infoFragment);
        } else if (id == R.id.nav_gallery) {
            transaction.replace(container, checkFragment);
        } else if (id == R.id.nav_slideshow) {
            transaction.replace(container, listFragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    protected String getNickname(){
        return nick;
    }
}
