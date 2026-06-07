package com.example.jobportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobportal.R;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.Job;
import java.util.ArrayList;
import java.util.List;

public class AdminJobsFragment extends Fragment {

    private RecyclerView rvJobs;
    private JobAdapter adapter;
    private AppDatabase database;
    private List<Job> jobList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_jobs, container, false);

        rvJobs = view.findViewById(R.id.rvJobs);
        rvJobs.setLayoutManager(new LinearLayoutManager(getContext()));

        database = AppDatabase.getInstance(getContext());

        adapter = new JobAdapter(jobList, jobId -> deleteJob(jobId));
        rvJobs.setAdapter(adapter);

        loadJobs();

        return view;
    }

    private void loadJobs() {
        new Thread(() -> {
            jobList = database.jobDao().getAllJobs();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.updateList(jobList));
            }
        }).start();
    }

    private void deleteJob(int jobId) {
        new Thread(() -> {
            database.jobDao().deleteJobById(jobId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Job deleted", Toast.LENGTH_SHORT).show();
                    loadJobs();
                });
            }
        }).start();
    }

    static class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
        private List<Job> jobs;
        private OnJobDeleteListener listener;

        interface OnJobDeleteListener {
            void onDelete(int jobId);
        }

        JobAdapter(List<Job> jobs, OnJobDeleteListener listener) {
            this.jobs = jobs;
            this.listener = listener;
        }

        void updateList(List<Job> newJobs) {
            this.jobs = newJobs;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_admin_job, parent, false);
            return new JobViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
            Job job = jobs.get(position);
            holder.tvJobTitle.setText(job.getTitle());
            holder.tvJobCompany.setText(job.getCompany());
            holder.tvJobEmployer.setText("Employer ID: " + job.getEmployerId());

            String statusText = "Status: " + (job.getStatus() != null ? job.getStatus() : "active");
            holder.tvJobStatus.setText(statusText);

            holder.btnDeleteJob.setOnClickListener(v -> listener.onDelete(job.getId()));
        }

        @Override
        public int getItemCount() {
            return jobs.size();
        }

        static class JobViewHolder extends RecyclerView.ViewHolder {
            TextView tvJobTitle, tvJobCompany, tvJobEmployer, tvJobStatus;
            ImageButton btnDeleteJob;

            JobViewHolder(View itemView) {
                super(itemView);
                tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
                tvJobCompany = itemView.findViewById(R.id.tvJobCompany);
                tvJobEmployer = itemView.findViewById(R.id.tvJobEmployer);
                tvJobStatus = itemView.findViewById(R.id.tvJobStatus);
                btnDeleteJob = itemView.findViewById(R.id.btnDeleteJob);
            }
        }
    }
}