/**
 * HVV1.js - Health Vault Viewer (Clinical Terminal Logic)
 * Updated to handle Side-by-Side Document & Notes Analysis
 */

//document.addEventListener('DOMContentLoaded', function() {
//    // 1. SELECTORS & INITIALIZATION
//    const drawer = new bootstrap.Offcanvas(document.getElementById('documentDrawer'));
//    const previewModalEl = document.getElementById('previewModal');
//    const previewModal = new bootstrap.Modal(previewModalEl);
//    const pdfRenderCanvas = document.getElementById('pdf-render');
//    const pdfContainer = document.getElementById('pdf-viewer-container');
//    const clinicalNotesContent = document.getElementById('clinicalNotesContent');
//    const popPanelBtn = document.getElementById('popPanelBtn');
//
//    const notesInput = document.getElementById('clinicalNotesInput');
//    const saveNotesBtn = document.getElementById('saveNotesBtn');
//    const saveStatus = document.getElementById('saveStatus');
//
//
//    // PDF.js Library Setup
//    const pdfjsLib = window['pdfjs-dist/build/pdf'];
//    pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.4.120/pdf.worker.min.js';
//
//    let activeId = null;
//    let activeNotes = "";
//    let pdfDoc = null;
//    let pageNum = 1;
//    let isRendering = false;
//
//    // 2. OPEN DRAWER ACTION
//    document.querySelectorAll('.view-details-btn').forEach(btn => {
//        btn.addEventListener('click', function() {
//            activeId = this.dataset.id;
//            activeNotes = this.dataset.notes || "No clinical remarks available.";
//            document.getElementById('drawerTitle').innerText = this.dataset.title;
//            drawer.show();
//        });
//    });
//
//    // 3. CORE RENDERING ENGINE (FIT-TO-WIDTH)
//    async function renderPage(num) {
//        if (isRendering || !pdfDoc) return;
//        isRendering = true;
//
//        try {
//            const page = await pdfDoc.getPage(num);
//
//            // 1. Get the viewport at a standard scale (1.5 is good for clarity)
//            const unscaledViewport = page.getViewport({ scale: 1.5 });
//
//            // 2. Calculate the exact scale needed to fit the container width
//            // we subtract 60px to account for padding and scrollbars
//            const containerWidth = pdfContainer.clientWidth - 60;
//            const scale = containerWidth / (unscaledViewport.width / 1.5);
//
//            const viewport = page.getViewport({ scale: scale });
//
//            // 3. CRITICAL: Set the internal canvas resolution
//            // This MUST match the viewport height exactly to show the whole doc
//            pdfRenderCanvas.height = viewport.height;
//            pdfRenderCanvas.width = viewport.width;
//
//            // 4. Force CSS to match so it doesn't "stretch" or "shrink"
//            pdfRenderCanvas.style.height = viewport.height + "px";
//            pdfRenderCanvas.style.width = viewport.width + "px";
//
//            const renderContext = {
//                canvasContext: pdfRenderCanvas.getContext('2d'),
//                viewport: viewport
//            };
//
//            // 5. Render and wait for completion
//            await page.render(renderContext).promise;
//
//            document.getElementById('pageNum').innerText = num;
//            isRendering = false;
//
//            console.log("Rendered Height: " + viewport.height); // Check console to see if this is > 1000
//
//        } catch (error) {
//            console.error("Render Error:", error);
//            isRendering = false;
//        }
//    }
//
//    // 4. TRIGGER DOCUMENT VIEWING
//    if (popPanelBtn) {
//        popPanelBtn.addEventListener('click', async function() {
//            if (!activeId) return;
//
//            // Clear previous session
//            const ctx = pdfRenderCanvas.getContext('2d');
//            ctx.clearRect(0, 0, pdfRenderCanvas.width, pdfRenderCanvas.height);
//            clinicalNotesContent.innerText = "Initializing secure decryption...";
//
//            try {
//                const loadingTask = pdfjsLib.getDocument(`/documents/${activeId}`);
//                pdfDoc = await loadingTask.promise;
//
//                document.getElementById('pageCount').innerText = pdfDoc.numPages;
//                pageNum = 1;
//
//                drawer.hide();
//                // Delay showing modal slightly to avoid ARIA focus conflicts
//                setTimeout(() => previewModal.show(), 150);
//
//            } catch (err) {
//                console.error("Load Error:", err);
//                clinicalNotesContent.innerText = "Error: Security handshake failed.";
//            }
//        });
//    }
//
//    // 5. MODAL EVENT: RENDER ONLY WHEN FULLY SHOWN
//    previewModalEl.addEventListener('shown.bs.modal', function () {
//        renderPage(pageNum);
//        clinicalNotesContent.innerText = activeNotes;
//    });
//
//    // 6. PAGE NAVIGATION
//    document.getElementById('prevPage')?.addEventListener('click', () => {
//        if (pdfDoc && pageNum > 1) { pageNum--; renderPage(pageNum); }
//    });
//
//    document.getElementById('nextPage')?.addEventListener('click', () => {
//        if (pdfDoc && pageNum < pdfDoc.numPages) { pageNum++; renderPage(pageNum); }
//    });
//});
//


