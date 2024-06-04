package com.bond.repository;

import com.bond.model.Company;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, String> {

    Optional<Company> findByName(String name);

    Optional<Company> findByRegistrationNumber(String registrationNumber);

    List<Company> findAllByOwnerId(Long id, Pageable pageable);
}
