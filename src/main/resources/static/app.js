'use strict';

/**
 * ============================================================================
 *  Algorithm Explorer — Frontend Controller (Vanilla JS)
 *  Group 13 · Final DAA Project
 *  Connected exclusively to the Java Spring Boot backend (POST /api/sort).
 * ============================================================================
 */

/* ==========================================================================
 * 1. CONFIGURATION & STATIC METADATA
 * ========================================================================== */

const CONFIG = {
  API_BASE_URL: 'http://localhost:8080/api',

  AUTOPLAY_ON_RUN: true,
  AUTOPLAY_INTERVAL_MS: 750,

  MIN_ELEMENTS: 2,
  MAX_ELEMENTS: 16,
  MIN_VALUE: -999,
  MAX_VALUE: 999,
  RANDOM_SIZE: 8,
  RANDOM_MAX: 99,
};

const DEFAULTS = {
  input: '45, 12, 89, 33, 7, 21, 54, 18',
  algorithm: 'QUICK_SORT',
  dataStructure: 'CUSTOM_ARRAYLIST',
};

const ALGORITHMS = {
  BUBBLE_SORT:    { label: 'Bubble Sort',            complexity: { worst: 'O(N²)',      average: 'O(N²)',      best: 'O(N)' } },
  SELECTION_SORT: { label: 'Selection Sort',         complexity: { worst: 'O(N²)',      average: 'O(N²)',      best: 'O(N²)' } },
  INSERTION_SORT: { label: 'Insertion Sort',         complexity: { worst: 'O(N²)',      average: 'O(N²)',      best: 'O(N)' } },
  MERGE_SORT:     { label: 'Merge Sort (Recursive)', complexity: { worst: 'O(N log N)', average: 'O(N log N)', best: 'O(N log N)' } },
  QUICK_SORT:     { label: 'Quick Sort (Recursive)', complexity: { worst: 'O(N²)',      average: 'O(N log N)', best: 'O(N log N)' } },
  HEAP_SORT:      { label: 'Heap Sort',              complexity: { worst: 'O(N log N)', average: 'O(N log N)', best: 'O(N log N)' } },
};

const DATA_STRUCTURES = {
  CUSTOM_ARRAYLIST:  { label: 'Custom ArrayList',  logTitle: 'Custom ArrayList Log' },
  CUSTOM_LINKEDLIST: { label: 'Custom LinkedList', logTitle: 'Custom LinkedList Log' },
  CUSTOM_STACK:      { label: 'Custom Stack',      logTitle: 'Custom Stack/Queue Log' },
  CUSTOM_QUEUE:      { label: 'Custom Queue',      logTitle: 'Custom Stack/Queue Log' },
};

/* ==========================================================================
 * 2. TRACKING OBJECT CONTRACT (Returned by Spring Boot POST /api/sort)
 * ========================================================================== */

/**
 * @typedef {Object} Pointer
 * @property {number} index
 * @property {string} label
 */

/**
 * @typedef {Object} Step
 * @property {number[]}  array
 * @property {string}    description
 * @property {string}   [trace]
 * @property {Pointer[]} [pointers]
 * @property {number[]} [active]
 * @property {?number}  [pivot]
 * @property {number[]} [sorted]
 * @property {number}   [comparisons]
 * @property {number}   [swaps]
 * @property {?Object}  [mergeTree]
 */

/**
 * @typedef {Object} TrackingObject
 * @property {string}   algorithm
 * @property {string}   dataStructure
 * @property {number[]} input
 * @property {number[]} output
 * @property {Step[]}   steps
 * @property {{runningTimeNs: number, comparisons: number, swaps: number}} metrics
 */

/* ==========================================================================
 * 3. APPLICATION STATE & DOM REFERENCES
 * ========================================================================== */

const state = {
  /** @type {TrackingObject|null} */
  tracking: null,
  currentStep: 0,
  timerId: null,
  isBusy: false,
  cardEls: [],
  heapCardEls: [],
};

const els = {};

