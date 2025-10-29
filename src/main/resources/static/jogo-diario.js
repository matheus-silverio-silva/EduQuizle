let materia = '';
let respostaCorretaId = null;
let tentativas = 0;
const MAX_TENTATIVAS = 8;
let gameOver = false;
let cabecalhos = [];
let listaNomesCompleta = [];

const tituloMateriaEl = document.getElementById('titulo-materia');
const palpiteInputEl = document.getElementById('palpite-input');
const sugestoesEl = document.getElementById('sugestoes');
const gridCabecalhoEl = document.getElementById('grid-cabecalho');
const gridResultadosEl = document.getElementById('grid-resultados');
const tentativasEl = document.getElementById('tentativas');
const mensagemFinalEl = document.getElementById('mensagem-final');
const guessFormEl = document.querySelector('.guess-form');
const guessButtonEl = guessFormEl.querySelector('button');

function displayMessage(message, color = 'red') {
    mensagemFinalEl.textContent = message;
    mensagemFinalEl.style.color = color;
    console.error(message);
}

async function setupGame() {
    // MODIFICADO: A lógica para determinar a matéria pode mudar.
    // Talvez o desafio diário seja único, ou ainda precise da matéria da URL?
    // Por enquanto, vamos manter a leitura da URL, mas talvez precise ajustar.
    const params = new URLSearchParams(window.location.search);
    materia = params.get('materia'); // Ou obter da API do desafio diário?

    if (!materia) { // Esta validação pode não ser mais necessária se a API diária retornar a matéria
        displayMessage('Matéria não especificada.');
        // ... desabilitar inputs ...
        // return; // Comentar/Remover se a API diária definir a matéria
    }

    tentativasEl.parentElement.textContent = `Tentativas: ${tentativas}/${MAX_TENTATIVAS}`;

    await carregarDadosIniciaisDiario(); // MODIFICADO: Chama a função específica do diário
}

async function carregarDadosIniciaisDiario() { // MODIFICADO: Nome da função
    try {
        // Pode precisar enviar informações de autenticação (headers) no futuro
        const desafioResponse = await fetch(`/api/desafios/diario?materia=${materia}`); // Ajuste a URL se necessário (talvez não precise de matéria?)
        if (!desafioResponse.ok) {
            // TODO: Tratar casos específicos, como "Desafio já completado hoje"
            throw new Error(`Erro ao buscar desafio diário: ${desafioResponse.status} ${desafioResponse.statusText}`);
        }
        const desafio = await desafioResponse.json();
        if (!desafio || !desafio.id || !Array.isArray(desafio.cabecalhos)) {
            throw new Error('Resposta da API de desafio diário inválida.');
        }
        respostaCorretaId = desafio.id;
        cabecalhos = desafio.cabecalhos;
        // Se a API retornar a matéria, atualize o título:
        // materia = desafio.materia; // Exemplo
        tituloMateriaEl.textContent = `Desafio Diário de ${materia.charAt(0).toUpperCase() + materia.slice(1)}`; // Garante título correto

        // Busca a lista de nomes possíveis para o autocomplete (mesmo endpoint do livre)
        const palpitesResponse = await fetch(`/api/respostas/nomes?materia=${materia}`);
        if (!palpitesResponse.ok) {
            throw new Error(`Erro ao buscar lista de nomes: ${palpitesResponse.status} ${palpitesResponse.statusText}`);
        }
        const listaNomes = await palpitesResponse.json();
        if (!Array.isArray(listaNomes)) {
            throw new Error('Resposta da API de nomes inválida.');
        }
        listaNomesCompleta = listaNomes;

        renderizarCabecalho();
        renderizarSugestoes();
        // TODO: Carregar tentativas anteriores do dia, se houver

    } catch (error) {
        displayMessage(`Erro ao carregar desafio diário: ${error.message}`);
        palpiteInputEl.disabled = true;
        guessButtonEl.disabled = true;
    }
}
async function handleGuess(event) {
    event.preventDefault();
    if (gameOver || guessButtonEl.disabled) return;

    const palpite = palpiteInputEl.value.trim();
    if (!palpite) return;

    guessButtonEl.disabled = true;
    palpiteInputEl.disabled = true;
    guessButtonEl.textContent = 'Verificando...';
    mensagemFinalEl.textContent = '';

    tentativas++;
    tentativasEl.parentElement.textContent = `Tentativas: ${tentativas}/${MAX_TENTATIVAS}`; // Atualiza contador na interface

    try {
        const response = await fetch('/api/desafios/diario/comparar', { // MODIFICADO: URL
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                palpite: palpite,
            }),
        });

        if (!response.ok) {
            let errorMsg = `Erro ${response.status}: ${response.statusText}`;
            try {
                const errorData = await response.json();
                errorMsg = errorData.erro || errorData.message || errorMsg;
            } catch (jsonError) { /* Ignora */ }
            throw new Error(errorMsg);
        }

        const resultado = await response.json();
        if (!resultado || typeof resultado.acertou === 'undefined' || !resultado.pistas) {
            throw new Error('Resposta da API de comparação inválida.');
        }

        adicionarResultadoNaGrid(palpite, resultado.pistas);

        if (resultado.acertou) {
            finalizarJogo(true, resultado.respostaCorreta || palpite);
        } else if (tentativas >= MAX_TENTATIVAS) {
            finalizarJogo(false, resultado.respostaCorreta);
        }

        palpiteInputEl.value = '';

    } catch (error) {
        displayMessage(`Erro ao processar palpite: ${error.message}`);
        tentativas--; // Desfaz incremento
        tentativasEl.parentElement.textContent = `Tentativas: ${tentativas}/${MAX_TENTATIVAS}`; // Atualiza contador

    } finally {
        if (!gameOver) {
            guessButtonEl.disabled = false;
            palpiteInputEl.disabled = false;
            guessButtonEl.textContent = 'Adivinhar';
            palpiteInputEl.focus();
        }
    }
}

function finalizarJogo(venceu, respostaCorreta) {
    gameOver = true;
    palpiteInputEl.disabled = true;
    guessButtonEl.disabled = true;
    guessButtonEl.textContent = 'Adivinhar';

    if (venceu) {
        displayMessage('Parabéns, você acertou!', '#28a745');
    } else {
        displayMessage(`Fim de jogo! A resposta era: ${respostaCorreta || 'N/A'}`, '#dc3545');
    }
}
document.addEventListener('DOMContentLoaded', setupGame);