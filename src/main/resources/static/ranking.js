document.addEventListener('DOMContentLoaded', () => {
    console.log("DEBUG [ranking.js]: Página carregada. Definindo elementos...");

    // 1. DEFINIR OS ELEMENTOS
    const tableBody = document.getElementById('ranking-table-body');
    const messageEl = document.getElementById('ranking-message');
    const filterButtons = document.querySelectorAll('.filter-button');

    async function fetchRanking(materia) {
        console.log(`DEBUG [ranking.js]: fetchRanking() - Buscando ranking para: ${materia}`);

        if (!tableBody || !messageEl) {
            console.error("DEBUG [ranking.js]: ERRO FATAL! Elementos 'tableBody' ou 'messageEl' não encontrados.");
            return;
        }

        // Limpa a tabela e mostra mensagem de carregando
        tableBody.innerHTML = '';
        messageEl.textContent = 'Carregando ranking...';
        messageEl.style.display = 'block';

        let endpoint = '';

        // Monta o endpoint da API
        if (materia === 'GERAL') {
            endpoint = '/api/ranking/top100';
        } else {
            // O 'materia' já vem maiúsculo do HTML (ex: "GEOGRAFIA")
            endpoint = `/api/ranking/top100?materia=${materia}`;
        }

        console.log(`DEBUG [ranking.js]: fetchRanking() - Chamando endpoint: ${endpoint}`);

        try {
            const response = await fetch(endpoint);
            if (!response.ok) {
                // Se a API retornar 404, 500, etc., vai cair aqui
                throw new Error(`Erro ao buscar dados: ${response.status} ${response.statusText}`);
            }

            const rankingData = await response.json();
            console.log("DEBUG [ranking.js]: fetchRanking() - Dados recebidos da API:", rankingData);

            renderTable(rankingData, materia);

        } catch (error) {
            console.error('DEBUG [ranking.js]: fetchRanking() - ERRO NA BUSCA:', error);
            messageEl.textContent = `Erro ao carregar ranking. ${error.message}`;
            messageEl.style.color = 'red';
        }
    }

    function renderTable(rankingData, materia) {
        console.log(`DEBUG [ranking.js]: renderTable() - Renderizando tabela para: ${materia}`);

        if (!rankingData || rankingData.length === 0) {
            console.warn("DEBUG [ranking.js]: renderTable() - Nenhum dado encontrado (rankingData está vazio).");
            messageEl.textContent = 'Nenhum dado encontrado para este ranking.';
            return;
        }

        // Esconde a mensagem de "Carregando"
        messageEl.style.display = 'none';

        // Cria as linhas da tabela
        rankingData.forEach((item, index) => {
            const newRow = document.createElement('tr');

            const posCell = document.createElement('td');
            posCell.textContent = index + 1; // Posição
            newRow.appendChild(posCell);

            const userCell = document.createElement('td');
            userCell.textContent = item.usuario?.login || 'Usuário Inválido'; // Nome do usuário
            newRow.appendChild(userCell);

            const scoreCell = document.createElement('td');
            let scoreToShow = 0; // Pontuação

            // Este switch está correto, pois 'materia' já está maiúsculo
            switch (materia) {
                case 'GEOGRAFIA':
                    scoreToShow = item.pontuacaoGeografia;
                    break;
                case 'HISTORIA':
                    scoreToShow = item.pontuacaoHistoria;
                    break;
                case 'BIOLOGIA':
                    scoreToShow = item.pontuacaoBiologia;
                    break;
                case 'QUIMICA':
                    scoreToShow = item.pontuacaoQuimica;
                    break;
                case 'GERAL':
                default:
                    scoreToShow = item.pontuacaoTotal;
            }
            scoreCell.textContent = scoreToShow;
            newRow.appendChild(scoreCell);

            tableBody.appendChild(newRow);
        });
    }

    // Adiciona os listeners nos botões
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Atualiza a classe 'active'
            filterButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');

            // Pega o 'data-materia' (que já é maiúsculo, ex: "GEOGRAFIA")
            const materia = button.dataset.materia;

            // Chama o fetch
            fetchRanking(materia);
        });
    });

    // Carga inicial (chama "GERAL" ao carregar a página)
    fetchRanking('GERAL');
});