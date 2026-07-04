// Global State
let currentUser = null;
const API_BASE = '/api';

// DOM Elements
const authView = document.getElementById('auth-view');
const portalView = document.getElementById('portal-view');
const mainNav = document.getElementById('main-nav');
const userDisplay = document.getElementById('user-display');
const logoutBtn = document.getElementById('logout-btn');

const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const toRegister = document.getElementById('to-register');
const toLogin = document.getElementById('to-login');
const regRoleSelect = document.getElementById('reg-role');
const patientFields = document.getElementById('patient-fields');
const doctorFields = document.getElementById('doctor-fields');

// Dashboard sub-sections
const adminDashboard = document.getElementById('admin-dashboard');
const doctorDashboard = document.getElementById('doctor-dashboard');
const patientDashboard = document.getElementById('patient-dashboard');

// Init Check
document.addEventListener('DOMContentLoaded', () => {
    const savedUser = localStorage.getItem('clinic_user');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        showPortal();
    } else {
        showAuth();
    }
});

// View Toggle Helpers
function showAuth() {
    authView.style.display = 'flex';
    portalView.style.display = 'none';
    mainNav.style.display = 'none';
}

function showPortal() {
    authView.style.display = 'none';
    portalView.style.display = 'block';
    mainNav.style.display = 'flex';
    
    userDisplay.innerText = `${currentUser.username} (${currentUser.role})`;
    
    // Route dashboard view
    adminDashboard.style.display = 'none';
    doctorDashboard.style.display = 'none';
    patientDashboard.style.display = 'none';
    
    if (currentUser.role === 'ADMIN') {
        adminDashboard.style.display = 'block';
        loadAdminDashboard();
    } else if (currentUser.role === 'DOCTOR') {
        doctorDashboard.style.display = 'block';
        loadDoctorDashboard();
    } else if (currentUser.role === 'PATIENT') {
        patientDashboard.style.display = 'block';
        loadPatientDashboard();
    }
}

// Authentication Forms Event Listeners
toRegister.addEventListener('click', (e) => {
    e.preventDefault();
    document.getElementById('auth-title').innerText = 'Create Account';
    loginForm.style.display = 'none';
    registerForm.style.display = 'block';
});

toLogin.addEventListener('click', (e) => {
    e.preventDefault();
    document.getElementById('auth-title').innerText = 'Welcome Back';
    loginForm.style.display = 'block';
    registerForm.style.display = 'none';
});

regRoleSelect.addEventListener('change', () => {
    if (regRoleSelect.value === 'PATIENT') {
        patientFields.style.display = 'block';
        doctorFields.style.display = 'none';
    } else {
        patientFields.style.display = 'none';
        doctorFields.style.display = 'block';
    }
});

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/signin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        const data = await res.json();
        if (res.ok) {
            currentUser = data;
            localStorage.setItem('clinic_user', JSON.stringify(data));
            showPortal();
        } else {
            alert(data.message || 'Login failed. Invalid credentials.');
        }
    } catch (err) {
        console.error(err);
        alert('Network error connecting to backend.');
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const bodyObj = {
        username: document.getElementById('reg-username').value,
        password: document.getElementById('reg-password').value,
        email: document.getElementById('reg-email').value,
        role: regRoleSelect.value
    };

    if (bodyObj.role === 'PATIENT') {
        bodyObj.patientName = document.getElementById('patient-name').value;
        bodyObj.dateOfBirth = document.getElementById('patient-dob').value;
        bodyObj.patientPhone = document.getElementById('patient-phone').value;
        bodyObj.medicalHistorySummary = document.getElementById('patient-history').value;
    } else {
        bodyObj.doctorName = document.getElementById('doctor-name').value;
        bodyObj.specialization = document.getElementById('doctor-specialization').value;
        bodyObj.doctorPhone = document.getElementById('doctor-phone').value;
        bodyObj.availability = document.getElementById('doctor-availability').value;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(bodyObj)
        });
        
        const data = await res.json();
        if (res.ok) {
            alert('Registration successful! Please login.');
            loginForm.style.display = 'block';
            registerForm.style.display = 'none';
            document.getElementById('auth-title').innerText = 'Welcome Back';
        } else {
            alert(data.message || 'Registration failed.');
        }
    } catch (err) {
        console.error(err);
        alert('Registration error.');
    }
});

