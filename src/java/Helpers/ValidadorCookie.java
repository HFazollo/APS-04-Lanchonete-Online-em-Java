package Helpers;

import DAO.DaoToken;
import javax.servlet.http.Cookie;

public class ValidadorCookie {
    private final DaoToken daoToken;

    public ValidadorCookie(DaoToken daoToken) {
        this.daoToken = daoToken;
    }

    public ValidadorCookie() {
        this.daoToken = new DaoToken();
    }

    public boolean validar(Cookie[] cookies) {
        if (cookies == null) return false;
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return daoToken.validar(cookie.getValue());
            }
        }
        return false;
    }

    public boolean validarFuncionario(Cookie[] cookies) {
        if (cookies == null) return false;
        for (Cookie cookie : cookies) {
            if ("tokenFuncionario".equals(cookie.getName())) {
                return daoToken.validar(cookie.getValue());
            }
        }
        return false;
    }

    public void deletar(Cookie[] cookies) {
        if (cookies == null) return;
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            if ("tokenFuncionario".equals(name) || "token".equals(name)) {
                daoToken.remover(value);
            }
        }
    }

    public String getCookieIdCliente(Cookie[] cookies) {
        if (cookies == null) return "erro";
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue().split("-")[0];
            }
        }
        return "erro";
    }

    public String getCookieIdFuncionario(Cookie[] cookies) {
        if (cookies == null) return "erro";
        for (Cookie cookie : cookies) {
            if ("tokenFuncionario".equals(cookie.getName())) {
                return cookie.getValue().split("-")[0];
            }
        }
        return "erro";
    }
}
