let taskId = 0;
const taskHistory = {};
const efficiencyScores = [];
const chartDates = [];
let chart;

function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
  ev.preventDefault();
  const data = ev.dataTransfer.getData("text");
  const task = document.getElementById(data);
  ev.target.appendChild(task);

  const newColumn = ev.target.closest(".column").id;
  if (!taskHistory[data]) taskHistory[data] = [];
  taskHistory[data].push({ column: newColumn, time: Date.now() });

  updateDateFilterOptions();
}

function addTask() {
  const input = document.getElementById("taskInput");
  const value = input.value.trim();
  if (!value) return;

  const task = document.createElement("div");
  task.className = "task";
  task.id = `task${taskId++}`;
  task.draggable = true;
  task.ondragstart = drag;
  task.innerText = value;

  document.getElementById("todo").appendChild(task);
  taskHistory[task.id] = [{ column: "todo", time: Date.now() }];
  input.value = "";

  updateDateFilterOptions();
}

function analyzeFlow(filterDate = null) {
  let totalTasks = 0;
  let stuckTasks = 0;
  let highHops = 0;

  for (const id in taskHistory) {
    const moves = taskHistory[id];

    if (filterDate && !moves.some(m => formatDate(new Date(m.time)) === filterDate)) {
      continue;
    }

    totalTasks++;
    const hops = new Set(moves.map(m => m.column)).size;
    if (moves[moves.length - 1].column !== "done") stuckTasks++;
    if (hops > 3) highHops++;
  }

  const score = totalTasks === 0 ? 0 : 100 - ((stuckTasks + highHops) / totalTasks) * 50;
  const today = formatDate(new Date());

  document.getElementById("efficiencyReport").innerHTML = `
    Total Tasks: ${totalTasks}<br>
    Stuck Tasks: ${stuckTasks}<br>
    High Hops: ${highHops}<br>
    🔧 Efficiency Score: <b>${score.toFixed(1)}%</b>
  `;

  if (!filterDate) {
    chartDates.push(today);
    efficiencyScores.push(score);
    updateChart();
  }
}

function exportCSV() {
  const rows = ["Task ID,Column,Time (Readable),Timestamp"];
  for (const id in taskHistory) {
    taskHistory[id].forEach(entry => {
      const readable = new Date(entry.time).toLocaleString();
      rows.push(`${id},${entry.column},${readable},${entry.time}`);
    });
  }

  const csvContent = rows.join("\n");
  const blob = new Blob([csvContent], { type: "text/csv" });
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = "task_history.csv";
  a.click();
}

function updateDateFilterOptions() {
  const select = document.getElementById("filterDate");
  const dates = new Set();

  for (const id in taskHistory) {
    taskHistory[id].forEach(entry => {
      dates.add(formatDate(new Date(entry.time)));
    });
  }

  select.innerHTML = '<option value="">🔍 Filter by Date</option>';
  Array.from(dates).sort().forEach(date => {
    const option = document.createElement("option");
    option.value = date;
    option.textContent = date;
    select.appendChild(option);
  });
}

function filterByDate() {
  const selected = document.getElementById("filterDate").value;
  analyzeFlow(selected);
}

function formatDate(date) {
  return date.toISOString().split("T")[0];
}

function updateChart() {
  if (chart) chart.destroy();
  const ctx = document.getElementById("efficiencyChart").getContext("2d");
  chart = new Chart(ctx, {
    type: "line",
    data: {
      labels: chartDates,
      datasets: [{
        label: 'Efficiency % Over Time',
        data: efficiencyScores,
        backgroundColor: 'rgba(0, 123, 255, 0.2)',
        borderColor: '#0078d7',
        borderWidth: 2,
        tension: 0.3,
        fill: true
      }]
    },
    options: {
      scales: {
        y: {
          min: 0,
          max: 100
        }
      }
    }
  });
}
