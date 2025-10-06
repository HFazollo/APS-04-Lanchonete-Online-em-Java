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

    private final ValidadorCookie validadorCookie;
    private final DaoCliente daoCliente;
    private final DaoLanche daoLanche;
    private final DaoBebida daoBebida;
    private final DaoPedido daoPedido;

    public comprar() {
        this.validadorCookie = new ValidadorCookie();
        this.daoCliente = new DaoCliente();
        this.daoLanche = new DaoLanche();
        this.daoBebida = new DaoBebida();
        this.daoPedido = new DaoPedido();
    }

    public comprar(ValidadorCookie validadorCookie, DaoCliente daoCliente, DaoLanche daoLanche, DaoBebida daoBebida,
            DaoPedido daoPedido) {
        this.validadorCookie = validadorCookie;
        this.daoCliente = daoCliente;
        this.daoLanche = daoLanche;
        this.daoBebida = daoBebida;
        this.daoPedido = daoPedido;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        BufferedReader br = request.getReader();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();

        boolean cookieValido = false;
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                cookieValido = this.validadorCookie.validar(cookies);
            }
        } catch (Exception e) {
        }

        if (json == null || json.isEmpty() || !cookieValido) {
            try (PrintWriter out = response.getWriter()) {
                out.println("erro");
            }
            return;
        }

        try {
            JSONObject dados = new JSONObject(json);
            Cliente cliente = this.daoCliente.pesquisaPorID(String.valueOf(dados.getInt("id")));

            Iterator<String> keys = dados.keys();
            Double valor_total = 0.00;
            List<Lanche> lanches = new ArrayList<>();
            List<Bebida> bebidas = new ArrayList<>();

            while (keys.hasNext()) {
                String nome = keys.next();
                if (nome.equals("id"))
                    continue;

                if (dados.getJSONArray(nome).get(1).equals("lanche")) {
                    Lanche lanche = this.daoLanche.pesquisaPorNome(nome);
                    int quantidade = dados.getJSONArray(nome).getInt(2);
                    lanche.setQuantidade(quantidade);
                    valor_total += lanche.getValor_venda() * quantidade;
                    lanches.add(lanche);
                } else if (dados.getJSONArray(nome).get(1).equals("bebida")) {
                    Bebida bebida = this.daoBebida.pesquisaPorNome(nome);
                    int quantidade = dados.getJSONArray(nome).getInt(2);
                    bebida.setQuantidade(quantidade);
                    valor_total += bebida.getValor_venda() * quantidade;
                    bebidas.add(bebida);
                }
            }

            Pedido pedido = new Pedido();
            pedido.setData_pedido(Instant.now().toString());
            pedido.setCliente(cliente);
            pedido.setValor_total(valor_total);

            this.daoPedido.salvar(pedido);
            Pedido pedidoSalvo = this.daoPedido.pesquisaPorData(pedido);
            pedidoSalvo.setCliente(cliente);

            for (Lanche lanche : lanches) {
                this.daoPedido.vincularLanche(pedidoSalvo, lanche);
            }
            for (Bebida bebida : bebidas) {
                this.daoPedido.vincularBebida(pedidoSalvo, bebida);
            }

            try (PrintWriter out = response.getWriter()) {
                out.println("Pedido Salvo com Sucesso!");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
