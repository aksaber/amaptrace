
package com.aksaber.trace;

import android.util.Log;
import java.util.ArrayList;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;

import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.query.model.HistoryTrackRequest;
import com.amap.api.track.query.model.HistoryTrackResponse;
import com.amap.api.track.query.entity.HistoryTrack;
import com.amap.api.track.query.model.OnTrackListener;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.query.model.ParamErrorResponse;
import com.amap.api.track.query.model.AddTrackResponse;
import com.amap.api.track.query.model.QueryTrackResponse;
import com.amap.api.track.query.model.DistanceResponse;
import com.amap.api.track.query.model.LatestPointResponse;
import com.amap.api.track.query.model.QueryTerminalResponse;
import com.amap.api.track.query.model.QueryTrackResponse;
import com.amap.api.track.query.model.AddTerminalResponse;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.AddTerminalRequest;
import com.amap.api.track.TrackParam;
import com.amap.api.track.query.entity.Point;

import com.aksaber.trace.TracePoint;
import com.alibaba.fastjson.JSONObject;

public class RNAmaptraceModule extends ReactContextBaseJavaModule {

  private static ReactApplicationContext reactContext = null;
    private Callback aSucCallback;
    private Callback aFailCallback;

    private AMapTrackClient aMapTrackClient = null;
    private long serviceId = 0;
    private long terminalId = 0;
    private String terminalName = "";

