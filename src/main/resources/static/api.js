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
async function handleLogin(event) {
    event.preventDefault(); // Impede o recarregamento da página

    const form = event.target;
    const login = form.login.value;
    const senha = form.senha.value;
    const msgElementId = 'msg';

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

            localStorage.setItem('eduquizle_user_login', usuario.login || login);
            localStorage.setItem('eduquizle_user_nome', usuario.nome);

            window.location.href = 'home.html';

        } else {
            const errorData = await response.json();
            const errorMessage = typeof errorData === 'string' ? errorData : errorData.erro || 'Erro desconhecido.';
            showMessage(msgElementId, errorMessage, 'red');
        }
    } catch (error) {
        showMessage(msgElementId, 'Não foi possível conectar ao servidor. Tente novamente.', 'red');
    }
}
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