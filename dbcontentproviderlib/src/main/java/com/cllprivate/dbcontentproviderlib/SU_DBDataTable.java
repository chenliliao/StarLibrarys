package com.cllprivate.dbcontentproviderlib;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class SU_DBDataTable {

    public abstract void onCreate(SQLiteDatabase db);


    public abstract String getName();

    private static final String SQLITE_STMT_TABLE_LIST = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android%'";
    private static final String SQLITE_TABLE_NAME_COLUMN = "name";
    private static final String SQLITE_STMT_TEMPLATE_DROP_TABLE = "DROP TABLE IF EXISTS %s";
    private static final String SQLITE_STMT_TEMPLATE_RENAME_TABLE = "ALTER TABLE %s RENAME TO %s";
    private static final String SQLITE_STMT_TEMPLATE_COPY_COLUMNS = "INSERT INTO %s (%s) SELECT %s FROM %s";
    private static final String SQLITE_STMT_TEMPLATE_LIST_COLUMNS = "SELECT * FROM %s LIMIT 1";
    private static final String TEMP_SUFFIX = "_temp_";


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (db != null && oldVersion < newVersion){
            String tempTable = getTmpTableName(getName());
            renameTable(db, getName(), tempTable);
            onCreate(db);
            joinColumns(db, tempTable, getName());
            dropTable(db, tempTable);

        }
    }

    public static void renameTable(SQLiteDatabase db, String oldName, String newName){
        if (db != null && !TextUtils.isEmpty(oldName) && !TextUtils.isEmpty(newName) && !TextUtils.equals(oldName, newName)){
            db.execSQL(String.format(SQLITE_STMT_TEMPLATE_RENAME_TABLE, oldName, newName));
        }
    }

    public static void joinColumns(SQLiteDatabase db, String tempTable, String newTable){

        if (db != null){
            db.delete(newTable, null, null);

            ArrayList<String> tempTableColumnList = new ArrayList<>(listColumns(db, tempTable));
            List<String> newTableColumnList = listColumns(db, newTable);
            tempTableColumnList.retainAll(newTableColumnList);

            String common_columns = TextUtils.join(",", tempTableColumnList);
            db.execSQL(String.format(SQLITE_STMT_TEMPLATE_COPY_COLUMNS, newTable,
                    common_columns, common_columns, tempTable));
        }
    }



    static List<String> listColumns(SQLiteDatabase db, String table) {
        Cursor cursor = db.rawQuery(String.format(SQLITE_STMT_TEMPLATE_LIST_COLUMNS, table), null);
        if (cursor == null) {
            return null;
        }

        List<String> columns = Arrays.asList(cursor.getColumnNames());
        cursor.close();

        return columns;
    }


    public static String getTmpTableName(String oldName){
        String tmp = oldName + TEMP_SUFFIX;
        if (!TextUtils.isEmpty(oldName)){
            if (TextUtils.equals(oldName, tmp)){
                tmp += new Random(Long.MAX_VALUE);
            }
        }
        return tmp;
    }


    public static ArrayList<String> getTableList(SQLiteDatabase db){
        ArrayList<String> list = null;
        if (db != null){

            try {
                db.beginTransaction();

                Cursor cursor = db.rawQuery(SQLITE_STMT_TABLE_LIST, null);

                if (cursor != null){
                    if (cursor.moveToFirst()){
                        int columnIndex = cursor.getColumnIndex(SQLITE_TABLE_NAME_COLUMN);
                        list = new ArrayList<>();
                        do {
                            list.add(cursor.getString(columnIndex));
                            Log.w("TAG","test get str " + cursor.getString(columnIndex));
                        }while (cursor.moveToNext());

                    }
                    cursor.close();
                }
                db.setTransactionSuccessful();

                return list;
            }catch (Throwable t){
                t.printStackTrace();
            }finally {
                db.endTransaction();
            }
        }
        return list;

    }


    public static void dropTable(SQLiteDatabase db, String table) {
        if (db != null && !TextUtils.isEmpty(table)){
            db.execSQL(String.format(SQLITE_STMT_TEMPLATE_DROP_TABLE, table));
        }

    }
}
