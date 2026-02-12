package org.iesch.psp.Ej10;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Representa un Claim (tipo + valor)
class Claim {
    private final String type;
    private final String value;

    public Claim(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() { return type; }
    public String getValue() { return value; }
}

// Representa una identidad con una lista de claims
class ClaimsIdentity {
    private final List<Claim> claims;

    public ClaimsIdentity(List<Claim> claims) {
        this.claims = claims;
    }

    public List<Claim> getClaims() { return claims; }
}

// Representa el principal (usuario) con una identidad
record ClaimsPrincipal(ClaimsIdentity identity) {

    // Buscar si tiene un claim concreto
    public boolean hasClaim(String type, String value) {
        for (Claim c : identity.getClaims()) {
            if (c.getType().equals(type) && c.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    // Obtener el valor de un claim por tipo
    public String getClaimValue(String type) {
        for (Claim c : identity.getClaims()) {
            if (c.getType().equals(type)) {
                return c.getValue();
            }
        }
        return null;
    }
}

// Clase para simular el hilo actual (como Thread.CurrentPrincipal)
class ThreadPrincipal {
    private static ClaimsPrincipal currentPrincipal;

    public static void setPrincipal(ClaimsPrincipal principal) {
        currentPrincipal = principal;
    }

    public static ClaimsPrincipal getPrincipal() {
        return currentPrincipal;
    }
}

public class ClaimsAuthExample {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Crear usuario con claims
        System.out.println("--- Creación de usuario con Claims ---");
        System.out.println("Introduce nombre de usuario:");
        String user = sc.nextLine().trim();

        List<Claim> claims = new ArrayList<>();
        claims.add(new Claim("NameIdentifier", user));
        claims.add(new Claim("Name", "Joe"));
        claims.add(new Claim("Surname", "Brown"));
        claims.add(new Claim("Role", "Operator"));
        claims.add(new Claim("Department", "IT"));
        claims.add(new Claim("Country", "ES"));
        claims.add(new Claim("SalesOrder", "Select")); // Solo consulta
        claims.add(new Claim("DomainUser", "Edition")); // Puede editar usuarios

        ClaimsIdentity identity = new ClaimsIdentity(claims);
        ClaimsPrincipal principal = new ClaimsPrincipal(identity);

        // Asignamos al hilo actual
        ThreadPrincipal.setPrincipal(principal);

        // Mostrar claims del usuario
        System.out.println("\nClaims del usuario:");
        for (Claim c : ThreadPrincipal.getPrincipal().identity().getClaims()) {
            System.out.println("  " + c.getType() + " = " + c.getValue());
        }

        // --- Sistema de autorización ---
        System.out.println("\n--- Comprobación de acceso a recursos ---");

        // Recurso 1: Panel de administración (requiere Role = Administrator)
        System.out.println("\n[Panel de Administración]");
        if (principal.hasClaim("Role", "Administrator")) {
            System.out.println(">> ACCESO PERMITIDO");
        } else {
            System.out.println(">> ACCESO DENEGADO. Se requiere Role: Administrator");
        }

        // Recurso 2: Gestión de usuarios del dominio (requiere DomainUser = Edition)
        System.out.println("\n[Gestión de Usuarios del Dominio]");
        if (principal.hasClaim("DomainUser", "Edition")) {
            System.out.println(">> ACCESO PERMITIDO. Puede editar usuarios.");
        } else {
            System.out.println(">> ACCESO DENEGADO. Se requiere DomainUser: Edition");
        }

        // Recurso 3: Órdenes de venta (requiere SalesOrder = Supervisor)
        System.out.println("\n[Autorizar Órdenes de Compra]");
        if (principal.hasClaim("SalesOrder", "Supervisor")) {
            System.out.println(">> ACCESO PERMITIDO. Puede autorizar órdenes.");
        } else {
            String nivel = principal.getClaimValue("SalesOrder");
            System.out.println(">> ACCESO DENEGADO. Nivel actual: " + nivel + ". Se requiere: Supervisor");
        }

        // Recurso 4: Consulta de órdenes (requiere SalesOrder = Select)
        System.out.println("\n[Consultar Órdenes de Compra]");
        if (principal.hasClaim("SalesOrder", "Select") || principal.hasClaim("SalesOrder", "Supervisor")) {
            System.out.println(">> ACCESO PERMITIDO. Puede consultar órdenes.");
        } else {
            System.out.println(">> ACCESO DENEGADO. Se requiere SalesOrder: Select o Supervisor");
        }

        // Recurso 5: Departamento IT (requiere Department = IT)
        System.out.println("\n[Recursos del Departamento IT]");
        if (principal.hasClaim("Department", "IT")) {
            System.out.println(">> ACCESO PERMITIDO. Usuario del departamento IT.");
        } else {
            System.out.println(">> ACCESO DENEGADO. Se requiere Department: IT");
        }
    }
}