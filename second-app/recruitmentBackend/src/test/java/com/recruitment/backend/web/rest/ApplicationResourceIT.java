package com.recruitment.backend.web.rest;

import static com.recruitment.backend.domain.ApplicationAsserts.*;
import static com.recruitment.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.backend.IntegrationTest;
import com.recruitment.backend.domain.Application;
import com.recruitment.backend.domain.enumeration.ApplicationStatus;
import com.recruitment.backend.repository.ApplicationRepository;
import com.recruitment.backend.service.ApplicationService;
import com.recruitment.backend.service.dto.ApplicationDTO;
import com.recruitment.backend.service.mapper.ApplicationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ApplicationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ApplicationResourceIT {

    private static final String DEFAULT_UNIQUE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_UNIQUE_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_SUBMISSION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMISSION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final ApplicationStatus DEFAULT_STATUS = ApplicationStatus.SAVED;
    private static final ApplicationStatus UPDATED_STATUS = ApplicationStatus.SUBMITTED;

    private static final Boolean DEFAULT_PAYMENT_SUCCESSFUL = false;
    private static final Boolean UPDATED_PAYMENT_SUCCESSFUL = true;

    private static final String ENTITY_API_URL = "/api/applications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Mock
    private ApplicationService applicationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApplicationMockMvc;

    private Application application;

    private Application insertedApplication;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Application createEntity() {
        return new Application()
            .uniqueNumber(DEFAULT_UNIQUE_NUMBER)
            .submissionDate(DEFAULT_SUBMISSION_DATE)
            .status(DEFAULT_STATUS)
            .paymentSuccessful(DEFAULT_PAYMENT_SUCCESSFUL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Application createUpdatedEntity() {
        return new Application()
            .uniqueNumber(UPDATED_UNIQUE_NUMBER)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .paymentSuccessful(UPDATED_PAYMENT_SUCCESSFUL);
    }

    @BeforeEach
    void initTest() {
        application = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedApplication != null) {
            applicationRepository.delete(insertedApplication);
            insertedApplication = null;
        }
    }

    @Test
    @Transactional
    void createApplication() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Application
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);
        var returnedApplicationDTO = om.readValue(
            restApplicationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApplicationDTO.class
        );

        // Validate the Application in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApplication = applicationMapper.toEntity(returnedApplicationDTO);
        assertApplicationUpdatableFieldsEquals(returnedApplication, getPersistedApplication(returnedApplication));

        insertedApplication = returnedApplication;
    }

    @Test
    @Transactional
    void createApplicationWithExistingId() throws Exception {
        // Create the Application with an existing ID
        application.setId(1L);
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Application in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUniqueNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        application.setUniqueNumber(null);

        // Create the Application, which fails.
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        restApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        application.setStatus(null);

        // Create the Application, which fails.
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        restApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllApplications() throws Exception {
        // Initialize the database
        insertedApplication = applicationRepository.saveAndFlush(application);

        // Get all the applicationList
        restApplicationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(application.getId().intValue())))
            .andExpect(jsonPath("$.[*].uniqueNumber").value(hasItem(DEFAULT_UNIQUE_NUMBER)))
            .andExpect(jsonPath("$.[*].submissionDate").value(hasItem(DEFAULT_SUBMISSION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentSuccessful").value(hasItem(DEFAULT_PAYMENT_SUCCESSFUL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllApplicationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(applicationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restApplicationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(applicationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllApplicationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(applicationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restApplicationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(applicationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getApplication() throws Exception {
        // Initialize the database
        insertedApplication = applicationRepository.saveAndFlush(application);

        // Get the application
        restApplicationMockMvc
            .perform(get(ENTITY_API_URL_ID, application.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(application.getId().intValue()))
            .andExpect(jsonPath("$.uniqueNumber").value(DEFAULT_UNIQUE_NUMBER))
            .andExpect(jsonPath("$.submissionDate").value(DEFAULT_SUBMISSION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentSuccessful").value(DEFAULT_PAYMENT_SUCCESSFUL));
    }

    @Test
    @Transactional
    void getNonExistingApplication() throws Exception {
        // Get the application
        restApplicationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApplication() throws Exception {
        // Initialize the database
        insertedApplication = applicationRepository.saveAndFlush(application);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the application
        Application updatedApplication = applicationRepository.findById(application.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApplication are not directly saved in db
        em.detach(updatedApplication);
        updatedApplication
            .uniqueNumber(UPDATED_UNIQUE_NUMBER)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .paymentSuccessful(UPDATED_PAYMENT_SUCCESSFUL);
        ApplicationDTO applicationDTO = applicationMapper.toDto(updatedApplication);

        restApplicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Application in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApplicationToMatchAllProperties(updatedApplication);
    }

    @Test
    @Transactional
    void putNonExistingApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        application.setId(longCount.incrementAndGet());

        // Create the Application
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Application in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        application.setId(longCount.incrementAndGet());

        // Create the Application
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Application in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        application.setId(longCount.incrementAndGet());

        // Create the Application
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Application in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApplicationWithPatch() throws Exception {
        // Initialize the database
        insertedApplication = applicationRepository.saveAndFlush(application);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the application using partial update
        Application partialUpdatedApplication = new Application();
        partialUpdatedApplication.setId(application.getId());

        partialUpdatedApplication.status(UPDATED_STATUS);

        restApplicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplication.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplication))
            )
            .andExpect(status().isOk());

        // Validate the Application in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApplication, application),
            getPersistedApplication(application)
        );
    }

    @Test
    @Transactional
    void fullUpdateApplicationWithPatch() throws Exception {
        // Initialize the database
        insertedApplication = applicationRepository.saveAndFlush(application);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the application using partial update
        Application partialUpdatedApplication = new Application();
        partialUpdatedApplication.setId(application.getId());

        partialUpdatedApplication
            .uniqueNumber(UPDATED_UNIQUE_NUMBER)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .paymentSuccessful(UPDATED_PAYMENT_SUCCESSFUL);

        restApplicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplication.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplication))
            )
            .andExpect(status().isOk());

        // Validate the Application in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationUpdatableFieldsEquals(partialUpdatedApplication, getPersistedApplication(partialUpdatedApplication));
    }

    @Test
    @Transactional
    void patchNonExistingApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        application.setId(longCount.incrementAndGet());

        // Create the Application
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, applicationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Application in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        application.setId(longCount.incrementAndGet());

        // Create the Application
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Application in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        application.setId(longCount.incrementAndGet());

        // Create the Application
        ApplicationDTO applicationDTO = applicationMapper.toDto(application);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(applicationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Application in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApplication() throws Exception {
        // Initialize the database
        insertedApplication = applicationRepository.saveAndFlush(application);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the application
        restApplicationMockMvc
            .perform(delete(ENTITY_API_URL_ID, application.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return applicationRepository.count();
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

    protected Application getPersistedApplication(Application application) {
        return applicationRepository.findById(application.getId()).orElseThrow();
    }

    protected void assertPersistedApplicationToMatchAllProperties(Application expectedApplication) {
        assertApplicationAllPropertiesEquals(expectedApplication, getPersistedApplication(expectedApplication));
    }

    protected void assertPersistedApplicationToMatchUpdatableProperties(Application expectedApplication) {
        assertApplicationAllUpdatablePropertiesEquals(expectedApplication, getPersistedApplication(expectedApplication));
    }
}
