package com.sun.myapplication1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.graphics.Color;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.litepal.LitePal;
import org.osmdroid.OsmdroidBuildInfo;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay;
import android.location.LocationManager;

//import com.arthas.androidy.entity.GnssStatusInfo;

import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.WarningBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.sun.myapplication1.model.Position;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstFragment extends Fragment implements View.OnClickListener{

    private IMapController mapController;
    private  Context ctx;

    private RoadManager roadManager;
    private Road road;
    private Polyline roadOverlay;
    private Marker nodeMarker;
    private Marker nodePaint;
    private Marker longPressMarker;
    private Drawable nodeIcon;
    private List<Marker> marker_array;
    private List<GeoPoint> pointPaintlist;
    private List<Marker> markerNodeList;
    private List<Polyline> pointPaintLinelist;//???marker??????
    private List<GeoPoint> crList;
    private List<Polygon> circleList;



    private Polyline polyline;  //???
    private Polygon circle;     //???
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mMapView = null;
    private TextView tv_1;
    private TextView tv_2;
    private Button bt_pencil;
    private Button button_helpme;
    private Button bt_back;
    private Button button_area;
    private Button button_target;
    private Button button_query;
    private Button button_me;
    private Button bt_material;

    private boolean bt_status;
    private boolean btArea_status;

    private final static double EARTH_RADIUS = 6378.137;

    private ListChangeListener listChangeListener;

    private double lt;
    private double lon;

    //??????????????????
    private MyLocationNewOverlay mLocationOverlay = null;
    private CompassOverlay mCompassOverlay = null;
    private MinimapOverlay mMinimapOverlay = null;
    private ScaleBarOverlay mScaleBarOverlay = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        button_helpme = (Button) getActivity().findViewById(R.id.button_helpme);
        button_query = (Button)getActivity().findViewById(R.id.button_query);
        button_target = (Button) getActivity().findViewById(R.id.button_target);
        button_me = (Button) getActivity().findViewById(R.id.button_me);
        bt_pencil = (Button) getActivity().findViewById(R.id.bt_pencil);
        button_area = (Button) getActivity().findViewById(R.id.button_area);
        bt_back = (Button) getActivity().findViewById(R.id.bt_back);

        Drawable pencil = getResources().getDrawable(R.drawable.pencil);
        Drawable target = getResources().getDrawable(R.drawable.ic_daohang);
        Drawable me = getResources().getDrawable(R.drawable.ic_loacme);
        Drawable dangerous = getResources().getDrawable(R.drawable.ic_dangerous);
        Drawable help = getResources().getDrawable(R.drawable.ic_help);
        Drawable material = getResources().getDrawable(R.drawable.ic_material);
        Drawable backSpace = getResources().getDrawable(R.drawable.ic_backspace);
        Drawable query = getResources().getDrawable(R.drawable.ic_query);

        pencil.setBounds(25, 5 , 100 ,100);
        target.setBounds(25, 5 ,100 ,100);
        me.setBounds(10, 5 ,100 ,100);
        dangerous.setBounds(10, 5 ,100, 100);
        help.setBounds(10,5,100,100);
        material.setBounds(10,5,100,100);
        backSpace.setBounds(10,5,100 ,100);
        query.setBounds(10,5,100,100);


        bt_pencil.setCompoundDrawables(pencil, null , null ,null);
        button_target.setCompoundDrawables(target, null , null , null);
        button_me.setCompoundDrawables(me, null , null , null);
        button_area.setCompoundDrawables(dangerous,null , null, null);
        button_helpme.setCompoundDrawables(help,null,null,null);
        bt_back.setCompoundDrawables(backSpace,null,null,null);
        button_query.setCompoundDrawables(query,null,null,null);

        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pencil:
                if(!btArea_status) {
                    bt_status = !bt_status;//????????????
                    if(bt_status){
                        listChangeListener.fragment1Call();
                        bt_pencil.setText("??????");
                    }else{
                        bt_pencil.setText("??????");
                    }
                }
                break;
            case R.id.button_helpme:
                Position position = new Position();
                BigDecimal b1 = new BigDecimal(MainActivity.l1);
                BigDecimal b2 = new BigDecimal(MainActivity.l2);
                double pl1 = b1.setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
                double pl2 = b2.setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
                position.setLatitude(pl1);
                position.setLongitude(pl2);
                position.save();
                break;
            case R.id.button_target:
                if(roadManager == null) {
                    roadManager = new OSRMRoadManager(ctx.getApplicationContext());
                }
                //?????????????????????
                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                GeoPoint nowPoint = new GeoPoint(MainActivity.l1,MainActivity.l2);
                GeoPoint endPoint = new GeoPoint(lt, lon);
                waypoints.add(nowPoint);
                waypoints.add(endPoint);
                road = roadManager.getRoad(waypoints);
                if(roadOverlay==null) {//?????????????????????
                    roadOverlay = RoadManager.buildRoadOverlay(road);
                    roadOverlay.getOutlinePaint().setColor(Color.BLUE);
                    mMapView.getOverlays().add(roadOverlay);
                }
                //??????????????????
                else{
                    mMapView.getOverlays().remove(roadOverlay);
                    roadOverlay = RoadManager.buildRoadOverlay(road);
                    roadOverlay.getOutlinePaint().setColor(Color.BLUE);
                    mMapView.getOverlays().add(roadOverlay);
                }

                mMapView.invalidate();

//        ??????????????????????????????????????????????????????????????????????????????
                nodeIcon = getResources().getDrawable(R.drawable.marker_node);
                if(marker_array==null) {
                    marker_array = new ArrayList<>();
                }
                //????????????
                else{
                    mMapView.getOverlays().removeAll(marker_array);
                    marker_array.clear();
                }
                for (int i = 0; i < road.mNodes.size(); i++) {
                    RoadNode node = road.mNodes.get(i);
                    nodeMarker = new Marker(mMapView);
                    nodeMarker.setPosition(node.mLocation);
                    nodeMarker.setSnippet(node.mInstructions);
                    nodeMarker.setSubDescription(Road.getLengthDurationText(ctx.getApplicationContext(), node.mLength, node.mDuration));
                    nodeMarker.setIcon(nodeIcon);
                    nodeMarker.setTitle("Step " + i);
                    Drawable icon = getResources().getDrawable(R.drawable.ic_continue);
                    nodeMarker.setImage(icon);
                    marker_array.add(nodeMarker);
                }
                mMapView.getOverlays().addAll(marker_array);
                mMapView.invalidate();
                break;
            case R.id.button_me:
                GeoPoint me = new GeoPoint(MainActivity.l1,MainActivity.l2);
                mapController.setCenter(me);//??????????????????
                mMapView.invalidate();
                break;
            case R.id.button_area:
                if(!bt_status) {
                    btArea_status = !btArea_status;
                    if(crList == null){
                        crList = new ArrayList<>();
                    }

                    if (btArea_status) {
                        button_area.setText("??????");
//                        replaceFragment(new FragmentPaintArea(),R.id.f3);
                    } else {
                        button_area.setText("????????????");
                    }
                }
                break;
            case R.id.bt_back:
                if(bt_status && pointPaintlist.size()>0){ //??????????????? & ????????????  -->????????????marker+?????????
                    mMapView.getOverlays().remove(markerNodeList.get(markerNodeList.size()-1));
                    listChangeListener.fragment1Back();
                    if(markerNodeList.size()>=2){
                        mMapView.getOverlays().remove(pointPaintLinelist.get(pointPaintLinelist.size()-1));
                        pointPaintLinelist.remove(pointPaintLinelist.size()-1);
                    }
                    pointPaintlist.remove(pointPaintlist.size()-1);
                    markerNodeList.remove(markerNodeList.size()-1);
                }
                if(btArea_status && crList.size()>0){
                    mMapView.getOverlays().remove(circleList.get(circleList.size()-1));
                    crList.remove(crList.size()-1);
                    circleList.remove(circleList.size()-1);
                }
                break;
            case R.id.button_query:
                List<Position> allPosition = LitePal.findAll(Position.class);
                for(Position q: allPosition){
                    SimpleLocationOverlay simpleLocation = new SimpleLocationOverlay(ctx.getApplicationContext());
                    simpleLocation.setEnabled(true);
                    simpleLocation.setLocation(new GeoPoint(q.getLatitude(),q.getLongitude()));
                    mMapView.getOverlays().add(simpleLocation);
                }
                break;
            default:
                break;
        }
    }

    public interface ListChangeListener{
        void fragment1Change(GeoPoint p);
        void fragment1Back();
        void fragment1Call();
    }
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        listChangeListener = (ListChangeListener) activity;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


        //setting this before the layout is inflated is a good idea

        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map

