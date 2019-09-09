package com.cllprivate.dbcontentproviderlib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public abstract class SU_Provider extends ContentProvider {


    private static final String TAG = SU_Provider.class.getSimpleName();
    private SU_DbHelper mSU_dbHelper;

    public abstract UriMatcher UriMatcher();

    public abstract ArrayList<DbInfo> DbInfo();

    public abstract LinkedHashMap<String, SU_DBDataTable> tables();

    public abstract String fileName();

    public abstract int version();

    private static String FILE_NAME;
    private static int DB_VERSION;
    private static UriMatcher mUriMatcher;
    private static LinkedHashMap<String, SU_DBDataTable> mTables;
    private static ArrayList<DbInfo> mDbInfo;


    @Override
    public boolean onCreate() {
        FILE_NAME = fileName();
        DB_VERSION = version();
        mUriMatcher = UriMatcher();
        mTables = tables();
        mDbInfo = DbInfo();
        if (mSU_dbHelper == null) {
            synchronized (this) {
                if (mSU_dbHelper == null) {
                    mSU_dbHelper = new SU_DbHelper(getContext(), FILE_NAME, DB_VERSION, mTables);
                }
            }
        }
        return true;
    }


    @Nullable
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = null;
            try {
                db = mSU_dbHelper.getReadableDatabase();
            }catch (Throwable t){
                t.printStackTrace();
                try {
                    db = mSU_dbHelper.getReadableDatabase();
                }catch (Throwable t2){
                    t.printStackTrace();
                }
            }

            if (mUriMatcher != null && mDbInfo != null) {
                for (int i = 0; i < mDbInfo.size(); i++) {
                    if (mUriMatcher.match(uri) == mDbInfo.get(i).getPathMatch()) {
                        cursor = db.query(mDbInfo.get(i).getTableName(), projection, selection, selectionArgs, null, null, sortOrder);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e(TAG, "query error " + t.getMessage());
        }

        return cursor;
    }


    @Nullable
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        try {
            SQLiteDatabase db = null;
            try {
                db = mSU_dbHelper.getWritableDatabase();
            }catch (Throwable t){
                t.printStackTrace();
                try {
                    db = mSU_dbHelper.getWritableDatabase();
                }catch (Throwable t2){
                    t.printStackTrace();
                }
            }

            long rowId = -1;
            if (mUriMatcher != null && mDbInfo != null) {
                for (int i = 0; i < mDbInfo.size(); i++) {
                    if (mUriMatcher.match(uri) == mDbInfo.get(i).getPathMatch()) {
                        if (db != null) rowId = db.insert(mDbInfo.get(i).getTableName(), null, values);
                    }
                }
            }
            if (rowId != -1) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e(TAG, "insert error " + t.getMessage());
        }


        return uri;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        try {
            SQLiteDatabase db = null;
            try {
                db = mSU_dbHelper.getWritableDatabase();
            }catch (Throwable t){
                t.printStackTrace();
                try {
                    db = mSU_dbHelper.getWritableDatabase();
                }catch (Throwable t2){
                    t.printStackTrace();
                }
            }

            if (mUriMatcher != null && mDbInfo != null) {
                for (int i = 0; i < mDbInfo.size(); i++) {
                    if (mUriMatcher.match(uri) == mDbInfo.get(i).getPathMatch()) {
                        count = db.update(mDbInfo.get(i).getTableName(), values, selection, selectionArgs);
                    }
                }
            }

            if (count > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

        } catch (Throwable t) {
            t.printStackTrace();
            Log.e(TAG, "update error " + t.getMessage());
        }


        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        try {
            SQLiteDatabase db = null;
            try {
                db = mSU_dbHelper.getWritableDatabase();
            }catch (Throwable t){
                t.printStackTrace();
                try {
                    db = mSU_dbHelper.getWritableDatabase();
                }catch (Throwable t2){
                    t.printStackTrace();
                }
            }

            if (mUriMatcher != null && mDbInfo != null) {
                for (int i = 0; i < mDbInfo.size(); i++) {
                    if (mUriMatcher.match(uri) == mDbInfo.get(i).getPathMatch()) {
                        count = db.delete(mDbInfo.get(i).getTableName(), selection, selectionArgs);
                    }
                }
            }

            if (count > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e(TAG, "delete error " + t.getMessage());
        }


        return count;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        if (mUriMatcher != null) {
            switch (mUriMatcher.match(uri)) {
//                case TABLE_1_MATCH:
//                    return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + TABLE_1;
            }
        }

        return null;
    }

}
