package tw.brad.apps.hank22;
//app => new => Folder => Assets Forder 資源不大包進去還ok,可把關卡的資料放這
//在assets新增File寫網頁html
//map:https://developers.google.com/maps/documentation/javascript/tutorial
//gps:要打開這兩個設定
//<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
//<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

//brad.html當你輸入數值,要用java來抓
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private EditText num;
    private LocationManager lmgr;
    private MyListener listener;
    private TextView urname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //少一段允許權限code

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},//gps連線全線
                    123);
        }

        lmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //取得允許全線
        listener = new MyListener();

        num = findViewById(R.id.num);
        webView = findViewById(R.id.webview);
        initWebView();
    }
        //用java取得js.html的物件方法
    public  class  MyJSObject{
        @JavascriptInterface //跟JS的溝通橋梁要讓他認識
        public  void callFromJS(String urname){ //這個方法將會被js呼叫,當你js輸入名字時這邊會顯示出來
            Log.v("brad", "Hello, " + urname);
            MainActivity.this.urname.setText("Hello, " + urname);
        }
        }
    //開的時候gps打開
//    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        lmgr.requestLocationUpdates( //(1.gps 2.幾秒 3.幾公尺 .監聽者)
                LocationManager.GPS_PROVIDER, 1*1000,10, listener);
    }
    //跳別頁時gps關
    @Override
    protected void onStop() {
        super.onStop();
        lmgr.removeUpdates(listener); //關掉gps感應器(事件)
    }

    //定位頃聽者介面實做
    private class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();//取得使用者的緯度
            double lng = location.getLongitude();//取得使用者的精度
            Log.v("brad",lat + ", " + lng );
            webView.loadUrl("javascript:moveTo(" + lat + ", " + lng + ")");//使用妳寫好的輸入經緯度顯示做標,追蹤你到哪地圖到哪
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }


    private void initWebView(){
        WebViewClient webViewClient = new WebViewClient();
        webView.setWebViewClient(webViewClient);


        //設定調整
        WebSettings settings = webView.getSettings();

        settings.setUseWideViewPort(true); //設定寬度
        settings.setLoadWithOverviewMode(true); //式定原始寬度顯示

        settings.setJavaScriptEnabled(true); //設定js顯示為有

        settings.setBuiltInZoomControls(true); //設定縮放功能為有
        settings.setDisplayZoomControls(true); //設定位置縮放為有

        //新增一個我寫好的java物件介面讓他認識 (我寫的物件方法,跟他的名字讓js認識)
        webView.addJavascriptInterface(new MyJSObject(),"brad");

        //webView.loadUrl("https://www.iii.org.tw");
        webView.loadUrl("file:///android_asset/brad.html"); //連接網頁
        //少一段

    }

    //檔案在js可以上傳實做,但這邊要靠繼承
    private  class  MyClient extends WebViewClient{

    }

    private class MyCClient extends WebChromeClient {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && webView.canGoBack()){ //按下返回件是4,如果返回而且畫面要返回的話
            webView.goBack(); //返回
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //點下去返回件
    public void goForward(View view) {
        webView.goForward();
    }

    //膽下去重新整理
    public void reload(View view) {
        webView.reload();
    }

    public void lottery(View view) {
        webView.loadUrl("java");
    }

//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//        Log.v("brad", "onBack");
//    }
}