package com.example.boilerplateapp.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.boilerplateapp.api.models.User;
import com.example.boilerplateapp.ui.AccountActivity;
import com.example.boilerplateapp.ui.HomeActivity;

import java.util.List;

public class BottomNavigationHelper {
    private final Context context;
    private final View tabHome;
    private final View tabAccount;
    private final User userAccount;

    public BottomNavigationHelper(Context context, View tabHome, View tabAccount, User userAccount) {
        this.context = context;
        this.tabHome = tabHome;
        this.tabAccount = tabAccount;
        this.userAccount = userAccount;
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Set up click listener for the Home tab
        tabHome.setOnClickListener(v -> navigateToHome());

        // Set up click listener for the Account tab
        tabAccount.setOnClickListener(v -> navigateToAccount());
    }

    private void navigateToHome() {
        if (!isActivityRunning(HomeActivity.class)) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("USER_OBJECT", userAccount);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    private void navigateToAccount() {
        if (!isActivityRunning(AccountActivity.class)) {
            Intent intent = new Intent(context, AccountActivity.class);
            intent.putExtra("USER_OBJECT", userAccount);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    // Helper method to check if an activity is currently running
    private boolean isActivityRunning(Class<?> activityClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(1);

        if (!taskList.isEmpty()) {
            String currentActivity = taskList.get(0).topActivity.getClassName();
            return currentActivity.equals(activityClass.getName());
        }
        return false;
    }
}
