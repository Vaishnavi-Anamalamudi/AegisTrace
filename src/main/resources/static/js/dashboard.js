document.addEventListener('DOMContentLoaded', function () {
    if (window.lucide) {
        window.lucide.createIcons();
    }

    startCodeRain();

    const scanForm = document.getElementById('scanForm');
    const scanTarget = document.getElementById('scanTarget');
    const scanMessage = document.getElementById('scanMessage');
    const scanResult = document.getElementById('scanResult');
    const scanTable = document.getElementById('scanTable');

    if (scanForm && scanTarget && scanMessage && scanResult && scanTable) {
        scanForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const target = scanTarget.value.trim();
            if (!target) {
                scanMessage.textContent = 'Enter an IP address or domain before scanning.';
                return;
            }

            const submitButton = scanForm.querySelector('button');
            submitButton.disabled = true;
            submitButton.classList.add('button-loading');
            scanMessage.textContent = `Scanning ${target}...`;
            scanResult.hidden = true;
            scanResult.classList.remove('error');

            try {
                const response = await fetch('/api/scans/run', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'same-origin',
                    body: JSON.stringify({ target })
                });

                if (!response.ok) {
                    const problem = await safeJson(response);
                    throw new Error(problem.message || (response.status === 403 ? 'Your role cannot run scans.' : 'Scan request failed.'));
                }

                const scan = await response.json();
                const openPorts = scan.openPorts && scan.openPorts.length ? scan.openPorts.join(', ') : 'No common open ports detected';
                const services = scan.services && scan.services.length ? scan.services.join(', ') : 'None detected';

                scanResult.innerHTML = `
                    <strong>${escapeHtml(scan.target)}</strong>
                    <span>Status: ${escapeHtml(scan.status)} | Risk: ${Number(scan.riskScore || 0).toFixed(1)}</span>
                    <small>Open ports: ${escapeHtml(openPorts)}</small>
                    <small>Services: ${escapeHtml(services)}</small>
                `;
                scanResult.hidden = false;
                scanMessage.textContent = 'Scan completed and saved.';
                prependScanRow(scanTable, scan);
                showToast(`Scan completed for ${scan.target || target}.`);
                scanTarget.value = '';
            } catch (error) {
                scanMessage.textContent = error.message || 'Scan failed.';
                scanResult.innerHTML = `
                    <strong>Scan could not be completed</strong>
                    <span>${escapeHtml(error.message || 'Review authorization and target format, then try again.')}</span>
                `;
                scanResult.classList.add('error');
                scanResult.hidden = false;
                showToast(error.message || 'Scan failed.', 'error');
            } finally {
                submitButton.disabled = false;
                submitButton.classList.remove('button-loading');
                if (window.lucide) {
                    window.lucide.createIcons();
                }
            }
        });
    }

    connectEventStream();

    const threatScoreEl = document.getElementById('threatScore');
    const riskValue = threatScoreEl ? Number(threatScoreEl.dataset.value || 0) : 0;
    const rows = Array.from(document.querySelectorAll('.event-table tbody tr[data-type]'));

    const severityCounts = rows.reduce((acc, row) => {
        const severity = row.dataset.severity || 'UNKNOWN';
        acc[severity] = (acc[severity] || 0) + 1;
        return acc;
    }, {});

    const typeCounts = rows.reduce((acc, row) => {
        const type = row.dataset.type || 'OTHER';
        acc[type] = (acc[type] || 0) + 1;
        return acc;
    }, {});

    if (!window.Chart) {
        return;
    }

    Chart.defaults.color = '#86b990';
    Chart.defaults.borderColor = 'rgba(115, 255, 151, 0.16)';
    Chart.defaults.font.family = 'Consolas, Cascadia Mono, monospace';

    const riskCanvas = document.getElementById('riskChart');
    if (riskCanvas) {
        const riskData = [
            severityCounts.CRITICAL || 0,
            severityCounts.HIGH || 0,
            (severityCounts.MEDIUM || 0) + (severityCounts.LOW || 0)
        ];

        new Chart(riskCanvas.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: ['Critical', 'High', 'Medium / Low'],
                datasets: [{
                    data: riskData.some(Boolean) ? riskData : [1, 0, 0],
                    backgroundColor: ['#ff4f64', '#c9ff5f', '#00ff88'],
                    borderWidth: 0
                }]
            },
            options: {
                cutout: '68%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { boxWidth: 10, boxHeight: 10 }
                    },
                    tooltip: {
                        callbacks: {
                            afterTitle: () => `Average risk score: ${riskValue}`
                        }
                    }
                }
            }
        });
    }

    const trafficCanvas = document.getElementById('trafficChart');
    if (trafficCanvas) {
        const labels = Object.keys(typeCounts).slice(0, 8);
        const chartLabels = labels.length ? labels : ['No telemetry'];
        const values = labels.length ? labels.map(label => typeCounts[label]) : [0];

        new Chart(trafficCanvas.getContext('2d'), {
            type: 'bar',
            data: {
                labels: chartLabels,
                datasets: [{
                    label: 'Events',
                    data: values,
                    backgroundColor: chartLabels.map((_, index) => index % 3 === 0 ? '#00ff88' : (index % 3 === 1 ? '#c9ff5f' : '#7cffc4')),
                    borderRadius: 6,
                    maxBarThickness: 44
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { precision: 0 },
                        grid: { color: 'rgba(115, 255, 151, 0.12)' }
                    },
                    x: {
                        grid: { display: false },
                        ticks: {
                            maxRotation: 0,
                            callback: function (value) {
                                const label = this.getLabelForValue(value);
                                return label.length > 13 ? label.slice(0, 13) + '..' : label;
                            }
                        }
                    }
                },
                plugins: {
                    legend: { display: false }
                }
            }
        });
    }
});

