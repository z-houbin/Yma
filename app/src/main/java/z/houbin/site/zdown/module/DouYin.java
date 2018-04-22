//package z.houbin.site.zdown.module;
//
//import android.util.Base64;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.Request;
//import okhttp3.Response;
//import z.houbin.site.zdown.info.BaseInfo;
//
//public class DouYin extends BaseModule{
//
//    public DouYin(String input) {
//        super(input);
//        Headers.Builder headerBuilder = new Headers.Builder();
//        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
//        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
//        headerBuilder.add("Connection", "keep-alive");
//        headerBuilder.add("Host", "www.douyin.com");
//        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
//        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
//        mHeaders = headerBuilder.build();
//    }
//
//    @Override
//    public void doInThread() {
//        super.doInThread();
//        Request request = new Request.Builder().get().url(mInput).headers(mHeaders).build();
//        mClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                onLoadError(e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String html = response.body().string();
//                Document document = Jsoup.parse(html);
//                Elements scriptElements = document.select("script");
//                String json = scriptElements.get(5).data();
//                json = json.substring(json.indexOf("["));
//                json = json.substring(0,json.lastIndexOf("];")+1);
//                mInfo = new BaseInfo() {};
//                try {
//                    JSONArray jsonArray = new JSONArray(json);
//                    JSONObject jsonObject = jsonArray.getJSONObject(0);
//                    mInfo.description = jsonObject.getString("desc");
//                    mInfo.music = jsonObject.getJSONObject("music").getJSONObject("play_url").getJSONArray("url_list").getString(0);
//                    JSONObject videoObj = jsonObject.getJSONObject("video");
//                    String video = jsonObject.getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list").getString(0);
//                    video = video.replaceAll("playwm","play");
//                    mInfo.video = video;
//                    mInfo.vwidth = videoObj.getString("width");
//                    mInfo.vheight = videoObj.getString("height");
//                    mInfo.image.add(videoObj.getJSONObject("cover").getJSONArray("url_list").getString(0));
//                    mInfo.image.add(videoObj.getJSONObject("dynamic_cover").getJSONArray("url_list").getString(0)+".gif");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                onLoadEnd();
//            }
//        });
//    }
//}
