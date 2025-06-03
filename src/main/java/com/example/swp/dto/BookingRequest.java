package com.example.swp.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingRequest {
    private int customerId;
    private int staffId;
    private int storageId;
    private Date bookingDate;
    private String purpose;
    private int rentalDays;


}

