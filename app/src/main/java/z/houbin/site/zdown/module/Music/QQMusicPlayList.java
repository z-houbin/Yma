package z.houbin.site.zdown.module.Music;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import z.houbin.site.zdown.info.QQMusicPlayListInfo;
import z.houbin.site.zdown.util.DownloadManager;

//https://y.qq.com/n/yqq/playsquare/1760653792.html#stat=y_new.index.playlist.name
public class QQMusicPlayList extends MusicModule {
    private static final String URL_INFO = "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid=%s&format=json&g_tk=5381&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";

    public QQMusicPlayList(String input) {
        super(input);
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "close");
        headerBuilder.add("Host", "y.qq.com");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        headerBuilder.add("Referer", "https://y.qq.com/portal/playlist.html");
        mHeaders = headerBuilder.build();
    }

    @Override
    public Object getSongInfo(int index) {
        MusicInfo info = musicInfos.get(index);
        String url = "http://base.music.qq.com/fcgi-bin/fcg_musicexpress.fcg?json=3&inCharset=utf8&outCharset=utf-8&format=json&guid=" + info.docid;
        Request request = new Request.Builder().get().url(url).build();
        try {
            Response response = mClient.newCall(request).execute();
            String json = response.body().string();
            JSONObject jsonObject = new JSONObject(json);
            String key = jsonObject.getString("key");
            if (info.sizeflac != 0) {
                return String.format(Locale.CHINA, "http://ws.stream.qqmusic.qq.com/F000%s.flac?vkey=%s&guid=%s&fromtag=53", info.songMid, key, info.docid);
            } else if (info.sizeApe != 0) {
                return String.format(Locale.CHINA, "http://ws.stream.qqmusic.qq.com/A000%s.ape?vkey=%s&guid=%s&fromtag=53", info.songMid, key, info.docid);
            } else if (info.size320 != 0) {
                return String.format(Locale.CHINA, "http://ws.stream.qqmusic.qq.com/M800%s.mp3?vkey=%s&guid=%s&fromtag=53", info.songMid, key, info.docid);
            } else if (info.size128 != 0) {
                return String.format(Locale.CHINA, "http://ws.stream.qqmusic.qq.com/M500%s.mp3?vkey=%s&guid=%s&fromtag=53", info.songMid, key, info.docid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void doInThread() {
        super.doInThread();
        String s = mInput.split(".html")[0];
        String id = s.substring(s.lastIndexOf("/") + 1);
        String url = String.format(Locale.CHINA, URL_INFO, id);
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
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        Gson gson = new GsonBuilder().create();
                        data = gson.fromJson(html, QQMusicPlayListInfo.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onLoadEnd();
            }
        });
    }

    private List<QQMusicPlayListInfo.CdlistBean.SonglistBean> getSongList() {
        if (data != null && data instanceof QQMusicPlayListInfo) {
            QQMusicPlayListInfo info = (QQMusicPlayListInfo) data;
            QQMusicPlayListInfo.CdlistBean cdlistBean = info.getCdlist().get(0);
            return cdlistBean.getSonglist();
        } else {
            return null;
        }
    }

    @Override
    public String[] getShowList() {
        String[] items = new String[]{};
        List<QQMusicPlayListInfo.CdlistBean.SonglistBean> songList = getSongList();
        if (songList != null) {
            items = new String[songList.size()];
            for (int i = 0; i < songList.size(); i++) {
                items[i] = songList.get(i).getSongname();
            }
        }
        return items;
    }

    @Override
    public void initMusicInfos() {
        if (musicInfos.isEmpty()) {
            List<QQMusicPlayListInfo.CdlistBean.SonglistBean> songList = getSongList();
            for (QQMusicPlayListInfo.CdlistBean.SonglistBean song : songList) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.albumid = song.getAlbumid() + "";
                musicInfo.albummid = song.getAlbummid();
                musicInfo.albumname = song.getAlbumname();
                musicInfo.singerId = song.getSinger().get(0).getId() + "";
                musicInfo.singerMid = song.getSinger().get(0).getMid();
                musicInfo.singerName = song.getSinger().get(0).getName();
                musicInfo.size128 = song.getSize128();
                musicInfo.size320 = song.getSize320();
                musicInfo.sizeApe = song.getSizeape();
                musicInfo.sizeflac = song.getSizeflac();
                musicInfo.sizeogg = song.getSizeogg();
                musicInfo.songId = song.getSongid() + "";
                musicInfo.songMid = song.getSongmid();
                musicInfo.songName = song.getSongname();
                musicInfo.docid = song.getSongid() + "";
                musicInfos.add(musicInfo);
            }
        }
    }

    @Override
    public void downloadAll() {
        initMusicInfos();
        super.downloadAll();
    }

    @Override
    public void download(final int... index) {
        super.download(index);
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i : index) {
                    MusicInfo info = (MusicInfo) musicInfos.get(i);
                    String songUrl = getSongInfo(i).toString();
                    if (songUrl.contains("ape")) {
                        DownloadManager.getImpl().startDownload(info, songUrl, ".ape");
                    } else if (songUrl.contains("flac")) {
                        DownloadManager.getImpl().startDownload(info, songUrl, ".flace");
                    } else {
                        DownloadManager.getImpl().startDownload(info, songUrl, ".mp3");
                    }
                }
            }
        }.start();
    }
}
