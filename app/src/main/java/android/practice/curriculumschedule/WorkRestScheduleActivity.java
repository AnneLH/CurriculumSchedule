package android.practice.curriculumschedule;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class WorkRestScheduleActivity extends AppCompatActivity {
    private static final String TAG = "WorkRestScheduleActivit";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_rest_schedule);
        Log.d(TAG, "onCreate: ==============");
    }
    
    public static void activityStart(Context context){
        Intent intent = new Intent(context,WorkRestScheduleActivity.class);
        context.startActivity(intent);
    }
}
