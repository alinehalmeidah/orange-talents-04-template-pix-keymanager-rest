package br.com.zup.edu.consulta.lista

import br.com.zup.edu.compartilhada.KeyManagerGrpcFactory
import br.com.zup.grpc.KeymanagerListaGrpcServiceGrpc
import br.com.zup.grpc.ListaChavePixResponse
import br.com.zup.grpc.TipoDeChave
import br.com.zup.grpc.TipoDeConta
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ListaChavesPixControllerTest {

    @field:Inject
    lateinit var listaChaveStub: KeymanagerListaGrpcServiceGrpc.KeymanagerListaGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    val CHAVE_EMAIL = "teste@teste.com.br"
    val CHAVE_CELULAR = "+5511912345678"
    val CONTA_CORRENTE = TipoDeConta.CONTA_CORRENTE
    val TIPO_DE_CHAVE_EMAIL = TipoDeChave.EMAIL
    val TIPO_DE_CHAVE_CELULAR = TipoDeChave.PHONE
    val INSTITUICAO = "Itau"
    val TITULAR = "Woody"
    val DOCUMENTO_DO_TITULAR = "34597563067"
    val AGENCIA = "0001"
    val NUMERO_DA_CONTA = "1010-1"
    val CHAVE_CRIADA_EM = LocalDateTime.now()

    @Test
    internal fun `deve listar todas as chaves pix existente`() {

        val clienteId = UUID.randomUUID().toString()

        val respostaGrpc = listaChavePixResponse(clienteId)

        given(listaChaveStub.lista(Mockito.any())).willReturn(respostaGrpc)


        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/")
        val response = client.toBlocking().exchange(request, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(response.body()!!.size, 2)
    }

    private fun listaChavePixResponse(clienteId: String): ListaChavePixResponse {
        val chaveEmail = ListaChavePixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipo(TIPO_DE_CHAVE_EMAIL)
            .setChave(CHAVE_EMAIL)
            .setTipoDeConta(CONTA_CORRENTE)
            .setCriadaEm(CHAVE_CRIADA_EM.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()
        val chaveCelular = ListaChavePixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipo(TIPO_DE_CHAVE_CELULAR)
            .setChave(CHAVE_CELULAR)
            .setTipoDeConta(CONTA_CORRENTE)
            .setCriadaEm(CHAVE_CRIADA_EM.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()


        return ListaChavePixResponse.newBuilder()
            .setClientId(clienteId)
            .addAllChaves(listOf(chaveEmail, chaveCelular))
            .build()

    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubListaMock() = Mockito.mock(KeymanagerListaGrpcServiceGrpc.KeymanagerListaGrpcServiceBlockingStub::class.java)
    }
}