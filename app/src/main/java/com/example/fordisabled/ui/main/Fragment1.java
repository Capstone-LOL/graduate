package com.example.fordisabled.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.location.LocationListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fordisabled.MainActivity;
import com.example.fordisabled.R;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.zip.Inflater;
public class Fragment1 extends Fragment implements OnMapReadyCallback{
    private GoogleMap mMap;
    private FragmentManager fm;
    LocationManager manager;

    public static Fragment1 newInstance(){
        Fragment1 fragment = new Fragment1();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FloatingActionButton fab =
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_map,container,false);
        //fragment 안에서 fragment를 가져오는 코드
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //맵을 사용할 준비가 되면 onMapReady 함수를 자동으로 호출
        mapFragment.getMapAsync(this);

        //프래그먼트가 호출된 상위 액티비티를 가져올수있음 (나를 호출한 액티비티를 가져옴) / getActivity는 나를 가지고있는 액티비티를 뜻한다
        //상위 액티비티의 자원을 사용하기 위해서 Activity를 가져온다
        MainActivity activity = (MainActivity) getActivity();
        manager = activity.getLocationManager();



        return view;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //좌표 생성
        LatLng SEOUL = new LatLng(37.56, 126.97);
        //마커 생성
        MarkerOptions markerOptions = new MarkerOptions();
        //마커에 좌표 넣기
        markerOptions.position(SEOUL);
        //마커 이름
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        //마커 그리기
        mMap.addMarker(markerOptions);
        //맵 중심을 해당 좌표로 이동, 좌표, 줌레벨을 전달해주면됨
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL,10));
    }

    @Override
    public void onResume() {
        super.onResume();
        //마시멜로 이상버전에서는 런타임 권한 체크여부를 확인해야 한다
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            // GPS 사용을 위한 권한 휙득이 되어 있지 않으면 리스너 해제하지 않는다
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
        }

        // GPS 리스너 등록
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, //위치제공자
                3000, //변경사항 체크 주기 millisecond 단위임
                1, //변경사항 체크 거리 meter단위
                locationListener //나는 locationListener를 쓸꺼야
        );
    }

    // 현재 프래그먼트가 정지
    @Override
    public void onPause() {
        super.onPause();


        // 리스너 해제 , 프래그먼트 작동 해제
        manager.removeUpdates(locationListener);
    }
    //GPS 사용을 위해서 좌표 리스너를 생성
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //경도
            double lng = location.getLongitude();
            //위도
            double lat = location.getLatitude();
            //고도
            double alt = location.getAltitude();
            //정확도
            float acc = location.getAccuracy();
            //위치제공자(ISP)
            String provider = location.getProvider();

            //바뀐 현재 좌표
            LatLng current = new LatLng(lat,lng);
            //현재좌표로 카메라를 이동시킬때 // TODO 바로 여기에 전역변수 구글맵이 들어감
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,17));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 위치 공급자의 상태가 바뀔 때 호출 됩니다
        }

        @Override
        public void onProviderEnabled(String provider) {
            // 위치 공급자가 사용 가능해질(enabled) 때 호출 됩니다.
        }

        @Override
        public void onProviderDisabled(String provider) {
            //  위치 공급자가 사용 불가능해질(disabled) 때 호출 됩니다.
        }

    };
}