function cacheElements() {
  const ids = {
    input: 'input-data',
    inputError: 'input-error',
    btnRandom: 'btn-random',
    algorithm: 'algorithm-select',
    dataStructure: 'structure-select',
    btnRun: 'btn-run',
    btnResetAll: 'btn-reset-all',
    cxWorst: 'cx-worst',
    cxAverage: 'cx-average',
    cxBest: 'cx-best',
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
    slider: 'progress-slider',
    metricTime: 'metric-time',
    metricComparisons: 'metric-comparisons',
    metricSwaps: 'metric-swaps',
    traceSubtitle: 'trace-subtitle',
    traceLog: 'trace-log',
  };
  for (const [key, id] of Object.entries(ids)) {
    const el = document.getElementById(id);
    if (!el) throw new Error(`Algorithm Explorer: missing element #${id}`);
    els[key] = el;
  }
}

/* ==========================================================================
 * 4. SPRING BOOT API LAYER
 * ========================================================================== */

/**
 * Calls the Spring Boot endpoint (POST /api/sort) and returns the TrackingObject.
 *
 * @param {number[]} inputArray
 * @param {string}   algorithm
 * @param {string}   [dataStructure]
 * @returns {Promise<TrackingObject>}
 */
async function fetchApiData(inputArray, algorithm, dataStructure = DEFAULTS.dataStructure) {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 10000);

  try {
    const response = await fetch(`${CONFIG.API_BASE_URL}/sort`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({ array: inputArray, algorithm, dataStructure }),
      signal: controller.signal,
    });

    if (!response.ok) {
      let detail = '';
      try {
        const errJson = await response.json();
        detail = errJson.message || errJson.error || '';
      } catch {
        detail = await response.text().catch(() => '');
      }
      throw new Error(`Backend responded ${response.status}: ${detail || response.statusText}`);
    }

    return await response.json();
  } finally {
    clearTimeout(timeout);
  }
}

/** Validates and normalizes the TrackingObject returned by Spring Boot. */
function normalizeTracking(tracking) {
  if (!tracking || !Array.isArray(tracking.steps) || tracking.steps.length === 0) {
    throw new Error('Tracking object contains no steps.');
  }

  const steps = tracking.steps.map((step, i) => {
    if (!Array.isArray(step.array)) throw new Error(`Step ${i} is missing its "array" snapshot.`);
    return {
      array: step.array,
      description: step.description ?? '',
      trace: step.trace ?? step.description ?? '',
      pointers: step.pointers ?? [],
      active: step.active ?? [],
      pivot: step.pivot ?? null,
      sorted: step.sorted ?? [],
      comparisons: step.comparisons ?? 0,
      swaps: step.swaps ?? 0,
      mergeTree: step.mergeTree ?? null,
    };
  });

  const last = steps[steps.length - 1];
  return {
    ...tracking,
    steps,
    metrics: {
      runningTimeNs: tracking.metrics?.runningTimeNs ?? null,
      comparisons: tracking.metrics?.comparisons ?? last.comparisons,
      swaps: tracking.metrics?.swaps ?? last.swaps,
    },
  };
}

/* ==========================================================================
 * 5. RENDERING
 * ========================================================================== */

const prefersReducedMotion = () => window.matchMedia('(prefers-reduced-motion: reduce)').matches;

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

/**
 * Renders the array as cards. Existing cards are re-used (matched by value) and
 * slid to their new position with a FLIP animation, so swaps are easy to follow.
 */
