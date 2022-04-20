package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.DakIdGenerator;
import com.techvg.eoffice.domain.Organization;
import com.techvg.eoffice.repository.DakIdGeneratorRepository;
import com.techvg.eoffice.service.criteria.DakIdGeneratorCriteria;
import com.techvg.eoffice.service.dto.DakIdGeneratorDTO;
import com.techvg.eoffice.service.mapper.DakIdGeneratorMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DakIdGeneratorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DakIdGeneratorResourceIT {

    private static final Long DEFAULT_NEXT_VAL_INWARD = 1L;
    private static final Long UPDATED_NEXT_VAL_INWARD = 2L;
    private static final Long SMALLER_NEXT_VAL_INWARD = 1L - 1L;

    private static final Long DEFAULT_NEXT_VAL_OUTWARD = 1L;
    private static final Long UPDATED_NEXT_VAL_OUTWARD = 2L;
    private static final Long SMALLER_NEXT_VAL_OUTWARD = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/dak-id-generators";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DakIdGeneratorRepository dakIdGeneratorRepository;

    @Autowired
    private DakIdGeneratorMapper dakIdGeneratorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDakIdGeneratorMockMvc;

    private DakIdGenerator dakIdGenerator;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DakIdGenerator createEntity(EntityManager em) {
        DakIdGenerator dakIdGenerator = new DakIdGenerator()
            .nextValInward(DEFAULT_NEXT_VAL_INWARD)
            .nextValOutward(DEFAULT_NEXT_VAL_OUTWARD);
        return dakIdGenerator;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DakIdGenerator createUpdatedEntity(EntityManager em) {
        DakIdGenerator dakIdGenerator = new DakIdGenerator()
            .nextValInward(UPDATED_NEXT_VAL_INWARD)
            .nextValOutward(UPDATED_NEXT_VAL_OUTWARD);
        return dakIdGenerator;
    }

    @BeforeEach
    public void initTest() {
        dakIdGenerator = createEntity(em);
    }

    @Test
    @Transactional
    void createDakIdGenerator() throws Exception {
        int databaseSizeBeforeCreate = dakIdGeneratorRepository.findAll().size();
        // Create the DakIdGenerator
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(dakIdGenerator);
        restDakIdGeneratorMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isCreated());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeCreate + 1);
        DakIdGenerator testDakIdGenerator = dakIdGeneratorList.get(dakIdGeneratorList.size() - 1);
        assertThat(testDakIdGenerator.getNextValInward()).isEqualTo(DEFAULT_NEXT_VAL_INWARD);
        assertThat(testDakIdGenerator.getNextValOutward()).isEqualTo(DEFAULT_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void createDakIdGeneratorWithExistingId() throws Exception {
        // Create the DakIdGenerator with an existing ID
        dakIdGenerator.setId(1L);
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(dakIdGenerator);

        int databaseSizeBeforeCreate = dakIdGeneratorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDakIdGeneratorMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDakIdGenerators() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList
        restDakIdGeneratorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dakIdGenerator.getId().intValue())))
            .andExpect(jsonPath("$.[*].nextValInward").value(hasItem(DEFAULT_NEXT_VAL_INWARD.intValue())))
            .andExpect(jsonPath("$.[*].nextValOutward").value(hasItem(DEFAULT_NEXT_VAL_OUTWARD.intValue())));
    }

    @Test
    @Transactional
    void getDakIdGenerator() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get the dakIdGenerator
        restDakIdGeneratorMockMvc
            .perform(get(ENTITY_API_URL_ID, dakIdGenerator.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dakIdGenerator.getId().intValue()))
            .andExpect(jsonPath("$.nextValInward").value(DEFAULT_NEXT_VAL_INWARD.intValue()))
            .andExpect(jsonPath("$.nextValOutward").value(DEFAULT_NEXT_VAL_OUTWARD.intValue()));
    }

    @Test
    @Transactional
    void getDakIdGeneratorsByIdFiltering() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        Long id = dakIdGenerator.getId();

        defaultDakIdGeneratorShouldBeFound("id.equals=" + id);
        defaultDakIdGeneratorShouldNotBeFound("id.notEquals=" + id);

        defaultDakIdGeneratorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDakIdGeneratorShouldNotBeFound("id.greaterThan=" + id);

        defaultDakIdGeneratorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDakIdGeneratorShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValInwardIsEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValInward equals to DEFAULT_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldBeFound("nextValInward.equals=" + DEFAULT_NEXT_VAL_INWARD);

        // Get all the dakIdGeneratorList where nextValInward equals to UPDATED_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValInward.equals=" + UPDATED_NEXT_VAL_INWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValInwardIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValInward not equals to DEFAULT_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValInward.notEquals=" + DEFAULT_NEXT_VAL_INWARD);

        // Get all the dakIdGeneratorList where nextValInward not equals to UPDATED_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldBeFound("nextValInward.notEquals=" + UPDATED_NEXT_VAL_INWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValInwardIsInShouldWork() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValInward in DEFAULT_NEXT_VAL_INWARD or UPDATED_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldBeFound("nextValInward.in=" + DEFAULT_NEXT_VAL_INWARD + "," + UPDATED_NEXT_VAL_INWARD);

        // Get all the dakIdGeneratorList where nextValInward equals to UPDATED_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValInward.in=" + UPDATED_NEXT_VAL_INWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValInwardIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValInward is not null
        defaultDakIdGeneratorShouldBeFound("nextValInward.specified=true");

        // Get all the dakIdGeneratorList where nextValInward is null
        defaultDakIdGeneratorShouldNotBeFound("nextValInward.specified=false");
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValInwardIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValInward is greater than or equal to DEFAULT_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldBeFound("nextValInward.greaterThanOrEqual=" + DEFAULT_NEXT_VAL_INWARD);

        // Get all the dakIdGeneratorList where nextValInward is greater than or equal to UPDATED_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValInward.greaterThanOrEqual=" + UPDATED_NEXT_VAL_INWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValInwardIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValInward is less than or equal to DEFAULT_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldBeFound("nextValInward.lessThanOrEqual=" + DEFAULT_NEXT_VAL_INWARD);

        // Get all the dakIdGeneratorList where nextValInward is less than or equal to SMALLER_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValInward.lessThanOrEqual=" + SMALLER_NEXT_VAL_INWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValInwardIsLessThanSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValInward is less than DEFAULT_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValInward.lessThan=" + DEFAULT_NEXT_VAL_INWARD);

        // Get all the dakIdGeneratorList where nextValInward is less than UPDATED_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldBeFound("nextValInward.lessThan=" + UPDATED_NEXT_VAL_INWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValInwardIsGreaterThanSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValInward is greater than DEFAULT_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValInward.greaterThan=" + DEFAULT_NEXT_VAL_INWARD);

        // Get all the dakIdGeneratorList where nextValInward is greater than SMALLER_NEXT_VAL_INWARD
        defaultDakIdGeneratorShouldBeFound("nextValInward.greaterThan=" + SMALLER_NEXT_VAL_INWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValOutwardIsEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValOutward equals to DEFAULT_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldBeFound("nextValOutward.equals=" + DEFAULT_NEXT_VAL_OUTWARD);

        // Get all the dakIdGeneratorList where nextValOutward equals to UPDATED_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValOutward.equals=" + UPDATED_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValOutwardIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValOutward not equals to DEFAULT_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValOutward.notEquals=" + DEFAULT_NEXT_VAL_OUTWARD);

        // Get all the dakIdGeneratorList where nextValOutward not equals to UPDATED_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldBeFound("nextValOutward.notEquals=" + UPDATED_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValOutwardIsInShouldWork() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValOutward in DEFAULT_NEXT_VAL_OUTWARD or UPDATED_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldBeFound("nextValOutward.in=" + DEFAULT_NEXT_VAL_OUTWARD + "," + UPDATED_NEXT_VAL_OUTWARD);

        // Get all the dakIdGeneratorList where nextValOutward equals to UPDATED_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValOutward.in=" + UPDATED_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValOutwardIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValOutward is not null
        defaultDakIdGeneratorShouldBeFound("nextValOutward.specified=true");

        // Get all the dakIdGeneratorList where nextValOutward is null
        defaultDakIdGeneratorShouldNotBeFound("nextValOutward.specified=false");
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValOutwardIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValOutward is greater than or equal to DEFAULT_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldBeFound("nextValOutward.greaterThanOrEqual=" + DEFAULT_NEXT_VAL_OUTWARD);

        // Get all the dakIdGeneratorList where nextValOutward is greater than or equal to UPDATED_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValOutward.greaterThanOrEqual=" + UPDATED_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValOutwardIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValOutward is less than or equal to DEFAULT_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldBeFound("nextValOutward.lessThanOrEqual=" + DEFAULT_NEXT_VAL_OUTWARD);

        // Get all the dakIdGeneratorList where nextValOutward is less than or equal to SMALLER_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValOutward.lessThanOrEqual=" + SMALLER_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValOutwardIsLessThanSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValOutward is less than DEFAULT_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValOutward.lessThan=" + DEFAULT_NEXT_VAL_OUTWARD);

        // Get all the dakIdGeneratorList where nextValOutward is less than UPDATED_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldBeFound("nextValOutward.lessThan=" + UPDATED_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByNextValOutwardIsGreaterThanSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        // Get all the dakIdGeneratorList where nextValOutward is greater than DEFAULT_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldNotBeFound("nextValOutward.greaterThan=" + DEFAULT_NEXT_VAL_OUTWARD);

        // Get all the dakIdGeneratorList where nextValOutward is greater than SMALLER_NEXT_VAL_OUTWARD
        defaultDakIdGeneratorShouldBeFound("nextValOutward.greaterThan=" + SMALLER_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakIdGeneratorsByOrganizationIsEqualToSomething() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);
        Organization organization;
        if (TestUtil.findAll(em, Organization.class).isEmpty()) {
            organization = OrganizationResourceIT.createEntity(em);
            em.persist(organization);
            em.flush();
        } else {
            organization = TestUtil.findAll(em, Organization.class).get(0);
        }
        em.persist(organization);
        em.flush();
        dakIdGenerator.setOrganization(organization);
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);
        Long organizationId = organization.getId();

        // Get all the dakIdGeneratorList where organization equals to organizationId
        defaultDakIdGeneratorShouldBeFound("organizationId.equals=" + organizationId);

        // Get all the dakIdGeneratorList where organization equals to (organizationId + 1)
        defaultDakIdGeneratorShouldNotBeFound("organizationId.equals=" + (organizationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDakIdGeneratorShouldBeFound(String filter) throws Exception {
        restDakIdGeneratorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dakIdGenerator.getId().intValue())))
            .andExpect(jsonPath("$.[*].nextValInward").value(hasItem(DEFAULT_NEXT_VAL_INWARD.intValue())))
            .andExpect(jsonPath("$.[*].nextValOutward").value(hasItem(DEFAULT_NEXT_VAL_OUTWARD.intValue())));

        // Check, that the count call also returns 1
        restDakIdGeneratorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDakIdGeneratorShouldNotBeFound(String filter) throws Exception {
        restDakIdGeneratorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDakIdGeneratorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDakIdGenerator() throws Exception {
        // Get the dakIdGenerator
        restDakIdGeneratorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDakIdGenerator() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();

        // Update the dakIdGenerator
        DakIdGenerator updatedDakIdGenerator = dakIdGeneratorRepository.findById(dakIdGenerator.getId()).get();
        // Disconnect from session so that the updates on updatedDakIdGenerator are not directly saved in db
        em.detach(updatedDakIdGenerator);
        updatedDakIdGenerator.nextValInward(UPDATED_NEXT_VAL_INWARD).nextValOutward(UPDATED_NEXT_VAL_OUTWARD);
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(updatedDakIdGenerator);

        restDakIdGeneratorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dakIdGeneratorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isOk());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
        DakIdGenerator testDakIdGenerator = dakIdGeneratorList.get(dakIdGeneratorList.size() - 1);
        assertThat(testDakIdGenerator.getNextValInward()).isEqualTo(UPDATED_NEXT_VAL_INWARD);
        assertThat(testDakIdGenerator.getNextValOutward()).isEqualTo(UPDATED_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void putNonExistingDakIdGenerator() throws Exception {
        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();
        dakIdGenerator.setId(count.incrementAndGet());

        // Create the DakIdGenerator
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(dakIdGenerator);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDakIdGeneratorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dakIdGeneratorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDakIdGenerator() throws Exception {
        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();
        dakIdGenerator.setId(count.incrementAndGet());

        // Create the DakIdGenerator
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(dakIdGenerator);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakIdGeneratorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDakIdGenerator() throws Exception {
        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();
        dakIdGenerator.setId(count.incrementAndGet());

        // Create the DakIdGenerator
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(dakIdGenerator);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakIdGeneratorMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDakIdGeneratorWithPatch() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();

        // Update the dakIdGenerator using partial update
        DakIdGenerator partialUpdatedDakIdGenerator = new DakIdGenerator();
        partialUpdatedDakIdGenerator.setId(dakIdGenerator.getId());

        restDakIdGeneratorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDakIdGenerator.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDakIdGenerator))
            )
            .andExpect(status().isOk());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
        DakIdGenerator testDakIdGenerator = dakIdGeneratorList.get(dakIdGeneratorList.size() - 1);
        assertThat(testDakIdGenerator.getNextValInward()).isEqualTo(DEFAULT_NEXT_VAL_INWARD);
        assertThat(testDakIdGenerator.getNextValOutward()).isEqualTo(DEFAULT_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void fullUpdateDakIdGeneratorWithPatch() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();

        // Update the dakIdGenerator using partial update
        DakIdGenerator partialUpdatedDakIdGenerator = new DakIdGenerator();
        partialUpdatedDakIdGenerator.setId(dakIdGenerator.getId());

        partialUpdatedDakIdGenerator.nextValInward(UPDATED_NEXT_VAL_INWARD).nextValOutward(UPDATED_NEXT_VAL_OUTWARD);

        restDakIdGeneratorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDakIdGenerator.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDakIdGenerator))
            )
            .andExpect(status().isOk());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
        DakIdGenerator testDakIdGenerator = dakIdGeneratorList.get(dakIdGeneratorList.size() - 1);
        assertThat(testDakIdGenerator.getNextValInward()).isEqualTo(UPDATED_NEXT_VAL_INWARD);
        assertThat(testDakIdGenerator.getNextValOutward()).isEqualTo(UPDATED_NEXT_VAL_OUTWARD);
    }

    @Test
    @Transactional
    void patchNonExistingDakIdGenerator() throws Exception {
        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();
        dakIdGenerator.setId(count.incrementAndGet());

        // Create the DakIdGenerator
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(dakIdGenerator);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDakIdGeneratorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dakIdGeneratorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDakIdGenerator() throws Exception {
        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();
        dakIdGenerator.setId(count.incrementAndGet());

        // Create the DakIdGenerator
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(dakIdGenerator);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakIdGeneratorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDakIdGenerator() throws Exception {
        int databaseSizeBeforeUpdate = dakIdGeneratorRepository.findAll().size();
        dakIdGenerator.setId(count.incrementAndGet());

        // Create the DakIdGenerator
        DakIdGeneratorDTO dakIdGeneratorDTO = dakIdGeneratorMapper.toDto(dakIdGenerator);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakIdGeneratorMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakIdGeneratorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DakIdGenerator in the database
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDakIdGenerator() throws Exception {
        // Initialize the database
        dakIdGeneratorRepository.saveAndFlush(dakIdGenerator);

        int databaseSizeBeforeDelete = dakIdGeneratorRepository.findAll().size();

        // Delete the dakIdGenerator
        restDakIdGeneratorMockMvc
            .perform(delete(ENTITY_API_URL_ID, dakIdGenerator.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DakIdGenerator> dakIdGeneratorList = dakIdGeneratorRepository.findAll();
        assertThat(dakIdGeneratorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
