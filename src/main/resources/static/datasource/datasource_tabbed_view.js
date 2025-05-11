// Module-level initialization flag
let isInitialized = false;
let isInitializingButtons = false;
let buttonInitTimeout = null;

// Store form values
const formState = {
    _state: new Map(),
    _formElements: ['input', 'select', 'textarea'],
    
    // Save all form elements in a tab
    save: function(tabId) {
        logDebug(`💾 Saving form state for tab: ${tabId}`);
        const mainContent = document.querySelector('.col-md-11 .flex-grow-1');
        if (!mainContent) return;

        const state = {};
        this._formElements.forEach(elementType => {
            const elements = mainContent.querySelectorAll(elementType);
            elements.forEach((el, index) => {
                if (el.id || el.name) {
                    const key = el.id || el.name;
                    state[key] = {
                        value: el.value,
                        type: el.type || elementType
                    };
                    logDebug(`📝 Saved ${key}:`, { value: el.value, type: el.type || elementType });
                }
            });
        });
        
        if (Object.keys(state).length > 0) {
            this._state.set(tabId, state);
            logDebug(`✅ Saved state for ${Object.keys(state).length} elements in tab: ${tabId}`);
        }
    },

    // Restore all form elements in a tab
    restore: function(tabId) {
        const state = this._state.get(tabId);
        if (!state) {
            logDebug(`ℹ️ No saved state found for tab: ${tabId}`);
            return;
        }

        logDebug(`📤 Restoring form state for tab: ${tabId}`);
        
        // Wait for DOM to be ready
        const tryRestore = (retries = 0) => {
            const mainContent = document.querySelector('.col-md-11 .flex-grow-1');
            if (!mainContent) {
                if (retries < 5) {
                    logDebug(`⏳ Main content not ready, retrying... (${retries + 1}/5)`);
                    setTimeout(() => tryRestore(retries + 1), 200);
                }
                return;
            }

            Object.entries(state).forEach(([key, data]) => {
                const element = mainContent.querySelector(`#${key}`) || mainContent.querySelector(`[name="${key}"]`);
                if (element) {
                    element.value = data.value;
                    logDebug(`✅ Restored ${key}:`, { value: data.value, type: data.type });
                    
                    // Trigger change event for any listeners
                    const event = new Event('change', { bubbles: true });
                    element.dispatchEvent(event);
                } else {
                    logDebug(`⚠️ Element not found: ${key}`);
                }
            });
        };

        // Start restoration attempt
        tryRestore();
    },

    // Clear state for a tab
    clear: function(tabId) {
        if (this._state.has(tabId)) {
            this._state.delete(tabId);
            logDebug(`🗑️ Cleared form state for tab: ${tabId}`);
        }
    }
};

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Tab URLs and cache are now module-scoped
const staticTabUrls = {
  dashboard: '/datasource/dashboard.html',
  search: '/datasource/search.html',
  navigator: '/datasource/navigator.html',
  creation: '/datasource/creation.html',
  utilities: '/datasource/utilities.html',
  profile: '/datasource/profile.html',
  map: '/datasource/map.html'
};

// Debug logging function (disabled)
function logDebug(message, data = '') {
    // Logging disabled
}

// Tab content cache with logging wrapper
const tabContentCache = {
    _cache: new Map(),
    set: function(key, value) {
        logDebug(`📥 Caching content for tab: ${key}`, { size: value.length });
        this._cache.set(key, value);
    },
    get: function(key) {
        const hit = this._cache.has(key);
        logDebug(`🔍 Cache ${hit ? 'HIT' : 'MISS'} for tab: ${key}`);
        return this._cache.get(key);
    },
    has: function(key) {
        return this._cache.has(key);
    }
};

