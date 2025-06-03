package com.example.swp.controller;

import com.example.swp.dto.BookingRequest;
import com.example.swp.entity.Booking;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Staff;
import com.example.swp.entity.Storage;
import com.example.swp.repository.BookingRepository;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.StaffReponsitory;
import com.example.swp.repository.StorageResponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffReponsitory staffRepository;

    @Autowired
    private StorageResponsitory storageRepository;

    @PostMapping
    public ResponseEntity<?> createBooking(@ModelAttribute BookingRequest request) {
        Optional<Storage> optionalStorage = storageRepository.findById(request.getStorageId());
        if (optionalStorage.isEmpty()) {
            return ResponseEntity.badRequest().body("Storage not found.");
        }

        Storage storage = optionalStorage.get();
        if (!storage.isStatus()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Storage is currently unavailable.");
        }

        Booking booking = new Booking();
        booking.setBookingDate(request.getBookingDate());
        booking.setPurpose(request.getPurpose());
        booking.setRentalDays(request.getRentalDays());

        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow();
        Staff staff = staffRepository.findById(request.getStaffId()).orElseThrow();

        booking.setCustomer(customer);
        booking.setStaff(staff);
        booking.setStorage(storage);

        double dailyRate = 100.0;
        double total = request.getRentalDays() * dailyRate;
        booking.setTotalAmount(total);

        bookingRepository.save(booking);
        return ResponseEntity.ok("Booking created successfully with total amount: " + total);
    }
}

