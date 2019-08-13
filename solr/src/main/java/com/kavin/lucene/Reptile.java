package com.kavin.lucene;




import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.kavin.lucene.bean.HtmlBean;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * @author kavin
 */
public class Reptile {



    public static List ReptileHtml(String URL){
        InputStream is=null;     //创建输入流用于读取流
        BufferedReader br=null;  //包装流,加快读取速度
        StringBuffer html=new StringBuffer(); //用来保存读取页面的数据.
        String  temp=""; //创建临时字符串用于保存每一次读的一行数据，然后html调用append方法写入temp;


        List list = new ArrayList();  //用于存储爬下来的htmlBean
        try {
            URL url = new URL(URL); //获取URL;
            is = url.openStream();   //打开流，准备开始读取数据;
            br= new BufferedReader(new InputStreamReader(is,"UTF-8")); //将流包装成字符流，调用br.readLine()可以提高读取效率，每次读取一行;
            while ((temp = br.readLine()) != null) {//读取数据,调用br.readLine()方法每次读取一行数据,并赋值给temp,如果没数据则值==null,跳出循环;
                html.append(temp); //将temp的值追加给html,这里注意的时String跟StringBuffere的区别前者不是可变的后者是可变的;
            }
            //System.out.println(html); //打印出爬取页面的全部代码;
            if(is!=null) {//接下来是关闭流,防止资源的浪费;
                is.close();
                is=null;
            }
            Document doc = Jsoup.parse(html.toString());  //通过Jsoup解析页面,生成一个document对象;
            Elements elements = doc.getElementsByTag("H4");//找到所有H4标签

            System.out.println("==开始==");

            for (Element element : elements) {
                Elements a = element.getElementsByTag("a").eq(0);
                String href = a.attr("href");
                String title = a.text();//获取文本内容
                if(href.startsWith("https://blog.csdn.net/liuhaiquan123521/article/details")  &&  !"".equals(title.trim())){
                    String content = getContent(href);
                    System.out.println(title);
                    System.out.println(href);
                    System.out.println();
                    HtmlBean bean = new HtmlBean();
                    bean.setUrl(href);
                    bean.setTitle(title);
                    bean.setContent(content);
                    list.add(bean);


                }
            }
            System.out.println("==结束==");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("走完了");

        return list;
    }


    /**
     * 获取博客内容
     * @param URL
     * @return
     */
    public static String getContent(String URL){
        InputStream is=null;     //创建输入流用于读取流
        BufferedReader br=null;  //包装流,加快读取速度
        StringBuffer html=new StringBuffer(); //用来保存读取页面的数据.
        String  temp=""; //创建临时字符串用于保存每一次读的一行数据，然后html调用append方法写入temp;

        try {
            URL url = new URL(URL); //获取URL;
            is = url.openStream();   //打开流，准备开始读取数据;
            br= new BufferedReader(new InputStreamReader(is,"UTF-8")); //将流包装成字符流，调用br.readLine()可以提高读取效率，每次读取一行;
            while ((temp = br.readLine()) != null) {//读取数据,调用br.readLine()方法每次读取一行数据,并赋值给temp,如果没数据则值==null,跳出循环;
                html.append(temp); //将temp的值追加给html,这里注意的时String跟StringBuffere的区别前者不是可变的后者是可变的;
            }
            //System.out.println(html); //打印出爬取页面的全部代码;
            if(is!=null) {//接下来是关闭流,防止资源的浪费;
                is.close();
                is=null;
            }
            Document doc = Jsoup.parse(html.toString());  //通过Jsoup解析页面,生成一个document对象;
            Element element = doc.getElementById("content_views");//找打文章正文的content
            return element.text();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return " ";
    }

    //爬取自己csdn 博客上的文章
    public static void main(String[] args) {
        List<HtmlBean>  list =ReptileHtml("https://blog.csdn.net/liuhaiquan123521");

        for(HtmlBean bean : list){
            String rootDir = "E:\\javaReptile\\searchsource\\";
            File file  = new File(rootDir+bean.getTitle()+"&"+bean.getUrl()+".txt");
            try {
                FileUtils.writeStringToFile(file,bean.getContent(),"GBK",true);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}