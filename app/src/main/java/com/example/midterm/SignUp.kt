@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.example.midterm

import androidx.annotation.OptIn;
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SignUp(navController: NavHostController){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val (focusUsername, focusPassword) = remember {FocusRequester.createRefs()}
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordVisible by remember{ mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val handleSignup = {
    if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
        if (password == confirmPassword) {
            firebaseAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task -> //tạo biến task thực hiện tác vụ kiểm tra trên firebase
                if (task.isSuccessful) { //kiểm tra đăng ký thành công
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.Signin.rout) // Quay lại đăng nhập
                } else {
                    Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                } // in báo looix
            }
        } else {
            Toast.makeText(context, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
    }
    }

    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFCF4)),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.40f)
        ){
            Image(
                    painter = painterResource(id = R.drawable._1),
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds)
        }
        Spacer(modifier = Modifier.size(15.dp))
        Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(text = "Thi Giữa Kì", fontSize = 21.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.size(28.dp))
            OutlinedTextField(value = username, onValueChange = {username = it},
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .focusRequester(focusUsername),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {focusPassword.requestFocus()}),
                    singleLine = true,
                    colors = outlinedTextFieldColors(
                            focusedBorderColor =Color(0xFFB90020),
                            unfocusedBorderColor = Color(0xFFB90020)
                    ),
                    label = { Text(text = "Email", color = Color(0xFFB90020))}
            )
            Spacer(modifier = Modifier.size(9.dp))
            OutlinedTextField(modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .focusRequester(focusPassword),
                    value = password,
                    onValueChange = {password = it},
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {keyboardController?.hide()}),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                            IconButton(onClick = {isPasswordVisible = !isPasswordVisible}) {
                            Icon(imageVector = if(isPasswordVisible)Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = "Password Toggle",
                    tint = Color(0xFFB90020)
                        )
                    }
                },
            colors = outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFB90020),
                    unfocusedBorderColor = Color(0xFFB90020)
            ),
                    label = { Text(text = "Password", color = Color(0xFFB90020),)},

                )
            Spacer(modifier = Modifier.size(9.dp))
            OutlinedTextField(modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .focusRequester(focusPassword),
                    value = confirmPassword,
                    onValueChange = {confirmPassword = it},
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {keyboardController?.hide(); handleSignup()}),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                            IconButton(onClick = {isPasswordVisible = !isPasswordVisible}) {
                            Icon(imageVector = if(isPasswordVisible)Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = "Cf Password Toggle",
                    tint = Color(0xFFB90020)
                        )
                    }
                },
            colors = outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFB90020),
                    unfocusedBorderColor = Color(0xFFB90020)
            ),
                    label = { Text(text = "Xác nhận mật khẩu", color = Color(0xFFB90020),)},

                )
            Spacer(modifier = Modifier.size(20.dp))
            Button(onClick = {
                            handleSignup()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 15.dp,
                            pressedElevation = 6.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB90020)
                    ),
                    border = BorderStroke(0.5.dp, Color.Red)
            ) {
                Text(text = "Đăng ký", fontWeight = FontWeight.Bold,fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), Arrangement.Center,verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Đã có tài khoản?",textAlign = TextAlign.Center)
                TextButton(onClick = {
                                navController.navigate(Screen.Signin.rout)
                        },
                        ) {
                    Text(text = "Đăng nhập!",fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB90020))
                }
            }
            Spacer(modifier = Modifier.size(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), Arrangement.Center) {
                Text(text = "Hoặc đăng ký với")
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(modifier = Modifier
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                Box(
                        modifier = Modifier
                                .size(40.dp)
                                .border(
                                        border = BorderStroke(0.5.dp, Color(0xFFD9D9D9)),
                                        shape = CircleShape
                                ),
                        contentAlignment = Alignment.Center
                ) {
                    Icon(
                            painter = painterResource(id = R.drawable.gg),
                            contentDescription = "Logo GG",
                            modifier = Modifier.size(25.dp),
                            tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                        modifier = Modifier
                                .size(40.dp)
                                .border(
                                        border = BorderStroke(0.5.dp, Color(0xFFD9D9D9)),
                                        shape = CircleShape
                                ),
                        contentAlignment = Alignment.Center
                ) {
                    Icon(
                            painter = painterResource(id = R.drawable.fb),
                            contentDescription = "Logo FB",
                            modifier = Modifier.size(25.dp),
                            tint = Color.Unspecified
                    )
                }
            }

        }
    }
}