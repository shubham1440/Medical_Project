/**
 * GlobalViewer: Standalone Module
 * Encapsulates all PDF.js and Note-saving logic
 */
const GlobalViewer = (function() {
    let pdfDoc = null;
    let pageNum = 1;
    let activeId = null;
    let activeNotes = "";
    let isRendering = false;
    let modalInstance = null;

    function init() {
        const modalEl = document.getElementById('previewModal');
        if (!modalEl) return;

        modalInstance = new bootstrap.Modal(modalEl);

        // UI Listeners
        document.getElementById('prevPage').onclick = () => changePage(-1);
        document.getElementById('nextPage').onclick = () => changePage(1);
        document.getElementById('saveNotesBtn').onclick = saveNotes;

        // Bridge Logic: Listen for any clicks on buttons with .open-viewer-btn
        document.body.addEventListener('click', function(e) {
            const btn = e.target.closest('.open-viewer-btn');
            if (btn) {
                // Map the data attributes from your Thymeleaf button
                const id = btn.dataset.docId;
                const patient = btn.dataset.patientName || "Unknown Patient";
                const notes = btn.dataset.docNotes || "";
                const title = `Medical Record: ${patient}`;

                open(id, title, notes);
            }
        });

        modalEl.addEventListener('shown.bs.modal', () => renderPage(pageNum));

        modalEl.addEventListener('hidden.bs.modal', () => {
            pdfDoc = null;
            pageNum = 1;
            const canvas = document.getElementById('pdf-render');
            const ctx = canvas.getContext('2d');
            ctx.clearRect(0, 0, canvas.width, canvas.height);
        });
    }

    async function open(id, title, notes) {
        activeId = id;
        activeNotes = notes || "No prior history.";
        pageNum = 1;

        document.getElementById('previewTitle').innerText = title;
        document.getElementById('previousNotesDisplay').innerText = activeNotes;
        document.getElementById('clinicalNotesInput').value = "";

        try {
            // Use the PDF.js library to load document
            const loadingTask = pdfjsLib.getDocument(`/documents/${id}`);
            pdfDoc = await loadingTask.promise;
            document.getElementById('pageCount').innerText = pdfDoc.numPages;
            modalInstance.show();
        } catch (err) {
            console.error("GlobalViewer Load Error:", err);
            alert("Security: Could not retrieve document.");
        }
    }

//    async function renderPage(num) {
//        if (isRendering || !pdfDoc) return;
//        isRendering = true;
//
//        const page = await pdfDoc.getPage(num);
//        const canvas = document.getElementById('pdf-render');
//        const container = document.getElementById('pdf-viewer-container');
//        const ctx = canvas.getContext('2d');
//
//        const unscaledViewport = page.getViewport({ scale: 1 });
//        const scale = (container.clientWidth - 50) / unscaledViewport.width;
//        const viewport = page.getViewport({ scale: scale });
//
//        canvas.height = viewport.height;
//        canvas.width = viewport.width;
//
//        await page.render({ canvasContext: ctx, viewport: viewport }).promise;
//        document.getElementById('pageNum').innerText = num;
//        isRendering = false;
//    }
    async function renderPage(num) {
        if (isRendering || !pdfDoc) return;
        isRendering = true;

        const page = await pdfDoc.getPage(num);
        const canvas = document.getElementById('pdf-render');
        const container = document.getElementById('pdf-viewer-container');
        const ctx = canvas.getContext('2d');

        // Scale logic: Use container width to determine scale
        // This allows the height to be dynamic (scrollable)
        const unscaledViewport = page.getViewport({ scale: 1 });
        const scale = (container.clientWidth - 40) / unscaledViewport.width;
        const viewport = page.getViewport({ scale: scale });

        canvas.height = viewport.height;
        canvas.width = viewport.width;

        const renderContext = {
            canvasContext: ctx,
            viewport: viewport
        };

        await page.render(renderContext).promise;
        document.getElementById('pageNum').innerText = num;
        isRendering = false;
    }

    function changePage(delta) {
        if (pdfDoc && (pageNum + delta > 0) && (pageNum + delta <= pdfDoc.numPages)) {
            pageNum += delta;
            renderPage(pageNum);
        }
    }

    async function saveNotes() {
        const inputField = document.getElementById('clinicalNotesInput');
        const newNote = inputField.value.trim();
        if (!newNote) return;

        // Create a structured chain entry
        const timestamp = new Date().toLocaleString();
        const updatedChain = activeNotes + `\n\n[Update ${timestamp}]:\n${newNote}`;

        const saveBtn = document.getElementById('saveNotesBtn');
        saveBtn.disabled = true;

        try {
            const response = await fetch(`/documents/update-notes/${activeId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ notes: updatedChain })
            });

            if (response.ok) {
                activeNotes = updatedChain;
                document.getElementById('previousNotesDisplay').innerText = updatedChain;
                inputField.value = "";
                const status = document.getElementById('saveStatus');
                status.classList.remove('d-none');
                setTimeout(() => status.classList.add('d-none'), 3000);
            }
        } catch (err) {
            alert("Error saving to record.");
        } finally {
            saveBtn.disabled = false;
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    return { open };
})();