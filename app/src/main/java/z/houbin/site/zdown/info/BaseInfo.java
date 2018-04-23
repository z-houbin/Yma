package z.houbin.site.zdown.info;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseInfo {
    //og:url
    public String url;
    //og:site_name
    public String site;
    //og:video
    public String video;
    //og:title
    public String title;
    //og:image
    public List<String> image = new ArrayList<>();
    //og:description
    public String description;
    public String userId;
    //og:type
    public String type;
    //og:video:width
    public String vwidth;
    //og:video:height
    public String vheight;
    //内容
    public String content = "";
    //音乐
    public String music = "";
    //作者
    public String author = "";
}
