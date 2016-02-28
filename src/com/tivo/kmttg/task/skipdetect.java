package com.tivo.kmttg.task;

import java.io.Serializable;
import java.util.Date;

import com.tivo.kmttg.main.jobData;
import com.tivo.kmttg.main.jobMonitor;
import com.tivo.kmttg.util.backgroundProcess;
import com.tivo.kmttg.util.debug;
import com.tivo.kmttg.util.log;

public class skipdetect extends baseTask implements Serializable {
   private static final long serialVersionUID = 1L;
   private Thread thread = null;
   private Boolean thread_running = false;
   private backgroundProcess process;
   private jobData job;

   // constructor
   public skipdetect(jobData job) {
      debug.print("job=" + job);
      this.job = job;
   }
   
   public backgroundProcess getProcess() {
      return process;
   }
   
   public Boolean launchJob() {
      debug.print("");            
      if ( start() ) {
         job.process = this;
         jobMonitor.updateJobStatus(job, "running");
         job.time = new Date().getTime();
      }
      return true;
   }
   
   public Boolean start() {
      debug.print("");
      log.print(">> RUNNING SKIP DETECT FOR: " + job.title);
      
      // Run in a separate thread
      Runnable r = new Runnable() {
         public void run () {
            //AutoSkip.autoDetect(job.tivoName, job.entry);
            thread_running = false;
         }
      };
      thread_running = true;
      thread = new Thread(r);
      thread.start();
      
      return true;
   }
   
   public void kill() {
      debug.print("");
      if (job.limit == 0)
         log.warn("Killing '" + job.type + "'");
      thread.interrupt();
      thread_running = false;
   }
   
   // Check status of a currently running job
   // Returns true if still running, false if job finished
   // If job is finished then check result
   public Boolean check() {
      if (thread_running) {
         // Still running
         return true;
      } else {
         // Job finished         
         jobMonitor.removeFromJobList(job);
      }
      
      return false;
   }
}
