# GeassClient
Android Root开发模块。
维持终端池,可以多线程同步执行SU命令。
支持同步和异步2种方式灵活执行SU命令。

#Dependency
```Groovy
compile 'com.squareup.okio:okio:1.11.0'
```
#Usage
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
API很简单,是不是感觉和OkHttp一毛一样。因为我就是膜仿它的设计啊。

##TODO
Call超时的功能还没测试。也不知道现在的有用没。

##Other
项目依赖了Okio库,如果怕版本冲突
```Groovy
compile ('com.squareup.okio:okio:1.11.0') {
   exclude module: 'okio'
}
```

License
-------

    Copyright 2016 Jude

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.






