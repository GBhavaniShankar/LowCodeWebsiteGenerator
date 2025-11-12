package com.recruitment.backend.web.rest;

import static com.recruitment.backend.domain.AdvertisementAsserts.*;
import static com.recruitment.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.backend.IntegrationTest;
import com.recruitment.backend.domain.Advertisement;
import com.recruitment.backend.repository.AdvertisementRepository;
import com.recruitment.backend.service.dto.AdvertisementDTO;
import com.recruitment.backend.service.mapper.AdvertisementMapper;
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
 * Integration tests for the {@link AdvertisementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AdvertisementResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/advertisements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdvertisementMockMvc;

    private Advertisement advertisement;

    private Advertisement insertedAdvertisement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Advertisement createEntity() {
        return new Advertisement().title(DEFAULT_TITLE).content(DEFAULT_CONTENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Advertisement createUpdatedEntity() {
        return new Advertisement().title(UPDATED_TITLE).content(UPDATED_CONTENT);
    }

    @BeforeEach
    void initTest() {
        advertisement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAdvertisement != null) {
            advertisementRepository.delete(insertedAdvertisement);
            insertedAdvertisement = null;
        }
    }

    @Test
    @Transactional
    void createAdvertisement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Advertisement
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);
        var returnedAdvertisementDTO = om.readValue(
            restAdvertisementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(advertisementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AdvertisementDTO.class
        );

        // Validate the Advertisement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAdvertisement = advertisementMapper.toEntity(returnedAdvertisementDTO);
        assertAdvertisementUpdatableFieldsEquals(returnedAdvertisement, getPersistedAdvertisement(returnedAdvertisement));

        insertedAdvertisement = returnedAdvertisement;
    }

    @Test
    @Transactional
    void createAdvertisementWithExistingId() throws Exception {
        // Create the Advertisement with an existing ID
        advertisement.setId(1L);
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdvertisementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(advertisementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Advertisement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        advertisement.setTitle(null);

        // Create the Advertisement, which fails.
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);

        restAdvertisementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(advertisementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAdvertisements() throws Exception {
        // Initialize the database
        insertedAdvertisement = advertisementRepository.saveAndFlush(advertisement);

        // Get all the advertisementList
        restAdvertisementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(advertisement.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    void getAdvertisement() throws Exception {
        // Initialize the database
        insertedAdvertisement = advertisementRepository.saveAndFlush(advertisement);

        // Get the advertisement
        restAdvertisementMockMvc
            .perform(get(ENTITY_API_URL_ID, advertisement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(advertisement.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
    }

    @Test
    @Transactional
    void getNonExistingAdvertisement() throws Exception {
        // Get the advertisement
        restAdvertisementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAdvertisement() throws Exception {
        // Initialize the database
        insertedAdvertisement = advertisementRepository.saveAndFlush(advertisement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the advertisement
        Advertisement updatedAdvertisement = advertisementRepository.findById(advertisement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAdvertisement are not directly saved in db
        em.detach(updatedAdvertisement);
        updatedAdvertisement.title(UPDATED_TITLE).content(UPDATED_CONTENT);
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(updatedAdvertisement);

        restAdvertisementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, advertisementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(advertisementDTO))
            )
            .andExpect(status().isOk());

        // Validate the Advertisement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAdvertisementToMatchAllProperties(updatedAdvertisement);
    }

    @Test
    @Transactional
    void putNonExistingAdvertisement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        advertisement.setId(longCount.incrementAndGet());

        // Create the Advertisement
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdvertisementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, advertisementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(advertisementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Advertisement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAdvertisement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        advertisement.setId(longCount.incrementAndGet());

        // Create the Advertisement
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdvertisementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(advertisementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Advertisement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAdvertisement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        advertisement.setId(longCount.incrementAndGet());

        // Create the Advertisement
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdvertisementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(advertisementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Advertisement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAdvertisementWithPatch() throws Exception {
        // Initialize the database
        insertedAdvertisement = advertisementRepository.saveAndFlush(advertisement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the advertisement using partial update
        Advertisement partialUpdatedAdvertisement = new Advertisement();
        partialUpdatedAdvertisement.setId(advertisement.getId());

        restAdvertisementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdvertisement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAdvertisement))
            )
            .andExpect(status().isOk());

        // Validate the Advertisement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdvertisementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAdvertisement, advertisement),
            getPersistedAdvertisement(advertisement)
        );
    }

    @Test
    @Transactional
    void fullUpdateAdvertisementWithPatch() throws Exception {
        // Initialize the database
        insertedAdvertisement = advertisementRepository.saveAndFlush(advertisement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the advertisement using partial update
        Advertisement partialUpdatedAdvertisement = new Advertisement();
        partialUpdatedAdvertisement.setId(advertisement.getId());

        partialUpdatedAdvertisement.title(UPDATED_TITLE).content(UPDATED_CONTENT);

        restAdvertisementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdvertisement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAdvertisement))
            )
            .andExpect(status().isOk());

        // Validate the Advertisement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdvertisementUpdatableFieldsEquals(partialUpdatedAdvertisement, getPersistedAdvertisement(partialUpdatedAdvertisement));
    }

    @Test
    @Transactional
    void patchNonExistingAdvertisement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        advertisement.setId(longCount.incrementAndGet());

        // Create the Advertisement
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdvertisementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, advertisementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(advertisementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Advertisement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAdvertisement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        advertisement.setId(longCount.incrementAndGet());

        // Create the Advertisement
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdvertisementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(advertisementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Advertisement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAdvertisement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        advertisement.setId(longCount.incrementAndGet());

        // Create the Advertisement
        AdvertisementDTO advertisementDTO = advertisementMapper.toDto(advertisement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdvertisementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(advertisementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Advertisement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAdvertisement() throws Exception {
        // Initialize the database
        insertedAdvertisement = advertisementRepository.saveAndFlush(advertisement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the advertisement
        restAdvertisementMockMvc
            .perform(delete(ENTITY_API_URL_ID, advertisement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return advertisementRepository.count();
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

    protected Advertisement getPersistedAdvertisement(Advertisement advertisement) {
        return advertisementRepository.findById(advertisement.getId()).orElseThrow();
    }

    protected void assertPersistedAdvertisementToMatchAllProperties(Advertisement expectedAdvertisement) {
        assertAdvertisementAllPropertiesEquals(expectedAdvertisement, getPersistedAdvertisement(expectedAdvertisement));
    }

    protected void assertPersistedAdvertisementToMatchUpdatableProperties(Advertisement expectedAdvertisement) {
        assertAdvertisementAllUpdatablePropertiesEquals(expectedAdvertisement, getPersistedAdvertisement(expectedAdvertisement));
    }
}
