// JavaScript extracted from datasource_tabbed_view.html
// Place all custom JS logic here, including blueprint builder, map, and tab logic

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
    const openTabs = {};    // --- Unified Tab Content Cache ---
    const tabContentCache = {};    function loadTabContent(tabId, url, fallbackHtml) {
      const mainContent = document.getElementById('main-content'); // Only inject into #main-content
      if (!mainContent) {
        console.error('Main content area (#main-content) not found!');
        return;
      }
      
      // For custom tabs, ensure we have the tab's element and it's marked as active
      if (tabId && tabId.startsWith('customtab-')) {
        const customTab = document.querySelector(`.sidebar-icon[data-tab="${tabId}"]`);
        if (customTab) {
          document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
          customTab.classList.add('active');
        }
      }
      
      if (tabContentCache[tabId]) {
        mainContent.innerHTML = tabContentCache[tabId];
        
        // Initialize utilities tab buttons if this is the utilities tab
        if (tabId === 'utilities') {
          setTimeout(initUtilitiesTabHoverButtons, 100);
        }
        
        return;
      }
      if (url) {
        fetch(url)
          .then(r => r.text())
          .then(html => {
            tabContentCache[tabId] = html;
            mainContent.innerHTML = html;
            
            // Initialize utilities tab buttons if this is the utilities tab
            if (tabId === 'utilities') {
              setTimeout(initUtilitiesTabHoverButtons, 100);
            }
          })
          .catch(() => {
            mainContent.innerHTML = '<div class="alert alert-danger">Failed to load content.</div>';
          });
      } else if (fallbackHtml) {
        tabContentCache[tabId] = fallbackHtml;
        mainContent.innerHTML = fallbackHtml;
      }
    }

    // --- Refactor static tab switching to use cache-aware loader ---
    const staticTabUrls = {
      dashboard: '/datasource/dashboard.html',
      search: '/datasource/search.html',
      navigator: '/datasource/navigator.html',
      creation: '/datasource/creation.html',
      utilities: '/datasource/utilities.html',
      map: '/datasource/map.html',
      profile: '/datasource/profile.html',
    };    // Function to initialize utilities tab hover buttons 
    function initUtilitiesTabHoverButtons() {
      // Remove any existing buttons to prevent duplicates
      document.querySelectorAll('.item-plus-btn').forEach(btn => {
        if (btn.parentNode && btn.parentNode.classList.contains('config-item')) {
          btn.remove();
        }
      });
      
      // Initialize plus buttons for all config items
      document.querySelectorAll('.config-item').forEach(function(item) {
        // Skip the blueprint item which has its own special handler
        if (item.classList.contains('blueprint-config-item')) {
          return;
        }
        
        // Make sure item has position relative
        item.style.position = 'relative';
        
        // Create and add plus button if not already present
        if (!item.querySelector('.item-plus-btn')) {
          const btn = document.createElement('button');
          btn.className = 'item-plus-btn';
          btn.title = 'Open in new tab';
          btn.innerHTML = '<i class="fas fa-plus"></i>';
          item.appendChild(btn);
          
          // Add event listeners
          item.addEventListener('mouseenter', function() {
            btn.style.display = 'flex';
          });
          
          item.addEventListener('mouseleave', function() {
            btn.style.display = 'none';
          });
          
          btn.onclick = function(e) {
            e.stopPropagation();
            window.lastPlusBtnClicked = item;
            const label = item.querySelector('span') ? item.querySelector('span').innerText.trim() : item.innerText.trim();
            openSidebarTab(label);
          };
        }
      });
      
      // Special handling for blueprint item
      const blueprintItem = document.querySelector('.blueprint-config-item');
      let blueprintBtn = blueprintItem ? blueprintItem.querySelector('.blueprint-plus-btn') : null;
      
      if (blueprintItem) {
        // Ensure position relative
        blueprintItem.style.position = 'relative';
        
        // Create button if it doesn't exist
        if (!blueprintBtn) {
          blueprintBtn = document.createElement('button');
          blueprintBtn.className = 'blueprint-plus-btn';
          blueprintBtn.title = 'Open in new tab';
          blueprintBtn.innerHTML = '<i class="fas fa-plus"></i>';
          blueprintItem.appendChild(blueprintBtn);
        }
        
        // Add hover events
        const mouseEnterHandler = function() {
          if (blueprintBtn) blueprintBtn.style.display = 'flex';
        };
        
        const mouseLeaveHandler = function() {
          if (blueprintBtn) blueprintBtn.style.display = 'none';
        };
        
        // Remove existing listeners to prevent duplicates
        blueprintItem.removeEventListener('mouseenter', mouseEnterHandler);
        blueprintItem.removeEventListener('mouseleave', mouseLeaveHandler);
        
        // Add new listeners
        blueprintItem.addEventListener('mouseenter', mouseEnterHandler);
        blueprintItem.addEventListener('mouseleave', mouseLeaveHandler);
        
        // Set click handler
        blueprintBtn.onclick = function(e) {
          e.stopPropagation();
          window.lastPlusBtnClicked = blueprintItem;
          const label = blueprintItem.querySelector('span') ? blueprintItem.querySelector('span').innerText.trim() : blueprintItem.innerText.trim();
          openSidebarTab(label);
        };
      }
    }// Prevent default navigation and handle tab switching client-side
    // This will prevent page reloads on tab click
    function setupSidebarTabClicks() {
      // Function to handle tab switching (can be reused for dynamic tabs)
      function handleTabClick(e, tabLink) {
        e.preventDefault();
        // Get tab id from href or data attribute
        let tabId = tabLink.getAttribute('data-tab');
        if (!tabId) {
          const href = tabLink.getAttribute('href');
          if (href && href.startsWith('#tab-')) tabId = href.substring(5);
        }
        if (!tabId) return;
        
        // Remove active from all
        document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
        tabLink.classList.add('active');
        
        // Check if this is a custom tab or a static tab
        if (tabId.startsWith('customtab-')) {
          loadTabContent(tabId, null, tabContentCache[tabId] || '');
        } else if (staticTabUrls[tabId]) {
          loadTabContent(tabId, staticTabUrls[tabId]);
        } else {
          // For rare cases, fallback to showing/hiding panels
          showTabContent('tab-' + tabId);
        }
      }
      
      // Set up click handlers for predefined tabs
      document.querySelectorAll('.sidebar-icon.nav-link').forEach(function(tabLink) {
        // First remove any existing click handlers to prevent duplicates
        const newTabLink = tabLink.cloneNode(true);
        if (tabLink.parentNode) {
          tabLink.parentNode.replaceChild(newTabLink, tabLink);
        }
        
        newTabLink.addEventListener('click', function(e) {
          handleTabClick(e, newTabLink);
        });
        
        // Remove href to prevent browser navigation
        newTabLink.removeAttribute('href');
      });
      
      // Also check for any dynamically added tabs that might not have handlers yet
      document.querySelectorAll('.custom-sidebar-tab .sidebar-icon').forEach(function(tabLink) {
        if (!tabLink._clickHandlerAttached) {
          tabLink.addEventListener('click', function(e) {
            handleTabClick(e, tabLink);
          });
          tabLink._clickHandlerAttached = true;
        }
      });
    }

    // Call setupSidebarTabClicks after DOM is ready
    setupSidebarTabClicks();// Load default tab content on page load (dashboard)
    window.addEventListener('DOMContentLoaded', function() {
      // Load dashboard as default
      const dashboardTab = document.querySelector('.sidebar-icon[data-tab="dashboard"]');
      if (dashboardTab) {
        dashboardTab.classList.add('active');
      }
      loadTabContent('dashboard', staticTabUrls['dashboard']);
      
      // Setup for utilities tab initialization
      const utilitiesTab = document.querySelector('.sidebar-icon[data-tab="utilities"]');
      if (utilitiesTab) {
        utilitiesTab.addEventListener('click', function() {
          // Add a small delay to ensure DOM is updated
          setTimeout(initUtilitiesTabHoverButtons, 200);
        });
      }
    });    // --- Refactor dynamic tab creation to use cache-aware loader ---
    function openSidebarTab(label) {
      const tabSlug = label.toLowerCase().replace(/[^a-z0-9]+/g, '-');
      const tabId = 'customtab-' + tabSlug + '-' + Date.now();
      const sidebar = document.querySelector('.sidebar .flex-grow-1');
      let iconClass = 'fas fa-star';
      
      // Try to get the icon class from the clicked item
      let found = false;
      if (window.lastPlusBtnClicked) {
          // Try to find the first non-plus icon in the clicked element
          let icons = window.lastPlusBtnClicked.querySelectorAll('i.fas, i.far, i.fab');
          for (let i = 0; i < icons.length; i++) {
              if (!icons[i].classList.contains('fa-plus')) {
                  iconClass = icons[i].className;
                  found = true;
                  break;
              }
          }
          // If not found, walk up to the closest .config-item and try again
          if (!found) {
              const parentItem = window.lastPlusBtnClicked.closest('.config-item');
              if (parentItem) {
                  let parentIcons = parentItem.querySelectorAll('i.fas, i.far, i.fab');
                  for (let i = 0; i < parentIcons.length; i++) {
                      if (!parentIcons[i].classList.contains('fa-plus')) {
                          iconClass = parentIcons[i].className;
                          found = true;
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
                  if (headerIcon && !headerIcon.classList.contains('fa-plus')) {
                      iconClass = headerIcon.className;
                  }
              }
          }
      }// Add sidebar tab
      const tabDiv = document.createElement('div');
      tabDiv.className = 'position-relative w-100 d-flex flex-column align-items-center custom-sidebar-tab';
      tabDiv.dataset.iconClass = iconClass; // Store icon class for later use
      // Use <button> instead of <a> for sidebar tab to prevent reload
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'sidebar-icon nav-link mb-1 active'; // Add nav-link class to ensure it gets picked up by setupSidebarTabClicks
      btn.setAttribute('data-tab', tabId); // Store tabId in data-tab attribute
      
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
      });      sidebar.appendChild(tabDiv);
      
      // Add direct click handler to the tab button (don't rely on setupSidebarTabClicks for dynamic tabs)
      btn.addEventListener('click', function(e) {
        e.preventDefault();
        // Remove active from all
        document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
        btn.classList.add('active');
        loadTabContent(tabId, null, tabContentCache[tabId] || `<div class='tab-content'><h2>${label}</h2><div class='alert alert-info mt-4'>This is a new tab for <b>${label}</b>. Data is preserved here.</div></div>`);
      });
      
      // Show and cache content immediately
      document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
      btn.classList.add('active');
      loadTabContent(tabId, null, `<div class='tab-content'><h2>${label}</h2><div class='alert alert-info mt-4'>This is a new tab for <b>${label}</b>. Data is preserved here.</div></div>`);
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
        // Don't process items that already have plus buttons or are the blueprint special case
        if (
            !item.querySelector('.item-plus-btn') &&
            !item.classList.contains('blueprint-config-item') // skip Blueprint special case
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
            // Make sure item has position relative for absolute positioning of plus button
            item.style.position = 'relative';
            item.appendChild(btn);
            
            // Add hover effects
            item.addEventListener('mouseenter', function() {
                const plusBtn = this.querySelector('.item-plus-btn');
                if (plusBtn) plusBtn.style.display = 'inline-block';
            });
            
            item.addEventListener('mouseleave', function() {
                const plusBtn = this.querySelector('.item-plus-btn');
                if (plusBtn) plusBtn.style.display = 'none';
            });
            
            btn.onclick = function(e) {
                e.stopPropagation();
                window.lastPlusBtnClicked = item; // Set reference to this item
                const label = item.querySelector('span') ? item.querySelector('span').innerText.trim() : item.innerText.trim();
                openSidebarTab(label);
            };
        }
    });    // Blueprint plus button hover logic (open a new sidebar tab, not modal)
    const blueprintItem = document.querySelector('.blueprint-config-item');
    const plusBtn = blueprintItem ? blueprintItem.querySelector('.blueprint-plus-btn') : null;
    if (blueprintItem && plusBtn) {
        // Make sure blueprint item has position relative
        blueprintItem.style.position = 'relative';
        
        // Add hover events for blueprint
        blueprintItem.addEventListener('mouseenter', function() {
            if (plusBtn) plusBtn.style.display = 'inline-block';
        });
        
        blueprintItem.addEventListener('mouseleave', function() {
            if (plusBtn) plusBtn.style.display = 'none';
        });
        
        // Update click handler
        plusBtn.onclick = function(e) {
            e.stopPropagation();
            window.lastPlusBtnClicked = blueprintItem; // Set reference to blueprint item
            const label = blueprintItem.querySelector('span') ? blueprintItem.querySelector('span').innerText.trim() : blueprintItem.innerText.trim();
            openSidebarTab(label);
        };
    }

    // Patch all plus button click handlers to set window.lastPlusBtnClicked (the clicked subitem or item)
    document.querySelectorAll('.subitem-plus-btn, .item-plus-btn, .blueprint-plus-btn').forEach(function(btn) {
        const originalClick = btn.onclick;
        btn.onclick = function(e) {
            // Set the clicked element itself (subitem or item)
            window.lastPlusBtnClicked = btn.closest('.config-subitem') || btn.closest('.config-item') || btn;
            // Call the original click handler if it exists
            if (typeof originalClick === 'function') {
                originalClick.call(this, e);
            }
        };
    });    // Debug: Log how many config-subitem elements are found
    const subitems = document.querySelectorAll('.config-subitem');    // Monitor for changes in the main content area and re-initialize utilities tab buttons
    function setupUtilitiesTabObserver() {
      // Create a MutationObserver to watch for DOM changes
      const observer = new MutationObserver(function(mutations) {
        // Check if we're on the utilities tab
        const activeTab = document.querySelector('.sidebar-icon.active');
        if (activeTab && activeTab.getAttribute('data-tab') === 'utilities') {
          initUtilitiesTabHoverButtons();
        }
      });
      
      // Start observing the main content area
      const mainContent = document.getElementById('main-content');
      if (mainContent) {
        observer.observe(mainContent, { 
          childList: true,
          subtree: true,
          attributes: false,
          characterData: false
        });
      }
    }

    // Call this on page load
    document.addEventListener('DOMContentLoaded', function() {
      setupUtilitiesTabObserver();
    });

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
    });
      // --- Refactor dynamic tab creation to use cache-aware loader ---
    // Function to re-initialize tab click handlers
    function refreshTabClickHandlers() {
      setupSidebarTabClicks();
        // Specifically check for any dynamic tabs without handlers
      document.querySelectorAll('.custom-sidebar-tab .sidebar-icon').forEach(function(btn) {
        const tabId = btn.getAttribute('data-tab');
        if (tabId && !btn._clickHandlerAttached) {
          btn.addEventListener('click', function(e) {
            e.preventDefault();
            // Remove active from all
            document.querySelectorAll('.sidebar-icon').forEach(el => el.classList.remove('active'));
            btn.classList.add('active');
            
            // Load the cached content if available
            if (tabContentCache[tabId]) {
              loadTabContent(tabId, null, tabContentCache[tabId]);
            }
          });
          btn._clickHandlerAttached = true;
        }
      });
    }
      // Monitor sidebar for any new tabs added
    function setupSidebarObserver() {
      const observer = new MutationObserver(function(mutations) {
        // Check for added nodes that might be tabs
        let tabsAdded = false;
        mutations.forEach(function(mutation) {
          if (mutation.addedNodes.length) {
            for (let i = 0; i < mutation.addedNodes.length; i++) {
              const node = mutation.addedNodes[i];
              if (node.nodeType === 1 && node.classList && node.classList.contains('custom-sidebar-tab')) {
                tabsAdded = true;
                break;
              }
            }
          }
        });
        
        if (tabsAdded) {
          setTimeout(refreshTabClickHandlers, 100);
        }
      });
      
      const sidebar = document.querySelector('.sidebar .flex-grow-1');
      if (sidebar) {
        observer.observe(sidebar, { 
          childList: true,
          subtree: false
        });
      }
    }

    // Call this on page load
    document.addEventListener('DOMContentLoaded', function() {
      setupSidebarObserver();
      // Initial call to refresh handlers
      setTimeout(refreshTabClickHandlers, 500);
    });
  });
