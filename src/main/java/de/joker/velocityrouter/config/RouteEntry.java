package de.joker.velocityrouter.config;

import java.util.List;

public class RouteEntry {
    private String hostname;
    private String defaultServer;
    private List<PermissionRoute> permissionRoutes;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getDefaultServer() {
        return defaultServer;
    }

    public void setDefaultServer(String defaultServer) {
        this.defaultServer = defaultServer;
    }

    public List<PermissionRoute> getPermissionRoutes() {
        return permissionRoutes;
    }

    public void setPermissionRoutes(List<PermissionRoute> permissionRoutes) {
        this.permissionRoutes = permissionRoutes;
    }
}