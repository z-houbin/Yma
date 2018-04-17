package z.houbin.site.zdown.module.Music.Kugou;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

public class Kugou extends MusicModule {
    private static final String URL_SEARCH = "http://songsearch.kugou.com/song_search_v2?callback=jQuery191034642999175022426_1489023388639&keyword=%s&page=1&pagesize=30&userid=-1&clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0&_=1489023388641";

    public Kugou(String input) {
        super(input);
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "keep-alive");
        headerBuilder.add("Host", "kugou.com");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        headerBuilder.add("Referer", "http://www.kugou.com");
        mHeaders = headerBuilder.build();
    }

    @Override
    public List<MusicInfo> getMusicInfos() {
        if (musicInfos.isEmpty()) {
            if (data != null && data instanceof SearchBean) {
                SearchBean search = (SearchBean) data;
                System.out.println();
                //musicInfos.addAll(search.getData().getLists());
            }
        }
        return musicInfos;
    }

    @Override
    public String[] getShowList() {
        List<MusicInfo> infos = getMusicInfos();
        return super.getShowList();
    }

    @Override
    public void search() {
        super.search();
        String url = String.format(Locale.CHINA, URL_SEARCH, mInput);
        Request request = new Request.Builder().get().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String html = response.body().string();
                    html = html.substring(html.indexOf("{"),html.length()-2);
                    Gson gson = new GsonBuilder().create();
                    data = gson.fromJson(html, SearchBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onLoadEnd();
            }
        });
    }

    @Override
    public void downloadAll() {
        super.downloadAll();
        List<MusicInfo> musicInfos = getMusicInfos();
    }
}
