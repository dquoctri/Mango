package com.dqtri.mango.configuring.security.csrf.config;

@WebAppConfiguration
@ContextConfiguration(classes = { MvcConfig.class, ManualSecurityConfig.class })
public class ManualSecurityIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * Execute custom login and access the endpoint
     */
    @Test
    public void whenLoginIsSuccessFulThenEndpointCanBeAccessedAndCurrentUserPrinted() throws Exception {

        mockMvc.perform(get("/custom/print"))
                .andExpect(status().isUnauthorized());

        HttpSession session = mockMvc.perform(post("/custom/login").param("username", "user1").param("password", "user1Pass"))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession();

        mockMvc.perform(get("/custom/print").session((MockHttpSession) session))
                .andExpect(status().is2xxSuccessful());
    }

}
