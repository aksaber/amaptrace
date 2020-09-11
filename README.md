# react-native-amaptrace

Amap Trace SDK modules for React Native （Android），高德地图猎鹰轨迹 React Native 模块。（安卓）

[高德猎鹰轨迹SDK文档](https://lbs.amap.com/api/android-track/summary/)

## Getting started

`$ npm install react-native-amaptrace --save`

### Mostly automatic installation

`$ react-native link react-native-amaptrace`

### Manual installation


#### iOS

​	暂不支持ios

#### Android

1. 设置高德apiKey

   在AndroidManifest.xml设置高德apiKey。key申请方法详情参见[高德申请密钥](https://lbs.amap.com/api/yuntu/guide/create-project/permission/)。

   ```
   <meta-data
   	android:name="com.amap.api.v2.apikey"
       android:value="您申请的高德apikey" />
   ```

   

2. 配置权限

   在AndroidManifest.xml中配置以上权限

   ```
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
   <!--用于访问GPS定位-->
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
   <!--用于获取运营商信息，用于支持提供运营商信息相关的接口-->
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
   <!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
   <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
   <!--用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
   <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
   <!--用于访问网络，网络定位需要上网-->
   <uses-permission android:name="android.permission.INTERNET"/>
   <!--用于读取手机当前的状态-->
   <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
   <!--用于写入缓存数据到扩展存储卡-->
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
   <!--用于申请调用A-GPS模块-->
   <uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS"/>
   <!--用于申请获取蓝牙信息进行室内定位-->
   <uses-permission android:name="android.permission.BLUETOOTH"/>
   <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
   ```


## Usage
```javascript
import RNAmaptrace, { AmapTraceEventEmitter } from 'react-native-amaptrace';
```

#### Init Service

```
RNAmaptrace.init({
    serviceId: 轨迹服务id,
    terminalId: 终端id,
    terminalName: 设备标识,
})
```

#### Start Service

```
// 开启轨迹上报
RNAmaptrace.createTrack();
```

#### Stop Service

```
RNAmaptrace.stopGather();  // 停止轨迹采集
RNAmaptrace.stopTrack();   // 停止轨迹服务
```

#### Query Trace

```
// 查询轨迹不能超过24小时，时间格式为字符串格式的时间戳
RNAmaptrace.historyTrackQuery({
    startTime: (new Date().getTime() - 12*60*60*1000).toString(),  // 起始时间
    endTime: new Date().getTime().toString(),  					   // 结束时间
});
```

#### 添加事件监听

| 监听器标志      | 备注         |
| --------------- | ------------ |
| ON_START_GATHER | 开启定位采集 |
| ON_START_TRACK  | 开启定位服务 |
| ON_STOP_GATHER  | 停止定位采集 |
| ON_STOP_TRACK   | 停止定位服务 |
| LATLNG          | 查询轨迹信息 |

```
AmapTraceEventEmitter.addListener('ON_START_GATHER', (result) => {
	// 开启定位采集时的监听回调
	console.log('ON_START_GATHER', result);
})
```

