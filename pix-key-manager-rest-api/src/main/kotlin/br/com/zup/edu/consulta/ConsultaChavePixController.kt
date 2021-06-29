package br.com.zup.edu.consulta

import br.com.zup.grpc.ConsultaChavePixRequest
import br.com.zup.grpc.KeymanagerConsultaGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/v1/clientes/{clienteId}")
class ConsultaChavePixController(val carregaChavePixClient: KeymanagerConsultaGrpcServiceGrpc.KeymanagerConsultaGrpcServiceBlockingStub) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/pix/{pixId}")
    fun carrega(clienteId: UUID,
                pixId: UUID) : HttpResponse<Any> {

        LOGGER.info("[$clienteId] carrega chave pix por id: $pixId")
        val chaveResponse = carregaChavePixClient.consulta(
            ConsultaChavePixRequest.newBuilder()
                .setPixId(ConsultaChavePixRequest.FiltroPorPix.newBuilder()
                    .setClientId(clienteId.toString())
                    .setPixId(pixId.toString())
                    .build()).
                build())

        return HttpResponse.ok(DetalheChavePixResponse(chaveResponse))
    }
}