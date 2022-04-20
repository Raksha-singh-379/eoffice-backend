package com.techvg.eoffice.service;

import com.techvg.eoffice.domain.HearingDetails;
import com.techvg.eoffice.repository.HearingDetailsRepository;
import com.techvg.eoffice.service.dto.HearingDetailsDTO;
import com.techvg.eoffice.service.mapper.HearingDetailsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link HearingDetails}.
 */
@Service
@Transactional
public class HearingDetailsService {

    private final Logger log = LoggerFactory.getLogger(HearingDetailsService.class);

    private final HearingDetailsRepository hearingDetailsRepository;

    private final HearingDetailsMapper hearingDetailsMapper;

    public HearingDetailsService(HearingDetailsRepository hearingDetailsRepository, HearingDetailsMapper hearingDetailsMapper) {
        this.hearingDetailsRepository = hearingDetailsRepository;
        this.hearingDetailsMapper = hearingDetailsMapper;
    }

    /**
     * Save a hearingDetails.
     *
     * @param hearingDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    public HearingDetailsDTO save(HearingDetailsDTO hearingDetailsDTO) {
        log.debug("Request to save HearingDetails : {}", hearingDetailsDTO);
        HearingDetails hearingDetails = hearingDetailsMapper.toEntity(hearingDetailsDTO);
        hearingDetails = hearingDetailsRepository.save(hearingDetails);
        return hearingDetailsMapper.toDto(hearingDetails);
    }

    /**
     * Update a hearingDetails.
     *
     * @param hearingDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    public HearingDetailsDTO update(HearingDetailsDTO hearingDetailsDTO) {
        log.debug("Request to save HearingDetails : {}", hearingDetailsDTO);
        HearingDetails hearingDetails = hearingDetailsMapper.toEntity(hearingDetailsDTO);
        hearingDetails = hearingDetailsRepository.save(hearingDetails);
        return hearingDetailsMapper.toDto(hearingDetails);
    }

    /**
     * Partially update a hearingDetails.
     *
     * @param hearingDetailsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HearingDetailsDTO> partialUpdate(HearingDetailsDTO hearingDetailsDTO) {
        log.debug("Request to partially update HearingDetails : {}", hearingDetailsDTO);

        return hearingDetailsRepository
            .findById(hearingDetailsDTO.getId())
            .map(existingHearingDetails -> {
                hearingDetailsMapper.partialUpdate(existingHearingDetails, hearingDetailsDTO);

                return existingHearingDetails;
            })
            .map(hearingDetailsRepository::save)
            .map(hearingDetailsMapper::toDto);
    }

    /**
     * Get all the hearingDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HearingDetailsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all HearingDetails");
        return hearingDetailsRepository.findAll(pageable).map(hearingDetailsMapper::toDto);
    }

    /**
     * Get all the hearingDetails with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<HearingDetailsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return hearingDetailsRepository.findAllWithEagerRelationships(pageable).map(hearingDetailsMapper::toDto);
    }

    /**
     * Get one hearingDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HearingDetailsDTO> findOne(Long id) {
        log.debug("Request to get HearingDetails : {}", id);
        return hearingDetailsRepository.findOneWithEagerRelationships(id).map(hearingDetailsMapper::toDto);
    }

    /**
     * Delete the hearingDetails by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete HearingDetails : {}", id);
        hearingDetailsRepository.deleteById(id);
    }
}
