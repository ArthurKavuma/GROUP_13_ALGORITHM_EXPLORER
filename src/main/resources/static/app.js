'use strict';

/**
 * ============================================================================
 *  Algorithm Explorer — Frontend Application Controller (Vanilla JS)
 *  CSC2107 · Group 13
 *  Connects all four modules (A: Recursion, B: Data Structures, C: Sorting,
 *  D: Time Complexity) to the Spring Boot backend.
 * ============================================================================
 */

const CONFIG = {
  API_BASE_URL: '/api',
  AUTOPLAY_ON_RUN: true,
  DEFAULT_SORT_INTERVAL_MS: 600,
  MIN_ELEMENTS: 2,
  MAX_ELEMENTS: 16,
  MIN_VALUE: -999,
  MAX_VALUE: 999,
  RANDOM_MAX: 99,
};

const ALGORITHMS = {
  BUBBLE_SORT: {
    label: 'Bubble Sort',
    complexity: { worst: 'O(n²)', average: 'O(n²)', best: 'O(n)', space: 'O(1)', stable: 'Yes (Stable)' },
  },
  SELECTION_SORT: {
    label: 'Selection Sort',
    complexity: { worst: 'O(n²)', average: 'O(n²)', best: 'O(n²)', space: 'O(1)', stable: 'No (Unstable)' },
  },
  INSERTION_SORT: {
    label: 'Insertion Sort',
    complexity: { worst: 'O(n²)', average: 'O(n²)', best: 'O(n)', space: 'O(1)', stable: 'Yes (Stable)' },
  },
  MERGE_SORT: {
    label: 'Merge Sort',
    complexity: { worst: 'O(n log n)', average: 'O(n log n)', best: 'O(n log n)', space: 'O(n)', stable: 'Yes (Stable)' },
  },
  QUICK_SORT: {
    label: 'Quicksort (First-Element Yellow/Green)',
    complexity: { worst: 'O(n²)', average: 'O(n log n)', best: 'O(n log n)', space: 'O(log n)', stable: 'No (Unstable)' },
  },
  QUICK_SORT_RANDOM: {
    label: 'Quicksort (Randomised Pivot)',
    complexity: { worst: 'O(n²) worst / O(n log n) exp.', average: 'O(n log n)', best: 'O(n log n)', space: 'O(log n)', stable: 'No (Unstable)' },
  },
};

const DISK_COLORS = [
  '#2563eb',
  '#16a34a',
  '#d97706',
  '#dc2626',
  '#7c3aed',
  '#0891b2',
  '#db2777',
  '#4f46e5',
];

const SVG_NS = 'http://www.w3.org/2000/svg';

/* ==========================================================================
 * 1. SHARED HELPERS & API CLIENT
 * ========================================================================== */

