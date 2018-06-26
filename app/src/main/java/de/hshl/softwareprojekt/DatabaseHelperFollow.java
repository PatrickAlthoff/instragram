package de.hshl.softwareprojekt;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DatabaseHelperFollow  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FollowSystem.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Follow";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_is = "isFollowing";
    public static final String COLUMN_by = "followedBy";

    public DatabaseHelperFollow(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_is + " TEXT, " +
                COLUMN_by + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void insertData(String id, int isFollowing, int followedBy) {
        long rowId = -1;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, id);
            contentValues.put(COLUMN_is, isFollowing);
            contentValues.put(COLUMN_by, followedBy);
            db.insert(TABLE_NAME, null, contentValues);

        } catch (SQLiteException exception) {
            Log.e(TAG, "insertData()" + exception);
        } finally {
            Log.d(TAG, "InsertData()" + rowId + ")");
            if (db != null) {
                db.close();
            }
        }
    }
    public void updateData(Integer id, int isFollowing, int FollowedBy) {
        int rowsUpdated = 0;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_is, isFollowing);
        contentValues.put(COLUMN_by, FollowedBy);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );

        rowsUpdated = db.update(TABLE_NAME, contentValues,"id= "+ COLUMN_ID,null);
        Log.d(TAG, "updateData()affected"+ rowsUpdated + "rows");
         } catch(SQLiteException exception){
            Log.e(TAG, "updateData()"+ exception);
        } finally {
            if (db != null){
                db.close();
            }
        }
    }
    public void deleteData(int id){
        int rowsDeleted = 0;
        SQLiteDatabase db = null;

        try{
            db = getWritableDatabase();
            rowsDeleted = db.delete(TABLE_NAME, "id=" + id, null);
            Log.d(TAG, "deleteData() affected" + rowsDeleted + "rows");
        }catch(SQLiteException exception){
            Log.e(TAG, "deleteData()"+ exception);
        } finally {
            if (db !=null){
                db.close();
            }
        }

    }
    public void setIsFollowing(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update Follow set isFollowing = _id ");
    }
    public void setIsFollowedBy(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update Follow set FollowedBy = _id ");
    }
    public Cursor getFollowingData(){
        SQLiteDatabase db = this.getWritableDatabase();
         String query = "SELECT * FROM " + COLUMN_is ;    // query nicht korrekt
         Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getFollowerData(){
        SQLiteDatabase db = this.getWritableDatabase();
       String query = "SELECT * FROM " + COLUMN_is ;    // query nicht korrekt
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
