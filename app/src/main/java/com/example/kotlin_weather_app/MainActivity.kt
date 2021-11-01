package com.example.kotlin_weather_app

import android.Manifest
import android.annotation.SuppressLint

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


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
import android.location.Address
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnSuccessListener
import android.location.Geocoder
import java.util.*


//for permission
lateinit var locationRequest: LocationRequest
  val REQUEST_CHECK_SETTINGS = 10001;

//for current location
 lateinit var fusedLocationClient: FusedLocationProviderClient







class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        button1.setOnClickListener {
        lastlocation()

        }

        button2.setOnClickListener {

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
                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
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
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                        }
                    }
                }

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