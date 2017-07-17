package entitys;

/**
 * Created by ty on 2017/6/9.
 */
public class Replier {

    private long replyObjectID;           //回复对象的ID
    private long  replyID;                //回复者ID
    private String replyName;             //回复者昵称
    private String replyContent;          //回复内容
    private long create_time;             //回复时间

    public Replier(long replyObjectID, long replyID, String replyName, String replyContent, long create_time) {
        this.replyObjectID = replyObjectID;
        this.replyID = replyID;
        this.replyName = replyName;
        this.replyContent = replyContent;
        this.create_time = create_time;
    }
}
