package com.example.swp.dto;

public class EContractRequest {
    private Long id;
    private String contractUrl;

    // Constructors
    public EContractRequest(Integer id, String contractUrl) {
    }

    public EContractRequest(Long id, String contractUrl) {
        this.id = id;
        this.contractUrl = contractUrl;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }
}
