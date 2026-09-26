package br.com.fiap.operacoes_ms.http;

import br.com.fiap.operacoes_ms.FeignConfig;
import br.com.fiap.operacoes_ms.dto.RelatorioColetaRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "voluntarios-ms", configuration = FeignConfig.class)
public interface VoluntarioClient {

    @PostMapping("/relatorios")
    void registrarRelatorio(@RequestBody RelatorioColetaRequestDto dto);
}