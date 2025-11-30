package com.recruitment.backend.repository;

import com.recruitment.backend.domain.Advertisement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Advertisement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {}
