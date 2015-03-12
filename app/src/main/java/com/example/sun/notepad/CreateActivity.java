package com.example.sun.notepad;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sun.notepad.db.DbHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class CreateActivity extends ActionBarActivity implements OnClickListener{

    private EditText editText;
    private Button createBtn;
    private DbHelper dbHelper;
    private CookieManager cookieManager = CookieManager.getInstance();
    private AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        dbHelper = new DbHelper(this, "notepad.db", null ,1);

        editText = (EditText) findViewById(R.id.content);
        createBtn = (Button) findViewById(R.id.createBtn);
        createBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.createBtn:
                final ProgressDialog dialog = new ProgressDialog(CreateActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("提交中，请稍候......");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                String content = editText.getText().toString();
                RequestParams params = new RequestParams();
                String cookie = cookieManager.getCookie("http://103.227.76.45");
                String uid = cookie.split(";")[0].split("=")[1];
                params.put("uid", uid);
                params.put("content", content);
                client.post("http://103.227.76.45/todo/save", params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        dialog.dismiss();
                        try {
                            if (response.getString("status").equals("200")) {
                                showDialog();
                            } else {
                                Toast.makeText(CreateActivity.this, response.getString("msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                /*
                ContentValues values = new ContentValues();
                values.put("content", content);
                db.insert("notepad", null ,values);
                */
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    protected void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("创建成功");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent("com.example.sun.notepad.LIST");
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("继续创建", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText("");
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
