package com.example.swp.service.impl;

import com.example.swp.entity.LeaveRequest;
import com.example.swp.entity.Staff;
import com.example.swp.repository.LeaveRequestRepository;
import com.example.swp.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Override
    public LeaveRequest createRequest(LeaveRequest request) {
        request.setCreatedAt(java.time.LocalDateTime.now());
        request.setStatus(LeaveRequest.Status.CHO_DUYET); // Đơn mới luôn là CHO_DUYET
        return leaveRequestRepository.save(request);
    }


    @Override
    public List<LeaveRequest> getRequestsByStaff(Staff staff) {
        return leaveRequestRepository.findByStaff(staff);
    }

    @Override
    public List<LeaveRequest> getPendingRequests() {
        return leaveRequestRepository.findByStatus(LeaveRequest.Status.CHO_DUYET);
    }

    @Override
    public LeaveRequest approveRequest(Long id, String managerNote) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ phép"));
        req.setStatus(LeaveRequest.Status.DUYET);
        req.setManagerNote(managerNote);
        return leaveRequestRepository.save(req);
    }

    @Override
    public LeaveRequest rejectRequest(Long id, String managerNote) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ phép"));
        req.setStatus(LeaveRequest.Status.TU_CHOI);
        req.setManagerNote(managerNote);
        return leaveRequestRepository.save(req);
    }
}
