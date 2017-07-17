package log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {

    //Logger实例
    public Logger logger = null;

    //将Log类封装为单例模式
    private static Log log = new Log();

    //构造函数，用于初始化Logger配置需要的属性
    private Log() {
        //获得当前目录路径
        String filePath = this.getClass().getResource("\\").getPath();
        System.out.println(filePath);
        logger = Logger.getLogger(this.getClass());
        //logger所需的配置文件路径
        PropertyConfigurator.configure("F:\\project\\com.TigerMovie.collectionFilmProfiles\\src\\main\\resources\\log4j.properties");
        //BasicConfigurator.configure (); //缺省
        logger.info(log);
    }

    public static Log getLogger() {
        return log;
    }
}
