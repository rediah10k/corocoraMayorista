package com.organizacion.controller;

import com.organizacion.config.UserSessionData;
import com.organizacion.dto.TareaActivaInfo;
import com.organizacion.service.CamundaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PedidoController {

    @Autowired
    private CamundaService camundaService;

    @Autowired
    UserSessionData sessionData;

    @Value("${camunda.pedidos.seleccionar.prioridad}")
    private String prioridadActivityId;

    @GetMapping("/")
    public String goHome(Model model) { // 🔑 CLAVE: Inyectar Model

        // 1. Verificar autenticación
        if (!sessionData.isAuthenticated() || sessionData.getClienteInfo() == null) {
            return "redirect:/login";
        }
        model.addAttribute("clienteInfo", sessionData.getClienteInfo());

        List<TareaActivaInfo> tareasInfo = camundaService.obtenerIdsActivosEnActivityId(prioridadActivityId);

        model.addAttribute("nuevosPedidos", tareasInfo);

        return "index"; // Renderiza la plantilla 'inicio.html'
    }

    @GetMapping("/{id}")
    public String pedidoProcess(
            @PathVariable("id") String rootProcessId, // Recibe el ID Raíz (para las variables)
            @RequestParam("taskId") String taskId,    // 💡 Recibe el Task ID del URL
            Model model
    ) {
        // 1. Obtener variables del proceso usando el ID Raíz
        Map<String, Object> resultado = camundaService.obtenerVariablesPorIdUnico(rootProcessId);

        String nombreCliente = (String) resultado.get("clienteNombre");
        String detallePedido = (String) resultado.get("productosPedido");

        // 2. Pasar todas las variables a la vista
        model.addAttribute("clienteNombre", nombreCliente);
        model.addAttribute("detallePedido", detallePedido);

        // 3. Pasar los IDs necesarios para la navegación y el POST
        model.addAttribute("rootProcessId", rootProcessId);
        model.addAttribute("taskId", taskId); // 💡 CLAVE: Pasamos el Task ID a la vista 'prioridad.html'

        return "prioridad";
    }


    @PostMapping("/asignar-prioridad")
    public String asignarPrioridad(
            @RequestParam("taskId") String taskId, // 💡 CAMBIO: Ahora espera 'taskId'
            @RequestParam("nuevaPrioridad") String prioridad) {

        // Construye el mapa de variables para enviar a Camunda
        Map<String, Object> variables = new HashMap<>();
        variables.put("prioridadAtencion", prioridad);

        // Completa la tarea usando el TASK ID (ya que modificamos el servicio para usarlo)
        camundaService.completarTareaEspecifica(taskId, variables); // Usamos 'taskId'

        // Nota: Corregí el path de redirección a tu raíz de pedidos
        return "redirect:/";
    }




}
