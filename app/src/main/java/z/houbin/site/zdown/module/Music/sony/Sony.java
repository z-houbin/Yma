package z.houbin.site.zdown.module.Music.sony;


import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.module.Music.MusicModule;
import z.houbin.site.zdown.util.DownloadManager;

public class Sony extends MusicModule {
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Sony() {
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        headerBuilder.add("Connection", "keep-alive");
        headerBuilder.add("Content-Type", "application/json;charset=UTF-8");
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
        mHeaders = headerBuilder.build();
    }

    @Override
    public void search(final String input) {
        super.search(input);
        musicInfos.clear();
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<MusicInfo> songList = searchSong(input);
                if(!songList.isEmpty()){
                    musicInfos.addAll(songList);
                }
                onLoadEnd();
            }
        }.start();
    }

    private List<MusicInfo> searchSong(String input) {
        List<MusicInfo> infoList = new ArrayList<>();
        int page = 1;
        int size = 50;
        String text = "keyword=" + input + "&" + "pageNo=" + (page - 1) + "&" + "pageSize=" + size + "&" + "type=TRACK";
        String url = "http://api.sonyselect.com.cn/es/search/v1/android/?sign=" + des3EncryptedText(text);
        System.out.println(url);

        final String postData =
                "{\"content\":{\"type\":\"TRACK\",\"pageSize\":\"" + size + "\",\"pageNo\":\"" + (page - 1) + "\",\"keyword\":\"" + input + "\"},\"header\":{\"sdkNo\":\"4.2.2\",\"model\":\"X9Plus\",\"manufacturer\":\"vivo\",\"imei\":\"133524532901500\"}}";

        RequestBody body = RequestBody.create(JSON, postData);
        Request request = new Request.Builder().url(url).post(body).headers(mHeaders).build();
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
        SonySongSearchBean searchBean = new Gson().fromJson(html, SonySongSearchBean.class);
        data = searchBean;

        List<SonySongSearchBean.ContentBeanX.TrackPageBean.ContentBean> contentBeans = searchBean.getContent().getTrackPage().getContent();
        for (SonySongSearchBean.ContentBeanX.TrackPageBean.ContentBean contentBean : contentBeans) {
            MusicInfo info = new MusicInfo();
            try {
                info.songId = contentBean.getId() + "";
                info.songName = contentBean.getName();
                info.url = contentBean.getDownloadUrl();
                info.duration = contentBean.getDuration();
                info.source = contentBean;
                SonySongSearchBean.ContentBeanX.TrackPageBean.ContentBean.AlbumsBean albumsBean = contentBean.getAlbums().get(0);
                info.singerName = albumsBean.getAritst();
                info.albumname = albumsBean.getName();
                info.albumid = albumsBean.getId() + "";
                info.pubtime = albumsBean.getCreateTime();
                info.bitrate = albumsBean.getBitrate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            infoList.add(info);
        }

        return infoList;
    }

    @Override
    public String[] getShowList() {
        String[] items = new String[musicInfos.size()];
        for (int i = 0; i < musicInfos.size(); i++) {
            MusicInfo info = musicInfos.get(i);
            String item = info.songName + "/" + info.singerName + "/" + info.albumid;
            items[i] = item;
        }
        return items;
    }

    public void searchAlbum(String input) {
        super.search(input);
        musicInfos.clear();

        String text = "albumId=" + input;
        String url = "http://api.sonyselect.com.cn/albumDetail/v1/android/?sign=" + des3EncryptedText(text);
        String postData =
                "{\"content\":{\"albumId\":\"" + input + "\"},\"header\":{\"sdkNo\":\"4.2.2\",\"model\":\"X9Plus\",\"manufacturer\":\"vivo\",\"imei\":\"133524532901500\"}}";
        System.out.println(url);

        RequestBody body = RequestBody.create(JSON, postData);
        Request request = new Request.Builder().url(url).post(body).headers(mHeaders).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                SonyAlbumSearchBean searchBean = new Gson().fromJson(html, SonyAlbumSearchBean.class);
                data = searchBean;

                SonyAlbumSearchBean.ContentBean.AlbumBean album = searchBean.getContent().getAlbum();
                title = album.getName();
                List<SonyAlbumSearchBean.ContentBean.AlbumBean.TracksBean> tracks = album.getTracks();
                for (SonyAlbumSearchBean.ContentBean.AlbumBean.TracksBean track : tracks) {
                    MusicInfo info = new MusicInfo();
                    try {
                        info.songId = track.getId() + "";
                        info.songName = track.getName();
                        info.url = track.getDownloadUrl();
                        info.duration = track.getDuration();
                        info.source = track;
                        info.singerName = album.getAritst();
                        info.albumname = album.getName();
                        info.albumid = album.getId() + "";
                        info.pubtime = album.getCreateTime();
                        info.bitrate = album.getBitrate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicInfos.add(info);
                }
                onLoadEnd();
            }
        });
    }

    @Override
    public void download(final int... index) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i : index) {
                    MusicInfo info = musicInfos.get(i);
                    List<MusicInfo> songList = searchSong(info.songName);
                    if(!songList.isEmpty()){
                        info = songList.get(0);
                    }
                    DownloadManager.getImpl().startDownload(info, info.url, ".flac");
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
                    List<MusicInfo> songList = searchSong(info.songName);
                    if(!songList.isEmpty()){
                        info = songList.get(0);
                    }
                    DownloadManager.getImpl().startDownload(info, info.url, ".flac");
                }
            }
        }.start();
    }

    private String des3EncryptedText(String input) {
        byte[] k = new byte[24];
        byte[] data = input.getBytes();
        int len = data.length;
        if (data.length % 8 != 0) {
            len = data.length - data.length % 8 + 8;
        }
        byte[] needData = null;
        if (len != 0)
            needData = new byte[len];
        for (int i = 0; i < len; i++) {
            needData[i] = 0x00;
        }
        System.arraycopy(data, 0, needData, 0, data.length);
        byte[] key = "1!QAZ2@WSXCDE#3$4RFVB7GT%5^6YYUMJU7&8*IK<.LO9(0P".getBytes();
        if (key.length == 16) {
            System.arraycopy(key, 0, k, 0, key.length);
            System.arraycopy(key, 0, k, 16, 8);
        } else {
            System.arraycopy(key, 0, k, 0, 24);
        }

        byte[] res = new byte[0];
        try {
            res = TripleDES.des3EncodeCBC(key, "12481632".getBytes(), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str = new String(Base64.encode(res, Base64.DEFAULT));
        System.out.println(str);
        return str;
    }
}
