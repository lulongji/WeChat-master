package com.llj.web.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpClientUtils {

    private static CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

    public static void main(String args[]) {
        String str = "{\"agent\":\"\",\"called\":\"02151265776\",\"caller\":\"87978100000008\",\"cmd\":\"authen\",\"direction\":0,\"origcalled\":\"\",\"service\":1,\"stationno\":1}";
        long start = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            System.out.println(post("http://192.168.177.220:8080/aaa/service", str));
            System.out.println(i);
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    public static String post(String url, String param) {


        HttpPost httpPost = new HttpPost(url);

        StringEntity stringentity = new StringEntity(param, "UTF-8");
        stringentity.setContentType("text/xml;charset=UTF-8");
        stringentity.setContentEncoding("UTF-8");
        httpPost.setEntity(stringentity);
        try {
            HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
            // 获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            // 响应状态
            // System.out.println("status:" + httpResponse.getStatusLine());
            // 判断响应实体是否为空
            if (entity != null) {
                // System.out.println("contentEncoding:" + entity.getContentEncoding());
                // System.out.println("response content:" + EntityUtils.toString(entity));
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流并释放资源
//				closeableHttpClient.close();
            httpPost.releaseConnection();
        }
        return null;

    }

    public static String post(String url, byte[] data, int timeout) throws Exception {
        HttpURLConnection con = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL httpUrl = new URL(url);
            con = (HttpURLConnection) httpUrl.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            con.addRequestProperty("Content-type", "application/json;charset=UTF-8");
            OutputStream out = con.getOutputStream();
            out.write(data);
            out.flush();
            out.close();
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                if (StringUtils.isBlank(result)) {
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("success", "true");
                    result = jsonData.toString();
                }
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (in != null) {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                }
            }

            if (con != null) {
                try {
                    con.getInputStream().close();
                } catch (Throwable e) {

                }
                try {
                    con.getOutputStream().close();
                } catch (Throwable e) {

                }
                con.disconnect();
            }
        }
        return result;
    }

    public static String get(String url) {

        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        System.out.println("--" + httpGet.getRequestLine());
        try {
            // 执行get请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            // 获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            // 响应状态
            System.out.println("status:" + httpResponse.getStatusLine());
            // 判断响应实体是否为空
            if (entity != null) {
                // System.out.println("contentEncoding:" + entity.getContentEncoding());
                // System.out.println("response content:" + EntityUtils.toString(entity));
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流并释放资源
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            OutputStreamWriter outWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            out = new PrintWriter(outWriter);
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


}
