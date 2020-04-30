package com.anguyen.mymap.commons

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


fun isGPSEnabled(from: Context): Boolean {
    val mLocationManager = from.getSystemService(Context.LOCATION_SERVICE)
            as LocationManager
    return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

/**
 * Request location permission, so that we can get the location of the
 * device. The result of the permission request is handled by a callback,
 * onRequestPermissionsResult.
 */
fun isAccessFineLocationGranted(from: Context): Boolean{
    return ContextCompat.checkSelfPermission(
        from,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun isAccessCoarseLocationGranted(from: Context): Boolean{
    return ContextCompat.checkSelfPermission(
        from,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun isAccessBackgroundLocationGranted(from: Context): Boolean{
    return ContextCompat.checkSelfPermission(
        from,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun isLocationPermissionGranted(from: Context): Boolean {
    return (isAccessFineLocationGranted(from)
                and (isAccessCoarseLocationGranted(from))
                and(isAccessBackgroundLocationGranted(from))
            )
}

fun getLocationPermissionGranted(from: Context, permissionGrantedFunc:() -> Unit){
    if(!isLocationPermissionGranted(from)) {
        ActivityCompat.requestPermissions(
            from as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }else{
        permissionGrantedFunc()
    }
}

fun isNetworkConnected(from: Context): Boolean?{
    (from.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } ?: false
        } else {
            activeNetworkInfo != null && activeNetworkInfo!!.isConnected
        }
    }
}
