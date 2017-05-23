package androlite.baitaplon;


import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    List<String> mAllValues;
    private ArrayAdapter<String> mAdapter;
    private Context mContext;
    DataDictionary db;
    private String DATABASE_NAME;
    private String TABLE_NAME;
    private View footer;
    private Setting db_setting;
    private int pageCount = 1;
    private boolean ys=true;
    private boolean dissearch=false;
    private boolean stopdata=false;
    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);

        if(getArguments().getString("DONE")==getString(R.string.label_anh_viet))
        {
            DATABASE_NAME="anh_viet.db";
            TABLE_NAME="anh_viet";

            Log.d("AA",getArguments().getString("DONE")+" 1"+DATABASE_NAME+" "+TABLE_NAME);
        }
        else if(getArguments().getString("DONE")==getString(R.string.label_viet_anh)){
            DATABASE_NAME="viet_anh.db";
            TABLE_NAME="viet_anh";
            Log.d("AA",getArguments().getString("DONE")+" 2"+DATABASE_NAME+" "+TABLE_NAME);
        }
        else  if(getArguments().getString("DONE")==getString(R.string.label_fav_av)){
            dissearch=true;
            DATABASE_NAME="anh_viet.db";
            TABLE_NAME="anh_viet";
            Log.d("CCC",dissearch+"");
        }
        else  if(getArguments().getString("DONE")==getString(R.string.label_fav_va)){
            dissearch=true;
            DATABASE_NAME="viet_anh.db";
            TABLE_NAME="viet_anh";
            Log.d("CCC",dissearch+"");
        }

//        }

        db = new DataDictionary(getActivity(),DATABASE_NAME,TABLE_NAME);
        // Add footer view
//         footer = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        footer = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view, null, false);

//        View layout = getView().inflate(R.layout.search_fragment,ViewGroup.VISIBLE , false);
//        ListView listView = (ListView) getView.findViewById(android.R.id.list);
        db_setting =new Setting(getActivity());
//        db_setting.close();
//


        if(db_setting.getFisttime()!=0 && dissearch!=true) {
            listallword();

//            Log.d("CCC","AAAA");
        }
        else{
            listbookmark();
        }
//
//

    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {

        Bundle Datasend = new Bundle();
        String item = (String) listView.getAdapter().getItem(position);
//        Toast.makeText(getActivity().getApplicationContext(),item, Toast.LENGTH_LONG).show();
        if (getActivity() instanceof OnItem1SelectedListener) {
            ((OnItem1SelectedListener) getActivity()).OnItem1SelectedListener(item);
        }
        getFragmentManager().popBackStack();
        Intent i= new Intent(getActivity().getApplicationContext(), Main3Activity.class);
        Datasend.putString("data", new String(DATABASE_NAME));
        Datasend.putString("table", new String(TABLE_NAME));
        Datasend.putString("word", new String(item));
        i.putExtras(Datasend);
        startActivityForResult(i,69);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.search_fragment, container, false);
        listView = (ListView) layout.findViewById(android.R.id.list);
        TextView emptyTextView = (TextView) layout.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyTextView);
        if(dissearch==false)
        {
            listView.addFooterView(footer);
        }


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, final int i, int i1, int i2) {
                final int total=i+i1;
                Log.d("TTT", "--firstItem:" + i + "  visibleItemCount:" + i1 + "  totalItems:" + i2 + "  pageCount:" +total);
                if (total == i2 && ys==true && stopdata==false) {

                    ys=false;
                    // Execute some code after 15 seconds have passed
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable(){
                        public void run() {

                            if(db_setting.getFisttime()!=0 && dissearch!=true) {
                                List<Note> Listwordfind= new ArrayList<Note>();
                                Listwordfind = db.Listwordper30(total);

                                for (Note item:Listwordfind)
                                {
                                    mAllValues.add(item.getNoteWord());
                                }

                                mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mAllValues);

                                setListAdapter(mAdapter);
                                listView.setSelection(i);

                                ys=true;
//
                            }

                        }
                    }, 300);
                }



            }
        });
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        if(dissearch==true)
        {
            searchItem.setVisible(false);
            Log.d("VVV","AAAA");

        }
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        private  String newText;
        public MyTask(String newtext)
        {
            this.newText=newtext;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            stopdata=true;
                List<Note> Listwordfind = new ArrayList<Note>();
                Listwordfind = db.Listwordfind(newText);


                if (newText == null || newText.trim().isEmpty()) {
//                    resetSearch();
                    stopdata=false;

//                    return false;
                } else {
//
                    List<String> filteredValues = new ArrayList<String>();

                    for (Note item:Listwordfind)
                    {
                        filteredValues.add(item.getNoteWord());

                    }
//                    Log.d("TFF",filteredValues.size()+"");
                    mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, filteredValues);
//                    setListAdapter(mAdapter);
//
                }
