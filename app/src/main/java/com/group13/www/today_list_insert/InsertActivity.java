package com.group13.www.today_list_insert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static com.group13.www.today_list_insert.MyDBHelper.QUERY_SELECT_ALL2;
import static com.group13.www.today_list_insert.MyDBHelper.TABLE_NAME1;
import static com.group13.www.today_list_insert.MyDBHelper.TABLE_NAME2;
import static com.group13.www.today_list_insert.MyDBHelper._ALARM;
import static com.group13.www.today_list_insert.MyDBHelper._CHECK;
import static com.group13.www.today_list_insert.MyDBHelper._CONTEXT;
import static com.group13.www.today_list_insert.MyDBHelper._CONTEXT_SUB;
import static com.group13.www.today_list_insert.MyDBHelper._ID;
import static com.group13.www.today_list_insert.MyDBHelper._ID_M;

public class InsertActivity extends AppCompatActivity {
    ListView lvItems;
    DatePicker dp1;
    CalendarView calendar;
    Button btnShow;
    TextView arrow;
    BackPressCloseHandler b;
    int checklistnum = 0;
    DialogInterface mPopupDlg = null;
    View dialgView;

    int currentItem = -1;
    MyCursorAdapter myAdapter;
    MyDBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;

    //다이얼로그 변수들
    CheckBox cb_d;
    EditText etContext, etContext_sub;
    String check_d = "F";
    String date_d;
    String time_d;
    //test
    //TextView tvtest;
    int num=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        Intent it=getIntent();
        num=it.getIntExtra("Mnum",0);
       // tvtest = (TextView) findViewById(R.id.tvTest);
        try {
            mHelper = new MyDBHelper(this);
            db = mHelper.getWritableDatabase();
            cursor = db.rawQuery(String.format("select * from %s where _id_m=%d", TABLE_NAME2, num), null);
            myAdapter = new MyCursorAdapter(this, cursor);
        } catch (Exception e) {
           // tvtest.setText(e + "");
        }


        arrow = (TextView) findViewById(R.id.arrow);
        arrow.setText("▲");

        dp1 = (DatePicker) findViewById(R.id.datePicker);
        lvItems = (ListView) findViewById(R.id.listitem_insert);
        lvItems.setAdapter(myAdapter);
        calendar = (CalendarView) findViewById(R.id.calendar);

        b = new BackPressCloseHandler(this, "취소");

        //날짜 서로 수정시 서로 똑같게 만들기

