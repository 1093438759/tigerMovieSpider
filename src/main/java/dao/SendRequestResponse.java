package dao;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通发送http get和post请求和获得获得响应的内容
 * Created by ty on 2017/5/22.
 */
public class SendRequestResponse {

    /**
     * 普通get请求 返回utf-8格式内容
     *
     * @param url 普通http   url
     * @return String
     */
    public String getHtml(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept-Encoding","gzip, deflate");
        httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3088.3 Safari/537.36");
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        httpGet.setConfig(requestConfig);

        /*HttpHost proxy = new HttpHost("192.168.2.183", 8888,"https");
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        httpGet.setConfig(config);*/

        HttpResponse response;
        String html = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    html = EntityUtils.toString(entity, "utf-8");
                    return html;
                }
            }
            httpClient.close();
        } catch (IOException e) {
            // e.printStackTrace();
          //  System.out.println(url + "  ");
            return null;

        }
        return null;
    }

    /**
     * 普通发送get请求，返回gbk格式内容
     *
     * @param url 普通http   url
     * @return String
     */
    public String getHtmlGBK(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept-Encoding","gzip, deflate");
        httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.8");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3088.3 Safari/537.36");
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        httpGet.setConfig(requestConfig);
        HttpResponse response;
        String html = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    html = EntityUtils.toString(entity, "gbk");
                    return html;
                }
            }
            httpClient.close();
        } catch (IOException e) {
          //  System.out.println(url + "  ");
            return null;
          //  System.out.println("获取URL地址失败");
        }
        return null;
    }


    /**
     * 普通的post请求
     *
     * @param url
     * @param
     */
    public boolean sendPostQequest(String url) {
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String content;
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                content = EntityUtils.toString(responseEntity, "UTF-8");
                if (content.indexOf("账号异常") >= 0 || content.indexOf("取消") >= 0) {
                    return false;
                }
            }
            httpClient.close();
        } catch (IOException e) {
            System.out.println("发送失败");
        }
        return true;
    }

    /**
     * 发送get请求并且获得JSONArray响应内容
     *
     * @param url 普通http   url
     * @return JSONArray
     * @throws IOException
     */
    public JSONArray sendGetQequest(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        String res;
        JSONArray jsonArray = null;
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                res = EntityUtils.toString(entity, "UTF-8");
                jsonArray = new JSONArray(res);
            }
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * 发送post请求和正文JSONArray数据
     *
     * @param url
     * @param jsonArray
     * @throws IOException
     */
    public void sendPostQequest(String url, JSONArray jsonArray) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String content;
        if (null != jsonArray) {
            StringEntity entity = new StringEntity(jsonArray.toString(), "utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        }
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                content = EntityUtils.toString(responseEntity, "UTF-8");
                System.out.println(content);
            }
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送post请求和正文JSONObject数据
     *
     * @param url
     * @param jsonObject
     */
    public void sendPostQequest(String url, JSONObject jsonObject) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String content;
        if (null != jsonObject) {
            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        }
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                content = EntityUtils.toString(responseEntity, "UTF-8");
                System.out.println(content);
            }
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("发送失败");
        }

    }

    /**
     * 发送post请求和正文json格式的String数据
     *
     * @param url
     * @param context json类型的字符串
     */
    public void sendPostQequest(String url, String context) {
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(context.toString(), "utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        String content;
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                content = EntityUtils.toString(responseEntity, "UTF-8");
                System.out.println(content);
            }
            httpClient.close();
        } catch (IOException e) {
            System.out.println("发送失败");
        }

    }

    public void login(String url) {
    }
}


//代理
  /*    HttpHost proxy = new HttpHost("192.168.110.1", 8888,"http");
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        httpPost.setConfig(config);*/


//u 客户名字   p 密码  s 不知什么什么东西
//https://api.weibo.cn/2/account/login?c=android&u=kxa5259904%40sina.cn&s=35ae1db7&
// p=TJ0BZrUNxGTfagpBN9f26cw39WmEZOab/EWq++MeiTQVRWQfbUj4ktL19CBHX4DD5f16KZP1wCbp5PRrpT5pBW9u3f5yd979wA5uppyoGO8ru0zMD0H6QuDv4kEFqdENDQdO34nYUjRg3A2qd+UQHgmYnE8VWQw7FF/YFgXo/eE=


//https://api.weibo.cn/2/account/login?v_f=2&moduleID=701&wb_version=3371&c=android&wm=2468_1001&imei=864895025135136&luicode=10000115&device_id=5022032dcc75357a4bddb1d8afb9aaf01ab84a0f&aid=01Aj9BR6U8H4uso7C3HDN5f22nZaT3ODubvQodtYr5m1FKyVs.&from=1075095010&networktype=wifi&lang=zh_CN&skin=default&i=722b6eb&u=kxa5259904%40sina.cn&flag=1&sflag=1&s=35ae1db7&p=qYtcRJPG7i%2FhAIpx4PQypPV7vn0o3j7pnT3USdBKjHLKGNu0uJVmQe3Kt4g2jJTCQO9N%2FRvHaZqLSXWYjzmIZxdfXeheI0fug2Xo9tlFVpT3o0b6wcqzCH%2BjLx3tCNIWPPemXS%2BvwUJfp7xNJjTkJrqJcUPTni7CpI%2BnBwb6EV0%3D&v_p=47&ua=HTC-HTC%20X920e__weibo__7.5.0__android__android4.2.2&oldwm=2468_1001&uicode=10000058