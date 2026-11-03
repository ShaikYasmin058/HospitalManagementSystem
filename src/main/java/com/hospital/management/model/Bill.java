
package com.hospital.management.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @NotNull(message = "Please select a patient")
    private Long patientId;

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;

    @DecimalMin(value = "0.0", message = "Consultation charge cannot be negative")
    private double consultationCharge;

    @DecimalMin(value = "0.0", message = "Medicine charge cannot be negative")
    private double medicineCharge;

    @DecimalMin(value = "0.0", message = "Treatment charge cannot be negative")
    private double treatmentCharge;

    private double totalAmount;

    private String paymentStatus;

    public Bill() {
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public double getConsultationCharge() {
        return consultationCharge;
    }

    public void setConsultationCharge(double consultationCharge) {
        this.consultationCharge = consultationCharge;
    }

    public double getMedicineCharge() {
        return medicineCharge;
    }

    public void setMedicineCharge(double medicineCharge) {
        this.medicineCharge = medicineCharge;
    }

    public double getTreatmentCharge() {
        return treatmentCharge;
    }

    public void setTreatmentCharge(double treatmentCharge) {
        this.treatmentCharge = treatmentCharge;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
