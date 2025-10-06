package Controllers;

import DAO.DaoBebida;
import Helpers.ValidadorCookie;
import Model.Bebida;
import com.google.gson.Gson;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class getBebidasTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    void testGetBebidasComSucesso() throws Exception {

        Cookie[] cookies = {new Cookie("tipoUsuario", "funcionario")};
        when(request.getCookies()).thenReturn(cookies);


        Bebida b1 = new Bebida(); 
        b1.setNome("Coca-Cola");
        Bebida b2 = new Bebida();
        b2.setNome("Pepsi");
        List<Bebida> listaDeBebidasMock = Arrays.asList(b1, b2);
        String expectedJson = new Gson().toJson(listaDeBebidasMock);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

 
        try (MockedConstruction<ValidadorCookie> mockedValidator = Mockito.mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any(Cookie[].class))).thenReturn(true));
             MockedConstruction<DaoBebida> mockedDao = Mockito.mockConstruction(DaoBebida.class,
                (mock, context) -> {
  
                    when(mock.listarTodos()).thenReturn(listaDeBebidasMock);
                })) {

            new getBebidas().doGet(request, response);
        }


        writer.flush();
        assertEquals(expectedJson, stringWriter.toString().trim());
    }

    @Test
    void testGetBebidasSemAutorizacao() throws Exception {

        when(request.getCookies()).thenReturn(null); 

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);


        try (MockedConstruction<ValidadorCookie> mockedValidator = Mockito.mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validarFuncionario(any())).thenReturn(false))) {

            new getBebidas().doGet(request, response);
        }


        writer.flush();
        assertEquals("erro", stringWriter.toString().trim());
    }
}