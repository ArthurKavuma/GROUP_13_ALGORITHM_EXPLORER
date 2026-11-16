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
/* ---------- 5a. Tree rendering (Heap Sort & Merge Sort) ---------- */

const SVG_NS = 'http://www.w3.org/2000/svg';

function currentAlgorithm() {
  return state.tracking?.algorithm ?? els.algorithm.value;
}

/**
 * Computes the recursive interval layout for Merge Sort on [0..n-1].
 */
function buildMergeTreeLevels(n) {
  const levels = [];

  const visit = (lo, hi, depth, parentId = null) => {
    if (!levels[depth]) levels[depth] = [];

    const id = `${1o}-${hi}`;
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
 * Draws SVG connectors between every child and its parent.
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

    path.setAttribute(
      'd',
      `M ${x1.toFixed(1)} ${y1.toFixed(1)} L ${x2.toFixed(1)} ${y2.toFixed(1)}`
    );

    let cls = 'tree-edge';

    if (childEl.dataset.edgeState === 'active') cls += ' is-active';
    if (childEl.dataset.edgeState === 'detached') cls += ' is-detached';

    path.setAttribute('class', cls);
    paths.push(path);
  }

  svg.replaceChildren(...paths);
}

/**
 * Renders the array as a complete binary heap tree.
 */
function renderHeapTree(values, step = null) {
  const n = values.length;

  const numLevels = Math.floor(Math.log2(Math.max(1, n))) + 1;

  const nodeSize = numLevels <= 3 ? '52px'
    : numLevels === 4 ? '46px' : '38px';

  const nodeFont = numLevels <= 3 ? '17px'
    : numLevels === 4 ? '15px' : '13px';

  const firstRects = new Map(
    state.heapCardEls.map(el => [el, el.getBoundingClientRect()])
  );

  const pool = new Map();

  for (const el of state.heapCardEls) {
    const key = el.dataset.value;
    if (!pool.has(key)) pool.set(key, []);
    pool.get(key).push(el);
  }

  const nextEls = values.map(v => {
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

        card.setAttribute(
          'aria-label',
          `Heap index ${idx}: ${values[idx]}`
        );

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

  nextEls.forEach(el => {
    const first = firstRects.get(el);
    if (!first) return;

    const last = el.getBoundingClientRect();

    const dx = first.left - last.left;
    const dy = first.top - last.top;

    if (dx !== 0 || dy !== 0) {
      el.animate(
        [
          { transform: `translate(${dx}px, ${dy}px)` },
          { transform: 'translate(0, 0)' }
        ],
        {
          duration: 340,
          easing: 'cubic-bezier(0.2, 0.7, 0.2, 1)'
        }
      );
    }
  });
}