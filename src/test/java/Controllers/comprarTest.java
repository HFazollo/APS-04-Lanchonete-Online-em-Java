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
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class comprarTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

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
    }

/*     @Test
    public void testProcessRequest_Success_WithLancheAndBebida() throws ServletException, IOException {
        String jsonInput = "{\"id\": 1, \"X-Burger\": [1, \"lanche\", 2], \"Coca-Cola\": [2, \"bebida\", 1]}";

        mockRequestInputStream(jsonInput);

        Cookie[] cookies = { new Cookie("token", "valid_token") };
        when(request.getCookies()).thenReturn(cookies);

        Cliente mockCliente = new Cliente();
        mockCliente.setId_cliente(1);

        Lanche mockLanche = new Lanche();
        mockLanche.setNome("X-Burger");
        mockLanche.setValor_venda(10.00);

        Bebida mockBebida = new Bebida();
        mockBebida.setNome("Coca-Cola");
        mockBebida.setValor_venda(5.00);

        Pedido mockPedido = new Pedido();
        mockPedido.setId_pedido(1);

        try (MockedConstruction<ValidadorCookie> mockedValidator = Mockito.mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validar(cookies)).thenReturn(true));
                MockedConstruction<DaoCliente> mockedDaoCliente = Mockito.mockConstruction(DaoCliente.class,
                        (mock, context) -> when(mock.pesquisaPorID("1")).thenReturn(mockCliente));

                MockedConstruction<DaoLanche> mockedDaoLanche = Mockito.mockConstruction(DaoLanche.class,
                        (mock, context) -> when(mock.pesquisaPorNome("X-Burger")).thenReturn(mockLanche));
                MockedConstruction<DaoBebida> mockedDaoBebida = Mockito.mockConstruction(DaoBebida.class,
                        (mock, context) -> when(mock.pesquisaPorNome("Coca-Cola")).thenReturn(mockBebida));

                MockedConstruction<DaoPedido> mockedDaoPedido = Mockito.mockConstruction(DaoPedido.class,
                        (mock, context) -> {
                            doNothing().when(mock).salvar(any(Pedido.class));
                            when(mock.pesquisaPorData(any(Pedido.class))).thenReturn(mockPedido);
                        })) {

            new comprar().processRequest(request, response);

            DaoPedido daoPedidoMockInstance = mockedDaoPedido.constructed().get(0);

            verify(daoPedidoMockInstance).salvar(any(Pedido.class));
            verify(daoPedidoMockInstance).pesquisaPorData(any(Pedido.class));
            verify(daoPedidoMockInstance, times(1)).vincularLanche(any(Pedido.class), any(Lanche.class));
            verify(daoPedidoMockInstance).vincularBebida(any(Pedido.class), any(Bebida.class));
        }

        assertTrue(stringWriter.toString().contains("Pedido Salvo com Sucesso!"));
    }
 */
    @Test
    public void testProcessRequest_InvalidCookie() throws ServletException, IOException {
        mockRequestInputStream("{\"id\": 1}");
        Cookie[] cookies = { new Cookie("token", "invalid_token") };
        when(request.getCookies()).thenReturn(cookies);

        try (MockedConstruction<ValidadorCookie> mockedValidator = Mockito.mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validar(cookies)).thenReturn(false))) {
            new comprar().processRequest(request, response);
        }
        assertTrue(stringWriter.toString().contains("erro"));
    }

    @Test
    public void testProcessRequest_NoCookies() throws ServletException, IOException {
        mockRequestInputStream("{\"id\": 1}");
        when(request.getCookies()).thenReturn(null);

        try (MockedConstruction<ValidadorCookie> mockedValidator = Mockito.mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validar(null)).thenReturn(false))) {
            new comprar().processRequest(request, response);
        }
        assertTrue(stringWriter.toString().contains("erro"));
    }

    @Test
    public void testProcessRequest_EmptyInput() throws ServletException, IOException {
        mockRequestInputStream("");
        Cookie[] cookies = { new Cookie("token", "valid_token") };
        when(request.getCookies()).thenReturn(cookies);

        try (MockedConstruction<ValidadorCookie> mockedValidator = Mockito.mockConstruction(ValidadorCookie.class,
                (mock, context) -> when(mock.validar(cookies)).thenReturn(true))) {
            new comprar().processRequest(request, response);
        }
        assertTrue(stringWriter.toString().contains("erro"));
    }
}