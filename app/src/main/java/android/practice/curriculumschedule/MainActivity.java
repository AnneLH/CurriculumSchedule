package android.practice.curriculumschedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private static final String DayOfWeek_1 = "Monday";
    private static final String DayOfWeek_2 = "Tuesday";
    private static final String DayOfWeek_3 = "Wednesday";
    private static final String DayOfWeek_4 = "Thursday";
    private static final String DayOfWeek_5 = "Friday";
    private IntentFilter intentFilter = null;
    private WhenTimeChangeReceiver timeChangeReceiver = null;
    private String  errMsg      = null,
                    sToday      = null,
                    sWeek       = null,
                    sCurHHmm    = null;
    private int     old_color   = 0;
    private Button  btn_wrSch    = null;
    private TextView    txt_date = null,
                        old_text = null;
    private TextView[]  txt_monday      = null,
                        txt_tuesday     = null,
                        txt_wednesday   = null,
                        txt_thursday    = null,
                        txt_friday      = null;
    private Boolean bEditMode    = false;
    private List<Schedule> scheduleList = null;
    private List<WorkRest> workrestList = null;
    private List<Curriculum> curriculumList = null;
    private String[] curriculums = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ================");
        btn_wrSch   = (Button)findViewById(R.id.btn_work_rest_schedule);
        btn_wrSch.setText("进入作息时间表");
        btn_wrSch.setOnClickListener(this);
        txt_date    = (TextView)findViewById(R.id.txt_date_main);
        initTextGroup();
        getCurrentDate();
        //检查WorkRest表是否存在，如果不存在，则创建。
        workrestList = DataSupport.findAll(WorkRest.class);
        if(workrestList == null || workrestList.size() == 0){
            //第一次运行程序，课程名称的表尚未建立。此处需要建立WorkRest表，并初始化。
            createWorkRest();
        }
    }

    class WhenTimeChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            judgeTimeAndSetColor();
        }
    }
    private void judgeTimeAndSetColor(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sCurHHmm = sdf.format(cal.getTime());
        String sBegin,sEnd,sName;
        int passNumber = 7;
        for(int i = workrestList.size()-1; i > 1; i--){
            sBegin  = workrestList.get(i).getTimeBegin();
            sEnd    = workrestList.get(i).getTimeEnd();
            sName   = workrestList.get(i).getName();
            Log.d(TAG, "judgeTimeAndSetColor: ========"+ sName.substring(0,1));
            if(timeCompare(sCurHHmm,sEnd)){
                //当前时间大于课程结束时间，结束循环。
                //将今天的全部课程都置为已经上的课的颜色。
                setPassedBackcolor(1,7,0xff00ced1);
                break;
            }else{
                //当前时间小于课程结束时间，检查是否小于本课程开始时间。
                if(timeCompare(sCurHHmm,sBegin)){
                    //当前时间在本次课程开始和结束时间之间（本次课程正在进行时）
                    //已经上的课程颜色为<color name="darkturquoise">#00ced1</color> <!-- 暗宝石绿 -->
                    //正在上的课程颜色为<color name="lime">#00ff00</color> <!-- 酸橙色 -->
                    setPassedBackcolor(1,passNumber - 1,0xff00ced1);
                    if(sName.substring(0,1).equals("第")){
                        setPassedBackcolor(passNumber,passNumber,0xff00ff00);
                    }else{
                        setPassedBackcolor(passNumber,passNumber,0xff00ced1);
                    }
                    break;
                }else{
                    //当前时间小于本次课程的开始时间，说明本节课未开始，置标志。
                    if(sName.substring(0,1).equals("第"))
                        passNumber -= 1;
                }
            }
        }
        Log.d(TAG, "judgeTimeAndSetColor: passnumber = " + passNumber);
    }

    private void setPassedBackcolor(int iFrom,int iTo,int iColor){
        Log.d(TAG, "setPassedBackcolor: from = " + iFrom + ", to = " + iTo + ", iColor = " + iColor);
        for(int j = iFrom; j <= iTo; j++){
            switch (sWeek){
                case "星期一":
                    txt_monday[j].setBackgroundColor(iColor);
                    break;
                case "星期二":
                    txt_tuesday[j].setBackgroundColor(iColor);
                    break;
                case "星期三":
                    txt_wednesday[j].setBackgroundColor(iColor);
                    break;
                case "星期四":
                    txt_thursday[j].setBackgroundColor(iColor);
                    break;
                case "星期五":
                    txt_friday[j].setBackgroundColor(iColor);
                    break;
                default:
                    Log.i(TAG, "run: unknow!!!!!!!!!==");
            }
        }
    }
    private void initTextGroup(){
        txt_monday = new TextView[8];
        txt_monday[0] = (TextView)findViewById(R.id.txt_monday_0);
        txt_monday[1] = (TextView)findViewById(R.id.txt_monday_1);
        txt_monday[2] = (TextView)findViewById(R.id.txt_monday_2);
        txt_monday[3] = (TextView)findViewById(R.id.txt_monday_3);
        txt_monday[4] = (TextView)findViewById(R.id.txt_monday_4);
        txt_monday[5] = (TextView)findViewById(R.id.txt_monday_5);
        txt_monday[6] = (TextView)findViewById(R.id.txt_monday_6);
        txt_monday[7] = (TextView)findViewById(R.id.txt_monday_7);
        txt_tuesday = new TextView[8];
        txt_tuesday[0] = (TextView)findViewById(R.id.txt_tuesday_0);
        txt_tuesday[1] = (TextView)findViewById(R.id.txt_tuesday_1);
        txt_tuesday[2] = (TextView)findViewById(R.id.txt_tuesday_2);
        txt_tuesday[3] = (TextView)findViewById(R.id.txt_tuesday_3);
        txt_tuesday[4] = (TextView)findViewById(R.id.txt_tuesday_4);
        txt_tuesday[5] = (TextView)findViewById(R.id.txt_tuesday_5);
        txt_tuesday[6] = (TextView)findViewById(R.id.txt_tuesday_6);
        txt_tuesday[7] = (TextView)findViewById(R.id.txt_tuesday_7);
        txt_wednesday = new TextView[8];
        txt_wednesday[0] = (TextView)findViewById(R.id.txt_wednesday_0);
        txt_wednesday[1] = (TextView)findViewById(R.id.txt_wednesday_1);
        txt_wednesday[2] = (TextView)findViewById(R.id.txt_wednesday_2);
        txt_wednesday[3] = (TextView)findViewById(R.id.txt_wednesday_3);
        txt_wednesday[4] = (TextView)findViewById(R.id.txt_wednesday_4);
        txt_wednesday[5] = (TextView)findViewById(R.id.txt_wednesday_5);
        txt_wednesday[6] = (TextView)findViewById(R.id.txt_wednesday_6);
        txt_wednesday[7] = (TextView)findViewById(R.id.txt_wednesday_7);
        txt_thursday = new TextView[8];
        txt_thursday[0] = (TextView)findViewById(R.id.txt_thursday_0);
        txt_thursday[1] = (TextView)findViewById(R.id.txt_thursday_1);
        txt_thursday[2] = (TextView)findViewById(R.id.txt_thursday_2);
        txt_thursday[3] = (TextView)findViewById(R.id.txt_thursday_3);
        txt_thursday[4] = (TextView)findViewById(R.id.txt_thursday_4);
        txt_thursday[5] = (TextView)findViewById(R.id.txt_thursday_5);
        txt_thursday[6] = (TextView)findViewById(R.id.txt_thursday_6);
        txt_thursday[7] = (TextView)findViewById(R.id.txt_thursday_7);
        txt_friday = new TextView[8];
        txt_friday[0] = (TextView)findViewById(R.id.txt_friday_0);
        txt_friday[1] = (TextView)findViewById(R.id.txt_friday_1);
        txt_friday[2] = (TextView)findViewById(R.id.txt_friday_2);
        txt_friday[3] = (TextView)findViewById(R.id.txt_friday_3);
        txt_friday[4] = (TextView)findViewById(R.id.txt_friday_4);
        txt_friday[5] = (TextView)findViewById(R.id.txt_friday_5);
        txt_friday[6] = (TextView)findViewById(R.id.txt_friday_6);
        txt_friday[7] = (TextView)findViewById(R.id.txt_friday_7);
        for(int i = 1;i<=7;i++){
            txt_monday[i].setOnClickListener(this);
            txt_tuesday[i].setOnClickListener(this);
            txt_wednesday[i].setOnClickListener(this);
            txt_thursday[i].setOnClickListener(this);
            txt_friday[i].setOnClickListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeChangeReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(timeChangeReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ===========");
        refreshSchedule();
        bEditMode = false;
        setEditMode();
        setColorOfToday();
        intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        timeChangeReceiver = new WhenTimeChangeReceiver();
        registerReceiver(timeChangeReceiver,intentFilter);
    }

    private void getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                sWeek = "星期日";break;
            case Calendar.MONDAY:
                sWeek = "星期一";break;
            case Calendar.TUESDAY:
                sWeek = "星期二";break;
            case Calendar.WEDNESDAY:
                sWeek = "星期三";break;
            case Calendar.THURSDAY:
                sWeek = "星期四";break;
            case Calendar.FRIDAY:
                sWeek = "星期五";break;
            case Calendar.SATURDAY:
                sWeek = "星期六";break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sToday = sdf.format(calendar.getTime());
        txt_date.setText(sToday + "  " + sWeek);
    }

    private void createSchedule(){
        Log.i(TAG, "createSchedule: ==========");
        try{
            Schedule schedule1 = new Schedule();
            schedule1.setClassId(1);
            schedule1.setMonday(getString(R.string.str_YuWen));
            schedule1.setTuesday(getString(R.string.str_YuWen));
            schedule1.setWednesday(getString(R.string.str_YuWen));
            schedule1.setThursday(getString(R.string.str_YuWen));
            schedule1.setFriday(getString(R.string.str_YuWen));
            schedule1.saveThrows();
            Schedule schedule2 = new Schedule();
            schedule2.setClassId(2);
            schedule2.setMonday(getString(R.string.str_YuWen));
            schedule2.setTuesday(getString(R.string.str_YuWen));
            schedule2.setWednesday(getString(R.string.str_YuWen));
            schedule2.setThursday(getString(R.string.str_YuWen));
            schedule2.setFriday(getString(R.string.str_YuWen));
            schedule2.saveThrows();
            Schedule schedule3 = new Schedule();
            schedule3.setClassId(3);
            schedule3.setMonday(getString(R.string.str_YuWen));
            schedule3.setTuesday(getString(R.string.str_YuWen));
            schedule3.setWednesday(getString(R.string.str_YuWen));
            schedule3.setThursday(getString(R.string.str_YuWen));
            schedule3.setFriday(getString(R.string.str_YuWen));
            schedule3.saveThrows();
            Schedule schedule4 = new Schedule();
            schedule4.setClassId(4);
            schedule4.setMonday(getString(R.string.str_YuWen));
            schedule4.setTuesday(getString(R.string.str_YuWen));
            schedule4.setWednesday(getString(R.string.str_YuWen));
            schedule4.setThursday(getString(R.string.str_YuWen));
            schedule4.setFriday(getString(R.string.str_YuWen));
            schedule4.saveThrows();
            Schedule schedule5 = new Schedule();
            schedule5.setClassId(5);
            schedule5.setMonday(getString(R.string.str_YuWen));
            schedule5.setTuesday(getString(R.string.str_YuWen));
            schedule5.setWednesday(getString(R.string.str_YuWen));
            schedule5.setThursday(getString(R.string.str_YuWen));
            schedule5.setFriday(getString(R.string.str_YuWen));
            schedule5.saveThrows();
            Schedule schedule6 = new Schedule();
            schedule6.setClassId(6);
            schedule6.setMonday(getString(R.string.str_YuWen));
            schedule6.setTuesday(getString(R.string.str_YuWen));
            schedule6.setWednesday(getString(R.string.str_YuWen));
            schedule6.setThursday(getString(R.string.str_YuWen));
            schedule6.setFriday(getString(R.string.str_YuWen));
            schedule6.saveThrows();
            Schedule schedule7 = new Schedule();
            schedule7.setClassId(7);
            schedule7.setMonday(getString(R.string.str_YuWen));
            schedule7.setTuesday(getString(R.string.str_YuWen));
            schedule7.setWednesday(getString(R.string.str_YuWen));
            schedule7.setThursday(getString(R.string.str_YuWen));
            schedule7.setFriday(getString(R.string.str_YuWen));
            schedule7.saveThrows();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "createSchedule: ========" + e.toString() );
        }finally {
            scheduleList = DataSupport.findAll(Schedule.class);
        }
    }

    private void createWorkRest(){
        Log.d(TAG, "createWorkRest: ============");
        try{
            WorkRest wr1 = new WorkRest();
            wr1.setMyId(1);
            wr1.setName("大课间");
            wr1.setTimeBegin("08:10");
            wr1.setTimeEnd("08:35");
            wr1.saveThrows();
            WorkRest wr2 = new WorkRest();
            wr2.setMyId(2);
            wr2.setName("晨会");
            wr2.setTimeBegin("08:35");
            wr2.setTimeEnd("08:50");
            wr2.saveThrows();
            WorkRest wr3 = new WorkRest();
            wr3.setMyId(3);
            wr3.setName("课间休息");
            wr3.setTimeBegin("08:50");
            wr3.setTimeEnd("08:55");
            wr3.saveThrows();
            WorkRest wr4 = new WorkRest();
            wr4.setMyId(4);
            wr4.setName("第一节课");
            wr4.setTimeBegin("08:55");
            wr4.setTimeEnd("09:30");
            wr4.saveThrows();
            WorkRest wr5 = new WorkRest();
            wr5.setMyId(5);
            wr5.setName("课间休息");
            wr5.setTimeBegin("09:30");
            wr5.setTimeEnd("09:40");
            wr5.saveThrows();
            WorkRest wr6 = new WorkRest();
            wr6.setMyId(6);
            wr6.setName("第二节课");
            wr6.setTimeBegin("09:40");
            wr6.setTimeEnd("10:15");
            wr6.saveThrows();
            WorkRest wr7 = new WorkRest();
            wr7.setMyId(7);
            wr7.setName("课间休息");
            wr7.setTimeBegin("10:15");
            wr7.setTimeEnd("10:25");
            wr7.saveThrows();
            WorkRest wr8 = new WorkRest();
            wr8.setMyId(8);
            wr8.setName("眼保健操");
            wr8.setTimeBegin("10:25");
            wr8.setTimeEnd("10:30");
            wr8.saveThrows();
            WorkRest wr9 = new WorkRest();
            wr9.setMyId(9);
            wr9.setName("第三节课");
            wr9.setTimeBegin("10:30");
            wr9.setTimeEnd("11:05");
            wr9.saveThrows();
            WorkRest wr10 = new WorkRest();
            wr10.setMyId(10);
            wr10.setName("课间休息");
            wr10.setTimeBegin("11:05");
            wr10.setTimeEnd("11:15");
            wr10.saveThrows();
            WorkRest wr11 = new WorkRest();
            wr11.setMyId(11);
            wr11.setName("第四节课");
            wr11.setTimeBegin("11:15");
            wr11.setTimeEnd("11:50");
            wr11.saveThrows();
            WorkRest wr12 = new WorkRest();
            wr12.setMyId(12);
            wr12.setName("写字");
            wr12.setTimeBegin("13:30");
            wr12.setTimeEnd("13:45");
            wr12.saveThrows();
            WorkRest wr13 = new WorkRest();
            wr13.setMyId(13);
            wr13.setName("课间休息");
            wr13.setTimeBegin("13:45");
            wr13.setTimeEnd("13:50");
            wr13.saveThrows();
            WorkRest wr14 = new WorkRest();
            wr14.setMyId(14);
            wr14.setName("眼保健操");
            wr14.setTimeBegin("13:50");
            wr14.setTimeEnd("13:55");
            wr14.saveThrows();
            WorkRest wr15 = new WorkRest();
            wr15.setMyId(15);
            wr15.setName("第五节课");
            wr15.setTimeBegin("13:55");
            wr15.setTimeEnd("14:30");
            wr15.saveThrows();
            WorkRest wr16 = new WorkRest();
            wr16.setMyId(16);
            wr16.setName("课间休息");
            wr16.setTimeBegin("14:30");
            wr16.setTimeEnd("14:40");
            wr16.saveThrows();
            WorkRest wr17 = new WorkRest();
            wr17.setMyId(17);
            wr17.setName("第六节课");
            wr17.setTimeBegin("14:40");
            wr17.setTimeEnd("15:15");
            wr17.saveThrows();
            WorkRest wr18 = new WorkRest();
            wr18.setMyId(18);
            wr18.setName("课间休息");
            wr18.setTimeBegin("15:15");
            wr18.setTimeEnd("15:25");
            wr18.saveThrows();
            WorkRest wr19 = new WorkRest();
            wr19.setMyId(19);
            wr19.setName("第七节课");
            wr19.setTimeBegin("15:25");
            wr19.setTimeEnd("16:00");
            wr19.saveThrows();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "createWorkRest: "+ e.toString() );
        }
    }

    //刷新课程名称表（Schedule）,将课程名称在recyclerView中显示。
    private void refreshSchedule(){
        Log.d(TAG, "refreshSchedule: ===============");
        //检查Schedule表是否存在，如果不存在，则创建。
        scheduleList = DataSupport.findAll(Schedule.class);
        if(scheduleList == null || scheduleList.size() == 0){
            //第一次运行程序，课程名称的表尚未建立。此处需要建立Schedule表，并初始化。
            createSchedule();
        }
        //将Schedule表中的数据显示在列表中。
        for(int i = 1; i <= 7; i++){
            txt_monday[i].setText(scheduleList.get(i -1).getMonday());
            txt_tuesday[i].setText(scheduleList.get(i -1).getTuesday());
            txt_wednesday[i].setText(scheduleList.get(i -1).getWednesday());
            txt_thursday[i].setText(scheduleList.get(i -1).getThursday());
            txt_friday[i].setText(scheduleList.get(i -1).getFriday());
        }
    }

    //设置当天课程的背景色
    //当天的颜色为<color name="mediumspringgreen">#00fa9a</color> <!-- 中春绿色 -->
    //正在上的课程颜色为<color name="lime">#00ff00</color> <!-- 酸橙色 -->
    //已经上的课程颜色为<color name="darkturquoise">#00ced1</color> <!-- 暗宝石绿 -->
    private void setColorOfToday(){
        Log.d(TAG, "setColorOfToday: =========");
        setPassedBackcolor(1,7,0xff00fa9a);
        judgeTimeAndSetColor();
    }

    //返回真，表示time1大于等于time2
    public boolean timeCompare(String time1,String time2){
        Log.d(TAG, "timeCompare: time1 = " + time1 + ",time2 = " + time2);
        boolean time1Bigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try{
            if(sdf.parse(time1).getTime() >= sdf.parse(time2).getTime()){
                Log.d(TAG, "timeCompare: time1Bigger");
                time1Bigger = true;
            }else{
                Log.d(TAG, "timeCompare: time2Bigger");
                time1Bigger = false;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "timeCompare: "+e.toString());
        }
        return time1Bigger;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_curriculumNameMaintenance:
                Log.d(TAG, "onOptionsItemSelected: menu_curriculumNameMaintenance");
                CurriculumMaintenanceActivity.activityStart(MainActivity.this);
                break;
            case R.id.menu_scheduleMaintenance:
                Log.d(TAG, "onOptionsItemSelected: menu_scheduleMaintenance");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("请确认")
                        .setMessage("是否进入课程表设置模式？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onOptionsItemSelected: 进入编辑模式==========");
                                bEditMode = true;
                                setEditMode();

                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bEditMode = false;
                                setEditMode();
                            }
                        }).show();
                break;
            default:
                errMsg = "没有这个选项，请联系程序管理员。";
                Log.i(TAG, "onOptionsItemSelected: " + errMsg);
                Toast.makeText(MainActivity.this,errMsg,Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    private void setEditMode(){
        Log.d(TAG, "setEditMode: =========");
        LinearLayout mylayout = (LinearLayout)findViewById(R.id.layout_main);
        if(bEditMode){
            mylayout.setBackgroundColor(Color.RED);
            btn_wrSch.setText("退出编辑模式");
            curriculumList = DataSupport.findAll(Curriculum.class);
            curriculums = new String[curriculumList.size()+1];
            for(int i = 0;i<curriculumList.size();i++){
                curriculums[i] = curriculumList.get(i).getName();
            }
            curriculums[curriculumList.size()] = "";
        }else{
            mylayout.setBackgroundColor(Color.WHITE);
            btn_wrSch.setText("进入作息时间表");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_work_rest_schedule:
                Log.d(TAG, "onClick: btn_wrSch ========");
                if(!bEditMode){
                    WorkRestScheduleActivity.activityStart(MainActivity.this);
                }else{
                    bEditMode = false;
                    setEditMode();
                }
                break;
            default:
                if(bEditMode){
                    Log.d(TAG, "onClick: edit curriculums =============");
                    for(int i = 1;i<=7;i++){
                        if(txt_monday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_1,i);
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_monday[i].getBackground()).getColor();
                            textViewSelected(txt_monday[i],true);
                            old_text = txt_monday[i];
                            break;
                        }
                        if(txt_tuesday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_2,i);
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_tuesday[i].getBackground()).getColor();
                            textViewSelected(txt_tuesday[i],true);
                            old_text = txt_tuesday[i];
                            break;
                        }
                        if(txt_wednesday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_3,i);
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_wednesday[i].getBackground()).getColor();
                            textViewSelected(txt_wednesday[i],true);
                            old_text = txt_wednesday[i];
                            break;
                        }
                        if(txt_thursday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_4,i);
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_thursday[i].getBackground()).getColor();
                            textViewSelected(txt_thursday[i],true);
                            old_text = txt_thursday[i];
                            break;
                        }
                        if(txt_friday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_5,i);
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_friday[i].getBackground()).getColor();
                            textViewSelected(txt_friday[i],true);
                            old_text = txt_friday[i];
                            break;
                        }
                    }
                }else{
                    Log.d(TAG, "onClick: bEditMode == false");
                    for(int i = 1;i<=7;i++){
                        if(txt_monday[i].getId() == v.getId()){
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_monday[i].getBackground()).getColor();
                            textViewSelected(txt_monday[i],true);
                            old_text = txt_monday[i];
                            break;
                        }
                        if(txt_tuesday[i].getId() == v.getId()){
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_tuesday[i].getBackground()).getColor();
                            textViewSelected(txt_tuesday[i],true);
                            old_text = txt_tuesday[i];
                            break;
                        }
                        if(txt_wednesday[i].getId() == v.getId()){
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_wednesday[i].getBackground()).getColor();
                            textViewSelected(txt_wednesday[i],true);
                            old_text = txt_wednesday[i];
                            break;
                        }
                        if(txt_thursday[i].getId() == v.getId()){
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_thursday[i].getBackground()).getColor();
                            textViewSelected(txt_thursday[i],true);
                            old_text = txt_thursday[i];
                            break;
                        }
                        if(txt_friday[i].getId() == v.getId()){
                            if(old_text != null)
                            textViewSelected(old_text,false);
                            old_color = ((ColorDrawable)txt_friday[i].getBackground()).getColor();
                            textViewSelected(txt_friday[i],true);
                            old_text = txt_friday[i];
                            break;
                        }
                    }
                }
        }
    }

    //设置具体某一节课的课程名称
    private void setCurriculum(String dayofweek, final int classid){
        Log.d(TAG, "setCurriculum: ===========");
        switch (dayofweek){
            case DayOfWeek_1:
                Log.d(TAG, "setCurriculum: DayOfWeek_1");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("课程设置")
                        .setSingleChoiceItems(curriculums, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: DayOfWeek_1,"+ which);
                                txt_monday[classid].setText(curriculums[which]);
                                Schedule schedule = new Schedule();
                                schedule.setMonday(curriculums[which]);
                                schedule.updateAll("classId = ?",classid+"");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消",null)
                        .show();
                break;
            case DayOfWeek_2:
                Log.d(TAG, "setCurriculum: DayOfWeek_2");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("课程设置")
                        .setSingleChoiceItems(curriculums, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: DayOfWeek_2,"+ which);
                                txt_tuesday[classid].setText(curriculums[which]);
                                Schedule schedule = new Schedule();
                                schedule.setTuesday(curriculums[which]);
                                schedule.updateAll("classId = ?",classid+"");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消",null)
                        .show();
                break;
            case DayOfWeek_3:
                Log.d(TAG, "setCurriculum: DayOfWeek_3");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("课程设置")
                        .setSingleChoiceItems(curriculums, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: DayOfWeek_3,"+ which);
                                txt_wednesday[classid].setText(curriculums[which]);
                                Schedule schedule = new Schedule();
                                schedule.setWednesday(curriculums[which]);
                                schedule.updateAll("classId = ?",classid+"");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消",null)
                        .show();
                break;
            case DayOfWeek_4:
                Log.d(TAG, "setCurriculum: DayOfWeek_4");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("课程设置")
                        .setSingleChoiceItems(curriculums, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: DayOfWeek_4,"+ which);
                                txt_thursday[classid].setText(curriculums[which]);
                                Schedule schedule = new Schedule();
                                schedule.setThursday(curriculums[which]);
                                schedule.updateAll("classId = ?",classid+"");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消",null)
                        .show();
                break;
            case DayOfWeek_5:
                Log.d(TAG, "setCurriculum: DayOfWeek_5");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("课程设置")
                        .setSingleChoiceItems(curriculums, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: DayOfWeek_5,"+ which);
                                txt_friday[classid].setText(curriculums[which]);
                                Schedule schedule = new Schedule();
                                schedule.setFriday(curriculums[which]);
                                schedule.updateAll("classId = ?",classid+"");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消",null)
                        .show();
                break;
            default:
                Log.i(TAG, "setCurriculum: Unexcepted!!!!!!!!!!!!!!");
        }
    }
    private void textViewSelected(TextView textview,boolean bool){
        if(bool){
            textview.setBackgroundColor(Color.YELLOW);
        }else{
            textview.setBackgroundColor(old_color);
        }
    }
}
