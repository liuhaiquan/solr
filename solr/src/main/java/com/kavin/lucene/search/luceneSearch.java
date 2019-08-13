package com.kavin.lucene.search;

import com.kavin.lucene.bean.HtmlBean;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class luceneSearch {

    /**
     * 索引查询
     *
     * @param path  ：Lucene 索引文件所在目录
     * @param queryKey ：检索的内容，默认从文章内容进行查询
     * @throws Exception
     */
    public static List indexSearch(Path path, String queryKey) throws Exception {
        if (path == null || queryKey == null || "".equals(queryKey)) {
            return new ArrayList();
        }

        /** 创建分词器
         * 1）创建索引 与 查询索引 所用的分词器必须一致
         * 2)现在使用 中文分词器 IKAnalyzer
         */
        /*Analyzer analyzer = new StandardAnalyzer();*/
        Analyzer analyzer = new IKAnalyzer();

        /**创建查询对象(QueryParser)：QueryParser(Version matchVersion,String f, Analyzer a)
         *  第一个参数：默认搜索域，与创建索引时的域名称必须相同
         *  第二个参数：分词器
         * 默认搜索域作用：
         *  如果搜索语法parse(String query)中指定了域名，则从指定域中搜索
         *  如果搜索语法parse(String query)中只指定了查询关键字，则从默认搜索域中进行搜索
         */
        QueryParser queryParser = new QueryParser("fileName", analyzer);

        /** parse 表示解析查询语法，查询语法为："域名:搜索的关键字"
         *  parse("fileName:web")：则从fileName域中进行检索 web 字符串
         * 如果为 parse("web")：则从默认搜索域 fileContext 中进行检索.
         * 1)查询不区分大小写
         * 2)如果使用的是 StandardAnalyzer(标准分词器)，对英文效果很好，如果此时检索中文，基本是行不通的
         */
        Query query = queryParser.parse("fileContext:" + queryKey);


        /** 与创建 索引 和 Lucene 文档 时一样，指定 索引和文档 的目录
         * 即指定查询的索引库
         * Lucene 5.5.5 中 FSDirectory.open 方法参数为 Path
         * Lucene 4.10。3 中 FSDirectory.open 方法参数为 File
         */
        /*Path path = Paths.get(indexDir.toURI());*/
        Directory dir = FSDirectory.open(path);

        /*** 创建 索引库读 对象
         * DirectoryReader 继承于org.apache.lucene.index.IndexReader
         * */
        DirectoryReader directoryReader = DirectoryReader.open(dir);

        /** 根据 索引对象创建 索引搜索对象
         **/
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        /**search(Query query, int n) 搜索
         * 第一个参数：查询语句对象
         * 第二个参数：指定查询最多返回多少条数据，此处则表示返回个数最多5条
         */
        TopDocs topdocs = indexSearcher.search(query, 5);

        System.out.println("查询结果总数：：：=====" + topdocs.totalHits);

        /**从搜索结果对象中获取结果集
         * 如果没有查询到值，则 ScoreDoc[] 数组大小为 0
         * */
        ScoreDoc[] scoreDocs = topdocs.scoreDocs;

        ScoreDoc loopScoreDoc = null;
        List list = new ArrayList();


        //============使用高亮显示

        //创建QueryScoer
        QueryScorer scorer=new QueryScorer(query);
        //设置高亮的格式
        SimpleHTMLFormatter simpleHTMLFormatter=new SimpleHTMLFormatter("<b><font color='red'>","</font></b>");
        Highlighter highlighter=new Highlighter(simpleHTMLFormatter, scorer);

      /**
　　　　* 创建Fragmenter 作用是将原始字符串拆分成独立的片段  三种实现类
　　　　* SimpleSpanFragmenter 是尝试将让片段永远包含跨度匹配的文档
　　　　* SimpleFragmenter 是负责将文本拆分封固定字符长度的片段，但它并处理子边界（默认100）
　　　　* NullFragmenter 整个字符串作为单个片段返回，这适合于处理title域和前台文本较短的域
　　　　*/
        Fragmenter fragmenter=new SimpleSpanFragmenter(scorer);
        highlighter.setTextFragmenter(fragmenter);



        for(ScoreDoc scoreDoc:topdocs.scoreDocs){
            Document doc=indexSearcher.doc(scoreDoc.doc);

            String fileName = doc.get("fileName");
            String fileContext = doc.get("fileContext");
            /* * 对设置域的文本使用指定的分词器对关键词进行高亮显示
             *   第一个参数：分词器
             *   第二个参数：需要高亮显示的域的名称
             *   第三个参数：该域对应的内容
             *  */
            String nameResult = highlighter.getBestFragment(analyzer,"fileName", fileName);
            String contentResult = highlighter.getBestFragment(analyzer,"fileContext", fileContext);
            HtmlBean bean = new HtmlBean();
            if(nameResult !=null){
                bean.setTitle(nameResult);
            }else{
                bean.setTitle(fileName.replace(".txt",""));
            }

            bean.setContent(contentResult);
            list.add(bean);
        }
        directoryReader.close();
        return list;
    }

    public static void main(String[] args) throws Exception {
        Path path = Paths.get("E:\\javaReptile\\luceneIndex");


        indexSearch(path, "spring");
    }


}
