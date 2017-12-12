# Instruções para execução


## Contexto

O projeto consiste no uso de 3 componentes estruturais (Redis, RabbitMQ e MongoDB), 
6 projetos de backend e 2 frontents. Os componentes estruturais e os backends 
estão *dockerizados*, ao passo que os frontends precisam ser executados manualmente.

Existe uma ordem de execução desses componentes. O docker compose trata da sequência
em que cada componente deve ser executado, e o código de cada serviço tem uma lógica
de tratamento da conexão.

O startup do projeto deverá ser feito via docker-compose, e as configurações adicionais
serão feitas através de scripts.

## Pré requisitos
* Linux ou MacOS (não testado no Windows)
* Docker
* Node.js 8.5.0
* npm 5.5.1

## Passo a passo

1. Faça o clone do projeto (não custa lembrar ;-) );
1. Na raiz, execute `docker-compose up --build`. Esse processo pode levar alguns minutos
dependendo dos layers que estarão no cache do docker da máquina cliente;
   1. Acompanhe os logs no intuito de verificar se ocorreu algum erro;
1. Uma vez que todos os componentes estiverem em execução sem erros, entre no diretório
`automation` e execute o script `setup.sh` (verifique se o script está com permissão de 
execução). Esse script é responsável pelas configurações adicionais que precisam ser 
feitas nos componentes estruturais:
   1. Criação e configuração da exchange, fanout e filas no RabbitMQ;
   1. Configuração da publicação do evento de expiração de registros no Redis;
   1. Enchimento do MongoDB com os dados fictícios de restaurantes (esse passo pode levar
   alguns minutos)
1. Instale o ember-cli, executando com permissão de root `npm i -g ember-cli`;
1. No diretório `comercial-frontend`, digite `npm install` e ao final do procedimento,
digite `ember s`;
1. Repita o procedimento acima no diretório `restaurant-frontend`;
   1. Em ambos os casos, o ember vai apontar erros de build, que são na verdade validações
   de boas práticas de codificação. Para efeito de teste, pode ignorá-los.

## Utilizando a solução

Foram criados 2 frontends:
* Operação do restaurante - http://localhost:4201/ - esse frontend tem o objetivo de
simular a operação de um restante. É esse frontend quem mantem o estado da comunicação
ativa;
* Equipe Comercial - http://localhost:4200/ - esse frontend simula a operação do time
comercial.

O teste básico a ser feito é seguindo os passos abaixo:
1. Entre no frontend do comercial (porta 4200), entre em *Realtime* e selecione o
grupo A;
1. No frontend do restaurante, efetue o login com qualquer usuário do grupo A conforme
instruções na tela de autenticação (a autenticação em si não é validada);
1. O resultado esperado é que, ao se logar no restaurante, o hexágono correspondente
mostre o status corrente em tempo real. Ao encerrar a sessão (botão sair) ou fechar
o navegador, o keep alive vai parar de acontecer e no tempo programado para expiração,
hexágono correspondente deverá ser atualizado;
1. Para um teste com mais carga, execute o comando abaixo, trocando o número de "pings"
conforme necessidade:
```bash
for i in `seq 1 1000`; do 
  curl -XPOST -H "Content-Type: application/json" -d "{\"clientId\" : $i }" http://localhost:3000/v1/keepalive/ping; 
done
```

### Funcionalidades não finalizadas

* Scheduler: nessa versão, só possível acionar uma *indisponibilidade* para o momento
corrente. A funcionalidade de agendamento não foi concluída;
* Ranking de restaurantes: A opção "Ranking" do frontend do comercial mostra dados
fixos, mas os links apontam para a página de detalhamento correto. Caso queira ver
o detalhamento de um restaurante em específico, é só alterar o ID do restaurante na
barra de endereços (o gerenciamento do estado é feito nesse caso);
* Detalhamento do Restaurante: O cálculo da quantidade de tempo online e offline ainda
apresenta problemas de implementação quando o restaurante está no status *Online*.

