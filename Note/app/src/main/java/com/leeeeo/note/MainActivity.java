package com.leeeeo.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView listview;
    private SimpleAdapter simple_adapter;
    private List<Map<String, Object>> dataList;
    private List<Map<String, Object>> dataListf;
    private Button addNote;
    private TextView tv_content;
    private SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();
    }

    //在activity显示的时候更新listview
    @Override
    protected void onStart() {
        super.onStart();
        RefreshNotesList();
    }

    private void InitView() {
        tv_content = (TextView) findViewById(R.id.tv_name);
        listview = (ListView) findViewById(R.id.listview);
        dataList = new ArrayList<Map<String, Object>>();
        dataListf = new ArrayList<Map<String, Object>>();
        addNote = (Button) findViewById(R.id.btn_editnote);

        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        addNote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, noteEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "");
                bundle.putInt("enter_state", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    //刷新listview
    public void RefreshNotesList() {
        //如果dataList已经有的内容，全部删掉
        //并且更新simp_adapter
        int size = dataListf.size();
        if (size > 0) {
            dataListf.removeAll(dataListf);
            simple_adapter.notifyDataSetChanged();
        }


        final String[] dataFileList = fileList();
        for (String aDataFileList : dataFileList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tv_name", aDataFileList);
//            map.put("tv_date", date);
            dataListf.add(map);
            System.out.println(aDataFileList);
        }
        simple_adapter = new SimpleAdapter(this, dataListf, R.layout.item,
                new String[]{"tv_name"}, new int[]{
                R.id.tv_name});
        listview.setAdapter(simple_adapter);
    }


    // 点击listview中某一项的点击监听事件
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        //获取listview中此个item中的内容
        String content = listview.getItemAtPosition(arg2) + "";
        String content1 = content.substring(content.indexOf("=") + 1, content.indexOf("."));

        System.out.println(content1);
        Intent myIntent = new Intent(MainActivity.this, noteEdit.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", content1);
        bundle.putInt("enter_state", 1);
        myIntent.putExtras(bundle);
        startActivity(myIntent);

    }

    // 点击listview中某一项长时间的点击事件
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
                                   long arg3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除该日志");
        builder.setMessage("确认删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取listview中此个item中的内容
                //删除该行后刷新listview的内容
                String content = listview.getItemAtPosition(arg2) + "";
                String content1 = content.substring(content.indexOf("=") + 1,
                        content.indexOf("."));
                boolean isDelete = deleteFile(content1 + ".txt");
                if (isDelete) {
                    Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                }
                RefreshNotesList();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
        return true;
    }


}
