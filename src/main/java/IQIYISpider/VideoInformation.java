package IQIYISpider;


import com.google.gson.Gson;
import dao.DataSwitch;
import dao.SendRequestResponse;
import entitys.Aiqiyi;
import entitys.Episode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 获取爱奇艺视频数据类
 * Created by ty on 2017/5/22.
 */
public class VideoInformation {
    /**
     * @param type 电视剧 电影 动漫
     */
    public void getVideoData(String type) {
        //电视剧  page_st=2 from_cid=2
        //电影   page_st=1 from_cid=1
        //动画   page_st=4 from_cid=4  page_st=2&pg_num=%d&from_cid=2
        String tv_url = "http://iface2.iqiyi.com/views/3.0/category_lib?source=NC&from_subtype=-1&from_rpage" +
                "=category_home.2&pg_size=30&secure_p=GPhone&app_k=69842642483add0a63503306d63f0443&app_v" +
                "=8.5.0&platform_id=10&dev_os=4.2.2&dev_ua=HTCX920e&net_sts=1&qyid=864895025135136&cupid_v" +
                "=3.10.005&scrn_scale=2&secure_v=1&dev_hw=['mem':'218.1MB','cpu':" + 0 + ",'gpu':'']&scrn_sts=0&scrn_res" +
                "=720,1280&scrn_dpi=320&province_id=2017&psp_status=1&pg_num=%d";

        String tv = "http://iface2.iqiyi.com/views/3.0/player_tabs?app_k=69842642483add0a63503306d63f0443&app_v=8.5.0&platform_id" +
                "=10&dev_os=4.2.2&dev_ua=HTCX920e&net_sts=1&qyid=864895025135136&cupid_v=3.10.005&scrn_scale=2&secure_p" +
                "=GPhone&secure_v=1&core=1&api_v=5.1&dev_hw=['mem':'217.8MB','cpu':" + 0 + ",'gpu':'']&scrn_sts=0&scrn_res=720,1280&scrn_dpi" +
                "=320&province_id=2017&album_id=%s&page_part=2&dl_res=16,8,4,128,&video_tab=1&psp_status=1";

        if ("电视剧".equals(type)) {
            String url = tv_url + "&page_st=2&from_cid=2";
            runIQiYI(tv, url, type);
        } else if ("电影".equals(type)) {
            String url = tv_url + "&page_st=1&from_cid=1";
            runIQiYI(tv, url, type);
        } else if ("动漫".equals(type)) {
            String url = tv_url + "&page_st=4&from_cid=4";
            runIQiYI(tv, url, type);
        }
    }

    /**
     * @param tv   具体视频的url
     * @param url  视频类型的url(电视剧、电影、动漫)
     * @param type 视频类型的(电视剧、电影、动漫)
     */
    public void runIQiYI(String tv, String url, String type) {
        int i = 0;
        while (true) {
            String tvURL = String.format(url, i);
            i++;
            List<String> ids = getID(tvURL);
            if (ids != null && ids.size() != 0) {
                getInformation(tv, ids, type);
            } else {
                System.out.println("结束");
                break;
            }
        }
    }

    /**
     * 获取一页视频ID（一页30个视频）
     *
     * @param url 视频url
     * @return List<String>
     */
    public List<String> getID(String url) {
        String html = getHtml(url);
        List<String> ids = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject(html);
        JSONArray cards = jsonObject.getJSONArray("cards");
        JSONArray items;
        if (cards != null) {
            if (cards.length() == 3) {
                items = cards.getJSONObject(2).getJSONArray("items");
            } else {
                items = cards.getJSONObject(0).getJSONArray("items");
            }
            for (int i = 0; i < items.length(); i++) {
                String object = items.getJSONObject(i).getString("_id");
                ids.add(object);
            }
            return ids;
        } else {
            System.out.println("插入完毕");
        }
        return null;
    }

