(function () {
  const DEBUG = false;

  const PANELS = [
    "DescriptionWindow",
    "FunctionGrapherWindow",
    "AlgebraEngineWindow",
    "LibraryWindow",
    "MatrixEngineWindow",
    "SettingsWindow"
  ];

  const NAV_MAP = {
    "DescriptionSelect": "DescriptionWindow",
    "GraphingEngineSelect": "FunctionGrapherWindow",
    "AlgebraEngineSelect": "AlgebraEngineWindow",
    "LibrarySelect": "LibraryWindow",
    "MatrixEngineSelect": "MatrixEngineWindow",
    "SettingsSelect": "SettingsWindow"
  };

  function byId(id) {
    return document.getElementById(id);
  }

  function safeAddClick(id, handler) {
    const el = byId(id);
    if (!el) return;
    el.addEventListener("click", handler);
  }

  function isFiniteNumber(n) {
    return typeof n === "number" && Number.isFinite(n);
  }

  function parsePointValue(v) {
    if (v === null || v === undefined || v === "") return NaN;
    const n = typeof v === "number" ? v : Number(v);
    return Number.isFinite(n) ? n : NaN;
  }

  function appendConsoleLine(outputEl, text, color) {
    if (!outputEl) return;
    const row = document.createElement("div");
    row.className = "log-entry";

    const span = document.createElement("span");
    if (color) span.style.color = color;
    span.textContent = text;

    row.appendChild(span);
    outputEl.appendChild(row);
    outputEl.scrollTop = outputEl.scrollHeight;
  }

  // --- Console Logic --- //
  const consoleInput = byId("consoleInput");
  const consoleOutput = byId("consoleOutput");

  if (consoleInput && consoleOutput) {
    consoleInput.addEventListener("keydown", async (e) => {
      if (e.key !== "Enter") return;

      e.preventDefault();
      const input = e.target.value;
      if (!input || !input.trim()) return;

      appendConsoleLine(consoleOutput, `>> ${input}`, "#777");
      e.target.value = "";

      try {
        const response = await fetch("/api/math/consoleFast", {
          method: "POST",
          headers: { "Content-Type": "text/plain" },
          body: input
        });

        if (!response.ok) throw new Error(`Server Error ${response.status}`);

        const result = await response.text();
        appendConsoleLine(consoleOutput, `  ${result}`, "#00e5ff");
      } catch (err) {
        appendConsoleLine(consoleOutput, "Error: Engine Offline or Dispatcher Failed", "#ff3333");
      }
    });
  }

  // --- Graphing state --- //
  const canvas = byId("graphCanvas");
  const exprEl = byId("expression");
  const minEl = byId("minX");
  const maxEl = byId("maxX");
  const stepEl = byId("step");
  const graphBtn = byId("graphBtn");
  const ctx = canvas ? canvas.getContext("2d") : null;

  let offsetX = 0;
  let offsetY = 0;
  let scaleX = 250;
  let scaleY = 250;

  let dragging = false;
  let dragStart = null;
  let currentExpression = "";
  let points = [];
  let abortController = null;
  let lastRequestedStep = NaN;

  const zoomFactor = 1.2;
  const MAX_POINTS = 1000000;
  const MIN_PIXEL_LABEL_SPACING = 42;

  function clearBackingStore() {
    if (!canvas || !ctx) return;
    const dpr = window.devicePixelRatio || 1;
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  }

  function setCanvasSize() {
    if (!canvas || !ctx) return;

    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    const cssW = Math.max(100, Math.round(rect.width));
    const cssH = Math.max(120, Math.round(rect.height));
    const bitmapW = Math.round(cssW * dpr);
    const bitmapH = Math.round(cssH * dpr);

    if (canvas.width !== bitmapW || canvas.height !== bitmapH) {
      ctx.setTransform(1, 0, 0, 1, 0, 0);
      canvas.width = bitmapW;
      canvas.height = bitmapH;
    }

    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  }

 function toPixelX(x) {
  return (x - offsetX) * scaleX + canvas.clientWidth / 2;
}

function toPixelY(y) {
  return canvas.clientHeight / 2 - (y - offsetY) * scaleY;
}

function fromPixelX(px) {
  return (px - canvas.clientWidth / 2) / scaleX + offsetX;
}

function fromPixelY(py) {
  return (canvas.clientHeight / 2 - py) / scaleY + offsetY;
}

 function getViewportBounds() {
  return {
    minX: fromPixelX(0),
    maxX: fromPixelX(canvas.clientWidth),
    minY: fromPixelY(canvas.clientHeight),
    maxY: fromPixelY(0)
  };
}

  function niceTickStep(range) {
    const target = Math.max(range / 8, Number.EPSILON);
    const pow = Math.pow(10, Math.floor(Math.log10(target)));
    const frac = target / pow;

    let niceFrac;
    if (frac <= 1) niceFrac = 1;
    else if (frac <= 2) niceFrac = 2;
    else if (frac <= 5) niceFrac = 5;
    else niceFrac = 10;

    return niceFrac * pow;
  }

  function formatNumberForStep(val, step) {
    if (!Number.isFinite(val)) return "";
    const absStep = Math.abs(step) || 1;
    const decimals = Math.min(12, Math.max(0, -Math.floor(Math.log10(absStep))));
    return Number(val.toFixed(decimals)).toString();
  }

 function drawGridAndAxes() {
  if (!canvas || !ctx) return;

  setCanvasSize();

  const w = canvas.clientWidth;
  const h = canvas.clientHeight;
  const b = getViewportBounds();

  ctx.clearRect(0, 0, w, h);

  const xStep = niceTickStep(b.maxX - b.minX || 1);
  const yStep = niceTickStep(b.maxY - b.minY || 1);

  const xStart = Math.floor(b.minX / xStep) * xStep;
  const yStart = Math.floor(b.minY / yStep) * yStep;

  ctx.save();

  ctx.strokeStyle = "#2b2b2b";
  ctx.fillStyle = "#a9a9a9";
  ctx.font = "12px sans-serif";

  const zeroX = toPixelX(0);
  const zeroY = toPixelY(0);

  // X grid
  ctx.textAlign = "center";
  let lastPx = -Infinity;

  for (let x = xStart; x <= b.maxX; x += xStep) {
    const px = toPixelX(x);

    ctx.beginPath();
    ctx.moveTo(px, 0);
    ctx.lineTo(px, h);
    ctx.stroke();

    if (Math.abs(px - lastPx) > MIN_PIXEL_LABEL_SPACING) {
      ctx.fillText(x.toFixed(2), px, h - 12);
      lastPx = px;
    }
  }

  // Y grid
  ctx.textAlign = "left";
  lastPx = -Infinity;

  for (let y = yStart; y <= b.maxY; y += yStep) {
    const py = toPixelY(y);

    ctx.beginPath();
    ctx.moveTo(0, py);
    ctx.lineTo(w, py);
    ctx.stroke();

    if (Math.abs(py - lastPx) > MIN_PIXEL_LABEL_SPACING) {
      ctx.fillText(y.toFixed(2),40, py);
      lastPx = py;
    }
  }

  // axes
  ctx.strokeStyle = "#888";
  ctx.lineWidth = 2;

  if (zeroY >= 0 && zeroY <= h) {
    ctx.beginPath();
    ctx.moveTo(0, zeroY);
    ctx.lineTo(w, zeroY);
    ctx.stroke();
  }

  if (zeroX >= 0 && zeroX <= w) {
    ctx.beginPath();
    ctx.moveTo(zeroX, 0);
    ctx.lineTo(zeroX, h);
    ctx.stroke();
  }

  ctx.restore();
}

 function drawFunctionFromPoints(pointsArray) {
  if (!canvas || !ctx || !pointsArray?.length) return;

  ctx.save();
  ctx.strokeStyle = "#00ffcc";
  ctx.lineWidth = 1.5;

  let started = false;
  let prevX = null;

  const GAP_THRESHOLD = lastRequestedStep * 1.5;
  const Y_BREAK = 1e6; // safety cutoff for spikes

  ctx.beginPath();

  for (const p of pointsArray) {
    if (!p || p.length < 2) continue;

    const x = Number(p[0]);
    const y = Number(p[1]);

    // HARD DISCONTINUITY CONDITIONS
    if (
      !Number.isFinite(x) ||
      !Number.isFinite(y) ||
      Math.abs(y) > Y_BREAK
    ) {
      ctx.stroke();
      ctx.beginPath();
      started = false;
      prevX = null;
      continue;
    }

    // GAP DETECTION (true asymptote spacing)
    if (prevX !== null && Math.abs(x - prevX) > GAP_THRESHOLD) {
      ctx.stroke();
      ctx.beginPath();
      started = false;
    }

    const px = toPixelX(x);
    const py = toPixelY(y);

    if (!started) {
      ctx.moveTo(px, py);
      started = true;
    } else {
      ctx.lineTo(px, py);
    }

    prevX = x;
  }

  ctx.stroke();
  ctx.restore();
}

  async function fetchTable(expression, minX, maxX, step, signal) {
    if (!expression || !isFinite(minX) || !isFinite(maxX) || !isFinite(step) || step === 0) {
      if (DEBUG) console.warn("fetchTable invalid args", { expression, minX, maxX, step });
      return [];
    }

    const approxPoints = Math.ceil((maxX - minX) / Math.abs(step)) + 1;

    if (approxPoints > MAX_POINTS) {
      if (!confirm(`Request would produce ${approxPoints.toLocaleString()} points. Continue?`)) {
        return [];
      }
    }

const url = `/api/table?expression=${encodeURIComponent(expression)}` +
`&minX=${minX}&maxX=${maxX}&step=${step}`;

    try {
      const res = await fetch(url, { signal });
      if (!res.ok) {
        if (DEBUG) console.error("fetchTable HTTP error", res.status);
        return [];
      }

      const data = await res.json();
      if (!Array.isArray(data)) {
        if (DEBUG) console.warn("fetchTable returned non-array", data);
        return [];
      }

      return data.map((d) => {
        if (Array.isArray(d)) {
          return [parsePointValue(d[0]), parsePointValue(d[1])];
        }
        if (d && typeof d === "object") {
          const x = "x" in d ? d.x : d[0];
          const y = "y" in d ? d.y : d[1];
          return [parsePointValue(x), parsePointValue(y)];
        }
        return [NaN, NaN];
      });
    } catch (err) {
      if (DEBUG) console.error("fetchTable error", err);
      return [];
    }
  }

 function drawFrame() {
  if (!canvas || !ctx) return;
  drawGridAndAxes();
  drawFunctionFromPoints(points);
}

  async function graph() {
  if (!exprEl) return;

  const expr = exprEl.value?.trim();
  if (expr) currentExpression = expr;
  if (!currentExpression) return;

  const minX = parseFloat(minEl?.value);
  const maxX = parseFloat(maxEl?.value);

  if (!Number.isFinite(minX) || !Number.isFinite(maxX) || maxX <= minX) return;

  let step = parseFloat(stepEl?.value);

  if (!Number.isFinite(step) || step <= 0) {
    step = (maxX - minX) / 1000;
  }

  lastRequestedStep = step;

  abortController?.abort();
  abortController = new AbortController();

  const rawPoints = await fetchTable(
    currentExpression,
    minX,
    maxX,
    step,
    abortController.signal
  );

  if (!rawPoints?.length) {
    points = [];
    drawFrame();
    return;
  }

  // ---- RESAMPLE TO STRICT STEP GRID ----
  const resampled = [];

  const stepAbs = Math.abs(step);
  const start = minX;
  const end = maxX;

  for (let x = start; x <= end; x += stepAbs) {
    // find closest backend point
    let best = null;
    let bestDist = Infinity;

    for (const p of rawPoints) {
      if (!p || !Number.isFinite(p[0]) || !Number.isFinite(p[1])) continue;

      const dx = Math.abs(p[0] - x);
      if (dx < bestDist) {
        bestDist = dx;
        best = p;
      }
    }

    if (best) {
      resampled.push([x, best[1]]);
    }
  }

  points = resampled;
  drawFrame();
}

  function onWheel(e) {
  if (!canvas) return;
  e.preventDefault();

  const rect = canvas.getBoundingClientRect();

  const mx = e.clientX - rect.left;
  const my = e.clientY - rect.top;

  const worldX = fromPixelX(mx);
  const worldY = fromPixelY(my);

  const factor = e.deltaY < 0 ? 1.15 : 1 / 1.15;

  scaleX *= factor;
  scaleY *= factor;

  offsetX = worldX - (mx - canvas.clientWidth / 2) / scaleX;
  offsetY = worldY - (canvas.clientHeight / 2 - my) / scaleY;

  drawFrame();
}

  function onMouseDown(e) {
    dragging = true;
    dragStart = { x: e.clientX, y: e.clientY };
  }

  function onMouseUp() {
    dragging = false;
    drawFrame();
  }

  function onMouseMove(e) {
    if (!dragging || !dragStart) return;

    const dx = e.clientX - dragStart.x;
    const dy = e.clientY - dragStart.y;

    offsetX -= dx / scaleX;
    offsetY += dy / scaleY;

    dragStart = { x: e.clientX, y: e.clientY };
    drawFrame();
  }

  function showSection(id) {
    PANELS.forEach((pid) => {
      const el = byId(pid);
      if (!el) return;

      const show = pid === id;

      if (!show && pid === "FunctionGrapherWindow") {
        try {
          clearBackingStore();
        } catch (e) {
          if (DEBUG) console.warn(e);
        }
      }

      el.hidden = !show;
      if (show) el.classList.remove("hidden");
      else el.classList.add("hidden");
      el.setAttribute("aria-hidden", (!show).toString());
    });

    if (id === "FunctionGrapherWindow") {
      requestAnimationFrame(() => {
        try {
          setCanvasSize();
          drawFrame();
        } catch (e) {
          if (DEBUG) console.warn("graph init error:", e);
        }
      });
    }
  }

  function wireNavigation() {
    Object.keys(NAV_MAP).forEach((buttonId) => {
      const btn = byId(buttonId);
      if (!btn) return;

      btn.addEventListener("click", () => {
        document.querySelectorAll(".nav-item").forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");
        showSection(NAV_MAP[buttonId]);
      });
    });
  }

  function initGraph() {
    if (!canvas || !ctx) return;

    setCanvasSize();

    const parent = canvas.parentElement || document.body;
    if (typeof ResizeObserver !== "undefined") {
      const ro = new ResizeObserver(() => {
        requestAnimationFrame(() => {
          setCanvasSize();
          drawFrame();
        });
      });
      ro.observe(parent);
    }

    canvas.addEventListener("wheel", onWheel, { passive: false });
    canvas.addEventListener("mousedown", onMouseDown);
    window.addEventListener("mouseup", onMouseUp);
    canvas.addEventListener("mousemove", onMouseMove);

    if (graphBtn) {
      graphBtn.addEventListener("click", (e) => {
        e.preventDefault();
        graph();
      });
    }

    window.graph = graph;
    window.showSection = showSection;

    drawFrame();

    window.addEventListener("resize", () => {
      setCanvasSize();
      drawFrame();
    });

    if (DEBUG) {
        fetch("/api/test")
        .then((r) => r.json())
        .then((j) => console.log("/api/test:", j))
        .catch(() => {});
    }
  }

  function start() {
    wireNavigation();

    safeAddClick("DescriptionSelect", () => showSection("DescriptionWindow"));
    safeAddClick("GraphingEngineSelect", () => showSection("FunctionGrapherWindow"));
    safeAddClick("AlgebraEngineSelect", () => showSection("AlgebraEngineWindow"));
    safeAddClick("LibrarySelect", () => showSection("LibraryWindow"));
    safeAddClick("MatrixEngineSelect", () => showSection("MatrixEngineWindow"));
    safeAddClick("SettingsSelect", () => showSection("SettingsWindow"));

    initGraph();
    showSection("DescriptionWindow");
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", start);
  } else {
    start();
  }
})();
