package Controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import DAO.DaoBebida;

@ExtendWith(MockitoExtension.class)
public class salvarBebidaTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    public void testSalvarBebida() throws Exception {
        String jsonBebida = "{\"nome\":\"Cerveja\",\"descricao\":\"Lata 350ml\",\"quantidade\":10,\"ValorCompra\":\"2.50\",\"ValorVenda\":\"4.00\",\"tipo\":\"Alcoólica\"}";

        InputStream inputStream = new ByteArrayInputStream(jsonBebida.getBytes("UTF-8"));
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
            public void setReadListener(ReadListener readListener) {}

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };

        when(request.getInputStream()).thenReturn(servletInputStream);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        try (MockedConstruction<DaoBebida> mockedDao = Mockito.mockConstruction(DaoBebida.class)) {
            
            new salvarBebida().doPost(request, response);

            writer.flush();
            assertEquals("Bebida Salva!", stringWriter.toString().trim());
        }
    }
}