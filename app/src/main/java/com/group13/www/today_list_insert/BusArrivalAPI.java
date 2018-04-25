package com.group13.www.today_list_insert;

import android.os.AsyncTask;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 2017-06-18.
 */

public class BusArrivalAPI {
    String serviceUrl = "http://openapi.gbis.go.kr/ws/busarrivalservice?wsdl";
    String serviceKey = "DhmrVYeCftvLOg%2Ff17mXzZHO5lDvipMLJN7L0TzEPQ0MJ1VzYlSU7s4YZzKPrq5TbborQAuca8GrC%2BTu%2Fp%2FNHw%3D%3D";
    String strUrl;
    StringBuilder strBuilder=new StringBuilder();
    BusArrivalAPI(String stationId) {
        strUrl = serviceUrl + "?serviceKey=" + serviceKey + "&stationId=" + stationId;

        try {
            new DownloadWebPageTask().execute(strUrl);
        } catch (Exception e) {
            //stationIds[0]="";
           strBuilder.append("에러!");
        }
    }
    public StringBuilder getArrival(){
        return strBuilder;
    }
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        String routeId = "";    //노선 아이디
        String locationNo1 = "";    //첫번째 차량 위치 정보(몇 번째 전)
        String predictTime1 = "";   //첫번째 차량 도착 예상 시간(몇 분후)
        String locationNo2 = "";    //두번째 차량 위치 정보
        String predictTime2 = "";   //두번째 차량 도작 예상 시간
        @Override
        protected void onPostExecute(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();

                boolean bSet_routeId = false;
                boolean bSet_locationNo1 = false;
                boolean bSet_predictTime1 = false;
                boolean bSet_locationNo2 = false;
                boolean bSet_predictTime2 = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("routeId")) {
                            bSet_routeId = true;
                        }
                        if (tag_name.equals("locationNo1")) {
                            bSet_locationNo1 = true;
                        }
                        if (tag_name.equals("predictTime1")) {
                            bSet_predictTime1 = true;
                        }
                        if (tag_name.equals("locationNo2")) {
                            bSet_locationNo2 = true;
                        }
                        if (tag_name.equals("predictTime2")) {
                            bSet_predictTime2 = true;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_routeId) {
                            routeId = xpp.getText();
                            strBuilder.append("버스 아이디 : " + routeId + "\n");
                            bSet_routeId = false;
                        }
                        if (bSet_locationNo1) {
                            locationNo1 = xpp.getText();
                            strBuilder.append(locationNo1 + "번째 전 ");
                            bSet_locationNo1 = false;
                        }
                        if (bSet_predictTime1) {
                            predictTime1 = xpp.getText();
                            strBuilder.append(predictTime1 + "분 후 도착\n");
                            bSet_predictTime1 = false;
                        }
                        if (bSet_locationNo2) {
                            locationNo2 = xpp.getText();
                            strBuilder.append(locationNo2 + "번째 전 ");
                            bSet_locationNo2 = false;
                        }
                        if (bSet_predictTime2) {
                            predictTime2 = xpp.getText();
                            strBuilder.append(predictTime2 + "분 후 도착\n");
                            bSet_predictTime2 = false;
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return (String) downloadUrl((String) params[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));

                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }
                return page;
            } finally {
                conn.disconnect();
            }
        }
    }
}