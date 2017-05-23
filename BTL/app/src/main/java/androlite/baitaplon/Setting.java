package androlite.baitaplon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Zenphone on 4/11/2017.
 */

public class Setting extends SQLiteOpenHelper {
    private static String DATABASE_PATH = "/data/data/androlite.baitaplon/databases/";
    private static final String DATABASE_NAME="UserData.db";
    private static final int DATABASE_VERSION=1;
    private  String TABLE_NAME="Table_Setting";
    private static final String KEY_ID="id";
    private static final String KEY_NAME="name";
    private static final String KEY_BOOLEN="enable";
    private SQLiteDatabase myDataBase;
    private static Context mycontext;

    public Setting(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("BBB","Tao du lieu");
        String sql="create table "+TABLE_NAME+
                "("+
                KEY_ID+" integer primary key autoincrement, "+
                KEY_NAME+" text, "+
                KEY_BOOLEN+ " INTEGER"+
                ")";
        sqLiteDatabase.execSQL(sql);
        ContentValues content= new ContentValues();
        content.put(KEY_NAME,"fisttime");
        content.put(KEY_BOOLEN,0);
        sqLiteDatabase.insert(TABLE_NAME,null,content);
    }
    public int getFisttime(){
        Log.d("BBB","AAA");
        SQLiteDatabase db = this.getReadableDatabase();
        String limit = "0,1";
        Cursor cursor=null;
        cursor = db.query(TABLE_NAME,new String[] {"id","name","enable"},"name like '"+"fisttime"+"'", null, null, null, null,limit);
        Log.d("BBB",cursor.getCount()+"");
        cursor.moveToFirst();
        return cursor.getInt(2);
    }
    public void updatefisttime() {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content=new ContentValues();
        content.put(KEY_ID,1);
        content.put(KEY_NAME,"fisttime");
        content.put(KEY_BOOLEN,1);
        Log.d("AAA","UPDATE");
        db.update(this.TABLE_NAME,content,"id=1",null);
//        String strSQL = "UPDATE "+this.TABLE_NAME+" SET Column1 = someValue WHERE columnId = "+ someValue;

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
