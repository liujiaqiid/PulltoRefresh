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
