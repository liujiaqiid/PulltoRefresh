# PulltoRefresh
[![](https://jitpack.io/v/jiang111/PulltoRefresh.svg)](https://jitpack.io/#jiang111/PulltoRefresh)
<br />
a library used to pull to refresh & loadmore

###usage:
Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
Step 2. Add the dependency
```
	dependencies {
	        compile 'com.github.jiang111:PulltoRefresh:1.5'
	}
```


###demo:

![](https://raw.githubusercontent.com/jiang111/PulltoRefresh/master/art/123.gif)


###说明:
>* 1.继承BaseView 并重写相关方法可实现各种效果。可在PullToRefreshLayout的实例调用setHeadView();setFootView()
>* 2.可以通过配置高度来决定Head或者Foot的高度。
```
 PullToRefreshLayout.setHeight(100);
        PullToRefreshLayout.setFoot(100);
```
>* 3.通过setRefresh(boolean); 和 setLoadMore(boolean); 来控制是否可以刷新和加载更多
>* 4.理论上支持各种Layout,亲测RecyclerView和ListView
>* 5.调用autoRefresh(); 可自动下拉刷新
>* 6.minSdkVersion -> 15

###如何定制Head和Foot样式

1.直接把项目下载下来。 <br />
2.自定义类继承BaseView,然后重写相关的方法,接着在可在PullToRefreshLayout的实例中调用setHeadView()或者setFootView(),如果把项目下载下来的话,可以直接替换库中的相关view
3.BaseView中需要重写的方法的相关说明:
```
    //注:里面用到的单位都是px
    void begin(); //表示开始滑动
    void progress(float progress, float all); //表示滑动的进度 progress表示当前距离顶部的px距离,all代表整个px距离,即默认高度的2倍
    void finishing(float progress, float all); //和progress(float progress, float all);一样,该方法在加载完成,开始往回滑动的时候调用,这时候并不会调用progress();
    void loading(); //表示正在加载中... 既 到达加载中默认位置的时候
    void normal();  //表示加载完成,或者没有任何操作的时候
```









