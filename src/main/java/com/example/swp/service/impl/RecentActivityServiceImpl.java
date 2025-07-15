package com.example.swp.service.impl;

import com.example.swp.entity.RecentActivity;
import com.example.swp.repository.RecentActivityRepository;
import com.example.swp.service.RecentActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecentActivityServiceImpl implements RecentActivityService {

    @Autowired
    private RecentActivityRepository recentActivityRepository;

    @Override
    public List<RecentActivity> getAllActivities() {
        return recentActivityRepository.findAll();
    }

    @Override
    public RecentActivity save(RecentActivity activity) {
        return recentActivityRepository.save(activity);
    }

    @Override
    public void deleteById(Integer id) {
        recentActivityRepository.deleteById(id);
    }
}