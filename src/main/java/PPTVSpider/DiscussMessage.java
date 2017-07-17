package PPTVSpider;

import com.google.gson.Gson;
import dao.SendRequestResponse;
import entitys.Discuss;
import entitys.Replier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by ty on 2017/6/1.
 */
public class DiscussMessage {

    /**
     * @param disussID 具体一个电视剧所有id(连续剧 电影 动漫)
     * @param videoID  具体一个视频的id
     */
    public void getDiscussData(List<String> disussID, String videoID) {
        String url = "http://apicdn.sc.pptv.com/sc/v3/pplive/ref/vod_%d/" +
                "combine/feed/list?appid=com.pplive.androidphone&appver=6.4.3&appplt=aph&ps=10&isShowTop=1";
        for (String ID : disussID) {

            contents(ID, videoID);

        }
    }

    /**
     * @param discussID 第一页评论的id
     * @param videoID   具体一个视频的id
     */
    public void contents(String discussID, String videoID) {
        Object timeStamp = "";

        boolean hasNext = true;
        do {
            timeStamp = getCommentsOnePage(timeStamp, discussID, videoID);
            // System.out.println(timeStamp);
        } while (hasNext && timeStamp != null);

    }

    /**
     * 获取当页评论的内容
     *
     * @param timeStamp 时间戳
     * @param discussID 当页评论id
     * @param videoID   当前视频的id
     * @return Object
     */
    private Object getCommentsOnePage(Object timeStamp, String discussID, String videoID) {
        String url = "http://apicdn.sc.pptv.com/sc/v3/pplive/ref/vod_%s/combine/feed/list?appid" +
                "=com.pplive.androidphone&appver=6.4.3&appplt=aph&ps=10&isShowTop=1";
        if (timeStamp != null)
            url += "&nt=" + timeStamp;
        JSONArray discussArray = new JSONArray();
        String html = new VideoInformation().getHtml(String.format(url, discussID));
        JSONObject jsonObject = new JSONObject(html);
        JSONArray list = jsonObject.getJSONObject("data").getJSONObject("newList").getJSONArray("list");
        int length = list.length();
        for (int i = 0; i < length; i++) {
            JSONObject replyObject = list.getJSONObject(i);
            List<Replier> replyArray = new ArrayList<Replier>();
            long id = replyObject.getLong("id");                                         //评论人ID
            String nick_nam = replyObject.getJSONObject("user").getString("nick_name"); //评论人名字
            String icon = replyObject.getJSONObject("user").getString("icon");          //评论人头像地址
            JSONArray replys = replyObject.getJSONArray("replys");                      //回复人信息
            if (replys.length() > 0) {
                for (int j = 0; j < replys.length(); j++) {
                    JSONObject reply = replys.getJSONObject(j);
                    long replyObjectID = reply.getLong("pid");
                    long replyID = reply.getLong("id");
                    String replyName = reply.getJSONObject("user").getString("nick_name");
                    String replyContent = reply.getString("content");
                    long create_time = reply.getLong("create_time");
                    Replier replier = new Replier(replyObjectID, replyID, replyName, replyContent, create_time);
                    replyArray.add(replier);
                }
            }
            long create_time = replyObject.getLong("create_time");                      //时间戳
            int dianzan = replyObject.getInt("up_ct");                                  //点赞
            String content = replyObject.getString("content");                          //评论内容
            Discuss discuss = new Discuss(videoID, id, nick_nam, replyArray, dianzan,
                    icon, content, "PPTV", create_time);
            String discus = new Gson().toJson(discuss);
            discussArray.put(discus);

        }
        timeStamp = jsonObject.getJSONObject("data").getJSONObject("newList").get("nt");
        try {
            new SendRequestResponse().sendPostQequest("http://127.0.0.1:3000/tv/updateDiscuss?", discussArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (discussArray.length() == 0) {
            return null;
        }
        return timeStamp;
    }
}
