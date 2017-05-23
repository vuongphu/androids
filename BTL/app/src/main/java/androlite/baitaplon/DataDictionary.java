package androlite.baitaplon;

/**
 * Created by Zenphone on 3/21/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataDictionary  extends  SQLiteOpenHelper{
    private static String DATABASE_PATH = "/data/data/androlite.baitaplon/databases/";
    private String DATABASE_NAME;
    private static final int DATABASE_VERSION=1;
    private  String TABLE_NAME;
    private static final String KEY_ID="id";
    private static final String KEY_WORD="word";
    private static final String KEY_CONTENT="content";
    private SQLiteDatabase myDataBase;
    private static Context mycontext;
    public DataDictionary(Context context,String DATABASE_NAME,String TABLE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mycontext = context;
        this.DATABASE_NAME= DATABASE_NAME;
        this.TABLE_NAME= TABLE_NAME;
    }
    public DataDictionary(Context context,String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mycontext = context;
        this.DATABASE_NAME= DATABASE_NAME;
    }
    public DataDictionary(Context context) {
        super(context, null, null, DATABASE_VERSION);
        mycontext = context;

    }
    public void openDataBase(String DATABASE_NAME) throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READONLY);
    }
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }
    private boolean checkDataBase(String DATABASE_NAME) {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }
    private void copyDataBase(String DATABASE_NAME) throws IOException {
        InputStream myInput = mycontext.getAssets().open(DATABASE_NAME);
        Log.d("BBB",mycontext.getAssets()+" ABC");
        Log.d("BBB",mycontext+" ABC");
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
    public void copyDataBaseFromDownload(String DATABASE_NAME) throws IOException {
        InputStream myInput = new FileInputStream("data/data/androlite.baitaplon/ditionary/"+DATABASE_NAME);
//        Log.d("BBB",mycontext.getAssets()+" ABC");
//        Log.d("BBB",mycontext+" ABC");
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        new File("data/data/androlite.baitaplon/ditionary/"+DATABASE_NAME).delete();

        new File("data/data/androlite.baitaplon/ditionary.zip").delete();

    }
    public void createDataBase(String DATABASE_NAME) throws IOException {
        boolean dbExist = checkDataBase(DATABASE_NAME);

        if (dbExist) {

        } else {
            this.getReadableDatabase();
            try {
                copyDataBase(DATABASE_NAME);
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }
    public Cursor SeachContent(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String limit = "0,1";
        Cursor cursor=null;
        cursor = db.query(this.TABLE_NAME,new String[] {"id","word","content"},"word like '"+keyword+"'", null, null, null, null,limit);
        return cursor;
    }
    public List<Note> Listwordfindutf(String keyword,String TABLE_NAME) {
        List<Note> Listwordreturn = new ArrayList<Note>();
        String limit = "0,16";
        Cursor cursor=null;
        SQLiteDatabase db = this.getReadableDatabase();
        String str = "";
        for (int i = 0; i <= keyword.length() - 1; i++)
        {


        }
        cursor = db.query(this.TABLE_NAME, // ten bang
                new String[] {"id","word","content"}, // danh sach cot can lay
                "word like '"+keyword+"%'", // dieu kien where
                null, // doi so dieu kien where
                null, // bieu thuc groupby
                null, // bieu thuc having
                null, // bieu thuc orderby
                limit // "0,3" //bieuthuc limit
        );

        if (cursor.getCount() != 0 && cursor != null) {
            cursor.moveToFirst();
            do {
                Note x = new Note(cursor.getString(1));
                Listwordreturn.add(x);
            } while (cursor.moveToNext());
        }

        return Listwordreturn;
    }

    public void deleterow(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(this.TABLE_NAME,"id="+id,null);

    }
    public void addnewtable(){
        SQLiteDatabase db = this.getReadableDatabase();
        String CREATE_TABLE_NEW_USER = "CREATE TABLE " + this.TABLE_NAME +
                "(" +
                KEY_ID+" integer primary key autoincrement, "+
                KEY_WORD+" text,"+
                KEY_CONTENT+" text"+
                ")";
        db.execSQL(CREATE_TABLE_NEW_USER);
        db.close();
    }
    public Cursor findNote(String word)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String limit = "0,1";
        Cursor cursor=null;
        cursor = db.query(this.TABLE_NAME,new String[] {"id","word","content"},"word like '"+word+"'", null, null, null, null,limit);
        return cursor;

    }

    public void updatenote(int id,String word,String note) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content=new ContentValues();
        content.put(KEY_ID,id);
        content.put(KEY_WORD,word);
        content.put(KEY_CONTENT,note);
        Log.d("AAA","UPDATE");
        db.update(this.TABLE_NAME,content,"id="+id,null);
//        String strSQL = "UPDATE "+this.TABLE_NAME+" SET Column1 = someValue WHERE columnId = "+ someValue;

    }
    public void newNode(String word,String note)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content=new ContentValues();
        content.put(KEY_WORD,word);
        content.put(KEY_CONTENT,note);
        String ColumnNull=null;
        db.insert(this.TABLE_NAME,ColumnNull,content);
    }
    public boolean isTableExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=null;
        try
        {
            cursor = db.query(this.TABLE_NAME, null,
                    null, null, null, null, null);
            return true;
        }
        catch (Exception e) {
    /* fail */
            return false;
        }
    }
    public List<Note> Listallbookmark(){
        List<Note> Listwordreturn = new ArrayList<Note>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=null;
        cursor = db.query(this.TABLE_NAME, // ten bang
                new String[] {"id","word","content"}, // danh sach cot can lay
                null, // dieu kien where
                null, // doi so dieu kien where
                null, // bieu thuc groupby
                null, // bieu thuc having
                null // bieu thuc orderby
                 // "0,3" //bieuthuc limit
        );
        if (cursor.getCount() != 0 && cursor != null) {
            cursor.moveToFirst();
            do {
                Note x = new Note(cursor.getString(1));
                Listwordreturn.add(x);
            } while (cursor.moveToNext());
        }

        return Listwordreturn;
    }
    public List<Note> Listwordper30(int setlimit){
        List<Note> Listwordreturn = new ArrayList<Note>();
        String limit = setlimit+",16";
        Cursor cursor=null;
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.query(this.TABLE_NAME, // ten bang
                new String[] {"id","word","content"}, // danh sach cot can lay
                null, // dieu kien where
                null, // doi so dieu kien where
                null, // bieu thuc groupby
                null, // bieu thuc having
                null, // bieu thuc orderby
                limit // "0,3" //bieuthuc limit
        );


        if (cursor.getCount() != 0 && cursor != null) {
            cursor.moveToFirst();
            do {
                Note x = new Note(cursor.getString(1));
                Listwordreturn.add(x);
            } while (cursor.moveToNext());
        }

        return Listwordreturn;
    }
    public List<Note> Listwordfind(String keyword) {
        List<Note> Listwordreturn = new ArrayList<Note>();
        String limit = "0,16";
        Cursor cursor=null;
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.query(this.TABLE_NAME, // ten bang
                new String[] {"id","word","content"}, // danh sach cot can lay
                "word like '"+keyword+"%'", // dieu kien where
                null, // doi so dieu kien where
                null, // bieu thuc groupby
                null, // bieu thuc having
                null, // bieu thuc orderby
                limit // "0,3" //bieuthuc limit
        );


        if (cursor.getCount() != 0 && cursor != null) {
            cursor.moveToFirst();
            do {
                Note x = new Note(cursor.getString(1));
                Listwordreturn.add(x);
            } while (cursor.moveToNext());
        }

        return Listwordreturn;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
