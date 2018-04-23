package z.houbin.site.zdown.module.Music;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.module.Music.QuanMingKg.Kg;
import z.houbin.site.zdown.util.DownloadManager;

/**
 * 全民K歌
 * http://kg.qq.com/node/play?s=eUcyS6eviHWU6e6U&g_f=share_html
 */
public class Qmkg extends MusicModule {
    private static final String URL_DETAIL = "http://cgi.kg.qq.com/fcgi-bin/kg_ugc_getdetail?inCharset=GB2312&outCharset=utf-8&v=4&shareid=%s";
    private MusicInfo musicInfo = new MusicInfo();


    public Qmkg() {
        super();
    }

    @Override
    public void parse(String text) {
        super.parse(text);
        final String shareId = text.substring(text.indexOf("=") + 1, text.indexOf("&"));
        String url = String.format(Locale.CHINA, URL_DETAIL, shareId);
        Request request = new Request.Builder().get().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("全面K歌获取失败," + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                try {
                    html = html.substring(html.indexOf("{"), html.lastIndexOf("}") + 1);
                    Gson gson = new GsonBuilder().create();
                    Kg kg = gson.fromJson(html, Kg.class);

                    musicInfo = new MusicInfo();
                    musicInfo.songName = kg.getData().getSong_name();
                    musicInfo.singerName = kg.getData().getSinger_name();

                    String songUrl = getM4aUrl(shareId);
                    System.out.println("歌曲地址:" + songUrl);
                    mInfo = new MusicInfo();
                    mInfo.video = getM4aUrl(shareId);
                    mInfo.author = kg.getData().getNick();
                    mInfo.description = kg.getData().getContent();
                    mInfo.title = kg.getData().getSong_name();
                    onLoadEnd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void download() {
        super.download();
        if (mInfo != null) {
            DownloadManager.getImpl().startDownload(musicInfo, mInfo.video, ".m4a");
        }
    }

    /**
     * 播放地址
     *
     * @param sharedId id,直接url截取
     * @return 文件地址
     */
    private String getM4aUrl(String sharedId) {
        return "http://node.kg.qq.com/cgi/fcgi-bin/fcg_get_play_url?shareid=" + sharedId;
    }
}
