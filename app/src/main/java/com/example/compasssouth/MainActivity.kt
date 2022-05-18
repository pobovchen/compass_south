package com.example.compasssouth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mOrientationSensor: Sensor
    private lateinit var mMagneticSensor: Sensor
    private lateinit var mOrientationListener: MySensorEventListener
    private lateinit var mMagneticListener: MySensorEventListener
    private var mStopDrawing: Boolean = false
    private var accelerometerValues = FloatArray(3)
    private var magneticFieldValues = FloatArray(3)
    private val mHandler = Handler()
    private lateinit var mLocationManager: LocationManager
    var oriLocation : Location? = null
    val permissionGranted = 11
    var LOCATION_PERMISSION:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initServices()
        GPSPermission()

        MobileAds.initialize(
            this
        ) { }

        val mAdView = findViewById<AdView> (R.id.adView)
        val adRequest = AdRequest.Builder (). build ()
        mAdView.loadAd (adRequest)
    }

    fun GPSPermission(){
        // 取得 GPS 權限
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                permissionGranted
            )
//            AlertDialog.Builder(this)
//                .setTitle("提醒")
//                .setMessage("請啟用位置權限")
//                .setCancelable(true)
//                .setPositiveButton(
//                    "我知道了"
//                ) { dialog, which -> dialog.cancel() }
//                .show()
        } else {
            LOCATION_PERMISSION = true
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                permissionGranted
            )
        }
    }

    fun calculateOrientation() {
        val values = FloatArray(3)
        val RValues = FloatArray(9)
        SensorManager.getRotationMatrix(RValues, null, accelerometerValues, magneticFieldValues)
        SensorManager.getOrientation(RValues, values)
        values[0] = Math.toDegrees(values[0].toDouble()).toFloat()


        val eastsouthwestnorth_dialog = AlertDialog.Builder(this@MainActivity)
        val SnDegree = findViewById<TextView>(R.id.TextSnDegree)
        val ArrowOne = findViewById<TextView>(R.id.ArrowOne)
        val textViewUp = findViewById<TextView>(R.id.myTextUp)
        val textViewDown = findViewById<TextView>(R.id.myTextDown)
        val textViewRight = findViewById<TextView>(R.id.myTextRight)
        val textViewLeft = findViewById<TextView>(R.id.myTextLeft)
        //根据角度计算方位
        val sn_degree =  ((values[0]+ 720) % 360).toInt()
        SnDegree.text = sn_degree.toString()
        ArrowOne.rotation = 90-(sn_degree).toFloat()
        if (sn_degree >= 135 && sn_degree <= 225) {
            // south
            eastsouthwestnorth_dialog.setTitle("南" + sn_degree.toString())
            textViewUp.text = "南"
            textViewDown.text = "北"
            textViewRight.text = "西"
            textViewLeft.text = "東"
        } else if (sn_degree >= 315 || sn_degree <= 45) {
            // north
            eastsouthwestnorth_dialog.setTitle("北" + sn_degree.toString())
            textViewUp.text = "北"
            textViewDown.text = "南"
            textViewRight.text = "東"
            textViewLeft.text = "西"
        } else if (sn_degree > 45 && sn_degree < 135) {
            //east
            eastsouthwestnorth_dialog.setTitle("東" + sn_degree.toString())
            textViewUp.text = "東"
            textViewDown.text = "西"
            textViewRight.text = "南"
            textViewLeft.text = "北"
        } else if (sn_degree > 225 && sn_degree < 315) {
            //west
            eastsouthwestnorth_dialog.setTitle("西" + sn_degree.toString())
            textViewUp.text = "西"
            textViewDown.text = "東"
            textViewRight.text = "北"
            textViewLeft.text = "南"
        }


//        eastsouthwestnorth_dialog.show();

//        AlertDialog.Builder(this@MainActivity)
//            .setMessage(values[0].toString())
//            .setTitle("Orientation")
//            .show()
    }

    fun randomElementUsingSequences(): String? {
        var list = mutableListOf("restaurant", "麵", "飯", "便利商店", "food", "咖哩", "飲料", "摩斯", "肯德基", "麥當勞", "速食", "pizza", "壽司", "牛排", "水餃", "鍋貼", "八方雲集")
        return list.asSequence().shuffled().find { true }
    }

    fun locationManager(){
        var isGPSEnabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

//        AlertDialog.Builder(this@MainActivity)
//            .setMessage(isGPSEnabled.toString())
//            .setTitle("isGPSEnabled")
//            .show()

//        AlertDialog.Builder(this@MainActivity)
//            .setMessage(isNetworkEnabled.toString())
//            .setTitle("locationManager")
//            .show()

        if (!(isGPSEnabled || isNetworkEnabled)) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
        }else{
            if (isGPSEnabled) {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    6000, 5f, locationListener)
                oriLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    6000, 5f, locationListener)
                oriLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        }
        var hereLocation = oriLocation;
        if(hereLocation != null) {
            var latitudePluslongitude:String = hereLocation.latitude.toString()+","+hereLocation.longitude.toString()
//            val LocationText = findViewById<TextView>(R.id.LocationText)
//            LocationText.text = latitudePluslongitude

            var randomElement:String? = randomElementUsingSequences()
            val dynamicUrl = "https://www.google.com/maps/search/"+randomElement+"/@"+latitudePluslongitude+",18z"
            // or whatever you want, it's dynamic
            val linkedText = String.format("<a href=\"%s\">今天吃\"%s\"</a> ", dynamicUrl,randomElement)
            val vWeb = findViewById<TextView>(R.id.vWeb)
            vWeb.setText(Html.fromHtml(linkedText))
            vWeb.setMovementMethod(LinkMovementMethod.getInstance())
        }else{
            Toast.makeText(this, LocalDateTime.now().toString(), Toast.LENGTH_LONG).show()
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000, 5f, locationListener)
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,6000, 5f, locationListener)
        }

    }

    val locationListener = object : LocationListener{
        override fun onLocationChanged(location: Location) {
            if(oriLocation == null) {
                oriLocation = location
            }
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }
    }


    fun initServices() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) //加速度傳感器
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)   //地磁場傳感器

        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onResume() {
        super.onResume()
        mOrientationListener = MySensorEventListener()
        mMagneticListener = MySensorEventListener()
        mSensorManager.registerListener(mOrientationListener, mOrientationSensor, Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(mMagneticListener, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL)
        mStopDrawing = false
        if(LOCATION_PERMISSION){
            locationManager()
        }
    }

    override fun onPause() {
        super.onPause()
        mStopDrawing = true
        mSensorManager.unregisterListener(mOrientationListener)
        mSensorManager.unregisterListener(mMagneticListener)
    }

    inner class MySensorEventListener : SensorEventListener2 {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onFlushCompleted(sensor: Sensor?) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values
                calculateOrientation()
            }
        }

    }
}