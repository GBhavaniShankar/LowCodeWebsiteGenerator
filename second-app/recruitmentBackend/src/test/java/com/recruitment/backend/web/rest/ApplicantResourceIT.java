package com.recruitment.backend.web.rest;

import static com.recruitment.backend.domain.ApplicantAsserts.*;
import static com.recruitment.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.backend.IntegrationTest;
import com.recruitment.backend.domain.Applicant;
import com.recruitment.backend.repository.ApplicantRepository;
import com.recruitment.backend.service.dto.ApplicantDTO;
import com.recruitment.backend.service.mapper.ApplicantMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ApplicantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApplicantResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "A?y&3@;s`|rZq*Qt";
    private static final String UPDATED_EMAIL = "*%xU4@7jix*z!buf";

    private static final String DEFAULT_PASSWORD_HASH = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACCOUNT_ACTIVATED = false;
    private static final Boolean UPDATED_IS_ACCOUNT_ACTIVATED = true;

    private static final String DEFAULT_AUTHORITIES = "AAAAAAAAAA";
    private static final String UPDATED_AUTHORITIES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/applicants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private ApplicantMapper applicantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApplicantMockMvc;

    private Applicant applicant;

    private Applicant insertedApplicant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Applicant createEntity() {
        return new Applicant()
            .username(DEFAULT_USERNAME)
            .email(DEFAULT_EMAIL)
            .passwordHash(DEFAULT_PASSWORD_HASH)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .isAccountActivated(DEFAULT_IS_ACCOUNT_ACTIVATED)
            .authorities(DEFAULT_AUTHORITIES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Applicant createUpdatedEntity() {
        return new Applicant()
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .isAccountActivated(UPDATED_IS_ACCOUNT_ACTIVATED)
            .authorities(UPDATED_AUTHORITIES);
    }

    @BeforeEach
    void initTest() {
        applicant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedApplicant != null) {
            applicantRepository.delete(insertedApplicant);
            insertedApplicant = null;
        }
    }

    @Test
    @Transactional
    void createApplicant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Applicant
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);
        var returnedApplicantDTO = om.readValue(
            restApplicantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApplicantDTO.class
        );

        // Validate the Applicant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApplicant = applicantMapper.toEntity(returnedApplicantDTO);
        assertApplicantUpdatableFieldsEquals(returnedApplicant, getPersistedApplicant(returnedApplicant));

        insertedApplicant = returnedApplicant;
    }

    @Test
    @Transactional
    void createApplicantWithExistingId() throws Exception {
        // Create the Applicant with an existing ID
        applicant.setId(1L);
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplicantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Applicant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        applicant.setUsername(null);

        // Create the Applicant, which fails.
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        restApplicantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        applicant.setEmail(null);

        // Create the Applicant, which fails.
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        restApplicantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllApplicants() throws Exception {
        // Initialize the database
        insertedApplicant = applicantRepository.saveAndFlush(applicant);

        // Get all the applicantList
        restApplicantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicant.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].passwordHash").value(hasItem(DEFAULT_PASSWORD_HASH)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].isAccountActivated").value(hasItem(DEFAULT_IS_ACCOUNT_ACTIVATED)))
            .andExpect(jsonPath("$.[*].authorities").value(hasItem(DEFAULT_AUTHORITIES)));
    }

    @Test
    @Transactional
    void getApplicant() throws Exception {
        // Initialize the database
        insertedApplicant = applicantRepository.saveAndFlush(applicant);

        // Get the applicant
        restApplicantMockMvc
            .perform(get(ENTITY_API_URL_ID, applicant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(applicant.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.passwordHash").value(DEFAULT_PASSWORD_HASH))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.isAccountActivated").value(DEFAULT_IS_ACCOUNT_ACTIVATED))
            .andExpect(jsonPath("$.authorities").value(DEFAULT_AUTHORITIES));
    }

    @Test
    @Transactional
    void getNonExistingApplicant() throws Exception {
        // Get the applicant
        restApplicantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApplicant() throws Exception {
        // Initialize the database
        insertedApplicant = applicantRepository.saveAndFlush(applicant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicant
        Applicant updatedApplicant = applicantRepository.findById(applicant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApplicant are not directly saved in db
        em.detach(updatedApplicant);
        updatedApplicant
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .isAccountActivated(UPDATED_IS_ACCOUNT_ACTIVATED)
            .authorities(UPDATED_AUTHORITIES);
        ApplicantDTO applicantDTO = applicantMapper.toDto(updatedApplicant);

        restApplicantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Applicant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApplicantToMatchAllProperties(updatedApplicant);
    }

    @Test
    @Transactional
    void putNonExistingApplicant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicant.setId(longCount.incrementAndGet());

        // Create the Applicant
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Applicant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApplicant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicant.setId(longCount.incrementAndGet());

        // Create the Applicant
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Applicant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApplicant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicant.setId(longCount.incrementAndGet());

        // Create the Applicant
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Applicant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApplicantWithPatch() throws Exception {
        // Initialize the database
        insertedApplicant = applicantRepository.saveAndFlush(applicant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicant using partial update
        Applicant partialUpdatedApplicant = new Applicant();
        partialUpdatedApplicant.setId(applicant.getId());

        partialUpdatedApplicant.passwordHash(UPDATED_PASSWORD_HASH).firstName(UPDATED_FIRST_NAME);

        restApplicantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplicant))
            )
            .andExpect(status().isOk());

        // Validate the Applicant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApplicant, applicant),
            getPersistedApplicant(applicant)
        );
    }

    @Test
    @Transactional
    void fullUpdateApplicantWithPatch() throws Exception {
        // Initialize the database
        insertedApplicant = applicantRepository.saveAndFlush(applicant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicant using partial update
        Applicant partialUpdatedApplicant = new Applicant();
        partialUpdatedApplicant.setId(applicant.getId());

        partialUpdatedApplicant
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .isAccountActivated(UPDATED_IS_ACCOUNT_ACTIVATED)
            .authorities(UPDATED_AUTHORITIES);

        restApplicantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplicant))
            )
            .andExpect(status().isOk());

        // Validate the Applicant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicantUpdatableFieldsEquals(partialUpdatedApplicant, getPersistedApplicant(partialUpdatedApplicant));
    }

    @Test
    @Transactional
    void patchNonExistingApplicant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicant.setId(longCount.incrementAndGet());

        // Create the Applicant
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, applicantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Applicant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApplicant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicant.setId(longCount.incrementAndGet());

        // Create the Applicant
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Applicant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApplicant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicant.setId(longCount.incrementAndGet());

        // Create the Applicant
        ApplicantDTO applicantDTO = applicantMapper.toDto(applicant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(applicantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Applicant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApplicant() throws Exception {
        // Initialize the database
        insertedApplicant = applicantRepository.saveAndFlush(applicant);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the applicant
        restApplicantMockMvc
            .perform(delete(ENTITY_API_URL_ID, applicant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return applicantRepository.count();
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

    protected Applicant getPersistedApplicant(Applicant applicant) {
        return applicantRepository.findById(applicant.getId()).orElseThrow();
    }

    protected void assertPersistedApplicantToMatchAllProperties(Applicant expectedApplicant) {
        assertApplicantAllPropertiesEquals(expectedApplicant, getPersistedApplicant(expectedApplicant));
    }

    protected void assertPersistedApplicantToMatchUpdatableProperties(Applicant expectedApplicant) {
        assertApplicantAllUpdatablePropertiesEquals(expectedApplicant, getPersistedApplicant(expectedApplicant));
    }
}
