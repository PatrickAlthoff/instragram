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
    private static final String TABLE_POSTS = "posts";
    public static final String SQL_TABLE_CREATE_POSTS =
            "CREATE TABLE " + TABLE_POSTS + " (_id INTEGER, username TEXT, path TEXT, titel TEXT, hashtags TEXT, date TEXT, liked INTEGER, userKey INTEGER)";
    private static final String TABLE_STORIES = "stories";
    public static final String SQL_TABLE_CREATE_STORIES = "CREATE TABLE " + TABLE_STORIES + " (_id INTEGER, username TEXT, base64 TEXT, titel TEXT, hashtags TEXT, date TEXT, liked INTEGER, userKey INTEGER)";
    private static final String TABLE_LIKECOUNT = "likecount";
    public static final String SQL_TABLE_CREATE_POSTLIKECOUNT = "CREATE TABLE " + TABLE_LIKECOUNT + " (_id INTEGER, username TEXT, liked INTEGER)";
    private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_POSTS + ";";
    private static final int DATABASE_VERSION = 1;

    //Constructor für die Datenbank
    public DatabaseHelperPosts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DBHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_TABLE_CREATE_POSTS + " angelegt.");
            db.execSQL(SQL_TABLE_CREATE_POSTS);
            Log.d(TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_TABLE_CREATE_STORIES + " angelegt.");
            db.execSQL(SQL_TABLE_CREATE_STORIES);
            Log.d(TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_TABLE_CREATE_POSTLIKECOUNT + " angelegt.");
            db.execSQL(SQL_TABLE_CREATE_POSTLIKECOUNT);
        } catch (Exception ex) {
            Log.e(TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrade database from Version " + oldVersion + " to " + newVersion);
        db.execSQL(SQL_TABLE_DROP);
        onCreate(db);

    }

    //Funktion zur Abfrage eines Like Status des jeweiligen Posts
    public int getLikeCount(long id, String username) {
        String[] columns = {"username", "liked"};
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int like = 0;
        int i = 0;
        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_LIKECOUNT, columns, "_id=" + id + " AND " + "username=" + "'" + username + "'", null, null, null, null);

            while (cursor.moveToNext()) {
                like = cursor.getInt(1);
                i++;
            }
        } catch (SQLiteException exception) {
            Log.e(TAG, "getStory()", exception);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return like + i;
    }

    //Insert Funktion zum Hinzufügen eines Eintrags in die Likecount Tabelle
    public void insertIntoLikeCount(long id, String name, boolean liked) {
        long rowId = -1;
        SQLiteDatabase db = null;
        int like = liked ? 1 : 0;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("_id", id);
            values.put("username", name);
            values.put("liked", like);
            rowId = db.insert(TABLE_LIKECOUNT, null, values);
        } catch (SQLiteException exception) {
            Log.e(TAG, "insertIntoLikeCount()", exception);
        } finally {
            Log.d(TAG, "insertIntoLikeCount(rowId:" + rowId + ")");

            if (db != null) {
                db.close();
            }
        }
    }


    //Insert Methode zum einfügen weiterer Nutzer (email + pw)
    public void insertStory(int id, String name, String path, String titel, String hashtags, String date, boolean liked, long userKey) {
        long rowId = -1;
        SQLiteDatabase db = null;
        int like = liked ? 1 : 0;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("_id", id);
            values.put("username", name);
            values.put("base64", path);
            values.put("titel", titel);
            values.put("hashtags", hashtags);
            values.put("date", date);
            values.put("liked", like);
            values.put("userKey", userKey);
            rowId = db.insert(TABLE_STORIES, null, values);
        } catch (SQLiteException exception) {
            Log.e(TAG, "insertPost()", exception);
        } finally {
            Log.d(TAG, "insertPost(rowId:" + rowId + ")");

            if (db != null) {
                db.close();
            }
        }
    }

    //Get Methode zur Datenausgabe
    public ArrayList<String> getStory(long userKey) {

        String[] columns = {"_id", "username", "base64", "titel", "hashtags", "date", "liked", "userKey"};

        ArrayList<String> story = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_STORIES, columns, "userKey=" + userKey, null, null, null, null);

            while (cursor.moveToNext()) {
                story.add(0, cursor.getString(0) + " : " + cursor.getString(1) + " : " + cursor.getString(2) + " : " + cursor.getString(3) + " : " + cursor.getString(4) + " : " + cursor.getString(5) + " : " + cursor.getString(6) + " : " + cursor.getString(7));
            }
        } catch (SQLiteException exception) {
            Log.e(TAG, "getStory()", exception);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return story;
    }


    //Get Methode zur Datenausgabe
    public ArrayList<String> getData() {

        String[] columns = {"_id", "username", "path", "titel", "hashtags", "date", "liked"};
        ArrayList<String> postList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_POSTS, columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                postList.add(0, cursor.getString(0) + " : " + cursor.getString(1) + " : " + cursor.getString(2) + " : " + cursor.getString(3) + " : " + cursor.getString(4) + " : " + cursor.getString(5) + " : " + cursor.getString(6));
            }
        } catch (SQLiteException exception) {
            Log.e(TAG, "getData()", exception);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return postList;
    }

    public void updateUserPosts(long id, String username) {

        int rowsUpdated = 0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            rowsUpdated = db.update(TABLE_POSTS, values, "userKey=" + id, null);
            Log.d(TAG, "updateUserPosts() affected " + rowsUpdated + " rows");

        } catch (SQLiteException exception) {
            Log.e(TAG, "updateUserPosts()", exception);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    //Update den Like Status
    public void updateStory(long id, String bas64, String titel) {

        int rowsUpdated = 0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("base64", bas64);
            values.put("titel", titel);
            rowsUpdated = db.update(TABLE_STORIES, values, "userKey=" + id, null);
            Log.d(TAG, "updateStory() affected " + rowsUpdated + " rows");

        } catch (SQLiteException exception) {
            Log.e(TAG, "updateLike()", exception);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    //Update den Like Status
    public void updateLike(long id, String username, boolean liked) {

        int rowsUpdated = 0;
        SQLiteDatabase db = null;
        int like = liked ? 1 : 0;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("liked", like);
            rowsUpdated = db.update(TABLE_LIKECOUNT, values, "_id=" + id + " AND " + "username=" + "'" + username + "'", null);
            Log.d(TAG, "updateLike() affected " + rowsUpdated + " rows");

        } catch (SQLiteException exception) {
            Log.e(TAG, "updateLike()", exception);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    //Methode zum Löschen des letzten Eintrages
    public void deleteStory(long id) {
        int rowsDeleted;
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            rowsDeleted = db.delete(TABLE_STORIES, "userKey=" + id, null);
            Log.d(TAG, "deleteStory() affected " + rowsDeleted + " rows");
        } catch (SQLiteException exception) {
            Log.e(TAG, "deleteStory()", exception);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    //Methode zum Löschen des letzten Eintrages
    public void deletePost(int id) {
        int rowsDeleted;
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            rowsDeleted = db.delete(TABLE_POSTS, "_id LIKE " + "'%" + id + "%'", null);
            Log.d(TAG, "deleteData() affected " + rowsDeleted + " rows");
        } catch (SQLiteException exception) {
            Log.e(TAG, "deleteData()", exception);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public int getID(long UserKey) {

        String[] columns = {"_id", "userKey"};
        int ID = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();

            cursor = db.query(TABLE_STORIES, columns, "userKey like " + "'%" + UserKey + "%'", null, null, null, null);
            while (cursor.moveToNext()) {
                ID = cursor.getInt(0);
            }

        } catch (SQLiteException exception) {
            Log.e(TAG, "getUserKey()", exception);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return ID;
    }
}
