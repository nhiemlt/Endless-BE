package com.datn.endless.repositories;

import com.datn.endless.entities.User;
import com.datn.endless.entities.Useraddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UseraddressRepository extends JpaRepository<Useraddress, String> {
    @Query("SELECT ur FROM Useraddress ur WHERE ur.userID.userID = :userID")
    List<Useraddress> findByUserID(String userID);

    Useraddress findByUserIDAndAddressID(User userid, String addressID);
}