logoutBtn.addEventListener('click', () => {
    localStorage.removeItem('clinic_user');
    currentUser = null;
    showAuth();
});

// Helper for JWT Headers
function getHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${currentUser.token}`
    };
}

// ----------------------------------------------------
// Admin Console Logic
// ----------------------------------------------------
async function loadAdminDashboard() {
    try {
        // 1. Get Doctor stats from Stored Procedure
        const statsRes = await fetch(`${API_BASE}/doctors/stats`, { headers: getHeaders() });
        const stats = await statsRes.json();
        
        const statsGrid = document.getElementById('admin-stats-grid');
        statsGrid.innerHTML = '';
        stats.forEach(stat => {
            const card = document.createElement('div');
            card.className = 'stat-card';
            card.innerHTML = `
                <h4>${stat.doctor_name}</h4>
                <p style="color: var(--text-secondary); font-size:14px;">${stat.specialization}</p>
                <div class="stat-val">${stat.appointment_count} Appts</div>
            `;
            statsGrid.appendChild(card);
        });

        // 2. Load all appointments
        const apptsRes = await fetch(`${API_BASE}/appointments`, { headers: getHeaders() });
        const appts = await apptsRes.json();
        const apptBody = document.querySelector('#admin-appointments-table tbody');
        apptBody.innerHTML = '';
        appts.forEach(appt => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${new Date(appt.appointmentDate).toLocaleString()}</td>
                <td>${appt.patient.name}</td>
                <td>${appt.doctor.name}</td>
                <td>${appt.reason}</td>
                <td><span class="badge badge-${appt.status.toLowerCase()}">${appt.status}</span></td>
            `;
            apptBody.appendChild(tr);
        });

        // 3. Load all doctors
        const docsRes = await fetch(`${API_BASE}/doctors`, { headers: getHeaders() });
        const docs = await docsRes.json();
        const docBody = document.querySelector('#admin-doctors-table tbody');
        docBody.innerHTML = '';
        docs.forEach(doc => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${doc.name}</td>
                <td>${doc.specialization}</td>
            `;
            docBody.appendChild(tr);
        });

    } catch (err) {
        console.error(err);
    }
}

// ----------------------------------------------------
// Doctor Console Logic
// ----------------------------------------------------
async function loadDoctorDashboard() {
    try {
        document.getElementById('doctor-welcome-sub').innerText = `Doctor ID: ${currentUser.id} | Managing scheduled appointments and prescriptions.`;
        
        // 1. Get Doctor's Appointments
        const apptsRes = await fetch(`${API_BASE}/appointments/doctor/${currentUser.id}`, { headers: getHeaders() });
        const appts = await apptsRes.json();
        const apptBody = document.querySelector('#doctor-appointments-table tbody');
        apptBody.innerHTML = '';
        appts.forEach(appt => {
            const tr = document.createElement('tr');
            let actionBtn = '';
            if (appt.status === 'SCHEDULED') {
                actionBtn = `
                    <button class="btn-secondary" style="padding: 6px 12px; font-size:12px; margin-right:5px;" onclick="updateStatus(${appt.id}, 'COMPLETED')">Complete</button>
                    <button class="btn-secondary" style="padding: 6px 12px; font-size:12px;" onclick="openPrescriptionModal(${appt.id}, ${appt.patient.id}, '${appt.patient.name}')">Prescribe</button>
                `;
            } else {
                actionBtn = `<span style="color:var(--text-secondary); font-size:12px;">No Actions</span>`;
            }
            tr.innerHTML = `
                <td>${new Date(appt.appointmentDate).toLocaleString()}</td>
                <td>${appt.patient.name}</td>
                <td>${appt.reason}</td>
                <td><span class="badge badge-${appt.status.toLowerCase()}">${appt.status}</span></td>
                <td>${actionBtn}</td>
            `;
            apptBody.appendChild(tr);
        });

        // 2. Load prescriptions issued by doctor
        const prescRes = await fetch(`${API_BASE}/prescriptions/doctor/${currentUser.id}`, { headers: getHeaders() });
        const prescs = await prescRes.json();
        const prescBody = document.querySelector('#doctor-prescriptions-table tbody');
        prescBody.innerHTML = '';
        prescs.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${p.patientName}</td>
                <td>${p.diagnosis}</td>
                <td><button class="btn-secondary" style="padding: 6px 12px; font-size:12px;" onclick="viewPrescription('${p.id}')">View Details</button></td>
            `;
            prescBody.appendChild(tr);
        });

    } catch (err) {
        console.error(err);
    }
}

