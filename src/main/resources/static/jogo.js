let materia = '';
let respostaCorretaId = null;
let tentativas = 0;
const MAX_TENTATIVAS = 10; // Usar const para valores que não mudam
let gameOver = false;
let cabecalhos = [];
let listaNomesCompleta = []; // Armazena a lista completa de nomes para referência

const tituloMateriaEl = document.getElementById('titulo-materia');
const palpiteInputEl = document.getElementById('palpite-input');
const sugestoesEl = document.getElementById('sugestoes');
const gridCabecalhoEl = document.getElementById('grid-cabecalho');
const gridResultadosEl = document.getElementById('grid-resultados');
const tentativasEl = document.getElementById('tentativas');
const mensagemFinalEl = document.getElementById('mensagem-final');
const guessFormEl = document.querySelector('.guess-form'); // Pegar o formulário
const guessButtonEl = guessFormEl.querySelector('button'); // Pegar o botão

function displayMessage(message, color = 'red') {
    mensagemFinalEl.textContent = message;
    mensagemFinalEl.style.color = color;
    console.error(message); // Mantém o log de erro para debug
}

async function setupGame() {
    const params = new URLSearchParams(window.location.search);
    materia = params.get('materia');

    if (!materia) {
        displayMessage('Matéria não especificada na URL.');
        palpiteInputEl.disabled = true; // Desabilita o jogo se não houver matéria
        guessButtonEl.disabled = true;
        return;
    }
    tituloMateriaEl.textContent = `Desafio de ${materia.charAt(0).toUpperCase() + materia.slice(1)}`;

    await carregarDadosIniciais();
}

async function carregarDadosIniciais() {
    try {
        // Busca o ID da resposta correta e os cabeçalhos da tabela
        const desafioResponse = await fetch(`/api/desafios/livre?materia=${materia}`);
        if (!desafioResponse.ok) {
            throw new Error(`Erro ao buscar desafio: ${desafioResponse.status} ${desafioResponse.statusText}`);
        }
        const desafio = await desafioResponse.json();
        // Validação mínima da resposta
        if (!desafio || !desafio.id || !Array.isArray(desafio.cabecalhos)) {
            throw new Error('Resposta da API de desafio inválida.');
        }
        respostaCorretaId = desafio.id;
        cabecalhos = desafio.cabecalhos;

        // Busca a lista de nomes possíveis para o autocomplete
        const palpitesResponse = await fetch(`/api/respostas/nomes?materia=${materia}`);
        if (!palpitesResponse.ok) {
            throw new Error(`Erro ao buscar lista de nomes: ${palpitesResponse.status} ${palpitesResponse.statusText}`);
        }
        const listaNomes = await palpitesResponse.json();
        if (!Array.isArray(listaNomes)) {
            throw new Error('Resposta da API de nomes inválida.');
        }
        listaNomesCompleta = listaNomes;

        // Preenche o cabeçalho da tabela
        renderizarCabecalho();

        // Preenche as sugestões do datalist
        renderizarSugestoes();

    } catch (error) {
        displayMessage(`Erro ao carregar dados iniciais: ${error.message}`);
        // Desabilita o jogo se a inicialização falhar
        palpiteInputEl.disabled = true;
        guessButtonEl.disabled = true;
    }
}

function renderizarCabecalho() {
    gridCabecalhoEl.innerHTML = ''; // Limpa cabeçalho anterior, se houver
    const cabecalhoRow = document.createElement('tr');
    cabecalhos.forEach(texto => {
        const th = document.createElement('th');
        th.textContent = texto;
        cabecalhoRow.appendChild(th);
    });
    gridCabecalhoEl.appendChild(cabecalhoRow);
}

function renderizarSugestoes() {
    sugestoesEl.innerHTML = ''; // Limpa sugestões anteriores
    listaNomesCompleta.forEach(nome => {
        const option = document.createElement('option');
        option.value = nome;
        sugestoesEl.appendChild(option);
    });
}

