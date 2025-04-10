package com.ssafy.lipit_app.ui.screens.edit_call.change_voice

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse
import com.ssafy.lipit_app.ui.screens.edit_call.change_voice.components.LockedProfileImage

@Composable
fun EditVoiceScreen(
    onBack: () -> Unit,
    onNavigateToAddVoice: () -> Unit,
    viewModel: EditVoiceViewModel = viewModel()
) {

    val state = viewModel.state.collectAsState().value
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDF8FF))
            .padding(top = 44.dp, start = 20.dp, end = 20.dp)
            .verticalScroll(scrollState),
    ) {

        BackHandler {
            onBack()
        }

        // 전체 제목
        Text(
            text = "My Voices",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),

                )
        )

        Spacer(modifier = Modifier.height(20.dp))

        //셀럽
        TextTitle("Celebrity")
        Spacer(modifier = Modifier.height(21.dp))


        CelebrityVoiceList(
            celebrityVoices = state.celebrityVoices,
            selectedVoiceName = state.selectedVoiceName,
            onSelectVoice = { voiceId, voiceName, voiceUrl ->
                viewModel.onIntent(
                    EditVoiceIntent.SelectVoice(
                        voiceId = voiceId,
                        voiceName = voiceName,
                        voiceUrl = voiceUrl
                    )
                )
            },
            onVoiceChange = { voiceId ->
                viewModel.onIntent(EditVoiceIntent.ChangeVoice(voiceId))
            }
        )

        Spacer(modifier = Modifier.height(50.dp))

        //커스텀
        TextTitle("Custom")
        Spacer(modifier = Modifier.height(21.dp))

        Log.d("EditVoiceScreen", "커스텀 목소리 수: ${state.customVoices}")

        CustomVoiceList(
            customVoices = state.customVoices,
            selectedVoiceName = state.selectedVoiceName,
            onSelectVoice = { voiceId, voiceName, voiceUrl ->
                viewModel.onIntent(
                    EditVoiceIntent.SelectVoice(
                        voiceId = voiceId,
                        voiceName = voiceName,
                        voiceUrl = voiceUrl
                    )
                )
            },
            onClickAddVoice = {
                Log.d("EditVoiceScreen", "음성 추가 버튼이 클릭되었습니다")
                viewModel.onIntent(EditVoiceIntent.NavigateToAddVoice)
                onNavigateToAddVoice()
            },
            onVoiceChange = { voiceId ->
                viewModel.onIntent(EditVoiceIntent.ChangeVoice(voiceId = voiceId))
            }
        )

        Spacer(modifier = Modifier.height(50.dp))

    }
}

@Composable
fun TextTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 20.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight(590),
            color = Color(0xFF222124),
        )
    )
}

@Composable
fun CelebrityVoiceList(
    celebrityVoices: List<CelabResponse>,
    selectedVoiceName: String,
    onSelectVoice: (Long, String, String) -> Unit,
    onVoiceChange: (Long) -> Unit
) {
    Log.d("CelebrityVoiceList", "셀럽 목소리 데이터: $celebrityVoices")
    val celebSize = celebrityVoices.size

    if (celebSize > 0) {

        val sortedVoices = celebrityVoices.sortedByDescending { it.activated }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(clip = false)
        ) {

            VerticalGrid(
                columns = SimpleGridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (index in 0..<sortedVoices.size) {
                    val voice = sortedVoices[index]
                    VoiceItem(
                        url = voice.customImageUrl,
                        name = voice.voiceName,
                        activated = voice.activated,
                        isSelected = voice.voiceName == selectedVoiceName,
                        onClick = {
                            onSelectVoice(voice.voiceId, voice.voiceName, voice.customImageUrl)
                        },
                        onVoiceChange = {
                            if (voice.activated) {
                                onVoiceChange(voice.voiceId)
                            }
                        }
                    )
                }
            }
        }
    } else {
        // 데이터가 없을 때 표시할 내용
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "셀럽 음성이 없습니다",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CustomVoiceList(
    customVoices: List<CustomResponse>,
    selectedVoiceName: String,
    onSelectVoice: (Long, String, String) -> Unit,
    onClickAddVoice: () -> Unit,
    onVoiceChange: (Long) -> Unit
) {
    Log.d("CustomVoiceList", "커스텀 목소리 데이터: $customVoices")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(clip = false)
    ) {

        VerticalGrid(
            columns = SimpleGridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (index in 0..<customVoices.size) {
                val voice = customVoices[index]
                VoiceItem(
                    url = voice.customImageUrl,
                    name = voice.voiceName,
                    activated = true,
                    isSelected = voice.voiceName == selectedVoiceName,
                    onClick = {
                        onSelectVoice(voice.voiceId, voice.voiceName, voice.customImageUrl)
                    },
                    onVoiceChange = {
                        onVoiceChange(voice.voiceId)
                    }
                )
            }
            // 추가 버튼 아이템
            AddVoiceItem(onClickAddVoice = onClickAddVoice)

        }
    }
}