    public RNAmaptraceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNAmaptrace";
    }

    @ReactMethod
    public void init(ReadableMap config) {
        // 轨迹服务ID
        serviceId = config.getInt("serviceId");
        // 终端ID
        terminalId = config.getInt("terminalId");
        // 设备标识
        terminalName = config.getString("terminalName");
        if (getReactApplicationContext() != null) {
            this.aMapTrackClient = new AMapTrackClient(getReactApplicationContext());
            this.aMapTrackClient.setInterval(60, 360);
        }
    }

    public static void sendEvent(String eventName, Integer status, String message) {
        WritableMap params = Arguments.createMap();
        params.putInt("status", status);
        params.putString("message", message);
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    // 创建轨迹服务启停状态的监听器
    OnTrackLifecycleListener onTrackLifecycleListener = new OnTrackLifecycleListener() {
        
        @Override
        public void onStartGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE ||
                    status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
                System.out.println("定位采集开启成功！");
                RNAmaptraceModule.sendEvent("ON_START_GATHER", status, msg);
                // Toast.makeText(TestDemo.this, "定位采集开启成功！", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("定位采集开启异常！");
                // aSucCallback.invoke("定位采集开启异常！");
                // Toast.makeText(TestDemo.this, "定位采集启动异常，" + msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE ||
                    status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK ||
                    status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                // 服务启动成功，继续开启收集上报
                aMapTrackClient.startGather(this);
            } else {
                System.out.println("轨迹上报服务服务启动异常，" + msg);
                RNAmaptraceModule.sendEvent("ON_START_TRACK", status, msg);
                // Toast.makeText(TestDemo.this, "轨迹上报服务服务启动异常，" + msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onBindServiceCallback(int status, String msg) {
            RNAmaptraceModule.sendEvent("ON_BIND_SERVICE", status, msg);;
        }

        @Override
        public void onStopGatherCallback(int status, String msg) {
            System.out.println("停止采集回调");
            RNAmaptraceModule.sendEvent("ON_STOP_GATHER", status, msg);
        }

        @Override
        public void onStopTrackCallback(int status, String msg) {
            System.out.println("停止服务回调");
            RNAmaptraceModule.sendEvent("ON_STOP_TRACK", status, msg);
        }
    };

    @ReactMethod
    public void createTrack() {
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(serviceId, terminalName), new OnTrackListener() {

            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if (queryTerminalResponse.isSuccess()) {
                    if (queryTerminalResponse.getTid() <= 0) {
                        // terminal还不存在，先创建
                        aMapTrackClient.addTerminal(new AddTerminalRequest(terminalName, serviceId), new OnTrackListener() {
                            
                            @Override
                            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {
                                if (addTerminalResponse.isSuccess()) {
                                    // 创建完成，开启猎鹰服务
                                    long terminalId = addTerminalResponse.getTid();
                                    aMapTrackClient.startTrack(new TrackParam(serviceId, terminalId), onTrackLifecycleListener);
                                } else {
                                    // 请求失败
                                    System.out.println("请求失败，" + addTerminalResponse.getErrorMsg());
                                    // Toast.makeText(TestDemo.this, "请求失败，" + addTerminalResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onAddTrackCallback(AddTrackResponse response) {}
                            @Override
                            public void onQueryTerminalCallback(QueryTerminalResponse response) {}
                            @Override
                            public void onDistanceCallback(DistanceResponse response) {}
                            @Override
                            public void onLatestPointCallback(LatestPointResponse response) {}
                            @Override
                            public void onHistoryTrackCallback(HistoryTrackResponse response) {}
                            @Override
                            public void onQueryTrackCallback(QueryTrackResponse response) {}
                            @Override
                            public void onParamErrorCallback(ParamErrorResponse response) {}
                        });
                    } else {
                        // terminal已经存在，直接开启猎鹰服务
                        long terminalId = queryTerminalResponse.getTid();
                        aMapTrackClient.startTrack(new TrackParam(serviceId, terminalId), onTrackLifecycleListener);
                    }
                } else {
                    // 请求失败
                    System.out.println("请求失败，" + queryTerminalResponse.getErrorMsg());
                    // Toast.makeText(TestDemo.this, "请求失败，" + queryTerminalResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAddTrackCallback(AddTrackResponse response) {}
            @Override
            public void onCreateTerminalCallback(AddTerminalResponse response) {}
            @Override
            public void onDistanceCallback(DistanceResponse response) {}
            @Override
            public void onLatestPointCallback(LatestPointResponse response) {}
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {}
            @Override
            public void onQueryTrackCallback(QueryTrackResponse response) {}
            @Override
            public void onParamErrorCallback(ParamErrorResponse response) {}
        });
    }

    @ReactMethod
    public void stopGather() {
        // 停止采集
        aMapTrackClient.stopGather(onTrackLifecycleListener);
    }

    @ReactMethod
    public void stopTrack() {
        // 停止服务
        aMapTrackClient.stopTrack(new TrackParam(serviceId, terminalId), onTrackLifecycleListener);
    }

    @ReactMethod
    public void historyTrackQuery(ReadableMap params) {
        HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(
            serviceId,
            terminalId,
            Long.parseLong(params.getString("startTime")),
            Long.parseLong(params.getString("endTime")),
            // System.currentTimeMillis() - 12 * 60 * 60 * 1000,
            // System.currentTimeMillis(),
            0,      // 不绑路
            0,      // 不做距离补偿
            5000,   // 距离补偿阈值，只有超过5km的点才启用距离补偿
            0,  // 由旧到新排序
            1,  // 返回第1页数据
            100,    // 一页不超过100条
            ""  // 暂未实现，该参数无意义，请留空
        );
        
        aMapTrackClient.queryHistoryTrack(historyTrackRequest, new OnTrackListener() {
            // 参数错误回调
            @Override
            public void onParamErrorCallback(ParamErrorResponse response) {
                System.out.println("参数错误回调：" + response);
            }
            
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                if (historyTrackResponse.isSuccess()) {
                    HistoryTrack historyTrack = historyTrackResponse.getHistoryTrack();
                    ArrayList<Point> points = historyTrack.getPoints();
                    ArrayList<TracePoint> tracePoints = new ArrayList<>();
                    points.forEach(point-> {
                        TracePoint tracePoint = new TracePoint();
                        tracePoint.setLatitude(point.getLat());
                        tracePoint.setLongitude(point.getLng());
                        tracePoints.add(tracePoint);
                    });
                    RNAmaptraceModule.sendEvent("LATLNG", 200, JSONObject.toJSONString(tracePoints));
                    // aSucCallback.invoke(JSONObject.toJSONString(tracePoints));
                    // historyTrack中包含终端轨迹信息
                } else {
                    // aSucCallback.invoke(historyTrackResponse);
                    // 查询失败
                }
            }

            @Override
            public void onAddTrackCallback(AddTrackResponse response) {}
            @Override
            public void onCreateTerminalCallback(AddTerminalResponse response) {}
            @Override
            public void onDistanceCallback(DistanceResponse response) {}
            @Override
            public void onLatestPointCallback(LatestPointResponse response) {}
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse response) {}
            @Override
            public void onQueryTrackCallback(QueryTrackResponse response) {}
        });
    }

    @ReactMethod
    public void setCallback(Callback errorCallback, Callback successCallback) {
        aSucCallback = successCallback;
        aFailCallback = errorCallback;
    }

}