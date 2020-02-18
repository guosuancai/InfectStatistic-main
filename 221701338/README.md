# InfectStatistic-221701338  
疫情统计  

描述你的项目，包括如何运行、功能简介、作业链接、博客链接等  
   
   
|  这个作业属于哪个课程  |  [2020春W班](https://edu.cnblogs.com/campus/fzu/2020SpringW)  |
| :--: | :--: |
|这个作业要求在哪里|[作业要求](https://edu.cnblogs.com/campus/fzu/2020SpringW/homework/10281)|
|这个作业的目标|1.|
|作业正文|....  |
|其他参考文献| csnd 博客园 github |  
# 一、我的Github仓库地址
- [我的Github仓库](https://github.com/guosuancai/InfectStatistic-main)
# 二、PSP表格
PSP2.1 | Personal Software Process Stages | 预估耗时（分钟）|   实际耗时（分钟）
 :-- | :--: |--:| --: 
Planning|计划| 30| 50
Estimate|估计这个任务需要多少时间| 20| 30   
Development|开发| 960 | 1280
Analysis|需求分析 (包括学习新技术)| 60| 90
Design Spec|生成设计文档| 30| 60
Design Review|设计复审| 40| 45
Coding Standard|代码规范 (为目前的开发制定合适的规范)| 20| 30 
Design|具体设计| 180| 300
Coding|具体编码| 600| 720
Code Review|代码复审| 120| 150
Test|测试（自我测试，修改代码，提交修改）| 60| 90 
Reporting|报告| 120| 180
Test Report|测试报告| 30| 30
Size Measurement|计算工作量| 50| 60
Postmortem & Process Improvement Plan|事后总结, 并提出过程改进计划| 30| 30
|合计| | 2350| 3135
# 三、解题思路描述  

   - **解析命令行**  
   1. 将传入的命令存入list中备用
   2. 从得到的list中判断各个参数是否存在，若是将其标注为存在
   3. 将list中的各个参数存入全局变量中  
   
   - **读入日志文本并转换为数据**  
   1. 一行一行读取日志
   2. 将读取的行与设置好的正则表达式对比，若符合要求则将其实例化为一个InfectSituation类
   3. 将类存入一个list中备用  
   
   - **处理数据**  
   1. 将读取来的list中每个相同省份的数据进行合并，并将所有数据相加，存到全国类里
   2. 将list以省的拼音来排序  
   
   - **输出数据**  
   1. 根据参数输出相应的数据  
# 四、设计实现过程  
  ![](https://images.cnblogs.com/cnblogs_com/suancai/1640147/o_200218084047%E6%8D%95%E8%8E%B7.PNG) 