async function safeJson(response) {
    try {
        return await response.json();
    } catch (error) {
        return {};
    }
}

function connectEventStream() {
    if (!window.SockJS || !window.Stomp) {
        return;
    }

    const eventTableBody = document.querySelector('.event-table tbody');
    if (!eventTableBody) {
        return;
    }

    const socket = new SockJS('/ws/events');
    const client = Stomp.over(socket);
    client.debug = null;
    client.connect({}, function () {
        client.subscribe('/topic/security-events', function (message) {
            const event = JSON.parse(message.body);
            prependEventRow(eventTableBody, event);
            showToast(`${event.severity || 'New'} event: ${event.eventType || 'security event'}`);
        });
    }, function () {
        showToast('Live event stream is unavailable. The dashboard will keep showing stored data.', 'error');
    });
}

function prependEventRow(eventTableBody, event) {
    const row = document.createElement('tr');
    row.dataset.severity = event.severity || 'UNKNOWN';
    row.dataset.type = event.eventType || 'OTHER';
    const severityClass = event.severity === 'CRITICAL' ? ' critical' : (event.severity === 'HIGH' ? ' high' : ' medium');
    row.innerHTML = `
        <td>${escapeHtml(event.timestamp || '')}</td>
        <td>
            <strong>${escapeHtml(event.eventType || 'EVENT')}</strong>
            <span>${escapeHtml(event.description || '')}</span>
        </td>
        <td>${escapeHtml(event.sourceIP || 'unknown')}</td>
        <td>${escapeHtml(event.destinationIP || 'unknown')}</td>
        <td>${escapeHtml(event.mitreTactic || 'Unmapped')}</td>
        <td><span class="severity-label${severityClass}">${escapeHtml(event.severity || 'MEDIUM')}</span></td>
        <td>${Number(event.riskScore || 0).toFixed(1)}</td>
    `;
    eventTableBody.prepend(row);
    while (eventTableBody.children.length > 12) {
        eventTableBody.lastElementChild.remove();
    }
}

function startCodeRain() {
    const canvas = document.getElementById('codeRain');
    if (!canvas) {
        return;
    }

    const context = canvas.getContext('2d');
    const glyphs = '010011101101001011010011AEGISTRACETHREATFORENSICS';
    const fontSize = 16;
    let columns = 0;
    let drops = [];

    function resize() {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        columns = Math.ceil(canvas.width / fontSize);
        drops = Array.from({ length: columns }, () => Math.floor(Math.random() * -80));
    }

    function draw() {
        context.fillStyle = 'rgba(2, 5, 3, 0.12)';
        context.fillRect(0, 0, canvas.width, canvas.height);
        context.font = `${fontSize}px Consolas, monospace`;

        for (let index = 0; index < drops.length; index += 1) {
            const glyph = glyphs[Math.floor(Math.random() * glyphs.length)];
            const x = index * fontSize;
            const y = drops[index] * fontSize;

            context.fillStyle = Math.random() > 0.975 ? '#e7ffe9' : '#30ff72';
            context.fillText(glyph, x, y);

            if (y > canvas.height && Math.random() > 0.975) {
                drops[index] = 0;
            }

            drops[index] += 1;
        }
    }

    resize();
    window.addEventListener('resize', resize);
    window.setInterval(draw, 58);
}

function prependScanRow(scanTable, scan) {
    const emptyState = scanTable.querySelector('.empty-state');
    if (emptyState) {
        emptyState.remove();
    }

    const row = document.createElement('div');
    row.className = 'scan-row';
    row.innerHTML = `
        <div>
            <strong>${escapeHtml(scan.target || 'unknown')}</strong>
            <span>${escapeHtml(scan.domain || 'n/a')}</span>
        </div>
        <small>${escapeHtml(scan.osFingerprint || scan.status || 'scan result')}</small>
        <b>${Number(scan.riskScore || 0).toFixed(1)}</b>
    `;
    scanTable.prepend(row);
}

function showToast(message, tone) {
    let stack = document.querySelector('.toast-stack');
    if (!stack) {
        stack = document.createElement('div');
        stack.className = 'toast-stack';
        stack.setAttribute('aria-live', 'polite');
        document.body.appendChild(stack);
    }

    const toast = document.createElement('div');
    toast.className = `trace-toast${tone === 'error' ? ' error' : ''}`;
    toast.textContent = message;
    stack.appendChild(toast);

    window.setTimeout(() => {
        toast.remove();
        if (!stack.children.length) {
            stack.remove();
        }
    }, 4200);
}

function escapeHtml(value) {
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
