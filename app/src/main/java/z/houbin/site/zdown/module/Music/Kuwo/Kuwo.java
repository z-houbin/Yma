package z.houbin.site.zdown.module.Music.Kuwo;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.module.Music.MusicModule;
import z.houbin.site.zdown.util.DownloadManager;

/**
 * http://www.kuwo.cn/yinyue/6657692
 * <p>
 * 接口:
 * 搜索:
 * http://search.kuwo.cn/r.s?all=%E5%91%A8%E6%9D%B0%E4%BC%A6&ft=music&itemset=web_2015&client=kt&pn=0&rn=50&rformat=json&encoding=utf8
 * 地址:
 * http://antiserver.kuwo.cn/anti.s?response=url&rid=MUSIC_6657692&format=mp3&type=convert_url
 * http://antiserver.kuwo.cn/anti.s?response=url&rid=APE_41187764&format=ALFLAC&type=convert_url2
 */
public class Kuwo extends MusicModule {
    private static final String URL_SEARCH = "http://search.kuwo.cn/r.s?all=%s&ft=music&itemset=web_2015&client=kt&pn=0&rn=50&rformat=json&encoding=utf8";

    public Kuwo() {
        super();
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "keep-alive");
        headerBuilder.add("Host", "search.kuwo.cn");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        headerBuilder.add("Referer", "http://search.kuwo.cn");
        mHeaders = headerBuilder.build();
        String quality = "1000";
        String format = "ape";
        String id = "41187765";
        String text = "type=convert_url2&br=" + quality + "&format=" + (format == "ape" ? "ape" : "mp3") + "&sig=0&rid=" + id + "&network=wifi";
        try {
            String q = new String(Base64.encode(KwDes.EncryptToBytes(text, "ylzsxkwm"), Base64.DEFAULT));
            String link = "http://nmobi.kuwo.cn/mobi.s?f=kuwo&q=" + q;
            System.out.println(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void search(String input) {
        super.search(input);
        musicInfos.clear();
        String url = String.format(Locale.CHINA, URL_SEARCH, input);
        Request request = new Request.Builder().get().url(url).headers(mHeaders).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                try {
                    Gson gson = new Gson();
                    KuwoSearchBean searchBean = gson.fromJson(html, KuwoSearchBean.class);
                    data = searchBean;
                    for (KuwoSearchBean.AbslistBean bean : searchBean.getAbslist()) {
                        MusicInfo info = new MusicInfo();
                        try {
                            info.albumid = bean.getALBUMID();
                            info.albumname = bean.getALBUM();
                            info.pubtime = bean.getRELEASEDATE();
                            info.songId = bean.getMUSICRID();
                            info.songName = bean.getNAME();

                            info.size320 = Long.parseLong(bean.getMP3SIZE());
                            info.sizeApe = Long.parseLong(bean.getAPESIZE());
                            info.singerName = bean.getARTIST();
                            info.singerId = bean.getARTISTID();

                            musicInfos.add(info);
                        } catch (Exception e) {
                            onLoadError(e);
                        }
                    }
                    title = "搜索结果";
                    onLoadEnd();
                } catch (Exception e) {
                    onLoadError(e);
                }
            }
        });
    }

    @Override
    public String[] getShowList() {
        String[] items = new String[0];
        if (data != null) {
            KuwoSearchBean searchBean = (KuwoSearchBean) data;
            List<KuwoSearchBean.AbslistBean> abslist = searchBean.getAbslist();
            items = new String[abslist.size()];
            for (int i = 0; i < abslist.size(); i++) {
                KuwoSearchBean.AbslistBean bean = abslist.get(i);
                items[i] = bean.getNAME() + "/" + bean.getARTIST();
            }
        }
        return items;
    }

    private String getSongUrl(MusicInfo info) {
        String url = "http://antiserver.kuwo.cn/anti.s?format=mp3&rid=%s&response=url&type=convert_url2";
        url = String.format(Locale.CHINA, url, info.songId);
        Request request = new Request.Builder().get().url(url).build();
        try {
            Response response = mClient.newCall(request).execute();
            String html = response.body().string();
            JSONObject jsonObject = new JSONObject(html);
            url = jsonObject.getString("mp3base") + jsonObject.getString("mp3url");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void download(final int... index) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i : index) {
                    MusicInfo info = musicInfos.get(i);
                    String url = getSongUrl(info);
                    DownloadManager.getImpl().startDownload(info, url, ".mp3");
                }
            }
        }.start();
    }

    @Override
    public void downloadAll() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (MusicInfo info : musicInfos) {
                    String url = getSongUrl(info);
                    DownloadManager.getImpl().startDownload(info, url, ".mp3");
                }
            }
        }.start();
    }
}
