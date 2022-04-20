package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.CommentMaster;
import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.repository.CommentMasterRepository;
import com.techvg.eoffice.service.CommentMasterService;
import com.techvg.eoffice.service.criteria.CommentMasterCriteria;
import com.techvg.eoffice.service.dto.CommentMasterDTO;
import com.techvg.eoffice.service.mapper.CommentMasterMapper;
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
 * Integration tests for the {@link CommentMasterResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CommentMasterResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/comment-masters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommentMasterRepository commentMasterRepository;

    @Mock
    private CommentMasterRepository commentMasterRepositoryMock;

    @Autowired
    private CommentMasterMapper commentMasterMapper;

    @Mock
    private CommentMasterService commentMasterServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommentMasterMockMvc;

    private CommentMaster commentMaster;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommentMaster createEntity(EntityManager em) {
        CommentMaster commentMaster = new CommentMaster()
            .description(DEFAULT_DESCRIPTION)
            .createdOn(DEFAULT_CREATED_ON)
            .createdBy(DEFAULT_CREATED_BY)
            .status(DEFAULT_STATUS)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        return commentMaster;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommentMaster createUpdatedEntity(EntityManager em) {
        CommentMaster commentMaster = new CommentMaster()
            .description(UPDATED_DESCRIPTION)
            .createdOn(UPDATED_CREATED_ON)
            .createdBy(UPDATED_CREATED_BY)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        return commentMaster;
    }

    @BeforeEach
    public void initTest() {
        commentMaster = createEntity(em);
    }

    @Test
    @Transactional
    void createCommentMaster() throws Exception {
        int databaseSizeBeforeCreate = commentMasterRepository.findAll().size();
        // Create the CommentMaster
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(commentMaster);
        restCommentMasterMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeCreate + 1);
        CommentMaster testCommentMaster = commentMasterList.get(commentMasterList.size() - 1);
        assertThat(testCommentMaster.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCommentMaster.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testCommentMaster.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCommentMaster.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCommentMaster.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testCommentMaster.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createCommentMasterWithExistingId() throws Exception {
        // Create the CommentMaster with an existing ID
        commentMaster.setId(1L);
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(commentMaster);

        int databaseSizeBeforeCreate = commentMasterRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentMasterMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCommentMasters() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList
        restCommentMasterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commentMaster.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCommentMastersWithEagerRelationshipsIsEnabled() throws Exception {
        when(commentMasterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCommentMasterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(commentMasterServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCommentMastersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(commentMasterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCommentMasterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(commentMasterServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getCommentMaster() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get the commentMaster
        restCommentMasterMockMvc
            .perform(get(ENTITY_API_URL_ID, commentMaster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commentMaster.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdOn").value(DEFAULT_CREATED_ON.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getCommentMastersByIdFiltering() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        Long id = commentMaster.getId();

        defaultCommentMasterShouldBeFound("id.equals=" + id);
        defaultCommentMasterShouldNotBeFound("id.notEquals=" + id);

        defaultCommentMasterShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCommentMasterShouldNotBeFound("id.greaterThan=" + id);

        defaultCommentMasterShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCommentMasterShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommentMastersByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where description equals to DEFAULT_DESCRIPTION
        defaultCommentMasterShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the commentMasterList where description equals to UPDATED_DESCRIPTION
        defaultCommentMasterShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCommentMastersByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where description not equals to DEFAULT_DESCRIPTION
        defaultCommentMasterShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the commentMasterList where description not equals to UPDATED_DESCRIPTION
        defaultCommentMasterShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCommentMastersByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultCommentMasterShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the commentMasterList where description equals to UPDATED_DESCRIPTION
        defaultCommentMasterShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCommentMastersByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where description is not null
        defaultCommentMasterShouldBeFound("description.specified=true");

        // Get all the commentMasterList where description is null
        defaultCommentMasterShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentMastersByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where description contains DEFAULT_DESCRIPTION
        defaultCommentMasterShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the commentMasterList where description contains UPDATED_DESCRIPTION
        defaultCommentMasterShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCommentMastersByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where description does not contain DEFAULT_DESCRIPTION
        defaultCommentMasterShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the commentMasterList where description does not contain UPDATED_DESCRIPTION
        defaultCommentMasterShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdOn equals to DEFAULT_CREATED_ON
        defaultCommentMasterShouldBeFound("createdOn.equals=" + DEFAULT_CREATED_ON);

        // Get all the commentMasterList where createdOn equals to UPDATED_CREATED_ON
        defaultCommentMasterShouldNotBeFound("createdOn.equals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedOnIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdOn not equals to DEFAULT_CREATED_ON
        defaultCommentMasterShouldNotBeFound("createdOn.notEquals=" + DEFAULT_CREATED_ON);

        // Get all the commentMasterList where createdOn not equals to UPDATED_CREATED_ON
        defaultCommentMasterShouldBeFound("createdOn.notEquals=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedOnIsInShouldWork() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdOn in DEFAULT_CREATED_ON or UPDATED_CREATED_ON
        defaultCommentMasterShouldBeFound("createdOn.in=" + DEFAULT_CREATED_ON + "," + UPDATED_CREATED_ON);

        // Get all the commentMasterList where createdOn equals to UPDATED_CREATED_ON
        defaultCommentMasterShouldNotBeFound("createdOn.in=" + UPDATED_CREATED_ON);
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdOn is not null
        defaultCommentMasterShouldBeFound("createdOn.specified=true");

        // Get all the commentMasterList where createdOn is null
        defaultCommentMasterShouldNotBeFound("createdOn.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdBy equals to DEFAULT_CREATED_BY
        defaultCommentMasterShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the commentMasterList where createdBy equals to UPDATED_CREATED_BY
        defaultCommentMasterShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdBy not equals to DEFAULT_CREATED_BY
        defaultCommentMasterShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the commentMasterList where createdBy not equals to UPDATED_CREATED_BY
        defaultCommentMasterShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultCommentMasterShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the commentMasterList where createdBy equals to UPDATED_CREATED_BY
        defaultCommentMasterShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdBy is not null
        defaultCommentMasterShouldBeFound("createdBy.specified=true");

        // Get all the commentMasterList where createdBy is null
        defaultCommentMasterShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdBy contains DEFAULT_CREATED_BY
        defaultCommentMasterShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the commentMasterList where createdBy contains UPDATED_CREATED_BY
        defaultCommentMasterShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where createdBy does not contain DEFAULT_CREATED_BY
        defaultCommentMasterShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the commentMasterList where createdBy does not contain UPDATED_CREATED_BY
        defaultCommentMasterShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where status equals to DEFAULT_STATUS
        defaultCommentMasterShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the commentMasterList where status equals to UPDATED_STATUS
        defaultCommentMasterShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCommentMastersByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where status not equals to DEFAULT_STATUS
        defaultCommentMasterShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the commentMasterList where status not equals to UPDATED_STATUS
        defaultCommentMasterShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCommentMastersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultCommentMasterShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the commentMasterList where status equals to UPDATED_STATUS
        defaultCommentMasterShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCommentMastersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where status is not null
        defaultCommentMasterShouldBeFound("status.specified=true");

        // Get all the commentMasterList where status is null
        defaultCommentMasterShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultCommentMasterShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the commentMasterList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultCommentMasterShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultCommentMasterShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the commentMasterList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultCommentMasterShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultCommentMasterShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the commentMasterList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultCommentMasterShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModified is not null
        defaultCommentMasterShouldBeFound("lastModified.specified=true");

        // Get all the commentMasterList where lastModified is null
        defaultCommentMasterShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultCommentMasterShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the commentMasterList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultCommentMasterShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultCommentMasterShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the commentMasterList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultCommentMasterShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultCommentMasterShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the commentMasterList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultCommentMasterShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModifiedBy is not null
        defaultCommentMasterShouldBeFound("lastModifiedBy.specified=true");

        // Get all the commentMasterList where lastModifiedBy is null
        defaultCommentMasterShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultCommentMasterShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the commentMasterList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultCommentMasterShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        // Get all the commentMasterList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultCommentMasterShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the commentMasterList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultCommentMasterShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllCommentMastersBySecurityUserIsEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);
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
        commentMaster.setSecurityUser(securityUser);
        commentMasterRepository.saveAndFlush(commentMaster);
        Long securityUserId = securityUser.getId();

        // Get all the commentMasterList where securityUser equals to securityUserId
        defaultCommentMasterShouldBeFound("securityUserId.equals=" + securityUserId);

        // Get all the commentMasterList where securityUser equals to (securityUserId + 1)
        defaultCommentMasterShouldNotBeFound("securityUserId.equals=" + (securityUserId + 1));
    }

    @Test
    @Transactional
    void getAllCommentMastersByDakMasterIsEqualToSomething() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);
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
        commentMaster.setDakMaster(dakMaster);
        commentMasterRepository.saveAndFlush(commentMaster);
        Long dakMasterId = dakMaster.getId();

        // Get all the commentMasterList where dakMaster equals to dakMasterId
        defaultCommentMasterShouldBeFound("dakMasterId.equals=" + dakMasterId);

        // Get all the commentMasterList where dakMaster equals to (dakMasterId + 1)
        defaultCommentMasterShouldNotBeFound("dakMasterId.equals=" + (dakMasterId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommentMasterShouldBeFound(String filter) throws Exception {
        restCommentMasterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commentMaster.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restCommentMasterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommentMasterShouldNotBeFound(String filter) throws Exception {
        restCommentMasterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommentMasterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCommentMaster() throws Exception {
        // Get the commentMaster
        restCommentMasterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCommentMaster() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();

        // Update the commentMaster
        CommentMaster updatedCommentMaster = commentMasterRepository.findById(commentMaster.getId()).get();
        // Disconnect from session so that the updates on updatedCommentMaster are not directly saved in db
        em.detach(updatedCommentMaster);
        updatedCommentMaster
            .description(UPDATED_DESCRIPTION)
            .createdOn(UPDATED_CREATED_ON)
            .createdBy(UPDATED_CREATED_BY)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(updatedCommentMaster);

        restCommentMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commentMasterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isOk());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
        CommentMaster testCommentMaster = commentMasterList.get(commentMasterList.size() - 1);
        assertThat(testCommentMaster.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCommentMaster.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testCommentMaster.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCommentMaster.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCommentMaster.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testCommentMaster.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingCommentMaster() throws Exception {
        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();
        commentMaster.setId(count.incrementAndGet());

        // Create the CommentMaster
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(commentMaster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commentMasterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommentMaster() throws Exception {
        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();
        commentMaster.setId(count.incrementAndGet());

        // Create the CommentMaster
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(commentMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommentMaster() throws Exception {
        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();
        commentMaster.setId(count.incrementAndGet());

        // Create the CommentMaster
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(commentMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentMasterMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommentMasterWithPatch() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();

        // Update the commentMaster using partial update
        CommentMaster partialUpdatedCommentMaster = new CommentMaster();
        partialUpdatedCommentMaster.setId(commentMaster.getId());

        partialUpdatedCommentMaster
            .description(UPDATED_DESCRIPTION)
            .createdOn(UPDATED_CREATED_ON)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED);

        restCommentMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommentMaster.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommentMaster))
            )
            .andExpect(status().isOk());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
        CommentMaster testCommentMaster = commentMasterList.get(commentMasterList.size() - 1);
        assertThat(testCommentMaster.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCommentMaster.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testCommentMaster.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCommentMaster.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCommentMaster.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testCommentMaster.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateCommentMasterWithPatch() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();

        // Update the commentMaster using partial update
        CommentMaster partialUpdatedCommentMaster = new CommentMaster();
        partialUpdatedCommentMaster.setId(commentMaster.getId());

        partialUpdatedCommentMaster
            .description(UPDATED_DESCRIPTION)
            .createdOn(UPDATED_CREATED_ON)
            .createdBy(UPDATED_CREATED_BY)
            .status(UPDATED_STATUS)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restCommentMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommentMaster.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommentMaster))
            )
            .andExpect(status().isOk());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
        CommentMaster testCommentMaster = commentMasterList.get(commentMasterList.size() - 1);
        assertThat(testCommentMaster.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCommentMaster.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testCommentMaster.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCommentMaster.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCommentMaster.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testCommentMaster.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingCommentMaster() throws Exception {
        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();
        commentMaster.setId(count.incrementAndGet());

        // Create the CommentMaster
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(commentMaster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commentMasterDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommentMaster() throws Exception {
        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();
        commentMaster.setId(count.incrementAndGet());

        // Create the CommentMaster
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(commentMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommentMaster() throws Exception {
        int databaseSizeBeforeUpdate = commentMasterRepository.findAll().size();
        commentMaster.setId(count.incrementAndGet());

        // Create the CommentMaster
        CommentMasterDTO commentMasterDTO = commentMasterMapper.toDto(commentMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentMasterMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commentMasterDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommentMaster in the database
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommentMaster() throws Exception {
        // Initialize the database
        commentMasterRepository.saveAndFlush(commentMaster);

        int databaseSizeBeforeDelete = commentMasterRepository.findAll().size();

        // Delete the commentMaster
        restCommentMasterMockMvc
            .perform(delete(ENTITY_API_URL_ID, commentMaster.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CommentMaster> commentMasterList = commentMasterRepository.findAll();
        assertThat(commentMasterList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
