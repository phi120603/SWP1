package com.example.swp.controller.api;

import com.example.swp.entity.Manager;
import com.example.swp.repository.ManageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
public class ManagerAvailabilityController {

    private final ManageRepository managerRepository;

    @GetMapping("/available")
    public ResponseEntity<List<ManagerDto>> getAvailableManagers() {
        List<Manager> availableManagers = managerRepository.findAll()
                .stream()
                .filter(Manager::isOnDuty)
                .collect(Collectors.toList());

        List<ManagerDto> managerDtos = availableManagers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(managerDtos);
    }

    @GetMapping
    public ResponseEntity<List<ManagerDto>> getAllManagers() {
        List<Manager> managers = managerRepository.findAll();
        List<ManagerDto> managerDtos = managers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(managerDtos);
    }

    private ManagerDto convertToDto(Manager manager) {
        return ManagerDto.builder()
                .id(manager.getId())
                .fullname(manager.getFullname())
                .email(manager.getEmail())
                .phone(manager.getPhone())
                .onDuty(manager.isOnDuty())
                .isOnline(manager.getIsOnline() != null ? manager.getIsOnline() : false)
                .lastSeen(manager.getLastSeen())
                .build();
    }

    // DTO class
    public static class ManagerDto {
        public Integer id;
        public String fullname;
        public String email;
        public String phone;
        public boolean onDuty;
        public boolean isOnline;
        public java.time.LocalDateTime lastSeen;

        public static ManagerDtoBuilder builder() {
            return new ManagerDtoBuilder();
        }

        public static class ManagerDtoBuilder {
            private Integer id;
            private String fullname;
            private String email;
            private String phone;
            private boolean onDuty;
            private boolean isOnline;
            private java.time.LocalDateTime lastSeen;

            public ManagerDtoBuilder id(Integer id) {
                this.id = id;
                return this;
            }

            public ManagerDtoBuilder fullname(String fullname) {
                this.fullname = fullname;
                return this;
            }

            public ManagerDtoBuilder email(String email) {
                this.email = email;
                return this;
            }

            public ManagerDtoBuilder phone(String phone) {
                this.phone = phone;
                return this;
            }

            public ManagerDtoBuilder onDuty(boolean onDuty) {
                this.onDuty = onDuty;
                return this;
            }

            public ManagerDtoBuilder isOnline(boolean isOnline) {
                this.isOnline = isOnline;
                return this;
            }

            public ManagerDtoBuilder lastSeen(java.time.LocalDateTime lastSeen) {
                this.lastSeen = lastSeen;
                return this;
            }

            public ManagerDto build() {
                ManagerDto dto = new ManagerDto();
                dto.id = this.id;
                dto.fullname = this.fullname;
                dto.email = this.email;
                dto.phone = this.phone;
                dto.onDuty = this.onDuty;
                dto.isOnline = this.isOnline;
                dto.lastSeen = this.lastSeen;
                return dto;
            }
        }
    }
}
