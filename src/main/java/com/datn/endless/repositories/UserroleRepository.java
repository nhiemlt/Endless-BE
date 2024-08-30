package com.datn.endless.repositories;

import com.datn.endless.entities.Userrole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserroleRepository extends JpaRepository<Userrole, String> {
}