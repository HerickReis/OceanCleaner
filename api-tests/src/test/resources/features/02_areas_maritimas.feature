# language: pt
@operacoes-ms @areas-maritimas
Funcionalidade: Gestão de áreas marítimas
  Como coordenador de limpeza
  Quero cadastrar e manter as áreas marítimas poluídas
  Para planejar as operações de limpeza sobre elas

  Contexto:
    Dado que estou autenticado como administrador

  @principal
  Cenário: Cadastrar uma área marítima válida e consultá-la pelo id
    Quando envio um POST para "/operacoes-ms/areas-maritimas" com o corpo:
      """
      {"nome": "Praia do Gonzaga {unico}", "descricao": "Muito plástico na faixa de areia",
       "localizacao": "Santos - SP", "nivelPoluicao": "ALTO"}
      """
    Então o status da resposta deve ser 201
    E a resposta deve respeitar o contrato "area-maritima.json"
    E a resposta deve conter:
      | nome          | Praia do Gonzaga {unico} |
      | localizacao   | Santos - SP              |
      | nivelPoluicao | ALTO                     |
    E guardo o campo "id" da resposta como "areaId"
    Quando envio um GET para "/operacoes-ms/areas-maritimas/{areaId}"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "area-maritima.json"
    E a resposta deve conter:
      | id   | {areaId}                 |
      | nome | Praia do Gonzaga {unico} |

  Cenário: Listar as áreas marítimas
    Dado que existe uma área marítima cadastrada
    Quando envio um GET para "/operacoes-ms/areas-maritimas"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "area-maritima-lista.json"
    E a lista da resposta deve conter o item com id "{areaId}"

  Cenário: Atualizar o nível de poluição de uma área
    Dado que existe uma área marítima cadastrada
    Quando envio um PUT para "/operacoes-ms/areas-maritimas/{areaId}" com o corpo:
      """
      {"nome": "Área limpa {unico}", "localizacao": "Santos - SP", "nivelPoluicao": "BAIXO"}
      """
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "area-maritima.json"
    E a resposta deve conter:
      | id            | {areaId}           |
      | nome          | Área limpa {unico} |
      | nivelPoluicao | BAIXO              |

  Cenário: Remover uma área marítima sem operações
    Dado que existe uma área marítima cadastrada
    Quando envio um DELETE para "/operacoes-ms/areas-maritimas/{areaId}"
    Então o status da resposta deve ser 204
    Quando envio um GET para "/operacoes-ms/areas-maritimas/{areaId}"
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Área marítima não encontrada com id: {areaId} |

  Esquema do Cenário: Rejeitar cadastro sem o campo obrigatório "<campo>"
    Quando envio um POST para "/operacoes-ms/areas-maritimas" com o corpo:
      """
      <corpo>
      """
    Então o status da resposta deve ser 400
    E a resposta deve respeitar o contrato "erro-validacao.json"
    E a resposta deve conter:
      | <campo> | <mensagem> |

    Exemplos:
      | campo         | corpo                                                       | mensagem                         |
      | nome          | {"localizacao": "Santos - SP", "nivelPoluicao": "ALTO"}     | Nome é obrigatório               |
      | localizacao   | {"nome": "Praia X", "nivelPoluicao": "ALTO"}                | A localização é obrigatória      |
      | nivelPoluicao | {"nome": "Praia X", "localizacao": "Santos - SP"}           | Nível de poluição é obrigatório  |

  Cenário: Consultar uma área marítima inexistente
    Quando envio um GET para "/operacoes-ms/areas-maritimas/999999999"
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Área marítima não encontrada com id: 999999999 |

  Cenário: Cadastrar área sem autenticação
    Dado que não estou autenticado
    Quando envio um POST para "/operacoes-ms/areas-maritimas" com o corpo:
      """
      {"nome": "Praia sem login", "localizacao": "Santos - SP", "nivelPoluicao": "ALTO"}
      """
    Então o status da resposta deve ser 401

  Cenário: Impedir a remoção de uma área com operações vinculadas
    Dado que existe uma área marítima cadastrada
    E que existe uma operação "PLANEJADA" cadastrada nessa área
    Quando envio um DELETE para "/operacoes-ms/areas-maritimas/{areaId}"
    Então o status da resposta deve ser 409
    E a resposta deve respeitar o contrato "erro.json"
    E o campo "erro" da resposta deve conter o texto "existem 1 operação(ões) vinculada(s)"
