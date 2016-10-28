package comvoroninlevan.httpsgithub.simplynote;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import comvoroninlevan.httpsgithub.simplynote.db.Contract;

/**
 * Created by Леван on 27.10.2016.
 */

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER = 1;

    private Uri mNoteUri;
    private EditText mHeader;
    private EditText mBody;

    private boolean mChanged = false;

    private View.OnTouchListener mTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        mNoteUri = intent.getData();

        if (mNoteUri == null) {
            setTitle(R.string.addNote);
        } else {
            setTitle(R.string.editNote);
            getLoaderManager().initLoader(LOADER, null, this);
        }

        mHeader = (EditText) findViewById(R.id.edit_header);
        mBody = (EditText) findViewById(R.id.edit_body);

        mHeader.setOnTouchListener(mTouch);
        mBody.setOnTouchListener(mTouch);
    }

    private void noteSave(){

        String header = mHeader.getText().toString().trim();
        String body = mBody.getText().toString().trim();

        if(mNoteUri == null && (TextUtils.isEmpty(header)) || TextUtils.isEmpty(body)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Contract.NotesEntry.COLUMN_NAME, header);
        values.put(Contract.NotesEntry.COLUMN_NOTE, body);

        if(mNoteUri == null){
            Uri newUri = getContentResolver().insert(Contract.NotesEntry.CONTENT_URI, values);

            if(newUri == null){
                Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            }
        }else{
            int rows = getContentResolver().update(mNoteUri, values, null, null);

            if(rows == 0){
                Toast.makeText(this, "Error with updating item", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void noteDelete() {

        if (mNoteUri != null) {

            int rowsDeleted = getContentResolver().delete(mNoteUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editorDeleteFailed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editorDeleteSuccessful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteConfirmationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogQuestion);
        builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noteDelete();
            }
        });
        builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        if (!mChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        unsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Contract.NotesEntry._ID,
                Contract.NotesEntry.COLUMN_NAME,
                Contract.NotesEntry.COLUMN_NOTE};

        return new CursorLoader(this, mNoteUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data == null){
            return;
        }

        if(data.moveToFirst()){

            int columnName = data.getColumnIndex(Contract.NotesEntry.COLUMN_NAME);
            int columnNote = data.getColumnIndex(Contract.NotesEntry.COLUMN_NOTE);

            String noteName = data.getString(columnName);
            String noteBody = data.getString(columnNote);

            mHeader.setText(noteName);
            mBody.setText(noteBody);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mHeader.setText("");
        mBody.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mNoteUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_note);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_note:
                noteSave();
                finish();
                return true;
            case R.id.delete_note:
                deleteConfirmationDialog();
                return true;
            case android.R.id.home:

                if (!mChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                unsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsavedChangesDialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keepEditing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
