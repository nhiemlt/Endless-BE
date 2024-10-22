package com.datn.endless.repositories;

import com.datn.endless.entities.User;
import com.datn.endless.entities.Useraddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UseraddressRepository extends JpaRepository<Useraddress, String> {
    // Tìm danh sách địa chỉ theo userID
    @Query("SELECT ua FROM Useraddress ua WHERE ua.userID = :user")
    List<Useraddress> findByUser(User user);
    // Tìm địa chỉ theo userID và addressID
    Useraddress findByUserIDAndAddressID(User user, String addressID);

    @Query("SELECT ua FROM Useraddress ua WHERE ua.userID.userID = :userId and ua.wardCode.code =:wardCode and ua.houseNumberStreet =:houseNumberStreet")
    Optional<Useraddress> findByUserIDAndWardCodeAndHouseNumberStreet(String userId, String wardCode, String houseNumberStreet);
}
