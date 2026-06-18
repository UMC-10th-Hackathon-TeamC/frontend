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
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import java.util.Locale
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.umc.hackathon.frontend.feature.home.map.createDistrictMarkerBitmap
@Composable
fun HomeMap(
    districts: List<DistrictMosquitoIndex>,
    selectedDistrict: String?,
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

    /* 마커 중복 방지 */
    val markers = remember { mutableListOf<Marker>() }

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

    /* 서울시청 근처 좌표, 서울 전역이 보일 수 있는 수준의 줌
    *  지도 옆 +/- 표시 제외 나침반 표시 등 제외
    *  Layer 제외
    *  커스텀 지도 적용
    * */
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
        }
    }

    /* 선택된 구가 바뀔 때마다 마커를 다시 그려 선택 마커만 크게 표시 */
    LaunchedEffect(mapView, districts, selectedDistrict) {
        mapView.getMapAsync { naverMap ->
            markers.forEach { it.map = null }
            markers.clear()

            districts.forEach { district ->
                val selected = district.districtName == selectedDistrict

                val marker = Marker().apply {
                    position = LatLng(district.latitude, district.longitude)
                    icon = OverlayImage.fromBitmap(
                        createDistrictMarkerBitmap(
                            context = context,
                            districtName = district.districtName,
                            mosquitoIndex = district.mosquitoIndex,
                            level = district.level,
                            selected = selected
                        )
                    )
                    zIndex = if (selected) 10 else 0
                    setOnClickListener {
                        naverMap.moveCamera(
                            CameraUpdate.scrollTo(
                                LatLng(district.latitude, district.longitude)
                            ).animate(CameraAnimation.Easing, 500)
                        )
                        onDistrictClick(district.districtName)
                        true
                    }
                    map = naverMap
                }

                markers.add(marker)
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
