
package multiThread;

import PPTVSpider.VideoInformation;

/**
 * Created by ty on 2017/6/8.
 */
public class MultiDideo extends Thread {

    private String name;

    public MultiDideo(String name) {
        this.name = name;
    }

    public void run() {
       VideoInformation videoInformation= new VideoInformation();
        if ("电视剧".equals(name))
        videoInformation.getVideoData("","", name);
        if ("电影".equals(name))
        videoInformation.getVideoData("","",name);
        if ("动画".equals(name))
        videoInformation.getVideoData("","",name);

        System.out.println(getName()+"开始执行"+name);
    }
}