@Composable
fun AddVoiceItem(onClickAddVoice: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 10.dp)
                .graphicsLayer {
                    clip = true
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.edit_voice_plus_icon),
                contentDescription = "plus icon",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .clip(CircleShape)
                    .clickable {
                        onClickAddVoice()
                    }
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = "음성 추가",
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight(590),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun VoiceItem(
    url: String,
    name: String,
    activated: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onVoiceChange: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer(clip = false)
            .clickable(enabled = activated) {
                onVoiceChange()
                onClick()
            }
    ) {

        Box(
            modifier = Modifier
                .padding(start = 5.dp)
                .width(90.dp)
                .height(90.dp)
                .shadow(
                    elevation = if (isSelected) 10.dp else 0.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF9C27B0),
                    spotColor = Color(0xFF9C27B0)
                )
                .background(
                    color = if (isSelected) Color(0x33D372FF) else Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 4.dp else 0.dp,
                    color = Color(0xFFD372FF),
                    shape = CircleShape
                )
                .graphicsLayer {
                    clip = false // 그림자 잘리지 않게 설정!
                }
        ) {

            Log.d("VoiceItem", "Voice URL: $url")

            if (activated) {
                if (url.isNotEmpty()) {
                    AsyncImage(
                        model = url,
                        contentDescription = "프로필 사진",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        error = painterResource(
                            id = R.drawable.img_add_image
                        )
                    )
                } else {
                    // URL이 비어있을 경우 기본 이미지 표시
                    Image(
                        painter = painterResource(
                            id = R.drawable.img_add_image
                        ),
                        contentDescription = "기본 프로필",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            } else {
                // 비활성화된 상태: 잠금 이미지 표시
                LockedProfileImage(
                    imgUrl = url
                )
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = name,
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight(590),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(7.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun EditVoiceScreenPreview() {

    val previewState = EditVoiceState(
        selectedVoiceName = "Harry Potter",
        selectedVoiceUrl = "https://picsum.photos/600/400",
        celebrityVoices = listOf(
            CelabResponse(
                voiceId = 1L,
                voiceName = "Harry Potter",
                customImageUrl = "https://example.com/image1.jpg",
                audioUrl = "https://example.com/image2.mp3",
                activated = true
            ),
            CelabResponse(
                voiceId = 2L,
                voiceName = "Iron Man",
                customImageUrl = "https://example.com/image2.jpg",
                audioUrl = "https://example.com/image2.mp3",
                activated = false
            )
        ),
        customVoices = listOf(
            CustomResponse(
                voiceId = 3L,
                voiceName = "My Voice",
                customImageUrl = "https://example.com/image3.jpg",
                audioUrl = "https://example.com/image3.mp3"
            )
        )
    )

    EditVoiceScreen(
        onBack = {},
        onNavigateToAddVoice = {}
    )

}