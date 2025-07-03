package com.example.swp.service;

import com.example.swp.entity.RecentActivity;

import java.util.List;

public interface RecentActivityService {
    List<RecentActivity> getAllActivities();
    RecentActivity save(RecentActivity activity);
    void deleteById(Integer id);
}
