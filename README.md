# soft-iot-dlt-auth

O `soft-iot-dlt-auth` (ou `auth` para os mais próximos) é o _bundle_ responsável por monitorar as novas conexões MQTT.
Ele atua como um "porteiro" verificando se o dispositivo interessado em ser reconhecido pela plataforma `soft-iot-dlt` está autorizado.

## Instalação

Para instalar o `auth` é necessário [configurar o repositório fonte](https://github.com/larsid/soft-iot-dlt-architecture#repositório-fonte) e em seguida executar o seguinte comando no terminal do servicemix.

    bundle:install mvn:com.github.larsid/soft-iot-dlt-auth/main

## Configurações

O `auth` já está configurado por padrão para se conectar serviço de mensageria e broker mqtt ActiveMQ do servicemix. Porém é possível alterar esse comportamente criando um arquivo de configuração chamado de `soft-iot-dlt-auth.cfg` na pasta `SERVICEMIX_HOME/etc` com as seguintes propriedades:

| Propriedade  | Descrição                                                                    | Valor padrão    |
| ------------ | ---------------------------------------------------------------------------- | --------------- |
| MQTTServerId | Nome utilizado para identificar a conexão do auth no broker MQTT.            | P_AUTH_CLIENT   |
| MQTTHost     | Define a URL do broker.                                                      | tcp://localhost |
| MQTTPort     | Informa ao auth qual porta o broker está esperando novas conexões.           | 1883            |
| MQTTUsername | Nome do usuário que o auth deve utilizar para se autenticar caso necessário. | karaf           |
| MQTTPassword | Senha de autenticação do broker caso necessário.                             | karaf           |

:warning: [Arquivo exemplo](src/main/resources/soft.iot.dlt.auth.cfg) :warning:

---

| :arrow_left: [architecture](https://github.com/larsid/soft-iot-dlt-architecture#readme) | ............................... :arrow_up: [Voltar ao topo](#soft-iot-dlt-auth) :arrow_up: ............................... | [load-monitor](https://github.com/larsid/soft-iot-dlt-load-monitor#readme) :arrow_right: |
| :-------------------------------------------------------------------------------------: | -------------------------------------------------------------------------------------------------------------------------- | :--------------------------------------------------------------------------------------: |
