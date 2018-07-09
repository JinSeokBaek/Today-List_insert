package com.group13.www.today_list_insert;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.widget.RemoteViews;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.group13.www.today_list_insert.MyDBHelper.TABLE_NAME1;
import static com.group13.www.today_list_insert.MyDBHelper.TABLE_NAME2;

/**
 * Implementation of App Widget functionality.
 */
public class Todaylist_widget extends AppWidgetProvider {
    public static final String ACTION_CLICK = "com.group13.www.today_list_insert.action.CLICK";
    public static final String ACTION_NEXT = "com.group13.www.today_list_insert.action.NEXT";
    public static final String ACTION_PREV = "com.group13.www.today_list_insert.action.PREV";
   static MyDBHelper mHelper;
   static SQLiteDatabase db;

   static SharedPreferences.Editor editor;
   static SharedPreferences setting;

   static Cursor cursor1,cursor2,cursor3;
   static Cursor cursorN,cursorP,max,min;

    static int num,numN,numP,numMax,numMin;

    static int id;
   static boolean n=false,p=false;
    //com.group13.www.today_list_insert.android.appwidget.action.CLICK
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        setting=context.getSharedPreferences("widget",Context.MODE_PRIVATE);
        editor=setting.edit();
        String action = intent.getAction();
        if(action!=null&&action.equals(ACTION_CLICK)){
            //int id=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
           // updateAppWidget(context,AppWidgetManager.getInstance(context),id);
            onUpdate(context);
        }
        else if(action!=null&&action.equals(ACTION_NEXT)){
            editor.putBoolean("N",true);
            editor.commit();
            int numN=setting.getInt("numN",0);
            if(numMax!=(numN+1))
             num=numN;
            int id=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
            updateAppWidget(context,AppWidgetManager.getInstance(context),id);
            //onUpdate(context,num);
        }
        else if(action!=null&&action.equals(ACTION_PREV)){
           editor.putBoolean("P",true);
            editor.commit();

            int numP=setting.getInt("numP",0);
            if(numMin!=(numP-1))
             num=numP;
            int id=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
            updateAppWidget(context,AppWidgetManager.getInstance(context),id);
            //onUpdate(context,num);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todaylist_widget);
        setting=context.getSharedPreferences("widget",Context.MODE_PRIVATE);

        n=setting.getBoolean("N",false);
        p=setting.getBoolean("P",false);
        setUpdate(views,context);
        editor=setting.edit();



        editor.putInt("numN",numN);
        editor.putInt("numP",numP);
        editor.commit();
        Intent intent = new Intent(context, ReadActivity.class);
        Intent itP=new Intent(context,Todaylist_widget.class);
        Intent itN=new Intent(context,Todaylist_widget.class);
        Intent itR=new Intent(context,Todaylist_widget.class);

        itP.setAction(ACTION_PREV);
        itN.setAction(ACTION_NEXT);
        itR.setAction(ACTION_CLICK);
        intent.putExtra("num",num);
        //intent.putExtra("widget",true);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        PendingIntent pendingIntentP=PendingIntent.getBroadcast(context,0,itP,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentN=PendingIntent.getBroadcast(context,0,itN,PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntentR=PendingIntent.getBroadcast(context,0,itR,PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.tvDate_w, pendingIntent);
        //views.setOnClickPendingIntent(R.id.btnPrev,pendingIntentP);
        //views.setOnClickPendingIntent(R.id.btnNext,pendingIntentN);
        views.setOnClickPendingIntent(R.id.btnRefresh,pendingIntentR);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    public static void setUpdate(RemoteViews views,Context context){
        setting=context.getSharedPreferences("widget",Context.MODE_PRIVATE);
        String date="";
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
        String currentdate= df.format(new Date());
        if(currentdate.charAt(5)=='0'){
            String s1=currentdate.substring(0,5);
            String s2=currentdate.substring(6);
            currentdate=s1+s2;
        }
        //int id=0;
        int count=0,countT=0;
        try {
            mHelper = new MyDBHelper(context);
            db = mHelper.getWritableDatabase();

            max=db.rawQuery(String.format("select max(_id) as 'm' from %s", TABLE_NAME1), null);
            min=db.rawQuery(String.format("select min(_id) as 'm' from %s", TABLE_NAME1), null);
            while(max.moveToNext())
                numMax=max.getInt(max.getColumnIndex("m"));
            while(min.moveToNext())
                numMin=min.getInt(min.getColumnIndex("m"));

            //num=numMin;
            /*
            if(n){

            }else if(p){

            }else {
            */
                cursor1 = db.rawQuery(String.format("select * from %s where date='%s'", TABLE_NAME1, currentdate), null);

                while (cursor1.moveToNext()) {
                    id = cursor1.getInt(cursor1.getColumnIndex("_id"));
                    String title = cursor1.getString(cursor1.getColumnIndex("title"));
                    date = cursor1.getString(cursor1.getColumnIndex("date"));
                }

                num = id;
            //}
            cursor2 = db.rawQuery(String.format("select count(*) as 'c' from %s where _id_m=%d", TABLE_NAME2, num), null);
            cursor3=db.rawQuery(String.format("select count(*) as 'c' from %s where _id_m=%d and _check='T'", TABLE_NAME2, num), null);

            cursorN=db.rawQuery(String.format("select _id from %s where _id>%s order by _id limit 1", TABLE_NAME1, num), null);
            cursorP=db.rawQuery(String.format("select _id from %s where _id<%s order by _id desc limit 1", TABLE_NAME1, num), null);


            while (cursor2.moveToNext())
                count=cursor2.getInt(cursor2.getColumnIndex("c"));
            while (cursor3.moveToNext())
                countT=cursor3.getInt(cursor3.getColumnIndex("c"));

            while (cursorN.moveToNext())
                numN=cursorN.getInt(cursorN.getColumnIndex("_id"));
            while (cursorP.moveToNext())
                numP=cursorP.getInt(cursorP.getColumnIndex("_id"));

            /*
            ArrayList<String> alarm=new ArrayList<>();
            Cursor cursor1= db.rawQuery(String.format("select _id from %s where date='%s'", TABLE_NAME1, currentdate), null);
            int id_m=0;
            while (cursor1.moveToNext())
                id_m=cursor1.getInt(0);
            Cursor cursor2=db.rawQuery(String.format("select _alarm from %s where _id_m=%d", TABLE_NAME2, id_m), null);
            while (cursor2.moveToNext()){
                String alarm_=cursor2.getString(0);
                alarm.add(alarm_);
            }
            String _alarm=alarm.get(i);
            int idx=_alarm.indexOf(":");
            String hour_s=_alarm.substring(0,idx-1);
            String min_s=_alarm.substring(idx+2);
            int hour=Integer.parseInt(hour_s);
            int minute=Integer.parseInt(min_s);*/

        }catch (Exception e){
            date=e+"";
        }
        if(num!=0){
            views.setViewVisibility(R.id.llgood,VISIBLE);
            views.setViewVisibility(R.id.llbad,GONE);

        }else{
            views.setViewVisibility(R.id.llgood,GONE);
            views.setViewVisibility(R.id.llbad,VISIBLE);
        }

        views.setTextViewText(R.id.tvDate_w, date);
        views.setTextViewText(R.id.tvC,count+"");
        views.setTextViewText(R.id.tvT,countT+"");
        views.setTextViewText(R.id.tvF,(count-countT)+"");
        //임시

        //views.setTextViewText(R.id.tvDate_w, currentdate);
        /*
        views.setTextViewText(R.id.tvC,num+"");
        views.setTextViewText(R.id.tvT,numN+"");
        views.setTextViewText(R.id.tvF,numP+"");
        */
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
           // RemoteViews remoteViews=updateWidgetListView(context,appWidgetId);
            //appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
           updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        /*
        cursor=mCursor(context);
        myAdapter = new MyCursorAdapter(context.getApplicationContext(), cursor);
        lv.setAdapter(myAdapter);
        */
        //updateWidgetContent(context,appWidgetManager,appWidgetIds);
    }
    private void onUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);

        // Uses getClass().getName() rather than MyWidget.class.getName() for
        // portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }
    private void onUpdate(Context context,int num) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);
        this.num=num;
        // Uses getClass().getName() rather than MyWidget.class.getName() for
        // portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {

        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

