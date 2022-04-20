package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.CommentMaster;
import com.techvg.eoffice.domain.DakJourney;
import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.domain.enumeration.DakStatus;
import com.techvg.eoffice.repository.DakJourneyRepository;
import com.techvg.eoffice.service.DakJourneyService;
import com.techvg.eoffice.service.criteria.DakJourneyCriteria;
import com.techvg.eoffice.service.dto.DakJourneyDTO;
import com.techvg.eoffice.service.mapper.DakJourneyMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DakJourneyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DakJourneyResourceIT {

    private static final Instant DEFAULT_ASSIGNED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final DakStatus DEFAULT_DAK_STATUS = DakStatus.CREATED;
    private static final DakStatus UPDATED_DAK_STATUS = DakStatus.UPDATED;

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String DEFAULT_DAK_ASSIGNED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DAK_ASSIGNED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_DAK_ASSIGNED_TO = "AAAAAAAAAA";
    private static final String UPDATED_DAK_ASSIGNED_TO = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/dak-journeys";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DakJourneyRepository dakJourneyRepository;

    @Mock
    private DakJourneyRepository dakJourneyRepositoryMock;

    @Autowired
    private DakJourneyMapper dakJourneyMapper;

    @Mock
    private DakJourneyService dakJourneyServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDakJourneyMockMvc;

    private DakJourney dakJourney;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DakJourney createEntity(EntityManager em) {
        DakJourney dakJourney = new DakJourney()
            .assignedOn(DEFAULT_ASSIGNED_ON)
            .updatedOn(DEFAULT_UPDATED_ON)
            .dakStatus(DEFAULT_DAK_STATUS)
            .status(DEFAULT_STATUS)
            .dakAssignedBy(DEFAULT_DAK_ASSIGNED_BY)
            .dakAssignedTo(DEFAULT_DAK_ASSIGNED_TO)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        return dakJourney;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DakJourney createUpdatedEntity(EntityManager em) {
        DakJourney dakJourney = new DakJourney()
            .assignedOn(UPDATED_ASSIGNED_ON)
            .updatedOn(UPDATED_UPDATED_ON)
            .dakStatus(UPDATED_DAK_STATUS)
            .status(UPDATED_STATUS)
            .dakAssignedBy(UPDATED_DAK_ASSIGNED_BY)
            .dakAssignedTo(UPDATED_DAK_ASSIGNED_TO)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        return dakJourney;
    }

    @BeforeEach
    public void initTest() {
        dakJourney = createEntity(em);
    }

    @Test
    @Transactional
    void createDakJourney() throws Exception {
        int databaseSizeBeforeCreate = dakJourneyRepository.findAll().size();
        // Create the DakJourney
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(dakJourney);
        restDakJourneyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO)))
            .andExpect(status().isCreated());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeCreate + 1);
        DakJourney testDakJourney = dakJourneyList.get(dakJourneyList.size() - 1);
        assertThat(testDakJourney.getAssignedOn()).isEqualTo(DEFAULT_ASSIGNED_ON);
        assertThat(testDakJourney.getUpdatedOn()).isEqualTo(DEFAULT_UPDATED_ON);
        assertThat(testDakJourney.getDakStatus()).isEqualTo(DEFAULT_DAK_STATUS);
        assertThat(testDakJourney.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDakJourney.getDakAssignedBy()).isEqualTo(DEFAULT_DAK_ASSIGNED_BY);
        assertThat(testDakJourney.getDakAssignedTo()).isEqualTo(DEFAULT_DAK_ASSIGNED_TO);
        assertThat(testDakJourney.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testDakJourney.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createDakJourneyWithExistingId() throws Exception {
        // Create the DakJourney with an existing ID
        dakJourney.setId(1L);
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(dakJourney);

        int databaseSizeBeforeCreate = dakJourneyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDakJourneyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDakJourneys() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList
        restDakJourneyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dakJourney.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedOn").value(hasItem(DEFAULT_ASSIGNED_ON.toString())))
            .andExpect(jsonPath("$.[*].updatedOn").value(hasItem(DEFAULT_UPDATED_ON.toString())))
            .andExpect(jsonPath("$.[*].dakStatus").value(hasItem(DEFAULT_DAK_STATUS.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].dakAssignedBy").value(hasItem(DEFAULT_DAK_ASSIGNED_BY)))
            .andExpect(jsonPath("$.[*].dakAssignedTo").value(hasItem(DEFAULT_DAK_ASSIGNED_TO)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDakJourneysWithEagerRelationshipsIsEnabled() throws Exception {
        when(dakJourneyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDakJourneyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(dakJourneyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDakJourneysWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(dakJourneyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDakJourneyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(dakJourneyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getDakJourney() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get the dakJourney
        restDakJourneyMockMvc
            .perform(get(ENTITY_API_URL_ID, dakJourney.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dakJourney.getId().intValue()))
            .andExpect(jsonPath("$.assignedOn").value(DEFAULT_ASSIGNED_ON.toString()))
            .andExpect(jsonPath("$.updatedOn").value(DEFAULT_UPDATED_ON.toString()))
            .andExpect(jsonPath("$.dakStatus").value(DEFAULT_DAK_STATUS.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.dakAssignedBy").value(DEFAULT_DAK_ASSIGNED_BY))
            .andExpect(jsonPath("$.dakAssignedTo").value(DEFAULT_DAK_ASSIGNED_TO))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getDakJourneysByIdFiltering() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        Long id = dakJourney.getId();

        defaultDakJourneyShouldBeFound("id.equals=" + id);
        defaultDakJourneyShouldNotBeFound("id.notEquals=" + id);

        defaultDakJourneyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDakJourneyShouldNotBeFound("id.greaterThan=" + id);

        defaultDakJourneyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDakJourneyShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDakJourneysByAssignedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where assignedOn equals to DEFAULT_ASSIGNED_ON
        defaultDakJourneyShouldBeFound("assignedOn.equals=" + DEFAULT_ASSIGNED_ON);

        // Get all the dakJourneyList where assignedOn equals to UPDATED_ASSIGNED_ON
        defaultDakJourneyShouldNotBeFound("assignedOn.equals=" + UPDATED_ASSIGNED_ON);
    }

    @Test
    @Transactional
    void getAllDakJourneysByAssignedOnIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where assignedOn not equals to DEFAULT_ASSIGNED_ON
        defaultDakJourneyShouldNotBeFound("assignedOn.notEquals=" + DEFAULT_ASSIGNED_ON);

        // Get all the dakJourneyList where assignedOn not equals to UPDATED_ASSIGNED_ON
        defaultDakJourneyShouldBeFound("assignedOn.notEquals=" + UPDATED_ASSIGNED_ON);
    }

    @Test
    @Transactional
    void getAllDakJourneysByAssignedOnIsInShouldWork() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where assignedOn in DEFAULT_ASSIGNED_ON or UPDATED_ASSIGNED_ON
        defaultDakJourneyShouldBeFound("assignedOn.in=" + DEFAULT_ASSIGNED_ON + "," + UPDATED_ASSIGNED_ON);

        // Get all the dakJourneyList where assignedOn equals to UPDATED_ASSIGNED_ON
        defaultDakJourneyShouldNotBeFound("assignedOn.in=" + UPDATED_ASSIGNED_ON);
    }

    @Test
    @Transactional
    void getAllDakJourneysByAssignedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where assignedOn is not null
        defaultDakJourneyShouldBeFound("assignedOn.specified=true");

        // Get all the dakJourneyList where assignedOn is null
        defaultDakJourneyShouldNotBeFound("assignedOn.specified=false");
    }

    @Test
    @Transactional
    void getAllDakJourneysByUpdatedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where updatedOn equals to DEFAULT_UPDATED_ON
        defaultDakJourneyShouldBeFound("updatedOn.equals=" + DEFAULT_UPDATED_ON);

        // Get all the dakJourneyList where updatedOn equals to UPDATED_UPDATED_ON
        defaultDakJourneyShouldNotBeFound("updatedOn.equals=" + UPDATED_UPDATED_ON);
    }

    @Test
    @Transactional
    void getAllDakJourneysByUpdatedOnIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where updatedOn not equals to DEFAULT_UPDATED_ON
        defaultDakJourneyShouldNotBeFound("updatedOn.notEquals=" + DEFAULT_UPDATED_ON);

        // Get all the dakJourneyList where updatedOn not equals to UPDATED_UPDATED_ON
        defaultDakJourneyShouldBeFound("updatedOn.notEquals=" + UPDATED_UPDATED_ON);
    }

    @Test
    @Transactional
    void getAllDakJourneysByUpdatedOnIsInShouldWork() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where updatedOn in DEFAULT_UPDATED_ON or UPDATED_UPDATED_ON
        defaultDakJourneyShouldBeFound("updatedOn.in=" + DEFAULT_UPDATED_ON + "," + UPDATED_UPDATED_ON);

        // Get all the dakJourneyList where updatedOn equals to UPDATED_UPDATED_ON
        defaultDakJourneyShouldNotBeFound("updatedOn.in=" + UPDATED_UPDATED_ON);
    }

    @Test
    @Transactional
    void getAllDakJourneysByUpdatedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where updatedOn is not null
        defaultDakJourneyShouldBeFound("updatedOn.specified=true");

        // Get all the dakJourneyList where updatedOn is null
        defaultDakJourneyShouldNotBeFound("updatedOn.specified=false");
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakStatus equals to DEFAULT_DAK_STATUS
        defaultDakJourneyShouldBeFound("dakStatus.equals=" + DEFAULT_DAK_STATUS);

        // Get all the dakJourneyList where dakStatus equals to UPDATED_DAK_STATUS
        defaultDakJourneyShouldNotBeFound("dakStatus.equals=" + UPDATED_DAK_STATUS);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakStatus not equals to DEFAULT_DAK_STATUS
        defaultDakJourneyShouldNotBeFound("dakStatus.notEquals=" + DEFAULT_DAK_STATUS);

        // Get all the dakJourneyList where dakStatus not equals to UPDATED_DAK_STATUS
        defaultDakJourneyShouldBeFound("dakStatus.notEquals=" + UPDATED_DAK_STATUS);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakStatusIsInShouldWork() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakStatus in DEFAULT_DAK_STATUS or UPDATED_DAK_STATUS
        defaultDakJourneyShouldBeFound("dakStatus.in=" + DEFAULT_DAK_STATUS + "," + UPDATED_DAK_STATUS);

        // Get all the dakJourneyList where dakStatus equals to UPDATED_DAK_STATUS
        defaultDakJourneyShouldNotBeFound("dakStatus.in=" + UPDATED_DAK_STATUS);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakStatus is not null
        defaultDakJourneyShouldBeFound("dakStatus.specified=true");

        // Get all the dakJourneyList where dakStatus is null
        defaultDakJourneyShouldNotBeFound("dakStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllDakJourneysByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where status equals to DEFAULT_STATUS
        defaultDakJourneyShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the dakJourneyList where status equals to UPDATED_STATUS
        defaultDakJourneyShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDakJourneysByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where status not equals to DEFAULT_STATUS
        defaultDakJourneyShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the dakJourneyList where status not equals to UPDATED_STATUS
        defaultDakJourneyShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDakJourneysByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultDakJourneyShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the dakJourneyList where status equals to UPDATED_STATUS
        defaultDakJourneyShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDakJourneysByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where status is not null
        defaultDakJourneyShouldBeFound("status.specified=true");

        // Get all the dakJourneyList where status is null
        defaultDakJourneyShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedByIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedBy equals to DEFAULT_DAK_ASSIGNED_BY
        defaultDakJourneyShouldBeFound("dakAssignedBy.equals=" + DEFAULT_DAK_ASSIGNED_BY);

        // Get all the dakJourneyList where dakAssignedBy equals to UPDATED_DAK_ASSIGNED_BY
        defaultDakJourneyShouldNotBeFound("dakAssignedBy.equals=" + UPDATED_DAK_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedBy not equals to DEFAULT_DAK_ASSIGNED_BY
        defaultDakJourneyShouldNotBeFound("dakAssignedBy.notEquals=" + DEFAULT_DAK_ASSIGNED_BY);

        // Get all the dakJourneyList where dakAssignedBy not equals to UPDATED_DAK_ASSIGNED_BY
        defaultDakJourneyShouldBeFound("dakAssignedBy.notEquals=" + UPDATED_DAK_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedByIsInShouldWork() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedBy in DEFAULT_DAK_ASSIGNED_BY or UPDATED_DAK_ASSIGNED_BY
        defaultDakJourneyShouldBeFound("dakAssignedBy.in=" + DEFAULT_DAK_ASSIGNED_BY + "," + UPDATED_DAK_ASSIGNED_BY);

        // Get all the dakJourneyList where dakAssignedBy equals to UPDATED_DAK_ASSIGNED_BY
        defaultDakJourneyShouldNotBeFound("dakAssignedBy.in=" + UPDATED_DAK_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedBy is not null
        defaultDakJourneyShouldBeFound("dakAssignedBy.specified=true");

        // Get all the dakJourneyList where dakAssignedBy is null
        defaultDakJourneyShouldNotBeFound("dakAssignedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedByContainsSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedBy contains DEFAULT_DAK_ASSIGNED_BY
        defaultDakJourneyShouldBeFound("dakAssignedBy.contains=" + DEFAULT_DAK_ASSIGNED_BY);

        // Get all the dakJourneyList where dakAssignedBy contains UPDATED_DAK_ASSIGNED_BY
        defaultDakJourneyShouldNotBeFound("dakAssignedBy.contains=" + UPDATED_DAK_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedByNotContainsSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedBy does not contain DEFAULT_DAK_ASSIGNED_BY
        defaultDakJourneyShouldNotBeFound("dakAssignedBy.doesNotContain=" + DEFAULT_DAK_ASSIGNED_BY);

        // Get all the dakJourneyList where dakAssignedBy does not contain UPDATED_DAK_ASSIGNED_BY
        defaultDakJourneyShouldBeFound("dakAssignedBy.doesNotContain=" + UPDATED_DAK_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedToIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedTo equals to DEFAULT_DAK_ASSIGNED_TO
        defaultDakJourneyShouldBeFound("dakAssignedTo.equals=" + DEFAULT_DAK_ASSIGNED_TO);

        // Get all the dakJourneyList where dakAssignedTo equals to UPDATED_DAK_ASSIGNED_TO
        defaultDakJourneyShouldNotBeFound("dakAssignedTo.equals=" + UPDATED_DAK_ASSIGNED_TO);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedToIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedTo not equals to DEFAULT_DAK_ASSIGNED_TO
        defaultDakJourneyShouldNotBeFound("dakAssignedTo.notEquals=" + DEFAULT_DAK_ASSIGNED_TO);

        // Get all the dakJourneyList where dakAssignedTo not equals to UPDATED_DAK_ASSIGNED_TO
        defaultDakJourneyShouldBeFound("dakAssignedTo.notEquals=" + UPDATED_DAK_ASSIGNED_TO);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedToIsInShouldWork() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedTo in DEFAULT_DAK_ASSIGNED_TO or UPDATED_DAK_ASSIGNED_TO
        defaultDakJourneyShouldBeFound("dakAssignedTo.in=" + DEFAULT_DAK_ASSIGNED_TO + "," + UPDATED_DAK_ASSIGNED_TO);

        // Get all the dakJourneyList where dakAssignedTo equals to UPDATED_DAK_ASSIGNED_TO
        defaultDakJourneyShouldNotBeFound("dakAssignedTo.in=" + UPDATED_DAK_ASSIGNED_TO);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedToIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedTo is not null
        defaultDakJourneyShouldBeFound("dakAssignedTo.specified=true");

        // Get all the dakJourneyList where dakAssignedTo is null
        defaultDakJourneyShouldNotBeFound("dakAssignedTo.specified=false");
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedToContainsSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedTo contains DEFAULT_DAK_ASSIGNED_TO
        defaultDakJourneyShouldBeFound("dakAssignedTo.contains=" + DEFAULT_DAK_ASSIGNED_TO);

        // Get all the dakJourneyList where dakAssignedTo contains UPDATED_DAK_ASSIGNED_TO
        defaultDakJourneyShouldNotBeFound("dakAssignedTo.contains=" + UPDATED_DAK_ASSIGNED_TO);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakAssignedToNotContainsSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where dakAssignedTo does not contain DEFAULT_DAK_ASSIGNED_TO
        defaultDakJourneyShouldNotBeFound("dakAssignedTo.doesNotContain=" + DEFAULT_DAK_ASSIGNED_TO);

        // Get all the dakJourneyList where dakAssignedTo does not contain UPDATED_DAK_ASSIGNED_TO
        defaultDakJourneyShouldBeFound("dakAssignedTo.doesNotContain=" + UPDATED_DAK_ASSIGNED_TO);
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultDakJourneyShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the dakJourneyList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultDakJourneyShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultDakJourneyShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the dakJourneyList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultDakJourneyShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultDakJourneyShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the dakJourneyList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultDakJourneyShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModified is not null
        defaultDakJourneyShouldBeFound("lastModified.specified=true");

        // Get all the dakJourneyList where lastModified is null
        defaultDakJourneyShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultDakJourneyShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakJourneyList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDakJourneyShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultDakJourneyShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakJourneyList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultDakJourneyShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultDakJourneyShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the dakJourneyList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDakJourneyShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModifiedBy is not null
        defaultDakJourneyShouldBeFound("lastModifiedBy.specified=true");

        // Get all the dakJourneyList where lastModifiedBy is null
        defaultDakJourneyShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultDakJourneyShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakJourneyList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultDakJourneyShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        // Get all the dakJourneyList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultDakJourneyShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakJourneyList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultDakJourneyShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakJourneysByDakMasterIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);
        DakMaster dakMaster;
        if (TestUtil.findAll(em, DakMaster.class).isEmpty()) {
            dakMaster = DakMasterResourceIT.createEntity(em);
            em.persist(dakMaster);
            em.flush();
        } else {
            dakMaster = TestUtil.findAll(em, DakMaster.class).get(0);
        }
        em.persist(dakMaster);
        em.flush();
        dakJourney.setDakMaster(dakMaster);
        dakJourneyRepository.saveAndFlush(dakJourney);
        Long dakMasterId = dakMaster.getId();

        // Get all the dakJourneyList where dakMaster equals to dakMasterId
        defaultDakJourneyShouldBeFound("dakMasterId.equals=" + dakMasterId);

        // Get all the dakJourneyList where dakMaster equals to (dakMasterId + 1)
        defaultDakJourneyShouldNotBeFound("dakMasterId.equals=" + (dakMasterId + 1));
    }

    @Test
    @Transactional
    void getAllDakJourneysBySecurityUserIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);
        SecurityUser securityUser;
        if (TestUtil.findAll(em, SecurityUser.class).isEmpty()) {
            securityUser = SecurityUserResourceIT.createEntity(em);
            em.persist(securityUser);
            em.flush();
        } else {
            securityUser = TestUtil.findAll(em, SecurityUser.class).get(0);
        }
        em.persist(securityUser);
        em.flush();
        dakJourney.setSecurityUser(securityUser);
        dakJourneyRepository.saveAndFlush(dakJourney);
        Long securityUserId = securityUser.getId();

        // Get all the dakJourneyList where securityUser equals to securityUserId
        defaultDakJourneyShouldBeFound("securityUserId.equals=" + securityUserId);

        // Get all the dakJourneyList where securityUser equals to (securityUserId + 1)
        defaultDakJourneyShouldNotBeFound("securityUserId.equals=" + (securityUserId + 1));
    }

    @Test
    @Transactional
    void getAllDakJourneysByCommentMasterIsEqualToSomething() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);
        CommentMaster commentMaster;
        if (TestUtil.findAll(em, CommentMaster.class).isEmpty()) {
            commentMaster = CommentMasterResourceIT.createEntity(em);
            em.persist(commentMaster);
            em.flush();
        } else {
            commentMaster = TestUtil.findAll(em, CommentMaster.class).get(0);
        }
        em.persist(commentMaster);
        em.flush();
        dakJourney.setCommentMaster(commentMaster);
        dakJourneyRepository.saveAndFlush(dakJourney);
        Long commentMasterId = commentMaster.getId();

        // Get all the dakJourneyList where commentMaster equals to commentMasterId
        defaultDakJourneyShouldBeFound("commentMasterId.equals=" + commentMasterId);

        // Get all the dakJourneyList where commentMaster equals to (commentMasterId + 1)
        defaultDakJourneyShouldNotBeFound("commentMasterId.equals=" + (commentMasterId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDakJourneyShouldBeFound(String filter) throws Exception {
        restDakJourneyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dakJourney.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedOn").value(hasItem(DEFAULT_ASSIGNED_ON.toString())))
            .andExpect(jsonPath("$.[*].updatedOn").value(hasItem(DEFAULT_UPDATED_ON.toString())))
            .andExpect(jsonPath("$.[*].dakStatus").value(hasItem(DEFAULT_DAK_STATUS.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].dakAssignedBy").value(hasItem(DEFAULT_DAK_ASSIGNED_BY)))
            .andExpect(jsonPath("$.[*].dakAssignedTo").value(hasItem(DEFAULT_DAK_ASSIGNED_TO)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restDakJourneyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDakJourneyShouldNotBeFound(String filter) throws Exception {
        restDakJourneyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDakJourneyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDakJourney() throws Exception {
        // Get the dakJourney
        restDakJourneyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDakJourney() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();

        // Update the dakJourney
        DakJourney updatedDakJourney = dakJourneyRepository.findById(dakJourney.getId()).get();
        // Disconnect from session so that the updates on updatedDakJourney are not directly saved in db
        em.detach(updatedDakJourney);
        updatedDakJourney
            .assignedOn(UPDATED_ASSIGNED_ON)
            .updatedOn(UPDATED_UPDATED_ON)
            .dakStatus(UPDATED_DAK_STATUS)
            .status(UPDATED_STATUS)
            .dakAssignedBy(UPDATED_DAK_ASSIGNED_BY)
            .dakAssignedTo(UPDATED_DAK_ASSIGNED_TO)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(updatedDakJourney);

        restDakJourneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dakJourneyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO))
            )
            .andExpect(status().isOk());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
        DakJourney testDakJourney = dakJourneyList.get(dakJourneyList.size() - 1);
        assertThat(testDakJourney.getAssignedOn()).isEqualTo(UPDATED_ASSIGNED_ON);
        assertThat(testDakJourney.getUpdatedOn()).isEqualTo(UPDATED_UPDATED_ON);
        assertThat(testDakJourney.getDakStatus()).isEqualTo(UPDATED_DAK_STATUS);
        assertThat(testDakJourney.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDakJourney.getDakAssignedBy()).isEqualTo(UPDATED_DAK_ASSIGNED_BY);
        assertThat(testDakJourney.getDakAssignedTo()).isEqualTo(UPDATED_DAK_ASSIGNED_TO);
        assertThat(testDakJourney.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testDakJourney.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingDakJourney() throws Exception {
        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();
        dakJourney.setId(count.incrementAndGet());

        // Create the DakJourney
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(dakJourney);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDakJourneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dakJourneyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDakJourney() throws Exception {
        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();
        dakJourney.setId(count.incrementAndGet());

        // Create the DakJourney
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(dakJourney);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakJourneyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDakJourney() throws Exception {
        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();
        dakJourney.setId(count.incrementAndGet());

        // Create the DakJourney
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(dakJourney);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakJourneyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDakJourneyWithPatch() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();

        // Update the dakJourney using partial update
        DakJourney partialUpdatedDakJourney = new DakJourney();
        partialUpdatedDakJourney.setId(dakJourney.getId());

        partialUpdatedDakJourney.updatedOn(UPDATED_UPDATED_ON).dakAssignedBy(UPDATED_DAK_ASSIGNED_BY);

        restDakJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDakJourney.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDakJourney))
            )
            .andExpect(status().isOk());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
        DakJourney testDakJourney = dakJourneyList.get(dakJourneyList.size() - 1);
        assertThat(testDakJourney.getAssignedOn()).isEqualTo(DEFAULT_ASSIGNED_ON);
        assertThat(testDakJourney.getUpdatedOn()).isEqualTo(UPDATED_UPDATED_ON);
        assertThat(testDakJourney.getDakStatus()).isEqualTo(DEFAULT_DAK_STATUS);
        assertThat(testDakJourney.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDakJourney.getDakAssignedBy()).isEqualTo(UPDATED_DAK_ASSIGNED_BY);
        assertThat(testDakJourney.getDakAssignedTo()).isEqualTo(DEFAULT_DAK_ASSIGNED_TO);
        assertThat(testDakJourney.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testDakJourney.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateDakJourneyWithPatch() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();

        // Update the dakJourney using partial update
        DakJourney partialUpdatedDakJourney = new DakJourney();
        partialUpdatedDakJourney.setId(dakJourney.getId());

        partialUpdatedDakJourney
            .assignedOn(UPDATED_ASSIGNED_ON)
            .updatedOn(UPDATED_UPDATED_ON)
            .dakStatus(UPDATED_DAK_STATUS)
            .status(UPDATED_STATUS)
            .dakAssignedBy(UPDATED_DAK_ASSIGNED_BY)
            .dakAssignedTo(UPDATED_DAK_ASSIGNED_TO)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restDakJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDakJourney.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDakJourney))
            )
            .andExpect(status().isOk());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
        DakJourney testDakJourney = dakJourneyList.get(dakJourneyList.size() - 1);
        assertThat(testDakJourney.getAssignedOn()).isEqualTo(UPDATED_ASSIGNED_ON);
        assertThat(testDakJourney.getUpdatedOn()).isEqualTo(UPDATED_UPDATED_ON);
        assertThat(testDakJourney.getDakStatus()).isEqualTo(UPDATED_DAK_STATUS);
        assertThat(testDakJourney.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDakJourney.getDakAssignedBy()).isEqualTo(UPDATED_DAK_ASSIGNED_BY);
        assertThat(testDakJourney.getDakAssignedTo()).isEqualTo(UPDATED_DAK_ASSIGNED_TO);
        assertThat(testDakJourney.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testDakJourney.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingDakJourney() throws Exception {
        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();
        dakJourney.setId(count.incrementAndGet());

        // Create the DakJourney
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(dakJourney);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDakJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dakJourneyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDakJourney() throws Exception {
        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();
        dakJourney.setId(count.incrementAndGet());

        // Create the DakJourney
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(dakJourney);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDakJourney() throws Exception {
        int databaseSizeBeforeUpdate = dakJourneyRepository.findAll().size();
        dakJourney.setId(count.incrementAndGet());

        // Create the DakJourney
        DakJourneyDTO dakJourneyDTO = dakJourneyMapper.toDto(dakJourney);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakJourneyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dakJourneyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DakJourney in the database
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDakJourney() throws Exception {
        // Initialize the database
        dakJourneyRepository.saveAndFlush(dakJourney);

        int databaseSizeBeforeDelete = dakJourneyRepository.findAll().size();

        // Delete the dakJourney
        restDakJourneyMockMvc
            .perform(delete(ENTITY_API_URL_ID, dakJourney.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DakJourney> dakJourneyList = dakJourneyRepository.findAll();
        assertThat(dakJourneyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
