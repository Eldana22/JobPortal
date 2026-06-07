package com.example.jobportal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.jobportal.fragments.AdminUsersFragment;
import com.example.jobportal.fragments.AdminJobsFragment;
import com.example.jobportal.fragments.AdminApplicationsFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminUsersFragment();
            case 1:
                return new AdminJobsFragment();
            case 2:
                return new AdminApplicationsFragment();
            default:
                return new AdminUsersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}