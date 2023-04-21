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

    @GetMapping("/send-notification")
    fun sendNotification(): String {
        val url = URL("https://newsapi.org/v2/top-headlines?country=us&apiKey=$API_KEY")
        val newsJson = ObjectMapper().readTree(url)
        val firstArticle = newsJson.get("articles").get(0)
        val title = firstArticle.get("title").asText()
        val image = firstArticle.get("urlToImage").asText()
        val description = firstArticle.get("description").asText()

        val notification = Notification.builder()
            .setTitle("News")
            .setBody(title)
            .setImage(image)
            .build()

        val message = com.google.firebase.messaging.Message.builder()
            .setNotification(notification)
            .setToken("cTcBh1a8SpOn_08niWIQqq:APA91bEuDy18PKkp9xVxjnR6wZU13Qx00gDITE_I9gpxhkICi6eYNlzuuuXZ_B_WzFwFg_iCdsUiCIkVTLbz8aXfMQFLhXG3ZLwyN1cpGggTXZ6ZQsCgIbNvqTXiLDHbfgjSNIvTse8K")
            .build()

        FirebaseMessaging.getInstance().send(message)

        return "Notification sent!"
    }
}
