package Controllers;

import Helpers.ValidadorCookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class validarTokenFuncTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testCookieValido() throws Exception {
        Cookie[] cookies = {new Cookie("tokenFuncionario", "123-abc")};
        when(request.getCookies()).thenReturn(cookies);

        try (MockedConstruction<ValidadorCookie> mocked =
                     Mockito.mockConstruction(ValidadorCookie.class,
                             (mock, context) -> when(mock.validarFuncionario(cookies)).thenReturn(true))) {

            new validarTokenFunc().doPost(request, response);

            writer.flush();
            assertEquals("valido", stringWriter.toString().trim());
        }
    }

    @Test
    public void testCookieInvalido() throws Exception {
        Cookie[] cookies = {new Cookie("tokenFuncionario", "invalid")};
        when(request.getCookies()).thenReturn(cookies);

        try (MockedConstruction<ValidadorCookie> mocked =
                     Mockito.mockConstruction(ValidadorCookie.class,
                             (mock, context) -> when(mock.validarFuncionario(cookies)).thenReturn(false))) {

            new validarTokenFunc().doPost(request, response);

            writer.flush();
            assertEquals("erro", stringWriter.toString().trim());
        }
    }

    @Test
    public void testSemCookies() throws Exception {
        when(request.getCookies()).thenReturn(null);

        try (MockedConstruction<ValidadorCookie> mocked =
                     Mockito.mockConstruction(ValidadorCookie.class)) {

            new validarTokenFunc().doPost(request, response);

            writer.flush();
            assertEquals("erro", stringWriter.toString().trim());
        }
    }

    @Test
    public void testExcecaoNoValidador() throws Exception {
        Cookie[] cookies = {new Cookie("tokenFuncionario", "test")};
        when(request.getCookies()).thenReturn(cookies);

        try (MockedConstruction<ValidadorCookie> mocked =
                     Mockito.mockConstruction(ValidadorCookie.class,
                             (mock, context) -> when(mock.validarFuncionario(any())).thenThrow(new NullPointerException("simulated")))) {

            new validarTokenFunc().doPost(request, response);

            writer.flush();
            assertEquals("erro", stringWriter.toString().trim());
        }
    }
}
