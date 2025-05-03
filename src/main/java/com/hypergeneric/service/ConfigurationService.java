package com.hypergeneric.service;

import com.hypergeneric.model.ConfigCategory;
import com.hypergeneric.model.ConfigSection;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

/**
 * Service for providing UI configuration data that was previously hardcoded in templates
 */
@Service
public class ConfigurationService {

    /**
     * Returns the configuration sections for the utilities tab
     */
    public List<ConfigSection> getUtilitiesConfiguration() {
        List<ConfigSection> sections = new ArrayList<>();
        
        // User Management Section
        ConfigSection userManagement = new ConfigSection("User Management", "userManagementContent");
        
        // Persons category
        ConfigCategory persons = new ConfigCategory("Persons", "fas fa-users text-primary", "personsSubConfig");
        persons.addItem("fas fa-user-plus", "Create user")
               .addItem("fas fa-user-edit", "Edit user")
               .addItem("fas fa-user-tag", "Assign roles")
               .addItem("fas fa-user-cog", "User preferences")
               .addItem("fas fa-user-clock", "Activity logs");
        userManagement.addCategory(persons);
        
        // Teams category
        ConfigCategory teams = new ConfigCategory("Teams", "fas fa-user-friends text-primary", "teamsSubConfig");
        teams.addItem("fas fa-plus-circle", "Create team")
             .addItem("fas fa-edit", "Edit team")
             .addItem("fas fa-user-plus", "Add members")
             .addItem("fas fa-tasks", "Team assignments")
             .addItem("fas fa-chart-line", "Team metrics");
        userManagement.addCategory(teams);
        
        // Admin groups category
        ConfigCategory adminGroups = new ConfigCategory("Admin groups", "fas fa-user-shield text-primary", "adminGroupsSubConfig");
        adminGroups.addItem("fas fa-plus-circle", "Create admin group")
                  .addItem("fas fa-edit", "Edit admin group")
                  .addItem("fas fa-user-plus", "Add admin")
                  .addItem("fas fa-lock", "Permissions")
                  .addItem("fas fa-key", "Access control");
        userManagement.addCategory(adminGroups);
        
        // Profiles category
        ConfigCategory profiles = new ConfigCategory("Profiles", "fas fa-id-card text-primary", "profilesSubConfig");
        profiles.addItem("fas fa-plus-circle", "Create profile")
                .addItem("fas fa-edit", "Edit profile")
                .addItem("fas fa-copy", "Clone profile")
                .addItem("fas fa-user-tag", "Assign profile")
                .addItem("fas fa-shield-alt", "Security settings");
        userManagement.addCategory(profiles);
        
        sections.add(userManagement);
        
        // Analytics & Navigation Section
        ConfigSection analyticsNavigation = new ConfigSection("Analytics & Navigation", "analyticsNavigationContent");
        
        // Statistics category
        ConfigCategory statistics = new ConfigCategory("Statistics", "fas fa-chart-bar text-primary", "statisticsSubConfig");
        statistics.addItem("fas fa-chart-line", "Usage metrics")
                 .addItem("fas fa-chart-pie", "Data visualization")
                 .addItem("fas fa-file-export", "Export reports")
                 .addItem("fas fa-calendar-alt", "Schedule reports")
                 .addItem("fas fa-users-cog", "User analytics");
        analyticsNavigation.addCategory(statistics);
        
        // Searches category
        ConfigCategory searches = new ConfigCategory("Searches", "fas fa-search text-primary", "searchesSubConfig");
        searches.addItem("fas fa-plus-circle", "New search")
               .addItem("fas fa-save", "Saved searches")
               .addItem("fas fa-share-alt", "Shared searches")
               .addItem("fas fa-wrench", "Search settings")
               .addItem("fas fa-history", "Search history");
        analyticsNavigation.addCategory(searches);
        
        // Views category
        ConfigCategory views = new ConfigCategory("Views", "fas fa-table text-primary", "viewsSubConfig");
        views.addItem("fas fa-plus-circle", "Create view")
            .addItem("fas fa-columns", "Customize columns")
            .addItem("fas fa-sort", "Sort options")
            .addItem("fas fa-filter", "Filter settings")
            .addItem("fas fa-share-alt", "Share view");
        analyticsNavigation.addCategory(views);
        
        // Navigators category
        ConfigCategory navigators = new ConfigCategory("Navigators", "fas fa-compass text-primary", "navigatorsSubConfig");
        navigators.addItem("fas fa-map", "Navigator layout")
                 .addItem("fas fa-sitemap", "Structure")
                 .addItem("fas fa-th-large", "Dashboard widgets")
                 .addItem("fas fa-street-view", "Default views")
                 .addItem("fas fa-cog", "Navigator settings");
        analyticsNavigation.addCategory(navigators);
        
        sections.add(analyticsNavigation);
        
        // Blueprint and Metadata Section
        ConfigSection documentMetadata = new ConfigSection("Blueprint and Metadata", "documentMetadataContent");
        
        // Blueprint category
        ConfigCategory blueprint = new ConfigCategory("Blueprint", "fas fa-cubes text-primary", "blueprintSubConfig");
        blueprint.addItem("fas fa-plus-circle", "Create blueprint")
              .addItem("fas fa-edit", "Edit blueprint")
              .addItem("fas fa-copy", "Clone blueprint")
              .addItem("fas fa-share-alt", "Share blueprint")
              .addItem("fas fa-download", "Export blueprint");
        documentMetadata.addCategory(blueprint);
        
        // Fields category
        ConfigCategory fields = new ConfigCategory("Fields", "fas fa-tags text-primary", "fieldsSubConfig");
        fields.addItem("fas fa-plus-circle", "Add field")
             .addItem("fas fa-edit", "Edit field")
             .addItem("fas fa-cogs", "Field properties")
             .addItem("fas fa-eye", "Visibility settings")
             .addItem("fas fa-check-square", "Validation rules");
        documentMetadata.addCategory(fields);
        
        // Confidentiality category
        ConfigCategory confidentiality = new ConfigCategory("Confidentiality", "fas fa-shield-alt text-primary", "confidentialitySubConfig");
        confidentiality.addItem("fas fa-lock", "Access levels")
                     .addItem("fas fa-user-lock", "User permissions")
                     .addItem("fas fa-mask", "Masking rules")
                     .addItem("fas fa-eye-slash", "Restricted content")
                     .addItem("fas fa-history", "Access logs");
        documentMetadata.addCategory(confidentiality);
        
        // Workflow category
        ConfigCategory workflow = new ConfigCategory("Workflow", "fas fa-project-diagram text-primary", "workflowSubConfig");
        workflow.addItem("fas fa-plus-circle", "Create workflow")
               .addItem("fas fa-edit", "Edit workflow")
               .addItem("fas fa-clipboard-check", "Approval steps")
               .addItem("fas fa-tasks", "Workflow tasks")
               .addItem("fas fa-history", "Audit trail");
        documentMetadata.addCategory(workflow);
        
        sections.add(documentMetadata);
        
        // Configuration & Settings Section
        ConfigSection configSettings = new ConfigSection("Configuration & Settings", "configSettingsContent");
        
        // System settings category
        ConfigCategory systemSettings = new ConfigCategory("System Settings", "fas fa-cogs text-primary", "systemSettingsSubConfig");
        systemSettings.addItem("fas fa-server", "Server configuration")
                    .addItem("fas fa-database", "Database settings")
                    .addItem("fas fa-envelope", "Email configuration")
                    .addItem("fas fa-clipboard-list", "Logging settings")
                    .addItem("fas fa-exclamation-triangle", "Error handling");
        configSettings.addCategory(systemSettings);
        
        // Appearance category
        ConfigCategory appearance = new ConfigCategory("Appearance", "fas fa-palette text-primary", "appearanceSubConfig");
        appearance.addItem("fas fa-paint-brush", "Theme settings")
                 .addItem("fas fa-mobile-alt", "Mobile layout")
                 .addItem("fas fa-desktop", "Desktop layout")
                 .addItem("fas fa-language", "Language options")
                 .addItem("fas fa-clock", "Time zone settings");
        configSettings.addCategory(appearance);
        
        // Security category
        ConfigCategory security = new ConfigCategory("Security", "fas fa-shield-alt text-primary", "securitySubConfig");
        security.addItem("fas fa-key", "Authentication")
               .addItem("fas fa-user-lock", "Access control")
               .addItem("fas fa-user-shield", "Permissions")
               .addItem("fas fa-history", "Audit logs")
               .addItem("fas fa-exclamation-circle", "Security alerts");
        configSettings.addCategory(security);
        
        // Integrations category
        ConfigCategory integrations = new ConfigCategory("Integrations", "fas fa-plug text-primary", "integrationsSubConfig");
        integrations.addItem("fas fa-plus-circle", "Add integration")
                  .addItem("fas fa-edit", "Configure integration")
                  .addItem("fas fa-exchange-alt", "Data mapping")
                  .addItem("fas fa-sync", "Sync settings")
                  .addItem("fas fa-history", "Integration logs");
        configSettings.addCategory(integrations);
        
        sections.add(configSettings);
        
        // Reference Tools Section
        ConfigSection referenceTools = new ConfigSection("Reference Tools", "referenceToolsContent");
        
        // Import/Export category
        ConfigCategory importExport = new ConfigCategory("Import/Export", "fas fa-exchange-alt text-primary", "importExportSubConfig");
        importExport.addItem("fas fa-file-import", "Import data")
                  .addItem("fas fa-file-export", "Export data")
                  .addItem("fas fa-file-csv", "CSV templates")
                  .addItem("fas fa-cogs", "Import settings")
                  .addItem("fas fa-history", "Import history");
        referenceTools.addCategory(importExport);
        
        // Reports category
        ConfigCategory reports = new ConfigCategory("Reports", "fas fa-chart-line text-primary", "reportsSubConfig");
        reports.addItem("fas fa-plus-circle", "Create report")
              .addItem("fas fa-edit", "Edit report")
              .addItem("fas fa-clock", "Scheduled reports")
              .addItem("fas fa-share-alt", "Share report")
              .addItem("fas fa-download", "Download report");
        referenceTools.addCategory(reports);
        
        // API Access category
        ConfigCategory apiAccess = new ConfigCategory("API Access", "fas fa-code text-primary", "apiAccessSubConfig");
        apiAccess.addItem("fas fa-key", "API keys")
                .addItem("fas fa-lock", "API permissions")
                .addItem("fas fa-file-code", "API documentation")
                .addItem("fas fa-history", "API usage logs")
                .addItem("fas fa-wrench", "API settings");
        referenceTools.addCategory(apiAccess);
        
        // Utilities category
        ConfigCategory utilities = new ConfigCategory("Utilities", "fas fa-toolbox text-primary", "utilitiesSubConfig");
        utilities.addItem("fas fa-broom", "Clean up tools")
                .addItem("fas fa-database", "Database management")
                .addItem("fas fa-file-archive", "Archiving tools")
                .addItem("fas fa-trash", "Purge data")
                .addItem("fas fa-diagnoses", "System diagnostics");
        referenceTools.addCategory(utilities);
        
        sections.add(referenceTools);
        
        return sections;
    }
    
    /**
     * Returns the available tabs for the datasource view
     */
    public String[] getAvailableTabs() {
        return new String[]{"dashboard", "navigator", "search", "creation", "utilities", "map"};
    }
}