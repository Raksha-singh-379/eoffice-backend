package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.domain.HearingDetails;
import com.techvg.eoffice.domain.enumeration.DakStatus;
import com.techvg.eoffice.repository.HearingDetailsRepository;
import com.techvg.eoffice.service.HearingDetailsService;
import com.techvg.eoffice.service.criteria.HearingDetailsCriteria;
import com.techvg.eoffice.service.dto.HearingDetailsDTO;
import com.techvg.eoffice.service.mapper.HearingDetailsMapper;
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
 * Integration tests for the {@link HearingDetailsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HearingDetailsResourceIT {

    private static final String DEFAULT_ACCUSER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACCUSER_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_ORDER_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDER_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RESPONDENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RESPONDENT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final DakStatus DEFAULT_STATUS = DakStatus.CREATED;
    private static final DakStatus UPDATED_STATUS = DakStatus.UPDATED;

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/hearing-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HearingDetailsRepository hearingDetailsRepository;

    @Mock
    private HearingDetailsRepository hearingDetailsRepositoryMock;

    @Autowired
    private HearingDetailsMapper hearingDetailsMapper;

    @Mock
    private HearingDetailsService hearingDetailsServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHearingDetailsMockMvc;

    private HearingDetails hearingDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HearingDetails createEntity(EntityManager em) {
        HearingDetails hearingDetails = new HearingDetails()
            .accuserName(DEFAULT_ACCUSER_NAME)
            .orderDate(DEFAULT_ORDER_DATE)
            .respondentName(DEFAULT_RESPONDENT_NAME)
            .comment(DEFAULT_COMMENT)
            .date(DEFAULT_DATE)
            .time(DEFAULT_TIME)
            .status(DEFAULT_STATUS)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        return hearingDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HearingDetails createUpdatedEntity(EntityManager em) {
        HearingDetails hearingDetails = new HearingDetails()
            .accuserName(UPDATED_ACCUSER_NAME)
            .orderDate(UPDATED_ORDER_DATE)
            .respondentName(UPDATED_RESPONDENT_NAME)
            .comment(UPDATED_COMMENT)
            .date(UPDATED_DATE)
            .time(UPDATED_TIME)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        return hearingDetails;
    }

    @BeforeEach
    public void initTest() {
        hearingDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createHearingDetails() throws Exception {
        int databaseSizeBeforeCreate = hearingDetailsRepository.findAll().size();
        // Create the HearingDetails
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(hearingDetails);
        restHearingDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        HearingDetails testHearingDetails = hearingDetailsList.get(hearingDetailsList.size() - 1);
        assertThat(testHearingDetails.getAccuserName()).isEqualTo(DEFAULT_ACCUSER_NAME);
        assertThat(testHearingDetails.getOrderDate()).isEqualTo(DEFAULT_ORDER_DATE);
        assertThat(testHearingDetails.getRespondentName()).isEqualTo(DEFAULT_RESPONDENT_NAME);
        assertThat(testHearingDetails.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testHearingDetails.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testHearingDetails.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testHearingDetails.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testHearingDetails.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testHearingDetails.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createHearingDetailsWithExistingId() throws Exception {
        // Create the HearingDetails with an existing ID
        hearingDetails.setId(1L);
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(hearingDetails);

        int databaseSizeBeforeCreate = hearingDetailsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHearingDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHearingDetails() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList
        restHearingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hearingDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].accuserName").value(hasItem(DEFAULT_ACCUSER_NAME)))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].respondentName").value(hasItem(DEFAULT_RESPONDENT_NAME)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHearingDetailsWithEagerRelationshipsIsEnabled() throws Exception {
        when(hearingDetailsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHearingDetailsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(hearingDetailsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHearingDetailsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(hearingDetailsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHearingDetailsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(hearingDetailsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getHearingDetails() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get the hearingDetails
        restHearingDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, hearingDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hearingDetails.getId().intValue()))
            .andExpect(jsonPath("$.accuserName").value(DEFAULT_ACCUSER_NAME))
            .andExpect(jsonPath("$.orderDate").value(DEFAULT_ORDER_DATE.toString()))
            .andExpect(jsonPath("$.respondentName").value(DEFAULT_RESPONDENT_NAME))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getHearingDetailsByIdFiltering() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        Long id = hearingDetails.getId();

        defaultHearingDetailsShouldBeFound("id.equals=" + id);
        defaultHearingDetailsShouldNotBeFound("id.notEquals=" + id);

        defaultHearingDetailsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultHearingDetailsShouldNotBeFound("id.greaterThan=" + id);

        defaultHearingDetailsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultHearingDetailsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByAccuserNameIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where accuserName equals to DEFAULT_ACCUSER_NAME
        defaultHearingDetailsShouldBeFound("accuserName.equals=" + DEFAULT_ACCUSER_NAME);

        // Get all the hearingDetailsList where accuserName equals to UPDATED_ACCUSER_NAME
        defaultHearingDetailsShouldNotBeFound("accuserName.equals=" + UPDATED_ACCUSER_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByAccuserNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where accuserName not equals to DEFAULT_ACCUSER_NAME
        defaultHearingDetailsShouldNotBeFound("accuserName.notEquals=" + DEFAULT_ACCUSER_NAME);

        // Get all the hearingDetailsList where accuserName not equals to UPDATED_ACCUSER_NAME
        defaultHearingDetailsShouldBeFound("accuserName.notEquals=" + UPDATED_ACCUSER_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByAccuserNameIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where accuserName in DEFAULT_ACCUSER_NAME or UPDATED_ACCUSER_NAME
        defaultHearingDetailsShouldBeFound("accuserName.in=" + DEFAULT_ACCUSER_NAME + "," + UPDATED_ACCUSER_NAME);

        // Get all the hearingDetailsList where accuserName equals to UPDATED_ACCUSER_NAME
        defaultHearingDetailsShouldNotBeFound("accuserName.in=" + UPDATED_ACCUSER_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByAccuserNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where accuserName is not null
        defaultHearingDetailsShouldBeFound("accuserName.specified=true");

        // Get all the hearingDetailsList where accuserName is null
        defaultHearingDetailsShouldNotBeFound("accuserName.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByAccuserNameContainsSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where accuserName contains DEFAULT_ACCUSER_NAME
        defaultHearingDetailsShouldBeFound("accuserName.contains=" + DEFAULT_ACCUSER_NAME);

        // Get all the hearingDetailsList where accuserName contains UPDATED_ACCUSER_NAME
        defaultHearingDetailsShouldNotBeFound("accuserName.contains=" + UPDATED_ACCUSER_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByAccuserNameNotContainsSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where accuserName does not contain DEFAULT_ACCUSER_NAME
        defaultHearingDetailsShouldNotBeFound("accuserName.doesNotContain=" + DEFAULT_ACCUSER_NAME);

        // Get all the hearingDetailsList where accuserName does not contain UPDATED_ACCUSER_NAME
        defaultHearingDetailsShouldBeFound("accuserName.doesNotContain=" + UPDATED_ACCUSER_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByOrderDateIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where orderDate equals to DEFAULT_ORDER_DATE
        defaultHearingDetailsShouldBeFound("orderDate.equals=" + DEFAULT_ORDER_DATE);

        // Get all the hearingDetailsList where orderDate equals to UPDATED_ORDER_DATE
        defaultHearingDetailsShouldNotBeFound("orderDate.equals=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByOrderDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where orderDate not equals to DEFAULT_ORDER_DATE
        defaultHearingDetailsShouldNotBeFound("orderDate.notEquals=" + DEFAULT_ORDER_DATE);

        // Get all the hearingDetailsList where orderDate not equals to UPDATED_ORDER_DATE
        defaultHearingDetailsShouldBeFound("orderDate.notEquals=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByOrderDateIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where orderDate in DEFAULT_ORDER_DATE or UPDATED_ORDER_DATE
        defaultHearingDetailsShouldBeFound("orderDate.in=" + DEFAULT_ORDER_DATE + "," + UPDATED_ORDER_DATE);

        // Get all the hearingDetailsList where orderDate equals to UPDATED_ORDER_DATE
        defaultHearingDetailsShouldNotBeFound("orderDate.in=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByOrderDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where orderDate is not null
        defaultHearingDetailsShouldBeFound("orderDate.specified=true");

        // Get all the hearingDetailsList where orderDate is null
        defaultHearingDetailsShouldNotBeFound("orderDate.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByRespondentNameIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where respondentName equals to DEFAULT_RESPONDENT_NAME
        defaultHearingDetailsShouldBeFound("respondentName.equals=" + DEFAULT_RESPONDENT_NAME);

        // Get all the hearingDetailsList where respondentName equals to UPDATED_RESPONDENT_NAME
        defaultHearingDetailsShouldNotBeFound("respondentName.equals=" + UPDATED_RESPONDENT_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByRespondentNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where respondentName not equals to DEFAULT_RESPONDENT_NAME
        defaultHearingDetailsShouldNotBeFound("respondentName.notEquals=" + DEFAULT_RESPONDENT_NAME);

        // Get all the hearingDetailsList where respondentName not equals to UPDATED_RESPONDENT_NAME
        defaultHearingDetailsShouldBeFound("respondentName.notEquals=" + UPDATED_RESPONDENT_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByRespondentNameIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where respondentName in DEFAULT_RESPONDENT_NAME or UPDATED_RESPONDENT_NAME
        defaultHearingDetailsShouldBeFound("respondentName.in=" + DEFAULT_RESPONDENT_NAME + "," + UPDATED_RESPONDENT_NAME);

        // Get all the hearingDetailsList where respondentName equals to UPDATED_RESPONDENT_NAME
        defaultHearingDetailsShouldNotBeFound("respondentName.in=" + UPDATED_RESPONDENT_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByRespondentNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where respondentName is not null
        defaultHearingDetailsShouldBeFound("respondentName.specified=true");

        // Get all the hearingDetailsList where respondentName is null
        defaultHearingDetailsShouldNotBeFound("respondentName.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByRespondentNameContainsSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where respondentName contains DEFAULT_RESPONDENT_NAME
        defaultHearingDetailsShouldBeFound("respondentName.contains=" + DEFAULT_RESPONDENT_NAME);

        // Get all the hearingDetailsList where respondentName contains UPDATED_RESPONDENT_NAME
        defaultHearingDetailsShouldNotBeFound("respondentName.contains=" + UPDATED_RESPONDENT_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByRespondentNameNotContainsSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where respondentName does not contain DEFAULT_RESPONDENT_NAME
        defaultHearingDetailsShouldNotBeFound("respondentName.doesNotContain=" + DEFAULT_RESPONDENT_NAME);

        // Get all the hearingDetailsList where respondentName does not contain UPDATED_RESPONDENT_NAME
        defaultHearingDetailsShouldBeFound("respondentName.doesNotContain=" + UPDATED_RESPONDENT_NAME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where comment equals to DEFAULT_COMMENT
        defaultHearingDetailsShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the hearingDetailsList where comment equals to UPDATED_COMMENT
        defaultHearingDetailsShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where comment not equals to DEFAULT_COMMENT
        defaultHearingDetailsShouldNotBeFound("comment.notEquals=" + DEFAULT_COMMENT);

        // Get all the hearingDetailsList where comment not equals to UPDATED_COMMENT
        defaultHearingDetailsShouldBeFound("comment.notEquals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultHearingDetailsShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the hearingDetailsList where comment equals to UPDATED_COMMENT
        defaultHearingDetailsShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where comment is not null
        defaultHearingDetailsShouldBeFound("comment.specified=true");

        // Get all the hearingDetailsList where comment is null
        defaultHearingDetailsShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByCommentContainsSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where comment contains DEFAULT_COMMENT
        defaultHearingDetailsShouldBeFound("comment.contains=" + DEFAULT_COMMENT);

        // Get all the hearingDetailsList where comment contains UPDATED_COMMENT
        defaultHearingDetailsShouldNotBeFound("comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where comment does not contain DEFAULT_COMMENT
        defaultHearingDetailsShouldNotBeFound("comment.doesNotContain=" + DEFAULT_COMMENT);

        // Get all the hearingDetailsList where comment does not contain UPDATED_COMMENT
        defaultHearingDetailsShouldBeFound("comment.doesNotContain=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where date equals to DEFAULT_DATE
        defaultHearingDetailsShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the hearingDetailsList where date equals to UPDATED_DATE
        defaultHearingDetailsShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where date not equals to DEFAULT_DATE
        defaultHearingDetailsShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the hearingDetailsList where date not equals to UPDATED_DATE
        defaultHearingDetailsShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where date in DEFAULT_DATE or UPDATED_DATE
        defaultHearingDetailsShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the hearingDetailsList where date equals to UPDATED_DATE
        defaultHearingDetailsShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where date is not null
        defaultHearingDetailsShouldBeFound("date.specified=true");

        // Get all the hearingDetailsList where date is null
        defaultHearingDetailsShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where time equals to DEFAULT_TIME
        defaultHearingDetailsShouldBeFound("time.equals=" + DEFAULT_TIME);

        // Get all the hearingDetailsList where time equals to UPDATED_TIME
        defaultHearingDetailsShouldNotBeFound("time.equals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where time not equals to DEFAULT_TIME
        defaultHearingDetailsShouldNotBeFound("time.notEquals=" + DEFAULT_TIME);

        // Get all the hearingDetailsList where time not equals to UPDATED_TIME
        defaultHearingDetailsShouldBeFound("time.notEquals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByTimeIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where time in DEFAULT_TIME or UPDATED_TIME
        defaultHearingDetailsShouldBeFound("time.in=" + DEFAULT_TIME + "," + UPDATED_TIME);

        // Get all the hearingDetailsList where time equals to UPDATED_TIME
        defaultHearingDetailsShouldNotBeFound("time.in=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where time is not null
        defaultHearingDetailsShouldBeFound("time.specified=true");

        // Get all the hearingDetailsList where time is null
        defaultHearingDetailsShouldNotBeFound("time.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where status equals to DEFAULT_STATUS
        defaultHearingDetailsShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the hearingDetailsList where status equals to UPDATED_STATUS
        defaultHearingDetailsShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where status not equals to DEFAULT_STATUS
        defaultHearingDetailsShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the hearingDetailsList where status not equals to UPDATED_STATUS
        defaultHearingDetailsShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultHearingDetailsShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the hearingDetailsList where status equals to UPDATED_STATUS
        defaultHearingDetailsShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where status is not null
        defaultHearingDetailsShouldBeFound("status.specified=true");

        // Get all the hearingDetailsList where status is null
        defaultHearingDetailsShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultHearingDetailsShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the hearingDetailsList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultHearingDetailsShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultHearingDetailsShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the hearingDetailsList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultHearingDetailsShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultHearingDetailsShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the hearingDetailsList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultHearingDetailsShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModified is not null
        defaultHearingDetailsShouldBeFound("lastModified.specified=true");

        // Get all the hearingDetailsList where lastModified is null
        defaultHearingDetailsShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultHearingDetailsShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the hearingDetailsList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultHearingDetailsShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultHearingDetailsShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the hearingDetailsList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultHearingDetailsShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultHearingDetailsShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the hearingDetailsList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultHearingDetailsShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModifiedBy is not null
        defaultHearingDetailsShouldBeFound("lastModifiedBy.specified=true");

        // Get all the hearingDetailsList where lastModifiedBy is null
        defaultHearingDetailsShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultHearingDetailsShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the hearingDetailsList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultHearingDetailsShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        // Get all the hearingDetailsList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultHearingDetailsShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the hearingDetailsList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultHearingDetailsShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllHearingDetailsByDakMasterIsEqualToSomething() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);
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
        hearingDetails.setDakMaster(dakMaster);
        hearingDetailsRepository.saveAndFlush(hearingDetails);
        Long dakMasterId = dakMaster.getId();

        // Get all the hearingDetailsList where dakMaster equals to dakMasterId
        defaultHearingDetailsShouldBeFound("dakMasterId.equals=" + dakMasterId);

        // Get all the hearingDetailsList where dakMaster equals to (dakMasterId + 1)
        defaultHearingDetailsShouldNotBeFound("dakMasterId.equals=" + (dakMasterId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHearingDetailsShouldBeFound(String filter) throws Exception {
        restHearingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hearingDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].accuserName").value(hasItem(DEFAULT_ACCUSER_NAME)))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].respondentName").value(hasItem(DEFAULT_RESPONDENT_NAME)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restHearingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHearingDetailsShouldNotBeFound(String filter) throws Exception {
        restHearingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHearingDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHearingDetails() throws Exception {
        // Get the hearingDetails
        restHearingDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHearingDetails() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();

        // Update the hearingDetails
        HearingDetails updatedHearingDetails = hearingDetailsRepository.findById(hearingDetails.getId()).get();
        // Disconnect from session so that the updates on updatedHearingDetails are not directly saved in db
        em.detach(updatedHearingDetails);
        updatedHearingDetails
            .accuserName(UPDATED_ACCUSER_NAME)
            .orderDate(UPDATED_ORDER_DATE)
            .respondentName(UPDATED_RESPONDENT_NAME)
            .comment(UPDATED_COMMENT)
            .date(UPDATED_DATE)
            .time(UPDATED_TIME)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(updatedHearingDetails);

        restHearingDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hearingDetailsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isOk());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
        HearingDetails testHearingDetails = hearingDetailsList.get(hearingDetailsList.size() - 1);
        assertThat(testHearingDetails.getAccuserName()).isEqualTo(UPDATED_ACCUSER_NAME);
        assertThat(testHearingDetails.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
        assertThat(testHearingDetails.getRespondentName()).isEqualTo(UPDATED_RESPONDENT_NAME);
        assertThat(testHearingDetails.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testHearingDetails.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testHearingDetails.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testHearingDetails.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testHearingDetails.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testHearingDetails.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingHearingDetails() throws Exception {
        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();
        hearingDetails.setId(count.incrementAndGet());

        // Create the HearingDetails
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(hearingDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHearingDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hearingDetailsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHearingDetails() throws Exception {
        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();
        hearingDetails.setId(count.incrementAndGet());

        // Create the HearingDetails
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(hearingDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHearingDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHearingDetails() throws Exception {
        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();
        hearingDetails.setId(count.incrementAndGet());

        // Create the HearingDetails
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(hearingDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHearingDetailsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHearingDetailsWithPatch() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();

        // Update the hearingDetails using partial update
        HearingDetails partialUpdatedHearingDetails = new HearingDetails();
        partialUpdatedHearingDetails.setId(hearingDetails.getId());

        partialUpdatedHearingDetails
            .accuserName(UPDATED_ACCUSER_NAME)
            .orderDate(UPDATED_ORDER_DATE)
            .date(UPDATED_DATE)
            .time(UPDATED_TIME)
            .status(UPDATED_STATUS)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restHearingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHearingDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHearingDetails))
            )
            .andExpect(status().isOk());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
        HearingDetails testHearingDetails = hearingDetailsList.get(hearingDetailsList.size() - 1);
        assertThat(testHearingDetails.getAccuserName()).isEqualTo(UPDATED_ACCUSER_NAME);
        assertThat(testHearingDetails.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
        assertThat(testHearingDetails.getRespondentName()).isEqualTo(DEFAULT_RESPONDENT_NAME);
        assertThat(testHearingDetails.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testHearingDetails.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testHearingDetails.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testHearingDetails.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testHearingDetails.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testHearingDetails.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateHearingDetailsWithPatch() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();

        // Update the hearingDetails using partial update
        HearingDetails partialUpdatedHearingDetails = new HearingDetails();
        partialUpdatedHearingDetails.setId(hearingDetails.getId());

        partialUpdatedHearingDetails
            .accuserName(UPDATED_ACCUSER_NAME)
            .orderDate(UPDATED_ORDER_DATE)
            .respondentName(UPDATED_RESPONDENT_NAME)
            .comment(UPDATED_COMMENT)
            .date(UPDATED_DATE)
            .time(UPDATED_TIME)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restHearingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHearingDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHearingDetails))
            )
            .andExpect(status().isOk());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
        HearingDetails testHearingDetails = hearingDetailsList.get(hearingDetailsList.size() - 1);
        assertThat(testHearingDetails.getAccuserName()).isEqualTo(UPDATED_ACCUSER_NAME);
        assertThat(testHearingDetails.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
        assertThat(testHearingDetails.getRespondentName()).isEqualTo(UPDATED_RESPONDENT_NAME);
        assertThat(testHearingDetails.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testHearingDetails.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testHearingDetails.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testHearingDetails.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testHearingDetails.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testHearingDetails.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingHearingDetails() throws Exception {
        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();
        hearingDetails.setId(count.incrementAndGet());

        // Create the HearingDetails
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(hearingDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHearingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hearingDetailsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHearingDetails() throws Exception {
        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();
        hearingDetails.setId(count.incrementAndGet());

        // Create the HearingDetails
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(hearingDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHearingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHearingDetails() throws Exception {
        int databaseSizeBeforeUpdate = hearingDetailsRepository.findAll().size();
        hearingDetails.setId(count.incrementAndGet());

        // Create the HearingDetails
        HearingDetailsDTO hearingDetailsDTO = hearingDetailsMapper.toDto(hearingDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHearingDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hearingDetailsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HearingDetails in the database
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHearingDetails() throws Exception {
        // Initialize the database
        hearingDetailsRepository.saveAndFlush(hearingDetails);

        int databaseSizeBeforeDelete = hearingDetailsRepository.findAll().size();

        // Delete the hearingDetails
        restHearingDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, hearingDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HearingDetails> hearingDetailsList = hearingDetailsRepository.findAll();
        assertThat(hearingDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