document.addEventListener('DOMContentLoaded', function() {
    // --- 1. INITIALIZE BOOTSTRAP COMPONENTS ---
    const drawerEl = document.getElementById('documentDrawer');
    const previewModalEl = document.getElementById('previewModal');
    const drawer = new bootstrap.Offcanvas(drawerEl);
    const previewModal = new bootstrap.Modal(previewModalEl);

    // --- 2. ELEMENT SELECTORS ---
    const pdfRenderCanvas = document.getElementById('pdf-render');
    const pdfContainer = document.getElementById('pdf-viewer-container');
    const notesInput = document.getElementById('clinicalNotesInput');
    const saveNotesBtn = document.getElementById('saveNotesBtn');
    const saveStatus = document.getElementById('saveStatus');
    const popPanelBtn = document.getElementById('popPanelBtn');

    // PDF.js Setup
    const pdfjsLib = window['pdfjs-dist/build/pdf'];
    pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.4.120/pdf.worker.min.js';

    let activeId = null;
    let activeNotes = "";
    let pdfDoc = null;
    let pageNum = 1;
    let isRendering = false;

    // --- 3. TABLE INTERACTION ---
    document.querySelectorAll('.view-details-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            activeId = this.dataset.id;
            activeNotes = this.dataset.notes || "";
            document.getElementById('drawerTitle').innerText = this.dataset.title;
            document.getElementById('drawerSize').innerText = this.dataset.size;
            drawer.show();
        });
    });

    // --- 4. CORE PDF RENDERING ENGINE (The Fix is here) ---
    async function renderPage(num) {
        if (isRendering || !pdfDoc) return;
        isRendering = true;

        try {
            const page = await pdfDoc.getPage(num);
            const ctx = pdfRenderCanvas.getContext('2d');

            // FIX: Use getViewport instead of getPageViewport
            const unscaledViewport = page.getViewport({ scale: 1 });

            // Calculate scale based on the current width of the panel
            const containerWidth = pdfContainer.clientWidth - 40;
            const scale = containerWidth / unscaledViewport.width;

            const viewport = page.getViewport({ scale: scale });

            // Set internal canvas resolution to match document pixels
            pdfRenderCanvas.height = viewport.height;
            pdfRenderCanvas.width = viewport.width;

            // Optional: Match CSS style to avoid blurring
            pdfRenderCanvas.style.width = viewport.width + "px";
            pdfRenderCanvas.style.height = viewport.height + "px";

            const renderContext = {
                canvasContext: ctx,
                viewport: viewport
            };

            await page.render(renderContext).promise;
            document.getElementById('pageNum').innerText = num;
            isRendering = false;
        } catch (error) {
            console.error("PDF Render Error:", error);
            isRendering = false;
        }
    }

    // --- 5. OPEN VIEWER ---
    if (popPanelBtn) {
        popPanelBtn.addEventListener('click', async function() {
            if (!activeId) return;

            const url = `/documents/${activeId}`;
            // Use .value for the textarea input block
            notesInput.value = "Decrypting secure records...";

            try {
                const loadingTask = pdfjsLib.getDocument(url);
                pdfDoc = await loadingTask.promise;

                document.getElementById('pageCount').innerText = pdfDoc.numPages;
                pageNum = 1;

                drawer.hide();
                setTimeout(() => previewModal.show(), 150);

            } catch (err) {
                console.error("PDF Load Error:", err);
                notesInput.value = "Error: Could not load the clinical document.";
            }
        });
    }

    previewModalEl.addEventListener('shown.bs.modal', function () {
        if (pdfDoc) {
            renderPage(pageNum);
            notesInput.value = activeNotes;
            document.getElementById('previewTitle').innerText = document.getElementById('drawerTitle').innerText;
        }
    });

    // --- 6. SAVE NOTES LOGIC ---
    if (saveNotesBtn) {
        saveNotesBtn.addEventListener('click', async function() {
            const updatedNotes = notesInput.value;
            saveNotesBtn.disabled = true;
            const originalHTML = saveNotesBtn.innerHTML;
            saveNotesBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Saving...';

            try {
                // If your backend uses @RequestParam, use URLSearchParams
                // If it uses @RequestBody, use JSON.stringify
                const response = await fetch(`/documents/update-notes/${activeId}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ notes: updatedNotes })
                });

                if (response.ok) {
                    activeNotes = updatedNotes;
                    saveStatus.classList.remove('d-none');
                    setTimeout(() => saveStatus.classList.add('d-none'), 3000);
                } else {
                    alert("Unable to save. Status: " + response.status);
                }
            } catch (err) {
                console.error("Save Error:", err);
            } finally {
                saveNotesBtn.disabled = false;
                saveNotesBtn.innerHTML = originalHTML;
            }
        });
    }

    // --- 7. NAVIGATION ---
    document.getElementById('prevPage')?.addEventListener('click', () => {
        if (pdfDoc && pageNum > 1) { pageNum--; renderPage(pageNum); }
    });

    document.getElementById('nextPage')?.addEventListener('click', () => {
        if (pdfDoc && pageNum < pdfDoc.numPages) { pageNum++; renderPage(pageNum); }
    });
});