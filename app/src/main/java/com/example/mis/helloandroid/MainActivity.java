package com.example.mis.helloandroid;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;

enum urlStatus {
    IS_URL,
    IS_A_WORD,
    NOTHING,
    IS_A_NO_PROTOCOL_URL;
}

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

        siteView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                toastWithContent("Error code: " + Integer.toString(errorCode) + "\n" + description);
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                siteView.loadUrl(request.getUrl());
//                return true;
//            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                siteView.loadUrl(url);
                return true;
            }
        });

        // check network connection
        if (!hasNetwork()) {
            this.toastWithContent("No network connection");
        }
    }

    private boolean hasNetwork() {
        ConnectivityManager connectMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectMng.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void clickConnectButton(View sender) {
        String input = this.urlField.getText().toString();

        //dismiss keyboard
        InputMethodManager inputMng = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMng.hideSoftInputFromWindow(this.urlField.getWindowToken(), 0);

        //check network
        if (!hasNetwork()) {
            this.toastWithContent("No network connection");
            return;
        }

        // make a blank site
        siteView.loadUrl("about:blank");

        //validate the url user put in
        switch (validateInput(input)) {
            case IS_URL:
                siteView.loadUrl(input);
                break;
            case NOTHING:
                toastWithContent("You must text something");
                break;
            case IS_A_WORD:
                String googleUrl = "https://google.de/search?q=" + URLEncoder.encode(input);
                siteView.loadUrl(googleUrl);
                break;
            case IS_A_NO_PROTOCOL_URL:
                siteView.loadUrl("http://" + input);
                break;
        }
    }
    
    private void toastWithContent(String content) {
        Toast toast = Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private urlStatus validateInput(String input) {
        if (input.isEmpty()) {         // has a charater in edittext
            return urlStatus.NOTHING;
        } else {                        // else ---> check is a valid url or not
            try {
                String lastPrefix = input.substring(input.lastIndexOf("."));    // the exception mean there is no lastPrefix like ".com"
                System.out.println("prefix: " + lastPrefix);
                if (input.startsWith("http://") || input.startsWith("http://")) {
                    return urlStatus.IS_URL;
                } else {
                    return urlStatus.IS_A_NO_PROTOCOL_URL;
                }
            } catch (Exception ex) {
                return urlStatus.IS_A_WORD;
            }
        }
    }
}
