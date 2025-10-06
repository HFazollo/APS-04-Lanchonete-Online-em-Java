package Helpers;

import DAO.DaoToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ValidadorCookieTest {

    @Mock
    private DaoToken daoToken;

    @InjectMocks
    private ValidadorCookie validadorCookie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidarCookieClienteValido() {
        Cookie[] cookies = {new Cookie("token", "tokenValidoCliente-123")};
        when(daoToken.validar("tokenValidoCliente-123")).thenReturn(true);

        assertTrue(validadorCookie.validar(cookies));
        verify(daoToken, times(1)).validar("tokenValidoCliente-123");
    }

    @Test
    void testValidarCookieClienteInvalido() {
        Cookie[] cookies = {new Cookie("token", "tokenInvalidoCliente-123")};
        when(daoToken.validar("tokenInvalidoCliente-123")).thenReturn(false);

        assertFalse(validadorCookie.validar(cookies));
        verify(daoToken, times(1)).validar("tokenInvalidoCliente-123");
    }

    @Test
    void testValidarCookieClienteAusente() {
        Cookie[] cookies = {new Cookie("outroCookie", "valor")};

        assertFalse(validadorCookie.validar(cookies));
        verify(daoToken, never()).validar(anyString());
    }

    @Test
    void testValidarCookieClienteMultiplosValido() {
        Cookie[] cookies = {
                new Cookie("session", "abc"),
                new Cookie("token", "tokenValidoCliente-123"),
                new Cookie("user", "test")
        };
        when(daoToken.validar("tokenValidoCliente-123")).thenReturn(true);

        assertTrue(validadorCookie.validar(cookies));
        verify(daoToken, times(1)).validar("tokenValidoCliente-123");
    }

    @Test
    void testValidarCookieFuncionarioValido() {
        Cookie[] cookies = {new Cookie("tokenFuncionario", "tokenValidoFuncionario-456")};
        when(daoToken.validar("tokenValidoFuncionario-456")).thenReturn(true);

        assertTrue(validadorCookie.validarFuncionario(cookies));
        verify(daoToken, times(1)).validar("tokenValidoFuncionario-456");
    }

    @Test
    void testValidarCookieFuncionarioInvalido() {
        Cookie[] cookies = {new Cookie("tokenFuncionario", "tokenInvalidoFuncionario-456")};
        when(daoToken.validar("tokenInvalidoFuncionario-456")).thenReturn(false);

        assertFalse(validadorCookie.validarFuncionario(cookies));
        verify(daoToken, times(1)).validar("tokenInvalidoFuncionario-456");
    }

    @Test
    void testValidarCookieFuncionarioAusente() {
        Cookie[] cookies = {new Cookie("outroCookie", "valor")};

        assertFalse(validadorCookie.validarFuncionario(cookies));
        verify(daoToken, never()).validar(anyString());
    }

    @Test
    void testDeletarCookiesClienteEFuncionario() {
        Cookie[] cookies = {
                new Cookie("token", "tokenCliente-123"),
                new Cookie("tokenFuncionario", "tokenFuncionario-456"),
                new Cookie("outroCookie", "valor")
        };
        doNothing().when(daoToken).remover(anyString());

        assertDoesNotThrow(() -> validadorCookie.deletar(cookies));
        verify(daoToken, times(1)).remover("tokenCliente-123");
        verify(daoToken, times(1)).remover("tokenFuncionario-456");
    }

    @Test
    void testDeletarNenhumCookieDeToken() {
        Cookie[] cookies = {new Cookie("outroCookie", "valor")};

        assertDoesNotThrow(() -> validadorCookie.deletar(cookies));
        verify(daoToken, never()).remover(anyString());
    }

    @Test
    void testDeletarCookieComExcecao() {
        Cookie[] cookies = {new Cookie("token", "tokenComErro-123")};
        doThrow(new RuntimeException("Erro de remoção")).when(daoToken).remover("tokenComErro-123");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            validadorCookie.deletar(cookies);
        });
        assertEquals("Erro de remoção", thrown.getMessage());
        verify(daoToken, times(1)).remover("tokenComErro-123");
    }

    @Test
    void testGetCookieIdClientePresente() {
        Cookie[] cookies = {new Cookie("token", "123-tokenCliente")};

        assertEquals("123", validadorCookie.getCookieIdCliente(cookies));
    }

    @Test
    void testGetCookieIdClienteAusente() {
        Cookie[] cookies = {new Cookie("outroCookie", "valor")};

        assertEquals("erro", validadorCookie.getCookieIdCliente(cookies));
    }

    @Test
    void testGetCookieIdClienteMalFormatado() {
        Cookie[] cookies = {new Cookie("token", "tokenClienteSemHifen")};

        assertEquals("tokenClienteSemHifen", validadorCookie.getCookieIdCliente(cookies));
    }

    @Test
    void testGetCookieIdFuncionarioPresente() {
        Cookie[] cookies = {new Cookie("tokenFuncionario", "456-tokenFuncionario")};

        assertEquals("456", validadorCookie.getCookieIdFuncionario(cookies));
    }

    @Test
    void testGetCookieIdFuncionarioAusente() {
        Cookie[] cookies = {new Cookie("outroCookie", "valor")};

        assertEquals("erro", validadorCookie.getCookieIdFuncionario(cookies));
    }

    @Test
    void testGetCookieIdFuncionarioMalFormatado() {
        Cookie[] cookies = {new Cookie("tokenFuncionario", "tokenFuncionarioSemHifen")};

        assertEquals("tokenFuncionarioSemHifen", validadorCookie.getCookieIdFuncionario(cookies));
    }

    @Test
    void testValidarComCookiesVazio() {
        Cookie[] cookies = {};
        assertFalse(validadorCookie.validar(cookies));
        verify(daoToken, never()).validar(anyString());
    }

    @Test
    void testValidarFuncionarioComCookiesVazio() {
        Cookie[] cookies = {};
        assertFalse(validadorCookie.validarFuncionario(cookies));
        verify(daoToken, never()).validar(anyString());
    }

    @Test
    void testDeletarComCookiesVazio() {
        Cookie[] cookies = {};
        assertDoesNotThrow(() -> validadorCookie.deletar(cookies));
        verify(daoToken, never()).remover(anyString());
    }

    @Test
    void testGetCookieIdClienteComCookiesVazio() {
        Cookie[] cookies = {};
        assertEquals("erro", validadorCookie.getCookieIdCliente(cookies));
    }

    @Test
    void testGetCookieIdFuncionarioComCookiesVazio() {
        Cookie[] cookies = {};
        assertEquals("erro", validadorCookie.getCookieIdFuncionario(cookies));
    }
}
