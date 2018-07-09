package com.group13.www.today_list_insert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;
    SharedPreferences setting;
    Button btnWeather, btnBus, btnSubway;
    LocationManager lm;
    View dialgView;
    ToggleButton tb;
    double lon, lat;
    TextView tv_w;
    LocationListener mLocationListener;

    ArrayList<String> busStops_id, busStops_name;

    BusAdapter busAdapter;
    EditText etBus;
    ListView lvBus;
    BusStopAPI busStopAPI;

    LinearLayout ll_bus;
    Button btnBus_search;
    String station;

    DialogInterface mPopupDlg = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("설정");
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();
        btnWeather = (Button) findViewById(R.id.btnWeather);
        btnBus = (Button) findViewById(R.id.btnBus);


        etBus = (EditText) findViewById(R.id.etBus);
        btnBus_search = (Button) findViewById(R.id.btnBus_search);
        ll_bus = (LinearLayout) findViewById(R.id.ll_bus);


        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alt = getAlt_w(SettingActivity.this, "위치 ");
                alt.show();
            }
        });
        btnBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_bus.getVisibility() == View.VISIBLE)
                    ll_bus.setVisibility(View.GONE);
                else
                    ll_bus.setVisibility(View.VISIBLE);
            }
        });

        btnBus_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                station = etBus.getText().toString();
                AlertDialog.Builder alt = getAlt_b(SettingActivity.this, "버스 정거장 ", station);
                mPopupDlg = alt.show();
            }
        });

        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //여기서 위치값이 갱신되면 이벤트가 발생한다.
                //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

                lon = location.getLongitude(); //경도
                lat = location.getLatitude();   //위도
                tv_w.setText("좌표를 찾았습니다!\n경도 : " + lon + "\n위도 : " + lat + "\n적용을 눌러주세요.");
                //lm.removeUpdates(this);
            }

            public void onProviderDisabled(String provider) {
                // Disabled시
            }

            public void onProviderEnabled(String provider) {
                // Enabled시

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // 변경시
            }
        };
    }
    public void doBus(String station){
        busStopAPI=new BusStopAPI(station);
    }
    public AlertDialog.Builder getAlt_b(Context c, String s, String station) {
        dialgView = View.inflate(SettingActivity.this, R.layout.alt_bus, null);
        AlertDialog.Builder alt = new AlertDialog.Builder(c);
        lvBus=(ListView)dialgView.findViewById(R.id.lvBus);

        doBus(station);
        alt.setView(dialgView);
        alt.setTitle(s + "을 선택하세요");
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ArrayList<Bus> list=new ArrayList<Bus>();
                busStops_id=busStopAPI.getStationId();
                busStops_name=busStopAPI.getStationName();

                int i=0;
                while(i<busStops_id.size()){
                    Bus b=new Bus(busStops_id.get(i),busStops_name.get(i));
                    list.add(b);
                    i++;
                }
                busAdapter=new BusAdapter(getApplication(),list);
                lvBus.setAdapter(busAdapter);//여기에 딜레이 후 시작할 작업들을 입력
            }
        }, 1000);// 0.5초 정도 딜레이를 준 후 시작


        return alt;
    }

    public AlertDialog.Builder getAlt_w(Context c, String s) {
        dialgView = View.inflate(SettingActivity.this, R.layout.alt_weather, null);
        AlertDialog.Builder alt = new AlertDialog.Builder(c);
        alt.setView(dialgView);
        tb = (ToggleButton) dialgView.findViewById(R.id.btnTb);
        tv_w = (TextView) dialgView.findViewById(R.id.tv_gps);
        alt.setTitle(s + " 설정");
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (tb.isChecked()) {
                        tv_w.setText("수신중..");
                        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                    } else {
                        tv_w.setText("위치정보 미수신중");
                        lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
                    }
                } catch (SecurityException ex) {
                }

            }
        });
        alt.setPositiveButton("적용", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //editor.putString("regin", regin);
                try {
                    editor.putString("lon", Double.toString(lon));
                    editor.putString("lat", Double.toString(lat));
                    lm.removeUpdates(mLocationListener);
                    editor.putBoolean("setted", true);
                    editor.commit();
                } catch (Exception e) {
                    Toast.makeText(getApplication(), e + "", Toast.LENGTH_LONG).show();
                }
            }
        });
        alt.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lm.removeUpdates(mLocationListener);
            }
        });

        return alt;
    }

    class BusAdapter extends BaseAdapter {
        Context context;
        ArrayList<Bus> post;

        public BusAdapter(Context c ,ArrayList<Bus> l){
            context=c;
            post=l;
        }
        @Override
        public int getCount() { // adpter총길이
            return post.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater lay = LayoutInflater.from(context);
            View view=lay.inflate(R.layout.item_bus,null);
            TextView tv=(TextView)view.findViewById(R.id.tvBusName);
            tv.setText(post.get(position).getBusName());
            final int p=position;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id=getID(p);
                    String name=getName(p);
                    editor.putString("bstop",id);
                    editor.putString("bstopname",name);
                    Toast.makeText(getApplication(),"설정되었습니다.",Toast.LENGTH_SHORT).show();
                    editor.commit();
                    mPopupDlg.dismiss();
                }
            });
            return  view;
        }
        public String getID(int position){
            String id=post.get(position).getBusID();
            return id;
        }
        public String getName(int position){
            String name=post.get(position).getBusName();
            return name;
        }
    }
}
