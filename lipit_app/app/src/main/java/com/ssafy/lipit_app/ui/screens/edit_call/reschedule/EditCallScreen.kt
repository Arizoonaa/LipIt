package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import kotlinx.coroutines.launch

@Composable
fun EditCallScreen(
    schedule: CallSchedule,
//    state: EditCallState,
    onBack: () -> Unit,
    onSuccess: (CallSchedule, Boolean) -> Unit
) {
    // 뒤로가기 이벤트 처리
    BackHandler { onBack() }

    val context = LocalContext.current
    val viewModel: EditCallViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditCallViewModel(
                    context = context
                ) as T
            }
        }
    )

//    val state by viewModel.state.collectAsState()

    //  schedule 초기값 추출
    val initialHour = schedule.scheduledTime.substringBefore(":").toIntOrNull() ?: 0
    val initialMinute =
        schedule.scheduledTime.substringAfter(":").substringBefore(":").toIntOrNull() ?: 0
    // 시간 상태 remember로 관리
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }


    // 스케줄 모드(수정or추가) & 카테고리 설정 관련 : 토픽이 없을 경우 '자유주제", 토픽이 있을 경우 해당카테고리 설정
    val isEditMode = schedule.callScheduleId != -1L

    val hasTopicCategory =
        !schedule.topicCategory.isNullOrBlank() && schedule.topicCategory != "자유주제"
    var selectedIndex by remember { mutableIntStateOf(if (hasTopicCategory) 1 else 0) }
    var selectedCategory by remember {
        mutableStateOf(
            if (schedule.topicCategory == "자유주제") "" else schedule.topicCategory ?: ""
        )
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDF8FF))
            .padding(top = 44.dp, start = 20.dp, end = 20.dp),
    ) {
        // 제목
        Text(
            text = "Reschedule Call",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // time picker
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            WheelTimePicker(
                hour = selectedHour,
                minute = selectedMinute,
                onHourChanged = { selectedHour = it },
                onMinuteChanged = { selectedMinute = it }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
        // topic(대화 주제) 타이틀

        Text(
            text = "Topic",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),

                )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 카테고리 선택 모달
        CustomSegmentedButtons(
            selectedIndex = selectedIndex,
            onSelected = {
                selectedIndex = it
                if (it == 0) selectedCategory = "" // 자유주제로 전환하면 선택한 카테고리 초기화
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 카테고리 선택 드롭다운
        if (selectedIndex == 1) {
            CategoryDropDownMenu(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        Spacer(modifier = Modifier.height(70.dp))


        val updatedSchedule = schedule.copy(
            scheduledTime = "%02d:%02d:00".format(selectedHour, selectedMinute),
            topicCategory = if (selectedIndex == 0 || selectedCategory == "자유주제") null else selectedCategory
        )
        EditCallButtons(
            schedule = updatedSchedule, // 최신값 반영된 객체 전달!
            isEditMode = isEditMode,
            onIntent = { intent ->
                viewModel.onIntent(intent)

                // onSuccess 콜백을 통해 일정 변경 및 알람 업데이트 이벤트 전달
                onSuccess(updatedSchedule, isEditMode)
            },
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(20.dp))

    }
}

@Composable
fun CategoryDropDownMenu(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    // 카테고리: 스포츠, 여행, 영화/책, 음식, 게임, 음악, 건강
    val options = listOf("스포츠", "여행", "영화/책", "음식", "게임", "음악", "건강")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent) // 박스 배경을 투명하게 설정
    ) {
        // 드롭다운을 여는 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE6E6E6),
                    shape = RoundedCornerShape(size = 15.dp)
                )
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFFFDF8FF))
                .clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedCategory.isEmpty()) "카테고리 선택" else selectedCategory,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF7D7D7D),
                ),
                modifier = Modifier
                    .padding(start = 18.dp)
            )

            // 드롭다운 화살표 아이콘
            Icon(
                painterResource(id = R.drawable.edit_call_dropdown_icon),
                contentDescription = "드롭다운",
                modifier = Modifier
                    .padding(end = 18.dp)
                    .width(14.dp)
                    .height(7.dp),
                tint = Color(0xFFD3D3D3)
            )
        }

        // 드롭다운 메뉴 - 오버레이로 표시
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) {
                    // 부모와 같은 너비로 설정
                    (LocalConfiguration.current.screenWidthDp - 40).dp
                })
                // 먼저 클리핑을 적용한 후 배경을 설정해야 합니다
//                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFFFDF8FF))
                .padding(vertical = 4.dp)
                .heightIn(max = 250.dp), // 높이 제한
            offset = DpOffset(0.dp, (-5).dp), // 약간 위로 올려서 겹치게 표시
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF222124),
                            )
                        )
                    },
                    onClick = {
                        onCategorySelected(label)
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        // 클리핑을 먼저 적용하고 배경 설정
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            if (selectedCategory == label) Color(0xFFF1F1F1) else Color(0xFFFDF8FF)
                        )
                )
            }
        }
    }
}

