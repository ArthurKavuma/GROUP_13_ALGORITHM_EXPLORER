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