async function apiPost(path, payload) {
  const response = await fetch(`${CONFIG.API_BASE_URL}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    body: JSON.stringify(payload ?? {}),
  });
  const data = await response.json().catch(() => null);
  if (!response.ok) {
    const msg = data?.message || data?.error || `HTTP ${response.status}`;
    throw new Error(msg);
  }
  return data;
}

function formatNs(ns) {
  if (ns == null || ns < 0 || Number.isNaN(ns)) return '—';
  const ms = (ns / 1e6).toFixed(3);
  return `${ms} ms (${Math.round(ns).toLocaleString('en-US')} ns)`;
}

function fillTraceList(listEl, lines) {
  if (!listEl) return;
  if (!lines || lines.length === 0) {
    const li = document.createElement('li');
    li.className = 'trace-entry';
    li.textContent = 'No steps recorded.';
    listEl.replaceChildren(li);
    return;
  }
  const items = lines.map((line, i) => {
    const li = document.createElement('li');
    li.className = 'trace-entry';
    const idx = document.createElement('span');
    idx.className = 'trace-index';
    idx.textContent = String(i + 1).padStart(2, '0');
    const txt = document.createElement('span');
    txt.textContent = line;
    li.append(idx, txt);
    return li;
  });
  listEl.replaceChildren(...items);
}

/* ==========================================================================
 * 2. MODULE & SUB-TAB NAVIGATION
 * ========================================================================== */

function initNavigation() {
  const moduleTabs = document.querySelectorAll('.module-tab');
  const moduleViews = document.querySelectorAll('.module-view');

  moduleTabs.forEach((btn) => {
    btn.addEventListener('click', () => {
      const targetId = btn.dataset.module;
      moduleTabs.forEach((b) => b.classList.toggle('is-active', b === btn));
      moduleViews.forEach((v) => {
        v.hidden = v.id !== targetId;
      });
      if (targetId === 'module-c') {
        setTimeout(drawTreeEdges, 30);
      }
    });
  });

  document.querySelectorAll('.module-view').forEach((moduleEl) => {
    const subtabs = moduleEl.querySelectorAll('.subtab');
    const subviews = moduleEl.querySelectorAll('.subview');
    subtabs.forEach((tab) => {
      tab.addEventListener('click', () => {
        const subId = tab.dataset.subtab;
        subtabs.forEach((t) => t.classList.toggle('is-active', t === tab));
        subviews.forEach((sv) => {
          sv.hidden = sv.id !== subId;
        });
      });
    });
  });
}

/* ==========================================================================
 * 3. MODULE A: RECURSION LAB (Fibonacci, Factorial, Tower of Hanoi)
 * ========================================================================== */

const hanoiState = {
  data: null,
  currentStep: 0,
  timerId: null,
};

async function runFibonacci() {
  const inputEl = document.getElementById('fib-n');
  const errEl = document.getElementById('fib-error');
  const raw = inputEl.value.trim();

  if (raw === '' || !/^-?\d+$/.test(raw)) {
    errEl.textContent = 'Please enter a valid whole number n.';
    errEl.hidden = false;
    return;
  }

  const n = Number(raw);
  try {
    errEl.hidden = true;
    const res = await apiPost('/recursion/fibonacci', { n });
    document.getElementById('fib-explanation').textContent = res.naiveLimitExplanation;

    // Naive
    const nv = res.naiveRecursion;
    document.getElementById('fib-naive-res').textContent = nv.executed ? nv.result.toLocaleString('en-US') : 'Skipped (n > 30)';
    document.getElementById('fib-naive-ops').textContent = nv.executed ? nv.operationsCount.toLocaleString('en-US') : nv.skippedReason;
    document.getElementById('fib-naive-time').textContent = nv.executed ? formatNs(nv.runningTimeNs) : '—';
    fillTraceList(document.getElementById('fib-naive-trace'), nv.traceSteps);

    // Memoized
    const mm = res.memoizedRecursion;
    document.getElementById('fib-memo-res').textContent = mm.result.toLocaleString('en-US');
    document.getElementById('fib-memo-ops').textContent = mm.operationsCount.toLocaleString('en-US');
    document.getElementById('fib-memo-time').textContent = formatNs(mm.runningTimeNs);
    fillTraceList(document.getElementById('fib-memo-trace'), mm.traceSteps);

    // Bottom-Up DP
    const dp = res.bottomUpDp;
    document.getElementById('fib-dp-res').textContent = dp.result.toLocaleString('en-US');
    document.getElementById('fib-dp-ops').textContent = dp.operationsCount.toLocaleString('en-US');
    document.getElementById('fib-dp-time').textContent = formatNs(dp.runningTimeNs);
    fillTraceList(document.getElementById('fib-dp-trace'), dp.traceSteps);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.hidden = false;
  }
}

async function runFactorial() {
  const inputEl = document.getElementById('fact-n');
  const errEl = document.getElementById('fact-error');
  const raw = inputEl.value.trim();

  if (raw === '' || !/^-?\d+$/.test(raw)) {
    errEl.textContent = 'Please enter a valid whole number n.';
    errEl.hidden = false;
    return;
  }

  const n = Number(raw);
  try {
    errEl.hidden = true;
    const res = await apiPost('/recursion/factorial', { n });

    const rec = res.recursive;
    document.getElementById('fact-rec-res').textContent = rec.result.toLocaleString('en-US');
    document.getElementById('fact-rec-ops').textContent = rec.operationsCount.toLocaleString('en-US');
    document.getElementById('fact-rec-time').textContent = formatNs(rec.runningTimeNs);
    fillTraceList(document.getElementById('fact-rec-trace'), rec.traceSteps);

    const iter = res.iterative;
    document.getElementById('fact-iter-res').textContent = iter.result.toLocaleString('en-US');
    document.getElementById('fact-iter-ops').textContent = iter.operationsCount.toLocaleString('en-US');
    document.getElementById('fact-iter-time').textContent = formatNs(iter.runningTimeNs);
    fillTraceList(document.getElementById('fact-iter-trace'), iter.traceSteps);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.hidden = false;
  }
}

function pauseHanoi() {
  if (hanoiState.timerId !== null) {
    clearInterval(hanoiState.timerId);
    hanoiState.timerId = null;
  }
  const btn = document.getElementById('btn-hanoi-start');
  if (btn) btn.textContent = 'Start / Play';
}

function renderHanoiStep(idx) {
  const data = hanoiState.data;
  if (!data || !data.steps || data.steps.length === 0) return;

  const maxIdx = data.steps.length - 1;
  hanoiState.currentStep = Math.max(0, Math.min(idx, maxIdx));
  const step = data.steps[hanoiState.currentStep];

  const renderPeg = (pegChar, arr) => {
    const container = document.getElementById(`hanoi-disks-${pegChar}`);
    const disks = (arr || []).map((diskNum) => {
      const el = document.createElement('div');
      el.className = 'hanoi-disk';
      const pct = 28 + (diskNum / data.disks) * 64;
      el.style.width = `${pct}%`;
      el.style.background = DISK_COLORS[(diskNum - 1) % DISK_COLORS.length];
      el.textContent = `D${diskNum}`;
      return el;
    });
    container.replaceChildren(...disks);
  };

  renderPeg('A', step.pegA);
  renderPeg('B', step.pegB);
  renderPeg('C', step.pegC);

  document.getElementById('hanoi-current-move').textContent = `${step.moveNumber} / ${data.totalMoves}`;
  document.getElementById('hanoi-step-desc').textContent =
    step.moveNumber === 0
      ? step.description
      : `Move ${step.moveNumber} of ${data.totalMoves}: ${step.description}`;

  const slider = document.getElementById('hanoi-slider');
  slider.max = String(maxIdx);
  slider.value = String(hanoiState.currentStep);
  const pct = maxIdx > 0 ? (hanoiState.currentStep / maxIdx) * 100 : 0;
  slider.style.setProperty('--progress', `${pct}%`);

  const logItems = document.getElementById('hanoi-move-log').children;
  for (let i = 0; i < logItems.length; i++) {
    logItems[i].classList.toggle('is-current', i === hanoiState.currentStep);
  }
}

function toggleHanoiPlay() {
  if (!hanoiState.data) return;
  if (hanoiState.timerId !== null) {
    pauseHanoi();
    return;
  }
  const maxIdx = hanoiState.data.steps.length - 1;
  if (hanoiState.currentStep >= maxIdx) {
    renderHanoiStep(0);
  }
  const speedControl = document.getElementById('hanoi-speed');
  const intervalMs = 1300 - Number(speedControl.value);
  document.getElementById('btn-hanoi-start').textContent = 'Pause';

  hanoiState.timerId = setInterval(() => {
    if (hanoiState.currentStep < maxIdx) {
      renderHanoiStep(hanoiState.currentStep + 1);
    } else {
      pauseHanoi();
    }
  }, Math.max(80, intervalMs));
}

async function runHanoi() {
  pauseHanoi();
  const errEl = document.getElementById('hanoi-error');
  const rawDisks = document.getElementById('hanoi-disks').value.trim();
  const sourcePeg = document.getElementById('hanoi-src').value;
  const auxiliaryPeg = document.getElementById('hanoi-aux').value;
  const destinationPeg = document.getElementById('hanoi-dest').value;

  if (!/^\d+$/.test(rawDisks)) {
    errEl.textContent = 'Please enter a whole number of disks between 1 and 8.';
    errEl.hidden = false;
    return;
  }

  try {
    errEl.hidden = true;
    const res = await apiPost('/recursion/hanoi', {
      disks: Number(rawDisks),
      sourcePeg,
      destinationPeg,
      auxiliaryPeg,
    });
    hanoiState.data = res;

    document.getElementById('peg-role-A').textContent = '';
    document.getElementById('peg-role-B').textContent = '';
    document.getElementById('peg-role-C').textContent = '';
    document.getElementById(`peg-role-${res.sourcePeg}`).textContent = '(Source)';
    document.getElementById(`peg-role-${res.auxiliaryPeg}`).textContent = '(Auxiliary)';
    document.getElementById(`peg-role-${res.destinationPeg}`).textContent = '(Destination)';

    document.getElementById('hanoi-total-moves').textContent = String(res.totalMoves);
    document.getElementById('hanoi-formula').textContent = `2^${res.disks} − 1 = ${res.formulaExpectedMoves} ✓`;
    document.getElementById('hanoi-calls').textContent = res.recursiveCalls.toLocaleString('en-US');
    document.getElementById('hanoi-time').textContent = formatNs(res.runningTimeNs);
    document.getElementById('hanoi-move-count-tag').textContent = `${res.totalMoves} moves`;

    const lines = res.steps.map((s) => (s.moveNumber === 0 ? s.description : `Move ${s.moveNumber}: ${s.description}`));
    fillTraceList(document.getElementById('hanoi-move-log'), lines);
    renderHanoiStep(0);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.hidden = false;
  }
}

function initModuleA() {
  document.getElementById('btn-fib-run').addEventListener('click', runFibonacci);
  document.querySelectorAll('[data-fib-preset]').forEach((btn) => {
    btn.addEventListener('click', () => {
      document.getElementById('fib-n').value = btn.dataset.fibPreset;
      runFibonacci();
    });
  });

  document.getElementById('btn-fact-run').addEventListener('click', runFactorial);
  document.querySelectorAll('[data-fact-preset]').forEach((btn) => {
    btn.addEventListener('click', () => {
      document.getElementById('fact-n').value = btn.dataset.factPreset;
      runFactorial();
    });
  });

  document.getElementById('btn-hanoi-solve').addEventListener('click', runHanoi);
  document.getElementById('btn-hanoi-start').addEventListener('click', toggleHanoiPlay);
  document.getElementById('btn-hanoi-prev').addEventListener('click', () => {
    pauseHanoi();
    renderHanoiStep(hanoiState.currentStep - 1);
  });
  document.getElementById('btn-hanoi-next').addEventListener('click', () => {
    pauseHanoi();
    renderHanoiStep(hanoiState.currentStep + 1);
  });
  document.getElementById('btn-hanoi-reset').addEventListener('click', () => {
    pauseHanoi();
    renderHanoiStep(0);
  });
  document.getElementById('hanoi-slider').addEventListener('input', (e) => {
    pauseHanoi();
    renderHanoiStep(Number(e.target.value));
  });

  runFibonacci();
  runFactorial();
  runHanoi();
}

/* ==========================================================================
 * 4. MODULE B: DATA STRUCTURES LAB
 * ========================================================================== */

async function execStackOp(operation) {
  const errEl = document.getElementById('stack-error');
  const capVal = Number(document.getElementById('stack-cap').value);
  const rawVal = document.getElementById('stack-val').value.trim();

  if (operation === 'PUSH' && (rawVal === '' || !/^-?\d+$/.test(rawVal))) {
    errEl.textContent = 'Enter a valid integer value to push onto the stack.';
    errEl.hidden = false;
    return;
  }

  try {
    errEl.hidden = true;
    const res = await apiPost('/ds/stack', {
      operation,
      value: rawVal !== '' ? Number(rawVal) : null,
      capacity: capVal,
    });
    renderStackState(res);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.hidden = false;
  }
}

function renderStackState(res) {
  const statusEl = document.getElementById('stack-status');
  statusEl.textContent = res.message;
  statusEl.classList.toggle('is-error', !res.success);

  document.getElementById('stack-meta-badge').textContent =
    `top = ${res.topIndex} | size = ${res.size} / ${res.capacity} | empty = ${res.empty} | full = ${res.full}`;
  document.getElementById('stack-time-badge').textContent =
    `${res.basicOperations} op(s) · ${formatNs(res.runningTimeNs)}`;

  const canvas = document.getElementById('stack-canvas');
  const row = document.createElement('div');
  row.className = 'ds-slots-row';

  (res.slots || []).forEach((val, idx) => {
    const wrap = document.createElement('div');
    wrap.className = 'ds-slot-wrap';

    const ptrs = document.createElement('div');
    ptrs.className = 'ds-ptr-tags';
    if (idx === res.topIndex) {
      const badge = document.createElement('span');
      badge.className = 'ds-ptr-badge';
      badge.textContent = 'TOP ↓';
      ptrs.appendChild(badge);
    }

    const box = document.createElement('div');
    box.className = 'ds-box';
    if (val !== null && val !== undefined) {
      box.classList.add('is-occupied');
      box.textContent = String(val);
    } else {
      box.textContent = '—';
    }
    if (idx === res.topIndex) {
      box.classList.add('is-top');
    }

    const idxLabel = document.createElement('span');
    idxLabel.className = 'ds-slot-idx';
    idxLabel.textContent = `[${idx}]`;

    wrap.append(ptrs, box, idxLabel);
    row.appendChild(wrap);
  });

  canvas.replaceChildren(row);
}

async function runBracketCheck() {
  const expr = document.getElementById('bracket-expr').value;
  const errEl = document.getElementById('stack-error');
  try {
    errEl.hidden = true;
    const res = await apiPost('/ds/brackets', { expression: expr });

    document.getElementById('bracket-message').textContent =
      `${res.message} (${res.operationsCount} stack operations, ${formatNs(res.runningTimeNs)})`;

    const tbody = document.getElementById('bracket-tbody');
    const rows = (res.steps || []).map((s) => {
      const tr = document.createElement('tr');
      const stackStr = s.stackAfterSymbol && s.stackAfterSymbol.length > 0
        ? `[ ${s.stackAfterSymbol.join(' , ')} ] ← TOP`
        : '[ empty ]';
      tr.innerHTML = `
        <td class="num">${s.stepNumber}</td>
        <td class="num"><strong>${s.symbol}</strong></td>
        <td>${s.action}</td>
        <td class="num">${stackStr}</td>
        <td><span class="badge ${s.validSoFar ? 'badge-ok' : 'badge-err'}">${s.validSoFar ? 'OK' : 'Error'}</span></td>
      `;
      return tr;
    });
    tbody.replaceChildren(...rows);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.hidden = false;
  }
}

async function execQueueOp(operation) {
  const errEl = document.getElementById('queue-error');
  const mode = document.getElementById('queue-mode').value;
  const endpoint = mode === 'CIRCULAR_QUEUE' ? '/ds/circular-queue' : '/ds/linear-queue';
  const capVal = Number(document.getElementById('queue-cap').value);
  const rawVal = document.getElementById('queue-val').value.trim();

  if (operation === 'ENQUEUE' && (rawVal === '' || !/^-?\d+$/.test(rawVal))) {
    errEl.textContent = 'Enter a valid integer value to enqueue.';
    errEl.hidden = false;
    return;
  }

  try {
    errEl.hidden = true;
    const res = await apiPost(endpoint, {
      operation,
      value: rawVal !== '' ? Number(rawVal) : null,
      capacity: capVal,
    });
    renderQueueState(res);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.hidden = false;
  }
}

function renderQueueState(res) {
  const isCircular = res.queueType === 'CIRCULAR_QUEUE';
  document.getElementById('queue-vis-title').textContent = isCircular
    ? 'Circular Queue Visualizer (Wrap-Around via (rear + 1) % capacity)'
    : 'Linear Queue Visualizer (Demonstrating "False Full")';

  const statusEl = document.getElementById('queue-status');
  statusEl.textContent = res.message;
  statusEl.classList.toggle('is-error', !res.success || res.falseFull);

  document.getElementById('queue-meta-badge').textContent =
    `front = ${res.frontIndex} | rear = ${res.rearIndex} | size = ${res.size} / ${res.capacity}`;
  document.getElementById('queue-time-badge').textContent =
    `${res.basicOperations} op(s) · ${formatNs(res.runningTimeNs)}`;

  const canvas = document.getElementById('queue-canvas');
  const row = document.createElement('div');
  row.className = 'ds-slots-row';

  (res.slots || []).forEach((val, idx) => {
    const wrap = document.createElement('div');
    wrap.className = 'ds-slot-wrap';

    const ptrs = document.createElement('div');
    ptrs.className = 'ds-ptr-tags';
    if (res.size > 0 && idx === res.frontIndex) {
      const fBadge = document.createElement('span');
      fBadge.className = 'ds-ptr-badge is-front';
      fBadge.textContent = 'FRONT';
      ptrs.appendChild(fBadge);
    }
    if (res.rearIndex === idx && idx >= 0) {
      const rBadge = document.createElement('span');
      rBadge.className = 'ds-ptr-badge is-rear';
      rBadge.textContent = 'REAR';
      ptrs.appendChild(rBadge);
    }

    const box = document.createElement('div');
    box.className = 'ds-box';
    const isWastedLinearSlot = !isCircular && val === null && idx < res.frontIndex && res.rearIndex >= 0;
    if (val !== null && val !== undefined) {
      box.classList.add('is-occupied');
      box.textContent = String(val);
    } else if (isWastedLinearSlot) {
      box.classList.add('is-wasted');
      box.textContent = 'wasted';
    } else {
      box.textContent = '—';
    }

    const idxLabel = document.createElement('span');
    idxLabel.className = 'ds-slot-idx';
    idxLabel.textContent = isCircular ? `[${idx}] ↻` : `[${idx}]`;

    wrap.append(ptrs, box, idxLabel);
    row.appendChild(wrap);
  });

  canvas.replaceChildren(row);
}

async function execListOp(operation) {
  const errEl = document.getElementById('list-error');
  const mode = document.getElementById('list-mode').value;
  const endpoint = mode === 'DOUBLY_LINKED_LIST' ? '/ds/doubly-list' : '/ds/singly-list';
  const rawVal = document.getElementById('list-val').value.trim();
  const rawTarget = document.getElementById('list-target').value.trim();

  const needsVal = ['INSERT_BEGINNING', 'INSERT_END', 'INSERT_BEFORE', 'INSERT_AFTER'].includes(operation);
  const needsTarget = ['INSERT_BEFORE', 'INSERT_AFTER', 'DELETE_BEFORE', 'DELETE_AFTER', 'DELETE_ELEMENT', 'SEARCH'].includes(operation);

  if (needsVal && (rawVal === '' || !/^-?\d+$/.test(rawVal))) {
    errEl.textContent = 'Please enter a valid integer in "New Value".';
    errEl.hidden = false;
    return;
  }
  if (needsTarget && (rawTarget === '' || !/^-?\d+$/.test(rawTarget))) {
    errEl.textContent = 'Please enter a valid integer in "Given Element".';
    errEl.hidden = false;
    return;
  }

  try {
    errEl.hidden = true;
    const res = await apiPost(endpoint, {
      operation,
      value: rawVal !== '' ? Number(rawVal) : null,
      target: rawTarget !== '' ? Number(rawTarget) : null,
    });
    renderLinkedListState(res);
  } catch (err) {
    errEl.textContent = err.message;
    errEl.hidden = false;
  }
}

function renderLinkedListState(res) {
  const isDoubly = res.listType === 'DOUBLY_LINKED_LIST';
  document.getElementById('list-vis-title').textContent = isDoubly
    ? 'Doubly Linked List Visualizer (Explicit prev & next references)'
    : 'Singly Linked List Visualizer (Explicit next references)';
  document.getElementById('btn-list-backward').disabled = !isDoubly;

  const statusEl = document.getElementById('list-status');
  statusEl.textContent = res.message;
  statusEl.classList.toggle('is-error', !res.success);

  document.getElementById('list-meta-badge').textContent = `size = ${res.size} node(s)`;
  document.getElementById('list-time-badge').textContent =
    `${res.basicOperations} op(s) · ${formatNs(res.runningTimeNs)}`;

  const canvas = document.getElementById('list-canvas');
  const chain = document.createElement('div');
  chain.className = 'll-chain';

  const elements = res.operation === 'DISPLAY_BACKWARD' && isDoubly
    ? res.backwardElements || []
    : res.forwardElements || [];

  const headTag = document.createElement('span');
  headTag.className = 'll-endpoint';
  headTag.textContent = res.operation === 'DISPLAY_BACKWARD' ? 'TAIL' : 'HEAD';
  chain.appendChild(headTag);

  if (elements.length === 0) {
    const arrow = document.createElement('span');
    arrow.className = 'll-arrow';
    arrow.textContent = '→';
    const nullTag = document.createElement('span');
    nullTag.className = 'll-endpoint';
    nullTag.textContent = 'NULL';
    chain.append(arrow, nullTag);
  } else {
    elements.forEach((val, idx) => {
      const arrow = document.createElement('span');
      arrow.className = 'll-arrow';
      arrow.textContent = isDoubly ? '⇄' : '→';
      chain.appendChild(arrow);

      const nodeWrap = document.createElement('div');
      nodeWrap.className = 'll-node';
      if (idx === res.highlightedIndex) {
        nodeWrap.classList.add('is-highlight');
      }

      const box = document.createElement('div');
      box.className = 'll-node-box';
      if (isDoubly) {
        box.innerHTML = `
          <span class="ll-cell ll-cell-ptr">prev</span>
          <span class="ll-cell ll-cell-data">${val}</span>
          <span class="ll-cell ll-cell-ptr">next</span>
        `;
      } else {
        box.innerHTML = `
          <span class="ll-cell ll-cell-data">${val}</span>
          <span class="ll-cell ll-cell-ptr">next</span>
        `;
      }

      const idxLabel = document.createElement('span');
      idxLabel.className = 'ds-slot-idx';
      idxLabel.textContent = `node[${idx}]`;

      nodeWrap.append(box, idxLabel);
      chain.appendChild(nodeWrap);
    });

    const endArrow = document.createElement('span');
    endArrow.className = 'll-arrow';
    endArrow.textContent = '→';
    const nullTag = document.createElement('span');
    nullTag.className = 'll-endpoint';
    nullTag.textContent = 'NULL';
    chain.append(endArrow, nullTag);
  }

  canvas.replaceChildren(chain);
}

function initModuleB() {
  document.getElementById('btn-stack-init').addEventListener('click', () => execStackOp('INIT'));
  document.querySelectorAll('[data-stack-op]').forEach((btn) => {
    btn.addEventListener('click', () => execStackOp(btn.dataset.stackOp));
  });

  document.getElementById('btn-bracket-check').addEventListener('click', runBracketCheck);
  document.querySelectorAll('[data-bracket-preset]').forEach((btn) => {
    btn.addEventListener('click', () => {
      document.getElementById('bracket-expr').value = btn.dataset.bracketPreset;
      runBracketCheck();
    });
  });

  document.getElementById('queue-mode').addEventListener('change', () => execQueueOp('STATE'));
  document.getElementById('btn-queue-init').addEventListener('click', () => execQueueOp('INIT'));
  document.querySelectorAll('[data-queue-op]').forEach((btn) => {
    btn.addEventListener('click', () => execQueueOp(btn.dataset.queueOp));
  });
  document.getElementById('btn-queue-demo').addEventListener('click', () => {
    const mode = document.getElementById('queue-mode').value;
    execQueueOp(mode === 'CIRCULAR_QUEUE' ? 'DEMO_WRAP_AROUND' : 'DEMO_FALSE_FULL');
  });

  document.getElementById('list-mode').addEventListener('change', () => execListOp('STATE'));
  document.querySelectorAll('[data-list-op]').forEach((btn) => {
    btn.addEventListener('click', () => execListOp(btn.dataset.listOp));
  });

  execStackOp('STATE');
  runBracketCheck();
  execQueueOp('STATE');
  execListOp('STATE');
}

/* ==========================================================================
 * 5. MODULE C: SORTING LAB
 * ========================================================================== */

const sortState = {
  tracking: null,
  activeSteps: [],
  currentStep: 0,
  timerId: null,
  isBusy: false,
  cardEls: [],
};

const els = {};

function cacheSortingElements() {
  const ids = {
    input: 'input-data',
    inputError: 'input-error',
    randomCount: 'random-count',
    btnRandom: 'btn-random',
    algorithm: 'algorithm-select',
    chkPassOnly: 'chk-pass-only',
    btnRun: 'btn-run',
    btnCompareAll: 'btn-compare-all',
    btnResetAll: 'btn-reset-all',
    cxWorst: 'cx-worst',
    cxAverage: 'cx-average',
    cxBest: 'cx-best',
    cxSpace: 'cx-space',
    cxStable: 'cx-stable',
    stage: 'stage',
    linearStage: 'linear-stage',
    arrayRow: 'array-row',
    pointerRow: 'pointer-row',
    treeStage: 'tree-stage',
    stepDescription: 'step-description',
    btnFirst: 'btn-first',
    btnPrev: 'btn-prev',
    btnPlay: 'btn-play',
    btnNext: 'btn-next',
    btnLast: 'btn-last',
    btnRewind: 'btn-rewind',
    sortSpeed: 'sort-speed',
    slider: 'progress-slider',
    metricTime: 'metric-time',
    metricComparisons: 'metric-comparisons',
    metricSwaps: 'metric-swaps',
    traceLog: 'trace-log',
    compareAllPanel: 'compare-all-panel',
    compareAllTbody: 'compare-all-tbody',
    compareInputSummary: 'compare-input-summary',
  };
  for (const [key, id] of Object.entries(ids)) {
    els[key] = document.getElementById(id);
  }
}

function createCard(value) {
  const card = document.createElement('div');
  card.className = 'card';
  card.setAttribute('role', 'listitem');
  card.dataset.value = String(value);

  const label = document.createElement('span');
  label.className = 'card-value';
  label.textContent = String(value);
  card.appendChild(label);
  return card;
}

function renderArray(values, step = null) {
  els.arrayRow.style.setProperty('--n', values.length);
  els.pointerRow.style.setProperty('--n', values.length);

  const nextEls = values.map((v) => createCard(v));
  els.arrayRow.replaceChildren(...nextEls);
  sortState.cardEls = nextEls;

  const active = new Set(step?.active ?? []);
  const sorted = new Set(step?.sorted ?? []);
  const yellow = new Set(step?.yellow ?? []);
  const green = new Set(step?.green ?? []);

  nextEls.forEach((el, i) => {
    el.classList.toggle('is-yellow', yellow.has(i));
    el.classList.toggle('is-green', green.has(i));
    el.classList.toggle('is-active', active.has(i));
    el.classList.toggle('is-pivot', step?.pivot === i);
    el.classList.toggle('is-sorted', sorted.has(i));
    el.setAttribute('aria-label', `Index ${i}: ${values[i]}`);
  });
}

function renderPointers(length, pointers = []) {
  const byIndex = Array.from({ length }, () => []);
  for (const p of pointers) {
    if (Number.isInteger(p.index) && p.index >= 0 && p.index < length) {
      byIndex[p.index].push(p.label);
    }
  }

  const slots = byIndex.map((labels) => {
    const slot = document.createElement('div');
    slot.className = 'pointer-slot';
    if (labels.length) {
      const tick = document.createElement('span');
      tick.className = 'pointer-tick';
      slot.appendChild(tick);
      for (const text of labels) {
        const label = document.createElement('span');
        label.className = 'pointer-label';
        label.textContent = text;
        slot.appendChild(label);
      }
    }
    return slot;
  });

  els.pointerRow.replaceChildren(...slots);
}

function buildMergeTreeLevels(n) {
  const levels = [];
  const visit = (lo, hi, depth, parentId = null) => {
    if (!levels[depth]) levels[depth] = [];
    const id = `${lo}-${hi}`;
    levels[depth].push({ id, lo, hi, depth, parentId });
    if (lo < hi) {
      const mid = Math.floor((lo + hi) / 2);
      visit(lo, mid, depth + 1, id);
      visit(mid + 1, hi, depth + 1, id);
    }
  };
  if (n > 0) visit(0, n - 1, 0, null);
  return levels;
}

function drawTreeEdges() {
  if (!els.treeStage || els.treeStage.hidden) return;
  const svg = els.treeStage.querySelector('.tree-edges');
  if (!svg) return;

  const stageRect = els.treeStage.getBoundingClientRect();
  if (stageRect.width === 0 || stageRect.height === 0) return;

  svg.setAttribute('viewBox', `0 0 ${stageRect.width} ${stageRect.height}`);

  const byId = new Map();
  for (const el of els.treeStage.querySelectorAll('[data-node-id]')) {
    byId.set(el.dataset.nodeId, el);
  }

  const paths = [];
  for (const childEl of els.treeStage.querySelectorAll('[data-parent-id]')) {
    const parentEl = byId.get(childEl.dataset.parentId);
    if (!parentEl) continue;

    const pRect = parentEl.getBoundingClientRect();
    const cRect = childEl.getBoundingClientRect();

    const x1 = pRect.left - stageRect.left + pRect.width / 2;
    const y1 = pRect.bottom - stageRect.top + 2;
    const x2 = cRect.left - stageRect.left + cRect.width / 2;
    const y2 = cRect.top - stageRect.top - 2;

    const path = document.createElementNS(SVG_NS, 'path');
    path.setAttribute('d', `M ${x1.toFixed(1)} ${y1.toFixed(1)} L ${x2.toFixed(1)} ${y2.toFixed(1)}`);

    let cls = 'tree-edge';
    if (childEl.dataset.edgeState === 'active') cls += ' is-active';
    if (childEl.dataset.edgeState === 'detached') cls += ' is-detached';
    path.setAttribute('class', cls);
    paths.push(path);
  }

  svg.replaceChildren(...paths);
}

function renderMergeTree(values, step = null) {
  const n = values.length;
  const levels = buildMergeTreeLevels(n);
  const numLevels = levels.length;

  const cellSize = n <= 8 ? '34px' : n <= 12 ? '28px' : '22px';
  const cellFont = n <= 8 ? '13px' : n <= 12 ? '12px' : '11px';
  const colMax = n <= 8 ? '54px' : n <= 12 ? '42px' : '34px';

  const treeData = step?.mergeTree ?? null;
  const nodeMap = {};
  if (Array.isArray(treeData?.nodes)) {
    treeData.nodes.forEach((nd) => {
      if (nd && nd.id) nodeMap[nd.id] = nd;
    });
  }

  const focusId = treeData?.focusId ?? `0-${n - 1}`;
  const activeIndices = new Set(treeData?.activeIndices ?? step?.active ?? []);
  const allSorted = (step?.sorted?.length ?? 0) === n;

  const tree = document.createElement('div');
  tree.className = 'merge-tree';
  tree.dataset.levels = String(numLevels);
  tree.style.setProperty('--cell-size', cellSize);
  tree.style.setProperty('--cell-font', cellFont);

  for (let d = 0; d < numLevels; d++) {
    const levelEl = document.createElement('div');
    levelEl.className = 'merge-level';
    levelEl.style.setProperty('--n', String(n));
    levelEl.style.setProperty('--col-max', colMax);

    for (const node of levels[d]) {
      const { id, lo, hi, parentId } = node;
      const recorded = nodeMap[id];
      const slice = recorded ? recorded.values : values.slice(lo, hi + 1);
      const status = recorded ? recorded.status : step ? 'merged' : d === 0 ? 'focus' : 'merged';
      const isFocus = id === focusId;

      const group = document.createElement('div');
      group.className = 'merge-group';
      group.style.setProperty('--start', String(lo + 1));
      group.style.setProperty('--span', String(hi - lo + 1));
      group.classList.toggle('is-focus', isFocus);
      group.classList.toggle('is-merged', !isFocus && status === 'merged');
      group.classList.toggle('is-pending', !isFocus && status === 'pending');
      group.dataset.nodeId = `merge-${id}`;

      if (parentId) {
        group.dataset.parentId = `merge-${parentId}`;
        if (isFocus || parentId === focusId) {
          group.dataset.edgeState = 'active';
        } else if (status === 'pending') {
          group.dataset.edgeState = 'detached';
        }
      }

      const cells = document.createElement('div');
      cells.className = 'merge-group-cells';

      slice.forEach((val, offset) => {
        const globalIdx = lo + offset;
        const cell = createCard(val);
        cell.classList.add('merge-card');
        if (isFocus && activeIndices.has(globalIdx)) {
          cell.classList.add('is-active');
        }
        if (allSorted && d === 0) {
          cell.classList.add('is-sorted');
        }
        cells.appendChild(cell);
      });

      const rangeLabel = document.createElement('span');
      rangeLabel.className = 'merge-range';
      rangeLabel.textContent = lo === hi ? `[${lo}]` : `[${lo}..${hi}]`;

      group.append(cells, rangeLabel);
      levelEl.appendChild(group);
    }

    tree.appendChild(levelEl);
  }

  const svg = document.createElementNS(SVG_NS, 'svg');
  svg.setAttribute('class', 'tree-edges');
  svg.setAttribute('aria-hidden', 'true');

  els.treeStage.replaceChildren(svg, tree);
  drawTreeEdges();
}

function renderVisualizer(values, step = null) {
  const algo = sortState.tracking?.algorithm ?? els.algorithm.value;
  const useTree = algo === 'MERGE_SORT';

  els.stage.classList.toggle('is-tree-mode', useTree);
  els.linearStage.hidden = useTree;
  els.treeStage.hidden = !useTree;

  if (useTree) {
    renderMergeTree(values, step);
  } else {
    renderArray(values, step);
    renderPointers(values.length, step?.pointers ?? []);
  }
}

function renderComplexity(algorithmKey) {
  const meta = ALGORITHMS[algorithmKey]?.complexity ?? {
    worst: '—',
    average: '—',
    best: '—',
    space: '—',
    stable: '—',
  };
  els.cxWorst.textContent = meta.worst;
  els.cxAverage.textContent = meta.average;
  els.cxBest.textContent = meta.best;
  els.cxSpace.textContent = meta.space;
  els.cxStable.textContent = meta.stable;
}

function rebuildActiveSteps() {
  if (!sortState.tracking) {
    sortState.activeSteps = [];
    return;
  }
  const all = sortState.tracking.steps;
  if (els.chkPassOnly.checked) {
    sortState.activeSteps = all.filter((s, idx) => s.passBoundary || idx === 0 || idx === all.length - 1);
  } else {
    sortState.activeSteps = all;
  }
}

function renderSortStep(index) {
  const steps = sortState.activeSteps;
  if (!steps || steps.length === 0) return;

  const total = steps.length;
  sortState.currentStep = Math.min(Math.max(index, 0), total - 1);
  const step = steps[sortState.currentStep];

  renderVisualizer(step.array, step);
  els.stepDescription.textContent = `Step ${sortState.currentStep + 1} of ${total}: ${step.description}`;
  els.metricComparisons.textContent = String(step.comparisons ?? 0);
  els.metricSwaps.textContent = String(step.swaps ?? 0);

  const items = els.traceLog.children;
  for (let i = 0; i < items.length; i++) {
    items[i].hidden = i > sortState.currentStep;
    items[i].classList.toggle('is-current', i === sortState.currentStep);
  }
  els.traceLog.scrollTop = els.traceLog.scrollHeight;
  syncSortPlaybackControls();
}

function syncSortPlaybackControls() {
  const hasRun = sortState.activeSteps.length > 0;
  const last = Math.max(0, sortState.activeSteps.length - 1);
  const atStart = sortState.currentStep <= 0;
  const atEnd = sortState.currentStep >= last;
  const playing = sortState.timerId !== null;

  els.btnFirst.disabled = !hasRun || atStart;
  els.btnPrev.disabled = !hasRun || atStart;
  els.btnNext.disabled = !hasRun || atEnd;
  els.btnLast.disabled = !hasRun || atEnd;
  els.btnRewind.disabled = !hasRun;
  els.btnPlay.disabled = !hasRun;

  els.btnPlay.classList.toggle('is-playing', playing);
  els.btnPlay.setAttribute('aria-pressed', String(playing));

  els.slider.disabled = !hasRun;
  els.slider.max = String(last);
  els.slider.value = String(sortState.currentStep);
  const pct = last > 0 ? (sortState.currentStep / last) * 100 : 0;
  els.slider.style.setProperty('--progress', `${pct}%`);
}

function pauseSort() {
  if (sortState.timerId !== null) {
    clearInterval(sortState.timerId);
    sortState.timerId = null;
  }
  syncSortPlaybackControls();
}

function playSort() {
  if (sortState.activeSteps.length === 0 || sortState.timerId !== null) return;
  const last = sortState.activeSteps.length - 1;
  if (sortState.currentStep >= last) {
    renderSortStep(0);
  }
  const intervalMs = 1320 - Number(els.sortSpeed.value);
  sortState.timerId = setInterval(() => {
    if (sortState.currentStep < sortState.activeSteps.length - 1) {
      renderSortStep(sortState.currentStep + 1);
    } else {
      pauseSort();
    }
  }, Math.max(80, intervalMs));
  syncSortPlaybackControls();
}

function parseInputArray(raw) {
  const tokens = raw.split(/[\s,;]+/).filter(Boolean);
  if (tokens.length < CONFIG.MIN_ELEMENTS) {
    return { error: `Enter at least ${CONFIG.MIN_ELEMENTS} numbers separated by commas.` };
  }
  if (tokens.length > CONFIG.MAX_ELEMENTS) {
    return { error: `Maximum of ${CONFIG.MAX_ELEMENTS} numbers allowed for visual animation (entered ${tokens.length}).` };
  }
  const values = [];
  for (const token of tokens) {
    if (!/^-?\d+$/.test(token)) {
      return { error: `"${token}" is not a valid integer.` };
    }
    const v = Number(token);
    if (v < CONFIG.MIN_VALUE || v > CONFIG.MAX_VALUE) {
      return { error: `Values must be between ${CONFIG.MIN_VALUE} and ${CONFIG.MAX_VALUE}.` };
    }
    values.push(v);
  }
  return { values };
}

function showSortError(msg) {
  els.inputError.textContent = msg;
  els.inputError.hidden = false;
  els.input.classList.add('has-error');
}

function clearSortError() {
  els.inputError.hidden = true;
  els.inputError.textContent = '';
  els.input.classList.remove('has-error');
}

function previewSortInput() {
  const parsed = parseInputArray(els.input.value);
  if (parsed.error) return;
  clearSortError();
  pauseSort();
  sortState.tracking = null;
  sortState.activeSteps = [];
  renderVisualizer(parsed.values);
  els.stepDescription.textContent = `Ready: ${parsed.values.length} elements loaded. Press "Run Selected Algorithm" or "Compare All Five Algorithms".`;
  els.metricTime.textContent = '—';
  els.metricComparisons.textContent = '—';
  els.metricSwaps.textContent = '—';
  fillTraceList(els.traceLog, []);
  syncSortPlaybackControls();
}

async function handleRunSort() {
  const parsed = parseInputArray(els.input.value);
  if (parsed.error) {
    showSortError(parsed.error);
    return;
  }
  clearSortError();
  pauseSort();

  try {
    const res = await apiPost('/sort', {
      array: parsed.values,
      algorithm: els.algorithm.value,
    });
    sortState.tracking = res;
    rebuildActiveSteps();
    els.metricTime.textContent = formatNs(res.metrics.runningTimeNs);
    fillTraceList(
      els.traceLog,
      sortState.activeSteps.map((s) => s.trace || s.description)
    );
    renderSortStep(0);
    if (CONFIG.AUTOPLAY_ON_RUN) playSort();
  } catch (err) {
    showSortError(err.message);
  }
}

async function handleCompareAll() {
  const parsed = parseInputArray(els.input.value);
  if (parsed.error) {
    showSortError(parsed.error);
    return;
  }
  clearSortError();

  try {
    const res = await apiPost('/sort/compare-all', {
      array: parsed.values,
      algorithm: els.algorithm.value,
    });
    els.compareAllPanel.hidden = false;
    els.compareInputSummary.textContent = `Input (${res.input.length}): [${res.input.join(', ')}] → Sorted: [${res.sortedOutput.join(', ')}]`;

    const rows = (res.results || []).map((item) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><strong>${item.algorithmName}</strong></td>
        <td class="num">${item.comparisons.toLocaleString('en-US')}</td>
        <td class="num">${item.swapsOrMoves.toLocaleString('en-US')}</td>
        <td class="num">${formatNs(item.runningTimeNs)}</td>
        <td class="num">${item.bestCase}</td>
        <td class="num">${item.averageCase}</td>
        <td class="num">${item.worstCase}</td>
        <td class="num">${item.extraSpace}</td>
        <td><span class="badge ${item.stable ? 'badge-ok' : ''}">${item.stable ? 'Stable' : 'Unstable'}</span></td>
      `;
      return tr;
    });
    els.compareAllTbody.replaceChildren(...rows);
  } catch (err) {
    showSortError(err.message);
  }
}