// Initialize utilities tab buttons
function initUtilitiesTabHoverButtons() {
    if (isInitializingButtons) {
        logDebug('🚫 Button initialization already in progress, skipping');
        return;
    }

    isInitializingButtons = true;
    logDebug('🔨 Initializing utilities tab buttons');
    
    try {
        // Remove any custom buttons and existing open-in-new-tab buttons
        const existingCustomButtons = document.querySelectorAll('.plus-btn-wrapper, .open-in-new-tab-btn');
        logDebug(`🧹 Removing ${existingCustomButtons.length} existing buttons`);
        existingCustomButtons.forEach(btn => btn.remove());

        // Add hover buttons to config items
        const configItems = document.querySelectorAll('.config-item');
        logDebug(`🔍 Found ${configItems.length} config items`);
        
        configItems.forEach(function(item, index) {
            if (item.classList.contains('blueprint-config-item')) {
                logDebug('⏭️ Skipping blueprint config item');
                return;
            }

            // Get the icon class from the existing item
            const existingIcon = item.querySelector('i.fas, i.far, i.fab');
            const iconClass = existingIcon ? existingIcon.className : 'fas fa-cog';

            // Ensure the item can position children properly
            item.style.position = 'relative';
            item.style.overflow = 'visible';

            if (!item.querySelector('.plus-btn-wrapper')) {
                const btnWrapper = document.createElement('div');
                btnWrapper.className = 'plus-btn-wrapper';
                btnWrapper.style.cssText = `
                    position: absolute;
                    right: 6px;
                    top: 50%;
                    transform: translateY(-50%);
                    z-index: 100;
                    display: none;
                    width: 24px;
                    height: 24px;
                    pointer-events: none;
                `;

                const btn = document.createElement('button');
                btn.className = 'item-plus-btn';
                btn.title = 'Open in new tab';
                btn.style.cssText = `
                    width: 100%;
                    height: 100%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    background: none;
                    border: none;
                    color: #007bff;
                    font-size: 1.1rem;
                    cursor: pointer;
                    pointer-events: all;
                    padding: 0;
                    margin: 0;
                    transition: transform 0.1s ease;
                `;
                
                const icon = document.createElement('i');
                icon.className = 'fas fa-external-link-alt';
                btn.appendChild(icon);
                btnWrapper.appendChild(btn);

                // Insert button before any text nodes to avoid overlapping
                const firstChild = item.firstChild;
                item.insertBefore(btnWrapper, firstChild);

                // Enhanced hover handling with wrapper
                const showButton = () => {
                    logDebug(`👁️ Showing button for item ${index}`);
                    btnWrapper.style.display = 'block';
                };
                const hideButton = () => {
                    logDebug(`🙈 Hiding button for item ${index}`);
                    btnWrapper.style.display = 'none';
                };

                // Add hover effect to button
                btn.addEventListener('mouseenter', () => {
                    btn.style.transform = 'scale(1.1)';
                });
                btn.addEventListener('mouseleave', () => {
                    btn.style.transform = 'scale(1)';
                });

                item.addEventListener('mouseenter', showButton);
                item.addEventListener('mouseleave', hideButton);                btn.onclick = function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    window.lastPlusBtnClicked = item;

                    // Specifically target the config item's main icon, excluding any button icons
                    const label = item.querySelector('span')?.innerText.trim() || item.innerText.trim();
                    
                    // Get the first icon that's a direct child of the item and not inside a button
                    const icons = Array.from(item.children).filter(child => 
                        child.tagName.toLowerCase() === 'i' && 
                        !child.closest('button')
                    );
                    const configIcon = icons[0];
                    
                    const iconClass = configIcon ? configIcon.className : 'fas fa-cog';
                    openSidebarTab(label, iconClass);
                };
                
                logDebug(`✅ Added button to item ${index}`);
            }
        });

        // Set up a single observer for the utilities tab content
        const mainContent = document.querySelector('.col-md-11 .flex-grow-1');
        if (mainContent && !mainContent.hasAttribute('data-observer-initialized')) {
            logDebug('👀 Setting up utilities content observer');
            
            const debouncedInit = debounce(() => {
                if (!isInitializingButtons) {
                    initUtilitiesTabHoverButtons();
                }
            }, 500);

            const observer = new MutationObserver((mutations) => {
                // Only react to meaningful changes
                const hasRelevantChanges = mutations.some(mutation => {
                    // Check if added nodes contain config items
                    const hasConfigItems = Array.from(mutation.addedNodes).some(node => {
                        return node.nodeType === 1 && (
                            node.classList?.contains('config-item') ||
                            node.querySelector?.('.config-item')
                        );
                    });
                    
                    // Check if the mutation affects the structure
                    const isStructuralChange = mutation.type === 'childList' && 
                        (mutation.addedNodes.length > 0 || mutation.removedNodes.length > 0);
                    
                    return hasConfigItems || isStructuralChange;
                });

                if (hasRelevantChanges) {
                    logDebug('🔄 Relevant content changes detected, scheduling reinitialization');
                    debouncedInit();
                }
            });

            observer.observe(mainContent, { 
                childList: true, 
                subtree: true,
                attributes: false,
                characterData: false
            });

            mainContent.setAttribute('data-observer-initialized', 'true');
        }    } catch (error) {
        // Error handling disabled
    } finally {
        isInitializingButtons = false;
    }
}