    /**
     * 获取一个视频的基本信息（标题 主演。。。）
     *
     * @param url  视频url
     * @param ids  一个页面30个是视频的ID
     * @param type 类型(电影，电视剧，动漫)
     */
    public void getInformation(String url, List<String> ids, String type) {

        JSONArray jsonArray = new JSONArray();

        for (String id : ids) {
            String newURL = String.format(url, id);
            String videoHtml = getHtml(newURL);
            String playHtml = getHtml(getVideo(newURL));                 //播放显示的文本

            JSONObject videoObject = new JSONObject(videoHtml);
            JSONObject playObject = new JSONObject(playHtml);
            JSONObject album = playObject.getJSONObject("album");

            String title = album.getString("_t");                   //标题
            int episodes = album.getInt("_tvs");                    //集数
            int severalEpisodes = album.getInt("p_s");              //更新到多少级
            String content = album.getString("desc");               //电视介绍
            // double score = album.getDouble("_sc");                     //评分
            long pv = album.getLong("vv");                          //播放量

            String[] catalog = album.getString("tag").split("\\s");//类型

            Date date = DataSwitch.getDate(album.getString("cn_year"));   //哪一年出的*/

            JSONArray cards = videoObject.getJSONArray("cards");
            Aiqiyi aiqiyi = new Aiqiyi();
            JSONObject videoDetails = cards.getJSONObject(2);
            JSONObject itemsObject = videoDetails.getJSONArray
                    ("items").getJSONObject(0);
            JSONArray meta = itemsObject.getJSONArray("meta");

            String tv_up = videoDetails.getString("tv_up");             //好看
            String tv_down = videoDetails.getString("tv_down");         //不好看

            long down = DataSwitch.toNum(tv_down);                                                     //不好看
            long up = DataSwitch.toNum(tv_up);                                                         //好看

            aiqiyi.setType(type);
            aiqiyi.setTitle(title);
            aiqiyi.setTv_down(down);
            aiqiyi.setTv_up(up);
            aiqiyi.setPv(pv);
            aiqiyi.setVid(id);
            aiqiyi.setSeveralEpisodes(severalEpisodes);
            aiqiyi.setEpisodes(episodes);
            aiqiyi.setContent(content);
            aiqiyi.setYear(date);
            aiqiyi.setCatalog(catalog);
            aiqiyi.setPlatform("爱奇艺");
            int length = meta.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject1 = meta.getJSONObject(i);
                String text = jsonObject1.getString("text");

                if (text.indexOf("主演") != -1) {
                    String actor = jsonObject1.getJSONObject("extra").getString("characters");
                    JSONArray actors = new JSONArray(actor);
                    aiqiyi.setActs(actors);
                }
                if (text.indexOf("更新") != -1) {
                    aiqiyi.setFixupdate(text);

                }
                if (text.indexOf("影片简介") != -1) {
                    aiqiyi.setContent(text);
                    //  System.out.println(text);
                }
                if (text.indexOf("本集简介") != -1) {
                    List<Episode> episode = EachContent(id, severalEpisodes);
                    aiqiyi.setEachIntroduce(episode);
                }
                if (text.indexOf("评分") != -1) {
                    String score = jsonObject1.getJSONObject("extra").getString("qy_score");
                    aiqiyi.setScore(Float.parseFloat(score));
                }
                if (text.indexOf("导演") != -1) {
                    String[] director = text.replace("导演: ", "").split("\\s");
                    aiqiyi.setDirector(director);
                }
            }
            String videos = new Gson().toJson(aiqiyi);
            jsonArray.put(videos);

            new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/tv/updateVideo?", jsonArray);
        }
    }


    /**
     * 获得一部电视剧每集基本信息
     *
     * @param severalEpisodes 更新至多少集
     * @return List<Episode>
     */
    public List<Episode> EachContent(String tid, int severalEpisodes) {

        String url = "http://iface2.iqiyi.com/views/3.0/card_view?page=player_tabs&fake_ids" +
                "=choose_set&plt_full=1&full=1&secure_p=GPhone&api_v=5.1&video_tab=1&wts=-1&app_k=69842642483add0a63503306d63f0443&app_v" +
                "=8.5.0&platform_id=10&dev_os=4.2.2&dev_ua=HTCX920e&net_sts=1&qyid=864895025135136&scrn_scale=2&secure_v=1&core=1&api_v" +
                "=5.1&profile=['group':'2','counter':" + 2 + "]&dev_hw=['mem':'213.2MB','cpu':" + 0 + ",'gpu':'']&scrn_sts=0&scrn_res" +
                "=720,1280&scrn_dpi=320&province_id=2017&psp_status=1";
        String html = getHtml(url + "&album_id=" + tid);
        JSONObject jsonObject = new JSONObject(html);
        JSONObject data = jsonObject.getJSONArray("cards").getJSONObject(0).getJSONObject("data");
        Iterator<String> ke = data.keys();
        List<Episode> contents = new ArrayList<Episode>();
        while (ke.hasNext()) {
            JSONObject keys = data.getJSONObject(ke.next());
            int order = keys.getInt("order");                           //第几集
            if (order <= severalEpisodes) {
                String eachId = keys.getString("_id");                  //id
                String content = getEachIntroduce(tid, eachId);              //本集简介
                JSONObject other = keys.getJSONObject("other");
                int duration = other.getInt("duration");                //时长
                Episode episode = new Episode(eachId,duration,order,content);
              /*  episode.setEachID(eachId);
                episode.setEpisodeNO(order);
                episode.setIntroduce(content);
                episode.setDurationSecond(duration);*/
                contents.add(episode);
            }
        }
        return contents;
    }

    /**
     * 获得本集的简介
     *
     * @param tid    具体一个连续剧的id eachId 每集的id
     * @param eachId
     * @return String
     */
    public String getEachIntroduce(String tid, String eachId) {

        String address = "http://iface2.iqiyi.com/views/3.0/player_tabs?app_k=69842642483add0a63503306d63f0443&app_v=8.5.0&platform_id" +
                "=10&dev_os=4.2.2&dev_ua=HTCX920e&net_sts=1&qyid=864895025135136&cupid_v=3.10.005&scrn_scale=2&secure_p" +
                "=GPhone&secure_v=1&core=1&api_v=5.1&dev_hw=['mem':'217.8MB','cpu':" + 0 + ",'gpu':'']&scrn_sts=0&scrn_res=720,1280&scrn_dpi" +
                "=320&province_id=2017&page_part=2&dl_res=16,8,4,128,&video_tab=1&psp_status=1&";

        String _id = "tv_id=" + eachId;
        String album_id = "album_id=" + tid + "&";
        String url = address + album_id + _id;
        String html = getHtml(url);
        JSONObject jsonObject = new JSONObject(html);
        JSONArray meta = jsonObject.getJSONArray("cards").getJSONObject(2).getJSONArray("items").
                getJSONObject(0).getJSONArray("meta");
        for (int i = 0; i < meta.length(); i++) {
            String test = meta.getJSONObject(i).getString("text");
            if (test.indexOf("本集简介") >= 0) {
                return test;
            }
        }
        return null;
    }

    public String getVideo(String url) {

        return url.replace("views/3.0/player_tabs?", "video/3.0/v_play?app_t=0&");
    }

    /**
     * 获取页面全部内容
     *
     * @param url 视频url
     * @return String
     */
    public static String getHtml(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("sign", "03e57fc4126df8a090fec64a3f565bd4");
        httpGet.setHeader("t", "461033689");
        HttpResponse response;
        String html = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    html = EntityUtils.toString(entity, "utf-8");
                }
            }
        } catch (IOException e) {
            System.out.println("获取URL内容失败");
        }
        return html;
    }
}
