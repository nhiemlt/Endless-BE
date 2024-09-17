package com.datn.endless.repositories;

import com.datn.endless.entities.User;
import com.datn.endless.entities.Useraddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UseraddressRepository extends JpaRepository<Useraddress, String> {
    Useraddress findByUserIDAndAddressID(User userid, String addressID);
}