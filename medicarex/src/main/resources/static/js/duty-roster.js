/**
 * Duty Roster Auto-Update Logic
 */
async function updateRoster() {
    const container = document.getElementById('dutyRosterContainer');
    const badge = document.getElementById('onlineCountBadge');

    if (!container || !badge) return;

    try {
        const response = await fetch('/admin/providers/active');

        // If API returns 401, 403, or 500, throw error to trigger catch block
        if (!response.ok) throw new Error('HTTP error! status: ' + response.status);

        const staffList = await response.json();

        // 1. Update Badge Count (Fixes "undefined Online")
        const count = Array.isArray(staffList) ? staffList.length : 0;
        badge.textContent = `${count} Online`;

        // 2. Clear Container
        container.innerHTML = '';

        if (count === 0) {
            container.innerHTML = '<div class="text-center py-4 small text-muted">No staff currently active</div>';
            return;
        }

        // 3. Render Rows (Fixes "Unable to load roster")
        staffList.forEach(staff => {
            // Safety checks for names
            const fName = staff.firstName || 'Staff';
            const lName = staff.lastName || '';
            const initials = (fName[0] + (lName ? lName[0] : '')).toUpperCase();

            // Generate a consistent color based on initials
            const colors = ['bg-primary', 'bg-info', 'bg-secondary', 'bg-dark'];
            const colorClass = colors[initials.charCodeAt(0) % colors.length];

            const rowHTML = `
                <div class="attendance-row staff-item">
                    <div class="position-relative">
                        <div class="staff-avatar ${colorClass}">${initials}</div>
                        <span class="status-badge bg-success"></span>
                    </div>
                    <div class="flex-grow-1">
                        <span class="d-block fw-bold text-dark small staff-name">${fName} ${lName}</span>
                        <small class="text-muted">In-Duty: ${staff.inDutyTime || '09:00 AM'}</small>
                    </div>
                </div>
            `;
            container.insertAdjacentHTML('beforeend', rowHTML);
        });
    } catch (error) {
        console.error("Roster Update Failed:", error);
        badge.textContent = "0 Online";
        container.innerHTML = `
            <div class="text-center py-4">
                <p class="small text-danger mb-0">Unable to load roster</p>
                <small class="text-muted" style="font-size: 10px;">Check API Connection</small>
            </div>`;
    }
}

// Initialize on load
document.addEventListener('DOMContentLoaded', () => {
    updateRoster();
    setInterval(updateRoster, 60000); // Auto-refresh every minute
});

function getAvatarColor(initials) {
    const colors = ['bg-primary', 'bg-secondary', 'bg-info', 'bg-dark', 'bg-success', 'bg-danger'];
    const charCode = initials.charCodeAt(0) + initials.charCodeAt(1);
    return colors[charCode % colors.length];
}