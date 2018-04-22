//package z.houbin.site.zdown.module;
//
//import android.util.Base64;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.Request;
//import okhttp3.Response;
//import z.houbin.site.zdown.info.BaseInfo;
//
///**
// * http://www.meipai.com/media/952173910
// */
//public class MeiPai extends BaseModule {
//
//    public MeiPai(String input) {
//        super(input);
//
//        Headers.Builder headerBuilder = new Headers.Builder();
//        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
//        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
//        headerBuilder.add("Connection", "keep-alive");
//        headerBuilder.add("Host", "www.meipai.com");
//        headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
//        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
//        mHeaders = headerBuilder.build();
//    }
//
//    @Override
//    public void doInThread() {
//        super.doInThread();
//        Request request = new Request.Builder().get().url(mInput).headers(mHeaders).build();
//        mClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                onLoadError(e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String html = response.body().string();
//                Document document = Jsoup.parse(html);
//                Elements metaElements = document.select("meta");
//                mInfo = new BaseInfo() {};
//                for (int i = 0; i < metaElements.size(); i++) {
//                    Element element = metaElements.get(i);
//                    if (element.hasAttr("property")) {
//                        String attr = element.attr("property");
//                        if (attr.contains("og:")) {
//                            metas.put(attr, element.attr("content"));
//
//                            switch (attr) {
//                                case "og:title":
//                                    mInfo.title = element.attr("content");
//                                    break;
//                                case "og:description":
//                                    mInfo.description = element.attr("content");
//                                    break;
//                                case "og:url":
//                                    mInfo.url = element.attr("content");
//                                    break;
//                                case "og:type":
//                                    mInfo.type = element.attr("content");
//                                    break;
//                                case "og:video":
//                                    mInfo.video = element.attr("content");
//                                    break;
//                                case "og:video:width":
//                                    mInfo.vwidth = element.attr("content");
//                                    break;
//                                case "og:video:height":
//                                    mInfo.vheight = element.attr("content");
//                                    break;
//                                case "og:image":
//                                    mInfo.image.add(element.attr("content"));
//                                    break;
//                                case "og:site_name":
//                                    mInfo.site = element.attr("content");
//                                    break;
//                            }
//
//                            if (attr.equalsIgnoreCase("og:video:url")) {
//                                //视频解码
//                                String code = element.attr("content");
//                                //获取前4字并反转
//                                String a = new StringBuilder(code.substring(0, 4)).reverse().toString();//269c
//                                //余下所有
//                                String b = code.substring(4);
//                                //4字转16进制
//                                String c = String.valueOf(Integer.parseInt(a, 16));//9884
//                                int[] arr = string2Array(c);
//                                //去除多余字符串
//                                String builder = b.substring(0, arr[0]) +
//                                        b.substring(arr[0] + arr[1], b.length() - arr[2] - arr[3]) +
//                                        b.substring(b.length() - arr[2]);
//
//                                String decodedString = new String(Base64.decode(builder, Base64.DEFAULT));
//                                metas.put(attr, decodedString);
//                                mInfo.video = decodedString;
//                            }
//                        }
//                    }
//                }
//                onLoadEnd();
//            }
//        });
//    }
//
//    private int[] string2Array(String str) {
//        int[] arr = new int[str.length()];
//        for (int i = 0; i < str.length(); i++) {
//            arr[i] = Integer.parseInt(str.charAt(i) + "");
//        }
//        return arr;
//    }
//}
