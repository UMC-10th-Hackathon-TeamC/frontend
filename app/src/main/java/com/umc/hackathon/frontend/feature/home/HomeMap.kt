package com.umc.hackathon.frontend.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import java.util.Locale
import com.naver.maps.map.overlay.Marker

@Composable
fun HomeMap(
    districts: List<DistrictMosquitoIndex>,
    onDistrictClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val mapView = remember {
        val options = NaverMapOptions()
            .customStyleId(MOGI_MAP_STYLE_ID)
            .locale(Locale.KOREAN)

        MapView(context, options).apply {
            onCreate(null)
        }
    }

    /* 안드로이드 생명주기를 지도와 맞춤 */
    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    /* 서울시청 근처 좌표, 서울 전역이 보일 수 있는 수준의 줌*/
    LaunchedEffect(mapView) {
        mapView.getMapAsync { naverMap ->
            naverMap.locale = Locale.KOREAN
            naverMap.setCustomStyleId(MOGI_MAP_STYLE_ID)
            naverMap.uiSettings.isCompassEnabled = false
            naverMap.uiSettings.isScaleBarEnabled = false
            naverMap.uiSettings.isLocationButtonEnabled = false

            naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
            naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, false)
            naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
            naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, false)
            naverMap.cameraPosition = CameraPosition(
                LatLng(37.5665, 126.9780),
                10.5
            )

            districts.forEach { district ->
                Marker().apply {
                    position = LatLng(district.latitude, district.longitude)
                    captionText = "${district.districtName} ${district.mosquitoIndex}"
                    setOnClickListener {
                        onDistrictClick(district.districtName)
                        true
                    }
                    map = naverMap
                }
            }
        }
    }

    /* 실제 지도 */
    AndroidView(
        modifier = modifier,
        factory = { mapView }
    )
}

private const val MOGI_MAP_STYLE_ID = "c4404416-58c4-48f6-bd1f-cd4144752e68"
