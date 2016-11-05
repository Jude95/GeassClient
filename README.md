# GeassClient
Android Root开发模块
```java
GeassClient client = new GeassClient();
//创建命令
Command command = new Command("ls /data/data");
Call call = client.newCall(command);
//异步执行
call.enqueue(new Callback() {
    @Override
    public void onFailure(Command command, final IOException e) {
    }

    @Override
    public void onResponse(final Response response) throws IOException {
    }
});
//同步执行
try {
    Response response = call.execute();
} catch (IOException e) {
    e.printStackTrace();
}
```

是不是感觉和OkHttp一毛一样。因为我就是暴力膜仿它的设计啊。
