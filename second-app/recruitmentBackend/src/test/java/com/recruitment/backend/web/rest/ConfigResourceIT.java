package com.recruitment.backend.web.rest;

import static com.recruitment.backend.domain.ConfigAsserts.*;
import static com.recruitment.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.backend.IntegrationTest;
import com.recruitment.backend.domain.Config;
import com.recruitment.backend.repository.ConfigRepository;
import com.recruitment.backend.service.dto.ConfigDTO;
import com.recruitment.backend.service.mapper.ConfigMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConfigResourceIT {

    private static final Boolean DEFAULT_PORTAL_ACTIVE = false;
    private static final Boolean UPDATED_PORTAL_ACTIVE = true;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SAMPLE_FORM_URL = "AAAAAAAAAA";
    private static final String UPDATED_SAMPLE_FORM_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConfigMockMvc;

    private Config config;

    private Config insertedConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Config createEntity() {
        return new Config()
            .portalActive(DEFAULT_PORTAL_ACTIVE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .sampleFormUrl(DEFAULT_SAMPLE_FORM_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Config createUpdatedEntity() {
        return new Config()
            .portalActive(UPDATED_PORTAL_ACTIVE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .sampleFormUrl(UPDATED_SAMPLE_FORM_URL);
    }

    @BeforeEach
    void initTest() {
        config = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedConfig != null) {
            configRepository.delete(insertedConfig);
            insertedConfig = null;
        }
    }

    @Test
    @Transactional
    void createConfig() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Config
        ConfigDTO configDTO = configMapper.toDto(config);
        var returnedConfigDTO = om.readValue(
            restConfigMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(configDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ConfigDTO.class
        );

        // Validate the Config in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedConfig = configMapper.toEntity(returnedConfigDTO);
        assertConfigUpdatableFieldsEquals(returnedConfig, getPersistedConfig(returnedConfig));

        insertedConfig = returnedConfig;
    }

    @Test
    @Transactional
    void createConfigWithExistingId() throws Exception {
        // Create the Config with an existing ID
        config.setId(1L);
        ConfigDTO configDTO = configMapper.toDto(config);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(configDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllConfigs() throws Exception {
        // Initialize the database
        insertedConfig = configRepository.saveAndFlush(config);

        // Get all the configList
        restConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(config.getId().intValue())))
            .andExpect(jsonPath("$.[*].portalActive").value(hasItem(DEFAULT_PORTAL_ACTIVE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].sampleFormUrl").value(hasItem(DEFAULT_SAMPLE_FORM_URL)));
    }

    @Test
    @Transactional
    void getConfig() throws Exception {
        // Initialize the database
        insertedConfig = configRepository.saveAndFlush(config);

        // Get the config
        restConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, config.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(config.getId().intValue()))
            .andExpect(jsonPath("$.portalActive").value(DEFAULT_PORTAL_ACTIVE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.sampleFormUrl").value(DEFAULT_SAMPLE_FORM_URL));
    }

    @Test
    @Transactional
    void getNonExistingConfig() throws Exception {
        // Get the config
        restConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConfig() throws Exception {
        // Initialize the database
        insertedConfig = configRepository.saveAndFlush(config);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the config
        Config updatedConfig = configRepository.findById(config.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedConfig are not directly saved in db
        em.detach(updatedConfig);
        updatedConfig
            .portalActive(UPDATED_PORTAL_ACTIVE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .sampleFormUrl(UPDATED_SAMPLE_FORM_URL);
        ConfigDTO configDTO = configMapper.toDto(updatedConfig);

        restConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, configDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(configDTO))
            )
            .andExpect(status().isOk());

        // Validate the Config in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConfigToMatchAllProperties(updatedConfig);
    }

    @Test
    @Transactional
    void putNonExistingConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        config.setId(longCount.incrementAndGet());

        // Create the Config
        ConfigDTO configDTO = configMapper.toDto(config);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, configDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(configDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        config.setId(longCount.incrementAndGet());

        // Create the Config
        ConfigDTO configDTO = configMapper.toDto(config);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(configDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        config.setId(longCount.incrementAndGet());

        // Create the Config
        ConfigDTO configDTO = configMapper.toDto(config);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(configDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Config in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConfigWithPatch() throws Exception {
        // Initialize the database
        insertedConfig = configRepository.saveAndFlush(config);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the config using partial update
        Config partialUpdatedConfig = new Config();
        partialUpdatedConfig.setId(config.getId());

        partialUpdatedConfig.startDate(UPDATED_START_DATE).sampleFormUrl(UPDATED_SAMPLE_FORM_URL);

        restConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConfig))
            )
            .andExpect(status().isOk());

        // Validate the Config in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConfigUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedConfig, config), getPersistedConfig(config));
    }

    @Test
    @Transactional
    void fullUpdateConfigWithPatch() throws Exception {
        // Initialize the database
        insertedConfig = configRepository.saveAndFlush(config);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the config using partial update
        Config partialUpdatedConfig = new Config();
        partialUpdatedConfig.setId(config.getId());

        partialUpdatedConfig
            .portalActive(UPDATED_PORTAL_ACTIVE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .sampleFormUrl(UPDATED_SAMPLE_FORM_URL);

        restConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConfig))
            )
            .andExpect(status().isOk());

        // Validate the Config in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConfigUpdatableFieldsEquals(partialUpdatedConfig, getPersistedConfig(partialUpdatedConfig));
    }

    @Test
    @Transactional
    void patchNonExistingConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        config.setId(longCount.incrementAndGet());

        // Create the Config
        ConfigDTO configDTO = configMapper.toDto(config);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, configDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(configDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        config.setId(longCount.incrementAndGet());

        // Create the Config
        ConfigDTO configDTO = configMapper.toDto(config);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(configDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Config in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        config.setId(longCount.incrementAndGet());

        // Create the Config
        ConfigDTO configDTO = configMapper.toDto(config);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConfigMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(configDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Config in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConfig() throws Exception {
        // Initialize the database
        insertedConfig = configRepository.saveAndFlush(config);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the config
        restConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, config.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return configRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Config getPersistedConfig(Config config) {
        return configRepository.findById(config.getId()).orElseThrow();
    }

    protected void assertPersistedConfigToMatchAllProperties(Config expectedConfig) {
        assertConfigAllPropertiesEquals(expectedConfig, getPersistedConfig(expectedConfig));
    }

    protected void assertPersistedConfigToMatchUpdatableProperties(Config expectedConfig) {
        assertConfigAllUpdatablePropertiesEquals(expectedConfig, getPersistedConfig(expectedConfig));
    }
}
