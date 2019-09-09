package com.cllprivate.dbcontentproviderlib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class SU_DbHelper extends SQLiteOpenHelper {


    private static final String TAG = SU_DbHelper.class.getSimpleName();
    private LinkedHashMap<String, SU_DBDataTable> mTables;
    public SU_DbHelper(@Nullable Context context, String dbFileName, int version, LinkedHashMap<String, SU_DBDataTable> tables) {
        super(context, dbFileName, null, version);
        mTables = tables;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG,"SQLiteOpenHelper onCreate db = " + (db == null));
        if (db != null){
            if (mTables != null){
                Collection<SU_DBDataTable> tables = mTables.values();
                Iterator<SU_DBDataTable> iterator = tables.iterator();
                try {
                    db.beginTransaction();

                    while (iterator.hasNext()){
                        iterator.next().onCreate(db);
                    }
                    db.setTransactionSuccessful();
                }catch (Throwable t){
                    t.printStackTrace();
                    Log.w(TAG,"SQLiteOpenHelper onCreate error " + t.getMessage());
                }finally {
                    if (db.inTransaction()){
                        db.endTransaction();
                    }
                }
            }

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,"SQLiteOpenHelper onUpgrade oldVersion is  " + oldVersion + "  newVersion is " + newVersion);
        if (db != null){
            ArrayList<String> oldTables = SU_DBDataTable.getTableList(db);
            if(mTables == null) return;
            Set<String> newTables = mTables.keySet();
            if (oldTables == null || oldTables.size() == 0 || newTables == null || newTables.size() == 0){
                onCreate(db);
                return;
            }

            try {
                Set<String> tmpTables = new HashSet<>();
                if (oldTables.size() == newTables.size()){
                    for (String oldName : oldTables){
                        if (!newTables.contains(oldName)){
                            SU_DBDataTable.dropTable(db, oldName);
                            tmpTables.add(oldName);
                        }
                    }
                    oldTables.remove(tmpTables);
                }

                for (String newName : newTables){
                    if (oldTables.contains(newName)){
                        mTables.get(newName).onUpgrade(db, oldVersion, newVersion);
                    }else{
                        mTables.get(newName).onCreate(db);
                    }
                }
                db.setTransactionSuccessful();
            }catch (Throwable t){
                t.printStackTrace();
            }finally {
                if (db.inTransaction()){
                    db.endTransaction();
                }
            }
        }
    }
}