async function updateStatus(apptId, status) {
    try {
        const res = await fetch(`${API_BASE}/appointments/${apptId}/status?status=${status}`, {
            method: 'PUT',
            headers: getHeaders()
        });
        if (res.ok) {
            loadDoctorDashboard();
        }
    } catch (err) {
        console.error(err);
    }
}

// ----------------------------------------------------
// Patient Console Logic
// ----------------------------------------------------
async function loadPatientDashboard() {
    try {
        // 1. Get Patient's Appointments
        const apptsRes = await fetch(`${API_BASE}/appointments/patient/${currentUser.id}`, { headers: getHeaders() });
        const appts = await apptsRes.json();
        const apptBody = document.querySelector('#patient-appointments-table tbody');
        apptBody.innerHTML = '';
        appts.forEach(appt => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${new Date(appt.appointmentDate).toLocaleString()}</td>
                <td>${appt.doctor.name} (${appt.doctor.specialization})</td>
                <td>${appt.reason}</td>
                <td><span class="badge badge-${appt.status.toLowerCase()}">${appt.status}</span></td>
            `;
            apptBody.appendChild(tr);
        });

        // 2. Get Patient's Prescriptions
        const prescRes = await fetch(`${API_BASE}/prescriptions/patient/${currentUser.id}`, { headers: getHeaders() });
        const prescs = await prescRes.json();
        const prescBody = document.querySelector('#patient-prescriptions-table tbody');
        prescBody.innerHTML = '';
        prescs.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${p.doctorName}</td>
                <td>${p.diagnosis}</td>
                <td>${p.instructions}</td>
                <td><button class="btn-secondary" style="padding: 6px 12px; font-size:12px;" onclick="viewPrescription('${p.id}')">View</button></td>
            `;
            prescBody.appendChild(tr);
        });

        // 3. Load Specialists in dropdown
        const docsRes = await fetch(`${API_BASE}/doctors`, { headers: getHeaders() });
        const docs = await docsRes.json();
        const docSelect = document.getElementById('book-doctor');
        docSelect.innerHTML = '';
        docs.forEach(doc => {
            const opt = document.createElement('option');
            opt.value = doc.id;
            opt.innerText = `${doc.name} - ${doc.specialization}`;
            docSelect.appendChild(opt);
        });

    } catch (err) {
        console.error(err);
    }
}

// Book Appointment Form Listener
document.getElementById('book-appointment-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const doctorId = document.getElementById('book-doctor').value;
    const dateVal = document.getElementById('book-date').value;
    const reason = document.getElementById('book-reason').value;

    try {
        const res = await fetch(`${API_BASE}/appointments`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({
                patientId: currentUser.id,
                doctorId: parseInt(doctorId),
                appointmentDate: dateVal,
                reason: reason
            })
        });
        if (res.ok) {
            alert('Appointment requested successfully!');
            document.getElementById('book-appointment-form').reset();
            loadPatientDashboard();
        } else {
            alert('Failed to request appointment.');
        }
    } catch (err) {
        console.error(err);
    }
});

