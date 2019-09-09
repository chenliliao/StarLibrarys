package com.cllprivate.dbcontentproviderlib;

public class DbInfo {

    private String path;
    private String tableName;
    private int pathMatch;

    public DbInfo(String path, String tableName, int pathMatch) {
        this.path = path;
        this.tableName = tableName;
        this.pathMatch = pathMatch;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getPathMatch() {
        return pathMatch;
    }

    public void setPathMatch(int pathMatch) {
        this.pathMatch = pathMatch;
    }
}
