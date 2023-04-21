package com.example.mayiinterruptyoubackend

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.FileInputStream

@SpringBootApplication
class MayIInterruptYouBackendApplication

fun main(args: Array<String>) {
    val serviceAccount = FileInputStream("src/main/resources/serviceAccountKey.json")

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)

    val scheduler = NotificationScheduler()
    scheduler.schedule()

    runApplication<MayIInterruptYouBackendApplication>(*args)
}