function initModuleC() {
  cacheSortingElements();
  renderComplexity(els.algorithm.value);
  previewSortInput();

  els.btnRun.addEventListener('click', handleRunSort);
  els.btnCompareAll.addEventListener('click', handleCompareAll);
  els.btnResetAll.addEventListener('click', () => {
    pauseSort();
    els.input.value = '45, 12, 89, 33, 7, 21, 54, 18';
    els.algorithm.value = 'QUICK_SORT';
    els.compareAllPanel.hidden = true;
    renderComplexity('QUICK_SORT');
    previewSortInput();
  });

  els.btnRandom.addEventListener('click', () => {
    let n = Number(els.randomCount.value);
    if (Number.isNaN(n) || n < CONFIG.MIN_ELEMENTS) n = CONFIG.MIN_ELEMENTS;
    if (n > CONFIG.MAX_ELEMENTS) n = CONFIG.MAX_ELEMENTS;
    els.randomCount.value = String(n);

    const arr = [];
    for (let i = 0; i < n; i++) {
      arr.push(Math.floor(Math.random() * 95) + 5);
    }
    els.input.value = arr.join(', ');
    previewSortInput();
  });

  els.algorithm.addEventListener('change', () => {
    renderComplexity(els.algorithm.value);
    previewSortInput();
  });

  els.chkPassOnly.addEventListener('change', () => {
    if (!sortState.tracking) return;
    pauseSort();
    rebuildActiveSteps();
    fillTraceList(
      els.traceLog,
      sortState.activeSteps.map((s) => s.trace || s.description)
    );
    renderSortStep(0);
  });

  els.btnFirst.addEventListener('click', () => {
    pauseSort();
    renderSortStep(0);
  });
  els.btnPrev.addEventListener('click', () => {
    pauseSort();
    renderSortStep(sortState.currentStep - 1);
  });
  els.btnPlay.addEventListener('click', () => {
    sortState.timerId !== null ? pauseSort() : playSort();
  });
  els.btnNext.addEventListener('click', () => {
    pauseSort();
    renderSortStep(sortState.currentStep + 1);
  });
  els.btnLast.addEventListener('click', () => {
    pauseSort();
    renderSortStep(sortState.activeSteps.length - 1);
  });
  els.btnRewind.addEventListener('click', () => {
    pauseSort();
    renderSortStep(0);
  });
  els.slider.addEventListener('input', () => {
    pauseSort();
    renderSortStep(Number(els.slider.value));
  });
}

