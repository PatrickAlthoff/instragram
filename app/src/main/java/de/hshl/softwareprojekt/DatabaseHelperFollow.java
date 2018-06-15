package de.hshl.softwareprojekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperFollow  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FollowSystem.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Follow";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_is= "isFollowing";
    public static final String COLUMN_by = "followedBy";

    public DatabaseHelperFollow(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }
//
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
    public boolean insertData(String id, int isFollowing, int followedBy) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_is, isFollowing);
        contentValues.put(COLUMN_by, followedBy);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }
    public boolean updateData(Integer id, int isFollowing, int FollowedBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_is, isFollowing);
        contentValues.put(COLUMN_by, FollowedBy);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    public Cursor getPerson(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[] { Integer.toString(id) } );
        return res;
    }
    public Cursor getAllPersons() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_NAME, null );
        return res;
    }
    public Integer deletePerson(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }
}
