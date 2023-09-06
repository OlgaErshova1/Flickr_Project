package ca.calvert.flickrproject.db;

public class SQLCommands {
    final public static String DATABASE_NAME = "photo_database";
    final public static String SCHEMA_VERSION = "1";

    final public static String TABLE_USER = "user";
    final public static String TABLE_LOADED = "loaded_photos";
    final public static String COLUMN_ID = "_id";

    final public static String COLUMN_NAME = "name";
    final public static String COLUMN_PHOTO = "photo";

    final public static String[] TABLE_COLUMNS = {"_id", "name", "photo"};

    final public static String COUNT_QUERY = "SELECT COUNT(*) FROM %s";

    final public static String CREATE_TABLE_USER =
            "CREATE TABLE user(_id INTEGER PRIMARY KEY , name TEXT , photo TEXT );";
    final public static String CREATE_TABLE_LOADED =
            "CREATE TABLE loaded_photos(_id INTEGER PRIMARY KEY , name TEXT , photo TEXT );";
}
