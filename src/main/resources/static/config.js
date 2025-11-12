const IDIOMA_KEY = 'eduquizle_idioma';
const TEMA_KEY = 'eduquizle_tema';

const selectIdioma = document.getElementById('select-idioma');
const themeRadios = document.querySelectorAll('input[name="theme"]');
const configStatus = document.getElementById('config-status');

const profileForm = document.getElementById('profile-form');
const saveProfileButton = document.getElementById('save-profile-button');
const profileStatus = document.getElementById('profile-status');

const passwordForm = document.getElementById('password-form');
const changePasswordButton = document.getElementById('change-password-button');
const passwordStatus = document.getElementById('password-status');

function showMessage(elementId, textKeyOrText, color = 'red', clearAfter = 3000) {
    const msgElement = document.getElementById(elementId);
    if (!msgElement) return;

    const message = (typeof currentTranslations !== 'undefined' && currentTranslations[textKeyOrText])
        ? currentTranslations[textKeyOrText]
        : textKeyOrText;

    msgElement.textContent = message;
    msgElement.style.color = color;

    if (clearAfter > 0) {
        setTimeout(() => {
            if (msgElement.textContent === message) {
                msgElement.textContent = '';
            }
        }, clearAfter);
    }
}

function applyTheme(themeValue) {
    const root = document.documentElement;
    root.classList.remove('light-mode', 'dark-mode');

    let themeToApply = themeValue;
    if (themeValue === 'system') {
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        themeToApply = prefersDark ? 'dark' : 'light';
    }

    if (themeToApply === 'dark') {
        root.classList.add('dark-mode');
    } else {
        root.classList.add('light-mode');
    }
    console.log(`Tema aplicado: ${themeToApply} (escolha: ${themeValue})`);
}

function loadSettings() {
    const savedIdioma = localStorage.getItem(IDIOMA_KEY) || 'pt-BR';
    if (selectIdioma) {
        selectIdioma.value = savedIdioma;
    }
    console.log(`Idioma carregado: ${savedIdioma}`);

    const savedTheme = localStorage.getItem(TEMA_KEY) || 'system';
    themeRadios.forEach(radio => {
        if (radio.value === savedTheme) {
            radio.checked = true;
        } else {
            radio.checked = false;
        }
    });
    applyTheme(savedTheme);
}

if (selectIdioma) {
    selectIdioma.addEventListener('change', async (event) => {
        const newIdioma = event.target.value;
        if (window.changeLanguage) {
            await window.changeLanguage(newIdioma);
            showMessage('config-status', 'config_saved_language', 'green');
        } else {
            localStorage.setItem(IDIOMA_KEY, newIdioma);
            showMessage('config-status', 'Idioma salvo! (Recarregue para ver efeito)', 'green');
        }
        console.log(`Idioma salvo: ${newIdioma}`);
    });
}

themeRadios.forEach(radio => {
    radio.addEventListener('change', (event) => {
        if (event.target.checked) {
            const newTheme = event.target.value;
            localStorage.setItem(TEMA_KEY, newTheme);
            applyTheme(newTheme);
            showMessage('config-status', 'config_saved_theme', 'green');
        }
    });
});

window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    const currentThemeSetting = localStorage.getItem(TEMA_KEY) || 'system';
    if (currentThemeSetting === 'system') {
        console.log("Preferência de cor do sistema mudou. Reaplicando tema...");
        applyTheme('system');
    }
});

async function loadUserProfile() {
    console.log("Carregando perfil do usuário...");
    try {
        const response = await fetch('/api/usuarios/me');

        if (response.status === 401 || response.status === 403) {
            window.location.href = 'index.html?erro=Sessão expirada. Faça login novamente.';
            return;
        }
        if (!response.ok) {
            throw new Error(`Erro ${response.status} ao buscar perfil.`);
        }

        const user = await response.json();

        const loginInput = document.getElementById('profile-login');
        const nameInput = document.getElementById('profile-name');
        const emailInput = document.getElementById('profile-email');

        if (loginInput) loginInput.value = user.login || '';
        if (nameInput) nameInput.value = user.nome || '';
        if (emailInput) emailInput.value = user.email || '';

    } catch (error) {
        showMessage('profile-status', `Erro ao carregar dados do perfil. ${error.message}`, 'red', 0);
    }
}

async function handleProfileUpdate(event) {
    event.preventDefault();
    const form = event.target;
    const button = saveProfileButton;
    const statusElementId = 'profile-status';

    const updatedData = {
        nome: form.nome.value.trim(),
        email: form.email.value.trim(),
        login: document.getElementById('profile-login').value
    };

    button.disabled = true;
    button.textContent = 'Salvando...';
    showMessage(statusElementId, '', 'black', 0);

    try {
        const response = await fetch('/api/usuarios/me', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedData)
        });

        if (response.ok) {
            showMessage(statusElementId, 'profile_update_success', 'green');
            localStorage.setItem('eduquizle_user', updatedData.nome);
        } else {
            let errorMsg = `Erro ${response.status}`;
            try {
                const errorData = await response.json();
                errorMsg = errorData.erro || errorData.message || errorMsg;
            } catch (e) { /* Ignora */ }
            throw new Error(errorMsg);
        }
    } catch (error) {
        showMessage(statusElementId, `Erro ao atualizar perfil: ${error.message}`, 'red', 0);
    } finally {
        button.disabled = false;
        button.textContent = 'Salvar Alterações';
    }
}

async function handleChangePassword(event) {
    event.preventDefault();
    const form = event.target;
    const button = changePasswordButton;
    const statusElementId = 'password-status';

    const currentPassword = form.senhaAtual.value;
    const newPassword = form.novaSenha.value;
    const confirmPassword = form.confirmacaoNovaSenha.value;

    if (!currentPassword || !newPassword || !confirmPassword) {
        showMessage(statusElementId, 'password_error_all_fields', 'red', 0);
        return;
    }
    if (newPassword !== confirmPassword) {
        showMessage(statusElementId, 'password_error_mismatch', 'red', 0);
        return;
    }
    if (newPassword.length < 6) {
        showMessage(statusElementId, 'password_error_length', 'red', 0);
        return;
    }

    const passwordData = {
        senhaAtual: currentPassword,
        novaSenha: newPassword,
        confirmacaoNovaSenha: confirmPassword,
        login: document.getElementById('profile-login').value
    };

    button.disabled = true;
    button.textContent = 'Alterando...';
    showMessage(statusElementId, '', 'black', 0);

    try {
        const response = await fetch('/api/usuarios/me/password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(passwordData)
        });

        if (response.ok) {
            showMessage(statusElementId, 'password_update_success', 'green');
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
        showMessage(statusElementId, `Erro ao alterar senha: ${error.message}`, 'red', 0);
    } finally {
        button.disabled = false;
        button.textContent = 'Alterar Senha';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadSettings();

    setTimeout(() => {
        loadUserProfile();
    }, 100);

    if (profileForm) {
        profileForm.addEventListener('submit', handleProfileUpdate);
    }
    if (passwordForm) {
        passwordForm.addEventListener('submit', handleChangePassword);
    }
    console.log("Listeners de Tema, Idioma, Perfil e Senha configurados.");
});

const initialTheme = localStorage.getItem(TEMA_KEY) || 'system';
applyTheme(initialTheme);