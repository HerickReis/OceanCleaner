# language: pt
@voluntarios-ms @voluntarios
Funcionalidade: Gestão de voluntários
  Como coordenador de limpeza
  Quero cadastrar e manter os voluntários
  Para registrar quem participou de cada operação

  Contexto:
    Dado que estou autenticado como administrador

  @principal
  Cenário: Cadastrar um voluntário válido e consultá-lo pelo id
    Quando envio um POST para "/voluntarios-ms/voluntarios" com o corpo:
      """
      {"nome": "Ana Souza", "email": "ana.{unico}@email.com", "telefone": "13988887777", "especialidade": "Mergulho"}
      """
    Então o status da resposta deve ser 201
    E a resposta deve respeitar o contrato "voluntario.json"
    E a resposta deve conter:
      | nome          | Ana Souza             |
      | email         | ana.{unico}@email.com |
      | especialidade | Mergulho              |
    E guardo o campo "id" da resposta como "voluntarioId"
    Quando envio um GET para "/voluntarios-ms/voluntarios/{voluntarioId}"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "voluntario.json"
    E a resposta deve conter:
      | id    | {voluntarioId}        |
      | email | ana.{unico}@email.com |

  Cenário: Listar os voluntários
    Dado que existe um voluntário cadastrado
    Quando envio um GET para "/voluntarios-ms/voluntarios"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "voluntario-lista.json"
    E a lista da resposta deve conter o item com id "{voluntarioId}"

  Cenário: Atualizar os dados de um voluntário
    Dado que existe um voluntário cadastrado
    Quando envio um PUT para "/voluntarios-ms/voluntarios/{voluntarioId}" com o corpo:
      """
      {"nome": "Nome Atualizado", "email": "atualizado.{unico}@email.com", "especialidade": "Barco"}
      """
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "voluntario.json"
    E a resposta deve conter:
      | id            | {voluntarioId}               |
      | nome          | Nome Atualizado              |
      | email         | atualizado.{unico}@email.com |
      | especialidade | Barco                        |

  Cenário: Remover um voluntário sem relatórios
    Dado que existe um voluntário cadastrado
    Quando envio um DELETE para "/voluntarios-ms/voluntarios/{voluntarioId}"
    Então o status da resposta deve ser 204
    Quando envio um GET para "/voluntarios-ms/voluntarios/{voluntarioId}"
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"

  @principal
  Cenário: Rejeitar cadastro com e-mail já utilizado
    Dado que existe um voluntário cadastrado
    Quando envio um POST para "/voluntarios-ms/voluntarios" com o corpo:
      """
      {"nome": "Outra Pessoa", "email": "voluntario.{unico}@teste.com"}
      """
    Então o status da resposta deve ser 409
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | E-mail já cadastrado: voluntario.{unico}@teste.com |

  Esquema do Cenário: Rejeitar cadastro com dados inválidos (<caso>)
    Quando envio um POST para "/voluntarios-ms/voluntarios" com o corpo:
      """
      <corpo>
      """
    Então o status da resposta deve ser 400
    E a resposta deve respeitar o contrato "erro-validacao.json"
    E a resposta deve conter:
      | <campo> | <mensagem> |

    Exemplos:
      | caso            | corpo                                         | campo | mensagem            |
      | sem nome        | {"email": "sem.nome@email.com"}               | nome  | Nome é obrigatório  |
      | sem e-mail      | {"nome": "Sem Email"}                         | email | Email é obrigatório |
      | e-mail inválido | {"nome": "Email Ruim", "email": "nao-e-email"} | email | Email inválido      |

  Cenário: Consultar um voluntário inexistente
    Quando envio um GET para "/voluntarios-ms/voluntarios/999999999"
    Então o status da resposta deve ser 404
    E a resposta deve respeitar o contrato "erro.json"
    E a resposta deve conter:
      | erro | Voluntário não encontrado com id: 999999999 |

  Cenário: Cadastrar voluntário sem autenticação
    Dado que não estou autenticado
    Quando envio um POST para "/voluntarios-ms/voluntarios" com o corpo:
      """
      {"nome": "Sem Login", "email": "sem.login.{unico}@email.com"}
      """
    Então o status da resposta deve ser 401

  Cenário: Impedir a remoção de um voluntário com relatórios
    Dado que existe uma área marítima cadastrada
    E que existe um voluntário cadastrado
    E que existe uma operação "EM_ANDAMENTO" cadastrada nessa área
    E que existe um relatório de coleta dessa operação
    Quando envio um DELETE para "/voluntarios-ms/voluntarios/{voluntarioId}"
    Então o status da resposta deve ser 409
    E a resposta deve respeitar o contrato "erro.json"
    E o campo "erro" da resposta deve conter o texto "existem 1 relatório(s) vinculado(s)"
