package com.ssafy.lipit_app.ui.screens.auth.Signup.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.components.SpacerHeight
import com.ssafy.lipit_app.ui.screens.auth.Signup.SignupIntent
import com.ssafy.lipit_app.ui.screens.auth.Signup.SignupState
import com.ssafy.lipit_app.ui.screens.auth.components.GenderSelectDialog

@Composable
fun InputForm(state: SignupState, onSuccess: () -> Unit, onIntent: (SignupIntent) -> Unit) {

    val context = LocalContext.current

    if (state.errorMessage != null) {
        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
        onSuccess()
    }

    if (state.signupSuccess) {
        Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
        onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 1. Input: ID
        OutlinedTextField(
            value = state.id,
            onValueChange = { onIntent(SignupIntent.OnIdChanged(it)) },
            placeholder = { Text("ID", color = Color(0xFFE2C7FF)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // 2. Input: PW
        SpacerHeight(18)
        OutlinedTextField(
            value = state.pw,
            onValueChange = { onIntent(SignupIntent.OnPwChanged(it)) },
            placeholder = { Text("PW", color = Color(0xFFE2C7FF)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (state.isPasswordVisible_1) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (state.isPasswordVisible_1)
                            R.drawable.ic_pw_closed2
                        else
                            R.drawable.ic_pw_show
                    ),
                    contentDescription = if (state.isPasswordVisible_1) "비밀번호 숨기기" else "비밀번호 보기",
                    tint = Color(0xFFE2C7FF),
                    modifier = Modifier
                        .clickable(onClick = {
                            onIntent(
                                SignupIntent.OnisPasswordVisible1Changed(
                                    state.isPasswordVisible_1
                                )
                            )
                        })
                )
            }
        )

        // 3. PW 확인용
        SpacerHeight(18)
        OutlinedTextField(
            value = state.pwConfirm,
            onValueChange = { onIntent(SignupIntent.OnPwConfirmChanged(it)) },
            placeholder = { Text("Re-enter PW", color = Color(0xFFE2C7FF)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (state.isPasswordVisible_2) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (state.isPasswordVisible_2)
                            R.drawable.ic_pw_closed2
                        else
                            R.drawable.ic_pw_show
                    ),
                    contentDescription = if (state.isPasswordVisible_2) "비밀번호 숨기기" else "비밀번호 보기",
                    tint = Color(0xFFE2C7FF),
                    modifier = Modifier
                        .clickable(onClick = {
                            onIntent(
                                SignupIntent.OnisPasswordVisible2Changed(
                                    state.isPasswordVisible_2
                                )
                            )
                        })
                )
            }
        )

        // 4. 영문이름(별명, name) 영역
        SpacerHeight(18)
        OutlinedTextField(
            value = state.englishName,
            onValueChange = { // 알파벳만 허용 (대소문자 모두)
                if (it.matches(Regex("^[a-zA-Z]*$"))) {
                    onIntent(SignupIntent.OnEnglishNameChanged(it))
                }
            },
            placeholder = { Text("English Name", color = Color(0xFFE2C7FF)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // 5. 성별: 드뢉 다운
        SpacerHeight(18)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onIntent(SignupIntent.OnExpandedChanged(state.expanded)) },
        ) {
            OutlinedTextField(
                value = state.selectedGender,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = { Text("Gender", color = Color(0xFFE2C7FF)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFFE2C7FF)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledBorderColor = Color(0xFFE2C7FF),
                    disabledTextColor = Color.White,
                    disabledPlaceholderColor = Color(0xFFE2C7FF),
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // DropDown 메뉴 활성화
            if (state.expanded) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val maxWidth = this.maxWidth

                    DropdownMenu(
                        expanded = state.expanded,
                        onDismissRequest = { onIntent(SignupIntent.OnExpandedChanged(state.expanded)) },
                        modifier = Modifier
                            .width(maxWidth) // 직접 너비 지정!
                            .padding(horizontal = 20.dp)
                            .background(Color.Transparent)
                    ) {
                        GenderSelectDialog(
                            modifier = Modifier.fillMaxWidth(),
                            onSelect = {
                                state.selectedGender = it
                            },
                            onDismiss = {
                                onIntent(SignupIntent.OnExpandedChanged(state.expanded))
                            }
                        )
                    }
                }
            }
        }

        // 6. 회원가입 버튼 JOIN
        SpacerHeight(28)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomFilledSignupButton(
                text = "JOIN",
                context = context,
                state = state,
                onClick = {
                    onIntent(SignupIntent.OnSignupClicked)
                }
            )


            SpacerHeight(12)
        }

        SpacerHeight(94)
    }
}

@Composable
fun CustomFilledSignupButton(
    text: String,
    context: Context,
    state: SignupState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFA37BBD),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, color = Color.White)

        LaunchedEffect(state.signupSuccess) {
            if (state.signupSuccess) {
                Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


// Input 유효성 검사 함수
fun validateSignupInput(
    id: String,
    password: String,
    passwordConfirm: String,
    name: String,
    selectedGender: String
): String? {
    return when {
        id.isBlank() -> "아이디를 입력해주세요."
        password.isBlank() -> "비밀번호를 입력해주세요."
        passwordConfirm.isBlank() -> "비밀번호 확인을 입력해주세요."
        password != passwordConfirm -> "비밀번호가 일치하지 않아요."
        name.isBlank() -> "영문 이름을 입력해주세요."
        selectedGender.isBlank() -> "성별을 선택해주세요."
        else -> null // 문제 없음
    }
}