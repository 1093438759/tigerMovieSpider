package topSearchSpider;

import com.google.gson.Gson;
import dao.SendRequestResponse;
import entitys.Search;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by ty on 2017/6/11.
 */
public class GetSogouDate {

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
                    html = EntityUtils.toString(Entity, "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取URL源码错误");
        }
        return html;
    }

    public int getPage(String url) {
        String html = getHtml(url);
        Elements elements = Jsoup.parse(html).select("a[href~=shishi_\\d.html]:matchesOwn(\\d+)");
        String page = elements.get(elements.size() - 1).text();
        return Integer.parseInt(page);
    }

    public void getData(String url) {
        int pages = getPage(String.format(url, 1));
        JSONArray jsonArray = new JSONArray();
        for (int i = 1; i <= pages; i++) {
            String html = getHtml(String.format(url, i));
            Elements lis = Jsoup.parse(html).select("ul[class=pub-list]>li");
            for (int j = 0; j < lis.size(); j++) {
                String rank = lis.get(j).select("i").get(0).text();                                     //排名
                String title = lis.get(j).select("span[class=s2]>p:first-child>a[href]").get(0).text(); //名字
                String topSearch = lis.get(j).select("span[class=s3]").text();                          //点击或者阅读量
                String topUrl = lis.get(j).select("a[href]").attr("href");                  //地址
                Search soGouSearch = new Search(title, Integer.parseInt(rank), topUrl, Long.parseLong(topSearch), 0, "sogou");
                String search = new Gson().toJson(soGouSearch);
                jsonArray.put(search);
            }
        }
         new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/tv/updateTopSearch?",jsonArray);
    }
}
