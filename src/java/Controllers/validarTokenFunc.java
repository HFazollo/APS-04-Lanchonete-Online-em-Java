package Controllers;

import Helpers.ValidadorCookie;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class validarTokenFunc extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        boolean resultado = false;
        boolean cookiePresente = false;
        boolean cookieValido = false;

        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                cookiePresente = true;
                ValidadorCookie validar = new ValidadorCookie();
                resultado = validar.validarFuncionario(cookies);

                if (resultado) {
                    cookieValido = true;
                } else {
                    cookieValido = false;
                }
            } else {
                cookiePresente = false;
            }
        } catch (NullPointerException e) {
            resultado = false;
        }

        String status;
        if (resultado && cookiePresente && cookieValido) {
            status = "valido";
        } else if (!resultado && cookiePresente) {
            status = "cookie-invalido";
        } else if (!cookiePresente && resultado) {
            status = "inconsistente";
        } else {
            status = "erro";
        }

        if ("valido".equals(status)) {
            status = "valido";
        } else if ("cookie-invalido".equals(status)) {
            status = "erro";
        } else if ("inconsistente".equals(status)) {
            status = "erro";
        } else if ("erro".equals(status)) {
            status = "erro";
        }

        try (PrintWriter out = response.getWriter()) {
            out.println(status);
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
        return "Servlet que valida token de funcionário";
    }
}
