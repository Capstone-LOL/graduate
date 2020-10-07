package com.example.fordisabled;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fordisabled.ui.main.SectionsPagerAdapter;

//1) viewPager.addOnPageChangeListener : Pager가 변경될때 발생하는 이벤트인데, 이때는 TabLayout의 탭까지 변경을 해줘야합니다.
// Pager를 슬라이딩하여 바꾼다고 하더라도 이 동작을 처리하지 않으면 Tab은 같이 변경되지 않습니다.
//
//
//2) tabLayout.addOnTabSelectedListener : 마찬가지로 tab이 눌려졌다면 page도 같이 변경해주어야합니다.
// 탭이 선탤될때 발생하는 이벤트는 onTabSelected이며 tab이라는 인자로 선택된 tab의 위치를 알 수 있습니다.
// 이것을 이용해서 pager를 선택하면 되는 것이죠.
public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    LocationManager manager;

    public LocationManager getLocationManager() {
        return manager;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Context에 있는 Location상수라는걸 알려주는 것이다
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //tab xml에서 id 로 tab layout 가져오기
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Map")); // tablayout에서 tab을 추가하고 이름 주기
        tabLayout.addTab(tabLayout.newTab().setText("tab2"));// tablayout에서 tab을 추가하고 이름 주기

        viewPager = (ViewPager)findViewById(R.id.view_pager);//tab화면을 스와이프하게 해줄 viewpager layout 가져오기
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));//화면 관리를 도와줄 fragmentmanager, adapter 연결

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout)); //탭화면 변화를 감지

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {//탭 선택시 어떻게 할지
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition()); // 탭선택시 해당 포지션의 페이지를 줌
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

//        FloatingActionButton fab = findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    // 위치제공자 사용을 위한 권한처리
    private final int REQ_PERMISSION = 100;

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        //1 권한체크 - 특정권한이 있는지 시스템에 물어본다
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 2. 권한이 없으면 사용자에 권한을 달라고 요청
            String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, REQ_PERMISSION); // -> 권한을 요구하는 팝업이 사용자 화면에 노출된다
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            // 3.1 사용자가 승인을 했음
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                cancel();
            }
        }
    }


    public void cancel() {

        Toast.makeText(this, "권한요청을 승인하셔야 GPS를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
        finish();

    }

}