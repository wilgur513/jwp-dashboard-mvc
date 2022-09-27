package com.techcourse.controller;

import static nextstep.test.MockRequestBuilder.get;
import static nextstep.test.MockRequestBuilder.post;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcourse.domain.User;
import com.techcourse.repository.InMemoryUserRepository;
import java.util.Optional;
import nextstep.mvc.controller.tobe.AnnotationHandlerAdapter;
import nextstep.mvc.controller.tobe.AnnotationHandlerMapping;
import nextstep.test.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ControllerTest {

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = new MockMvc(
                new AnnotationHandlerMapping("com.techcourse"),
                new AnnotationHandlerAdapter()
        );

        objectMapper = new ObjectMapper();
    }

    @Test
    void forwardIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .forwardTo("/index.jsp");
    }

    @Test
    void loginSuccessfully() throws Exception {
        mockMvc.perform(post("/login")
                .param("account", "gugu")
                .param("password", "password")
        )
                .redirectTo("/index.jsp");
    }

    @Test
    void loginFailed() throws Exception {
        mockMvc.perform(post("/login")
                .param("account", "gugu")
                .param("password", "invalid")
        )
                .redirectTo("/401.jsp");
    }

    @Test
    void loginView() throws Exception {
        mockMvc.perform(get("/login/view"))
                .forwardTo("/login.jsp");
    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(get("/logout"))
                .redirectTo("/");
    }

    @Test
    void register() throws Exception {
        mockMvc.perform(post("/register")
                .param("account", "verus")
                .param("password", "password")
                .param("email", "verus@email.com")
        )
                .redirectTo("/index.jsp");

        final Optional<User> verus = InMemoryUserRepository.findByAccount("verus");
        assertThat(verus).isPresent();
    }

    @Test
    void registerView() throws Exception {
        mockMvc.perform(get("/register/view"))
                .forwardTo("/register.jsp");
    }

    @Test
    void getUserByJsonBody() throws Exception {
        User user = InMemoryUserRepository.findByAccount("gugu").orElseThrow();

        mockMvc.perform(get("/api/user")
                .param("account", "gugu")
        )
                .jsonBody(objectMapper.writeValueAsString(user));
    }
}
