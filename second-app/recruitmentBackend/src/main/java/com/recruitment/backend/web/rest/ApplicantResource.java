package com.recruitment.backend.web.rest;

import com.recruitment.backend.repository.ApplicantRepository;
import com.recruitment.backend.service.ApplicantService;
import com.recruitment.backend.service.dto.ApplicantDTO;
import com.recruitment.backend.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.recruitment.backend.domain.Applicant}.
 */
@RestController
@RequestMapping("/api/applicants")
public class ApplicantResource {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicantResource.class);

    private static final String ENTITY_NAME = "recruitmentBackendApplicant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApplicantService applicantService;

    private final ApplicantRepository applicantRepository;

    public ApplicantResource(ApplicantService applicantService, ApplicantRepository applicantRepository) {
        this.applicantService = applicantService;
        this.applicantRepository = applicantRepository;
    }

    /**
     * {@code POST  /applicants} : Create a new applicant.
     *
     * @param applicantDTO the applicantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new applicantDTO, or with status {@code 400 (Bad Request)} if the applicant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ApplicantDTO> createApplicant(@Valid @RequestBody ApplicantDTO applicantDTO) throws URISyntaxException {
        LOG.debug("REST request to save Applicant : {}", applicantDTO);
        if (applicantDTO.getId() != null) {
            throw new BadRequestAlertException("A new applicant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        applicantDTO = applicantService.save(applicantDTO);
        return ResponseEntity.created(new URI("/api/applicants/" + applicantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, applicantDTO.getId().toString()))
            .body(applicantDTO);
    }

    /**
     * {@code PUT  /applicants/:id} : Updates an existing applicant.
     *
     * @param id the id of the applicantDTO to save.
     * @param applicantDTO the applicantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicantDTO,
     * or with status {@code 400 (Bad Request)} if the applicantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the applicantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApplicantDTO> updateApplicant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApplicantDTO applicantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Applicant : {}, {}", id, applicantDTO);
        if (applicantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        applicantDTO = applicantService.update(applicantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, applicantDTO.getId().toString()))
            .body(applicantDTO);
    }

    /**
     * {@code PATCH  /applicants/:id} : Partial updates given fields of an existing applicant, field will ignore if it is null
     *
     * @param id the id of the applicantDTO to save.
     * @param applicantDTO the applicantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicantDTO,
     * or with status {@code 400 (Bad Request)} if the applicantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the applicantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the applicantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApplicantDTO> partialUpdateApplicant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApplicantDTO applicantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Applicant partially : {}, {}", id, applicantDTO);
        if (applicantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApplicantDTO> result = applicantService.partialUpdate(applicantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, applicantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /applicants} : get all the applicants.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applicants in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ApplicantDTO>> getAllApplicants(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Applicants");
        Page<ApplicantDTO> page = applicantService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /applicants/:id} : get the "id" applicant.
     *
     * @param id the id of the applicantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the applicantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicantDTO> getApplicant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Applicant : {}", id);
        Optional<ApplicantDTO> applicantDTO = applicantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(applicantDTO);
    }

    /**
     * {@code DELETE  /applicants/:id} : delete the "id" applicant.
     *
     * @param id the id of the applicantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplicant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Applicant : {}", id);
        applicantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
