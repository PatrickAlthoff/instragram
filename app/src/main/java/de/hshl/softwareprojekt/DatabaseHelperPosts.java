package de.hshl.softwareprojekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelperPosts extends SQLiteOpenHelper {
    //Variablen zur Verwaltung der SQLite Datenbank
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DATABASE_NAME = "post.db";
    private static final String TABLE_NAME = "posts";
    private static final String COL_1 = "_id";
    private static final String COL_2 = "email";
    private static final String COL_3 = "pw";
    public static final String SQL_ALTER_TABLE = "ALTER TABLE{"+TABLE_NAME+"} ADD RENAME TO TempOldTable;";
    public static final String SQL_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (_id INTEGER, username TEXT, path TEXT, titel TEXT, hashtags TEXT, date TEXT, liked INTEGER)";
    private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    private static final int DATABASE_VERSION = 1;

    //Constructor für die Datenbank
    public DatabaseHelperPosts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DBHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_TABLE_CREATE + " angelegt.");
            db.execSQL(SQL_TABLE_CREATE);
        }
        catch (Exception ex) {
            Log.e(TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrade database from Version " + oldVersion + " to " + newVersion);
        db.execSQL(SQL_TABLE_DROP);
        onCreate(db);

    }
    //Insert Methode zum einfügen weiterer Nutzer (email + pw)
    public void insertData(int id, String name, String path, String titel, String hashtags, String date, boolean liked) {
        long rowId = -1;
        SQLiteDatabase db = null;
        int like = liked ? 1:0;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("_id", id);
            values.put("username", name);
            values.put("path",  path);
            values.put("titel", titel);
            values.put("hashtags", hashtags);
            values.put("date", date);
            values.put("liked", like);
            rowId = db.insert(TABLE_NAME, null, values);
        }
        catch (SQLiteException exception) {
            Log.e(TAG, "insertData()", exception);
        }
        finally {
            Log.d(TAG, "insertData(rowId:" + rowId + ")");

            if (db != null){
                db.close();
            }
        }
    }

    //Get Methode zur Datenausgabe
    public ArrayList<String> getData(){

        String[] columns = {"_id", "username", "path", "titel", "hashtags", "date","liked"};
        ArrayList<String> postList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_NAME, columns,  null, null,null,null,null);

            while (cursor.moveToNext()) {
                postList.add(0,cursor.getString(0) + " : "  +  cursor.getString(1) + " : "  + cursor.getString(2 ) + " : "  + cursor.getString(3 ) +  " : "  + cursor.getString(4 ) + " : " + cursor.getString(5)+ " : " + cursor.getString(6));
            }
        }
        catch(SQLiteException exception) {
            Log.e(TAG, "getData()", exception);
        }
        finally {
            if (cursor != null){
                cursor.close();
            }
            if(db != null){
                db.close();
            }
        }
        return postList;
    }
    //Get Methode zur Ausgabe der Nutzermailadresse
    public String getPost(String username){

        String[] columns = {"path"};
        String userEntry = "";
        ArrayList<String> user= new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            db = getReadableDatabase();
            cursor = db.query(TABLE_NAME, columns, "email like " + "'%" + username + "%'", null,null,null,null);


        }
        catch(SQLiteException exception){
            Log.e(TAG, "getUser()", exception);
        }
        finally {
            if (cursor != null){
                cursor.close();
            }
            if(db != null){
                db.close();
            }
        }

        return userEntry;
    }

    //Methode zum Löschen des letzten Eintrages
    public void deletePost(int id){
        int rowsDeleted;
        SQLiteDatabase db = null;

        try{
            db = getWritableDatabase();
            rowsDeleted = db.delete(TABLE_NAME, "_id LIKE " + "'%" + id + "%'", null);
            Log.d(TAG, "deleteData() affected " + rowsDeleted + " rows");
        }
        catch (SQLiteException exception){
            Log.e(TAG, "deleteData()", exception);
        }
        finally {
            if(db != null){
                db.close();
            }
        }
    }
}
