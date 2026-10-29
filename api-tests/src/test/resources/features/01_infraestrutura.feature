# language: pt
@infraestrutura
Funcionalidade: Infraestrutura e descoberta de serviços
  Como equipe de operação do Ocean Cleaner
  Quero que o Gateway esteja saudável e os microsserviços registrados no Eureka
  Para que os clientes acessem toda a API por um único endereço

  Cenário: Gateway saudável e identificando o ambiente
    Quando envio um GET para "/actuator/health"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "health.json"
    E a resposta deve conter:
      | status | UP |
    Quando envio um GET para "/actuator/info"
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "info.json"
    E a resposta deve conter:
      | app.name | Ocean Cleaner |

  Cenário: Microsserviços registrados no Eureka
    Quando consulto os serviços registrados no Eureka
    Então o status da resposta deve ser 200
    E a resposta deve respeitar o contrato "eureka-apps.json"
    E o Eureka deve listar o serviço "GATEWAY"
    E o Eureka deve listar o serviço "OPERACOES-MS"
    E o Eureka deve listar o serviço "VOLUNTARIOS-MS"

  Cenário: Rota sem serviço correspondente no Gateway
    Quando envio um GET para "/servico-inexistente/recurso"
    Então o status da resposta deve ser 404
