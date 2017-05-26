package com.bsi.dms.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "download.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE info(path VARCHAR(1024), thid INTEGER, done INTEGER, PRIMARY KEY(path, thid))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS info"); // 此处是删除数据表，在实际的业务中一般是需要数据备份的
		onCreate(db); // 调用onCreate方法重新创建数据表，也可以自己根据业务需要创建新的的数据表
	}

}
