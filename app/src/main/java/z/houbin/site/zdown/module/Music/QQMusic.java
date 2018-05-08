package z.houbin.site.zdown.module.Music;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.info.QQMusicPlayListInfo;
import z.houbin.site.zdown.util.DownloadManager;
import z.houbin.site.zdown.util.DownloadUtil;

public class QQMusic extends MusicModule {
    private static final String URL_SEARCH = "https://c.y.qq.com/soso/fcgi-bin/search_for_qq_cp?g_tk=5381&uin=0&format=jsonp&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&w=%s&zhidaqu=1&catZhida=1&t=0&flag=1&ie=utf-8&sem=1&aggr=0&perpage=20&n=30&p=1&remoteplace=txt.mqq.all&_=1520833663464";

    public QQMusic() {
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "keep-alive");
        headerBuilder.add("Host", "y.qq.com");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        mHeaders = headerBuilder.build();
    }

    @Override
    public Object getSongInfo(int index) {
        MusicInfo info = musicInfos.get(index);
        String url = "http://base.music.qq.com/fcgi-bin/fcg_musicexpress.fcg?json=3&guid=" + info.docid;
        Request request = new Request.Builder().get().url(url).build();
        try {
            Response response = mClient.newCall(request).execute();
            String json = response.body().string();
            json = DownloadUtil.trimJson(json);
            JSONObject jsonObject = new JSONObject(json);
            String key = jsonObject.getString("key");

            String _flac = String.format(Locale.CHINA, "http://ws.stream.qqmusic.qq.com/F000%s.flac?vkey=%s&guid=%s&fromtag=53", info.songMid, key, info.docid);
            String _ape = String.format(Locale.CHINA, "http://ws.stream.qqmusic.qq.com/A000%s.ape?vkey=%s&guid=%s&fromtag=53", info.songMid, key, info.docid);
            String _128 = String.format(Locale.CHINA, "http://ws.stream.qqmusic.qq.com/M500%s.mp3?vkey=%s&guid=%s&fromtag=53", info.songMid, key, info.docid);
            String _320 = String.format(Locale.CHINA, "http://ws.stream.qqmusic.qq.com/M800%s.mp3?vkey=%s&guid=%s&fromtag=53", info.songMid, key, info.docid);
            HashMap<String, String> map = new HashMap<>();
            map.put("128", _128);
            map.put("320", _320);
            map.put("ape", _ape);
            map.put("flac", _flac);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getSongInfo(index);
    }

    public void parseSong(final String mInput) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String mid = "";
                    String[] items = mInput.split("&");
                    for (String item : items) {
                        if (item.startsWith("songmid")) {
                            mid = item.split("=")[1];
                        }
                    }
                    String url = String.format(Locale.CHINA, "https://c.y.qq.com/v8/fcg-bin/fcg_play_single_song.fcg?songmid=%s&tpl=yqq_song_detail&format=jsonp&callback=getOneSongInfoCallback&g_tk=5381&jsonpCallback=getOneSongInfoCallback&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", mid);
                    Request request = new Request.Builder().get().url(url).headers(mHeaders).build();
                    Response response = mClient.newCall(request).execute();
                    String html = response.body().string();
                    html = html.substring(html.indexOf("{"), html.length() - 1);
                    JSONObject jsonObject = new JSONObject(html);
                    JSONObject dataObject = jsonObject.getJSONArray("data").getJSONObject(0);
                    MusicInfo info = new MusicInfo();
                    try {
                        JSONObject albumObject = dataObject.getJSONObject("album");
                        info.albumid = albumObject.getString("id");
                        info.albummid = albumObject.getString("mid");
                        info.albumname = albumObject.getString("name");
                        info.docid = dataObject.getString("id");
                        info.pubtime = dataObject.getString("time_public");
                        info.songId = dataObject.getString("id");
                        info.songMid = dataObject.getString("mid");
                        info.songName = dataObject.getString("name");

                        JSONObject fileObject = dataObject.getJSONObject("file");
                        info.size128 = fileObject.getLong("size_128mp3");
                        info.size320 = fileObject.getLong("size_320mp3");
                        info.sizeApe = fileObject.getLong("size_ape");
                        info.sizeflac = fileObject.getLong("size_flac");
                        info.sizeogg = fileObject.getLong("size_192ogg");

                        JSONArray singerArray = dataObject.getJSONArray("singer");
                        String singerName = "";
                        for (int i = 0; i < singerArray.length(); i++) {
                            JSONObject singerObject = singerArray.getJSONObject(i);
                            singerName += singerObject.getString("name");
                            if (i != singerArray.length() - 1) {
                                singerName += "&";
                            }
                        }
                        info.singerName = singerName;

                        title = "单曲";
                    } catch (JSONException e) {
                        System.err.println("QQ音乐解析信息错误:" + e.getMessage());
                    }
                    musicInfos.add(info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onLoadEnd();
            }
        }.start();
    }

    @Override
    public void search(String key) {
        super.search(key);
        musicInfos.clear();

        String url = String.format(Locale.CHINA, URL_SEARCH, key);
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
                    html = DownloadUtil.trimJson(html);
                    JSONObject jsonObject = new JSONObject(html);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        JSONObject songObj = dataObj.getJSONObject("song");
                        JSONArray listArr = songObj.getJSONArray("list");
                        for (int i = 0; i < listArr.length(); i++) {
                            JSONObject obj = listArr.getJSONObject(i);
                            MusicInfo info = new MusicInfo();
                            try {
                                info.albumid = obj.getString("albumid");
                                info.albummid = obj.getString("albummid");
                                info.albumname = obj.getString("albumname");
                                info.chinesesinger = obj.getInt("chinesesinger");
                                info.docid = obj.getString("docid");
                                info.pubtime = obj.getString("pubtime");
                                info.songId = obj.getString("songid");
                                info.songMid = obj.getString("songmid");
                                info.songName = obj.getString("songname");
                                info.stream = obj.getInt("stream");

                                info.size128 = obj.getLong("size128");
                                info.size320 = obj.getLong("size320");
                                info.sizeApe = obj.getLong("sizeape");
                                info.sizeflac = obj.getLong("sizeflac");
                                info.sizeogg = obj.getLong("sizeogg");
                            } catch (JSONException e) {
                                System.err.println("QQ音乐解析信息错误:" + e.getMessage());
                            }

                            try {
                                JSONObject singer = obj.getJSONArray("singer").getJSONObject(0);
                                info.singerId = singer.getString("id");
                                info.singerMid = singer.getString("mid");
                                info.singerName = singer.getString("name");
                            } catch (Exception e) {
                                System.err.println("QQ音乐解析歌手错误:" + e.getMessage());
                            }

                            musicInfos.add(info);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("list " + musicInfos.size());
                title = "搜索结果";
                onLoadEnd();
            }
        });
    }

    /**
     * 解析专辑
     * //https://y.qq.com/n/yqq/album/001BQNXZ2QkzMh.html#stat=y_new.album_lib.album_name
     * //https://y.qq.com/n/yqq/album/004AhJHV3slLjN.html
     *
     * @param url 地址
     */
    public void parseAlbum(final String url) {
        musicInfos.clear();
        new Thread() {
            @Override
            public void run() {
                super.run();
                final String URL_INFO = "http://c.y.qq.com/v8/fcg-bin/fcg_v8_album_info_cp.fcg?inCharset=utf8&outCharset=utf-8&albummid=%s";
                String mInput = url;
                String albummid = "";
                try {
                    Document document = Jsoup.connect(mInput).get();
                    mInput = document.location();
                    URL url = new URL(mInput);
                    String[] querys = url.getQuery().split("&");
                    for (String s : querys) {
                        if (s.startsWith("albumId")) {
                            albummid = s.split("=")[1];
                            //https://y.qq.com/n/yqq/album/4018548_num.html
                            mInput = String.format(Locale.CHINA, "https://y.qq.com/n/yqq/album/%s_num.html", albummid);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //手机QQ音乐专辑分享
                try {
                    Request request = new Request.Builder().get().url(mInput).headers(mHeaders).build();
                    Response response = mClient.newCall(request).execute();
                    Document document = Jsoup.parse(response.body().string());
                    Elements elements = document.select("link");
                    mInput = elements.get(0).attr("href");
                    String s = mInput.split(".html")[0];
                    albummid = s.substring(s.lastIndexOf("/") + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String url = String.format(Locale.CHINA, URL_INFO, albummid);
                Request request = new Request.Builder().get().url(url).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    String html = response.body().string();
                    JSONObject jsonObject = new JSONObject(html);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        JSONArray listArr = dataObj.getJSONArray("list");
                        for (int i = 0; i < listArr.length(); i++) {
                            JSONObject obj = listArr.getJSONObject(i);
                            MusicInfo info = new MusicInfo();
                            try {
                                info.albumid = obj.getString("albumid");
                                info.albummid = obj.getString("albummid");
                                info.albumname = obj.getString("albumname");
                                Random random = new Random(1000000000);
                                info.docid = "" + (random.nextInt() + 1000000000);
                                info.songId = obj.getString("songid");
                                info.songMid = obj.getString("songmid");
                                info.songName = obj.getString("songname");
                                info.stream = obj.getInt("stream");

                                info.size128 = obj.getLong("size128");
                                info.size320 = obj.getLong("size320");
                                info.sizeApe = obj.getLong("sizeape");
                                info.sizeflac = obj.getLong("sizeflac");
                                info.sizeogg = obj.getLong("sizeogg");
                            } catch (JSONException e) {
                                System.err.println("QQ音乐解析信息错误:" + e.getMessage());
                            }

                            try {
                                JSONObject singer = obj.getJSONArray("singer").getJSONObject(0);
                                info.singerId = singer.getString("id");
                                info.singerMid = singer.getString("mid");
                                info.singerName = singer.getString("name");
                            } catch (Exception e) {
                                System.err.println("QQ音乐解析歌手错误:" + e.getMessage());
                            }

                            musicInfos.add(info);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onLoadError(e);
                    return;
                }
                title = "专辑";
                onLoadEnd();
            }
        }.start();
    }

    /**
     * 解析歌单
     * https://y.qq.com/n/yqq/playsquare/1760653792.html#stat=y_new.index.playlist.name
     * http://y.qq.com/w/taoge.html?hostuin=1306818598&id=1895868982&appshare=android_qq
     *
     * @param mInput 歌单
     */
    public void parsePlayList(final String mInput) {
        musicInfos.clear();
        String id = "";
        if (mInput.startsWith("http://y.qq.com/w/taoge.html")) {
            URL url = null;
            try {
                url = new URL(mInput);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String query = url.getQuery();
            String[] kvs = query.split("&");
            for (String kv : kvs) {
                if (kv.startsWith("id=")) {
                    id = kv.split("=")[1];
                    break;
                }
            }
        } else {
            String s = mInput.split(".html")[0];
            id = s.substring(s.lastIndexOf("/") + 1);
        }
        final String URL_INFO = "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid=%s&format=json&g_tk=5381&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
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
                    parseHtmlPlayList(html);
                } catch (Exception e) {
                    e.printStackTrace();
                    onLoadError(e);
                    return;
                }
                onLoadEnd();
            }
        });
    }

    private void parseHtmlPlayList(String html) throws JSONException {
        JSONObject jsonObject = new JSONObject(html);
        int code = jsonObject.getInt("code");
        if (code == 0) {
            Gson gson = new GsonBuilder().create();
            QQMusicPlayListInfo playListInfo = gson.fromJson(html, QQMusicPlayListInfo.class);
            QQMusicPlayListInfo.CdlistBean cdlistBean = playListInfo.getCdlist().get(0);
            List<QQMusicPlayListInfo.CdlistBean.SonglistBean> songList = cdlistBean.getSonglist();
            title = String.format(Locale.CHINA, "%s (%d)", cdlistBean.getDissname(), songList.size());
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
    public void download(final int... index) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i : index) {
                    MusicInfo info = musicInfos.get(i);
                    HashMap<String, String> musicUrl = (HashMap<String, String>) getSongInfo(i);
                    if (info.sizeflac != 0 && musicUrl.containsKey("flac") && !TextUtils.isEmpty(musicUrl.get("flac"))) {
                        DownloadManager.getImpl().startDownload(info, musicUrl.get("flac"), ".flac");
                    } else if (info.sizeApe != 0 && musicUrl.containsKey("ape") && !TextUtils.isEmpty(musicUrl.get("ape"))) {
                        DownloadManager.getImpl().startDownload(info, musicUrl.get("ape"), ".ape");
                    } else if (info.size320 != 0 && musicUrl.containsKey("320") && !TextUtils.isEmpty(musicUrl.get("320"))) {
                        DownloadManager.getImpl().startDownload(info, musicUrl.get("320"), ".mp3");
                    } else if (info.size128 != 0 && musicUrl.containsKey("128") && !TextUtils.isEmpty(musicUrl.get("128"))) {
                        DownloadManager.getImpl().startDownload(info, musicUrl.get("128"), ".mp3");
                    }
                }
            }
        }.start();
    }

    @Override
    public void downloadAll() {
        super.downloadAll();
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < musicInfos.size(); i++) {
                    MusicInfo info = musicInfos.get(i);
                    HashMap<String, String> musicUrl = (HashMap<String, String>) getSongInfo(i);
                    if (info.sizeflac != 0 && musicUrl.containsKey("flac") && !TextUtils.isEmpty(musicUrl.get("flac"))) {
                        DownloadManager.getImpl().startDownload(info, musicUrl.get("flac"), ".flac");
                    } else if (info.sizeApe != 0 && musicUrl.containsKey("ape") && !TextUtils.isEmpty(musicUrl.get("ape"))) {
                        DownloadManager.getImpl().startDownload(info, musicUrl.get("ape"), ".ape");
                    } else if (info.size320 != 0 && musicUrl.containsKey("320") && !TextUtils.isEmpty(musicUrl.get("320"))) {
                        DownloadManager.getImpl().startDownload(info, musicUrl.get("320"), ".mp3");
                    } else if (info.size128 != 0 && musicUrl.containsKey("128") && !TextUtils.isEmpty(musicUrl.get("128"))) {
                        DownloadManager.getImpl().startDownload(info, musicUrl.get("128"), ".mp3");
                    }
                }
            }
        }.start();
    }
}
