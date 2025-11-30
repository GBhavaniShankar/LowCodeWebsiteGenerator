package com.recruitment.backend.service;

import com.recruitment.backend.domain.Advertisement;
import com.recruitment.backend.repository.AdvertisementRepository;
import com.recruitment.backend.service.dto.AdvertisementDTO;
import com.recruitment.backend.service.mapper.AdvertisementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.recruitment.backend.domain.Advertisement}.
 */
@Service
@Transactional
public class AdvertisementService {

    private static final Logger LOG = LoggerFactory.getLogger(AdvertisementService.class);

    private final AdvertisementRepository advertisementRepository;

    private final AdvertisementMapper advertisementMapper;

    public AdvertisementService(AdvertisementRepository advertisementRepository, AdvertisementMapper advertisementMapper) {
        this.advertisementRepository = advertisementRepository;
        this.advertisementMapper = advertisementMapper;
    }

    /**
     * Save a advertisement.
     *
     * @param advertisementDTO the entity to save.
     * @return the persisted entity.
     */
    public AdvertisementDTO save(AdvertisementDTO advertisementDTO) {
        LOG.debug("Request to save Advertisement : {}", advertisementDTO);
        Advertisement advertisement = advertisementMapper.toEntity(advertisementDTO);
        advertisement = advertisementRepository.save(advertisement);
        return advertisementMapper.toDto(advertisement);
    }

    /**
     * Update a advertisement.
     *
     * @param advertisementDTO the entity to save.
     * @return the persisted entity.
     */
    public AdvertisementDTO update(AdvertisementDTO advertisementDTO) {
        LOG.debug("Request to update Advertisement : {}", advertisementDTO);
        Advertisement advertisement = advertisementMapper.toEntity(advertisementDTO);
        advertisement = advertisementRepository.save(advertisement);
        return advertisementMapper.toDto(advertisement);
    }

    /**
     * Partially update a advertisement.
     *
     * @param advertisementDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AdvertisementDTO> partialUpdate(AdvertisementDTO advertisementDTO) {
        LOG.debug("Request to partially update Advertisement : {}", advertisementDTO);

        return advertisementRepository
            .findById(advertisementDTO.getId())
            .map(existingAdvertisement -> {
                advertisementMapper.partialUpdate(existingAdvertisement, advertisementDTO);

                return existingAdvertisement;
            })
            .map(advertisementRepository::save)
            .map(advertisementMapper::toDto);
    }

    /**
     * Get all the advertisements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AdvertisementDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Advertisements");
        return advertisementRepository.findAll(pageable).map(advertisementMapper::toDto);
    }

    /**
     * Get one advertisement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AdvertisementDTO> findOne(Long id) {
        LOG.debug("Request to get Advertisement : {}", id);
        return advertisementRepository.findById(id).map(advertisementMapper::toDto);
    }

    /**
     * Delete the advertisement by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Advertisement : {}", id);
        advertisementRepository.deleteById(id);
    }
}
