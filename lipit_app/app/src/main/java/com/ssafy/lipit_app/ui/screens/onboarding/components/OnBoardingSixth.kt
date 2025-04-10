package com.ssafy.lipit_app.ui.screens.onboarding.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.util.calculateFontSize

@Composable
fun OnBoardingSixth(onNext: () -> Unit) {
    // 화면 높이와 너비 가져오기
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // 상대적인 크기 계산
    val titleFontSize = calculateFontSize(screenHeight, 0.03f)
    val subtitleFontSize = calculateFontSize(screenHeight, 0.018f)
    val buttonFontSize = calculateFontSize(screenHeight, 0.024f)
    val permissionTitleFontSize = calculateFontSize(screenHeight, 0.021f)
    val permissionItemFontSize = calculateFontSize(screenHeight, 0.018f)
    val permissionStatusFontSize = calculateFontSize(screenHeight, 0.016f)
    val noticeFontSize = calculateFontSize(screenHeight, 0.016f)

    // 상대적인 여백 계산
    val topSpacerHeight = screenHeight * 0.15f
    val subtitleTopSpacerHeight = screenHeight * 0.04f
    val permissionItemVerticalPadding = screenHeight * 0.012f
    val noticeTopSpacerHeight = screenHeight * 0.06f
    val horizontalPadding = screenWidth * 0.08f
    val buttonHeight = screenHeight * 0.1f

    // 아이콘 크기 계산
    val iconSize = screenHeight * 0.03f

    val context = LocalContext.current
    var showPermissionToast by remember { mutableStateOf(false) }

    // 권한 상태 관리
    var notificationPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Android 13 미만에서는 알림 권한이 필요하지 않음
            }
        )
    }

    var microphonePermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var storagePermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    // 모든 권한이 허용되었는지 확인
    fun areAllPermissionsGranted(): Boolean {
        // 실시간으로 권한 상태 갱신
        microphonePermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        storagePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 13 미만에서는 알림 권한이 필요하지 않음
        }

        return microphonePermissionGranted && storagePermissionGranted &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || notificationPermissionGranted)
    }

    // 개별 권한 요청 런처들
    val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        notificationPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "알림 권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "알림 기능 사용을 위해 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    val requestMicrophonePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        microphonePermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "마이크 권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "통화 및 음성 인식을 위해 마이크 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        storagePermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "저장소 권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "사진 등록을 위해 저장소 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 다중 권한 요청 런처
    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            when (permission) {
                Manifest.permission.RECORD_AUDIO -> {
                    microphonePermissionGranted = isGranted
                }
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        storagePermissionGranted = isGranted
                    }
                }
                Manifest.permission.READ_MEDIA_IMAGES -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        storagePermissionGranted = isGranted
                    }
                }
                Manifest.permission.POST_NOTIFICATIONS -> {
                    notificationPermissionGranted = isGranted
                }
            }
        }
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    // 모든 필요한 권한 요청 함수
    fun requestMissingPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!microphonePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (!storagePermissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            multiplePermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionGranted) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            Toast.makeText(context, "설정 화면에서 알림 권한을 활성화해주세요", Toast.LENGTH_LONG).show()
            context.startActivity(intent)
        }
    }

    // 모든 필요한 권한 요청 함수
    fun requestAllPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!microphonePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (!storagePermissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            multiplePermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    // 권한 체크 및 요청
    LaunchedEffect(Unit) {
        requestAllPermissions()
    }

    // 토스트 메시지 표시 효과
    LaunchedEffect(showPermissionToast) {
        if (showPermissionToast) {
            Toast.makeText(context, "필수 권한을 모두 허용해야 진행할 수 있습니다", Toast.LENGTH_LONG).show()
            showPermissionToast = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_onboarding),
                contentScale = ContentScale.FillBounds
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .windowInsetsPadding(WindowInsets.ime)
            .imePadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(topSpacerHeight))

            Text(
                text = "앱 사용을 위해\n접근 권한을 허용해주세요",
                color = Color.White,
                fontSize = titleFontSize,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Start,
                lineHeight = titleFontSize * 1.6f
            )

            Spacer(modifier = Modifier.height(subtitleTopSpacerHeight))

            Text(
                text = "필수 권한",
                color = Color.White.copy(0.7f),
                fontSize = permissionTitleFontSize,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(permissionItemVerticalPadding))

            // 권한 상태 표시
            CustomText(
                image = R.drawable.ic_bell,
                title = "알림",
                content = "통화 수신 및 부재중 알림 발송",
                isGranted = notificationPermissionGranted,
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                iconSize = iconSize,
                titleFontSize = permissionItemFontSize,
                contentFontSize = permissionItemFontSize,
                statusFontSize = permissionStatusFontSize,
                verticalPadding = permissionItemVerticalPadding
            )

            CustomText(
                image = R.drawable.ic_camera,
                title = "갤러리",
                content = "커스텀 보이스 사진 등록",
                isGranted = storagePermissionGranted,
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                },
                iconSize = iconSize,
                titleFontSize = permissionItemFontSize,
                contentFontSize = permissionItemFontSize,
                statusFontSize = permissionStatusFontSize,
                verticalPadding = permissionItemVerticalPadding
            )

            CustomText(
                image = R.drawable.ic_microphone,
                title = "마이크",
                content = "통화 및 음성 인식",
                isGranted = microphonePermissionGranted,
                onRequestPermission = {
                    requestMicrophonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                iconSize = iconSize,
                titleFontSize = permissionItemFontSize,
                contentFontSize = permissionItemFontSize,
                statusFontSize = permissionStatusFontSize,
                verticalPadding = permissionItemVerticalPadding
            )

            Spacer(modifier = Modifier.height(noticeTopSpacerHeight))

            Text(
                text = "필수 권한의 경우 허용하지 않으면 주요 기능 사용이\n" +
                        "불가능하여 서비스 이용이 제한됩니다.",
                color = Color(0xffC494D9),
                fontSize = noticeFontSize,
                fontWeight = FontWeight.Light,
                lineHeight = noticeFontSize * 2.0f
            )
        }

        // 하단 버튼 - 박스 맨 아래에 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .align(Alignment.BottomCenter)
                .background(Color(0xff603981))
                .clickable(onClick = {
                    // 모든 권한이 허용되었는지 확인
                    val allGranted = areAllPermissionsGranted()

                    if (allGranted) {
                        // 모든 권한이 허용되었으면 다음으로 진행
                        onNext()
                    } else {
                        // 권한이 없으면 권한 요청 다이얼로그 표시 후 토스트 메시지
                        requestMissingPermissions()
                        openAppSettings()
                        showPermissionToast = true
                    }
                }),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = buttonFontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = buttonHeight * 0.3f)
            )
        }
    }
}

@Composable
fun CustomText(
    image: Int,
    title: String,
    content: String,
    isGranted: Boolean = false,
    onRequestPermission: () -> Unit = {},
    iconSize: Dp,
    titleFontSize: TextUnit,
    contentFontSize: TextUnit,
    statusFontSize: TextUnit,
    verticalPadding: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding)
            .clickable(onClick = onRequestPermission),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )

        Text(
            text = title,
            color = Color.White,
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = content,
            color = Color(0xffC494D9),
            fontSize = contentFontSize
        )

        Spacer(modifier = Modifier.weight(1f))

        // 권한 상태 표시
        Text(
            text = if (isGranted) "허용됨" else "허용 필요",
            color = if (isGranted) Color.Green else Color(0xFFFFAA00),
            fontSize = statusFontSize,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BoardingSixthPreview() {
    OnBoardingSixth {
    }
}