package z.houbin.site.zdown.module.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import z.houbin.site.zdown.info.MusicInfo;
import z.houbin.site.zdown.module.BaseModule;

public abstract class MusicModule extends BaseModule {
    protected List<MusicInfo> musicInfos = new ArrayList<>();

    public MusicModule(String input) {
        super(input);
    }

    public void search() {

    }

    public void download() {

    }

    public void download(int ... index){

    }

    public List<MusicInfo> getMusicInfos() {
        return musicInfos;
    }

    public HashMap<String, String> getMusicUrl(MusicInfo info) {
        return new HashMap<>();
    }

}
