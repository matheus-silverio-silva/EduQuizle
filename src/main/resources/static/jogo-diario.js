let materia = '';
let respostaCorretaId = null;
let idDesafioAtual = null;
let tentativas = 0;
const MAX_TENTATIVAS = 8;
let gameOver = false;
let cabecalhos = [];
let listaNomesCompleta = [];

let tituloMateriaEl;
let palpiteInputEl;
let sugestoesEl;
let gridCabecalhoEl;
let gridResultadosEl;
let tentativasEl;
let mensagemFinalEl;
let guessFormEl;
let guessButtonEl;

document.addEventListener('DOMContentLoaded', () => {
    console.log("DEBUG [jogo-diario.js]: Página carregada. Definindo elementos...");

    tituloMateriaEl = document.getElementById('titulo-materia');
    palpiteInputEl = document.getElementById('palpite-input');
    sugestoesEl = document.getElementById('sugestoes');
    gridCabecalhoEl = document.getElementById('grid-cabecalho');
    gridResultadosEl = document.getElementById('grid-resultados');
    tentativasEl = document.getElementById('tentativas');
    mensagemFinalEl = document.getElementById('mensagem-final');
    guessFormEl = document.querySelector('.guess-form');
    guessButtonEl = guessFormEl.querySelector('button');

    if (guessFormEl) {
        guessFormEl.addEventListener('submit', handleGuess);
    }

    setupGame();
});

function displayMessage(message, color = 'red') {
    if (mensagemFinalEl) {
        mensagemFinalEl.textContent = message;
        mensagemFinalEl.style.color = color;
    }
    console.error(`DEBUG [jogo-diario.js]: displayMessage() - ${message}`);
}


async function setupGame() {

    const params = new URLSearchParams(window.location.search);
    materia = params.get('materia');

    if (!materia) {
        displayMessage('Matéria não especificada na URL.');
        if (palpiteInputEl) palpiteInputEl.disabled = true;
        if (guessButtonEl) guessButtonEl.disabled = true;
        return;
    }

    if (tituloMateriaEl) {
        tituloMateriaEl.textContent = `Desafio Diário de ${materia.charAt(0).toUpperCase() + materia.slice(1)}`;
    }

    if (tentativasEl) {
        tentativasEl.textContent = tentativas;
    }

    await carregarDadosIniciais();
}

async function carregarDadosIniciais() {
    try {
        console.log(`DEBUG [jogo-diario.js]: carregarDadosIniciais() - Buscando desafio: /api/desafios/diario?materia=${materia}`);
        const desafioResponse = await fetch(`/api/desafios/diario?materia=${materia}`);
        if (!desafioResponse.ok) {
            throw new Error(`Erro ao buscar desafio: ${desafioResponse.status} ${desafioResponse.statusText}`);
        }
        const desafio = await desafioResponse.json();
        console.log("DEBUG [jogo-diario.js]: carregarDadosIniciais() - Desafio recebido:", desafio);

        if (!desafio || !desafio.id_resposta || !desafio.id_desafio || !Array.isArray(desafio.cabecalhos)) {
            if (!desafio || !desafio.id || !Array.isArray(desafio.cabecalhos)) {
                throw new Error('Resposta da API de desafio inválida. (Esperava id_resposta e id_desafio)');
            }
            respostaCorretaId = desafio.id;
            idDesafioAtual = desafio.id;
            cabecalhos = desafio.cabecalhos;
        } else {
            respostaCorretaId = desafio.id_resposta;
            idDesafioAtual = desafio.id_desafio;
            cabecalhos = desafio.cabecalhos;
        }

        const chaveStorage = `eduquizle_desafio_${idDesafioAtual}`;
        console.log(`DEBUG [jogo-diario.js]: Verificando localStorage pela chave: ${chaveStorage}`);

        if (localStorage.getItem(chaveStorage) === 'completo') {
            console.warn("DEBUG [jogo-diario.js]: Desafio já completo. Bloqueando o jogo.");
            displayMessage('Você já completou o desafio de hoje para esta matéria!', '#28a745');

            if (palpiteInputEl) palpiteInputEl.disabled = true;
            if (guessButtonEl) guessButtonEl.disabled = true;
            if (tentativasEl && tentativasEl.parentElement) {
                tentativasEl.parentElement.style.display = 'none';
            }
            return;
        }

        console.log(`DEBUG [jogo-diario.js]: carregarDadosIniciais() - Buscando nomes: /api/respostas/nomes?materia=${materia}`);
        const palpitesResponse = await fetch(`/api/respostas/nomes?materia=${materia}`);
        if (!palpitesResponse.ok) {
            throw new Error(`Erro ao buscar lista de nomes: ${palpitesResponse.status} ${palpitesResponse.statusText}`);
        }
        const listaNomes = await palpitesResponse.json();
        listaNomesCompleta = listaNomes;
        console.log(`DEBUG [jogo-diario.js]: carregarDadosIniciais() - ${listaNomes.length} nomes recebidos.`);

        renderizarCabecalho();
        renderizarSugestoes();

    } catch (error) {
        displayMessage(`Erro ao carregar dados iniciais: ${error.message}`);
        if (palpiteInputEl) palpiteInputEl.disabled = true;
        if (guessButtonEl) guessButtonEl.disabled = true;
    }
}

