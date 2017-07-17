package entitys;

import java.util.Date;

/**
 * Created by ty on 2017/6/8.
 */
public class Episode {
    private String eachID;//视频id
    private Date createTime;//创建时间
    private int durationSecond;//时长
    private long pv;//播放量
    private int episodeNO;//集数
    private String introduce;//本集简介

    /**
     * @param eachID
     * @param createTime
     * @param durationSecond
     * @param pv
     * @param episodeNO
     * @param introduce
     */
    public Episode(String eachID, Date createTime, int
            durationSecond, long pv, int episodeNO, String introduce) {
        this.eachID = eachID;
        this.createTime = createTime;
        this.durationSecond = durationSecond;
        this.pv = pv;
        this.episodeNO = episodeNO;
        this.introduce = introduce;
    }

    /**
     * 爱奇艺
     *
     * @param eachID
     * @param durationSecond
     * @param episodeNO
     * @param introduce
     */
    public Episode(String eachID, int
            durationSecond, int episodeNO, String introduce) {
        this.eachID = eachID;
        this.durationSecond = durationSecond;
        this.episodeNO = episodeNO;
        this.introduce = introduce;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "eachID='" + eachID + '\'' +
                ", durationSecond=" + durationSecond +
                ", episodeNO=" + episodeNO +
                ", introduce='" + introduce + '\'' +
                '}';
    }

    public Episode() {
    }

    public String getEachID() {
        return eachID;
    }

    public void setEachID(String eachID) {
        this.eachID = eachID;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getDurationSecond() {
        return durationSecond;
    }

    public void setDurationSecond(int durationSecond) {
        this.durationSecond = durationSecond;
    }

    public long getPv() {
        return pv;
    }

    public void setPv(long pv) {
        this.pv = pv;
    }

    public int getEpisodeNO() {
        return episodeNO;
    }

    public void setEpisodeNO(int episodeNO) {
        this.episodeNO = episodeNO;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
}
