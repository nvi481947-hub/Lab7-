package com.example.lab7

data class Course(
    // Các trường dữ liệu được lưu vào Firestore Document
    var courseID: String? = "",
    var courseName: String? = "",
    var courseDuration: String? = "",
    var courseDescription: String? = ""
)
