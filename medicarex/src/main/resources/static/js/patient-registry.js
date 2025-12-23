async function updatePatientRegistry() {
    const container = document.getElementById('patientRegistryContainer');
    if (!container) return;

    try {
        // MATCH THE CONTROLLER PATH EXACTLY
        const response = await fetch('/admin/patients/registry');

        if (!response.ok) {
            console.error('Server responded with status:', response.status);
            throw new Error('Server Error');
        }

        const patients = await response.json();
        container.innerHTML = '';

        if (!patients || patients.length === 0) {
            container.innerHTML = '<div class="text-center py-4 small text-muted">No records found</div>';
            return;
        }

        patients.forEach(patient => {
            // Safety check for fields to prevent 'undefined'
            const fullName = patient.fullName || 'Unknown Patient';
            const id = patient.id || 'N/A';
            const info = patient.info || 'General Registry';

            container.insertAdjacentHTML('beforeend', `
                <div class="patient-row patient-item">
                    <i class="bi bi-person-circle fs-5 text-muted"></i>
                    <div class="flex-grow-1">
                        <span class="d-block fw-bold text-dark small patient-name">
                            ${fullName} (ID: ${id})
                        </span>
                        <small class="text-muted">${info}</small>
                    </div>
                </div>
            `);
        });
    } catch (error) {
        console.error('Fetch error:', error);
        container.innerHTML = `
            <div class="text-center py-4">
                <span class="text-danger small d-block">Unable to load registry</span>
                <button class="btn btn-sm btn-link text-decoration-none" onclick="updatePatientRegistry()">Retry Now</button>
            </div>`;
    }
}

document.addEventListener('DOMContentLoaded', updatePatientRegistry);