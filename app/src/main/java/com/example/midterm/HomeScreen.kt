package com.example.midterm

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.io.InputStream
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import kotlin.text.set


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val productList = remember { mutableStateListOf<Product>() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    val focusManager = LocalFocusManager.current


    fun addProduct() {
        if (name.isNotEmpty() && price.isNotEmpty() && category.isNotEmpty()) {
            val base64String = if (imageUri != null) {
                uriToBase64(context, imageUri!!) ?: ""
            } else {
                ""
            }

            val id = db.collection("Products").document().id
            val product = Product(id, name, category, price, base64String)

            db.collection("Products").document(id).set(product)
                .addOnSuccessListener {
                    name = ""
                    category = ""
                    price = ""
                    imageUri = null
                    Toast.makeText(context, "Thành công!", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { e ->
                    android.util.Log.e("FIRESTORE_ERROR", "Lỗi: ${e.message}")
                    Toast.makeText(context, "Lỗi Server: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        db.collection("Products").addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener
            if (value != null) {
                val items = value.toObjects(Product::class.java)
                productList.clear()
                productList.addAll(items)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dữ liệu sản phẩm",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 20.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên sản phẩm") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Loại sản phẩm") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )

        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Giá sản phẩm") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            keyboardActions = KeyboardActions(
                onDone = { addProduct() }
            )
        )

        OutlinedTextField(
            value = imageUri?.lastPathSegment ?: "",
            onValueChange = {},
            label = { Text("Chọn hình ảnh") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            leadingIcon = {
                IconButton(onClick = { launcher.launch("image/*") }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                addProduct()
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("THÊM SẢN PHẨM", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Danh sách sản phẩm:",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(productList) { product ->
                ProductItem(product, db, navController)
            }
        }
    }


}



fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        // Mã hóa mảng byte thành chuỗi Base64
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ProductItem(product: Product, db: FirebaseFirestore, navController: NavHostController) {
    val context = LocalContext.current
    //Chuyển chuỗi Base64 từ Firestore thành mảng Byte
    val imageBytes = remember(product.imageUri) {
        if (product.imageUri.isNotEmpty()) {
            try {
                android.util.Base64.decode(product.imageUri, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    val painter = if (imageBytes != null) {
        rememberAsyncImagePainter(model = imageBytes) //biến dữ liệu thô thành painter vẽ lên app
    } else {
        painterResource(id = R.drawable.ic_launcher_background)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(text = "Tên: ${product.name}", fontWeight = FontWeight.Bold)
                Text(text = "Giá: ${product.price} VNĐ")
                Text(text = "Loại: ${product.category}", color = Color.Gray)
            }

            Column {
                IconButton(onClick = {
                    navController.navigate("edit_screen/${product.id}")
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = Color(0xFFFF9800)
                    )
                }

                IconButton(onClick = {
                    db.collection("Products").document(product.id).delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}