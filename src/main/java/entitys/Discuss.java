package entitys;

import java.util.List;

/**
 * Created by ty on 2017/6/2.
 */
public class Discuss {

    private String vid;                               //  视频id
    private long id;                                  //  评论ID*/
    private String name;                              //  评论客户名
    private List<Replier> replys;                         //  评论的回复  3.1：回复的客户名内容
    private int dianzan;                              //  点赞
    private String icon;                              //  头像地址 （可要可不要
    private String content;                           //  评论内容
    private String platform;                          //  平台
    private long create_time;                         //  创建评论的时间（过去多久）时间戳

    public Discuss(String vid, long id, String name, List<Replier> replys, int dianzan,
                   String icon, String content, String platform, long create_time) {
        this.vid = vid;
        this.id = id;
        this.name = name;
        this.replys = replys;
        this.dianzan = dianzan;
        this.icon = icon;
        this.content = content;
        this.platform = platform;
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "Discuss{" +
                ", vid='" + vid + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", replys=" + replys +
                ", dianzan=" + dianzan +
                ", icon='" + icon + '\'' +
                ", content='" + content + '\'' +
                ", platform='" + platform + '\'' +
                ", create_time=" + create_time +
                '}';
    }
}
