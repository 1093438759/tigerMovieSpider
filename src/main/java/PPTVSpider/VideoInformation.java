package PPTVSpider;

import com.google.gson.Gson;


import dao.DataSwitch;
import dao.SendRequestResponse;
import entitys.Episode;
import entitys.PPTV;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * pptv的爬虫
 * Created by ty on 2017/5/11.
 */
public class VideoInformation {

    /**
     * 获取电视源码文本
     *
     * @param url
     * @return String
     */
    public String getHtml(String url) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        String html = null;
        HttpResponse response;
        try {
            response = httpClient.execute(httpget);
            int resStatus = response.getStatusLine().getStatusCode();//状态码
            if (resStatus == HttpStatus.SC_OK) {
                HttpEntity Entity = response.getEntity();
                if (Entity != null)
                    html = EntityUtils.toString(Entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取URL源码错误");
        }
        return html;
    }

    /**
     * 传入视频id 返回每一集介绍以及评论id
     *
     * @param id 视频id
     * @return List<Episode>
     */
    public List<Episode> getTVMessage(String id, int severalEpisodes) {
        String url = "http://epg.api.pptv.com/detail.api?auth=d410fafad87e7bbf6c6dd62434345818&canal" +
                "=56&userLevel=1&appid=com.pplive.androidphone&appver=6.4.3&appplt=aph&vid" +
                "=%s&series=1&virtual=1&ver=4&platform=android3";
        String name = String.format(url, id);
        String html = getHtml(name);
        List<Episode> allInformation = new ArrayList<Episode>();
        Document document = Jsoup.parse(html);
        Elements ids = document.select("video_list").select("playlink2");
        if (ids == null || ids.size() == 0) {
            return null;
        }
        for (int i = 0; i < ids.size(); i++) {
            Episode episodeInformation = new Episode(ids.get(i).attr("id"),
                    DataSwitch.getDate(ids.get(i).attr("createTime")),
                    Integer.parseInt(ids.get(i).attr("durationSecond")),
                    Long.parseLong(ids.get(i).attr("pv")),
                    i + 1, null
            );
            if (severalEpisodes == 0)
                episodeInformation.setIntroduce(null);       //本集简介
            else
                episodeInformation.setIntroduce(eachIntroduce(id, i + 1));   //本集简介
            allInformation.add(episodeInformation);
        }
        return allInformation;
    }

    /**
     * 获取本集电视剧介绍
     *
     * @param id              视频id
     * @param severalEpisodes 更新至几集
     * @return String
     */
    public String eachIntroduce(String id, int severalEpisodes) {

        String url = "http://epg.api.pptv.com/epintro.api?pid=%s&episode=%d";
        String html = getHtml(String.format(url, id, severalEpisodes));
        String content = Jsoup.parse(html).select("content").text();
        if (content == null || content.equals("") || content.length() == 0)
            return null;
        return content;
    }

    /**
     * 获得一页视频的ID
     *
     * @param url 一页的url
     * @return List<String>
     */
    public List<String> getID(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        List<String> listID = new ArrayList<String>();
        HttpResponse response;
        String html = null;
        try {
            response = httpClient.execute(httpget);
            int resStatus = response.getStatusLine().getStatusCode();
            if (resStatus == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                    html = EntityUtils.toString(entity);
                Document document = Jsoup.parse(html);
                Elements elements = document.select("playlink2");
                for (Element element : elements)
                    listID.add(element.attr("id"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取ID失败");
        }
        return listID;
    }

    /**
     * 获取电视有多少页
     *
     * @param url 随便一页的视频url
     * @return int
     */
    public int getPageCount(String url) {
        String html = getHtml(url);
        Elements elements = Jsoup.parse(html).select("page_count");
        return Integer.parseInt(elements.get(0).text().toString());
    }

    /**
     * @param url       一页视频url
     * @param TVNameUrl 具体视频的url
     * @param type      电视类型（电影 电视剧 动漫）
     */
    public void getVideoData(String url, String TVNameUrl, String type) {
//电视剧 url
        String urlTV = "http://epg.api.pptv.com/list.api?auth=d410fafad87e7bbf6c6dd62434345818&appver=6.4.3&canal" +
                "=56&userLevel=1&virtual=1&order=t&s=%d&c=30&vt=21&contype=-1&ver=2&type=2&appplt=aph";

        String urlTV1 = "http://epg.api.pptv.com/list.api?auth=d410fafad87e7bbf6c6dd62434345818&appver=6.4.3&canal" +
                "=56&userLevel=1&virtual=1&order=t&s=1&c=30&vt=21&contype=-1&ver=2&type=2&appplt=aph";

        String teleplay = "http://epg.api.pptv.com/detail.api?auth=d410fafad87e7bbf6c6dd62434345818&canal" +
                "=56&userLevel=1&appid=com.pplive.androidphone&appver=6.4.3&appplt=aph&vid" +
                "=%s&series=1&virtual=1&ver=4&platform=android3";
//获得视频页数
        int pageCount = getPageCount(urlTV1);
        System.out.println(pageCount);
        if (type.equals("电视剧")) {
            System.out.println("插入电视剧开始");
            for (int i = 1; i <= pageCount; i++) {
                try {
                    String tv_url = String.format(urlTV, i);
                    getData(tv_url, teleplay, type);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("插入数据失败");
                }
            }
        } else if (type.equals("动画")) {
            System.out.println("插入动画开始");
            String url_tv = urlTV.replace("type=2", "type=3");
            for (int i = 1; i <= pageCount; i++) {
                try {
                    String TVurl = String.format(url_tv, i);
                    getData(TVurl, teleplay, type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (type.equals("电影")) {
            System.out.println("插入电影开始");
            String url_tv = urlTV.replace("type=2", "type=1");
            for (int i = 1; i <= pageCount; i++) {
                try {
                    String movie = String.format(url_tv, i);
                    getData(movie, teleplay, type);
                } catch (Exception e) {
                    System.out.println("插入失败");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("url地址错误");
            System.exit(0);
        }
    }

    /**
     * 获取视频的基本信息
     *
     * @param urlTV     一页视频url(一般第一页开始)
     * @param TVNameUrl 具体视频的url
     * @param type      电视类型（电影 电视剧 动漫）
     */
    public void getData(String urlTV, String TVNameUrl, String type) {
        List<String> teleplayID = getID(urlTV);
        String TVHtml;
        for (String id : teleplayID) {
            JSONArray jsonArray = new JSONArray();
            String tvUrl = String.format(TVNameUrl, id);
            TVHtml = getHtml(tvUrl);
            TVHtml = TVHtml.replace("area>", "area1>");
            Document doc = Jsoup.parse(TVHtml);
            String titles = doc.select("title").get(0).text();                              //电视名字
            if (titles == null || titles.equals("")) {
                System.exit(0);
            }
            String vid = doc.select("vid").get(0).text();                                   //电视ID
            int episodes = Integer.parseInt(doc.select("total_state").get(0).text());       //集数
            String fixupdates = doc.select("fixupdate").get(0).text();                      //更新
            try {
                int vsTitle = 0;
                String vsTitles = doc.select("vsTitle").get(0).text();                         //更新至几级
                if (!vsTitles.equals("") && vsTitles != null) {
                    vsTitle = Integer.parseInt(vsTitles);
                }
                String directors = doc.select("director").get(0).text();                        //导演
                String[] act = doc.select("act").get(0).text().split(",");               //主演
                String[] catalog = doc.select("catalog").get(0).text().split(",");       //类型
                String areas = doc.select("area1").get(0).text();                               //那地拍摄
                String year = doc.select("year").get(0).text();                                 //哪一年播出
                int years = 0;
                if (!year.equals("") && year != null) {
                    years = Integer.parseInt(year);
                }
                float score = Float.parseFloat(doc.select("mark").get(0).text());               //评分
                long pvs = Long.parseLong(doc.select("pv").get(0).text());                      //播放量
                String contents = doc.select("content").get(0).text();                          //电视简介
                List<Episode> eachIntroduc = getTVMessage(vid, vsTitle);                                //一部电视剧每集介绍
                PPTV video = new PPTV(titles, vid, episodes, fixupdates, vsTitle, eachIntroduc, directors, act, type,
                        catalog, areas, years, score, pvs, contents, "PPTV");

                String videos = new Gson().toJson(video);
                jsonArray.put(videos);
                new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/tv/updateVideo?", jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("视频不存在");
            }
        }
    }
}