//        ????????????????????????????????? API ?????????
//
//        ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? SDK ???3.0???????????????????????????????????????????????????????????????"????????????.????????????"???????????????????????? NetworkOn ?????????????????????
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setTargetFragment(this.getTargetFragment(), REQUEST_PERMISSIONS_REQUEST_CODE);

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mapController = mMapView.getController();
        mapController.setZoom(9.5);//???????????????
        GeoPoint startPoint = new GeoPoint(28.661265, 115.830921);//?????????
        mapController.setCenter(startPoint);//??????????????????
        requestPermissionsIfNecessary(new String[] {
                // if you need to show the current location, uncomment the line below
                // Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
        //??????????????????????????????
        //notice:????????????????????????????????????????????????????????????API23+API,????????????????????????????????????????????????????????????
        //notice:??????????????????????????????????????????????????????Android?????????????????????5.6.5???????????????????????????????????????
        //   ????????????????????????Android???????????????????????????6.0.0 ????????????????????????????????????????????????Pause???Resume??????
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx.getApplicationContext()), mMapView);
        this.mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);


        //??????????????????
        this.mCompassOverlay = new CompassOverlay(ctx.getApplicationContext(), new InternalCompassOrientationProvider(ctx.getApplicationContext()), mMapView);
        this.mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(this.mCompassOverlay);

        //?????????????????????
