package spider;


import dao.SendRequestResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.bind.SchemaOutputResolver;

/**
 * Created by ty on 2017/6/28.
 */
public class WeiBoAttention {


    public void Attentions(long id, int sum, String gisd, String s) {
        //_2A250UnVyDeTxGeNJ7lsZ-SfMzTuIHXVVRo-6rDV6PUJbkdAKLRblkWqRFpznqVLT-Q_jCbw7uQIF62ZOuQ..
        String url = "https://api.weibo.cn/2/friendships/create?uid=%d&networktype=wifi&lang=zh_CN&gsid=" + gisd + "&trim=1&v_f=2&c=android&s=" + s;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(String.format(url, id));
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
        httpPost.setConfig(requestConfig);
        HttpResponse response;
        String html;
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    html = EntityUtils.toString(entity, "utf-8");
                    System.out.println(html);
                    JSONObject result = new JSONObject(html);
                    if (result.getBoolean("result") == true) {
                        System.out.println("成功关注第" + sum + "记录");
                    } else {
                        System.out.println("关注失败" + id);
                    }
                }
            }
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取URL内容失败");
        }
    }


    public String getHtml(int page, String gisd, String s) {
        //_2A250UnVyDeTxGeNJ7lsZ-SfMzTuIHXVVRo-6rDV6PUJbkdAKLRblkWqRFpznqVLT-Q_jCbw7uQIF62ZOuQ..
        String url = "https://api.weibo.cn/2/cardlist?count=20&from=1075095010&networktype" +
                "=wifi&lang=zh_CN&gsid=" + gisd + "&page=" + page + "&containerid=231093_-_selffollowed&c" +
                "=android&wm=2468_1001&s=" + s;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Host", "api.weibo.cn");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("User-Agent", "HTC X920e_4.2.2_weibo_7.5.0_android");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate");
        HttpResponse response;
        String html;
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    html = EntityUtils.toString(entity, "utf-8");
                    return html;
                }
            }
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取URL内容失败");
        }
        return null;
    }

   /* */

    /**
     * 读取文件获取明星id
     *
     * @return
     */
    public List<Long> readFile() {
        StringBuffer stringBuffer = new StringBuffer();
        List<Long> list = new ArrayList<Long>();
        try {
            FileInputStream file = new FileInputStream("../tigerMovieSpider/微博待关注明星.txt");
            InputStreamReader reader = new InputStreamReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(stringBuffer.toString());
            while (matcher.find()) {
                list.add(Long.parseLong(matcher.group()));
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return
     */
    public JSONArray getAllNoAttention() {
        JSONArray jsonArray = new SendRequestResponse().sendGetQequest("http://127.0.0.1:3000/star/tenAllNoAttentionStar?");
        return jsonArray;
    }

    /**
     * 全部明星id
     *
     * @return
     */
    public JSONObject getAStar(int record) {
        JSONArray jsonArray = new SendRequestResponse().sendGetQequest("http://127.0.0.1:3000/star/aStar?record=" + record);
        if (jsonArray.length() == 0 || jsonArray == null) {
            return null;
        }
        return jsonArray.getJSONObject(0);
    }

    /**
     * 没有被当前长账号关注的明星
     *
     * @param uid 账号id
     */
    public JSONArray getNoAttention(String uid) {
        JSONArray jsonArray = new SendRequestResponse().sendGetQequest("http://127.0.0.1:3000/star/noAttentionStar?sid=" + uid);
        return jsonArray;
    }

    /**
     * 单前账号已关注的明星id
     *
     * @param uid
     */
    public JSONArray getAttention(String uid) {
        JSONArray jsonArray = new SendRequestResponse().
                sendGetQequest("http://127.0.0.1:3000/star/nowAttentionStar?sid=" + uid + "&execute=1");
        return jsonArray;
    }

    /**
     * 关注明星
     * 明星第一次被关注
     *
     * @param
     */
    public void sendAttention(String sid, String gisd, String s) throws InterruptedException {
        JSONArray allAttention = getAllNoAttention();
        if (allAttention == null || allAttention.length() == 0) {
            JSONArray partNoAttention = getNoAttention(sid);   //未关注的  3000
            System.out.println(partNoAttention);
            Thread.sleep(120000);
            JSONArray partAttention = getAttention(sid);       //关注的    1000
            int record = partAttention.getJSONObject(partAttention.length() - 1).getInt("record"); //单前关注最大的数
            int now = 0;
            for (int j = 0; j < partNoAttention.length(); j++) {
                int aa = partNoAttention.getJSONObject(j).getInt("record");
                if (record + 1 < 4347) {
                    if (record + 1 == aa) {
                        now = j;
                    }
                } else {
                    now = 1;
                }
            }
            int discrepancy = partNoAttention.length() - now; //两数相差
            if (discrepancy >= 75) {
                //   int sum = 1;
                for (int i = now; i < 75 + now; i++) {
                    long uid = partNoAttention.getJSONObject(i).getLong("uid");
                    //   System.out.println(uid + "=======" + i + "=========" + sum++);
                    new WeiBoAttention().Attentions(uid, i, gisd, s);
                    Thread.sleep(2000);
                }     //3000-2900=10

            } else if (discrepancy < 75) {
                // int sum = 0;
                for (int i = now; i < discrepancy; i++) {
                    long uid = partNoAttention.getJSONObject(i).getLong("uid");
                    new WeiBoAttention().Attentions(uid, i + 1, gisd, s);
                    // System.out.println(uid + "=======" + i + "=========" + sum++);
                    Thread.sleep(2000);
                }
                for (int i = 0; i < 75 - discrepancy; i++) {
                    long uid = partNoAttention.getJSONObject(i).getLong("uid");
                    new WeiBoAttention().Attentions(uid, i + 1, gisd, s);
                    // System.out.println(uid + "=======" + sum + "=========" + sum++);
                    // System.out.println(95);
                    Thread.sleep(2000);
                }
            }
            getStarId(sid, gisd, s);
        } else {
            for (int n = 0; n < allAttention.length(); n++) {
                long uid = allAttention.getJSONObject(n).getLong("uid");
                if (n >= 0 && n < 75) {
                    new WeiBoAttention().Attentions(uid, n + 1, gisd, s);
                    Thread.sleep(2000);
                }
            }
            getStarId(sid, gisd, s);
        }

    }

    /**
     * 获取关注成功的id
     *
     * @param sid 用户id
     * @throws InterruptedException
     */

    public void getStarId(String sid, String gisd, String s) throws InterruptedException {
        //遍历获取关注id
        int sum = 1;
        for (int i = 1; i <= 4; i++) {
            String html = new WeiBoAttention().getHtml(i, gisd, s);
            JSONObject jsonObject = new JSONObject(html);
            if (jsonObject.length() == 1)
                return;
            JSONArray cards = jsonObject.getJSONArray("cards");

            if (cards.length() == 1) {
                JSONArray jsonArray = cards.getJSONObject(0).getJSONArray("card_group");
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject attentions = new JSONObject();
                    attentions.put(sid, true);
                    long id = jsonArray.getJSONObject(j).getJSONObject("user").getLong("id");
                    attentions.put("uid", id);
                    attentions.put("attention", true);
                    new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/star/attentionStar?", attentions);
                    System.out.println(attentions + "====" + sum++);
                    Thread.sleep(100);
                }
            } else if (cards.length() == 2) {
                JSONArray jsonArray = jsonObject.getJSONArray("cards").getJSONObject(1).getJSONArray("card_group");
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject attentions = new JSONObject();
                    attentions.put(sid, true);
                    long id = jsonArray.getJSONObject(j).getJSONObject("user").getLong("id");
                    attentions.put("uid", id);
                    attentions.put("attention", true);
                    new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/star/attentionStar?", attentions);
                    System.out.println(attentions + "====" + sum++);
                    Thread.sleep(100);
                }
            } else if (cards.length() == 3) {
                JSONArray jsonArray = jsonObject.getJSONArray("cards").getJSONObject(2).getJSONArray("card_group");
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject attentions = new JSONObject();
                    attentions.put(sid, true);
                    long id = jsonArray.getJSONObject(j).getJSONObject("user").getLong("id");
                    attentions.put("uid", id);
                    attentions.put("attention", true);
                    new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/star/attentionStar?", attentions);
                    System.out.println(attentions + "====" + sum++);
                    Thread.sleep(100);
                }
            }
            Thread.sleep(4000);
        }
    }

    public void code(String uid, String gisd, String s) {

        String url = "https://api.weibo.cn/2/account/login?v_f=2&moduleID=701&wm=90027_90002&networktype=wifi&s=" + s +
                "&gsid=" + gisd;
        boolean result = new SendRequestResponse().sendPostQequest(url);
        if (!result) {
            System.out.println(uid + "登入失败");
        } else {
            System.out.println(uid + "登入成功");
        }

    }


    public void runTenStar() throws InterruptedException {

       /* code("5612953946", "_2A250YFf6DeTxGeNI6lAY9S3FzzqIHXVVT3G5rDV6PUJbkdAKLWHRkWqaacT1iy2J-OtA8islFEYdpYMYdw..", "66cff4b0");
        Thread.sleep(2000);
        code("5606426713", "_2A250Wdk4DeTxGeNI61QV8ijLyj-IHXVVTdFcrDV6PUJbkdAKLRLykWoFjx9vAcMZiHEWu37HjrvAOJhhpQ..", "311dd2f1");
        Thread.sleep(2000);
        code("5779402132", "_2A250adauDeTxGeNJ7FsV8CzNyD6IHXVVP21mrDV6PUJbkdAKLXHskWoZaefgeE5gYyJ4NXbpwoXspgjOCQ..", "e24cac07");
        Thread.sleep(2000);
        code("5606361948", "_2A250WepdDeTxGeNI61QS9i_FzzSIHXVVT3qVrDV6PUJbkdANLWegkWp9uBh_FiXiJOO5BwCLnqGj38ZXuQ..", "9432cf6a");
        Thread.sleep(2000);
        code("5612734149", "_2A250ae7aDeTxGeNI6lAW8yrNzzWIHXVVP2USrDV6PUJbkdANLUaskWo4e_PQoZC1myTuYeodUtzv7-GiYg..", "27750d24");
        Thread.sleep(2000);
        //code("5773073053", "_2A250ZpCxDeTxGeNJ7FER9y3Mzj-IHXVVTTaerDV6PUJbkdANLULkkWpeqfRBNdEn4whVkQtSIMKALroheA..", "18bba3d5");
       // Thread.sleep(2000);
        code("5778135054", "_2A250aeyeDeTxGeNJ7FoQ8yvMzjiIHXVVTCVGrDV6PUJbkdANLRD8kWqADb6P_1KSlm7dOFD1uPnnSGF76Q..", "517230c1");
        Thread.sleep(12000);*/

      /*  new WeiBoAttention().sendAttention("5612953946",
                "_2A250YFf6DeTxGeNI6lAY9S3FzzqIHXVVT3G5rDV6PUJbkdAKLWHRkWqaacT1iy2J-OtA8islFEYdpYMYdw..", "66cff4b0");
*/
        // new WeiBoAttention().sendAttention("5777882736",
        //         "_2A250WoLRDeTxGeNJ7FUZ-CzLyDqIHXVVT30VrDV6PUJbkdAKLW3akWplwIaide8MuRl7Sys2_Qde3Zjd8g..", "7b3ed342");

        new WeiBoAttention().sendAttention("5606426713",
                "_2A250Wdk4DeTxGeNI61QV8ijLyj-IHXVVTdFcrDV6PUJbkdAKLRLykWoFjx9vAcMZiHEWu37HjrvAOJhhpQ..", "311dd2f1");

        new WeiBoAttention().sendAttention("5779402132",
                "_2A250adauDeTxGeNJ7FsV8CzNyD6IHXVVP21mrDV6PUJbkdAKLXHskWoZaefgeE5gYyJ4NXbpwoXspgjOCQ..", "e24cac07");

        //new WeiBoAttention().sendAttention("5769060256",
        //        "_2A250WduGDeTxGeNJ7VsR9i7OzjqIHXVVTcr7rDV6PUJbkdAKLW7ckWpjMwv55LPcrBGdqS1RDFnvwlpXfQ..", "981d0d69");

     /*   new WeiBoAttention().sendAttention("5606361948",
                "_2A250WepdDeTxGeNI61QS9i_FzzSIHXVVT3qVrDV6PUJbkdANLWegkWp9uBh_FiXiJOO5BwCLnqGj38ZXuQ..", "9432cf6a");

        new WeiBoAttention().sendAttention("5612734149",
                "_2A250ae7aDeTxGeNI6lAW8yrNzzWIHXVVP2USrDV6PUJbkdANLUaskWo4e_PQoZC1myTuYeodUtzv7-GiYg..", "27750d24");
*/
        //  new WeiBoAttention().sendAttention("5773073053",
        //        "_2A250ZpCxDeTxGeNJ7FER9y3Mzj-IHXVVTTaerDV6PUJbkdANLULkkWpeqfRBNdEn4whVkQtSIMKALroheA..", "18bba3d5");

        // new WeiBoAttention().sendAttention("5612740918",
        //          "_2A250Xz-oDeTxGeNI6lAW9C7FyjSIHXVVTTRgrDV6PUJbkdAKLUHAkWqCk0ekdDidegqupKiX6VO482jayA..", "00ed022b");
/*
        new WeiBoAttention().sendAttention("5778135054",
                "_2A250aeyeDeTxGeNJ7FoQ8yvMzjiIHXVVTCVGrDV6PUJbkdANLRD8kWqADb6P_1KSlm7dOFD1uPnnSGF76Q..", "517230c1");*/
    }

    public static void main(String[] args) throws ParseException, InterruptedException {


        try {
            new WeiBoAttention().runTenStar();
            // new WeiBoAttention().getStarId("5773073053", "_2A250ZpCxDeTxGeNJ7FER9y3Mzj-IHXVVTTaerDV6PUJbkdANLULkkWpeqfRBNdEn4whVkQtSIMKALroheA..", "18bba3d5", 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
       /* Set<Integer> set = new HashSet<Integer>();
        List<Integer> list = new ArrayList<Integer>();
        JSONArray jsonArray1 = new WeiBoAttention().getAttention("5612953946");
        JSONArray jsonArray2 = new WeiBoAttention().getAttention("5606426713");
        JSONArray jsonArray3 = new WeiBoAttention().getAttention("5779402132");
        JSONArray jsonArray4 = new WeiBoAttention().getAttention("5606361948");
        JSONArray jsonArray5 = new WeiBoAttention().getAttention("5612734149");
        JSONArray jsonArray6 = new WeiBoAttention().getAttention("5778135054");
        for (int i = 0; i < jsonArray1.length(); i++) {
            set.add(jsonArray1.getJSONObject(i).getInt("record"));
        }
        for (int i = 0; i < jsonArray2.length(); i++) {
            set.add(jsonArray2.getJSONObject(i).getInt("record"));
        }
        for (int i = 0; i < jsonArray3.length(); i++) {
            set.add(jsonArray3.getJSONObject(i).getInt("record"));
        }
        for (int i = 0; i < jsonArray4.length(); i++) {
            set.add(jsonArray4.getJSONObject(i).getInt("record"));
        }
        for (int i = 0; i < jsonArray5.length(); i++) {
            set.add(jsonArray5.getJSONObject(i).getInt("record"));
        }
        for (int i = 0; i < jsonArray6.length(); i++) {
            set.add(jsonArray6.getJSONObject(i).getInt("record"));
        }
        int sum = 1;
        for (int a : set) {
            while (a > sum) {
                list.add(sum);
                sum++;
            }
            sum++;
        }
        for (int a : list) {
            System.out.println(a);

        }
        System.out.println(list.size());

        for (int i = 0; i < list.size(); i++) {
            if (i >= 0 && i <= 35) {
                JSONObject jsonObject = new WeiBoAttention().getAStar(list.get(i));
                if (jsonObject == null) {
                    continue;
                }
                long uid = jsonObject.getLong("uid");
                System.out.println(uid);
                new WeiBoAttention().Attentions(uid, i,
                        "_2A250YFf6DeTxGeNI6lAY9S3FzzqIHXVVT3G5rDV6PUJbkdAKLWHRkWqaacT1iy2J-OtA8islFEYdpYMYdw..", "66cff4b0");
                Thread.sleep(2000);
            } else if (i >= 0 && i <= 35) {
                JSONObject jsonObject = new WeiBoAttention().getAStar(list.get(i));
                if (jsonObject == null) {
                    continue;
                }
                long uid = jsonObject.getLong("uid");
                System.out.println(uid);
                new WeiBoAttention().Attentions(uid, i,
                        "_2A250Wdk4DeTxGeNI61QV8ijLyj-IHXVVTdFcrDV6PUJbkdAKLRLykWoFjx9vAcMZiHEWu37HjrvAOJhhpQ..", "311dd2f1");
                Thread.sleep(2000);
            } else if (i >= 0 && i <= 35) {
                JSONObject jsonObject = new WeiBoAttention().getAStar(list.get(i));
                if (jsonObject == null) {
                    continue;
                }
                long uid = jsonObject.getLong("uid");
                System.out.println(uid);
                new WeiBoAttention().Attentions(uid, i,
                        "_2A250Ya6oDeTxGeNJ7FsV8CzNyD6IHXVVMZhSrDV6PUJbkdANLWL7kWpRAkym0Eu51nmyV77tZugnhKwFHg..", "e24cac07");
                Thread.sleep(2000);
            } else if (i >= 0 && i <= 35) {
                JSONObject jsonObject = new WeiBoAttention().getAStar(list.get(i));
                if (jsonObject == null) {
                    continue;
                }
                long uid = jsonObject.getLong("uid");
                System.out.println(uid);
                new WeiBoAttention().Attentions(uid, i,
                        "_2A250WepdDeTxGeNI61QS9i_FzzSIHXVVT3qVrDV6PUJbkdANLWegkWp9uBh_FiXiJOO5BwCLnqGj38ZXuQ..", "9432cf6a");
                Thread.sleep(2000);
            } else if (i >= 0 && i <= 35) {
                JSONObject jsonObject = new WeiBoAttention().getAStar(list.get(i));
                if (jsonObject == null) {
                    continue;
                }
                long uid = jsonObject.getLong("uid");
                System.out.println(uid);
                new WeiBoAttention().Attentions(uid, i,
                        "_2A250Wd3yDeTxGeNI6lAW8yrNzzWIHXVVTcjcrDV6PUJbkdAKLUGikWoQftUADwxMJpaIwN97YYCdLtLsog..", "27750d24");
                Thread.sleep(2000);*//**//**//**//*
                if (i >= 0 && i <= 20) {
                    JSONObject jsonObject = new WeiBoAttention().getAStar(list.get(i));
                    if (jsonObject == null) {
                        continue;
                    }
                    long uid = jsonObject.getLong("uid");
                    System.out.println(uid);
                    new WeiBoAttention().Attentions(uid, i,
                            "_2A250YFnlDeTxGeNJ7FoQ8yvMzjiIHXVVTCVGrDV6PUJbkdANLRD8kWpAqod7yK_98zmw8hR2XiitGJwHmQ..", "517230c1");
                    Thread.sleep(2000);
                }
            }

        }
    }
}*/

 /*  List<Long> list = new WeiBoAttention().readFile();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uid", list.get(i));
            jsonObject.put("attention", false);
            jsonObject.put("record", i + 1);
            new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/star/attentionStar?",jsonObject);
       }
*/