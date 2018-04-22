//package z.houbin.site.zdown.module;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Headers;
//import okhttp3.Request;
//import okhttp3.Response;
//import z.houbin.site.zdown.info.InstagramInfo;
//
///**
// * 单张照片:https://www.instagram.com/p/Bgoydu6A6PS/
// * 多张照片:https://www.instagram.com/p/BgUIBy6A-EC/?taken-by=ashleybenson
// * 视频:https://www.instagram.com/p/BgpM_mlg6ih/?taken-by=ashleybenson
// */
//public class Instagram extends BaseModule {
//
//    public Instagram(String input) {
//        super(input);
//        Headers.Builder headerBuilder = new Headers.Builder();
//        headerBuilder.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*");
//        headerBuilder.add("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
//        headerBuilder.add("Connection", "keep-alive");
//        headerBuilder.add("Host", "www.instagram.com");
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
//                //基本信息
//                Elements metaElements = document.select("meta");
//                mInfo = new InstagramInfo();
//                for (int i = 0; i < metaElements.size(); i++) {
//                    Element element = metaElements.get(i);
//                    if (element.hasAttr("property")) {
//                        String attr = element.attr("property");
//                        if (attr.contains("og:")) {
//                            metas.put(attr, element.attr("content"));
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
//                        }
//                    }
//                }
//                //图片查找
//                Elements scriptElements = document.select("script");
//                for (Element element : scriptElements) {
//                    if (element.data().contains("window._sharedData")) {
//                        String data = element.data();
//                        String json = data.substring(data.indexOf("{"));
//                        try {
//                            JSONObject jsonObject = new JSONObject(json);
//                            JSONObject media = jsonObject.getJSONObject("entry_data").getJSONArray("PostPage").getJSONObject(0).getJSONObject("graphql").getJSONObject("shortcode_media");
//                            //内容
//                            try {
//                                mInfo.content = media.getJSONObject("edge_media_to_caption").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("text");
//                            }catch (Exception e){
//
//                            }
//
//
//                            List<String> picList = new ArrayList<>();
//                            JSONArray edges = media.getJSONObject("edge_sidecar_to_children").getJSONArray("edges");
//                            for (int i = 0; i < edges.length(); i++) {
//                                JSONObject edge = edges.getJSONObject(i);
//                                JSONObject node = edge.getJSONObject("node");
//                                JSONArray resources = node.getJSONArray("display_resources");
//                                JSONObject resource = resources.getJSONObject(resources.length() - 1);
//                                picList.add(resource.getString("src"));
//                            }
//                            mInfo.image.addAll(picList);
//                            metas.put("og:image", picList);
//                            System.out.println();
//                        } catch (Exception e) {
//                            //单图
//                            //e.printStackTrace();
//                        }
//                    }
//                }
//                onLoadEnd();
//            }
//        });
//    }
//}
