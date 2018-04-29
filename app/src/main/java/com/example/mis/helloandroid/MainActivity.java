package com.example.mis.helloandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

enum urlStatus {
    IS_URL,
    IS_A_WORD,
    NOTHING,
    IS_A_NO_PROTOCOL_URL
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
        urlField = findViewById(R.id.editText2);
        connectButton = findViewById(R.id.button);
        siteView = findViewById(R.id.websiteView);

        siteView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                toastWithContent("Error code: " + Integer.toString(errorCode) + "\n" + description);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                siteView.loadUrl(url);
                return true;
            }
        });

        // check network connection
        if (hasNoNetwork()) {
            this.toastWithContent("No network connection");
        }
    }

    private boolean hasNoNetwork() {
        ConnectivityManager connectMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectMng != null ? connectMng.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnected();
    }

    public void clickConnectButton(View sender) {
        String input = this.urlField.getText().toString();

        //dismiss keyboard
        InputMethodManager inputMng = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMng != null) {
            inputMng.hideSoftInputFromWindow(this.urlField.getWindowToken(), 0);
        }

        //check network
        if (hasNoNetwork()) {
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
                String googleUrl = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        googleUrl = "https://google.de/search?q=" + URLEncoder.encode(input,
                                StandardCharsets.UTF_8.toString());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
        if (input.isEmpty()) {              // has a character in edit text
            return urlStatus.NOTHING;
        } else {                            // else ---> check is a valid url or not
            try {
                String lastPrefix = input.substring(input.lastIndexOf("."));
                System.out.println("prefix: " + lastPrefix);
                if (input.startsWith("http://") || input.startsWith("http://")) {
                    return urlStatus.IS_URL;
                } else {
                    return urlStatus.IS_A_NO_PROTOCOL_URL;
                }
            } catch (Exception ex) {        // the exception mean there is no lastPrefix like ".com"
                return urlStatus.IS_A_WORD;
            }
        }
    }
}
