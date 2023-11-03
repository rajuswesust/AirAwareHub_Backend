package com.airawarehub.backend.repository;

import com.airawarehub.backend.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
}
