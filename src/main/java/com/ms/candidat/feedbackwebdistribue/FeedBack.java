package com.ms.candidat.feedbackwebdistribue;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class FeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    private LocalDateTime submissionDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    public FeedBack() {}

    public FeedBack(LocalDateTime submissionDate, String description) {
        this.submissionDate = submissionDate;
        this.description = description;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