/* ==========================================================================
 * 6. MODULE D: TIME COMPLEXITY ANALYSIS & SVG CHARTS
 * ========================================================================== */

const benchState = {
  data: null,
};

const SERIES_COLORS = {
  BUBBLE_SORT: '#dc2626',
  SELECTION_SORT: '#d97706',
  INSERTION_SORT: '#16a34a',
  MERGE_SORT: '#2563eb',
  QUICK_SORT: '#7c3aed',
  QUICK_SORT_RANDOM: '#0891b2',
};

function drawLineChart(containerEl, seriesList) {
  const width = 520;
  const height = 230;
  const padL = 56;
  const padR = 20;
  const padT = 18;
  const padB = 36;
  const plotW = width - padL - padR;
  const plotH = height - padT - padB;

  const allPoints = [];
  seriesList.forEach((s) => {
    (s.points || []).forEach((pt) => {
      if (pt.executed && pt.averageTimeMs >= 0) {
        allPoints.push(pt);
      }
    });
  });

  if (allPoints.length === 0) {
    containerEl.textContent = 'No chart data yet.';
    return;
  }

  const sizes = Array.from(new Set(allPoints.map((p) => p.n))).sort((a, b) => a - b);
  const maxY = Math.max(0.01, ...allPoints.map((p) => p.averageTimeMs));

  const xOf = (nVal) => {
    const idx = sizes.indexOf(nVal);
    if (sizes.length <= 1) return padL + plotW / 2;
    return padL + (idx / (sizes.length - 1)) * plotW;
  };
  const yOf = (ms) => padT + plotH - (ms / maxY) * plotH;

  const svg = document.createElementNS(SVG_NS, 'svg');
  svg.setAttribute('viewBox', `0 0 ${width} ${height}`);
  svg.setAttribute('class', 'chart-svg');

  // Grid lines & Y labels
  for (let g = 0; g <= 4; g++) {
    const val = (maxY * g) / 4;
    const y = yOf(val);
    const line = document.createElementNS(SVG_NS, 'line');
    line.setAttribute('x1', String(padL));
    line.setAttribute('x2', String(width - padR));
    line.setAttribute('y1', String(y));
    line.setAttribute('y2', String(y));
    line.setAttribute('stroke', '#e2e5e9');
    svg.appendChild(line);

    const lbl = document.createElementNS(SVG_NS, 'text');
    lbl.setAttribute('x', String(padL - 6));
    lbl.setAttribute('y', String(y + 4));
    lbl.setAttribute('text-anchor', 'end');
    lbl.setAttribute('font-size', '10');
    lbl.setAttribute('fill', '#5c6370');
    lbl.textContent = `${val.toFixed(val < 1 ? 2 : 1)}ms`;
    svg.appendChild(lbl);
  }

  // X labels
  sizes.forEach((nVal) => {
    const x = xOf(nVal);
    const lbl = document.createElementNS(SVG_NS, 'text');
    lbl.setAttribute('x', String(x));
    lbl.setAttribute('y', String(height - 10));
    lbl.setAttribute('text-anchor', 'middle');
    lbl.setAttribute('font-size', '10');
    lbl.setAttribute('fill', '#5c6370');
    lbl.textContent = nVal >= 1000 ? `${nVal / 1000}k` : String(nVal);
    svg.appendChild(lbl);
  });

  // Series lines & dots
  seriesList.forEach((s) => {
    const validPts = (s.points || []).filter((p) => p.executed && p.averageTimeMs >= 0);
    if (validPts.length === 0) return;

    const color = SERIES_COLORS[s.algorithmKey] || '#3b4454';
    const dStr = validPts
      .map((p, i) => `${i === 0 ? 'M' : 'L'} ${xOf(p.n).toFixed(1)} ${yOf(p.averageTimeMs).toFixed(1)}`)
      .join(' ');

    const path = document.createElementNS(SVG_NS, 'path');
    path.setAttribute('d', dStr);
    path.setAttribute('fill', 'none');
    path.setAttribute('stroke', color);
    path.setAttribute('stroke-width', '2.2');
    svg.appendChild(path);

    validPts.forEach((p) => {
      const circle = document.createElementNS(SVG_NS, 'circle');
      circle.setAttribute('cx', String(xOf(p.n)));
      circle.setAttribute('cy', String(yOf(p.averageTimeMs)));
      circle.setAttribute('r', '3.5');
      circle.setAttribute('fill', color);
      svg.appendChild(circle);
    });
  });

  const legend = document.createElement('div');
  legend.className = 'chart-legend';
  seriesList.forEach((s) => {
    const item = document.createElement('span');
    item.className = 'legend-item';
    const dot = document.createElement('span');
    dot.className = 'legend-dot';
    dot.style.background = SERIES_COLORS[s.algorithmKey] || '#3b4454';
    item.append(dot, document.createTextNode(s.algorithmName));
    legend.appendChild(item);
  });

  containerEl.replaceChildren(svg, legend);
}

