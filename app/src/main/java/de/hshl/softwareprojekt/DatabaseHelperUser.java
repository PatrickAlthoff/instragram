package de.hshl.softwareprojekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHelperUser extends SQLiteOpenHelper {
    //Variablen zur Verwaltung der SQLite Datenbank
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DATABASE_NAME = "user.db";
    private static final String TABLE_NAME = "User_Table";
    public static final String SQL_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (_id INTEGER, username TEXT ,email TEXT, pw TEXT, base64 TEXT, remember INTEGER)";
    private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    private static final int DATABASE_VERSION = 1;

    //Constructor für die Datenbank
    public DatabaseHelperUser(Context context) {
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
    public void insertData(long Id,String username, String email, String pw, String base64, boolean remember) {
        long rowId = -1;
        int member = remember ? 1:0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("_id", Id);
            values.put("username", username);
            values.put("email", email);
            values.put("pw",  pw);
            values.put("base64", base64);
            values.put("remember", member);
            rowId = db.insert(TABLE_NAME, null, values);
        }
        catch (SQLiteException exception) {
            Log.e(TAG, "insertPost()", exception);
        }
        finally {
            Log.d(TAG, "insertPost(rowId:" + rowId + ")");

            if (db != null){
                db.close();
            }
        }
    }


    //Get Methode zur Datenausgabe
    public ArrayList<String> getData(){

        String[] columns = {"_id","username", "email", "pw", "base64"};
        ArrayList<String> userList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_NAME, columns,  null, null,null,null,null);

            while (cursor.moveToNext()) {
                userList.add(0, cursor.getString(0) + ":" +cursor.getString(1) + ":" + cursor.getString(2) + ":"  + cursor.getString(3) + ":" + cursor.getString(4));
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

    //Get Methode zur Ausgabe der Nutzermailadresse
    public String getUserPic(long id){

        String[] columns = {"base64"};
        String userPic = "";
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            db = getReadableDatabase();
            cursor = db.query(TABLE_NAME, columns, "_id="+id, null,null,null,null);
            while(cursor.moveToNext()){
                userPic = cursor.getString(0);
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

        return userPic;
    }

    public void updateRemember(String email, boolean remember){
        int member = remember ? 1:0;
        int rowsUpdated = 0;
        SQLiteDatabase db = null;
        try{
            db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("remember", member);
            rowsUpdated = db.update(TABLE_NAME, cv, "email='" + email + "'", null);
            Log.d(TAG,"updateData() affected " + rowsUpdated + " rows");
        }
        catch(SQLiteException exception){
            Log.e(TAG, "updateData()", exception);
        }
        finally {
            if(db != null){
                db.close();
            }
        }

    }

    public void updateUser(long id, String username){
        int rowsUpdated = 0;
        SQLiteDatabase db = null;
        try{
            db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("username",username);
            rowsUpdated = db.update(TABLE_NAME, cv, "_id=" + id, null);
            Log.d(TAG,"updateData() affected " + rowsUpdated + " rows");
        }
        catch(SQLiteException exception){
            Log.e(TAG, "updateData()", exception);
        }
        finally {
            if(db != null){
                db.close();
            }
        }

    }

    public void updateUserPic(long id, String base64){
        int rowsUpdated = 0;
        SQLiteDatabase db = null;
        try{
            db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("base64", base64);
            rowsUpdated = db.update(TABLE_NAME, cv, "_id=" + id, null);
            Log.d(TAG,"updateData() affected " + rowsUpdated + " rows");
        }
        catch(SQLiteException exception){
            Log.e(TAG, "updateData()", exception);
        }
        finally {
            if(db != null){
                db.close();
            }
        }

    }

    //Get Methode zur Ausgabe der Nutzermailadresse
    public String getRemember(){

        String[] columns = {"email", "pw"};
        String userEntry = "";
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try{
            db = getReadableDatabase();
            cursor = db.query(TABLE_NAME, columns, "remember= 1", null,null,null,null);
            while(cursor.moveToNext()){
                userEntry = cursor.getString(0) + ":" + cursor.getString(1);
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

    //Methode zum Löschen des letzten Eintrages
    public void deleteData(){
        int rowsDeleted;
        SQLiteDatabase db = null;

        try{
            db = getWritableDatabase();
            rowsDeleted = db.delete(TABLE_NAME, "_id = (SELECT MAX(_id) FROM " + TABLE_NAME +")", null);
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
