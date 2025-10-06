package Helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptadorMD5Test {

    private EncryptadorMD5 encryptador;

    @BeforeEach
    void setUp() {
        encryptador = new EncryptadorMD5();
    }

    @Test
    @DisplayName("Deve encriptar uma senha comum corretamente")
    void testEncryptarComSenhaValida() {
        String senha = "senha123";
        String hashEsperado = "e7d80ffeefa212b7c5c55700e4f7193e";

        String hashResultado = encryptador.encryptar(senha);

        assertEquals(hashEsperado, hashResultado, "O hash MD5 gerado não corresponde ao esperado.");
    }

    @Test
    @DisplayName("Deve encriptar uma string vazia corretamente")
    void testEncryptarComStringVazia() {

        String senhaVazia = "";

        String hashEsperado = "d41d8cd98f00b204e9800998ecf8427e";

        String hashResultado = encryptador.encryptar(senhaVazia);

        assertEquals(hashEsperado, hashResultado, "O hash de uma string vazia não foi calculado corretamente.");
    }

    @Test
    @DisplayName("Deve retornar null ao receber uma entrada nula")
    void testEncryptarComInputNulo() {

        String senhaNula = null;

        String hashResultado = encryptador.encryptar(senhaNula);

        assertNull(hashResultado, "Deveria retornar null quando a entrada é nula.");
    }

    @Test
    @DisplayName("Deve garantir que o hash tenha sempre 32 caracteres com preenchimento de zeros")
    void testEncryptarGarantePaddingDeZeros() {

        String senha = "a"; // O hash de "a" é 0cc175b9c0f1b6a831c399e269772661

        String hashResultado = encryptador.encryptar(senha);

        assertEquals(32, hashResultado.length(), "O hash final deve sempre ter 32 caracteres.");
        assertTrue(hashResultado.startsWith("0"), "O hash para 'a' deveria começar com '0'.");
    }
}