async function handleGuess(event) {
    event.preventDefault();
    if (gameOver || (guessButtonEl && guessButtonEl.disabled)) return;

    const palpite = palpiteInputEl.value.trim();
    if (!palpite) return;

    console.log(`DEBUG [jogo-diario.js]: handleGuess() - Palpite enviado: ${palpite}`);

    guessButtonEl.disabled = true;
    palpiteInputEl.disabled = true;
    guessButtonEl.textContent = 'Verificando...';
    if(mensagemFinalEl) mensagemFinalEl.textContent = '';

    tentativas++;
    if (tentativasEl) {
        tentativasEl.textContent = tentativas;
    }
    console.log(`DEBUG [jogo-diario.js]: handleGuess() - Tentativa número: ${tentativas}`);

    try {
        const endpoint = `/api/desafios/diario/comparar?materia=${materia}`;
        console.log(`DEBUG [jogo-diario.js]: handleGuess() - Chamando API de comparação: ${endpoint}`);

        const response = await fetch(endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                palpite: palpite
            }),
        });

        if (!response.ok) {
            const errorData = await response.json();
            console.error("DEBUG [jogo-diario.js]: handleGuess() - ERRO (response not ok):", errorData);
            let errorMsg = errorData.erro || `Erro ${response.status}: ${response.statusText}`;
            throw new Error(errorMsg);
        }

        const resultado = await response.json();
        console.log("DEBUG [jogo-diario.js]: handleGuess() - Resultado da comparação recebido:", resultado);

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
        tentativas--;
        if (tentativasEl) {
            tentativasEl.textContent = tentativas;
        }
    } finally {
        if (!gameOver) {
            guessButtonEl.disabled = false;
            palpiteInputEl.disabled = false;
            guessButtonEl.textContent = 'Adivinhar';
            palpiteInputEl.focus();
        }
    }
}

function renderizarCabecalho() {
    if (!gridCabecalhoEl) return;
    gridCabecalhoEl.innerHTML = '';
    const cabecalhoRow = document.createElement('tr');
    cabecalhos.forEach(texto => {
        const th = document.createElement('th');
        th.textContent = texto;
        cabecalhoRow.appendChild(th);
    });
    gridCabecalhoEl.appendChild(cabecalhoRow);
}

function renderizarSugestoes() {
    if (!sugestoesEl) return;
    sugestoesEl.innerHTML = '';
    listaNomesCompleta.forEach(nome => {
        const option = document.createElement('option');
        option.value = nome;
        sugestoesEl.appendChild(option);
    });
}

