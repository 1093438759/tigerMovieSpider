package spider;

import dao.DataSwitch;
import dao.SendRequestResponse;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ty on 2017/6/26.
 */
public class FictionSpider {

    public void getNovelInformation() {
        int sum = 1;
        while (true) {
            String html = new SendRequestResponse().getHtml("http://www.xxsy.net/search?s_wd=&sort=9&pn=" + sum);
            Document document = Jsoup.parse(html);
            Elements elements = document.select("ul[class=con_ord_2]");
            if (elements.size() == 0) {
                return;
            }
            for (Element element : elements) {
                JSONObject jsonObject = new JSONObject();
                String url = "http://www.xxsy.net" + element.select("a").get(0).attr("href");
                List<String> list = getRead(url);
                if (list == null) {
                    continue;
                }
                long click = Long.parseLong(list.get(0));
                Date createTime = DataSwitch.getDate(list.get(1));
                String sourceId = getID(url);
                String urlPoster = element.select("img").get(0).attr("src");
                String title = element.select("li[class=title] a").get(0).text();
                //System.out.println(title);
                Elements spans = element.select("li[class=title] span[class=key]");
                String author = spans.get(0).text();
                String type = spans.get(2).text();
                String[] tags = spans.get(3).text().split(",");
                String simplePlot = element.select("li[class=info]").text();
                //element.select("li[class=ls_bottom] span").get(0);     // 月点击
                //element.select("li[class=ls_bottom] span").get(1);     // 月票

                jsonObject.put("sourceId", sourceId);                               //原始id
                jsonObject.put("url", url);                                         //链接
                jsonObject.put("click", click);                                     //阅读量
                jsonObject.put("createTime", createTime);                           //上传时间
                jsonObject.put("urlPoster", urlPoster);                             //封面
                jsonObject.put("title", title);                                     //标题
                jsonObject.put("author", author);                                   //作者名字
                jsonObject.put("type", type);                                       //类型
                jsonObject.put("tags", tags);                                       //标签
                jsonObject.put("simplePlot", simplePlot);                           //简介
                jsonObject.put("platform", "潇湘书院");                             //平台


                Elements ls_bottom = element.select("li[class=ls_bottom]>span");
                int length = ls_bottom.size();
                String bottom = ls_bottom.toString();
                if (bottom.indexOf("订阅人气：") >= 0) {
                    String hot = ls_bottom.get(length - 3).text().replace("订阅人气：", "");
                    jsonObject.put("hot", Integer.parseInt(hot));                   //人气

                }
                if (bottom.indexOf("更新：") >= 0) {
                    String updateTime = ls_bottom.get(length - 2).text().replace("更新：", "");
                    jsonObject.put("updateTime", DataSwitch.getDate(updateTime));    //更新


                }
                if (bottom.indexOf("字数：") >= 0) {
                    String wordCount = ls_bottom.get(length - 1).text().replace("字数：", "");
                    jsonObject.put("wordCount", Integer.parseInt(wordCount));       //字数

                }
                new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/fiction/updateFictions?", jsonObject);
                // System.out.println(jsonObject);
            }
            sum++;
            System.out.println("成功更新或者插入" + sum + "条记录");
        }
    }

    /**
     * 获取阅读量和一开始上传平台时间
     *
     * @param url
     * @return
     */
    public List<String> getRead(String url) {
        String html = new SendRequestResponse().getHtml(url);
        try {
            if (html.indexOf("404，找不到页面") < 0 || html.length() != 0) {
                List<String> list = new ArrayList<String>();
                Document document = Jsoup.parse(html);
                Elements message = document.select("ul[class=infolist]");
                list.add(message.select("#b-info-click").text());   //阅读量
                Pattern pattern = Pattern.compile("\\d{4}(-|/|.)\\d{1,2}\\1\\d{1,2}");
                Matcher matcher = pattern.matcher(message.toString());
                if (matcher.find()) {
                    list.add(matcher.group());      //上传日期
                }
                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 传入一个字符串获得第一个数字(x xx xxx....)
     *
     * @param url
     * @return
     */
    public String getID(String url) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(url);
        String id = null;
        if (matcher.find()) {
            id = matcher.group(1);
        }
        return id;
    }

    public static void main(String[] args) {
        new FictionSpider().getNovelInformation();
    }
}
