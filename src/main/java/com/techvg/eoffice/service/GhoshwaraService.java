package com.techvg.eoffice.service;

import com.techvg.eoffice.domain.Ghoshwara;
import com.techvg.eoffice.repository.GhoshwaraRepository;
import com.techvg.eoffice.service.dto.GhoshwaraDTO;
import com.techvg.eoffice.service.mapper.GhoshwaraMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Ghoshwara}.
 */
@Service
@Transactional
public class GhoshwaraService {

    private final Logger log = LoggerFactory.getLogger(GhoshwaraService.class);

    private final GhoshwaraRepository ghoshwaraRepository;

    private final GhoshwaraMapper ghoshwaraMapper;

    public GhoshwaraService(GhoshwaraRepository ghoshwaraRepository, GhoshwaraMapper ghoshwaraMapper) {
        this.ghoshwaraRepository = ghoshwaraRepository;
        this.ghoshwaraMapper = ghoshwaraMapper;
    }

    /**
     * Save a ghoshwara.
     *
     * @param ghoshwaraDTO the entity to save.
     * @return the persisted entity.
     */
    public GhoshwaraDTO save(GhoshwaraDTO ghoshwaraDTO) {
        log.debug("Request to save Ghoshwara : {}", ghoshwaraDTO);
        Ghoshwara ghoshwara = ghoshwaraMapper.toEntity(ghoshwaraDTO);
        ghoshwara = ghoshwaraRepository.save(ghoshwara);
        return ghoshwaraMapper.toDto(ghoshwara);
    }

    /**
     * Update a ghoshwara.
     *
     * @param ghoshwaraDTO the entity to save.
     * @return the persisted entity.
     */
    public GhoshwaraDTO update(GhoshwaraDTO ghoshwaraDTO) {
        log.debug("Request to save Ghoshwara : {}", ghoshwaraDTO);
        Ghoshwara ghoshwara = ghoshwaraMapper.toEntity(ghoshwaraDTO);
        ghoshwara = ghoshwaraRepository.save(ghoshwara);
        return ghoshwaraMapper.toDto(ghoshwara);
    }

    /**
     * Partially update a ghoshwara.
     *
     * @param ghoshwaraDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GhoshwaraDTO> partialUpdate(GhoshwaraDTO ghoshwaraDTO) {
        log.debug("Request to partially update Ghoshwara : {}", ghoshwaraDTO);

        return ghoshwaraRepository
            .findById(ghoshwaraDTO.getId())
            .map(existingGhoshwara -> {
                ghoshwaraMapper.partialUpdate(existingGhoshwara, ghoshwaraDTO);

                return existingGhoshwara;
            })
            .map(ghoshwaraRepository::save)
            .map(ghoshwaraMapper::toDto);
    }

    /**
     * Get all the ghoshwaras.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<GhoshwaraDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Ghoshwaras");
        return ghoshwaraRepository.findAll(pageable).map(ghoshwaraMapper::toDto);
    }

    /**
     * Get all the ghoshwaras with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<GhoshwaraDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ghoshwaraRepository.findAllWithEagerRelationships(pageable).map(ghoshwaraMapper::toDto);
    }

    /**
     * Get one ghoshwara by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GhoshwaraDTO> findOne(Long id) {
        log.debug("Request to get Ghoshwara : {}", id);
        return ghoshwaraRepository.findOneWithEagerRelationships(id).map(ghoshwaraMapper::toDto);
    }

    /**
     * Delete the ghoshwara by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Ghoshwara : {}", id);
        ghoshwaraRepository.deleteById(id);
    }
}
