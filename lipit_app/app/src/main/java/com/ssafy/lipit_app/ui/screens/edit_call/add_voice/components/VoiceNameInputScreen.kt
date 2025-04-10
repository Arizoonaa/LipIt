package com.ssafy.lipit_app.ui.screens.edit_call.add_voice.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.AddVoiceIntent
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.AddVoiceState

@Composable
fun VoiceNameInputScreen(
    state: AddVoiceState,
    onIntent: (AddVoiceIntent) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // URI가 null이 아니면 Intent로 전송
        uri?.let {
            onIntent(AddVoiceIntent.SetVoiceImage(uri))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FF))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // 타이틀
        Text(
            text = "Voice Information",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222124)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 이미지 영역 - 클릭 시 갤러리 열기
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD6B7E8))
                    .clickable {
                        launcher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedImageUri != null) {
                    AsyncImage(
                        model = state.selectedImageUri,
                        contentDescription = "Selected Voice Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // 반투명 검정색 오버레이
                    Box() {
                        Image(
                            painter = painterResource(id = R.drawable.img_add_image),
                            contentDescription = "기본 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "plus icon",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                    }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "사진 추가",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }


        Spacer(modifier = Modifier.height(40.dp))

        // 음성 이름 입력 필드
        OutlinedTextField(
            value = state.voiceName,
            onValueChange = {
                onIntent(AddVoiceIntent.SetVoiceName(it))
            },
            label = { Text("Voice Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFFB282C5)
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // 저장 버튼
        Button(
            onClick = { onIntent(AddVoiceIntent.SubmitVoice) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB282C5),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFD6B7E8)
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !state.isUploading && state.voiceName.isNotBlank()
        ) {
            if (state.isUploading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "저장",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun VoiceNameInputScreenPreview() {
    val dummyState = AddVoiceState(
        voiceName = "예시 목소리",
        selectedImageUri = null, // 또는 Uri.parse("https://via.placeholder.com/150")
        isUploading = false
    )

    VoiceNameInputScreen(
        state = dummyState,
        onIntent = {}
    )
}