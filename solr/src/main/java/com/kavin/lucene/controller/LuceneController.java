package com.kavin.lucene.controller;


import com.kavin.lucene.Reptile;
import com.kavin.lucene.bean.HtmlBean;
import com.kavin.lucene.index.LuceneIndex;
import com.kavin.lucene.search.luceneSearch;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LuceneController {
    @RequestMapping("/index")
    public String index(HttpServletRequest request){
        //使用重定向让前台页面重定向到具体的某个页面，而不是返回一个逻辑视图，这样能防止前台页面中的相对路径能找到对应的资源
        return "redirect:/static/app/index.html";
    }

    @RequestMapping(value = "/reptile",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String reptile(){
        List<HtmlBean> list =Reptile.ReptileHtml("https://blog.csdn.net/liuhaiquan123521");
        ModelAndView model = new  ModelAndView();
        if (list.size()>0){
            for(HtmlBean bean : list){
                String rootDir = "E:\\javaReptile\\searchsource\\";
                System.out.println(bean.getContent());
                File file  = new File(rootDir+bean.getTitle()+".txt");
                try {
                    FileUtils.writeStringToFile(file,bean.getContent(),"GBK",true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return "拉取成功";
        }

        return "拉取失败";
    }


    @RequestMapping(value = "/createIndex",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createIndex() throws IOException {
        File file1 = new File("E:\\javaReptile\\searchsource");
        File file2 = new File("E:\\javaReptile\\luceneIndex");
        LuceneIndex.indexCreate(file1, file2);
        return "创建成功";
    }


    @RequestMapping(value = "/search", method ={RequestMethod.POST})
    @ResponseBody
    public  Map search(@RequestBody  Map parameter) throws Exception {
        File indexDir = new File("E:\\javaReptile\\luceneIndex");
        String  queryKey = parameter.get("queryKey")+"";

        List list = luceneSearch.indexSearch(indexDir.toPath(), queryKey);
        Map map = new HashMap();
        map.put("row",list);
        return map;
    }
}



