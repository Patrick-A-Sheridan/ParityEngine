(function () {
  const DEBUG = false;
  
const PING_INTERVAL = 14 * 60 * 1000; // 14 minutes in milliseconds
const BACKEND_URL = "https://parityengine.onrender.com"; // Create a simple /ping endpoint in Spring Boot

function keepAlive() {
    fetch(BACKEND_URL)
        .then(() => console.log("Backend pinged to stay awake."))
        .catch(err => console.error("Ping failed:", err));
}

// Start the ping cycle
setInterval(keepAlive, PING_INTERVAL);
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

  const MAX_POINTS = 1000000;
  const MIN_PIXEL_LABEL_SPACING = 42;

  // Adaptive sampling controls
  const Y_BREAK = 1e4;          // hard cut for spikes
  const Y_JUMP = 250;           // nearby y jump that suggests a discontinuity
  const MAX_SLOPE = 2000;       // very steep local segment -> split
  const MAX_DEPTH = 11;         // keeps recursion under control

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
        ctx.fillText(y.toFixed(2), 40, py);
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

  function sanitizePoints(rawPoints) {
    const cleaned = [];
    if (!Array.isArray(rawPoints)) return cleaned;

    for (const p of rawPoints) {
      if (!Array.isArray(p) || p.length < 2) continue;

      const x = parsePointValue(p[0]);
      const y = parsePointValue(p[1]);

      if (!Number.isFinite(x) || !Number.isFinite(y)) continue;
      cleaned.push([x, y]);
    }

    return cleaned;
  }

  function fetchTable(expression, minX, maxX, step, signal) {
    if (!expression || !isFinite(minX) || !isFinite(maxX) || !isFinite(step) || step === 0) {
      if (DEBUG) console.warn("fetchTable invalid args", { expression, minX, maxX, step });
      return Promise.resolve([]);
    }

    const url =
      `http://localhost:8080/api/table?expression=${encodeURIComponent(expression)}` +
      `&minX=${minX}&maxX=${maxX}&step=${step}`;

    return fetch(url, { signal })
      .then((res) => {
        if (!res.ok) return [];
        return res.json();
      })
      .then((data) => sanitizePoints(data))
      .catch((err) => {
        if (DEBUG && err?.name !== "AbortError") console.error("fetchTable error", err);
        return [];
      });
  }

  function segmentLooksBad(a, b) {
    const [x1, y1] = a;
    const [x2, y2] = b;

    if (!Number.isFinite(x1) || !Number.isFinite(y1) || !Number.isFinite(x2) || !Number.isFinite(y2)) {
      return true;
    }

    if (Math.abs(y1) > Y_BREAK || Math.abs(y2) > Y_BREAK) {
      return true;
    }

    const dx = x2 - x1;
    const dy = y2 - y1;

    if (dx === 0) {
      return Math.abs(dy) > 0;
    }

    if (Math.abs(dy) > Y_BREAK) return true;

    const slope = Math.abs(dy / dx);
    if (Number.isFinite(slope) && slope > MAX_SLOPE) return true;

    if (Math.sign(y1) !== Math.sign(y2) && Math.abs(y1) > 10 && Math.abs(y2) > 10) {
      return true;
    }

    if (Math.abs(dy) > Y_JUMP) {
      return true;
    }

    return false;
  }

  async function sampleAdaptiveRange(expression, minX, maxX, signal, depth = 0) {
    if (signal?.aborted) throw new DOMException("Aborted", "AbortError");

    if (!Number.isFinite(minX) || !Number.isFinite(maxX) || maxX <= minX) {
      return [];
    }

    const interval = maxX - minX;
    const minInterval = Math.max(Math.abs(lastRequestedStep) / 8, 1e-4);

    // Base case: stop splitting and just take this interval's points.
    if (depth >= MAX_DEPTH || interval <= minInterval) {
      return await fetchTable(expression, minX, maxX, interval / 2, signal);
    }

    // Sample the interval at start / middle / end.
    const pts = await fetchTable(expression, minX, maxX, interval / 2, signal);

    if (pts.length < 2) {
      return pts;
    }

    let needsSplit = false;
    for (let i = 1; i < pts.length; i++) {
      if (segmentLooksBad(pts[i - 1], pts[i])) {
        needsSplit = true;
        break;
      }
    }

    if (!needsSplit) {
      return pts;
    }

    const mid = (minX + maxX) / 2;

    const left = await sampleAdaptiveRange(expression, minX, mid, signal, depth + 1);
    const right = await sampleAdaptiveRange(expression, mid, maxX, signal, depth + 1);

    if (!left.length) return right;
    if (!right.length) return left;

    const merged = left.slice();

    // avoid duplicating the midpoint if both halves include it
    const lastLeft = left[left.length - 1];
    const firstRight = right[0];

    const sameMidpoint =
      Array.isArray(lastLeft) &&
      Array.isArray(firstRight) &&
      lastLeft.length >= 2 &&
      firstRight.length >= 2 &&
      Math.abs(lastLeft[0] - firstRight[0]) < 1e-12 &&
      Math.abs(lastLeft[1] - firstRight[1]) < 1e-12;

    if (!sameMidpoint) {
      merged.push([NaN, NaN]); // explicit break in the line
    }

    merged.push(...right);
    return merged;
  }

  function drawFunctionFromPoints(pointsArray) {
    if (!canvas || !ctx || !pointsArray?.length) return;

    ctx.save();
    ctx.strokeStyle = "#00ffcc";
    ctx.lineWidth = 1.5;

    let started = false;
    let prev = null;

    ctx.beginPath();

    for (const p of pointsArray) {
      if (!p || p.length < 2) {
        ctx.stroke();
        ctx.beginPath();
        started = false;
        prev = null;
        continue;
      }

      const x = Number(p[0]);
      const y = Number(p[1]);

      if (!Number.isFinite(x) || !Number.isFinite(y)) {
        ctx.stroke();
        ctx.beginPath();
        started = false;
        prev = null;
        continue;
      }

      if (Math.abs(y) > Y_BREAK) {
        ctx.stroke();
        ctx.beginPath();
        started = false;
        prev = null;
        continue;
      }

      if (prev && segmentLooksBad(prev, [x, y])) {
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

      prev = [x, y];
    }

    ctx.stroke();
    ctx.restore();
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

    try {
      const sampled = await sampleAdaptiveRange(
        currentExpression,
        minX,
        maxX,
        abortController.signal,
        0
      );

      points = sampled.length ? sampled : [];
      drawFrame();
    } catch (err) {
      if (err?.name !== "AbortError") {
        points = [];
        drawFrame();
      }
    }
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
      fetch("http://localhost:8080/api/test")
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
