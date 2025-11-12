package com.recruitment.backend.repository;

import com.recruitment.backend.domain.ApplicationFeeCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ApplicationFeeCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApplicationFeeCategoryRepository extends JpaRepository<ApplicationFeeCategory, Long> {}