//        LatLonGridlineOverlay2 overlay = new LatLonGridlineOverlay2();
//        mMapView.getOverlays().add(overlay);

        //?????????
//        Marker startMarker = new Marker(mMapView);
//        startMarker.setPosition(startPoint);
//        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        mMapView.getOverlays().add(startMarker);

        //??????????????????
//        mRotationGestureOverlay = new RotationGestureOverlay(ctx.getApplicationContext(), mMapView);
//        mRotationGestureOverlay.setEnabled(true);
//        mMapView.setMultiTouchControls(true);
//        mMapView.getOverlays().add(this.mRotationGestureOverlay);


        //Mini map
        mMinimapOverlay = new MinimapOverlay(ctx.getApplicationContext(), mMapView.getTileRequestCompleteHandler());
        mMinimapOverlay.setWidth(ctx.getApplicationContext().getResources().getDisplayMetrics().widthPixels / 5);
        mMinimapOverlay.setHeight(ctx.getApplicationContext().getResources().getDisplayMetrics().heightPixels / 5);
        mMapView.getOverlays().add(this.mMinimapOverlay);


        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(ctx.getApplicationContext().getResources().getDisplayMetrics().widthPixels / 2, 10);
        mMapView.getOverlays().add(this.mScaleBarOverlay);

        //your items
//        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
//        items.add(new OverlayItem("??????", "?????????????????????", new GeoPoint(28.6579,115.8214))); // Lat/Lon decimal degrees
//        items.add(new OverlayItem("??????", "?????????????????????", new GeoPoint(28.6847,115.8549)));

        //the overlay
//        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
//                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
//                    @Override
//                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
//                        //do something
//                        return false;
//                    }
//                    @Override
//                    public boolean onItemLongPress(final int index, final OverlayItem item) {
//                        return false;
//                    }
//                }, ctx.getApplicationContext());
//        mOverlay.setFocusItemsOnTap(true);
//        mMapView.getOverlays().add(mOverlay);


//        final Polyline line = new Polyline(mMapView);
//        line.setColor(COLOR_POLYLINE_STATIC);
//        line.setWidth(LINE_WIDTH_BIG);
//        line.setPoints(mGeoPoints);
//        line.getPaint().setStrokeCap(Paint.Cap.ROUND);
//        mMapView.getOverlayManager().add(line);
        //?????????
//        Polyline polyline = new Polyline();
//        polyline.setColor(COLOR_POLYLINE_STATIC);
//        polyline.setPoints(getGeoPoints());
//        mMapView.getOverlays().add(polyline);


//      POI?????????  ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? POI????????????????????????????????????
        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
        ArrayList<POI> pois = poiProvider.getPOICloseTo(startPoint, "cinema", 50, 0.1);
//        ?????????????????????????????? POI???
//        ArrayList<POI> pois = poiProvider.getPOIAlong(road.getRouteLow(), "fuel", 50, 2.0);

//        ?????????????????? POI ???????????????????????????????????????
//
//        ??????????????????????????????????????????????????? POI ????????????????????????????????????????????????????????????????????????????????????????????????????????????
//
//        ????????????????????????????????????????????????????????????????????????
        FolderOverlay poiMarkers = new FolderOverlay(ctx.getApplicationContext());
        mMapView.getOverlays().add(poiMarkers);
