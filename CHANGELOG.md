# **Changelog**

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.1.0] - 2025-08-24

### **Adicionado**
- **Sistema de Dependências de Tarefas**
  - Entidade `TaskDependency` com tipos flexíveis (Finish-to-Start, Start-to-Start, Finish-to-Finish, Start-to-Finish)
  - Controle de lag/lead days para otimização de cronogramas
  - Identificação automática de caminhos críticos
  - Prevenção de deadlocks e dependências circulares
  - Relacionamentos bidirecionais entre tarefas dependentes

- **Gestão de Recursos e Orçamento**
  - Entidade `ResourceAllocation` para alocação de recursos
  - Suporte a diferentes tipos: recursos humanos, equipamentos, software, infraestrutura e serviços externos
  - Controle de custos em tempo real com taxas horárias
  - Gestão de status de alocação (Planejado, Ativo, Concluído, Cancelado, Em Espera)
  - Rastreamento de horas alocadas vs. horas trabalhadas

- **Workflows Customizáveis**
  - Entidade `CustomWorkflow` para definição de fluxos personalizados
  - `WorkflowStatus` com configurações de cores, ícones e comportamentos
  - `WorkflowTransition` com regras de transição condicionais
  - Sistema de aprovações configurável
  - Suporte a workflows automáticos e manuais

- **Templates de Projetos**
  - Entidade `ProjectTemplate` com categorização e complexidade
  - `TemplateTask` com estimativas e dependências
  - `TemplateMilestone` com critérios de sucesso
  - `TemplateResource` com alocação de recursos
  - Biblioteca de templates reutilizáveis e versionáveis

- **Timesheet e Controle de Tempo**
  - Entidade `Timesheet` com status de aprovação e tipos de trabalho
  - `TimesheetEntry` com controle de tempo start/stop
  - Cálculo automático de duração e valores faturáveis
  - Sistema de aprovações hierárquico
  - Suporte a horas extras, fins de semana e feriados

### **Alterado**
- **Arquitetura Reorganizada**
  - Separação clara entre entidades core, business e workflow
  - Estrutura de pacotes otimizada para melhor organização
  - Relacionamentos JPA aprimorados com fetch strategies apropriadas

- **Validações Aprimoradas**
  - Implementação de Bean Validation com anotações específicas
  - Validações de negócio para campos críticos
  - Anotações de tamanho e formato para campos de texto

### **Corrigido**
- **Imports Não Utilizados**
  - Remoção de imports desnecessários em todas as entidades
  - Limpeza de código para melhor performance de compilação

- **APIs Deprecated**
  - Substituição de `BigDecimal.ROUND_HALF_UP` por `RoundingMode.HALF_UP`
  - Atualização para APIs Java 17 compatíveis

### **Documentação**
- README.md completamente reformulado com badges e estrutura organizada
- Documentação detalhada de todas as novas funcionalidades
- Guias de instalação e execução atualizados
- Exemplos de uso para cada nova funcionalidade

### **Estrutura do Projeto**
```
src/main/java/com/proximaai/domain/entity/
├── core/           # Entidades principais (User, Project, Task)
├── business/       # Funcionalidades de negócio
│   ├── TaskDependency.java
│   ├── ResourceAllocation.java
│   ├── CustomWorkflow.java
│   ├── ProjectTemplate.java
│   └── Timesheet.java
├── workflow/       # Sistema de workflows
│   ├── WorkflowStatus.java
│   └── WorkflowTransition.java
└── ai/             # Funcionalidades de IA e Machine Learning
    ├── DelayPrediction.java
    ├── AllocationRecommendation.java
    ├── ProjectSummary.java
    ├── SentimentAnalysis.java
    ├── TaskCluster.java
    └── ClusterTask.java
```

## [1.2.0] - 2025-08-24

### **Adicionado**
- **Funcionalidades Avançadas de IA e Machine Learning**
  - **Predição de Atrasos**: Sistema de ML para prever atrasos em tarefas
  - **Recomendações de Alocação**: IA sugere melhor profissional para cada tarefa
  - **Resumo Automático de Status**: Geração automática de relatórios em linguagem natural
  - **Análise de Sentimento**: Monitoramento de bem-estar da equipe
  - **Clusterização Automática**: Agrupamento inteligente de tarefas relacionadas

- **Entidades de IA**
  - `DelayPrediction`: Predições de atrasos com scores de confiança
  - `AllocationRecommendation`: Recomendações inteligentes de alocação
  - `ProjectSummary`: Resumos automáticos com IA generativa
  - `SentimentAnalysis`: Análise de sentimento e burnout risk
  - `TaskCluster`: Clusterização automática de tarefas
  - `ClusterTask`: Relacionamento tarefa-cluster

- **Repositórios JPA de IA**
  - `DelayPredictionRepository`: Consultas otimizadas para predições
  - `AllocationRecommendationRepository`: Busca por scores e skills
  - `ProjectSummaryRepository`: Filtros por tipo e período
  - `SentimentAnalysisRepository`: Análises por humor e estresse
  - `TaskClusterRepository`: Clusters por qualidade e algoritmo
  - `ClusterTaskRepository`: Relacionamentos tarefa-cluster

- **Algoritmos de Machine Learning**
  - Regressão para predição de atrasos
  - Clustering para organização de tarefas
  - NLP para análise de sentimento
  - IA generativa para resumos automáticos
  - Similarity scoring para recomendações

### **Alterado**
- **Arquitetura de IA**
  - Novo pacote `ai` para funcionalidades de inteligência artificial
  - Separação clara entre entidades de negócio e entidades de IA
  - Sistema de scores e métricas para todas as funcionalidades de IA
  - Estrutura de repositórios com consultas JPQL otimizadas

### **Documentação**
- `AI_FEATURES_OVERVIEW.md`: Documentação completa das funcionalidades de IA
- Exemplos de uso para cada funcionalidade de ML
- Casos de uso e benefícios para organizações
- Status de implementação atualizado com repositórios

---

## [1.0.0] - 2024-12-01

### **Adicionado**
- Sistema base de gerenciamento de projetos
- Entidades principais: User, Project, Task, Team
- Sistema de notificações
- Autenticação JWT
- Documentação OpenAPI
- Configuração Docker
- Sistema de auditoria
- Controle de acesso baseado em roles

---

##  Notas de Versão

### **Migração da Versão 1.0.0 para 1.1.0**
- **Compatibilidade**: Totalmente compatível com versão anterior
- **Breaking Changes**: Nenhuma mudança que quebre compatibilidade
- **Database**: Novas tabelas serão criadas automaticamente via JPA
- **Configuração**: Nenhuma configuração adicional necessária

### **Próximas Versões**
- **1.2.0**: Funcionalidades avançadas de IA e Machine Learning ✅
- **1.3.0**: Implementação de repositórios e serviços
- **1.4.0**: Controllers REST e APIs
- **1.5.0**: Interface de usuário e dashboards
- **2.0.0**: Funcionalidades avançadas de IA e machine learning

---

##  Contribuição

Para contribuir com este projeto, consulte o [README.md](README.md) para instruções detalhadas.

##  Licença

Este projeto está licenciado sob a MIT License - veja o arquivo [LICENSE](LICENSE) para detalhes.
