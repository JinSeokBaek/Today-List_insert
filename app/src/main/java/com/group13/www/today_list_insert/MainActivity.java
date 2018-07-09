package com.group13.www.today_list_insert;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.group13.www.today_list_insert.MyDBHelper.QUERY_SELECT_ALL1;
import static com.group13.www.today_list_insert.MyDBHelper.TABLE_NAME1;
import static com.group13.www.today_list_insert.MyDBHelper.TABLE_NAME2;
import static com.group13.www.today_list_insert.MyDBHelper._ID;
import static com.group13.www.today_list_insert.MyDBHelper._TITLE;

public class MainActivity extends AppCompatActivity {
    final static int MainCode=1010;
    SharedPreferences setting,s2;
    WeatherAPI weatherAPI;
    BusArrivalAPI busArrivalAPI;
    //TextView testtv;
    ImageView ivWeather;

    MyDBHelper mHelper;
    SQLiteDatabase db;

    BackPressCloseHandler back;

    String regin,bstop,bstop2;
    TextView tvWeather,tvBus,tvBusStop;
    double lon,lat;

    MyCursorAdapter myAdapter;
    ListView lvMemo;

    ArrayList<String> alarm = new ArrayList<String>();

    Cursor cursor2;
    Cursor cursor3;


    String title = "";
    String content="";

    int hour;
    int minute;
    String hm;

    Cursor cursor1,cursor;
    int num;
    boolean b_num=false;
    boolean b=false;
    String getTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivWeather=(ImageView)findViewById(R.id.ivWeather);

        tvWeather=(TextView)findViewById(R.id.tvWeather);
        tvBus=(TextView)findViewById(R.id.tvBus);
        tvBusStop=(TextView)findViewById(R.id.busStop);
        s2=getSharedPreferences("widget",0);
        setting = getSharedPreferences("setting", 0);
        back = new BackPressCloseHandler(this, "종료");
        //testtv=(TextView)findViewById(R.id.testtest);
        getTime = getToday();
        int id=0;
        try {
            mHelper = new MyDBHelper(this);
            db = mHelper.getWritableDatabase();
            cursor=db.rawQuery(QUERY_SELECT_ALL1,null);
            cursor1 = db.rawQuery(String.format("select _id from %s where date='%s'", TABLE_NAME1, getTime), null);
            myAdapter = new MyCursorAdapter(this, cursor);
            while (cursor1.moveToNext())
                id = cursor1.getInt(cursor1.getColumnIndex("_id"));
            num=id;
            if(num>0)
                b_num=true;

            //testtv.setText(num+""+b_num);
        } catch (Exception e) {
            // tvtest.setText(e + "");
        }
        lvMemo=(ListView)findViewById(R.id.lvMemo);
        lvMemo.setAdapter(myAdapter);
       // regin=setting.getString("regin", "error");
        bstop=setting.getString("bstopname", "정거장을 설정하세요");
        bstop2=setting.getString("bstop","0");
       lon=Double.parseDouble(setting.getString("lon","128"));
        lat=Double.parseDouble(setting.getString("lat","36"));


            doWeather();
//#####################################################################################################################################
            doBusArrival();
//#####################################################################################################################################



        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //tvWeather.setText("날짜 "+weatherAPI.getDay());
                //testtv.append();
                //tvWeather.append("\n지역 : " + weatherAPI.getRegin());
                try {
                    tvWeather.setText("지역 : " + weatherAPI.getRegin());
                    //tvWeather.append("\n날씨 : " + weatherAPI.getWeather_name());
                    //tvWeather.append("\n" + weatherAPI.getWeather_num());
                    tvWeather.append("\n" + weatherAPI.getTemp_min() + "°C / " + weatherAPI.getTemp_max() + "°C");

                    String wnum_s = weatherAPI.getWeather_num();
                    int wnum = Integer.parseInt(wnum_s);

                    if (wnum >= 200 && wnum <= 622 && b_num) {
                        int id = 0;
                        Cursor c = db.rawQuery(String.format("select _id from %s where context='%s' and _id_m=%d", TABLE_NAME2, "우산", num), null);
                        while (c.moveToNext())
                            id = c.getInt(0);
                        if (id == 0) {

                            String query;
                            query = "insert into " + TABLE_NAME2 + " values(" +
                                    "null, " + num + " , '7:00','F','우산','자동생성됨');";
                            // tvtest.setText(query + "");

                            exeQuery(query);
                        }
                        //refreshDB();
                    }



                //tvWeather.append("\n"+wnum);
                ivWeather.setImageResource(weatherAPI.getIcon(wnum));
                //여기에 딜레이 후 시작할 작업들을 입력
                    }catch (Exception e){
                    Toast.makeText(getApplication(),e+"",Toast.LENGTH_LONG).show();
            }
            }
        }, 500);// 0.5초 정도 딜레이를 준 후 시작

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                tvBusStop.setText(bstop);
                try {
               StringBuilder stringBuilder=busArrivalAPI.getArrival();

                    tvBus.setText(stringBuilder.toString());
                }catch (Exception e){
                    Toast.makeText(getApplication(),"정거장을 설정하세요",Toast.LENGTH_LONG).show();
                }
                //여기에 딜레이 후 시작할 작업들을 입력
            }
        }, 500);// 0.5초 정도 딜레이를 준 후 시작

        mHelper = new MyDBHelper(this);

        db = mHelper.getWritableDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query="";

                query = "insert into " + TABLE_NAME1 + " values(" +
                        "null,null,null );";
                //tvtest.setText(query + "");

                exeQuery(query);
                Cursor cursor2;
                int id=0;
                query=String.format("select max(_id) as max from %s", TABLE_NAME1);
                cursor2 = db.rawQuery(query, null);
                try {
                    while (cursor2.moveToNext()) {
                        id = cursor2.getInt(cursor2.getColumnIndex("max"));

                    }
                }catch (Exception e){
                    Toast.makeText(getApplication(),e+"",Toast.LENGTH_LONG).show();
                }
                Intent it=new Intent(MainActivity.this,InsertActivity.class);
                it.putExtra("Mnum",id);
                startActivityForResult(it,MainCode);
            }
        });

        //alarm();
        //Toast.makeText(getApplication(),title+"+"+content+"+"+hm,Toast.LENGTH_SHORT).show();
    }