async function handleGuess(event) {
    event.preventDefault(); // Impede o recarregamento padrão da página
    // Impede o envio se o jogo acabou ou se já está processando um palpite
    if (gameOver || guessButtonEl.disabled) return;

    const palpite = palpiteInputEl.value.trim();
    if (!palpite) return; // Ignora palpites vazios

    // --- Feedback de Carregamento ---
    guessButtonEl.disabled = true;
    palpiteInputEl.disabled = true;
    guessButtonEl.textContent = 'Verificando...';
    mensagemFinalEl.textContent = ''; // Limpa mensagens anteriores

    tentativas++;
    tentativasEl.textContent = tentativas;

    try {
        // Envia o palpite para a API
        const response = await fetch('/api/desafios/comparar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                palpite: palpite,
                respostaId: respostaCorretaId, // Envia o ID da resposta correta para comparação no backend
            }),
        });

        // Trata erros vindos da API (ex: 400 Bad Request, 500 Internal Server Error)
        if (!response.ok) {
            let errorMsg = `Erro ${response.status}: ${response.statusText}`;
            try {
                // Tenta pegar uma mensagem de erro mais específica do corpo da resposta
                const errorData = await response.json();
                errorMsg = errorData.erro || errorData.message || errorMsg;
            } catch(jsonError) { /* Ignora se a resposta de erro não for JSON */ }
            throw new Error(errorMsg);
        }

        const resultado = await response.json();
        // Validação mínima da resposta de comparação
        if (!resultado || typeof resultado.acertou === 'undefined' || !resultado.pistas) {
            throw new Error('Resposta da API de comparação inválida.');
        }

        // Adiciona a linha com as pistas na tabela
        adicionarResultadoNaGrid(palpite, resultado.pistas);

        // Verifica o estado do jogo
        if (resultado.acertou) {
            finalizarJogo(true, resultado.respostaCorreta || palpite); // Usa o próprio palpite se a API não retornar o nome
        } else if (tentativas >= MAX_TENTATIVAS) {
            finalizarJogo(false, resultado.respostaCorreta);
        }

        palpiteInputEl.value = ''; // Limpa o campo após o sucesso

    } catch (error) {
        displayMessage(`Erro ao processar palpite: ${error.message}`);
        tentativas--; // Desfaz o incremento da tentativa em caso de erro
        tentativasEl.textContent = tentativas;

    } finally {
        if (!gameOver) {
            guessButtonEl.disabled = false;
            palpiteInputEl.disabled = false;
            guessButtonEl.textContent = 'Adivinhar';
            palpiteInputEl.focus(); // Coloca o foco de volta no input
        }
    }
}

function adicionarResultadoNaGrid(palpiteTexto, pistas) {
    const newRow = document.createElement('tr');
    cabecalhos.forEach((cabecalho, index) => {
        const td = document.createElement('td');
        const cabecalhoKey = cabecalho; // Usa o nome exato do cabeçalho como chave

        if (index === 0 && cabecalhoKey.toLowerCase() === 'palpite') {
            td.textContent = palpiteTexto;
            const statusPalpite = pistas[cabecalhoKey]?.status || 'incorreto'; // Assume incorreto se não vier
            td.className = statusPalpite;
        } else {
            const pistaInfo = pistas[cabecalhoKey];

            if (pistaInfo) {
                td.textContent = pistaInfo.texto !== null && pistaInfo.texto !== undefined ? pistaInfo.texto : '-';

                td.className = pistaInfo.status || 'incorreto';

                if (pistaInfo.direcao === 'maior') {
                    td.textContent += ' ↑';
                } else if (pistaInfo.direcao === 'menor') {
                    td.textContent += ' ↓';
                }
            } else {
                td.textContent = '?';
                td.className = 'incorreto';
                console.warn(`Pista não encontrada para o cabeçalho: ${cabecalhoKey}`);
            }
        }
        newRow.appendChild(td);
    });
    gridResultadosEl.appendChild(newRow);
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