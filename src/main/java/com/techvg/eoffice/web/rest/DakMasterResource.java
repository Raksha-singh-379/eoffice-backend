package com.techvg.eoffice.web.rest;

import com.techvg.eoffice.repository.DakMasterRepository;
import com.techvg.eoffice.service.DakMasterQueryService;
import com.techvg.eoffice.service.DakMasterService;
import com.techvg.eoffice.service.criteria.DakMasterCriteria;
import com.techvg.eoffice.service.dto.DakMasterDTO;
import com.techvg.eoffice.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.techvg.eoffice.domain.DakMaster}.
 */
@RestController
@RequestMapping("/api")
public class DakMasterResource {

    private final Logger log = LoggerFactory.getLogger(DakMasterResource.class);

    private static final String ENTITY_NAME = "dakMaster";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DakMasterService dakMasterService;

    private final DakMasterRepository dakMasterRepository;

    private final DakMasterQueryService dakMasterQueryService;

    public DakMasterResource(
        DakMasterService dakMasterService,
        DakMasterRepository dakMasterRepository,
        DakMasterQueryService dakMasterQueryService
    ) {
        this.dakMasterService = dakMasterService;
        this.dakMasterRepository = dakMasterRepository;
        this.dakMasterQueryService = dakMasterQueryService;
    }

    /**
     * {@code POST  /dak-masters} : Create a new dakMaster.
     *
     * @param dakMasterDTO the dakMasterDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dakMasterDTO, or with status {@code 400 (Bad Request)} if the dakMaster has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dak-masters")
    public ResponseEntity<DakMasterDTO> createDakMaster(@RequestBody DakMasterDTO dakMasterDTO) throws URISyntaxException {
        log.debug("REST request to save DakMaster : {}", dakMasterDTO);
        if (dakMasterDTO.getId() != null) {
            throw new BadRequestAlertException("A new dakMaster cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DakMasterDTO result = dakMasterService.save(dakMasterDTO);
        return ResponseEntity
            .created(new URI("/api/dak-masters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /dak-masters/:id} : Updates an existing dakMaster.
     *
     * @param id the id of the dakMasterDTO to save.
     * @param dakMasterDTO the dakMasterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dakMasterDTO,
     * or with status {@code 400 (Bad Request)} if the dakMasterDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dakMasterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dak-masters/{id}")
    public ResponseEntity<DakMasterDTO> updateDakMaster(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DakMasterDTO dakMasterDTO
    ) throws URISyntaxException {
        log.debug("REST request to update DakMaster : {}, {}", id, dakMasterDTO);
        if (dakMasterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dakMasterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dakMasterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DakMasterDTO result = dakMasterService.update(dakMasterDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dakMasterDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /dak-masters/:id} : Partial updates given fields of an existing dakMaster, field will ignore if it is null
     *
     * @param id the id of the dakMasterDTO to save.
     * @param dakMasterDTO the dakMasterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dakMasterDTO,
     * or with status {@code 400 (Bad Request)} if the dakMasterDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dakMasterDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dakMasterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/dak-masters/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DakMasterDTO> partialUpdateDakMaster(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DakMasterDTO dakMasterDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update DakMaster partially : {}, {}", id, dakMasterDTO);
        if (dakMasterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dakMasterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dakMasterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DakMasterDTO> result = dakMasterService.partialUpdate(dakMasterDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dakMasterDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /dak-masters} : get all the dakMasters.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dakMasters in body.
     */
    @GetMapping("/dak-masters")
    public ResponseEntity<List<DakMasterDTO>> getAllDakMasters(
        DakMasterCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get DakMasters by criteria: {}", criteria);
        Page<DakMasterDTO> page = dakMasterQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dak-masters/count} : count all the dakMasters.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/dak-masters/count")
    public ResponseEntity<Long> countDakMasters(DakMasterCriteria criteria) {
        log.debug("REST request to count DakMasters by criteria: {}", criteria);
        return ResponseEntity.ok().body(dakMasterQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /dak-masters/:id} : get the "id" dakMaster.
     *
     * @param id the id of the dakMasterDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dakMasterDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dak-masters/{id}")
    public ResponseEntity<DakMasterDTO> getDakMaster(@PathVariable Long id) {
        log.debug("REST request to get DakMaster : {}", id);
        Optional<DakMasterDTO> dakMasterDTO = dakMasterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dakMasterDTO);
    }

    /**
     * {@code DELETE  /dak-masters/:id} : delete the "id" dakMaster.
     *
     * @param id the id of the dakMasterDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dak-masters/{id}")
    public ResponseEntity<Void> deleteDakMaster(@PathVariable Long id) {
        log.debug("REST request to delete DakMaster : {}", id);
        dakMasterService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
