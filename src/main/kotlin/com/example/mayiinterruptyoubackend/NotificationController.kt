package com.example.mayiinterruptyoubackend

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Notification
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URL
import io.github.cdimascio.dotenv.dotenv

@RestController
class NotificationController {
    val dotenv = dotenv()
    var API_KEY = dotenv["API_KEY"]
    var FCM_TOKEN = dotenv["FCM_TOKEN"]

    @GetMapping("/send-notification")
    fun sendNotification(subject: String?): String {
        val url = URL("https://newsapi.org/v2/top-headlines?country=us&category=$subject&apiKey=$API_KEY")
        val newsJson = ObjectMapper().readTree(url)
        val firstArticle = newsJson.get("articles").get(0)
        val title = firstArticle.get("title").asText()
        val image = firstArticle.get("urlToImage").asText()
        val articleURL = firstArticle.get("url").asText()

        val notification = Notification.builder()
            .setTitle("News")
            .setBody(title)
            .setImage(image)
            .build()

        val data = mutableMapOf<String, String>()
        data["url"] = articleURL

        val message = com.google.firebase.messaging.Message.builder()
            .setNotification(notification)
            .setToken(FCM_TOKEN)
            .putAllData(data)
            .build()

        FirebaseMessaging.getInstance().send(message)

        return "Notification sent!"
    }
}
