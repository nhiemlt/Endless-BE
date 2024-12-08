package com.datn.endless.repositories;

import com.datn.endless.entities.User;
import com.datn.endless.entities.Useraddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UseraddressRepository extends JpaRepository<Useraddress, String> {
    // Tìm danh sách địa chỉ theo userID
    @Query("SELECT ua FROM Useraddress ua WHERE ua.userID = :user")
    List<Useraddress> findByUser(User user);

    @Query("SELECT ua FROM Useraddress ua WHERE ua.userID.userID = :userId")
    List<Useraddress> findAllByUserID(String userId);

    // Tìm địa chỉ theo userID và addressID
    Useraddress findByUserIDAndAddressID(User user, String addressID);

    // Kiểm tra xem địa chỉ có tồn tại và thuộc về người dùng hiện tại không
    @Query("SELECT ua FROM Useraddress ua WHERE ua.addressID =:addressId and ua.userID.username =:username")
    Useraddress findByIdAndUsername(@Param("addressId") String addressId, @Param("username") String username);

    Optional <Useraddress> findByAddressID(String addressID);

    boolean existsByUserID_UserIDAndProvinceIDAndDistrictIDAndWardCodeAndDetailAddress(
            String userID, Integer provinceID, Integer districtID, Integer wardCode, String detailAddress);
}

