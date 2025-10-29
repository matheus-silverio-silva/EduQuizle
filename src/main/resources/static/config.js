const IDIOMA_KEY = 'eduquizle_idioma';
const TEMA_KEY = 'eduquizle_tema';
const selectIdioma = document.getElementById('select-idioma');
const themeRadios = document.querySelectorAll('input[name="theme"]');
const configStatus = document.getElementById('config-status');

function applyTheme(themeValue) {
    const root = document.documentElement; // Usar o <html> é mais comum
    root.classList.remove('light-mode', 'dark-mode'); // Remove temas anteriores

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
function showStatusMessage(message) {
    configStatus.textContent = message;
    setTimeout(() => {
        configStatus.textContent = '';
    }, 3000);
}

function loadSettings() {
    const savedIdioma = localStorage.getItem(IDIOMA_KEY) || 'pt-BR'; // Padrão pt-BR
    if (selectIdioma) {
        selectIdioma.value = savedIdioma;
    }
    console.log(`Idioma carregado: ${savedIdioma}`);

    const savedTheme = localStorage.getItem(TEMA_KEY) || 'system'; // Padrão sistema
    let appliedThemeRadio = null;
    themeRadios.forEach(radio => {
        if (radio.value === savedTheme) {
            radio.checked = true;
            appliedThemeRadio = radio;
        } else {
            radio.checked = false;
        }
    });
    applyTheme(savedTheme);
}
if (selectIdioma) {
    selectIdioma.addEventListener('change', (event) => {
        const newIdioma = event.target.value;
        localStorage.setItem(IDIOMA_KEY, newIdioma);
        showStatusMessage('Idioma salvo!');
        console.log(`Idioma salvo: ${newIdioma}`);
    });
}
themeRadios.forEach(radio => {
    radio.addEventListener('change', (event) => {
        if (event.target.checked) {
            const newTheme = event.target.value;
            localStorage.setItem(TEMA_KEY, newTheme);
            applyTheme(newTheme);
            showStatusMessage('Tema salvo!');
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

document.addEventListener('DOMContentLoaded', loadSettings);

const initialTheme = localStorage.getItem(TEMA_KEY) || 'system';
applyTheme(initialTheme);