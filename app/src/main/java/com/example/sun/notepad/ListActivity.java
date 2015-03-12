package com.example.sun.notepad;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.ArrayAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.sun.notepad.adapter.NoteAdapter;
import com.example.sun.notepad.db.DbHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;


public class ListActivity extends ActionBarActivity implements OnClickListener{

    private Button toCreateBtn;
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private SwipeRefreshLayout swipeContainer;
    private List<NotePad> npList = new ArrayList<NotePad>();
    private NoteAdapter adapter = null;
    private ListView listView;
    private static long back_pressed;
    private AsyncHttpClient client = new AsyncHttpClient();
    private CookieManager cookieManager = CookieManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toCreateBtn = (Button) findViewById(R.id.toCreateBtn);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipContainer);
        listView = (ListView) findViewById(R.id.dataList);
        toCreateBtn.setOnClickListener(this);
        /*
        dbHelper = new DbHelper(this, "notepad.db", null ,1);
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("notepad", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex("content"));
                NotePad np = new NotePad();
                np.setContent(content);
                np.setId(cursor.getInt(cursor.getColumnIndex("id")));
                npList.add(np);
            } while (cursor.moveToNext());
        }
        cursor.close();
        */
        fetchTimelineAsync(0);

        adapter = new NoteAdapter(ListActivity.this,R.layout.note_item , npList);
        listView.setAdapter(adapter);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });

    }
    private void fetchTimelineAsync(int page) {
        npList.clear();
        String cookie = cookieManager.getCookie("http://103.227.76.45");
        final RequestParams params = new RequestParams();
        Log.d("DEBUG", cookie);
        String uid = cookie.split(";")[0].split("=")[1];
        Log.d("uid", uid);
        params.put("uid", uid);

        client.get("http://103.227.76.45/todo/list", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray data = null;
                try {
                    data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        NotePad np = new NotePad();
                        np.setContent(obj.getString("content"));
                        np.setId((obj.getString("id")));
                        npList.add(np);
                    }
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        /*
        npList.clear();
        Cursor cursor = db.query("notepad", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex("content"));
                NotePad np = new NotePad();
                np.setContent(content);
                np.setId(cursor.getInt(cursor.getColumnIndex("id")));
                npList.add(np);
            } while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
        cursor.close();
        */
        fetchTimelineAsync(0);
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toCreateBtn:
                Intent intent = new Intent("com.example.sun.notepad.CREATE");
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "再按一次退出!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toCreateBtn) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
