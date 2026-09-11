package br.com.example.davidarchanjo.controller;

import br.com.example.davidarchanjo.model.dto.AppDTO;
import br.com.example.davidarchanjo.service.AppService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static br.com.example.davidarchanjo.utils.AppUtils.createAppDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
public class AppControllerUnitTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AppService service;

    @BeforeEach
    void setup() {
        AppController controller = new AppController(service);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateNewAppSuccessfully() throws Exception {

        AppDTO dto = createAppDto(
                "awesome-app",
                "1.0.0",
                "Java Duke"
        );

        when(service.createNewApp(any(AppDTO.class)))
                .thenReturn(1L);

        mockMvc.perform(
                        post("/api/v1/apps")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost/api/v1/apps/1"
                        )
                );
    }

    @Test
    void shouldGetAllAppsSuccessfully() throws Exception {

        AppDTO app1 = createAppDto(
                "github",
                "1.3.7",
                "Java Duke"
        );

        AppDTO app2 = createAppDto(
                "linkedin",
                "1.8",
                "Java Duke"
        );

        when(service.getAllApps())
                .thenReturn(
                        Arrays.asList(
                                Optional.of(app1),
                                Optional.of(app2)
                        )
                );

        mockMvc.perform(
                        get("/api/v1/apps")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType(
                                MediaType.APPLICATION_JSON_VALUE
                        )
                )
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].appName", is("github")))
                .andExpect(jsonPath("$[0].appVersion", is("1.3.7")))
                .andExpect(jsonPath("$[0].devName", is("Java Duke")))
                .andExpect(jsonPath("$[1].appName", is("linkedin")))
                .andExpect(jsonPath("$[1].appVersion", is("1.8")))
                .andExpect(jsonPath("$[1].devName", is("Java Duke")));
    }

    @Test
    void shouldGetAppByIdSuccessfully() throws Exception {

        AppDTO app = createAppDto(
                "facebook",
                "1.0.0-SNAPSHOT",
                "Java Duke"
        );

        when(service.getAppById(1L))
                .thenReturn(Optional.of(app));

        mockMvc.perform(
                        get("/api/v1/apps/1")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentType(
                                MediaType.APPLICATION_JSON_VALUE
                        )
                )
                .andExpect(jsonPath("$.appName", is("facebook")))
                .andExpect(
                        jsonPath(
                                "$.appVersion",
                                is("1.0.0-SNAPSHOT")
                        )
                )
                .andExpect(
                        jsonPath("$.devName", is("Java Duke"))
                );
    }
}