package topSearchSpider;


import com.google.gson.Gson;
import dao.SendRequestResponse;
import entitys.Search;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * Created by ty on 2017/6/12.
 */
public class GetBaiduDate {

    /**
     * 实时热点
     *
     * @param url
     */
    public void getRealTimeData(String url, String realTime) {
        JSONArray topArray = new JSONArray();
        Search search;
        String html = new SendRequestResponse().getHtmlGBK(url);
        Element doc = Jsoup.parse(html);
        Elements trs = doc.select("tr:not(.item-tr)");
        int size = trs.size();
        for (int i = 1; i < size; i++) {
            Element tr = trs.get(i);
            Element firstLine = tr.select("td[class=first]").get(0);
            Element secondLine = tr.select("td[class=keyword]").get(0);
            Element lastLine = tr.select("td[class=last]").get(0);
            String rank = firstLine.text();                                                       //排名
            String title = secondLine.select("a[href]").get(0).text();                  //标题
            String topUrl = secondLine.select("a[href]").attr("href");      //地址
            String readAmount = lastLine.text();                                                  //阅读
            Elements news = secondLine.select("span");                                  //打新
            search = new Search(title, Integer.parseInt(rank), topUrl, Long.parseLong(readAmount),
                    0, "baidu");
            if (news.size() != 0)
                search.setType(1);
            String searchs = new Gson().toJson(search);
            topArray.put(searchs);

        }
        new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/tv/updateTopSearch?", topArray);
    }

}

