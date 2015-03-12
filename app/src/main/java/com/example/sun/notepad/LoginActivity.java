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
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.RequestUserAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.HttpCookie;


public class LoginActivity extends ActionBarActivity implements OnClickListener{

    private Button loginBtn;
    private Button registerBtn;
    private TextView accountInput;
    private TextView passwordInput;
    private AsyncHttpClient client = new AsyncHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        accountInput = (TextView) findViewById(R.id.account);
        passwordInput = (TextView) findViewById(R.id.password);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("登录中，请稍候......");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                String name = accountInput.getText().toString();
                String pwd = passwordInput.getText().toString();
                RequestParams params = new RequestParams();
                params.put("name", name);
                params.put("pwd", pwd);
                client.post("http://103.227.76.45/user/auth", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getString("status").equals("200")) {
                                Log.d("DEBUG", "****************");
                                CookieManager cookieManager = CookieManager.getInstance();
                                Log.d("DEBUG", response.getJSONObject("data").getString("id"));

                                HttpCookie cookie = new HttpCookie("uid", response.getJSONObject("data").getString("id"));
                                cookie.setDomain("http://103.227.76.45");
                                cookie.setPath("/");
                                cookie.setMaxAge(1000000);
                                Log.d("DEBUG", cookie.toString());
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                    //noinspection deprecation
                                    CookieSyncManager.createInstance(LoginActivity.this.getApplicationContext());
                                }
                                cookieManager.setCookie("http://103.227.76.45", cookie.toString());
                                dialog.dismiss();
                                Intent intent = new Intent("com.example.sun.notepad.LIST");
                                LoginActivity.this.startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, response.getString("msg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.registerBtn:
                Intent intent = new Intent("com.example.sun.notepad.REGISTER");
                startActivity(intent);
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