function renderBenchmarkView() {
  const data = benchState.data;
  if (!data) return;

  const selectedType = document.getElementById('bench-dist-select').value;
  const dist = (data.distributions || []).find((d) => d.inputType === selectedType) || data.distributions[0];
  if (!dist) return;

  const quadSeries = dist.series.filter((s) => s.complexityGroup === 'QUADRATIC');
  const nlognSeries = dist.series.filter((s) => s.complexityGroup === 'NLOGN');

  drawLineChart(document.getElementById('chart-quadratic'), quadSeries);
  drawLineChart(document.getElementById('chart-nlogn'), nlognSeries);

  document.getElementById('bench-table-title').textContent =
    `${dist.inputLabel} — Average Running Time (ms) & Comparisons (${data.repetitions}-Run Average)`;
  document.getElementById('bench-reps-badge').textContent = `${data.repetitions}-Run Average`;

  const allSizes = [...data.standardSizes, ...(data.largeSizes || [])];
  const thead = document.getElementById('bench-thead');
  thead.innerHTML = `
    <tr>
      <th>Algorithm</th>
      ${allSizes.map((n) => `<th>n = ${n.toLocaleString('en-US')}</th>`).join('')}
    </tr>
  `;

  const tbody = document.getElementById('bench-tbody');
  const rows = dist.series.map((s) => {
    const tr = document.createElement('tr');
    const byN = {};
    (s.points || []).forEach((pt) => {
      byN[pt.n] = pt;
    });

    const cells = allSizes.map((n) => {
      const pt = byN[n];
      if (!pt) return `<td class="num">—</td>`;
      if (!pt.executed) {
        return `<td class="num" title="${pt.note || ''}">O(n²) skip</td>`;
      }
      return `<td class="num"><strong>${pt.averageTimeMs.toFixed(3)} ms</strong><br/><small>${pt.averageComparisons.toLocaleString('en-US')} cmp</small></td>`;
    });

    tr.innerHTML = `<td><strong>${s.algorithmName}</strong></td>${cells.join('')}`;
    return tr;
  });
  tbody.replaceChildren(...rows);
}