//    public void alarm(){
//
//        //#############################################################################################################################################
//        //고정된 노티바
//        PendingIntent pendingNoti = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        NotificationCompat.Builder noti = new NotificationCompat.Builder(getApplicationContext())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Today-List")
//                .setContentText("오늘 할 일 있음")
//                .setLargeIcon(icon)
//                .setAutoCancel(false)
//                .setOngoing(true)
//                .setContentIntent(pendingNoti);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
//            noti.setPriority(5);   //이 설정은 젤리빈 이상일떄만 사용
//        }
//
//        NotificationManager manageNoti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manageNoti.cancel(0);
//        manageNoti.notify(0, noti.build());
//        //#############################################################################################################################################
//
//
//        long now = System.currentTimeMillis();  //현재 시간을 msec로 구한다
//        Date date = new Date(now);  //현재 시간을 date변수에 저장
//        //SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd");  //시간을 나타낼 포맷 저장
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//        String currentdate = df.format(new Date());
//        if(currentdate.charAt(5) == '0'){
//            String s1 = currentdate.substring(0, 5);
//            String s2 = currentdate.substring(6);
//            currentdate = s1 + s2;
//        }
//        //String formatDate = sdfNow.format(date);    //newDate변수에 값을 저장
//        try {
//            cursor2 = db.rawQuery(String.format("select _id, title from %s where date=%s", TABLE_NAME1, currentdate), null);
//            int id_m = 0;
//
//            while (cursor2.moveToNext()) {
//                id_m = cursor2.getInt(0);
//                title = cursor2.getString(1);
//            }
//            cursor3 = db.rawQuery(String.format("select _alarm, context from %s where _id_m=%d", TABLE_NAME2, id_m), null);
//            while (cursor3.moveToNext()) {
//                String alarm_ = cursor3.getString(0);
//                content=cursor3.getString(1);
//                alarm.add(alarm_);
//            }
//            //현재 시간
//            SimpleDateFormat sdfNow_clock = new SimpleDateFormat("HH:mm");
//            String formatClock = sdfNow_clock.format(date);
//
//            int i = 0;
//            while (i < alarm.size()) {
//                String _alarm = alarm.get(i);
//                int idx = _alarm.indexOf(":");
//                String hour_s = _alarm.substring(0, idx-1);
//                String min_s = _alarm.substring(idx+2);
//                hour = Integer.parseInt(hour_s);
//                minute = Integer.parseInt(min_s);
//
//                SimpleDateFormat h = new SimpleDateFormat("H",Locale.KOREA);
//                SimpleDateFormat m = new SimpleDateFormat("m",Locale.KOREA);
//                if (hour == Integer.parseInt(h.toString()) && minute == Integer.parseInt(m.toString())) {
//                    hm = String.valueOf(hour) +":"+ String.valueOf(minute);
//                    new Alarm(getApplication()).Alarm();
//                }
//                i++;
//            }
//
//            new Alarm(getApplication()).Alarm();
//            //String dbMonth = String.valueOf(clock.charAt(0)) + String.valueOf(clock.charAt(1));
//        }catch (Exception e){
//            Toast.makeText(getApplication(),e+"",Toast.LENGTH_LONG).show();
//        }
//       //Toast.makeText(getApplication(),title+"+"+content+"+"+hm+" "+currentdate,Toast.LENGTH_SHORT).show();
//    }
//    public class  Alarm {
//        private Context context;
//        public Alarm(Context context){
//            this.context = context;
//        }
//
//        public void Alarm() {
//            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(MainActivity.this, BroadcastD.class);
//            intent.putExtra("title", title);
//            intent.putExtra("content", content);
//            intent.putExtra("clock", hm);
//
//            PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//            Calendar calendar = Calendar.getInstance();
//
//            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour, minute, 00);
//
//            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
//        }
//    }


    @Override
    public void onBackPressed() {
        back.onBackPressed();
    }

    public void refreshDB() {
        cursor = db.rawQuery(String.format(QUERY_SELECT_ALL1), null);
        myAdapter.changeCursor(cursor);
    }

    class MyCursorAdapter extends CursorAdapter {
        @SuppressWarnings("deprecation")
        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.item_memo, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvId=(TextView)view.findViewById(R.id.tvId);
            TextView tvDate=(TextView)view.findViewById(R.id.tvDate);
            final Button btnDel_m=(Button)view.findViewById(R.id.btnDel_m);
            LinearLayout ll_memo=(LinearLayout)view.findViewById(R.id.ll_memo);

            final int _id=cursor.getInt(cursor.getColumnIndex(_ID));
            final String _title = cursor.getString(cursor.getColumnIndex(_TITLE));

            tvId.setText(_id+"");
            tvDate.setText(_title+" 할일");
            btnDel_m.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String query = String.format("delete from %s where _id=%s", TABLE_NAME1, _id);
                        String query2=String.format("delete from %s where _id_m=%s", TABLE_NAME2, _id);
                        // tvtest.setText(query);
                        exeQuery(query);
                        exeQuery(query2);
                        refreshDB();
                    } catch (Exception e) {
                    }
                }
            });

            ll_memo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it=new Intent(MainActivity.this,ReadActivity.class);
                    it.putExtra("num",_id);
                    startActivity(it);
                }
            });
            ll_memo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(btnDel_m.getVisibility()==View.GONE)
                        btnDel_m.setVisibility(View.VISIBLE);
                    else
                        btnDel_m.setVisibility(View.GONE);
                    return true;
                }
            });
        }
    }

    public boolean exeQuery(String query) {
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            Toast.makeText(getApplication(),e+"",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public String getToday(){
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String currentdate= df.format(new Date());
        if(currentdate.charAt(5)=='0'){
            String s1=currentdate.substring(0,5);
            String s2=currentdate.substring(6);
            currentdate=s1+s2;
        }
        return currentdate;
    }
    public void doWeather(){
        try {
            weatherAPI = new WeatherAPI(lat, lon, getTime);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
        }
    }
    public void doBusArrival(){
        try {
            busArrivalAPI = new BusArrivalAPI(bstop2);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent it=new Intent(MainActivity.this,SettingActivity.class);
            startActivityForResult(it,MainCode);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            //listview새로고침
            //resume()가 할수있음
        }else if(resultCode==RESULT_CANCELED){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDB();
        setting = getSharedPreferences("setting", 0);
        lon=Double.parseDouble(setting.getString("lon","128"));
        lat=Double.parseDouble(setting.getString("lat","36"));
        doWeather();
        doBusArrival();
        if(setting.getBoolean("setted",false)) {
            regin=setting.getString("regin", "error");
            bstop=setting.getString("bstopname", "정거장을 설정하세요");
            //station=setting.getString("station", "error");
        }
        //alarm();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //tvWeather.setText("날짜 "+weatherAPI.getDay());
                //testtv.append();
                //tvWeather.append("\n지역 : " + weatherAPI.getRegin());
                try{
                tvWeather.setText("지역 : " + weatherAPI.getRegin());
                //tvWeather.append("\n날씨 : " + weatherAPI.getWeather_name());
                //tvWeather.append("\n" + weatherAPI.getWeather_num());
                tvWeather.append("\n"+weatherAPI.getTemp_min()+"°C / " + weatherAPI.getTemp_max()+"°C");

                String wnum_s= weatherAPI.getWeather_num();
                int wnum=Integer.parseInt(wnum_s);
                if(wnum>=200&&wnum<=622&&b_num){
                    int id=0;
                    Cursor c=db.rawQuery(String.format("select _id from %s where context='%s' and _id_m=%d", TABLE_NAME2, "우산",num), null);
                    while(c.moveToNext())
                        id=c.getInt(0);
                    if(id==0) {
                        //testtv.append(id+"");
                        String query;
                        query = "insert into " + TABLE_NAME2 + " values(" +
                                "null, " + num + " , '7:00','F','우산','자동생성됨');";
                        // tvtest.setText(query + "");

                        exeQuery(query);
                    }
                }

                //tvWeather.append("\n"+wnum);
                ivWeather.setImageResource(weatherAPI.getIcon(wnum));
                //여기에 딜레이 후 시작할 작업들을 입력
                }catch (Exception e){
                    Toast.makeText(getApplication(),"날씨 지역을 설정하세요",Toast.LENGTH_LONG).show();
                }
            }
        }, 500);// 0.5초 정도 딜레이를 준 후 시작

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                tvBusStop.setText(bstop);
                try {
                    StringBuilder stringBuilder=busArrivalAPI.getArrival();

                    tvBus.setText(stringBuilder.toString());
                }catch (Exception e){
                    Toast.makeText(getApplication(),"정거장을 설정하세요",Toast.LENGTH_LONG).show();
                }
                //여기에 딜레이 후 시작할 작업들을 입력

            }
        }, 500);// 0.5초 정도 딜레이를 준 후 시작

    }
}
