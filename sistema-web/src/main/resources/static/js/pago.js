document.addEventListener('DOMContentLoaded', () => {
    const formulario = document.getElementById('formularioPago');
    if (!formulario) {
        return;
    }
    const seccionEfectivo = formulario.querySelector('.seccion-efectivo');
    const seccionTarjeta = formulario.querySelector('.seccion-tarjeta');
    const montoRecibido = document.getElementById('montoRecibido');
    const cambio = document.getElementById('cambio');
    const total = parseFloat(montoRecibido.dataset.total);

    const actualizarMetodo = () => {
        const seleccionado = formulario.querySelector('input[name="metodo"]:checked');
        const esTarjeta = seleccionado && seleccionado.value === 'TARJETA';
        seccionEfectivo.classList.toggle('d-none', esTarjeta);
        seccionTarjeta.classList.toggle('d-none', !esTarjeta);
    };

    const actualizarCambio = () => {
        const recibido = parseFloat(montoRecibido.value) || 0;
        const diferencia = recibido - total;
        cambio.textContent = diferencia >= 0 ? 'Q ' + diferencia.toFixed(2) : 'Faltan Q ' + Math.abs(diferencia).toFixed(2);
        cambio.classList.toggle('text-danger', diferencia < 0);
    };

    formulario.querySelectorAll('input[name="metodo"]').forEach(radio => radio.addEventListener('change', actualizarMetodo));
    montoRecibido.addEventListener('input', actualizarCambio);
    actualizarMetodo();
    actualizarCambio();
});
