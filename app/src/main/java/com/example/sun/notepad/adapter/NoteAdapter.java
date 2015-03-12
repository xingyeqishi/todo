package com.example.sun.notepad.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sun.notepad.NotePad;
import com.example.sun.notepad.R;

import java.util.List;

/**
 * Created by sun on 15-3-5.
 */
public class NoteAdapter extends ArrayAdapter<NotePad> {

    private int resourceId;

    public NoteAdapter(Context context, int textViewResourceId, List<NotePad> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotePad notePad = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView noteContent = (TextView) view.findViewById(R.id.note_content);
        noteContent.setText(notePad.getContent());
        return view;
    }
}
