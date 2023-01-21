package modulos.produto;

import dataFactory.ProduroDataFactory;
import dataFactory.UsuarioDataFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.ComponentePojo;
import pojo.ProdutoPojo;
import pojo.UsuarioPojo;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;//ajuda a economizar na escrita pois o sistema ja vai entender que não preciso ficar colocando RestAssured.Algumacolasse e vai colocar só AlgumaClasse
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API rest do módulo de produto")
public class ProdutoTest {
        private String token;

        @BeforeEach //faça antes de cada teste -> obter o token
        public void beforeEach(){
                //Configurando os dados da API Rest da lojinha
                baseURI = "http://165.227.93.41";
                // port = 8080;
                basePath = "/lojinha";


                //obter o token do usuario admin
                this.token = given()
                        .contentType(ContentType.JSON)  //application/json
                        .body(UsuarioDataFactory.criarUsuarioAdmin())//nessa parte ele transforma meu usuario e senha em jason e envia a requisição, mas antes é preciso instalar a biblioteca Jackson Databind » 2.12.3
                .when()
                        .post("/v2/login")
                .then()
                        .extract()
                        .path("data.token");
        }


        @Test
        @DisplayName("Validar os limites proibidos do valor do produto igual a 0.00 nao e permitido ")
        public void testValidarLimitesZeradoProibidoValorProduto() {

                //Tentar inserir um produto com valor 0.00 e validar que a mensagem de erro foi apresentada
                // status code retornado foi 422

                given()
                        .contentType(ContentType.JSON)
                        .header("token", this.token)
                        .body(ProduroDataFactory.criarProdutoComumComOValorIgualA(0.00))
                .when()
                        .post("/v2/produtos")
                .then()
                        .assertThat()
                        .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                        .statusCode(422);

        }
        @Test
        @DisplayName("Validar os limites proibidos do valor do produto igual a 7000.01 nao e permitido")
        public void testValidarLimitesMaiorSeteMilProibidoValorProduto(){
                //Tentar inserir um produto com valor 7000.01 e validar que a mensagem de erro foi apresentada
                // status code retornado foi 422
                given()
                        .contentType(ContentType.JSON)
                        .header("token",this.token)
                        .body(ProduroDataFactory.criarProdutoComumComOValorIgualA(7000.01))
                .when()
                        .post("/v2/produtos")
                .then()
                        .assertThat()
                        .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                        .statusCode(422);
                }
}
