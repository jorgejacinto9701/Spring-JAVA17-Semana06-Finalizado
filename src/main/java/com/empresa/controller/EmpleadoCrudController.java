package com.empresa.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.empresa.entity.Empleado;
import com.empresa.service.EmpleadoService;
import com.empresa.util.FunctionUtil;

import jakarta.servlet.http.HttpSession;

@Controller
public class EmpleadoCrudController {
	
	@Autowired
	private EmpleadoService empleadoService;

	@GetMapping("/verCrudEmpleado")
	public String verInicio() {
		return "crudEmpleado";
	}
	
	@GetMapping("/consultaCrudEmpleado")
	@ResponseBody
	public List<Empleado> listaEmpleado(String filtro){
		List<Empleado> lstSalida = empleadoService.listaPorNombreApellidoLike("%" + filtro + "%");
		return lstSalida;
	}
	
	@PostMapping("/registraCrudEmpleado")
	@ResponseBody
	public Map<?, ?> registra(Empleado obj, HttpSession session) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		obj.setEstado(1);
		obj.setFechaRegistro(new Date());
		obj.setFechaActualizacion(new Date()); 
	
		//Para la PC2
		//Usuario objUsuario=(Usuario)session.getAttribute("objUsuario");
		//obj.setUsuarioRegistro(objUsuario);
		//obj.setUsuarioActualiza(objUsuario);
		
		//Validaciones por nombre y apellido
		List<Empleado> lstSalida = empleadoService.listaPorNombreApellidoIgual(obj.getNombres(), obj.getApellidos());
		if (!CollectionUtils.isEmpty(lstSalida)) {
			map.put("mensaje", "El empleado " + obj.getNombres()+ " " + obj.getApellidos() + " ya existe");
			List<Empleado> lista = empleadoService.listaPorNombreApellidoLike("%");
			map.put("lista", lista);
			return map;
		}
		
		Empleado objSalida = empleadoService.insertaEmpleado(obj);
		if (objSalida == null) {
			map.put("mensaje", "Error en el registro");
		} else {
			map.put("mensaje", "Registro exitoso");
			List<Empleado> lista = empleadoService.listaPorNombreApellidoLike("%");
			map.put("lista", lista);
		}
	

		return map;
	}
	
	@GetMapping("/buscaEmpleadoMayorEdad")
	@ResponseBody
	public String validaMayorEdad(String fechaNacimiento) {
		if(FunctionUtil.isMayorEdad(fechaNacimiento)) {
			return "{\"valid\":true}";
		}else {
			return "{\"valid\":false}";
		}
	}
	
	
	@PostMapping("/actualizaCrudEmpleado")
	@ResponseBody
	public Map<?, ?> actualiza(Empleado obj, HttpSession session) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		  

		
		Optional<Empleado> optEmpleado= empleadoService.buscaEmpleado(obj.getIdEmpleado());
		
		//Validaciones por nombre y apellido
		List<Empleado> lstSalida = empleadoService.listaPorNombreApellidoIgualActualiza(obj.getNombres(), obj.getApellidos(), optEmpleado.get().getIdEmpleado());
		if (!CollectionUtils.isEmpty(lstSalida)) {
			map.put("mensaje", "El empleado " + obj.getNombres()+ " " + obj.getApellidos() + " ya existe");
			List<Empleado> lista = empleadoService.listaPorNombreApellidoLike("%");
			map.put("lista", lista);
			return map;
		}
				
		obj.setFechaRegistro(optEmpleado.get().getFechaRegistro());
		obj.setEstado(optEmpleado.get().getEstado());
		obj.setFechaActualizacion(new Date());
		
		
		//Para la PC2
		//Usuario objUsuario=(Usuario)session.getAttribute("objUsuario");
		//obj.setUsuarioActualiza(objUsuario);
		//obj.setUsuarioRegistro(optEmpleado.get().getFechaRegistro());
		
		Empleado objSalida = empleadoService.actualizaEmpleado(obj);
		if (objSalida == null) {
			map.put("mensaje", "Error en actualizar");
		} else {
			map.put("mensaje", "Actualización exitosa");
			List<Empleado> lista = empleadoService.listaPorNombreApellidoLike("%");
			map.put("lista", lista);
		}
		return map;
	}
	
	@ResponseBody
	@PostMapping("/eliminaCrudEmpleado")
	public Map<?, ?> elimina(int id) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		Empleado objEmpleado= empleadoService.buscaEmpleado(id).get();
		objEmpleado.setFechaActualizacion(new Date());  
		objEmpleado.setEstado( objEmpleado.getEstado() == 1 ? 0 : 1);
		Empleado objSalida = empleadoService.actualizaEmpleado(objEmpleado);
		if (objSalida == null) {
			map.put("mensaje", "Error en actualizar");
		} else {
			List<Empleado> lista = empleadoService.listaPorNombreApellidoLike("%");
			map.put("lista", lista);
		}
		return map;
	}
}



