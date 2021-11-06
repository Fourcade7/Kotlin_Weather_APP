package com.example.kotlin_weather_app

import android.Manifest
import android.annotation.SuppressLint

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.kotlin_weather_app.Constants
import kotlinx.android.synthetic.main.activity_main.*

import android.provider.Settings
import android.widget.Toast

import com.permissionx.guolindev.PermissionX

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

import android.content.IntentSender
import android.content.IntentSender.SendIntentException

import com.google.android.gms.common.api.ResolvableApiException

import com.google.android.gms.common.api.ApiException
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnSuccessListener
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import com.example.kotlin_weather_app.Response.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


//for permission
lateinit var locationRequest: LocationRequest
  val REQUEST_CHECK_SETTINGS = 10001;
//for current location
 lateinit var fusedLocationClient: FusedLocationProviderClient
 var lat:Double=0.0
 var lon:Double=0.0

 class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        window.statusBarColor= Color.WHITE
//        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        check()
       floatingactionbutton.setOnClickListener {
           check()
       }





    }


     fun check(){
         if (Constants.isNetworkAvailable(this@MainActivity) && Constants.isLocationAvailable(this@MainActivity)){
             Toast.makeText(this@MainActivity, "Internet and Location Yes", Toast.LENGTH_LONG).show()

             lastlocation()

         }
         if (Constants.isNetworkAvailable(this@MainActivity)){
             Toast.makeText(this@MainActivity, "Internet Yes", Toast.LENGTH_LONG).show()

         }else{
             Toast.makeText(this@MainActivity, "Internet No", Toast.LENGTH_LONG).show()

         }
         if (Constants.isLocationAvailable(this@MainActivity)){
             Toast.makeText(this@MainActivity, "Location Yes", Toast.LENGTH_LONG).show()

         }else{
             Toast.makeText(this@MainActivity, "Location No", Toast.LENGTH_LONG).show()
             turnGPSOn()

         }
     }


    fun lastlocation(){
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)

            return
        }
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it!=null){
                    lat=it.latitude
                    lon=it.longitude
                    gorequest()
                    Toast.makeText(this@MainActivity,"${it.latitude}  ${it.longitude}", Toast.LENGTH_SHORT).show()

                    Log.d("Pr","${it.latitude}  ${it.longitude}")
                }
            }
    }














    fun permissionbuilder(){
        PermissionX.init(this@MainActivity)
            .permissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            .explainReasonBeforeRequest()
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this@MainActivity, "All permissions are granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }


    }





    fun turnGPSOn() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(applicationContext).checkLocationSettings(builder.build())

        result.addOnCompleteListener(object :OnCompleteListener<LocationSettingsResponse>{

            override fun onComplete(p0: Task<LocationSettingsResponse>) {
                try {
                    val response: LocationSettingsResponse=p0.getResult(ApiException::class.java)
                    Toast.makeText(this@MainActivity, "GPS is already tured on", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val resolvableApiException = e as ResolvableApiException
                            resolvableApiException.startResolutionForResult(
                                this@MainActivity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (ex: SendIntentException) {
                            ex.printStackTrace()
                            Toast.makeText(this@MainActivity, "GPS is already tured 1", Toast.LENGTH_SHORT)

                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                        }
                    }
                }

            }

        })

    }


     fun gorequest(){
         val cal:Call<WeatherResponse>?=Constants.getApi().getWeather(lat, lon)
         cal?.enqueue(object :Callback<WeatherResponse>{

             override fun onResponse(
                 call: Call<WeatherResponse>,
                 response: Response<WeatherResponse>
             ) {
                 if (response.isSuccessful){

                        val weatherResponse=response.body()
                     var temp=weatherResponse?.main?.temp
                     val decimalformat=DecimalFormat("#.##")
                    // decimalformat.roundingMode=RoundingMode.CEILING
                    decimalformat.format(temp)


                     if (weatherResponse?.main?.temp!!-273.0>0){
                         textview1.setText("+${temp!!-273.00}°C")
                     }else{
                         textview1.setText("${temp!! -273.00}°C")
                     }
                         val calendar=Calendar.getInstance()
                        var hour=calendar.get(Calendar.HOUR_OF_DAY)
                        var minute=calendar.get(Calendar.MINUTE)

                        textview2.setText(weatherResponse?.weather!!.get(0).main)
                        textview3.setText("${weatherResponse?.sys?.country} ${weatherResponse?.name}")
                        textview4.text="${weatherResponse?.wind?.speed!!} m/s"
                     textview5.text="Weather ${weatherResponse?.weather!!.get(0).description}"
                     when(weatherResponse?.weather!!.get(0).description){

                         "clear sky"-> {

                             if(hour>18 || hour<7) {
                                 lottieanimationview4.setAnimation("moon.json")
                                 lottieanimationview4.playAnimation()

                                 lottieanimationview1.setAnimation("moon.json")
                                 lottieanimationview1.playAnimation()

                             }else{
                                 lottieanimationview4.setAnimation("sun.json")
                                 lottieanimationview4.playAnimation()
                                 lottieanimationview1.setAnimation("sun.json")
                                 lottieanimationview1.playAnimation()

                             }
                         }
                         "rain"->lottieanimationview4.setAnimation("storm.json")
                         "snow"->lottieanimationview4.setAnimation("snow.json")
                         "mist"->lottieanimationview4.setAnimation("mist.json")

                     }

                 }
             }

             override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                 Toast.makeText(this@MainActivity, "try again", Toast.LENGTH_SHORT).show()
             }
         })
     }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                RESULT_OK -> {
                    Toast.makeText(this, "GPS is tured on", Toast.LENGTH_SHORT).show()

                    Toast.makeText(this, "GPS required to be tured on", Toast.LENGTH_SHORT).show()
                }
                RESULT_CANCELED -> Toast.makeText(
                    this,
                    "GPS required to be tured on",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }








}