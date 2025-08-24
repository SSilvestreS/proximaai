#  ProximaAI - Project Manager

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.1.0-blue.svg)](https://github.com/seu-usuario/ProximaAI/releases)

> **Sistema Inteligente de Gerenciamento de Projetos com IA Assistente**

O sistema permite gerenciar equipes, projetos e tarefas de forma completa, com Kanban interativo, controle de acesso por papéis, auditoria detalhada e notificações em tempo real. Sua inteligência artificial analisa histórico de projetos e capacidade da equipe para estimar prazos, detectar sobrecarga, sugerir otimizações de fluxo e identificar riscos, oferecendo insights acionáveis para líderes e colaboradores.

Construído com Spring Boot, PostgreSQL, Redis, Elasticsearch e Kafka, e integrando modelos de IA, o ProximaAI é escalável, seguro e modular, ideal para empresas que buscam automatizar decisões, aumentar a eficiência e transformar dados de projetos em resultados estratégicos.

##  **Índice**

- [ **Visão Geral**](#-visão-geral)
- [ **Funcionalidades Avançadas de Negócio**](#-funcionalidades-avançadas-de-negócio)
- [ **IA Assistente**](#-ia-assistente)
- [ **Estrutura do Projeto**](#️-estrutura-do-projeto)
- [ **Tecnologias**](#️-tecnologias)
- [ **Instalação**](#-instalação)
- [ **Execução**](#-execução)
- [ **Monitoramento**](#-monitoramento)
- [ **Testes**](#-testes)
- [ **API Documentation**](#-api-documentation)
- [ **Contribuição**](#-contribuição)
- [ **Licença**](#-licença)

##  **Visão Geral**

O **ProximaAI** é uma plataforma avançada de gerenciamento de projetos que combina metodologias tradicionais com inteligência artificial para otimizar processos, prever riscos e maximizar a produtividade das equipes.

###  **Características Principais**

- **Gestão Inteligente** de projetos, tarefas e equipes
- **IA Assistente** para estimativas, análise de riscos e otimizações
- **Workflows Customizáveis** para diferentes tipos de projetos
- **Controle de Recursos** e orçamento integrado
- **Timesheet Profissional** com aprovações e faturamento
- **Templates Reutilizáveis** para acelerar setup de projetos
- **Sistema de Dependências** entre tarefas
- **Monitoramento em Tempo Real** com dashboards interativos

##  **Funcionalidades Avançadas de Negócio**

###  **Dependências de Tarefas**
- **Controle Completo** de dependências entre tarefas
- **Tipos Flexíveis**: Finish-to-Start, Start-to-Start, Finish-to-Finish, Start-to-Finish
- **Gestão de Lag/Lead** para otimização de cronogramas
- **Identificação de Caminhos Críticos** automática
- **Prevenção de Deadlocks** e dependências circulares

###  **Gestão de Recursos e Orçamento**
- **Alocação Inteligente** de recursos humanos, equipamentos e software
- **Controle de Custos** em tempo real
- **Gestão de Orçamento** com alertas de estouro
- **Análise de Capacidade** e disponibilidade
- **Relatórios Financeiros** detalhados

###  **Workflows Customizáveis**
- **Criação de Fluxos** personalizados por equipe
- **Status Dinâmicos** com cores e ícones
- **Transições Condicionais** com regras de negócio
- **Sistema de Aprovações** configurável
- **Automação de Processos** com triggers

###  **Templates de Projetos**
- **Modelos Categorizados** por tipo de projeto
- **Componentes Reutilizáveis**: tarefas, marcos e recursos
- **Estimativas Automáticas** de tempo e custo
- **Biblioteca de Templates** compartilhável
- **Versionamento** de templates

###  **Timesheet Profissional**
- **Controle de Tempo** com start/stop automático
- **Categorização de Atividades** por tipo de trabalho
- **Sistema de Aprovações** hierárquico
- **Cálculo Automático** de horas faturáveis
- **Integração com Folha de Pagamento**

##  **IA Assistente**

###  **Capacidades Inteligentes**

- **Estimativa de Tarefas** baseada em histórico e complexidade
- **Detecção de Sobrecarga** de trabalho e burnout
- **Recomendações de Deadlines** otimizadas
- **Análise de Riscos** proativa
- **Otimização de Workflows** baseada em dados
- **Insights de Produtividade** personalizados
- **Previsão de Atrasos** com alertas antecipados

###  **Análise Preditiva**

- **Machine Learning** para estimativas de tempo
- **Análise de Padrões** de produtividade
- **Identificação de Gargalos** em processos
- **Recomendações de Melhorias** baseadas em dados
- **Relatórios Inteligentes** com insights acionáveis

##  **Estrutura do Projeto**

```
ProximaAI/
├── src/main/java/com/proximaai/
│   ├── domain/entity/          # Entidades JPA
│   │   ├── core/               # Entidades principais
│   │   │   ├── User.java
│   │   │   ├── Project.java
│   │   │   ├── Task.java
│   │   │   ├── Team.java
│   │   │   └── Notification.java
│   │   ├── business/           # Funcionalidades de negócio
│   │   │   ├── TaskDependency.java
│   │   │   ├── ResourceAllocation.java
│   │   │   ├── CustomWorkflow.java
│   │   │   ├── ProjectTemplate.java
│   │   │   └── Timesheet.java
│   │   └── workflow/           # Sistema de workflows
│   │       ├── WorkflowStatus.java
│   │       └── WorkflowTransition.java
│   ├── repository/              # Repositórios JPA
│   ├── service/                 # Lógica de negócio
│   │   ├── ai/                  # Serviços de IA
│   │   ├── business/            # Serviços de negócio
│   │   └── workflow/            # Serviços de workflow
│   ├── config/                  # Configurações
│   └── ProximaProjectManagerApplication.java
├── src/main/resources/          # Configurações e recursos
├── src/test/                    # Testes automatizados
├── docker/                      # Configurações Docker
├── pom.xml                      # Dependências Maven
├── docker-compose.yml           # Orquestração de containers
└── README.md                    # Documentação
```

##  **Tecnologias**

### **Backend**
- **Java 17** - Linguagem principal
- **Spring Boot 3.2** - Framework de aplicação
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Spring WebSocket** - Comunicação em tempo real

### **Banco de Dados & Cache**
- **PostgreSQL 15** - Banco de dados principal
- **Redis 7** - Cache e sessões
- **Elasticsearch 8.11** - Busca e análise

### **Mensageria & Eventos**
- **Apache Kafka 7.4** - Processamento de eventos
- **Zookeeper** - Coordenação de cluster

### **DevOps & Containerização**
- **Docker** - Containerização da aplicação
- **Docker Compose** - Orquestração local
- **Maven** - Gerenciamento de dependências

### **Monitoramento & Observabilidade**
- **Kafka UI** - Interface para gerenciamento do Kafka
- **Kibana** - Visualização e análise de dados

## **Instalação**

### **Pré-requisitos**
- Java 17 ou superior
- Docker e Docker Compose
- Maven 3.8+

### **Clone do Repositório**
```bash
git clone https://github.com/seu-usuario/ProximaAI.git
cd ProximaAI
```

### **Configuração do Ambiente**
```bash
# Copiar arquivo de configuração
cp src/main/resources/application-example.yml src/main/resources/application.yml

# Editar configurações (banco, Redis, Kafka, etc.)
nano src/main/resources/application.yml
```

## **Execução**

### **Execução com Docker Compose (Recomendado)**
```bash
# Iniciar todos os serviços
docker-compose up -d

# Verificar status dos serviços
docker-compose ps

# Logs em tempo real
docker-compose logs -f
```

### **Execução Local**
```bash
# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Executar aplicação
mvn spring-boot:run
```

### **Acessos**
- **Aplicação**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Kafka UI**: http://localhost:8080/kafka-ui
- **Kibana**: http://localhost:5601

## **Monitoramento**

### **Dashboards Disponíveis**
- **Métricas de Projetos** - Status, progresso e KPIs
- **Análise de Equipes** - Produtividade e carga de trabalho
- **Monitoramento de Recursos** - Utilização e custos
- **Relatórios de Timesheet** - Horas trabalhadas e faturamento
- **Análise de Workflows** - Eficiência e gargalos

### **Alertas Inteligentes**
- **Atrasos de Projetos** com notificações proativas
- **Sobrecarga de Recursos** com sugestões de rebalanceamento
- **Estouro de Orçamento** com alertas financeiros
- **Problemas de Dependências** com sugestões de resolução

## **Testes**

### **Execução de Testes**
```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=UserServiceTest

# Cobertura de código
mvn jacoco:report
```

### **Tipos de Testes**
- **Unitários** - Lógica de negócio isolada
- **Integração** - Persistência e serviços
- **End-to-End** - Fluxos completos de usuário
- **Performance** - Testes de carga e stress

## 📝 **API Documentation**

### **Swagger UI**
- **URL**: http://localhost:8080/swagger-ui.html
- **Especificação OpenAPI 3.0**
- **Testes Interativos** de endpoints
- **Documentação Automática** de modelos

### **Endpoints Principais**
- **`/api/v1/projects`** - Gestão de projetos
- **`/api/v1/tasks`** - Gestão de tarefas
- **`/api/v1/workflows`** - Configuração de workflows
- **`/api/v1/timesheets`** - Controle de tempo
- **`/api/v1/resources`** - Alocação de recursos
- **`/api/v1/templates`** - Templates de projetos

## **Contribuição**

### **Como Contribuir**
1. **Fork** o projeto
2. **Crie** uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. **Abra** um Pull Request

### **Padrões de Código**
- **Java Code Style** - Google Java Style
- **Commits Semânticos** - Conventional Commits
- **Testes Obrigatórios** - Cobertura mínima de 80%
- **Documentação** - JavaDoc para APIs públicas

## **Licença**

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## **Changelog - Versão 1.1.0**

### **Novas Funcionalidades**
- ✨ **Sistema de Dependências de Tarefas** - Controle completo de dependências entre tarefas
- ✨ **Gestão de Recursos e Orçamento** - Alocação inteligente de pessoas, equipamentos e controle de custos
- ✨ **Workflows Customizáveis** - Cada equipe pode criar seus próprios fluxos de trabalho
- ✨ **Templates de Projetos** - Modelos reutilizáveis para acelerar setup de novos projetos
- ✨ **Timesheet Profissional** - Controle de tempo, aprovações e faturamento integrado

### **Melhorias**
- 🔧 **Arquitetura Reorganizada** - Separação clara entre entidades core, business e workflow
- 🔧 **Validações Aprimoradas** - Bean Validation com anotações específicas
- 🔧 **Relacionamentos JPA** - Mapeamentos otimizados com fetch strategies apropriadas

### **Correções**
-  **Imports Não Utilizados** - Remoção de imports desnecessários
-  **Deprecated APIs** - Atualização de BigDecimal.ROUND_HALF_UP para RoundingMode.HALF_UP

---


[![GitHub](https://img.shields.io/badge/GitHub-ProximaAI-black.svg)](https://github.com/seu-usuario/ProximaAI)
[![Issues](https://img.shields.io/badge/Issues-Welcome-brightgreen.svg)](https://github.com/seu-usuario/ProximaAI/issues)
[![Stars](https://img.shields.io/badge/Stars-Welcome-yellow.svg)](https://github.com/seu-usuario/ProximaAI/stargazers)
