package br.com.zup.edu.registra

import br.com.zup.grpc.KeymanagerRegistraGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{clientId}")
class RegistraChavePixController(@Inject val grpcClient: KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post("/pix")
    fun create(clientId: UUID, @Valid @Body request: NovaChavePixRequest): HttpResponse<Any> {
        LOGGER.info("[$clientId] criando uma nova chave pix com $request")
        val grpcResponse = grpcClient.registra(request.paraModeloGrpc(clientId))

        return HttpResponse.created(location(clientId, grpcResponse.pixId))
    }

    private fun location(clienteId: UUID, pixId: String) = HttpResponse
        .uri("/api/v1/clientes/$clienteId/pix/${pixId}")
}