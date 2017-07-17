package spider;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import dao.SendRequestResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 爬取腾讯视频基本信息
 * Created by ty on 2017/5/15.
 */
public class Tencent {

    public static List<String> getTVUrl(String url) {
        String html =new SendRequestResponse().getHtml(url);
        List<String> list = new ArrayList<String>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("strong a");
        for (Element element : elements) {
            list.add(element.attr("href"));
        }
        return list;
    }

    public static void getTVData(String url,int sum) throws IOException {
        List<String> list = getTVUrl(url);
        //DataInteraction dataInteraction =new MyData();
       // DBCollection dbCollection= MongoDBUtils.getConnection("docs","TV");
        DBObject dbObject=new BasicDBObject();

        for (String tvUrl : list) {
            //获取源码
            //Document document = Jsoup.connect(tvUrl).get();
            String html=new SendRequestResponse().getHtml(tvUrl);

            Document document =Jsoup.parse(html);
            //名字
            Elements elements = document.select(".album_title_link");
            String tvName = elements.get(0).text();
            //导演
            Elements director = document.select("a[_stat=desc:director]");
            //主演
            Elements actors = document.select("a[_stat=desc:actor]");
            //类型
            Elements description = document.select("a[_stat=description:tag]");
            //那地拍摄
            String contentLocation = document.select("meta[itemprop=contentLocation]").attr("content");
            //哪一年出的
            String year = document.select("meta[itemprop=datePublished]").attr("content");
            //评分
            Elements score = document.select(".video_score");
            //播放量
            String interactionCount = document.select("meta[itemprop=interactionCount]").attr("content");
            //电视简介
            String descriptionContent = document.select("meta[name=description]").attr("content");



            dbObject.put("title", tvName);
            dbObject.put("director", director.text());
            dbObject.put("act", actors.text());
            dbObject.put("catalog", description.text());
            dbObject.put("area", contentLocation);
            dbObject.put("year", year);
            dbObject.put("mark", score.text());
            dbObject.put("pv", interactionCount);
            dbObject.put("content", descriptionContent);
            dbObject.put("平台", "腾讯");                                   //平台

           // dataInteraction.updateData(dbObject,dbCollection);
           // System.out.println(dbObject.put("title", tvName));
        }
        System.out.println("插入了第" + sum++ + "次数据");
    }

    public static void main(String[] args) throws IOException {
        int sum=0;
        for (int i = 0; i <= 4980; i += 30) {
            sum++;
            Tencent.getTVData("http://v.qq.com/x/list/tv?&offset=" + i, sum);
          //  Tencent.getTVData("http://v.qq.com/x/list/movie?&offset=" + i, mongoCollection, sum);
        }
    }
}
