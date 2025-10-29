function showProfileMessage(elementId, textKeyOrText, color = 'red', clearAfter = 3000) {
    const msgElement = document.getElementById(elementId);
    if (!msgElement) return;

    const message = (typeof currentTranslations !== 'undefined' && currentTranslations[textKeyOrText])
        ? currentTranslations[textKeyOrText]
        : textKeyOrText;

    msgElement.textContent = message;
    msgElement.style.color = color;

    if (clearAfter > 0) {
        setTimeout(() => {
            // Só limpa se a mensagem ainda for a mesma (evita apagar msg de erro subsequente)
            if (msgElement.textContent === message) {
                msgElement.textContent = '';
            }
        }, clearAfter);
    }
}

async function loadUserProfile() {
    console.log("Carregando perfil do usuário...");
    try {
        // !!! IMPORTANTE: Substitua '/api/usuarios/me' pelo endpoint real se for diferente !!!
        // !!! IMPORTANTE: Adicionar headers de autenticação (ex: Bearer token) se sua API exigir !!!
        const response = await fetch('/api/usuarios/me' /* , { headers: { 'Authorization': 'Bearer SEU_TOKEN' }} */);

        if (response.status === 401 || response.status === 403) {
            // Não autorizado ou proibido - redireciona para login
            window.location.href = 'index.html?erro=Sessão expirada. Faça login novamente.';
            return;
        }
        if (!response.ok) {
            throw new Error(`Erro ${response.status} ao buscar perfil.`);
        }

        const user = await response.json();

        // Preenche os campos do formulário
        const loginInput = document.getElementById('profile-login');
        const nameInput = document.getElementById('profile-name');
        const emailInput = document.getElementById('profile-email');

        if (loginInput) loginInput.value = user.login || '';
        if (nameInput) nameInput.value = user.nome || '';
        if (emailInput) emailInput.value = user.email || '';

    } catch (error) {
        showProfileMessage('profile-status', `Erro ao carregar dados do perfil. ${error.message}`, 'red', 0);
    }
}

async function handleProfileUpdate(event) {
    event.preventDefault();
    const form = event.target;
    const button = document.getElementById('save-profile-button');
    const statusElementId = 'profile-status';

    const updatedData = {
        nome: form.nome.value.trim(),
        email: form.email.value.trim()
    };

    // Feedback visual de carregamento
    button.disabled = true;
    button.textContent = 'Salvando...'; // TODO: Internacionalizar "Salvando..."
    showProfileMessage(statusElementId, '', 'black', 0); // Limpa status anterior

    try {
        // !!! IMPORTANTE: Substitua '/api/usuarios/me' pelo endpoint real se for diferente !!!
        // !!! IMPORTANTE: Adicionar headers de autenticação se necessário !!!
        const response = await fetch('/api/usuarios/me', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                // 'Authorization': 'Bearer SEU_TOKEN'
            },
            body: JSON.stringify(updatedData)
        });

        if (response.ok) {
            showProfileMessage(statusElementId, 'profile_update_success', 'green');
            localStorage.setItem('eduquizle_user', updatedData.nome);
        } else {
            let errorMsg = `Erro ${response.status}`;
            try {
                const errorData = await response.json();
                errorMsg = errorData.erro || errorData.message || errorMsg;
            } catch (e) {  }
            throw new Error(errorMsg);
        }
    } catch (error) {
        showProfileMessage(statusElementId, `Erro ao atualizar perfil: ${error.message}`, 'red', 0);
    } finally {
        // Restaura o botão
        button.disabled = false;
        // TODO: Internacionalizar "Salvar Alterações"
        button.textContent = 'Salvar Alterações';
    }
}

async function handleChangePassword(event) {
    event.preventDefault();
    const form = event.target;
    const button = document.getElementById('change-password-button');
    const statusElementId = 'password-status';

    const currentPassword = form.senhaAtual.value;
    const newPassword = form.novaSenha.value;
    const confirmPassword = form.confirmacaoNovaSenha.value;

    if (!currentPassword || !newPassword || !confirmPassword) {
        showProfileMessage(statusElementId, 'password_error_all_fields', 'red', 0); // Chave i18n
        return;
    }
    if (newPassword !== confirmPassword) {
        showProfileMessage(statusElementId, 'password_error_mismatch', 'red', 0); // Chave i18n
        return;
    }
    if (newPassword.length < 6) {
        showProfileMessage(statusElementId, 'password_error_length', 'red', 0); // Chave i18n
        return;
    }

    const passwordData = {
        senhaAtual: currentPassword,
        novaSenha: newPassword,
        confirmacaoNovaSenha: confirmPassword
    };

    button.disabled = true;
    button.textContent = 'Alterando...'; // TODO: Internacionalizar
    showProfileMessage(statusElementId, '', 'black', 0); // Limpa status

    try {
        // !!! IMPORTANTE: Substitua '/api/usuarios/me/password' pelo endpoint real se for diferente !!!
        // !!! IMPORTANTE: Adicionar headers de autenticação se necessário !!!
        const response = await fetch('/api/usuarios/me/password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(passwordData)
        });

        if (response.ok) {
            showProfileMessage(statusElementId, 'password_update_success', 'green'); // Chave i18n
            form.reset();
        } else {
            let errorMsg = `Erro ${response.status}`;
            try {
                const errorData = await response.json();
                errorMsg = errorData.erro || errorData.message || errorMsg;
            } catch (e) { /* Ignora */ }
            throw new Error(errorMsg);
        }
    } catch (error) {
        showProfileMessage(statusElementId, `Erro ao alterar senha: ${error.message}`, 'red', 0);
    } finally {
        button.disabled = false;
        button.textContent = 'Alterar Senha';
    }
}

document.addEventListener('DOMContentLoaded', () => {

    setTimeout(() => {
        loadUserProfile();

        const profileForm = document.getElementById('profile-form');
        if (profileForm) {
            profileForm.addEventListener('submit', handleProfileUpdate);
        }

        const passwordForm = document.getElementById('password-form');
        if (passwordForm) {
            passwordForm.addEventListener('submit', handleChangePassword);
        }
        console.log("Listeners de perfil e senha adicionados.");
    }, 100);
});