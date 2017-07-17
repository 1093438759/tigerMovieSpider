package spider;

import com.mongodb.DBObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lenovo on 2017/5/2.
 */
public class MovieSarftSpider {

    public static void main(String[] args) throws JSONException {
        int sum = 0;      //统计插入数据次数

        //初始化mongodb连接
       // DBCollection movieSarft = MongoDBUtil.getConnection("psdoc", "movieSarft");//连接数据库

        String pageUrl = "http://dy.chinasarft.gov.cn/shanty.deploy/" +
                "catalog.nsp?id=0129dffcccb1015d402881cd29de91ec&pageIndex=1";

        //获得所有详细页的链接
        List<String> allUrl = getDetailUrls(pageUrl);          //获取这个页面的所有链接（包括子链接）
       /* for (int i = 0; i <allUrl.size() ; i++) {
            System.out.println(allUrl.get(i)+i);
        }*/
        //遍历详细页的链接
        for (String url : allUrl) {

            //根据url，获得详细页中的数据
            JSONObject keyValuesMap = null;
            keyValuesMap = getDataFromDetailPage(url);
            //存入mongodb
            DBObject dbObject = (DBObject) keyValuesMap;
          //  movieSarft.insert(dbObject);

            sum++;
            System.out.println("插入数据记录：" + sum + "\t url:" + url);

        }
        System.out.println("插入数据完毕");
    }

    /**
     * 根据url，获得详细页中的数据
     *
     * @param url 页面链接
     * @throws JSONException
     */
    private static JSONObject getDataFromDetailPage(String url) throws JSONException {
        //存放数据
        JSONObject keyValuesMap = new JSONObject();
        //页面源码
        String detailPageContent = getPageContent(url);


        Pattern trConentPattern = Pattern.compile("<td.*?style=\"FONT-SIZE: 16px;.*?\">(.*?)<");
        //获得表头内容
        List<String> keys = getKeys(detailPageContent, trConentPattern);
        //获得表格内容
        List<String> values = getValues(detailPageContent, trConentPattern);


        //遍历keys，把对应的values存进去，如： keyValuesMap.put("备案立项号","影剧备字[2017]第1769号");
        for (int i = 0; i < keys.size(); i++) {
            keyValuesMap.put(keys.get(i), values.get(i));
        }

        //获得梗概
        Pattern descPattern = Pattern.compile("<b style=\"text-indent: 1em;\">(.*?)</b>.*<span>(.*?)</span>");
        Matcher descMatcher = descPattern.matcher(detailPageContent);
        while (descMatcher.find()) {
            keyValuesMap.put(descMatcher.group(1), descMatcher.group(2));
        }
        return keyValuesMap;

    }

    private static List<String> getValues(String detailPageContent, Pattern tdContent) {
        List<String> values = new ArrayList<String>();
        String valuesTrContent = getValuesTrContent(detailPageContent);
        // 从valuesTrContent中取出所有td里的值，存到values中
        Matcher valuesMatcher = tdContent.matcher(valuesTrContent);
        while (valuesMatcher.find()) {
            values.add(valuesMatcher.group(1).replace("&nbsp;", ""));
        }
        return values;
    }

    private static List<String> getKeys(String detailPageContent, Pattern tdContent) {
        List<String> keys = new ArrayList<String>();
        String keysTrContent = getKeysTrContent(detailPageContent);
        // 从keysTrContent中取出所有td里的值，存到keys中
        Matcher keysMatcher = tdContent.matcher(keysTrContent);
        while (keysMatcher.find()) {
            System.out.println(keysMatcher.group(1));
            keys.add(keysMatcher.group(1));
        }
        return keys;
    }

    /**
     * 获得表头内容
     *
     * @param pageConent
     * @return
     */
    private static String getValuesTrContent(String pageConent) {

        Pattern valuesTrPattern = Pattern.compile("<tr align=\"center\" height=\"30px\">.*?</tr>");
        Matcher valuesMatcher = valuesTrPattern.matcher(pageConent);
        while (valuesMatcher.find()) {
            return valuesMatcher.group();
        }
        return null;
    }

    /**
     * 获得表格内容s的中整段内容
     *
     * @param pageConent
     * @return
     */
    private static String getKeysTrContent(String pageConent) {
        Pattern keysTrPattern = Pattern.compile("<tr align=\"center\">.*?</tr>");
        Matcher keysMatcher = keysTrPattern.matcher(pageConent);
        while (keysMatcher.find()) {
            return keysMatcher.group();
        }
        return null;
    }


    /**
     * 获得详细页的网页源码
     *
     * @param url
     * @return pageContent = 网页源码
     */
    private static String getPageContent(String url) {
        StringBuffer pageContentBuffer = new StringBuffer();
        try {
            URL detailUrl = new URL(url);
            BufferedReader buff = new BufferedReader(new InputStreamReader(detailUrl.openStream()));
            String line;
            while ((line = buff.readLine()) != null) {
                pageContentBuffer.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("链接网页失败");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("读取数据失败");
        }
        return pageContentBuffer.toString();


     /*  CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet=new HttpGet("http://v.qq.com/x/list/tv?&offset=90");
        CloseableHttpResponse closeableHttpResponse=httpClient.execute(httpGet);
        HttpEntity httpEntity=closeableHttpResponse.getEntity();
        String tt=EntityUtils.toString(httpEntity,"UTF-8");*/
    }


    /**
     * 获得详细页的url列表
     *
     * @param pageUrl
     * @return
     */
    public static List<String> getDetailUrls(String pageUrl) {
        List<String> detailUrls = new ArrayList<String>();
        String builder = "http://dy.chinasarft.gov.cn/";
        List<String> listUrl = getUrls(pageUrl);//从列表页取出备案公示的当页链接
        for (String url : listUrl) {
            List<String> list1 = getUrls(builder.concat(url));//从公示页，取出公示详情的所有链接
            for (int j = 0; j < list1.size(); j++) {
                detailUrls.add(builder.concat(list1.get(j)));
            }
        }
        return detailUrls;
    }

    /**
     * 从页面中用正则表达式，抽取出url
     *
     * @param url
     * @return
     */
    public static List<String> getUrls(String url) { //获取页面的子链接
        String content = getPageContent(url);
        List<String> UrlList = new ArrayList<String>();
        Pattern pa = Pattern.compile("/(shanty.deploy/blueprint.nsp\\?id=015.*?templateId=\\w+)");
        Matcher matcher = pa.matcher(content);
        while (matcher.find()) {
            UrlList.add(matcher.group(1));
        }
        return UrlList;
    }
}