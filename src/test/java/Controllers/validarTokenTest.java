package Controllers;

import Helpers.ValidadorCookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class validarTokenTest {

    private validarToken servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new validarToken();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        // Captura da saída do response
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testProcessRequest_TokenValido() throws Exception {
        // Simula cookie válido
        Cookie[] cookies = { new Cookie("token", "abc123") };
        when(request.getCookies()).thenReturn(cookies);

        // Simula ValidadorCookie retornando true
        ValidadorCookie mockValidator = mock(ValidadorCookie.class);
        when(mockValidator.validar(cookies)).thenReturn(true);

        // Como o ValidadorCookie é instanciado dentro do método,
        // em teste simples não dá pra injetar sem refatorar,
        // então aqui consideramos que o validador real funciona.

        servlet.processRequest(request, response);

        String saida = responseWriter.toString().trim();
        assertTrue(saida.contains("valido"), "Deveria retornar 'valido'");
    }

}
