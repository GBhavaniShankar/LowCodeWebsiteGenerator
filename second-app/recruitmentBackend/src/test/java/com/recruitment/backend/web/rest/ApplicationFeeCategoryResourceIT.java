package com.recruitment.backend.web.rest;

import static com.recruitment.backend.domain.ApplicationFeeCategoryAsserts.*;
import static com.recruitment.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.recruitment.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.backend.IntegrationTest;
import com.recruitment.backend.domain.ApplicationFeeCategory;
import com.recruitment.backend.repository.ApplicationFeeCategoryRepository;
import com.recruitment.backend.service.dto.ApplicationFeeCategoryDTO;
import com.recruitment.backend.service.mapper.ApplicationFeeCategoryMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ApplicationFeeCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApplicationFeeCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_FEE = new BigDecimal(0);
    private static final BigDecimal UPDATED_FEE = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/application-fee-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApplicationFeeCategoryRepository applicationFeeCategoryRepository;

    @Autowired
    private ApplicationFeeCategoryMapper applicationFeeCategoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApplicationFeeCategoryMockMvc;

    private ApplicationFeeCategory applicationFeeCategory;

    private ApplicationFeeCategory insertedApplicationFeeCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationFeeCategory createEntity() {
        return new ApplicationFeeCategory().name(DEFAULT_NAME).fee(DEFAULT_FEE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationFeeCategory createUpdatedEntity() {
        return new ApplicationFeeCategory().name(UPDATED_NAME).fee(UPDATED_FEE);
    }

    @BeforeEach
    void initTest() {
        applicationFeeCategory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedApplicationFeeCategory != null) {
            applicationFeeCategoryRepository.delete(insertedApplicationFeeCategory);
            insertedApplicationFeeCategory = null;
        }
    }

    @Test
    @Transactional
    void createApplicationFeeCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ApplicationFeeCategory
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);
        var returnedApplicationFeeCategoryDTO = om.readValue(
            restApplicationFeeCategoryMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationFeeCategoryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApplicationFeeCategoryDTO.class
        );

        // Validate the ApplicationFeeCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApplicationFeeCategory = applicationFeeCategoryMapper.toEntity(returnedApplicationFeeCategoryDTO);
        assertApplicationFeeCategoryUpdatableFieldsEquals(
            returnedApplicationFeeCategory,
            getPersistedApplicationFeeCategory(returnedApplicationFeeCategory)
        );

        insertedApplicationFeeCategory = returnedApplicationFeeCategory;
    }

    @Test
    @Transactional
    void createApplicationFeeCategoryWithExistingId() throws Exception {
        // Create the ApplicationFeeCategory with an existing ID
        applicationFeeCategory.setId(1L);
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplicationFeeCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationFeeCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ApplicationFeeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        applicationFeeCategory.setName(null);

        // Create the ApplicationFeeCategory, which fails.
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        restApplicationFeeCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationFeeCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFeeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        applicationFeeCategory.setFee(null);

        // Create the ApplicationFeeCategory, which fails.
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        restApplicationFeeCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationFeeCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllApplicationFeeCategories() throws Exception {
        // Initialize the database
        insertedApplicationFeeCategory = applicationFeeCategoryRepository.saveAndFlush(applicationFeeCategory);

        // Get all the applicationFeeCategoryList
        restApplicationFeeCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationFeeCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fee").value(hasItem(sameNumber(DEFAULT_FEE))));
    }

    @Test
    @Transactional
    void getApplicationFeeCategory() throws Exception {
        // Initialize the database
        insertedApplicationFeeCategory = applicationFeeCategoryRepository.saveAndFlush(applicationFeeCategory);

        // Get the applicationFeeCategory
        restApplicationFeeCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, applicationFeeCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(applicationFeeCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fee").value(sameNumber(DEFAULT_FEE)));
    }

    @Test
    @Transactional
    void getNonExistingApplicationFeeCategory() throws Exception {
        // Get the applicationFeeCategory
        restApplicationFeeCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApplicationFeeCategory() throws Exception {
        // Initialize the database
        insertedApplicationFeeCategory = applicationFeeCategoryRepository.saveAndFlush(applicationFeeCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationFeeCategory
        ApplicationFeeCategory updatedApplicationFeeCategory = applicationFeeCategoryRepository
            .findById(applicationFeeCategory.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedApplicationFeeCategory are not directly saved in db
        em.detach(updatedApplicationFeeCategory);
        updatedApplicationFeeCategory.name(UPDATED_NAME).fee(UPDATED_FEE);
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(updatedApplicationFeeCategory);

        restApplicationFeeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationFeeCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationFeeCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationFeeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApplicationFeeCategoryToMatchAllProperties(updatedApplicationFeeCategory);
    }

    @Test
    @Transactional
    void putNonExistingApplicationFeeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationFeeCategory.setId(longCount.incrementAndGet());

        // Create the ApplicationFeeCategory
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationFeeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationFeeCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationFeeCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationFeeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApplicationFeeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationFeeCategory.setId(longCount.incrementAndGet());

        // Create the ApplicationFeeCategory
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationFeeCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationFeeCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationFeeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApplicationFeeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationFeeCategory.setId(longCount.incrementAndGet());

        // Create the ApplicationFeeCategory
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationFeeCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationFeeCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationFeeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApplicationFeeCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedApplicationFeeCategory = applicationFeeCategoryRepository.saveAndFlush(applicationFeeCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationFeeCategory using partial update
        ApplicationFeeCategory partialUpdatedApplicationFeeCategory = new ApplicationFeeCategory();
        partialUpdatedApplicationFeeCategory.setId(applicationFeeCategory.getId());

        partialUpdatedApplicationFeeCategory.fee(UPDATED_FEE);

        restApplicationFeeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationFeeCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplicationFeeCategory))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationFeeCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationFeeCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApplicationFeeCategory, applicationFeeCategory),
            getPersistedApplicationFeeCategory(applicationFeeCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdateApplicationFeeCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedApplicationFeeCategory = applicationFeeCategoryRepository.saveAndFlush(applicationFeeCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationFeeCategory using partial update
        ApplicationFeeCategory partialUpdatedApplicationFeeCategory = new ApplicationFeeCategory();
        partialUpdatedApplicationFeeCategory.setId(applicationFeeCategory.getId());

        partialUpdatedApplicationFeeCategory.name(UPDATED_NAME).fee(UPDATED_FEE);

        restApplicationFeeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationFeeCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplicationFeeCategory))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationFeeCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationFeeCategoryUpdatableFieldsEquals(
            partialUpdatedApplicationFeeCategory,
            getPersistedApplicationFeeCategory(partialUpdatedApplicationFeeCategory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingApplicationFeeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationFeeCategory.setId(longCount.incrementAndGet());

        // Create the ApplicationFeeCategory
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationFeeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, applicationFeeCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicationFeeCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationFeeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApplicationFeeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationFeeCategory.setId(longCount.incrementAndGet());

        // Create the ApplicationFeeCategory
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationFeeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicationFeeCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationFeeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApplicationFeeCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationFeeCategory.setId(longCount.incrementAndGet());

        // Create the ApplicationFeeCategory
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = applicationFeeCategoryMapper.toDto(applicationFeeCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationFeeCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(applicationFeeCategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationFeeCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApplicationFeeCategory() throws Exception {
        // Initialize the database
        insertedApplicationFeeCategory = applicationFeeCategoryRepository.saveAndFlush(applicationFeeCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the applicationFeeCategory
        restApplicationFeeCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, applicationFeeCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return applicationFeeCategoryRepository.count();
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

    protected ApplicationFeeCategory getPersistedApplicationFeeCategory(ApplicationFeeCategory applicationFeeCategory) {
        return applicationFeeCategoryRepository.findById(applicationFeeCategory.getId()).orElseThrow();
    }

    protected void assertPersistedApplicationFeeCategoryToMatchAllProperties(ApplicationFeeCategory expectedApplicationFeeCategory) {
        assertApplicationFeeCategoryAllPropertiesEquals(
            expectedApplicationFeeCategory,
            getPersistedApplicationFeeCategory(expectedApplicationFeeCategory)
        );
    }

    protected void assertPersistedApplicationFeeCategoryToMatchUpdatableProperties(ApplicationFeeCategory expectedApplicationFeeCategory) {
        assertApplicationFeeCategoryAllUpdatablePropertiesEquals(
            expectedApplicationFeeCategory,
            getPersistedApplicationFeeCategory(expectedApplicationFeeCategory)
        );
    }
}
