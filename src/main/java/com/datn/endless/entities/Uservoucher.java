package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "uservouchers")
public class Uservoucher {
    private String userVoucherID;

    private User userID;

    private Voucher voucherID;

    private String status;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "UserVoucherID", nullable = false, length = 36)
    public String getUserVoucherID() {
        return userVoucherID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    public User getUserID() {
        return userID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VoucherID", nullable = false)
    public Voucher getVoucherID() {
        return voucherID;
    }

    @Size(max = 50)
    @Column(name = "Status", length = 50)
    public String getStatus() {
        return status;
    }

}