package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.SecurityPermission;
import com.techvg.eoffice.domain.SecurityRole;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.repository.SecurityPermissionRepository;
import com.techvg.eoffice.service.criteria.SecurityPermissionCriteria;
import com.techvg.eoffice.service.dto.SecurityPermissionDTO;
import com.techvg.eoffice.service.mapper.SecurityPermissionMapper;
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
 * Integration tests for the {@link SecurityPermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SecurityPermissionResourceIT {

    private static final String DEFAULT_PERMISSION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PERMISSION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/security-permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SecurityPermissionRepository securityPermissionRepository;

    @Autowired
    private SecurityPermissionMapper securityPermissionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSecurityPermissionMockMvc;

    private SecurityPermission securityPermission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityPermission createEntity(EntityManager em) {
        SecurityPermission securityPermission = new SecurityPermission()
            .permissionName(DEFAULT_PERMISSION_NAME)
            .description(DEFAULT_DESCRIPTION)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        return securityPermission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SecurityPermission createUpdatedEntity(EntityManager em) {
        SecurityPermission securityPermission = new SecurityPermission()
            .permissionName(UPDATED_PERMISSION_NAME)
            .description(UPDATED_DESCRIPTION)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        return securityPermission;
    }

    @BeforeEach
    public void initTest() {
        securityPermission = createEntity(em);
    }

    @Test
    @Transactional
    void createSecurityPermission() throws Exception {
        int databaseSizeBeforeCreate = securityPermissionRepository.findAll().size();
        // Create the SecurityPermission
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);
        restSecurityPermissionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeCreate + 1);
        SecurityPermission testSecurityPermission = securityPermissionList.get(securityPermissionList.size() - 1);
        assertThat(testSecurityPermission.getPermissionName()).isEqualTo(DEFAULT_PERMISSION_NAME);
        assertThat(testSecurityPermission.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSecurityPermission.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testSecurityPermission.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createSecurityPermissionWithExistingId() throws Exception {
        // Create the SecurityPermission with an existing ID
        securityPermission.setId(1L);
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);

        int databaseSizeBeforeCreate = securityPermissionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSecurityPermissionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPermissionNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = securityPermissionRepository.findAll().size();
        // set the field null
        securityPermission.setPermissionName(null);

        // Create the SecurityPermission, which fails.
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);

        restSecurityPermissionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSecurityPermissions() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList
        restSecurityPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(securityPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].permissionName").value(hasItem(DEFAULT_PERMISSION_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));
    }

    @Test
    @Transactional
    void getSecurityPermission() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get the securityPermission
        restSecurityPermissionMockMvc
            .perform(get(ENTITY_API_URL_ID, securityPermission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(securityPermission.getId().intValue()))
            .andExpect(jsonPath("$.permissionName").value(DEFAULT_PERMISSION_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getSecurityPermissionsByIdFiltering() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        Long id = securityPermission.getId();

        defaultSecurityPermissionShouldBeFound("id.equals=" + id);
        defaultSecurityPermissionShouldNotBeFound("id.notEquals=" + id);

        defaultSecurityPermissionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSecurityPermissionShouldNotBeFound("id.greaterThan=" + id);

        defaultSecurityPermissionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSecurityPermissionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByPermissionNameIsEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where permissionName equals to DEFAULT_PERMISSION_NAME
        defaultSecurityPermissionShouldBeFound("permissionName.equals=" + DEFAULT_PERMISSION_NAME);

        // Get all the securityPermissionList where permissionName equals to UPDATED_PERMISSION_NAME
        defaultSecurityPermissionShouldNotBeFound("permissionName.equals=" + UPDATED_PERMISSION_NAME);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByPermissionNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where permissionName not equals to DEFAULT_PERMISSION_NAME
        defaultSecurityPermissionShouldNotBeFound("permissionName.notEquals=" + DEFAULT_PERMISSION_NAME);

        // Get all the securityPermissionList where permissionName not equals to UPDATED_PERMISSION_NAME
        defaultSecurityPermissionShouldBeFound("permissionName.notEquals=" + UPDATED_PERMISSION_NAME);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByPermissionNameIsInShouldWork() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where permissionName in DEFAULT_PERMISSION_NAME or UPDATED_PERMISSION_NAME
        defaultSecurityPermissionShouldBeFound("permissionName.in=" + DEFAULT_PERMISSION_NAME + "," + UPDATED_PERMISSION_NAME);

        // Get all the securityPermissionList where permissionName equals to UPDATED_PERMISSION_NAME
        defaultSecurityPermissionShouldNotBeFound("permissionName.in=" + UPDATED_PERMISSION_NAME);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByPermissionNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where permissionName is not null
        defaultSecurityPermissionShouldBeFound("permissionName.specified=true");

        // Get all the securityPermissionList where permissionName is null
        defaultSecurityPermissionShouldNotBeFound("permissionName.specified=false");
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByPermissionNameContainsSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where permissionName contains DEFAULT_PERMISSION_NAME
        defaultSecurityPermissionShouldBeFound("permissionName.contains=" + DEFAULT_PERMISSION_NAME);

        // Get all the securityPermissionList where permissionName contains UPDATED_PERMISSION_NAME
        defaultSecurityPermissionShouldNotBeFound("permissionName.contains=" + UPDATED_PERMISSION_NAME);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByPermissionNameNotContainsSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where permissionName does not contain DEFAULT_PERMISSION_NAME
        defaultSecurityPermissionShouldNotBeFound("permissionName.doesNotContain=" + DEFAULT_PERMISSION_NAME);

        // Get all the securityPermissionList where permissionName does not contain UPDATED_PERMISSION_NAME
        defaultSecurityPermissionShouldBeFound("permissionName.doesNotContain=" + UPDATED_PERMISSION_NAME);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where description equals to DEFAULT_DESCRIPTION
        defaultSecurityPermissionShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the securityPermissionList where description equals to UPDATED_DESCRIPTION
        defaultSecurityPermissionShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where description not equals to DEFAULT_DESCRIPTION
        defaultSecurityPermissionShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the securityPermissionList where description not equals to UPDATED_DESCRIPTION
        defaultSecurityPermissionShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultSecurityPermissionShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the securityPermissionList where description equals to UPDATED_DESCRIPTION
        defaultSecurityPermissionShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where description is not null
        defaultSecurityPermissionShouldBeFound("description.specified=true");

        // Get all the securityPermissionList where description is null
        defaultSecurityPermissionShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where description contains DEFAULT_DESCRIPTION
        defaultSecurityPermissionShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the securityPermissionList where description contains UPDATED_DESCRIPTION
        defaultSecurityPermissionShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where description does not contain DEFAULT_DESCRIPTION
        defaultSecurityPermissionShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the securityPermissionList where description does not contain UPDATED_DESCRIPTION
        defaultSecurityPermissionShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultSecurityPermissionShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the securityPermissionList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultSecurityPermissionShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultSecurityPermissionShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the securityPermissionList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultSecurityPermissionShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultSecurityPermissionShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the securityPermissionList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultSecurityPermissionShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModified is not null
        defaultSecurityPermissionShouldBeFound("lastModified.specified=true");

        // Get all the securityPermissionList where lastModified is null
        defaultSecurityPermissionShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the securityPermissionList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the securityPermissionList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the securityPermissionList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModifiedBy is not null
        defaultSecurityPermissionShouldBeFound("lastModifiedBy.specified=true");

        // Get all the securityPermissionList where lastModifiedBy is null
        defaultSecurityPermissionShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the securityPermissionList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        // Get all the securityPermissionList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the securityPermissionList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultSecurityPermissionShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsBySecurityRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);
        SecurityRole securityRole;
        if (TestUtil.findAll(em, SecurityRole.class).isEmpty()) {
            securityRole = SecurityRoleResourceIT.createEntity(em);
            em.persist(securityRole);
            em.flush();
        } else {
            securityRole = TestUtil.findAll(em, SecurityRole.class).get(0);
        }
        em.persist(securityRole);
        em.flush();
        securityPermission.addSecurityRole(securityRole);
        securityPermissionRepository.saveAndFlush(securityPermission);
        Long securityRoleId = securityRole.getId();

        // Get all the securityPermissionList where securityRole equals to securityRoleId
        defaultSecurityPermissionShouldBeFound("securityRoleId.equals=" + securityRoleId);

        // Get all the securityPermissionList where securityRole equals to (securityRoleId + 1)
        defaultSecurityPermissionShouldNotBeFound("securityRoleId.equals=" + (securityRoleId + 1));
    }

    @Test
    @Transactional
    void getAllSecurityPermissionsBySecurityUserIsEqualToSomething() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);
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
        securityPermission.addSecurityUser(securityUser);
        securityPermissionRepository.saveAndFlush(securityPermission);
        Long securityUserId = securityUser.getId();

        // Get all the securityPermissionList where securityUser equals to securityUserId
        defaultSecurityPermissionShouldBeFound("securityUserId.equals=" + securityUserId);

        // Get all the securityPermissionList where securityUser equals to (securityUserId + 1)
        defaultSecurityPermissionShouldNotBeFound("securityUserId.equals=" + (securityUserId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSecurityPermissionShouldBeFound(String filter) throws Exception {
        restSecurityPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(securityPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].permissionName").value(hasItem(DEFAULT_PERMISSION_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restSecurityPermissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSecurityPermissionShouldNotBeFound(String filter) throws Exception {
        restSecurityPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSecurityPermissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSecurityPermission() throws Exception {
        // Get the securityPermission
        restSecurityPermissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSecurityPermission() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();

        // Update the securityPermission
        SecurityPermission updatedSecurityPermission = securityPermissionRepository.findById(securityPermission.getId()).get();
        // Disconnect from session so that the updates on updatedSecurityPermission are not directly saved in db
        em.detach(updatedSecurityPermission);
        updatedSecurityPermission
            .permissionName(UPDATED_PERMISSION_NAME)
            .description(UPDATED_DESCRIPTION)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(updatedSecurityPermission);

        restSecurityPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, securityPermissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
        SecurityPermission testSecurityPermission = securityPermissionList.get(securityPermissionList.size() - 1);
        assertThat(testSecurityPermission.getPermissionName()).isEqualTo(UPDATED_PERMISSION_NAME);
        assertThat(testSecurityPermission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSecurityPermission.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testSecurityPermission.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingSecurityPermission() throws Exception {
        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();
        securityPermission.setId(count.incrementAndGet());

        // Create the SecurityPermission
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSecurityPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, securityPermissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSecurityPermission() throws Exception {
        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();
        securityPermission.setId(count.incrementAndGet());

        // Create the SecurityPermission
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSecurityPermission() throws Exception {
        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();
        securityPermission.setId(count.incrementAndGet());

        // Create the SecurityPermission
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityPermissionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSecurityPermissionWithPatch() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();

        // Update the securityPermission using partial update
        SecurityPermission partialUpdatedSecurityPermission = new SecurityPermission();
        partialUpdatedSecurityPermission.setId(securityPermission.getId());

        partialUpdatedSecurityPermission.lastModified(UPDATED_LAST_MODIFIED);

        restSecurityPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSecurityPermission.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSecurityPermission))
            )
            .andExpect(status().isOk());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
        SecurityPermission testSecurityPermission = securityPermissionList.get(securityPermissionList.size() - 1);
        assertThat(testSecurityPermission.getPermissionName()).isEqualTo(DEFAULT_PERMISSION_NAME);
        assertThat(testSecurityPermission.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSecurityPermission.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testSecurityPermission.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateSecurityPermissionWithPatch() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();

        // Update the securityPermission using partial update
        SecurityPermission partialUpdatedSecurityPermission = new SecurityPermission();
        partialUpdatedSecurityPermission.setId(securityPermission.getId());

        partialUpdatedSecurityPermission
            .permissionName(UPDATED_PERMISSION_NAME)
            .description(UPDATED_DESCRIPTION)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restSecurityPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSecurityPermission.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSecurityPermission))
            )
            .andExpect(status().isOk());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
        SecurityPermission testSecurityPermission = securityPermissionList.get(securityPermissionList.size() - 1);
        assertThat(testSecurityPermission.getPermissionName()).isEqualTo(UPDATED_PERMISSION_NAME);
        assertThat(testSecurityPermission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSecurityPermission.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testSecurityPermission.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingSecurityPermission() throws Exception {
        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();
        securityPermission.setId(count.incrementAndGet());

        // Create the SecurityPermission
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSecurityPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, securityPermissionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSecurityPermission() throws Exception {
        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();
        securityPermission.setId(count.incrementAndGet());

        // Create the SecurityPermission
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSecurityPermission() throws Exception {
        int databaseSizeBeforeUpdate = securityPermissionRepository.findAll().size();
        securityPermission.setId(count.incrementAndGet());

        // Create the SecurityPermission
        SecurityPermissionDTO securityPermissionDTO = securityPermissionMapper.toDto(securityPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(securityPermissionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SecurityPermission in the database
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSecurityPermission() throws Exception {
        // Initialize the database
        securityPermissionRepository.saveAndFlush(securityPermission);

        int databaseSizeBeforeDelete = securityPermissionRepository.findAll().size();

        // Delete the securityPermission
        restSecurityPermissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, securityPermission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        assertThat(securityPermissionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
