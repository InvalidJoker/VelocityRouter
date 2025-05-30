# VelocityRouter

> Route players across your Minecraft network dynamically based on their permissions and the hostname they connect to.

---

## Overview

**VelocityRouter** is a powerful routing plugin for Velocity proxy servers that directs players to specific backend servers based on:

* The hostname they joined with,
* Their permissions,
* And fallback default servers when no permissions match or servers are offline.

This allows you to create complex routing rules for different player groups like VIPs, staff, or general users with ease.

---

## Features

*  **Permission-Based Routing**
  Direct players to different servers depending on their assigned permissions.

*  **Hostname-Based Routing**
  Configure routing rules specific to the hostname or domain players connect with.

*  **Fallback Servers**
  Specify default servers for each hostname if no permission route matches or if the target server is offline.

*  **Custom Deny Messages**
  Display personalized messages to players who lack permission to access certain servers.

*  **Wildcard Support**
  Use a wildcard (`*`) hostname to define global fallback routing rules.

---

## Example Configuration

```yaml
routes:
  # Route 1: Default general access
  - hostname: "play.example.com"
    defaultServer: "lobby"
    permissionRoutes: []  # No special permission routing for this hostname

  # Route 2: VIP access
  - hostname: "vip.example.com"
    defaultServer: "vip_lobby"  # Fallback if no permission matches or VIP server is offline
    permissionRoutes:
      - permission: "velocity.vip.access"
        targetServer: "vip_server"
        denyMessage: "<red>You need VIP rank to access this server."

  # Route 3: Staff access
  - hostname: "staff.example.com"
    defaultServer: "admin_lobby"  # Fallback if no permission matches
    permissionRoutes:
      - permission: "velocity.staff.access"
        targetServer: "staff_server"
        denyMessage: "<red>You do not have staff access."

  # Route 4: Global fallback (wildcard hostname)
  - hostname: "*"  # Matches any hostname not explicitly configured above
    defaultServer: "lobby"
    permissionRoutes: []
```

---

## How It Works

1. When a player connects, **VelocityRouter** checks the hostname they used.
2. It looks for a matching route configuration.
3. Within that route, it checks for any permission-based routes the player qualifies for.
4. If a matching permission route is found and the target server is online, the player is forwarded there.
5. If no permission matches or the server is offline, the player is sent to the route's `defaultServer`.
6. If the player lacks permission for a matched permission route, the configured `denyMessage` is shown.