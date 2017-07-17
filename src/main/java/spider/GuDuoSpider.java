package spider;

import dao.SendRequestResponse;
import log4j.Log;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by ty on 2017/6/16.
 */
public class GuDuoSpider {

    /**
     * 获取具体的骨朵艺人的基本信息和发送post请求获得数据
     */
    public void getStarMessage() {
        Log log = Log.getLogger();
        for (int i = 1; i < 1500; i++) {
            JSONObject actor = new JSONObject();
            JSONObject ids = new JSONObject();
            String html = new SendRequestResponse().getHtml("https://d.guduomedia.com/m/actor/actorInfo?actor_id" +
                    "=" + i);
            if (html.indexOf("出错") < 0) {
                Document document = Jsoup.parse(html);
                String name = document.select("#actor_name").text();
                String img = document.select(".photo>img").get(0).attr("src");
                if (name.length() > 0) {
                    actor.put("name", name);
                    actor.put("avatar", img);
                    ids.put("guduo", i);
                    actor.put("ids", ids);
                    Elements elements = document.select("div[class=basic]>ul>li");
                    for (int j = 0; j < elements.size(); j++) {
                        String key = elements.get(j).select("span").get(0).text();
                        String value = elements.get(j).select("span").get(1).text();
                        if (key.equals("身高："))
                            actor.put("height", value);
                        else if (key.equals("体重："))
                            actor.put("weight", value);
                        else if (key.equals("出生地："))
                            actor.put("placeOfBirth", value);
                        else if (key.equals("毕业学校："))
                            actor.put("school", value);
                        else if (key.equals("经纪公司："))
                            actor.put("agent", value);
                    }
                   // new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/star/updateStar?", actor);
                }
            }
        }
    }
}


