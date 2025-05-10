// Sidebar tab logic with caching for static and dynamic tabs
const tabContentCache = {};
const dynamicLogoCache = {};

function setSidebarLogo(html) {
  document.getElementById('sidebar-logo').innerHTML = html;
  dynamicLogoCache['logo'] = html;
}

function loadSidebarLogo() {
  // Example: fetch logo HTML dynamically (customize as needed)
  if (dynamicLogoCache['logo']) {
    setSidebarLogo(dynamicLogoCache['logo']);
    return;
  }
  fetch('/datasource/sidebar_logo.html')
    .then(r => r.text())
    .then(html => setSidebarLogo(html))
    .catch(() => setSidebarLogo('<span>My App</span>'));
}

function loadTabContent(tabKey, url, isDynamic = false) {
  const contentArea = document.getElementById('main-content');
  if (tabContentCache[tabKey]) {
    contentArea.innerHTML = tabContentCache[tabKey];
    return;
  }
  fetch(url)
    .then(r => r.text())
    .then(html => {
      tabContentCache[tabKey] = html;
      contentArea.innerHTML = html;
    })
    .catch(() => {
      contentArea.innerHTML = '<div class="alert alert-danger">Failed to load content.</div>';
    });
}

function activateTab(tabKey, url, isDynamic = false) {
  document.querySelectorAll('.sidebar-tab').forEach(tab => tab.classList.remove('active'));
  const tab = document.querySelector(`.sidebar-tab[data-tab="${tabKey}"]`);
  if (tab) tab.classList.add('active');
  loadTabContent(tabKey, url, isDynamic);
}

function addDynamicTab(tabKey, label, iconClass, url) {
  if (document.querySelector(`.sidebar-tab[data-tab="${tabKey}"]`)) return;
  const li = document.createElement('li');
  li.className = 'sidebar-tab dynamic-tab';
  li.setAttribute('data-tab', tabKey);
  li.innerHTML = `<i class="${iconClass}"></i> ${label}`;
  li.onclick = () => activateTab(tabKey, url, true);
  document.getElementById('sidebar-tabs').appendChild(li);
}

document.addEventListener('DOMContentLoaded', function() {
  loadSidebarLogo();
  // Static tab URLs (customize as needed)
  const staticTabUrls = {
    dashboard: '/datasource/dashboard.html',
    search: '/datasource/search.html',
    navigator: '/datasource/navigator.html',
    creation: '/datasource/creation.html',
    utilities: '/datasource/utilities.html',
    map: '/datasource/map.html'
  };
  document.querySelectorAll('.sidebar-tab.static-tab').forEach(tab => {
    tab.onclick = function() {
      const tabKey = this.getAttribute('data-tab');
      activateTab(tabKey, staticTabUrls[tabKey]);
    };
  });
  // Load default tab (dashboard)
  activateTab('dashboard', staticTabUrls['dashboard']);
});

// Example usage for dynamic tabs (call from elsewhere):
// addDynamicTab('blueprint', 'Blueprint', 'fas fa-project-diagram', '/datasource/blueprint_builder.html');
