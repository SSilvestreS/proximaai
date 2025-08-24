# ProximaAI - Sistema de Gestão de Projetos com IA Assistente

Um sistema completo de gerenciamento de projetos similar ao Jira/Asana, com módulo de recomendação inteligente baseado em IA.

##  Características Principais

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

### Tecnologias
- **Backend**: Spring Boot 3.2 + JPA/Hibernate
- **Banco de Dados**: PostgreSQL + Redis
- **Busca**: Elasticsearch
- **Mensageria**: Apache Kafka
- **Notificações**: WebSockets
- **Segurança**: Spring Security + JWT
- **Documentação**: OpenAPI 3

##  Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Docker e Docker Compose
- 8GB RAM disponível (para todos os serviços)

##  Instalação

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/ProximaAI.git
cd ProximaAI
```

### 2. Configure as variáveis de ambiente
```bash
# Copie o arquivo de exemplo
cp .env.example .env

# Edite as variáveis necessárias
nano .env
```

### 3. Inicie os serviços de infraestrutura
```bash
# Inicia PostgreSQL, Redis, Elasticsearch e Kafka
docker-compose up -d postgres redis elasticsearch kafka

# Aguarde todos os serviços estarem prontos
docker-compose ps
```

### 4. Execute a aplicação
```bash
# Compila e executa
mvn spring-boot:run

# Ou compile primeiro e depois execute
mvn clean package
java -jar target/proxima-project-manager-1.0.0.jar
```

##  Acessos

- **Aplicação Principal**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Kafka UI**: http://localhost:8080
- **Kibana**: http://localhost:5601
- **pgAdmin**: http://localhost:5050

##  Uso da API

### Autenticação
```bash
# Login
POST /api/auth/login
{
  "username": "admin",
  "password": "admin123"
}

# Use o token retornado no header Authorization
Authorization: Bearer <seu-token>
```

### Exemplos de Uso

#### Criar um Projeto
```bash
POST /api/projects
{
  "name": "Novo Projeto",
  "description": "Descrição do projeto",
  "priority": "HIGH",
  "startDate": "2024-01-01",
  "endDate": "2024-06-30"
}
```

#### Criar uma Tarefa
```bash
POST /api/tasks
{
  "title": "Implementar Login",
  "description": "Criar sistema de autenticação",
  "projectId": 1,
  "type": "FEATURE",
  "priority": "HIGH",
  "estimatedHours": 16
}
```

#### Obter Recomendações de IA
```bash
GET /api/ai/projects/1/recommendations
GET /api/ai/tasks/1/duration-estimate
GET /api/ai/users/1/overload-check
```

## 🔧 Configuração

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/proxima_projects
    username: postgres
    password: password
  
  kafka:
    bootstrap-servers: localhost:9092
  
  data:
    elasticsearch:
      uris: http://localhost:9200

ai:
  openai:
    api-key: ${OPENAI_API_KEY:}
    model: gpt-3.5-turbo
```

### Variáveis de Ambiente
```bash
# OpenAI (opcional)
OPENAI_API_KEY=sk-...

# Email (opcional)
EMAIL_USERNAME=seu-email@gmail.com
EMAIL_PASSWORD=sua-senha-app
```

##  Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/proximaai/
│   │   ├── config/          # Configurações
│   │   ├── domain/          # Entidades JPA
│   │   ├── repository/      # Repositórios
│   │   ├── service/         # Lógica de negócio
│   │   │   └── ai/         # Serviços de IA
│   │   ├── controller/      # APIs REST
│   │   └── ProximaProjectManagerApplication.java
│   └── resources/
│       └── application.yml
└── test/                    # Testes
```

##  Funcionalidades de IA

### Estimativa de Duração
- Analisa descrição da tarefa
- Considera histórico do projeto
- Ajusta baseado em tipo e prioridade

### Detecção de Sobrecarga
- Monitora tarefas ativas por usuário
- Calcula horas estimadas vs. disponibilidade
- Alerta quando limite é excedido

### Análise de Riscos
- Identifica tarefas atrasadas
- Detecta gargalos na equipe
- Avalia precisão das estimativas

### Otimizações
- Sugere paralelização de tarefas
- Recomenda ordem ideal de execução
- Identifica melhorias no processo

## 📊 Monitoramento

### Métricas Disponíveis
- Progresso do projeto
- Velocidade da equipe
- Precisão das estimativas
- Tempo em cada status
- Produtividade por membro

### Alertas Automáticos
- Tarefas atrasadas
- Sobrecarga de usuários
- Riscos identificados
- Mudanças críticas

##  Segurança

- **JWT Authentication**
- **Role-based Access Control**
- **Audit Logging** de todas as ações
- **Input Validation**
- **SQL Injection Protection**

##  Testes

```bash
# Executa todos os testes
mvn test

# Executa testes de integração
mvn verify

# Cobertura de código
mvn jacoco:report
```

##  Deploy

### Docker
```bash
# Build da imagem
docker build -t proxima-ai .

# Execução
docker run -p 8080:8080 proxima-ai
```

### Kubernetes
```bash
# Aplica os manifests
kubectl apply -f k8s/

# Verifica o status
kubectl get pods
```

##  Roadmap

- [ ] Frontend React com TypeScript
- [ ] Integração com GitHub/GitLab
- [ ] Relatórios avançados
- [ ] Mobile App
- [ ] Integração com Slack/Teams
- [ ] Machine Learning para predições

##  Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

##  Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.


---

**ProximaAI** - Transformando a gestão de projetos com inteligência artificial.
