package comvoroninlevan.httpsgithub.simplynote;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import comvoroninlevan.httpsgithub.simplynote.db.Contract;

/**
 * Created by Леван on 27.10.2016.
 */

public class NotesCursorAdapter extends CursorAdapter {

    public NotesCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    private static class ViewHolder{

        TextView noteHeader;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.noteHeader = (TextView)view.findViewById(R.id.note_header);

        int columnName = cursor.getColumnIndex(Contract.NotesEntry.COLUMN_NAME);

        String header = cursor.getString(columnName);

        viewHolder.noteHeader.setText(header);

    }

}
