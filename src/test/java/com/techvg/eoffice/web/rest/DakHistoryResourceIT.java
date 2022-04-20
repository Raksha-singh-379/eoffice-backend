package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.DakHistory;
import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.domain.enumeration.DakStatus;
import com.techvg.eoffice.repository.DakHistoryRepository;
import com.techvg.eoffice.service.DakHistoryService;
import com.techvg.eoffice.service.criteria.DakHistoryCriteria;
import com.techvg.eoffice.service.dto.DakHistoryDTO;
import com.techvg.eoffice.service.mapper.DakHistoryMapper;
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
 * Integration tests for the {@link DakHistoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DakHistoryResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ASSIGNED_BY = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_ASSIGNED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final DakStatus DEFAULT_DAK_STATUS = DakStatus.CREATED;
    private static final DakStatus UPDATED_DAK_STATUS = DakStatus.UPDATED;

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/dak-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DakHistoryRepository dakHistoryRepository;

    @Mock
    private DakHistoryRepository dakHistoryRepositoryMock;

    @Autowired
    private DakHistoryMapper dakHistoryMapper;

    @Mock
    private DakHistoryService dakHistoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDakHistoryMockMvc;

    private DakHistory dakHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DakHistory createEntity(EntityManager em) {
        DakHistory dakHistory = new DakHistory()
            .date(DEFAULT_DATE)
            .assignedBy(DEFAULT_ASSIGNED_BY)
            .assignedOn(DEFAULT_ASSIGNED_ON)
            .createdOn(DEFAULT_CREATED_ON)
            .dakStatus(DEFAULT_DAK_STATUS)
            .status(DEFAULT_STATUS)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        return dakHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DakHistory createUpdatedEntity(EntityManager em) {
        DakHistory dakHistory = new DakHistory()
            .date(UPDATED_DATE)
            .assignedBy(UPDATED_ASSIGNED_BY)
            .assignedOn(UPDATED_ASSIGNED_ON)
            .createdOn(UPDATED_CREATED_ON)
            .dakStatus(UPDATED_DAK_STATUS)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        return dakHistory;
    }

    @BeforeEach
    public void initTest() {
        dakHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createDakHistory() throws Exception {
        int databaseSizeBeforeCreate = dakHistoryRepository.findAll().size();
        // Create the DakHistory
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(dakHistory);
        restDakHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO)))
            .andExpect(status().isCreated());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        DakHistory testDakHistory = dakHistoryList.get(dakHistoryList.size() - 1);
        assertThat(testDakHistory.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testDakHistory.getAssignedBy()).isEqualTo(DEFAULT_ASSIGNED_BY);
        assertThat(testDakHistory.getAssignedOn()).isEqualTo(DEFAULT_ASSIGNED_ON);
        assertThat(testDakHistory.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testDakHistory.getDakStatus()).isEqualTo(DEFAULT_DAK_STATUS);
        assertThat(testDakHistory.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDakHistory.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testDakHistory.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createDakHistoryWithExistingId() throws Exception {
        // Create the DakHistory with an existing ID
        dakHistory.setId(1L);
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(dakHistory);

        int databaseSizeBeforeCreate = dakHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDakHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDakHistories() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList
        restDakHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dakHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].assignedBy").value(hasItem(DEFAULT_ASSIGNED_BY)))
            .andExpect(jsonPath("$.[*].assignedOn").value(hasItem(DEFAULT_ASSIGNED_ON.toString())))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())))
            .andExpect(jsonPath("$.[*].dakStatus").value(hasItem(DEFAULT_DAK_STATUS.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDakHistoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(dakHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDakHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(dakHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDakHistoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(dakHistoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDakHistoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(dakHistoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getDakHistory() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get the dakHistory
        restDakHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, dakHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dakHistory.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.assignedBy").value(DEFAULT_ASSIGNED_BY))
            .andExpect(jsonPath("$.assignedOn").value(DEFAULT_ASSIGNED_ON.toString()))
            .andExpect(jsonPath("$.createdOn").value(DEFAULT_CREATED_ON.toString()))
            .andExpect(jsonPath("$.dakStatus").value(DEFAULT_DAK_STATUS.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getDakHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        Long id = dakHistory.getId();

        defaultDakHistoryShouldBeFound("id.equals=" + id);
        defaultDakHistoryShouldNotBeFound("id.notEquals=" + id);

        defaultDakHistoryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDakHistoryShouldNotBeFound("id.greaterThan=" + id);

        defaultDakHistoryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDakHistoryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where date equals to DEFAULT_DATE
        defaultDakHistoryShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the dakHistoryList where date equals to UPDATED_DATE
        defaultDakHistoryShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where date not equals to DEFAULT_DATE
        defaultDakHistoryShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the dakHistoryList where date not equals to UPDATED_DATE
        defaultDakHistoryShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where date in DEFAULT_DATE or UPDATED_DATE
        defaultDakHistoryShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the dakHistoryList where date equals to UPDATED_DATE
        defaultDakHistoryShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where date is not null
        defaultDakHistoryShouldBeFound("date.specified=true");

        // Get all the dakHistoryList where date is null
        defaultDakHistoryShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedByIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedBy equals to DEFAULT_ASSIGNED_BY
        defaultDakHistoryShouldBeFound("assignedBy.equals=" + DEFAULT_ASSIGNED_BY);

        // Get all the dakHistoryList where assignedBy equals to UPDATED_ASSIGNED_BY
        defaultDakHistoryShouldNotBeFound("assignedBy.equals=" + UPDATED_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedBy not equals to DEFAULT_ASSIGNED_BY
        defaultDakHistoryShouldNotBeFound("assignedBy.notEquals=" + DEFAULT_ASSIGNED_BY);

        // Get all the dakHistoryList where assignedBy not equals to UPDATED_ASSIGNED_BY
        defaultDakHistoryShouldBeFound("assignedBy.notEquals=" + UPDATED_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedByIsInShouldWork() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedBy in DEFAULT_ASSIGNED_BY or UPDATED_ASSIGNED_BY
        defaultDakHistoryShouldBeFound("assignedBy.in=" + DEFAULT_ASSIGNED_BY + "," + UPDATED_ASSIGNED_BY);

        // Get all the dakHistoryList where assignedBy equals to UPDATED_ASSIGNED_BY
        defaultDakHistoryShouldNotBeFound("assignedBy.in=" + UPDATED_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedBy is not null
        defaultDakHistoryShouldBeFound("assignedBy.specified=true");

        // Get all the dakHistoryList where assignedBy is null
        defaultDakHistoryShouldNotBeFound("assignedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedByContainsSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedBy contains DEFAULT_ASSIGNED_BY
        defaultDakHistoryShouldBeFound("assignedBy.contains=" + DEFAULT_ASSIGNED_BY);

        // Get all the dakHistoryList where assignedBy contains UPDATED_ASSIGNED_BY
        defaultDakHistoryShouldNotBeFound("assignedBy.contains=" + UPDATED_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedByNotContainsSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedBy does not contain DEFAULT_ASSIGNED_BY
        defaultDakHistoryShouldNotBeFound("assignedBy.doesNotContain=" + DEFAULT_ASSIGNED_BY);

        // Get all the dakHistoryList where assignedBy does not contain UPDATED_ASSIGNED_BY
        defaultDakHistoryShouldBeFound("assignedBy.doesNotContain=" + UPDATED_ASSIGNED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedOn equals to DEFAULT_ASSIGNED_ON
        defaultDakHistoryShouldBeFound("assignedOn.equals=" + DEFAULT_ASSIGNED_ON);

        // Get all the dakHistoryList where assignedOn equals to UPDATED_ASSIGNED_ON
        defaultDakHistoryShouldNotBeFound("assignedOn.equals=" + UPDATED_ASSIGNED_ON);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedOnIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedOn not equals to DEFAULT_ASSIGNED_ON
        defaultDakHistoryShouldNotBeFound("assignedOn.notEquals=" + DEFAULT_ASSIGNED_ON);

        // Get all the dakHistoryList where assignedOn not equals to UPDATED_ASSIGNED_ON
        defaultDakHistoryShouldBeFound("assignedOn.notEquals=" + UPDATED_ASSIGNED_ON);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedOnIsInShouldWork() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedOn in DEFAULT_ASSIGNED_ON or UPDATED_ASSIGNED_ON
        defaultDakHistoryShouldBeFound("assignedOn.in=" + DEFAULT_ASSIGNED_ON + "," + UPDATED_ASSIGNED_ON);

        // Get all the dakHistoryList where assignedOn equals to UPDATED_ASSIGNED_ON
        defaultDakHistoryShouldNotBeFound("assignedOn.in=" + UPDATED_ASSIGNED_ON);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByAssignedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where assignedOn is not null
        defaultDakHistoryShouldBeFound("assignedOn.specified=true");

        // Get all the dakHistoryList where assignedOn is null
        defaultDakHistoryShouldNotBeFound("assignedOn.specified=false");
    }

    @Test
    @Transactional
    void getAllDakHistoriesByCreatedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where createdOn equals to DEFAULT_CREATED_ON
        defaultDakHistoryShouldBeFound("createdOn.equals=" + DEFAULT_CREATED_ON);

        // Get all the dakHistoryList where createdOn equals to UPDATED_CREATED_ON
        defaultDakHistoryShouldNotBeFound("createdOn.equals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByCreatedOnIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where createdOn not equals to DEFAULT_CREATED_ON
        defaultDakHistoryShouldNotBeFound("createdOn.notEquals=" + DEFAULT_CREATED_ON);

        // Get all the dakHistoryList where createdOn not equals to UPDATED_CREATED_ON
        defaultDakHistoryShouldBeFound("createdOn.notEquals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByCreatedOnIsInShouldWork() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where createdOn in DEFAULT_CREATED_ON or UPDATED_CREATED_ON
        defaultDakHistoryShouldBeFound("createdOn.in=" + DEFAULT_CREATED_ON + "," + UPDATED_CREATED_ON);

        // Get all the dakHistoryList where createdOn equals to UPDATED_CREATED_ON
        defaultDakHistoryShouldNotBeFound("createdOn.in=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByCreatedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where createdOn is not null
        defaultDakHistoryShouldBeFound("createdOn.specified=true");

        // Get all the dakHistoryList where createdOn is null
        defaultDakHistoryShouldNotBeFound("createdOn.specified=false");
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDakStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where dakStatus equals to DEFAULT_DAK_STATUS
        defaultDakHistoryShouldBeFound("dakStatus.equals=" + DEFAULT_DAK_STATUS);

        // Get all the dakHistoryList where dakStatus equals to UPDATED_DAK_STATUS
        defaultDakHistoryShouldNotBeFound("dakStatus.equals=" + UPDATED_DAK_STATUS);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDakStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where dakStatus not equals to DEFAULT_DAK_STATUS
        defaultDakHistoryShouldNotBeFound("dakStatus.notEquals=" + DEFAULT_DAK_STATUS);

        // Get all the dakHistoryList where dakStatus not equals to UPDATED_DAK_STATUS
        defaultDakHistoryShouldBeFound("dakStatus.notEquals=" + UPDATED_DAK_STATUS);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDakStatusIsInShouldWork() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where dakStatus in DEFAULT_DAK_STATUS or UPDATED_DAK_STATUS
        defaultDakHistoryShouldBeFound("dakStatus.in=" + DEFAULT_DAK_STATUS + "," + UPDATED_DAK_STATUS);

        // Get all the dakHistoryList where dakStatus equals to UPDATED_DAK_STATUS
        defaultDakHistoryShouldNotBeFound("dakStatus.in=" + UPDATED_DAK_STATUS);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDakStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where dakStatus is not null
        defaultDakHistoryShouldBeFound("dakStatus.specified=true");

        // Get all the dakHistoryList where dakStatus is null
        defaultDakHistoryShouldNotBeFound("dakStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllDakHistoriesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where status equals to DEFAULT_STATUS
        defaultDakHistoryShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the dakHistoryList where status equals to UPDATED_STATUS
        defaultDakHistoryShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where status not equals to DEFAULT_STATUS
        defaultDakHistoryShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the dakHistoryList where status not equals to UPDATED_STATUS
        defaultDakHistoryShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultDakHistoryShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the dakHistoryList where status equals to UPDATED_STATUS
        defaultDakHistoryShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where status is not null
        defaultDakHistoryShouldBeFound("status.specified=true");

        // Get all the dakHistoryList where status is null
        defaultDakHistoryShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultDakHistoryShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the dakHistoryList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultDakHistoryShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultDakHistoryShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the dakHistoryList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultDakHistoryShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultDakHistoryShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the dakHistoryList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultDakHistoryShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModified is not null
        defaultDakHistoryShouldBeFound("lastModified.specified=true");

        // Get all the dakHistoryList where lastModified is null
        defaultDakHistoryShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultDakHistoryShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakHistoryList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDakHistoryShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultDakHistoryShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakHistoryList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultDakHistoryShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultDakHistoryShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the dakHistoryList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDakHistoryShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModifiedBy is not null
        defaultDakHistoryShouldBeFound("lastModifiedBy.specified=true");

        // Get all the dakHistoryList where lastModifiedBy is null
        defaultDakHistoryShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultDakHistoryShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakHistoryList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultDakHistoryShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        // Get all the dakHistoryList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultDakHistoryShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakHistoryList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultDakHistoryShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakHistoriesByDakMasterIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);
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
        dakHistory.setDakMaster(dakMaster);
        dakHistoryRepository.saveAndFlush(dakHistory);
        Long dakMasterId = dakMaster.getId();

        // Get all the dakHistoryList where dakMaster equals to dakMasterId
        defaultDakHistoryShouldBeFound("dakMasterId.equals=" + dakMasterId);

        // Get all the dakHistoryList where dakMaster equals to (dakMasterId + 1)
        defaultDakHistoryShouldNotBeFound("dakMasterId.equals=" + (dakMasterId + 1));
    }

    @Test
    @Transactional
    void getAllDakHistoriesBySecurityUserIsEqualToSomething() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);
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
        dakHistory.setSecurityUser(securityUser);
        dakHistoryRepository.saveAndFlush(dakHistory);
        Long securityUserId = securityUser.getId();

        // Get all the dakHistoryList where securityUser equals to securityUserId
        defaultDakHistoryShouldBeFound("securityUserId.equals=" + securityUserId);

        // Get all the dakHistoryList where securityUser equals to (securityUserId + 1)
        defaultDakHistoryShouldNotBeFound("securityUserId.equals=" + (securityUserId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDakHistoryShouldBeFound(String filter) throws Exception {
        restDakHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dakHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].assignedBy").value(hasItem(DEFAULT_ASSIGNED_BY)))
            .andExpect(jsonPath("$.[*].assignedOn").value(hasItem(DEFAULT_ASSIGNED_ON.toString())))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())))
            .andExpect(jsonPath("$.[*].dakStatus").value(hasItem(DEFAULT_DAK_STATUS.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restDakHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDakHistoryShouldNotBeFound(String filter) throws Exception {
        restDakHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDakHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDakHistory() throws Exception {
        // Get the dakHistory
        restDakHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDakHistory() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();

        // Update the dakHistory
        DakHistory updatedDakHistory = dakHistoryRepository.findById(dakHistory.getId()).get();
        // Disconnect from session so that the updates on updatedDakHistory are not directly saved in db
        em.detach(updatedDakHistory);
        updatedDakHistory
            .date(UPDATED_DATE)
            .assignedBy(UPDATED_ASSIGNED_BY)
            .assignedOn(UPDATED_ASSIGNED_ON)
            .createdOn(UPDATED_CREATED_ON)
            .dakStatus(UPDATED_DAK_STATUS)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(updatedDakHistory);

        restDakHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dakHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
        DakHistory testDakHistory = dakHistoryList.get(dakHistoryList.size() - 1);
        assertThat(testDakHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDakHistory.getAssignedBy()).isEqualTo(UPDATED_ASSIGNED_BY);
        assertThat(testDakHistory.getAssignedOn()).isEqualTo(UPDATED_ASSIGNED_ON);
        assertThat(testDakHistory.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testDakHistory.getDakStatus()).isEqualTo(UPDATED_DAK_STATUS);
        assertThat(testDakHistory.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDakHistory.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testDakHistory.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingDakHistory() throws Exception {
        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();
        dakHistory.setId(count.incrementAndGet());

        // Create the DakHistory
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(dakHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDakHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dakHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDakHistory() throws Exception {
        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();
        dakHistory.setId(count.incrementAndGet());

        // Create the DakHistory
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(dakHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDakHistory() throws Exception {
        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();
        dakHistory.setId(count.incrementAndGet());

        // Create the DakHistory
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(dakHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDakHistoryWithPatch() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();

        // Update the dakHistory using partial update
        DakHistory partialUpdatedDakHistory = new DakHistory();
        partialUpdatedDakHistory.setId(dakHistory.getId());

        partialUpdatedDakHistory.date(UPDATED_DATE).assignedBy(UPDATED_ASSIGNED_BY).createdOn(UPDATED_CREATED_ON);

        restDakHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDakHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDakHistory))
            )
            .andExpect(status().isOk());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
        DakHistory testDakHistory = dakHistoryList.get(dakHistoryList.size() - 1);
        assertThat(testDakHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDakHistory.getAssignedBy()).isEqualTo(UPDATED_ASSIGNED_BY);
        assertThat(testDakHistory.getAssignedOn()).isEqualTo(DEFAULT_ASSIGNED_ON);
        assertThat(testDakHistory.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testDakHistory.getDakStatus()).isEqualTo(DEFAULT_DAK_STATUS);
        assertThat(testDakHistory.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDakHistory.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testDakHistory.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateDakHistoryWithPatch() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();

        // Update the dakHistory using partial update
        DakHistory partialUpdatedDakHistory = new DakHistory();
        partialUpdatedDakHistory.setId(dakHistory.getId());

        partialUpdatedDakHistory
            .date(UPDATED_DATE)
            .assignedBy(UPDATED_ASSIGNED_BY)
            .assignedOn(UPDATED_ASSIGNED_ON)
            .createdOn(UPDATED_CREATED_ON)
            .dakStatus(UPDATED_DAK_STATUS)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restDakHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDakHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDakHistory))
            )
            .andExpect(status().isOk());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
        DakHistory testDakHistory = dakHistoryList.get(dakHistoryList.size() - 1);
        assertThat(testDakHistory.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDakHistory.getAssignedBy()).isEqualTo(UPDATED_ASSIGNED_BY);
        assertThat(testDakHistory.getAssignedOn()).isEqualTo(UPDATED_ASSIGNED_ON);
        assertThat(testDakHistory.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testDakHistory.getDakStatus()).isEqualTo(UPDATED_DAK_STATUS);
        assertThat(testDakHistory.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDakHistory.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testDakHistory.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingDakHistory() throws Exception {
        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();
        dakHistory.setId(count.incrementAndGet());

        // Create the DakHistory
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(dakHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDakHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dakHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDakHistory() throws Exception {
        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();
        dakHistory.setId(count.incrementAndGet());

        // Create the DakHistory
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(dakHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDakHistory() throws Exception {
        int databaseSizeBeforeUpdate = dakHistoryRepository.findAll().size();
        dakHistory.setId(count.incrementAndGet());

        // Create the DakHistory
        DakHistoryDTO dakHistoryDTO = dakHistoryMapper.toDto(dakHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dakHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DakHistory in the database
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDakHistory() throws Exception {
        // Initialize the database
        dakHistoryRepository.saveAndFlush(dakHistory);

        int databaseSizeBeforeDelete = dakHistoryRepository.findAll().size();

        // Delete the dakHistory
        restDakHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, dakHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DakHistory> dakHistoryList = dakHistoryRepository.findAll();
        assertThat(dakHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
