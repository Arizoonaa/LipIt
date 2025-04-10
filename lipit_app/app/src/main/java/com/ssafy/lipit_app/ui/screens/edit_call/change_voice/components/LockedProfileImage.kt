package com.ssafy.lipit_app.ui.screens.edit_call.change_voice.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.lipit_app.R

/**
 * 잠금 표시가 있는 프로필 이미지 컴포넌트
 *
 * @param imageUrl 프로필 이미지 URL
 * @param isLocked 잠금 여부
 * @param size 이미지 크기
 * @param modifier 추가 수정자
 */
@Composable
fun LockedProfileImage(
    imgUrl: String,
    size: Int = 90
) {
    Box(
        modifier = Modifier
            .size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        // Box 내부에 이미지와 오버레이 배치
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        ) {
            // 프로필 이미지
            AsyncImage(
                model = imgUrl,
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = painterResource(id = R.drawable.img_add_image)
            )

            // 잠금 상태일 때 반투명 오버레이 추가

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.4f))
            )
        }

        // 잠금 상태일 때 자물쇠 아이콘 표시

        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = "잠금",
                modifier = Modifier.size((size / 1.5).dp)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LockedProfileImagePreview() {
    Surface {
        Box(
            modifier = Modifier.padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            LockedProfileImage(
                imgUrl = "R.drawable.profile_img",
                size = 100
            )
        }
    }
}