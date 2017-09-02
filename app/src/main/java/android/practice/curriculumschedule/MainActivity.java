package android.practice.curriculumschedule;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private static final String DayOfWeek_1 = "Monday";
    private static final String DayOfWeek_2 = "Tuesday";
    private static final String DayOfWeek_3 = "Wednesday";
    private static final String DayOfWeek_4 = "Thursday";
    private static final String DayOfWeek_5 = "Friday";
    private String  errMsg  = null,
                    sToday  = null,
                    sWeek   = null;
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
        refreshSchedule();
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
        old_text = txt_monday[1];
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ===========");
        refreshSchedule();
        bEditMode = false;
        setEditMode();
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
                            textViewSelected(old_text,false);
                            textViewSelected(txt_monday[i],true);
                            old_text = txt_monday[i];
                            break;
                        }
                        if(txt_tuesday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_2,i);
                            textViewSelected(old_text,false);
                            textViewSelected(txt_tuesday[i],true);
                            old_text = txt_tuesday[i];
                            break;
                        }
                        if(txt_wednesday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_3,i);
                            textViewSelected(old_text,false);
                            textViewSelected(txt_wednesday[i],true);
                            old_text = txt_wednesday[i];
                            break;
                        }
                        if(txt_thursday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_4,i);
                            textViewSelected(old_text,false);
                            textViewSelected(txt_thursday[i],true);
                            old_text = txt_thursday[i];
                            break;
                        }
                        if(txt_friday[i].getId() == v.getId()){
                            setCurriculum(DayOfWeek_5,i);
                            textViewSelected(old_text,false);
                            textViewSelected(txt_friday[i],true);
                            old_text = txt_friday[i];
                            break;
                        }
                    }
                }else{
                    Log.d(TAG, "onClick: bEditMode == false");
                    for(int i = 1;i<=7;i++){
                        if(txt_monday[i].getId() == v.getId()){
                            textViewSelected(old_text,false);
                            textViewSelected(txt_monday[i],true);
                            old_text = txt_monday[i];
                            break;
                        }
                        if(txt_tuesday[i].getId() == v.getId()){
                            textViewSelected(old_text,false);
                            textViewSelected(txt_tuesday[i],true);
                            old_text = txt_tuesday[i];
                            break;
                        }
                        if(txt_wednesday[i].getId() == v.getId()){
                            textViewSelected(old_text,false);
                            textViewSelected(txt_wednesday[i],true);
                            old_text = txt_wednesday[i];
                            break;
                        }
                        if(txt_thursday[i].getId() == v.getId()){
                            textViewSelected(old_text,false);
                            textViewSelected(txt_thursday[i],true);
                            old_text = txt_thursday[i];
                            break;
                        }
                        if(txt_friday[i].getId() == v.getId()){
                            textViewSelected(old_text,false);
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
            textview.setBackgroundColor(0xfff5fffa);
        }
    }
}
