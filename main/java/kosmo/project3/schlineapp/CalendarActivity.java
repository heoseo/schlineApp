package kosmo.project3.schlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    String TAG = "CalendarActivity";

    String clickDate;//체크한 날짜
    public String fname=null;
    public String str=null;
    public CalendarView calendarView;
    public Button cha_Btn,del_Btn,save_Btn;
    public TextView diaryTextView,textView2,textView3, text_cal;
    public EditText contextEditText;



    ArrayList<Calendar> sc = new ArrayList<Calendar>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        calendarView=findViewById(R.id.calendarView);
        diaryTextView=findViewById(R.id.diaryTextView);
        save_Btn=findViewById(R.id.save_Btn);
        del_Btn=findViewById(R.id.del_Btn);
        cha_Btn=findViewById(R.id.cha_Btn);
        textView2=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);
        contextEditText=findViewById(R.id.contextEditText);
        //추가부분
        text_cal = findViewById(R.id.text_cal);

        //이름 설정
        //textView3.setText(StaticUserInformation.userID+" 일정표");





        //날짜 변화가 생겼을때
        calendarView.setOnDateChangeListener(
                new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);

                text_cal.setVisibility(View.VISIBLE);

                textView2.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);

                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));

                contextEditText.setText("");
                //checkDay(year,month,dayOfMonth,StaticUserInformation.userID,"안녕");
                if(month<9) {
                    if(dayOfMonth<10) {
                        clickDate = String.format("%d-0%d-0%d", year, month + 1, dayOfMonth);//체크한날
                    }else{
                        clickDate = String.format("%d-0%d-%d", year, month + 1, dayOfMonth);//체크한날
                    }
                }
                else{
                    clickDate = String.format("%d-%d-%d", year, month + 1, dayOfMonth);//체크한날
                    if(dayOfMonth<10) {
                        clickDate = String.format("%d-0%d-0%d", year, month + 1, dayOfMonth);//체크한날
                    }else{
                        clickDate = String.format("%d-0%d-%d", year, month + 1, dayOfMonth);//체크한날
                    }
                }
                Log.i(TAG, "체크날1="+clickDate);
                //서버의 spring mybatis로 인해 쿼리실행후 결과값받아옴.
                new AsyncHttpServer().execute(
                        "http://"+StaticInfo.my_ip+"/schline/android/examList.do",
                        "user_id="+StaticUserInformation.userID,
                        "year="+year,
                        "month="+(month+1),
                        "day="+dayOfMonth
                );
            }
        });

        //저장 버튼 눌렀을때
        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveDiary(fname);
                str=contextEditText.getText().toString();
                textView2.setText(str);
                save_Btn.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
            }
        });
    }


    public void  checkDay(int cYear,int cMonth,int cDay,String userID, String text){
        fname=""+userID+cYear+"-"+(cMonth+1)+""+"-"+cDay+".txt";//저장할 파일 이름설정
        FileInputStream fis=null;//FileStream fis 변수*/


        Log.i(TAG, "넘어온 text"+text);
        Log.i(TAG, "넘어온 cYear"+cYear+cMonth+cDay);


        try{
        fis=openFileInput(fname);

        byte[] fileData=new byte[fis.available()];
        fis.read(fileData);
        fis.close();

        str=new String(fileData);

        contextEditText.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText(str);//기존 저장된 값 불러오기

  /*          String k ="";
            for(int i=0; i<text.length ; i++){
                k += text[i]+"\n";
                text_cal.setText(k);
            }*/

            //왜 작동을 안하지????
            //text_cal.setVisibility(View.VISIBLE);
            //text_cal.setText(text);

        save_Btn.setVisibility(View.INVISIBLE);
        cha_Btn.setVisibility(View.VISIBLE);
        del_Btn.setVisibility(View.VISIBLE);

        cha_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                contextEditText.setText(str);

                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                textView2.setText(contextEditText.getText());
            }
        });

        //삭제버튼
            del_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView2.setVisibility(View.INVISIBLE);
                    contextEditText.setText("");
                    contextEditText.setVisibility(View.VISIBLE);
                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    removeDiary(fname);
                }
            });
            if(textView2.getText()==null){
                textView2.setVisibility(View.INVISIBLE);
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content="";
            fos.write((content).getBytes());
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content=contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /////////////캘린더 서버연결
    class AsyncHttpServer extends AsyncTask<String, Void, String>
    {
        protected void onPreExecute() {
            super.onPreExecute();
        }
        //doInBackground -스레드에 의해 처리될 내용을 담기위한 함수.
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "캘린더 doInBackground진입함.");
            //스프링 서버에서 반환하는 JSON데이터를 저장할 변수
            StringBuffer receiveData = new StringBuffer();

            try {
                URL url = new URL(strings[0]);// 파라미터1 : 요청URL
                Log.i(TAG, "url > " + url);
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                conn.setDoOutput(true);

                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes("UTF-8")); //파라미터2 :사용자아이디.
                out.write("&".getBytes("UTF-8"));//&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[2].getBytes("UTF-8")); //파라미터3 : 사용자가클릭한 년월.
                out.write("&".getBytes("UTF-8"));//&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[3].getBytes("UTF-8")); //파라미터4 : 사용자가클릭한 년월.
                out.write("&".getBytes("UTF-8"));//&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[4].getBytes("UTF-8")); //파라미터4 : 사용자가클릭한 년월.
                out.flush();// 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
                out.close();// 출력 스트림을 닫고 모든 시스템 자원을 해제.

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    Log.i(TAG, "캘린더 HTTP OK 성공");

                    //스프링 서버에 연결성공한 경우 JSON데이터를 읽어서 저장한다.
                    // [2-4]. 읽어온 결과물 리턴.
                    // 요청한 URL의 출력물을 BufferedReader로 받는다.
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );
                    // 출력물의 라인과 그 합에 대한 변수.
                    String responseData;
                    // 라인을 받아와 합친다.
                    while ((responseData = reader.readLine()) != null) {
                        receiveData.append(responseData + "\r\n");
                        Log.i("TAG","캘린더receiveData="+receiveData.toString());
                    }
                    reader.close();

                } else {
                    Log.d(TAG, "HTTP_OK 안됨. 연결실패.");
                }

                //################성공후##################################
                //읽어온 JSON데이터를 로그로 출력
                Log.i(TAG,receiveData.toString());
                //먼저 JSON배열로 파싱.
                JSONArray jsonArray = new JSONArray(receiveData.toString());
                //StringBuffer 객체를 비움
                Log.i("TAG","체크날2="+clickDate);

                receiveData.setLength(0);

                //배열 크기만큼 반복
                for(int i=0; i<jsonArray.length(); i++) {
                    //배열의 요소는 객체이므로 JSON객체로 파싱
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.i("TAG","체크날3="+clickDate);
                    Log.i(TAG, "jsonObject.getString(exam_date)1 > " + jsonObject.getString("exam_date"));

                    if(jsonObject.getString("exam_date").equals(clickDate)){//날짜 넣어주기..
                        Log.i(TAG, "jsonObject.getString(exam_date)2 > " + jsonObject.getString("exam_date"));

                        //////이게맞나 ??
                        //diaryTextView.setText("일정있음");


                        //////여기에 값 넣어주기
                        String text = jsonObject.getString("exam_name");//시험내용
                        String[] a = jsonObject.getString("exam_date").split("-");
                        Log.i(TAG, "text값="+text);

                        text_cal.setVisibility(View.VISIBLE);
                        text_cal.setText(text);
                        //text_cal.setBackgroundResource(R.drawable.alram);
                        //calendarView.setBackgroundResource(R.drawable.alram);

                      /*  int year = Integer.parseInt(a[0]);
                        int month = Integer.parseInt(a[1]);
                        int dayOfMonth = Integer.parseInt(a[2]);*/
                        int year = Integer.parseInt(a[0]);
                        String month = a[1];
                        String dayOfMonth = a[2];
                        String cm = month.replace("0","");
                        String cd = dayOfMonth.replace("0","");
                        int rcm = Integer.parseInt(cm);
                        int rcd = Integer.parseInt(cd);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, rcm, rcd);
                        //어레이리스트에 캘린더담기
                        sc.add(cal);


                        //calendarView.addDecorator(new EventDecorator(Color.RED, sc));

                        //text_cal.setText(text);
                        //text_cal.setVisibility(View.VISIBLE);
                        //MaterialCalendarView calendarView = findViewById(R.id.calendarView);
                        //calendarView.setSelectedDate(CalendarDay.);
                        //calendarView.addDecorator(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.today())));

                        //앞에 0 빼기


                        //checkDay(year, rcm, rcd, StaticUserInformation.userID, text);


                        //각 Key에 해당하는 값을 가져와서 StringBuffer객체에 저장.
                        receiveData.append("★ " +
                                jsonObject.getString("exam_name")+"\n");
                        receiveData.append("마감일 : " +
                                jsonObject.getString("exam_date")+"\n");
                    }
                    ArrayList<String> getExam_date;
                    ArrayList<String> getExam_name;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //로그출력
            //저장된 내용을 로그로 출력한 후 onPostExecute()로 반환한다.
            Log.i(TAG, "receiveData.toString()>> "+receiveData.toString());
            //서버에서 내려준 JSON정보를 저장한후 반환.
            /*
            파싱이 완료된 StringBuffer객체를 String으로 변환하여 반환한다.
            여기서 반환된 값은 onPostExecute()로 전달된다.
             */


            return receiveData.toString(); //onPostExecute전달
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute문 진입함.");
            Log.d(TAG, "-textResult>>>>" + text_cal);
            //결과값을 텍스트뷰에 출력한다.
            text_cal.setText(s);
        }
    }////AsyncHttpRequest 끝.



}