function renderArray(values, step = null) {
  els.arrayRow.style.setProperty('--n', values.length);
  els.pointerRow.style.setProperty('--n', values.length);

  const firstRects = new Map(state.cardEls.map((el) => [el, el.getBoundingClientRect()]));

  const pool = new Map();
  for (const el of state.cardEls) {
    const key = el.dataset.value;
    if (!pool.has(key)) pool.set(key, []);
    pool.get(key).push(el);
  }
  const nextEls = values.map((v) => pool.get(String(v))?.shift() ?? createCard(v));

  els.arrayRow.replaceChildren(...nextEls);
  state.cardEls = nextEls;

  const active = new Set(step?.active ?? []);
  const sorted = new Set(step?.sorted ?? []);
  nextEls.forEach((el, i) => {
    el.classList.toggle('is-active', active.has(i));
    el.classList.toggle('is-pivot', step?.pivot === i);
    el.classList.toggle('is-sorted', sorted.has(i));
    el.setAttribute('aria-label', `Index ${i}: ${values[i]}`);
  });

  if (prefersReducedMotion()) return;

  nextEls.forEach((el) => {
    const first = firstRects.get(el);
    if (!first) {
      el.animate([{ opacity: 0, transform: 'translateY(6px)' }, { opacity: 1, transform: 'none' }], { duration: 220, easing: 'ease-out' });
      return;
    }
    const last = el.getBoundingClientRect();
    const dx = first.left - last.left;
    const dy = first.top - last.top;
    if (dx !== 0 || dy !== 0) {
      el.animate([{ transform: `translate(${dx}px, ${dy}px)` }, { transform: 'translate(0, 0)' }], { duration: 340, easing: 'cubic-bezier(0.2, 0.7, 0.2, 1)' });
    }
  });
}

