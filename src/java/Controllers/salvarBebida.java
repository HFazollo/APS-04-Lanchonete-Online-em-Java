/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import DAO.DaoBebida;
import Helpers.ValidadorCookie;
import Model.Bebida;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author kener_000
 */
public class salvarBebida extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String json = "";

        boolean resultado = true;

        if (br != null) {
            json = br.readLine();

            if (json != null && !json.isEmpty()) {
                byte[] bytes = json.getBytes(ISO_8859_1);
                String jsonStr = new String(bytes, UTF_8);
                JSONObject dados = new JSONObject(jsonStr);

                if (dados.has("nome") && dados.has("descricao") && dados.has("tipo") && dados.has("ValorCompra")) {

                    Bebida bebida = new Bebida();
                    String tipo = dados.getString("tipo");

                    switch (tipo.toUpperCase()) {
                        case "REFRIGERANTE":
                        case "SUCO":
                        case "ALCOOLICO":
                            bebida.setTipo(tipo);
                            break;
                        default:
                            bebida.setTipo(tipo);
                            break;
                    }

                    bebida.setNome(dados.getString("nome"));
                    bebida.setDescricao(dados.getString("descricao"));
                    bebida.setQuantidade(dados.getInt("quantidade"));
                    bebida.setValor_compra(Double.parseDouble(dados.getString("ValorCompra")));
                    bebida.setValor_venda(Double.parseDouble(dados.getString("ValorVenda")));
                    bebida.setFg_ativo(1);

                    for (int i = 0; i < 1; i++) {
                        DaoBebida bebidaDAO = new DaoBebida();
                        bebidaDAO.salvar(bebida);
                    }


                    try (PrintWriter out = response.getWriter()) {
                        out.println("Bebida Salva!");
                    }
                }
            }
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("erro");
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
