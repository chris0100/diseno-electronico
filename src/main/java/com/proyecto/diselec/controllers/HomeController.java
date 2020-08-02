package com.proyecto.diselec.controllers;

import com.proyecto.diselec.models.Producto;
import com.proyecto.diselec.services.OrdenService;
import com.proyecto.diselec.services.ProductoService;
import com.proyecto.diselec.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    ProductoService productoServiceObj;

    @Autowired
    OrdenService ordenServiceObj;



    // Pagina de Catalogo
    @GetMapping("/catalogo")
    public String catalogo(Model model){
        //Obtener la lista de productos
        List<Producto> listaProductos = productoServiceObj.findAll();

        //Obtener lista de categorias
        List<String> listaCategorias = Util.listaCategorias();


        if (listaProductos.size() == 0){
            model.addAttribute("vacio", "No hay productos disponibles aun");
        }

        int minimo = 1000;
        int maximo = 500000;

        model.addAttribute("minimo",minimo);
        model.addAttribute("maximo",maximo);

        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("listaProductos", listaProductos);
        return "catalogo";
    }


    //Catalogo filtrado por categoria
    @GetMapping("/catalogo/{categoria}")
    public String catalogoCategoria(Model model, @PathVariable("categoria") String categoria,
                                    @RequestParam("minimo")int minimo, @RequestParam("maximo") int maximo){

        System.out.println("min:" + minimo + " max:" + maximo);

        //Obtener la lista de productos
        List<Producto> listaProductos = productoServiceObj.findAllByCategoria(categoria);

        //Obtener lista de categorias
        List<String> listaCategorias = Util.listaCategorias();
        model.addAttribute("selected", categoria);


        model.addAttribute("minimo",minimo);
        model.addAttribute("maximo",maximo);
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("listaProductos", listaProductos);
        return "catalogo";
    }


    //Catalogo filtrado por precios
    @GetMapping("/catalogo/precios")
    public String catalogoPrecios(Model model, @ModelAttribute("minimo") String minimo,
                                  @ModelAttribute("maximo") String maximo,
                                  @ModelAttribute("selected") String categoria){

        System.out.println("min: " + minimo + " max: " + maximo);

        //Extraer los precios
        double precioMinimo = Double.parseDouble(minimo.substring(1));
        double precioMaximo = Double.parseDouble(maximo.substring(1));

        //Se realiza la consulta de los productos que cumplan con la condicion
        List<Producto> listaProductos = productoServiceObj.
                findAllByPrecioVentaBetweenAndCategoria(precioMinimo, precioMaximo,categoria);
        System.out.println(listaProductos.size());

        //Obtener lista de categorias
        List<String> listaCategorias = Util.listaCategorias();
        model.addAttribute("selected", categoria);

        model.addAttribute("minimoAjuste",(int) precioMinimo);
        model.addAttribute("maximoAjuste",(int) precioMaximo);


        model.addAttribute("minimo",1000);
        model.addAttribute("maximo",500000);
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("listaProductos", listaProductos);
        return "catalogo";

    }



    // Pagina detalle de producto
    @GetMapping("/info-producto/{id}")
    public String infoProducto(@PathVariable("id") Long id, Model model){

        //Se localiza el producto
        Producto producto = productoServiceObj.findOne(id);

        model.addAttribute("producto", producto);

        return "info-producto";
    }





    // Pagina de inicio
    @GetMapping("/")
    public String home(Model model){

        //Obtener los ultimos productos agregados
        List<Producto> listaUltimosProductos = productoServiceObj.findAllByFechaIsBefore(new Date());
        if (listaUltimosProductos.size() > 5){
            listaUltimosProductos = listaUltimosProductos.subList(0,4);
        }


        System.out.println(listaUltimosProductos.size());

        for (Producto obj : listaUltimosProductos){
            System.out.println(obj.getFecha());
        }

        model.addAttribute("listaProductos", listaUltimosProductos);
        return "index";
    }


    // Pagina de Servicios
    //@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/servicios")
    public String servicios(){
        return "servicios";
    }


    // Pagina de Contacto
    @GetMapping("/contactenos")
    public String contactenos(){
        return "contactenos";
    }







}
