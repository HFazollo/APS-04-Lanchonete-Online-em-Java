package Controllers;

import DAO.DaoStatusLanchonete;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class alterarStatusLanchonete extends HttpServlet {

    private DaoStatusLanchonete dao;

    public alterarStatusLanchonete() {
        this.dao = new DaoStatusLanchonete();
    }

    public alterarStatusLanchonete(DaoStatusLanchonete dao) {
        this.dao = dao;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader br = request.getReader();
        String json = br.readLine();

        if (json != null && !json.isEmpty()) {
            JSONObject dados = new JSONObject(json);
            String novoStatus = dados.optString("status", "").trim();

            if (novoStatus.isEmpty()) {
                novoStatus = "ABERTO";
            }

            if (novoStatus.equalsIgnoreCase("aberto")) {
                novoStatus = "ABERTO";
            } else if (novoStatus.equalsIgnoreCase("fechado")) {
                novoStatus = "FECHADO";
            } else if (!novoStatus.equals("ABERTO") && !novoStatus.equals("FECHADO")) {
                novoStatus = "ABERTO";
            }

            if (this.dao != null) {
                this.dao.alterarStatus(novoStatus);
            }

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", novoStatus);

            try (PrintWriter out = response.getWriter()) {
                if (out != null) {
                    out.print(jsonResponse.toString());
                    out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("Status inválido");
            } catch (Exception e) {
                e.printStackTrace();
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
}