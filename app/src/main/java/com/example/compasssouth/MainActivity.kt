package com.example.compasssouth

import android.Manifest
import android.content.Context
import android.content.Intent
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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
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
    private val TAG = "MainActivity"

    companion object {
        var cooklinglist : MutableList<String> = mutableListOf("牛肉", "拉麵", "restaurant", "麵", "飯", "便利商店", "food", "咖哩", "飲料", "摩斯", "肯德基", "麥當勞", "速食", "pizza", "壽司", "牛排", "水餃", "鍋貼", "八方雲集")
        var randomAlready : MutableList<String> = mutableListOf()
    }

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

        Log.w(TAG, "64");
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, CookingSetActivity::class.java).apply {
                    putExtra("26key", "26value")
                }
                startActivity(intent)
                return true
            }
            // Otherwise, do nothing and use the core event handling

            // when clauses require that all possible paths be accounted for explicitly,
            // for instance both the true and false cases if the value is a Boolean,
            // or an else to catch all unhandled cases.
            else -> super.onOptionsItemSelected(item)
        }
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

            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Log.w(TAG, "124");
            }else{
                Log.w(TAG, "126");
                LOCATION_PERMISSION = true
            }

//            AlertDialog.Builder(this)
//                .setTitle("提醒")
//                .setMessage("請啟用位置權限")
//                .setCancelable(true)
//                .setPositiveButton(
//                    "我知道了"
//                ) { dialog, which -> dialog.cancel() }
//                .show()
        } else {
            Log.w(TAG, "139");
            LOCATION_PERMISSION = true
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
        var eastsouthwestnorth_list = listOf("東","東南","南","西南","西","西北","北","東北")
        var eastsouthwestnorth_text_position = listOf(textViewUp,textViewRight,textViewDown,textViewLeft)

        fun match_eastsouthwestnorth(start_index:Int){
            var start_index=start_index
            eastsouthwestnorth_text_position.forEach { item->
                if( start_index >= eastsouthwestnorth_list.size){
                    start_index -= eastsouthwestnorth_list.size
                }
                item.text =eastsouthwestnorth_list[start_index]
                start_index += 2
            }
        }

        if (sn_degree >= 165 && sn_degree <= 195) {
            // south
            eastsouthwestnorth_dialog.setTitle("南" + sn_degree.toString())
            match_eastsouthwestnorth(2)
            /*textViewUp.text = eastsouthwestnorth_list[2]
            textViewRight.text = eastsouthwestnorth_list[4]
            textViewDown.text = eastsouthwestnorth_list[6]
            textViewLeft.text = eastsouthwestnorth_list[0]*/
        } else if (sn_degree >= 345 || sn_degree <= 15) {
            // north
            eastsouthwestnorth_dialog.setTitle("北" + sn_degree.toString())
            match_eastsouthwestnorth(6)
        } else if (sn_degree >= 75 && sn_degree <= 105) {
            //east
            eastsouthwestnorth_dialog.setTitle("東" + sn_degree.toString())
            match_eastsouthwestnorth(0)
        } else if (sn_degree >= 255 && sn_degree <= 285) {
            //west
            eastsouthwestnorth_dialog.setTitle("西" + sn_degree.toString())
            match_eastsouthwestnorth(4)
        } else if (sn_degree > 285 && sn_degree < 345) {
            //west
            eastsouthwestnorth_dialog.setTitle("西北" + sn_degree.toString())
            match_eastsouthwestnorth(5)
        } else if (sn_degree > 15 && sn_degree < 75) {
            //west
            eastsouthwestnorth_dialog.setTitle("東北" + sn_degree.toString())
            match_eastsouthwestnorth(7)
        } else if (sn_degree > 105 && sn_degree < 165) {
            //west
            eastsouthwestnorth_dialog.setTitle("東南" + sn_degree.toString())
            match_eastsouthwestnorth(1)
        } else if (sn_degree > 195 && sn_degree < 255) {
            //west
            eastsouthwestnorth_dialog.setTitle("西南" + sn_degree.toString())
            match_eastsouthwestnorth(3)
        }


//        eastsouthwestnorth_dialog.show();

//        AlertDialog.Builder(this@MainActivity)
//            .setMessage(values[0].toString())
//            .setTitle("Orientation")
//            .show()
    }

    object readcookingUtils {
        fun readcookinglist(context: Context): MutableList<String>{
            var getcookinglist=context.getSharedPreferences("cookinglist", MODE_PRIVATE)
                .getStringSet("cookinglist",setOf<String>())?.toMutableList()
            if (getcookinglist != null && getcookinglist.size>0) {
                cooklinglist = getcookinglist
                return cooklinglist
            }else{
                return cooklinglist
            }
        }
    }

    fun randomElementUsingSequences(): String? {
        var list = readcookingUtils.readcookinglist(this)
        Log.w(TAG, "180")
        Log.w(TAG, list.toString())
        list.removeAll(randomAlready)
        if (list != null) {
            var randomItemforcheck = list.asSequence().shuffled().find { true }.toString()
            list.remove(randomItemforcheck)
            randomAlready.add(randomItemforcheck)
            if( list.size == 0){
                randomAlready.clear()
            }
            /*Toast.makeText(this, list.toString(), Toast.LENGTH_SHORT).show()
            Toast.makeText(this, randomAlready.toString(), Toast.LENGTH_SHORT).show()*/
            return randomItemforcheck
        }else{
            return "172"
        }
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
            Log.w(TAG, "239");
            Log.w(TAG, randomElement.toString());

            val dynamicUrl = "https://www.google.com/maps/search/"+randomElement+"/@"+latitudePluslongitude+",18z"
            // or whatever you want, it's dynamic
            val linkedText = String.format("<a href=\"%s\">今天吃\"%s\"</a> ", dynamicUrl,randomElement)
            val vWeb = findViewById<TextView>(R.id.vWeb)
            vWeb.setText(Html.fromHtml(linkedText))
            vWeb.setMovementMethod(LinkMovementMethod.getInstance())
        }else{
            Toast.makeText(this, LocalDateTime.now().toString(), Toast.LENGTH_SHORT).show()
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
//        Toast.makeText(this, onResumewhytwice.toString(), Toast.LENGTH_SHORT).show()
        if(LOCATION_PERMISSION){
            locationManager()
        }
    }

    override fun onBackPressed() {
        finishAffinity();
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