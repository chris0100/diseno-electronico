package com.proyecto.diselec.view.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
import com.proyecto.diselec.models.ItemCarrito;
import com.proyecto.diselec.models.Orden;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.List;
import java.util.Map;

@Component("panel-clientes/ordenes-detalle-cliente")
public class FacturaPdfView extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {

        Orden orden = (Orden) model.get("orden");


        Image image = Image.getInstance("http://"+request.getServerName()+":"+request.getServerPort()+"/img/logo-sin-fondo.png");
        PdfContentByte canvas = writer.getDirectContent();
        image.scaleAbsolute(600,400);
        image.setAbsolutePosition(50,300);
        canvas.saveState();

        PdfGState state = new PdfGState();
        state.setFillOpacity(0.1f);
        canvas.setGState(state);
        canvas.addImage(image);
        canvas.restoreState();



        PdfPTable tabla = new PdfPTable(1);
        tabla.setSpacingAfter(20);

        PdfPCell cell2 = null;

        cell2 = new PdfPCell(new Phrase("Orden #" + orden.getId()));
        cell2.setBackgroundColor(new Color(18, 218, 255));
        cell2.setPadding(8f);
        tabla.addCell(cell2);

        PdfPCell cell = null;

        cell = new PdfPCell(new Phrase("Datos del cliente"));
        cell.setBackgroundColor(new Color(184, 218, 255));
        cell.setPadding(8f);
        tabla.addCell(cell);

        tabla.addCell("Cliente: " + orden.getUsuario().getNombre());
        tabla.addCell("Email: " + orden.getUsuario().getUsername());
        tabla.addCell("Telefono: " + orden.getUsuario().getTelefono());
        tabla.addCell("Direccion: " + orden.getDireccionUsuario().getDireccion());


        PdfPTable tabla2 = new PdfPTable(4);
        tabla2.setWidths(new float [] {3.5f, 1, 1, 1});
        tabla2.addCell("Producto");
        tabla2.addCell("Precio");
        tabla2.addCell("Cantidad");
        tabla2.addCell("Total");

        //Obtener la lista de items
        List<ItemCarrito> listaItemCarrito = orden.getListaItemCarrito();

        for (ItemCarrito item : listaItemCarrito){
            tabla2.addCell(item.getProducto().getNombre());
            tabla2.addCell("$ "+item.getProducto().getPrecioVenta());

            cell = new PdfPCell(new Phrase(item.getCantidad()+""));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            tabla2.addCell(cell);
            tabla2.addCell("$ "+item.getSubtotal());
        }

        cell = new PdfPCell(new Phrase("Total: "));
        cell.setColspan(3);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tabla2.addCell(cell);
        tabla2.addCell("$ "+orden.getGrandTotal());






        document.add(tabla);
        document.add(tabla2);


        document.close();
        writer.close();
    }
}
