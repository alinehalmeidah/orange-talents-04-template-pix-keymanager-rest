package br.com.zup.edu.registra

import br.com.zup.edu.compartilhada.KeyManagerGrpcFactory
import br.com.zup.grpc.KeymanagerRegistraGrpcServiceGrpc
import br.com.zup.grpc.RegistraChavePixResponse
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
@MicronautTest
internal class RegistraChavePixControllerTest {

    @field:Inject
    lateinit var registraStub: KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    internal fun `deve registrar uma nova chave pix`() {

        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val respostaGrpc = RegistraChavePixResponse.newBuilder()
            .setClientId(clienteId)
            .setPixId(pixId)
            .build()

        given(registraStub.registra(Mockito.any())).willReturn(respostaGrpc)


        val novaChavePix = NovaChavePixRequest(tipoDeConta = TipoDeContaRequest.CONTA_CORRENTE,
            chave = "teste@teste.com.br",
            tipoDeChave = TipoDeChaveRequest.EMAIL)

        val request = HttpRequest.POST("/api/v1/clientes/$clienteId/pix", novaChavePix)
        val response = client.toBlocking().exchange(request, NovaChavePixRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(pixId))
    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub::class.java)
    }
}