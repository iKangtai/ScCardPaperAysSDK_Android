# ScPaperAnalysiserDemo_Android
### 一.引入试纸sdk库
   ```java
       api 'com.ikangtai.papersdk:ScCardPaperAnalysiserLib:1.0.0-alpha1'
   ```
### 二.添加依赖库地址
   ```java
      maven { url 'https://dl.bintray.com/ikangtaijcenter123/ikangtai' }
   ```
### 三.使用方法
  ```java
      //网络配置需要在初始化sdk之前
      //使用测试网络
      Config.setTestServer(true);
      //网络超时时间
      Config.setNetTimeOut(30);

  
  ```
  1.初始化
  ```java
    //初始化sdk
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");
  ```
  2.常规配置
  ```java
    /**
    * log默认路径/data/Android/pageName/files/Documents/log.txt,可以通过LogUtils.getLogFilePath()获取
    * 自定义log文件有两种方式,设置一次即可
    * 1. {@link Config.Builder#logWriter(Writer)}
    * 2. {@link Config.Builder#logFilePath(String)}
    */
    String logFilePath = new File(FileUtil.createRootPath(getContext()), "log_test.txt").getAbsolutePath();
    BufferedWriter logWriter = null;
    try {
        logWriter = new BufferedWriter(new FileWriter(logFilePath, true), 2048);
    } catch (IOException e) {
       e.printStackTrace();
    }
    //试纸识别sdk相关配置
    Config config = new Config.Builder().pixelOfdExtended(true).paperMinHeight(PxDxUtil.dip2px(getContext(), 20)).uiOption(uiOption).logWriter(logWriter).build();
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com",config);
  ```
  
  3.调用识别试纸图片
  ```java
    float paperLeft = data.innerLeft * 1f / paperResultView.getPaperWidth();
    float paperTop = data.innerTop * 1f / paperResultView.getPaperWidth();
    float paperRight = data.innerRight * 1f / paperResultView.getPaperWidth();
    float paperBottom = data.innerBottom * 1f / paperResultView.getPaperWidth();
    float tagLineLoc = (data.tagLineLoc - data.innerLeft) / data.innerWidth;
    /**
     * paperLeft,paperTop,paperRight,paperBottom 试纸取景框相对试纸全景图坐标位置
     * tagLineLoc 标记线相对于试纸框位置
     */
    cardPaperAnalysiserClient.startPaperSassAnalysis(originSquareBitmap, paperLeft, paperTop, paperRight, paperBottom, tagLineLoc, new ISessionResultEvent() {
        @Override
        public void onResult(Session session) {
            
        }
    });
  ```
 4.修改试纸识别结果
  ```java
  float paperLeft = data.innerLeft * 1f / paperResultView.getPaperWidth();
  float paperTop = data.innerTop * 1f / paperResultView.getPaperWidth();
  float paperRight = data.innerRight * 1f / paperResultView.getPaperWidth();
  float paperBottom = data.innerBottom * 1f / paperResultView.getPaperWidth();
  float tagLineLoc = (data.tagLineLoc - data.innerLeft) / data.innerWidth;
  float test1LineLoc = (float) paperResult.getLine1Pos();
  float test2LineLoc = (float) paperResult.getLine1Pos();
  /**
    * paperLeft,paperTop,paperRight,paperBottom 试纸取景框相对试纸全景图坐标位置
    * tagLineLoc 标记线相对于试纸框位置
    * test1LineLoc 第1条测试线相对于试纸框位置
    * test2LineLoc 第1条测试线相对于试纸框位置
    */  
  cardPaperAnalysiserClient.updatePaperSassAnalysis(originSquareBitmap, paperLeft, paperTop, paperRight, paperBottom, tagLineLoc, test1LineLoc, test2LineLoc, new ISessionResultEvent() {
      @Override
      public void onResult(Session session) {
          paperResult.setPaperValue(session.getPaperValue());
      }
  });  
  ```
  7.调用完成释放资源
  ```java
    cardPaperAnalysiserClient.stopPaperSassAnalysis();
  ```

  8.混淆代码过滤
  ```java
    -dontwarn  com.ikangtai.cardpapersdk.**
    -keep class com.ikangtai.cardpapersdk.** {*;}
    -keepclasseswithmembernames class *{
    	native <methods>;
    }
  ```
