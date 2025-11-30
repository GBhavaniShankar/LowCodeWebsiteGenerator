package com.recruitment.backend.web.rest;

import com.recruitment.backend.repository.ConfigRepository;
import com.recruitment.backend.service.ConfigService;
import com.recruitment.backend.service.dto.ConfigDTO;
import com.recruitment.backend.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.recruitment.backend.domain.Config}.
 */
@RestController
@RequestMapping("/api/configs")
public class ConfigResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigResource.class);

    private static final String ENTITY_NAME = "recruitmentBackendConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConfigService configService;

    private final ConfigRepository configRepository;

    public ConfigResource(ConfigService configService, ConfigRepository configRepository) {
        this.configService = configService;
        this.configRepository = configRepository;
    }

    /**
     * {@code POST  /configs} : Create a new config.
     *
     * @param configDTO the configDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new configDTO, or with status {@code 400 (Bad Request)} if the config has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ConfigDTO> createConfig(@RequestBody ConfigDTO configDTO) throws URISyntaxException {
        LOG.debug("REST request to save Config : {}", configDTO);
        if (configDTO.getId() != null) {
            throw new BadRequestAlertException("A new config cannot already have an ID", ENTITY_NAME, "idexists");
        }
        configDTO = configService.save(configDTO);
        return ResponseEntity.created(new URI("/api/configs/" + configDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, configDTO.getId().toString()))
            .body(configDTO);
    }

    /**
     * {@code PUT  /configs/:id} : Updates an existing config.
     *
     * @param id the id of the configDTO to save.
     * @param configDTO the configDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated configDTO,
     * or with status {@code 400 (Bad Request)} if the configDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the configDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConfigDTO> updateConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ConfigDTO configDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Config : {}, {}", id, configDTO);
        if (configDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, configDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!configRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        configDTO = configService.update(configDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, configDTO.getId().toString()))
            .body(configDTO);
    }

    /**
     * {@code PATCH  /configs/:id} : Partial updates given fields of an existing config, field will ignore if it is null
     *
     * @param id the id of the configDTO to save.
     * @param configDTO the configDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated configDTO,
     * or with status {@code 400 (Bad Request)} if the configDTO is not valid,
     * or with status {@code 404 (Not Found)} if the configDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the configDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ConfigDTO> partialUpdateConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ConfigDTO configDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Config partially : {}, {}", id, configDTO);
        if (configDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, configDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!configRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ConfigDTO> result = configService.partialUpdate(configDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, configDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /configs} : get all the configs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of configs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ConfigDTO>> getAllConfigs(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Configs");
        Page<ConfigDTO> page = configService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /configs/:id} : get the "id" config.
     *
     * @param id the id of the configDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the configDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConfigDTO> getConfig(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Config : {}", id);
        Optional<ConfigDTO> configDTO = configService.findOne(id);
        return ResponseUtil.wrapOrNotFound(configDTO);
    }

    /**
     * {@code DELETE  /configs/:id} : delete the "id" config.
     *
     * @param id the id of the configDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Config : {}", id);
        configService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
