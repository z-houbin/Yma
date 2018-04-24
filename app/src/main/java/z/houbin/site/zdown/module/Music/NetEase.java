package z.houbin.site.zdown.module.Music;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.module.Music.netease.NetEasePlayListBean;
import z.houbin.site.zdown.module.Music.netease.NetEaseSearchBean;
import z.houbin.site.zdown.util.DownloadManager;

public class NetEase extends MusicModule {
    public NetEase() {
        super();
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "application/json");
        headerBuilder.add("accept-encoding", "deflate");
        headerBuilder.add("Accept-Language", "en-US,en;q=0.8");
        headerBuilder.add("Cookie", "");
        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.117 Safari/537.36");
        mHeaders = headerBuilder.build();
    }

    @Override
    public void search(String input) {
        super.search(input);
        musicInfos.clear();
        final String search_url = "http://music.163.com/api/search/get/";
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("s", "" + input);
        bodyBuilder.add("limit", "30");
        bodyBuilder.add("sub", "false");
        bodyBuilder.add("type", "1");
        bodyBuilder.add("offset", "0");
        Request request = new Request.Builder().post(bodyBuilder.build()).url(search_url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                Gson gson = new Gson();
                NetEaseSearchBean searchBean = gson.fromJson(html, NetEaseSearchBean.class);
                List<NetEaseSearchBean.ResultBean.SongsBean> songs = searchBean.getResult().getSongs();
                for (int i = 0; i < songs.size(); i++) {
                    NetEaseSearchBean.ResultBean.SongsBean song = songs.get(i);
                    MusicInfo info = new MusicInfo();
                    info.albumid = song.getAlbum().getId() + "";
                    info.albumname = song.getAlbum().getName();
                    info.songId = song.getId() + "";
                    info.songName = song.getName();
                    info.author = song.getArtists().get(0).getName();
                    if (song.getAlias() != null && !song.getAlias().isEmpty()) {
                        info.songName += "(" + song.getAlias().get(0) + ")";
                    }
                    info.singerName = info.author;
                    musicInfos.add(info);
                }
                onLoadEnd();
            }
        });
    }

    /**
     * http://music.163.com/#/playlist?id=2181606783
     *
     * @param input 地址
     */
    public void parsePlayList(String input) {
        musicInfos.clear();
        String id = "";
        if(input.contains("#")){
            //PC
            id = input.substring(input.indexOf("=") + 1);
        }else{
            //手机
            String [] subs = input.split("/");
            id = subs[4];
        }
        String url = String.format(Locale.CHINA, "http://music.163.com/api/playlist/detail?id=%s", id);
        Request request = new Request.Builder().get().url(url).headers(mHeaders).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                Gson gson = new Gson();

                NetEasePlayListBean playListBean = gson.fromJson(html, NetEasePlayListBean.class);
                if (playListBean.getCode() == 200) {
                    NetEasePlayListBean.ResultBean resultBean = playListBean.getResult();
                    title = resultBean.getName();

                    List<NetEasePlayListBean.ResultBean.TracksBean> tracks = resultBean.getTracks();
                    for (NetEasePlayListBean.ResultBean.TracksBean track : tracks) {
                        MusicInfo info = new MusicInfo();
                        info.albumid = track.getAlbum().getId() + "";
                        info.albumname = track.getAlbum().getName();
                        info.songId = track.getId() + "";
                        info.songName = track.getName();
                        info.author = track.getArtists().get(0).getName();
                        if (track.getAlias() != null && !track.getAlias().isEmpty()) {
                            info.songName += "(" + track.getAlias().get(0) + ")";
                        }
                        info.singerName = info.author;
                        musicInfos.add(info);
                    }
                }
                onLoadEnd();
            }
        });
    }

    private String getSongUrl(MusicInfo info) {
        String url = String.format(Locale.CHINA, "https://music.163.com/api/song/enhance/download/url?br=320000&id=%s", info.songId);
        Request request = new Request.Builder().get().url(url).build();
        try {
            Response response = mClient.newCall(request).execute();
            String html = response.body().string();
            JSONObject jsonObject = new JSONObject(html);
            url = jsonObject.getJSONObject("data").getString("url");
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void downloadAll() {
        super.downloadAll();
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (MusicInfo info : musicInfos) {
                    String url = getSongUrl(info);
                    if (TextUtils.isEmpty(url) || "null".equals(url)) {
                        url = String.format(Locale.CHINA, "http://music.163.com/song/media/outer/url?id=%s.mp3", info.songId);
                    }
                    if (!TextUtils.isEmpty(url)) {
                        DownloadManager.getImpl().startDownload(info, url, ".mp3");
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
                for (int i = 0; i < index.length; i++) {
                    MusicInfo info = musicInfos.get(index[i]);
                    String url = getSongUrl(info);
                    if (TextUtils.isEmpty(url) || "null".equals(url)) {
                        url = String.format(Locale.CHINA, "http://music.163.com/song/media/outer/url?id=%s.mp3", info.songId);
                    }
                    if (!TextUtils.isEmpty(url)) {
                        DownloadManager.getImpl().startDownload(info, url, ".mp3");
                    }
                }
            }
        }.start();
    }
}
