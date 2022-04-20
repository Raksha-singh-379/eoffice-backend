package com.techvg.eoffice.service;

import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.repository.DakMasterRepository;
import com.techvg.eoffice.service.dto.DakMasterDTO;
import com.techvg.eoffice.service.mapper.DakMasterMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link DakMaster}.
 */
@Service
@Transactional
public class DakMasterService {

    private final Logger log = LoggerFactory.getLogger(DakMasterService.class);

    private final DakMasterRepository dakMasterRepository;

    private final DakMasterMapper dakMasterMapper;

    public DakMasterService(DakMasterRepository dakMasterRepository, DakMasterMapper dakMasterMapper) {
        this.dakMasterRepository = dakMasterRepository;
        this.dakMasterMapper = dakMasterMapper;
    }

    /**
     * Save a dakMaster.
     *
     * @param dakMasterDTO the entity to save.
     * @return the persisted entity.
     */
    public DakMasterDTO save(DakMasterDTO dakMasterDTO) {
        log.debug("Request to save DakMaster : {}", dakMasterDTO);
        DakMaster dakMaster = dakMasterMapper.toEntity(dakMasterDTO);
        dakMaster = dakMasterRepository.save(dakMaster);
        return dakMasterMapper.toDto(dakMaster);
    }

    /**
     * Update a dakMaster.
     *
     * @param dakMasterDTO the entity to save.
     * @return the persisted entity.
     */
    public DakMasterDTO update(DakMasterDTO dakMasterDTO) {
        log.debug("Request to save DakMaster : {}", dakMasterDTO);
        DakMaster dakMaster = dakMasterMapper.toEntity(dakMasterDTO);
        dakMaster = dakMasterRepository.save(dakMaster);
        return dakMasterMapper.toDto(dakMaster);
    }

    /**
     * Partially update a dakMaster.
     *
     * @param dakMasterDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DakMasterDTO> partialUpdate(DakMasterDTO dakMasterDTO) {
        log.debug("Request to partially update DakMaster : {}", dakMasterDTO);

        return dakMasterRepository
            .findById(dakMasterDTO.getId())
            .map(existingDakMaster -> {
                dakMasterMapper.partialUpdate(existingDakMaster, dakMasterDTO);

                return existingDakMaster;
            })
            .map(dakMasterRepository::save)
            .map(dakMasterMapper::toDto);
    }

    /**
     * Get all the dakMasters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DakMasterDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DakMasters");
        return dakMasterRepository.findAll(pageable).map(dakMasterMapper::toDto);
    }

    /**
     * Get all the dakMasters with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<DakMasterDTO> findAllWithEagerRelationships(Pageable pageable) {
        return dakMasterRepository.findAllWithEagerRelationships(pageable).map(dakMasterMapper::toDto);
    }

    /**
     * Get one dakMaster by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DakMasterDTO> findOne(Long id) {
        log.debug("Request to get DakMaster : {}", id);
        return dakMasterRepository.findOneWithEagerRelationships(id).map(dakMasterMapper::toDto);
    }

    /**
     * Delete the dakMaster by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete DakMaster : {}", id);
        dakMasterRepository.deleteById(id);
    }
}
