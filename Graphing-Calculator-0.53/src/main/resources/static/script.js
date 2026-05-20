(function () {
  const DEBUG = false;

  const BACKEND_URL = "https://parityengine.onrender.com";
  const API_BASE = BACKEND_URL;
  const PING_URL = `${BACKEND_URL}/ping`;
  const PING_INTERVAL = 14 * 60 * 1000; // 14 minutes

  const PANELS = [
    "DescriptionWindow",
    "FunctionGrapherWindow",
    "AlgebraEngineWindow",
    "LibraryWindow",
    "MatrixEngineWindow",
    "SettingsWindow",
  ];

  const NAV_MAP = {
    DescriptionSelect: "DescriptionWindow",
    GraphingEngineSelect: "FunctionGrapherWindow",
    AlgebraEngineSelect: "AlgebraEngineWindow",
    LibrarySelect: "LibraryWindow",
    MatrixEngineSelect: "MatrixEngineWindow",
    SettingsSelect: "SettingsWindow",
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

  async function keepAlive() {
    try {
      await fetch(PING_URL, {
        method: "GET",
        cache: "no-store",
      });
      if (DEBUG) console.log("Backend pinged to stay awake.");
    } catch (err) {
      if (DEBUG) console.error("Ping failed:", err);
    }
  }

  function startKeepAlive() {
    keepAlive();
    setInterval(() => {
      if (document.visibilityState !== "hidden") {
        keepAlive();
      }
    }, PING_INTERVAL);

    document.addEventListener("visibilitychange", () => {
      if (document.visibilityState === "visible") {
        keepAlive();
      }
    });
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
        const response = await fetch(`${API_BASE}/api/math/consoleFast`, {
          method: "POST",
          headers: { "Content-Type": "text/plain" },
          body: input,
        });

        if (!response.ok) throw new Error(`Server Error ${response.status}`);

        const result = await response.text();
        appendConsoleLine(consoleOutput, `  ${result}`, "#00e5ff");
      } catch (err) {
        appendConsoleLine(
          consoleOutput,
          "Error: Engine Offline or Dispatcher Failed",
          "#ff3333"
        );
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

  const MIN_PIXEL_LABEL_SPACING = 42;
  const MAX_POINTS = 1_000_000;
  const MAX_POINT_COUNT_WARNING = 250_000;

  function canvasClientWidth() {
    return canvas ? canvas.clientWidth : 0;
  }

  function canvasClientHeight() {
    return canvas ? canvas.clientHeight : 0;
  }

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
    return (x - offsetX) * scaleX + canvasClientWidth() / 2;
  }

  function toPixelY(y) {
    return canvasClientHeight() / 2 - (y - offsetY) * scaleY;
  }

  function fromPixelX(px) {
    return (px - canvasClientWidth() / 2) / scaleX + offsetX;
  }

  function fromPixelY(py) {
    return (canvasClientHeight() / 2 - py) / scaleY + offsetY;
  }

  function getViewportBounds() {
    return {
      minX: fromPixelX(0),
      maxX: fromPixelX(canvasClientWidth()),
      minY: fromPixelY(canvasClientHeight()),
      maxY: fromPixelY(0),
    };
  }

  function niceTickStep(range) {
    const safeRange = Math.max(Math.abs(range), Number.EPSILON);
    const target = safeRange / 8;
    const pow = Math.pow(10, Math.floor(Math.log10(target)));
    const frac = target / pow;

    let niceFrac;
    if (frac <= 1) niceFrac = 1;
    else if (frac <= 2) niceFrac = 2;
    else if (frac <= 5) niceFrac = 5;
    else niceFrac = 10;

    return niceFrac * pow;
  }

  function formatAxisValue(value, step) {
    if (!Number.isFinite(value)) return "";

    const absStep = Math.abs(step) || 1;
    const decimals = Math.min(
      15,
      Math.max(0, Math.ceil(-Math.log10(absStep)) + 1)
    );

    const rounded =
      Math.round((value + Number.EPSILON) * 10 ** decimals) / 10 ** decimals;

    return rounded
      .toFixed(decimals)
      .replace(/\.?0+$/, "")
      .replace(/^-0$/, "0");
  }

  function drawGridAndAxes() {
    if (!canvas || !ctx) return;

    setCanvasSize();

    const w = canvasClientWidth();
    const h = canvasClientHeight();
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
    ctx.lineWidth = 1;

    const zeroX = toPixelX(0);
    const zeroY = toPixelY(0);

    // X grid + labels
    ctx.textAlign = "center";
    let lastLabelPx = -Infinity;

    for (let x = xStart; x <= b.maxX + xStep; x += xStep) {
      const px = toPixelX(x);

      if (px >= -1 && px <= w + 1) {
        ctx.beginPath();
        ctx.moveTo(px, 0);
        ctx.lineTo(px, h);
        ctx.stroke();

        if (Math.abs(px - lastLabelPx) > MIN_PIXEL_LABEL_SPACING) {
          ctx.fillText(formatAxisValue(x, xStep), px, h - 12);
          lastLabelPx = px;
        }
      }
    }

    // Y grid + labels
    ctx.textAlign = "left";
    lastLabelPx = -Infinity;

    for (let y = yStart; y <= b.maxY + yStep; y += yStep) {
      const py = toPixelY(y);

      if (py >= -1 && py <= h + 1) {
        ctx.beginPath();
        ctx.moveTo(0, py);
        ctx.lineTo(w, py);
        ctx.stroke();

        if (Math.abs(py - lastLabelPx) > MIN_PIXEL_LABEL_SPACING) {
          ctx.fillText(formatAxisValue(y, yStep), 40, py);
          lastLabelPx = py;
        }
      }
    }

    // Axes
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

    const w = canvasClientWidth();
    const h = canvasClientHeight();

    ctx.save();
    ctx.strokeStyle = "#00ffcc";
    ctx.lineWidth = 1.5;
    ctx.lineJoin = "round";
    ctx.lineCap = "round";

    let started = false;
    let prevPx = NaN;
    let prevPy = NaN;

    ctx.beginPath();

    for (const p of pointsArray) {
      if (!p || p.length < 2) continue;

      const x = Number(p[0]);
      const y = Number(p[1]);

      if (!Number.isFinite(x) || !Number.isFinite(y)) {
        if (started) {
          ctx.stroke();
          ctx.beginPath();
          started = false;
        }
        prevPx = NaN;
        prevPy = NaN;
        continue;
      }

      if (Math.abs(y) > 1e12) {
        if (started) {
          ctx.stroke();
          ctx.beginPath();
          started = false;
        }
        prevPx = NaN;
        prevPy = NaN;
        continue;
      }

      const px = toPixelX(x);
      const py = toPixelY(y);

      const offscreenTooFar =
        px < -w * 2 || px > w * 3 || py < -h * 2 || py > h * 3;

      if (offscreenTooFar) {
        if (started) {
          ctx.stroke();
          ctx.beginPath();
          started = false;
        }
        prevPx = NaN;
        prevPy = NaN;
        continue;
      }

      const hugeJump =
        Number.isFinite(prevPx) &&
        Number.isFinite(prevPy) &&
        Math.abs(px - prevPx) > 1000 &&
        Math.abs(py - prevPy) > 1000;

      const bigVerticalJump =
        Number.isFinite(prevPy) && Math.abs(py - prevPy) > Math.max(400, h * 2);

      if (hugeJump || bigVerticalJump) {
        if (started) {
          ctx.stroke();
          ctx.beginPath();
          started = false;
        }
      }

      if (!started) {
        ctx.moveTo(px, py);
        started = true;
      } else {
        ctx.lineTo(px, py);
      }

      prevPx = px;
      prevPy = py;
    }

    ctx.stroke();
    ctx.restore();
  }

  async function fetchTable(expression, minX, maxX, step, signal) {
    if (
      !expression ||
      !isFinite(minX) ||
      !isFinite(maxX) ||
      !isFinite(step) ||
      step === 0
    ) {
      if (DEBUG) console.warn("fetchTable invalid args", { expression, minX, maxX, step });
      return [];
    }

    const approxPoints = Math.floor((maxX - minX) / Math.abs(step)) + 1;

    if (approxPoints > MAX_POINTS) {
      const ok = confirm(
        `Request would produce about ${approxPoints.toLocaleString()} points. Continue?`
      );
      if (!ok) return [];
    }

    const url =
      `${API_BASE}/api/table?expression=${encodeURIComponent(expression)}` +
      `&minX=${encodeURIComponent(minX)}` +
      `&maxX=${encodeURIComponent(maxX)}` +
      `&step=${encodeURIComponent(step)}`;

    try {
      const res = await fetch(url, { signal, cache: "no-store" });
      if (!res.ok) {
        if (DEBUG) console.error("fetchTable HTTP error", res.status);
        return [];
      }

      const data = await res.json();
      if (!Array.isArray(data)) {
        if (DEBUG) console.warn("fetchTable returned non-array", data);
        return [];
      }

      const cleaned = [];
      for (const d of data) {
        if (Array.isArray(d)) {
          cleaned.push([parsePointValue(d[0]), parsePointValue(d[1])]);
        } else if (d && typeof d === "object") {
          const x = "x" in d ? d.x : d[0];
          const y = "y" in d ? d.y : d[1];
          cleaned.push([parsePointValue(x), parsePointValue(y)]);
        }
      }

      return cleaned;
    } catch (err) {
      if (DEBUG) console.error("fetchTable error", err);
      return [];
    }
  }

  function resampleToStepGrid(rawPoints, minX, maxX, stepAbs) {
    if (!Array.isArray(rawPoints) || rawPoints.length === 0) return [];

    const filtered = rawPoints
      .filter(
        (p) =>
          Array.isArray(p) &&
          Number.isFinite(p[0]) &&
          Number.isFinite(p[1])
      )
      .slice()
      .sort((a, b) => a[0] - b[0]);

    if (filtered.length === 0) return [];

    const resampled = [];
    const n = filtered.length;

    let j = 0;
    const count = Math.floor((maxX - minX) / stepAbs);

    for (let i = 0; i <= count; i++) {
      const x = minX + i * stepAbs;

      while (
        j + 1 < n &&
        filtered[j + 1][0] < x &&
        Math.abs(filtered[j + 1][0] - x) <= Math.abs(filtered[j][0] - x)
      ) {
        j++;
      }

      while (j + 1 < n && filtered[j + 1][0] < x) {
        j++;
      }

      if (j + 1 >= n) {
        const last = filtered[n - 1];
        if (last) resampled.push([x, last[1]]);
        continue;
      }

      const p0 = filtered[j];
      const p1 = filtered[j + 1];

      if (!p0 || !p1) continue;

      const x0 = p0[0];
      const y0 = p0[1];
      const x1 = p1[0];
      const y1 = p1[1];

      if (!Number.isFinite(x0) || !Number.isFinite(y0) || !Number.isFinite(x1) || !Number.isFinite(y1)) {
        continue;
      }

      if (x1 === x0) {
        resampled.push([x, y0]);
        continue;
      }

      const t = (x - x0) / (x1 - x0);
      const y = y0 + t * (y1 - y0);

      if (Number.isFinite(y)) {
        resampled.push([x, y]);
      } else {
        resampled.push([x, NaN]);
      }
    }

    return resampled;
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

    const stepAbs = Math.abs(step);

    if (rawPoints.length > MAX_POINT_COUNT_WARNING && DEBUG) {
      console.warn("Large point set:", rawPoints.length);
    }

    // Keep x values locked to the requested step grid, but interpolate y from the backend table.
    points = resampleToStepGrid(rawPoints, minX, maxX, stepAbs);

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

    offsetX = worldX - (mx - canvasClientWidth() / 2) / scaleX;
    offsetY = worldY - (canvasClientHeight() / 2 - my) / scaleY;

    drawFrame();
  }

  function onMouseDown(e) {
    dragging = true;
    dragStart = { x: e.clientX, y: e.clientY };
  }

  function onMouseUp() {
    dragging = false;
    dragStart = null;
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
        document
          .querySelectorAll(".nav-item")
          .forEach((b) => b.classList.remove("active"));

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
      fetch(`${API_BASE}`)
        .then((r) => r.text())
        .then((t) => console.log("Backend reachable:", t.slice(0, 80)))
        .catch(() => {});
    }
  }

  function start() {
    startKeepAlive();

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