//        ????????????????????????????????????????????????????????????????????????
        Drawable poiIcon = getResources().getDrawable(R.drawable.marker_poi_default);
        for (POI poi:pois){
            Marker poiMarker = new Marker(mMapView);
            poiMarker.setTitle(poi.mType);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setPosition(poi.mLocation);
            poiMarker.setIcon(poiIcon);
            if (poi.mThumbnail != null){
                poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
            }
            poiMarkers.add(poiMarker);
        }

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(ctx.getApplicationContext(), new MapEventsReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {//??????
                if(bt_status){
                    if(pointPaintlist==null){
                        pointPaintlist = new ArrayList<>();
                        
                    }
                    if(pointPaintLinelist==null){
                        pointPaintLinelist = new ArrayList<>();
                    }
                    if(markerNodeList==null){
                        markerNodeList = new ArrayList<>();
                    }
                    //???????????????
                    nodePaint = new Marker(mMapView);
                    nodePaint.setPosition(p);
                    nodePaint.setIcon(getResources().getDrawable(R.drawable.marker_node));

                    pointPaintlist.add(p);//???
                    markerNodeList.add(nodePaint);//??????
                    //2?????????????????????polyline
                    int tar = 0;
                    if(markerNodeList.size()>=2) {//????????????????????????
                        //------------------------------------------------------
                        //----
                        if (crList != null) {
                            for (GeoPoint g : crList) {
                                if (2000 >
                                        getNearestDistance(pointPaintlist.get(pointPaintlist.size() - 2).getLongitude(),
                                                pointPaintlist.get(pointPaintlist.size() - 2).getLatitude(),
                                                pointPaintlist.get(pointPaintlist.size() - 1).getLongitude(),
                                                pointPaintlist.get(pointPaintlist.size() - 1).getLatitude(),
                                                g.getLongitude(),
                                                g.getLatitude())) {
                                      tar = 1 ;
                                      break;
                                }
                            }
                            if(tar == 1){
                                    markerNodeList.remove(markerNodeList.size() - 1);
                                    pointPaintlist.remove(pointPaintlist.size() - 1);
                            } else {
                                printInformationOnMap(p);
                            }
                        } else {
                            printInformationOnMap(p);
                        }
                     }
                    else {
                        mMapView.getOverlays().add(markerNodeList.get(markerNodeList.size() - 1));
                        listChangeListener.fragment1Change(p);
                    }
                    return true;
                }
                if(btArea_status){
                    //------
                    if(circleList == null){
                        circleList = new ArrayList<>();
                    }
                    circle = new Polygon(mMapView);

                    circle.setPoints(Polygon.pointsAsCircle(p, 2000));
                    circle.setFillColor(0x12121212);
                    circle.setStrokeColor(Color.RED);
                    circle.setStrokeWidth(2);
                    circle.setTitle("Center on "+p.getLatitude()+","+p.getLongitude()+"radiusMeter=2000");

                    crList.add(p);
                    circleList.add(circle);
                    mMapView.getOverlays().add(circle);
                    mMapView.invalidate();

                }
                //----
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {//??????
                lt = p.getLatitude();
                lon = p.getLongitude();
                if(longPressMarker !=null){
                    mMapView.getOverlays().remove(longPressMarker);
                }
                longPressMarker = new Marker(mMapView);
                longPressMarker.setPosition(p);
                longPressMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                longPressMarker.setTitle(""+p.getLatitude()+","+p.getLongitude());

                /***
                 *
                 * ??????????????????
                 */

//                QWeather.getWeatherNow(ctx.getApplicationContext(), "CN101010100", Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.i("MainActivity", "getWeather onError: " + e);
//                    }
//
//                    @Override
//                    public void onSuccess(WeatherNowBean weatherBean) {
//                        Log.i("MainActivity", "getWeather onSuccess: " + new Gson().toJson(weatherBean));
//                        //??????????????????status??????????????????status???????????????????????????status?????????????????????status?????????Code???????????????
//                        if (Code.OK == weatherBean.getCode()) {
//                            WeatherNowBean.NowBaseBean now = weatherBean.getNow();
//                        } else {
//                            //???????????????????????????????????????
//                            Code code = weatherBean.getCode();
//                            Log.i("MainActivity", "failed code: " + code);
//                        }
//                    }
//                });
//                QWeather.getWarning(ctx.getApplicationContext(), 117.03 + "," +28.24, new QWeather.OnResultWarningListener() {
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Log.i("MainActivity", "getWeather onError: " );
//                    }
//
//                    @Override
//                    public void onSuccess(WarningBean warningBean) {
//                        Log.i("MainActivity", "getWeather onSuccess: " + new Gson().toJson(warningBean));
//                        if (Code.OK == warningBean.getCode()) {
//                            WarningBean.WarningBeanBase warningBeanBase = new WarningBean.WarningBeanBase();
//                            Log.i("MainActivity", "xxxxxxxxxxxxxxxxxx: " + warningBeanBase.getText());
//                        }
//                    }
//                });

                mMapView.getOverlays().add(longPressMarker);
                return true;
            }
        });
        mMapView.getOverlays().add(0, mapEventsOverlay);

        button_query.setOnClickListener(this);
        button_helpme.setOnClickListener(this);
        button_area.setOnClickListener(this);
        button_me.setOnClickListener(this);
        button_target.setOnClickListener(this);
        bt_back.setOnClickListener(this);
        bt_pencil.setOnClickListener(this);

    }
    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mMapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mMapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this.getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this.getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this.getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    //?????????????????????x???y  ,??????????????????x?????????longgitude
    public double getY(GeoPoint p1,GeoPoint p2,double x){
        double x1=p1.getLongitude();
        double y1=p1.getLatitude();
        double x2=p2.getLongitude();
        double y2=p2.getLatitude();
        double k = (y2 - y1)/(x2 - x1);
        double b = y2 - k*x2;
        return x*k+b;
    }


    private  double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * ?????????????????????????????????double?????????????????????????????????????????????
     */
    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = (s * 10000) / 10;
        return s;
    }


    /***
     * ???????????????????????????
     * @param PAx  ??????a??????lon
     * @param PAy   ??????a??????la
     * @param PBx   ??????b??????lon
     * @param PBy   ??????b??????la
     * @param PCx   ????????????lon
     * @param PCy   ????????????la
     * @return
     */
    double getNearestDistance(double PAx, double PAy,double PBx, double PBy,double PCx, double PCy) {
        double a, b, c;
        a = getDistance(PAy, PAx, PBy, PBx);//??????????????????????????????????????????
        b = getDistance(PBy, PBx, PCy, PCx);//??????????????????????????????????????????
        c = getDistance(PAy, PAx, PCy, PCx);//??????????????????????????????????????????
        if (b * b >= c * c + a * a) return c;
        if (c * c >= b * b + a * a) return b;
        double l = (a + b + c) / 2;     //???????????????
        double s = Math.sqrt(l * (l - a) * (l - b) * (l - c));  //?????????????????????
        return 2 * s / a;
    }

    public void printInformationOnMap(GeoPoint p){
        polyline = new Polyline();
        polyline.setWidth(2.0f);
        polyline.setPoints(pointPaintlist);
        pointPaintLinelist.add(polyline);//???
        listChangeListener.fragment1Change(p);
        mMapView.getOverlays().add(pointPaintLinelist.get(pointPaintLinelist.size() - 1));//??????????????????
        mMapView.getOverlays().add(markerNodeList.get(markerNodeList.size() - 1));
    }

    public Point solve(GeoPoint c,GeoPoint p){
        double r =0; // ????????????
        PointF E = new PointF();
        PointF F = new PointF();
        PointF G = new PointF();
        PointF H = new PointF();
        //????????????????????????
        Point p1 = mMapView.getProjection().toPixels(c,null);
        //????????????????????????
        Point p2 = mMapView.getProjection().toPixels(p,null);
        //????????????????????????
        r = mMapView.getProjection().metersToEquatorPixels(2000);

        E.x = p2.x-p1.x;
        E.y = p2.y-p1.y;

        double t = r / Math.sqrt(E.x * E.x + E.y * E.y);
        F.x =(float)(E.x * t);  F.y =(float)(E.y * t);

        double a = Math.acos(t);

        G.x = (float)(F.x*Math.cos(a) - F.y*Math.sin(a));
        G.y = (float)(F.x*Math.sin(a) + F.y*Math.cos(a));

        H.x = G.x + p1.x;
        H.y = G.y + p1.y;

        return new Point((int)(H.x),(int)(H.y));
    }

}
