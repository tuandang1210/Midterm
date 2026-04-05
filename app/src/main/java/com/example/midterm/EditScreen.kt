package com.example.midterm

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(navController: NavHostController, productId: String) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentBase64 by remember { mutableStateOf("") }

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val focusManager = LocalFocusManager.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    LaunchedEffect(productId) { //launcheffeft đảm bảo code chạy 1 lần
        db.collection("Products").document(productId).get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    name = doc.getString("name") ?: ""
                    category = doc.getString("category") ?: ""
                    price = doc.getString("price") ?: ""
                    currentBase64 = doc.getString("imageUri") ?: ""
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Không thể tải dữ liệu!", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateProduct() {
        if (name.isNotBlank() && price.isNotBlank() && category.isNotBlank()) {
            val finalBase64 = if (imageUri != null) {
                uriToBase64(context, imageUri!!) ?: currentBase64
            } else {
                currentBase64
            }

            val updatedData = mapOf(
                "name" to name,
                "category" to category,
                "price" to price,
                "imageUri" to finalBase64
            )

            db.collection("Products").document(productId).update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Vui lòng không để trống!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên sản phẩm") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Loại sản phẩm") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Giá sản phẩm") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { updateProduct() })
            )

            OutlinedTextField(
                value = imageUri?.lastPathSegment ?: "Giữ ảnh cũ",
                onValueChange = {},
                label = { Text("Thay đổi hình ảnh (Không bắt buộc)") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                leadingIcon = {
                    IconButton(onClick = { launcher.launch("image/*") }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { updateProduct() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("LƯU THAY ĐỔI", color = Color.White)
            }
        }
    }
}