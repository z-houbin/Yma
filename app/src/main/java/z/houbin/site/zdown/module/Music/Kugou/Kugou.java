package z.houbin.site.zdown.module.Music.Kugou;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import z.houbin.site.zdown.util.HTMLSpirit;
import z.houbin.site.zdown.util.MD5;

public class Kugou extends MusicModule {
    //搜索
    private static final String URL_SEARCH = "http://songsearch.kugou.com/song_search_v2?callback=jQuery191034642999175022426_1489023388639&keyword=%s&page=1&pagesize=30&userid=-1&clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0&_=1489023388641";
    //歌曲详情
    private static final String URL_DATA = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=%s";
    //歌曲地址sqhash,md5(kg.sqhash + "kgcloud") 32位
    private static final String URL_SONG = "http://trackercdn.kugou.com/i/?cmd=4&hash=%s&key=%s&pid=1&forceDown=0&vip=1";

    protected List<SearchBean.DataBean.ListsBean> musicInfos = new ArrayList<SearchBean.DataBean.ListsBean>();

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
    public void initMusicInfos() {
        if (musicInfos.isEmpty()) {
            if (data != null && data instanceof SearchBean) {
                SearchBean search = (SearchBean) data;
                musicInfos.addAll(search.getData().getLists());
            }
        }
    }

    @Override
    public String[] getShowList() {
        String[] items = new String[musicInfos.size()];
        for (int i = 0; i < musicInfos.size(); i++) {
            items[i] = HTMLSpirit.delHTMLTag(musicInfos.get(i).getFileName());
        }
        return items;
    }

    @Override
    public Object getSongInfo(int index) {
        SearchBean.DataBean.ListsBean bean = musicInfos.get(index);
        String hash = bean.getFileHash();
        if (bean.getSQFileSize() != 0) {
            hash = bean.getSQFileHash();
        } else if (bean.getHQFileSize() != 0) {
            hash = bean.getHQFileHash();
        }
        String url = String.format(Locale.CHINA, URL_DATA, hash);
        Request request = new Request.Builder().get().url(url).build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String html = null;
        try {
            html = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().create();
        KugouSong song = gson.fromJson(html, KugouSong.class);
        if (TextUtils.isEmpty(song.getData().getPlay_url())) {
            //获取地址
            String realUrl = getSongUrl(hash);
            if (!realUrl.contains("error")) {
                song.getData().setPlay_url(realUrl);
            }
        }
        return song;
    }

    private String getSongUrl(String hash) {
        String key = MD5.MD5_32bit(hash + "kgcloud");
        String url = String.format(Locale.CHINA, URL_SONG, hash, key);
        Request request = new Request.Builder().get().url(url).build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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
                    html = html.substring(html.indexOf("{"), html.length() - 2);
                    Gson gson = new GsonBuilder().create();
                    data = gson.fromJson(html, SearchBean.class);
                    initMusicInfos();
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
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < musicInfos.size(); i++) {
                    SearchBean.DataBean.ListsBean musicInfo = musicInfos.get(i);
                    KugouSong song = (KugouSong) getSongInfo(i);
                    MusicInfo info = new MusicInfo();
                    info.songName = musicInfo.getSongName();
                    info.singerName = musicInfo.getSingerName();
                    String songUrl = song.getData().getPlay_url();
                    System.out.println("歌曲地址:" + songUrl);
                    if (!TextUtils.isEmpty(songUrl)) {
                        DownloadManager.getImpl().startDownload(info, song.getData().getPlay_url(), songUrl.substring(songUrl.lastIndexOf(".")));
                    }
                }
            }
        }.start();
    }

    @Override
    public void download(final int... index) {
        super.download(index);
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i : index) {
                    SearchBean.DataBean.ListsBean bean = musicInfos.get(i);
                    KugouSong song = (KugouSong) getSongInfo(i);
                    MusicInfo info = new MusicInfo();
                    info.songName = bean.getSongName();
                    info.singerName = bean.getSingerName();
                    String songUrl = song.getData().getPlay_url();
                    System.out.println("歌曲地址:" + songUrl);
                    if (!TextUtils.isEmpty(songUrl)) {
                        DownloadManager.getImpl().startDownload(info, song.getData().getPlay_url(), songUrl.substring(songUrl.lastIndexOf(".")));
                    }
                }
            }
        }.start();
    }
}
