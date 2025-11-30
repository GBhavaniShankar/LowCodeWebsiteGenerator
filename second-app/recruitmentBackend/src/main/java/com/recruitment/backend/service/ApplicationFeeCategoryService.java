package com.recruitment.backend.service;

import com.recruitment.backend.domain.ApplicationFeeCategory;
import com.recruitment.backend.repository.ApplicationFeeCategoryRepository;
import com.recruitment.backend.service.dto.ApplicationFeeCategoryDTO;
import com.recruitment.backend.service.mapper.ApplicationFeeCategoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.recruitment.backend.domain.ApplicationFeeCategory}.
 */
@Service
@Transactional
public class ApplicationFeeCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFeeCategoryService.class);

    private final ApplicationFeeCategoryRepository applicationFeeCategoryRepository;

    private final ApplicationFeeCategoryMapper applicationFeeCategoryMapper;

    public ApplicationFeeCategoryService(
        ApplicationFeeCategoryRepository applicationFeeCategoryRepository,
        ApplicationFeeCategoryMapper applicationFeeCategoryMapper
    ) {
        this.applicationFeeCategoryRepository = applicationFeeCategoryRepository;
        this.applicationFeeCategoryMapper = applicationFeeCategoryMapper;
    }

    /**
     * Save a applicationFeeCategory.
     *
     * @param applicationFeeCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ApplicationFeeCategoryDTO save(ApplicationFeeCategoryDTO applicationFeeCategoryDTO) {
        LOG.debug("Request to save ApplicationFeeCategory : {}", applicationFeeCategoryDTO);
        ApplicationFeeCategory applicationFeeCategory = applicationFeeCategoryMapper.toEntity(applicationFeeCategoryDTO);
        applicationFeeCategory = applicationFeeCategoryRepository.save(applicationFeeCategory);
        return applicationFeeCategoryMapper.toDto(applicationFeeCategory);
    }

    /**
     * Update a applicationFeeCategory.
     *
     * @param applicationFeeCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ApplicationFeeCategoryDTO update(ApplicationFeeCategoryDTO applicationFeeCategoryDTO) {
        LOG.debug("Request to update ApplicationFeeCategory : {}", applicationFeeCategoryDTO);
        ApplicationFeeCategory applicationFeeCategory = applicationFeeCategoryMapper.toEntity(applicationFeeCategoryDTO);
        applicationFeeCategory = applicationFeeCategoryRepository.save(applicationFeeCategory);
        return applicationFeeCategoryMapper.toDto(applicationFeeCategory);
    }

    /**
     * Partially update a applicationFeeCategory.
     *
     * @param applicationFeeCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ApplicationFeeCategoryDTO> partialUpdate(ApplicationFeeCategoryDTO applicationFeeCategoryDTO) {
        LOG.debug("Request to partially update ApplicationFeeCategory : {}", applicationFeeCategoryDTO);

        return applicationFeeCategoryRepository
            .findById(applicationFeeCategoryDTO.getId())
            .map(existingApplicationFeeCategory -> {
                applicationFeeCategoryMapper.partialUpdate(existingApplicationFeeCategory, applicationFeeCategoryDTO);

                return existingApplicationFeeCategory;
            })
            .map(applicationFeeCategoryRepository::save)
            .map(applicationFeeCategoryMapper::toDto);
    }

    /**
     * Get all the applicationFeeCategories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ApplicationFeeCategoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ApplicationFeeCategories");
        return applicationFeeCategoryRepository.findAll(pageable).map(applicationFeeCategoryMapper::toDto);
    }

    /**
     * Get one applicationFeeCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ApplicationFeeCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get ApplicationFeeCategory : {}", id);
        return applicationFeeCategoryRepository.findById(id).map(applicationFeeCategoryMapper::toDto);
    }

    /**
     * Delete the applicationFeeCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ApplicationFeeCategory : {}", id);
        applicationFeeCategoryRepository.deleteById(id);
    }
}
