
        const getCsrf = () => ({
            token: document.querySelector('meta[name="_csrf"]')?.content,
            header: document.querySelector('meta[name="_csrf_header"]')?.content
        });

        /**
         * Standard Approval Flow
         */
        async function processApproval(requestId) {
            const docElement = document.getElementById(`doc-select-${requestId}`);
            const expiryElement = document.getElementById(`expiry-select-${requestId}`);

            if (!docElement.value) {
                alert("Please select a document from the vault first.");
                return;
            }

            // ISO date without milliseconds to match Java pattern: yyyy-MM-dd'T'HH:mm:ss
            const date = new Date();
            date.setHours(date.getHours() + parseInt(expiryElement.value));
            const formattedExpiry = date.toISOString().split('.')[0];

            const payload = {
                expiryTime: formattedExpiry,
                comment: "Approved via Patient Portal",
                documentId: parseInt(docElement.value)
            };

            const csrf = getCsrf();
            try {
                const response = await fetch(`/consents/${requestId}/approve`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrf.header]: csrf.token
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    location.reload();
                } else {
                    const errorMsg = await response.text();
                    console.error("Server Error:", errorMsg);
                    alert("Approval failed. Check console for details.");
                }
            } catch (err) {
                console.error("Network Error:", err);
            }
        }

        /**
         * Handle Rejection
         */
        async function processRejection(requestId) {
            if(!confirm("Are you sure you want to deny this access request?")) return;

            const csrf = getCsrf();
            try {
                // Since your backend uses processDecision(id, dto, false),
                // we send a PATCH to trigger the denial branch.
                const response = await fetch(`/consents/${requestId}/deny`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrf.header]: csrf.token
                    },
                    body: JSON.stringify({
                        expiryTime: new Date().toISOString().split('.')[0],
                        comment: "Denied by patient",
                        documentId: 0
                    })
                });

                if (response.ok) location.reload();
            } catch (err) {
                console.error(err);
            }
        }

        function triggerUpload(requestId) {
            document.getElementById(`file-input-${requestId}`).click();
        }

        async function handleDirectUpload(requestId) {
            const fileInput = document.getElementById(`file-input-${requestId}`);
            const hours = document.getElementById(`expiry-select-${requestId}`).value;
            if (!fileInput.files[0]) return;

            const date = new Date();
            date.setHours(date.getHours() + parseInt(hours));
            const formattedExpiry = date.toISOString().split('.')[0];

            const formData = new FormData();
            formData.append("file", fileInput.files[0]);
            formData.append("expiryTime", formattedExpiry);

            document.getElementById(`card-${requestId}`).classList.add('upload-loading');

            const csrf = getCsrf();
            try {
                const response = await fetch(`/api/consents/${requestId}/approve-with-upload`, {
                    method: 'POST',
                    headers: { [csrf.header]: csrf.token },
                    body: formData
                });
                if (response.ok) location.reload();
                else alert("Upload failed.");
            } catch (e) {
                console.error(e);
            } finally {
                document.getElementById(`card-${requestId}`).classList.remove('upload-loading');
            }
        }

