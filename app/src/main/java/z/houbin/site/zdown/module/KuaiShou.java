package z.houbin.site.zdown.module;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.info.BaseInfo;
import z.houbin.site.zdown.util.DownloadManager;

/**
 * https://www.kuaishou.com/photo/1XPaCXnlajP7JAV7yag00FZw/1XYbUaSReeL-uZZeCBgeH6Lw?userId=1XPaCXnlajP7JAV7yag00FZw&photoId=1XYbUaSReeL-uZZeCBgeH6Lw&timestamp=1523376875390&cc=share_copylink&fid=896511413&et=1_i%2F1597376299632201732_h120
 */
public class KuaiShou extends BaseModule {

    public KuaiShou() {
        super();
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "keep-alive");
        headerBuilder.add("Host", "www.kuaishou.com");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        mHeaders = headerBuilder.build();
    }

    @Override
    public void parse(String text) {
        super.parse(text);
        Request request = new Request.Builder().get().url(text).headers(mHeaders).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                Pattern pattern = Pattern.compile("\\{.+\\}");
                Matcher matcher = pattern.matcher(html);
                if (matcher.find()) {
                    mInfo = new BaseInfo() {
                    };
                    String json = matcher.group();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        mInfo.title = jsonObject.getString("title");
                        mInfo.description = jsonObject.getString("description");
                        mInfo.video = jsonObject.getString("mp4Url");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                onLoadEnd();
            }
        });
    }

    @Override
    public void download() {
        super.download();
        if (mInfo != null) {
            DownloadManager.getImpl().startDownload(mInfo.video, ".mp4");
        }
    }
}
