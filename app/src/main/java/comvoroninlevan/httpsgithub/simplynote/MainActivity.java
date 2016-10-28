package comvoroninlevan.httpsgithub.simplynote;

import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import comvoroninlevan.httpsgithub.simplynote.db.Contract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    NotesCursorAdapter mAdapter;

    private static final int LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.note_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mAdapter = new NotesCursorAdapter(this, null);
        listView.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER, null, this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                Uri currentUri = ContentUris.withAppendedId(Contract.NotesEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Contract.NotesEntry._ID,
                Contract.NotesEntry.COLUMN_NAME};

        return new CursorLoader(this, Contract.NotesEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
