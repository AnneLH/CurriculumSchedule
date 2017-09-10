package android.practice.curriculumschedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WorkRestScheduleActivity extends AppCompatActivity {
    private static final String TAG = "WorkRestScheduleActivit";
    private IntentFilter intentFilter = null;
    private TimeTickReceiver tickReceiver = null;
    private TextView[]  txt_left    = null,
                        txt_right   = null;
    private static String sWeek = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_rest_schedule);
        Log.d(TAG, "onCreate: ==============");
        initTextView();
        if(!sWeek.equals("星期六") && !sWeek.equals("星期日")){
            refreshColorShow();
            intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
            tickReceiver = new TimeTickReceiver();
            registerReceiver(tickReceiver,intentFilter);}
    }

    private void initTextView(){
        txt_left = new TextView[20];
        txt_right = new TextView[20];
        txt_left[0] = (TextView)findViewById(R.id.txt_0L);
        txt_left[1] = (TextView)findViewById(R.id.txt_1L);
        txt_left[2] = (TextView)findViewById(R.id.txt_2L);
        txt_left[3] = (TextView)findViewById(R.id.txt_3L);
        txt_left[4] = (TextView)findViewById(R.id.txt_4L);
        txt_left[5] = (TextView)findViewById(R.id.txt_5L);
        txt_left[6] = (TextView)findViewById(R.id.txt_6L);
        txt_left[7] = (TextView)findViewById(R.id.txt_7L);
        txt_left[8] = (TextView)findViewById(R.id.txt_8L);
        txt_left[9] = (TextView)findViewById(R.id.txt_9L);
        txt_left[10] = (TextView)findViewById(R.id.txt_10L);
        txt_left[11] = (TextView)findViewById(R.id.txt_11L);
        txt_left[12] = (TextView)findViewById(R.id.txt_12L);
        txt_left[13] = (TextView)findViewById(R.id.txt_13L);
        txt_left[14] = (TextView)findViewById(R.id.txt_14L);
        txt_left[15] = (TextView)findViewById(R.id.txt_15L);
        txt_left[16] = (TextView)findViewById(R.id.txt_16L);
        txt_left[17] = (TextView)findViewById(R.id.txt_17L);
        txt_left[18] = (TextView)findViewById(R.id.txt_18L);
        txt_left[19] = (TextView)findViewById(R.id.txt_19L);
        txt_right[0] = (TextView)findViewById(R.id.txt_0R);
        txt_right[1] = (TextView)findViewById(R.id.txt_1R);
        txt_right[2] = (TextView)findViewById(R.id.txt_2R);
        txt_right[3] = (TextView)findViewById(R.id.txt_3R);
        txt_right[4] = (TextView)findViewById(R.id.txt_4R);
        txt_right[5] = (TextView)findViewById(R.id.txt_5R);
        txt_right[6] = (TextView)findViewById(R.id.txt_6R);
        txt_right[7] = (TextView)findViewById(R.id.txt_7R);
        txt_right[8] = (TextView)findViewById(R.id.txt_8R);
        txt_right[9] = (TextView)findViewById(R.id.txt_9R);
        txt_right[10] = (TextView)findViewById(R.id.txt_10R);
        txt_right[11] = (TextView)findViewById(R.id.txt_11R);
        txt_right[12] = (TextView)findViewById(R.id.txt_12R);
        txt_right[13] = (TextView)findViewById(R.id.txt_13R);
        txt_right[14] = (TextView)findViewById(R.id.txt_14R);
        txt_right[15] = (TextView)findViewById(R.id.txt_15R);
        txt_right[16] = (TextView)findViewById(R.id.txt_16R);
        txt_right[17] = (TextView)findViewById(R.id.txt_17R);
        txt_right[18] = (TextView)findViewById(R.id.txt_18R);
        txt_right[19] = (TextView)findViewById(R.id.txt_19R);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tickReceiver != null){
            unregisterReceiver(tickReceiver);
            tickReceiver = null;}
    }

    public static void activityStart(Context context,String sweek){
        Intent intent = new Intent(context,WorkRestScheduleActivity.class);
        sWeek = sweek;
        context.startActivity(intent);
    }

    private void refreshColorShow(){
        Log.d(TAG, "refreshColorShow: =========");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timeNow = sdf.format(c.getTime());
        String timeBegin,timeEnd,time;
        String[] temp;
        //判断当前时间在哪个时间区域内，则将这个时间区域对应的TextView的背景色变为设定的颜色。
        for(int i = 0; i < txt_left.length; i++){
            time = txt_left[i].getText().toString().trim();
            temp = time.split("-");
            timeBegin = temp[0].trim();
            timeEnd   = temp[1].trim();
            if(MainActivity.timeCompare(timeNow,timeBegin) &&
                    MainActivity.timeCompare(timeEnd,timeNow)){
                txt_left[i].setBackgroundColor(Color.GREEN);
                txt_right[i].setBackgroundColor(Color.GREEN);
                break;
            }else{
                txt_left[i].setBackgroundColor(Color.WHITE);
                txt_right[i].setBackgroundColor(Color.WHITE);
            }
        }
    }

    class TimeTickReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新显示颜色。
            refreshColorShow();
        }
    }
}
