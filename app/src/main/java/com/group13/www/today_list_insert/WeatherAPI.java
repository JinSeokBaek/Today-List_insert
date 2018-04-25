package com.group13.www.today_list_insert;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by admin on 2017-06-06.
 */

public class WeatherAPI {
    String serviceUrl="http://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=";
    String serviceKey="1a0c9b30454076241d26f23557ca7566";
    String strUrl;
    String date;
    String day,weather_name,weather_num,temp_min,temp_max,regin;
    boolean rtype=false;
    double lat,lon;
    public WeatherAPI(double lat,double lon,String date){
        strUrl=serviceUrl+serviceKey+
                "&lat="+lat+
                "&lon="+lon+
                "&mode=xml" +
                "&units=metric"+
                "&cnt=" + 15;
        this.date=date;
        getWeather();
    }
    public WeatherAPI(String regin,String date){
        //getCoo(regin);
        strUrl=serviceUrl+serviceKey+
                "&lat="+lat+
                "&lon="+lon+
                "&mode=xml" +
                "&units=metric"+
                "&cnt=" + 15;
        this.date=date;
        getWeather();
    }
    public int getIcon(int num){
        //int weather_num_int=Integer.parseInt(weather_num);
        int image;
        if(num>=200&&num<=531)
            image=R.drawable.rain;
        else if(num>=600&&num<=622)
            image=R.drawable.snow;
        else if(num>=701&&num<=781)
            image=R.drawable.etc;
        else if(num==800)
            image=R.drawable.sun;
        else if(num>=801)
            image=R.drawable.cloud;
        else
            image=R.drawable.sun;
        return image;
    }
    public String getDay(){
        return day;
    }
    public String getWeather_name(){

        return weather_name;
    }
    public String getWeather_num(){

        return weather_num;
    }
    public String getTemp_min(){

        return temp_min;
    }
    public String getTemp_max(){

        return temp_max;
    }
    public String getRegin(){
        return regin;
    }
    public void getWeather(){
        new DownloadTask_weather().execute(strUrl);
    }
   private class DownloadTask_weather extends AsyncTask<String ,Void,String>{
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
       }

       @Override
       protected void onPostExecute(String s) {
           parseXml(s);
           toHanguel();
       }

       @Override
       protected void onProgressUpdate(Void... values) {
           super.onProgressUpdate(values);
       }

       @Override
       protected String doInBackground(String... params) {
            try{
                return (String)downloadUrl(params[0]);
            }catch (IOException e){
                return "다운로드실패";
            }
       }
       private String downloadUrl(String myurl)throws IOException{
           HttpURLConnection conn=null;
           try{
               URL url=new URL(myurl);
               conn=(HttpURLConnection)url.openConnection();
               InputStream is=conn.getInputStream();
               BufferedInputStream bufIs=new BufferedInputStream(is);
               InputStreamReader isReader=new InputStreamReader(bufIs,"utf-8");
               BufferedReader bufReader=new BufferedReader(isReader);

               String line=null;
               StringBuilder builder=new StringBuilder();
               while((line=bufReader.readLine())!=null){
                   builder.append(line);
               }
               return builder.toString();
           }finally {
               conn.disconnect();
           }
       }
        private void parseXml(String result){
            try {
                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser=factory.newPullParser();
                parser.setInput(new StringReader(result));

                int parserEvent=parser.getEventType();
                //StringBuilder builder=new StringBuilder();
                boolean equalday = false;
                while (parserEvent != XmlPullParser.END_DOCUMENT) {

                   if (parserEvent == XmlPullParser.START_TAG) {
                       if(parser.getName().equals("name")){
                          rtype=true;
                       }else if(parser.getName().equals("time")){
                            day = parser.getAttributeValue(null, "from");
                            daytoSimple();
                            if(day.equals(date))
                                equalday=true;
                        }
                        else if (parser.getName().equals("symbol")) {
                            weather_num = parser.getAttributeValue(null, "number");
                            weather_name = parser.getAttributeValue(null, "name");

                        } else if (parser.getName().equals("temperature")) {
                            temp_min = parser.getAttributeValue(null, "min");
                            temp_max = parser.getAttributeValue(null, "max");
                            if(equalday)
                                break;
                        }
                    }else if(parserEvent==XmlPullParser.TEXT){
                       if(rtype){
                           regin=parser.getText();
                           rtype=false;
                       }
                   }
                    //}
                    parserEvent = parser.next();
                }
            }catch (Exception e){

            }
        }
        public void daytoSimple(){
            int idx=day.indexOf("T");
            String simpleday=day.substring(0,idx);
            day=simpleday;
        }

        public void toHanguel(){
            int weather_num_int=Integer.parseInt(weather_num);
            String n="";
            //노가다노가다노간나리ㅓㅣㄹㄴ아
            switch (weather_num_int){
                case 200: n="번개와 보슬비"; break;
                case 201: n="번개와 비"; break;
                case 202: n="번개와 집중호우"; break;
                case 210: n="번개와 천둥"; break;
                case 211: n="천둥 번개"; break;
                case 212: n="강한 천둥 번개"; break;
                case 221: n="매우 강한 천둥 번개"; break;
                case 230: n="번개와 가벼운 이슬비"; break;
                case 231: n="번개와 이슬비"; break;
                case 232: n="번개와 집중호우"; break;
                case 300: n="약한 이슬비";break;
                case 301: n="이슬비";break;
                case 302:n="강한 이슬비";break;
                case 310:n="약한 이슬비"; break;
                case 311:n="이슬비";break;
                case 312:n="강한 이슬비";break;
                case 313:n="소나기";
                case 314:n="강한 소나기";break;
                case 321:n="소나기";break;
                case 500:n="가벼운 비";break;
                case 501:n="비";break;
                case 502:n="강한 비";break;
                case 503:n="집중 호우";break;
                case 504:n="집중 호우";break;
                case 511:n="어는 비";break;
                case 520:n="가벼운 소나기";break;

                case 521:n ="소나기";break;
                case 522 :n="강한 소나기";break;
                case 531 :n="매우 강한 소나기";
                case 600 :n="약한 눈"  ;break;
                case 601 :n="눈"  ;break;
                case 602 :n="거센 눈"  ;break;
                case 611 :n="진눈 깨비"  ;break;
                case 612 :n="급 진눈 깨비"  ;break;
                case 615 :n="약한 눈과 비"  ;break;
                case 616 :n="눈과 비"  ;break;
                case 620 :n="눈"  ;break;
                case 621 :n="소낙눈"  ;break;
                case 622 :n="강한 소낙눈"  ;break;

                case 701 :n="안개"  ;break;
                case 711 :n="연기"  ;break;
                case 721 :n="실안개"  ;break;
                case 731 :n="황사 바람"  ;break;
                case 741 :n="안개"  ;break;
                case 751 :n="황사"  ;break;
                case 761 :n="황사"  ;break;
                case 762 :n="화산재"  ;break;
                case 771 :n="돌풍"  ;break;
                case 781 :n="태풍"  ;break;

                case 800 :n="맑은 하늘" ;break;
                case 801 :n="구름 조금";break;
                case 802 :n="조각 구름" ;break;
                case 803 :n="조각 구름"  ;break;
                case 804 :n="흐림"  ;break;


            }
            //우산: 622보다 작으면 전부
            weather_name=n;
        }

   }
}
