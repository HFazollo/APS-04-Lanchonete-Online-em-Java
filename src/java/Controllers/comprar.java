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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class comprar extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 1. Leitura do corpo da requisição (JSON)
        StringBuilder sb = new StringBuilder();
        BufferedReader br = request.getReader();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();

        // 2. Validação do Cookie
        boolean cookieValido = false;
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                ValidadorCookie validar = new ValidadorCookie();
                cookieValido = validar.validar(cookies);
            }
        } catch (Exception e) {
            // O erro será tratado pela verificação do cookieValido
        }

        // 3. Verifica se o JSON está vazio ou se o cookie é inválido
        if (json == null || json.isEmpty() || !cookieValido) {
            try (PrintWriter out = response.getWriter()) {
                out.println("erro");
            }
            return;
        }

        // 4. Processamento do Pedido
        try {
            JSONObject dados = new JSONObject(json);
            DaoCliente clienteDao = new DaoCliente();
            Cliente cliente = clienteDao.pesquisaPorID(String.valueOf(dados.getInt("id")));

            Iterator<String> keys = dados.keys();
            Double valor_total = 0.00;
            List<Lanche> lanches = new ArrayList<>();
            List<Bebida> bebidas = new ArrayList<>();

            while (keys.hasNext()) {
                String nome = keys.next();
                if (nome.equals("id")) continue; // Pula a chave 'id'

                if (dados.getJSONArray(nome).get(1).equals("lanche")) {
                    DaoLanche lancheDao = new DaoLanche();
                    Lanche lanche = lancheDao.pesquisaPorNome(nome);
                    int quantidade = dados.getJSONArray(nome).getInt(2);
                    lanche.setQuantidade(quantidade);
                    valor_total += lanche.getValor_venda() * quantidade; // CORREÇÃO: Multiplica valor pela quantidade
                    lanches.add(lanche);
                } else if (dados.getJSONArray(nome).get(1).equals("bebida")) {
                    DaoBebida bebidaDao = new DaoBebida();
                    Bebida bebida = bebidaDao.pesquisaPorNome(nome);
                    int quantidade = dados.getJSONArray(nome).getInt(2);
                    bebida.setQuantidade(quantidade);
                    valor_total += bebida.getValor_venda() * quantidade; // CORREÇÃO: Multiplica valor pela quantidade
                    bebidas.add(bebida);
                }
            }

            DaoPedido pedidoDao = new DaoPedido();
            Pedido pedido = new Pedido();
            pedido.setData_pedido(Instant.now().toString());
            pedido.setCliente(cliente);
            pedido.setValor_total(valor_total);
            
            pedidoDao.salvar(pedido);
            Pedido pedidoSalvo = pedidoDao.pesquisaPorData(pedido);
            pedidoSalvo.setCliente(cliente);

            for (Lanche lanche : lanches) {
                pedidoDao.vincularLanche(pedidoSalvo, lanche);
            }
            for (Bebida bebida : bebidas) {
                pedidoDao.vincularBebida(pedidoSalvo, bebida);
            }

            try (PrintWriter out = response.getWriter()) {
                out.println("Pedido Salvo com Sucesso!");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Informa que a requisição foi mal formatada
            try (PrintWriter out = response.getWriter()) {
                out.println("erro");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para processar compras de lanches e bebidas.";
    }
}
