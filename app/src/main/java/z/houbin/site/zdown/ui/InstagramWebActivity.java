package z.houbin.site.zdown.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.R;
import z.houbin.site.zdown.util.DownloadManager;

public class InstagramWebActivity extends Activity implements DownloadManager.DownloadStatusUpdater {
    private Handler handler = new Handler();
    private String url;
    private String name;
    private String queryHash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        setTitle("Instagram");
        getActionBar().setSubtitle("快拍下载(需要登录)");

        url = getIntent().getStringExtra("data");
        name = url.substring(26, url.length() - 1);
        final WebView web = findViewById(R.id.web);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient() {

        });

        web.setWebViewClient(new WebViewClient() {
            private boolean loadFinish;
            private boolean findMp4;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadFinish = false;
                findMp4 = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadFinish = true;
                view.loadUrl("javascript:window.local_obj.showCookie(document.cookie);");
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                System.out.println(url);
                if (url.endsWith(".mp4")) {
                    findMp4 = true;
                    startDownload(url);
                    //Intent intent = new Intent();
                    //intent.setAction(Intent.ACTION_VIEW);
                    //intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    //intent.setData(Uri.parse(url));
                    //startActivity(intent);
                }
                if (url.contains("query_hash")) {
                    queryHash = url;
                }
                return super.shouldInterceptRequest(view, url);
            }
        });

        web.loadUrl(getStories());
        web.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        DownloadManager.getImpl().addUpdater(this);
    }


    private class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showCookie(String cookie) {
            if (queryHash != null) {
                OkHttpClient client = new OkHttpClient();
                Headers.Builder builder = new Headers.Builder();
                builder.add("Cookie",cookie);
                builder.add("Accept-Encoding","gzip, deflate, br");
                builder.add("Accept-Language","zh-HK,zh-CN;q=0.9,zh;q=0.8,ja-JP;q=0.7,ja;q=0.6,zh-TW;q=0.5,en-US;q=0.4,en;q=0.3");
                builder.add("Host","www.instagram.com");
                builder.add("Connection","keep-alive");
                builder.add("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                builder.add("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Mobile Safari/537.36");
                Request request = new Request.Builder().url(queryHash).get().build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String html = response.body().string();
                        List<String> picList = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(html);
                            JSONArray reels = jsonObject.getJSONObject("data").getJSONArray("reels_media");
                            JSONArray items = reels.getJSONObject(0).getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONArray videos = items.getJSONObject(i).getJSONArray("video_resources");
                                for (int j = 0; j < videos.length(); j++) {
                                    picList.add(videos.getJSONObject(j).getString("src"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("@@@@@@@@@@@@@@@@@@@" + picList.size());
                    }
                });
            }
        }

        @JavascriptInterface
        public void showSource(String html) {
            System.out.println(html);
            //图片查找
            Document document = Jsoup.parse(html);
            Elements scriptElements = document.select("script");
            List<String> picList = new ArrayList<>();
            for (Element element : scriptElements) {
                if (element.data().contains("window._sharedData")) {
                    String data = element.data();
                    try {
                        String json = data.substring(data.indexOf("{"), data.length() - 1);
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray pages = jsonObject.getJSONObject("entry_data").getJSONArray("StoriesPage");
                        for (int i = 0; i < pages.length(); i++) {
                            try {
                                picList.add(pages.getJSONObject(i).getJSONObject("user").getString("profile_pic_url"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    } catch (Exception e) {
                        //单图
                        e.printStackTrace();
                    }
                }
            }
            if (picList.size() == 0) {
                Toast.makeText(getApplicationContext(), "没有快拍", Toast.LENGTH_SHORT).show();
            }
            System.out.println(picList.toArray(new String[]{}));
        }
    }

    private void startDownload(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int res = DownloadManager.getImpl().startDownload(url, ".mp4");
                if (res == -2) {
                    Toast.makeText(InstagramWebActivity.this, "文件已经存在", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InstagramWebActivity.this, "开始下载,下载时长取决于网速", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private String getStories() {
        return String.format(Locale.CHINA, "https://www.instagram.com/stories/%s/", name);
    }

    @Override
    public void blockComplete(BaseDownloadTask task) {
        System.out.println("InstagramWebActivity.blockComplete");
    }

    @Override
    public void complete(BaseDownloadTask task) {
        System.out.println("InstagramWebActivity.complete");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "下载完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void update(BaseDownloadTask task) {
        System.out.println("InstagramWebActivity.update");
    }
}
