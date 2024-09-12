package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(name = "uservouchers")
public class Uservoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Size(max = 36)
//    @ColumnDefault("(uuid())")
    @Column(name = "UserVoucherID", nullable = false, length = 36)
    private String userVoucherID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User userID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VoucherID", nullable = false)
    private Voucher voucherID;

    @Size(max = 50)
    @Column(name = "Status", length = 50)
    private String status;

}