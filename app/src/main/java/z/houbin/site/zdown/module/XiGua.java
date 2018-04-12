package z.houbin.site.zdown.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.info.BaseInfo;

/**
 * https://m.365yg.com/group/6542348798960599555/?iid=28814392123&app=video_article&timestamp=1523377589&utm_source=copy_link&utm_medium=android&utm_campaign=client_share
 */
public class XiGua extends BaseModule{

    public XiGua(String input) {
        super(input);
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "keep-alive");
        headerBuilder.add("Host", "m.365yg.com");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Mobile Safari/537.36");
        mHeaders = headerBuilder.build();
    }

    @Override
    public void doInThread() {
        super.doInThread();
        String id = mInput.substring(26,45);
        String url = String.format(Locale.CHINA,"https://m.365yg.com/i%s/info/?i=%s",id,id);
        Request request = new Request.Builder().get().url(url).headers(mHeaders).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadError(e);
            }

            public long right_shift(long val,int n){
                //return val >> n if( val >= 0) {}else (val + 0x100000000L) >> n;
                return val;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                mInfo = new BaseInfo() {};
                try {
                    JSONObject jsonObject = new JSONObject(html);
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    mInfo.title = dataObj.getString("title");
                    mInfo.url = dataObj.getString("url");
                    String content = dataObj.getString("content");
                    String vid = content.substring(41,73);
                    StringBuilder builder = new StringBuilder();
                    builder.append("http://i.snssdk.com/video/urls/v/1/toutiao/mp4/");
                    builder.append(vid);
                    String url = builder.toString();
                    CRC32 crc32 = new CRC32();
                    crc32.update((url+"?r=82").getBytes());
                    long i = crc32.getValue();
                    url = url + String.format(Locale.CHINA,"?r=%s&s=%s","82",i+"");
                    System.out.println();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Pattern pattern = Pattern.compile("\\{.+\\}");
                Matcher matcher = pattern.matcher(html);
                if(matcher.find()){
                    mInfo = new BaseInfo() {};
                    String json = matcher.group();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
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
}
