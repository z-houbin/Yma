package z.houbin.site.zdown.module;

import android.util.Base64;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.info.BaseInfo;

/**
 * www.miaopai.com/show/ZJPpAOppvT3snYn0JS1oNw__.htm
 */
public class MiaoPai extends BaseModule {

    public MiaoPai(String input) {
        super(input);

        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "keep-alive");
        headerBuilder.add("Host", "www.miaopai.com");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        mHeaders = headerBuilder.build();
    }

    @Override
    public void doInThread() {
        super.doInThread();
        String id = mInput.substring(mInput.lastIndexOf("/")+1).replaceAll(".htm","");
        final String url = String.format(Locale.CHINA,"http://gslb.miaopai.com/stream/%s.json?token=",id);
        Request request = new Request.Builder().get().url(url).headers(mHeaders).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String html = response.body().string();
                    JSONObject jsonObject = new JSONObject(html);
                    JSONObject result = jsonObject.getJSONArray("result").getJSONObject(0);
                    StringBuilder builder = new StringBuilder();
                    builder.append(result.getString("scheme"));
                    builder.append(result.getString("host"));
                    builder.append(result.getString("path"));
                    mInfo = new BaseInfo() {};
                    mInfo.video = builder.toString();
                    mInfo.site = "www.miaopai.com";
                    mInfo.url = url;

                    onLoadEnd();
                }catch (Exception e){
                    onLoadError(e);
                }
            }
        });
    }

    private int[] string2Array(String str) {
        int[] arr = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            arr[i] = Integer.parseInt(str.charAt(i) + "");
        }
        return arr;
    }
}
