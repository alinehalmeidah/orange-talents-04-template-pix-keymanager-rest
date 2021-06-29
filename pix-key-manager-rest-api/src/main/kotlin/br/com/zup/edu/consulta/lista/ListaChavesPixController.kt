package br.com.zup.edu.consulta.lista

import br.com.zup.grpc.KeymanagerListaGrpcServiceGrpc
import br.com.zup.grpc.ListaChavePixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/v1/clientes/{clienteId}")
class ListaChavesPixController(val listaChavesPixClient: KeymanagerListaGrpcServiceGrpc.KeymanagerListaGrpcServiceBlockingStub) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/pix/")
    fun lista(clienteId: UUID) : HttpResponse<Any> {

        LOGGER.info("[$clienteId] listando chaves pix")
        val pix = listaChavesPixClient.lista(
            ListaChavePixRequest.newBuilder()
                .setClientId(clienteId.toString())
                .build())

        val chaves = pix.chavesList.map { ChavePixResponse(it) }
        return HttpResponse.ok(chaves)
    }
}