// Create new tab from utilities item
function openSidebarTab(label, iconClass = 'fas fa-star') {
    logDebug(`🗂️ Creating new tab for: ${label} with icon ${iconClass}`);
    
    const sidebar = document.querySelector('.sidebar .flex-grow-1');    if (!sidebar) {
        return;
    }

    const tabId = 'customtab-' + Date.now();

    // Create tab structure
    const tabDiv = document.createElement('div');
    tabDiv.className = 'position-relative w-100 d-flex flex-column align-items-center';
    
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'sidebar-icon nav-link mb-1';
    btn.setAttribute('data-tab', tabId);
    btn.innerHTML = `<i class="${iconClass}"></i>`;

    const tooltip = document.createElement('span');
    tooltip.className = 'sidebar-tooltip';
    tooltip.textContent = label;

    tabDiv.appendChild(btn);
    tabDiv.appendChild(tooltip);
    sidebar.appendChild(tabDiv);

    // Add click handler
    btn.addEventListener('click', (e) => {
        e.preventDefault();
        document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
        btn.classList.add('active');

        const content = `<div class='custom-tab-content'><h2>${label}</h2><div class='alert alert-info mt-4'>This is a new tab for <b>${label}</b>.</div></div>`;
        loadTabContent(tabId, null, content);
    });

    // Trigger click to show content
    btn.click();
    
    logDebug(`✨ Created new tab: ${label}`);
    return tabId;
}

// Initialize map with dynamic resource loading
async function initializeMap() {
    const mapContainer = document.getElementById('map');
    if (!mapContainer || mapContainer.classList.contains('map-initialized')) {
        return;
    }

    try {
        logDebug('🗺️ Loading Mapbox resources...');
        await window.loadMapboxResources();
        logDebug('✅ Mapbox resources loaded successfully');

        mapboxgl.accessToken = 'pk.eyJ1IjoiZXVnZW5lbmd1eWVuIiwiYSI6ImNtOXo5OHdxajBpdjgybHNkb2dteHpiOGsifQ.09yjZss9L31zEdvdQL_tzA';
        const map = new mapboxgl.Map({
            container: 'map',
            style: 'mapbox://styles/mapbox/streets-v11',
            center: [106.7, 10.7],
            zoom: 4
        });

        map.addControl(new mapboxgl.NavigationControl());
        
        map.on('load', function() {
            logDebug('🗺️ Map initialized successfully');
            loadMapLayers(map);
        });

        mapContainer.classList.add('map-initialized');    } catch (error) {
        mapContainer.innerHTML = '<div class="alert alert-danger">Failed to load map. Please check your internet connection and refresh the page.</div>';
    }
}

// Load map layers separately
async function loadMapLayers(map) {
    try {
        // Load Vietnam ports
        const portsResponse = await fetch(vietnamPortsUrl);
        const portsData = await portsResponse.json();
        
        map.addSource('vietnam_ports', {
            type: 'geojson',
            data: portsData
        });
        
        map.addLayer({
            id: 'vietnam_ports_layer',
            type: 'circle',
            source: 'vietnam_ports',
            paint: {
                'circle-radius': 6,
                'circle-color': '#007cbf',
                'circle-stroke-width': 2,
                'circle-stroke-color': '#fff'
            }
        });

        // Add port click handling
        map.on('click', 'vietnam_ports_layer', function(e) {
            const coordinates = e.features[0].geometry.coordinates.slice();
            const name = e.features[0].properties.name || 'Port';
            new mapboxgl.Popup()
                .setLngLat(coordinates)
                .setHTML('<strong>' + name + '</strong>')
                .addTo(map);
        });

        // Add hover effects
        map.on('mouseenter', 'vietnam_ports_layer', () => map.getCanvas().style.cursor = 'pointer');
        map.on('mouseleave', 'vietnam_ports_layer', () => map.getCanvas().style.cursor = '');

        // Load shipping routes
        const routesResponse = await fetch(shippingRoutesUrl);
        const routesData = await routesResponse.json();
        
        map.addSource('shipping_routes', {
            type: 'geojson',
            data: routesData
        });
        
        map.addLayer({
            id: 'shipping_routes_layer',
            type: 'line',
            source: 'shipping_routes',
            paint: {
                'line-width': 2,
                'line-color': '#ff8800',
                'line-opacity': 0.7
            }
        });

        logDebug('✅ Map layers loaded successfully');    } catch (error) {
        // Error handling disabled
    }
}

