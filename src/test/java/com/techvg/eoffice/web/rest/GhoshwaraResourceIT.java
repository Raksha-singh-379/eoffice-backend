package com.techvg.eoffice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.techvg.eoffice.IntegrationTest;
import com.techvg.eoffice.domain.Ghoshwara;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.domain.enumeration.RegisterType;
import com.techvg.eoffice.repository.GhoshwaraRepository;
import com.techvg.eoffice.service.GhoshwaraService;
import com.techvg.eoffice.service.criteria.GhoshwaraCriteria;
import com.techvg.eoffice.service.dto.GhoshwaraDTO;
import com.techvg.eoffice.service.mapper.GhoshwaraMapper;
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
 * Integration tests for the {@link GhoshwaraResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GhoshwaraResourceIT {

    private static final RegisterType DEFAULT_REGISTER_TYPE = RegisterType.WORK_DESCRIPTION;
    private static final RegisterType UPDATED_REGISTER_TYPE = RegisterType.AWAITED_REGISTER;

    private static final Integer DEFAULT_INITIAL_PENDINGS = 1;
    private static final Integer UPDATED_INITIAL_PENDINGS = 2;
    private static final Integer SMALLER_INITIAL_PENDINGS = 1 - 1;

    private static final Integer DEFAULT_CURRENT_WEEK_INWARDS = 1;
    private static final Integer UPDATED_CURRENT_WEEK_INWARDS = 2;
    private static final Integer SMALLER_CURRENT_WEEK_INWARDS = 1 - 1;

    private static final Integer DEFAULT_TOTAL = 1;
    private static final Integer UPDATED_TOTAL = 2;
    private static final Integer SMALLER_TOTAL = 1 - 1;

    private static final Integer DEFAULT_SELF_GENERATED = 1;
    private static final Integer UPDATED_SELF_GENERATED = 2;
    private static final Integer SMALLER_SELF_GENERATED = 1 - 1;

    private static final Integer DEFAULT_CURRENT_WEEK_CLEARED = 1;
    private static final Integer UPDATED_CURRENT_WEEK_CLEARED = 2;
    private static final Integer SMALLER_CURRENT_WEEK_CLEARED = 1 - 1;

    private static final Integer DEFAULT_WEEKLY_PENDINGS = 1;
    private static final Integer UPDATED_WEEKLY_PENDINGS = 2;
    private static final Integer SMALLER_WEEKLY_PENDINGS = 1 - 1;

    private static final Integer DEFAULT_FIRST_WEEK = 1;
    private static final Integer UPDATED_FIRST_WEEK = 2;
    private static final Integer SMALLER_FIRST_WEEK = 1 - 1;

    private static final Integer DEFAULT_SECOND_WEEK = 1;
    private static final Integer UPDATED_SECOND_WEEK = 2;
    private static final Integer SMALLER_SECOND_WEEK = 1 - 1;

    private static final Integer DEFAULT_THIRD_WEEK = 1;
    private static final Integer UPDATED_THIRD_WEEK = 2;
    private static final Integer SMALLER_THIRD_WEEK = 1 - 1;

    private static final Integer DEFAULT_FIRST_MONTH = 1;
    private static final Integer UPDATED_FIRST_MONTH = 2;
    private static final Integer SMALLER_FIRST_MONTH = 1 - 1;

    private static final Integer DEFAULT_SECOND_MONTH = 1;
    private static final Integer UPDATED_SECOND_MONTH = 2;
    private static final Integer SMALLER_SECOND_MONTH = 1 - 1;

    private static final Integer DEFAULT_THIRD_MONTH = 1;
    private static final Integer UPDATED_THIRD_MONTH = 2;
    private static final Integer SMALLER_THIRD_MONTH = 1 - 1;

    private static final Integer DEFAULT_WITHIN_SIX_MONTHS = 1;
    private static final Integer UPDATED_WITHIN_SIX_MONTHS = 2;
    private static final Integer SMALLER_WITHIN_SIX_MONTHS = 1 - 1;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ghoshwaras";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GhoshwaraRepository ghoshwaraRepository;

    @Mock
    private GhoshwaraRepository ghoshwaraRepositoryMock;

    @Autowired
    private GhoshwaraMapper ghoshwaraMapper;

    @Mock
    private GhoshwaraService ghoshwaraServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGhoshwaraMockMvc;

    private Ghoshwara ghoshwara;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ghoshwara createEntity(EntityManager em) {
        Ghoshwara ghoshwara = new Ghoshwara()
            .registerType(DEFAULT_REGISTER_TYPE)
            .initialPendings(DEFAULT_INITIAL_PENDINGS)
            .currentWeekInwards(DEFAULT_CURRENT_WEEK_INWARDS)
            .total(DEFAULT_TOTAL)
            .selfGenerated(DEFAULT_SELF_GENERATED)
            .currentWeekCleared(DEFAULT_CURRENT_WEEK_CLEARED)
            .weeklyPendings(DEFAULT_WEEKLY_PENDINGS)
            .firstWeek(DEFAULT_FIRST_WEEK)
            .secondWeek(DEFAULT_SECOND_WEEK)
            .thirdWeek(DEFAULT_THIRD_WEEK)
            .firstMonth(DEFAULT_FIRST_MONTH)
            .secondMonth(DEFAULT_SECOND_MONTH)
            .thirdMonth(DEFAULT_THIRD_MONTH)
            .withinSixMonths(DEFAULT_WITHIN_SIX_MONTHS)
            .date(DEFAULT_DATE)
            .lastModified(DEFAULT_LAST_MODIFIED)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        return ghoshwara;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ghoshwara createUpdatedEntity(EntityManager em) {
        Ghoshwara ghoshwara = new Ghoshwara()
            .registerType(UPDATED_REGISTER_TYPE)
            .initialPendings(UPDATED_INITIAL_PENDINGS)
            .currentWeekInwards(UPDATED_CURRENT_WEEK_INWARDS)
            .total(UPDATED_TOTAL)
            .selfGenerated(UPDATED_SELF_GENERATED)
            .currentWeekCleared(UPDATED_CURRENT_WEEK_CLEARED)
            .weeklyPendings(UPDATED_WEEKLY_PENDINGS)
            .firstWeek(UPDATED_FIRST_WEEK)
            .secondWeek(UPDATED_SECOND_WEEK)
            .thirdWeek(UPDATED_THIRD_WEEK)
            .firstMonth(UPDATED_FIRST_MONTH)
            .secondMonth(UPDATED_SECOND_MONTH)
            .thirdMonth(UPDATED_THIRD_MONTH)
            .withinSixMonths(UPDATED_WITHIN_SIX_MONTHS)
            .date(UPDATED_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        return ghoshwara;
    }

    @BeforeEach
    public void initTest() {
        ghoshwara = createEntity(em);
    }

    @Test
    @Transactional
    void createGhoshwara() throws Exception {
        int databaseSizeBeforeCreate = ghoshwaraRepository.findAll().size();
        // Create the Ghoshwara
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(ghoshwara);
        restGhoshwaraMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO)))
            .andExpect(status().isCreated());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeCreate + 1);
        Ghoshwara testGhoshwara = ghoshwaraList.get(ghoshwaraList.size() - 1);
        assertThat(testGhoshwara.getRegisterType()).isEqualTo(DEFAULT_REGISTER_TYPE);
        assertThat(testGhoshwara.getInitialPendings()).isEqualTo(DEFAULT_INITIAL_PENDINGS);
        assertThat(testGhoshwara.getCurrentWeekInwards()).isEqualTo(DEFAULT_CURRENT_WEEK_INWARDS);
        assertThat(testGhoshwara.getTotal()).isEqualTo(DEFAULT_TOTAL);
        assertThat(testGhoshwara.getSelfGenerated()).isEqualTo(DEFAULT_SELF_GENERATED);
        assertThat(testGhoshwara.getCurrentWeekCleared()).isEqualTo(DEFAULT_CURRENT_WEEK_CLEARED);
        assertThat(testGhoshwara.getWeeklyPendings()).isEqualTo(DEFAULT_WEEKLY_PENDINGS);
        assertThat(testGhoshwara.getFirstWeek()).isEqualTo(DEFAULT_FIRST_WEEK);
        assertThat(testGhoshwara.getSecondWeek()).isEqualTo(DEFAULT_SECOND_WEEK);
        assertThat(testGhoshwara.getThirdWeek()).isEqualTo(DEFAULT_THIRD_WEEK);
        assertThat(testGhoshwara.getFirstMonth()).isEqualTo(DEFAULT_FIRST_MONTH);
        assertThat(testGhoshwara.getSecondMonth()).isEqualTo(DEFAULT_SECOND_MONTH);
        assertThat(testGhoshwara.getThirdMonth()).isEqualTo(DEFAULT_THIRD_MONTH);
        assertThat(testGhoshwara.getWithinSixMonths()).isEqualTo(DEFAULT_WITHIN_SIX_MONTHS);
        assertThat(testGhoshwara.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testGhoshwara.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testGhoshwara.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createGhoshwaraWithExistingId() throws Exception {
        // Create the Ghoshwara with an existing ID
        ghoshwara.setId(1L);
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(ghoshwara);

        int databaseSizeBeforeCreate = ghoshwaraRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGhoshwaraMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGhoshwaras() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList
        restGhoshwaraMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ghoshwara.getId().intValue())))
            .andExpect(jsonPath("$.[*].registerType").value(hasItem(DEFAULT_REGISTER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].initialPendings").value(hasItem(DEFAULT_INITIAL_PENDINGS)))
            .andExpect(jsonPath("$.[*].currentWeekInwards").value(hasItem(DEFAULT_CURRENT_WEEK_INWARDS)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.[*].selfGenerated").value(hasItem(DEFAULT_SELF_GENERATED)))
            .andExpect(jsonPath("$.[*].currentWeekCleared").value(hasItem(DEFAULT_CURRENT_WEEK_CLEARED)))
            .andExpect(jsonPath("$.[*].weeklyPendings").value(hasItem(DEFAULT_WEEKLY_PENDINGS)))
            .andExpect(jsonPath("$.[*].firstWeek").value(hasItem(DEFAULT_FIRST_WEEK)))
            .andExpect(jsonPath("$.[*].secondWeek").value(hasItem(DEFAULT_SECOND_WEEK)))
            .andExpect(jsonPath("$.[*].thirdWeek").value(hasItem(DEFAULT_THIRD_WEEK)))
            .andExpect(jsonPath("$.[*].firstMonth").value(hasItem(DEFAULT_FIRST_MONTH)))
            .andExpect(jsonPath("$.[*].secondMonth").value(hasItem(DEFAULT_SECOND_MONTH)))
            .andExpect(jsonPath("$.[*].thirdMonth").value(hasItem(DEFAULT_THIRD_MONTH)))
            .andExpect(jsonPath("$.[*].withinSixMonths").value(hasItem(DEFAULT_WITHIN_SIX_MONTHS)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGhoshwarasWithEagerRelationshipsIsEnabled() throws Exception {
        when(ghoshwaraServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGhoshwaraMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ghoshwaraServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGhoshwarasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ghoshwaraServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGhoshwaraMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ghoshwaraServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getGhoshwara() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get the ghoshwara
        restGhoshwaraMockMvc
            .perform(get(ENTITY_API_URL_ID, ghoshwara.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ghoshwara.getId().intValue()))
            .andExpect(jsonPath("$.registerType").value(DEFAULT_REGISTER_TYPE.toString()))
            .andExpect(jsonPath("$.initialPendings").value(DEFAULT_INITIAL_PENDINGS))
            .andExpect(jsonPath("$.currentWeekInwards").value(DEFAULT_CURRENT_WEEK_INWARDS))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL))
            .andExpect(jsonPath("$.selfGenerated").value(DEFAULT_SELF_GENERATED))
            .andExpect(jsonPath("$.currentWeekCleared").value(DEFAULT_CURRENT_WEEK_CLEARED))
            .andExpect(jsonPath("$.weeklyPendings").value(DEFAULT_WEEKLY_PENDINGS))
            .andExpect(jsonPath("$.firstWeek").value(DEFAULT_FIRST_WEEK))
            .andExpect(jsonPath("$.secondWeek").value(DEFAULT_SECOND_WEEK))
            .andExpect(jsonPath("$.thirdWeek").value(DEFAULT_THIRD_WEEK))
            .andExpect(jsonPath("$.firstMonth").value(DEFAULT_FIRST_MONTH))
            .andExpect(jsonPath("$.secondMonth").value(DEFAULT_SECOND_MONTH))
            .andExpect(jsonPath("$.thirdMonth").value(DEFAULT_THIRD_MONTH))
            .andExpect(jsonPath("$.withinSixMonths").value(DEFAULT_WITHIN_SIX_MONTHS))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getGhoshwarasByIdFiltering() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        Long id = ghoshwara.getId();

        defaultGhoshwaraShouldBeFound("id.equals=" + id);
        defaultGhoshwaraShouldNotBeFound("id.notEquals=" + id);

        defaultGhoshwaraShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultGhoshwaraShouldNotBeFound("id.greaterThan=" + id);

        defaultGhoshwaraShouldBeFound("id.lessThanOrEqual=" + id);
        defaultGhoshwaraShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByRegisterTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where registerType equals to DEFAULT_REGISTER_TYPE
        defaultGhoshwaraShouldBeFound("registerType.equals=" + DEFAULT_REGISTER_TYPE);

        // Get all the ghoshwaraList where registerType equals to UPDATED_REGISTER_TYPE
        defaultGhoshwaraShouldNotBeFound("registerType.equals=" + UPDATED_REGISTER_TYPE);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByRegisterTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where registerType not equals to DEFAULT_REGISTER_TYPE
        defaultGhoshwaraShouldNotBeFound("registerType.notEquals=" + DEFAULT_REGISTER_TYPE);

        // Get all the ghoshwaraList where registerType not equals to UPDATED_REGISTER_TYPE
        defaultGhoshwaraShouldBeFound("registerType.notEquals=" + UPDATED_REGISTER_TYPE);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByRegisterTypeIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where registerType in DEFAULT_REGISTER_TYPE or UPDATED_REGISTER_TYPE
        defaultGhoshwaraShouldBeFound("registerType.in=" + DEFAULT_REGISTER_TYPE + "," + UPDATED_REGISTER_TYPE);

        // Get all the ghoshwaraList where registerType equals to UPDATED_REGISTER_TYPE
        defaultGhoshwaraShouldNotBeFound("registerType.in=" + UPDATED_REGISTER_TYPE);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByRegisterTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where registerType is not null
        defaultGhoshwaraShouldBeFound("registerType.specified=true");

        // Get all the ghoshwaraList where registerType is null
        defaultGhoshwaraShouldNotBeFound("registerType.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByInitialPendingsIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where initialPendings equals to DEFAULT_INITIAL_PENDINGS
        defaultGhoshwaraShouldBeFound("initialPendings.equals=" + DEFAULT_INITIAL_PENDINGS);

        // Get all the ghoshwaraList where initialPendings equals to UPDATED_INITIAL_PENDINGS
        defaultGhoshwaraShouldNotBeFound("initialPendings.equals=" + UPDATED_INITIAL_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByInitialPendingsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where initialPendings not equals to DEFAULT_INITIAL_PENDINGS
        defaultGhoshwaraShouldNotBeFound("initialPendings.notEquals=" + DEFAULT_INITIAL_PENDINGS);

        // Get all the ghoshwaraList where initialPendings not equals to UPDATED_INITIAL_PENDINGS
        defaultGhoshwaraShouldBeFound("initialPendings.notEquals=" + UPDATED_INITIAL_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByInitialPendingsIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where initialPendings in DEFAULT_INITIAL_PENDINGS or UPDATED_INITIAL_PENDINGS
        defaultGhoshwaraShouldBeFound("initialPendings.in=" + DEFAULT_INITIAL_PENDINGS + "," + UPDATED_INITIAL_PENDINGS);

        // Get all the ghoshwaraList where initialPendings equals to UPDATED_INITIAL_PENDINGS
        defaultGhoshwaraShouldNotBeFound("initialPendings.in=" + UPDATED_INITIAL_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByInitialPendingsIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where initialPendings is not null
        defaultGhoshwaraShouldBeFound("initialPendings.specified=true");

        // Get all the ghoshwaraList where initialPendings is null
        defaultGhoshwaraShouldNotBeFound("initialPendings.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByInitialPendingsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where initialPendings is greater than or equal to DEFAULT_INITIAL_PENDINGS
        defaultGhoshwaraShouldBeFound("initialPendings.greaterThanOrEqual=" + DEFAULT_INITIAL_PENDINGS);

        // Get all the ghoshwaraList where initialPendings is greater than or equal to UPDATED_INITIAL_PENDINGS
        defaultGhoshwaraShouldNotBeFound("initialPendings.greaterThanOrEqual=" + UPDATED_INITIAL_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByInitialPendingsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where initialPendings is less than or equal to DEFAULT_INITIAL_PENDINGS
        defaultGhoshwaraShouldBeFound("initialPendings.lessThanOrEqual=" + DEFAULT_INITIAL_PENDINGS);

        // Get all the ghoshwaraList where initialPendings is less than or equal to SMALLER_INITIAL_PENDINGS
        defaultGhoshwaraShouldNotBeFound("initialPendings.lessThanOrEqual=" + SMALLER_INITIAL_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByInitialPendingsIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where initialPendings is less than DEFAULT_INITIAL_PENDINGS
        defaultGhoshwaraShouldNotBeFound("initialPendings.lessThan=" + DEFAULT_INITIAL_PENDINGS);

        // Get all the ghoshwaraList where initialPendings is less than UPDATED_INITIAL_PENDINGS
        defaultGhoshwaraShouldBeFound("initialPendings.lessThan=" + UPDATED_INITIAL_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByInitialPendingsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where initialPendings is greater than DEFAULT_INITIAL_PENDINGS
        defaultGhoshwaraShouldNotBeFound("initialPendings.greaterThan=" + DEFAULT_INITIAL_PENDINGS);

        // Get all the ghoshwaraList where initialPendings is greater than SMALLER_INITIAL_PENDINGS
        defaultGhoshwaraShouldBeFound("initialPendings.greaterThan=" + SMALLER_INITIAL_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekInwardsIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekInwards equals to DEFAULT_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldBeFound("currentWeekInwards.equals=" + DEFAULT_CURRENT_WEEK_INWARDS);

        // Get all the ghoshwaraList where currentWeekInwards equals to UPDATED_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldNotBeFound("currentWeekInwards.equals=" + UPDATED_CURRENT_WEEK_INWARDS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekInwardsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekInwards not equals to DEFAULT_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldNotBeFound("currentWeekInwards.notEquals=" + DEFAULT_CURRENT_WEEK_INWARDS);

        // Get all the ghoshwaraList where currentWeekInwards not equals to UPDATED_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldBeFound("currentWeekInwards.notEquals=" + UPDATED_CURRENT_WEEK_INWARDS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekInwardsIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekInwards in DEFAULT_CURRENT_WEEK_INWARDS or UPDATED_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldBeFound("currentWeekInwards.in=" + DEFAULT_CURRENT_WEEK_INWARDS + "," + UPDATED_CURRENT_WEEK_INWARDS);

        // Get all the ghoshwaraList where currentWeekInwards equals to UPDATED_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldNotBeFound("currentWeekInwards.in=" + UPDATED_CURRENT_WEEK_INWARDS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekInwardsIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekInwards is not null
        defaultGhoshwaraShouldBeFound("currentWeekInwards.specified=true");

        // Get all the ghoshwaraList where currentWeekInwards is null
        defaultGhoshwaraShouldNotBeFound("currentWeekInwards.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekInwardsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekInwards is greater than or equal to DEFAULT_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldBeFound("currentWeekInwards.greaterThanOrEqual=" + DEFAULT_CURRENT_WEEK_INWARDS);

        // Get all the ghoshwaraList where currentWeekInwards is greater than or equal to UPDATED_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldNotBeFound("currentWeekInwards.greaterThanOrEqual=" + UPDATED_CURRENT_WEEK_INWARDS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekInwardsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekInwards is less than or equal to DEFAULT_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldBeFound("currentWeekInwards.lessThanOrEqual=" + DEFAULT_CURRENT_WEEK_INWARDS);

        // Get all the ghoshwaraList where currentWeekInwards is less than or equal to SMALLER_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldNotBeFound("currentWeekInwards.lessThanOrEqual=" + SMALLER_CURRENT_WEEK_INWARDS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekInwardsIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekInwards is less than DEFAULT_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldNotBeFound("currentWeekInwards.lessThan=" + DEFAULT_CURRENT_WEEK_INWARDS);

        // Get all the ghoshwaraList where currentWeekInwards is less than UPDATED_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldBeFound("currentWeekInwards.lessThan=" + UPDATED_CURRENT_WEEK_INWARDS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekInwardsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekInwards is greater than DEFAULT_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldNotBeFound("currentWeekInwards.greaterThan=" + DEFAULT_CURRENT_WEEK_INWARDS);

        // Get all the ghoshwaraList where currentWeekInwards is greater than SMALLER_CURRENT_WEEK_INWARDS
        defaultGhoshwaraShouldBeFound("currentWeekInwards.greaterThan=" + SMALLER_CURRENT_WEEK_INWARDS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where total equals to DEFAULT_TOTAL
        defaultGhoshwaraShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the ghoshwaraList where total equals to UPDATED_TOTAL
        defaultGhoshwaraShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where total not equals to DEFAULT_TOTAL
        defaultGhoshwaraShouldNotBeFound("total.notEquals=" + DEFAULT_TOTAL);

        // Get all the ghoshwaraList where total not equals to UPDATED_TOTAL
        defaultGhoshwaraShouldBeFound("total.notEquals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultGhoshwaraShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the ghoshwaraList where total equals to UPDATED_TOTAL
        defaultGhoshwaraShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where total is not null
        defaultGhoshwaraShouldBeFound("total.specified=true");

        // Get all the ghoshwaraList where total is null
        defaultGhoshwaraShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where total is greater than or equal to DEFAULT_TOTAL
        defaultGhoshwaraShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the ghoshwaraList where total is greater than or equal to UPDATED_TOTAL
        defaultGhoshwaraShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where total is less than or equal to DEFAULT_TOTAL
        defaultGhoshwaraShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the ghoshwaraList where total is less than or equal to SMALLER_TOTAL
        defaultGhoshwaraShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where total is less than DEFAULT_TOTAL
        defaultGhoshwaraShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the ghoshwaraList where total is less than UPDATED_TOTAL
        defaultGhoshwaraShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where total is greater than DEFAULT_TOTAL
        defaultGhoshwaraShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the ghoshwaraList where total is greater than SMALLER_TOTAL
        defaultGhoshwaraShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySelfGeneratedIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where selfGenerated equals to DEFAULT_SELF_GENERATED
        defaultGhoshwaraShouldBeFound("selfGenerated.equals=" + DEFAULT_SELF_GENERATED);

        // Get all the ghoshwaraList where selfGenerated equals to UPDATED_SELF_GENERATED
        defaultGhoshwaraShouldNotBeFound("selfGenerated.equals=" + UPDATED_SELF_GENERATED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySelfGeneratedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where selfGenerated not equals to DEFAULT_SELF_GENERATED
        defaultGhoshwaraShouldNotBeFound("selfGenerated.notEquals=" + DEFAULT_SELF_GENERATED);

        // Get all the ghoshwaraList where selfGenerated not equals to UPDATED_SELF_GENERATED
        defaultGhoshwaraShouldBeFound("selfGenerated.notEquals=" + UPDATED_SELF_GENERATED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySelfGeneratedIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where selfGenerated in DEFAULT_SELF_GENERATED or UPDATED_SELF_GENERATED
        defaultGhoshwaraShouldBeFound("selfGenerated.in=" + DEFAULT_SELF_GENERATED + "," + UPDATED_SELF_GENERATED);

        // Get all the ghoshwaraList where selfGenerated equals to UPDATED_SELF_GENERATED
        defaultGhoshwaraShouldNotBeFound("selfGenerated.in=" + UPDATED_SELF_GENERATED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySelfGeneratedIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where selfGenerated is not null
        defaultGhoshwaraShouldBeFound("selfGenerated.specified=true");

        // Get all the ghoshwaraList where selfGenerated is null
        defaultGhoshwaraShouldNotBeFound("selfGenerated.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySelfGeneratedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where selfGenerated is greater than or equal to DEFAULT_SELF_GENERATED
        defaultGhoshwaraShouldBeFound("selfGenerated.greaterThanOrEqual=" + DEFAULT_SELF_GENERATED);

        // Get all the ghoshwaraList where selfGenerated is greater than or equal to UPDATED_SELF_GENERATED
        defaultGhoshwaraShouldNotBeFound("selfGenerated.greaterThanOrEqual=" + UPDATED_SELF_GENERATED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySelfGeneratedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where selfGenerated is less than or equal to DEFAULT_SELF_GENERATED
        defaultGhoshwaraShouldBeFound("selfGenerated.lessThanOrEqual=" + DEFAULT_SELF_GENERATED);

        // Get all the ghoshwaraList where selfGenerated is less than or equal to SMALLER_SELF_GENERATED
        defaultGhoshwaraShouldNotBeFound("selfGenerated.lessThanOrEqual=" + SMALLER_SELF_GENERATED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySelfGeneratedIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where selfGenerated is less than DEFAULT_SELF_GENERATED
        defaultGhoshwaraShouldNotBeFound("selfGenerated.lessThan=" + DEFAULT_SELF_GENERATED);

        // Get all the ghoshwaraList where selfGenerated is less than UPDATED_SELF_GENERATED
        defaultGhoshwaraShouldBeFound("selfGenerated.lessThan=" + UPDATED_SELF_GENERATED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySelfGeneratedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where selfGenerated is greater than DEFAULT_SELF_GENERATED
        defaultGhoshwaraShouldNotBeFound("selfGenerated.greaterThan=" + DEFAULT_SELF_GENERATED);

        // Get all the ghoshwaraList where selfGenerated is greater than SMALLER_SELF_GENERATED
        defaultGhoshwaraShouldBeFound("selfGenerated.greaterThan=" + SMALLER_SELF_GENERATED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekClearedIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekCleared equals to DEFAULT_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldBeFound("currentWeekCleared.equals=" + DEFAULT_CURRENT_WEEK_CLEARED);

        // Get all the ghoshwaraList where currentWeekCleared equals to UPDATED_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldNotBeFound("currentWeekCleared.equals=" + UPDATED_CURRENT_WEEK_CLEARED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekClearedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekCleared not equals to DEFAULT_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldNotBeFound("currentWeekCleared.notEquals=" + DEFAULT_CURRENT_WEEK_CLEARED);

        // Get all the ghoshwaraList where currentWeekCleared not equals to UPDATED_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldBeFound("currentWeekCleared.notEquals=" + UPDATED_CURRENT_WEEK_CLEARED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekClearedIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekCleared in DEFAULT_CURRENT_WEEK_CLEARED or UPDATED_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldBeFound("currentWeekCleared.in=" + DEFAULT_CURRENT_WEEK_CLEARED + "," + UPDATED_CURRENT_WEEK_CLEARED);

        // Get all the ghoshwaraList where currentWeekCleared equals to UPDATED_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldNotBeFound("currentWeekCleared.in=" + UPDATED_CURRENT_WEEK_CLEARED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekClearedIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekCleared is not null
        defaultGhoshwaraShouldBeFound("currentWeekCleared.specified=true");

        // Get all the ghoshwaraList where currentWeekCleared is null
        defaultGhoshwaraShouldNotBeFound("currentWeekCleared.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekClearedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekCleared is greater than or equal to DEFAULT_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldBeFound("currentWeekCleared.greaterThanOrEqual=" + DEFAULT_CURRENT_WEEK_CLEARED);

        // Get all the ghoshwaraList where currentWeekCleared is greater than or equal to UPDATED_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldNotBeFound("currentWeekCleared.greaterThanOrEqual=" + UPDATED_CURRENT_WEEK_CLEARED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekClearedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekCleared is less than or equal to DEFAULT_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldBeFound("currentWeekCleared.lessThanOrEqual=" + DEFAULT_CURRENT_WEEK_CLEARED);

        // Get all the ghoshwaraList where currentWeekCleared is less than or equal to SMALLER_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldNotBeFound("currentWeekCleared.lessThanOrEqual=" + SMALLER_CURRENT_WEEK_CLEARED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekClearedIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekCleared is less than DEFAULT_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldNotBeFound("currentWeekCleared.lessThan=" + DEFAULT_CURRENT_WEEK_CLEARED);

        // Get all the ghoshwaraList where currentWeekCleared is less than UPDATED_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldBeFound("currentWeekCleared.lessThan=" + UPDATED_CURRENT_WEEK_CLEARED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByCurrentWeekClearedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where currentWeekCleared is greater than DEFAULT_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldNotBeFound("currentWeekCleared.greaterThan=" + DEFAULT_CURRENT_WEEK_CLEARED);

        // Get all the ghoshwaraList where currentWeekCleared is greater than SMALLER_CURRENT_WEEK_CLEARED
        defaultGhoshwaraShouldBeFound("currentWeekCleared.greaterThan=" + SMALLER_CURRENT_WEEK_CLEARED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWeeklyPendingsIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where weeklyPendings equals to DEFAULT_WEEKLY_PENDINGS
        defaultGhoshwaraShouldBeFound("weeklyPendings.equals=" + DEFAULT_WEEKLY_PENDINGS);

        // Get all the ghoshwaraList where weeklyPendings equals to UPDATED_WEEKLY_PENDINGS
        defaultGhoshwaraShouldNotBeFound("weeklyPendings.equals=" + UPDATED_WEEKLY_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWeeklyPendingsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where weeklyPendings not equals to DEFAULT_WEEKLY_PENDINGS
        defaultGhoshwaraShouldNotBeFound("weeklyPendings.notEquals=" + DEFAULT_WEEKLY_PENDINGS);

        // Get all the ghoshwaraList where weeklyPendings not equals to UPDATED_WEEKLY_PENDINGS
        defaultGhoshwaraShouldBeFound("weeklyPendings.notEquals=" + UPDATED_WEEKLY_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWeeklyPendingsIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where weeklyPendings in DEFAULT_WEEKLY_PENDINGS or UPDATED_WEEKLY_PENDINGS
        defaultGhoshwaraShouldBeFound("weeklyPendings.in=" + DEFAULT_WEEKLY_PENDINGS + "," + UPDATED_WEEKLY_PENDINGS);

        // Get all the ghoshwaraList where weeklyPendings equals to UPDATED_WEEKLY_PENDINGS
        defaultGhoshwaraShouldNotBeFound("weeklyPendings.in=" + UPDATED_WEEKLY_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWeeklyPendingsIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where weeklyPendings is not null
        defaultGhoshwaraShouldBeFound("weeklyPendings.specified=true");

        // Get all the ghoshwaraList where weeklyPendings is null
        defaultGhoshwaraShouldNotBeFound("weeklyPendings.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWeeklyPendingsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where weeklyPendings is greater than or equal to DEFAULT_WEEKLY_PENDINGS
        defaultGhoshwaraShouldBeFound("weeklyPendings.greaterThanOrEqual=" + DEFAULT_WEEKLY_PENDINGS);

        // Get all the ghoshwaraList where weeklyPendings is greater than or equal to UPDATED_WEEKLY_PENDINGS
        defaultGhoshwaraShouldNotBeFound("weeklyPendings.greaterThanOrEqual=" + UPDATED_WEEKLY_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWeeklyPendingsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where weeklyPendings is less than or equal to DEFAULT_WEEKLY_PENDINGS
        defaultGhoshwaraShouldBeFound("weeklyPendings.lessThanOrEqual=" + DEFAULT_WEEKLY_PENDINGS);

        // Get all the ghoshwaraList where weeklyPendings is less than or equal to SMALLER_WEEKLY_PENDINGS
        defaultGhoshwaraShouldNotBeFound("weeklyPendings.lessThanOrEqual=" + SMALLER_WEEKLY_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWeeklyPendingsIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where weeklyPendings is less than DEFAULT_WEEKLY_PENDINGS
        defaultGhoshwaraShouldNotBeFound("weeklyPendings.lessThan=" + DEFAULT_WEEKLY_PENDINGS);

        // Get all the ghoshwaraList where weeklyPendings is less than UPDATED_WEEKLY_PENDINGS
        defaultGhoshwaraShouldBeFound("weeklyPendings.lessThan=" + UPDATED_WEEKLY_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWeeklyPendingsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where weeklyPendings is greater than DEFAULT_WEEKLY_PENDINGS
        defaultGhoshwaraShouldNotBeFound("weeklyPendings.greaterThan=" + DEFAULT_WEEKLY_PENDINGS);

        // Get all the ghoshwaraList where weeklyPendings is greater than SMALLER_WEEKLY_PENDINGS
        defaultGhoshwaraShouldBeFound("weeklyPendings.greaterThan=" + SMALLER_WEEKLY_PENDINGS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstWeekIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstWeek equals to DEFAULT_FIRST_WEEK
        defaultGhoshwaraShouldBeFound("firstWeek.equals=" + DEFAULT_FIRST_WEEK);

        // Get all the ghoshwaraList where firstWeek equals to UPDATED_FIRST_WEEK
        defaultGhoshwaraShouldNotBeFound("firstWeek.equals=" + UPDATED_FIRST_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstWeekIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstWeek not equals to DEFAULT_FIRST_WEEK
        defaultGhoshwaraShouldNotBeFound("firstWeek.notEquals=" + DEFAULT_FIRST_WEEK);

        // Get all the ghoshwaraList where firstWeek not equals to UPDATED_FIRST_WEEK
        defaultGhoshwaraShouldBeFound("firstWeek.notEquals=" + UPDATED_FIRST_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstWeekIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstWeek in DEFAULT_FIRST_WEEK or UPDATED_FIRST_WEEK
        defaultGhoshwaraShouldBeFound("firstWeek.in=" + DEFAULT_FIRST_WEEK + "," + UPDATED_FIRST_WEEK);

        // Get all the ghoshwaraList where firstWeek equals to UPDATED_FIRST_WEEK
        defaultGhoshwaraShouldNotBeFound("firstWeek.in=" + UPDATED_FIRST_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstWeekIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstWeek is not null
        defaultGhoshwaraShouldBeFound("firstWeek.specified=true");

        // Get all the ghoshwaraList where firstWeek is null
        defaultGhoshwaraShouldNotBeFound("firstWeek.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstWeekIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstWeek is greater than or equal to DEFAULT_FIRST_WEEK
        defaultGhoshwaraShouldBeFound("firstWeek.greaterThanOrEqual=" + DEFAULT_FIRST_WEEK);

        // Get all the ghoshwaraList where firstWeek is greater than or equal to UPDATED_FIRST_WEEK
        defaultGhoshwaraShouldNotBeFound("firstWeek.greaterThanOrEqual=" + UPDATED_FIRST_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstWeekIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstWeek is less than or equal to DEFAULT_FIRST_WEEK
        defaultGhoshwaraShouldBeFound("firstWeek.lessThanOrEqual=" + DEFAULT_FIRST_WEEK);

        // Get all the ghoshwaraList where firstWeek is less than or equal to SMALLER_FIRST_WEEK
        defaultGhoshwaraShouldNotBeFound("firstWeek.lessThanOrEqual=" + SMALLER_FIRST_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstWeekIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstWeek is less than DEFAULT_FIRST_WEEK
        defaultGhoshwaraShouldNotBeFound("firstWeek.lessThan=" + DEFAULT_FIRST_WEEK);

        // Get all the ghoshwaraList where firstWeek is less than UPDATED_FIRST_WEEK
        defaultGhoshwaraShouldBeFound("firstWeek.lessThan=" + UPDATED_FIRST_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstWeekIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstWeek is greater than DEFAULT_FIRST_WEEK
        defaultGhoshwaraShouldNotBeFound("firstWeek.greaterThan=" + DEFAULT_FIRST_WEEK);

        // Get all the ghoshwaraList where firstWeek is greater than SMALLER_FIRST_WEEK
        defaultGhoshwaraShouldBeFound("firstWeek.greaterThan=" + SMALLER_FIRST_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondWeekIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondWeek equals to DEFAULT_SECOND_WEEK
        defaultGhoshwaraShouldBeFound("secondWeek.equals=" + DEFAULT_SECOND_WEEK);

        // Get all the ghoshwaraList where secondWeek equals to UPDATED_SECOND_WEEK
        defaultGhoshwaraShouldNotBeFound("secondWeek.equals=" + UPDATED_SECOND_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondWeekIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondWeek not equals to DEFAULT_SECOND_WEEK
        defaultGhoshwaraShouldNotBeFound("secondWeek.notEquals=" + DEFAULT_SECOND_WEEK);

        // Get all the ghoshwaraList where secondWeek not equals to UPDATED_SECOND_WEEK
        defaultGhoshwaraShouldBeFound("secondWeek.notEquals=" + UPDATED_SECOND_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondWeekIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondWeek in DEFAULT_SECOND_WEEK or UPDATED_SECOND_WEEK
        defaultGhoshwaraShouldBeFound("secondWeek.in=" + DEFAULT_SECOND_WEEK + "," + UPDATED_SECOND_WEEK);

        // Get all the ghoshwaraList where secondWeek equals to UPDATED_SECOND_WEEK
        defaultGhoshwaraShouldNotBeFound("secondWeek.in=" + UPDATED_SECOND_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondWeekIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondWeek is not null
        defaultGhoshwaraShouldBeFound("secondWeek.specified=true");

        // Get all the ghoshwaraList where secondWeek is null
        defaultGhoshwaraShouldNotBeFound("secondWeek.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondWeekIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondWeek is greater than or equal to DEFAULT_SECOND_WEEK
        defaultGhoshwaraShouldBeFound("secondWeek.greaterThanOrEqual=" + DEFAULT_SECOND_WEEK);

        // Get all the ghoshwaraList where secondWeek is greater than or equal to UPDATED_SECOND_WEEK
        defaultGhoshwaraShouldNotBeFound("secondWeek.greaterThanOrEqual=" + UPDATED_SECOND_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondWeekIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondWeek is less than or equal to DEFAULT_SECOND_WEEK
        defaultGhoshwaraShouldBeFound("secondWeek.lessThanOrEqual=" + DEFAULT_SECOND_WEEK);

        // Get all the ghoshwaraList where secondWeek is less than or equal to SMALLER_SECOND_WEEK
        defaultGhoshwaraShouldNotBeFound("secondWeek.lessThanOrEqual=" + SMALLER_SECOND_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondWeekIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondWeek is less than DEFAULT_SECOND_WEEK
        defaultGhoshwaraShouldNotBeFound("secondWeek.lessThan=" + DEFAULT_SECOND_WEEK);

        // Get all the ghoshwaraList where secondWeek is less than UPDATED_SECOND_WEEK
        defaultGhoshwaraShouldBeFound("secondWeek.lessThan=" + UPDATED_SECOND_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondWeekIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondWeek is greater than DEFAULT_SECOND_WEEK
        defaultGhoshwaraShouldNotBeFound("secondWeek.greaterThan=" + DEFAULT_SECOND_WEEK);

        // Get all the ghoshwaraList where secondWeek is greater than SMALLER_SECOND_WEEK
        defaultGhoshwaraShouldBeFound("secondWeek.greaterThan=" + SMALLER_SECOND_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdWeekIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdWeek equals to DEFAULT_THIRD_WEEK
        defaultGhoshwaraShouldBeFound("thirdWeek.equals=" + DEFAULT_THIRD_WEEK);

        // Get all the ghoshwaraList where thirdWeek equals to UPDATED_THIRD_WEEK
        defaultGhoshwaraShouldNotBeFound("thirdWeek.equals=" + UPDATED_THIRD_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdWeekIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdWeek not equals to DEFAULT_THIRD_WEEK
        defaultGhoshwaraShouldNotBeFound("thirdWeek.notEquals=" + DEFAULT_THIRD_WEEK);

        // Get all the ghoshwaraList where thirdWeek not equals to UPDATED_THIRD_WEEK
        defaultGhoshwaraShouldBeFound("thirdWeek.notEquals=" + UPDATED_THIRD_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdWeekIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdWeek in DEFAULT_THIRD_WEEK or UPDATED_THIRD_WEEK
        defaultGhoshwaraShouldBeFound("thirdWeek.in=" + DEFAULT_THIRD_WEEK + "," + UPDATED_THIRD_WEEK);

        // Get all the ghoshwaraList where thirdWeek equals to UPDATED_THIRD_WEEK
        defaultGhoshwaraShouldNotBeFound("thirdWeek.in=" + UPDATED_THIRD_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdWeekIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdWeek is not null
        defaultGhoshwaraShouldBeFound("thirdWeek.specified=true");

        // Get all the ghoshwaraList where thirdWeek is null
        defaultGhoshwaraShouldNotBeFound("thirdWeek.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdWeekIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdWeek is greater than or equal to DEFAULT_THIRD_WEEK
        defaultGhoshwaraShouldBeFound("thirdWeek.greaterThanOrEqual=" + DEFAULT_THIRD_WEEK);

        // Get all the ghoshwaraList where thirdWeek is greater than or equal to UPDATED_THIRD_WEEK
        defaultGhoshwaraShouldNotBeFound("thirdWeek.greaterThanOrEqual=" + UPDATED_THIRD_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdWeekIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdWeek is less than or equal to DEFAULT_THIRD_WEEK
        defaultGhoshwaraShouldBeFound("thirdWeek.lessThanOrEqual=" + DEFAULT_THIRD_WEEK);

        // Get all the ghoshwaraList where thirdWeek is less than or equal to SMALLER_THIRD_WEEK
        defaultGhoshwaraShouldNotBeFound("thirdWeek.lessThanOrEqual=" + SMALLER_THIRD_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdWeekIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdWeek is less than DEFAULT_THIRD_WEEK
        defaultGhoshwaraShouldNotBeFound("thirdWeek.lessThan=" + DEFAULT_THIRD_WEEK);

        // Get all the ghoshwaraList where thirdWeek is less than UPDATED_THIRD_WEEK
        defaultGhoshwaraShouldBeFound("thirdWeek.lessThan=" + UPDATED_THIRD_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdWeekIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdWeek is greater than DEFAULT_THIRD_WEEK
        defaultGhoshwaraShouldNotBeFound("thirdWeek.greaterThan=" + DEFAULT_THIRD_WEEK);

        // Get all the ghoshwaraList where thirdWeek is greater than SMALLER_THIRD_WEEK
        defaultGhoshwaraShouldBeFound("thirdWeek.greaterThan=" + SMALLER_THIRD_WEEK);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstMonthIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstMonth equals to DEFAULT_FIRST_MONTH
        defaultGhoshwaraShouldBeFound("firstMonth.equals=" + DEFAULT_FIRST_MONTH);

        // Get all the ghoshwaraList where firstMonth equals to UPDATED_FIRST_MONTH
        defaultGhoshwaraShouldNotBeFound("firstMonth.equals=" + UPDATED_FIRST_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstMonthIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstMonth not equals to DEFAULT_FIRST_MONTH
        defaultGhoshwaraShouldNotBeFound("firstMonth.notEquals=" + DEFAULT_FIRST_MONTH);

        // Get all the ghoshwaraList where firstMonth not equals to UPDATED_FIRST_MONTH
        defaultGhoshwaraShouldBeFound("firstMonth.notEquals=" + UPDATED_FIRST_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstMonthIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstMonth in DEFAULT_FIRST_MONTH or UPDATED_FIRST_MONTH
        defaultGhoshwaraShouldBeFound("firstMonth.in=" + DEFAULT_FIRST_MONTH + "," + UPDATED_FIRST_MONTH);

        // Get all the ghoshwaraList where firstMonth equals to UPDATED_FIRST_MONTH
        defaultGhoshwaraShouldNotBeFound("firstMonth.in=" + UPDATED_FIRST_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstMonthIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstMonth is not null
        defaultGhoshwaraShouldBeFound("firstMonth.specified=true");

        // Get all the ghoshwaraList where firstMonth is null
        defaultGhoshwaraShouldNotBeFound("firstMonth.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstMonthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstMonth is greater than or equal to DEFAULT_FIRST_MONTH
        defaultGhoshwaraShouldBeFound("firstMonth.greaterThanOrEqual=" + DEFAULT_FIRST_MONTH);

        // Get all the ghoshwaraList where firstMonth is greater than or equal to UPDATED_FIRST_MONTH
        defaultGhoshwaraShouldNotBeFound("firstMonth.greaterThanOrEqual=" + UPDATED_FIRST_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstMonthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstMonth is less than or equal to DEFAULT_FIRST_MONTH
        defaultGhoshwaraShouldBeFound("firstMonth.lessThanOrEqual=" + DEFAULT_FIRST_MONTH);

        // Get all the ghoshwaraList where firstMonth is less than or equal to SMALLER_FIRST_MONTH
        defaultGhoshwaraShouldNotBeFound("firstMonth.lessThanOrEqual=" + SMALLER_FIRST_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstMonthIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstMonth is less than DEFAULT_FIRST_MONTH
        defaultGhoshwaraShouldNotBeFound("firstMonth.lessThan=" + DEFAULT_FIRST_MONTH);

        // Get all the ghoshwaraList where firstMonth is less than UPDATED_FIRST_MONTH
        defaultGhoshwaraShouldBeFound("firstMonth.lessThan=" + UPDATED_FIRST_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByFirstMonthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where firstMonth is greater than DEFAULT_FIRST_MONTH
        defaultGhoshwaraShouldNotBeFound("firstMonth.greaterThan=" + DEFAULT_FIRST_MONTH);

        // Get all the ghoshwaraList where firstMonth is greater than SMALLER_FIRST_MONTH
        defaultGhoshwaraShouldBeFound("firstMonth.greaterThan=" + SMALLER_FIRST_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondMonthIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondMonth equals to DEFAULT_SECOND_MONTH
        defaultGhoshwaraShouldBeFound("secondMonth.equals=" + DEFAULT_SECOND_MONTH);

        // Get all the ghoshwaraList where secondMonth equals to UPDATED_SECOND_MONTH
        defaultGhoshwaraShouldNotBeFound("secondMonth.equals=" + UPDATED_SECOND_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondMonthIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondMonth not equals to DEFAULT_SECOND_MONTH
        defaultGhoshwaraShouldNotBeFound("secondMonth.notEquals=" + DEFAULT_SECOND_MONTH);

        // Get all the ghoshwaraList where secondMonth not equals to UPDATED_SECOND_MONTH
        defaultGhoshwaraShouldBeFound("secondMonth.notEquals=" + UPDATED_SECOND_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondMonthIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondMonth in DEFAULT_SECOND_MONTH or UPDATED_SECOND_MONTH
        defaultGhoshwaraShouldBeFound("secondMonth.in=" + DEFAULT_SECOND_MONTH + "," + UPDATED_SECOND_MONTH);

        // Get all the ghoshwaraList where secondMonth equals to UPDATED_SECOND_MONTH
        defaultGhoshwaraShouldNotBeFound("secondMonth.in=" + UPDATED_SECOND_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondMonthIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondMonth is not null
        defaultGhoshwaraShouldBeFound("secondMonth.specified=true");

        // Get all the ghoshwaraList where secondMonth is null
        defaultGhoshwaraShouldNotBeFound("secondMonth.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondMonthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondMonth is greater than or equal to DEFAULT_SECOND_MONTH
        defaultGhoshwaraShouldBeFound("secondMonth.greaterThanOrEqual=" + DEFAULT_SECOND_MONTH);

        // Get all the ghoshwaraList where secondMonth is greater than or equal to UPDATED_SECOND_MONTH
        defaultGhoshwaraShouldNotBeFound("secondMonth.greaterThanOrEqual=" + UPDATED_SECOND_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondMonthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondMonth is less than or equal to DEFAULT_SECOND_MONTH
        defaultGhoshwaraShouldBeFound("secondMonth.lessThanOrEqual=" + DEFAULT_SECOND_MONTH);

        // Get all the ghoshwaraList where secondMonth is less than or equal to SMALLER_SECOND_MONTH
        defaultGhoshwaraShouldNotBeFound("secondMonth.lessThanOrEqual=" + SMALLER_SECOND_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondMonthIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondMonth is less than DEFAULT_SECOND_MONTH
        defaultGhoshwaraShouldNotBeFound("secondMonth.lessThan=" + DEFAULT_SECOND_MONTH);

        // Get all the ghoshwaraList where secondMonth is less than UPDATED_SECOND_MONTH
        defaultGhoshwaraShouldBeFound("secondMonth.lessThan=" + UPDATED_SECOND_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecondMonthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where secondMonth is greater than DEFAULT_SECOND_MONTH
        defaultGhoshwaraShouldNotBeFound("secondMonth.greaterThan=" + DEFAULT_SECOND_MONTH);

        // Get all the ghoshwaraList where secondMonth is greater than SMALLER_SECOND_MONTH
        defaultGhoshwaraShouldBeFound("secondMonth.greaterThan=" + SMALLER_SECOND_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdMonthIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdMonth equals to DEFAULT_THIRD_MONTH
        defaultGhoshwaraShouldBeFound("thirdMonth.equals=" + DEFAULT_THIRD_MONTH);

        // Get all the ghoshwaraList where thirdMonth equals to UPDATED_THIRD_MONTH
        defaultGhoshwaraShouldNotBeFound("thirdMonth.equals=" + UPDATED_THIRD_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdMonthIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdMonth not equals to DEFAULT_THIRD_MONTH
        defaultGhoshwaraShouldNotBeFound("thirdMonth.notEquals=" + DEFAULT_THIRD_MONTH);

        // Get all the ghoshwaraList where thirdMonth not equals to UPDATED_THIRD_MONTH
        defaultGhoshwaraShouldBeFound("thirdMonth.notEquals=" + UPDATED_THIRD_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdMonthIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdMonth in DEFAULT_THIRD_MONTH or UPDATED_THIRD_MONTH
        defaultGhoshwaraShouldBeFound("thirdMonth.in=" + DEFAULT_THIRD_MONTH + "," + UPDATED_THIRD_MONTH);

        // Get all the ghoshwaraList where thirdMonth equals to UPDATED_THIRD_MONTH
        defaultGhoshwaraShouldNotBeFound("thirdMonth.in=" + UPDATED_THIRD_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdMonthIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdMonth is not null
        defaultGhoshwaraShouldBeFound("thirdMonth.specified=true");

        // Get all the ghoshwaraList where thirdMonth is null
        defaultGhoshwaraShouldNotBeFound("thirdMonth.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdMonthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdMonth is greater than or equal to DEFAULT_THIRD_MONTH
        defaultGhoshwaraShouldBeFound("thirdMonth.greaterThanOrEqual=" + DEFAULT_THIRD_MONTH);

        // Get all the ghoshwaraList where thirdMonth is greater than or equal to UPDATED_THIRD_MONTH
        defaultGhoshwaraShouldNotBeFound("thirdMonth.greaterThanOrEqual=" + UPDATED_THIRD_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdMonthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdMonth is less than or equal to DEFAULT_THIRD_MONTH
        defaultGhoshwaraShouldBeFound("thirdMonth.lessThanOrEqual=" + DEFAULT_THIRD_MONTH);

        // Get all the ghoshwaraList where thirdMonth is less than or equal to SMALLER_THIRD_MONTH
        defaultGhoshwaraShouldNotBeFound("thirdMonth.lessThanOrEqual=" + SMALLER_THIRD_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdMonthIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdMonth is less than DEFAULT_THIRD_MONTH
        defaultGhoshwaraShouldNotBeFound("thirdMonth.lessThan=" + DEFAULT_THIRD_MONTH);

        // Get all the ghoshwaraList where thirdMonth is less than UPDATED_THIRD_MONTH
        defaultGhoshwaraShouldBeFound("thirdMonth.lessThan=" + UPDATED_THIRD_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByThirdMonthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where thirdMonth is greater than DEFAULT_THIRD_MONTH
        defaultGhoshwaraShouldNotBeFound("thirdMonth.greaterThan=" + DEFAULT_THIRD_MONTH);

        // Get all the ghoshwaraList where thirdMonth is greater than SMALLER_THIRD_MONTH
        defaultGhoshwaraShouldBeFound("thirdMonth.greaterThan=" + SMALLER_THIRD_MONTH);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWithinSixMonthsIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where withinSixMonths equals to DEFAULT_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldBeFound("withinSixMonths.equals=" + DEFAULT_WITHIN_SIX_MONTHS);

        // Get all the ghoshwaraList where withinSixMonths equals to UPDATED_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldNotBeFound("withinSixMonths.equals=" + UPDATED_WITHIN_SIX_MONTHS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWithinSixMonthsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where withinSixMonths not equals to DEFAULT_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldNotBeFound("withinSixMonths.notEquals=" + DEFAULT_WITHIN_SIX_MONTHS);

        // Get all the ghoshwaraList where withinSixMonths not equals to UPDATED_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldBeFound("withinSixMonths.notEquals=" + UPDATED_WITHIN_SIX_MONTHS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWithinSixMonthsIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where withinSixMonths in DEFAULT_WITHIN_SIX_MONTHS or UPDATED_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldBeFound("withinSixMonths.in=" + DEFAULT_WITHIN_SIX_MONTHS + "," + UPDATED_WITHIN_SIX_MONTHS);

        // Get all the ghoshwaraList where withinSixMonths equals to UPDATED_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldNotBeFound("withinSixMonths.in=" + UPDATED_WITHIN_SIX_MONTHS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWithinSixMonthsIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where withinSixMonths is not null
        defaultGhoshwaraShouldBeFound("withinSixMonths.specified=true");

        // Get all the ghoshwaraList where withinSixMonths is null
        defaultGhoshwaraShouldNotBeFound("withinSixMonths.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWithinSixMonthsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where withinSixMonths is greater than or equal to DEFAULT_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldBeFound("withinSixMonths.greaterThanOrEqual=" + DEFAULT_WITHIN_SIX_MONTHS);

        // Get all the ghoshwaraList where withinSixMonths is greater than or equal to UPDATED_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldNotBeFound("withinSixMonths.greaterThanOrEqual=" + UPDATED_WITHIN_SIX_MONTHS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWithinSixMonthsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where withinSixMonths is less than or equal to DEFAULT_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldBeFound("withinSixMonths.lessThanOrEqual=" + DEFAULT_WITHIN_SIX_MONTHS);

        // Get all the ghoshwaraList where withinSixMonths is less than or equal to SMALLER_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldNotBeFound("withinSixMonths.lessThanOrEqual=" + SMALLER_WITHIN_SIX_MONTHS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWithinSixMonthsIsLessThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where withinSixMonths is less than DEFAULT_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldNotBeFound("withinSixMonths.lessThan=" + DEFAULT_WITHIN_SIX_MONTHS);

        // Get all the ghoshwaraList where withinSixMonths is less than UPDATED_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldBeFound("withinSixMonths.lessThan=" + UPDATED_WITHIN_SIX_MONTHS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByWithinSixMonthsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where withinSixMonths is greater than DEFAULT_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldNotBeFound("withinSixMonths.greaterThan=" + DEFAULT_WITHIN_SIX_MONTHS);

        // Get all the ghoshwaraList where withinSixMonths is greater than SMALLER_WITHIN_SIX_MONTHS
        defaultGhoshwaraShouldBeFound("withinSixMonths.greaterThan=" + SMALLER_WITHIN_SIX_MONTHS);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where date equals to DEFAULT_DATE
        defaultGhoshwaraShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the ghoshwaraList where date equals to UPDATED_DATE
        defaultGhoshwaraShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where date not equals to DEFAULT_DATE
        defaultGhoshwaraShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the ghoshwaraList where date not equals to UPDATED_DATE
        defaultGhoshwaraShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByDateIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where date in DEFAULT_DATE or UPDATED_DATE
        defaultGhoshwaraShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the ghoshwaraList where date equals to UPDATED_DATE
        defaultGhoshwaraShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where date is not null
        defaultGhoshwaraShouldBeFound("date.specified=true");

        // Get all the ghoshwaraList where date is null
        defaultGhoshwaraShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModified equals to DEFAULT_LAST_MODIFIED
        defaultGhoshwaraShouldBeFound("lastModified.equals=" + DEFAULT_LAST_MODIFIED);

        // Get all the ghoshwaraList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultGhoshwaraShouldNotBeFound("lastModified.equals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModified not equals to DEFAULT_LAST_MODIFIED
        defaultGhoshwaraShouldNotBeFound("lastModified.notEquals=" + DEFAULT_LAST_MODIFIED);

        // Get all the ghoshwaraList where lastModified not equals to UPDATED_LAST_MODIFIED
        defaultGhoshwaraShouldBeFound("lastModified.notEquals=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModified in DEFAULT_LAST_MODIFIED or UPDATED_LAST_MODIFIED
        defaultGhoshwaraShouldBeFound("lastModified.in=" + DEFAULT_LAST_MODIFIED + "," + UPDATED_LAST_MODIFIED);

        // Get all the ghoshwaraList where lastModified equals to UPDATED_LAST_MODIFIED
        defaultGhoshwaraShouldNotBeFound("lastModified.in=" + UPDATED_LAST_MODIFIED);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModified is not null
        defaultGhoshwaraShouldBeFound("lastModified.specified=true");

        // Get all the ghoshwaraList where lastModified is null
        defaultGhoshwaraShouldNotBeFound("lastModified.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultGhoshwaraShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the ghoshwaraList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultGhoshwaraShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultGhoshwaraShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the ghoshwaraList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultGhoshwaraShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultGhoshwaraShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the ghoshwaraList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultGhoshwaraShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModifiedBy is not null
        defaultGhoshwaraShouldBeFound("lastModifiedBy.specified=true");

        // Get all the ghoshwaraList where lastModifiedBy is null
        defaultGhoshwaraShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultGhoshwaraShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the ghoshwaraList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultGhoshwaraShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllGhoshwarasByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        // Get all the ghoshwaraList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultGhoshwaraShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the ghoshwaraList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultGhoshwaraShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllGhoshwarasBySecurityUserIsEqualToSomething() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);
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
        ghoshwara.setSecurityUser(securityUser);
        ghoshwaraRepository.saveAndFlush(ghoshwara);
        Long securityUserId = securityUser.getId();

        // Get all the ghoshwaraList where securityUser equals to securityUserId
        defaultGhoshwaraShouldBeFound("securityUserId.equals=" + securityUserId);

        // Get all the ghoshwaraList where securityUser equals to (securityUserId + 1)
        defaultGhoshwaraShouldNotBeFound("securityUserId.equals=" + (securityUserId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGhoshwaraShouldBeFound(String filter) throws Exception {
        restGhoshwaraMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ghoshwara.getId().intValue())))
            .andExpect(jsonPath("$.[*].registerType").value(hasItem(DEFAULT_REGISTER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].initialPendings").value(hasItem(DEFAULT_INITIAL_PENDINGS)))
            .andExpect(jsonPath("$.[*].currentWeekInwards").value(hasItem(DEFAULT_CURRENT_WEEK_INWARDS)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.[*].selfGenerated").value(hasItem(DEFAULT_SELF_GENERATED)))
            .andExpect(jsonPath("$.[*].currentWeekCleared").value(hasItem(DEFAULT_CURRENT_WEEK_CLEARED)))
            .andExpect(jsonPath("$.[*].weeklyPendings").value(hasItem(DEFAULT_WEEKLY_PENDINGS)))
            .andExpect(jsonPath("$.[*].firstWeek").value(hasItem(DEFAULT_FIRST_WEEK)))
            .andExpect(jsonPath("$.[*].secondWeek").value(hasItem(DEFAULT_SECOND_WEEK)))
            .andExpect(jsonPath("$.[*].thirdWeek").value(hasItem(DEFAULT_THIRD_WEEK)))
            .andExpect(jsonPath("$.[*].firstMonth").value(hasItem(DEFAULT_FIRST_MONTH)))
            .andExpect(jsonPath("$.[*].secondMonth").value(hasItem(DEFAULT_SECOND_MONTH)))
            .andExpect(jsonPath("$.[*].thirdMonth").value(hasItem(DEFAULT_THIRD_MONTH)))
            .andExpect(jsonPath("$.[*].withinSixMonths").value(hasItem(DEFAULT_WITHIN_SIX_MONTHS)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModified").value(hasItem(DEFAULT_LAST_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restGhoshwaraMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGhoshwaraShouldNotBeFound(String filter) throws Exception {
        restGhoshwaraMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGhoshwaraMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGhoshwara() throws Exception {
        // Get the ghoshwara
        restGhoshwaraMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGhoshwara() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();

        // Update the ghoshwara
        Ghoshwara updatedGhoshwara = ghoshwaraRepository.findById(ghoshwara.getId()).get();
        // Disconnect from session so that the updates on updatedGhoshwara are not directly saved in db
        em.detach(updatedGhoshwara);
        updatedGhoshwara
            .registerType(UPDATED_REGISTER_TYPE)
            .initialPendings(UPDATED_INITIAL_PENDINGS)
            .currentWeekInwards(UPDATED_CURRENT_WEEK_INWARDS)
            .total(UPDATED_TOTAL)
            .selfGenerated(UPDATED_SELF_GENERATED)
            .currentWeekCleared(UPDATED_CURRENT_WEEK_CLEARED)
            .weeklyPendings(UPDATED_WEEKLY_PENDINGS)
            .firstWeek(UPDATED_FIRST_WEEK)
            .secondWeek(UPDATED_SECOND_WEEK)
            .thirdWeek(UPDATED_THIRD_WEEK)
            .firstMonth(UPDATED_FIRST_MONTH)
            .secondMonth(UPDATED_SECOND_MONTH)
            .thirdMonth(UPDATED_THIRD_MONTH)
            .withinSixMonths(UPDATED_WITHIN_SIX_MONTHS)
            .date(UPDATED_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(updatedGhoshwara);

        restGhoshwaraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ghoshwaraDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO))
            )
            .andExpect(status().isOk());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
        Ghoshwara testGhoshwara = ghoshwaraList.get(ghoshwaraList.size() - 1);
        assertThat(testGhoshwara.getRegisterType()).isEqualTo(UPDATED_REGISTER_TYPE);
        assertThat(testGhoshwara.getInitialPendings()).isEqualTo(UPDATED_INITIAL_PENDINGS);
        assertThat(testGhoshwara.getCurrentWeekInwards()).isEqualTo(UPDATED_CURRENT_WEEK_INWARDS);
        assertThat(testGhoshwara.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testGhoshwara.getSelfGenerated()).isEqualTo(UPDATED_SELF_GENERATED);
        assertThat(testGhoshwara.getCurrentWeekCleared()).isEqualTo(UPDATED_CURRENT_WEEK_CLEARED);
        assertThat(testGhoshwara.getWeeklyPendings()).isEqualTo(UPDATED_WEEKLY_PENDINGS);
        assertThat(testGhoshwara.getFirstWeek()).isEqualTo(UPDATED_FIRST_WEEK);
        assertThat(testGhoshwara.getSecondWeek()).isEqualTo(UPDATED_SECOND_WEEK);
        assertThat(testGhoshwara.getThirdWeek()).isEqualTo(UPDATED_THIRD_WEEK);
        assertThat(testGhoshwara.getFirstMonth()).isEqualTo(UPDATED_FIRST_MONTH);
        assertThat(testGhoshwara.getSecondMonth()).isEqualTo(UPDATED_SECOND_MONTH);
        assertThat(testGhoshwara.getThirdMonth()).isEqualTo(UPDATED_THIRD_MONTH);
        assertThat(testGhoshwara.getWithinSixMonths()).isEqualTo(UPDATED_WITHIN_SIX_MONTHS);
        assertThat(testGhoshwara.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testGhoshwara.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testGhoshwara.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingGhoshwara() throws Exception {
        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();
        ghoshwara.setId(count.incrementAndGet());

        // Create the Ghoshwara
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(ghoshwara);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGhoshwaraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ghoshwaraDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGhoshwara() throws Exception {
        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();
        ghoshwara.setId(count.incrementAndGet());

        // Create the Ghoshwara
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(ghoshwara);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGhoshwaraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGhoshwara() throws Exception {
        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();
        ghoshwara.setId(count.incrementAndGet());

        // Create the Ghoshwara
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(ghoshwara);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGhoshwaraMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGhoshwaraWithPatch() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();

        // Update the ghoshwara using partial update
        Ghoshwara partialUpdatedGhoshwara = new Ghoshwara();
        partialUpdatedGhoshwara.setId(ghoshwara.getId());

        partialUpdatedGhoshwara
            .registerType(UPDATED_REGISTER_TYPE)
            .initialPendings(UPDATED_INITIAL_PENDINGS)
            .selfGenerated(UPDATED_SELF_GENERATED)
            .thirdWeek(UPDATED_THIRD_WEEK)
            .firstMonth(UPDATED_FIRST_MONTH)
            .thirdMonth(UPDATED_THIRD_MONTH)
            .date(UPDATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restGhoshwaraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGhoshwara.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGhoshwara))
            )
            .andExpect(status().isOk());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
        Ghoshwara testGhoshwara = ghoshwaraList.get(ghoshwaraList.size() - 1);
        assertThat(testGhoshwara.getRegisterType()).isEqualTo(UPDATED_REGISTER_TYPE);
        assertThat(testGhoshwara.getInitialPendings()).isEqualTo(UPDATED_INITIAL_PENDINGS);
        assertThat(testGhoshwara.getCurrentWeekInwards()).isEqualTo(DEFAULT_CURRENT_WEEK_INWARDS);
        assertThat(testGhoshwara.getTotal()).isEqualTo(DEFAULT_TOTAL);
        assertThat(testGhoshwara.getSelfGenerated()).isEqualTo(UPDATED_SELF_GENERATED);
        assertThat(testGhoshwara.getCurrentWeekCleared()).isEqualTo(DEFAULT_CURRENT_WEEK_CLEARED);
        assertThat(testGhoshwara.getWeeklyPendings()).isEqualTo(DEFAULT_WEEKLY_PENDINGS);
        assertThat(testGhoshwara.getFirstWeek()).isEqualTo(DEFAULT_FIRST_WEEK);
        assertThat(testGhoshwara.getSecondWeek()).isEqualTo(DEFAULT_SECOND_WEEK);
        assertThat(testGhoshwara.getThirdWeek()).isEqualTo(UPDATED_THIRD_WEEK);
        assertThat(testGhoshwara.getFirstMonth()).isEqualTo(UPDATED_FIRST_MONTH);
        assertThat(testGhoshwara.getSecondMonth()).isEqualTo(DEFAULT_SECOND_MONTH);
        assertThat(testGhoshwara.getThirdMonth()).isEqualTo(UPDATED_THIRD_MONTH);
        assertThat(testGhoshwara.getWithinSixMonths()).isEqualTo(DEFAULT_WITHIN_SIX_MONTHS);
        assertThat(testGhoshwara.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testGhoshwara.getLastModified()).isEqualTo(DEFAULT_LAST_MODIFIED);
        assertThat(testGhoshwara.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateGhoshwaraWithPatch() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();

        // Update the ghoshwara using partial update
        Ghoshwara partialUpdatedGhoshwara = new Ghoshwara();
        partialUpdatedGhoshwara.setId(ghoshwara.getId());

        partialUpdatedGhoshwara
            .registerType(UPDATED_REGISTER_TYPE)
            .initialPendings(UPDATED_INITIAL_PENDINGS)
            .currentWeekInwards(UPDATED_CURRENT_WEEK_INWARDS)
            .total(UPDATED_TOTAL)
            .selfGenerated(UPDATED_SELF_GENERATED)
            .currentWeekCleared(UPDATED_CURRENT_WEEK_CLEARED)
            .weeklyPendings(UPDATED_WEEKLY_PENDINGS)
            .firstWeek(UPDATED_FIRST_WEEK)
            .secondWeek(UPDATED_SECOND_WEEK)
            .thirdWeek(UPDATED_THIRD_WEEK)
            .firstMonth(UPDATED_FIRST_MONTH)
            .secondMonth(UPDATED_SECOND_MONTH)
            .thirdMonth(UPDATED_THIRD_MONTH)
            .withinSixMonths(UPDATED_WITHIN_SIX_MONTHS)
            .date(UPDATED_DATE)
            .lastModified(UPDATED_LAST_MODIFIED)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restGhoshwaraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGhoshwara.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGhoshwara))
            )
            .andExpect(status().isOk());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
        Ghoshwara testGhoshwara = ghoshwaraList.get(ghoshwaraList.size() - 1);
        assertThat(testGhoshwara.getRegisterType()).isEqualTo(UPDATED_REGISTER_TYPE);
        assertThat(testGhoshwara.getInitialPendings()).isEqualTo(UPDATED_INITIAL_PENDINGS);
        assertThat(testGhoshwara.getCurrentWeekInwards()).isEqualTo(UPDATED_CURRENT_WEEK_INWARDS);
        assertThat(testGhoshwara.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testGhoshwara.getSelfGenerated()).isEqualTo(UPDATED_SELF_GENERATED);
        assertThat(testGhoshwara.getCurrentWeekCleared()).isEqualTo(UPDATED_CURRENT_WEEK_CLEARED);
        assertThat(testGhoshwara.getWeeklyPendings()).isEqualTo(UPDATED_WEEKLY_PENDINGS);
        assertThat(testGhoshwara.getFirstWeek()).isEqualTo(UPDATED_FIRST_WEEK);
        assertThat(testGhoshwara.getSecondWeek()).isEqualTo(UPDATED_SECOND_WEEK);
        assertThat(testGhoshwara.getThirdWeek()).isEqualTo(UPDATED_THIRD_WEEK);
        assertThat(testGhoshwara.getFirstMonth()).isEqualTo(UPDATED_FIRST_MONTH);
        assertThat(testGhoshwara.getSecondMonth()).isEqualTo(UPDATED_SECOND_MONTH);
        assertThat(testGhoshwara.getThirdMonth()).isEqualTo(UPDATED_THIRD_MONTH);
        assertThat(testGhoshwara.getWithinSixMonths()).isEqualTo(UPDATED_WITHIN_SIX_MONTHS);
        assertThat(testGhoshwara.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testGhoshwara.getLastModified()).isEqualTo(UPDATED_LAST_MODIFIED);
        assertThat(testGhoshwara.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingGhoshwara() throws Exception {
        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();
        ghoshwara.setId(count.incrementAndGet());

        // Create the Ghoshwara
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(ghoshwara);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGhoshwaraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ghoshwaraDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGhoshwara() throws Exception {
        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();
        ghoshwara.setId(count.incrementAndGet());

        // Create the Ghoshwara
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(ghoshwara);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGhoshwaraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGhoshwara() throws Exception {
        int databaseSizeBeforeUpdate = ghoshwaraRepository.findAll().size();
        ghoshwara.setId(count.incrementAndGet());

        // Create the Ghoshwara
        GhoshwaraDTO ghoshwaraDTO = ghoshwaraMapper.toDto(ghoshwara);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGhoshwaraMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ghoshwaraDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ghoshwara in the database
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGhoshwara() throws Exception {
        // Initialize the database
        ghoshwaraRepository.saveAndFlush(ghoshwara);

        int databaseSizeBeforeDelete = ghoshwaraRepository.findAll().size();

        // Delete the ghoshwara
        restGhoshwaraMockMvc
            .perform(delete(ENTITY_API_URL_ID, ghoshwara.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ghoshwara> ghoshwaraList = ghoshwaraRepository.findAll();
        assertThat(ghoshwaraList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
