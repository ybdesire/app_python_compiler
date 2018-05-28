package compilers.ybdesire.com.pycompiler;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.util.Base64;
import org.json.JSONObject;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;

    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Admob
        MobileAds.initialize(this, "ca-app-pub-8100413825150401~5715492852");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //Edit Text
        final AppCompatEditText editText = (AppCompatEditText) findViewById(R.id.text_input_code);

        //Buttons
        Button btn=findViewById(R.id.button_tab);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.getText().insert(editText.getSelectionStart(), "    ");
            }
        });
        btn=findViewById(R.id.button_println);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.getText().insert(editText.getSelectionStart(), "print(  )");
            }
        });
        btn=findViewById(R.id.button_quote);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.getText().insert(editText.getSelectionStart(), "\"");
            }
        });
        btn=findViewById(R.id.button_semi);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.getText().insert(editText.getSelectionStart(), ";");
            }
        });
        // compile
        btn=findViewById(R.id.button_compile);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try  {

                            //Your code goes here
                            String code = editText.getText().toString();
                            String url = "http://runcode-api2-ng.shucunwang.com/compile2";
                            HttpClient httpclient = new DefaultHttpClient();

                            HttpPost httppost = new HttpPost(url);
                            httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
                            httppost.addHeader("Origin", "http://www.dooccn.com");
                            httppost.addHeader("Referer", "http://www.dooccn.com/c/");
                            httppost.addHeader("Accept-Language", "zh-cn");

                            // Add your data
                            List<NameValuePair> nameValuePairs = new ArrayList< NameValuePair >(5);
                            nameValuePairs.add(new BasicNameValuePair("code", Base64.encodeToString(code.getBytes(),0)));
                            nameValuePairs.add(new BasicNameValuePair("language", "19"));
                            nameValuePairs.add(new BasicNameValuePair("stdin", "123\\nhaha2\\n"));

                            try {
                                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                                Log.d("myapp", "works till here. 2");
                                try {
                                    HttpResponse response = httpclient.execute(httppost);
                                    String responseBody = EntityUtils.toString(response.getEntity());
                                    TextView txtOutput=findViewById(R.id.txt_output);//find output label by id

                                    JSONObject jdata = new JSONObject(responseBody);
                                    if( (jdata.get("errors")).equals("") )
                                    {
                                        setText(txtOutput,(String)jdata.get("output"));
                                    }
                                    else
                                    {
                                        setText(txtOutput,(String)jdata.get("errors"));
                                    }


                                    Log.d("myapp", "response " + responseBody);
                                    Log.d("myapp", "errors " + (String)jdata.get("errors"));
                                    Log.d("myapp", "output " + (String)jdata.get("output"));
                                } catch (ClientProtocolException e) {
                                    e.printStackTrace();
                                    TextView txtOutput=findViewById(R.id.txt_output);//find output label by id
                                    setText(txtOutput,getString(R.string.err_network));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    TextView txtOutput=findViewById(R.id.txt_output);//find output label by id
                                    setText(txtOutput,getString(R.string.err_network));
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                TextView txtOutput=findViewById(R.id.txt_output);//find output label by id
                                setText(txtOutput,getString(R.string.err_network));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView txtOutput=findViewById(R.id.txt_output);//find output label by id

                            txtOutput.setText(getString(R.string.err_network));
                        }
                    }
                });

                thread.start();
                //disable button and modify color
                Button btnc=findViewById(R.id.button_compile);
                btnc.setClickable(false);
                btnc.setBackgroundColor(Color.GRAY);

                //timer for 5s delay and enable button
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        Button btncc=findViewById(R.id.button_compile);
                        btncc.setClickable(true);
                        btncc.setBackgroundResource(android.R.drawable.btn_default);
                    }
                }, 5000);


            }
        });

        //init
        String str = "import os\n\nprint('hello world')\n";
        SpannableString ss = CodeEditText.setHighLight(str);
        editText.setText(ss);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int start, int count, int after) {
                //Log.d("onTextChanged", "onTextChanged,str_len="+cs.toString().length());
                //Log.d("onTextChanged", "start="+start);
                //Log.d("onTextChanged", "str="+cs.toString().substring(start,start+1));
                //Log.d("onTextChanged", "count="+count);
                //Log.d("onTextChanged", "after"+after);

                //if(cs.toString().substring(start,start+1).equals(" "))
                //{
                //Log.d("onTextChanged", "get space");
                    /*
                    SpannableString ss = new SpannableString(cs.toString());
                    String textToSearch = "public";
                    Pattern pattern = Pattern.compile(textToSearch);
                    Matcher matcher = pattern.matcher(ss);
                    while (matcher.find()) {
                        ss.setSpan(new ForegroundColorSpan(Color.RED), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    edittext.setText(ss);
                    */
                //}

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //Log.d("DBG", "beforeTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("DBG", "afterTextChanged");
                editText.removeTextChangedListener(this);
                String str = editText.getText().toString();

                int po = editText.getSelectionStart();//get cursor
                SpannableString ss = CodeEditText.setHighLight(str);
                editText.setText(ss);

                editText.setSelection(po);//set cursor
                editText.addTextChangedListener(this);

            }

        });


    }
}
