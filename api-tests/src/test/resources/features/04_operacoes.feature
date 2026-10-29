# language: pt
@operacoes-ms @operacoes
Funcionalidade: Operações de limpeza e integração com voluntários
  Como coordenador de limpeza
  Quero planejar, acompanhar e concluir operações de limpeza
  Para que o resíduo coletado fique registrado no relatório do voluntário

  Contexto:
    Dado que estou autenticado como administrador
    E que existe uma área marítima cadastrada

  Cenário: Planejar uma operação em uma área existente
    Quando envio um POST para "/operacoes-ms/operacoes" com o corpo:
      """
      {"titulo": "Mutirão {unico}", "descricao": "Limpeza da faixa de areia",
       "dataOperacao": "2026-12-10", "status": "PLANEJADA", "idArea": {areaId}}
      """
    Então o status da resposta deve ser 201
    E a resposta deve respeitar o contrato "operacao.json"
    E a resposta deve conter:
      | titulo       | Mutirão {unico} |
      | status       | PLANEJADA       |
      | dataOperacao | 2026-12-10      |
      | idArea       | {areaId}        |
      | nomeArea     | Área de teste {unico} |
    E guardo o campo "id" da resposta como "operacaoId"
    Quando envio um GET para "/operacoes-ms/operacoes/{operacaoId}"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "operacao.json"
    E a resposta deve conter:
      | id | {operacaoId} |

  Cenário: Listar todas as operações
    Dado que existe uma operação "PLANEJADA" cadastrada nessa área
    Quando envio um GET para "/operacoes-ms/operacoes"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "operacao-lista.json"
    E a lista da resposta deve conter o item com id "{operacaoId}"

  Cenário: Filtrar operações por status
    Dado que existe uma operação "EM_ANDAMENTO" cadastrada nessa área
    Quando envio um GET para "/operacoes-ms/operacoes/status/EM_ANDAMENTO"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "operacao-lista.json"
    E a lista da resposta deve conter o item com id "{operacaoId}"
    E todos os itens da lista devem ter "status" igual a "EM_ANDAMENTO"
    Quando envio um GET para "/operacoes-ms/operacoes/status/CANCELADA"
    Então o status da resposta deve ser 200
    E a lista da resposta não deve conter o item com id "{operacaoId}"

  Cenário: Atualizar uma operação sem concluí-la
    Dado que existe uma operação "PLANEJADA" cadastrada nessa área
    Quando envio um PUT para "/operacoes-ms/operacoes/{operacaoId}" com o corpo:
      """
      {"titulo": "Mutirão remarcado", "dataOperacao": "2026-12-20", "status": "EM_ANDAMENTO", "idArea": {areaId}}
      """
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "operacao.json"
    E a resposta deve conter:
      | titulo       | Mutirão remarcado |
      | status       | EM_ANDAMENTO      |
      | dataOperacao | 2026-12-20        |
    Quando envio um GET para "/voluntarios-ms/relatorios/operacao/{operacaoId}"
    Então o status da resposta deve ser 200
    E a lista da resposta deve ter 0 itens

  Cenário: Remover uma operação sem relatórios
    Dado que existe uma operação "PLANEJADA" cadastrada nessa área
    Quando envio um DELETE para "/operacoes-ms/operacoes/{operacaoId}"
    Então o status da resposta deve ser 204
    Quando envio um GET para "/operacoes-ms/operacoes/{operacaoId}"
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Operação não encontrada com id: {operacaoId} |

  # ---------------------------------------------------------------- comunicação entre serviços

  @principal @integracao
  Cenário: Concluir uma operação gera o relatório de coleta no voluntarios-ms
    Dado que existe um voluntário cadastrado
    E que existe uma operação "EM_ANDAMENTO" cadastrada nessa área
    Quando envio um PUT para "/operacoes-ms/operacoes/{operacaoId}" com o corpo:
      """
      {"titulo": "Mutirão {unico}", "dataOperacao": "2026-12-10", "status": "CONCLUIDA", "idArea": {areaId},
       "idVoluntario": {voluntarioId}, "quantidadeResiduos": 120, "tipoResiduo": "Plástico",
       "observacoes": "Concluída pelos testes"}
      """
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "operacao.json"
    E a resposta deve conter:
      | status | CONCLUIDA |
    Quando envio um GET para "/voluntarios-ms/relatorios/operacao/{operacaoId}"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "relatorio-lista.json"
    E a lista da resposta deve ter 1 item
    E a resposta deve conter:
      | [0].idOperacao         | {operacaoId}            |
      | [0].idVoluntario       | {voluntarioId}          |
      | [0].nomeVoluntario     | Voluntário {unico}      |
      | [0].quantidadeResiduos | 120                     |
      | [0].tipoResiduo        | Plástico                |
      | [0].observacoes        | Concluída pelos testes  |

  @principal @integracao
  Cenário: Rejeitar conclusão sem os dados da coleta
    Dado que existe uma operação "EM_ANDAMENTO" cadastrada nessa área
    Quando envio um PUT para "/operacoes-ms/operacoes/{operacaoId}" com o corpo:
      """
      {"titulo": "Mutirão {unico}", "dataOperacao": "2026-12-10", "status": "CONCLUIDA", "idArea": {areaId}}
      """
    Então o status da resposta deve ser 409
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Conclusão exige idVoluntario, quantidadeResiduos e tipoResiduo |
    Quando envio um GET para "/voluntarios-ms/relatorios/operacao/{operacaoId}"
    Então a lista da resposta deve ter 0 itens

  @integracao
  Cenário: Concluir com voluntário inexistente retorna falha de comunicação com o voluntarios-ms
    Dado que existe uma operação "EM_ANDAMENTO" cadastrada nessa área
    Quando envio um PUT para "/operacoes-ms/operacoes/{operacaoId}" com o corpo:
      """
      {"titulo": "Mutirão {unico}", "dataOperacao": "2026-12-10", "status": "CONCLUIDA", "idArea": {areaId},
       "idVoluntario": 999999999, "quantidadeResiduos": 10, "tipoResiduo": "Metal"}
      """
    Então o status da resposta deve ser 502
    E a resposta deve respeitar o contrato "erro.json"
    E o campo "erro" da resposta deve conter o texto "Falha voluntarios-ms"

  @integracao
  Cenário: Impedir a remoção de uma operação que já tem relatório
    Dado que existe um voluntário cadastrado
    E que existe uma operação "EM_ANDAMENTO" cadastrada nessa área
    E que essa operação foi concluída pelo voluntário com 80 kg de "Vidro"
    Quando envio um DELETE para "/operacoes-ms/operacoes/{operacaoId}"
    Então o status da resposta deve ser 409
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Não é possível excluir a operação pois existem relatórios vinculados a ela |

  # ---------------------------------------------------------------- validações

  Cenário: Rejeitar operação em área inexistente
    Quando envio um POST para "/operacoes-ms/operacoes" com o corpo:
      """
      {"titulo": "Área fantasma", "dataOperacao": "2026-12-10", "status": "PLANEJADA", "idArea": 999999999}
      """
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Área marítima não encontrada com id: 999999999 |

  Cenário: Rejeitar operação sem os campos obrigatórios
    Quando envio um POST para "/operacoes-ms/operacoes" com o corpo:
      """
      {"descricao": "sem nada"}
      """
    Então o status da resposta deve ser 400
    E a resposta deve respeitar o contrato "erro-validacao.json"
    E a resposta deve conter:
      | titulo       | Título é obrigatório             |
      | dataOperacao | Data da operação é obrigatória   |
      | status       | Status é obrigatório             |
      | idArea       | ID da área é obrigatório         |

  Cenário: Consultar uma operação inexistente
    Quando envio um GET para "/operacoes-ms/operacoes/999999999"
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"

  Cenário: Excluir operação sem autenticação
    Dado que existe uma operação "PLANEJADA" cadastrada nessa área
    E que não estou autenticado
    Quando envio um DELETE para "/operacoes-ms/operacoes/{operacaoId}"
    Então o status da resposta deve ser 401
