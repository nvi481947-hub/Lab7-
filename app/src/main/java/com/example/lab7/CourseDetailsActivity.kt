package com.example.lab7

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab7.ui.theme.Lab7Theme
import com.example.lab7.ui.theme.greenColor
import com.google.firebase.firestore.FirebaseFirestore

class CourseDetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab7Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = greenColor
                                ),
                                title = {
                                    Text(
                                        text = "GFG",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                }
                            )
                        }
                    ) { innerPadding ->
                        // Danh sách phản ứng chứa các khóa học tải về từ Firestore
                        val courseList = remember { mutableStateListOf<Course?>() }

                        // Kết nối Firestore và lấy danh sách document từ collection "Courses"
                        LaunchedEffect(Unit) {
                            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                            db.collection("Courses").get()
                                .addOnSuccessListener { queryDocumentSnapshots ->
                                    if (!queryDocumentSnapshots.isEmpty) {
                                        val list = queryDocumentSnapshots.documents
                                        courseList.clear()
                                        for (d in list) {
                                            val c: Course? = d.toObject(Course::class.java)
                                            // Gán document ID để truyền sang màn hình Update
                                            c?.courseID = d.id
                                            Log.e("TAG", "Course id is : " + c?.courseID)
                                            courseList.add(c)
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@CourseDetailsActivity,
                                            "No data found in Database",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@CourseDetailsActivity,
                                        "Fail to get the data.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                        // Gọi hàm hiển thị giao diện danh sách
                        Box(modifier = Modifier.padding(innerPadding)) {
                            firebaseUI(LocalContext.current, courseList)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun firebaseUI(context: Context, courseList: SnapshotStateList<Course?>) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LazyColumn cuộn danh sách khóa học
            LazyColumn {
                itemsIndexed(courseList) { index, item ->
                    Card(
                        onClick = {
                            // Khi nhấn vào item -> Mở màn hình UpdateCourse (Bước 7) và truyền dữ liệu đi kèm
                            val i = Intent(context, UpdateCourse::class.java)
                            i.putExtra("courseName", item?.courseName)
                            i.putExtra("courseDuration", item?.courseDuration)
                            i.putExtra("courseDescription", item?.courseDescription)
                            i.putExtra("courseID", item?.courseID)
                            context.startActivity(i)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            // Tên khóa học
                            item?.courseName?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(4.dp),
                                    color = greenColor,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))

                            // Thời lượng khóa học
                            item?.courseDuration?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(4.dp),
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontSize = 15.sp)
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))

                            // Mô tả khóa học
                            item?.courseDescription?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(4.dp),
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontSize = 15.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
