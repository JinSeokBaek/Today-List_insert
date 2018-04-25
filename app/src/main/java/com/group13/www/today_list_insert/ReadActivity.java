package com.group13.www.today_list_insert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.group13.www.today_list_insert.MyDBHelper.TABLE_NAME1;
import static com.group13.www.today_list_insert.MyDBHelper.TABLE_NAME2;
import static com.group13.www.today_list_insert.MyDBHelper._ALARM;
import static com.group13.www.today_list_insert.MyDBHelper._CHECK;
import static com.group13.www.today_list_insert.MyDBHelper._CONTEXT;
import static com.group13.www.today_list_insert.MyDBHelper._CONTEXT_SUB;
import static com.group13.www.today_list_insert.MyDBHelper._DATE;
import static com.group13.www.today_list_insert.MyDBHelper._ID;
import static com.group13.www.today_list_insert.MyDBHelper._ID_M;

public class ReadActivity extends AppCompatActivity {
    SharedPreferences setting;
    int currentItem = -1;
    MyCursorAdapter_read myAdapter;
    MyDBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor,cursor2;
    ListView lvShow;
    DialogInterface mPopupDlg = null;
    View dialgView;

    CheckBox cb_d;
    EditText etContext, etContext_sub;
    String check_d = "F";
    String date_d;
    String time_d;

    int num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setting=getSharedPreferences("widget",Context.MODE_PRIVATE);
        setContentView(R.layout.activity_read);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //testv=(TextView)findViewById(R.id.tvtest2);
        Intent it=getIntent();
        num=it.getIntExtra("num",0);
        //boolean widget=it.getBooleanExtra("widget",false);
        mHelper = new MyDBHelper(this);
        db = mHelper.getWritableDatabase();
        String date="";String query="";
        try {
            query=String.format("select * from %s where _id=%s", TABLE_NAME1,num);
            cursor2 = db.rawQuery(query, null);
            while (cursor2.moveToNext()){
                int id=cursor2.getInt(cursor2.getColumnIndex("_id"));
                String title=cursor2.getString(cursor2.getColumnIndex("title"));
                date=cursor2.getString(cursor2.getColumnIndex("date"));
            }
            setTitle(date);
        }catch (Exception e){
            //testv.setText(e+"\n");
        }
        cursor = db.rawQuery(String.format("select * from %s where _id_m=%d", TABLE_NAME2,num ), null);
        myAdapter = new MyCursorAdapter_read(this, cursor);
        lvShow = (ListView) findViewById(R.id.lvShow);
        lvShow.setAdapter(myAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildAlt b=new BuildAlt();
                AlertDialog.Builder alt = b.makeAlt();
                mPopupDlg = alt.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
            try {
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
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
            return false;
        }
    }


    class MyCursorAdapter_read extends CursorAdapter {
        @SuppressWarnings("deprecation")
        public MyCursorAdapter_read(Context context, Cursor c) {
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
                        //tvtest.append(query+"\n");
                        exeQuery(query);
                        refreshDB();
                    }catch (Exception e){
                        //tvtest.append("\n"+e);
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

    public class BuildAlt{
        public AlertDialog.Builder makeAlt(){
            dialgView = View.inflate(ReadActivity.this, R.layout.alert_insert, null);
            AlertDialog.Builder alt = new AlertDialog.Builder(ReadActivity.this);
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
                        //tvtest.setText(query + "");

                        exeQuery(query);
                        refreshDB();
                        mPopupDlg.dismiss();
                    } catch (Exception e) {
                        //tvtest.setText(e + "");
                    }
                }
            });
            return alt;
        }
        public AlertDialog.Builder makeModiAlt(int _id, int _id_m, String _context, String _context_sub, final String _check, String _alarm) {
            dialgView = View.inflate(ReadActivity.this, R.layout.alert_insert, null);
            AlertDialog.Builder alt = new AlertDialog.Builder(ReadActivity.this);
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
}
