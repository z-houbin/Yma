package z.houbin.site.zdown.module.Music;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.util.DownloadManager;

public abstract class MusicModule extends BaseModule {
    protected List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
    protected Object data = null;

    public MusicModule(String input) {
        super(input);
    }

    public void search() {

    }

    public void download(int... index) {

    }

    public void downloadAll() {
        if (musicInfos.isEmpty()) {
            return;
        }
        if (musicInfos.get(0) != null) {
            return;
        }
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                List<MusicInfo> infos = musicInfos;
//                for (int i = 0; i < infos.size(); i++) {
//                    MusicInfo info = infos.get(i);
//                    HashMap<String, String> musicUrl = getMusicUrl(i);
//                    if (info.sizeflac != 0 && musicUrl.containsKey("flac") && !TextUtils.isEmpty(musicUrl.get("flac"))) {
//                        DownloadManager.getImpl().startDownload(info, musicUrl.get("flac"), ".flac");
//                    } else if (info.sizeApe != 0 && musicUrl.containsKey("ape") && !TextUtils.isEmpty(musicUrl.get("ape"))) {
//                        DownloadManager.getImpl().startDownload(info, musicUrl.get("ape"), ".ape");
//                    } else if (info.size320 != 0 && musicUrl.containsKey("320") && !TextUtils.isEmpty(musicUrl.get("320"))) {
//                        DownloadManager.getImpl().startDownload(info, musicUrl.get("320"), ".mp3");
//                    } else if (info.size128 != 0 && musicUrl.containsKey("128") && !TextUtils.isEmpty(musicUrl.get("128"))) {
//                        DownloadManager.getImpl().startDownload(info, musicUrl.get("128"), ".mp3");
//                    }
//                }
//            }
//        }.start();
    }

    public String[] getShowList() {
        String[] items = new String[musicInfos.size()];
        for (int i = 0; i < musicInfos.size(); i++) {
            MusicInfo info = (MusicInfo) musicInfos.get(i);
            items[i] = info.songName + "/" + info.singerName;
        }
        return items;
    }

    public void initMusicInfos() {
    }

    public Object getSongInfo(int index) {
        return new Object();
    }

}
