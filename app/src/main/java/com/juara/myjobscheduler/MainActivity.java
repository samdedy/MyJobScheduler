package com.juara.myjobscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStart, btnCancel;
    private int jobId = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btn_start);
        btnCancel = findViewById(R.id.btn_cancel);

        btnStart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                startJob();
                break;
            case R.id.btn_cancel:
                cancelJob();
                break;
        }
    }

    private void startJob(){
        if (isJobRunning(this)){
            Toast.makeText(this, "Jon Service is already scheduled", Toast.LENGTH_SHORT).show();
            return;
        }
        ComponentName mServiceComponent = new ComponentName(this, GetCurrentWeatherJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, mServiceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        //1000 ms = 1 detik
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            builder.setPeriodic(900000); //15 menit
        } else {
            builder.setPeriodic(180000); //3 menit
        }
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

        Toast.makeText(this, "Job Service started", Toast.LENGTH_SHORT).show();
    }

    private void cancelJob(){
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(jobId);
        Toast.makeText(this, "Job Service canceled", Toast.LENGTH_SHORT).show();
//        finish();
    }

    private boolean isJobRunning(Context context){
        boolean isScheduled = false;

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (scheduler != null){
            for (JobInfo jobInfo : scheduler.getAllPendingJobs()){
                if (jobInfo.getId() == jobId){
                    isScheduled = true;
                    break;
                }
            }
        }
        return isScheduled;
    }
}