// ----------------------------------------------------
// Modals Functions
// ----------------------------------------------------
const prescModal = document.getElementById('prescription-modal');
const viewPrescModal = document.getElementById('view-prescription-modal');

window.openPrescriptionModal = function(apptId, patientId, patientName) {
    document.getElementById('presc-appt-id').value = apptId;
    document.getElementById('presc-pat-id').value = patientId;
    document.getElementById('presc-pat-name').value = patientName;
    prescModal.style.display = 'flex';
};

window.closePrescriptionModal = function() {
    prescModal.style.display = 'none';
    document.getElementById('prescription-form').reset();
};

document.getElementById('prescription-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const apptId = document.getElementById('presc-appt-id').value;
    const patientId = document.getElementById('presc-pat-id').value;
    const patientName = document.getElementById('presc-pat-name').value;
    
    const symptoms = document.getElementById('presc-symptoms').value.split(',').map(s => s.trim());
    const diagnosis = document.getElementById('presc-diagnosis').value;
    const instructions = document.getElementById('presc-instructions').value;
    
    const medName = document.getElementById('med-name').value;
    const medFreq = document.getElementById('med-frequency').value;
    const medDur = document.getElementById('med-duration').value;

    const prescriptionObj = {
        appointmentId: parseInt(apptId),
        patientId: parseInt(patientId),
        doctorId: currentUser.id,
        patientName: patientName,
        doctorName: currentUser.username,
        symptoms: symptoms,
        diagnosis: diagnosis,
        instructions: instructions,
        medications: [{
            name: medName,
            dosage: "",
            frequency: medFreq,
            duration: medDur
        }]
    };

    try {
        const res = await fetch(`${API_BASE}/prescriptions`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(prescriptionObj)
        });
        
        if (res.ok) {
            // Also mark appointment as completed
            await updateStatus(apptId, 'COMPLETED');
            closePrescriptionModal();
            loadDoctorDashboard();
        } else {
            alert('Failed to submit prescription.');
        }
    } catch (err) {
        console.error(err);
    }
});

window.viewPrescription = async function(id) {
    try {
        const res = await fetch(`${API_BASE}/prescriptions/${id}`, { headers: getHeaders() });
        const p = await res.json();
        
        const content = document.getElementById('prescription-detail-content');
        const medsHtml = p.medications.map(m => `
            <div style="background: rgba(255,255,255,0.03); border:1px solid var(--border-card); border-radius:10px; padding:15px; margin-top:10px;">
                <strong>${m.name}</strong><br>
                <span style="color:var(--text-secondary); font-size:14px;">Frequency: ${m.frequency} | Duration: ${m.duration}</span>
            </div>
        `).join('');

        content.innerHTML = `
            <p><strong>Doctor:</strong> ${p.doctorName}</p>
            <p><strong>Patient:</strong> ${p.patientName}</p>
            <p><strong>Date:</strong> ${new Date(p.prescriptionDate).toLocaleDateString()}</p>
            <p><strong>Symptoms:</strong> ${p.symptoms.join(', ')}</p>
            <p><strong>Diagnosis:</strong> ${p.diagnosis}</p>
            <h4 style="margin-top:20px;">Prescribed Medications</h4>
            ${medsHtml}
            <h4 style="margin-top:20px;">Instructions</h4>
            <p style="color: var(--text-secondary);">${p.instructions}</p>
        `;
        
        viewPrescModal.style.display = 'flex';
    } catch (err) {
        console.error(err);
    }
};

window.closeViewPrescriptionModal = function() {
    viewPrescModal.style.display = 'none';
};
