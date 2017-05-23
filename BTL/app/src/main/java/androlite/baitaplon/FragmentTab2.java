package androlite.baitaplon;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Zenphone on 3/22/2017.
 */

public class FragmentTab2 extends Fragment{
    EditText et1;
    DataDictionary db;
    Note x;
    String word;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment2, container, false);
        et1 = (EditText)rootView.findViewById(R.id.edit);
        word=getArguments().getString("word").toString();
        db=new DataDictionary(getContext(),getArguments().getString("data").toString(),getArguments().getString("table").toString()+"_note");
        if(db.isTableExists()==true){
//            find data
            Cursor cursor = db.SeachContent(word);

            if (cursor.moveToFirst())
            {
                x = new Note(Integer.parseInt((cursor.getString(0))),cursor.getString(1),cursor.getString(2));

                et1.setText(x.getNoteContent());

            }

        }
        else{
            db.addnewtable();
        }
//        et1 = (EditText) rootView.findViewById(R.id.edit);
        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();
        if (et1.getText()!=null)
        {
            Cursor cursor = db.SeachContent(word);

            if (cursor.moveToFirst())
            {

                db.updatenote(x.getNoteId(),word, et1.getText().toString());

            }
            else{
                db.newNode(word, et1.getText().toString());
            }

        }


//        Toast.makeText(getContext(),et1.getText()+"1", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText(getContext(),et1.getText()+"2", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(getContext(),et1.getText()+"3", Toast.LENGTH_SHORT).show();
    }
}
