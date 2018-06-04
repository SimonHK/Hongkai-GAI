## 开发日志

处理过程
- 1、爬虫原始数据准备。
   a、将爬取的数据入到graphaicrawler库中crawlercontent表中。
   （crawlercontent表需要定期维护）此表的定期运行规则还为完善。
   b、将爬取的URL放入graphaicrawler库crawlerurls表中由定时任务进行自动爬取原始数据。
    (此部分需要细化，还未完善。)

- 2、nlp分析爬虫获取的原始数据。
    a、将graphaicrawler库中crawlercontent表中的原始数据通过NLP及事件规则进行程序处理后，形成
    较规范的结构化数据存入graphainlp库中storeresult表中。
    b、需要维护graphainlp库中nlprule规则公式表。

- 3、事件处理分析。
    a、将graphainlp库中的storeresult表中的初步事件规则进行分析，分析过程要用到
    graphainlp库中locations表此表为机构或地域数据结构化表。将其分析后的结果入到graphainlp库中
    eventlibrary_news表中，形成相关locations中数据的事件内容。
    
此过程涉及数据库：（GRAPHAICRAWLER:爬虫语料库）（GRAPHAINLP:NLP数据分析库）
涉及表：
1、GRAPHAICRAWLER库中：crawlercontent(原始网页信息表),crawlerurls（网页URL）

2、GRAPHAINLP库中：nlprule(规则公式表),storeresult(初步事件处理后存储库),locations(机构地域信息表)
   eventlibrary_news(事件存储库)
   
> 涉及开源技术： HLP、xxl-job、webmagic


# 按段落处理
# 事件抽取，不按机构地域循环，事件匹配实体，人名，时间。
# 权量规则+新闻语料（全部，自己爬语料）
    

    
   
    