async function runBenchmarkSuite() {
  const btn = document.getElementById('btn-bench-run');
  const statusEl = document.getElementById('bench-status');
  const reps = Math.max(3, Number(document.getElementById('bench-reps').value) || 3);
  const includeLargeN = document.getElementById('bench-large').checked;

  btn.disabled = true;
  btn.textContent = 'Running Benchmarks…';
  statusEl.textContent = `Running ${reps} repetitions per configuration across Random, Sorted, and Reverse-Sorted inputs…`;
  statusEl.classList.remove('is-error');

  try {
    const res = await apiPost('/benchmark/run', { repetitions: reps, includeLargeN });
    benchState.data = res;
    statusEl.textContent = `Completed ${res.repetitions}-run average benchmark across Random, Already Sorted, and Reverse Sorted inputs.`;
    document.getElementById('btn-bench-csv').disabled = false;

    const discEl = document.getElementById('bench-discussion-list');
    discEl.replaceChildren(
      ...(res.discussionPoints || []).map((pt) => {
        const li = document.createElement('li');
        li.textContent = pt;
        return li;
      })
    );

    renderBenchmarkView();
  } catch (err) {
    statusEl.textContent = `Benchmark failed: ${err.message}`;
    statusEl.classList.add('is-error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Run Full Benchmark Suite';
  }
}

function exportBenchmarkCsv() {
  if (!benchState.data) return;
  const lines = ['InputDistribution,Algorithm,n,AverageTimeMs,AverageTimeNs,AverageComparisons,AverageSwapsOrMoves'];
  benchState.data.distributions.forEach((dist) => {
    dist.series.forEach((s) => {
      s.points.forEach((pt) => {
        if (pt.executed) {
          lines.push(
            `"${dist.inputType}","${s.algorithmName}",${pt.n},${pt.averageTimeMs},${pt.averageTimeNs},${pt.averageComparisons},${pt.averageSwapsOrMoves}`
          );
        }
      });
    });
  });

  const blob = new Blob([lines.join('\n')], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'CSC2107_ModuleD_Benchmark_Results.csv';
  a.click();
  URL.revokeObjectURL(url);
}

function initModuleD() {
  document.getElementById('btn-bench-run').addEventListener('click', runBenchmarkSuite);
  document.getElementById('bench-dist-select').addEventListener('change', renderBenchmarkView);
  document.getElementById('btn-bench-csv').addEventListener('click', exportBenchmarkCsv);
}

/* ==========================================================================
 * 7. BOOTSTRAP
 * ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  initNavigation();
  initModuleA();
  initModuleB();
  initModuleC();
  initModuleD();
});
