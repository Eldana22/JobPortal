package com.example.jobportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobportal.R;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.Application;
import java.util.ArrayList;
import java.util.List;

public class AdminApplicationsFragment extends Fragment {

    private RecyclerView rvApplications;
    private ApplicationAdapter adapter;
    private AppDatabase database;
    private List<Application> appList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_applications, container, false);

        rvApplications = view.findViewById(R.id.rvApplications);
        rvApplications.setLayoutManager(new LinearLayoutManager(getContext()));

        database = AppDatabase.getInstance(getContext());

        adapter = new ApplicationAdapter(appList);
        rvApplications.setAdapter(adapter);

        loadApplications();

        return view;
    }

    private void loadApplications() {
        new Thread(() -> {
            appList = database.applicationDao().getAllApplications();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.updateList(appList));
            }
        }).start();
    }

    static class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {
        private List<Application> applications;

        ApplicationAdapter(List<Application> applications) {
            this.applications = applications;
        }

        void updateList(List<Application> newApps) {
            this.applications = newApps;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ApplicationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
            Application app = applications.get(position);
            holder.text1.setText("Job ID: " + app.getJobId() + " | Seeker ID: " + app.getSeekerId());
            holder.text2.setText("Status: " + app.getStatus() + " | Date: " + app.getAppliedDate());
        }

        @Override
        public int getItemCount() {
            return applications.size();
        }

        static class ApplicationViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            ApplicationViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}