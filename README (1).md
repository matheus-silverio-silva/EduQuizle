# 📘 EduQuizle  

Plataforma educacional gamificada de quizzes que permite interação entre **alunos e professores**, oferecendo desafios diários, sistema de pontuação e acompanhamento de desempenho.  

## 🚀 Objetivo  
O **EduQuizle** busca tornar o processo de aprendizado mais dinâmico e interativo por meio de jogos de perguntas e respostas. Ele incentiva a **participação contínua**, promove a **fixação de conteúdo** e possibilita que professores criem quizzes personalizados para seus alunos.  

## ⚙️ Funcionalidades  

- 👤 **Cadastro e Login de Usuários**  
  - Registro com nome, e-mail, senha e perfil (professor ou aluno).  
  - Autenticação segura e recuperação de senha.  

- 🎯 **Seleção de Temas**  
  - O usuário pode escolher entre diversos temas, como: Fauna Brasileira, Figuras Históricas, Elementos Químicos etc.  

- 📝 **Geração de Desafios**  
  - **Modo Diário**: desafio único, igual para todos os usuários.  
  - **Modo Livre**: quizzes livres baseados nos temas selecionados.  

- 🏆 **Sistema de Pontuação e Ranking**  
  - Pontuação baseada em acertos e tempo de resposta.  
  - Ranking geral e por tema.  

- 👨‍🏫 **Painel do Professor**  
  - Criação de quizzes personalizados.  
  - Relatórios de desempenho dos alunos.  

- 📊 **Histórico e Estatísticas**  
  - Alunos podem acompanhar sua evolução.  
  - Professores têm acesso ao progresso das turmas.  

## 🛠️ Tecnologias Utilizadas  

### Backend  
- **Java** com **Spring Boot**  
- **Hibernate / JPA** para persistência  
- **MySQL** como banco de dados  

### Frontend  
- **HTML, CSS, JavaScript**
- Responsividade para dispositivos móveis  

### Outras  
- **Maven** para gerenciamento de dependências  
- **Git/GitHub** para versionamento  

## 📂 Estrutura do Projeto  

```
EduQuizle/
│── backend/          # Código do servidor (Spring Boot)
│── frontend/         # Interface do usuário (HTML/CSS/JS)
│── docs/             # Documentação e diagramas
│── README.md         # Documentação principal
```

## ⚡ Como Executar o Projeto  

1. Clone o repositório:  
   ```bash
   git clone https://github.com/seu-usuario/eduquizle.git
   ```

2. Configure o backend:  
   - Importar o projeto **Maven** no Eclipse/IntelliJ.  
   - Ajustar o `application.properties` com as credenciais do MySQL.  
   - Executar a aplicação Spring Boot.  

3. Configure o banco de dados:  
   - Rodar os scripts de criação do banco em `scripts/db.sql`.  

4. Configure o frontend:  
   - Acesse a pasta `frontend/`.  
   - Execute em servidor local (ex: Live Server, XAMPP ou Node.js).  

5. Acesse no navegador:  
   ```
   http://localhost:8080
   ```  

## 👨‍💻 Equipe  
- **Matheus Silvério** – Backend & Arquitetura  
