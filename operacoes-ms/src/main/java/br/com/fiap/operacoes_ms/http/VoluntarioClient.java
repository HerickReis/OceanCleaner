package br.com.fiap.operacoes_ms.http;

import br.com.fiap.operacoes_ms.FeignConfig;
import br.com.fiap.operacoes_ms.dto.RelatorioColetaRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "voluntarios-ms", configuration = FeignConfig.class)
public interface VoluntarioClient {

    @PostMapping("/relatorios")
    void registrarRelatorio(@RequestBody RelatorioColetaRequestDto dto);

    @GetMapping("/relatorios/operacao/{idOperacao}")
    List<Map<String, Object>> listarPorOperacao(@PathVariable Long idOperacao);
}