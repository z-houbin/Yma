//package z.houbin.site.zdown.module.Music.Kuwo;
//
//import android.util.Base64;
//
//import z.houbin.site.zdown.module.Music.MusicModule;
//
///**
// * http://www.kuwo.cn/yinyue/6657692
// *
// * 接口:
// * 搜索:
// * http://search.kuwo.cn/r.s?all=%E5%91%A8%E6%9D%B0%E4%BC%A6&ft=music&itemset=web_2015&client=kt&pn=0&rn=50&rformat=json&encoding=utf8
// * 地址:
// * http://antiserver.kuwo.cn/anti.s?response=url&rid=MUSIC_6657692&format=mp3&type=convert_url
// * http://antiserver.kuwo.cn/anti.s?response=url&rid=APE_41187764&format=ALFLAC&type=convert_url2
// */
//public class Kuwo extends MusicModule{
//    public Kuwo(String input) {
//        super(input);
//        String quality = "1000";
//        String format = "ape";
//        String id = "41187765";
//        String text = "type=convert_url2&br=" + quality + "&format="+(format == "ape" ? "ape" : "mp3") +"&sig=0&rid="+id+"&network=wifi";
//        try {
//            String q = new String(Base64.encode(KwDes.EncryptToBytes(text, "ylzsxkwm"),Base64.DEFAULT));
//            String link = "http://nmobi.kuwo.cn/mobi.s?f=kuwo&q=" + q;
//            System.out.println(link);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
