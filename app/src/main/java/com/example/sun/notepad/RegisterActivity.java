package com.example.sun.notepad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.CookieManager;

import java.net.HttpCookie;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener{
    private EditText name;
    private EditText pwd;
    private EditText pwd2;
    private Button registerBtn;
    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText)findViewById(R.id.account);
        pwd = (EditText)findViewById(R.id.pwd);
        pwd2 = (EditText)findViewById(R.id.pwd2);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        client = new AsyncHttpClient();
        registerBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.registerBtn:
                final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("注册中，请稍候......");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                RequestParams params = new RequestParams();
                params.put("name", name.getText());
                params.put("pwd", pwd.getText());
                params.put("pwd2", pwd2.getText());
                client.post("http://103.227.76.45/user/save", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            dialog.dismiss();
                            if (response.getString("status").equals("200")) {
                                HttpCookie cookie = new HttpCookie("uid", response.getString("data"));
                                CookieManager cookieManager = CookieManager.getInstance();

                                Log.d("DEBUG", response.getString("data"));
                                cookie.setDomain("http://103.227.76.45");
                                cookie.setPath("/");
                                cookie.setMaxAge(1000000);
                                Log.d("DEBUG", cookie.toString());
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                    //noinspection deprecation
                                    CookieSyncManager.createInstance(RegisterActivity.this.getApplicationContext());
                                }
                                cookieManager.setCookie("http://103.227.76.45", cookie.toString());

                                Intent intent = new Intent("com.example.sun.notepad.LIST");
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, response.getString("msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
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
