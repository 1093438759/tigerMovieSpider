package entitys;

/**
 * Created by ty on 2017/6/12.
 */
public class Search {

    private String title;               //名字
    private int rank;                   //排名
    private String url;                 //地址
    private long readAmount;            //点击或者阅读量
    private int type;                   //类型(比如;最新最热)
    private String platform;            //平台

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public Search(String title, int rank, String url, long readAmount, int type, String platform) {
        this.title = title;
        this.rank = rank;
        this.url = url;
        this.readAmount = readAmount;
        this.type = type;
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "SoGouSearch{" +
                "title='" + title + '\'' +
                ", rank='" + rank + '\'' +
                ", url='" + url + '\'' +
                ", readAmount='" + readAmount + '\'' +
                ", type=" + type +
                ", platform='" + platform + '\'' +
                '}';
    }
}
