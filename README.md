# EduQuizle 🧠💡

<p align="center">
  <img src="src/main/resources/static/Imagens/logo projeto.png" alt="Logo do EduQuizle" width="300"/>
</p>

<p align="center">
  Um jogo de adivinhação educacional inspirado em Wordle, onde o desafio é descobrir conceitos de diversas matérias escolares através de pistas.
</p>

---

## 📖 Sobre o Projeto

EduQuizle é uma aplicação web que transforma o aprendizado em um jogo divertido. Em vez de adivinhar palavras, o jogador deve adivinhar um conceito, evento histórico, elemento químico ou local geográfico. A cada tentativa, o sistema fornece dicas visuais, indicando se os atributos do palpite estão corretos, parciais ou incorretos, ajudando o jogador a deduzir a resposta certa.

O projeto foi totalmente migrado de uma arquitetura Java/JPA tradicional para uma API REST moderna utilizando **Spring Boot**, com um front-end desacoplado em HTML/CSS/JavaScript.

---

## ✨ Funcionalidades

* **Autenticação de Usuários**: Sistema completo de cadastro e login.
* **Modo de Jogo "Desafio Livre"**: Permite que o jogador escolha uma matéria e jogue sem limites.
* **Mecânica de Pistas**: Feedback visual (cores) para cada atributo do palpite.
* **API RESTful**: Backend robusto que serve os dados e a lógica do jogo.
* **Front-end Dinâmico**: Interface que consome a API de forma assíncrona, sem recarregar a página.

---

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído com as seguintes tecnologias:

#### **Backend**
* **Java 17+**
* **Spring Boot 3**
* **Spring Data JPA** (Hibernate)
* **MySQL** (Banco de Dados Persistente)
* **H2 Database** (Banco de Dados em Memória para Testes)
* **Lombok**
* **Maven**

#### **Frontend**
* **HTML5**
* **CSS3**
* **JavaScript (ES6+)**
    * Utilização da `fetch` API para comunicação com o backend.

---

## 🚀 Como Começar

Siga os passos abaixo para executar o projeto em sua máquina local.

### **Pré-requisitos**

* **JDK 17** ou superior instalado.
* **Maven** instalado e configurado.
* **MySQL Server** instalado e rodando.

### **Configuração**

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/matheus-silverio-silva/EduQuizle.git](https://github.com/matheus-silverio-silva/EduQuizle.git)
    cd EduQuizle
    ```

2.  **Configure o Banco de Dados MySQL:**
    * Crie uma base de dados vazia. Ex: `eduquizle_db`.
    * Crie um usuário com permissões para acessar e modificar esta base de dados.

3.  **Configure o Backend:**
    * Abra o arquivo `src/main/resources/application.properties`.
    * Altere as seguintes propriedades com os dados do seu banco de dados:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/eduquizle_db
        spring.datasource.username=seu_usuario_aqui
        spring.datasource.password=sua_senha_aqui
        ```

4.  **Execute a Aplicação:**
    * Abra um terminal na raiz do projeto e execute o comando Maven:
        ```bash
        mvn spring-boot:run
        ```
    * O servidor será iniciado na porta `8080`.

5.  **Acesse o Jogo:**
    * Abra seu navegador e acesse `http://localhost:8080`.

---

## 🔌 Endpoints da API

A API REST do EduQuizle oferece os seguintes endpoints:

| Método HTTP | Endpoint                    | Descrição                                         |
| :---------- | :-------------------------- | :------------------------------------------------ |
| `POST`      | `/api/usuarios/cadastro`    | Registra um novo usuário.                         |
| `POST`      | `/api/usuarios/login`       | Autentica um usuário e retorna seus dados.        |
| `GET`       | `/api/desafios/livre`       | Inicia um novo desafio livre para a matéria informada. |
| `GET`       | `/api/respostas/nomes`      | Retorna uma lista de nomes para o autocomplete.   |
| `POST`      | `/api/desafios/comparar`    | Compara um palpite com a resposta correta.        |

---

## ✒️ Autor

* **Matheus Silvério da Silva** - *Desenvolvedor do Projeto*
