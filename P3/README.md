## Diseño de la sociedad de agentes

- Jerarquía:
  - 4 drones
  - 1 controlador **(toda la inteligencia)**

## Diseño de la estrategia de movimiento de los agentes

- Nuestro agente controlador es quien contiene el mapa y quien lo actualiza en base a la información que le envían los drones
- **Priorización** de movimientos:
  1. Halcón
  2. Pájaro
  3. Mosca
- División del mapa en 4, proporcional a las casillas que ve cada dron
- El agente se mueve las casillas que puede ver dividido entre 2 (la dirección depende de la casilla donde aparezcan)
- Exploración del mapa por turnos acorde a la división del mapa asignado por el controlador y por prioridad
- Movimiento de los drones en zig-zag en vertical. En el caso del halcón exploración en zig-zag diagonal para descubrir más terreno
- Cuando los 4 se les agote la batería se sigue la prioridad anterior
- ¿Cómo priorizamos según los agentes que nos dan?

## Diseño de comunicación

- Los drones se comunican en todo momento con el agente controlador para saber si se pueden mover
- El agente controlador envía las órdenes a los drones y éstos lo reenvían al controlador del servidor
