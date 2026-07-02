const API_BASE = 'http://localhost:8080/system_web/api';
const state = {
    user: localStorage.getItem('techmartUser'),
    role: localStorage.getItem('techmartRole')
};

document.addEventListener('DOMContentLoaded', () => {
    if (!state.user || state.role !== 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }

    document.getElementById('user-greeting').textContent = `Welcome, Admin`;
    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'index.html';
    });

    loadSystemMetrics();
    setInterval(loadSystemMetrics, 5000); // Poll every 5 seconds
    
    document.getElementById('export-perf-btn').addEventListener('click', exportPerformanceStats);
});

let currentMetrics = null;

async function loadSystemMetrics() {
    try {
        const response = await fetch(`${API_BASE}/metrics`, { headers: { 'X-Role': state.role } });
        if (response.ok) {
            currentMetrics = await response.json();
            
            // Heap Memory
            document.getElementById('metric-heap-used').textContent = currentMetrics.heapUsedMB;
            document.getElementById('metric-heap-max').textContent = currentMetrics.heapMaxMB;
            const percentage = Math.min(100, Math.round((currentMetrics.heapUsedMB / currentMetrics.heapMaxMB) * 100));
            const bar = document.getElementById('metric-heap-bar');
            bar.style.width = percentage + '%';
            if (percentage > 80) bar.className = "bg-red-500 h-2 rounded-full";
            else if (percentage > 60) bar.className = "bg-amber-500 h-2 rounded-full";
            else bar.className = "bg-indigo-500 h-2 rounded-full";
            
            // Threads
            document.getElementById('metric-threads').textContent = currentMetrics.activeThreads;
            document.getElementById('metric-threads-peak').textContent = currentMetrics.peakThreads;
            
            // Active Sessions
            document.getElementById('metric-sessions').textContent = currentMetrics.activeSessions;
            
            // Uptime
            document.getElementById('metric-uptime').textContent = currentMetrics.uptime;
            
            // Advanced Metrics
            document.getElementById('metric-cpu-cores').textContent = currentMetrics.cpuCores;
            document.getElementById('metric-cpu-load').textContent = currentMetrics.systemCpuLoad;
            
            // Database Latency
            const dbLatencyEl = document.getElementById('metric-db-latency');
            dbLatencyEl.textContent = currentMetrics.dbLatencyMs;
            if (currentMetrics.dbStatus === 'ERROR') {
                dbLatencyEl.className = "text-3xl font-bold text-red-500";
            } else if (currentMetrics.dbLatencyMs > 50) {
                dbLatencyEl.className = "text-3xl font-bold text-amber-500";
            } else {
                dbLatencyEl.className = "text-3xl font-bold text-emerald-400";
            }
            
            // JMS
            document.getElementById('metric-jms').textContent = currentMetrics.jmsProcessed;
            document.getElementById('metric-jms-failed').textContent = currentMetrics.jmsFailed;
        }
    } catch(e) {
        console.error("Failed to fetch system metrics", e);
    }
}

function exportPerformanceStats() {
    if (!currentMetrics) {
        alert("Performance metrics are not yet loaded!");
        return;
    }
    const headers = ["Timestamp", "JVM Heap Used (MB)", "JVM Heap Max (MB)", "Active Threads", "Peak Threads", "Active Cart Sessions", "System Uptime", "CPU Cores", "CPU Load Avg", "DB Latency (ms)", "JMS Processed", "JMS Failed"];
    const row = [
        new Date().toISOString(),
        currentMetrics.heapUsedMB,
        currentMetrics.heapMaxMB,
        currentMetrics.activeThreads,
        currentMetrics.peakThreads,
        currentMetrics.activeSessions,
        currentMetrics.uptime,
        currentMetrics.cpuCores,
        currentMetrics.systemCpuLoad,
        currentMetrics.dbLatencyMs,
        currentMetrics.jmsProcessed,
        currentMetrics.jmsFailed
    ];
    
    let csvContent = "data:text/csv;charset=utf-8," + headers.join(",") + "\n" + row.join(",");
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", "performance_snapshot.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
