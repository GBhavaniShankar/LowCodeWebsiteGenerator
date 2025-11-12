package com.recruitment.backend.service;

import com.recruitment.backend.domain.Applicant;
import com.recruitment.backend.repository.ApplicantRepository;
import com.recruitment.backend.service.dto.ApplicantDTO;
import com.recruitment.backend.service.mapper.ApplicantMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.recruitment.backend.domain.Applicant}.
 */
@Service
@Transactional
public class ApplicantService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicantService.class);

    private final ApplicantRepository applicantRepository;

    private final ApplicantMapper applicantMapper;

    public ApplicantService(ApplicantRepository applicantRepository, ApplicantMapper applicantMapper) {
        this.applicantRepository = applicantRepository;
        this.applicantMapper = applicantMapper;
    }

    /**
     * Save a applicant.
     *
     * @param applicantDTO the entity to save.
     * @return the persisted entity.
     */
    public ApplicantDTO save(ApplicantDTO applicantDTO) {
        LOG.debug("Request to save Applicant : {}", applicantDTO);
        Applicant applicant = applicantMapper.toEntity(applicantDTO);
        applicant = applicantRepository.save(applicant);
        return applicantMapper.toDto(applicant);
    }

    /**
     * Update a applicant.
     *
     * @param applicantDTO the entity to save.
     * @return the persisted entity.
     */
    public ApplicantDTO update(ApplicantDTO applicantDTO) {
        LOG.debug("Request to update Applicant : {}", applicantDTO);
        Applicant applicant = applicantMapper.toEntity(applicantDTO);
        applicant = applicantRepository.save(applicant);
        return applicantMapper.toDto(applicant);
    }

    /**
     * Partially update a applicant.
     *
     * @param applicantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ApplicantDTO> partialUpdate(ApplicantDTO applicantDTO) {
        LOG.debug("Request to partially update Applicant : {}", applicantDTO);

        return applicantRepository
            .findById(applicantDTO.getId())
            .map(existingApplicant -> {
                applicantMapper.partialUpdate(existingApplicant, applicantDTO);

                return existingApplicant;
            })
            .map(applicantRepository::save)
            .map(applicantMapper::toDto);
    }

    /**
     * Get all the applicants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ApplicantDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Applicants");
        return applicantRepository.findAll(pageable).map(applicantMapper::toDto);
    }

    /**
     * Get one applicant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ApplicantDTO> findOne(Long id) {
        LOG.debug("Request to get Applicant : {}", id);
        return applicantRepository.findById(id).map(applicantMapper::toDto);
    }

    /**
     * Delete the applicant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Applicant : {}", id);
        applicantRepository.deleteById(id);
    }
}
