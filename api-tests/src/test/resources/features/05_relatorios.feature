# language: pt
@voluntarios-ms @relatorios
Funcionalidade: Relatórios de coleta
  Como coordenador de limpeza
  Quero registrar e consultar os relatórios de coleta
  Para acompanhar quanto resíduo cada operação e cada voluntário removeram

  Contexto:
    Dado que estou autenticado como administrador
    E que existe uma área marítima cadastrada
    E que existe um voluntário cadastrado
    E que existe uma operação "EM_ANDAMENTO" cadastrada nessa área

  Cenário: Registrar um relatório de coleta manualmente
    Quando envio um POST para "/voluntarios-ms/relatorios" com o corpo:
      """
      {"idOperacao": {operacaoId}, "idVoluntario": {voluntarioId},
       "quantidadeResiduos": 42, "tipoResiduo": "Redes de pesca", "observacoes": "Coleta manual"}
      """
    Então o status da resposta deve ser 201
    E a resposta deve respeitar o contrato "relatorio.json"
    E a resposta deve conter:
      | idOperacao         | {operacaoId}       |
      | idVoluntario       | {voluntarioId}     |
      | nomeVoluntario     | Voluntário {unico} |
      | quantidadeResiduos | 42                 |
      | tipoResiduo        | Redes de pesca     |
    E guardo o campo "id" da resposta como "relatorioId"
    Quando envio um GET para "/voluntarios-ms/relatorios/{relatorioId}"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "relatorio.json"
    E a resposta deve conter:
      | id | {relatorioId} |

  Cenário: Listar todos os relatórios
    Dado que existe um relatório de coleta dessa operação
    Quando envio um GET para "/voluntarios-ms/relatorios"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "relatorio-lista.json"
    E a lista da resposta deve conter o item com id "{relatorioId}"

  Cenário: Consultar relatórios por operação e por voluntário
    Dado que existe um relatório de coleta dessa operação
    Quando envio um GET para "/voluntarios-ms/relatorios/operacao/{operacaoId}"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "relatorio-lista.json"
    E a lista da resposta deve ter 1 item
    E a lista da resposta deve conter o item com id "{relatorioId}"
    Quando envio um GET para "/voluntarios-ms/relatorios/voluntario/{voluntarioId}"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "relatorio-lista.json"
    E a lista da resposta deve ter 1 item
    E todos os itens da lista devem ter "idVoluntario" igual a "{voluntarioId}"

  Cenário: Remover um relatório
    Dado que existe um relatório de coleta dessa operação
    Quando envio um DELETE para "/voluntarios-ms/relatorios/{relatorioId}"
    Então o status da resposta deve ser 204
    Quando envio um GET para "/voluntarios-ms/relatorios/{relatorioId}"
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Relatório não encontrado com id: {relatorioId} |

  Cenário: Rejeitar relatório de voluntário inexistente
    Quando envio um POST para "/voluntarios-ms/relatorios" com o corpo:
      """
      {"idOperacao": {operacaoId}, "idVoluntario": 999999999, "quantidadeResiduos": 5, "tipoResiduo": "Metal"}
      """
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Voluntário não encontrado com id: 999999999 |

  Cenário: Rejeitar relatório sem os campos obrigatórios
    Quando envio um POST para "/voluntarios-ms/relatorios" com o corpo:
      """
      {"observacoes": "incompleto"}
      """
    Então o status da resposta deve ser 400
    E a resposta deve respeitar o contrato "erro-validacao.json"
    E a resposta deve conter:
      | idOperacao         | ID da operação é obrigatório        |
      | idVoluntario       | ID do voluntário é obrigatório      |
      | quantidadeResiduos | Quantidade de resíduos é obrigatória |
      | tipoResiduo        | Tipo de resíduo é obrigatório       |

  Cenário: Consultar relatórios de uma operação sem coleta
    Quando envio um GET para "/voluntarios-ms/relatorios/operacao/{operacaoId}"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "relatorio-lista.json"
    E a lista da resposta deve ter 0 itens

  Cenário: Remover relatório sem autenticação
    Dado que existe um relatório de coleta dessa operação
    E que não estou autenticado
    Quando envio um DELETE para "/voluntarios-ms/relatorios/{relatorioId}"
    Então o status da resposta deve ser 401
