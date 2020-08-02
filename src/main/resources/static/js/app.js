import axios from 'axios';
import Swal from "sweetalert2";

document.addEventListener('DOMContentLoaded', () => {
    const formsEliminar = document.querySelectorAll('.eliminar-producto');

    //revisar que exista los formularios
    if (formsEliminar.length > 0) {
        formsEliminar.forEach(obj => {
            obj.addEventListener('submit', eliminarProducto);
        })
    }
});


function eliminarProducto(e) {
    e.preventDefault();

    Swal.fire({
        title: 'Eliminar Producto',
        text: "Un producto eliminado no se puede recuperar",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Borrar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {

        if (result.value) {
            //tomar el id del comentario
            const idProducto = this.children[0].value;

            //crear el objeto
            const datos = {
                idProducto
            };


            //el this.action y datos es lo que se envia a comentariosControllerFE,
            //ya luego lo que viene es la respuesta que el envia
            axios.post(this.action, datos)
                .then(respuesta => {
                    Swal.fire(
                        'Eliminado!',
                        respuesta.data,
                        'success'
                    );

                    //Eliminar del DOM
                    this.parentElement.parentElement.parentElement.remove();

                })
                .catch(error => {
                    if (error.response.status === 403 || error.response.status === 404){
                        Swal.fire('Error', error.response.data,'error');
                    }
                });
        }
    });


}
