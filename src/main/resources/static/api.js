/**
 * Função genérica para exibir mensagens na tela.
 * @param {string} elementId - O ID do elemento <p> onde a mensagem será exibida.
 * @param {string} text - O texto da mensagem.
 * @param {string} color - A cor do texto (ex: 'red' para erro, 'green' para sucesso).
 */
function showMessage(elementId, text, color) {
    const msgElement = document.getElementById(elementId);
    if (msgElement) {
        msgElement.textContent = text;
        msgElement.style.color = color;
    }
}

/**
 * Função para lidar com o login do usuário.
 * @param {Event} event - O evento de submit do formulário.
 */
async function handleLogin(event) {
    event.preventDefault(); // Impede o recarregamento da página

    const form = event.target;
    const login = form.login.value;
    const senha = form.senha.value;
    const msgElementId = 'msg';

    // Monta o objeto (DTO) que a API espera
    const loginData = {
        login: login,
        senha: senha,
    };

    try {
        const response = await fetch('/api/usuarios/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(loginData),
        });

        if (response.ok) {
            // Se a resposta for OK (status 200), o login foi bem-sucedido
            const usuario = await response.json();
            // Salva o nome do usuário no localStorage para usar na home
            localStorage.setItem('eduquizle_user', usuario.nome);
            // Redireciona para a página principal
            window.location.href = 'home.html';
        } else {
            // Se a resposta não for OK, trata como erro
            const errorData = await response.json();
            const errorMessage = typeof errorData === 'string' ? errorData : errorData.erro || 'Erro desconhecido.';
            showMessage(msgElementId, errorMessage, 'red');
        }
    } catch (error) {
        showMessage(msgElementId, 'Não foi possível conectar ao servidor. Tente novamente.', 'red');
    }
}

/**
 * Função para lidar com o cadastro de um novo usuário.
 * @param {Event} event - O evento de submit do formulário.
 */
async function handleCadastro(event) {
    event.preventDefault(); // Impede o recarregamento da página

    const form = event.target;
    const msgElementId = 'msg';

    // Validação simples no frontend
    if (form.senha.value !== form.confirma_senha.value) {
        showMessage(msgElementId, 'As senhas não conferem.', 'red');
        return;
    }
    if (!form.termos.checked) {
        showMessage(msgElementId, 'Você precisa aceitar os termos de uso.', 'red');
        return;
    }

    const cadastroData = {
        nome: form.nome.value,
        login: form.login.value,
        email: form.email.value,
        senha: form.senha.value,
        confirmaSenha: form.confirma_senha.value,
        termos: form.termos.checked,
    };

    try {
        const response = await fetch('/api/usuarios/cadastro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(cadastroData),
        });

        const data = await response.json();

        if (response.ok) { // Status 201 Created
            // Redireciona para a página de login com uma mensagem de sucesso
            window.location.href = 'index.html?ok=Cadastro realizado com sucesso!';
        } else { // Erro de validação (status 400)
            showMessage(msgElementId, data.erro || 'Ocorreu um erro no cadastro.', 'red');
        }
    } catch (error) {
        showMessage(msgElementId, 'Não foi possível conectar ao servidor. Tente novamente.', 'red');
    }
}