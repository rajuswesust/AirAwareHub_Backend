package com.airawarehub.backend.repository;

import com.airawarehub.backend.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