        dp1.init(dp1.getYear(), dp1.getMonth(), dp1.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.setDate(mTime(dp1));
            }
        });
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                dp1.updateDate(year, month, dayOfMonth);
            }
        });


        final Animation animation_down = AnimationUtils.loadAnimation(getApplication(), R.anim.dropdown);

        final Animation animation_up = AnimationUtils.loadAnimation(getApplication(), R.anim.riseup);

        animation_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                calendar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        final Animation animation_turn = AnimationUtils.loadAnimation(getApplication(), R.anim.rotate);

        btnShow = (Button) findViewById(R.id.btnShow);
        lvItems = (ListView) findViewById(R.id.listitem_insert);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendar.getVisibility() == View.VISIBLE) {
                    arrow.startAnimation(animation_turn);
                    calendar.startAnimation(animation_up);
                    arrow.setText("▼");
                } else {
                    arrow.startAnimation(animation_turn);
                    calendar.startAnimation(animation_down);
                    calendar.setVisibility(View.VISIBLE);
                    arrow.setText("▲");
                }
            }
        });

        //db추가
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildAlt b=new BuildAlt();
                AlertDialog.Builder alt = b.makeAlt();
                mPopupDlg = alt.show();
                //Snackbar.make(view, "체크 리스트 추가", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
    }

    public class BuildAlt{
        public AlertDialog.Builder makeAlt(){
            dialgView = View.inflate(InsertActivity.this, R.layout.alert_insert, null);
            AlertDialog.Builder alt = new AlertDialog.Builder(InsertActivity.this);
            alt.setView(dialgView);
            etContext = (EditText) dialgView.findViewById(R.id.context);
            etContext_sub = (EditText) dialgView.findViewById(R.id.context_sub);
            cb_d = (CheckBox) dialgView.findViewById(R.id.cb);
            Button btnShow = (Button) dialgView.findViewById(R.id.btnShow_alert);

            Button btnCansel = (Button) dialgView.findViewById(R.id.btnCansel);
            Button btnSubmit = (Button) dialgView.findViewById(R.id.btnSubmit);

            final LinearLayout ll_sub = (LinearLayout) dialgView.findViewById(R.id.ll_sub);
            final TimePicker tp1=(TimePicker)dialgView.findViewById(R.id.tp1);

            btnShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ll_sub.getVisibility() == View.VISIBLE)
                        ll_sub.setVisibility(View.GONE);
                    else
                        ll_sub.setVisibility(View.VISIBLE);
                }
            });
            btnCansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupDlg.dismiss();
                }
            });
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String context = etContext.getText().toString();
                        String context_sub = etContext_sub.getText().toString();

                        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.M){
                            time_d=tp1.getHour()+" : "+tp1.getMinute();
                        }else
                            time_d=tp1.getCurrentHour()+" : "+tp1.getCurrentMinute();

                        if (cb_d.isChecked())
                            check_d = "T";
                        else
                            check_d = "F";
                        String query="";

                            query = "insert into " + TABLE_NAME2 + " values(" +
                                    "null, "+num+" , '" + time_d + "','" + check_d + "','" + context + "','" + context_sub + "');";
                           // tvtest.setText(query + "");

                        exeQuery(query);
                        refreshDB();
                        mPopupDlg.dismiss();
                    } catch (Exception e) {
                       // tvtest.setText(e + "");
                    }
                }
            });
            return alt;
        }
        public AlertDialog.Builder makeModiAlt(int _id, int _id_m, String _context, String _context_sub, final String _check, String _alarm) {
            dialgView = View.inflate(InsertActivity.this, R.layout.alert_insert, null);
            AlertDialog.Builder alt = new AlertDialog.Builder(InsertActivity.this);
            alt.setView(dialgView);
            etContext = (EditText) dialgView.findViewById(R.id.context);
            etContext_sub = (EditText) dialgView.findViewById(R.id.context_sub);
            cb_d = (CheckBox) dialgView.findViewById(R.id.cb);
            Button btnShow = (Button) dialgView.findViewById(R.id.btnShow_alert);

            Button btnCansel = (Button) dialgView.findViewById(R.id.btnCansel);
            Button btnSubmit = (Button) dialgView.findViewById(R.id.btnSubmit);

            final LinearLayout ll_sub = (LinearLayout) dialgView.findViewById(R.id.ll_sub);
            final TimePicker tp1=(TimePicker)dialgView.findViewById(R.id.tp1);

            final int num=_id;

            etContext.setText(_context);
            etContext_sub.setText(_context_sub);
            if(_check.equals("T"))
                cb_d.setChecked(true);

            int idx=_alarm.indexOf(":");
            String hour_s=_alarm.substring(0,idx-1);
            String min_s=_alarm.substring(idx+2);
            int hour=Integer.parseInt(hour_s);
            int minute=Integer.parseInt(min_s);

            if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.M) {
                tp1.setHour(hour);
                tp1.setMinute(minute);
            }else{
                tp1.setCurrentHour(hour);
                tp1.setCurrentMinute(minute);
            }

            btnShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ll_sub.getVisibility() == View.VISIBLE)
                        ll_sub.setVisibility(View.GONE);
                    else
                        ll_sub.setVisibility(View.VISIBLE);
                }
            });
            btnCansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupDlg.dismiss();
                }
            });
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String context = etContext.getText().toString();
                        String context_sub = etContext_sub.getText().toString();

                        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.M){
                            time_d=tp1.getHour()+" : "+tp1.getMinute();
                        }else
                            time_d=tp1.getCurrentHour()+" : "+tp1.getCurrentMinute();

                        if (cb_d.isChecked())
                            check_d = "T";
                        else
                            check_d = "F";
                        String query="";
                        query = String.format("update %s " +
                                "set _alarm='%s'," +
                                "_check='%s'," +
                                "context='%s'," +
                                "context_sub='%s'" +
                                " where _id=%s", TABLE_NAME2, time_d,check_d,context,context_sub, num);
                        //tvtest.setText(query + "");
                        exeQuery(query);
                        refreshDB();
                        mPopupDlg.dismiss();
                    } catch (Exception e) {
                       // tvtest.setText(e + "");
                    }
                }
            });

            return alt;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel) {
            AlertDialog.Builder alt = new AlertDialog.Builder(InsertActivity.this);
            alt.setTitle("취소하시겠습니까?");
            alt.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String query = String.format("delete from %s where _id=%s", TABLE_NAME1, num);
                    // tvtest.setText(query);
                    exeQuery(query);
                    refreshDB();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
            alt.setNegativeButton("아니오", null);
            alt.show();
        } else if (id == R.id.action_submit) {
            try {
                date_d=getDate(dp1);
                String temp=null;
                Cursor c=db.rawQuery(String.format("select title from %s where title='%s'", TABLE_NAME1, date_d), null);
                while (c.moveToNext()){
                    temp=c.getString(0);
                }
                if(temp==null){
                    String query=String.format("update %s set title='%s',date='%s' where _id=%s",TABLE_NAME1,date_d,date_d,num);
                    exeQuery(query);
                    refreshDB();
                    Toast.makeText(getApplicationContext(), date_d + " 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Toast.makeText(getApplication(),"이미 저장되어있는 날짜입니다.",Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "저장에 실패하였습니다."+e, Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public String getDate(DatePicker dp1) {
        String date = "";
        date += dp1.getYear() + "-";
        date += dp1.getMonth() + 1 + "-";
        date += dp1.getDayOfMonth();
        return date;
    }
    public String getTime(TimePicker tp){
        String time="";
        //안되면 따로
        //time=tp.getHour()+" : "+tp.getMinute();
        return time;
    }
    public long mTime(DatePicker dp1) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, dp1.getYear());
        c.set(Calendar.MONTH, dp1.getMonth());
        c.set(Calendar.DAY_OF_MONTH, dp1.getDayOfMonth());
        long mTime = c.getTimeInMillis();
        return mTime;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        String query = String.format("delete from %s where _id=%s", TABLE_NAME1, num);
        // tvtest.setText(query);
        exeQuery(query);
        refreshDB();
        b.onBackPressed();

    }

    public void refreshDB() {
        cursor = db.rawQuery(String.format("select * from %s where _id_m=%s", TABLE_NAME2, num), null);
        myAdapter.changeCursor(cursor);
    }

    public boolean exeQuery(String query) {
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            //tvtest.append(e+"");
            return false;
        }
    }


    class MyCursorAdapter extends CursorAdapter {
        @SuppressWarnings("deprecation")
        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.item_insert, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvcontext = (TextView) view.findViewById(R.id.tvcontext);
            TextView tvcontext_sub = (TextView) view.findViewById(R.id.tvcontext_sub);

            final LinearLayout llshow=(LinearLayout)view.findViewById(R.id.llshow);

            final Button btnDel = (Button) view.findViewById(R.id.btnDel);
            Button btnModi=(Button)view.findViewById(R.id.btnModi);

            Button btnShow = (Button) view.findViewById(R.id.btnShow_item);

            final CheckBox cb_insert = (CheckBox) view.findViewById(R.id.cb_insert);

            TextView tvdate = (TextView) view.findViewById(R.id.tvdate);

            final int _id=cursor.getInt(cursor.getColumnIndex(_ID));
            final int _id_m= cursor.getInt(cursor.getColumnIndex(_ID_M));

            final String _context = cursor.getString(cursor.getColumnIndex(_CONTEXT));
            final String _context_sub = cursor.getString(cursor.getColumnIndex(_CONTEXT_SUB));

            final String _check = cursor.getString(cursor.getColumnIndex(_CHECK));
            final String _alarm = cursor.getString(cursor.getColumnIndex(_ALARM));

            final LinearLayout ll_sub = (LinearLayout) view.findViewById(R.id.ll_sub);
            LinearLayout ll_item = (LinearLayout) view.findViewById(R.id.ll_item);

            tvcontext.setText(String.valueOf(_context));
            tvcontext_sub.setText(String.valueOf(_context_sub));
            tvdate.setText(String.valueOf(_alarm));
            cb_insert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try{
                    String check = "";
                    if (cb_insert.isChecked())
                        check = "T";
                    else
                        check = "F";
                    String query = String.format("update %s " +
                            "set %s='%s'" +
                            " where _id=%s", TABLE_NAME2, _CHECK, check, _id);
                   // tvtest.append(query+"\n");
                    exeQuery(query);
                    refreshDB();
                    }catch (Exception e){
                       // tvtest.append("\n"+e);
                    }
                }
            });
            if (_check.equals("T"))
                cb_insert.setChecked(true);
            else
                cb_insert.setChecked(false);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String query = String.format("delete from %s where _id=%s", TABLE_NAME2, _id);
                       // tvtest.setText(query);
                        exeQuery(query);
                        refreshDB();
                    } catch (Exception e) {
                    }
                }
            });
            btnModi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        BuildAlt b=new BuildAlt();
                        AlertDialog.Builder alt=b.makeModiAlt(_id,_id_m,_context,_context_sub,_check,_alarm);
                        mPopupDlg = alt.show();
                    }catch (Exception e){
                       // tvtest.setText(e+"");
                    }
                }
            });

            btnShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ll_sub.getVisibility() == View.VISIBLE)
                        ll_sub.setVisibility(View.GONE);
                    else
                        ll_sub.setVisibility(View.VISIBLE);
                }
            });
            ll_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (llshow.getVisibility() == View.GONE)
                        llshow.setVisibility(View.VISIBLE);
                    else
                        llshow.setVisibility(View.GONE);
                    return true;
                }
            });
        }
    }
}
