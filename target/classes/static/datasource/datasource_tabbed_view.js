// JavaScript extracted from datasource_tabbed_view.html
// Place all custom JS logic here, including blueprint builder, map, and tab logic

console.log('DEBUG: JS loaded/reloaded at', new Date().toISOString());

// Use Thymeleaf's inline processing to get the correct URLs
const vietnamPortsUrl = '/vietnam_ports.geojson';
const shippingRoutesUrl = '/global_shipping_routes.geojson';
const eastFacingShipUrl = '/east_facing_ship.png';
const westFacingShipUrl = '/west_facing_ship.png';

// Mapbox map initialization for the Map tab
function initializeMap() {
    const mapContainer = document.getElementById('map');
    if (mapContainer && !mapContainer.classList.contains('map-initialized')) {
        mapboxgl.accessToken = 'pk.eyJ1IjoiZXVnZW5lbmd1eWVuIiwiYSI6ImNtOXo5OHdxajBpdjgybHNkb2dteHpiOGsifQ.09yjZss9L31zEdvdQL_tzA'; // Replace with your Mapbox token
        const map = new mapboxgl.Map({
            container: 'map',
            style: 'mapbox://styles/mapbox/streets-v11',
            center: [106.7, 10.7], // Example: Vietnam
            zoom: 4
        });
        map.addControl(new mapboxgl.NavigationControl());
        map.on('load', function() {
            // Add Vietnam ports as a layer
            fetch(vietnamPortsUrl)
                .then(response => response.json())
                .then(data => {
                    map.addSource('vietnam_ports', {
                        type: 'geojson',
                        data: data
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
                    // Optional: Add popup on click
                    map.on('click', 'vietnam_ports_layer', function(e) {
                        const coordinates = e.features[0].geometry.coordinates.slice();
                        const name = e.features[0].properties.name || 'Port';
                        new mapboxgl.Popup()
                            .setLngLat(coordinates)
                            .setHTML('<strong>' + name + '</strong>')
                            .addTo(map);
                    });
                    map.on('mouseenter', 'vietnam_ports_layer', function() {
                        map.getCanvas().style.cursor = 'pointer';
                    });
                    map.on('mouseleave', 'vietnam_ports_layer', function() {
                        map.getCanvas().style.cursor = '';
                    });
                });
            // Add global shipping routes as a layer
            fetch(shippingRoutesUrl)
                .then(response => response.json())
                .then(data => {
                    map.addSource('shipping_routes', {
                        type: 'geojson',
                        data: data
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
                });
        });
        mapContainer.classList.add('map-initialized');
    }
}

// Always try to initialize the map after DOMContentLoaded and after any tab change
function tryMapInitOnTabChange() {
    initializeMap();
}

document.addEventListener('DOMContentLoaded', function() {
    initializeMap();
    // If you use tab navigation via links, also listen for clicks
    document.querySelectorAll('.sidebar-icon').forEach(function(tabLink) {
        tabLink.addEventListener('click', function() {
            setTimeout(tryMapInitOnTabChange, 300); // Wait for tab to show
        });
    });

    // --- Client-side Tab System ---
    // Container for dynamic tab content panels
    let tabPanelsContainer = document.getElementById('dynamic-tab-panels');
    if (!tabPanelsContainer) {
        tabPanelsContainer = document.createElement('div');
        tabPanelsContainer.id = 'dynamic-tab-panels';
        tabPanelsContainer.style.width = '100%';
        document.querySelector('.flex-grow-1').prepend(tabPanelsContainer);
    }

    // Keep track of open tabs
    const openTabs = {};

    // Sidebar tab logic: open a new client-side tab (no reload)
    function openSidebarTab(label) {
        // Generate a slug for the label to use as the tab name
        const tabSlug = label.toLowerCase().replace(/[^a-z0-9]+/g, '-');
        const tabId = 'customtab-' + tabSlug + '-' + Date.now();
        // Find the sidebar
        const sidebar = document.querySelector('.sidebar .flex-grow-1');
        // Try to get the icon class from the clicked item
        let iconClass = 'fas fa-star'; // default
        let found = false;
        if (window.lastPlusBtnClicked) {
            // Debug: log the clicked element and its HTML
            console.log('DEBUG: lastPlusBtnClicked:', window.lastPlusBtnClicked);
            console.log('DEBUG: lastPlusBtnClicked.outerHTML:', window.lastPlusBtnClicked.outerHTML);
            // Try to find the first non-plus icon in the clicked element
            let icons = window.lastPlusBtnClicked.querySelectorAll('i.fas, i.far, i.fab');
            console.log('DEBUG: icons in clicked:', Array.from(icons).map(i => i.className));
            for (let i = 0; i < icons.length; i++) {
                if (!icons[i].classList.contains('fa-plus')) {
                    iconClass = icons[i].className;
                    found = true;
                    console.log('DEBUG: selected icon from clicked:', iconClass);
                    break;
                }
            }
            // If not found, walk up to the closest .config-item and try again
            if (!found) {
                const parentItem = window.lastPlusBtnClicked.closest('.config-item');
                if (parentItem) {
                    let parentIcons = parentItem.querySelectorAll('i.fas, i.far, i.fab');
                    console.log('DEBUG: parentItem.outerHTML:', parentItem.outerHTML);
                    console.log('DEBUG: icons in parentItem:', Array.from(parentIcons).map(i => i.className));
                    for (let i = 0; i < parentIcons.length; i++) {
                        if (!parentIcons[i].classList.contains('fa-plus')) {
                            iconClass = parentIcons[i].className;
                            found = true;
                            console.log('DEBUG: selected icon from parentItem:', iconClass);
                            break;
                        }
                    }
                }
            }
            // If still not found, try section header as before
            if (!found) {
                const sectionBox = window.lastPlusBtnClicked.closest('.section-box');
                if (sectionBox) {
                    const headerIcon = sectionBox.querySelector('.section-header i.fas, .section-header i.far, .section-header i.fab');
                    console.log('DEBUG: sectionBox.outerHTML:', sectionBox.outerHTML);
                    if (headerIcon && !headerIcon.classList.contains('fa-plus')) {
                        iconClass = headerIcon.className;
                        console.log('DEBUG: selected icon from section header:', iconClass);
                    } else {
                        console.log('DEBUG: no suitable icon found in section header');
                    }
                } else {
                    console.log('DEBUG: no sectionBox found for clicked');
                }
            }
        } else {
            console.log('DEBUG: no lastPlusBtnClicked set');
        }
        // Add sidebar tab
        const tabDiv = document.createElement('div');
        tabDiv.className = 'position-relative w-100 d-flex flex-column align-items-center custom-sidebar-tab';
        tabDiv.dataset.iconClass = iconClass; // Store icon class for later use
        // Use <button> instead of <a> for sidebar tab to prevent reload
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'sidebar-icon nav-link mb-1 active';
        
        // Create the icon with both the icon class and text content for better rendering
        const iconElement = document.createElement('i');
        iconElement.className = iconClass; 
        btn.appendChild(iconElement);
        
        // Apply style directly to ensure visibility
        btn.style.background = 'none';
        btn.style.border = 'none';
        btn.style.padding = '0';
        btn.style.margin = '0';
        btn.style.width = '40px';
        btn.style.height = '40px';
        btn.style.display = 'flex';
        btn.style.alignItems = 'center';
        btn.style.justifyContent = 'center';
        tabDiv.appendChild(btn);
        const tooltip = document.createElement('span');
        tooltip.className = 'sidebar-tooltip';
        tooltip.innerText = label;
        tabDiv.appendChild(tooltip);
        // Add close button
        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '&times;';
        closeBtn.title = 'Close tab';
        closeBtn.className = 'tab-close-btn';
        closeBtn.style.cssText = 'position:absolute;top:2px;right:2px;background:none;border:none;color:#888;font-size:1.1rem;z-index:3;display:none;';
        tabDiv.appendChild(closeBtn);
        tabDiv.addEventListener('mouseenter',()=>{closeBtn.style.display='block';});
        tabDiv.addEventListener('mouseleave',()=>{closeBtn.style.display='none';});
        closeBtn.addEventListener('click',function(e){
            e.stopPropagation();
            e.preventDefault();
            // Remove tab and panel
            tabDiv.remove();
            const panel = document.getElementById(tabId);
            if(panel) panel.remove();
            // If this tab was active, show the first tab panel if any
            if(btn.classList.contains('active')) {
                const firstTab = document.querySelector('.custom-sidebar-tab .sidebar-icon');
                if(firstTab) firstTab.click();
                else {
                    // Hide all panels
                    document.querySelectorAll('.tab-content-panel').forEach(p=>p.style.display='none');
                }
            }
        });
        sidebar.appendChild(tabDiv);
        // Tab content panel
        const panel = document.createElement('div');
        panel.className = 'tab-content-panel';
        panel.id = tabId;
        panel.style.display = 'none';
        panel.innerHTML = `<div class='tab-content'><h2>${label}</h2><div class='alert alert-info mt-4'>This is a new tab for <b>${label}</b>. Data is preserved here.</div></div>`;
        tabPanelsContainer.appendChild(panel);
        // Tab switching logic
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('DEBUG: Sidebar tab clicked for', label, 'at', new Date().toISOString());
            document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
            btn.classList.add('active');
            
            // Re-apply icon to ensure it's visible after tab switch
            if (iconElement && !iconElement.isConnected) {
                // If icon was somehow removed, recreate it
                while (btn.firstChild) btn.removeChild(btn.firstChild);
                const newIconElement = document.createElement('i');
                newIconElement.className = iconClass;
                btn.appendChild(newIconElement);
            }
            
            showTabContent(tabId);
        });
        // Show this new tab
        document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
        btn.classList.add('active');
        showTabContent(tabId);
        // Track open tab
        openTabs[tabId] = true;
    }
    function showTabContent(tabId) {
        // Hide all static tab-content elements (Dashboard, Utilities, etc.)
        document.querySelectorAll('.tab-content').forEach(panel => {
            panel.style.display = 'none';
        });
        // Show/hide dynamic tab panels
        document.querySelectorAll('.tab-content-panel').forEach(panel => {
            panel.style.display = (panel.id === tabId) ? 'block' : 'none';
        });
    }

    // Plus button logic for all config subitems
    document.querySelectorAll('.config-subitem').forEach(function(subitem) {
        // Add plus button if not present
        if (!subitem.querySelector('.subitem-plus-btn')) {
            const btn = document.createElement('button');
            btn.className = 'subitem-plus-btn';
            btn.title = 'Open in new tab';
            btn.style.display = 'none';
            btn.style.position = 'absolute';
            btn.style.right = '6px';
            btn.style.top = '50%';
            btn.style.transform = 'translateY(-50%)';
            btn.style.background = 'none';
            btn.style.border = 'none';
            btn.style.color = '#007bff';
            btn.style.fontSize = '1.1rem';
            btn.style.cursor = 'pointer';
            btn.style.zIndex = '2';
            btn.innerHTML = '<i class="fas fa-plus"></i>';
            subitem.appendChild(btn);
        }
        subitem.addEventListener('mouseenter', function() {
            const btn = this.querySelector('.subitem-plus-btn');
            if (btn) btn.style.display = 'inline-block';
        });
        subitem.addEventListener('mouseleave', function() {
            const btn = this.querySelector('.subitem-plus-btn');
            if (btn) btn.style.display = 'none';
        });
        subitem.querySelector('.subitem-plus-btn').onclick = function(e) {
            e.stopPropagation();
            const label = subitem.innerText.trim();
            openSidebarTab(label);
        };
    });

    // Plus button logic for all config-item elements that do NOT have .config-subitems, except Blueprint
    document.querySelectorAll('.config-item').forEach(function(item, idx) {
        if (
            !item.querySelector('.config-subitems') &&
            !item.querySelector('.item-plus-btn') &&
            !item.classList.contains('blueprint-config-item') // skip Blueprint
        ) {
            const btn = document.createElement('button');
            btn.className = 'item-plus-btn';
            btn.title = 'Open in new tab';
            btn.style.display = 'none';
            btn.style.position = 'absolute';
            btn.style.right = '6px';
            btn.style.top = '50%';
            btn.style.transform = 'translateY(-50%)';
            btn.style.background = 'none';
            btn.style.border = 'none';
            btn.style.color = '#007bff';
            btn.style.fontSize = '1.1rem';
            btn.style.cursor = 'pointer';
            btn.style.zIndex = '2';
            btn.innerHTML = '<i class="fas fa-plus"></i>';
            item.classList.add('position-relative');
            item.appendChild(btn);
            item.addEventListener('mouseenter', function() {
                btn.style.display = 'inline-block';
            });
            item.addEventListener('mouseleave', function() {
                btn.style.display = 'none';
            });
            btn.onclick = function(e) {
                e.stopPropagation();
                const label = item.innerText.trim();
                openSidebarTab(label);
            };
        }
    });

    // Blueprint plus button hover logic (open a new sidebar tab, not modal)
    const blueprintItem = document.querySelector('.blueprint-config-item');
    const plusBtn = blueprintItem ? blueprintItem.querySelector('.blueprint-plus-btn') : null;
    if (blueprintItem && plusBtn) {
        blueprintItem.addEventListener('mouseenter', () => { plusBtn.style.display = 'inline-block'; });
        blueprintItem.addEventListener('mouseleave', () => { plusBtn.style.display = 'none'; });
        plusBtn.onclick = function(e) {
            e.stopPropagation();
            const label = blueprintItem.innerText.trim();
            openSidebarTab(label);
        };
    }

    // Patch all plus button click handlers to set window.lastPlusBtnClicked (the clicked subitem or item)
    document.querySelectorAll('.subitem-plus-btn, .item-plus-btn, .blueprint-plus-btn').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            // Set the clicked element itself (subitem or item)
            window.lastPlusBtnClicked = btn.closest('.config-subitem') || btn.closest('.config-item');
        }, true);
    });

    // Debug: Log how many config-subitem elements are found
    const subitems = document.querySelectorAll('.config-subitem');
    console.log('Found', subitems.length, '.config-subitem elements');

});
