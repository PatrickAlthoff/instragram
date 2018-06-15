package de.hshl.softwareprojekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = MainActivity.class.getSimpleName();


    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String COL_1 = "_id";
    private static final String COL_2 = "email";
    private static final String COL_3 = "pw";
    // Für Follow Funktion
    public static final String FOLLOW_TABLE_NAME = "FollowSystem";
    static final String COL_01 = "_id";
    static final String COL_02 = "followedBy";
    static final String COL_03 = "isfollowing";

    // Tabelle für Follow
    public static final String Follow_TABLE_CREATE =
            "CREATE TABLE " + FOLLOW_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, followedBy TEXT, isfollowing TEXT)";




    public static final String SQL_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, pw TEXT)";




    private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DBHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_TABLE_CREATE + " angelegt.");
            db.execSQL(SQL_TABLE_CREATE);
            db.execSQL(Follow_TABLE_CREATE);   //Follow Funktion

        }
        catch (Exception ex) {
            Log.e(TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrade database from Version " + oldVersion + " to " + newVersion);
        db.execSQL(SQL_TABLE_DROP);
        db.execSQL(FOLLOW_TABLE_NAME);
        onCreate(db);

    }

    // hinzufügen
    public boolean addFollowData(String followedBy, String isFollowing){
        //long rowId = -1;
        //int rowsUpdated=0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("followedBy", followedBy);
            contentValues.put("isFollowing", isFollowing);
            //rowsUpdated = db.update(FOLLOW_TABLE_NAME, contentValues, "_id=" +_id, null);
            db.insert(FOLLOW_TABLE_NAME, null, contentValues);
            Log.d(TAG, "updateFollowData()affected");


        }catch (SQLiteException exception){
            Log.e(TAG, "Exception");
        }
        finally {
            Log.d(TAG, "insertData");

            if (db != null){
                db.close();
            }

        }
      return true;
    }
    //aktualisieren
    public boolean updateFollowData(Integer id, String isFollowing, String followedBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_02, followedBy);
        contentValues.put(COL_03, isFollowing);
        db.update(FOLLOW_TABLE_NAME, contentValues, COL_01 + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    public Cursor getFollow(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + FOLLOW_TABLE_NAME + " WHERE " +
                COL_01 + "=?", new String[] { Integer.toString(id) } );
        return res;
    }
    public Integer deleteFollow(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FOLLOW_TABLE_NAME,
                COL_01 + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public void insertData(String email, String pw) {
        long rowId = -1;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("pw",  pw);
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


    public ArrayList<String> getData(){

        String[] columns = {"_id", "email", "pw"};
        ArrayList<String> userList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_NAME, columns,  null, null,null,null,null);

            while (cursor.moveToNext()) {
                userList.add(0, cursor.getString(1) + ":"  + cursor.getString(2));
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
        return userList;
    }
    public String getUser(String username){

        String[] columns = {"email"};
        String userEntry = "";
        ArrayList<String> user= new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            db = getReadableDatabase();
            cursor = db.query(TABLE_NAME, columns, "email like " + "'%" + username + "%'", null,null,null,null);
            while(cursor.moveToNext()){
                userEntry = cursor.getString(0);
                String[] pieces = userEntry.split("@");
                userEntry = pieces[0];
            }

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
    public void deleteData(int id){
        int rowsDeleted;
        SQLiteDatabase db = null;

        try{
            db = getWritableDatabase();
            rowsDeleted = db.delete(TABLE_NAME, "_id=" + id, null);
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

    public Cursor getPerson(int personID) {

        return null;
    }}
