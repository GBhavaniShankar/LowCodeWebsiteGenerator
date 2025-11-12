package com.recruitment.backend.web.rest;

import com.recruitment.backend.repository.ApplicationFeeCategoryRepository;
import com.recruitment.backend.service.ApplicationFeeCategoryService;
import com.recruitment.backend.service.dto.ApplicationFeeCategoryDTO;
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
 * REST controller for managing {@link com.recruitment.backend.domain.ApplicationFeeCategory}.
 */
@RestController
@RequestMapping("/api/application-fee-categories")
public class ApplicationFeeCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFeeCategoryResource.class);

    private static final String ENTITY_NAME = "recruitmentBackendApplicationFeeCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApplicationFeeCategoryService applicationFeeCategoryService;

    private final ApplicationFeeCategoryRepository applicationFeeCategoryRepository;

    public ApplicationFeeCategoryResource(
        ApplicationFeeCategoryService applicationFeeCategoryService,
        ApplicationFeeCategoryRepository applicationFeeCategoryRepository
    ) {
        this.applicationFeeCategoryService = applicationFeeCategoryService;
        this.applicationFeeCategoryRepository = applicationFeeCategoryRepository;
    }

    /**
     * {@code POST  /application-fee-categories} : Create a new applicationFeeCategory.
     *
     * @param applicationFeeCategoryDTO the applicationFeeCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new applicationFeeCategoryDTO, or with status {@code 400 (Bad Request)} if the applicationFeeCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ApplicationFeeCategoryDTO> createApplicationFeeCategory(
        @Valid @RequestBody ApplicationFeeCategoryDTO applicationFeeCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ApplicationFeeCategory : {}", applicationFeeCategoryDTO);
        if (applicationFeeCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new applicationFeeCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        applicationFeeCategoryDTO = applicationFeeCategoryService.save(applicationFeeCategoryDTO);
        return ResponseEntity.created(new URI("/api/application-fee-categories/" + applicationFeeCategoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, applicationFeeCategoryDTO.getId().toString()))
            .body(applicationFeeCategoryDTO);
    }

    /**
     * {@code PUT  /application-fee-categories/:id} : Updates an existing applicationFeeCategory.
     *
     * @param id the id of the applicationFeeCategoryDTO to save.
     * @param applicationFeeCategoryDTO the applicationFeeCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationFeeCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the applicationFeeCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the applicationFeeCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationFeeCategoryDTO> updateApplicationFeeCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApplicationFeeCategoryDTO applicationFeeCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ApplicationFeeCategory : {}, {}", id, applicationFeeCategoryDTO);
        if (applicationFeeCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicationFeeCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationFeeCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        applicationFeeCategoryDTO = applicationFeeCategoryService.update(applicationFeeCategoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, applicationFeeCategoryDTO.getId().toString()))
            .body(applicationFeeCategoryDTO);
    }

    /**
     * {@code PATCH  /application-fee-categories/:id} : Partial updates given fields of an existing applicationFeeCategory, field will ignore if it is null
     *
     * @param id the id of the applicationFeeCategoryDTO to save.
     * @param applicationFeeCategoryDTO the applicationFeeCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationFeeCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the applicationFeeCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the applicationFeeCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the applicationFeeCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApplicationFeeCategoryDTO> partialUpdateApplicationFeeCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApplicationFeeCategoryDTO applicationFeeCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ApplicationFeeCategory partially : {}, {}", id, applicationFeeCategoryDTO);
        if (applicationFeeCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicationFeeCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationFeeCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApplicationFeeCategoryDTO> result = applicationFeeCategoryService.partialUpdate(applicationFeeCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, applicationFeeCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /application-fee-categories} : get all the applicationFeeCategories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applicationFeeCategories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ApplicationFeeCategoryDTO>> getAllApplicationFeeCategories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ApplicationFeeCategories");
        Page<ApplicationFeeCategoryDTO> page = applicationFeeCategoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /application-fee-categories/:id} : get the "id" applicationFeeCategory.
     *
     * @param id the id of the applicationFeeCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the applicationFeeCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationFeeCategoryDTO> getApplicationFeeCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ApplicationFeeCategory : {}", id);
        Optional<ApplicationFeeCategoryDTO> applicationFeeCategoryDTO = applicationFeeCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(applicationFeeCategoryDTO);
    }

    /**
     * {@code DELETE  /application-fee-categories/:id} : delete the "id" applicationFeeCategory.
     *
     * @param id the id of the applicationFeeCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplicationFeeCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ApplicationFeeCategory : {}", id);
        applicationFeeCategoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
