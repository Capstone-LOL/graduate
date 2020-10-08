package com.example.fordisabled;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import java.lang.Thread;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fordisabled.ui.main.SectionsPagerAdapter;
//stt, tts에 필요한 라이브러리
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Locale;

import android.util.Log;
//1) viewPager.addOnPageChangeListener : Pager가 변경될때 발생하는 이벤트인데, 이때는 TabLayout의 탭까지 변경을 해줘야합니다.
// Pager를 슬라이딩하여 바꾼다고 하더라도 이 동작을 처리하지 않으면 Tab은 같이 변경되지 않습니다.
//
//
//2) tabLayout.addOnTabSelectedListener : 마찬가지로 tab이 눌려졌다면 page도 같이 변경해주어야합니다.
// 탭이 선탤될때 발생하는 이벤트는 onTabSelected이며 tab이라는 인자로 선택된 tab의 위치를 알 수 있습니다.
// 이것을 이용해서 pager를 선택하면 되는 것이죠.
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    FloatingActionButton fab;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    TextView  textView;
    // find loaction 권한, internet, recording 권한에 대한 상수 정의
    final int REQ_PERMISSION = 100;
    final int PERMISSION_INTERNET = 200;
    final int PERMISSION_RECORDING = 300;
    //stt,tts 관련 변수 선언
    SpeechRecognizer mRecognizer;
    Intent intent;
    TextToSpeech tts;

    String resultStr = ""; //검색어를 저장할 변수
    LocationManager manager;

    public LocationManager getLocationManager() {
        return manager;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버전이 특정 버전 이상일 때 퍼미션 체크 함수 불러오기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        //버튼 xml 연결
        fab = findViewById(R.id.fab);
        //텍스트 뷰 xml 연결
//        textView = (TextView) findViewById(R.id.sttResult);

        // STT, TTS 초기화
        speechinit();



        //Context에 있는 Location 상수 값 전달
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
                if (tab.getPosition() == 0){
//                    fab.setImageResource(android.R.drawable.ic_dialog_email);
                    fab.show();
                }else if (tab.getPosition() == 1){
                    fab.hide();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //플로팅 버튼 누를 시 동작
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    funcVoiceOut("목적지를 말씀해 주십시오.");
                    Thread.sleep(2000);// tts 다 들을 때까지 기다리기 안그럼 stt에 입력으로 들어감
                    speechStart();
                }catch (InterruptedException ie){
                    System.out.println(ie.getStackTrace());
                }

            }
        });



    }
    //stt,tts 객체들 정의(초기화)
    private void speechinit(){
        //RecognizerIntent 객체 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        tts = new TextToSpeech(this, this);

    }

    public  void speechStart() {
        //SpeechRecognizer 객체 생성
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(mRecoListener);
        mRecognizer.startListening(intent);
    }

    //RecognierListener 객체 생성
    RecognitionListener mRecoListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }
        //에러시 발생할 메시지들
        @Override
        public void onError(int error) {
            String message;

            switch (error){
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 타임 아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "가용한 RECOGNIZER가 없음";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버 이상";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "입력 시간 초과";
                    break;
                default:
                    message = "알 수 없음 에러";
                    break;
            }
            String guideStr = "에러가 발생되었습니다. : 에러 원인 " + message;
            Toast.makeText(getApplicationContext(), guideStr, Toast.LENGTH_LONG).show();
            funcVoiceOut(guideStr); //에러 발생시 에러 원인 TTS로 말해주기
        }

        @Override
        public void onResults(Bundle results) {
            try{
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                resultStr = "";
                //나중에 정규 표현식 처리로 잘못된 문자가 들어가지 않도록 변경해야 한다.
                for(int i = 0; i< matches.size(); i++){
                    //logcat으로 stt 결과물을 볼 수 있음
                    resultStr += matches.get(i);
                    Log.d("stt_test",matches.get(i));
                }
                //음성 인식 결과가 아무 것도 없을 때
                if(resultStr.trim().length()==0) return;
                else if(resultStr.trim().length()!=0){
                    funcVoiceOut(resultStr+" 로 경로 검색을 시작하겠습니다.");
                    Thread.sleep(2000); // 음성 다들을 때까지 기다리기
                    //경로 탐색 기능
                }
                Log.d("stt_test",String.valueOf(resultStr.length()));


//            resultStr = resultStr.trim();

//                //대답 결과가 예나 아니오인 경우
//                if(resultStr.trim().equals("예")||resultStr.trim().equals("네")){
//                    //검색 기능 구현
//                }
//                else if(resultStr.trim().equals("아니오")||resultStr.trim().equals("아니요")){
//                    String retry = "다시 버튼을 눌러주세요";
//                    funcVoiceOut(retry);
//                }
//                else {//대답 결과가 예 아니오가 아닌 목적지 인 경우//말하는 동안 다시 입력으로들어가는 문제 발생
//                    String check = "가 맞습니까  예 아니오로 대답해주세요";
//                    funcVoiceOut(resultStr+check);
//
//                    Thread.sleep(1000);
//                    speechinit();
//                    speechStart();
//                }
            }catch (InterruptedException ie){
                System.out.println(ie.getStackTrace());
            }



            //moveActivity(resultStr);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    public void moveActivity(String resultStr){

    }
    //tts 로 소리를 내는 부분을 함수로 만들어둠
    public void funcVoiceOut(String OutMsg){
        if(OutMsg.length()<1) return;
        if(!tts.isSpeaking()){
            try{
                tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
                Thread.sleep(1000);
            }catch (InterruptedException ie){
                System.out.println(ie.getStackTrace());
            }


        }
    }
    //tts 설정
    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            //설정 여러개 있으니 테스트 해보길
            tts.setPitch((float) 0.9);
            tts.setSpeechRate((float) 0.9);
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1);
            tts.setVoice(tts.getVoice());//
        } else {
            Log.e("TTS", "초기화 실패");
        }
    }

    // 위치제공자 사용을 위한 권한처리

    @TargetApi(Build.VERSION_CODES.M)

    String permissions[] = new String[3];
    private void checkPermission() {
        //1 권한체크 - 특정권한이 있는지 시스템에 물어본다
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 2. 권한이 없으면 사용자에 권한을 달라고 요청
            String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, REQ_PERMISSION); // -> 권한을 요구하는 팝업이 사용자 화면에 노출된다
        }
        if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 2. 권한이 없으면 사용자에 권한을 달라고 요청
            String permissions[] = {Manifest.permission.INTERNET};
            requestPermissions(permissions, PERMISSION_INTERNET); // -> 권한을 요구하는 팝업이 사용자 화면에 노출된다
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

        }else {
            // 2. 권한이 없으면 사용자에 권한을 달라고 요청
            String permissions[] = {Manifest.permission.RECORD_AUDIO};
            requestPermissions(permissions, PERMISSION_RECORDING); // -> 권한을 요구하는 팝업이 사용자 화면에 노출된다
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
        if (requestCode == PERMISSION_RECORDING) {
            // 3.1 사용자가 승인을 했음
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                cancel();
            }
        }
        if (requestCode == PERMISSION_INTERNET) {
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

    //액티비티 종료 라이프 사이클
    @Override
    protected void onDestroy() {
        //tts 종료하기
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
        //stt 종료
        if(mRecognizer !=null){
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer = null;
        }
        //액티비티 사라짐
        super.onDestroy();
    }
}