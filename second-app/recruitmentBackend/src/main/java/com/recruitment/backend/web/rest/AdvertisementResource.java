package com.recruitment.backend.web.rest;

import com.recruitment.backend.repository.AdvertisementRepository;
import com.recruitment.backend.service.AdvertisementService;
import com.recruitment.backend.service.dto.AdvertisementDTO;
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
 * REST controller for managing {@link com.recruitment.backend.domain.Advertisement}.
 */
@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdvertisementResource.class);

    private static final String ENTITY_NAME = "recruitmentBackendAdvertisement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdvertisementService advertisementService;

    private final AdvertisementRepository advertisementRepository;

    public AdvertisementResource(AdvertisementService advertisementService, AdvertisementRepository advertisementRepository) {
        this.advertisementService = advertisementService;
        this.advertisementRepository = advertisementRepository;
    }

    /**
     * {@code POST  /advertisements} : Create a new advertisement.
     *
     * @param advertisementDTO the advertisementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new advertisementDTO, or with status {@code 400 (Bad Request)} if the advertisement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AdvertisementDTO> createAdvertisement(@Valid @RequestBody AdvertisementDTO advertisementDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Advertisement : {}", advertisementDTO);
        if (advertisementDTO.getId() != null) {
            throw new BadRequestAlertException("A new advertisement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        advertisementDTO = advertisementService.save(advertisementDTO);
        return ResponseEntity.created(new URI("/api/advertisements/" + advertisementDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, advertisementDTO.getId().toString()))
            .body(advertisementDTO);
    }

    /**
     * {@code PUT  /advertisements/:id} : Updates an existing advertisement.
     *
     * @param id the id of the advertisementDTO to save.
     * @param advertisementDTO the advertisementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated advertisementDTO,
     * or with status {@code 400 (Bad Request)} if the advertisementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the advertisementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> updateAdvertisement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AdvertisementDTO advertisementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Advertisement : {}, {}", id, advertisementDTO);
        if (advertisementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, advertisementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!advertisementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        advertisementDTO = advertisementService.update(advertisementDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, advertisementDTO.getId().toString()))
            .body(advertisementDTO);
    }

    /**
     * {@code PATCH  /advertisements/:id} : Partial updates given fields of an existing advertisement, field will ignore if it is null
     *
     * @param id the id of the advertisementDTO to save.
     * @param advertisementDTO the advertisementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated advertisementDTO,
     * or with status {@code 400 (Bad Request)} if the advertisementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the advertisementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the advertisementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AdvertisementDTO> partialUpdateAdvertisement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AdvertisementDTO advertisementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Advertisement partially : {}, {}", id, advertisementDTO);
        if (advertisementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, advertisementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!advertisementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AdvertisementDTO> result = advertisementService.partialUpdate(advertisementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, advertisementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /advertisements} : get all the advertisements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of advertisements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AdvertisementDTO>> getAllAdvertisements(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Advertisements");
        Page<AdvertisementDTO> page = advertisementService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /advertisements/:id} : get the "id" advertisement.
     *
     * @param id the id of the advertisementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the advertisementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> getAdvertisement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Advertisement : {}", id);
        Optional<AdvertisementDTO> advertisementDTO = advertisementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(advertisementDTO);
    }

    /**
     * {@code DELETE  /advertisements/:id} : delete the "id" advertisement.
     *
     * @param id the id of the advertisementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Advertisement : {}", id);
        advertisementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
