document.addEventListener('DOMContentLoaded', () => {
    const cuerpo = document.querySelector('#tablaLineas tbody');
    const botonAgregar = document.getElementById('agregarLinea');
    const totalPedido = document.getElementById('totalPedido');
    const puntosPedido = document.getElementById('puntosPedido');
    const plantilla = cuerpo.querySelector('.linea').cloneNode(true);

    const formatear = valor => 'Q ' + valor.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});

    const reindexar = () => {
        cuerpo.querySelectorAll('.linea').forEach((fila, indice) => {
            fila.querySelector('.selector-producto').name = `lineas[${indice}].productoId`;
            fila.querySelector('.campo-cantidad').name = `lineas[${indice}].cantidad`;
        });
    };

    const recalcular = () => {
        let total = 0;
        let puntos = 0;
        cuerpo.querySelectorAll('.linea').forEach(fila => {
            const opcion = fila.querySelector('.selector-producto').selectedOptions[0];
            const cantidad = parseInt(fila.querySelector('.campo-cantidad').value, 10) || 0;
            const precio = opcion && opcion.dataset.precio ? parseFloat(opcion.dataset.precio) : 0;
            const puntosUnidad = opcion && opcion.dataset.puntos ? parseInt(opcion.dataset.puntos, 10) : 0;
            const subtotal = precio * cantidad;
            fila.querySelector('.subtotal').textContent = formatear(subtotal);
            total += subtotal;
            puntos += puntosUnidad * cantidad;
        });
        totalPedido.textContent = formatear(total);
        puntosPedido.textContent = puntos;
    };

    botonAgregar.addEventListener('click', () => {
        const nueva = plantilla.cloneNode(true);
        nueva.querySelector('.selector-producto').value = '';
        nueva.querySelector('.campo-cantidad').value = 1;
        cuerpo.appendChild(nueva);
        reindexar();
        recalcular();
    });

    cuerpo.addEventListener('click', evento => {
        if (evento.target.classList.contains('quitar-linea')) {
            const filas = cuerpo.querySelectorAll('.linea');
            if (filas.length > 1) {
                evento.target.closest('.linea').remove();
            } else {
                filas[0].querySelector('.selector-producto').value = '';
                filas[0].querySelector('.campo-cantidad').value = 1;
            }
            reindexar();
            recalcular();
        }
    });

    cuerpo.addEventListener('input', recalcular);
    cuerpo.addEventListener('change', recalcular);
    recalcular();
});
