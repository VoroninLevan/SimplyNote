package comvoroninlevan.httpsgithub.simplynote.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Леван on 27.10.2016.
 */

public class Provider extends ContentProvider {

    public static final String LOG_TAG = Provider.class.getSimpleName();
    private NotesDbHelper mDbHelper;

    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH, NOTES);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH + "/#", NOTES_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new NotesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match){
            case NOTES:
                cursor = database.query(Contract.NotesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case NOTES_ID:
                selection = Contract.NotesEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Contract.NotesEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case NOTES:
                return Contract.NotesEntry.CONTENT_LIST_TYPE;
            case NOTES_ID:
                return Contract.NotesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return insertNote(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertNote(Uri uri, ContentValues values){

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(Contract.NotesEntry.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(LOG_TAG, "" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int deletedRows;
        switch (match){
            case NOTES:
                deletedRows = database.delete(Contract.NotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTES_ID:
                selection = Contract.NotesEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(Contract.NotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return updateNote(uri, values, selection, selectionArgs);
            case NOTES_ID:
                selection = Contract.NotesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateNote(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateNote(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if(values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int updatedRows = database.update(Contract.NotesEntry.TABLE_NAME, values, selection, selectionArgs);

        if(updatedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }
}
