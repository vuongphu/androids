package androlite.baitaplon;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    DataDictionary db;
    private String DATABASE_NAME;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String TABLE_NAME;
    Note x;

    DataDictionary db_bookmark;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Menu menu;
    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        word= getIntent().getExtras().getString("word");
        getSupportActionBar().setTitle(word);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        this.menu = menu;
        DATABASE_NAME = getIntent().getExtras().getString("data");
        TABLE_NAME = getIntent().getExtras().getString("table");
        db=new DataDictionary(this,DATABASE_NAME,TABLE_NAME);

        Cursor cursor = db.SeachContent(word);
        Log.d("EEE","search "+TABLE_NAME);
        if (cursor.moveToFirst())
        {
            Log.d("EEE","INTO");
            x = new Note(Integer.parseInt((cursor.getString(0))),cursor.getString(1),cursor.getString(2));


        }



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        Log.d("CCC","1");
//        FragmentTab1 fragment_obj = (FragmentTab1)getSupportFragmentManager().findFragmentById(R.id.frame222);
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment[] fragments = new Fragment[1];
//        fragments[0] = fm.findFragmentById(R.id.frame222);
//        FragmentTab1 abc= new FragmentTab1();
//
//        Fragment fras;

//        abc.update("A","C");
//        TextView t=(TextView) fragments[0].getView().findViewById(R.id.section_label);
//        t.setText("ABC");
//        String s="";
//        (TextView) fragment_obj.getView().findViewById(R.id.section_label).setText("AD");

//        t.setText("sd");


    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d("CCC","2");
        getMenuInflater().inflate(R.menu.menu_main3, menu);
        this.menu = menu;
        MenuItem menuItem = menu.findItem(R.id.action_favorite);
        db_bookmark=new DataDictionary(this,DATABASE_NAME,TABLE_NAME+"_bookmark");
        if(db_bookmark.isTableExists()==true){
            Cursor cursor_b = db_bookmark.SeachContent(word);
            if (cursor_b.moveToFirst())
            {
                Log.d("AAA","TURN oN");
                Drawable myDrawable = getResources().getDrawable(R.drawable.bookmark_on);
                menuItem.setTitle(getString(R.string.bookmark_on));
                menuItem.setIcon(myDrawable);
            }

        }
        else{
            db_bookmark.addnewtable();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("CCC","3");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("VVV","VVV");
            mSectionsPagerAdapter.getItem(1);
            return true;
        }
        if (id == android.R.id.home) {
//            Toast.makeText(this,item+"", Toast.LENGTH_SHORT).show();

            finish(); // close this activity and return to preview activity (if there is any)
        }
        if (id == R.id.action_favorite) {
            String selItem = (String) item.getTitle();
//            Log.d("AA",item.getTitleCondensed()+"");



            if ( selItem.equals(getString(R.string.bookmark_off)))

            {
//                Log.d("AA",1+"");

                Drawable myDrawable = getResources().getDrawable(R.drawable.bookmark_on);
                item.setTitle(getString(R.string.bookmark_on));
                item.setIcon(myDrawable);
                db_bookmark.newNode(word, "1");
//                Cursor cursor = db_bookmark.SeachContent(word);
//                if (cursor.moveToFirst())
//                {
//                    Note y = new Note(Integer.parseInt((cursor.getString(0))),cursor.getString(1),cursor.getString(2));
//                    db.updatenote(y.getNoteId(),word, et1.getText().toString());
//
//                }
//                else{

//                }
            }
            else
            {
//                Log.d("AA",2+"");
                Drawable myDrawable = getResources().getDrawable(R.drawable.bookmark_off);
                item.setTitle(getString(R.string.bookmark_off));
                item.setIcon(myDrawable);
                Cursor cursor_s = db_bookmark.SeachContent(word);
                if (cursor_s.moveToFirst())
                {
                    Log.d("AAA","XOA");
                    Note y=new Note(Integer.parseInt((cursor_s.getString(0))),cursor_s.getString(1),cursor_s.getString(2));
                    db_bookmark.deleterow(y.getNoteId());
                }




            }



//            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_menu_camera));
            Toast.makeText(this,"DONE"+"", Toast.LENGTH_SHORT).show();

        }

        // User chose the "Favorite" action, mark the current item
        // as a favorite...


//        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    /**
     * A placeholder fragment containing a simple view.
     */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("CCC","4");
            Bundle args = new Bundle();
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){

                case 0:
                    FragmentTab1 tab1=new FragmentTab1();
                    args.putString("data",DATABASE_NAME);
                    args.putString("table",TABLE_NAME);
                    args.putString("word",x.getNoteWord());
                    Log.d("CCC","CHECK");
                    Log.d("CCC","x="+x.getNoteId());
                    Log.d("CCC","X="+x.getNoteContent());
                    args.putString("DONE",x.getNoteContent());

                    tab1.setArguments(args);
                    return tab1;
                case 1:
                    FragmentTab2 tab2=new FragmentTab2();

                    args.putString("data",DATABASE_NAME);
                    args.putString("table",TABLE_NAME);
                    args.putString("word",x.getNoteWord());
                    tab2.setArguments(args);
                    return tab2;
//                case 2:
//                    FragmentTab3 tab3=new FragmentTab3();
//                    return tab3;
                default:
                    return  null;
            }
//            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    if (DATABASE_NAME.equals("anh_viet.db"))
                    {
                        return "Anh Viet";
                    }
                    return "Viet Anh";
                case 1:
                    return "Ghi Chu";
//                case 2:
//                    return "SECTION 3";
            }
            return null;
        }
    }
}
