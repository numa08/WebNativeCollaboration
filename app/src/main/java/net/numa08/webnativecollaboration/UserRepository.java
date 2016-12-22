package net.numa08.webnativecollaboration;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

public class UserRepository {

    final WebView webView;

    public UserRepository(WebView webView) {
        this.webView = webView;
    }

    @JavascriptInterface
    public void findUserById(final long id, final String callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final User user = new User();
                user.id = id;
                user.name = "test";
                final Gson gson = new GsonBuilder().create();
                final String json = gson.toJson(user);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        final String javascript;
                        if (callback.startsWith("function ")) {
                            javascript = "javascript:(" + callback + ")(null,'" + json + "');";
                        } else {
                            javascript = "javascript:" + callback + "(null, '" + json + "');";
                        }
                        Log.d("debug", "code " + javascript);
                        webView.evaluateJavascript(javascript, null);
                    }
                });
            }
        }).start();
    }

}
