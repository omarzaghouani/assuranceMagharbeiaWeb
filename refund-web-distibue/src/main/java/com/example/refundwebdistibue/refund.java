package com.example.refundwebdistibue;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class refund {
    @Id
    @GeneratedValue
    private int id ;
    private double amount;
    private String status;
    private LocalDate requestDate;
    private LocalDate processedDate;

    public refund() {
    }

    public refund(double amount, String status, LocalDate requestDate, LocalDate processedDate) {
        this.amount = amount;
        this.status = status;
        this.requestDate = requestDate;
        this.processedDate = processedDate;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDate processedDate) {
        this.processedDate = processedDate;
    }
}
