package com.leeeeo.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Leeeeo on 2017/7/20.
 */

public class noteEdit extends Activity implements View.OnClickListener {
    private TextView tv_date;
    private EditText et_content;
    private Button btn_ok;
    private Button btn_cancel;
    public int enter_state = 0;//用来区分是新建一个note还是更改原来的note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        InitView();
    }

    private void InitView() {
        tv_date = (TextView) findViewById(R.id.tv_date);
        et_content = (EditText) findViewById(R.id.et_content);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        //获取此时时刻时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = sdf.format(date);
        tv_date.setText(dateString);

        //接收内容和id
        Bundle myBundle = this.getIntent().getExtras();
        String name = myBundle.getString("name");
        if (name.equals(""))
            et_content.setText("");
        else
            openNoteFile(name);
        enter_state = myBundle.getInt("enter_state");
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                // 获取edittext内容
                final String content = et_content.getText().toString();

                // 添加一个新的日志
                if (enter_state == 0) {
                    if (!content.equals("")) {
                        //获取此时时刻时间
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String dateString = sdf.format(date);
                        final EditText et = new EditText(this);
                        new AlertDialog.Builder(this).setTitle("保存：请以 \".txt\"结尾")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setView(et)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String input = et.getText().toString();
                                        System.out.println(input);
                                        saveNoteFile(input, content);
                                        finish();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }
                }
                // 查看并修改一个已有的日志
                else {
                    Bundle myBundle = this.getIntent().getExtras();
                    saveNoteFile(myBundle.getString("name")+".txt", content);
                    System.out.println(myBundle.getString("name")+".txt"+content);
                    finish();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    public void saveNoteFile(String fileName, String fileContent) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(fileContent)) {
            Toast.makeText(this, "输入不全，请重新输入！", Toast.LENGTH_SHORT).show();
            return;
        }
        //执行写操作，打开输出流；
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] contentData = fileContent.getBytes();
            bos.write(contentData, 0, contentData.length);
            bos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
    }

    public void openNoteFile(String filename) {
        filename = filename + ".txt";
        File fileDir = getFilesDir().getAbsoluteFile();
        FileInputStream fis = null;//执行读操作的时候不需要把设定模式
        try {
            fis = openFileInput(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while (-1 != (length = fis.read(buffer))) {
                baos.write(buffer, 0, length);
                baos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String string = new String(baos.toByteArray());
//        Toast.makeText(this, "内存中存在，打开成功", Toast.LENGTH_SHORT).show();
        et_content.setText(string);
    }
}

