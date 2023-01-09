package com.juice.seoulpubliclibreries

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.juice.seoulpubliclibreries.data.Library
import com.juice.seoulpubliclibreries.data.Row
import com.juice.seoulpubliclibreries.databinding.ActivityMapsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var clusterManager: ClusterManager<Row>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //클러스트 매니저 세팅
        clusterManager = ClusterManager(this, mMap)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        loadLibraries()
    }

    fun loadLibraries() {
        val retrofit = Retrofit.Builder()
            .baseUrl(SeoulOpenApi.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val seoulOpenService = retrofit.create(SeoulOpenService::class.java)
        seoulOpenService
            .getLibrary(SeoulOpenApi.API_KEY)
            .enqueue(object : Callback<Library> {
                override fun onResponse(call: Call<Library>, response: Response<Library>) {
                    showLibraries(response.body() as Library)
                }

                override fun onFailure(call: Call<Library>, t: Throwable) {
                    Toast.makeText(
                        baseContext, "서버에서 데이터를 가져올 수 없습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun showLibraries(libraries: Library){
        val latLngBounds = LatLngBounds.Builder()
        for (lib in libraries.SeoulPublicLibraryInfo.row) {

            clusterManager.addItem(lib)
            val position = LatLng(lib.XCNTS.toDouble(), lib.YDNTS.toDouble())
            //클러스트 전
            /*val marker = MarkerOptions().position(position).title(lib.LBRRY_NAME)

            var obj = mMap.addMarker(marker)
            obj.tag = lib.HMPG_URL

            mMap.setOnMarkerClickListener {
                if (it.tag != null) {
                    var url = it.tag as String
                    if (!url.startsWith("http")) {
                        url = "http://${url}"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
                true
            }*/

//            latLngBounds.include(marker.position)
            latLngBounds.include(position)
        }

        val bounds = latLngBounds.build()
        val padding = 0
        val updated = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(updated)
    }

}