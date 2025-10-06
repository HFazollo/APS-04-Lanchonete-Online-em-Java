package Controllers;

import DAO.DaoBebida;
import Helpers.ValidadorCookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class alterarBebidaTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private void mockRequestInputStream(String json) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(json.getBytes("UTF-8"));
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override public boolean isFinished() { return false; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(ReadListener readListener) {}
            @Override public int read() throws IOException { return inputStream.read(); }
        };
        when(request.getInputStream()).thenReturn(servletInputStream);
    }

    @Test
    void testAlterarBebidaComSucesso() throws Exception {
        String jsonBebida = "{\"id\":1,\"nome\":\"Cerveja Especial\",\"descricao\":\"Garrafa 600ml\",\"quantidade\":20,\"ValorCompra\":5.00,\"ValorVenda\":8.00,\"tipo\":\"Alcoólica\"}";
        mockRequestInputStream(jsonBebida);

        Cookie[] cookies = {new Cookie("tipoUsuario", "funcionario")};
        when(request.getCookies()).thenReturn(cookies);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        try (MockedConstruction<ValidadorCookie> mockedValidator = Mockito.mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(true));
             MockedConstruction<DaoBebida> mockedDao = Mockito.mockConstruction(DaoBebida.class)) {

            new alterarBebida().doPost(request, response);
        }

        writer.flush();
        assertEquals("Bebida Alterada!", stringWriter.toString().trim());
    }

    @Test
    void testAlterarBebidaSemAutorizacao() throws Exception {
        mockRequestInputStream("{}");
        when(request.getCookies()).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        try (MockedConstruction<ValidadorCookie> mockedValidator = Mockito.mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any())).thenReturn(false))) {

            new alterarBebida().doPost(request, response);
        }

        writer.flush();
        assertEquals("erro", stringWriter.toString().trim());
    }
}