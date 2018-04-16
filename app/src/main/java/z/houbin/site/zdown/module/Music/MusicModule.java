package z.houbin.site.zdown.module.Music;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.util.DownloadManager;

public abstract class MusicModule extends BaseModule {
    protected List<MusicInfo> musicInfos = new ArrayList<>();
    protected Object data = null;

    public MusicModule(String input) {
        super(input);
    }

    public void search() {

    }

    public void download(int... index) {

    }

    public void downloadAll() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<MusicInfo> infos = getMusicInfos();
                for (MusicInfo info : infos) {
                    HashMap<String, String> musicUrl = getMusicUrl(info);
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

    public String[] getShowList() {
        String[] items = new String[musicInfos.size()];
        for (int i = 0; i < musicInfos.size(); i++) {
            items[i] = musicInfos.get(i).songName + "/" + musicInfos.get(i).singerName;
        }
        return items;
    }

    public List<MusicInfo> getMusicInfos() {
        return musicInfos;
    }

    public HashMap<String, String> getMusicUrl(MusicInfo info) {
        return new HashMap<>();
    }

}
