package comvoroninlevan.httpsgithub.simplynote.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Леван on 27.10.2016.
 */

public final class Contract {

    public static final String CONTENT_AUTHORITY = "comvoroninlevan.httpsgithub.simplynote";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH = "notes";

    public static final class NotesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH);

        //Table
        public static final String TABLE_NAME = "notes";

        //Columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NOTE = "note";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
    }
}
