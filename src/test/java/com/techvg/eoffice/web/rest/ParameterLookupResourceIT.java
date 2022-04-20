package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.Organization;
import com.techvg.eoffice.domain.ParameterLookup;
import com.techvg.eoffice.repository.ParameterLookupRepository;
import com.techvg.eoffice.service.criteria.ParameterLookupCriteria;
import com.techvg.eoffice.service.dto.ParameterLookupDTO;
import com.techvg.eoffice.service.mapper.ParameterLookupMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ParameterLookupResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParameterLookupResourceIT {

    private static final String DEFAULT_PARAMETER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PARAMETER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PARAMETER_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_PARAMETER_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/parameter-lookups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParameterLookupRepository parameterLookupRepository;

    @Autowired
    private ParameterLookupMapper parameterLookupMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParameterLookupMockMvc;

    private ParameterLookup parameterLookup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParameterLookup createEntity(EntityManager em) {
        ParameterLookup parameterLookup = new ParameterLookup()
            .parameterName(DEFAULT_PARAMETER_NAME)
            .parameterValue(DEFAULT_PARAMETER_VALUE)
            .type(DEFAULT_TYPE)
            .status(DEFAULT_STATUS)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .createdBy(DEFAULT_CREATED_BY)
            .createdOn(DEFAULT_CREATED_ON);
        return parameterLookup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParameterLookup createUpdatedEntity(EntityManager em) {
        ParameterLookup parameterLookup = new ParameterLookup()
            .parameterName(UPDATED_PARAMETER_NAME)
            .parameterValue(UPDATED_PARAMETER_VALUE)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON);
        return parameterLookup;
    }

    @BeforeEach
    public void initTest() {
        parameterLookup = createEntity(em);
    }

    @Test
    @Transactional
    void createParameterLookup() throws Exception {
        int databaseSizeBeforeCreate = parameterLookupRepository.findAll().size();
        // Create the ParameterLookup
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);
        restParameterLookupMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeCreate + 1);
        ParameterLookup testParameterLookup = parameterLookupList.get(parameterLookupList.size() - 1);
        assertThat(testParameterLookup.getParameterName()).isEqualTo(DEFAULT_PARAMETER_NAME);
        assertThat(testParameterLookup.getParameterValue()).isEqualTo(DEFAULT_PARAMETER_VALUE);
        assertThat(testParameterLookup.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testParameterLookup.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testParameterLookup.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testParameterLookup.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testParameterLookup.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testParameterLookup.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
    }

    @Test
    @Transactional
    void createParameterLookupWithExistingId() throws Exception {
        // Create the ParameterLookup with an existing ID
        parameterLookup.setId(1L);
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        int databaseSizeBeforeCreate = parameterLookupRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParameterLookupMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkParameterNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = parameterLookupRepository.findAll().size();
        // set the field null
        parameterLookup.setParameterName(null);

        // Create the ParameterLookup, which fails.
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        restParameterLookupMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isBadRequest());

        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkParameterValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = parameterLookupRepository.findAll().size();
        // set the field null
        parameterLookup.setParameterValue(null);

        // Create the ParameterLookup, which fails.
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        restParameterLookupMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isBadRequest());

        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = parameterLookupRepository.findAll().size();
        // set the field null
        parameterLookup.setType(null);

        // Create the ParameterLookup, which fails.
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        restParameterLookupMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isBadRequest());

        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParameterLookups() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList
        restParameterLookupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parameterLookup.getId().intValue())))
            .andExpect(jsonPath("$.[*].parameterName").value(hasItem(DEFAULT_PARAMETER_NAME)))
            .andExpect(jsonPath("$.[*].parameterValue").value(hasItem(DEFAULT_PARAMETER_VALUE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())));
    }

    @Test
    @Transactional
    void getParameterLookup() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get the parameterLookup
        restParameterLookupMockMvc
            .perform(get(ENTITY_API_URL_ID, parameterLookup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parameterLookup.getId().intValue()))
            .andExpect(jsonPath("$.parameterName").value(DEFAULT_PARAMETER_NAME))
            .andExpect(jsonPath("$.parameterValue").value(DEFAULT_PARAMETER_VALUE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdOn").value(DEFAULT_CREATED_ON.toString()));
    }

    @Test
    @Transactional
    void getParameterLookupsByIdFiltering() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        Long id = parameterLookup.getId();

        defaultParameterLookupShouldBeFound("id.equals=" + id);
        defaultParameterLookupShouldNotBeFound("id.notEquals=" + id);

        defaultParameterLookupShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultParameterLookupShouldNotBeFound("id.greaterThan=" + id);

        defaultParameterLookupShouldBeFound("id.lessThanOrEqual=" + id);
        defaultParameterLookupShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterNameIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterName equals to DEFAULT_PARAMETER_NAME
        defaultParameterLookupShouldBeFound("parameterName.equals=" + DEFAULT_PARAMETER_NAME);

        // Get all the parameterLookupList where parameterName equals to UPDATED_PARAMETER_NAME
        defaultParameterLookupShouldNotBeFound("parameterName.equals=" + UPDATED_PARAMETER_NAME);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterName not equals to DEFAULT_PARAMETER_NAME
        defaultParameterLookupShouldNotBeFound("parameterName.notEquals=" + DEFAULT_PARAMETER_NAME);

        // Get all the parameterLookupList where parameterName not equals to UPDATED_PARAMETER_NAME
        defaultParameterLookupShouldBeFound("parameterName.notEquals=" + UPDATED_PARAMETER_NAME);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterNameIsInShouldWork() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterName in DEFAULT_PARAMETER_NAME or UPDATED_PARAMETER_NAME
        defaultParameterLookupShouldBeFound("parameterName.in=" + DEFAULT_PARAMETER_NAME + "," + UPDATED_PARAMETER_NAME);

        // Get all the parameterLookupList where parameterName equals to UPDATED_PARAMETER_NAME
        defaultParameterLookupShouldNotBeFound("parameterName.in=" + UPDATED_PARAMETER_NAME);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterName is not null
        defaultParameterLookupShouldBeFound("parameterName.specified=true");

        // Get all the parameterLookupList where parameterName is null
        defaultParameterLookupShouldNotBeFound("parameterName.specified=false");
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterNameContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterName contains DEFAULT_PARAMETER_NAME
        defaultParameterLookupShouldBeFound("parameterName.contains=" + DEFAULT_PARAMETER_NAME);

        // Get all the parameterLookupList where parameterName contains UPDATED_PARAMETER_NAME
        defaultParameterLookupShouldNotBeFound("parameterName.contains=" + UPDATED_PARAMETER_NAME);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterNameNotContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterName does not contain DEFAULT_PARAMETER_NAME
        defaultParameterLookupShouldNotBeFound("parameterName.doesNotContain=" + DEFAULT_PARAMETER_NAME);

        // Get all the parameterLookupList where parameterName does not contain UPDATED_PARAMETER_NAME
        defaultParameterLookupShouldBeFound("parameterName.doesNotContain=" + UPDATED_PARAMETER_NAME);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterValueIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterValue equals to DEFAULT_PARAMETER_VALUE
        defaultParameterLookupShouldBeFound("parameterValue.equals=" + DEFAULT_PARAMETER_VALUE);

        // Get all the parameterLookupList where parameterValue equals to UPDATED_PARAMETER_VALUE
        defaultParameterLookupShouldNotBeFound("parameterValue.equals=" + UPDATED_PARAMETER_VALUE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterValue not equals to DEFAULT_PARAMETER_VALUE
        defaultParameterLookupShouldNotBeFound("parameterValue.notEquals=" + DEFAULT_PARAMETER_VALUE);

        // Get all the parameterLookupList where parameterValue not equals to UPDATED_PARAMETER_VALUE
        defaultParameterLookupShouldBeFound("parameterValue.notEquals=" + UPDATED_PARAMETER_VALUE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterValueIsInShouldWork() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterValue in DEFAULT_PARAMETER_VALUE or UPDATED_PARAMETER_VALUE
        defaultParameterLookupShouldBeFound("parameterValue.in=" + DEFAULT_PARAMETER_VALUE + "," + UPDATED_PARAMETER_VALUE);

        // Get all the parameterLookupList where parameterValue equals to UPDATED_PARAMETER_VALUE
        defaultParameterLookupShouldNotBeFound("parameterValue.in=" + UPDATED_PARAMETER_VALUE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterValue is not null
        defaultParameterLookupShouldBeFound("parameterValue.specified=true");

        // Get all the parameterLookupList where parameterValue is null
        defaultParameterLookupShouldNotBeFound("parameterValue.specified=false");
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterValueContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterValue contains DEFAULT_PARAMETER_VALUE
        defaultParameterLookupShouldBeFound("parameterValue.contains=" + DEFAULT_PARAMETER_VALUE);

        // Get all the parameterLookupList where parameterValue contains UPDATED_PARAMETER_VALUE
        defaultParameterLookupShouldNotBeFound("parameterValue.contains=" + UPDATED_PARAMETER_VALUE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByParameterValueNotContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where parameterValue does not contain DEFAULT_PARAMETER_VALUE
        defaultParameterLookupShouldNotBeFound("parameterValue.doesNotContain=" + DEFAULT_PARAMETER_VALUE);

        // Get all the parameterLookupList where parameterValue does not contain UPDATED_PARAMETER_VALUE
        defaultParameterLookupShouldBeFound("parameterValue.doesNotContain=" + UPDATED_PARAMETER_VALUE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where type equals to DEFAULT_TYPE
        defaultParameterLookupShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the parameterLookupList where type equals to UPDATED_TYPE
        defaultParameterLookupShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where type not equals to DEFAULT_TYPE
        defaultParameterLookupShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the parameterLookupList where type not equals to UPDATED_TYPE
        defaultParameterLookupShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultParameterLookupShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the parameterLookupList where type equals to UPDATED_TYPE
        defaultParameterLookupShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where type is not null
        defaultParameterLookupShouldBeFound("type.specified=true");

        // Get all the parameterLookupList where type is null
        defaultParameterLookupShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllParameterLookupsByTypeContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where type contains DEFAULT_TYPE
        defaultParameterLookupShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the parameterLookupList where type contains UPDATED_TYPE
        defaultParameterLookupShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where type does not contain DEFAULT_TYPE
        defaultParameterLookupShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the parameterLookupList where type does not contain UPDATED_TYPE
        defaultParameterLookupShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where status equals to DEFAULT_STATUS
        defaultParameterLookupShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the parameterLookupList where status equals to UPDATED_STATUS
        defaultParameterLookupShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where status not equals to DEFAULT_STATUS
        defaultParameterLookupShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the parameterLookupList where status not equals to UPDATED_STATUS
        defaultParameterLookupShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultParameterLookupShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the parameterLookupList where status equals to UPDATED_STATUS
        defaultParameterLookupShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where status is not null
        defaultParameterLookupShouldBeFound("status.specified=true");

        // Get all the parameterLookupList where status is null
        defaultParameterLookupShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllParameterLookupsByStatusContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where status contains DEFAULT_STATUS
        defaultParameterLookupShouldBeFound("status.contains=" + DEFAULT_STATUS);

        // Get all the parameterLookupList where status contains UPDATED_STATUS
        defaultParameterLookupShouldNotBeFound("status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where status does not contain DEFAULT_STATUS
        defaultParameterLookupShouldNotBeFound("status.doesNotContain=" + DEFAULT_STATUS);

        // Get all the parameterLookupList where status does not contain UPDATED_STATUS
        defaultParameterLookupShouldBeFound("status.doesNotContain=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultParameterLookupShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the parameterLookupList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultParameterLookupShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultParameterLookupShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the parameterLookupList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultParameterLookupShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultParameterLookupShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the parameterLookupList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultParameterLookupShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModified is not null
        defaultParameterLookupShouldBeFound("lastModified.specified=true");

        // Get all the parameterLookupList where lastModified is null
        defaultParameterLookupShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultParameterLookupShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the parameterLookupList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultParameterLookupShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultParameterLookupShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the parameterLookupList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultParameterLookupShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultParameterLookupShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the parameterLookupList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultParameterLookupShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModifiedBy is not null
        defaultParameterLookupShouldBeFound("lastModifiedBy.specified=true");

        // Get all the parameterLookupList where lastModifiedBy is null
        defaultParameterLookupShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultParameterLookupShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the parameterLookupList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultParameterLookupShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultParameterLookupShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the parameterLookupList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultParameterLookupShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdBy equals to DEFAULT_CREATED_BY
        defaultParameterLookupShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the parameterLookupList where createdBy equals to UPDATED_CREATED_BY
        defaultParameterLookupShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdBy not equals to DEFAULT_CREATED_BY
        defaultParameterLookupShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the parameterLookupList where createdBy not equals to UPDATED_CREATED_BY
        defaultParameterLookupShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultParameterLookupShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the parameterLookupList where createdBy equals to UPDATED_CREATED_BY
        defaultParameterLookupShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdBy is not null
        defaultParameterLookupShouldBeFound("createdBy.specified=true");

        // Get all the parameterLookupList where createdBy is null
        defaultParameterLookupShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdBy contains DEFAULT_CREATED_BY
        defaultParameterLookupShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the parameterLookupList where createdBy contains UPDATED_CREATED_BY
        defaultParameterLookupShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdBy does not contain DEFAULT_CREATED_BY
        defaultParameterLookupShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the parameterLookupList where createdBy does not contain UPDATED_CREATED_BY
        defaultParameterLookupShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdOn equals to DEFAULT_CREATED_ON
        defaultParameterLookupShouldBeFound("createdOn.equals=" + DEFAULT_CREATED_ON);

        // Get all the parameterLookupList where createdOn equals to UPDATED_CREATED_ON
        defaultParameterLookupShouldNotBeFound("createdOn.equals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedOnIsNotEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdOn not equals to DEFAULT_CREATED_ON
        defaultParameterLookupShouldNotBeFound("createdOn.notEquals=" + DEFAULT_CREATED_ON);

        // Get all the parameterLookupList where createdOn not equals to UPDATED_CREATED_ON
        defaultParameterLookupShouldBeFound("createdOn.notEquals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedOnIsInShouldWork() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdOn in DEFAULT_CREATED_ON or UPDATED_CREATED_ON
        defaultParameterLookupShouldBeFound("createdOn.in=" + DEFAULT_CREATED_ON + "," + UPDATED_CREATED_ON);

        // Get all the parameterLookupList where createdOn equals to UPDATED_CREATED_ON
        defaultParameterLookupShouldNotBeFound("createdOn.in=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllParameterLookupsByCreatedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        // Get all the parameterLookupList where createdOn is not null
        defaultParameterLookupShouldBeFound("createdOn.specified=true");

        // Get all the parameterLookupList where createdOn is null
        defaultParameterLookupShouldNotBeFound("createdOn.specified=false");
    }

    @Test
    @Transactional
    void getAllParameterLookupsByOrganizationIsEqualToSomething() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);
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
        parameterLookup.setOrganization(organization);
        parameterLookupRepository.saveAndFlush(parameterLookup);
        Long organizationId = organization.getId();

        // Get all the parameterLookupList where organization equals to organizationId
        defaultParameterLookupShouldBeFound("organizationId.equals=" + organizationId);

        // Get all the parameterLookupList where organization equals to (organizationId + 1)
        defaultParameterLookupShouldNotBeFound("organizationId.equals=" + (organizationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParameterLookupShouldBeFound(String filter) throws Exception {
        restParameterLookupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parameterLookup.getId().intValue())))
            .andExpect(jsonPath("$.[*].parameterName").value(hasItem(DEFAULT_PARAMETER_NAME)))
            .andExpect(jsonPath("$.[*].parameterValue").value(hasItem(DEFAULT_PARAMETER_VALUE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())));

        // Check, that the count call also returns 1
        restParameterLookupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParameterLookupShouldNotBeFound(String filter) throws Exception {
        restParameterLookupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParameterLookupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingParameterLookup() throws Exception {
        // Get the parameterLookup
        restParameterLookupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewParameterLookup() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();

        // Update the parameterLookup
        ParameterLookup updatedParameterLookup = parameterLookupRepository.findById(parameterLookup.getId()).get();
        // Disconnect from session so that the updates on updatedParameterLookup are not directly saved in db
        em.detach(updatedParameterLookup);
        updatedParameterLookup
            .parameterName(UPDATED_PARAMETER_NAME)
            .parameterValue(UPDATED_PARAMETER_VALUE)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON);
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(updatedParameterLookup);

        restParameterLookupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parameterLookupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
        ParameterLookup testParameterLookup = parameterLookupList.get(parameterLookupList.size() - 1);
        assertThat(testParameterLookup.getParameterName()).isEqualTo(UPDATED_PARAMETER_NAME);
        assertThat(testParameterLookup.getParameterValue()).isEqualTo(UPDATED_PARAMETER_VALUE);
        assertThat(testParameterLookup.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testParameterLookup.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testParameterLookup.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testParameterLookup.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testParameterLookup.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testParameterLookup.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void putNonExistingParameterLookup() throws Exception {
        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();
        parameterLookup.setId(count.incrementAndGet());

        // Create the ParameterLookup
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParameterLookupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parameterLookupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParameterLookup() throws Exception {
        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();
        parameterLookup.setId(count.incrementAndGet());

        // Create the ParameterLookup
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterLookupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParameterLookup() throws Exception {
        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();
        parameterLookup.setId(count.incrementAndGet());

        // Create the ParameterLookup
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterLookupMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParameterLookupWithPatch() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();

        // Update the parameterLookup using partial update
        ParameterLookup partialUpdatedParameterLookup = new ParameterLookup();
        partialUpdatedParameterLookup.setId(parameterLookup.getId());

        partialUpdatedParameterLookup.status(UPDATED_STATUS).lastModified(UPDATED_LAST_MODIFIED);

        restParameterLookupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParameterLookup.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParameterLookup))
            )
            .andExpect(status().isOk());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
        ParameterLookup testParameterLookup = parameterLookupList.get(parameterLookupList.size() - 1);
        assertThat(testParameterLookup.getParameterName()).isEqualTo(DEFAULT_PARAMETER_NAME);
        assertThat(testParameterLookup.getParameterValue()).isEqualTo(DEFAULT_PARAMETER_VALUE);
        assertThat(testParameterLookup.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testParameterLookup.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testParameterLookup.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testParameterLookup.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testParameterLookup.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testParameterLookup.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
    }

    @Test
    @Transactional
    void fullUpdateParameterLookupWithPatch() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();

        // Update the parameterLookup using partial update
        ParameterLookup partialUpdatedParameterLookup = new ParameterLookup();
        partialUpdatedParameterLookup.setId(parameterLookup.getId());

        partialUpdatedParameterLookup
            .parameterName(UPDATED_PARAMETER_NAME)
            .parameterValue(UPDATED_PARAMETER_VALUE)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON);

        restParameterLookupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParameterLookup.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParameterLookup))
            )
            .andExpect(status().isOk());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
        ParameterLookup testParameterLookup = parameterLookupList.get(parameterLookupList.size() - 1);
        assertThat(testParameterLookup.getParameterName()).isEqualTo(UPDATED_PARAMETER_NAME);
        assertThat(testParameterLookup.getParameterValue()).isEqualTo(UPDATED_PARAMETER_VALUE);
        assertThat(testParameterLookup.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testParameterLookup.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testParameterLookup.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testParameterLookup.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testParameterLookup.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testParameterLookup.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void patchNonExistingParameterLookup() throws Exception {
        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();
        parameterLookup.setId(count.incrementAndGet());

        // Create the ParameterLookup
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParameterLookupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parameterLookupDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParameterLookup() throws Exception {
        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();
        parameterLookup.setId(count.incrementAndGet());

        // Create the ParameterLookup
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterLookupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParameterLookup() throws Exception {
        int databaseSizeBeforeUpdate = parameterLookupRepository.findAll().size();
        parameterLookup.setId(count.incrementAndGet());

        // Create the ParameterLookup
        ParameterLookupDTO parameterLookupDTO = parameterLookupMapper.toDto(parameterLookup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterLookupMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parameterLookupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParameterLookup in the database
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParameterLookup() throws Exception {
        // Initialize the database
        parameterLookupRepository.saveAndFlush(parameterLookup);

        int databaseSizeBeforeDelete = parameterLookupRepository.findAll().size();

        // Delete the parameterLookup
        restParameterLookupMockMvc
            .perform(delete(ENTITY_API_URL_ID, parameterLookup.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ParameterLookup> parameterLookupList = parameterLookupRepository.findAll();
        assertThat(parameterLookupList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
