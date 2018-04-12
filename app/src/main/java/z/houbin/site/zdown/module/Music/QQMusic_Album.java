package z.houbin.site.zdown.module.Music;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.util.DownloadManager;

//https://y.qq.com/n/yqq/album/001BQNXZ2QkzMh.html#stat=y_new.album_lib.album_name
//https://y.qq.com/n/yqq/album/004AhJHV3slLjN.html
public class QQMusic_Album extends MusicModule {
    private static final String URL_INFO = "http://c.y.qq.com/v8/fcg-bin/fcg_v8_album_info_cp.fcg?inCharset=utf8&outCharset=utf-8&albummid=%s";

    public QQMusic_Album(String input) {
        super(input);
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "close");
        headerBuilder.add("Host", "y.qq.com");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        headerBuilder.add("Referer", "http://y.qq.com");
        mHeaders = headerBuilder.build();
    }

    @Override
    public HashMap<String, String> getMusicUrl(MusicInfo info) {
        String url = "http://base.music.qq.com/fcgi-bin/fcg_musicexpress.fcg?json=3&inCharset=utf8&outCharset=utf-8&format=json&guid=" + info.docid;
        Request request = new Request.Builder().get().url(url).build();
        try {
            Response response = mClient.newCall(request).execute();
            String json = response.body().string();
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
        return super.getMusicUrl(info);
    }

    @Override
    public void doInThread() {
        super.doInThread();
        if(mInput.contains("url.cn")){
            try {
                Document document = Jsoup.connect(mInput).get();
                mInput = document.location();
                mInput = String.format(Locale.CHINA,"https://y.qq.com/n/yqq/album/%s_num.html",document.location().split("\\?")[1].split("&")[0].split("=")[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(mInput.contains("_num.html")){
            //手机QQ音乐专辑分享
            try {
                Request request = new Request.Builder().get().url(mInput).headers(mHeaders).build();
                Response response = mClient.newCall(request).execute();
                Document document = Jsoup.parse(response.body().string());
                Elements elements = document.select("link");
                mInput = elements.get(0).attr("href");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String s = mInput.split(".html")[0];
        String albummid = s.substring(s.lastIndexOf("/") + 1);
        String url = String.format(Locale.CHINA, URL_INFO, albummid);
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
                }

                onLoadEnd();
            }
        });
    }

    @Override
    public void download(final int... index) {
        super.download(index);
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i : index) {
                    MusicInfo info = musicInfos.get(i);
                    HashMap<String, String> musicUrl = getMusicUrl(musicInfos.get(i));
                    if (info.sizeflac != 0 && musicUrl.containsKey("flac") && !TextUtils.isEmpty(musicUrl.get("flac"))) {
                        DownloadManager.getImpl().startDownload(info,musicUrl.get("flac"), ".flac");
                    } else if (info.sizeApe != 0 &&musicUrl.containsKey("ape") && !TextUtils.isEmpty(musicUrl.get("ape"))) {
                        DownloadManager.getImpl().startDownload(info,musicUrl.get("ape"), ".ape");
                    } else if (info.size320 !=0 && musicUrl.containsKey("320") && !TextUtils.isEmpty(musicUrl.get("320"))) {
                        DownloadManager.getImpl().startDownload(info,musicUrl.get("320"), ".mp3");
                    }else if(info.size128 !=0 && musicUrl.containsKey("128") && !TextUtils.isEmpty(musicUrl.get("128"))){
                        DownloadManager.getImpl().startDownload(info,musicUrl.get("128"), ".mp3");
                    }
//                    for (String key : musicUrl.keySet()) {
//                        String suffix = ".mp3";
//                        if (key.equals("128") || key.equals("320")) {
//                            suffix = ".mp3";
//                        } else {
//                            suffix = "." + key;
//                        }
//                        DownloadManager.getImpl().startDownload(musicUrl.get(key), suffix);
//                    }
                }
            }
        }.start();
    }

    @Override
    public void download() {
        super.download();
        download(0);
    }
}