// Load tab content
function loadTabContent(tabId, url, fallbackHtml) {
    logDebug(`🔄 Loading content for tab: ${tabId}`, { url, cached: tabContentCache.has(tabId) });
    
    const mainContent = document.querySelector('.col-md-11 .flex-grow-1');    if (!mainContent) {
        return;
    }

    // Save current tab's form state before switching
    const currentTab = document.querySelector('.sidebar-icon.active');
    if (currentTab) {
        const currentTabId = currentTab.dataset.tab || currentTab.getAttribute('href')?.split('/').pop();
        if (currentTabId) {
            formState.save(currentTabId);
            logDebug(`📥 Saved state for current tab: ${currentTabId}`);
        }
    }

    try {
        // Check cache first
        if (tabContentCache.has(tabId)) {
            const content = tabContentCache.get(tabId);
            logDebug(`📋 Using cached content for: ${tabId}`);
            mainContent.innerHTML = content;
            
            if (tabId === 'utilities') {
                setTimeout(initUtilitiesTabHoverButtons, 100);
            }

            // Wait a bit longer for DOM to be ready before restoring state
            setTimeout(() => {
                formState.restore(tabId);
                logDebug(`📤 Restored state for tab: ${tabId}`);
            }, 250);
            
            return;
        }

        // Not in cache, need to fetch
        if (url) {
            logDebug(`📡 Fetching fresh content from: ${url}`);
            fetch(url)
                .then(response => {
                    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                    return response.text();
                })
                .then(html => {
                    mainContent.innerHTML = html;
                    tabContentCache.set(tabId, html);
                    logDebug(`✅ Successfully loaded and cached content for: ${tabId}`);
                    
                    if (tabId === 'utilities') {
                        setTimeout(initUtilitiesTabHoverButtons, 100);
                    }

                    // Wait for content to be properly rendered
                    setTimeout(() => {
                        formState.restore(tabId);
                        logDebug(`📤 Restored state for tab: ${tabId}`);
                    }, 250);
                })                .catch(error => {
                    if (fallbackHtml) {
                        mainContent.innerHTML = fallbackHtml;
                        tabContentCache.set(tabId, fallbackHtml);
                    }
                });
        } else if (fallbackHtml) {
            mainContent.innerHTML = fallbackHtml;
            tabContentCache.set(tabId, fallbackHtml);
            
            if (tabId === 'utilities') {
                setTimeout(initUtilitiesTabHoverButtons, 100);
            }

            setTimeout(() => {
                formState.restore(tabId);
                logDebug(`📤 Restored state for tab: ${tabId}`);
            }, 250);
        }    } catch (error) {
        if (fallbackHtml) {
            mainContent.innerHTML = fallbackHtml;
        }
    }
}

// Setup tab handlers
function setupSidebarTabClicks() {
    const tabs = document.querySelectorAll('.sidebar-icon[data-tab], .sidebar-icon.nav-link');
    logDebug(`🔧 Found ${tabs.length} tabs to initialize`);
    
    tabs.forEach(tabLink => {
        const tabId = tabLink.dataset.tab || tabLink.getAttribute('href')?.split('/').pop();
        if (!tabId) return;

        // Remove default behavior and add our handler
        tabLink.addEventListener('click', (e) => {
            e.preventDefault();
            logDebug(`👆 Tab clicked: ${tabId}`);

            document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
            tabLink.classList.add('active');

            const url = staticTabUrls[tabId];
            const fallbackHtml = `<div class='tab-content'><h2>${tabId}</h2>Loading...</div>`;
            loadTabContent(tabId, url, fallbackHtml);
        });
    });
}

// Initialize everything
function initializeApp() {
    if (isInitialized) {
        logDebug('🚫 Application already initialized, skipping');
        return;
    }

    logDebug('🚀 Starting application initialization');
    setupSidebarTabClicks();

    // Load default tab
    const activeTab = document.querySelector('.sidebar-icon.active');
    if (activeTab) {
        const tabId = activeTab.dataset.tab || activeTab.getAttribute('href')?.split('/').pop();
        if (tabId && staticTabUrls[tabId]) {
            logDebug('📊 Loading default tab: ' + tabId);
            loadTabContent(tabId, staticTabUrls[tabId]);
        }
    }

    isInitialized = true;
    logDebug('✅ Application initialization complete');
}

// One-time initialization when the DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeApp, { once: true });
} else {
    initializeApp();
}
