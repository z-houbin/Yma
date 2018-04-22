//package z.houbin.site.zdown.module.Music;
//
//import android.text.TextUtils;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonSyntaxException;
//
//import java.io.IOException;
//import java.util.Locale;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Request;
//import okhttp3.Response;
//import z.houbin.site.zdown.info.MusicInfo;
//import z.houbin.site.zdown.module.Music.QuanMingKg.Kg;
//import z.houbin.site.zdown.util.DownloadManager;
//
///**
// * 全民K歌
// * http://kg.qq.com/node/play?s=eUcyS6eviHWU6e6U&g_f=share_html
// */
//public class QMKg extends MusicModule {
//    private static final String URL_DETAIL = "http://cgi.kg.qq.com/fcgi-bin/kg_ugc_getdetail?inCharset=GB2312&outCharset=utf-8&v=4&shareid=%s";
//
//    public QMKg(String input) {
//        super(input);
//    }
//
//    @Override
//    public void downloadAll() {
//        super.downloadAll();
//        final String shareid = mInput.substring(mInput.indexOf("=")+1, mInput.indexOf("&"));
//        String url = String.format(Locale.CHINA, URL_DETAIL, shareid);
//        Request request = new Request.Builder().get().url(url).build();
//        mClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                System.out.println("全面K歌获取失败," + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String html = response.body().string();
//                try {
//                    html = html.substring(html.indexOf("{"),html.lastIndexOf("}")+1);
//                    Gson gson = new GsonBuilder().create();
//                    Kg kg = gson.fromJson(html, Kg.class);
//
//                    MusicInfo info = new MusicInfo();
//                    info.songName = kg.getData().getSong_name();
//                    info.singerName = kg.getData().getSinger_name();
//                    String songUrl = getM4aUrl(shareid);
//                    System.out.println("歌曲地址:" + songUrl);
//                    if (!TextUtils.isEmpty(songUrl)) {
//                        DownloadManager.getImpl().startDownload(info, songUrl, ".m4a");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    /**
//     * 播放地址
//     * @param sharedId id,直接url截取
//     * @return 文件地址
//     */
//    private String getM4aUrl(String sharedId) {
//        return "http://node.kg.qq.com/cgi/fcgi-bin/fcg_get_play_url?shareid=" + sharedId;
//    }
//
//    /**
//     * 下载主页所有
//     */
//    public void downloadHome() {
//
//    }
//}
