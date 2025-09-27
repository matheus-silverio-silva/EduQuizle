let materia = '';
let respostaCorretaId = null;
let tentativas = 0;
const MAX_TENTATIVAS = 10;
let gameOver = false;
let cabecalhos = [];

const tituloMateriaEl = document.getElementById('titulo-materia');
const palpiteInputEl = document.getElementById('palpite-input');
const sugestoesEl = document.getElementById('sugestoes');
const gridCabecalhoEl = document.getElementById('grid-cabecalho');
const gridResultadosEl = document.getElementById('grid-resultados');
const tentativasEl = document.getElementById('tentativas');
const mensagemFinalEl = document.getElementById('mensagem-final');


document.addEventListener('DOMContentLoaded', async () => {
    const params = new URLSearchParams(window.location.search);
    materia = params.get('materia');

    if (!materia) {
        tituloMateriaEl.textContent = 'Matéria não encontrada!';
        return;
    }

    tituloMateriaEl.textContent = `Desafio de ${materia}`;

    await carregarDadosIniciais();
});

async function carregarDadosIniciais() {
    try {
        const desafioResponse = await fetch(`/api/desafios/livre?materia=${materia}`);
        const desafio = await desafioResponse.json();
        respostaCorretaId = desafio.id; // API deve retornar o ID da resposta e os cabeçalhos
        cabecalhos = desafio.cabecalhos;

        const palpitesResponse = await fetch(`/api/respostas/nomes?materia=${materia}`);
        const listaNomes = await palpitesResponse.json();

        const cabecalhoRow = document.createElement('tr');
        cabecalhos.forEach(texto => {
            const th = document.createElement('th');
            th.textContent = texto;
            cabecalhoRow.appendChild(th);
        });
        gridCabecalhoEl.appendChild(cabecalhoRow);

        listaNomes.forEach(nome => {
            const option = document.createElement('option');
            option.value = nome;
            sugestoesEl.appendChild(option);
        });

    } catch (error) {
        tituloMateriaEl.textContent = 'Erro ao carregar o desafio.';
        console.error('Erro:', error);
    }
}

async function handleGuess(event) {
    event.preventDefault();
    if (gameOver) return;

    const palpite = palpiteInputEl.value.trim();
    if (!palpite) return;

    tentativas++;
    tentativasEl.textContent = tentativas;

    try {
        const response = await fetch('/api/desafios/comparar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                palpite: palpite,
                respostaId: respostaCorretaId,
            }),
        });

        const resultado = await response.json();
        adicionarResultadoNaGrid(resultado.pistas);

        if (resultado.acertou) {
            finalizarJogo(true, resultado.respostaCorreta);
        } else if (tentativas >= MAX_TENTATIVAS) {
            finalizarJogo(false, resultado.respostaCorreta);
        }

        palpiteInputEl.value = '';

    } catch (error) {
        alert('Erro ao processar o palpite.');
        console.error('Erro:', error);
    }
}

function adicionarResultadoNaGrid(pistas) {
    const newRow = document.createElement('tr');
    // Adiciona as células na ordem correta dos cabeçalhos
    cabecalhos.forEach(cabecalho => {
        const td = document.createElement('td');
        const pistaInfo = pistas[cabecalho];
        td.textContent = pistaInfo.texto;
        td.className = pistaInfo.status;
        newRow.appendChild(td);
    });
    gridResultadosEl.appendChild(newRow);
}

function finalizarJogo(venceu, respostaCorreta) {
    gameOver = true;
    palpiteInputEl.disabled = true;

    if (venceu) {
        mensagemFinalEl.textContent = 'Parabéns, você acertou!';
        mensagemFinalEl.style.color = '#28a745';
    } else {
        mensagemFinalEl.textContent = `Fim de jogo! A resposta era: ${respostaCorreta}`;
        mensagemFinalEl.style.color = '#dc3545';
    }
}