package com.example.mis.helloandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    EditText urlField;
    Button connectButton;
    WebView siteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //register
        urlField = (EditText) findViewById(R.id.editText2);
        connectButton = (Button) findViewById(R.id.button);
        siteView = (WebView) findViewById(R.id.websiteView);

        // check network connection
        if (hasNetwork()) {
        } else {
            Toast toast = Toast.makeText(MainActivity.this, "No network connection", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private boolean hasNetwork() {
        ConnectivityManager connectMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectMng.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void clickConnectButton(View sender) {
        String input = this.urlField.getText().toString();
        siteView.loadUrl("about:blank");

        //dismiss keyboard
        InputMethodManager inputMng = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMng.hideSoftInputFromWindow(this.urlField.getWindowToken(), 0);


        //get response status code from url
        try {
            URL url = new URL(input);
            Log.v("url:", url.toString());
            siteView.loadUrl(input);
        } catch (IOException exception) {
            Toast toast = Toast.makeText(MainActivity.this,
                    "Invail url - " + exception.getMessage(),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
    }
    private boolean getResponse(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        return (con.getResponseCode() == 200);
    }
}
