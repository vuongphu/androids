package androlite.baitaplon;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URLDecoder;
import java.util.Locale;

/**
 * Created by Zenphone on 3/22/2017.
 */

public class FragmentTab1 extends Fragment {
    private WebViewClient client;
    private String DATABASE_NAME;
    private String TABLE_NAME;
    private String WORD;
    private ImageButton img_bt1;
    DataDictionary db;
    private TextToSpeech t1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment1, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        final WebView wv = (WebView) rootView.findViewById(R.id.webview);

        img_bt1 = (ImageButton) rootView.findViewById(R.id.imageButton);
        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        DATABASE_NAME = getArguments().getString("data").toString();
        TABLE_NAME = getArguments().getString("table").toString();
        WORD = getArguments().getString("word").toString();
        wv.setWebViewClient(new MyWebViewClient());
        WebSettings settings = wv.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        if (DATABASE_NAME.equals("viet_anh.db"))
        {
            img_bt1.setVisibility(View.INVISIBLE);
            Log.d("VVV","VO DAY");
        }
        else
        {
            img_bt1.setEnabled(true);
        }
        img_bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WORD=WORD.replace("-"," ");
                WORD=WORD.replace("\"","");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    Toast.makeText( getActivity(), "speaker 1", Toast.LENGTH_SHORT).show();
                    t1.speak(WORD, TextToSpeech.QUEUE_FLUSH, null, null);
                }else{
//                    Toast.makeText( getActivity(), "speaker 2", Toast.LENGTH_SHORT).show();
                    t1.speak(WORD, TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });


//        img_bt1.setEnabled(false);
        Log.d("GGG",DATABASE_NAME);
        Log.d("GGG","anh_viet.db");
//        String b2=

//        if (!"anh_viet.db".equals(DATABASE_NAME))
//        {
//            img_bt1.setVisibility(View.INVISIBLE);
//        }
        wv.loadData(getArguments().getString("DONE").toString()+"<br>", "text/html; charset=utf-8","UTF-8");

//        Log.d("RRR","Có "+ wv);

//        textView.setText(Html.fromHtml(getArguments().getString("DONE")));
//        textView.setText(Html.fromHtml(getString(R.string.section_format, getArguments().getInt("DONE"))));

        return rootView;
    }

    @Override
    public void onPause() {
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    public void update(String buttonTxt, String txtTxt){
        TextView t1=(TextView) getView().findViewById(R.id.section_label);
        t1.setText("ABV");


    }
    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            db=new DataDictionary(getActivity(),DATABASE_NAME,TABLE_NAME);

            String html="";
            try {
                html = URLDecoder.decode(url, "UTF-8").replace("entry://","");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT<=15)
            {
                html=IDN.toUnicode(url.replace("entry://",""),1);
            }
//            Log.d("RRR",java.net.IDN.toUnicode("xn--st-mia.com",1));
//
//            Log.d("RRR","Có ");
//            Log.d("RRR","Có "+IDN.toUnicode(url,0));
//            Log.d("RRR",url);
//            Log.d("RRR", Charset.forName("UTF-8").encode(url.replace("entry://","").toString())+"");
            Cursor cursor = db.SeachContent(html);
            if (cursor.moveToFirst())
            {
//                Log.d("RRR","Có ");
                Bundle Datasend = new Bundle();
                Intent i= new Intent(getActivity().getApplicationContext(), Main3Activity.class);
//              i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Datasend.putString("data", new String(DATABASE_NAME));
                Datasend.putString("table", new String(TABLE_NAME));
                Datasend.putString("word", new String(html));
                i.putExtras(Datasend);
                startActivity(i);
            }
            else{
//                url=url.replace("entry://","");
//                String afterDecode = URLDecoder.decode(url, "UTF-8");
                Toast.makeText( getActivity(), "Không tìm thấy từ "+html, Toast.LENGTH_SHORT).show();
            }

            return true;
        }

    }
}