@Composable
fun WheelTimePicker(
    hour: Int, minute: Int, onHourChanged: (Int) -> Unit, onMinuteChanged: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn((0..23).map { it.toString().padStart(2, '0') }, hour, onHourChanged)
        Text(
            text = ":",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        WheelColumn((0..59).map { it.toString().padStart(2, '0') }, minute, onMinuteChanged)
    }
}

@Composable
fun WheelColumn(
    items: List<String>,
    initialIndex: Int,
    onSelectedChanged: (Int) -> Unit
) {
    // 패딩 항목을 고려하여 초기 인덱스 조정
    val paddingItemCount = 2
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex + paddingItemCount)
    val coroutineScope = rememberCoroutineScope()
    var selectedIndex by remember { mutableIntStateOf(initialIndex) }

    // 스크롤이 멈추면 가장 가까운 아이템으로 스냅
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            // 현재 스크롤 오프셋과 인덱스 기반으로 스냅할 위치 결정
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset

            // 아이템 높이가 40dp라면, 절반(20dp)을 넘었는지 확인
            val targetIndex = if (offset > 20) {
                firstVisibleIndex + 1
            } else {
                firstVisibleIndex
            }

            // 실제 선택된 값의 인덱스 계산 (LazyColumn에 패딩 항목이 있으므로 조정)
            val adjustedIndex = (targetIndex - paddingItemCount).coerceIn(0, items.size - 1)

            // 조정된 인덱스에 패딩을 다시 더해 실제 스크롤할 위치 계산
            val scrollToIndex = adjustedIndex + paddingItemCount

            // 스크롤 애니메이션 (범위 내 인덱스로만 스크롤)
            coroutineScope.launch {
                listState.animateScrollToItem(scrollToIndex)
            }

            // 선택된 항목 업데이트
            selectedIndex = adjustedIndex
            onSelectedChanged(adjustedIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .height(150.dp)
            .width(60.dp),
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(vertical = 55.dp) // 여백 증가
    ) {
        items(items.size + paddingItemCount * 2) { index ->
            val adjustedIndex = index - paddingItemCount

            if (adjustedIndex in items.indices) {
                val isSelected = adjustedIndex == selectedIndex
                Text(
                    text = items[adjustedIndex],
                    fontSize = if (isSelected) 24.sp else 17.sp,
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    color = if (isSelected) Color.Black else Color.Gray
                )
            } else {
                // 패딩 아이템을 위한 빈 공간
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@Composable
fun EditCallButtons(
    schedule: CallSchedule,
    isEditMode: Boolean,
    onIntent: (EditCallIntent) -> Unit,
    onBack: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {

        // 왼쪽 버튼
        Button(
            onClick = { onBack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD7D8DA),
            ),
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            content = {
                Text(
                    text = "취소하기",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight(510),
                        color = Color(0xFF6F6F6F),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 전화 알림(스케줄)일정 수정 OR 추가
        Button(
            onClick = {
                if (isEditMode) {
                    Log.d("TAG", "EditCallButtons: Change CallPlan ${schedule}")
                    onIntent(EditCallIntent.UpdateSchedule(schedule))
                } else {
                    Log.d("TAG", "EditCallButtons: AddedCallPlan ${schedule}")
                    onIntent(EditCallIntent.CreateSchedule(schedule))
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA37BBD),
            ),
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            content = {
                Text(
                    text = if (isEditMode) "변경하기" else "추가하기",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight(510),
                        color = Color(0xFFFDF8FF),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(20.dp))
}


// 대화 주제 선택
@Composable
fun CustomSegmentedButtons(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf("자유주제", "카테고리")

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(20.dp))
            .padding(4.dp)
            .height(40.dp)
    ) {
//        val totalWidth = constraints.maxWidth.toDp() // 전체 너비
        val density = LocalDensity.current
        val totalWidth = with(density) { constraints.maxWidth.toDp() }

        val buttonWidth = totalWidth / items.size    // 항목 개수로 나누기
        val indicatorOffset by animateDpAsState(
            targetValue = selectedIndex * buttonWidth,
            label = "SegmentSlide"
        )

        // 움직이는 흰색 배경
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(buttonWidth)
                .fillMaxHeight()
                .background(Color(0xFFFDF8FF), shape = RoundedCornerShape(15.dp))
        )

        Row {
            items.forEachIndexed { index, text ->
                Box(
                    modifier = Modifier
                        .width(buttonWidth)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onSelected(index)

                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = if (index == selectedIndex) Color(0xFF1D1D1F) else Color(0xFF5F5F61),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(400),
                        )
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EditCallsPreview() {
    EditCallScreen(
        onBack = {},
        schedule = CallSchedule(
            callScheduleId = -1L,
            memberId = -1L,
            scheduleDay = "MONDAY",
            scheduledTime = "00:00:00",
            topicCategory = "FOOD",
        ),
        onSuccess = { _, _ -> }
    )

}