// DOM Elements
const sidebarToggle = document.querySelector('.sidebar-toggle');
const sidebar = document.querySelector('.sidebar');
const userMenu = document.querySelector('.user-menu');
const dropdownMenu = document.querySelector('.dropdown-menu');

// Sidebar Toggle Functionality
sidebarToggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
    document.body.classList.toggle('sidebar-open');
});

// User Menu Dropdown
userMenu.addEventListener('click', (e) => {
    e.stopPropagation();
    dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none' : 'block';
});

// Close dropdown when clicking outside
document.addEventListener('click', () => {
    dropdownMenu.style.display = 'none';
});

// Menu Item Active State
const menuItems = document.querySelectorAll('.menu-item');
menuItems.forEach(item => {
    item.addEventListener('click', () => {
        menuItems.forEach(mi => mi.classList.remove('active'));
        item.classList.add('active');
    });
});

// Simulated Chart Data (for demonstration)
// In a real Spring Boot application, this would come from your REST API
const chartData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [{
        label: 'Storage Utilization %',
        data: [65, 70, 75, 80, 78, 85],
        borderColor: '#667eea',
        backgroundColor: 'rgba(102, 126, 234, 0.1)',
        tension: 0.4
    }]
};

// Initialize Chart (placeholder for Chart.js integration)
function initializeChart() {
    const canvas = document.getElementById('utilizationChart');
    if (canvas) {
        const ctx = canvas.getContext('2d');

        // Simple chart placeholder - replace with Chart.js in production
        ctx.fillStyle = '#667eea';
        ctx.fillRect(50, 150, 300, 20);
        ctx.fillStyle = '#333';
        ctx.font = '14px Arial';
        ctx.fillText('Storage Utilization Chart', 50, 30);
        ctx.fillText('(Replace with Chart.js implementation)', 50, 50);
    }
}

// API Integration Functions (for Spring Boot backend)
class StorageAPI {
    constructor(baseUrl = '/api') {
        this.baseUrl = baseUrl;
    }

    // Get dashboard statistics
    async getDashboardStats() {
        try {
            const response = await fetch(`${this.baseUrl}/dashboard/stats`);
            return await response.json();
        } catch (error) {
            console.error('Error fetching dashboard stats:', error);
            return null;
        }
    }

    // Get storage units
    async getStorageUnits(page = 0, size = 10) {
        try {
            const response = await fetch(`${this.baseUrl}/storage-units?page=${page}&size=${size}`);
            return await response.json();
        } catch (error) {
            console.error('Error fetching storage units:', error);
            return null;
        }
    }

    // Get recent activities
    async getRecentActivities(limit = 10) {
        try {
            const response = await fetch(`${this.baseUrl}/activities/recent?limit=${limit}`);
            return await response.json();
        } catch (error) {
            console.error('Error fetching recent activities:', error);
            return null;
        }
    }

