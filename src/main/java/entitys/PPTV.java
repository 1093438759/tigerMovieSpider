package entitys;

import java.util.List;

/**
 * Created by ty on 2017/5/26.
 */
public class PPTV {

    private String title;                    //视频名字
    private String vid;                      //视频ID
    private int episodes;                    //集数
    private String fixupdate;                //更新
    private int severalEpisodes;             //更新至几级
    private List<Episode> eachIntroduce;     //一部电视剧每集介绍
    private String director;                 //导演
    private String[] acts;                   //主演
    private String type;                     //分类
    private String[] catalog;                //类型
    private String area;                     //那地拍摄
    private int year;                        //哪一年出的
    private float score;                     //评分
    private long pv;                         //播放量
    private String content;                  //视频简介
    private String platform;                 //平台

    public PPTV(String title, String vid, int episodes, String fixupdate, int severalEpisodes,
                List<Episode> eachIntroduce, String director, String[] acts, String type,
                String[] catalog, String area, int year, float score, long pv, String content,
                String platform) {
        this.title = title;
        this.vid = vid;
        this.episodes = episodes;
        this.fixupdate = fixupdate;
        this.severalEpisodes = severalEpisodes;
        this.eachIntroduce = eachIntroduce;
        this.director = director;
        this.acts = acts;
        this.type = type;
        this.catalog = catalog;
        this.area = area;
        this.year = year;
        this.score = score;
        this.pv = pv;
        this.content = content;
        this.platform = platform;
    }

    /**
     * 爱奇艺
     * title vid episodes severalEpisodes act type catalog pv content platform tv_up tv_down
     */


}