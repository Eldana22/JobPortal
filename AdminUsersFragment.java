package com.example.jobportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobportal.R;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.User;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private AppDatabase database;
    private List<User> userList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        rvUsers = view.findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        database = AppDatabase.getInstance(getContext());

        adapter = new UserAdapter(userList, new UserAdapter.UserActionsListener() {
            @Override
            public void onDelete(int userId) {
                confirmDelete(userId);
            }

            @Override
            public void onMakeAdmin(int userId) {
                promoteToAdmin(userId);
            }

            @Override
            public void onRemoveAdmin(int userId) {
                demoteFromAdmin(userId);
            }
        });
        rvUsers.setAdapter(adapter);

        loadUsers();

        return view;
    }

    private void loadUsers() {
        new Thread(() -> {
            userList = database.userDao().getAllNonAdminUsers();
            // Also get admin users to show them
            List<User> adminUsers = database.userDao().getAllAdminUsers();
            if (adminUsers != null) {
                userList.addAll(adminUsers);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.updateList(userList));
            }
        }).start();
    }

    private void confirmDelete(int userId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user? This will also delete all their jobs and applications.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(userId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser(int userId) {
        new Thread(() -> {
            database.userDao().deleteUserById(userId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                    loadUsers();
                });
            }
        }).start();
    }

    private void promoteToAdmin(int userId) {
        new Thread(() -> {
            User user = database.userDao().getUserById(userId);
            if (user != null && !user.getUserType().equals("admin")) {
                user.setUserType("admin");
                database.userDao().update(user);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), user.getFullName() + " is now an admin", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    });
                }
            }
        }).start();
    }

    private void demoteFromAdmin(int userId) {
        new Thread(() -> {
            User user = database.userDao().getUserById(userId);
            if (user != null && user.getUserType().equals("admin")) {
                user.setUserType("seeker");
                database.userDao().update(user);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), user.getFullName() + " is no longer an admin", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    });
                }
            }
        }).start();
    }

    // Adapter inner class
    static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<User> users;
        private UserActionsListener listener;

        interface UserActionsListener {
            void onDelete(int userId);
            void onMakeAdmin(int userId);
            void onRemoveAdmin(int userId);
        }

        UserAdapter(List<User> users, UserActionsListener listener) {
            this.users = users;
            this.listener = listener;
        }

        void updateList(List<User> newUsers) {
            this.users = newUsers;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_admin_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.tvUserName.setText(user.getFullName());
            holder.tvUserEmail.setText(user.getEmail());

            String userTypeDisplay = user.getUserType().equals("admin") ? "🔑 ADMIN" : "👤 " + user.getUserType();
            holder.tvUserType.setText(userTypeDisplay);

            // Show/hide buttons based on user type
            if (user.getUserType().equals("admin")) {
                holder.btnMakeAdmin.setVisibility(View.GONE);
                holder.btnRemoveAdmin.setVisibility(View.VISIBLE);
            } else {
                holder.btnMakeAdmin.setVisibility(View.VISIBLE);
                holder.btnRemoveAdmin.setVisibility(View.GONE);
            }

            holder.btnDeleteUser.setOnClickListener(v -> listener.onDelete(user.getId()));
            holder.btnMakeAdmin.setOnClickListener(v -> listener.onMakeAdmin(user.getId()));
            holder.btnRemoveAdmin.setOnClickListener(v -> listener.onRemoveAdmin(user.getId()));
        }

        @Override
        public int getItemCount() {
            return users != null ? users.size() : 0;
        }

        static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tvUserName, tvUserEmail, tvUserType;
            ImageButton btnDeleteUser, btnMakeAdmin, btnRemoveAdmin;

            UserViewHolder(View itemView) {
                super(itemView);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
                tvUserType = itemView.findViewById(R.id.tvUserType);
                btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
                btnMakeAdmin = itemView.findViewById(R.id.btnMakeAdmin);
                btnRemoveAdmin = itemView.findViewById(R.id.btnRemoveAdmin);
            }
        }
    }
}