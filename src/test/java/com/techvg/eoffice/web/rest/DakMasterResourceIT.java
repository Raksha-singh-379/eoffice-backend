package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.domain.Organization;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.domain.enumeration.DakStatus;
import com.techvg.eoffice.domain.enumeration.LetterType;
import com.techvg.eoffice.repository.DakMasterRepository;
import com.techvg.eoffice.service.DakMasterService;
import com.techvg.eoffice.service.criteria.DakMasterCriteria;
import com.techvg.eoffice.service.dto.DakMasterDTO;
import com.techvg.eoffice.service.mapper.DakMasterMapper;
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
 * Integration tests for the {@link DakMasterResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DakMasterResourceIT {

    private static final String DEFAULT_INWARD_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_INWARD_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final Instant DEFAULT_LETTER_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LETTER_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final DakStatus DEFAULT_CURRENT_STATUS = DakStatus.CREATED;
    private static final DakStatus UPDATED_CURRENT_STATUS = DakStatus.UPDATED;

    private static final Boolean DEFAULT_LETTER_STATUS = false;
    private static final Boolean UPDATED_LETTER_STATUS = true;

    private static final Instant DEFAULT_LETTER_RECEIVED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LETTER_RECEIVED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_AWAIT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_AWAIT_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_DISPATCH_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DISPATCH_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final LetterType DEFAULT_LETTER_TYPE = LetterType.INWARD;
    private static final LetterType UPDATED_LETTER_TYPE = LetterType.OUTWARD;

    private static final Boolean DEFAULT_IS_RESPONSE_RECEIVED = false;
    private static final Boolean UPDATED_IS_RESPONSE_RECEIVED = true;

    private static final Instant DEFAULT_ASSIGNED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_DAK_ASSIGNED_FROM = "AAAAAAAAAA";
    private static final String UPDATED_DAK_ASSIGNED_FROM = "BBBBBBBBBB";

    private static final String DEFAULT_DAK_ASSIGNEE = "AAAAAAAAAA";
    private static final String UPDATED_DAK_ASSIGNEE = "BBBBBBBBBB";

    private static final String DEFAULT_DISPATCH_BY = "AAAAAAAAAA";
    private static final String UPDATED_DISPATCH_BY = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_OUTWARD = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_OUTWARD = "BBBBBBBBBB";

    private static final String DEFAULT_OUTWARD_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_OUTWARD_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_TALUKA = "AAAAAAAAAA";
    private static final String UPDATED_TALUKA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/dak-masters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DakMasterRepository dakMasterRepository;

    @Mock
    private DakMasterRepository dakMasterRepositoryMock;

    @Autowired
    private DakMasterMapper dakMasterMapper;

    @Mock
    private DakMasterService dakMasterServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDakMasterMockMvc;

    private DakMaster dakMaster;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DakMaster createEntity(EntityManager em) {
        DakMaster dakMaster = new DakMaster()
            .inwardNumber(DEFAULT_INWARD_NUMBER)
            .senderName(DEFAULT_SENDER_NAME)
            .contactNumber(DEFAULT_CONTACT_NUMBER)
            .senderAddress(DEFAULT_SENDER_ADDRESS)
            .senderEmail(DEFAULT_SENDER_EMAIL)
            .subject(DEFAULT_SUBJECT)
            .letterDate(DEFAULT_LETTER_DATE)
            .currentStatus(DEFAULT_CURRENT_STATUS)
            .letterStatus(DEFAULT_LETTER_STATUS)
            .letterReceivedDate(DEFAULT_LETTER_RECEIVED_DATE)
            .awaitReason(DEFAULT_AWAIT_REASON)
            .dispatchDate(DEFAULT_DISPATCH_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .letterType(DEFAULT_LETTER_TYPE)
            .isResponseReceived(DEFAULT_IS_RESPONSE_RECEIVED)
            .assignedDate(DEFAULT_ASSIGNED_DATE)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .dakAssignedFrom(DEFAULT_DAK_ASSIGNED_FROM)
            .dakAssignee(DEFAULT_DAK_ASSIGNEE)
            .dispatchBy(DEFAULT_DISPATCH_BY)
            .senderOutward(DEFAULT_SENDER_OUTWARD)
            .outwardNumber(DEFAULT_OUTWARD_NUMBER)
            .taluka(DEFAULT_TALUKA);
        return dakMaster;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DakMaster createUpdatedEntity(EntityManager em) {
        DakMaster dakMaster = new DakMaster()
            .inwardNumber(UPDATED_INWARD_NUMBER)
            .senderName(UPDATED_SENDER_NAME)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .senderAddress(UPDATED_SENDER_ADDRESS)
            .senderEmail(UPDATED_SENDER_EMAIL)
            .subject(UPDATED_SUBJECT)
            .letterDate(UPDATED_LETTER_DATE)
            .currentStatus(UPDATED_CURRENT_STATUS)
            .letterStatus(UPDATED_LETTER_STATUS)
            .letterReceivedDate(UPDATED_LETTER_RECEIVED_DATE)
            .awaitReason(UPDATED_AWAIT_REASON)
            .dispatchDate(UPDATED_DISPATCH_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .letterType(UPDATED_LETTER_TYPE)
            .isResponseReceived(UPDATED_IS_RESPONSE_RECEIVED)
            .assignedDate(UPDATED_ASSIGNED_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .dakAssignedFrom(UPDATED_DAK_ASSIGNED_FROM)
            .dakAssignee(UPDATED_DAK_ASSIGNEE)
            .dispatchBy(UPDATED_DISPATCH_BY)
            .senderOutward(UPDATED_SENDER_OUTWARD)
            .outwardNumber(UPDATED_OUTWARD_NUMBER)
            .taluka(UPDATED_TALUKA);
        return dakMaster;
    }

    @BeforeEach
    public void initTest() {
        dakMaster = createEntity(em);
    }

    @Test
    @Transactional
    void createDakMaster() throws Exception {
        int databaseSizeBeforeCreate = dakMasterRepository.findAll().size();
        // Create the DakMaster
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(dakMaster);
        restDakMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakMasterDTO)))
            .andExpect(status().isCreated());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeCreate + 1);
        DakMaster testDakMaster = dakMasterList.get(dakMasterList.size() - 1);
        assertThat(testDakMaster.getInwardNumber()).isEqualTo(DEFAULT_INWARD_NUMBER);
        assertThat(testDakMaster.getSenderName()).isEqualTo(DEFAULT_SENDER_NAME);
        assertThat(testDakMaster.getContactNumber()).isEqualTo(DEFAULT_CONTACT_NUMBER);
        assertThat(testDakMaster.getSenderAddress()).isEqualTo(DEFAULT_SENDER_ADDRESS);
        assertThat(testDakMaster.getSenderEmail()).isEqualTo(DEFAULT_SENDER_EMAIL);
        assertThat(testDakMaster.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testDakMaster.getLetterDate()).isEqualTo(DEFAULT_LETTER_DATE);
        assertThat(testDakMaster.getCurrentStatus()).isEqualTo(DEFAULT_CURRENT_STATUS);
        assertThat(testDakMaster.getLetterStatus()).isEqualTo(DEFAULT_LETTER_STATUS);
        assertThat(testDakMaster.getLetterReceivedDate()).isEqualTo(DEFAULT_LETTER_RECEIVED_DATE);
        assertThat(testDakMaster.getAwaitReason()).isEqualTo(DEFAULT_AWAIT_REASON);
        assertThat(testDakMaster.getDispatchDate()).isEqualTo(DEFAULT_DISPATCH_DATE);
        assertThat(testDakMaster.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDakMaster.getLetterType()).isEqualTo(DEFAULT_LETTER_TYPE);
        assertThat(testDakMaster.getIsResponseReceived()).isEqualTo(DEFAULT_IS_RESPONSE_RECEIVED);
        assertThat(testDakMaster.getAssignedDate()).isEqualTo(DEFAULT_ASSIGNED_DATE);
        assertThat(testDakMaster.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testDakMaster.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testDakMaster.getDakAssignedFrom()).isEqualTo(DEFAULT_DAK_ASSIGNED_FROM);
        assertThat(testDakMaster.getDakAssignee()).isEqualTo(DEFAULT_DAK_ASSIGNEE);
        assertThat(testDakMaster.getDispatchBy()).isEqualTo(DEFAULT_DISPATCH_BY);
        assertThat(testDakMaster.getSenderOutward()).isEqualTo(DEFAULT_SENDER_OUTWARD);
        assertThat(testDakMaster.getOutwardNumber()).isEqualTo(DEFAULT_OUTWARD_NUMBER);
        assertThat(testDakMaster.getTaluka()).isEqualTo(DEFAULT_TALUKA);
    }

    @Test
    @Transactional
    void createDakMasterWithExistingId() throws Exception {
        // Create the DakMaster with an existing ID
        dakMaster.setId(1L);
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(dakMaster);

        int databaseSizeBeforeCreate = dakMasterRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDakMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakMasterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDakMasters() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList
        restDakMasterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dakMaster.getId().intValue())))
            .andExpect(jsonPath("$.[*].inwardNumber").value(hasItem(DEFAULT_INWARD_NUMBER)))
            .andExpect(jsonPath("$.[*].senderName").value(hasItem(DEFAULT_SENDER_NAME)))
            .andExpect(jsonPath("$.[*].contactNumber").value(hasItem(DEFAULT_CONTACT_NUMBER)))
            .andExpect(jsonPath("$.[*].senderAddress").value(hasItem(DEFAULT_SENDER_ADDRESS)))
            .andExpect(jsonPath("$.[*].senderEmail").value(hasItem(DEFAULT_SENDER_EMAIL)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].letterDate").value(hasItem(DEFAULT_LETTER_DATE.toString())))
            .andExpect(jsonPath("$.[*].currentStatus").value(hasItem(DEFAULT_CURRENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].letterStatus").value(hasItem(DEFAULT_LETTER_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].letterReceivedDate").value(hasItem(DEFAULT_LETTER_RECEIVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].awaitReason").value(hasItem(DEFAULT_AWAIT_REASON)))
            .andExpect(jsonPath("$.[*].dispatchDate").value(hasItem(DEFAULT_DISPATCH_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].letterType").value(hasItem(DEFAULT_LETTER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].isResponseReceived").value(hasItem(DEFAULT_IS_RESPONSE_RECEIVED.booleanValue())))
            .andExpect(jsonPath("$.[*].assignedDate").value(hasItem(DEFAULT_ASSIGNED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].dakAssignedFrom").value(hasItem(DEFAULT_DAK_ASSIGNED_FROM)))
            .andExpect(jsonPath("$.[*].dakAssignee").value(hasItem(DEFAULT_DAK_ASSIGNEE)))
            .andExpect(jsonPath("$.[*].dispatchBy").value(hasItem(DEFAULT_DISPATCH_BY)))
            .andExpect(jsonPath("$.[*].senderOutward").value(hasItem(DEFAULT_SENDER_OUTWARD)))
            .andExpect(jsonPath("$.[*].outwardNumber").value(hasItem(DEFAULT_OUTWARD_NUMBER)))
            .andExpect(jsonPath("$.[*].taluka").value(hasItem(DEFAULT_TALUKA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDakMastersWithEagerRelationshipsIsEnabled() throws Exception {
        when(dakMasterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDakMasterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(dakMasterServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDakMastersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(dakMasterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDakMasterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(dakMasterServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getDakMaster() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get the dakMaster
        restDakMasterMockMvc
            .perform(get(ENTITY_API_URL_ID, dakMaster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dakMaster.getId().intValue()))
            .andExpect(jsonPath("$.inwardNumber").value(DEFAULT_INWARD_NUMBER))
            .andExpect(jsonPath("$.senderName").value(DEFAULT_SENDER_NAME))
            .andExpect(jsonPath("$.contactNumber").value(DEFAULT_CONTACT_NUMBER))
            .andExpect(jsonPath("$.senderAddress").value(DEFAULT_SENDER_ADDRESS))
            .andExpect(jsonPath("$.senderEmail").value(DEFAULT_SENDER_EMAIL))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.letterDate").value(DEFAULT_LETTER_DATE.toString()))
            .andExpect(jsonPath("$.currentStatus").value(DEFAULT_CURRENT_STATUS.toString()))
            .andExpect(jsonPath("$.letterStatus").value(DEFAULT_LETTER_STATUS.booleanValue()))
            .andExpect(jsonPath("$.letterReceivedDate").value(DEFAULT_LETTER_RECEIVED_DATE.toString()))
            .andExpect(jsonPath("$.awaitReason").value(DEFAULT_AWAIT_REASON))
            .andExpect(jsonPath("$.dispatchDate").value(DEFAULT_DISPATCH_DATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.letterType").value(DEFAULT_LETTER_TYPE.toString()))
            .andExpect(jsonPath("$.isResponseReceived").value(DEFAULT_IS_RESPONSE_RECEIVED.booleanValue()))
            .andExpect(jsonPath("$.assignedDate").value(DEFAULT_ASSIGNED_DATE.toString()))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.dakAssignedFrom").value(DEFAULT_DAK_ASSIGNED_FROM))
            .andExpect(jsonPath("$.dakAssignee").value(DEFAULT_DAK_ASSIGNEE))
            .andExpect(jsonPath("$.dispatchBy").value(DEFAULT_DISPATCH_BY))
            .andExpect(jsonPath("$.senderOutward").value(DEFAULT_SENDER_OUTWARD))
            .andExpect(jsonPath("$.outwardNumber").value(DEFAULT_OUTWARD_NUMBER))
            .andExpect(jsonPath("$.taluka").value(DEFAULT_TALUKA));
    }

    @Test
    @Transactional
    void getDakMastersByIdFiltering() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        Long id = dakMaster.getId();

        defaultDakMasterShouldBeFound("id.equals=" + id);
        defaultDakMasterShouldNotBeFound("id.notEquals=" + id);

        defaultDakMasterShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDakMasterShouldNotBeFound("id.greaterThan=" + id);

        defaultDakMasterShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDakMasterShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDakMastersByInwardNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where inwardNumber equals to DEFAULT_INWARD_NUMBER
        defaultDakMasterShouldBeFound("inwardNumber.equals=" + DEFAULT_INWARD_NUMBER);

        // Get all the dakMasterList where inwardNumber equals to UPDATED_INWARD_NUMBER
        defaultDakMasterShouldNotBeFound("inwardNumber.equals=" + UPDATED_INWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByInwardNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where inwardNumber not equals to DEFAULT_INWARD_NUMBER
        defaultDakMasterShouldNotBeFound("inwardNumber.notEquals=" + DEFAULT_INWARD_NUMBER);

        // Get all the dakMasterList where inwardNumber not equals to UPDATED_INWARD_NUMBER
        defaultDakMasterShouldBeFound("inwardNumber.notEquals=" + UPDATED_INWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByInwardNumberIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where inwardNumber in DEFAULT_INWARD_NUMBER or UPDATED_INWARD_NUMBER
        defaultDakMasterShouldBeFound("inwardNumber.in=" + DEFAULT_INWARD_NUMBER + "," + UPDATED_INWARD_NUMBER);

        // Get all the dakMasterList where inwardNumber equals to UPDATED_INWARD_NUMBER
        defaultDakMasterShouldNotBeFound("inwardNumber.in=" + UPDATED_INWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByInwardNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where inwardNumber is not null
        defaultDakMasterShouldBeFound("inwardNumber.specified=true");

        // Get all the dakMasterList where inwardNumber is null
        defaultDakMasterShouldNotBeFound("inwardNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByInwardNumberContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where inwardNumber contains DEFAULT_INWARD_NUMBER
        defaultDakMasterShouldBeFound("inwardNumber.contains=" + DEFAULT_INWARD_NUMBER);

        // Get all the dakMasterList where inwardNumber contains UPDATED_INWARD_NUMBER
        defaultDakMasterShouldNotBeFound("inwardNumber.contains=" + UPDATED_INWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByInwardNumberNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where inwardNumber does not contain DEFAULT_INWARD_NUMBER
        defaultDakMasterShouldNotBeFound("inwardNumber.doesNotContain=" + DEFAULT_INWARD_NUMBER);

        // Get all the dakMasterList where inwardNumber does not contain UPDATED_INWARD_NUMBER
        defaultDakMasterShouldBeFound("inwardNumber.doesNotContain=" + UPDATED_INWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderNameIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderName equals to DEFAULT_SENDER_NAME
        defaultDakMasterShouldBeFound("senderName.equals=" + DEFAULT_SENDER_NAME);

        // Get all the dakMasterList where senderName equals to UPDATED_SENDER_NAME
        defaultDakMasterShouldNotBeFound("senderName.equals=" + UPDATED_SENDER_NAME);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderName not equals to DEFAULT_SENDER_NAME
        defaultDakMasterShouldNotBeFound("senderName.notEquals=" + DEFAULT_SENDER_NAME);

        // Get all the dakMasterList where senderName not equals to UPDATED_SENDER_NAME
        defaultDakMasterShouldBeFound("senderName.notEquals=" + UPDATED_SENDER_NAME);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderNameIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderName in DEFAULT_SENDER_NAME or UPDATED_SENDER_NAME
        defaultDakMasterShouldBeFound("senderName.in=" + DEFAULT_SENDER_NAME + "," + UPDATED_SENDER_NAME);

        // Get all the dakMasterList where senderName equals to UPDATED_SENDER_NAME
        defaultDakMasterShouldNotBeFound("senderName.in=" + UPDATED_SENDER_NAME);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderName is not null
        defaultDakMasterShouldBeFound("senderName.specified=true");

        // Get all the dakMasterList where senderName is null
        defaultDakMasterShouldNotBeFound("senderName.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderNameContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderName contains DEFAULT_SENDER_NAME
        defaultDakMasterShouldBeFound("senderName.contains=" + DEFAULT_SENDER_NAME);

        // Get all the dakMasterList where senderName contains UPDATED_SENDER_NAME
        defaultDakMasterShouldNotBeFound("senderName.contains=" + UPDATED_SENDER_NAME);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderNameNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderName does not contain DEFAULT_SENDER_NAME
        defaultDakMasterShouldNotBeFound("senderName.doesNotContain=" + DEFAULT_SENDER_NAME);

        // Get all the dakMasterList where senderName does not contain UPDATED_SENDER_NAME
        defaultDakMasterShouldBeFound("senderName.doesNotContain=" + UPDATED_SENDER_NAME);
    }

    @Test
    @Transactional
    void getAllDakMastersByContactNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where contactNumber equals to DEFAULT_CONTACT_NUMBER
        defaultDakMasterShouldBeFound("contactNumber.equals=" + DEFAULT_CONTACT_NUMBER);

        // Get all the dakMasterList where contactNumber equals to UPDATED_CONTACT_NUMBER
        defaultDakMasterShouldNotBeFound("contactNumber.equals=" + UPDATED_CONTACT_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByContactNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where contactNumber not equals to DEFAULT_CONTACT_NUMBER
        defaultDakMasterShouldNotBeFound("contactNumber.notEquals=" + DEFAULT_CONTACT_NUMBER);

        // Get all the dakMasterList where contactNumber not equals to UPDATED_CONTACT_NUMBER
        defaultDakMasterShouldBeFound("contactNumber.notEquals=" + UPDATED_CONTACT_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByContactNumberIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where contactNumber in DEFAULT_CONTACT_NUMBER or UPDATED_CONTACT_NUMBER
        defaultDakMasterShouldBeFound("contactNumber.in=" + DEFAULT_CONTACT_NUMBER + "," + UPDATED_CONTACT_NUMBER);

        // Get all the dakMasterList where contactNumber equals to UPDATED_CONTACT_NUMBER
        defaultDakMasterShouldNotBeFound("contactNumber.in=" + UPDATED_CONTACT_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByContactNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where contactNumber is not null
        defaultDakMasterShouldBeFound("contactNumber.specified=true");

        // Get all the dakMasterList where contactNumber is null
        defaultDakMasterShouldNotBeFound("contactNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByContactNumberContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where contactNumber contains DEFAULT_CONTACT_NUMBER
        defaultDakMasterShouldBeFound("contactNumber.contains=" + DEFAULT_CONTACT_NUMBER);

        // Get all the dakMasterList where contactNumber contains UPDATED_CONTACT_NUMBER
        defaultDakMasterShouldNotBeFound("contactNumber.contains=" + UPDATED_CONTACT_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByContactNumberNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where contactNumber does not contain DEFAULT_CONTACT_NUMBER
        defaultDakMasterShouldNotBeFound("contactNumber.doesNotContain=" + DEFAULT_CONTACT_NUMBER);

        // Get all the dakMasterList where contactNumber does not contain UPDATED_CONTACT_NUMBER
        defaultDakMasterShouldBeFound("contactNumber.doesNotContain=" + UPDATED_CONTACT_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderAddress equals to DEFAULT_SENDER_ADDRESS
        defaultDakMasterShouldBeFound("senderAddress.equals=" + DEFAULT_SENDER_ADDRESS);

        // Get all the dakMasterList where senderAddress equals to UPDATED_SENDER_ADDRESS
        defaultDakMasterShouldNotBeFound("senderAddress.equals=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderAddressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderAddress not equals to DEFAULT_SENDER_ADDRESS
        defaultDakMasterShouldNotBeFound("senderAddress.notEquals=" + DEFAULT_SENDER_ADDRESS);

        // Get all the dakMasterList where senderAddress not equals to UPDATED_SENDER_ADDRESS
        defaultDakMasterShouldBeFound("senderAddress.notEquals=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderAddressIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderAddress in DEFAULT_SENDER_ADDRESS or UPDATED_SENDER_ADDRESS
        defaultDakMasterShouldBeFound("senderAddress.in=" + DEFAULT_SENDER_ADDRESS + "," + UPDATED_SENDER_ADDRESS);

        // Get all the dakMasterList where senderAddress equals to UPDATED_SENDER_ADDRESS
        defaultDakMasterShouldNotBeFound("senderAddress.in=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderAddress is not null
        defaultDakMasterShouldBeFound("senderAddress.specified=true");

        // Get all the dakMasterList where senderAddress is null
        defaultDakMasterShouldNotBeFound("senderAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderAddressContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderAddress contains DEFAULT_SENDER_ADDRESS
        defaultDakMasterShouldBeFound("senderAddress.contains=" + DEFAULT_SENDER_ADDRESS);

        // Get all the dakMasterList where senderAddress contains UPDATED_SENDER_ADDRESS
        defaultDakMasterShouldNotBeFound("senderAddress.contains=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderAddressNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderAddress does not contain DEFAULT_SENDER_ADDRESS
        defaultDakMasterShouldNotBeFound("senderAddress.doesNotContain=" + DEFAULT_SENDER_ADDRESS);

        // Get all the dakMasterList where senderAddress does not contain UPDATED_SENDER_ADDRESS
        defaultDakMasterShouldBeFound("senderAddress.doesNotContain=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderEmail equals to DEFAULT_SENDER_EMAIL
        defaultDakMasterShouldBeFound("senderEmail.equals=" + DEFAULT_SENDER_EMAIL);

        // Get all the dakMasterList where senderEmail equals to UPDATED_SENDER_EMAIL
        defaultDakMasterShouldNotBeFound("senderEmail.equals=" + UPDATED_SENDER_EMAIL);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderEmail not equals to DEFAULT_SENDER_EMAIL
        defaultDakMasterShouldNotBeFound("senderEmail.notEquals=" + DEFAULT_SENDER_EMAIL);

        // Get all the dakMasterList where senderEmail not equals to UPDATED_SENDER_EMAIL
        defaultDakMasterShouldBeFound("senderEmail.notEquals=" + UPDATED_SENDER_EMAIL);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderEmailIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderEmail in DEFAULT_SENDER_EMAIL or UPDATED_SENDER_EMAIL
        defaultDakMasterShouldBeFound("senderEmail.in=" + DEFAULT_SENDER_EMAIL + "," + UPDATED_SENDER_EMAIL);

        // Get all the dakMasterList where senderEmail equals to UPDATED_SENDER_EMAIL
        defaultDakMasterShouldNotBeFound("senderEmail.in=" + UPDATED_SENDER_EMAIL);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderEmail is not null
        defaultDakMasterShouldBeFound("senderEmail.specified=true");

        // Get all the dakMasterList where senderEmail is null
        defaultDakMasterShouldNotBeFound("senderEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderEmailContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderEmail contains DEFAULT_SENDER_EMAIL
        defaultDakMasterShouldBeFound("senderEmail.contains=" + DEFAULT_SENDER_EMAIL);

        // Get all the dakMasterList where senderEmail contains UPDATED_SENDER_EMAIL
        defaultDakMasterShouldNotBeFound("senderEmail.contains=" + UPDATED_SENDER_EMAIL);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderEmailNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderEmail does not contain DEFAULT_SENDER_EMAIL
        defaultDakMasterShouldNotBeFound("senderEmail.doesNotContain=" + DEFAULT_SENDER_EMAIL);

        // Get all the dakMasterList where senderEmail does not contain UPDATED_SENDER_EMAIL
        defaultDakMasterShouldBeFound("senderEmail.doesNotContain=" + UPDATED_SENDER_EMAIL);
    }

    @Test
    @Transactional
    void getAllDakMastersBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where subject equals to DEFAULT_SUBJECT
        defaultDakMasterShouldBeFound("subject.equals=" + DEFAULT_SUBJECT);

        // Get all the dakMasterList where subject equals to UPDATED_SUBJECT
        defaultDakMasterShouldNotBeFound("subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllDakMastersBySubjectIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where subject not equals to DEFAULT_SUBJECT
        defaultDakMasterShouldNotBeFound("subject.notEquals=" + DEFAULT_SUBJECT);

        // Get all the dakMasterList where subject not equals to UPDATED_SUBJECT
        defaultDakMasterShouldBeFound("subject.notEquals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllDakMastersBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where subject in DEFAULT_SUBJECT or UPDATED_SUBJECT
        defaultDakMasterShouldBeFound("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT);

        // Get all the dakMasterList where subject equals to UPDATED_SUBJECT
        defaultDakMasterShouldNotBeFound("subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllDakMastersBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where subject is not null
        defaultDakMasterShouldBeFound("subject.specified=true");

        // Get all the dakMasterList where subject is null
        defaultDakMasterShouldNotBeFound("subject.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersBySubjectContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where subject contains DEFAULT_SUBJECT
        defaultDakMasterShouldBeFound("subject.contains=" + DEFAULT_SUBJECT);

        // Get all the dakMasterList where subject contains UPDATED_SUBJECT
        defaultDakMasterShouldNotBeFound("subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllDakMastersBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where subject does not contain DEFAULT_SUBJECT
        defaultDakMasterShouldNotBeFound("subject.doesNotContain=" + DEFAULT_SUBJECT);

        // Get all the dakMasterList where subject does not contain UPDATED_SUBJECT
        defaultDakMasterShouldBeFound("subject.doesNotContain=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterDateIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterDate equals to DEFAULT_LETTER_DATE
        defaultDakMasterShouldBeFound("letterDate.equals=" + DEFAULT_LETTER_DATE);

        // Get all the dakMasterList where letterDate equals to UPDATED_LETTER_DATE
        defaultDakMasterShouldNotBeFound("letterDate.equals=" + UPDATED_LETTER_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterDate not equals to DEFAULT_LETTER_DATE
        defaultDakMasterShouldNotBeFound("letterDate.notEquals=" + DEFAULT_LETTER_DATE);

        // Get all the dakMasterList where letterDate not equals to UPDATED_LETTER_DATE
        defaultDakMasterShouldBeFound("letterDate.notEquals=" + UPDATED_LETTER_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterDateIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterDate in DEFAULT_LETTER_DATE or UPDATED_LETTER_DATE
        defaultDakMasterShouldBeFound("letterDate.in=" + DEFAULT_LETTER_DATE + "," + UPDATED_LETTER_DATE);

        // Get all the dakMasterList where letterDate equals to UPDATED_LETTER_DATE
        defaultDakMasterShouldNotBeFound("letterDate.in=" + UPDATED_LETTER_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterDate is not null
        defaultDakMasterShouldBeFound("letterDate.specified=true");

        // Get all the dakMasterList where letterDate is null
        defaultDakMasterShouldNotBeFound("letterDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByCurrentStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where currentStatus equals to DEFAULT_CURRENT_STATUS
        defaultDakMasterShouldBeFound("currentStatus.equals=" + DEFAULT_CURRENT_STATUS);

        // Get all the dakMasterList where currentStatus equals to UPDATED_CURRENT_STATUS
        defaultDakMasterShouldNotBeFound("currentStatus.equals=" + UPDATED_CURRENT_STATUS);
    }

    @Test
    @Transactional
    void getAllDakMastersByCurrentStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where currentStatus not equals to DEFAULT_CURRENT_STATUS
        defaultDakMasterShouldNotBeFound("currentStatus.notEquals=" + DEFAULT_CURRENT_STATUS);

        // Get all the dakMasterList where currentStatus not equals to UPDATED_CURRENT_STATUS
        defaultDakMasterShouldBeFound("currentStatus.notEquals=" + UPDATED_CURRENT_STATUS);
    }

    @Test
    @Transactional
    void getAllDakMastersByCurrentStatusIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where currentStatus in DEFAULT_CURRENT_STATUS or UPDATED_CURRENT_STATUS
        defaultDakMasterShouldBeFound("currentStatus.in=" + DEFAULT_CURRENT_STATUS + "," + UPDATED_CURRENT_STATUS);

        // Get all the dakMasterList where currentStatus equals to UPDATED_CURRENT_STATUS
        defaultDakMasterShouldNotBeFound("currentStatus.in=" + UPDATED_CURRENT_STATUS);
    }

    @Test
    @Transactional
    void getAllDakMastersByCurrentStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where currentStatus is not null
        defaultDakMasterShouldBeFound("currentStatus.specified=true");

        // Get all the dakMasterList where currentStatus is null
        defaultDakMasterShouldNotBeFound("currentStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterStatus equals to DEFAULT_LETTER_STATUS
        defaultDakMasterShouldBeFound("letterStatus.equals=" + DEFAULT_LETTER_STATUS);

        // Get all the dakMasterList where letterStatus equals to UPDATED_LETTER_STATUS
        defaultDakMasterShouldNotBeFound("letterStatus.equals=" + UPDATED_LETTER_STATUS);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterStatus not equals to DEFAULT_LETTER_STATUS
        defaultDakMasterShouldNotBeFound("letterStatus.notEquals=" + DEFAULT_LETTER_STATUS);

        // Get all the dakMasterList where letterStatus not equals to UPDATED_LETTER_STATUS
        defaultDakMasterShouldBeFound("letterStatus.notEquals=" + UPDATED_LETTER_STATUS);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterStatusIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterStatus in DEFAULT_LETTER_STATUS or UPDATED_LETTER_STATUS
        defaultDakMasterShouldBeFound("letterStatus.in=" + DEFAULT_LETTER_STATUS + "," + UPDATED_LETTER_STATUS);

        // Get all the dakMasterList where letterStatus equals to UPDATED_LETTER_STATUS
        defaultDakMasterShouldNotBeFound("letterStatus.in=" + UPDATED_LETTER_STATUS);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterStatus is not null
        defaultDakMasterShouldBeFound("letterStatus.specified=true");

        // Get all the dakMasterList where letterStatus is null
        defaultDakMasterShouldNotBeFound("letterStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterReceivedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterReceivedDate equals to DEFAULT_LETTER_RECEIVED_DATE
        defaultDakMasterShouldBeFound("letterReceivedDate.equals=" + DEFAULT_LETTER_RECEIVED_DATE);

        // Get all the dakMasterList where letterReceivedDate equals to UPDATED_LETTER_RECEIVED_DATE
        defaultDakMasterShouldNotBeFound("letterReceivedDate.equals=" + UPDATED_LETTER_RECEIVED_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterReceivedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterReceivedDate not equals to DEFAULT_LETTER_RECEIVED_DATE
        defaultDakMasterShouldNotBeFound("letterReceivedDate.notEquals=" + DEFAULT_LETTER_RECEIVED_DATE);

        // Get all the dakMasterList where letterReceivedDate not equals to UPDATED_LETTER_RECEIVED_DATE
        defaultDakMasterShouldBeFound("letterReceivedDate.notEquals=" + UPDATED_LETTER_RECEIVED_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterReceivedDateIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterReceivedDate in DEFAULT_LETTER_RECEIVED_DATE or UPDATED_LETTER_RECEIVED_DATE
        defaultDakMasterShouldBeFound("letterReceivedDate.in=" + DEFAULT_LETTER_RECEIVED_DATE + "," + UPDATED_LETTER_RECEIVED_DATE);

        // Get all the dakMasterList where letterReceivedDate equals to UPDATED_LETTER_RECEIVED_DATE
        defaultDakMasterShouldNotBeFound("letterReceivedDate.in=" + UPDATED_LETTER_RECEIVED_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterReceivedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterReceivedDate is not null
        defaultDakMasterShouldBeFound("letterReceivedDate.specified=true");

        // Get all the dakMasterList where letterReceivedDate is null
        defaultDakMasterShouldNotBeFound("letterReceivedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByAwaitReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where awaitReason equals to DEFAULT_AWAIT_REASON
        defaultDakMasterShouldBeFound("awaitReason.equals=" + DEFAULT_AWAIT_REASON);

        // Get all the dakMasterList where awaitReason equals to UPDATED_AWAIT_REASON
        defaultDakMasterShouldNotBeFound("awaitReason.equals=" + UPDATED_AWAIT_REASON);
    }

    @Test
    @Transactional
    void getAllDakMastersByAwaitReasonIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where awaitReason not equals to DEFAULT_AWAIT_REASON
        defaultDakMasterShouldNotBeFound("awaitReason.notEquals=" + DEFAULT_AWAIT_REASON);

        // Get all the dakMasterList where awaitReason not equals to UPDATED_AWAIT_REASON
        defaultDakMasterShouldBeFound("awaitReason.notEquals=" + UPDATED_AWAIT_REASON);
    }

    @Test
    @Transactional
    void getAllDakMastersByAwaitReasonIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where awaitReason in DEFAULT_AWAIT_REASON or UPDATED_AWAIT_REASON
        defaultDakMasterShouldBeFound("awaitReason.in=" + DEFAULT_AWAIT_REASON + "," + UPDATED_AWAIT_REASON);

        // Get all the dakMasterList where awaitReason equals to UPDATED_AWAIT_REASON
        defaultDakMasterShouldNotBeFound("awaitReason.in=" + UPDATED_AWAIT_REASON);
    }

    @Test
    @Transactional
    void getAllDakMastersByAwaitReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where awaitReason is not null
        defaultDakMasterShouldBeFound("awaitReason.specified=true");

        // Get all the dakMasterList where awaitReason is null
        defaultDakMasterShouldNotBeFound("awaitReason.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByAwaitReasonContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where awaitReason contains DEFAULT_AWAIT_REASON
        defaultDakMasterShouldBeFound("awaitReason.contains=" + DEFAULT_AWAIT_REASON);

        // Get all the dakMasterList where awaitReason contains UPDATED_AWAIT_REASON
        defaultDakMasterShouldNotBeFound("awaitReason.contains=" + UPDATED_AWAIT_REASON);
    }

    @Test
    @Transactional
    void getAllDakMastersByAwaitReasonNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where awaitReason does not contain DEFAULT_AWAIT_REASON
        defaultDakMasterShouldNotBeFound("awaitReason.doesNotContain=" + DEFAULT_AWAIT_REASON);

        // Get all the dakMasterList where awaitReason does not contain UPDATED_AWAIT_REASON
        defaultDakMasterShouldBeFound("awaitReason.doesNotContain=" + UPDATED_AWAIT_REASON);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchDateIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchDate equals to DEFAULT_DISPATCH_DATE
        defaultDakMasterShouldBeFound("dispatchDate.equals=" + DEFAULT_DISPATCH_DATE);

        // Get all the dakMasterList where dispatchDate equals to UPDATED_DISPATCH_DATE
        defaultDakMasterShouldNotBeFound("dispatchDate.equals=" + UPDATED_DISPATCH_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchDate not equals to DEFAULT_DISPATCH_DATE
        defaultDakMasterShouldNotBeFound("dispatchDate.notEquals=" + DEFAULT_DISPATCH_DATE);

        // Get all the dakMasterList where dispatchDate not equals to UPDATED_DISPATCH_DATE
        defaultDakMasterShouldBeFound("dispatchDate.notEquals=" + UPDATED_DISPATCH_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchDateIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchDate in DEFAULT_DISPATCH_DATE or UPDATED_DISPATCH_DATE
        defaultDakMasterShouldBeFound("dispatchDate.in=" + DEFAULT_DISPATCH_DATE + "," + UPDATED_DISPATCH_DATE);

        // Get all the dakMasterList where dispatchDate equals to UPDATED_DISPATCH_DATE
        defaultDakMasterShouldNotBeFound("dispatchDate.in=" + UPDATED_DISPATCH_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchDate is not null
        defaultDakMasterShouldBeFound("dispatchDate.specified=true");

        // Get all the dakMasterList where dispatchDate is null
        defaultDakMasterShouldNotBeFound("dispatchDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where createdBy equals to DEFAULT_CREATED_BY
        defaultDakMasterShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the dakMasterList where createdBy equals to UPDATED_CREATED_BY
        defaultDakMasterShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where createdBy not equals to DEFAULT_CREATED_BY
        defaultDakMasterShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the dakMasterList where createdBy not equals to UPDATED_CREATED_BY
        defaultDakMasterShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultDakMasterShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the dakMasterList where createdBy equals to UPDATED_CREATED_BY
        defaultDakMasterShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where createdBy is not null
        defaultDakMasterShouldBeFound("createdBy.specified=true");

        // Get all the dakMasterList where createdBy is null
        defaultDakMasterShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where createdBy contains DEFAULT_CREATED_BY
        defaultDakMasterShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the dakMasterList where createdBy contains UPDATED_CREATED_BY
        defaultDakMasterShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where createdBy does not contain DEFAULT_CREATED_BY
        defaultDakMasterShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the dakMasterList where createdBy does not contain UPDATED_CREATED_BY
        defaultDakMasterShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterType equals to DEFAULT_LETTER_TYPE
        defaultDakMasterShouldBeFound("letterType.equals=" + DEFAULT_LETTER_TYPE);

        // Get all the dakMasterList where letterType equals to UPDATED_LETTER_TYPE
        defaultDakMasterShouldNotBeFound("letterType.equals=" + UPDATED_LETTER_TYPE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterType not equals to DEFAULT_LETTER_TYPE
        defaultDakMasterShouldNotBeFound("letterType.notEquals=" + DEFAULT_LETTER_TYPE);

        // Get all the dakMasterList where letterType not equals to UPDATED_LETTER_TYPE
        defaultDakMasterShouldBeFound("letterType.notEquals=" + UPDATED_LETTER_TYPE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterTypeIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterType in DEFAULT_LETTER_TYPE or UPDATED_LETTER_TYPE
        defaultDakMasterShouldBeFound("letterType.in=" + DEFAULT_LETTER_TYPE + "," + UPDATED_LETTER_TYPE);

        // Get all the dakMasterList where letterType equals to UPDATED_LETTER_TYPE
        defaultDakMasterShouldNotBeFound("letterType.in=" + UPDATED_LETTER_TYPE);
    }

    @Test
    @Transactional
    void getAllDakMastersByLetterTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where letterType is not null
        defaultDakMasterShouldBeFound("letterType.specified=true");

        // Get all the dakMasterList where letterType is null
        defaultDakMasterShouldNotBeFound("letterType.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByIsResponseReceivedIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where isResponseReceived equals to DEFAULT_IS_RESPONSE_RECEIVED
        defaultDakMasterShouldBeFound("isResponseReceived.equals=" + DEFAULT_IS_RESPONSE_RECEIVED);

        // Get all the dakMasterList where isResponseReceived equals to UPDATED_IS_RESPONSE_RECEIVED
        defaultDakMasterShouldNotBeFound("isResponseReceived.equals=" + UPDATED_IS_RESPONSE_RECEIVED);
    }

    @Test
    @Transactional
    void getAllDakMastersByIsResponseReceivedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where isResponseReceived not equals to DEFAULT_IS_RESPONSE_RECEIVED
        defaultDakMasterShouldNotBeFound("isResponseReceived.notEquals=" + DEFAULT_IS_RESPONSE_RECEIVED);

        // Get all the dakMasterList where isResponseReceived not equals to UPDATED_IS_RESPONSE_RECEIVED
        defaultDakMasterShouldBeFound("isResponseReceived.notEquals=" + UPDATED_IS_RESPONSE_RECEIVED);
    }

    @Test
    @Transactional
    void getAllDakMastersByIsResponseReceivedIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where isResponseReceived in DEFAULT_IS_RESPONSE_RECEIVED or UPDATED_IS_RESPONSE_RECEIVED
        defaultDakMasterShouldBeFound("isResponseReceived.in=" + DEFAULT_IS_RESPONSE_RECEIVED + "," + UPDATED_IS_RESPONSE_RECEIVED);

        // Get all the dakMasterList where isResponseReceived equals to UPDATED_IS_RESPONSE_RECEIVED
        defaultDakMasterShouldNotBeFound("isResponseReceived.in=" + UPDATED_IS_RESPONSE_RECEIVED);
    }

    @Test
    @Transactional
    void getAllDakMastersByIsResponseReceivedIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where isResponseReceived is not null
        defaultDakMasterShouldBeFound("isResponseReceived.specified=true");

        // Get all the dakMasterList where isResponseReceived is null
        defaultDakMasterShouldNotBeFound("isResponseReceived.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByAssignedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where assignedDate equals to DEFAULT_ASSIGNED_DATE
        defaultDakMasterShouldBeFound("assignedDate.equals=" + DEFAULT_ASSIGNED_DATE);

        // Get all the dakMasterList where assignedDate equals to UPDATED_ASSIGNED_DATE
        defaultDakMasterShouldNotBeFound("assignedDate.equals=" + UPDATED_ASSIGNED_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByAssignedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where assignedDate not equals to DEFAULT_ASSIGNED_DATE
        defaultDakMasterShouldNotBeFound("assignedDate.notEquals=" + DEFAULT_ASSIGNED_DATE);

        // Get all the dakMasterList where assignedDate not equals to UPDATED_ASSIGNED_DATE
        defaultDakMasterShouldBeFound("assignedDate.notEquals=" + UPDATED_ASSIGNED_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByAssignedDateIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where assignedDate in DEFAULT_ASSIGNED_DATE or UPDATED_ASSIGNED_DATE
        defaultDakMasterShouldBeFound("assignedDate.in=" + DEFAULT_ASSIGNED_DATE + "," + UPDATED_ASSIGNED_DATE);

        // Get all the dakMasterList where assignedDate equals to UPDATED_ASSIGNED_DATE
        defaultDakMasterShouldNotBeFound("assignedDate.in=" + UPDATED_ASSIGNED_DATE);
    }

    @Test
    @Transactional
    void getAllDakMastersByAssignedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where assignedDate is not null
        defaultDakMasterShouldBeFound("assignedDate.specified=true");

        // Get all the dakMasterList where assignedDate is null
        defaultDakMasterShouldNotBeFound("assignedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultDakMasterShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the dakMasterList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultDakMasterShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultDakMasterShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the dakMasterList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultDakMasterShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultDakMasterShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the dakMasterList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultDakMasterShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModified is not null
        defaultDakMasterShouldBeFound("lastModified.specified=true");

        // Get all the dakMasterList where lastModified is null
        defaultDakMasterShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultDakMasterShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakMasterList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDakMasterShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultDakMasterShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakMasterList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultDakMasterShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultDakMasterShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the dakMasterList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDakMasterShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModifiedBy is not null
        defaultDakMasterShouldBeFound("lastModifiedBy.specified=true");

        // Get all the dakMasterList where lastModifiedBy is null
        defaultDakMasterShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultDakMasterShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakMasterList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultDakMasterShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultDakMasterShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the dakMasterList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultDakMasterShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssignedFromIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignedFrom equals to DEFAULT_DAK_ASSIGNED_FROM
        defaultDakMasterShouldBeFound("dakAssignedFrom.equals=" + DEFAULT_DAK_ASSIGNED_FROM);

        // Get all the dakMasterList where dakAssignedFrom equals to UPDATED_DAK_ASSIGNED_FROM
        defaultDakMasterShouldNotBeFound("dakAssignedFrom.equals=" + UPDATED_DAK_ASSIGNED_FROM);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssignedFromIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignedFrom not equals to DEFAULT_DAK_ASSIGNED_FROM
        defaultDakMasterShouldNotBeFound("dakAssignedFrom.notEquals=" + DEFAULT_DAK_ASSIGNED_FROM);

        // Get all the dakMasterList where dakAssignedFrom not equals to UPDATED_DAK_ASSIGNED_FROM
        defaultDakMasterShouldBeFound("dakAssignedFrom.notEquals=" + UPDATED_DAK_ASSIGNED_FROM);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssignedFromIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignedFrom in DEFAULT_DAK_ASSIGNED_FROM or UPDATED_DAK_ASSIGNED_FROM
        defaultDakMasterShouldBeFound("dakAssignedFrom.in=" + DEFAULT_DAK_ASSIGNED_FROM + "," + UPDATED_DAK_ASSIGNED_FROM);

        // Get all the dakMasterList where dakAssignedFrom equals to UPDATED_DAK_ASSIGNED_FROM
        defaultDakMasterShouldNotBeFound("dakAssignedFrom.in=" + UPDATED_DAK_ASSIGNED_FROM);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssignedFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignedFrom is not null
        defaultDakMasterShouldBeFound("dakAssignedFrom.specified=true");

        // Get all the dakMasterList where dakAssignedFrom is null
        defaultDakMasterShouldNotBeFound("dakAssignedFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssignedFromContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignedFrom contains DEFAULT_DAK_ASSIGNED_FROM
        defaultDakMasterShouldBeFound("dakAssignedFrom.contains=" + DEFAULT_DAK_ASSIGNED_FROM);

        // Get all the dakMasterList where dakAssignedFrom contains UPDATED_DAK_ASSIGNED_FROM
        defaultDakMasterShouldNotBeFound("dakAssignedFrom.contains=" + UPDATED_DAK_ASSIGNED_FROM);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssignedFromNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignedFrom does not contain DEFAULT_DAK_ASSIGNED_FROM
        defaultDakMasterShouldNotBeFound("dakAssignedFrom.doesNotContain=" + DEFAULT_DAK_ASSIGNED_FROM);

        // Get all the dakMasterList where dakAssignedFrom does not contain UPDATED_DAK_ASSIGNED_FROM
        defaultDakMasterShouldBeFound("dakAssignedFrom.doesNotContain=" + UPDATED_DAK_ASSIGNED_FROM);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssigneeIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignee equals to DEFAULT_DAK_ASSIGNEE
        defaultDakMasterShouldBeFound("dakAssignee.equals=" + DEFAULT_DAK_ASSIGNEE);

        // Get all the dakMasterList where dakAssignee equals to UPDATED_DAK_ASSIGNEE
        defaultDakMasterShouldNotBeFound("dakAssignee.equals=" + UPDATED_DAK_ASSIGNEE);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssigneeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignee not equals to DEFAULT_DAK_ASSIGNEE
        defaultDakMasterShouldNotBeFound("dakAssignee.notEquals=" + DEFAULT_DAK_ASSIGNEE);

        // Get all the dakMasterList where dakAssignee not equals to UPDATED_DAK_ASSIGNEE
        defaultDakMasterShouldBeFound("dakAssignee.notEquals=" + UPDATED_DAK_ASSIGNEE);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssigneeIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignee in DEFAULT_DAK_ASSIGNEE or UPDATED_DAK_ASSIGNEE
        defaultDakMasterShouldBeFound("dakAssignee.in=" + DEFAULT_DAK_ASSIGNEE + "," + UPDATED_DAK_ASSIGNEE);

        // Get all the dakMasterList where dakAssignee equals to UPDATED_DAK_ASSIGNEE
        defaultDakMasterShouldNotBeFound("dakAssignee.in=" + UPDATED_DAK_ASSIGNEE);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssigneeIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignee is not null
        defaultDakMasterShouldBeFound("dakAssignee.specified=true");

        // Get all the dakMasterList where dakAssignee is null
        defaultDakMasterShouldNotBeFound("dakAssignee.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssigneeContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignee contains DEFAULT_DAK_ASSIGNEE
        defaultDakMasterShouldBeFound("dakAssignee.contains=" + DEFAULT_DAK_ASSIGNEE);

        // Get all the dakMasterList where dakAssignee contains UPDATED_DAK_ASSIGNEE
        defaultDakMasterShouldNotBeFound("dakAssignee.contains=" + UPDATED_DAK_ASSIGNEE);
    }

    @Test
    @Transactional
    void getAllDakMastersByDakAssigneeNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dakAssignee does not contain DEFAULT_DAK_ASSIGNEE
        defaultDakMasterShouldNotBeFound("dakAssignee.doesNotContain=" + DEFAULT_DAK_ASSIGNEE);

        // Get all the dakMasterList where dakAssignee does not contain UPDATED_DAK_ASSIGNEE
        defaultDakMasterShouldBeFound("dakAssignee.doesNotContain=" + UPDATED_DAK_ASSIGNEE);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchByIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchBy equals to DEFAULT_DISPATCH_BY
        defaultDakMasterShouldBeFound("dispatchBy.equals=" + DEFAULT_DISPATCH_BY);

        // Get all the dakMasterList where dispatchBy equals to UPDATED_DISPATCH_BY
        defaultDakMasterShouldNotBeFound("dispatchBy.equals=" + UPDATED_DISPATCH_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchBy not equals to DEFAULT_DISPATCH_BY
        defaultDakMasterShouldNotBeFound("dispatchBy.notEquals=" + DEFAULT_DISPATCH_BY);

        // Get all the dakMasterList where dispatchBy not equals to UPDATED_DISPATCH_BY
        defaultDakMasterShouldBeFound("dispatchBy.notEquals=" + UPDATED_DISPATCH_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchByIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchBy in DEFAULT_DISPATCH_BY or UPDATED_DISPATCH_BY
        defaultDakMasterShouldBeFound("dispatchBy.in=" + DEFAULT_DISPATCH_BY + "," + UPDATED_DISPATCH_BY);

        // Get all the dakMasterList where dispatchBy equals to UPDATED_DISPATCH_BY
        defaultDakMasterShouldNotBeFound("dispatchBy.in=" + UPDATED_DISPATCH_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchByIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchBy is not null
        defaultDakMasterShouldBeFound("dispatchBy.specified=true");

        // Get all the dakMasterList where dispatchBy is null
        defaultDakMasterShouldNotBeFound("dispatchBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchByContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchBy contains DEFAULT_DISPATCH_BY
        defaultDakMasterShouldBeFound("dispatchBy.contains=" + DEFAULT_DISPATCH_BY);

        // Get all the dakMasterList where dispatchBy contains UPDATED_DISPATCH_BY
        defaultDakMasterShouldNotBeFound("dispatchBy.contains=" + UPDATED_DISPATCH_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersByDispatchByNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where dispatchBy does not contain DEFAULT_DISPATCH_BY
        defaultDakMasterShouldNotBeFound("dispatchBy.doesNotContain=" + DEFAULT_DISPATCH_BY);

        // Get all the dakMasterList where dispatchBy does not contain UPDATED_DISPATCH_BY
        defaultDakMasterShouldBeFound("dispatchBy.doesNotContain=" + UPDATED_DISPATCH_BY);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderOutwardIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderOutward equals to DEFAULT_SENDER_OUTWARD
        defaultDakMasterShouldBeFound("senderOutward.equals=" + DEFAULT_SENDER_OUTWARD);

        // Get all the dakMasterList where senderOutward equals to UPDATED_SENDER_OUTWARD
        defaultDakMasterShouldNotBeFound("senderOutward.equals=" + UPDATED_SENDER_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderOutwardIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderOutward not equals to DEFAULT_SENDER_OUTWARD
        defaultDakMasterShouldNotBeFound("senderOutward.notEquals=" + DEFAULT_SENDER_OUTWARD);

        // Get all the dakMasterList where senderOutward not equals to UPDATED_SENDER_OUTWARD
        defaultDakMasterShouldBeFound("senderOutward.notEquals=" + UPDATED_SENDER_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderOutwardIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderOutward in DEFAULT_SENDER_OUTWARD or UPDATED_SENDER_OUTWARD
        defaultDakMasterShouldBeFound("senderOutward.in=" + DEFAULT_SENDER_OUTWARD + "," + UPDATED_SENDER_OUTWARD);

        // Get all the dakMasterList where senderOutward equals to UPDATED_SENDER_OUTWARD
        defaultDakMasterShouldNotBeFound("senderOutward.in=" + UPDATED_SENDER_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderOutwardIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderOutward is not null
        defaultDakMasterShouldBeFound("senderOutward.specified=true");

        // Get all the dakMasterList where senderOutward is null
        defaultDakMasterShouldNotBeFound("senderOutward.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderOutwardContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderOutward contains DEFAULT_SENDER_OUTWARD
        defaultDakMasterShouldBeFound("senderOutward.contains=" + DEFAULT_SENDER_OUTWARD);

        // Get all the dakMasterList where senderOutward contains UPDATED_SENDER_OUTWARD
        defaultDakMasterShouldNotBeFound("senderOutward.contains=" + UPDATED_SENDER_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakMastersBySenderOutwardNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where senderOutward does not contain DEFAULT_SENDER_OUTWARD
        defaultDakMasterShouldNotBeFound("senderOutward.doesNotContain=" + DEFAULT_SENDER_OUTWARD);

        // Get all the dakMasterList where senderOutward does not contain UPDATED_SENDER_OUTWARD
        defaultDakMasterShouldBeFound("senderOutward.doesNotContain=" + UPDATED_SENDER_OUTWARD);
    }

    @Test
    @Transactional
    void getAllDakMastersByOutwardNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where outwardNumber equals to DEFAULT_OUTWARD_NUMBER
        defaultDakMasterShouldBeFound("outwardNumber.equals=" + DEFAULT_OUTWARD_NUMBER);

        // Get all the dakMasterList where outwardNumber equals to UPDATED_OUTWARD_NUMBER
        defaultDakMasterShouldNotBeFound("outwardNumber.equals=" + UPDATED_OUTWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByOutwardNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where outwardNumber not equals to DEFAULT_OUTWARD_NUMBER
        defaultDakMasterShouldNotBeFound("outwardNumber.notEquals=" + DEFAULT_OUTWARD_NUMBER);

        // Get all the dakMasterList where outwardNumber not equals to UPDATED_OUTWARD_NUMBER
        defaultDakMasterShouldBeFound("outwardNumber.notEquals=" + UPDATED_OUTWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByOutwardNumberIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where outwardNumber in DEFAULT_OUTWARD_NUMBER or UPDATED_OUTWARD_NUMBER
        defaultDakMasterShouldBeFound("outwardNumber.in=" + DEFAULT_OUTWARD_NUMBER + "," + UPDATED_OUTWARD_NUMBER);

        // Get all the dakMasterList where outwardNumber equals to UPDATED_OUTWARD_NUMBER
        defaultDakMasterShouldNotBeFound("outwardNumber.in=" + UPDATED_OUTWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByOutwardNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where outwardNumber is not null
        defaultDakMasterShouldBeFound("outwardNumber.specified=true");

        // Get all the dakMasterList where outwardNumber is null
        defaultDakMasterShouldNotBeFound("outwardNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByOutwardNumberContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where outwardNumber contains DEFAULT_OUTWARD_NUMBER
        defaultDakMasterShouldBeFound("outwardNumber.contains=" + DEFAULT_OUTWARD_NUMBER);

        // Get all the dakMasterList where outwardNumber contains UPDATED_OUTWARD_NUMBER
        defaultDakMasterShouldNotBeFound("outwardNumber.contains=" + UPDATED_OUTWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByOutwardNumberNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where outwardNumber does not contain DEFAULT_OUTWARD_NUMBER
        defaultDakMasterShouldNotBeFound("outwardNumber.doesNotContain=" + DEFAULT_OUTWARD_NUMBER);

        // Get all the dakMasterList where outwardNumber does not contain UPDATED_OUTWARD_NUMBER
        defaultDakMasterShouldBeFound("outwardNumber.doesNotContain=" + UPDATED_OUTWARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllDakMastersByTalukaIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where taluka equals to DEFAULT_TALUKA
        defaultDakMasterShouldBeFound("taluka.equals=" + DEFAULT_TALUKA);

        // Get all the dakMasterList where taluka equals to UPDATED_TALUKA
        defaultDakMasterShouldNotBeFound("taluka.equals=" + UPDATED_TALUKA);
    }

    @Test
    @Transactional
    void getAllDakMastersByTalukaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where taluka not equals to DEFAULT_TALUKA
        defaultDakMasterShouldNotBeFound("taluka.notEquals=" + DEFAULT_TALUKA);

        // Get all the dakMasterList where taluka not equals to UPDATED_TALUKA
        defaultDakMasterShouldBeFound("taluka.notEquals=" + UPDATED_TALUKA);
    }

    @Test
    @Transactional
    void getAllDakMastersByTalukaIsInShouldWork() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where taluka in DEFAULT_TALUKA or UPDATED_TALUKA
        defaultDakMasterShouldBeFound("taluka.in=" + DEFAULT_TALUKA + "," + UPDATED_TALUKA);

        // Get all the dakMasterList where taluka equals to UPDATED_TALUKA
        defaultDakMasterShouldNotBeFound("taluka.in=" + UPDATED_TALUKA);
    }

    @Test
    @Transactional
    void getAllDakMastersByTalukaIsNullOrNotNull() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where taluka is not null
        defaultDakMasterShouldBeFound("taluka.specified=true");

        // Get all the dakMasterList where taluka is null
        defaultDakMasterShouldNotBeFound("taluka.specified=false");
    }

    @Test
    @Transactional
    void getAllDakMastersByTalukaContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where taluka contains DEFAULT_TALUKA
        defaultDakMasterShouldBeFound("taluka.contains=" + DEFAULT_TALUKA);

        // Get all the dakMasterList where taluka contains UPDATED_TALUKA
        defaultDakMasterShouldNotBeFound("taluka.contains=" + UPDATED_TALUKA);
    }

    @Test
    @Transactional
    void getAllDakMastersByTalukaNotContainsSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        // Get all the dakMasterList where taluka does not contain DEFAULT_TALUKA
        defaultDakMasterShouldNotBeFound("taluka.doesNotContain=" + DEFAULT_TALUKA);

        // Get all the dakMasterList where taluka does not contain UPDATED_TALUKA
        defaultDakMasterShouldBeFound("taluka.doesNotContain=" + UPDATED_TALUKA);
    }

    @Test
    @Transactional
    void getAllDakMastersByOrganizationIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);
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
        dakMaster.setOrganization(organization);
        dakMasterRepository.saveAndFlush(dakMaster);
        Long organizationId = organization.getId();

        // Get all the dakMasterList where organization equals to organizationId
        defaultDakMasterShouldBeFound("organizationId.equals=" + organizationId);

        // Get all the dakMasterList where organization equals to (organizationId + 1)
        defaultDakMasterShouldNotBeFound("organizationId.equals=" + (organizationId + 1));
    }

    @Test
    @Transactional
    void getAllDakMastersBySecurityUserIsEqualToSomething() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);
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
        dakMaster.addSecurityUser(securityUser);
        dakMasterRepository.saveAndFlush(dakMaster);
        Long securityUserId = securityUser.getId();

        // Get all the dakMasterList where securityUser equals to securityUserId
        defaultDakMasterShouldBeFound("securityUserId.equals=" + securityUserId);

        // Get all the dakMasterList where securityUser equals to (securityUserId + 1)
        defaultDakMasterShouldNotBeFound("securityUserId.equals=" + (securityUserId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDakMasterShouldBeFound(String filter) throws Exception {
        restDakMasterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dakMaster.getId().intValue())))
            .andExpect(jsonPath("$.[*].inwardNumber").value(hasItem(DEFAULT_INWARD_NUMBER)))
            .andExpect(jsonPath("$.[*].senderName").value(hasItem(DEFAULT_SENDER_NAME)))
            .andExpect(jsonPath("$.[*].contactNumber").value(hasItem(DEFAULT_CONTACT_NUMBER)))
            .andExpect(jsonPath("$.[*].senderAddress").value(hasItem(DEFAULT_SENDER_ADDRESS)))
            .andExpect(jsonPath("$.[*].senderEmail").value(hasItem(DEFAULT_SENDER_EMAIL)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].letterDate").value(hasItem(DEFAULT_LETTER_DATE.toString())))
            .andExpect(jsonPath("$.[*].currentStatus").value(hasItem(DEFAULT_CURRENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].letterStatus").value(hasItem(DEFAULT_LETTER_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].letterReceivedDate").value(hasItem(DEFAULT_LETTER_RECEIVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].awaitReason").value(hasItem(DEFAULT_AWAIT_REASON)))
            .andExpect(jsonPath("$.[*].dispatchDate").value(hasItem(DEFAULT_DISPATCH_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].letterType").value(hasItem(DEFAULT_LETTER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].isResponseReceived").value(hasItem(DEFAULT_IS_RESPONSE_RECEIVED.booleanValue())))
            .andExpect(jsonPath("$.[*].assignedDate").value(hasItem(DEFAULT_ASSIGNED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].dakAssignedFrom").value(hasItem(DEFAULT_DAK_ASSIGNED_FROM)))
            .andExpect(jsonPath("$.[*].dakAssignee").value(hasItem(DEFAULT_DAK_ASSIGNEE)))
            .andExpect(jsonPath("$.[*].dispatchBy").value(hasItem(DEFAULT_DISPATCH_BY)))
            .andExpect(jsonPath("$.[*].senderOutward").value(hasItem(DEFAULT_SENDER_OUTWARD)))
            .andExpect(jsonPath("$.[*].outwardNumber").value(hasItem(DEFAULT_OUTWARD_NUMBER)))
            .andExpect(jsonPath("$.[*].taluka").value(hasItem(DEFAULT_TALUKA)));

        // Check, that the count call also returns 1
        restDakMasterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDakMasterShouldNotBeFound(String filter) throws Exception {
        restDakMasterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDakMasterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDakMaster() throws Exception {
        // Get the dakMaster
        restDakMasterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDakMaster() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();

        // Update the dakMaster
        DakMaster updatedDakMaster = dakMasterRepository.findById(dakMaster.getId()).get();
        // Disconnect from session so that the updates on updatedDakMaster are not directly saved in db
        em.detach(updatedDakMaster);
        updatedDakMaster
            .inwardNumber(UPDATED_INWARD_NUMBER)
            .senderName(UPDATED_SENDER_NAME)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .senderAddress(UPDATED_SENDER_ADDRESS)
            .senderEmail(UPDATED_SENDER_EMAIL)
            .subject(UPDATED_SUBJECT)
            .letterDate(UPDATED_LETTER_DATE)
            .currentStatus(UPDATED_CURRENT_STATUS)
            .letterStatus(UPDATED_LETTER_STATUS)
            .letterReceivedDate(UPDATED_LETTER_RECEIVED_DATE)
            .awaitReason(UPDATED_AWAIT_REASON)
            .dispatchDate(UPDATED_DISPATCH_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .letterType(UPDATED_LETTER_TYPE)
            .isResponseReceived(UPDATED_IS_RESPONSE_RECEIVED)
            .assignedDate(UPDATED_ASSIGNED_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .dakAssignedFrom(UPDATED_DAK_ASSIGNED_FROM)
            .dakAssignee(UPDATED_DAK_ASSIGNEE)
            .dispatchBy(UPDATED_DISPATCH_BY)
            .senderOutward(UPDATED_SENDER_OUTWARD)
            .outwardNumber(UPDATED_OUTWARD_NUMBER)
            .taluka(UPDATED_TALUKA);
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(updatedDakMaster);

        restDakMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dakMasterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakMasterDTO))
            )
            .andExpect(status().isOk());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
        DakMaster testDakMaster = dakMasterList.get(dakMasterList.size() - 1);
        assertThat(testDakMaster.getInwardNumber()).isEqualTo(UPDATED_INWARD_NUMBER);
        assertThat(testDakMaster.getSenderName()).isEqualTo(UPDATED_SENDER_NAME);
        assertThat(testDakMaster.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
        assertThat(testDakMaster.getSenderAddress()).isEqualTo(UPDATED_SENDER_ADDRESS);
        assertThat(testDakMaster.getSenderEmail()).isEqualTo(UPDATED_SENDER_EMAIL);
        assertThat(testDakMaster.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testDakMaster.getLetterDate()).isEqualTo(UPDATED_LETTER_DATE);
        assertThat(testDakMaster.getCurrentStatus()).isEqualTo(UPDATED_CURRENT_STATUS);
        assertThat(testDakMaster.getLetterStatus()).isEqualTo(UPDATED_LETTER_STATUS);
        assertThat(testDakMaster.getLetterReceivedDate()).isEqualTo(UPDATED_LETTER_RECEIVED_DATE);
        assertThat(testDakMaster.getAwaitReason()).isEqualTo(UPDATED_AWAIT_REASON);
        assertThat(testDakMaster.getDispatchDate()).isEqualTo(UPDATED_DISPATCH_DATE);
        assertThat(testDakMaster.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDakMaster.getLetterType()).isEqualTo(UPDATED_LETTER_TYPE);
        assertThat(testDakMaster.getIsResponseReceived()).isEqualTo(UPDATED_IS_RESPONSE_RECEIVED);
        assertThat(testDakMaster.getAssignedDate()).isEqualTo(UPDATED_ASSIGNED_DATE);
        assertThat(testDakMaster.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testDakMaster.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDakMaster.getDakAssignedFrom()).isEqualTo(UPDATED_DAK_ASSIGNED_FROM);
        assertThat(testDakMaster.getDakAssignee()).isEqualTo(UPDATED_DAK_ASSIGNEE);
        assertThat(testDakMaster.getDispatchBy()).isEqualTo(UPDATED_DISPATCH_BY);
        assertThat(testDakMaster.getSenderOutward()).isEqualTo(UPDATED_SENDER_OUTWARD);
        assertThat(testDakMaster.getOutwardNumber()).isEqualTo(UPDATED_OUTWARD_NUMBER);
        assertThat(testDakMaster.getTaluka()).isEqualTo(UPDATED_TALUKA);
    }

    @Test
    @Transactional
    void putNonExistingDakMaster() throws Exception {
        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();
        dakMaster.setId(count.incrementAndGet());

        // Create the DakMaster
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(dakMaster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDakMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dakMasterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDakMaster() throws Exception {
        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();
        dakMaster.setId(count.incrementAndGet());

        // Create the DakMaster
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(dakMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dakMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDakMaster() throws Exception {
        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();
        dakMaster.setId(count.incrementAndGet());

        // Create the DakMaster
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(dakMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakMasterMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dakMasterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDakMasterWithPatch() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();

        // Update the dakMaster using partial update
        DakMaster partialUpdatedDakMaster = new DakMaster();
        partialUpdatedDakMaster.setId(dakMaster.getId());

        partialUpdatedDakMaster
            .inwardNumber(UPDATED_INWARD_NUMBER)
            .senderName(UPDATED_SENDER_NAME)
            .subject(UPDATED_SUBJECT)
            .currentStatus(UPDATED_CURRENT_STATUS)
            .letterStatus(UPDATED_LETTER_STATUS)
            .dispatchDate(UPDATED_DISPATCH_DATE)
            .letterType(UPDATED_LETTER_TYPE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .dakAssignedFrom(UPDATED_DAK_ASSIGNED_FROM)
            .dispatchBy(UPDATED_DISPATCH_BY);

        restDakMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDakMaster.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDakMaster))
            )
            .andExpect(status().isOk());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
        DakMaster testDakMaster = dakMasterList.get(dakMasterList.size() - 1);
        assertThat(testDakMaster.getInwardNumber()).isEqualTo(UPDATED_INWARD_NUMBER);
        assertThat(testDakMaster.getSenderName()).isEqualTo(UPDATED_SENDER_NAME);
        assertThat(testDakMaster.getContactNumber()).isEqualTo(DEFAULT_CONTACT_NUMBER);
        assertThat(testDakMaster.getSenderAddress()).isEqualTo(DEFAULT_SENDER_ADDRESS);
        assertThat(testDakMaster.getSenderEmail()).isEqualTo(DEFAULT_SENDER_EMAIL);
        assertThat(testDakMaster.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testDakMaster.getLetterDate()).isEqualTo(DEFAULT_LETTER_DATE);
        assertThat(testDakMaster.getCurrentStatus()).isEqualTo(UPDATED_CURRENT_STATUS);
        assertThat(testDakMaster.getLetterStatus()).isEqualTo(UPDATED_LETTER_STATUS);
        assertThat(testDakMaster.getLetterReceivedDate()).isEqualTo(DEFAULT_LETTER_RECEIVED_DATE);
        assertThat(testDakMaster.getAwaitReason()).isEqualTo(DEFAULT_AWAIT_REASON);
        assertThat(testDakMaster.getDispatchDate()).isEqualTo(UPDATED_DISPATCH_DATE);
        assertThat(testDakMaster.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDakMaster.getLetterType()).isEqualTo(UPDATED_LETTER_TYPE);
        assertThat(testDakMaster.getIsResponseReceived()).isEqualTo(DEFAULT_IS_RESPONSE_RECEIVED);
        assertThat(testDakMaster.getAssignedDate()).isEqualTo(DEFAULT_ASSIGNED_DATE);
        assertThat(testDakMaster.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testDakMaster.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDakMaster.getDakAssignedFrom()).isEqualTo(UPDATED_DAK_ASSIGNED_FROM);
        assertThat(testDakMaster.getDakAssignee()).isEqualTo(DEFAULT_DAK_ASSIGNEE);
        assertThat(testDakMaster.getDispatchBy()).isEqualTo(UPDATED_DISPATCH_BY);
        assertThat(testDakMaster.getSenderOutward()).isEqualTo(DEFAULT_SENDER_OUTWARD);
        assertThat(testDakMaster.getOutwardNumber()).isEqualTo(DEFAULT_OUTWARD_NUMBER);
        assertThat(testDakMaster.getTaluka()).isEqualTo(DEFAULT_TALUKA);
    }

    @Test
    @Transactional
    void fullUpdateDakMasterWithPatch() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();

        // Update the dakMaster using partial update
        DakMaster partialUpdatedDakMaster = new DakMaster();
        partialUpdatedDakMaster.setId(dakMaster.getId());

        partialUpdatedDakMaster
            .inwardNumber(UPDATED_INWARD_NUMBER)
            .senderName(UPDATED_SENDER_NAME)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .senderAddress(UPDATED_SENDER_ADDRESS)
            .senderEmail(UPDATED_SENDER_EMAIL)
            .subject(UPDATED_SUBJECT)
            .letterDate(UPDATED_LETTER_DATE)
            .currentStatus(UPDATED_CURRENT_STATUS)
            .letterStatus(UPDATED_LETTER_STATUS)
            .letterReceivedDate(UPDATED_LETTER_RECEIVED_DATE)
            .awaitReason(UPDATED_AWAIT_REASON)
            .dispatchDate(UPDATED_DISPATCH_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .letterType(UPDATED_LETTER_TYPE)
            .isResponseReceived(UPDATED_IS_RESPONSE_RECEIVED)
            .assignedDate(UPDATED_ASSIGNED_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .dakAssignedFrom(UPDATED_DAK_ASSIGNED_FROM)
            .dakAssignee(UPDATED_DAK_ASSIGNEE)
            .dispatchBy(UPDATED_DISPATCH_BY)
            .senderOutward(UPDATED_SENDER_OUTWARD)
            .outwardNumber(UPDATED_OUTWARD_NUMBER)
            .taluka(UPDATED_TALUKA);

        restDakMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDakMaster.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDakMaster))
            )
            .andExpect(status().isOk());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
        DakMaster testDakMaster = dakMasterList.get(dakMasterList.size() - 1);
        assertThat(testDakMaster.getInwardNumber()).isEqualTo(UPDATED_INWARD_NUMBER);
        assertThat(testDakMaster.getSenderName()).isEqualTo(UPDATED_SENDER_NAME);
        assertThat(testDakMaster.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
        assertThat(testDakMaster.getSenderAddress()).isEqualTo(UPDATED_SENDER_ADDRESS);
        assertThat(testDakMaster.getSenderEmail()).isEqualTo(UPDATED_SENDER_EMAIL);
        assertThat(testDakMaster.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testDakMaster.getLetterDate()).isEqualTo(UPDATED_LETTER_DATE);
        assertThat(testDakMaster.getCurrentStatus()).isEqualTo(UPDATED_CURRENT_STATUS);
        assertThat(testDakMaster.getLetterStatus()).isEqualTo(UPDATED_LETTER_STATUS);
        assertThat(testDakMaster.getLetterReceivedDate()).isEqualTo(UPDATED_LETTER_RECEIVED_DATE);
        assertThat(testDakMaster.getAwaitReason()).isEqualTo(UPDATED_AWAIT_REASON);
        assertThat(testDakMaster.getDispatchDate()).isEqualTo(UPDATED_DISPATCH_DATE);
        assertThat(testDakMaster.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDakMaster.getLetterType()).isEqualTo(UPDATED_LETTER_TYPE);
        assertThat(testDakMaster.getIsResponseReceived()).isEqualTo(UPDATED_IS_RESPONSE_RECEIVED);
        assertThat(testDakMaster.getAssignedDate()).isEqualTo(UPDATED_ASSIGNED_DATE);
        assertThat(testDakMaster.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testDakMaster.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDakMaster.getDakAssignedFrom()).isEqualTo(UPDATED_DAK_ASSIGNED_FROM);
        assertThat(testDakMaster.getDakAssignee()).isEqualTo(UPDATED_DAK_ASSIGNEE);
        assertThat(testDakMaster.getDispatchBy()).isEqualTo(UPDATED_DISPATCH_BY);
        assertThat(testDakMaster.getSenderOutward()).isEqualTo(UPDATED_SENDER_OUTWARD);
        assertThat(testDakMaster.getOutwardNumber()).isEqualTo(UPDATED_OUTWARD_NUMBER);
        assertThat(testDakMaster.getTaluka()).isEqualTo(UPDATED_TALUKA);
    }

    @Test
    @Transactional
    void patchNonExistingDakMaster() throws Exception {
        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();
        dakMaster.setId(count.incrementAndGet());

        // Create the DakMaster
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(dakMaster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDakMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dakMasterDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDakMaster() throws Exception {
        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();
        dakMaster.setId(count.incrementAndGet());

        // Create the DakMaster
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(dakMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dakMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDakMaster() throws Exception {
        int databaseSizeBeforeUpdate = dakMasterRepository.findAll().size();
        dakMaster.setId(count.incrementAndGet());

        // Create the DakMaster
        DakMasterDTO dakMasterDTO = dakMasterMapper.toDto(dakMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDakMasterMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dakMasterDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DakMaster in the database
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDakMaster() throws Exception {
        // Initialize the database
        dakMasterRepository.saveAndFlush(dakMaster);

        int databaseSizeBeforeDelete = dakMasterRepository.findAll().size();

        // Delete the dakMaster
        restDakMasterMockMvc
            .perform(delete(ENTITY_API_URL_ID, dakMaster.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DakMaster> dakMasterList = dakMasterRepository.findAll();
        assertThat(dakMasterList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
