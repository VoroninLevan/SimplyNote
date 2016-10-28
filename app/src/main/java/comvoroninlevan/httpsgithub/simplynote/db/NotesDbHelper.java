package comvoroninlevan.httpsgithub.simplynote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Леван on 27.10.2016.
 */

public class NotesDbHelper extends SQLiteOpenHelper {

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";
    public static final String PRIM_KEY = " PRIMARY KEY";
    public static final String AUTOINCR = " AUTOINCREMENT";
    public static final String CREATE_TABLE = "CREATE TABLE ";

    public static final String CREATE_NOTES = CREATE_TABLE + Contract.NotesEntry.TABLE_NAME + "(" +
            Contract.NotesEntry._ID + INTEGER_TYPE + PRIM_KEY + AUTOINCR + COMMA_SEP +
            Contract.NotesEntry.COLUMN_NAME +TEXT_TYPE + COMMA_SEP +
            Contract.NotesEntry.COLUMN_NOTE + TEXT_TYPE + ");";

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Notes.db";

    public NotesDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
