package z.houbin.site.zdown.info;

import java.util.HashMap;

public class MusicInfo extends BaseInfo{
    public String albumid;
    public String albummid;
    public String albumname;
    public int chinesesinger;
    public String pubtime;
    public String singerId;
    public String singerMid;
    public String singerName;
    public HashMap<String,String> url = new HashMap<>();
    public String songId;
    public String songMid;
    public String songName;
    public String songUrl;
    public int stream;
    public long size128;
    public long size320;
    public long sizeApe;
    public long sizeflac;
    public long sizeogg;
    public String docid;

    @Override
    public String toString() {
        return "MusicInfo{" +
                "albumid='" + albumid + '\'' +
                ", albummid='" + albummid + '\'' +
                ", albumname='" + albumname + '\'' +
                ", chinesesinger=" + chinesesinger +
                ", pubtime='" + pubtime + '\'' +
                ", singerId='" + singerId + '\'' +
                ", singerMid='" + singerMid + '\'' +
                ", singerName='" + singerName + '\'' +
                ", url=" + url +
                ", songId='" + songId + '\'' +
                ", songMid='" + songMid + '\'' +
                ", songName='" + songName + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", stream=" + stream +
                ", size128=" + size128 +
                ", size320=" + size320 +
                ", sizeApe=" + sizeApe +
                ", sizeflac=" + sizeflac +
                ", sizeogg=" + sizeogg +
                ", docid='" + docid + '\'' +
                '}';
    }
}
