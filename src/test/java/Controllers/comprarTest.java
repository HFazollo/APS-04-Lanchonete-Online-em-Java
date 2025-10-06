package Controllers;

import DAO.DaoBebida;
import DAO.DaoCliente;
import DAO.DaoLanche;
import DAO.DaoPedido;
import Helpers.ValidadorCookie;
import Model.Bebida;
import Model.Cliente;
import Model.Lanche;
import Model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class comprarTest {

    @InjectMocks
    private comprar servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ValidadorCookie validadorCookie;

    @Mock
    private DaoCliente daoCliente;

    @Mock
    private DaoLanche daoLanche;

    @Mock
    private DaoBebida daoBebida;

    @Mock
    private DaoPedido daoPedido;

    private StringWriter stringWriter;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    private void mockRequestInputStream(String jsonInput) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(jsonInput.getBytes("UTF-8"));
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(inputStream)));
    }

    @Test
    public void testProcessRequest_Success_WithLancheAndBebida() throws ServletException, IOException {
        String jsonInput = "{\"id\": 1, \"X-Burger\": [1, \"lanche\", 2], \"Coca-Cola\": [2, \"bebida\", 1]}";
        mockRequestInputStream(jsonInput);

        Cookie[] cookies = {new Cookie("token", "valid_token")};
        when(request.getCookies()).thenReturn(cookies);
        when(validadorCookie.validar(cookies)).thenReturn(true);

        Cliente mockCliente = new Cliente();
        mockCliente.setId_cliente(1);
        when(daoCliente.pesquisaPorID("1")).thenReturn(mockCliente);

        Lanche mockLanche = new Lanche();
        mockLanche.setNome("X-Burger");
        mockLanche.setValor_venda(10.00);
        when(daoLanche.pesquisaPorNome("X-Burger")).thenReturn(mockLanche);

        Bebida mockBebida = new Bebida();
        mockBebida.setNome("Coca-Cola");
        mockBebida.setValor_venda(5.00);
        when(daoBebida.pesquisaPorNome("Coca-Cola")).thenReturn(mockBebida);

        Pedido mockPedido = new Pedido();
        mockPedido.setId_pedido(1);
        doNothing().when(daoPedido).salvar(any(Pedido.class));
        when(daoPedido.pesquisaPorData(any(Pedido.class))).thenReturn(mockPedido);

        servlet.processRequest(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(validadorCookie).validar(cookies);
        verify(daoCliente).pesquisaPorID("1");
        verify(daoLanche, times(1)).pesquisaPorNome("X-Burger");
        verify(daoBebida, times(1)).pesquisaPorNome("Coca-Cola");
        verify(daoPedido).salvar(any(Pedido.class));
        verify(daoPedido).pesquisaPorData(any(Pedido.class));
        verify(daoPedido).vincularLanche(any(Pedido.class), any(Lanche.class));
        verify(daoPedido).vincularBebida(any(Pedido.class), any(Bebida.class));

        assertTrue(stringWriter.toString().contains("Pedido Salvo com Sucesso!"));
    }

    @Test
    public void testProcessRequest_InvalidCookie() throws ServletException, IOException {
        mockRequestInputStream("{\"id\": 1}");
        Cookie[] cookies = {new Cookie("token", "invalid_token")};
        when(request.getCookies()).thenReturn(cookies);
        when(validadorCookie.validar(cookies)).thenReturn(false);

        servlet.processRequest(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(validadorCookie).validar(cookies);
        verifyNoInteractions(daoCliente, daoLanche, daoBebida, daoPedido);

        assertTrue(stringWriter.toString().contains("erro"));
    }

    @Test
    public void testProcessRequest_NoCookies() throws ServletException, IOException {
        mockRequestInputStream("{\"id\": 1}");
        when(request.getCookies()).thenReturn(null);
        when(validadorCookie.validar(null)).thenReturn(false);

        servlet.processRequest(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verifyNoInteractions(daoCliente, daoLanche, daoBebida, daoPedido);

        assertTrue(stringWriter.toString().contains("erro"));
    }

    @Test
    public void testProcessRequest_EmptyInput() throws ServletException, IOException {
        mockRequestInputStream("");
        Cookie[] cookies = {new Cookie("token", "valid_token")};
        when(request.getCookies()).thenReturn(cookies);
        when(validadorCookie.validar(cookies)).thenReturn(true);

        servlet.processRequest(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(validadorCookie).validar(cookies);

        assertTrue(stringWriter.toString().contains("erro"));
    }
}