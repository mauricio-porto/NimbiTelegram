# NimbiTelegram

Este projeto usa a API do Telegram para enviar mensagens a um canal de divulgação (_broadcast_) sobre pedidos de compra na plataforma Nimbi que estejam potencialmente atrasados no seu fluxo normal, alertando os interessados para que tenham tempo hábil de verificar e tomar alguma ação corretiva se for necessário.

Vamos ver aqui neste **_README_** primeiro como configurar o Telegram para receber as mensagens e depois como o programa funciona.

## Criando e configurando o BOT Telegram em 3 passos

### Primeiro passo: criar o BOT Telegram

- Criar o BOT Telegram via _BotFather_.
  - Se você ainda não tem o _BotFather_ no seu Telegram, busque pelo username "@botfather" e inicie um chat com ele.
- No chat com o _BotFather_, escreva _/newbot_
- Ele irá pedir para escolher um nome para seu bot. Por exemplo, escreva: `Monitor de Fluxo`
- A seguir ele pedirá para você definir um _username_ para seu bot. Esse precisa terminar com "__bot_". Por exemplo, escreva: `monitordefluxo_bot`.
- O seu bot está pronto. O _BotFather_ irá informar um token de acesso à API que irá se parecer com:

      `1234567890:AAAAxY6udNO5u-9fO793yFdZaL1qU2RIkGT`

**ATENÇÃO:** Não compartilhe essa chave de acesso com ninguém, pois ela é que permite controlar o seu bot.
Você sempre poderá consultar as informações sobre seus _bots_ através do _BotFather_.
Basta escrever _/mybots_ e ele irá responder com a lista dos seus _bots_.
Ao clicar sobre um deles, irão surgir botões para consultar ou editar informações.

### Segundo passo: criar um canal Telegram de broadcast

- No Telegram, use o ícone do lápis (canto inferior direito) para acessar o menu.
- No menu, escolha a opção "Novo Canal" (New Channel)
- Informe um nome para seu canal de divulgação. Por exemplo, escreva: `Monitores de Fluxo`
- Opcionalmente escreva uma descrição do seu canal.
- A seguir, mantenha a opção 'Canal Público' e forneça um link exclusivo de acesso ao seu canal. Por exemplo, escreva: `t.me/MonitoresDeFluxo`

**ATENÇÃO:** Esse nome fornecido no link de acesso é que será usado como identificador (_username_) do canal ao usar a API para enviar mensagens. No exemplo acima, esse identificador seria `@MonitoresDeFluxo`. 

### Terceiro passo: adicionar o BOT Telegram ao seu canal de broadcast

No Telegram, apenas usuários 'Admin' podem enviar mensagens em canais. Portanto, é preciso que você adicione o seu novo BOT como sendo 'Admin' do seu novo canal.

- Acesse as configurações do seu canal e use a opção de "**Administradores**"
- Então use a opção "**Adicionar Administrador**" (_Add Admin_), busque pelo nome do seu bot ('**_Monitor de Fluxo_**' no nosso exemplo) e o adicione como administrador do canal, com os direitos _default_.
- Está feito, você está apto a enviar mensagens ao canal do Telegram usando a API.

### Testando o envio de mensagens ao canal

Você pode usar o browser para testar o envio de mensagens via API do Telegram.

Precisa usar o seguinte formato na URL:

      `https://api.telegram.org/bot[BOT API KEY]/sendMessage?chat_id=[CHANNEL ID]&text=[MESSAGE TEXT]`

Substitua os campos `[BOT API KEY]`, `[CHANNEL ID]` e `[MESSAGE TEXT]` colocando os seus valores. Por exemplo:

      `https://api.telegram.org/bot1234567890:AAAAxY6udNO5u-9fO793yFdZaL1qU2RIkGT/sendMessage?chat_id=@MonitoresDeFluxo&text=OIE`

O browser irá mostrar a reposta (em JSON) contendo detalhes do envio da mensagem. Você também poderá verificar a recepção da mensagem pelo Telegram.

## O programa **_NimbiTelegram_**

Este projeto baseia-se em Spring Boot e faz uso de um banco MongoDB para armazenar alguns dados.

Em síntese, ele consulta periodicamente a plataforma Nimbi através de sua API para buscar pedidos de compra aguardando aprovação que estejam sem atualização nas últimas 12 horas.

Nessa primeira consulta via API (ver o método _CheckPendingApprovalsService::fetchPurchaseOrdersFromNimbi()_), o retorno traz apenas o identificador (chave) do pedido de compra e a hora da última atualização.

Uma vez filtrados apenas os que estão sem atualização nas últimas 12 horas, fizemos uma segunda consulta (ver o método _CheckPendingApprovalsService::getPurchaseOrdersDetailedFiltered()_) para cada um desses para obter mais detalhes.

O resultado dessa segunda chamada pelos detalhes de cada pedido de compras é novamente filtrado para restarem apenas os pedidos que são considerados **'urgentes'**.

O resultado filtrado é então usado para enviar uma mensagem pelo Telegram (ver método _CheckPendingApprovalsService::executeSendMessage()_) com a lista dos identificadores de pedidos de compra pendentes de aprovação, que estão sem atualização há mais de 12 horas.

### O acesso à plataforma Nimbi

Para que seja possível consultar a [API Nimbi](https://api001.nimbi.com.br/APIMonitor/HowToUse.aspx), são necessárias credenciais que você precisa obter junto à Nimbi.

### Dados de configuração e acesso

O arquivo de configuração do programa, chamado application.yml, define o ambiente de execução como sendo _'devl'_ (_development_).

Sendo assim, os parâmetros de configuração do programa, essenciais para o acesso às APIs externas (Nimbi e Telegram) devem ser declarados no arquivo 'application-devl.yml', que por questões de confidencialidade, não figura neste repositório do projeto.

Quem quiser utilizar o projeto como base para seu próprio projeto, precisará definir esse arquivo com os respectivos parâmetros.
Para a lista completa desses parâmetros, ver as classes **_NimbiComprasClientConfig_** e **_BotTelegramClientConfig_**.


### Armazenamento de dados no MongoDB

Não é essencial, mas serve como base para futuras extensões e também para consulta local via console de BD sobre detalhes dos pedidos de compra.

Acrescentar um frontend ao programa para mostrar no browser tais pedidos de compra, é tarefa bem simples. Talvez eu faça no futuro, ou algum leitor aqui se voluntarie para isso.

