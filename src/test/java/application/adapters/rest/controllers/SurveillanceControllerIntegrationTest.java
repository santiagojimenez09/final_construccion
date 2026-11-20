package application.adapters.rest.controllers;

import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SurveillanceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SurveillanceDeviceRepositoryPort repositoryPort;

    @BeforeEach
    void cleanMemory() {
        repositoryPort.deleteAll();
    }

    // 2.1. Procesamiento de información completa (F1)
    @Test
    void shouldProcessCompleteInformationSimultaneously() throws Exception {
        String payload = """
                {
                  "devices": [
                    {
                      "name": "STAR Labs",
                      "distance": 100.0,
                      "message": ["este", "", "", "mensaje", ""]
                    },
                    {
                      "name": "CCPD",
                      "distance": 115.5,
                      "message": ["", "es", "", "", "secreto"]
                    },
                    {
                      "name": "S.T.A.R. Satellite",
                      "distance": 142.7,
                      "message": ["este", "", "un", "", ""]
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/surveillance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position.x", is(-487.29)))
                .andExpect(jsonPath("$.position.y", is(1557.01)))
                .andExpect(jsonPath("$.message", is("este es un mensaje secreto")));
    }

    // 2.2 & 2.3. Registro individual en cualquier orden y procesamiento acumulado (F2, F3)
    @Test
    void shouldRegisterDevicesIndividuallyInAnyOrderAndProcessAccumulatedInformation() throws Exception {
        // Before registering 3 devices, consultation must return 404 insufficient information
        mockMvc.perform(get("/api/v1/surveillance"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("No existen datos suficientes")));

        // 1. Register CCPD first
        mockMvc.perform(post("/api/v1/surveillance/devices/CCPD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "distance": 115.5,
                                  "message": ["", "es", "", "", "secreto"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("CCPD")));

        // 2. Register S.T.A.R. Satellite via body identifier
        mockMvc.perform(post("/api/v1/surveillance/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "S.T.A.R. Satellite",
                                  "distance": 142.7,
                                  "message": ["este", "", "un", "", ""]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("S.T.A.R. Satellite")));

        // Still only 2 devices -> insufficient data
        mockMvc.perform(get("/api/v1/surveillance"))
                .andExpect(status().isNotFound());

        // 3. Register STAR Labs
        mockMvc.perform(post("/api/v1/surveillance/devices/STAR_LABS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "distance": 100.0,
                                  "message": ["este", "", "", "mensaje", ""]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("STAR Labs")));

        // Now all 3 are registered -> process accumulated information succeeds
        mockMvc.perform(get("/api/v1/surveillance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position.x", is(-487.29)))
                .andExpect(jsonPath("$.position.y", is(1557.01)))
                .andExpect(jsonPath("$.message", is("este es un mensaje secreto")));
    }

    // 2.4, 2.5, 2.6. Reemplazo completo (PUT), Actualización parcial (PATCH) y Eliminación (DELETE) (F4, F5, F6)
    @Test
    void shouldSupportFullReplacementPartialUpdateAndDeletionMaintainingConsistency() throws Exception {
        // Register all 3 devices
        mockMvc.perform(post("/api/v1/surveillance/devices/STAR_LABS")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"distance\": 100.0, \"message\": [\"hola\", \"\", \"mundo\"]}")).andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/surveillance/devices/CCPD")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"distance\": 115.5, \"message\": [\"\", \"gran\", \"\"]}")).andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/surveillance/devices/STAR_SATELLITE")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"distance\": 142.7, \"message\": [\"\", \"\", \"mundo\"]}")).andExpect(status().isCreated());

        // 2.4 Full Replacement (PUT) of CCPD without affecting STAR Labs or S.T.A.R. Satellite
        mockMvc.perform(put("/api/v1/surveillance/devices/CCPD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "distance": 200.0,
                                  "message": ["", "nuevo", ""]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("CCPD")))
                .andExpect(jsonPath("$.distance", is(200.0)))
                .andExpect(jsonPath("$.message[1]", is("nuevo")));

        // Verify accumulated message reflects replacement
        mockMvc.perform(get("/api/v1/surveillance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("hola nuevo mundo")));

        // 2.5 Partial Update (PATCH) of CCPD - updating ONLY distance, preserving message
        mockMvc.perform(patch("/api/v1/surveillance/devices/CCPD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "distance": 115.5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance", is(115.5)))
                .andExpect(jsonPath("$.message[1]", is("nuevo")));

        // Partial Update (PATCH) of STAR_SATELLITE - updating ONLY message, preserving distance (142.7)
        mockMvc.perform(patch("/api/v1/surveillance/devices/STAR_SATELLITE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": ["", "", "alquimia"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance", is(142.7)))
                .andExpect(jsonPath("$.message[2]", is("alquimia")));

        // 2.6 Delete (DELETE) STAR_SATELLITE without affecting the other two devices
        mockMvc.perform(delete("/api/v1/surveillance/devices/STAR_SATELLITE"))
                .andExpect(status().isNoContent());

        // Verify remaining devices are 2
        mockMvc.perform(get("/api/v1/surveillance/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Verify accumulated calculation now fails due to insufficient information
        mockMvc.perform(get("/api/v1/surveillance"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleUnknownDevicesAndValidationErrorsGracefully() throws Exception {
        // Unknown device
        mockMvc.perform(post("/api/v1/surveillance/devices/UNKNOWN_DEVICE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"distance\": 100.0, \"message\": [\"test\"]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Unknown Surveillance Device")));

        // Negative distance validation error
        mockMvc.perform(post("/api/v1/surveillance/devices/CCPD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"distance\": -50.0, \"message\": [\"test\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")));
    }
}
