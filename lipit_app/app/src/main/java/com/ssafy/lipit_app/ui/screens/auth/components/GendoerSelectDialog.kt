package com.ssafy.lipit_app.ui.screens.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.ui.components.SpacerHeight

@Composable
fun GenderSelectDialog(
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val genders = listOf("Male", "Female")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            // Header
            Text(
                text = "Gender",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

            // Divider
            SpacerHeight(dp = 8)
            androidx.compose.material.Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 5.dp)
            )
            SpacerHeight(dp = 4)

            // Gender Items
            genders.forEach { gender ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(gender)
                            onDismiss()
                        }
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = gender,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = if (gender == "Male") "남성" else "여성",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
