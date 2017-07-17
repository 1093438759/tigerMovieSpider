package spider;

import dao.DataSwitch;
import dao.SendRequestResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

/**
 * 获取百晓生数据的一个类
 * Created by ty on 2017/6/19.
 */
public class BaixsSpider {

    /**
     * 获取具体艺人的基本信息
     */
    public void getActor() {
        for (int i = 1; i < 24000; i++) {
            String html = getCookieHtml("http://v1.moonpool.com.cn/ios/actorInfo/queryStarById", i);
            if (html != null) {
                JSONObject jsonObject = new JSONObject(html);
                JSONObject data = jsonObject.getJSONObject("data");
                if (data == null) {
                    return;
                }
                JSONObject stars = new JSONObject();
                JSONObject ids = new JSONObject();
                ids.put("Baixs", i);                                         //id
                String name = data.getString("name");                   //名字

                stars.put("ids", ids);
                stars.put("name", name);

                if (data.has("introduction")) {
                    String introduce = data.getString("introduction");      //介绍
                    stars.put("introduce", introduce);
                }
                if (data.has("occupation ")) {
                    String section = data.getString("occupation");          //职业
                    stars.put("section", section);
                }
                if (data.has("label")) {
                    String feature = data.getString("label");           //特点
                    stars.put("feature", feature.split(","));
                }

                if (data.has("born")) {
                    String placeOfBirth = data.getString("born");        //出生地
                    stars.put("placeOfBirth", placeOfBirth);
                }

                if (data.has("school")) {
                    String school = data.getString("school");           //毕业院校
                    stars.put("school", school);

                }

                if (data.has("birthday")) {
                    String birthday = data.getString("birthday");       //生日
                    Date date = DataSwitch.getDate(birthday);
                    if (date != null)
                        stars.put("dateOfBirth", date);
                    System.out.println(date);
                }

                if (data.has("height")) {
                    String height = data.getString("height");           //身高
                    stars.put("height", height);

                }
                if (data.has("weight")) {
                    String weight = data.getString("weight");           //体重
                    stars.put("weight", weight);
                }
              /*  if (data.has("awards")) {
                    String[] awards = data.getString("awards").split("  ");     //获奖
                    stars.put("awards", awards);
                }*/
                if (data.has("brokeragefirm")) {
                    String brokeragefirm = data.getString("brokeragefirm"); //经纪公司
                    stars.put("agent", brokeragefirm);
                }
                if (data.has("nation")) {
                    String nation = data.getString("nation");               //名族
                    stars.put("nation", nation);
                }
                if (data.has("imageurl")) {
                    String imageurl = data.getString("imageurl");        //头像
                    stars.put("avatar", imageurl);
                }
                new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/star/updateStar?", stars);
            }

        }
        System.out.println("插入完毕");
    }

    /**
     * 百晓生艺人
     *
     * @param url 登入百晓生之后艺人的url
     * @param id  艺人id
     * @return String
     */
    public String getCookieHtml(String url, int id) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        /*    HttpHost proxy = new HttpHost("192.168.2.183", 8888,"http");
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            httpPost.setConfig(config);
    */
       // httpPost.setHeader("Accept-Encoding", "gzip, deflate");
        httpPost.setHeader("Referer", "http://v1.moonpool.com.cn/ios/forward?");
        httpPost.setHeader("Origin", "http://v1.moonpool.com.cn");
        httpPost.setHeader("Cookie", "JSESSIONID=E243A33A637651F3334C011E8634ECD9");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        String name = "quest={starid:'" + id + "',tab_occupation:'star'}&clientToken=7ff33c8e-6fa9-4425-b8a3-55d6ae05f015_" +
                "1497607631424_22747e8f-1d95-499d-9952-7c872c9bc7f9";
        StringEntity entity = new StringEntity(name.toString(), "utf-8");
        httpPost.setEntity(entity);
        String content = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                content = EntityUtils.toString(responseEntity, "UTF-8");
                int length = content.length();
                if (length < 80)
                    return null;
            }
            httpClient.close();

        } catch (IOException e) {
            System.out.println("发送失败");
        }
        return content;
    }
}