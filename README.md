# ProximaAI - Sistema de Gestão de Projetos com IA Assistente

Um sistema completo de gerenciamento de projetos similar ao Jira/Asana, com módulo de recomendação inteligente baseado em IA.

## 🚀 Características Principais

### Core Features
- **CRUD completo** de equipes, projetos e tarefas
- **Kanban Board** com drag & drop
- **Notificações em tempo real** via WebSockets
- **Sistema de auditoria** detalhado de todas as alterações
- **Controle de acesso** baseado em roles

### IA Assistente
- **Estimativa inteligente** de duração de tarefas
- **Detecção automática** de sobrecarga de trabalho
- **Recomendações de prazos** baseadas em capacidade da equipe
- **Análise de riscos** do projeto
- **Otimizações automáticas** de fluxo de trabalho
- **Insights de produtividade** da equipe

## 🛠️ Stack Tecnológica

### Backend
- **Spring Boot 3.2** (Java 17)
- **JPA/Hibernate** para ORM
- **PostgreSQL** para banco principal
- **Redis** para cache
- **Elasticsearch** para busca avançada
- **Apache Kafka** para eventos assíncronos
- **Spring Security + JWT** para autenticação
- **WebSockets** para notificações em tempo real

### Ferramentas
- **Maven** para gerenciamento de dependências
- **Docker & Docker Compose** para containerização
- **OpenAPI 3** para documentação da API
- **JUnit 5** para testes

## 📋 Pré-requisitos

- **Java 17** ou superior
- **Maven 3.8+**
- **Docker** e **Docker Compose**
- **PostgreSQL 14+**
- **Redis 6+**
- **Elasticsearch 8+**
- **Apache Kafka 3+**

## 📦 Instalação

### 1. Clone o repositório
```bash
git clone https://github.com/SSilvestreS/proximaai.git
cd proximaai
```

### 2. Configure as variáveis de ambiente
```bash
cp env.example .env
# Edite o arquivo .env com suas configurações
```

### 3. Inicie os serviços com Docker
```bash
docker-compose up -d
```

### 4. Execute a aplicação
```bash
mvn spring-boot:run
```

## 🌐 Acesso

- **Aplicação**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html
- **PostgreSQL**: localhost:5432
- **Redis**: localhost:6379
- **Elasticsearch**: http://localhost:9200
- **Kafka**: localhost:9092

## 📚 Uso da API

### Autenticação
```bash
# Login
POST /api/auth/login
{
  "username": "admin",
  "password": "password"
}

# Usar o token retornado
Authorization: Bearer <token>
```

### Exemplos de Endpoints

#### Usuários
```bash
# Listar usuários
GET /api/users

# Criar usuário
POST /api/users
{
  "username": "novo_usuario",
  "email": "usuario@exemplo.com",
  "role": "USER"
}
```

#### Projetos
```bash
# Listar projetos
GET /api/projects

# Criar projeto
POST /api/projects
{
  "name": "Novo Projeto",
  "description": "Descrição do projeto",
  "priority": "HIGH"
}
```

#### Tarefas
```bash
# Listar tarefas
GET /api/tasks

# Criar tarefa
POST /api/tasks
{
  "title": "Nova Tarefa",
  "description": "Descrição da tarefa",
  "priority": "MEDIUM",
  "type": "FEATURE"
}
```

## 🔧 Configuração

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/proximaai
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

  redis:
    host: localhost
    port: 6379

elasticsearch:
  uri: http://localhost:9200

kafka:
  bootstrap-servers: localhost:9092

ai:
  openai:
    api-key: ${OPENAI_API_KEY}
  huggingface:
    api-key: ${HUGGINGFACE_API_KEY}
```

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/proximaai/
│   │   ├── domain/entity/     # Entidades JPA
│   │   ├── repository/         # Repositórios
│   │   ├── service/           # Lógica de negócio
│   │   │   └── ai/           # Serviços de IA
│   │   ├── config/            # Configurações
│   │   └── ProximaProjectManagerApplication.java
│   └── resources/
│       └── application.yml
└── test/                      # Testes
```

## 🤖 Funcionalidades de IA

### Estimativa de Duração
- Análise de tarefas similares
- Consideração de complexidade
- Ajuste baseado na equipe

### Detecção de Sobrecarga
- Monitoramento de carga de trabalho
- Alertas automáticos
- Sugestões de redistribuição

### Análise de Riscos
- Identificação de gargalos
- Previsão de atrasos
- Recomendações de mitigação

### Otimização de Processos
- Identifica melhorias no processo
- Sugere sequenciamento otimal
- Analisa eficiência da equipe

## 📊 Monitoramento

### Métricas Disponíveis
- **Performance** da aplicação
- **Uso de recursos** do sistema
- **Tempo de resposta** das APIs
- **Taxa de erro** das operações

### Logs e Auditoria
- **Todas as operações** são registradas
- **Histórico completo** de mudanças
- **Rastreamento** de usuários
- **Mudanças críticas**

## 🔒 Segurança

- **JWT Authentication**
- **Role-based Access Control**
- **Input Validation**
- **SQL Injection Protection**
- **XSS Protection**
- **CSRF Protection**

## 🧪 Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify

# Cobertura de código
mvn jacoco:report
```

## 🚀 Deploy

### Docker
```bash
# Build da imagem
docker build -t proximaai .

# Executar container
docker run -p 8080:8080 proximaai
```

### Produção
```bash
# Build para produção
mvn clean package -Pprod

# Executar JAR
java -jar target/proximaai-1.0.0.jar
```

## 📈 Roadmap

- [ ] Frontend React com TypeScript
- [ ] Sistema de relatórios avançados
- [ ] Integração com calendário
- [ ] Chat interno integrado
- [ ] Sistema de arquivos e anexos
- [ ] API GraphQL
- [ ] Sistema de plugins
- [ ] Modo offline
- [ ] Aplicativo móvel nativo
- [ ] Integração com Slack/Teams
- [ ] Sistema de timesheet
- [ ] Dashboard executivo
- [ ] Machine Learning para predições

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a **Licença MIT** - veja o arquivo LICENSE para detalhes.

```
MIT License

Copyright (c) 2024 ProximaAI Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📞 Suporte

- **Issues**: [GitHub Issues](https://github.com/SSilvestreS/proximaai/issues)
- **Documentação**: [Wiki](https://github.com/SSilvestreS/proximaai/wiki)
- **Email**: suporte@proxima.com

## 🙏 Agradecimentos

- **Spring Boot** - Framework Java incrível
- **PostgreSQL** - Banco de dados robusto e confiável
- **Redis** - Cache de alta performance
- **Elasticsearch** - Busca e análise de dados
- **Apache Kafka** - Streaming de eventos
- **Docker** - Containerização que simplifica tudo
- **GitHub** - Plataforma que torna o desenvolvimento colaborativo possível

---

**⭐ Se este projeto te ajudou, considere dar uma estrela no GitHub! ⭐**

**🚀 ProximaAI - Transformando a gestão de projetos com IA 🚀**