function renderPointers(length, pointers = []) {
  const byIndex = Array.from({ length }, () => []);
  for (const p of pointers) {
    if (Number.isInteger(p.index) && p.index >= 0 && p.index < length) byIndex[p.index].push(p.label);
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

/* ---------- 5a. Tree rendering (Heap Sort & Merge Sort) ---------- */

const SVG_NS = 'http://www.w3.org/2000/svg';

function currentAlgorithm() {
  return state.tracking?.algorithm ?? els.algorithm.value;
}

/**
 * Computes the recursive interval layout for Merge Sort on [0..n-1] so each
 * subarray group [lo..hi] is positioned directly above its two child halves.
 */
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

function createEdgesSvg() {
  const svg = document.createElementNS(SVG_NS, 'svg');
  svg.setAttribute('class', 'tree-edges');
  svg.setAttribute('aria-hidden', 'true');
  return svg;
}

/**
 * Draws SVG connectors between every [data-parent-id] element and its parent
 * [data-node-id] element inside #tree-stage.
 */
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
    const pAnchor = parentEl.parentElement?.classList.contains('heap-slot')
      ? parentEl.parentElement.getBoundingClientRect()
      : pRect;
    const cRect = childEl.getBoundingClientRect();

    const x1 = pRect.left - stageRect.left + pRect.width / 2;
    const y1 = pAnchor.bottom - stageRect.top + 2;
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

/**
 * Renders the array as a complete binary heap tree (level-order indices 0..n-1).
 */
function renderHeapTree(values, step = null) {
  const n = values.length;
  const numLevels = Math.floor(Math.log2(Math.max(1, n))) + 1;
  const nodeSize = numLevels <= 3 ? '52px' : numLevels === 4 ? '46px' : '38px';
  const nodeFont = numLevels <= 3 ? '17px' : numLevels === 4 ? '15px' : '13px';

  const firstRects = new Map(state.heapCardEls.map((el) => [el, el.getBoundingClientRect()]));

  const pool = new Map();
  for (const el of state.heapCardEls) {
    const key = el.dataset.value;
    if (!pool.has(key)) pool.set(key, []);
    pool.get(key).push(el);
  }
  const nextEls = values.map((v) => {
    const card = pool.get(String(v))?.shift() ?? createCard(v);
    card.className = 'card heap-card';
    return card;
  });

  const active = new Set(step?.active ?? []);
  const sorted = new Set(step?.sorted ?? []);
  const pointersByIndex = Array.from({ length: n }, () => []);
  for (const p of step?.pointers ?? []) {
    if (Number.isInteger(p.index) && p.index >= 0 && p.index < n) {
      pointersByIndex[p.index].push(p.label);
    }
  }

  const tree = document.createElement('div');
  tree.className = 'heap-tree';
  tree.dataset.levels = String(numLevels);
  tree.style.setProperty('--node-size', nodeSize);
  tree.style.setProperty('--node-font', nodeFont);

  for (let d = 0; d < numLevels; d++) {
    const cols = 1 << d;
    const startIdx = cols - 1;
    const levelEl = document.createElement('div');
    levelEl.className = 'heap-level';
    levelEl.style.setProperty('--cols', String(cols));

    for (let pos = 0; pos < cols; pos++) {
      const idx = startIdx + pos;
      const slot = document.createElement('div');
      slot.className = 'heap-slot';

      if (idx < n) {
        const card = nextEls[idx];
        card.classList.toggle('is-active', active.has(idx));
        card.classList.toggle('is-pivot', step?.pivot === idx);
        card.classList.toggle('is-sorted', sorted.has(idx));
        card.setAttribute('aria-label', `Heap index ${idx}: ${values[idx]}`);
        card.dataset.nodeId = `heap-${idx}`;

        if (idx > 0) {
          const parentIdx = Math.floor((idx - 1) / 2);
          card.dataset.parentId = `heap-${parentIdx}`;
          if (sorted.has(idx)) {
            card.dataset.edgeState = 'detached';
          } else if (active.has(idx) && active.has(parentIdx)) {
            card.dataset.edgeState = 'active';
          } else {
            delete card.dataset.edgeState;
          }
        } else {
          delete card.dataset.parentId;
          delete card.dataset.edgeState;
        }

        const meta = document.createElement('div');
        meta.className = 'heap-meta';

        const indexTag = document.createElement('span');
        indexTag.className = 'heap-index';
        indexTag.textContent = `[${idx}]`;
        meta.appendChild(indexTag);

        for (const text of pointersByIndex[idx]) {
          const ptrTag = document.createElement('span');
          ptrTag.className = 'heap-pointer';
          ptrTag.textContent = text;
          meta.appendChild(ptrTag);
        }

        slot.append(card, meta);
      }

      levelEl.appendChild(slot);
    }

    tree.appendChild(levelEl);
  }

  els.treeStage.replaceChildren(createEdgesSvg(), tree);
  state.heapCardEls = nextEls;

  drawTreeEdges();

  if (prefersReducedMotion()) return;

  nextEls.forEach((el) => {
    const first = firstRects.get(el);
    if (!first) return;
    const last = el.getBoundingClientRect();
    const dx = first.left - last.left;
    const dy = first.top - last.top;
    if (dx !== 0 || dy !== 0) {
      el.animate(
        [{ transform: `translate(${dx}px, ${dy}px)` }, { transform: 'translate(0, 0)' }],
        { duration: 340, easing: 'cubic-bezier(0.2, 0.7, 0.2, 1)' },
      );
    }
  });
}

/**
 * Renders the recursive split/merge tree for Merge Sort using the mergeTree
 * snapshot returned by the Spring Boot MergeSort service.
 */
function renderMergeTree(values, step = null) {
  const n = values.length;
  const levels = buildMergeTreeLevels(n);
  const numLevels = levels.length;

  const cellSize = n <= 8 ? '34px' : n <= 12 ? '28px' : '22px';
  const cellFont = n <= 8 ? '13px' : n <= 12 ? '12px' : '11px';
  const colMax = n <= 8 ? '54px' : n <= 12 ? '42px' : '34px';

  const treeData = step?.mergeTree ?? null;
  let focusId = treeData?.focusId ?? `0-${n - 1}`;
  if (!treeData && step?.active?.length) {
    const lo = Math.min(...step.active);
    const hi = Math.max(...step.active);
    focusId = `${lo}-${hi}`;
  }
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
      const recorded = treeData?.nodes?.[id];
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

  els.treeStage.replaceChildren(createEdgesSvg(), tree);
  drawTreeEdges();
}

/**
 * Dispatches between the tree visualizer (Merge Sort & Heap Sort) and the
 * linear card visualizer (Bubble, Selection, Insertion, Quick Sort).
 */
function renderVisualizer(values, step = null) {
  const algo = currentAlgorithm();
  const useTree = algo === 'MERGE_SORT' || algo === 'HEAP_SORT';

  els.stage.classList.toggle('is-tree-mode', useTree);
  els.linearStage.hidden = useTree;
  els.treeStage.hidden = !useTree;

  if (useTree) {
    state.cardEls = [];
    if (algo === 'HEAP_SORT') {
      renderHeapTree(values, step);
    } else {
      state.heapCardEls = [];
      renderMergeTree(values, step);
    }
  } else {
    state.heapCardEls = [];
    renderArray(values, step);
    renderPointers(values.length, step?.pointers ?? []);
  }
}

function renderComplexity(algorithmKey) {
  const { complexity } = ALGORITHMS[algorithmKey] ?? { complexity: { worst: '—', average: '—', best: '—' } };
  els.cxWorst.textContent = complexity.worst;
  els.cxAverage.textContent = complexity.average;
  els.cxBest.textContent = complexity.best;
}

function renderTraceSubtitle(dataStructureKey) {
  els.traceSubtitle.textContent = DATA_STRUCTURES[dataStructureKey]?.logTitle ?? 'Execution Log';
}

function formatRunningTime(ns) {
  if (ns == null || Number.isNaN(ns)) return '—';
  const ms = (ns / 1e6).toFixed(3);
  return `${ms} ms (${Math.round(ns).toLocaleString('en-US')} ns)`;
}

function renderMetrics(step) {
  els.metricComparisons.textContent = step ? String(step.comparisons) : '—';
  els.metricSwaps.textContent = step ? String(step.swaps) : '—';
}

function buildTraceLog(steps) {
  const items = steps.map((step, i) => {
    const li = document.createElement('li');
    li.className = 'trace-entry';

    const index = document.createElement('span');
    index.className = 'trace-index';
    index.textContent = String(i + 1).padStart(2, '0');

    const text = document.createElement('span');
    text.textContent = step.trace;

    li.append(index, text);
    return li;
  });
  els.traceLog.replaceChildren(...items);
}

function renderTraceProgress(currentIndex) {
  const items = els.traceLog.children;
  for (let i = 0; i < items.length; i++) {
    items[i].hidden = i > currentIndex;
    items[i].classList.toggle('is-current', i === currentIndex);
  }
  els.traceLog.scrollTop = els.traceLog.scrollHeight;
}

function renderEmptyTrace(message = 'No trace yet. Press Run to start.') {
  const li = document.createElement('li');
  li.className = 'trace-empty';
  li.textContent = message;
  els.traceLog.replaceChildren(li);
}

function renderStep(index) {
  const { tracking } = state;
  if (!tracking) return;

  const total = tracking.steps.length;
  state.currentStep = Math.min(Math.max(index, 0), total - 1);
  const step = tracking.steps[state.currentStep];

  renderVisualizer(step.array, step);
  els.stepDescription.textContent = `Step ${state.currentStep + 1} of ${total}: ${step.description}`;
  renderMetrics(step);
  renderTraceProgress(state.currentStep);
  syncPlaybackControls();
}

function renderPreview(values) {
  state.tracking = null;
  state.currentStep = 0;

  renderVisualizer(values);
  els.stepDescription.textContent = `Ready: ${values.length} elements loaded. Press Run to execute on the Spring Boot server.`;
  els.metricTime.textContent = '—';
  renderMetrics(null);
  renderEmptyTrace();
  syncPlaybackControls();
}

/* ==========================================================================
 * 6. PLAYBACK CONTROLLER
 * ========================================================================== */

const lastStepIndex = () => (state.tracking ? state.tracking.steps.length - 1 : 0);
const isPlaying = () => state.timerId !== null;

function goToStep(index) {
  renderStep(index);
}

function stepForward() {
  if (state.currentStep < lastStepIndex()) goToStep(state.currentStep + 1);
}

function stepBackward() {
  if (state.currentStep > 0) goToStep(state.currentStep - 1);
}

function play() {
  if (!state.tracking || isPlaying()) return;
  if (state.currentStep >= lastStepIndex()) goToStep(0);

  state.timerId = setInterval(() => {
    stepForward();
    if (state.currentStep >= lastStepIndex()) pause();
  }, CONFIG.AUTOPLAY_INTERVAL_MS);

  syncPlaybackControls();
}

function pause() {
  if (state.timerId !== null) clearInterval(state.timerId);
  state.timerId = null;
  if (els.btnPlay) syncPlaybackControls();
}

function togglePlay() {
  isPlaying() ? pause() : play();
}

const manual = (action) => () => {
  pause();
  action();
};

function syncPlaybackControls() {
  const hasRun = state.tracking !== null;
  const last = lastStepIndex();
  const atStart = state.currentStep <= 0;
  const atEnd = state.currentStep >= last;
  const playing = isPlaying();

  els.btnFirst.disabled = !hasRun || atStart;
  els.btnPrev.disabled = !hasRun || atStart;
  els.btnNext.disabled = !hasRun || atEnd;
  els.btnLast.disabled = !hasRun || atEnd;
  els.btnRewind.disabled = !hasRun;
  els.btnPlay.disabled = !hasRun;

  els.btnPlay.classList.toggle('is-playing', playing);
  els.btnPlay.setAttribute('aria-pressed', String(playing));
  els.btnPlay.setAttribute('aria-label', playing ? 'Pause' : 'Auto-play');
  els.btnPlay.title = playing ? 'Pause (Space)' : 'Auto-play (Space)';

  els.slider.disabled = !hasRun;
  els.slider.max = String(last);
  els.slider.value = String(state.currentStep);
  const pct = last > 0 ? (state.currentStep / last) * 100 : 0;
  els.slider.style.setProperty('--progress', `${pct}%`);
}

/* ==========================================================================
 * 7. INPUT HANDLING
 * ========================================================================== */

function parseInput(raw) {
  const tokens = raw.split(/[\s,;]+/).filter(Boolean);

  if (tokens.length < CONFIG.MIN_ELEMENTS) {
    return { error: `Enter at least ${CONFIG.MIN_ELEMENTS} numbers, separated by commas.` };
  }
  if (tokens.length > CONFIG.MAX_ELEMENTS) {
    return { error: `Maximum of ${CONFIG.MAX_ELEMENTS} elements (you entered ${tokens.length}).` };
  }

  const values = [];
  for (const token of tokens) {
    if (!/^-?\d+$/.test(token)) return { error: `"${token}" is not a whole number.` };
    const value = Number(token);
    if (value < CONFIG.MIN_VALUE || value > CONFIG.MAX_VALUE) {
      return { error: `Values must be between ${CONFIG.MIN_VALUE} and ${CONFIG.MAX_VALUE}.` };
    }
    values.push(value);
  }
  return { values };
}

function showInputError(message) {
  els.inputError.textContent = message;
  els.inputError.hidden = false;
  els.input.classList.add('has-error');
  els.input.setAttribute('aria-invalid', 'true');
}

function clearInputError() {
  els.inputError.hidden = true;
  els.inputError.textContent = '';
  els.input.classList.remove('has-error');
  els.input.removeAttribute('aria-invalid');
}

function generateRandomArray(size = CONFIG.RANDOM_SIZE, max = CONFIG.RANDOM_MAX) {
  const pool = Array.from({ length: max }, (_, k) => k + 1);
  for (let i = pool.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [pool[i], pool[j]] = [pool[j], pool[i]];
  }
  return pool.slice(0, size);
}

function previewFromInput({ showErrors = false } = {}) {
  const parsed = parseInput(els.input.value);
  if (parsed.error) {
    if (showErrors) showInputError(parsed.error);
    return;
  }
  clearInputError();
  pause();
  renderPreview(parsed.values);
}

function setBusy(busy) {
  state.isBusy = busy;
  els.btnRun.disabled = busy;
  els.btnRun.textContent = busy ? 'Running…' : 'Run';
}

async function handleRun() {
  if (state.isBusy) return;

  const parsed = parseInput(els.input.value);
  if (parsed.error) {
    showInputError(parsed.error);
    return;
  }
  clearInputError();
  pause();
  setBusy(true);

  const algorithm = els.algorithm.value;
  const dataStructure = els.dataStructure.value;

  try {
    const raw = await fetchApiData(parsed.values, algorithm, dataStructure);
    const tracking = normalizeTracking(raw);

    state.tracking = tracking;
    els.metricTime.textContent = formatRunningTime(tracking.metrics.runningTimeNs);
    buildTraceLog(tracking.steps);
    renderStep(0);

    if (CONFIG.AUTOPLAY_ON_RUN) play();
  } catch (err) {
    console.error('[Algorithm Explorer] Backend request failed:', err);
    els.stepDescription.textContent = `Spring Boot server unreachable at ${CONFIG.API_BASE_URL}/sort (${err.message}). Start the Spring Boot app on port 8080 and press Run.`;
  } finally {
    setBusy(false);
  }
}

function handleResetAll() {
  pause();
  els.input.value = DEFAULTS.input;
  els.algorithm.value = DEFAULTS.algorithm;
  els.dataStructure.value = DEFAULTS.dataStructure;
  renderComplexity(DEFAULTS.algorithm);
  renderTraceSubtitle(DEFAULTS.dataStructure);
  previewFromInput();
}

function handleRandom() {
  els.input.value = generateRandomArray().join(', ');
  previewFromInput();
}

function handleKeyboard(event) {
  if (event.target.closest('input, textarea, select') || !state.tracking) return;

  const actions = {
    ArrowRight: manual(stepForward),
    ArrowLeft: manual(stepBackward),
    Home: manual(() => goToStep(0)),
    End: manual(() => goToStep(lastStepIndex())),
    ' ': togglePlay,
  };
  const action = actions[event.key];
  if (action) {
    event.preventDefault();
    action();
  }
}

/* ==========================================================================
 * 8. BOOTSTRAP / EVENT WIRING
 * ========================================================================== */

function debounce(fn, wait) {
  let id;
  return (...args) => {
    clearTimeout(id);
    id = setTimeout(() => fn(...args), wait);
  };
}

function bindEvents() {
  els.btnRun.addEventListener('click', handleRun);
  els.btnResetAll.addEventListener('click', handleResetAll);
  els.btnRandom.addEventListener('click', handleRandom);
  els.input.addEventListener('input', debounce(() => previewFromInput(), 300));
  els.input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) handleRun();
  });
  els.algorithm.addEventListener('change', () => {
    renderComplexity(els.algorithm.value);
    previewFromInput();
  });
  els.dataStructure.addEventListener('change', () => {
    renderTraceSubtitle(els.dataStructure.value);
    previewFromInput();
  });

  els.btnFirst.addEventListener('click', manual(() => goToStep(0)));
  els.btnPrev.addEventListener('click', manual(stepBackward));
  els.btnPlay.addEventListener('click', togglePlay);
  els.btnNext.addEventListener('click', manual(stepForward));
  els.btnLast.addEventListener('click', manual(() => goToStep(lastStepIndex())));
  els.btnRewind.addEventListener('click', manual(() => goToStep(0)));
  els.slider.addEventListener('input', manual(() => goToStep(Number(els.slider.value))));

  document.addEventListener('keydown', handleKeyboard);
  window.addEventListener('resize', debounce(drawTreeEdges, 60));
}

function init() {
  cacheElements();
  bindEvents();
  renderComplexity(els.algorithm.value);
  renderTraceSubtitle(els.dataStructure.value);
  previewFromInput({ showErrors: true });
}

if (typeof document !== 'undefined') {
  document.addEventListener('DOMContentLoaded', init);
}