function adicionarResultadoNaGrid(palpiteTexto, pistas) {
    if (!gridResultadosEl) return;
    const newRow = document.createElement('tr');
    cabecalhos.forEach((cabecalho, index) => {
        const td = document.createElement('td');
        const cabecalhoKey = cabecalho;

        if (index === 0 && cabecalhoKey.toLowerCase() === 'palpite') {
            td.textContent = palpiteTexto;
            const statusPalpite = pistas[cabecalhoKey]?.status || 'incorreto';
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
    if (palpiteInputEl) palpiteInputEl.disabled = true;
    if (guessButtonEl) {
        guessButtonEl.disabled = true;
        guessButtonEl.textContent = 'Adivinhar';
    }

    if (venceu) {
        console.log("DEBUG [jogo-diario.js]: finalizarJogo() - O jogador VENCEU.");
        displayMessage('Parabéns, você acertou!', '#28a745');
        const pontosGanhos = calcularPontuacao(tentativas, 'diario');
        console.log(`DEBUG [jogo-diario.js]: finalizarJogo() - Pontos calculados: ${pontosGanhos}`);

        if (pontosGanhos > 0) {
            enviarPontuacaoParaRanking(pontosGanhos);
        }

        const chaveStorage = `eduquizle_desafio_${idDesafioAtual}`;
        localStorage.setItem(chaveStorage, 'completo');
        console.log(`DEBUG [jogo-diario.js]: finalizarJogo() - Desafio ${idDesafioAtual} marcado como completo.`);

    } else {
        console.log("DEBUG [jogo-diario.js]: finalizarJogo() - O jogador PERDEU (sem tentativas).");
        displayMessage(`Fim de jogo! A resposta era: ${respostaCorreta || 'N/A'}`, '#dc3545');
    }
}

function calcularPontuacao(tentativasUsadas, modoJogo) {
    const PONTUACAO_BASE = 100;
    const PENALIDADE_POR_TENTATIVA = 10;
    const MULTIPLICADOR_DIARIO = 1.5;

    let pontos = PONTUACAO_BASE - ((tentativasUsadas - 1) * PENALIDADE_POR_TENTATIVA);

    if (modoJogo === 'diario') {
        pontos *= MULTIPLICADOR_DIARIO;
    }

    return Math.max(10, Math.floor(pontos));
}

async function enviarPontuacaoParaRanking(pontos) {
    console.log("DEBUG [jogo-diario.js]: enviarPontuacaoParaRanking() - Função chamada.");

    const login = localStorage.getItem('eduquizle_user_login');
    console.log(`DEBUG [jogo-diario.js]: enviarPontuacaoParaRanking() - Login encontrado no localStorage: '${login}'`);

    if (!login) {
        console.warn('DEBUG [jogo-diario.js]: enviarPontuacaoParaRanking() - BLOQUEADO. Usuário não logado (login é nulo ou vazio).');
        return;
    }

    const dataToSend = {
        login: login,
        pontos: pontos,
        materia: materia
    };
    console.log("DEBUG [jogo-diario.js]: enviarPontuacaoParaRanking() - Enviando dados para a API:", dataToSend);

    try {
        const response = await fetch('/api/ranking/adicionar-pontos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dataToSend)
        });

        if (!response.ok) {
            const errorData = await response.json();
            console.error("DEBUG [jogo-diario.js]: enviarPontuacaoParaRanking() - ERRO (response not ok):", errorData);
            throw new Error(`Falha ao salvar (API retornou ${response.status})`);
        }

        const resultado = await response.json();
        console.log('DEBUG [jogo-diario.js]: enviarPontuacaoParaRanking() - SUCESSO! Resposta da API:', resultado);

    } catch (error) {
        console.error('DEBUG [jogo-diario.js]: enviarPontuacaoParaRanking() - ERRO FATAL (fetch/catch):', error);
    }
}