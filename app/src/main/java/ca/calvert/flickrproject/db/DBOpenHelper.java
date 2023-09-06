package ca.calvert.flickrproject.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBOpenHelper";

    public DBOpenHelper(Context context) {
        super(context, SQLCommands.DATABASE_NAME, null, Integer.parseInt(SQLCommands.SCHEMA_VERSION));
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Perform necessary upgrades if needed
    }

    // Check if a specific table exists
    public boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = null;
        boolean isExists = false;
        try {
            String query = "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = ?";
            cursor = db.rawQuery(query, new String[]{tableName});
            isExists = cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isExists;
    }

    // Create the database tables
    private void createTables(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables...");
        db.execSQL(SQLCommands.CREATE_TABLE_LOADED);
        db.execSQL(SQLCommands.CREATE_TABLE_USER);
        Log.d(TAG, "Database tables created successfully.");
    }
}
