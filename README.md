# WhoDo - Payment Order Service (Financial Backend)

Microservicio financiero transaccional desarrollado en **Java y Spring Boot**, responsable de la orquestación, procesamiento seguro de cobros y dispersión de fondos dentro del ecosistema de economía colaborativa **WhoDo**. 

Este componente actúa como la pasarela de enlace entre la lógica de negocio distribuida (`whodo-workorder-service`) y los procesadores de pago externos, garantizando la consistencia transaccional y la seguridad financiera de los usuarios.

## 🚀 Arquitectura y Decisiones de Diseño

El diseño de este microservicio prioriza la integridad de los datos, la seguridad en la comunicación con terceros y el desacoplamiento operativo:

*   **Modelo Transaccional de Escrow:** Implementa una arquitectura basada en lógica de estados diferidos a nivel de backend. Retiene los saldos de forma lógica en la base de datos hasta recibir confirmaciones explícitas de finalización del servicio, mitigando riesgos de fraude o incumplimiento de tareas.
*   **Consumo Asíncrono de Webhooks:** Expone endpoints seguros configurados para procesar notificaciones de eventos en tiempo real (*IPN / Webhooks*) provenientes del procesador de pagos, asegurando la consistencia del estado transaccional sin bloquear el hilo principal de la aplicación.
*   **Entornos de Simulación Seguros:** Integra el uso estratégico de túneles **ngrok** para exponer los endpoints locales durante la fase de desarrollo, permitiendo realizar pruebas integradas de callbacks y webhooks en caliente sin necesidad de despliegues productivos prematuros.

## 🛠️ Stack Tecnológico

*   **Lenguaje:** Java (Programación Orientada a Objetos robusta)
*   **Framework Principal:** Spring Boot (Spring Web, Spring Data)
*   **Base de Datos:** Persistencia y mapeo transaccional SQL/NoSQL.
*   **Integraciones Externas:** SDK Oficial de MercadoPago.
*   **Herramientas de Red:** ngrok (Túneles de red seguros y exposición de puertos).

## ⚙️ Características Técnicas Principales (Foco Líder Técnico)

1.  **Orquestación de Checkout Seguro:** Implementa el flujo de pagos procesando de forma segura los identificadores de preferencia (*Preference ID*), garantizando contratos limpios de datos hacia el cliente móvil Android.
2.  **Manejo Resiliente de Payloads Externos:** Estructura parsers dinámicos para validar, limpiar e interpretar los payloads entrantes de MercadoPago, gestionando reintentos lógicos y protegiendo al backend contra inconsistencias de red.
3.  **Tolerancia a Fallos y Sincronización:** Diseñado bajo principios de idempotencia para asegurar que un mismo webhook de pago procesado múltiples veces no altere el estado financiero del negocio o duplique transacciones.

## 📁 Estructura del Proyecto

*   `controller/`: Capa encargada de exponer la API transaccional y los webhooks de escucha pública.
*   `service/`: Núcleo financiero, donde reside la lógica de escrow, cálculo de estados y validación de cobros.
*   `repository/`: Abstracción e integración de la capa de persistencia de datos.
*   `model/`: Definición y tipado estricto de las entidades transaccionales.
