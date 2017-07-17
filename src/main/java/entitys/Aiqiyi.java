package entitys;

import org.json.JSONArray;

import java.util.Date;
import java.util.List;

/**
 * Created by ty on 2017/6/23.
 */
public class Aiqiyi {

    private String title;                    //视频名字
    private String vid;                      //视频ID
    private int episodes;                    //集数
    private String fixupdate;                //更新
    private int severalEpisodes;             //更新至几级
    private List<Episode> eachIntroduce;     //一部电视剧每集介绍
    private String[] director;               //导演
    private JSONArray acts;                   //主演
    private String type;                     //分类
    private String[] catalog;                //类型

    private Date year;                        //哪一年出的
    private float score;                     //评分
    private long pv;                         //播放量
    private String content;                  //视频简介
    private String platform;                 //平台

    private double tv_up;                    //好看
    private double tv_down;                  //不好看

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public String getFixupdate() {
        return fixupdate;
    }

    public void setFixupdate(String fixupdate) {
        this.fixupdate = fixupdate;
    }

    public int getSeveralEpisodes() {
        return severalEpisodes;
    }

    public void setSeveralEpisodes(int severalEpisodes) {
        this.severalEpisodes = severalEpisodes;
    }

    public List<Episode> getEachIntroduce() {
        return eachIntroduce;
    }

    public void setEachIntroduce(List<Episode> eachIntroduce) {
        this.eachIntroduce = eachIntroduce;
    }

    public String[] getDirector() {
        return director;
    }

    public void setDirector(String[] director) {
        this.director = director;
    }

    public JSONArray getActs() {
        return acts;
    }

    public void setActs(JSONArray acts) {
        this.acts = acts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getCatalog() {
        return catalog;
    }

    public void setCatalog(String[] catalog) {
        this.catalog = catalog;
    }

    public Date getYear() {
        return year;
    }

    public void setYear(Date year) {
        this.year = year;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getPv() {
        return pv;
    }

    public void setPv(long pv) {
        this.pv = pv;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public double getTv_up() {
        return tv_up;
    }

    public void setTv_up(double tv_up) {
        this.tv_up = tv_up;
    }

    public double getTv_down() {
        return tv_down;
    }

    public void setTv_down(double tv_down) {
        this.tv_down = tv_down;
    }
}
