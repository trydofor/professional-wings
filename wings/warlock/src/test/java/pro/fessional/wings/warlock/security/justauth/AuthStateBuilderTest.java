package pro.fessional.wings.warlock.security.justauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.data.Null;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pro.fessional.wings.warlock.security.justauth.AuthStateBuilder.KeyStateArr;
import static pro.fessional.wings.warlock.security.justauth.AuthStateBuilder.ParamState;

public class AuthStateBuilderTest {
    private AuthStateBuilder authStateBuilder;

    @BeforeEach
    public void setup() {
        authStateBuilder = new AuthStateBuilder(Map.of("/login", "{1}/#{0}{2}"));
    }

    @Test
    public void test() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(Map.of(ParamState, new String[] {"/login", "http://localhost:8080"}));
        final String state = authStateBuilder.buildState(request);

        when(request.getParameter(ParamState)).thenReturn(state);
        final Map<String, Object> map = authStateBuilder.parseParam(request);
        final String[] args = (String[]) map.getOrDefault(KeyStateArr, Null.StrArr);

        assertEquals("/login", args[0]);
        assertEquals("http://localhost:8080", args[1]);
    }
}
