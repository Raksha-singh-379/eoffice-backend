package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.Organization;
import com.techvg.eoffice.domain.enumeration.OrganizationType;
import com.techvg.eoffice.repository.OrganizationRepository;
import com.techvg.eoffice.service.criteria.OrganizationCriteria;
import com.techvg.eoffice.service.dto.OrganizationDTO;
import com.techvg.eoffice.service.mapper.OrganizationMapper;
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
 * Integration tests for the {@link OrganizationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrganizationResourceIT {

    private static final String DEFAULT_ORGANIZATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORGANIZATION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_ON = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_ON = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVATE = false;
    private static final Boolean UPDATED_IS_ACTIVATE = true;

    private static final OrganizationType DEFAULT_ORGNIZATION_TYPE = OrganizationType.AR;
    private static final OrganizationType UPDATED_ORGNIZATION_TYPE = OrganizationType.DDR;

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/organizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrganizationMockMvc;

    private Organization organization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createEntity(EntityManager em) {
        Organization organization = new Organization()
            .organizationName(DEFAULT_ORGANIZATION_NAME)
            .address(DEFAULT_ADDRESS)
            .createdOn(DEFAULT_CREATED_ON)
            .description(DEFAULT_DESCRIPTION)
            .isActivate(DEFAULT_IS_ACTIVATE)
            .orgnizationType(DEFAULT_ORGNIZATION_TYPE)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        return organization;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createUpdatedEntity(EntityManager em) {
        Organization organization = new Organization()
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .address(UPDATED_ADDRESS)
            .createdOn(UPDATED_CREATED_ON)
            .description(UPDATED_DESCRIPTION)
            .isActivate(UPDATED_IS_ACTIVATE)
            .orgnizationType(UPDATED_ORGNIZATION_TYPE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        return organization;
    }

    @BeforeEach
    public void initTest() {
        organization = createEntity(em);
    }

    @Test
    @Transactional
    void createOrganization() throws Exception {
        int databaseSizeBeforeCreate = organizationRepository.findAll().size();
        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);
        restOrganizationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate + 1);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(DEFAULT_ORGANIZATION_NAME);
        assertThat(testOrganization.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testOrganization.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testOrganization.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOrganization.getIsActivate()).isEqualTo(DEFAULT_IS_ACTIVATE);
        assertThat(testOrganization.getOrgnizationType()).isEqualTo(DEFAULT_ORGNIZATION_TYPE);
        assertThat(testOrganization.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testOrganization.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createOrganizationWithExistingId() throws Exception {
        // Create the Organization with an existing ID
        organization.setId(1L);
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        int databaseSizeBeforeCreate = organizationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganizationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrganizationNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().size();
        // set the field null
        organization.setOrganizationName(null);

        // Create the Organization, which fails.
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        restOrganizationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrgnizationTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().size();
        // set the field null
        organization.setOrgnizationType(null);

        // Create the Organization, which fails.
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        restOrganizationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrganizations() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList
        restOrganizationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organization.getId().intValue())))
            .andExpect(jsonPath("$.[*].organizationName").value(hasItem(DEFAULT_ORGANIZATION_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isActivate").value(hasItem(DEFAULT_IS_ACTIVATE.booleanValue())))
            .andExpect(jsonPath("$.[*].orgnizationType").value(hasItem(DEFAULT_ORGNIZATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));
    }

    @Test
    @Transactional
    void getOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get the organization
        restOrganizationMockMvc
            .perform(get(ENTITY_API_URL_ID, organization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(organization.getId().intValue()))
            .andExpect(jsonPath("$.organizationName").value(DEFAULT_ORGANIZATION_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.createdOn").value(DEFAULT_CREATED_ON))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isActivate").value(DEFAULT_IS_ACTIVATE.booleanValue()))
            .andExpect(jsonPath("$.orgnizationType").value(DEFAULT_ORGNIZATION_TYPE.toString()))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getOrganizationsByIdFiltering() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        Long id = organization.getId();

        defaultOrganizationShouldBeFound("id.equals=" + id);
        defaultOrganizationShouldNotBeFound("id.notEquals=" + id);

        defaultOrganizationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrganizationShouldNotBeFound("id.greaterThan=" + id);

        defaultOrganizationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrganizationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrganizationNameIsEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where organizationName equals to DEFAULT_ORGANIZATION_NAME
        defaultOrganizationShouldBeFound("organizationName.equals=" + DEFAULT_ORGANIZATION_NAME);

        // Get all the organizationList where organizationName equals to UPDATED_ORGANIZATION_NAME
        defaultOrganizationShouldNotBeFound("organizationName.equals=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrganizationNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where organizationName not equals to DEFAULT_ORGANIZATION_NAME
        defaultOrganizationShouldNotBeFound("organizationName.notEquals=" + DEFAULT_ORGANIZATION_NAME);

        // Get all the organizationList where organizationName not equals to UPDATED_ORGANIZATION_NAME
        defaultOrganizationShouldBeFound("organizationName.notEquals=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrganizationNameIsInShouldWork() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where organizationName in DEFAULT_ORGANIZATION_NAME or UPDATED_ORGANIZATION_NAME
        defaultOrganizationShouldBeFound("organizationName.in=" + DEFAULT_ORGANIZATION_NAME + "," + UPDATED_ORGANIZATION_NAME);

        // Get all the organizationList where organizationName equals to UPDATED_ORGANIZATION_NAME
        defaultOrganizationShouldNotBeFound("organizationName.in=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrganizationNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where organizationName is not null
        defaultOrganizationShouldBeFound("organizationName.specified=true");

        // Get all the organizationList where organizationName is null
        defaultOrganizationShouldNotBeFound("organizationName.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrganizationNameContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where organizationName contains DEFAULT_ORGANIZATION_NAME
        defaultOrganizationShouldBeFound("organizationName.contains=" + DEFAULT_ORGANIZATION_NAME);

        // Get all the organizationList where organizationName contains UPDATED_ORGANIZATION_NAME
        defaultOrganizationShouldNotBeFound("organizationName.contains=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrganizationNameNotContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where organizationName does not contain DEFAULT_ORGANIZATION_NAME
        defaultOrganizationShouldNotBeFound("organizationName.doesNotContain=" + DEFAULT_ORGANIZATION_NAME);

        // Get all the organizationList where organizationName does not contain UPDATED_ORGANIZATION_NAME
        defaultOrganizationShouldBeFound("organizationName.doesNotContain=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllOrganizationsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where address equals to DEFAULT_ADDRESS
        defaultOrganizationShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the organizationList where address equals to UPDATED_ADDRESS
        defaultOrganizationShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllOrganizationsByAddressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where address not equals to DEFAULT_ADDRESS
        defaultOrganizationShouldNotBeFound("address.notEquals=" + DEFAULT_ADDRESS);

        // Get all the organizationList where address not equals to UPDATED_ADDRESS
        defaultOrganizationShouldBeFound("address.notEquals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllOrganizationsByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultOrganizationShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the organizationList where address equals to UPDATED_ADDRESS
        defaultOrganizationShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllOrganizationsByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where address is not null
        defaultOrganizationShouldBeFound("address.specified=true");

        // Get all the organizationList where address is null
        defaultOrganizationShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizationsByAddressContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where address contains DEFAULT_ADDRESS
        defaultOrganizationShouldBeFound("address.contains=" + DEFAULT_ADDRESS);

        // Get all the organizationList where address contains UPDATED_ADDRESS
        defaultOrganizationShouldNotBeFound("address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllOrganizationsByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where address does not contain DEFAULT_ADDRESS
        defaultOrganizationShouldNotBeFound("address.doesNotContain=" + DEFAULT_ADDRESS);

        // Get all the organizationList where address does not contain UPDATED_ADDRESS
        defaultOrganizationShouldBeFound("address.doesNotContain=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllOrganizationsByCreatedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where createdOn equals to DEFAULT_CREATED_ON
        defaultOrganizationShouldBeFound("createdOn.equals=" + DEFAULT_CREATED_ON);

        // Get all the organizationList where createdOn equals to UPDATED_CREATED_ON
        defaultOrganizationShouldNotBeFound("createdOn.equals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllOrganizationsByCreatedOnIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where createdOn not equals to DEFAULT_CREATED_ON
        defaultOrganizationShouldNotBeFound("createdOn.notEquals=" + DEFAULT_CREATED_ON);

        // Get all the organizationList where createdOn not equals to UPDATED_CREATED_ON
        defaultOrganizationShouldBeFound("createdOn.notEquals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllOrganizationsByCreatedOnIsInShouldWork() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where createdOn in DEFAULT_CREATED_ON or UPDATED_CREATED_ON
        defaultOrganizationShouldBeFound("createdOn.in=" + DEFAULT_CREATED_ON + "," + UPDATED_CREATED_ON);

        // Get all the organizationList where createdOn equals to UPDATED_CREATED_ON
        defaultOrganizationShouldNotBeFound("createdOn.in=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllOrganizationsByCreatedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where createdOn is not null
        defaultOrganizationShouldBeFound("createdOn.specified=true");

        // Get all the organizationList where createdOn is null
        defaultOrganizationShouldNotBeFound("createdOn.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizationsByCreatedOnContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where createdOn contains DEFAULT_CREATED_ON
        defaultOrganizationShouldBeFound("createdOn.contains=" + DEFAULT_CREATED_ON);

        // Get all the organizationList where createdOn contains UPDATED_CREATED_ON
        defaultOrganizationShouldNotBeFound("createdOn.contains=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllOrganizationsByCreatedOnNotContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where createdOn does not contain DEFAULT_CREATED_ON
        defaultOrganizationShouldNotBeFound("createdOn.doesNotContain=" + DEFAULT_CREATED_ON);

        // Get all the organizationList where createdOn does not contain UPDATED_CREATED_ON
        defaultOrganizationShouldBeFound("createdOn.doesNotContain=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllOrganizationsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where description equals to DEFAULT_DESCRIPTION
        defaultOrganizationShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the organizationList where description equals to UPDATED_DESCRIPTION
        defaultOrganizationShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOrganizationsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where description not equals to DEFAULT_DESCRIPTION
        defaultOrganizationShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the organizationList where description not equals to UPDATED_DESCRIPTION
        defaultOrganizationShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOrganizationsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultOrganizationShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the organizationList where description equals to UPDATED_DESCRIPTION
        defaultOrganizationShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOrganizationsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where description is not null
        defaultOrganizationShouldBeFound("description.specified=true");

        // Get all the organizationList where description is null
        defaultOrganizationShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizationsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where description contains DEFAULT_DESCRIPTION
        defaultOrganizationShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the organizationList where description contains UPDATED_DESCRIPTION
        defaultOrganizationShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOrganizationsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where description does not contain DEFAULT_DESCRIPTION
        defaultOrganizationShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the organizationList where description does not contain UPDATED_DESCRIPTION
        defaultOrganizationShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOrganizationsByIsActivateIsEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where isActivate equals to DEFAULT_IS_ACTIVATE
        defaultOrganizationShouldBeFound("isActivate.equals=" + DEFAULT_IS_ACTIVATE);

        // Get all the organizationList where isActivate equals to UPDATED_IS_ACTIVATE
        defaultOrganizationShouldNotBeFound("isActivate.equals=" + UPDATED_IS_ACTIVATE);
    }

    @Test
    @Transactional
    void getAllOrganizationsByIsActivateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where isActivate not equals to DEFAULT_IS_ACTIVATE
        defaultOrganizationShouldNotBeFound("isActivate.notEquals=" + DEFAULT_IS_ACTIVATE);

        // Get all the organizationList where isActivate not equals to UPDATED_IS_ACTIVATE
        defaultOrganizationShouldBeFound("isActivate.notEquals=" + UPDATED_IS_ACTIVATE);
    }

    @Test
    @Transactional
    void getAllOrganizationsByIsActivateIsInShouldWork() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where isActivate in DEFAULT_IS_ACTIVATE or UPDATED_IS_ACTIVATE
        defaultOrganizationShouldBeFound("isActivate.in=" + DEFAULT_IS_ACTIVATE + "," + UPDATED_IS_ACTIVATE);

        // Get all the organizationList where isActivate equals to UPDATED_IS_ACTIVATE
        defaultOrganizationShouldNotBeFound("isActivate.in=" + UPDATED_IS_ACTIVATE);
    }

    @Test
    @Transactional
    void getAllOrganizationsByIsActivateIsNullOrNotNull() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where isActivate is not null
        defaultOrganizationShouldBeFound("isActivate.specified=true");

        // Get all the organizationList where isActivate is null
        defaultOrganizationShouldNotBeFound("isActivate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrgnizationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where orgnizationType equals to DEFAULT_ORGNIZATION_TYPE
        defaultOrganizationShouldBeFound("orgnizationType.equals=" + DEFAULT_ORGNIZATION_TYPE);

        // Get all the organizationList where orgnizationType equals to UPDATED_ORGNIZATION_TYPE
        defaultOrganizationShouldNotBeFound("orgnizationType.equals=" + UPDATED_ORGNIZATION_TYPE);
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrgnizationTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where orgnizationType not equals to DEFAULT_ORGNIZATION_TYPE
        defaultOrganizationShouldNotBeFound("orgnizationType.notEquals=" + DEFAULT_ORGNIZATION_TYPE);

        // Get all the organizationList where orgnizationType not equals to UPDATED_ORGNIZATION_TYPE
        defaultOrganizationShouldBeFound("orgnizationType.notEquals=" + UPDATED_ORGNIZATION_TYPE);
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrgnizationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where orgnizationType in DEFAULT_ORGNIZATION_TYPE or UPDATED_ORGNIZATION_TYPE
        defaultOrganizationShouldBeFound("orgnizationType.in=" + DEFAULT_ORGNIZATION_TYPE + "," + UPDATED_ORGNIZATION_TYPE);

        // Get all the organizationList where orgnizationType equals to UPDATED_ORGNIZATION_TYPE
        defaultOrganizationShouldNotBeFound("orgnizationType.in=" + UPDATED_ORGNIZATION_TYPE);
    }

    @Test
    @Transactional
    void getAllOrganizationsByOrgnizationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where orgnizationType is not null
        defaultOrganizationShouldBeFound("orgnizationType.specified=true");

        // Get all the organizationList where orgnizationType is null
        defaultOrganizationShouldNotBeFound("orgnizationType.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultOrganizationShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the organizationList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultOrganizationShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultOrganizationShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the organizationList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultOrganizationShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultOrganizationShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the organizationList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultOrganizationShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModified is not null
        defaultOrganizationShouldBeFound("lastModified.specified=true");

        // Get all the organizationList where lastModified is null
        defaultOrganizationShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultOrganizationShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the organizationList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultOrganizationShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultOrganizationShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the organizationList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultOrganizationShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultOrganizationShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the organizationList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultOrganizationShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModifiedBy is not null
        defaultOrganizationShouldBeFound("lastModifiedBy.specified=true");

        // Get all the organizationList where lastModifiedBy is null
        defaultOrganizationShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultOrganizationShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the organizationList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultOrganizationShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllOrganizationsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultOrganizationShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the organizationList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultOrganizationShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrganizationShouldBeFound(String filter) throws Exception {
        restOrganizationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organization.getId().intValue())))
            .andExpect(jsonPath("$.[*].organizationName").value(hasItem(DEFAULT_ORGANIZATION_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isActivate").value(hasItem(DEFAULT_IS_ACTIVATE.booleanValue())))
            .andExpect(jsonPath("$.[*].orgnizationType").value(hasItem(DEFAULT_ORGNIZATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restOrganizationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrganizationShouldNotBeFound(String filter) throws Exception {
        restOrganizationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrganizationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrganization() throws Exception {
        // Get the organization
        restOrganizationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();

        // Update the organization
        Organization updatedOrganization = organizationRepository.findById(organization.getId()).get();
        // Disconnect from session so that the updates on updatedOrganization are not directly saved in db
        em.detach(updatedOrganization);
        updatedOrganization
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .address(UPDATED_ADDRESS)
            .createdOn(UPDATED_CREATED_ON)
            .description(UPDATED_DESCRIPTION)
            .isActivate(UPDATED_IS_ACTIVATE)
            .orgnizationType(UPDATED_ORGNIZATION_TYPE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        OrganizationDTO organizationDTO = organizationMapper.toDto(updatedOrganization);

        restOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organizationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testOrganization.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testOrganization.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testOrganization.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganization.getIsActivate()).isEqualTo(UPDATED_IS_ACTIVATE);
        assertThat(testOrganization.getOrgnizationType()).isEqualTo(UPDATED_ORGNIZATION_TYPE);
        assertThat(testOrganization.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testOrganization.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        organization.setId(count.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organizationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        organization.setId(count.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        organization.setId(count.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .description(UPDATED_DESCRIPTION)
            .isActivate(UPDATED_IS_ACTIVATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganization))
            )
            .andExpect(status().isOk());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testOrganization.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testOrganization.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testOrganization.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganization.getIsActivate()).isEqualTo(UPDATED_IS_ACTIVATE);
        assertThat(testOrganization.getOrgnizationType()).isEqualTo(DEFAULT_ORGNIZATION_TYPE);
        assertThat(testOrganization.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testOrganization.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .address(UPDATED_ADDRESS)
            .createdOn(UPDATED_CREATED_ON)
            .description(UPDATED_DESCRIPTION)
            .isActivate(UPDATED_IS_ACTIVATE)
            .orgnizationType(UPDATED_ORGNIZATION_TYPE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganization))
            )
            .andExpect(status().isOk());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testOrganization.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testOrganization.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testOrganization.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOrganization.getIsActivate()).isEqualTo(UPDATED_IS_ACTIVATE);
        assertThat(testOrganization.getOrgnizationType()).isEqualTo(UPDATED_ORGNIZATION_TYPE);
        assertThat(testOrganization.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testOrganization.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        organization.setId(count.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, organizationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        organization.setId(count.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        organization.setId(count.incrementAndGet());

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.toDto(organization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organizationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        int databaseSizeBeforeDelete = organizationRepository.findAll().size();

        // Delete the organization
        restOrganizationMockMvc
            .perform(delete(ENTITY_API_URL_ID, organization.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
