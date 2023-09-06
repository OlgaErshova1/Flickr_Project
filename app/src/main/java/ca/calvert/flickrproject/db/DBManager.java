package ca.calvert.flickrproject.db;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ca.calvert.flickrproject.model.Photo;

public class DBManager extends Application {

    private static final String TAG = "DBManager";

    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.d(TAG, "Application context: " + context);
        dbOpenHelper = new DBOpenHelper(context);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        Log.d(TAG, "SQLite database: " + sqLiteDatabase);
    }

    public DBManager() {
    }

    // Check if the database exists
    public boolean isDatabaseExists() {
        if (sqLiteDatabase == null) {
            Log.d(TAG, "SQLite database is null. Opening database...");
            sqLiteDatabase = dbOpenHelper.getWritableDatabase();
            Log.d(TAG, "SQLite database after opening: " + sqLiteDatabase);
        }
        File dbFile = getApplicationContext().getDatabasePath(SQLCommands.DATABASE_NAME);
        return dbFile.exists();
    }

    // Check if the tables exist
    public boolean doTablesExists() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        boolean isLoadedTableExists = dbOpenHelper.isTableExists(db, SQLCommands.TABLE_LOADED);
        boolean isUserTableExists = dbOpenHelper.isTableExists(db, SQLCommands.TABLE_USER);
        db.close();
        return isLoadedTableExists && isUserTableExists;
    }

    // Create the database and tables if they don't exist
    public void createDatabaseAndTables() {
        if (!isDatabaseExists()) {
            sqLiteDatabase = dbOpenHelper.getWritableDatabase();
            createTables();
        }
    }

    // Create the tables
    private void createTables() {
        Log.d(TAG, "Creating database tables...");
        sqLiteDatabase.execSQL(SQLCommands.CREATE_TABLE_LOADED);
        sqLiteDatabase.execSQL(SQLCommands.CREATE_TABLE_USER);
        Log.d(TAG, "Database tables created successfully.");
    }

    // Insert a photo into the user table
    public void insertPhoto(Context context, Photo photo) {
        ContentValues values = new ContentValues();
        values.put(SQLCommands.COLUMN_ID, photo.get_id());
        values.put(SQLCommands.COLUMN_NAME, photo.getName());
        values.put(SQLCommands.COLUMN_PHOTO, photo.getImg_url());

        if (isPhotoSaved(photo.get_id())) {
            Toast.makeText(context, "Image already saved", Toast.LENGTH_SHORT).show();
        } else {
            long result = insertPhotoToUserTable(values);
            if (result != -1) {
                int rowCount = getUserTableRowCount();
                Toast.makeText(context, "Image saved to database. Table size: " + rowCount, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to save image to database", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Insert a photo into the user table
    private long insertPhotoToUserTable(ContentValues values) {
        return insertInTable(SQLCommands.TABLE_USER, values);
    }

    // Insert photos into the loaded table
    public void insertPhotosLoadedTable(ArrayList<Photo> photos) {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        }

        sqLiteDatabase.beginTransaction();
        try {
            for (Photo photo : photos) {
                ContentValues values = new ContentValues();
                values.put(SQLCommands.COLUMN_NAME, photo.getName());
                values.put(SQLCommands.COLUMN_PHOTO, photo.getImg_url());
                sqLiteDatabase.insert(SQLCommands.TABLE_LOADED, null, values);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    // Insert a row into a table
    public long insertInTable(String tableName, ContentValues values) {
        return sqLiteDatabase.insert(tableName, null, values);
    }

    // Get the count of rows in the user table
    private int getUserTableRowCount() {
        return getTableRowCount(SQLCommands.TABLE_USER);
    }

    private int getTableRowCount(String tableName) {
        Cursor cursor = null;
        int rowCount = 0;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                rowCount = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return rowCount;
    }

    // Get the users saved photos
    public ArrayList<Photo> getSavedPhotos() {
        ArrayList<Photo> savedPhotos = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(SQLCommands.TABLE_USER, null, null, null, null, null, null);
            int idColumnIndex = cursor.getColumnIndex(SQLCommands.COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(SQLCommands.COLUMN_NAME);
            int photoUrlColumnIndex = cursor.getColumnIndex(SQLCommands.COLUMN_PHOTO);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                String photoUrl = cursor.getString(photoUrlColumnIndex);
                Photo photo = new Photo(id, name, photoUrl);
                savedPhotos.add(photo);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return savedPhotos;
    }

    // Check if the photo ID already exists in the user table
    private boolean isPhotoSaved(long photoId) {
        String selection = SQLCommands.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(photoId)};
        Cursor cursor = sqLiteDatabase.query(SQLCommands.TABLE_USER, null, selection, selectionArgs, null, null, null);
        boolean isSaved = false;
        if (cursor != null && cursor.getCount() > 0) {
            isSaved = true;
        }
        if (cursor != null) {
            cursor.close();
        }
        return isSaved;
    }

    // Check if the user table has anything
    public boolean isUserTableEmpty() {
        boolean isEmpty = true;
        Cursor cursor = null;
        try {
            String query = String.format(SQLCommands.COUNT_QUERY, SQLCommands.TABLE_USER);
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                isEmpty = (count == 0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isEmpty;
    }

    public void deleteAllRowsFromLoadedTable() {
        int rowsDeleted = deleteRowFromTable(SQLCommands.TABLE_LOADED, null, null);
        Log.d("DBManager", "Deleted " + rowsDeleted + " rows from the loaded table");
    }

    // Delete a row from a table
    public int deleteRowFromTable(String tableName, String selection, String[] selectionArgs) {
        return sqLiteDatabase.delete(tableName, selection, selectionArgs);
    }
}
