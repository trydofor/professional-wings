package pro.fessional.wings.warlock.security.justauth;

import io.qameta.allure.TmsLink;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.data.Null;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pro.fessional.wings.warlock.security.justauth.AuthStateBuilder.KeyStateArr;
import static pro.fessional.wings.warlock.security.justauth.AuthStateBuilder.ParamState;

/**
 * @author robbietree8
 * @since 2022-08-22
 */
public class AuthStateBuilderTest {
    private AuthStateBuilder authStateBuilder;

    @BeforeEach
    public void setup() {
        authStateBuilder = new AuthStateBuilder(Map.of("/login", "{1}/#{0}{2}"));
    }

    @Test
    @TmsLink("C14038")
    public void authStateBuilder() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(Map.of(ParamState, new String[] {"/login", "http://localhost:8080"}));
        final String state = authStateBuilder.buildState(request);

        when(request.getParameter(ParamState)).thenReturn(state);
        final Map<String, String[]> map = authStateBuilder.parseParam(request);
        final String[] args = map.getOrDefault(KeyStateArr, Null.StrArr);

        assertEquals("/login", args[0]);
        assertEquals("http://localhost:8080", args[1]);
    }
}
