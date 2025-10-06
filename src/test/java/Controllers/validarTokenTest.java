package Controllers;

import Helpers.ValidadorCookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class validarTokenTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    public void testCookieValido() throws Exception {
        Cookie[] cookies = {new Cookie("token", "12345")};
        when(request.getCookies()).thenReturn(cookies);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        try (MockedConstruction<ValidadorCookie> mockedValidador =
                     org.mockito.Mockito.mockConstruction(ValidadorCookie.class, (mock, context) -> {
                         when(mock.validar(cookies)).thenReturn(true);
                     })) {

            new validarToken().doGet(request, response);
            writer.flush();

            assertEquals("valido", stringWriter.toString().trim());
        }
    }


    @Test
    public void testCookieInvalido() throws Exception {
        Cookie[] cookies = {new Cookie("token", "abc")};
        when(request.getCookies()).thenReturn(cookies);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        try (MockedConstruction<ValidadorCookie> mockedValidador =
                     org.mockito.Mockito.mockConstruction(ValidadorCookie.class, (mock, context) -> {
                         when(mock.validar(cookies)).thenReturn(false);
                     })) {

            new validarToken().doPost(request, response);
            writer.flush();

            assertEquals("erro", stringWriter.toString().trim());
        }
    }


    @Test
    public void testValidadorLancaExcecao() throws Exception {
        Cookie[] cookies = {new Cookie("token", "123")};
        when(request.getCookies()).thenReturn(cookies);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        try (MockedConstruction<ValidadorCookie> mockedValidador =
                     org.mockito.Mockito.mockConstruction(ValidadorCookie.class, (mock, context) -> {
                         when(mock.validar(cookies)).thenThrow(new RuntimeException("Erro interno"));
                     })) {

            new validarToken().doPost(request, response);
            writer.flush();

            assertEquals("erro", stringWriter.toString().trim());
        }
    }

    @Test
    public void testSemCookies() throws Exception {
        when(request.getCookies()).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Mock para o construtor do ValidadorCookie (não deve ser chamado)
        try (MockedConstruction<ValidadorCookie> ignored =
                     org.mockito.Mockito.mockConstruction(ValidadorCookie.class)) {

            new validarToken().doGet(request, response);
            writer.flush();

            assertEquals("erro", stringWriter.toString().trim());
        }
    }
}
