package z.houbin.site.zdown.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.util.DownloadManager;

public class InstagramWebActivity extends Activity implements DownloadManager.DownloadStatusUpdater {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        setTitle("Instagram");
        getActionBar().setSubtitle("快拍下载(需要登录)");

        final WebView web = findViewById(R.id.web);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient() {

        });

        web.setWebViewClient(new WebViewClient() {
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
                    System.out.println("下载:" + url);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    DownloadManager.getImpl().startDownload(url);
                }
                return super.shouldInterceptRequest(view, url);
            }
        });

        web.loadUrl("https://www.instagram.com/gucci_grammm");
        DownloadManager.getImpl().addUpdater(this);
    }

    @Override
    public void blockComplete(BaseDownloadTask task) {
        System.out.println("InstagramWebActivity.blockComplete");
    }

    @Override
    public void complete(BaseDownloadTask task) {
        System.out.println("InstagramWebActivity.complete");
    }

    @Override
    public void update(BaseDownloadTask task) {
        System.out.println("InstagramWebActivity.update");
    }
}
