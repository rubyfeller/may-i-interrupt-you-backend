package com.example.mayiinterruptyoubackend

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.cdimascio.dotenv.dotenv
import org.quartz.JobBuilder
import org.quartz.JobExecutionContext
import org.quartz.Job
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

class NotificationScheduler() {
    val dotenv = dotenv()
    var FIREBASE_INSTANCE_URL = dotenv["FIREBASE_INSTANCE_URL"]

    private val databaseRef = FirebaseDatabase.getInstance(FIREBASE_INSTANCE_URL).reference

    fun schedule() {
        getNewsInterval { interval ->
            val job = JobBuilder.newJob(NotificationJob::class.java).build()

            val trigger = TriggerBuilder.newTrigger()
                .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(interval))
                .build()

            val schedulerFactory = StdSchedulerFactory()
            val scheduler = schedulerFactory.scheduler
            scheduler.start()
            scheduler.scheduleJob(job, trigger)
        }
    }

    fun getNewsSubject(callback: (String?) -> Unit) {
        databaseRef.child("news").child("subject")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val subject = dataSnapshot.getValue(String::class.java)
                    callback(subject)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback(null)
                }
            })
    }

    private fun getNewsInterval(callback: (Int) -> Unit) {
        databaseRef.child("news").child("interval")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val interval = snapshot.getValue(Int::class.java) ?: 1
                    callback(interval)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error)
                }
            })
    }

    class NotificationJob : Job {
        override fun execute(context: JobExecutionContext) {
            val controller = NotificationController()

            val scheduler = NotificationScheduler()

            scheduler.getNewsSubject { subject ->
                controller.sendNotification(subject)
            }
        }
    }
}
