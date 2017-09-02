package android.practice.curriculumschedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;

public class CurriculumMaintenanceActivity extends AppCompatActivity {
    private static final String TAG = "CurriculumMaintenanceAc";

    private List<Curriculum> curriculumList = null;
    private CurAdapter adapter = null;
    private RecyclerView recyclerView = null;
    private Button  btn_insert  = null,
                    btn_update  = null,
                    btn_delete  = null,
                    btn_back    = null;
    private EditText    edt_name    = null;
    private String  errMsg  = null,
                    oldName = null;
    private int pos     = -1,
                oldId   = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculum_maintenance);
        Log.d(TAG, "onCreate: ============");
        btn_insert  = (Button)findViewById(R.id.btn_insert_curriculumMaintenance);
        btn_update  = (Button)findViewById(R.id.btn_update_curriculumMaintenance);
        btn_delete  = (Button)findViewById(R.id.btn_delete_curriculumMaintenance);
        btn_back    = (Button)findViewById(R.id.btn_back_curriculumMaintenance);
        edt_name    = (EditText)findViewById(R.id.edt_name_curriculumMaintenance);
        curriculumList = DataSupport.findAll(Curriculum.class);
        if(curriculumList == null || curriculumList.size() == 0){
            //第一次运行程序，课程名称的表尚未建立。此处需要建立curriculum表，并初始化。
            createCurriculum();
        }
        //将curriculum表中的数据显示在列表中。
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_curriculum);
        LinearLayoutManager layoutManger = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManger);
        adapter = new CurAdapter(curriculumList);
        recyclerView.setAdapter(adapter);

        btn_insert.setOnClickListener(new btn_insert());
        btn_update.setOnClickListener(new btn_update());
        btn_delete.setOnClickListener(new btn_delete());
        btn_back.setOnClickListener(new btn_back());
    }

    //向curriculum表（课程名称表）中增加课程数据。
    class btn_insert implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: btn_insert ============");
            //首先检查课程名称是否已经存在
            String sName = edt_name.getText().toString().trim();
            Boolean bFound = false;
            for(Curriculum curriculum:curriculumList){
                if(curriculum.getName().equals(sName)){
                    bFound = true;
                    break;
                }
            }
            if(!bFound){
                //没有找到同名的课程，向curriculum表中新增一个课程。
                Curriculum curriculumNew = new Curriculum();
                curriculumNew.setName(sName);
                if(!curriculumNew.save()){
                    errMsg = "新增课程失败！课程名称：" + sName;
                    Log.e(TAG, "onClick: btn_insert =====:" + errMsg );
                    Toast.makeText(CurriculumMaintenanceActivity.this,errMsg,Toast.LENGTH_SHORT).show();
                }else{
                    //刷新课程名称表（curriculum）
                    errMsg = "新增课程成功！课程名称：" + sName;
                    Log.d(TAG, "onClick: btn_insert =====" + errMsg);
                    Toast.makeText(CurriculumMaintenanceActivity.this,errMsg,Toast.LENGTH_SHORT).show();
                    edt_name.setText("");
                    refreshList();
                }
            }else{
                //找到同名的课程，弹出提示。
                errMsg = "找到同名课程，不能新增。";
                Log.i(TAG, "onClick: btn_insert ======:" + errMsg);
                Toast.makeText(CurriculumMaintenanceActivity.this,errMsg,Toast.LENGTH_SHORT).show();
            }
        }
    }

    //修改curriculum表（课程名称表）中数据。
    class btn_update implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: btn_update ============");
            //首先检查课程名称是否为空
            final String sName = edt_name.getText().toString().trim();
            if(sName.isEmpty()){
                errMsg = "课程名称为空，修改失败。";
                Log.d(TAG, "onClick: btn_update =========" + errMsg);
                Toast.makeText(CurriculumMaintenanceActivity.this,errMsg,Toast.LENGTH_SHORT).show();
                return;
            }
            //修改前确认
            errMsg = "确定要将课程名称\"" + oldName + "\"改为\"" + sName + "\"吗？";
            new AlertDialog.Builder(CurriculumMaintenanceActivity.this)
                    .setTitle("请确认")
                    .setMessage(errMsg)
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //修改课程名称。
                            Curriculum curriculumNew = new Curriculum();
                            curriculumNew.setName(sName);
                            int i_update = curriculumNew.update(oldId);
                            if(i_update == 0){
                                errMsg = "修改课程失败！要修改的课程名称：" + oldName;
                                Log.e(TAG, "onClick: btn_update =====:" + errMsg );
                                Toast.makeText(CurriculumMaintenanceActivity.this,errMsg,Toast.LENGTH_SHORT).show();
                            }else {
                                //刷新课程名称表（curriculum）
                                errMsg = "修改课程成功！原名称：\"" + oldName + "\"，新名称：\"" + sName + "\"";
                                Log.d(TAG, "onClick: btn_update =====" + errMsg);
                                Toast.makeText(CurriculumMaintenanceActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                refreshList();
                                oldName = sName;
                            }
                        }
                    }).setNegativeButton("否",null)
                    .show();
        }
    }

    //删除curriculum表（课程名称表）中数据。
    class btn_delete implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: btn_delete ============");
            //首先检查课程名称是否为空
            String sName = edt_name.getText().toString().trim();
            if(sName.isEmpty()){
                errMsg = "课程名称为空，删除失败。";
                Log.d(TAG, "onClick: btn_delete =========" + errMsg);
                Toast.makeText(CurriculumMaintenanceActivity.this,errMsg,Toast.LENGTH_SHORT).show();
                return;
            }
            //删除前确认
            errMsg = "确定要删除\"" + oldName + "\"课程吗？";
            new AlertDialog.Builder(CurriculumMaintenanceActivity.this)
                    .setTitle("请确认")
                    .setMessage(errMsg)
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除课程。
                            int i_update = DataSupport.delete(Curriculum.class,oldId);
                            if(i_update == 0){
                                errMsg = "删除课程失败！要删除的课程id = " + oldId + "，名称为\"" + oldName + "\"";
                                Log.e(TAG, "onClick: btn_update =====:" + errMsg );
                                Toast.makeText(CurriculumMaintenanceActivity.this,errMsg,Toast.LENGTH_SHORT).show();
                            }else {
                                //刷新课程名称表（curriculum）
                                errMsg = "删除课程成功！课程id = " + oldId + "，课程名称：\"" + oldName + "\"";
                                Log.d(TAG, "onClick: btn_update =====" + errMsg);
                                Toast.makeText(CurriculumMaintenanceActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                                refreshList();
                            }
                        }
                    }).setNegativeButton("否",null)
                    .show();
        }
    }

    class btn_back implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: btn_back ==============");
            finish();
        }
    }

    private void createCurriculum(){
        Log.i(TAG, "createCurriculum: ==========");
        try{
            Curriculum curriculum1 = new Curriculum();
            curriculum1.setName(getString(R.string.str_YuWen));
            curriculum1.saveThrows();
            Curriculum curriculum2 = new Curriculum();
            curriculum2.setName(getString(R.string.str_ShuXue));
            curriculum2.saveThrows();
            Curriculum curriculum3 = new Curriculum();
            curriculum3.setName(getString(R.string.str_YingYu));
            curriculum3.saveThrows();
            Curriculum curriculum4 = new Curriculum();
            curriculum4.setName(getString(R.string.str_YinYue));
            curriculum4.saveThrows();
            Curriculum curriculum5 = new Curriculum();
            curriculum5.setName(getString(R.string.str_TiYu));
            curriculum5.saveThrows();
            Curriculum curriculum6 = new Curriculum();
            curriculum6.setName(getString(R.string.str_MeiShu));
            curriculum6.saveThrows();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            curriculumList = DataSupport.findAll(Curriculum.class);
        }
    }

    public static void activityStart(Context context){
        Intent intent = new Intent(context,CurriculumMaintenanceActivity.class);
        context.startActivity(intent);
    }

    //刷新课程名称表（curriculum）,将课程名称在recyclerView中显示。
    private void refreshList(){
        Log.d(TAG, "refreshList: ===============");
        curriculumList = DataSupport.findAll(Curriculum.class);
        adapter = new CurAdapter(curriculumList);
        recyclerView.setAdapter(adapter);
    }

    public class CurAdapter extends RecyclerView.Adapter<CurAdapter.ViewHolder>{
        private List<Curriculum> mCurriculumList;

        public CurAdapter(List<Curriculum> curriculumList){
            mCurriculumList = curriculumList;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView txt_id,txt_name;
            View holeLine;
            public ViewHolder(View itemView) {
                super(itemView);
                holeLine = itemView;
                txt_id   = (TextView)itemView.findViewById(R.id.item_id_curriculumList);
                txt_name = (TextView)itemView.findViewById(R.id.item_name_curriculumList);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.curriculumlistitem,parent,false);
            final ViewHolder holder = new ViewHolder(view);
            holder.holeLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = holder.getAdapterPosition();
                    oldId   = mCurriculumList.get(pos).getId();
                    oldName = mCurriculumList.get(pos).getName();
                    edt_name.setText(oldName);
                    edt_name.setSelection(oldName.length());
                    Log.d(TAG, "onClick: pos = " + pos);
                    notifyDataSetChanged();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Curriculum curriculum = mCurriculumList.get(position);
            holder.txt_id.setText(curriculum.getId() + "");
            holder.txt_name.setText(curriculum.getName());
            if(pos == position){
                holder.txt_id.setBackgroundColor(Color.YELLOW);
                holder.txt_name.setBackgroundColor(Color.YELLOW);
            }else{
                holder.txt_id.setBackgroundColor(0xFFf5fffa);
                holder.txt_name.setBackgroundColor(0xFFf5fffa);
            }
        }

        @Override
        public int getItemCount() {
            return mCurriculumList.size();
        }

    }
}
