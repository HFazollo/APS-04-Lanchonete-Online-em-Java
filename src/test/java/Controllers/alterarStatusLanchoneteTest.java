package Controllers;

import DAO.DaoStatusLanchonete;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class alterarStatusLanchoneteTest {

    private alterarStatusLanchonete servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private DaoStatusLanchonete mockDao;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        mockDao = mock(DaoStatusLanchonete.class);

        servlet = new alterarStatusLanchonete(mockDao);

        responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testStatusAbertoValido() throws Exception {
        String jsonInput = "{\"status\": \"ABERTO\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));

        servlet.doPost(request, response);

        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockDao).alterarStatus(statusCaptor.capture());
        assertEquals("ABERTO", statusCaptor.getValue());

        JSONObject jsonResponse = new JSONObject(responseWriter.toString());
        assertEquals("ABERTO", jsonResponse.getString("status"));
    }

    @Test
    public void testStatusFechadoCaixaBaixaNormalizado() throws Exception {
        String jsonInput = "{\"status\": \"fechado\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));

        servlet.doPost(request, response);

        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockDao).alterarStatus(statusCaptor.capture());
        assertEquals("FECHADO", statusCaptor.getValue());

        JSONObject jsonResponse = new JSONObject(responseWriter.toString());
        assertEquals("FECHADO", jsonResponse.getString("status"));
    }

    @Test
    public void testStatusInvalidoConversaoParaAberto() throws Exception {
        String jsonInput = "{\"status\": \"INDISPONIVEL\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));

        servlet.doPost(request, response);

        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockDao).alterarStatus(statusCaptor.capture());
        assertEquals("ABERTO", statusCaptor.getValue());

        JSONObject jsonResponse = new JSONObject(responseWriter.toString());
        assertEquals("ABERTO", jsonResponse.getString("status"));
    }

    @Test
    public void testStatusVazioConversaoParaAberto() throws Exception {
        String jsonInput = "{\"status\": \"\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));

        servlet.doPost(request, response);

        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockDao).alterarStatus(statusCaptor.capture());
        assertEquals("ABERTO", statusCaptor.getValue());
    }

    @Test
    public void testStatusAusenteConverteParaAberto() throws Exception {
        String jsonInput = "{}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));

        servlet.doPost(request, response);

        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockDao).alterarStatus(statusCaptor.capture());
        assertEquals("ABERTO", statusCaptor.getValue());
    }

/*     @Test
    public void testRequisicaoSemBody() throws Exception {
        BufferedReader mockReader = mock(BufferedReader.class);
        when(mockReader.readLine()).thenReturn(null);
        when(request.getReader()).thenReturn(mockReader);

        servlet.doPost(request, response);

        verify(mockDao, never()).alterarStatus(anyString());

        JSONObject jsonResponse = new JSONObject(responseWriter.toString());
        assertEquals("erro", jsonResponse.getString("status"));
        assertEquals("Corpo da requisição nulo ou inválido.", jsonResponse.getString("mensagem"));
    } */

    @Test
    public void testFalhaNaPersistenciaGeraExcecao() throws Exception {

        String jsonInput = "{\"status\": \"FECHADO\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        doThrow(new RuntimeException("Simulated DB Error")).when(mockDao).alterarStatus(anyString());

        assertThrows(RuntimeException.class, () -> {
            servlet.doPost(request, response);
        });

        verify(mockDao).alterarStatus(anyString());
    }
}