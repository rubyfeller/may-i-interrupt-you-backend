package com.example.mayiinterruptyoubackend

import org.quartz.JobBuilder
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.Job
import org.quartz.SchedulerFactory
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import java.util.concurrent.TimeUnit

class NotificationScheduler {
    fun schedule() {
        val job = JobBuilder.newJob(NotificationJob::class.java).build()

        val trigger = TriggerBuilder.newTrigger()
            .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(10))
            .build()

        val schedulerFactory = StdSchedulerFactory()
        val scheduler = schedulerFactory.scheduler
        scheduler.start()
        scheduler.scheduleJob(job, trigger)
    }
}

class NotificationJob : Job {
    override fun execute(context: JobExecutionContext) {
        val controller = NotificationController()
        controller.sendNotification()
    }
}
