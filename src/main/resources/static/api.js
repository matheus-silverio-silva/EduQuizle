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
 * Lida com a submissão do formulário de login.
 */
async function handleLogin(event) {
    event.preventDefault(); // Impede o recarregamento da página

    const form = event.target;
    const login = form.login.value;
    const senha = form.senha.value;
    const msgElementId = 'msg'; // ID do elemento de mensagem no form

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
            const usuario = await response.json();

            // Salva os dados do usuário no localStorage
            localStorage.setItem('eduquizle_user_login', usuario.login || login);

            // --- CORREÇÃO AQUI ---
            // A chave deve ser 'eduquizle_user' para ser lida corretamente pela home.html
            localStorage.setItem('eduquizle_user', usuario.nome);
            // ---------------------

            // Redireciona para a home
            window.location.href = 'home.html';

        } else {
            // Trata erros de login (ex: senha errada)
            const errorData = await response.json();
            const errorMessage = typeof errorData === 'string' ? errorData : errorData.erro || 'Login ou senha inválidos.';
            showMessage(msgElementId, errorMessage, 'red');
        }
    } catch (error) {
        // Trata erros de conexão
        console.error("Erro de fetch no login:", error);
        showMessage(msgElementId, 'Não foi possível conectar ao servidor. Tente novamente.', 'red');
    }
}

/**
 * Lida com a submissão do formulário de cadastro.
 */
async function handleCadastro(event) {
    event.preventDefault(); // Impede o recarregamento da página

    const form = event.target;
    const msgElementId = 'msg'; // ID do elemento de mensagem no form

    // Validação simples no frontend
    if (form.senha.value !== form.confirma_senha.value) {
        showMessage(msgElementId, 'As senhas não conferem.', 'red');
        return;
    }
    if (form.senha.value.length < 6) {
        showMessage(msgElementId, 'A senha deve ter no mínimo 6 caracteres.', 'red');
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

        if (response.ok) {
            window.location.href = 'index.html?ok=Cadastro realizado com sucesso!';
        } else {
            showMessage(msgElementId, data.erro || 'Ocorreu um erro no cadastro.', 'red');
        }
    } catch (error) {
        console.error("Erro de fetch no cadastro:", error);
        showMessage(msgElementId, 'Não foi possível conectar ao servidor. Tente novamente.', 'red');
    }
}