    // Add new storage unit
    async addStorageUnit(unitData) {
        try {
            const response = await fetch(`${this.baseUrl}/storage-units`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(unitData)
            });
            return await response.json();
        } catch (error) {
            console.error('Error adding storage unit:', error);
            return null;
        }
    }

    // Update storage unit
    async updateStorageUnit(unitId, unitData) {
        try {
            const response = await fetch(`${this.baseUrl}/storage-units/${unitId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(unitData)
            });
            return await response.json();
        } catch (error) {
            console.error('Error updating storage unit:', error);
            return null;
        }
    }

    // Delete storage unit
    async deleteStorageUnit(unitId) {
        try {
            const response = await fetch(`${this.baseUrl}/storage-units/${unitId}`, {
                method: 'DELETE'
            });
            return response.ok;
        } catch (error) {
            console.error('Error deleting storage unit:', error);
            return false;
        }
    }
}

// Initialize API client
const api = new StorageAPI();

// Data Loading Functions
async function loadDashboardData() {
    try {
        // Load dashboard statistics
        const stats = await api.getDashboardStats();
        if (stats) {
            updateDashboardStats(stats);
        }

        // Load recent activities
        const activities = await api.getRecentActivities();
        if (activities) {
            updateRecentActivities(activities);
        }

        // Load storage units
        const storageUnits = await api.getStorageUnits();
        if (storageUnits) {
            updateStorageUnitsTable(storageUnits);
        }
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

// Update dashboard statistics
function updateDashboardStats(stats) {
    // Update stat cards with real data
    const statNumbers = document.querySelectorAll('.stat-number');
    const statChanges = document.querySelectorAll('.stat-change');

    if (stats.totalUnits) {
        statNumbers[0].textContent = stats.totalUnits;
    }
    if (stats.capacityUsed) {
        statNumbers[1].textContent = `${stats.capacityUsed}%`;
    }
    if (stats.totalItems) {
        statNumbers[2].textContent = stats.totalItems.toLocaleString();
    }
    if (stats.monthlyRevenue) {
        statNumbers[3].textContent = `$${stats.monthlyRevenue.toLocaleString()}`;
    }
}

// Update recent activities
function updateRecentActivities(activities) {
    const activityList = document.querySelector('.activity-list');
    if (activityList && activities.length > 0) {
        activityList.innerHTML = activities.map(activity => `
            <div class="activity-item">
                <div class="activity-icon ${activity.type}">
                    <i class="fas fa-${getActivityIcon(activity.type)}"></i>
                </div>
                <div class="activity-details">
                    <p><strong>${activity.title}</strong> ${activity.description}</p>
                    <span class="activity-time">${formatTime(activity.timestamp)}</span>
                </div>
            </div>
        `).join('');
    }
}

// Update storage units table
function updateStorageUnitsTable(units) {
    const tbody = document.querySelector('.data-table tbody');
    if (tbody && units.content && units.content.length > 0) {
        tbody.innerHTML = units.content.map(unit => `
            <tr>
                <td><strong>${unit.unitId}</strong></td>
                <td>${unit.location}</td>
                <td>${unit.capacity} m³</td>
                <td>
                    <div class="progress-bar">
                        <div class="progress-fill ${unit.usedPercentage > 90 ? 'warning' : ''}" 
                             style="width: ${unit.usedPercentage}%"></div>
                    </div>
                    <span>${unit.usedPercentage}%</span>
                </td>
                <td>${unit.itemCount}</td>
                <td><span class="status-badge ${unit.status.toLowerCase()}">${unit.status}</span></td>
                <td>${formatTime(unit.lastUpdated)}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-icon" onclick="viewUnit('${unit.id}')" title="View Details">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn-icon" onclick="editUnit('${unit.id}')" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn-icon danger" onclick="deleteUnit('${unit.id}')" title="Delete">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }
}

// Utility Functions
function getActivityIcon(type) {
    const icons = {
        'added': 'plus',
        'moved': 'exchange-alt',
        'alert': 'exclamation-triangle',
        'removed': 'minus'
    };
    return icons[type] || 'info';
}

function formatTime(timestamp) {
    const now = new Date();
    const time = new Date(timestamp);
    const diff = now - time;

    if (diff < 60000) return 'Just now';
    if (diff < 3600000) return `${Math.floor(diff / 60000)} minutes ago`;
    if (diff < 86400000) return `${Math.floor(diff / 3600000)} hours ago`;
    return `${Math.floor(diff / 86400000)} days ago`;
}

// Action Handlers
function viewUnit(unitId) {
    // Implement view unit details
    console.log('Viewing unit:', unitId);
    // In a real application, this would open a modal or navigate to a detail page
}

function editUnit(unitId) {
    // Implement edit unit functionality
    console.log('Editing unit:', unitId);
    // In a real application, this would open an edit form
}

async function deleteUnit(unitId) {
    if (confirm('Are you sure you want to delete this storage unit?')) {
        const success = await api.deleteStorageUnit(unitId);
        if (success) {
            // Reload the table
            const units = await api.getStorageUnits();
            if (units) {
                updateStorageUnitsTable(units);
            }
        } else {
            alert('Error deleting storage unit');
        }
    }
}

// Initialize Dashboard
document.addEventListener('DOMContentLoaded', () => {
    initializeChart();

    // Load data from Spring Boot backend
    // Comment out the line below if backend is not ready
    // loadDashboardData();

    // Auto-refresh data every 30 seconds
    setInterval(() => {
        // loadDashboardData();
    }, 30000);
});

// Search functionality
const searchInput = document.querySelector('.search-box input');
if (searchInput) {
    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        // Implement search functionality
        console.log('Searching for:', searchTerm);
    });
}

// Notification handling
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    // Add to page
    document.body.appendChild(notification);

    // Remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}