//
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.d("TFF",mAdapter.getCount()+"");
            if(stopdata==false)
            {
                resetSearch();
            }
            else
            {
                listView.removeFooterView(footer);
                setListAdapter(mAdapter);
            }


        }
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        final String newText2=newText;

        new MyTask(newText2).execute();


//        Handler handler = new Handler();
//        class DelayedTask implements Runnable {
//            int cnt, time;
//            Handler handler;
//            DelayedTask(Handler h) {handler=h;}
//            public void run() {
//                stopdata=true;
//                List<Note> Listwordfind = new ArrayList<Note>();
//                Listwordfind = db.Listwordfind(newText2);
//
//
//                if (newText2 == null || newText2.trim().isEmpty()) {
//                    resetSearch();
//                    stopdata=false;
//
////                    return false;
//                } else {
//                    listView.removeFooterView(footer);
//                    List<String> filteredValues = new ArrayList<String>();
//
//                    for (Note item:Listwordfind)
//                    {
//                        filteredValues.add(item.getNoteWord());
//
//                    }
//                    mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, filteredValues);
//                    setListAdapter(mAdapter);
//                }
//
//            }
//        }
//        handler.postDelayed(new DelayedTask(handler),150);

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run() {
//                stopdata=true;
//                List<Note> Listwordfind = new ArrayList<Note>();
//                Listwordfind = db.Listwordfind(newText2);
//
//
//                if (newText2 == null || newText2.trim().isEmpty()) {
//                    resetSearch();
//                    stopdata=false;
//
////                    return false;
//                } else
//                {
//                    listView.removeFooterView(footer);
//                    List<String> filteredValues = new ArrayList<String>();
//
//                    for (Note item:Listwordfind)
//                    {
//                        filteredValues.add(item.getNoteWord());
//
//                    }
//
//
//
//
//
//                    mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, filteredValues);
//                    setListAdapter(mAdapter);
//                }
//
//
//
//            }
//        }, 150);
//
        return false;
    }

    public void resetSearch() {
        List<String> filteredValues = new ArrayList<String>();
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, filteredValues);
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    public interface OnItem1SelectedListener {
        void OnItem1SelectedListener(String item);
    }
    private void listbookmark(){
        DataDictionary db_bookmark=new DataDictionary(getActivity(),DATABASE_NAME,TABLE_NAME+"_bookmark");
        if(db_bookmark.isTableExists()==true){
            List<Note> Listwordfind= new ArrayList<Note>();
            Listwordfind = db_bookmark.Listallbookmark();
            List<String> filteredValues = new ArrayList<String>();
            for (Note item:Listwordfind)
            {
                filteredValues.add(item.getNoteWord());
            }
            mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, filteredValues);
            setListAdapter(mAdapter);

        }
        else{
            db_bookmark.addnewtable();
        }


    }
    private void listallword(){
        DataDictionary dballword=new DataDictionary(getActivity(),DATABASE_NAME,TABLE_NAME);
        mAllValues = new ArrayList<>();
        List<Note> Listwordfind= new ArrayList<Note>();
        Listwordfind = dballword.Listwordper30(0);
        Log.d("TTT",Listwordfind.size()+"");
        for (Note item:Listwordfind)
        {
            mAllValues.add(item.getNoteWord());
        }

        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mAllValues);
        setListAdapter(mAdapter);
        dballword.close();
    }
    private void populateList(){

        mAllValues = new ArrayList<>();

        mAllValues.add("ABC");




        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mAllValues);
        setListAdapter(mAdapter);
    }
}
