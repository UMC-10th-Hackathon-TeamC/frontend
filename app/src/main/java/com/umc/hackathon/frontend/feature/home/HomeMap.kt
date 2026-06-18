package com.umc.hackathon.frontend.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.naver.maps.map.MapView
import androidx.compose.runtime.LaunchedEffect
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition

@Composable
fun HomeMap(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val mapView = remember {
        MapView(context).apply {
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
            naverMap.cameraPosition = CameraPosition(
                LatLng(37.5665, 126.9780),
                10.5
            )
        }
    }

    /* 실제 지도 */
    AndroidView(
        modifier = modifier,
        factory = { mapView }
    )
}
