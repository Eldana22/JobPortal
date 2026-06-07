package com.example.jobportal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobportal.R;
import com.example.jobportal.model.Job;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> jobList = new ArrayList<>();
    private Set<Integer> savedJobIds = new HashSet<>();
    private OnJobClickListener listener;
    private OnSaveClickListener saveListener;
    private OnDeleteClickListener deleteListener;
    private String userType;
    private int currentUserId;
    private boolean isSavedJobsView = false;  // New flag for saved jobs view

    public interface OnJobClickListener {
        void onJobClick(int jobId);
    }

    public interface OnSaveClickListener {
        void onSaveClick(int jobId, boolean isSaved);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int jobId);
    }

    public JobAdapter(OnJobClickListener listener, OnSaveClickListener saveListener,
                      OnDeleteClickListener deleteListener, String userType, int currentUserId) {
        this.listener = listener;
        this.saveListener = saveListener;
        this.deleteListener = deleteListener;
        this.userType = userType;
        this.currentUserId = currentUserId;
    }

    public void setJobs(List<Job> jobs) {
        if (jobs != null) {
            this.jobList = jobs;
        } else {
            this.jobList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public void setSavedJobIds(List<Integer> savedIds) {
        savedJobIds.clear();
        if (savedIds != null) {
            savedJobIds.addAll(savedIds);
        }
        notifyDataSetChanged();
    }

    public void setSavedJobsView(boolean isSavedView) {
        this.isSavedJobsView = isSavedView;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.tvJobTitle.setText(job.getTitle());
        holder.tvCompany.setText(job.getCompany());
        holder.tvSalary.setText("💰 " + job.getSalary());

        // Show "CLOSED" status if job is closed/cancelled
        if ("closed".equals(job.getStatus())) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("🔴 CLOSED");
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        // For Job Seekers
        if ("seeker".equals(userType)) {
            if (isSavedJobsView) {
                // In saved jobs view - show delete button only
                holder.btnSave.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
                holder.btnDelete.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onDeleteClick(job.getId());
                    }
                });
            } else {
                // Normal view - show save button
                holder.btnSave.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.GONE);

                boolean isSaved = savedJobIds.contains(job.getId());
                holder.btnSave.setImageResource(isSaved ?
                        android.R.drawable.btn_star_big_on :
                        android.R.drawable.btn_star_big_off);

                holder.btnSave.setOnClickListener(v -> {
                    boolean newState = !savedJobIds.contains(job.getId());
                    if (saveListener != null) {
                        saveListener.onSaveClick(job.getId(), newState);
                    }
                    if (newState) {
                        savedJobIds.add(job.getId());
                    } else {
                        savedJobIds.remove(job.getId());
                    }
                    holder.btnSave.setImageResource(newState ?
                            android.R.drawable.btn_star_big_on :
                            android.R.drawable.btn_star_big_off);
                });
            }
        }
        // For Employers - show delete button only on their own jobs
        else if ("employer".equals(userType) && job.getEmployerId() == currentUserId) {
            holder.btnSave.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setImageResource(android.R.drawable.ic_menu_delete);

            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(job.getId());
                }
            });
        } else {
            holder.btnSave.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJobClick(job.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCompany, tvSalary, tvStatus;
        ImageButton btnSave, btnDelete;

        JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}