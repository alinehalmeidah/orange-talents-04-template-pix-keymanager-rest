package br.com.zup.edu.consulta.lista

import br.com.zup.grpc.ListaChavePixResponse
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class ChavePixResponse(chavePix: ListaChavePixResponse.ChavePix) {

    val id = chavePix.pixId
    val chave = chavePix.chave
    val tipo = chavePix.tipo
    val tipoDeConta = chavePix.tipoDeConta
    val criadaEm = chavePix.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}